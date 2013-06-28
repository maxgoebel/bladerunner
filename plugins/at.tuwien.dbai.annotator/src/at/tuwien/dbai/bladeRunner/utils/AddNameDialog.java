/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.dbai.bladeRunner.utils;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddNameDialog extends TitleAreaDialog {

	String benchName = null;

	public AddNameDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Specify benchmark name");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Missing benchmark name");
		setErrorMessage("Please specify the name of the benchmark before saving...");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("Benchmark Name");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		final Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(gridData);
		text.setEditable(true);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				benchName = text.getText();
			}
		});

		return parent;
	}

	public String getBenchName() {
		return benchName;
	};

}
