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

public class EvalExpression {
	protected String expression;
	protected IJSValue result;
	
	public EvalExpression( String expression, IJSValue result ) {
		this.expression = expression;
		this.result = result;
		
		this.result.setExpression( this );
	}
	
	public String getExpression() {
		return expression;
	}
	public IJSValue getResult() {
		return result;
	}
	
	
}
