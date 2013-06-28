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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.DialogField;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.IDialogFieldListener;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.IStringButtonAdapter;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.StringButtonDialogField;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.StringDialogField;

public class AddERDialog extends StatusDialog {

	private IAddERDialogRequestor fRequestor;

	private IERInstall fEditedER;

	private StringButtonDialogField fERRoot;
	private StringDialogField fERName;

	private IStatus[] fStati;

	public AddERDialog(IAddERDialogRequestor requestor, Shell shell,
			IERInstall editedER) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fRequestor = requestor;
		fStati = new IStatus[2];
		for (int i = 0; i < fStati.length; i++) {
			fStati[i] = new StatusInfo();
		}

		fEditedER = editedER;
	}

	/**
	 * @see Windows#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IJavaDebugHelpContextIds.EDIT_ER_DIALOG);
	}

	protected void createDialogFields() {
		fERName = new StringDialogField();
		fERName.setLabelText(mt.lb_addERDialog_erName);

		fERRoot = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				browseForInstallDir();
			}
		});
		fERRoot.setLabelText(mt.lb_addERDialog_erHome);
		fERRoot.setButtonLabel(mt.btn_Browse);
	}

	protected void createFieldListeners() {
		fERName.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setERNameStatus(validateERName());
				updateStatusLine();
			}
		});

		fERRoot.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setERLocationStatus(validateERLocation());
				updateStatusLine();
			}
		});

	}

	protected String getERName() {
		return fERName.getText();
	}

	protected File getInstallLocation() {
		return new File(fERRoot.getText());
	}

	protected Control createDialogArea(Composite ancestor) {
		createDialogFields();
		Composite parent = (Composite) super.createDialogArea(ancestor);
		((GridLayout) parent.getLayout()).numColumns = 3;

		fERName.doFillIntoGrid(parent, 3);

		fERRoot.doFillIntoGrid(parent, 3);

		Text t = fERRoot.getTextControl(parent);
		GridData gd = (GridData) t.getLayoutData();
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = convertWidthInCharsToPixels(50);

		initializeFields();
		createFieldListeners();
		applyDialogFont(parent);
		return parent;
	}

	public void create() {
		super.create();
		fERName.setFocus();
	}

	private void initializeFields() {
		if (fEditedER == null) {
			fERName.setText(""); //$NON-NLS-1$
			fERRoot.setText(""); //$NON-NLS-1$
		} else {
			fERName.setText(fEditedER.getName());
			fERRoot.setText(fEditedER.getInstallLocation().getAbsolutePath());
		}
		setERNameStatus(validateERName());
		updateStatusLine();
	}

	private IStatus validateERLocation() {
		String locationName = fERRoot.getText();
		IStatus s = null;
		File file = null;
		if (locationName.length() == 0) {
			s = new StatusInfo(IStatus.INFO, mt.lb_addERDialog_enterLocation);
		} else {
			file = new File(locationName);
			if (!file.exists()) {
				s = new StatusInfo(IStatus.ERROR,
						mt.lb_addERDialog_locationNotExists);
			} else {
				final IStatus[] temp = new IStatus[1];
				final File tempFile = file;
				Runnable r = new Runnable() {
					/**
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						try {
							temp[0] = ExtractionRuntime
									.validateInstallLocation(tempFile);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				};
				BusyIndicator.showWhile(getShell().getDisplay(), r);
				s = temp[0];
			}
		}
		if (s.isOK()) {
			String name = fERName.getText();
			if (name == null || name.trim().length() == 0) {
				// auto-generate ER name
				try {
					String genName = null;
					IPath path = new Path(file.getCanonicalPath());
					int segs = path.segmentCount();
					if (segs == 1) {
						genName = path.segment(0);
					} else if (segs >= 2) {
						String last = path.lastSegment();
						if ("er".equalsIgnoreCase(last)) { //$NON-NLS-1$
							genName = path.segment(segs - 2);
						} else {
							genName = last;
						}
					}
					if (genName != null) {
						fERName.setText(genName);
					}
				} catch (IOException e) {
				}
			}
		}
		return s;
	}

	private IStatus validateERName() {
		StatusInfo status = new StatusInfo();
		String name = fERName.getText();
		if (name == null || name.trim().length() == 0) {
			status.setInfo(mt.lb_addERDialog_enterName);
		} else {
			if (fRequestor.isDuplicateName(name)
					&& (fEditedER == null || !name.equals(fEditedER.getName()))) {
				status.setError(mt.lb_addERDialog_duplicateName);
			} else {
				IStatus s = ResourcesPlugin.getWorkspace().validateName(name,
						IResource.FILE);
				if (!s.isOK()) {
					status.setError(mt
							.bind(mt.lbfmt_addERDialog_er_name_must_be_a_valid_file_name,
									s.getMessage()));
				}
			}
		}
		return status;
	}

	protected void updateStatusLine() {
		IStatus max = null;
		for (int i = 0; i < fStati.length; i++) {
			IStatus curr = fStati[i];
			if (curr.matches(IStatus.ERROR)) {
				updateStatus(curr);
				return;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max = curr;
			}
		}
		updateStatus(max);
	}

	private void browseForInstallDir() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setFilterPath(fERRoot.getText());
		dialog.setMessage(mt.lb_addERDialog_pickERRootDialog_message);
		String newPath = dialog.open();
		if (newPath != null) {
			fERRoot.setText(newPath);
		}
	}

	protected void okPressed() {
		doOkPressed();
		super.okPressed();
	}

	private void doOkPressed() {
		if (fEditedER == null) {
			IERInstall er = new ERInstall(createUniqueId());
			setFieldValuesToER(er);
			fRequestor.erAdded(er);
		} else {
			setFieldValuesToER(fEditedER);
		}
	}

	protected static String createUniqueId() {
		String id = null;
		do {
			id = String.valueOf(System.currentTimeMillis());
		} while (ExtractionRuntime.findERInstall(id) != null);
		return id;
	}

	protected void setFieldValuesToER(IERInstall er) {
		File dir = new File(fERRoot.getText());
		try {
			er.setInstallLocation(dir.getCanonicalFile());
		} catch (IOException e) {
			er.setInstallLocation(dir.getAbsoluteFile());
		}
		er.setName(fERName.getText());
	}

	protected File getAbsoluteFileOrEmpty(String path) {
		if (path == null || path.length() == 0) {
			return new File(""); //$NON-NLS-1$
		}
		return new File(path).getAbsoluteFile();
	}

	private void setERNameStatus(IStatus status) {
		fStati[0] = status;
	}

	private void setERLocationStatus(IStatus status) {
		fStati[1] = status;
	}

	/**
	 * Updates the status of the ok button to reflect the given status.
	 * Subclasses may override this method to update additional buttons.
	 * 
	 * @param status
	 *            the status.
	 */
	protected void updateButtonsEnableState(IStatus status) {
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null && !ok.isDisposed())
			ok.setEnabled(status.getSeverity() == IStatus.OK);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#setButtonLayoutData(org.eclipse.swt.widgets.Button)
	 */
	protected void setButtonLayoutData(Button button) {
		super.setButtonLayoutData(button);
	}

	/**
	 * Returns the name of the section that this dialog stores its settings in
	 * 
	 * @return String
	 */
	protected String getDialogSettingsSectionName() {
		return "ADD_ER_DIALOG_SECTION"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	// protected IDialogSettings getDialogBoundsSettings() {
	// IDialogSettings settings =
	// JDIDebugUIPlugin.getDefault().getDialogSettings();
	// IDialogSettings section =
	// settings.getSection(getDialogSettingsSectionName());
	// if (section == null) {
	// section = settings.addNewSection(getDialogSettingsSectionName());
	// }
	// return section;
	// }
}
