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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;

/**
 * The Installed ERs (extraction runtimes) preference page.
 * 
 * @since 3.0
 */
public class RuntimePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final String ER_PREFERENCE_PAGE = DocWrapUIUtils
			.getPlugindId() + "er_preference_page_context"; //$NON-NLS-1$

	// ER Block
	private InstalledERsBlock fERBlock;

	public RuntimePreferencePage() {
		super();

		// only used when page is shown programatically
		setTitle(mt.dlg_Installed_extraction_runtimes);

		setDescription(mt.lb_Add_remove_edit_runtimes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Find & verify the default VM.
	 */
	private void initDefaultER() {
		IERInstall realDefault = ExtractionRuntime.getDefaultERInstall();
		if (realDefault != null) {
			IERInstall[] ers = fERBlock.getERs();
			for (int i = 0; i < ers.length; i++) {
				IERInstall fakeER = ers[i];
				if (fakeER.equals(realDefault)) {
					verifyDefaultER(fakeER);
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		initializeDialogUnits(ancestor);

		noDefaultAndApplyButton();

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		fERBlock = new InstalledERsBlock();
		fERBlock.createControl(ancestor);
		Control control = fERBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		fERBlock.restoreColumnSettings(LearnUIPlugin.getDefault()
				.getDialogSettings(), ER_PREFERENCE_PAGE);

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(ancestor,
		// IJavaDebugHelpContextIds.JRE_PREFERENCE_PAGE);
		initDefaultER();
		fERBlock.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IERInstall install = getCurrentDefaultER();
				if (install == null) {
					// FIXME waiting for default runtime shipped with binaries
					// setValid(false);
					// setErrorMessage(mt.lb_Select_a_default_extraction_runtime);
				} else {
					setValid(true);
					setErrorMessage(null);
				}
			}
		});
		applyDialogFont(ancestor);
		return ancestor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		final boolean[] canceled = new boolean[] { false };
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				IERInstall defaultER = getCurrentDefaultER();
				IERInstall[] ers = fERBlock.getERs();
				if (!ExtractionRuntime.saveERInstalls(ers, defaultER)) {
					canceled[0] = true;
				}
			}
		});

		if (canceled[0]) {
			return false;
		}

		// save column widths
		IDialogSettings settings = LearnUIPlugin.getDefault()
				.getDialogSettings();
		fERBlock.saveColumnSettings(settings, ER_PREFERENCE_PAGE);

		return super.performOk();
	}

	/**
	 * Verify that the specified ER can be a valid default ER. This amounts to
	 * verifying that the ER's install location contains all needed subdirs on
	 * the file system. If this fails, remove the ER from the table and try to
	 * set another default.
	 */
	private void verifyDefaultER(IERInstall er) {
		if (er != null) {

			// Verify that the specified ER's location actually
			// contains correct files
			IStatus s = ExtractionRuntime.validateInstallLocation(er
					.getInstallLocation());
			boolean exist = s.isOK();

			// If the install location is correct,
			// check the corresponding entry in the list,
			// otherwise remove the ER
			if (exist) {
				fERBlock.setCheckedER(er);
			} else {
				fERBlock.removeERs(new IERInstall[] { er });
				IERInstall def = ExtractionRuntime.getDefaultERInstall();
				if (def == null) {
					fERBlock.setCheckedER(null);
				} else {
					fERBlock.setCheckedER(def);
				}
				ErrorDialog.openError(getControl().getShell(),
						mt.dlg_Installed_extraction_runtimes,
						mt.lb_Installed_ER_location_no_longer_exists,
						DocWrapUIUtils.userError(mt.msg_error_ER_deleted));
				return;
			}
		} else {
			fERBlock.setCheckedER(null);
		}
	}

	private IERInstall getCurrentDefaultER() {
		return fERBlock.getCheckedER();
	}

}
