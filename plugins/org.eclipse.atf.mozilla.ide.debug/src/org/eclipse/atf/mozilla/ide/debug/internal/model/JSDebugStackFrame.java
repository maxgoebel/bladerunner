/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.debug.internal.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.mozilla.interfaces.jsdIExecutionHook;
import org.mozilla.interfaces.jsdIProperty;
import org.mozilla.interfaces.jsdIScript;
import org.mozilla.interfaces.jsdIStackFrame;
import org.mozilla.interfaces.jsdIValue;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSDebugStackFrame extends JSDebugElementWithVariables implements IStackFrame {

	private IThread _thread;

	//	private JSDebugVariable _scope;
	private jsdIStackFrame _frameProxy;
	private boolean _initializedVars = false;
	private String _label;
	private int _line = -1;
	private long _lastpc = -1;

	// The following items should stay the same across suspensions
	private int _depth;
	private String _location, _function;
	private IPath _relativePath;
	private String _displayFunction; //TODO: temporary
	private long _tag;
	private boolean _topLevelStart;

	/**
	 * @param target
	 * @param parent
	 */
	public JSDebugStackFrame(IDebugTarget target, IThread thread, jsdIStackFrame frame, int depth, boolean topLevelStart) {
		super(target);

		_thread = thread;
		_depth = depth;
		_topLevelStart = topLevelStart;
		if (!topLevelStart) {
			_function = frame.getFunctionName();
			_displayFunction = _function;
		} else {
			_function = "Suspended at start";
			_displayFunction = _function;
		}
		jsdIScript script = frame.getScript();
		if (script != null) {
			_location = script.getFileName();
			if (_location != null) {
				JSSourceLocator locator = (JSSourceLocator) getDebugTarget().getLaunch().getSourceLocator();
				_relativePath = locator.getContentRelativePath(_location);
			}

			_tag = script.getTag();

			if ("anonymous".equals(_function)) {
				// note: might really be a function called "anonymous"
				String guess = guessFunction(script);
				if (guess != null) {
					_displayFunction = "[" + guess + "]";
				}
			}
		}

		validate(frame);
	}

	private InputStream getScriptContents() throws CoreException {

		String location = getLocation();
		try {
			if (location != null) {
				URL locationURL = new URL(location);
				String appBase = ((JSSourceLocator) getLaunch().getSourceLocator()).getAppBase();
				IProject project = ((JSSourceLocator) getLaunch().getSourceLocator()).getProject();
				return SourceLocatorUtil.getInstance().getSourceContent(locationURL, appBase, project);
			}
		} catch (MalformedURLException mue) {
			IStatus status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Invalid URL supplied by Mozilla.  Unable to open source.", mue); //$NON-NLS-1$
			DebugPlugin.log(status);
			//fallthrough

			throw new CoreException(new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Incorrect URL <" + location + ">" /*TODO:i18n*/, mue));
		}

		return null;

	}

	private final Pattern _propertyPattern = Pattern.compile("(\\w+)\\s*[:=]\\s*$");

	private String guessFunction(jsdIScript script) {
		String guess = null;
		InputStream stream = null;
		//TODO: need to cache these
		try {
			stream = getScriptContents();
			int i, ch;
			Vector lines = new Vector();
			long base = script.getBaseLineNumber();
			for (i = 0; i < base; i++) {
				StringBuffer line = new StringBuffer();
				do {
					ch = stream.read();
					if (ch == -1)
						return null;
					if (ch != '\n')
						line.append((char) ch);
				} while (ch != '\n');
				lines.add(line.toString());
			}
			String line = (String) lines.lastElement();
			String match = "function";//script.getFunctionSource().split("\n")[0];
			int offset = line.indexOf(match);
			if (offset == -1)
				return null; // something went wrong; function wasn't declared on the line reported?
			StringBuffer region = new StringBuffer(line.substring(0, offset));
			for (int j = 2; (j < 5) && (i - j > 0); j++) {
				region.insert(0, (String) lines.elementAt(i - j));
			}

			Matcher m = _propertyPattern.matcher(region.toString());
			if (m.find()) {
				guess = m.group(1);
			}
			//			MozillaDebugPlugin.debug("tag="+script.getTag());
		} catch (CoreException ce) {
			MozillaDebugPlugin.log(ce);
			//abort and return null
		} catch (IOException ioe) {
			MozillaDebugPlugin.log(ioe);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		return guess;
	}

	private void initializeVars() throws DebugException {
		synchronized (_thread) {
			if (_initializedVars)
				return;
			// TODO this is a work around for a null pointer exception - Still need to fix the real problem
			if (_frameProxy == null)
				return;

			try {
				//			jsdIValue globalValue = frameProxy.getExecutionContext().getGlobalObject();
				//			if (globalValue != null) {
				//				JSDebugVariable globalVar = new JSDebugVariable(getDebugTarget(), this, "global", globalValue);
				//				addVariable(globalVar);
				//				globalVar.getValue().getVariables();
				//			}

				jsdIValue thisValue = _frameProxy.getThisValue();
				jsdIValue scopeValue = _frameProxy.getScope();

				if (thisValue != null) {
					JSDebugVariable thisVar = new JSDebugVariable(getDebugTarget(), _thread, "this", thisValue);
					addVariable(thisVar);
					//				thisVar.getValue().getVariables();
				}

				// Add variables in the local scope directly to the stack frame
				if (scopeValue != null) {
					JSDebugVariable scope = new JSDebugVariable(getDebugTarget(), _thread, "scope", scopeValue);
					//				addVariable(scopeVar);
					IVariable[] vars = scope.getValue().getVariables();
					for (int i = 0; i < vars.length; i++) {
						// Skip prototype, constructor, etc.
						//						if (vars[i].getName().startsWith("__"))
						//							continue;
						addVariable((JSDebugVariable) vars[i]);
					}
				}
			} catch (RuntimeException re) {
				MozillaDebugPlugin.log(re);
				throw new DebugException(new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR, "Internal Mozilla Exception" /*TODO:i18n*/, re));
			}

			_initializedVars = true;
		}
	}

	private void updateLabel() {
		String name = _function == null ? "<inline>" /*TODO: i18n*/: _displayFunction;
		String path = (_relativePath == null) ? _location : _relativePath.toPortableString();
		_label = name;
		if (!_topLevelStart) {
			_label = _label + getArgumentList() + (isNative() ? " [native function]" : " file=" + path + " line=" + _line + " pc=" + _lastpc);
		} else {
			_label = _label + " file=" + path;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement#getLabel()
	 */
	public String getLabel() {
		return _label;
	}

	private boolean isNative() {
		return Boolean.valueOf(_frameProxy.getIsNative()).booleanValue();
	}

	private String getArgumentList() {
		StringBuffer args = new StringBuffer();
		jsdIValue scope = _frameProxy.getScope();
		jsdIProperty propsRef[][] = new jsdIProperty[1][];
		long lengthRef[] = new long[1];
		scope.getProperties(propsRef, lengthRef);
		if (propsRef == null)
			return null;

		args.append('(');
		for (int i = 0; i < lengthRef[0]; i++) {
			if ((propsRef[0][i].getFlags() & jsdIProperty.FLAG_ARGUMENT) > 0) {
				if (args.length() > 1)
					args.append(',');
				args.append(propsRef[0][i].getName().getStringValue());
			}
		}
		args.append(')');

		return args.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
	 */
	public IThread getThread() {
		return _thread;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
	 */
	public int getLineNumber() throws DebugException {
		return _line;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
	 */
	public int getCharStart() throws DebugException {
		// information not available
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
	 */
	public int getCharEnd() throws DebugException {
		// information not available
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getName()
	 */
	public String getName() throws DebugException {
		return _function; // == null ? "<inline>" /*TODO: i18n*/ : _function;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
	 */
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
	 */
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return _thread.canStepInto();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return _thread.canStepOver();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return _thread.canStepReturn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return _thread.isStepping();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		JSDebugThread thread = (JSDebugThread) getThread();
		thread.setStepping(_depth, getLineNumber(), getName(), -1);
		//		thread._stepPastDepth = _depth;
		//		thread._stepPastFrame = getName();
		//		thread._stepPastLine = getLineNumber();
		//MozillaDebugPlugin.debug("stepInto depth="+_depth+" frame="+getName()+" line="+getLineNumber());
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		thread.setInterruptHook();
		target.exitNestedEventLoop(jsdIExecutionHook.RETURN_CONTINUE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		// if the line we're at doesn't call out to a function, /next is the same as /step.
		//		TODO: this isn't working.  Stepover still skips lines which don't call out to a function
		JSDebugThread thread = (JSDebugThread) getThread();
		thread.setStepping(_depth, getLineNumber(), getName(), _depth);
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		thread.setInterruptHook();
		thread.setFunctionHook();
		target.exitNestedEventLoop(jsdIExecutionHook.RETURN_CONTINUE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		//	MozillaDebugPlugin.debug("stepReturn depth="+_depth);
		JSDebugThread thread = (JSDebugThread) getThread();
		//		thread._stepOverDepth = _depth - 1;
		thread.setStepping(-1, -1, null, _depth - 1);
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.unsetInterruptHook();
		thread.setFunctionHook();
		target.exitNestedEventLoop(jsdIExecutionHook.RETURN_CONTINUE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return _thread.canResume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return _thread.canSuspend();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return _thread.isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		_thread.resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		_thread.suspend();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return _thread.canTerminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return _thread.isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		_thread.terminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElementWithVariables#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		if (!_initializedVars)
			initializeVars();

		return super.getVariables();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElementWithVariables#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		if (!_initializedVars)
			initializeVars();

		return super.hasVariables();
	}

	public int getDepth() {
		return _depth;
	}

	public void validate(jsdIStackFrame frame) {
		_frameProxy = (jsdIStackFrame) XPCOMThreadProxy.createProxy(frame, ((JSDebugTarget) getDebugTarget()).getProxyHelper());

		_label = null;
		try {
			_line = (int) _frameProxy.getLine();
			if (!isNative())
				_lastpc = _frameProxy.getPc();
		} catch (RuntimeException re) {
			MozillaDebugPlugin.log(re);
		}

		//TODO: more intelligent refresh to keep objects persistent
		_variables = null;
		_initializedVars = false;

		updateLabel();
	}

	public void invalidate() {
		_frameProxy = null;
		_line = -1;
		_variables = null;
		_initializedVars = false;
		_topLevelStart = false;
	}

	/**
	 * Is this stack the same as frame, assuming the same depth, where the pc has not moved?
	 * @param thatFrame
	 * @return
	 */
	public boolean isUnchangedFrom(jsdIStackFrame thatFrame) {
		return isSameAs(thatFrame) && (_lastpc == thatFrame.getPc());
	}

	/**
	 * Is this stack the same as frame, assuming the same depth, where the pc may have moved?
	 * 
	 * @param thatFrame
	 * @param depth
	 * @return
	 */
	public boolean isSameAs(jsdIStackFrame thatFrame) {
		//TODO: to do this more accurately, do I need to monitor function exit/entry?
		//TODO: use script 'tag' attribute for uniqueness instead of filename?

		jsdIScript script = thatFrame.getScript();
		if ((script != null) && (_tag == script.getTag())) {
			String thatFunction = thatFrame.getFunctionName();
			return (_function == null) ? thatFunction == null : _function.equals(thatFunction);
		}

		return false;
	}

	/**
	 * Returns the a reference containing the file with the code for this stack frame, as provided
	 * by the browser debug engine.  This is typically a URL.
	 * 
	 * @return the URL corresponding to this stack frame
	 */
	public String getLocation() {
		return _location;
	}

	public IPath getRelativePath() {
		return _relativePath;
	}

	public IValue eval(String expression) {
		IValue value = null;
		if (_frameProxy != null) {
			jsdIScript script = _frameProxy.getScript();
			String fileName = script.getFileName();
			jsdIValue[] result = new jsdIValue[1];
			boolean status = _frameProxy.eval(expression, fileName, 1, result);
			// status of false doesn't alway seem to mean failure
			if (status || !status) {
				if (result.length != 0) {
					JSDebugVariable thisVar = new JSDebugVariable(getDebugTarget(), _thread, "this", result[0]);
					try {
						value = thisVar.getValue();
						value.getVariables();
					} catch (DebugException e) {
						// JSDebugVariable doesn't throw this exception
						MozillaDebugPlugin.log(e);
					}
				}
			} else {
				value = null;
			}
		}
		return value;
	}
}
