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
package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.util.MozBrowserUtil;
import org.eclipse.atf.mozilla.ide.ui.preferences.IPreferenceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * This class takes care of detecting when a new browser window needs to be opened. It gives the end-user the option
 * of opening the new browser as an editor or in a popup dialog.
 * 
 * Some limitations:
 * - The Event does not provide any information about size so we are defaulting to 300x300
 * - 
 *
 */
class PopupWindowBrowserListener implements OpenWindowListener {

	protected final static String DIALOG_MSG = "The browser is attempting to open a new window. Opening as an Editor will allow the use of the browser tools, such as the DOM Inspector. Opening as a Dialog will open the browser in a separate window. By default, the popup will be ignored.\n\nHow would you like to handle?";
	protected final static String DIALOG_TITLE = "Popup browser window detected.";
	protected final static String DIALOG_TGL_MSG = "Remember my decision";

	protected final static String AS_EDITOR_BUTTON_LABEL = "Open as Editor";
	protected final static String AS_DIALOG_BUTTON_LABEL = "Open as Dialog";
	protected final static String IGNORE_BUTTON_LABEL = "Ignore";

	protected final static int AS_EDITOR_BUTTON_ID = 0;
	protected final static int AS_DIALOG_BUTTON_ID = 1;
	protected final static int IGNORE_BUTTON_ID = 2;

	//used to get a shell used to open dialogs
	protected IShellProvider shellProvider = null;

	public PopupWindowBrowserListener(IShellProvider shellProvider) {
		this.shellProvider = shellProvider;
	}

	public void open(WindowEvent event) {
		final IPreferenceStore store = MozIDEUIPlugin.getDefault().getPreferenceStore();
		String prefValue = store.getString(IPreferenceConstants.POPUP_HANDLING);

		//only open the dialog to ask the user if the pref is set to PROMPT
		if (prefValue.equals(IPreferenceConstants.POPUP_HANDLING_PROMPT)) {
			Shell parentShell = shellProvider.getShell();
			MessageDialogWithToggle d = new MessageDialogWithToggle(parentShell, DIALOG_TITLE, null, DIALOG_MSG, MessageDialog.QUESTION, new String[] { AS_EDITOR_BUTTON_LABEL, AS_DIALOG_BUTTON_LABEL, IGNORE_BUTTON_LABEL }, 2, DIALOG_TGL_MSG, false) {

				protected void buttonPressed(int buttonId) {

					//save the preference
					if (getToggleState()) {
						switch (buttonId - IDialogConstants.INTERNAL_ID) { //need to substract this because internally the ids are getting offset
						case AS_EDITOR_BUTTON_ID:
							store.setValue(IPreferenceConstants.POPUP_HANDLING, IPreferenceConstants.POPUP_HANDLING_AS_EDITOR);
							break;
						case AS_DIALOG_BUTTON_ID:
							store.setValue(IPreferenceConstants.POPUP_HANDLING, IPreferenceConstants.POPUP_HANDLING_AS_DIALOG);
							break;

						case IGNORE_BUTTON_ID:
							store.setValue(IPreferenceConstants.POPUP_HANDLING, IPreferenceConstants.POPUP_HANDLING_IGNORE);
							break;

						}
					}

					//super super does this
					setReturnCode(buttonId);
					close();
				}

			};

			int returnCode = d.open() - IDialogConstants.INTERNAL_ID; //need to substract this because internally the ids are getting offset

			switch (returnCode) {
			case AS_EDITOR_BUTTON_ID:
				openAsEditor(event);
				break;
			case AS_DIALOG_BUTTON_ID:
				openAsDialog(event);
				break;

			default:
				//ignoring is the default case
				event.browser = null;
				break;
			}
		} else if (prefValue.equals(IPreferenceConstants.POPUP_HANDLING_AS_EDITOR)) {
			openAsEditor(event);
		} else if (prefValue.equals(IPreferenceConstants.POPUP_HANDLING_AS_DIALOG)) {
			openAsDialog(event);
		}
		//else is ignore so do nothing

	}

	protected void openAsEditor(WindowEvent event) {
		try {
			IWorkbenchPage page = MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

			IWebBrowser wb = MozBrowserUtil.openMozillaBrowser("about:blank", page);
			Browser b = (Browser) wb.getAdapter(Browser.class);
			event.browser = b;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MozIDEUIPlugin.log(e);
		}
	}

	protected void openAsDialog(WindowEvent event) {
		MozBrowserDialog mozDialog = new MozBrowserDialog(shellProvider.getShell());
		mozDialog.setBlockOnOpen(false);

		mozDialog.open();

		mozDialog.getShell().setSize(300, 300);

		Browser b = mozDialog.getMozillaBrowser();
		event.browser = b;
	}

}
