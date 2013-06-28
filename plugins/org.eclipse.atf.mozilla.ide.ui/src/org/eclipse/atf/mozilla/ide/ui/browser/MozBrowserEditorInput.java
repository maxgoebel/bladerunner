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

package org.eclipse.atf.mozilla.ide.ui.browser;

import java.net.MalformedURLException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;

public class MozBrowserEditorInput implements IEditorInput {

	protected String url = null;
	
	//MEMENTO SUPPORT
	protected MozBrowserEditorPesistanceSuport pesistanceSupport = new MozBrowserEditorPesistanceSuport();
	
	public MozBrowserEditorInput( String url ){
		setURL(url);
	}
	
	public MozBrowserEditorInput( IFileEditorInput input ) throws MalformedURLException{
		setURL(input.getFile().getLocationURI().toURL().toString());
	}
	
	public String getURL(){
		return url;
	}
	
	public void setURL( String url ){
		this.url = url;
		pesistanceSupport.setURL( url );
	}
	
	public boolean exists() {
		return url != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		return url;
	}

	public IPersistableElement getPersistable() {
		return pesistanceSupport;
	}

	public String getToolTipText() {
		return url.toString();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
