/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/
/*
 * This is used to communicate println to the ATF JavaScript Console
 */
dojo.provide("dojo.debug.ATF");

dojo.debug.ATF = function(){}
dojo.debug.ATF.logConsoleMessage = function () {
	
	//log one argument at a time
	for (var i = 0; i < arguments.length; i++){
		
		//create a SPAN with the text message inside
		var span = document.createElement( "SPAN" );
		span.innerHTML = dojo.string.escape( "xml", arguments[i] );
		
		/*
		 * This will create an nsIDOMEvent
		 */
		var ev = document.createEvent("mutationevents");
		ev.initMutationEvent("ATFConsoleLog", false, false, span, null, null, null, null );
		
		document.dispatchEvent(ev);	
		
	}
	
}

if (dojo.render.html.moz) {
	dojo.hostenv.println=dojo.debug.ATF.logConsoleMessage;
}
