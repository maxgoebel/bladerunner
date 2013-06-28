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
if( !ATF ){
	var ATF = {
		INTERNAL_ID: "___ATF_INTERNAL"
		
	};
}

if( !ATF.Eval ){
	ATF.Eval = {
		eval_in: null,
		eval_out: null,
		isInit: false
	};
	
	/*
	 * <div style="display: none;">					
	 * 		<input />
	 *		<xml></xml>		
	 * </div>
	 */
	ATF.Eval.init = function(){
		if( this.isInit )
			return;
		
		//alert("ATF.Eval.init()");
		var wrap = document.createElement( "DIV" );
		wrap.setAttribute( "id", ATF.INTERNAL_ID+"_EVAL" );
		wrap.setAttribute( "class", ATF.INTERNAL_ID );
		wrap.setAttribute( "style", "display: none;" );
		
		this.eval_in = document.createElement( "INPUT" );
		wrap.appendChild( this.eval_in );
		
		this.eval_out = document.createElement( "TEXTAREA" );
		wrap.appendChild( this.eval_out );
		
		document.body.appendChild( wrap );
		
		//send ready event
		this.connect();
		
		document.addEventListener( "ATF_EVAL_SET", function( event ){
			ATF.Eval.eval.call( ATF.Eval, event );
		}, true );
		
		document.addEventListener( "ATF_EVAL_RECONNECT", function( event ){
			ATF.Eval.connect.call( ATF.Eval, event );
		}, true );
		
		this.isInit = true;
	}
	
	/*
	 * Sends a ready event to the JAVA side so that it can set the references
	 * to the page components used for eval
	 */
	ATF.Eval.connect = function(){
		//send ready event
		var event = document.createEvent( "Event" );
		event.initEvent( "ATF_EVAL_READY", false, false );
		this.eval_in.dispatchEvent( event );
	}
	
	ATF.Eval.eval = function( event ){
		try{
			var ret;
			if( event.target == this.eval_in ){
				var expr = this.eval_in.value;
				ret = window.eval( expr );
				
			}
			else{
				var context = event.target;
				var expr = this.eval_in.value;
				if( expr == "" || expr === null ){
					ret = context;
				}
				else{
					ret = context.eval( expr );
				}
			}
			
			this.eval_out.value = this.serialize(ret, true);
			
			//send an event to notify that eval is done
			var event = document.createEvent( "Event" );
			event.initEvent( "ATF_EVAL_DONE", false, false );
			this.eval_out.dispatchEvent( event );
		}
		catch( e ){
		
			this.eval_out.value = "<error>"+e+"</error>";
			
			//send an event to notify that eval is done
			var event = document.createEvent( "Event" );
			event.initEvent( "ATF_EVAL_ERROR", false, false );
			this.eval_out.dispatchEvent( event );
		}
	}
	
	ATF.Eval.serialize = function( obj, deep ){
		var ser = "";
		
		var type = typeof obj;
		
		
		if( obj === null ){
			ser += "<null/>";
		}
		else if( type === "undefined" ){
			//alert("STRING");
			ser += "<undefined/>";
		}
		else if( type === "boolean" ){
			ser += "<boolean>"+obj+"</boolean>";
		}
		else if( type === "string" ){
			//alert("STRING");
			//var sub = obj.length > 100? obj.substr(0,97)+"...": obj;
			
			//ser += "<string>"+escape(obj)+"</string>";
			ser += "<string>"+"<![CDATA["+escape(obj)+"]]>"+"</string>";
		}
		else if( type === "number" ){
			//alert("NUMBER");
			ser += "<number>"+obj+"</number>";
		}
		else if( type === "function" ){
			//alert("FUNCTION");
			ser += "<function>";
			
			if( deep ){
				for( i in obj ){
					
					ser += this.serializeProperty( obj, i );
				}
			}
			
			ser += "</function>"
		}
		else if( type === "object" ){
			//alert("OBJECT");
			ser = "<object>\n";
			
			if( deep ){
				for( i in obj ){
					
					ser += this.serializeProperty( obj, i );
				}
			}
			
			ser += "</object>"
		}
		
		
		return ser;
	}
	
	ATF.Eval.serializeProperty = function( obj, prop ){
			var ser = "";
		
		ser += "\t<property name='"+prop+"'>";
		try{
			
			ser += this.serialize( obj[prop], false );
			
		}
		catch( e ){
			ser += "\t<error>"+e+"</error>\n";
		}
		ser += "</property>\n";
		
		
		return ser;
	}
	
	ATF.Eval.init();
}