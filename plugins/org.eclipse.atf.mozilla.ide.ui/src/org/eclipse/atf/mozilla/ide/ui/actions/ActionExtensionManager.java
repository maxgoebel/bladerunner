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
package org.eclipse.atf.mozilla.ide.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Manages action extensions and configures them
 * for a given control as a menu.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class ActionExtensionManager {
	
	/**
	 * Action XML strings
	 */
	public static final String ACTION_POINT = "org.eclipse.atf.mozilla.ide.ui.action";
	public static final String ITEM_NODE = "action";
	public static final String CLASS_ATTR = "class";
	public static final String TEXT_ATTR = "text";
	public static final String ICON_ATTR = "icon";
	public static final String TYPES_ATTR = "types";
	public static final String PATH_ATTR = "path";
	
	private static MenuManager manager = null;
	
	//String<Path> -> List<IAction>
	private static Map actionGroups = new HashMap();
	private static List configurables = null;
	
	/**
	 * 
	 * @param control
	 * @param selection
	 * @return
	 */
	public static Menu configure(Control control, IDOMNodeSelection selection) {
		manager = new MenuManager();
		actionGroups.clear();
		if( configurables == null ) {
			configurables = new ArrayList();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IExtensionPoint ep = reg.getExtensionPoint(ACTION_POINT);
			IExtension[] extensions = ep.getExtensions();
			for(int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] ce = extensions[i].getConfigurationElements();
				for(int j = 0; j < ce.length; j++ ) {		
					if( ce[j].getName().equals(ITEM_NODE) ) {
						configurables.add(ce[j]);
					}
				}
			}
		}
		for( int i = 0; i < configurables.size(); i++ ) {
			configureAction((IConfigurationElement)configurables.get(i), selection);
		}
		Object[] keys = actionGroups.keySet().toArray();
		for( int i = 0; i < keys.length; i++ ) {
			List actions = (List)actionGroups.get(keys[i]);
			MenuManager subManager = null; 
			for( int j = 0; j < actions.size(); j++ ) {
				DOMSelectionAction action = (DOMSelectionAction)actions.get(j);
				if( action.getPath().indexOf('/') != -1 ) {
					if( subManager == null ) {
						if( manager.indexOf(action.getPath()) != -1 ) {
							subManager = (MenuManager)manager.getItems()[manager.indexOf(action.getPath())];
						} else {
							subManager = new MenuManager(action.getPath().substring(action.getPath().indexOf('/')+1,action.getPath().length()),action.getPath());
							manager.add(subManager);
						}
					}
					subManager.add(action);
				} else {
					manager.add(action);
				}
			}
			manager.add( new Separator() );
		}
		return manager.createContextMenu(control);
	}
	
	/**
	 * Creates an action from a configuration element and sets it with a 
	 * DOM node selection.
	 * 
	 * @param element - extension point element
	 * @param selection - DOM node selection
	 */
	private static void configureAction(IConfigurationElement element, IDOMNodeSelection selection) {
		String menuitem = element.getAttribute(CLASS_ATTR);
		String text = element.getAttribute(TEXT_ATTR);
		String icon = element.getAttribute(ICON_ATTR);
		String types = element.getAttribute(TYPES_ATTR);
		String path = element.getAttribute(PATH_ATTR);
		path = path == null ? "ROOT": path;
		String group = path.indexOf('/') != -1 ? path.substring(0, path.indexOf('/')) : path;
		if( menuitem != null && text != null && icon != null ) {
			try {
				String pluginID = element.getContributor().getName();
				Object item = element.createExecutableExtension(CLASS_ATTR);
				if( item instanceof DOMSelectionAction ) {
					DOMSelectionAction action = (DOMSelectionAction)item;
					if( types != null ) {
						action.setTypes(types);
					}
					action.setPath(path);
					List list = actionGroups.containsKey(group) ? (List)actionGroups.get(group) : new ArrayList();
					list.add(action);
					actionGroups.put(group, list);
					manager.addMenuListener(action);
					action.setText(text);
					action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(pluginID,icon));
					action.setSelection(selection);
				}
			} catch (Exception e) {}
		}
	}

}
