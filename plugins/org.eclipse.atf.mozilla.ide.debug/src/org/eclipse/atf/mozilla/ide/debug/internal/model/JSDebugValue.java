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

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.mozilla.interfaces.jsdIProperty;
import org.mozilla.interfaces.jsdIValue;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSDebugValue extends JSDebugElementWithVariables implements IValue {

	//TODO implement a value class with IIndexedValue for enhanced display of arrays
	private jsdIValue _value;
	private String _valueString; //TODO need to cache this?
	private String _typeName = null;
	private boolean _compound;
	private boolean _initializedVars;
	private IThread _thread;

	/**
	 * @param parent
	 */
	public JSDebugValue(IDebugTarget target, IThread thread, jsdIValue value) {
		super(target);

		_thread = thread;
		_value = value;
		//		__compound = value.getPropertyCount() > 0;
		_compound = !(_value.getIsNumber() || _value.getIsPrimitive());
	}

	//	/**
	//	 * @param parent
	//	 */
	//	public JSDebugValue(IDebugTarget target, String parentExpression, IDebugElement parent) {
	//		super(target, parent);
	//		_parentExpression = parentExpression;
	//	}
	//
	//	/**
	//	 * @param parent
	//	 */
	//	public JSDebugValue(IDebugTarget target, IDebugElement parent, String id) {
	//		super(target, parent);
	//		_id = id;
	//	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement#getLabel()
	 */
	public String getLabel() {
		try {
			return getValueString();
		} catch (DebugException de) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		if (_typeName == null) {
			//MozillaDebugPlugin.debug(_value.getJsClassName());
			switch ((int) _value.getJsType()) {
			case (int) jsdIValue.TYPE_BOOLEAN:
				_typeName = "boolean";
				break;
			case (int) jsdIValue.TYPE_DOUBLE:
				_typeName = "double";
				break;
			case (int) jsdIValue.TYPE_INT:
				_typeName = "int";
				break;
			case (int) jsdIValue.TYPE_FUNCTION:
				_typeName = "function";
				break;
			case (int) jsdIValue.TYPE_NULL:
				_typeName = "null";
				break;
			case (int) jsdIValue.TYPE_OBJECT:
				_typeName = "object";
				break;
			case (int) jsdIValue.TYPE_STRING:
				_typeName = "string";
				break;
			case (int) jsdIValue.TYPE_VOID:
				_typeName = "void";
				break;
			default:
				_typeName = "unknown";
			}
		}

		return _typeName;
	}

	//	public void setReferenceTypeName(String typeName) {
	//		_typeName = typeName;
	//	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		if (_valueString == null)
			_valueString = _value.getStringValue();

		return _valueString;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String setValueString(String valueString) {
		return null; //TODO
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElementWithVariables#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		if (!_initializedVars) {
			initializeVars();
		}

		//TODO		return filterVariables(super.getVariables());
		return super.getVariables();
	}

	//	private IVariable[] filterVariables(IVariable[] vars) throws DebugException {
	//		//TODO: make this settable by toolbar actions in the UI.  Separate settings
	//		// for function, private, protected, etc.
	//		//TODO: cache the results
	//		Vector filteredVector = new Vector();
	//		for (int i = 0; i < vars.length; i++) {
	//			IVariable var = vars[i];
	//			String name = var.getName();
	//			if (name.startsWith("_"))
	//				continue;
	//			String type = var.getReferenceTypeName();
	//			if ("function".equalsIgnoreCase(type))
	//				continue;
	//			filteredVector.add(var);
	//		}
	//		IVariable[] filteredVars = new IVariable[filteredVector.size()];
	//		filteredVector.copyInto(filteredVars);
	//		return filteredVars;
	//	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElementWithVariables#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		if (!_initializedVars) {
			//			TODO
			return _compound;
		}

		return super.hasVariables();
	}

	private void initializeVars() throws DebugException {

		synchronized (_thread) {
			if (_initializedVars)
				return;

			jsdIValue proto = _value.getJsPrototype();
			if (proto != null)
				addVariable(new JSDebugVariable(getDebugTarget(), _thread, "prototype", proto, true));

			jsdIValue constr = _value.getJsConstructor();
			if (constr != null)
				addVariable(new JSDebugVariable(getDebugTarget(), _thread, "constructor", constr, true));

			jsdIValue parent = _value.getJsParent();
			if (parent != null)
				addVariable(new JSDebugVariable(getDebugTarget(), _thread, "parent", parent, true));

			jsdIProperty propArray[][] = new jsdIProperty[1][];
			long lengthRef[] = new long[1];
			_value.getProperties(propArray, lengthRef);
			//		MozillaDebugPlugin.debug("Value.initializeVars() length="+lengthRef[0]);

			//SORT THE Properties
			if (lengthRef[0] > 0) {
				Arrays.sort(propArray[0], 0, (int) (lengthRef[0]), new Comparator() {

					public int compare(Object val1, Object val2) {

						try {
							String prop1 = ((jsdIProperty) val1).getName().getStringValue();
							String prop2 = ((jsdIProperty) val2).getName().getStringValue();
							return prop1.compareTo(prop2);
						} catch (Exception e) {
							MozillaDebugPlugin.log(e);
							return 0;
						}
					}

				});
			}

			for (int i = 0; i < lengthRef[0]; i++) {
				jsdIProperty prop = propArray[0][i];
				String name = prop.getName().getStringValue();
				jsdIValue value = prop.getValue();
				//			MozillaDebugPlugin.debug("Prop "+i+":"+name);
				addVariable(new JSDebugVariable(getDebugTarget(), _thread, name, value));
			}

			_initializedVars = true;
		}
	}

	public Object getAdapter(Class adapter) {
		if (jsdIValue.class.equals(adapter)) {
			return _value;
		}

		return super.getAdapter(adapter);
	}
}
