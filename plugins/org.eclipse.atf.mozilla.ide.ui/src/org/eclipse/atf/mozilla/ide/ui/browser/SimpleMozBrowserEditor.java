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
package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;

public class SimpleMozBrowserEditor extends EditorPart {

	protected Browser browser = null;

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void createPartControl(Composite parent) {

		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.verticalSpacing = 1;
		displayArea.setLayout(gridLayout);

		GridData data;

		Label l = new Label(displayArea, SWT.NONE);
		l.setText("Hello World");

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		l.setLayoutData(data);

		//Browser
		browser = new Browser(displayArea, SWT.MOZILLA);

		//		nsIWebBrowser mozBrowser = (nsIWebBrowser)this.browser.getWebBrowser();
		//		nsIDOMWindow window = mozBrowser.getContentDOMWindow();
		//		
		//		nsIInterfaceRequestor req = (nsIInterfaceRequestor) window
		//				.queryInterface(nsIInterfaceRequestor.NS_IINTERFACEREQUESTOR_IID);
		//		nsIWebNavigation webNav = (nsIWebNavigation) req
		//				.getInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
		//		
		//		nsIDocShell shell = (nsIDocShell)webNav.queryInterface( nsIDocShell.NS_IDOCSHELL_IID );
		//		
		//		nsIWebProgress webProgress = (nsIWebProgress)shell.queryInterface( nsIWebProgress.NS_IWEBPROGRESS_IID );

		((nsIWebBrowser) browser.getWebBrowser()).addWebBrowserListener(new nsIWebProgressListener() {

			public void onLocationChange(nsIWebProgress aWebProgress, nsIRequest aRequest, nsIURI aLocation) {
			}

			public void onProgressChange(nsIWebProgress aWebProgress, nsIRequest aRequest, int aCurSelfProgress, int aMaxSelfProgress, int aCurTotalProgress, int aMaxTotalProgress) {
			}

			public void onSecurityChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aState) {
			}

			public void onStateChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aStateFlags, long aStatus) {
				MozIDEUIPlugin.debug("onStateChange()");

			}

			public void onStatusChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aStatus, String aMessage) {
			}

			public nsISupports queryInterface(String uuid) {
				return Mozilla.queryInterface(this, uuid);
			}

		}, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);

		//		webProgress.addProgressListener( new nsIWebProgressListener(){
		//
		//			public void onLocationChange(nsIWebProgress aWebProgress, nsIRequest aRequest, nsIURI aLocation) {
		//				MozIDEUIPlugin.debug("onLocationChange()");
		//			}
		//
		//			public void onProgressChange(nsIWebProgress aWebProgress, nsIRequest aRequest, int aCurSelfProgress, int aMaxSelfProgress, int aCurTotalProgress, int aMaxTotalProgress) {
		//				MozIDEUIPlugin.debug("onProgressChange()");
		//			}
		//
		//			public void onSecurityChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aState) {
		//				MozIDEUIPlugin.debug("onSecurityChange()");
		//			}
		//
		//			public void onStateChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aStateFlags, long aStatus) {
		//				MozIDEUIPlugin.debug("onStateChange()");
		//				
		//			}
		//
		//			public void onStatusChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aStatus, String aMessage) {
		//				MozIDEUIPlugin.debug("onStatusChange()");
		//			}
		//
		//			public nsISupports queryInterface(String uuid) {
		//				return Mozilla.queryInterface( this, uuid );
		//			}
		//			
		//		}, nsIWebProgress.NOTIFY_ALL );

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);

		browser.setUrl("http://google.com");

		Label l2 = new Label(displayArea, SWT.NONE);
		l2.setText("Hello World2");

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		l2.setLayoutData(data);
	}

	public void setFocus() {
		if (!browser.isDisposed())
			browser.setFocus();
	}

}
