/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. -ongoing improvements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.common;

import org.eclipse.core.runtime.IAdaptable;
import org.mozilla.interfaces.nsIDOMDocument;

/**
 * Identifies browser process/window instance
 *
 */
public interface IWebBrowser extends IAdaptable {

	nsIDOMDocument getDocument();

	boolean isDocumentLoading();

	void setSelection(IDOMNodeSelection selection);

	WebBrowserType getType();

}
