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

package org.eclipse.atf.mozilla.ide.ui.browser.toolbar;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public class NavigationBar extends Composite {
	
	//These will be use to set tools so that only on action can be set in these
	//areas at a time
	protected static final String BACK_TOOL = "BACK";
	protected static final String FORWARD_TOOL = "FORWARD";
	protected static final String REFRESH_TOOL = "REFRESH";
	protected static final String STOP_TOOL = "STOP";
	protected static final String GO_TOOL = "GO";
	protected static final String EXTENSION_TOOL = "EXTENSION";
	
	//UI elements
	protected ToolBarManager navigationTBManager = null;
	protected Text location	= null;
	protected ToolBarManager extensibleTBManager = null;
	protected ToolBarManager extensibleMenuTBManager = null;
	protected MenuManager extensibleMenuManager = null;
	
	public NavigationBar( Composite parent, int style ) {
		super( parent, style );
		createUI();
	}
	
	protected void createUI(){
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 2;
		this.setLayout(gridLayout);
		
		//TOOLBAR AREA
		navigationTBManager = new ToolBarManager( SWT.FLAT );
				
		navigationTBManager.add(new GroupMarker(BACK_TOOL));
		navigationTBManager.add(new GroupMarker(FORWARD_TOOL));
		navigationTBManager.add(new GroupMarker(REFRESH_TOOL));
		navigationTBManager.add(new GroupMarker(STOP_TOOL));
		
		navigationTBManager.createControl(this);
		
		Label labelAddress = new Label(this, SWT.FILL);
		labelAddress.setText("Address");
		
		location = new Text(this, SWT.BORDER);
		location.setText( "http://" );
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		location.setLayoutData( data );
		
		location.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				//call the Go Action if it exists
				IContributionItem item = extensibleTBManager.find(GO_TOOL+"ACTION");
				
				if( item != null )
					((ActionContributionItem)item).getAction().run();
				
			}
		});
		
		/*
		 * need to override the relayout method because the default implementation does
		 * not seem to operate after adding an extension to the Toolbar.
		 * 
		 * this implementation will relayout every time the oldcount and newcount are 
		 * different.
		 */
		extensibleTBManager = new ToolBarManager( SWT.FLAT ){

			protected void relayout(ToolBar layoutBar, int oldCount, int newCount) {
				
				if( oldCount != newCount )
					layoutBar.getParent().layout();
				else
					super.relayout(layoutBar, oldCount, newCount);
			}
			
		};
		
		extensibleTBManager.add(new GroupMarker(GO_TOOL));
		extensibleTBManager.add( new Separator(EXTENSION_TOOL));
		
		extensibleTBManager.createControl( this );
		
		//Menu
		
		extensibleMenuTBManager = new ToolBarManager( SWT.FLAT );
		
		Action openExtensibleMenuAction = new Action(){

			public void run() {
				
				Menu aMenu = extensibleMenuManager.createContextMenu( NavigationBar.this );
				//need to check that there are contributions to the extensible menu
				
				//find location to open Menu in relation to the Menu toolbar button
				Point toolbarSize = extensibleMenuTBManager.getControl().getSize();
				Point location = extensibleMenuTBManager.getControl().toDisplay(0,toolbarSize.y);
		        
		        aMenu.setLocation(location.x, location.y);
		        aMenu.setVisible(true);
				
			}
			
		};
		openExtensibleMenuAction.setImageDescriptor( MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.SHOWMENU_IMG_ID) );
		extensibleMenuTBManager.add( openExtensibleMenuAction );
		extensibleMenuTBManager.createControl( this );
		
		extensibleMenuManager = new MenuManager("NavBarMenu", "NavBarMenu");		
	}	
	
	public void setLocationURL( String url ){
		location.setText( url );
	}
	
	public String getLocationURL(){
		return location.getText();
	}
	
	//interface to add actions (only allow one of the non-Extension actions to
	//exist at a time
	public void setBackAction( IAction action ){
		setAction(action, BACK_TOOL);
	}
	
	public void setForwardAction( IAction action ){
		setAction(action, FORWARD_TOOL);
	}
	
	public void setRefreshAction( IAction action ){
		setAction(action, REFRESH_TOOL);
	}
	
	public void setStopAction( IAction action ){
		setAction(action, STOP_TOOL);
	}
	
	public void setGoAction( IAction action ){
		setAction(action, GO_TOOL);
	}
	
	//remove the existing Action and substutite
	protected void setAction( IAction action, String group ){
				
		action.setId(group+"ACTION");
		if( group == GO_TOOL ){
			IContributionItem item = extensibleTBManager.find( action.getId() );
			if( item != null )
				extensibleTBManager.remove(item);
			
			extensibleTBManager.appendToGroup(GO_TOOL, action);
			extensibleTBManager.update(true); //important call to get the newly added menus to show
		}
		else{
			IContributionItem item = navigationTBManager.find( action.getId() );
			if( item != null )
				navigationTBManager.remove(item);
			
			navigationTBManager.appendToGroup(group, action);
			navigationTBManager.update(true); //important call to get the newly added menus to show
		}
	}
	
	public void addExtensionAction( IAction action ){
		extensibleTBManager.appendToGroup(EXTENSION_TOOL, action);
		extensibleTBManager.update(true); //important call to get the newly added menus to show
	}
	
	public void removeExtensionAction( IAction action ){
		extensibleTBManager.remove(action.getId());
		extensibleTBManager.update(true); //important call to get the newly added menus to show
	}
	
	public void addMenuExtensionAction( IAction action ){
		extensibleMenuManager.add(action);
		//extensibleTBManager.update(true); //important call to get the newly added menus to show
	}
	
	public void removeMenuExtensionAction( IAction action ){
		extensibleMenuManager.remove(action.getId());
		//extensibleTBManager.update(true); //important call to get the newly added menus to show
	}
}
