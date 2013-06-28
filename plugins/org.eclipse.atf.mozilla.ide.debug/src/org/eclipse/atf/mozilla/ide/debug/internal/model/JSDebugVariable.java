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

import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.mozilla.interfaces.jsdIValue;

/**
 * @author Adam
 *
 */
public class JSDebugVariable extends JSDebugElement implements IVariable {

	private String _name;
	private IValue _value;
	private boolean isInternal;

	public JSDebugVariable(IDebugTarget target, IThread thread, String name, jsdIValue initialValue, boolean isInternal) {
		this(target, thread, name, initialValue);
		this.isInternal = isInternal;
	}

	/**
	 * @param target
	 * @param parent
	 */
	public JSDebugVariable(IDebugTarget target, IThread thread, String name, jsdIValue initialValue) {
		super(target);

		_name = name;

		if (initialValue != null) {
			initialValue = (jsdIValue) XPCOMThreadProxy.createProxy(initialValue, ((JSDebugTarget) target).getProxyHelper());
			_value = new JSDebugValue(getDebugTarget(), thread, initialValue);
			//			value.setValueString(initialValue);
			//			value.setReferenceTypeName(_type);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement#getLabel()
	 */
	public String getLabel() {
		StringBuffer buffer = new StringBuffer(_name);
		try {
			buffer.append("=");
			buffer.append(getValue().getValueString());
		} catch (DebugException de) {
			return _name;
		}
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return _value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return _name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return _value.getReferenceTypeName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	public void setValue(String expression) throws DebugException {
		_value = null;

		// TODO
		//		IDebugTarget target = getDebugTarget();
		//		ILZXDebugConnector connect = ((JSDebugTarget)target).getDebugConnector();
		//		if (connect == null)
		//			throw new DebugException(new Status(IStatus.ERROR, MozillaDebugPlugin.getPluginId(),
		//					IStatus.OK, "Unable to connect", null)); //TODO i18n
		//
		//		String pending = "<pending>";//TODO i18n
		//		IJSValue parentValue = (IJSValue)getParent();
		//		String id = parentValue.getID();
		//		String property = getName();
		//		IJSValue thisValue = (IJSValue)getValue();
		//		if (MozillaDebugPlugin.isSetSupported) {
		//			ILZXDebugDataCallback callback = new LZXSetCallback(thisValue, expression); //TODO
		//			connect.set(callback, id, property, expression);
		//		} else {
		//			ILZXDebugDataCallback callback = new LZXEvalExpressionCallback(thisValue);
		//			connect.eval(callback, makeGlobalRef() + '=' + expression, null);
		//		}
		//
		//		thisValue.setValueString(pending);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
		_value = null; //TODO

		throw new DebugException(new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, IStatus.OK, "not supported", null));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return false; //TODO
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Internal variable is either prototype, constructor or parent
	 * @return
	 */
	public boolean isInternal() {
		return isInternal;
	}
}