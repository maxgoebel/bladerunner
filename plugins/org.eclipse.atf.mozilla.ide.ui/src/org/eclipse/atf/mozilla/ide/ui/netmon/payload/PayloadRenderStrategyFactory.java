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
package org.eclipse.atf.mozilla.ide.ui.netmon.payload;

import java.util.HashMap;

import org.eclipse.atf.mozilla.ide.network.IContentTypeConstants;
import org.eclipse.atf.mozilla.ide.network.IHeaderContainer;

public class PayloadRenderStrategyFactory {

	private static PayloadRenderStrategyFactory instance = null;

	public static PayloadRenderStrategyFactory getInstance() {
		if (instance == null)
			instance = new PayloadRenderStrategyFactory();

		return instance;
	}

	protected HashMap cachedStrategies = new HashMap();
	protected IPayloadRenderStrategy defaultStrategy = null;

	private PayloadRenderStrategyFactory() {

	}

	/**
	 * Create an appropriate instance of IPayloadRenderStrategy based on the 
	 * headers if a request/response.
	 * 
	 * @param headerContainer
	 * @return
	 */
	public IPayloadRenderStrategy getStrategy(IHeaderContainer headerContainer) {

		String contentType = headerContainer.getHeaders().get(IContentTypeConstants.CONTENT_TYPE_HEADER);

		//set to ? because an enpty string will match with the first check
		String type = "?";
		if (contentType != null) {
			type = contentType;
		}

		//parse out the mime type
		int mimeDelim = type.indexOf(';');
		if (mimeDelim != -1) {
			type = type.substring(0, mimeDelim);
		}

		IPayloadRenderStrategy strategy = null;

		/*
		 * The condintions below are ordered so that the most common cases are
		 * checked first
		 */
		if (IContentTypeConstants.IMG_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.IMG_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.IMG_TYPE, new ImagePayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.IMG_TYPE);

		} else if (IContentTypeConstants.JS_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.JS_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.JS_TYPE, new SimpleTextPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.JS_TYPE);
		} else if (IContentTypeConstants.CSS_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.CSS_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.CSS_TYPE, new CSSPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.CSS_TYPE);
		} else if (IContentTypeConstants.HTML_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.HTML_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.HTML_TYPE, new HTMLPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.HTML_TYPE);
		} else if (IContentTypeConstants.XML_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.XML_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.XML_TYPE, new HTMLPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.XML_TYPE);
		} else if (IContentTypeConstants.XHTML_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.XHTML_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.XHTML_TYPE, new HTMLPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.XHTML_TYPE);
		} else if (IContentTypeConstants.TEXT_TYPE.indexOf(type) != -1) {

			if (!cachedStrategies.containsKey(IContentTypeConstants.TEXT_TYPE)) {
				cachedStrategies.put(IContentTypeConstants.TEXT_TYPE, new SimpleTextPayloadRenderStrategy());
			}
			strategy = (IPayloadRenderStrategy) cachedStrategies.get(IContentTypeConstants.TEXT_TYPE);
		} else { //Unsupported conditions

			if (defaultStrategy == null)
				defaultStrategy = new UnsupportedContentStrategy();

			strategy = defaultStrategy;
		}

		return strategy;
	}

}
