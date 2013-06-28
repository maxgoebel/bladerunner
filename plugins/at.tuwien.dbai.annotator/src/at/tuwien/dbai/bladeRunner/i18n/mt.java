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
package at.tuwien.dbai.bladeRunner.i18n;

//import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.util.NLS;

/**
 * base class for the plugin-local message translations classes
 */
public class mt extends NLS {

	private static final String BUNDLE_NAME = "at.tuwien.dbai.bladeRunner.i18n.mt"; //$NON-NLS-1$

	static {
		// Debug.DEBUG_MESSAGE_BUNDLES = true;
		NLS.initializeMessages(BUNDLE_NAME, mt.class);
	}

	public static String _UI_CreateChild_menu_item;
	public static String _UI_CreateSibling_menu_item;
	public static String _UI_RefreshViewer_menu_item;
	public static String _UI_ShowPropertiesView_menu_item;
	public static String _UI_FileConflict_label;
	public static String _UI_SelectionPage_label;
	public static String _UI_MultiObjectSelected;
	public static String _UI_SingleObjectSelected;
	public static String _UI_NoObjectSelected;
	// public static String _UI_XMLEncodingChoices;
	// public static String _UI_ModelObject;
	// public static String _UI_XMLEncoding;
	public static String _WARN_FileConflict;
	public static String _WARN_FilenameExtension;
	public static String _UI_OpenEditorError_label;

	public static String _UI_WrapperEditor_menu;
	public static String _UI_WrapperWizard_WindowTitle_label;
	public static String _UI_WrapperWizard_NewFilePage_label;
	public static String _UI_WrapperWizard_NewFilePage_description;
	public static String _UI_WrapperEditor_FilenameDefaultBase;
	public static String _UI_WrapperEditor_FilenameExtension;
	public static String _UI_NavigationWizard_StartURIPage_label;
	public static String _UI_NavigationWizard_StartURIPage_description;

	public static String _UI_ClusteringEditor_menu;
	public static String _UI_ClusteringWizard_WindowTitle_label;
	public static String _UI_ClusteringWizard_NewFilePage_label;
	public static String _UI_ClusteringWizard_NewFilePage_description;
	public static String _UI_ClusteringEditor_FilenameDefaultBase;
	public static String _UI_ClusteringEditor_FilenameExtension;

	// public static String _UI_NavigationWizard_WindowTitle_label;
	// public static String _UI_NavigationWizard_NewFilePage_label;
	// public static String _UI_NavigationWizard_NewFilePage_description;
	// public static String _UI_NavigationEditor_FilenameDefaultBase;
	// public static String _UI_NavigationEditor_FilenameExtension;

	public static String ac_Wrap;
	public static String ac_Stop;
	public static String ac_Auto_Learn;
	public static String ac_Learn_Now;
	public static String ac_Auto_Evaluate;
	public static String ac_Evaluate_Now;
	public static String ac_Restart_Learning;
	public static String ac_Document_Done;
	public static String ac_Wrap_Cluster;
	public static String ac_Documents_Add_URL;
	public static String ac_Documents_Load_Benchmark;
	public static String ac_Documents_Open;
	public static String ac_Documents_SaveAs;
	public static String ac_Documents_Add_from_File;
	public static String ac_Documents_Add_from_Browser;
	public static String ac_Documents_Group;
	public static String ac_Documents_Add_from_Navigation;
	public static String ac_Build_Document_Templates;
	public static String ac_Documents_Ungroup;
	public static String ac_Fragmentation_Toggle;
	public static String ac_Fragmentation_AsTable;

	public static String cmd_Remove_Weka_Disjunction;
	public static String cmd_Remove_Weka_Conjunction;
	public static String cmd_Remove_Weka_Feature;

	public static String cmd_Add_Example;
	public static String cmd_Remove_Example;
	// public static String cmd_Set_Complete;
	public static String cmd_Restart_Learning;
	public static String cmd_Learn_Now;
	public static String cmd_Group_Documents;
	public static String cmd_Build_Templates;
	public static String cmd_Add_Navigation_Documents;
	public static String cmd_Add_Documents;
	public static String cmd_Add_Document;
	public static String cmd_All_Unclassified;
	public static String cmd_Load_Examples;

	public static String dlg_Error;
	public static String dlg_Warning;
	public static String dlg_Information;
	public static String dlg_Copy_ER;
	public static String dlg_Add_ER;
	public static String dlg_Edit_ER;
	public static String dlg_No_parent_matches;
	public static String dlg_Enter_Address_of_Doc_to_Add;
	public static String dlg_Installed_extraction_runtimes;

	public static String btn_Search;
	public static String btn_Remove;
	public static String btn_Copy;
	public static String btn_Edit;
	public static String btn_Add;
	public static String btn_Browse;

	// DocWrap labels
	public static String lb_DW_Graph_Distance;
	public static String lb_DW_Graph_Distance_txt;
	public static String lb_DW_GD_GedExact;
	public static String lb_DW_GD_GedApprox;
	public static String lb_DW_GD_RandWalk;
	public static String lb_DW_GD_TopoWalk;
	public static String lb_DW_GD_Probing;
	public static String lb_DW_GM_NodesOnly;
	public static String lb_DW_GM_NodesEdges;
	public static String lb_DW_GM_EdgesOnly;

	public static String lb_DW_Graph_Representation;
	public static String lb_DW_Graph_Representation_txt;
	public static String lb_DW_GR_Flat;
	public static String lb_DW_GR_Level;
	public static String lb_DW_GR_Hier;
	public static String lb_DW_Graph_Search;
	public static String lb_DW_GS_Scanline;

	public static String lb_Anno_Graph_Detail;
	public static String lb_Anno_Graph_PdfInstr;
	public static String lb_Anno_Graph_Word;
	public static String lb_Anno_Graph_Line;
	public static String lb_Anno_Graph_Detail_txt;

	public static String lb_DW_SEM_txt;
	public static String lb_DW_SEM_Activated;

	// Weblearn labels
	public static String lb_Learning_method;
	public static String lb_Learner_Boolean_functions;
	public static String lb_Learner_Weka;
	public static String lb_Learner_Query;
	public static String lb_Learner_Attribute;
	public static String lb_Learner_Alignment;
	public static String lb_Highlighting_Colors;
	public static String lb_Positive_example;
	public static String lb_Extracted_instance;
	public static String lb_Input_instance;
	public static String lb_Negative_example;
	public static String lb_Unclassified_Documents;
	public static String lb_Document_Templates;
	public static String lb_Document_Template;
	public static String lb_Document_Group;
	public static String lb_Benchmark_Group;
	public static String lb_No_Document_Collection_in_Editor;
	public static String lb_No_Fragment_Collection_in_Editor;
	public static String lb_No_Example_Collection_in_Editor;
	public static String lb_No_Table_in_Editor;
	public static String lb_Record_Template;
	public static String lb_Templates;
	public static String lbfmt_Found_xxx_items;
	public static String lb_Found_one_item;
	public static String lb_No_items_found;
	public static String lb_Choose_a_Web_extraction_configuration_to_run;
	public static String lb_Expand_the_tree_to_edit_preferences_for_a_specific_content_type;
	public static String lb_Add_remove_edit_runtimes;
	public static String lb_Column_Name;
	public static String lb_Column_Location;
	public static String lb_Column_Type;
	public static String lb_Installed_runtimes;
	public static String lb_addERDialog_erName;
	public static String lb_addERDialog_erHome;
	public static String lb_addERDialog_erArgs;
	public static String lb_addERDialog_enterLocation;
	public static String lb_addERDialog_enterName;
	public static String lb_addERDialog_locationNotExists;
	public static String lb_addERDialog_duplicateName;
	public static String lbfmt_addERDialog_er_name_must_be_a_valid_file_name;
	public static String lb_addERDialog_pickERRootDialog_message;
	public static String lb_Runtime_Not_a_ER_root_Plugins_subdir_was_not_found;
	public static String lb_Runtime_Not_a_ER_root_runtime_executable_was_not_found;
	public static String lb_ok;
	public static String lbfmt_Locked_ER;
	public static String lb_Select_a_directory_to_search_in;
	public static String lb_Directory_Selection;
	public static String lb_Searching;
	public static String lbfmt_No_ERs_found_in;
	public static String lbfmt_Found_Searching;
	public static String lb_Extraction_Runtime_Details;
	public static String lb_Select_a_default_extraction_runtime;
	public static String lb_Installed_ER_location_no_longer_exists;
	public static String lb_Run_Active_Editor;
	public static String lb_Run_Autofind;
	public static String lb_Run_Navigation;
	public static String lb_Run_Browse;
	public static String lb_Run_Document_clustering;
	public static String lb_Run_Group_pages_by_similarity;
	public static String lb_Run_Clustering;
	public static String lb_Run_Wrapper;
	public static String lb_Run_Extract_information_from_reached_pages;
	public static String lb_Run_Web_Navigation_and_Extraction;
	public static String lb_Run_Load_start_page_instead_replaying_navigation;
	public static String lb_Run_Extraction_Files;
	public static String lb_Run_only_simulate_wrapping_on_visited_documents;
	public static String lb_Run_Start_location_must_be_specified;
	public static String lb_Run_Start_location_is_not_a_valid_URL;
	public static String lb_Run_Wrapper_file_must_be_specified;
	public static String lb_Run_Wrapper_file_does_not_exist;
	public static String lb_Run_Clustering_file_must_be_specified;
	public static String lb_Run_Clustering_file_does_not_exist;
	public static String lb_URL_Address;
	public static String lb_Run_Keep_running_after_completing_the_extraction;
	public static String lb_Start_location;
	public static String lb_Open_all_links_in_new_tabs;
	public static String lb_Which_actions_would_you_like_to_perform_on_start_page;
	public static String lb_Detect_menu_and_open_all_menu_items_in_new_tabs;
	public static String lb_Experiment_outdir;
	public static String lb_Experiment_type;
	// public static String lb_Experiment_type_SW_RUNNING_TIME;

	public static String msg_internal_learning_error;
	public static String msg_internal_wrapping_error;
	public static String msg_internal_highlighting_error;
	public static String msg_internal_examples_loading_error;
	public static String msg_internal_examples_saving_error;
	public static String msg_learning_failed;
	public static String msg_documents_loading_failed;
	public static String msgfmt_documents_saving_failed;
	public static String msgfmt_documents_saving_some_examples_not_saved;
	public static String msg_error_creating_default_webex_launch_config;
	public static String msg_unable_to_find_wrapper_file_to_run;
	public static String msg_failed_to_execute_web_extraction;
	public static String msg_error_failed_to_read_navigation_doccol;
	public static String msgfmt_error_failed_to_replay_navigation_error_exit_code;
	public static String msg_error_failed_to_launch_navigation_replaying;
	public static String msgfmt_warn_no_parent_matches_in_named;
	public static String msg_warn_no_parent_matches_in_unnamed;
	public static String msgfmt_info_doc_not_added_already_in_collection;
	public static String msg_info_docs_not_added_already_in_collection;
	public static String msg_error_address_is_not_valid_url;
	public static String msg_warn_address_should_start_with_http_or_https_prefix;
	public static String msg_error_empty_value_not_allowed_here;
	public static String msg_error_pattern_with_same_name_already_exists;
	public static String msg_error_invalid_start_URL_is_specified;
	public static String msg_error_no_active_wrapper_editor;
	public static String msg_error_can_not_locate_wrapper_file;
	public static String msg_error_file_missing;
	public static String msg_error_invalid_editor;
	public static String msg_error_ER_deleted;
	public static String msg_error_ER_missing;
	public static String msg_error_Run_Configuration_specifies_nonexisting_runtime_location;
	public static String msg_error_Run_Can_not_locate_a_default_extraction_runtime_to_be_used_for_the_extraction;
	public static String msg_error_Run_Location_of_the_default_extraction_runtime_is_invalid;
	public static String msg_error_no_doccol_output_generated;
	public static String msg_error_invalid_experiment_type_is_specified;
	public static String msg_error_invalid_experiment_outDir_is_specified;
	public static String msg_info_no_docs_obtained_with_replaying_the_navigation;

	public static String prg_learning_runing;
	public static String prg_evaluation_runing;
	public static String prg_grouping_documents_running;
	public static String prg_building_templates_running;
	public static String prg_loading_documents_running;
	public static String prg_loading_document;
	public static String prg_copying_dom_tree;
	public static String prg_loading_documents;
	public static String prg_building_template;
	public static String prg_building_fragments;
	public static String prg_choosing_group;
	public static String prg_highlighting_runing;
	public static String prg_replaying_navigation_running;
	public static String prg_importing_documents;
	public static String prg_loading_documents_from_navigation;
	public static String prg_docload_runing_suffix;
	public static String prg_loading_examples;

	public static String title_WrappingSuffix;
	public static String title_Web_Extraction_Configuration_Selection;

	public static String tt_Automatic_Learning_on_Change_in_Examples;
	public static String tt_Build_wrapper;
	public static String tt_Stop_wrapper_building;
	public static String tt_Automatic_Evaluation_on_Wrapper_Change;
	public static String tt_Evaluate_Wrapper;
	public static String tt_Restart_Learning_of_Filter;
	public static String tt_Sort_Unclassified_Documents_into_Groups;
	public static String tt_Mark_Document_as_Correctly_Wrapped;
	public static String tt_Define_One_Wrapper_for_All_Documents_in_Cluster;
	public static String tt_Open_Document_Collection;
	public static String tt_Save_Document_Collection;
	public static String tt_Build_Document_Templates_from_Document_Collection;
	public static String tt_Add_Unclassfied_Documents_from_File_to_Collection;
	public static String tt_Add_Document_from_Browser;
	public static String tt_Add_Document_URL;
	public static String tt_Add_Documents_Reached_by_Navigation;
	public static String tt_Move_Documents_Into_Unclassified_Group;
	public static String tt_Toggle_Fragmentation;
	public static String tt_Send_To_Table_View;

	public static String ExtAppRunner_Constructing_command_line;
	public static String ExtAppRunner_Launching_Application;
	public static String ExtAppRunner_Starting_Application;
	public static String ExtAppRunner_Specified_working_directory_does_not_exist_or_is_not_a_directory__0__;
	public static String ExtAppRunner_Specified_executable__0__does_not_exist_for__1__;
	public static String ExtAppRunner_ProcessDesc__0____1__;
	public static String ExtAppRunner_FailedCreateProcess;

	public static String Default_New_Navigation_Start_URL;
	public static String Default_New_Experiment_Outdir;

	// public static String ;
	// public static String ;
	// public static String ;
	// public static String ;

}
