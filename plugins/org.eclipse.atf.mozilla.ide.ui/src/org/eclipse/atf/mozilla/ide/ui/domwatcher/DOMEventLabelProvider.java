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

import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.IDOMEvent;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class DOMEventLabelProvider extends LabelProvider implements ITableLabelProvider{

	protected static final int TYPE_COLUMN_INDEX = 0;
	protected static final int TIMESTAMP_COLUMN_INDEX = 1;
	protected static final int DETAILS_COLUMN_INDEX = 2;
	
	public Image getColumnImage(Object element, int columnIndex) {
		
		
		
		return null;
		
	}

	public String getColumnText(Object element, int columnIndex) {

		IDOMEvent event = (IDOMEvent)element;
		
		switch ( columnIndex ) {
			
		case TYPE_COLUMN_INDEX:
			return event.getType();
			
		case TIMESTAMP_COLUMN_INDEX:
			return event.getTimestamp();
			
		case DETAILS_COLUMN_INDEX:
			return event.getDetails();
		}
		
		return null;
	}	
}
