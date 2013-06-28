/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.ui.netmon;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


public class NetworkCallSorter extends ViewerSorter {

	public final static int STATE		 		= 1;
	public final static int URL 				= 2;
	public final static int METHOD			 	= 3;
	public final static int START_TIME			= 4;
	public final static int STOP_TIME			= 5;
	public final static int ELAPSED_TIME		= 6;
	
	private int criteria;
	private boolean ascending = true;
	
	public NetworkCallSorter(int criteria) {
		super();
		this.criteria = criteria;
	}
	
	public NetworkCallSorter(int criteria, boolean ascending) {
		super();
		this.criteria = criteria;
		this.ascending = ascending;
		
	}
	
	public int getColumnCriteria() {
		return criteria;
	}
	
	public void setColumnCriteria(int criteria) {
		this.criteria = criteria;
	}
	
	public boolean isAscending() {
		return ascending;
	}
	
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
	public int compare(Viewer viewer, Object o1, Object o2) {

		INetworkCall call1 = (INetworkCall) o1;
		INetworkCall call2 = (INetworkCall) o2;

		switch (criteria) {
			case STATE :
				return compareState(call1, call2);
			case URL :
				return compareUrl(call1, call2);
			case METHOD :
				return compareMethod(call1, call2);
			case START_TIME :
				return compareStartTime(call1, call2);
			case STOP_TIME :
				return compareStopTime(call1, call2);
			case ELAPSED_TIME :
				return compareElapsedTime(call1, call2);
			default:
				return 0;
		}
	}
	
	/**
	 * Returns a number reflecting the order of the given calls
	 * based on the state.
	 *
	 * @param call1
	 * @param call2
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	private int compareState(INetworkCall call1, INetworkCall call2) {
		int compare =  getComparator().compare(call1.getState(), call2.getState());
		if(ascending)
			return compare;
		return -compare;
	}
	
	private int compareUrl(INetworkCall call1, INetworkCall call2) {
		
		int compare = getComparator().compare( call1.getRequest().getURL(), call2.getRequest().getURL() );
		if(ascending)
			return compare;
		return -compare;
	}
	
	private int compareMethod(INetworkCall call1, INetworkCall call2) {
		
		String method1 = "";
		if( call1 instanceof IHTTPRequest ){
			method1 = ((IHTTPRequest)call1).getMethod();
		}
		
		String method2 = "";
		if( call1 instanceof IHTTPRequest ){
			method2 = ((IHTTPRequest)call2).getMethod();
		}
		
		int compare = getComparator().compare( method1, method2 );
		if(ascending)
			return compare;
		return -compare;
	}
	
	private int compareStartTime(INetworkCall call1, INetworkCall call2) {
		long diff = call1.getStartTime() - call2.getStartTime();
		if(ascending)
			return diff < 0 ? -1 : diff > 0 ? 1 : 0;
		else
			return diff > 0 ? -1 : diff < 0 ? 1 : 0;
	}
	
	private int compareStopTime(INetworkCall call1, INetworkCall call2) {
		long diff = call1.getEndTime() - call2.getEndTime();
		if(ascending)
			return diff < 0 ? -1 : diff > 0 ? 1 : 0;
		else
			return diff > 0 ? -1 : diff < 0 ? 1 : 0;
		
	}
	
	private int compareElapsedTime(INetworkCall call1, INetworkCall call2) {
		int diff = call1.getTotalTime() - call2.getTotalTime();
		if(ascending)
			return diff < 0 ? -1 : diff > 0 ? 1 : 0;
		else
			return diff > 0 ? -1 : diff < 0 ? 1 : 0;
	}
	
	
}
