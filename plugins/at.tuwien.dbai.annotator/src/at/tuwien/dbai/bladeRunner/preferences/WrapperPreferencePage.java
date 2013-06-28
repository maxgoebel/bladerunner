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
package at.tuwien.dbai.bladeRunner.preferences;

import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_HIGHLIGHT_INPUT;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_HIGHLIGHT_MATCH;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_HIGHLIGHT_NEG_EXAMPLE;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_HIGHLIGHT_POS_EXAMPLE;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER_VALUE_ALIGNMENT;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER_VALUE_ATTRIBUTE;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER_VALUE_BOOLEANFUNC;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER_VALUE_QUERY;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_LEARNER_VALUE_WEKA;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.ComboFieldEditor;
import at.tuwien.dbai.bladeRunner.utils.DebugLayout;

/**
 * WrapperPreferencePage.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 27, 2011
 */
public class WrapperPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 */
	public WrapperPreferencePage() {
		super(GRID);
		setPreferenceStore(LearnUIPlugin.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {
		createHighlightingBlock(getFieldEditorParent());
		createLearnerBlock(getFieldEditorParent());
	}

	private void createHighlightingBlock(Composite parent) {
		final Label l = new Label(parent, SWT.NONE);
		DebugLayout.setBackground(l);
		l.setText(mt.lb_Highlighting_Colors);
		final GridData gd1 = new GridData();
		gd1.grabExcessHorizontalSpace = true;
		gd1.horizontalAlignment = GridData.FILL;
		gd1.horizontalSpan = 2;
		l.setLayoutData(gd1);

		final Composite cmp = new Composite(parent, SWT.NONE);
		DebugLayout.setBackground(cmp);
		final GridData gd2 = new GridData();
		gd2.grabExcessHorizontalSpace = true;
		gd2.horizontalAlignment = GridData.FILL;
		gd2.horizontalSpan = 2;
		cmp.setLayoutData(gd2);

		addField(new ColorFieldEditor(PREF_HIGHLIGHT_POS_EXAMPLE,
				mt.lb_Positive_example, cmp));
		DebugLayout.setBackground(l);
		addField(new ColorFieldEditor(PREF_HIGHLIGHT_NEG_EXAMPLE,
				mt.lb_Negative_example, cmp));
		addField(new ColorFieldEditor(PREF_HIGHLIGHT_MATCH,
				mt.lb_Extracted_instance, cmp));
		addField(new ColorFieldEditor(PREF_HIGHLIGHT_INPUT,
				mt.lb_Input_instance, cmp));
		if (cmp.getLayout() instanceof GridLayout) {
			// GridLayout is recreated in addField,
			// so change it as the last one
			GridLayout cmpgl = (GridLayout) cmp.getLayout();
			cmpgl.marginLeft = 8;
			cmpgl.verticalSpacing = 0;
		}
	}

	private void createLearnerBlock(Composite parent) {

		final Composite cmp = new Composite(parent, SWT.NONE);
		DebugLayout.setBackground(cmp);
		final GridData gd2 = new GridData();
		gd2.grabExcessHorizontalSpace = true;
		gd2.horizontalAlignment = GridData.FILL;
		gd2.verticalIndent = 10;
		cmp.setLayoutData(gd2);

		ComboFieldEditor cfe = new ComboFieldEditor(
				PREF_LEARNER,
				mt.lb_Learning_method,
				new String[][] {
						{ mt.lb_Learner_Boolean_functions,
								PREF_LEARNER_VALUE_BOOLEANFUNC },
						{ mt.lb_Learner_Weka, PREF_LEARNER_VALUE_WEKA },
						{ mt.lb_Learner_Query, PREF_LEARNER_VALUE_QUERY },
						{ mt.lb_Learner_Attribute, PREF_LEARNER_VALUE_ATTRIBUTE },
						{ mt.lb_Learner_Alignment, PREF_LEARNER_VALUE_ALIGNMENT }, },
				cmp);
		addField(cfe);

		Label lfe = cfe.getLabelControl(cmp);
		DebugLayout.setBackground(lfe);
		Combo cmb = cfe.getComboBoxControl(cmp);
		DebugLayout.setBackground(cmb);
		if (cmb.getLayoutData() instanceof GridData) {
			final GridData gd3 = (GridData) cmb.getLayoutData();
			gd3.grabExcessHorizontalSpace = true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
