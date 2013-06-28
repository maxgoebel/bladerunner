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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.model.IJSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.atf.mozilla.ide.events.ApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.mozilla.interfaces.jsdICallHook;
import org.mozilla.interfaces.jsdIContext;
import org.mozilla.interfaces.jsdIErrorHook;
import org.mozilla.interfaces.jsdIExecutionHook;
import org.mozilla.interfaces.jsdIObject;
import org.mozilla.interfaces.jsdIProperty;
import org.mozilla.interfaces.jsdIScript;
import org.mozilla.interfaces.jsdIScriptHook;
import org.mozilla.interfaces.jsdIStackFrame;
import org.mozilla.interfaces.jsdIValue;
import org.mozilla.interfaces.nsIBaseWindow;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMLocation;
import org.mozilla.interfaces.nsIDOMNSDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

/**
 * @author Adam
 * 
 */
public class JSDebugThread extends JSDebugElement implements IThread {

	public final static String JAVASCRIPT_FUNCTION = "org.eclipse.atf.debug.javascript.function";
	public final static String JAVASCRIPT_SOURCE_URI = "javascriptSourceFileURI";
	public final static String JAVASCRIPT_FUNCTION_NAME = "functionName";
	public final static String JAVASCRIPT_FUNCTION_STARTLINE = "functionStartLine";

	final private IStackFrame[] _emptyStack = new IStackFrame[0];
	private IBreakpoint[] _breakpoints = new IBreakpoint[0];
	private IStackFrame[] _stackFrames = _emptyStack;
	final static private long TYPE_RUNNING = -1L;
	private long _suspendedType = TYPE_RUNNING;
	private JSDebugVariable _exceptionObject = null;
	private boolean _stepping = false;
	private boolean _terminated = false;
	private IBreakpointManager _breakpointManager;
	private boolean _suspendOnErrors;
	private boolean _suspendOnExceptions;
	private boolean _suspendOnTopLevel;
	private boolean _reachedFirstLine;

	private class DebuggerServiceHook implements jsdIExecutionHook,
			jsdICallHook, jsdIErrorHook, jsdIScriptHook {

		public long onExecute(jsdIStackFrame frame, long type, jsdIValue[] rv) {

			if (MozillaDebugPlugin.getDefault().isProfiling()) {
				return jsdIExecutionHook.RETURN_CONTINUE;
			}

			return onExecute0(frame, type, rv);
		}

		public void onCall(jsdIStackFrame frame, long type) {
			if (MozillaDebugPlugin.getDefault().isProfiling()) {
				postJavaScriptCallEvent(frame, (type == TYPE_FUNCTION_CALL)
						|| (type == TYPE_TOPLEVEL_START));
				return;
			}

			onCall0(frame, type);
		}

		public boolean onError(String message, String filename, long line,
				long pos, long flags, long errornum, jsdIValue exception) {
			if (MozillaDebugPlugin.getDefault().isProfiling())
				return true;
			return onError0(message, filename, line, pos, flags, errornum,
					exception);
		}

		public void onScriptCreated(jsdIScript script) {

			if (MozillaDebugPlugin.getDefault().isProfiling())
				return;
			onScriptCreated0(script);
		}

		public void onScriptDestroyed(jsdIScript script) {
			if (MozillaDebugPlugin.getDefault().isProfiling())
				return;
			onScriptDestroyed0(script);
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface(this, id);
		}
	};

	DebuggerServiceHook hook = new DebuggerServiceHook();

	/*
	 * jsdIScript elements are going to be organized in a hierarchical model
	 * based on the jsdIScript filename and the baselinenumber and lineextend
	 * members. Only a reference to the Top level scripts (should map to files)
	 * are kept in this map and keyed by the filename URL.
	 */
	protected HashMap topScriptElements = new HashMap();

	/**
	 * @param parent
	 */
	public JSDebugThread(IDebugTarget target) {
		super(target);
		_breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	public IStackFrame[] getStackFrames() throws DebugException {
		return isSuspended() ? _stackFrames : _emptyStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	public boolean hasStackFrames() throws DebugException {
		return _stackFrames != null && (_stackFrames.length > 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	public IStackFrame getTopStackFrame() throws DebugException {
		return _stackFrames.length > 0 ? _stackFrames[0] : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	public String getName() throws DebugException {
		return "Application Thread"; // TODO i18n
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints() {
		return _breakpoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isSuspended();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return _suspendedType != TYPE_RUNNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		resume(false);
	}

	/*
	 * Special Method used when a refresh in the browser is done when suspended.
	 * Resume but abort the running script.
	 */
	protected void resumeAbortScript() {
		resume(true);
		_suspendedType = TYPE_RUNNING;
	}

	/*
	 * The logic for resume
	 */
	private void resume(boolean abortScript) {

		if (_stepping) {
			_stepping = false;

			JSDebugTarget target = (JSDebugTarget) getDebugTarget();
			target.getProxyHelper().syncExec(new Runnable() {
				public void run() {
					((JSDebugTarget) getDebugTarget()).unsetInterruptHook();
				}
			});
		}

		_reachedFirstLine = false;
		long returnType = (_suspendedType == jsdIExecutionHook.TYPE_THROW) ? jsdIExecutionHook.RETURN_CONTINUE_THROW
				: jsdIExecutionHook.RETURN_CONTINUE;
		// MozillaDebugPlugin.debug("resume type="+((_suspendedType ==
		// TYPE_THROW) ? "RETURN_CONTINUE_THROW" : "RETURN_CONTINUE"));
		if (abortScript)
			returnType = jsdIExecutionHook.RETURN_ABORT;
		((JSDebugTarget) getDebugTarget()).exitNestedEventLoop(returnType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		getDebugTarget().suspend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return isSuspended() && (!_reachedFirstLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return isSuspended() && (!_reachedFirstLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return isSuspended() && (!_reachedFirstLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return _stepping;
	}

	public void setStepping(int pastDepth, int pastLine, String pastFrame,
			int overDepth) {
		_stepping = true;
		_stepPastDepth = pastDepth;
		_stepPastLine = pastLine;
		_stepPastFrame = pastFrame;
		_stepOverDepth = overDepth;
	}

	private int _stepPastDepth = -1;
	private int _stepPastLine = -1;
	private String _stepPastFrame = null;
	private int _stepOverDepth = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		getTopStackFrame().stepInto();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		getTopStackFrame().stepOver();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		getTopStackFrame().stepReturn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return _terminated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (isTerminated())
			return;

		_terminated = true;
		_stackFrames = null;
		fireTerminateEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement#getLabel
	 * ()
	 */
	public String getLabel() {
		StringBuffer label = new StringBuffer("<unknown>"); // TODO i18n

		try {
			label = new StringBuffer(getName());
			label.append(" (");
			try {
				switch ((int) _suspendedType) {
				case (int) jsdIExecutionHook.TYPE_INTERRUPTED:
					label.append(isStepping() ? "stepping" : "interrupted"); // TODO
																				// i18n
					break;
				case (int) jsdIExecutionHook.TYPE_BREAKPOINT:
					label.append("at breakpoint"); // TODO i18n
					break;
				case (int) jsdIExecutionHook.TYPE_DEBUG_REQUESTED:
					label.append("at error breakpoint"); // TODO i18n
					break;
				case (int) jsdIExecutionHook.TYPE_DEBUGGER_KEYWORD:
					if (!_reachedFirstLine) {
						label.append("debugger keyword"); // TODO i18n
					} else {
						label.append("suspended at start"); // TODO i18n
					}
					break;
				case (int) jsdIExecutionHook.TYPE_THROW:
					label.append("exception: "); // TODO i18n
					if (_exceptionObject != null) {
						jsdIValue exception = (jsdIValue) _exceptionObject
								.getValue().getAdapter(jsdIValue.class);
						if (exception != null
								&& exception.getJsClassName() == "Error")
							label.append(exception.getProperty("message"));
						else
							label.append(_exceptionObject.getValue()
									.getValueString());
					}
					break;
				case (int) TYPE_RUNNING:
					label.append("running"); // TODO i18n
					break;
				default:
					label.append("unknown"); // TODO i18n
					break;
				}
			} finally {
				label.append(')');
			}
		} catch (DebugException de) {
			MozillaDebugPlugin.log(de);
		}

		return label.toString();
	}

	public void setSuspendOnErrors(boolean suspend) {
		_suspendOnErrors = suspend;
	}

	public void setSuspendOnExceptions(boolean suspend) {
		_suspendOnExceptions = suspend;
	}

	public void setSuspendOnTopLevel(boolean suspend) {
		_suspendOnTopLevel = suspend;
	}

	public boolean onError0(final String message, String fileName, long line,
			long pos, long flags, long errnum, final jsdIValue exception) {
		// MozillaDebugPlugin.log("onError: "+message+" filename="+fileName+" exception="+exception.getStringValue());

		boolean resume = !_suspendOnErrors;
		try {
			int severity = IStatus.OK;
			Throwable throwable = null;

			if ((flags & jsdIErrorHook.REPORT_WARNING) > 0)
				severity = IStatus.WARNING;
			if ((flags & jsdIErrorHook.REPORT_ERROR) > 0) {
				severity = IStatus.ERROR;
			}
			if ((flags & jsdIErrorHook.REPORT_EXCEPTION) > 0) {
				severity = IStatus.INFO;
				throwable = new Throwable() {
					// private String _message = exception.getStringValue();
					private String _stackTrace = JSDebugThread.this._stackTrace;

					public void printStackTrace(PrintStream s) {
						s.println(_stackTrace);
					}

					public void printStackTrace(PrintWriter s) {
						s.println(_stackTrace);
					}

					public String getMessage() {
						return "JavaScript stack trace:\n" + _stackTrace;
					}
				};
			}
			if ((flags & jsdIErrorHook.REPORT_STRICT) > 0)
				severity = IStatus.INFO;

			MozillaDebugPlugin
					.log(new DebugException(new Status(IStatus.ERROR,
							MozillaDebugPlugin.PLUGIN_ID,
							DebugException.REQUEST_FAILED,
							"Mozilla JavaScript Exception: " + message /*
																		 * TODO:i18n
																		 */,
							throwable)));
			_stackTrace = null;
		} catch (RuntimeException re) {
			// This is called by native code, so exceptions will disappear. Log
			// them.
			// Most likely, an XPCOMException due to some mismatch with
			// xulrunner.
			MozillaDebugPlugin.log(re);
			throw re;
		} catch (Error e) {
			MozillaDebugPlugin.log(e);
			throw e;
		}

		return resume; /* false to break */
	}

	private String _stackTrace = null;
	private jsdIContext _cx;

	/*
	 * Special Method used when a refresh in the browser is done when suspended.
	 * Allow scripts to run.
	 */
	public void clearCX() {
		if (isSuspended() && _cx != null)
			_cx.setScriptsEnabled(true);
	}

	private long onExecute0(jsdIStackFrame frame, long type, jsdIValue[] rv) {
		try {
			String[] types = { "TYPE_INTERRUPTED", "TYPE_BREAKPOINT",
					"TYPE_DEBUG_REQUESTED", "TYPE_DEBUGGER_KEYWORD",
					"TYPE_THROW", "RETURN_HOOK_ERROR", "RETURN_CONTINUE",
					"RETURN_ABORT", "RETURN_RET_WITH_VAL",
					"RETURN_THROW_WITH_VAL", "RETURN_CONTINUE_THROW" };
			MozillaDebugPlugin.debug("jsdiExecutionHook.onExecute: pc="
					+ frame.getPc() + " calee=" + frame.getCallingFrame()
					+ " isConstructing=" + frame.getIsConstructing()
					+ " function=" + frame.getFunctionName() + " type="
					+ types[(int) type] + " scope=" + jsdP(frame.getScope())
					+ " this=" + jsdP(frame.getThisValue()) + " ctx="
					+ frame.getExecutionContext().getOptions());

			if (frame.getIsNative())
				return jsdIExecutionHook.RETURN_CONTINUE;

			if (type == jsdIExecutionHook.TYPE_THROW) {
				if (_stackTrace == null || false) { // TODO: need to determine
													// if this is a new
													// exception?
					// fill in stack frame if this is the first time we've seen
					// this exception
					// If we try to do this after creating JSDebugStackFrames,
					// bad things happen to our
					// state, so we construct this directly from jsdIStackFrame
					// instead
					// TODO: perhaps the trace should be passed around as
					// structured data instead of string for the JS Console?
					StringBuffer trace = new StringBuffer();
					for (jsdIStackFrame f = frame; f != null; f = f
							.getCallingFrame()) {
						trace.append(f.getFunctionName()); // TODO: need "guess"
						trace.append("()"/* argList */);
						trace.append(' ');
						boolean isNative = Boolean.valueOf(f.getIsNative())
								.booleanValue();
						if (isNative) {
							trace.append("[native function]");
						} else {
							jsdIScript script = frame.getScript();
							trace.append(script.getFileName());
							trace.append(':');
							trace.append(f.getLine());
						}
						trace.append('\n');
					}
					_stackTrace = trace.toString();
				}

				if (!_suspendOnExceptions)
					return jsdIExecutionHook.RETURN_CONTINUE_THROW;
			}

			// Stop scripts from executing while we're suspended
			_cx = frame.getExecutionContext();
			_cx.setScriptsEnabled(false);

			long result = jsdIExecutionHook.RETURN_ABORT;
			nsIBaseWindow window = (nsIBaseWindow) getDebugTarget()
					.getProcess().getAdapter(nsIBaseWindow.class);
			try {
				try {
					// Prevent user from taking further actions within the
					// browser while suspended
					window.setEnabled(false);
				} catch (XPCOMException xpcome) {
					if (xpcome.errorcode != Mozilla.NS_ERROR_NOT_IMPLEMENTED)
						throw xpcome;
				}

				_suspendedType = type;
				// final jsdIStackFrame aFrame = frame;

				switch ((int) type) {
				case (int) jsdIExecutionHook.TYPE_INTERRUPTED:
					if (isStepping()) {
						boolean theSameFrame = ((_stepPastFrame == null) && (frame
								.getFunctionName() == null))
								|| ((_stepPastFrame != null) && _stepPastFrame
										.equals(frame.getFunctionName()));
						if ((_stepPastDepth == getDepth(frame))
								&& (theSameFrame)
								&& (_stepPastLine == frame.getLine())) {
							MozillaDebugPlugin.debug("stepPast: "
									+ _stepPastFrame + "line: " + _stepPastLine
									+ " depth:" + _stepPastDepth);
							return jsdIExecutionHook.RETURN_CONTINUE;
						}

						_stepPastDepth = -1;
					}
					break;
				case (int) jsdIExecutionHook.TYPE_BREAKPOINT:
					Vector foundBreakpoints = new Vector();
					// TODO match breakpoints with current line of code
					_breakpoints = (IBreakpoint[]) foundBreakpoints
							.toArray(new IBreakpoint[0]);
					break;
				default:
				}

				updateFrames(frame);

				if (type == jsdIExecutionHook.TYPE_THROW) {
					// Add variable reference to exception object
					if (rv.length > 0) {
						jsdIValue exception = rv[0];
						if (exception != null) {
							_exceptionObject = new JSDebugVariable(
									getDebugTarget(), JSDebugThread.this,
									"[exception object]", exception);
							JSDebugStackFrame topFrame = (JSDebugStackFrame) _stackFrames[0];
							topFrame.addVariable(_exceptionObject);
						}
					}
				}

				result = waitLoop();

				if (_stackFrames != null) {
					for (int i = 0; i < _stackFrames.length; i++) {
						JSDebugStackFrame f = (JSDebugStackFrame) _stackFrames[i];
						f.invalidate();
					}
				}
			} finally {
				_suspendedType = TYPE_RUNNING;
				_exceptionObject = null;
				_breakpoints = new IBreakpoint[0];

				// Free up the browser now that we're no longer suspended
				_cx.setScriptsEnabled(true);
				_cx = null;
				try {
					window.setEnabled(true);
				} catch (XPCOMException xpcome) {
					if (xpcome.errorcode != Mozilla.NS_ERROR_NOT_IMPLEMENTED)
						throw xpcome;
				}
			}
			return result;
		} catch (RuntimeException re) {
			// This is called by native code, so exceptions will disappear. Log
			// them.
			// Most likely, an XPCOMException due to some mismatch with
			// xulrunner.
			MozillaDebugPlugin.log(re);
			throw re;
		} catch (Error e) {
			MozillaDebugPlugin.log(e);
			throw e;
		}
	}

	private String jsdP(jsdIValue value) {
		switch ((int) value.getJsType()) {
		case (int) jsdIValue.TYPE_BOOLEAN:
			return Boolean.toString(value.getBooleanValue());
		case (int) jsdIValue.TYPE_DOUBLE:
			return Double.toString(value.getDoubleValue());
		case (int) jsdIValue.TYPE_FUNCTION:
			return "Fn" + value.getJsClassName() + value.getJsFunctionName();
		case (int) jsdIValue.TYPE_INT:
			return Integer.toString(value.getIntValue());
		case (int) jsdIValue.TYPE_NULL:
			return "NuLL";
		case (int) jsdIValue.TYPE_OBJECT:
			jsdIObject obj = value.getObjectValue();
			return obj.getConstructorURL() + ":" + obj.getConstructorLine()
					+ "/" + obj.getCreatorURL() + ":" + obj.getCreatorLine();
		case (int) jsdIValue.TYPE_STRING:
			return value.getStringValue();
		case (int) jsdIValue.TYPE_VOID:
			return "void";
		}

		return "??";
	}

	private long waitLoop() {
		fireSuspendEvent(isStepping() ? DebugEvent.STEP_END
				: DebugEvent.BREAKPOINT);
		JSDebugTarget target = ((JSDebugTarget) getDebugTarget());
		target.unsetFunctionHook();
		fireChangeEvent(DebugEvent.STATE);
		long result = target.enterNestedEventLoop(null/*
													 * new
													 * JSDebugTarget.INestedCallback
													 * () { // public void
													 * onNest() { //
													 * populateFrames(aFrame); }
													 * }
													 */);

		// MozillaDebugPlugin.debug("nestedEventLoop done.  stepping="+_stepping);
		if (!isStepping() && target.getDebuggerService() != null) {
			((JSDebugTarget) getDebugTarget()).unsetInterruptHook();
		}

		target.fireChangeEvent(DebugEvent.STATE);
		fireResumeEvent(isStepping() ? DebugEvent.UNSPECIFIED /* TODO */
		: DebugEvent.CLIENT_REQUEST);

		return result;
	}

	private void onCall0(jsdIStackFrame frame, long type) {
		try {
			String[] callTypes = { "TYPE_TOPLEVEL_START", "TYPE_TOPLEVEL_END",
					"TYPE_FUNCTION_CALL", "TYPE_FUNCTION_RETURN" };
			// MozillaDebugPlugin.debug(MozillaDebugPlugin.DEBUG_JSDCALLHOOK,
			// "jsdiCallHook.onCall(" + frame.getFunctionName() + ", "
			// + callTypes[(int) type] + ")");

			JSDebugTarget target = ((JSDebugTarget) getDebugTarget());

			switch ((int) type) {
			case (int) jsdICallHook.TYPE_TOPLEVEL_START:
				if (_suspendOnTopLevel && (!_reachedFirstLine)) {
					try {
						_reachedFirstLine = true;
						onExecute0(frame,
								jsdIExecutionHook.TYPE_DEBUGGER_KEYWORD,
								new jsdIValue[] {});
					} finally {
					}
				}
				// TODO: optionally break here?
				// unsetInterruptHook();
				break;

			case (int) jsdICallHook.TYPE_FUNCTION_CALL: // function enter
				if (isStepping()) {
					// For stepover, since a function call was made, cancel the
					// interrupt and wait
					// for the function return before breaking.
					((JSDebugTarget) getDebugTarget()).unsetInterruptHook();
				}
				break;

			case (int) jsdICallHook.TYPE_FUNCTION_RETURN: // function exit
				if (isStepping()) {
					// we're called *before* the returning frame is popped from
					// the
					// stack, so we want our depth calculation to be off by one.
					int depth = getDepth(frame) - 1;

					// dd ("Returning: " + frame.functionName +
					// ", target depth: " + _stepOverDepth +
					// ", current depth: " + depth);

					if (depth <= _stepOverDepth) {
						MozillaDebugPlugin
								.debug("step over at target depth of " + depth);
						setInterruptHook();
						// target.unsetFunctionHook();
						// _stepOverDepth = -1;
						// TODO: sometimes this leaves us at the end of the
						// calling instruction, rather than at the next line
					}
				}

				break;

			case (int) jsdICallHook.TYPE_TOPLEVEL_END:
				target.unsetInterruptHook();
				break;

			default:
			}
		} catch (RuntimeException re) {
			// This is called by native code, so exceptions will disappear. Log
			// them.
			// Most likely, an XPCOMException due to some mismatch with
			// xulrunner.
			MozillaDebugPlugin.log(re);
			throw re;
		} catch (Error e) {
			MozillaDebugPlugin.log(e);
			throw e;
		}
	}

	private void onScriptCreated0(jsdIScript script) {
		try {
			String fileName = script.getFileName();
			URL url = null;
			try {
				url = new URL(fileName);
			} catch (MalformedURLException e1) {
				MozillaDebugPlugin.debug("Error creating URL for request: "
						+ fileName);
				return;
			}

			String path = url.getPath();

			MozillaDebugPlugin.debug(
					MozillaDebugPlugin.DEBUG_JSDSCRIPTHOOK,
					"onScriptCreated file=" + fileName + " func="
							+ script.getFunctionName() + " baseline="
							+ script.getBaseLineNumber() + " extent"
							+ script.getLineExtent());
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager().getBreakpoints();
			JSSourceLocator locator = (JSSourceLocator) getLaunch()
					.getSourceLocator();
			if (_breakpointManager.isEnabled()) {
				for (int i = 0; i < breakpoints.length; i++) {
					try {
						if (breakpoints[i].isEnabled()) {
							((JSDebugTarget) getDebugTarget())
									.establishBreakpoint(true, locator, script,
											breakpoints[i]);
						}
					} catch (CoreException e) {
						// Breakpoint doesn't throw this exception
						MozillaDebugPlugin.log(e);
					}
				}
			}

			// TODO: set djconfig.debugAtAllCosts to true for dojo via extension
			// point?

			// create the JSDebugScriptElement
			JSDebugTopScriptElement topScriptElement = null;
			if (topScriptElements.containsKey(path)) {
				topScriptElement = (JSDebugTopScriptElement) topScriptElements
						.get(path);
			} else {
				topScriptElement = new JSDebugTopScriptElement(getDebugTarget());
				topScriptElement.setName(path);
				topScriptElement.setLocation(fileName);

				topScriptElements.put(path, topScriptElement);
			}

		} catch (RuntimeException re) {
			// This is called by native code, so exceptions will disappear. Log
			// them.
			// Most likely, an XPCOMException due to some mismatch with
			// xulrunner.
			MozillaDebugPlugin.log(re);
			throw re;
		} catch (Error e) {
			MozillaDebugPlugin.log(e);
			throw e;
		}
	}

	public IJSDebugScriptElement[] getTopScriptElements() {
		IJSDebugScriptElement[] array = new IJSDebugScriptElement[topScriptElements
				.size()];
		topScriptElements.values().toArray(array);
		return array;
	}

	private void onScriptDestroyed0(jsdIScript script) {
		try {
			MozillaDebugPlugin.debug(MozillaDebugPlugin.DEBUG_JSDSCRIPTHOOK,
					"onScriptDestroyed file=" + script.getFileName() + " func="
							+ script.getFunctionName());
		} catch (RuntimeException re) {
			// This is called by native code, so exceptions will disappear. Log
			// them.
			// Most likely, an XPCOMException due to some mismatch with
			// xulrunner.
			MozillaDebugPlugin.log(re);
			throw re;
		} catch (Error e) {
			MozillaDebugPlugin.log(e);
			throw e;
		}
	}

	private void updateFrames(jsdIStackFrame topFrame) {
		// MozillaDebugPlugin.debug("STACK FRAME: "+topFrame.getFunctionName()+":"+topFrame.getLine()+" file="+topFrame.getScript().getFileName());
		// List frames = new ArrayList();
		// int depth = getDepth(topFrame);
		// jsdIStackFrame frame = topFrame;
		// while (frame != null)
		// {
		// frames.add(new JSDebugStackFrame(getDebugTarget(), this, this, frame,
		// depth--));
		// frame = frame.getCallingFrame();
		// }
		//
		// _stackFrames = (IStackFrame[])frames.toArray(new IStackFrame[0]);

		// Create a new call stack to reflect the chain pointed to by topFrame,
		// but preserve JSDebugStackFrames which persist from the previous call
		// as appropriate

		IStackFrame[] newStack = _emptyStack;

		try {
			final jsdIStackFrame[] newFrames = getFullStack(topFrame);
			final int newLength = newFrames.length;

			// Starting at the bottom of both stacks, keep those which are
			// identical
			int i = _stackFrames.length - 1;
			for (int j = newFrames.length - 1; i >= 0 && j >= 0; i--, j--) {
				JSDebugStackFrame oldFrame = (JSDebugStackFrame) _stackFrames[i];
				jsdIStackFrame newFrame = newFrames[j];

				if (!oldFrame.isUnchangedFrom(newFrame)) {
					if (oldFrame.isSameAs(newFrame)) {
						// Frame is the same, but the pc moved, so include one
						// more
						i--;

						// And refresh its data
						oldFrame.validate(newFrame);
					}
					break;
				}

				// Provide a fresh XPCOM frame pointer and refresh data in the
				// cached object as necessary.
				// This will also update the children (variables) which may have
				// changed since the last break.
				// Even though the unchanged stack frames should not have had
				// any variable changes, we cannot
				// guarantee that the frames are identical, so refresh anyway.
				oldFrame.validate(newFrame);
			}

			// MozillaDebugPlugin.debug("call frames the same down to i="+i);

			// If all stack frames are the same, then we're done.
			if ((i < 0) && (_stackFrames.length == newLength))
				return;

			// Allocate a new array of the right size
			newStack = new IStackFrame[newLength];

			// Copy over the shared frame objects
			int n = newLength - 1;
			for (int m = _stackFrames.length - 1; m > i; m--, n--) {
				newStack[n] = _stackFrames[m];
			}

			// Now add new frames, if any
			for (; n >= 0; n--) {
				newStack[n] = new JSDebugStackFrame(getDebugTarget(), this,
						newFrames[n], newLength - 1 - n, _reachedFirstLine);
			}
		} catch (RuntimeException re) {
			// Under unexpected failures, like XPCOMExceptions, be sure to still
			// set the debugger state
			// and follow through with the wait loop
			MozillaDebugPlugin.log(re);
		}

		_stackFrames = newStack;
	}

	private int getDepth(jsdIStackFrame frame) {
		int depth = -1;

		while (frame != null) {
			frame = frame.getCallingFrame();
			depth++;
		}

		return depth;
	}

	private jsdIStackFrame[] getFullStack(jsdIStackFrame topFrame) {
		List frames = new ArrayList();

		for (jsdIStackFrame frame = topFrame; frame != null; frame = frame
				.getCallingFrame()) {
			frames.add(frame);
		}

		return (jsdIStackFrame[]) frames.toArray(new jsdIStackFrame[0]);
	}

	private boolean isCorrectContext(jsdIStackFrame frame) {

		nsIWebBrowser browser = (nsIWebBrowser) getDebugTarget().getProcess()
				.getAdapter(nsIWebBrowser.class);
		nsIDOMWindow window = browser.getContentDOMWindow();
		nsIDOMDocument document = window.getDocument();
		nsIDOMNSDocument domNSDoc = (org.mozilla.interfaces.nsIDOMNSDocument) document
				.queryInterface(org.mozilla.interfaces.nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID);
		nsIDOMLocation location = domNSDoc.getLocation();

		jsdIContext context = frame.getExecutionContext();
		jsdIValue globalObject = context.getGlobalObject();
		jsdIProperty prop = globalObject.getProperty("location");
		jsdIValue value = prop.getValue();
		String frameLoc = "";
		MozillaDebugPlugin.debug("Window href: " + location.getHref());
		if (value.getIsValid()) {
			frameLoc = value.getStringValue();
			MozillaDebugPlugin.debug("Frame Location: " + frameLoc);
			if (frameLoc.equalsIgnoreCase(location.getHref()))
				return true;
		}

		return false;
	}

	public void setInterruptHook() {
		MozillaDebugPlugin.debug("setInterruptHook(thread)");
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setInterruptHook(hook);
	}

	public void setDebuggerHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setDebuggerHook(hook);
	}

	public void setThrowHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setThrowHook(hook);
	}

	public void setDebugHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setDebugHook(hook);
	}

	public void setBreakpointHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setBreakpointHook(hook);
	}

	public void setTopLevelHook() {
		MozillaDebugPlugin.debug("setTopLevelHook(thread)");
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setTopLevelHook(hook);
	}

	public void setFunctionHook() {
		MozillaDebugPlugin.debug("setFunctionHook(thread)");
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setFunctionHook(hook);
	}

	public void setErrorHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setErrorHook(hook);
	}

	public void setScriptHook() {
		JSDebugTarget target = (JSDebugTarget) getDebugTarget();
		target.getDebuggerService().setScriptHook(hook);
	}

	private List events = new ArrayList();

	private void postJavaScriptCallEvent(jsdIStackFrame frame, boolean start) {
		String eventType;

		ApplicationEvent event;
		if (start) {
			ITimedEvent parent = null;
			Map data = new HashMap();
			data.put(JAVASCRIPT_SOURCE_URI, frame.getScript().getFileName());
			data.put(JAVASCRIPT_FUNCTION_NAME, frame.getFunctionName());
			data.put(JAVASCRIPT_FUNCTION_STARTLINE, (int) frame.getLine());
			JSDebugTarget target = (JSDebugTarget) getDebugTarget();
			IWebBrowser wb = (IWebBrowser) target.getProcess().getAdapter(
					IWebBrowser.class);
			event = new ApplicationEvent(wb, JAVASCRIPT_FUNCTION, parent, data);
			events.add(event);
			return;
		}

		event = (ApplicationEvent) events.remove(events.size() - 1);
		event.setLength(System.currentTimeMillis() - event.getStartTime());

		IApplicationEventAdmin adm = MozillaDebugPlugin.getDefault()
				.getApplicationEventAdmin();
		adm.postEvent(event);
	}
}
