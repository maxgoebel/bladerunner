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

import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DISTANCE;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DIST_VALUE_GED_APPROX;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DIST_VALUE_GED_EXACT;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DIST_VALUE_PROBING;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DIST_VALUE_RAND_WALK;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_DIST_VALUE_TOPO_WALK;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_GED_EDGES;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_GED_NODES;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_REP;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_SEMANTIC;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_VALUE_REP_FLAT;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_VALUE_REP_HIER;
import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_DW_VALUE_REP_LEVEL;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.preferences.dialogfields.ComboFieldEditor;
import at.tuwien.dbai.bladeRunner.utils.DebugLayout;

/**
 * DocWrapPreferencePage.java
 * 
 * A simple preference page for the DocWrap project.
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 23, 2011
 */
public class DocWrapPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	private BooleanFieldEditor bfe1 = null, bfe2 = null;
	private Composite editBlock = null;

	/**
	 * Constructor.
	 */
	public DocWrapPreferencePage() {
		super(GRID);
	}

	private void updateEnablement(String newValue) {
		if (editBlock == null || bfe1 == null || bfe2 == null)
			return;

		if (newValue.equals(PreferenceConstants.PREF_DW_DIST_VALUE_GED_EXACT)
				|| newValue
						.equals(PreferenceConstants.PREF_DW_DIST_VALUE_GED_APPROX)
				|| newValue
						.equals(PreferenceConstants.PREF_DW_DIST_VALUE_RAND_WALK)
				|| newValue
						.equals(PreferenceConstants.PREF_DW_DIST_VALUE_PROBING)) {
			bfe1.setEnabled(true, editBlock);
			bfe2.setEnabled(true, editBlock);
		} else {
			bfe1.setEnabled(false, editBlock);
			bfe2.setEnabled(false, editBlock);
		}
	}

	public void createFieldEditors() {
		createRepresentationBlock(getFieldEditorParent());
		createDistanceBlock(getFieldEditorParent());
		createSemanticBlock(getFieldEditorParent());
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
		gridData.heightHint = 180;
		gridData.widthHint = 380;
		gridData.verticalIndent = 5;
		block.setLayoutData(gridData);
		block.setLayout(new GridLayout());

		// a section header
		Label label = new Label(block, SWT.NONE);
		FontData fontData = new FontData("Arial", 10, SWT.BOLD);
		Font font = new Font(parent.getDisplay(), fontData);
		label.setFont(font);
		label.setText("Graph Representation:");

		// a section description
		Text desc = new Text(block, SWT.MULTI | SWT.WRAP);
		desc.setText(mt.lb_DW_Graph_Representation_txt);
		desc.setEditable(false);
		gridData = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gridData.heightHint = 35;
		desc.setLayoutData(gridData);

		ComboFieldEditor cfe = new ComboFieldEditor(PREF_DW_REP,
				mt.lb_DW_Graph_Representation, new String[][] {
						{ mt.lb_DW_GR_Flat, PREF_DW_VALUE_REP_FLAT },
						{ mt.lb_DW_GR_Level, PREF_DW_VALUE_REP_LEVEL },
						{ mt.lb_DW_GR_Hier, PREF_DW_VALUE_REP_HIER }, }, block);
		addField(cfe);

		Label lfe = cfe.getLabelControl(block);
		DebugLayout.setBackground(lfe);
		Combo cmb = cfe.getComboBoxControl(block);
		DebugLayout.setBackground(cmb);
		if (cmb.getLayoutData() instanceof GridData) {
			final GridData gd3 = (GridData) cmb.getLayoutData();
			gd3.grabExcessHorizontalSpace = true;
		}
	}

	/**
	 * Create the distance preference block.
	 * 
	 * @param parent
	 */
	private void createDistanceBlock(Composite parent) {
		// define a block container
		Composite block = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
				false);
		gridData.heightHint = 180;
		gridData.widthHint = 380;
		gridData.verticalIndent = 5;
		gridData.horizontalSpan = 2;
		gridData.verticalSpan = 1;
		block.setLayoutData(gridData);
		block.setLayout(new GridLayout());

		// a section header
		Label label = new Label(block, SWT.NONE);
		FontData fontData = new FontData("Arial", 10, SWT.BOLD);
		Font font = new Font(parent.getDisplay(), fontData);
		label.setFont(font);
		label.setText("Distance Function:");

		// a section description
		Text desc = new Text(block, SWT.MULTI | SWT.WRAP);
		desc.setText(mt.lb_DW_Graph_Distance_txt);
		desc.setEditable(false);
		gridData = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gridData.heightHint = 35;
		desc.setLayoutData(gridData);

		ComboFieldEditor cfe = new ComboFieldEditor(
				PREF_DW_DISTANCE,
				mt.lb_DW_Graph_Distance,
				new String[][] {
						{ mt.lb_DW_GD_GedApprox, PREF_DW_DIST_VALUE_GED_APPROX },
						{ mt.lb_DW_GD_GedExact, PREF_DW_DIST_VALUE_GED_EXACT },
						{ mt.lb_DW_GD_Probing, PREF_DW_DIST_VALUE_PROBING },
						{ mt.lb_DW_GD_RandWalk, PREF_DW_DIST_VALUE_RAND_WALK },
						{ mt.lb_DW_GD_TopoWalk, PREF_DW_DIST_VALUE_TOPO_WALK }, },
				block);
		cfe.setPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {

				updateEnablement((String) event.getNewValue());
			}
		});
		addField(cfe);

		Label lfe = cfe.getLabelControl(block);
		DebugLayout.setBackground(lfe);
		Combo cmb = cfe.getComboBoxControl(block);
		DebugLayout.setBackground(cmb);
		if (cmb.getLayoutData() instanceof GridData) {
			final GridData gd3 = (GridData) cmb.getLayoutData();
			gd3.grabExcessHorizontalSpace = true;
		}

		editBlock = new Composite(block, SWT.NONE);
		editBlock.setLayout(new RowLayout(SWT.HORIZONTAL));

		bfe1 = new BooleanFieldEditor(PREF_DW_GED_NODES, mt.lb_DW_GM_NodesOnly,
				editBlock);

		bfe2 = new BooleanFieldEditor(PREF_DW_GED_EDGES, mt.lb_DW_GM_EdgesOnly,
				editBlock);

		IPreferenceStore prefStore = LearnUIPlugin.getDefault()
				.getPreferenceStore();
		if (prefStore.getString(PREF_DW_DISTANCE).equals(
				PreferenceConstants.PREF_DW_DIST_VALUE_GED_EXACT)
				|| prefStore.getString(PREF_DW_DISTANCE).equals(
						PreferenceConstants.PREF_DW_DIST_VALUE_GED_APPROX)) {
			bfe1.setEnabled(true, editBlock);
			bfe2.setEnabled(true, editBlock);
		} else {
			bfe1.setEnabled(false, editBlock);
			bfe2.setEnabled(false, editBlock);
		}

		addField(bfe1);
		addField(bfe2);
	}

	/**
	 * Create the semantic preferences block.
	 * 
	 * @param parent
	 */
	private void createSemanticBlock(Composite parent) {
		// define a block container
		Composite block = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
				false);
		gridData.heightHint = 180;
		gridData.widthHint = 380;
		gridData.verticalIndent = 5;
		block.setLayoutData(gridData);
		block.setLayout(new GridLayout());

		// a section header
		Label label = new Label(block, SWT.NONE);
		FontData fontData = new FontData("Arial", 10, SWT.BOLD);
		Font font = new Font(parent.getDisplay(), fontData);
		label.setFont(font);
		label.setText("Semantic Tools:");

		// a section description
		Text desc = new Text(block, SWT.MULTI | SWT.WRAP);
		desc.setText(mt.lb_DW_SEM_txt);
		desc.setEditable(false);
		gridData = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		gridData.heightHint = 35;
		desc.setLayoutData(gridData);

		addField(new BooleanFieldEditor(PREF_DW_SEMANTIC,
				mt.lb_DW_SEM_Activated, block));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for the DocWrap application");
	}
}
