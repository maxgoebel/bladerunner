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
package at.tuwien.dbai.bladeRunner.preferences.runtime;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;

/**
 * Displays details of a ER install (read only, for contributed ERs).
 * 
 * @since 3.2
 */
public class ERDetailsDialog extends Dialog {

	private IERInstall fER;

	public ERDetailsDialog(Shell shell, IERInstall er) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fER = er;
	}

	/**
	 * @see Windows#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(mt.lb_Extraction_Runtime_Details);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IJavaDebugHelpContextIds.ER_DETAILS_DIALOG);
	}

	protected Control createDialogArea(Composite ancestor) {
		Composite parent = (Composite) super.createDialogArea(ancestor);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		// name
		createLabel(parent, mt.lb_addERDialog_erName);
		createLabel(parent, fER.getName());

		// home
		createLabel(parent, mt.lb_addERDialog_erHome);
		createLabel(parent, fER.getInstallLocation().getAbsolutePath());

		// er args
		createLabel(parent, mt.lb_addERDialog_erArgs);
		String text = fER.getERArgs();
		if (text == null) {
			text = ""; //$NON-NLS-1$
		}
		createLabel(parent, text);

		applyDialogFont(parent);
		return parent;
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	/**
	 * Returns the name of the section that this dialog stores its settings in
	 * 
	 * @return String
	 */
	protected String getDialogSettingsSectionName() {
		return "ER_DETAILS_DIALOG_SECTION"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = LearnUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings
				.getSection(getDialogSettingsSectionName());
		if (section == null) {
			section = settings.addNewSection(getDialogSettingsSectionName());
		}
		return section;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}
}
