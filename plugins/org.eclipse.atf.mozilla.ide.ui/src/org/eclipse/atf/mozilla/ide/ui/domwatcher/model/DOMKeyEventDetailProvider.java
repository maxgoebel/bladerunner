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
import org.mozilla.interfaces.nsIDOMKeyEvent;

public class DOMKeyEventDetailProvider implements IDOMEventDetailProvider {

	protected static final String [] KEY_SET_1 = {
		"CANCEL",
		"",
		"",
		"HELP",
		"",
		"BACK_SPACE",
		"TAB",
		"",
		"",
		"CLEAR",
		"RETURN",
		"ENTER",
		"",
		"SHIFT",
		"CONTROL",
		"ALT",
		"PAUSE",
		"CAPS_LOCK",
		"",
		"",
		"",
		"",
		"",
		"",
		"ESCAPE",
		"",
		"",
		"",
		"",
		"SPACE",
		"PAGE_UP",
		"PAGE_DOWN",
		"END",
		"HOME",
		"LEFT",
		"UP",
		"RIGHT",
		"DOWN",
		"",
		"",
		"",
		"PRINTSCREEN",
		"INSERT",
		"DELETE"
	};
	
	protected static final String [] KEY_SET_2 = {
		"NUMPAD0",
		"NUMPAD1",
		"NUMPAD2",
		"NUMPAD3",
		"NUMPAD4",
		"NUMPAD5",
		"NUMPAD6",
		"NUMPAD7",
		"NUMPAD8",
		"NUMPAD9",
		"MULTIPLY",
		"ADD",
		"SEPARATOR",
		"SUBTRACT",
		"DECIMAL",
		"DIVIDE",
		"F1",
		"F2",
		"F3",
		"F4",
		"F5",
		"F6",
		"F7",
		"F8",
		"F9",
		"F10",
		"F11",
		"F12",
		"F13",
		"F14",
		"F15",
		"F16",
		"F17",
		"F18",
		"F19",
		"F20",
		"F21",
		"F22",
		"F23",
		"F24"
	};
	
	protected static final String [] KEY_SET_3 = {
		"COMMA",
		"",
		"PERIOD",
		"SLASH"	
	};	
	
	protected static final String [] KEY_SET_4 = {
		"OPEN_BRACKET",
		"BACK_SLASH",
		"CLOSE_BRACKET",
		"QUOTE"
	};
	
	public String getDetail(nsIDOMEvent event) {
		nsIDOMKeyEvent kEvent = (nsIDOMKeyEvent)event.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
		StringBuffer details = new StringBuffer();
			
		if( kEvent.getShiftKey() )
			details.append( "Shift " );
		
		if( kEvent.getCtrlKey() )
			details.append( "Ctrl " );
		
		if( kEvent.getAltKey() )
			details.append( "Alt " );
		
		if( kEvent.getMetaKey() )
			details.append( "Meta " );
		
		details.append( "charCode:" );
		details.append( charCodeAsString((int)kEvent.getCharCode()) );
		
		details.append( " keyCode:" );
		details.append( keyCodeAsString((int)kEvent.getKeyCode()) );
		
		return details.toString();
	}
	
	protected String keyCodeAsString( int keyCode ){
		
		if( keyCode == 0 ){
			return "0";
		}
		
		StringBuffer code = new StringBuffer();
		code.append( keyCode );
		
		code.append( '(' );
		
		//from nsIDOMKeyEvent.idl
		if( keyCode >= 0x03 && keyCode <= 0x2E ){
			code.append( KEY_SET_1[keyCode-0x03] );
		}
		
		//0-9
		else if ( keyCode >= 0x30 && keyCode <= 0x39 ){
			code.append( (char)keyCode );
		}
		
		else if ( keyCode == 0x3B ){
			code.append( ';' );
		}
		
		else if ( keyCode == 0x3D ){
			code.append( '=' );
		}
		
		//A-Z
		else if ( keyCode >= 0x41 && keyCode <= 0x5A ){
			code.append( (char)keyCode );
		}
		
		else if ( keyCode == 0x5D ){
			code.append( "CONTEXT_MENU" );
		}
		
		else if( keyCode >= 0x60 && keyCode <= 0x87 ){
			code.append( KEY_SET_2[keyCode-0x60] );
		}
		
		else if ( keyCode == 0x90 ){
			code.append( "NUM_LOCK" );
		}
		
		else if ( keyCode == 0x91 ){
			code.append( "SCROLL_LOCK" );
		}
		
		else if( keyCode >= 0xBC && keyCode <= 0xBF ){
			code.append( KEY_SET_3[keyCode-0xBC] );
		}
		
		else if ( keyCode == 0xC0 ){
			code.append( "BACK_QUOTE" );
		}
		
		else if( keyCode >= 0xDB && keyCode <= 0xDE ){
			code.append( KEY_SET_4[keyCode-0xDB] );
		}
		
		else if ( keyCode == 0xE0 ){
			code.append( "META" );
		}
		
		code.append( ')' );
		
		return code.toString();
	}
	
	protected String charCodeAsString( int charCode ){
		if( charCode == 0 ){
			return "0";
		}
		
		StringBuffer code = new StringBuffer();
		code.append( charCode );
		code.append( '(' );
		code.append( (char)charCode );
		code.append( ')' );
		return code.toString();
	}

}
