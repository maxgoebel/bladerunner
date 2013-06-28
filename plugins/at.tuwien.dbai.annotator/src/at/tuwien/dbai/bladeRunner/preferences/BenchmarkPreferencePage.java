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

import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_ANNO_DETAIL;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_INSTR;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_LINE;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_WORD;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.ComboFieldEditor;
import at.tuwien.dbai.bladeRunner.utils.DebugLayout;
import at.tuwien.dbai.bladeRunner.utils.HoverHelp;
import at.tuwien.dbai.bladeRunner.utils.InfoPopUp;

/**
 * BenchmarkPreferencePage.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 18, 2013
 */
public class BenchmarkPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	DirectoryFieldEditor xsdDir = null;

	/**
	 * Constructor.
	 */
	public BenchmarkPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for Benchmark Creation");
	}

	@Override
	protected void createFieldEditors() {
		// xsdDir = new DirectoryFieldEditor("PATH",
		// "&XSD directory:",getFieldEditorParent());
		// addField(xsdDir);

		createRepresentationBlock(getFieldEditorParent());
	}

	/**
	 * Create the representation preference block.
	 * 
	 * @param parent
	 */
	private void createRepresentationBlock(Composite parent) {
		// define a block container
		Composite block = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
				false);
		gridData.heightHint = 250;
		gridData.widthHint = 380;
		gridData.verticalIndent = 5;
		block.setLayoutData(gridData);
		block.setLayout(new GridLayout());

		// a section header
		Label label = new Label(block, SWT.NONE);
		FontData fontData = new FontData("Arial", 10, SWT.BOLD);
		Font font = new Font(parent.getDisplay(), fontData);
		label.setFont(font);
		label.setText("Document Graph Detail:");

		// a section description
		Text desc = new Text(block, SWT.MULTI | SWT.WRAP);
		desc.setText(mt.lb_Anno_Graph_Detail_txt);
		desc.setEditable(false);
		gridData = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gridData.heightHint = 65;
		desc.setLayoutData(gridData);

		ComboFieldEditor cfe = new ComboFieldEditor(
				PREF_ANNO_DETAIL,
				mt.lb_Anno_Graph_Detail,
				new String[][] {
						{ mt.lb_Anno_Graph_PdfInstr,
								PREF_ANNO_VAL_GRAPHDETAIL_INSTR },
						{ mt.lb_Anno_Graph_Word, PREF_ANNO_VAL_GRAPHDETAIL_WORD },
						{ mt.lb_Anno_Graph_Line, PREF_ANNO_VAL_GRAPHDETAIL_LINE }, },
				block);
		addField(cfe);

		Label lfe = cfe.getLabelControl(block);
		DebugLayout.setBackground(lfe);
		Combo cmb = cfe.getComboBoxControl(block);
		DebugLayout.setBackground(cmb);
		if (cmb.getLayoutData() instanceof GridData) {
			final GridData gd3 = (GridData) cmb.getLayoutData();
			gd3.grabExcessHorizontalSpace = true;
		}
		
		Button help = new Button(parent, SWT.PUSH);
		help.setText("help");
		help.addListener(SWT.Selection, new Listener() {
						
			@Override
			public void handleEvent(Event event) {
				new HoverHelp().open(getShell().getDisplay());
			}
		});
	
	}

	public String getXsdDir() {
		return xsdDir.getStringValue();
	}

}
