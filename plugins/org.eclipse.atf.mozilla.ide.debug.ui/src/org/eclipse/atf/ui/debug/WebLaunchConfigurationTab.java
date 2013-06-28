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

package org.eclipse.atf.ui.debug;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class WebLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	protected Text pathField;
	protected Label appLabel;
	protected Label browserLabel;
	protected String applicationType = "Mozilla";
	protected String[] requiredNatures = new String[0];
	private String appPath = "";
	private Button appButton;
	private Button urlTypeButton;
	private Button fileTypeButton;

	protected boolean isURL;
	private String filePath = "", urlPath = "";

	private class WebLaunchLabelProvider extends WorkbenchLabelProvider {

		protected String decorateText(String input, Object element) {
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				String filename = file.getName();
				return filename;
			}
			return input;
		}
	}

	public WebLaunchConfigurationTab(String applicationType, String[] requiredNatures, String mode) {
		super();
		this.applicationType = applicationType;
		this.requiredNatures = requiredNatures;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		return getErrorMessage() == null;
	}

	public boolean canSave() {
		return getErrorMessage() == null;
	}

	public void createControl(Composite parent) {
		Composite tab = new Composite(parent, SWT.NONE);
		tab.setFont(parent.getFont());
		tab.setLayout(new GridLayout());
		tab.setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(tab);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);

		Group webGroup = new Group(tab, SWT.NONE);
		webGroup.setFont(parent.getFont());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		webGroup.setLayout(gridLayout);
		webGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		webGroup.setText("Debug Target:");

		Composite radioGroup = new Composite(webGroup, SWT.NONE);
		radioGroup.setLayout(new RowLayout());
		fileTypeButton = new Button(radioGroup, SWT.RADIO);
		fileTypeButton.setFont(parent.getFont());
		fileTypeButton.setText("Project file");
		urlTypeButton = new Button(radioGroup, SWT.RADIO);
		urlTypeButton.setFont(parent.getFont());
		urlTypeButton.setText("URL");

		urlTypeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				if (urlTypeButton.getSelection()) {
					if (!isURL) {
						isURL = true;
						appButton.setVisible(false);
						filePath = pathField.getText();
						if (urlPath != null)
							pathField.setText(urlPath);
					}
				}
				updateWidgetEnablements();
				updateLaunchConfigurationDialog();
			}
		});
		fileTypeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				if (fileTypeButton.getSelection()) {
					if (isURL) {
						isURL = false;
						appButton.setVisible(true);
						urlPath = pathField.getText();
						if (filePath != null)
							pathField.setText(filePath);
					}
				}
				updateWidgetEnablements();
				updateLaunchConfigurationDialog();
			}
		});

		Composite temp = new Composite(webGroup, SWT.NULL);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		temp.setLayout(gridLayout);
		temp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		pathField = new Text(temp, SWT.SINGLE | SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		pathField.setLayoutData(data);
		pathField.setFont(parent.getFont());
		pathField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				appPath = pathField.getText();
				updateWidgetEnablements();
				updateLaunchConfigurationDialog();

			}
		});

		appButton = createPushButton(temp, "Browse...", null);
		appButton.setVisible(!isURL);
		appButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleAppButtonSelected();
				updateWidgetEnablements();
			}
		});

		Composite serverOptionGroup = new Composite(webGroup, SWT.NONE);
		serverOptionGroup.setLayout(new RowLayout());
	}

	protected void updateWidgetEnablements() {

		appButton.setEnabled(true);
		setErrorMessage(null);

		if (isURL) {
			if (pathField.getText().equals("")) {
				setErrorMessage("A valid URL must be specified");
			} else {
				try {
					new URL(pathField.getText());
				} catch (MalformedURLException e) {
					setErrorMessage(e.getMessage());
				}
			}
		} else if (pathField.getText().equals("") || !isValidPath(pathField.getText())) {
			setErrorMessage("A valid workspace file must be specified");
		}
	}

	protected void handleAppButtonSelected() {

		// Figure out the right initial directory for the file dialog, based
		// on the project field
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ApplicationFileSelectionDialog dialog = new ApplicationFileSelectionDialog(getShell(), new WebLaunchLabelProvider(), "Web application file selection", "Select a target web application file from the project's contents", projects, requiredNatures, false);
		dialog.setInput(projects);
		dialog.setAllowMultiple(false);
		//		dialog.setSorter(new ResourceSorter(ResourceSorter.TYPE));
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			Object[] targetFiles = dialog.getResult();
			IFile targetFile = (IFile) targetFiles[0];
			if (targetFile != null) {
				pathField.setText(targetFile.getFullPath().toPortableString());
				updateLaunchConfigurationDialog();
			}
		}
	}

	protected IPath computeDefaultDirectory(IPath projectPath) {
		return projectPath;
	}

	protected boolean isValidProject(String projName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		if (project.exists())
			return true;
		return false;
	}

	protected boolean isValidPath(String path) {
		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		isURL = false;
	}

	public void initializeFrom(ILaunchConfiguration config) {
		try {
			isURL = config.getAttribute(ILaunchConfigurationConstants.IS_URL, false);
			urlTypeButton.setSelection(isURL);
			//project
			String projectName = config.getAttribute(ILaunchConfigurationConstants.PROJECT, "");

			//App file path
			appPath = config.getAttribute(ILaunchConfigurationConstants.APP_PATH, "");

			if (isURL) {
				pathField.setText(appPath);
			} else {
				if (!config.getAttribute(ILaunchConfigurationConstants.FULL_PATH, "").equals("")) {
					pathField.setText(config.getAttribute(ILaunchConfigurationConstants.FULL_PATH, ""));
				} else {
					pathField.setText(appPath);
				}
			}

			//File type
			fileTypeButton.setSelection(!isURL);
			appButton.setVisible(!isURL);

			updateWidgetEnablements();

		} catch (CoreException ce) {
			MozillaDebugUIPlugin.getDefault().getLog().log(ce.getStatus());
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		Path path = new Path(pathField.getText());

		if (isURL) {
			config.setAttribute(ILaunchConfigurationConstants.APP_PATH, appPath);
		} else {
			String projectName = path.segment(0);
			config.setAttribute(ILaunchConfigurationConstants.PROJECT, projectName);
			config.setAttribute(ILaunchConfigurationConstants.APP_PATH, appPath);
		}
		config.setAttribute(ILaunchConfigurationConstants.PROCESS_TYPE, getName());
		config.setAttribute(ILaunchConfigurationConstants.IS_URL, urlTypeButton.getSelection());
		config.setAttribute(ILaunchConfigurationConstants.FULL_PATH, pathField.getText());
	}

	public String getName() {
		return "Configuration";
	}

	public void update() {
		updateLaunchConfigurationDialog();
	}

}
