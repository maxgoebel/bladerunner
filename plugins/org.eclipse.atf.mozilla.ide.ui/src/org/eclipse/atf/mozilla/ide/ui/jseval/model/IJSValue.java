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

public interface IJSValue {
	final static String NUMBER = "number";
	final static String STRING = "string";
	final static String FUNCTION = "function";
	final static String OBJECT = "object";
	final static String NULL = "null";
	final static String BOOLEAN = "boolean";
	final static String UNDEFINED = "undefined";
	final static String ERROR = "error";
	
	String getType();
	JSObjectProperty getParentProperty();
	
	EvalExpression getExpression();
	void setExpression( EvalExpression expression );
	
	//set the expression
}
