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

import java.util.HashMap;

public class JSObject extends BaseJSValue {
	
	protected HashMap props = new HashMap();
	
	public boolean evaluated = false;
	
	public String getType(){
		return IJSValue.OBJECT;
	}
	
	public void addProperty( JSObjectProperty prop ){
		prop.parentObject = this;
		props.put( prop.name, prop );
	}
	
	public JSObjectProperty[] getProperties(){
		JSObjectProperty [] ret = new JSObjectProperty[ props.size() ];
		return (JSObjectProperty [])props.values().toArray(ret);
	}
	
	public void setProperties( JSObjectProperty [] propsIn ){
		props.clear();
		for (int i = 0; i < propsIn.length; i++) {
			addProperty( propsIn[i] );
		}
		
	}
}
