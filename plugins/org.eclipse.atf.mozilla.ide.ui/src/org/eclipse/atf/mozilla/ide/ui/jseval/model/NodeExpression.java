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

import org.mozilla.interfaces.nsIDOMNode;


public class NodeExpression extends EvalExpression{
	protected nsIDOMNode context;
	
	public NodeExpression( nsIDOMNode node, String expression, IJSValue result ) {
		super( expression, result );
		
		this.context = node;
	}
	
	public nsIDOMNode getContext(){
		return context;
	}
}
