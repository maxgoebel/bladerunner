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

package org.eclipse.atf.mozilla.ide.debug.internal.model;

import java.util.Vector;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class JSDebugElementWithVariables extends JSDebugElement {

	protected Vector _variables = null;

	/**
	 * @param target
	 * @param parent
	 */
	public JSDebugElementWithVariables(IDebugTarget target) {
		super(target);
	}

	public IVariable[] getVariables() throws DebugException {
//		PERF: cache these arrays?
		
		// If there are no children do not return null, this causes
		// a problem for the local variables and you get a null pointer 
		// exception. 
		if (_variables == null || _variables.size() == 0)
			return new IVariable[0];
			
		int numberOfChildren = _variables.size();
					
		IVariable[] variables = new IVariable[numberOfChildren];
		_variables.copyInto(variables);
		
		return variables;
	}

	public boolean hasVariables() throws DebugException {
		return _variables != null && _variables.size() != 0;
	}

	public void addVariable(JSDebugVariable variable) {
		if (_variables == null)
			_variables = new Vector();
		_variables.addElement(variable);
		variable.fireCreationEvent(); 
	}
}
