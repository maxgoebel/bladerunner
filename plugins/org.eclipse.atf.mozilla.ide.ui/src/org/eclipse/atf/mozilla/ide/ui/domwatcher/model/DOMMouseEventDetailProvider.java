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
import org.mozilla.interfaces.nsIDOMMouseEvent;

public class DOMMouseEventDetailProvider implements IDOMEventDetailProvider{

	public String getDetail(nsIDOMEvent event) {
		
		nsIDOMMouseEvent mEvent = (nsIDOMMouseEvent)event.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
		StringBuffer details = new StringBuffer();
		details.append( "screen(" );
		details.append( mEvent.getScreenX() );
		details.append( ',' );
		details.append( mEvent.getScreenY() );
		details.append( ") " );
		
		details.append( "client(" );
		details.append( mEvent.getClientX() );
		details.append( ',' );
		details.append( mEvent.getClientY() );
		details.append( ") " );
		
		if( "click".equals(event.getType()) || "mousedown".equals(event.getType()) || "mouseup".equals(event.getType()) ){ 
			switch( mEvent.getButton() ){
			
			case 0:
				details.append( "LEFT " );
				break;
				
			case 1:
				details.append( "MIDDLE " );
				break;
				
			case 2:
				details.append( "RIGHT " );
				break;
			}
		}
		
		if( mEvent.getShiftKey() )
			details.append( "Shift " );
		
		if( mEvent.getCtrlKey() )
			details.append( "Ctrl " );
		
		if( mEvent.getAltKey() )
			details.append( "Alt " );
		
		if( mEvent.getMetaKey() )
			details.append( "Meta " );
		
		return details.toString();
	}

}
