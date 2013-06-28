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
package org.eclipse.atf.mozilla.ide.ui.netmon.filter;

import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.network.IContentTypeConstants;
import org.eclipse.atf.mozilla.ide.network.IHeaderContainer;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.network.IResponse;
import org.eclipse.atf.mozilla.ide.network.IStatusChange;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

abstract public class MimeTypeViewerFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IStatusChange)
			element = ((IStatusChange) element).getCall();

		if (element instanceof ITimedEvent)
			element = ((ITimedEvent) element).getData();

		if (!(element instanceof INetworkCall))
			return true;

		IResponse res = ((INetworkCall) element).getResponse();

		if (res != null && res instanceof IHeaderContainer) {
			String contentTypeHeader = ((IHeaderContainer) res).getHeaders().get(IContentTypeConstants.CONTENT_TYPE_HEADER);

			//set to ? because an enpty string will match with the first check
			String type = "?";
			if (contentTypeHeader != null) {
				type = contentTypeHeader;
			}

			//parse out the mime type
			int mimeDelim = type.indexOf(';');
			if (mimeDelim != -1) {
				type = type.substring(0, mimeDelim);
			}

			return (getMimeTypeCompareString().indexOf(type) != -1);
		} else
			return false;

	}

	abstract protected String getMimeTypeCompareString();
}
