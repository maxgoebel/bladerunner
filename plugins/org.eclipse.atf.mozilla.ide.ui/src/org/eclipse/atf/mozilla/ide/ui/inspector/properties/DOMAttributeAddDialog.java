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



import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DOMAttributeAddDialog extends Dialog {

	protected Text attributeNameText = null;
	protected Text attributeValueText = null;
	
	protected String attributeName = "";
	protected String attributeValue = "";
	
	protected String title = "";
	
	public DOMAttributeAddDialog( Shell shell ){
		super( shell );
		
		this.title = "Add DOM Attribute:";
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	protected Control createDialogArea(Composite parent) {
		
		Composite displayArea = new Composite( parent, SWT.FILL );
		GridLayout layout = new GridLayout( 2, false );
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label nameLabel = new Label (displayArea, SWT.NONE);
		nameLabel.setText ("Name:");
		
		attributeNameText = new Text (displayArea, SWT.BORDER);
		attributeNameText.setText ("");
		GridData data = new GridData ( GridData.FILL_HORIZONTAL );
		//data.grabExcessHorizontalSpace = true;
		attributeNameText.setLayoutData (data);

		Label valueLabel = new Label (displayArea, SWT.NONE);
		valueLabel.setText ("Value:");
		
		attributeValueText = new Text (displayArea, SWT.BORDER);
		attributeValueText.setText ("");
		data = new GridData ( GridData.FILL_HORIZONTAL );
		//data.grabExcessHorizontalSpace = true;
		attributeValueText.setLayoutData (data);
		
		return displayArea;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText( title );
	}
	
	protected void okPressed() {
		
		attributeName = attributeNameText.getText();
		attributeValue = attributeValueText.getText();
		
		super.okPressed();
	}

	public String getAttributeName(){
		return attributeName;
	}
	
	public String getAttributeValue(){
		return attributeValue;
	}
	
	
}
