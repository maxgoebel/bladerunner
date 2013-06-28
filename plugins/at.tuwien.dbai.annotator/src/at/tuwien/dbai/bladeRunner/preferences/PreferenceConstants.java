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

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	// DocWrap related preference constants
	// the document representations
	public static final String PREF_DW_REP = "representation";
	public static final String PREF_DW_VALUE_REP_FLAT = "flat";
	public static final String PREF_DW_VALUE_REP_LEVEL = "level-stack";
	public static final String PREF_DW_VALUE_REP_HIER = "hierarchical";
	public static final String PREF_DW_DISTANCE = "distance";
	public static final String PREF_DW_DISTANCE_TXT = "distance-txt";

	public static final String PREF_ANNO_DETAIL = "docGraphDetail";
	public static final String PREF_ANNO_VAL_GRAPHDETAIL_INSTR = "PDF Instruction";
	public static final String PREF_ANNO_VAL_GRAPHDETAIL_CHAR = "Character";
	public static final String PREF_ANNO_VAL_GRAPHDETAIL_WORD = "Word";
	public static final String PREF_ANNO_VAL_GRAPHDETAIL_LINE = "Line";

	// the distance function
	public static final String PREF_DW_DIST_VALUE_GED_EXACT = "gedExact";
	public static final String PREF_DW_DIST_VALUE_GED_APPROX = "gedApprox";
	public static final String PREF_DW_DIST_VALUE_RAND_WALK = "randWalk";
	public static final String PREF_DW_DIST_VALUE_TOPO_WALK = "topoWalk";
	public static final String PREF_DW_DIST_VALUE_PROBING = "probing";
	public static final String PREF_DW_SEARCH = "search";
	public static final String PREF_DW_SEMANTIC = "semantics";
	// define the type of matching
	public static final String PREF_DW_GED_NODES = "nodes";
	public static final String PREF_DW_GED_EDGES = "edges";

	// Weblearn related preference constants
	public static final String PREF_LEARNER = "learner"; //$NON-NLS-1$
	public static final String PREF_LEARNER_VALUE_BOOLEANFUNC = "boolean_func"; //$NON-NLS-1$
	public static final String PREF_LEARNER_VALUE_QUERY = "query"; //$NON-NLS-1$
	public static final String PREF_LEARNER_VALUE_ALIGNMENT = "alignment";
	public static final String PREF_LEARNER_VALUE_ATTRIBUTE = "attribute";
	public static final String PREF_LEARNER_VALUE_WEKA = "weka2";

	public static final String PREF_HIGHLIGHT_POS_EXAMPLE = "highlight_pos_example"; //$NON-NLS-1$
	public static final String PREF_HIGHLIGHT_NEG_EXAMPLE = "highlight_neg_example"; //$NON-NLS-1$
	public static final String PREF_HIGHLIGHT_MATCH = "highlight_match"; //$NON-NLS-1$
	public static final String PREF_HIGHLIGHT_INPUT = "highlight_input"; //$NON-NLS-1$
	public static final String PREF_HIGHLIGHT_FRAGMENT = "highlight_fragment";
	public static final String PREF_ADD_URLS = "add_urls";
}
