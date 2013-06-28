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
package org.eclipse.atf.mozilla.ide.ui.browser.toolbar;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.common.SelectionProviderHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.progress.WorkbenchJob;
import org.mozilla.interfaces.nsIDOMNode;

public class DOMSelectionBar extends BrowserToolbar implements SelectionListener {

	private static final String SELECTED = "Selected: ";
	private static final String HOVERED = "Hovered: ";
	private static final String NONE = "None";

	protected IWebBrowser browser;
	protected Link domPath = null;
	protected nsIDOMNode node = null;
	protected Job refreshJob = null;
	protected SelectionProviderHandler handler;
	protected ListenerList listeners = new ListenerList();
	
	public void dispose() {
		if( refreshJob != null )
			refreshJob.cancel();
	}
	
	protected void createRefreshJob(){
		refreshJob = new WorkbenchJob("DOMSelectionBar: Restore to Selection"){
		
			public IStatus runInUIThread(IProgressMonitor monitor) {
				updateSelectedPath();
		        return Status.OK_STATUS;
			}
		
		};
		refreshJob.setSystem(true);
	}
		
	//updates the content of the selection path box
	private void updateSelectedPath() {
		if( domPath.getParent() == null || domPath.getParent().isDisposed() )
			return;
		
		//clear the links
		if( node == null ){
			domPath.setText(SELECTED + NONE);			
		}
		else{
			StringBuffer path = new StringBuffer("");
			int count = 0;
			nsIDOMNode _node = this.node;
			if( _node.getNodeName().equals("#document") ) {
				path.insert(0, "<A HREF=\""+count+"\">"+_node.getNodeName()+"</A>");
			} else {
				while( _node != null ) {
					if( !_node.getNodeName().equals("#document")) {
						path.insert(0, "/<A HREF=\""+count+"\">"+_node.getNodeName()+"</A>");
					}
					count++;
					_node = _node.getParentNode();
				}
				path.deleteCharAt(0);
			}
			path.insert(0, SELECTED);
			domPath.setText(path.toString());
			
		}
	
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		//do nothing		
	}
	
	public void widgetSelected(SelectionEvent e) {
		if( node != null ) {
			nsIDOMNode selected = node;
			for( int i = 0; i < Integer.parseInt(e.text);i++ ) {
				selected = selected.getParentNode();
			}
			final nsIDOMNode node = selected;
			browser.setSelection(new IDOMNodeSelection() {
			
				public boolean isEmpty() {
					return false;
				}
			
				public nsIDOMNode getSelectedNode() {
					return node;
				}
			
			});
		}
	}

	public void fillToolbar(Composite parent) {
		domPath = new Link( parent, SWT.HORIZONTAL );
		domPath.setText(SELECTED + NONE);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		domPath.setLayoutData(data);
		
		domPath.addSelectionListener(this);
		
		createRefreshJob();
	}
	
	public void show(boolean show) {
		if( node != null ) {
			updateSelectedPath();
		}
		super.show(show);
	}

	public void setHoverSelection(IDOMNodeSelection selection) {
		if( selection != null && !selection.isEmpty() ) {
			nsIDOMNode tempNode = selection.getSelectedNode();
			//clear the links
			if( tempNode != null ){
				refreshJob.cancel();
				
				StringBuffer path = new StringBuffer("");
				int count = 0;
				nsIDOMNode _node = tempNode;
				if( _node.getNodeName().equals("#document") ) {
					path.insert(0, _node.getNodeName());
				} else {
					while( _node != null ) {
						if( !_node.getNodeName().equals("#document")) {
							path.insert(0, "/"+_node.getNodeName() );
						}
						count++;
						_node = _node.getParentNode();
					}
					path.deleteCharAt(0);
				}
				path.insert(0, HOVERED);
				domPath.setText(path.toString());
				
				//delay refresh to show the selection
		    	refreshJob.schedule(500);
				
			}
		}
	}

	public void setNodeSelection(IDOMNodeSelection selection) {
		
		//protect against disposed
		if( domPath == null || domPath.isDisposed() )
			return;
		
		if( selection != null && !selection.isEmpty() ) {
			this.node = selection.getSelectedNode();
		} else {
			this.node = null;
		}
		updateSelectedPath();
	}

	public void setBrowser(IWebBrowser browser) {
		this.browser = browser;
	}	
}
