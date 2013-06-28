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
package org.eclipse.atf.mozilla.ide.ui.domwatcher;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.DOMEvent;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.IDOMEvent;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.settings.DOMWatcherSettings;
import org.eclipse.atf.mozilla.ide.ui.util.DOMNodeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

public class Watcher{
	
	/* This is the node that is being watched as the target of DOM events */
	protected nsIDOMNode watchedNode = null;
	
	/* List of all the DOM events that target the watchedNode while the Watcher is running */
	protected List events = new ArrayList();
	
	/* Watch state of the Watcher */
	protected boolean watching = false;
	
	/* 
	 * Determines if the Watcher can be started. A disconnected Watchers is due 
	 * to the the owner document of the element being unloaded.
	 * 
	 * A Disconnected Watcher is just available as a cache. It can no longer
	 * be used.
	 */
	protected boolean disconnected; //set to false by constructor
	
	protected ListenerList listeners = new ListenerList();
	
	/*
	 * Internal nsIDOMEventListener
	 */
	protected nsIDOMEventListener domEventListener =  new nsIDOMEventListener(){

		public void handleEvent(nsIDOMEvent domEvent) {
			
			nsIDOMEventTarget eventTarget = domEvent.getTarget();
			//need to qi to nsIDOMNode to test equality
			if( watchedNode.equals( (nsIDOMNode)(eventTarget.queryInterface(nsIDOMNode.NS_IDOMNODE_IID))) ){
				
				Date date = new Date();
			    Format formatter = new SimpleDateFormat("HH:mm.ss.SSSS");
			    String time = formatter.format(date);
			    
				final IDOMEvent newEvent = new DOMEvent( time,
									domEvent.getType(),
									DOMWatcherSettings.getInstance().getSetting(domEvent.getType()).getDetailProvider().getDetail(domEvent),
									"");
				
				events.add( newEvent );
				
				//notify listenres on a separate thread to allow this handler to return quickly
				UIJob job = new UIJob( "Fire New DOM Event" ){

					public IStatus runInUIThread(IProgressMonitor monitor) {
						fireNewEvent( newEvent );
						return Status.OK_STATUS;
					}
					
				};
				
				job.schedule();
				
			}
		}
		
		public nsISupports queryInterface( String id ) {
			return Mozilla.queryInterface( this, id );
		}
		
	};
	
	public Watcher( nsIDOMNode node ){
		watchedNode = node;
		disconnected = false;
	}
	
	protected nsIDOMEventTarget getEventTarget(){
		if( watchedNode.getNodeType() == nsIDOMNode.DOCUMENT_NODE ){
			return (nsIDOMEventTarget)watchedNode.queryInterface( nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID );
		}
		else{
			return (nsIDOMEventTarget)watchedNode.getOwnerDocument().queryInterface( nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID );
		}
	}
	
	public nsIDOMNode getWatchedNode() {
		return watchedNode;
	}

	public IDOMEvent [] getEvents(){
		IDOMEvent [] eventArray = new IDOMEvent[events.size()];                    
		return (IDOMEvent [])events.toArray( eventArray );
	}
	
	/**
	 * This method hooks all the event types that are supported to the event target.
	 * Currently the eventTarget is the ownerDocument in order capture the event.
	 * 
	 * A simple Start event is also registered and fired to mark the beginning of watching
	 *
	 */
	public void start(){
		
		synchronized ( this ) {
					
			//no need to do this if the watcher is already watching or disconnected
			if( isDisconnected() || isWatching() )
				return;
				
			nsIDOMEventTarget eventTarget = getEventTarget();
			
			String [] eventTypes = DOMWatcherSettings.getInstance().getSupportedEventTypes();
			
			for (int i = 0; i < eventTypes.length; i++) {
				
				//check the settings to see if this event type should be watched
				if( DOMWatcherSettings.getInstance().getSetting(eventTypes[i]).shouldWatch() )
					eventTarget.addEventListener( eventTypes[i], domEventListener, true ); //hooking to capture phase
			}
			
			watching = true;
		}
		
		//create an internal event to mark the start of watching
		IDOMEvent startEvent = new DOMEvent( "", "START WATCH", "", "" );
		events.add( startEvent );
		
		fireNewEvent( startEvent );
		
		
	}
	
	/**
	 * This method unhooks all the event types that are supported to the event target.
	 * Currently the eventTarget is the ownerDocument in order capture the event.
	 * 
	 * A simple Stop event is also registered and fired to mark the end of watching
	 *
	 */
	public void stop(){
		
		synchronized ( this ) {
			
			if( isDisconnected() || !isWatching() )
				return;
			
			nsIDOMEventTarget eventTarget = getEventTarget();
			
			String [] eventTypes = DOMWatcherSettings.getInstance().getSupportedEventTypes();
			for (int i = 0; i < eventTypes.length; i++) {
				
				//at this point try to remove the listener from all supported event types
				//no need to keep track of which are hooked
				try{
					eventTarget.removeEventListener( eventTypes[i], domEventListener, true ); //hooking to capture phase
				}
				catch( Exception e ){
					//ignore
				}
			}
			
			watching = false;
		}
		
		//create an internal event to mark the end of watching
		IDOMEvent endEvent = new DOMEvent( "", "STOP WATCH", "", "" );
		events.add( endEvent );
		
		fireNewEvent( endEvent );
		
	}
	
	/**
	 * Clears the internal cache of events
	 */
	public void clear(){
		events.clear();
	}
	
	/**
	 * Disconnects the Watcher so that it cannot be 
	 */
	
	public void disconnect(){
		
		synchronized (this) {
			stop();
			disconnected = true;
			listeners.clear(); //remove all listeners
		}
		
	}
	
	public boolean isDisconnected(){
		return disconnected;
	}
	
	/**
	 * Signals if the Watcher is currently hooked as a listener and watching for
	 * events.
	 *
	 */
	public boolean isWatching(){
		return watching;
	}
	
	protected void fireNewEvent( IDOMEvent event ){
		for (int i = 0; i < listeners.size(); i++) {
			IDOMEventWatcherListener listener = (IDOMEventWatcherListener)listeners.getListeners()[i];
			listener.newEvent( event );
		}
	}
	
	public void addListener( IDOMEventWatcherListener listener ){
		listeners.add( listener );
	}
	
	public void removeListener( IDOMEventWatcherListener listener ){
		listeners.remove( listener );
	}

	//need to save it so that we can show it after the node's document has been
	//unloaded
	protected String nodeAsString = null;
	public String toString(){
		StringBuffer buf = new StringBuffer();
		
		if( nodeAsString == null ){
			//create the path string
			nodeAsString = DOMNodeUtils.nodeToString( watchedNode );
		}
		
		buf.append( nodeAsString );
		if( isDisconnected() )
			buf.append( "  <<disconnected>>" );
		
		return buf.toString();
	}
	
}
