/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IIndexedValue;
import org.eclipse.debug.core.model.ILogicalStructureTypeDelegate;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSLogicalStructureTypeDelegate implements
		ILogicalStructureTypeDelegate {

	/**
	 * 
	 */
	public JSLogicalStructureTypeDelegate() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILogicalStructureTypeDelegate#providesLogicalStructure(org.eclipse.debug.core.model.IValue)
	 */
	public boolean providesLogicalStructure(IValue value) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILogicalStructureTypeDelegate#getLogicalStructure(org.eclipse.debug.core.model.IValue)
	 */
	public IValue getLogicalStructure(IValue value) throws CoreException {
		final IValue v = value;
		return new IIndexedValue() {
			private boolean _initialized = false;
			private IVariable[] _variables = null;
			public String getReferenceTypeName() throws DebugException {
				return v.getReferenceTypeName();
			}
			public String getValueString() throws DebugException {
				return v.getValueString();
				
			}
			public boolean isAllocated() throws DebugException {
				return v.isAllocated();
			}
			public IVariable[] getVariables() throws DebugException {
				if (!_initialized) {
					IVariable[] variables = v.getVariables();
					int len = (variables == null) ? 0 : variables.length;
					IVariable subviews = null;
					for (int i = 0; i < len; i++) {
						if ("subviews".equals(variables[i].getName())) {
							subviews = variables[i];
							break;
						}
					}
					_variables = (subviews == null) ? new IVariable[0] : sort(subviews.getValue().getVariables());
					_initialized = true;
				}
				return _variables;
			}
			private IVariable[] sort(IVariable[] vars) throws DebugException {
				IVariable[] sortedVars = new IVariable[vars.length];
				for (int i = 0; i < vars.length; i++) {
					int index = Integer.parseInt(vars[i].getName());
					sortedVars[index] = vars[i];
				}
				return sortedVars;
			}
			public boolean hasVariables() throws DebugException {
				boolean result = v.hasVariables();
				if (result) {
					IVariable[] variables = getVariables();
					result = (variables != null) && (variables.length > 0);
				}
				return result;				
			}
			public String getModelIdentifier() {
				return v.getModelIdentifier();
			}
			public IDebugTarget getDebugTarget() {
				return v.getDebugTarget();
			}
			public ILaunch getLaunch() {
				return v.getLaunch();
			}
			public Object getAdapter(Class adapter) {
				if (adapter.equals(IIndexedValue.class)) {
					return this;
				}
				
				return v.getAdapter(adapter);
			}

			// implement IIndexedValue
			public IVariable getVariable(int offset) throws DebugException {
				return getVariables()[offset];
			}
			public IVariable[] getVariables(int offset, int length) throws DebugException {
				IVariable[] vars = new IVariable[length];
				System.arraycopy(getVariables(), offset, vars, 0, length);
				return vars;
			}
			public int getSize() throws DebugException {
				return getVariables().length;
			}
			public int getInitialOffset() {
				return 0;
			}
		};
	}
}
