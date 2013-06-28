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
package org.eclipse.atf.mozilla.ide.ui.preferences;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This preference page containes preferences that affect the embedded ATF browser.
 * 
 * The preferences include:
 * 
 * - Popup Window Handling: Open in editor | Open in dialog | ignore | prompt
 * 
 * 
 * @author Gino Bustelo
 *
 */
public class BrowserPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	protected static final String PAGE_TITLE = "Browser Settings";
	protected static final String PAGE_DESC = "Various settings controlling the embedded browser.";
	
	
	/*Popup Handling*/
	protected static final String POPUP_HANDLING_LABEL_TEXT = "Popup Window Handling:";
	protected static final String [] POPUP_HANDLING_OPTIONS = {
			"Open in new Browser Editor",
			"Open as a Dialog",
			"Ignore",
			"Prompt"
	};
	
	protected final static int AS_EDITOR_IDX = 0;
	protected final static int AS_DIALOG_IDX = 1;
	protected final static int IGNORE_IDX = 2;
	protected final static int PROMPT_IDX = 3;
	
	protected String popUpHanding = null;
	
	public BrowserPreferencePage(){
		super();
		
		setTitle( PAGE_TITLE );
		setDescription( PAGE_DESC );
	}
	
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		noDefaultAndApplyButton();
		
		//client area UI
		Composite displayArea = new Composite( parent, SWT.NONE );
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		displayArea.setLayoutData(layoutData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		
		displayArea.setLayout( layout );
		
		//Popup handling
		Label popupHandlingLabel = new Label( displayArea, SWT.LEFT | SWT.WRAP );
		popupHandlingLabel.setFont( displayArea.getFont() );
		popupHandlingLabel.setText( POPUP_HANDLING_LABEL_TEXT );
		
		layoutData = new GridData();
		layoutData.horizontalSpan = 1;
		layoutData.horizontalAlignment = GridData.FILL;
		popupHandlingLabel.setLayoutData(layoutData);
		
		Combo popupHandlingCombo = new Combo( displayArea, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER );
		popupHandlingCombo.setFont( displayArea.getFont() );

		popupHandlingCombo.setItems( POPUP_HANDLING_OPTIONS );
		
		if( popUpHanding.equals(IPreferenceConstants.POPUP_HANDLING_AS_EDITOR) ){
			popupHandlingCombo.setText( POPUP_HANDLING_OPTIONS[AS_EDITOR_IDX] );
		}
		else if( popUpHanding.equals(IPreferenceConstants.POPUP_HANDLING_AS_DIALOG) ){
			popupHandlingCombo.setText( POPUP_HANDLING_OPTIONS[AS_DIALOG_IDX] );
		}
		else if( popUpHanding.equals(IPreferenceConstants.POPUP_HANDLING_IGNORE) ){
			popupHandlingCombo.setText( POPUP_HANDLING_OPTIONS[IGNORE_IDX] );
		}
		else if( popUpHanding.equals(IPreferenceConstants.POPUP_HANDLING_PROMPT) ){
			popupHandlingCombo.setText( POPUP_HANDLING_OPTIONS[PROMPT_IDX] );
		}
		
		layoutData = new GridData();
		layoutData.horizontalSpan = 1;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.widthHint = convertWidthInCharsToPixels(50);
		popupHandlingCombo.setLayoutData(layoutData);
		
		popupHandlingCombo.addModifyListener( new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				int selectedIdx = ((Combo)e.widget).getSelectionIndex();
				
				switch (selectedIdx) {
				case AS_EDITOR_IDX:
					popUpHanding = IPreferenceConstants.POPUP_HANDLING_AS_EDITOR;
					break;
				case AS_DIALOG_IDX:
					popUpHanding = IPreferenceConstants.POPUP_HANDLING_AS_DIALOG;
					break;
				case IGNORE_IDX:
					popUpHanding = IPreferenceConstants.POPUP_HANDLING_IGNORE;
					break;
				case PROMPT_IDX:
					popUpHanding = IPreferenceConstants.POPUP_HANDLING_PROMPT;
					break;
				}
			}
			
		});
		
		return displayArea;
	}

	public void init(IWorkbench workbench) {
		popUpHanding = MozIDEUIPlugin.getDefault().getPreferenceStore().getString( IPreferenceConstants.POPUP_HANDLING );
	}
	
	public boolean performOk() {
		//setting the preference
		MozIDEUIPlugin.getDefault().getPreferenceStore().setValue( IPreferenceConstants.POPUP_HANDLING, popUpHanding );
		return super.performOk();
	}	

}
