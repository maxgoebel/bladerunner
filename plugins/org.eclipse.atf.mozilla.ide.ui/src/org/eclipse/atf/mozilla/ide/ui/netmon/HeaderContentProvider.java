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

import org.eclipse.atf.mozilla.ide.network.IHeaderContainer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class HeaderContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		//inputElement is the selected XHRCall object

		if (inputElement instanceof IHeaderContainer) {
			return ((IHeaderContainer) inputElement).getHeaders().entrySet().toArray();
		} else
			return new Object[] {};
	}

	public void dispose() {
		//nothing to do here
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//Don't think I have to do anything here
	}

}
