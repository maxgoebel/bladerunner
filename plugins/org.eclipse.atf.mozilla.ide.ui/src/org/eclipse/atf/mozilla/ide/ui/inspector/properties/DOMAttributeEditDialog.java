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


package org.eclipse.atf.mozilla.ide.ui.inspector.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gino Bustelo
 *
 */
public class DOMAttributeEditDialog extends DOMAttributeAddDialog {

	public DOMAttributeEditDialog(Shell shell, String attributeName, String attributeValue ) {
		super(shell);
		
		this.title = "Edit DOM Attribute:";
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	protected Control createDialogArea(Composite parent) {
		Control retControl = super.createDialogArea(parent);
		
		attributeNameText.setEditable(false);
		attributeNameText.setText( attributeName );
		
		attributeValueText.setText( attributeValue );
		
		return retControl;
	}

	
}
