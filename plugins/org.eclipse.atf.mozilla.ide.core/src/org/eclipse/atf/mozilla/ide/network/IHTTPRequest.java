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
package org.eclipse.atf.mozilla.ide.network;

public interface IHTTPRequest extends IRequest, IHeaderContainer {

	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	public static final String HEAD_METHOD = "HEAD";
	public static final String PUT_METHOD = "PUT";
	public static final String DELETE_METHOD = "DELETE";
	
	/**
	 * This is the HTTP Method (GET, PUT, POST...)
	 * @return
	 */
	String getMethod();
	
	/**
	 * This it the body part of the request
	 * @return
	 */
	String getBody();
	
	/**
	 * 
	 */
	boolean isXHR();
}
