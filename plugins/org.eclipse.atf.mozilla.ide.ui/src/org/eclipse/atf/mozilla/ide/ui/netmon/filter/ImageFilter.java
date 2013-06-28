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

import org.eclipse.atf.mozilla.ide.network.IContentTypeConstants;

public class ImageFilter extends MimeTypeViewerFilter {

	protected String getMimeTypeCompareString() {
		return IContentTypeConstants.IMG_TYPE;
	}

}
