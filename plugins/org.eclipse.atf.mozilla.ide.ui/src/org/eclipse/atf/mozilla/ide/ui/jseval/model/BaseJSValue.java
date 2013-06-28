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
package org.eclipse.atf.mozilla.ide.ui.jseval.model;

public abstract class BaseJSValue implements IJSValue {

	protected JSObjectProperty parentProperty = null;
	
	protected EvalExpression expression = null;
	
	public JSObjectProperty getParentProperty() {
		return parentProperty;
	}
	
	public void setParentProperty( JSObjectProperty parentProperty ){
		this.parentProperty = parentProperty;
	}

	public EvalExpression getExpression() {
		return expression;
	}

	public void setExpression(EvalExpression expression) {
		this.expression = expression;
	}
	
	
}
