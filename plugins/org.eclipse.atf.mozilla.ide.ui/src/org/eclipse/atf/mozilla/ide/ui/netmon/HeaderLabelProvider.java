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

import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class HeaderLabelProvider extends LabelProvider implements ITableLabelProvider {

	protected static final int NAME_COL_INDEX = 0;
	protected static final int VALUE_COL_INDEX = 1;

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		//the elements are IHeader
		if (columnIndex == NAME_COL_INDEX)
			return (String) ((Map.Entry) element).getKey();
		else if (columnIndex == VALUE_COL_INDEX)
			return (String) ((Map.Entry) element).getValue();
		else
			return null; //unknown column
	}

}
