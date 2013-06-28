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
package org.eclipse.atf.mozilla.ide.ui.domwatcher.model;

import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMMutationEvent;

public class DOMMutationEventDetailProvider implements IDOMEventDetailProvider {

	public String getDetail(nsIDOMEvent event) {
		nsIDOMMutationEvent mutEvent = (nsIDOMMutationEvent)event.queryInterface(nsIDOMMutationEvent.NS_IDOMMUTATIONEVENT_IID);
		
		StringBuffer details = new StringBuffer();
		
		if( "DOMNodeInserted".equals(event.getType()) ){
			details.append( "Node Inserted" );
		}
		else if( "DOMNodeRemoved".equals(event.getType()) ){
			details.append( "Node Removed" );
		}
		else if( "DOMAttrModified".equals(event.getType()) ){
			details.append( "Attribute " );
			details.append( mutEvent.getAttrName() );
			details.append( " new value<" );
			details.append( mutEvent.getNewValue() );
			details.append( "> old value<" );
			details.append( mutEvent.getPrevValue() );
			details.append( ">" );
			
		}
		return details.toString();
	}

}