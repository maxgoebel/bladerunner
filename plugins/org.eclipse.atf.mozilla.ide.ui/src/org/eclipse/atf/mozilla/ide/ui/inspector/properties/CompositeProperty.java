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

package org.eclipse.atf.mozilla.ide.ui.inspector.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mozilla.interfaces.nsIDOMNode;

/*
 * This implementation is used to aggregate a set of strategies
 */
public class CompositeProperty implements IDOMInspectorProperty {

	protected String displayName = "";
	protected List properties = new ArrayList();
	
	protected String separator = " ";
	
	public CompositeProperty( String displayName ){
		this.displayName = displayName;		
	}
	
	public CompositeProperty( String displayName, String separator ){
		this( displayName );
		this.separator = separator;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}

	public String getValue(nsIDOMNode node) {
		
		StringBuffer value = new StringBuffer();
		
		for (Iterator iter = properties.iterator(); iter.hasNext();) {
			IDOMInspectorProperty property = (IDOMInspectorProperty) iter.next();
			
			value.append( property.getValue(node) );
			
			if( iter.hasNext() )
				value.append( separator );
			
		}
		
		return value.toString();
	}

	public void addProperties( IDOMInspectorProperty property ){
		properties.add( property );
	}
	
	public void removeProperties( IDOMInspectorProperty property ){
		properties.remove( property );
	}
}
