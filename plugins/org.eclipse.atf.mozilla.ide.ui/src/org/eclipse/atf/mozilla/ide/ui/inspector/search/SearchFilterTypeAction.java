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
package org.eclipse.atf.mozilla.ide.ui.inspector.search;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class SearchFilterTypeAction extends Action implements IMenuCreator {

	public static final String FILTERTYPE_PROP = "filterType";
	protected Menu filterTypeMenu = null;
	protected MenuManager filterTypeMenuManager = null;
	
	protected Action filterByElementNameAction = null;
	protected Action filterByElementIdAction = null;
	protected Action filterByElementClassAction = null;
	
	
	public SearchFilterTypeAction(){
		this.setMenuCreator(this);
		this.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERBYNAME_IMG_ID));
		this.setToolTipText("Filter DOM elements by name.");
		
		createSubActions();
	}
	
	protected void createSubActions(){
		
		filterByElementNameAction = new Action(){

			public void run() {
				SearchFilterTypeAction.this.setImageDescriptor(this.getImageDescriptor());
				SearchFilterTypeAction.this.setToolTipText(this.getToolTipText());
				SearchFilterTypeAction.this.firePropertyChange( "filterType", null, new Integer(DOMTreePatternFilter.ELEMENTNAME_FILTERTYPE) );
			}
		};
		filterByElementNameAction.setText( "Name Filter" );
		filterByElementNameAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERBYNAME_IMG_ID));
		filterByElementNameAction.setToolTipText("Filter DOM elements by name.");
		
		filterByElementIdAction = new Action(){

			public void run() {
				SearchFilterTypeAction.this.setImageDescriptor(this.getImageDescriptor());
				SearchFilterTypeAction.this.setToolTipText(this.getToolTipText());
				SearchFilterTypeAction.this.firePropertyChange( "filterType", null, new Integer(DOMTreePatternFilter.ELEMENTID_FILTERTYPE) );
			}
		};
		filterByElementIdAction.setText( "ID Filter" );
		filterByElementIdAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERBYID_IMG_ID));
		filterByElementIdAction.setToolTipText("Filter DOM elements by id attribute.");
		
		filterByElementClassAction = new Action(){

			public void run() {
				SearchFilterTypeAction.this.setImageDescriptor(this.getImageDescriptor());
				SearchFilterTypeAction.this.setToolTipText(this.getToolTipText());
				SearchFilterTypeAction.this.firePropertyChange( FILTERTYPE_PROP, null, new Integer(DOMTreePatternFilter.ELEMENTCLASS_FILTERTYPE) );
			}
		};
		filterByElementClassAction.setText( "Class Filter" );
		filterByElementClassAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERBYCLASS_IMG_ID));
		filterByElementClassAction.setToolTipText("Filter DOM elements by class attribute.");
		
		filterTypeMenuManager = new MenuManager();
		filterTypeMenuManager.add(filterByElementNameAction);
		filterTypeMenuManager.add(filterByElementIdAction);
		filterTypeMenuManager.add(filterByElementClassAction);
	}
	
	public void dispose() {
		
	}
	
	public Menu getMenu(Control parent) {
		return filterTypeMenuManager.createContextMenu(parent);
	}

	public Menu getMenu(Menu parent) {

		if( filterTypeMenu != null && filterTypeMenu.getParent().equals(parent) ){
			return filterTypeMenu;
		}
		else{
			//dispose of previous menu
			if( filterTypeMenu != null && !filterTypeMenu.isDisposed() ){
				filterTypeMenu.dispose();
			}
			
			filterTypeMenu = new Menu( parent );
			
			new ActionContributionItem( filterByElementNameAction ).fill( filterTypeMenu, 0 );
			new ActionContributionItem( filterByElementIdAction ).fill( filterTypeMenu, 1 );
			new ActionContributionItem( filterByElementClassAction ).fill( filterTypeMenu, 2 );

			//recreate menu
			return filterTypeMenu;
		}
	}

}
