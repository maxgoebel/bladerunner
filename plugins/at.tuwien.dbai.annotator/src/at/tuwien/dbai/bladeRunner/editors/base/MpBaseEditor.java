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
package at.tuwien.dbai.bladeRunner.editors.base;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor;

/**
 * MpMozBrowserEditor.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 27, 2011
 */
public abstract class MpBaseEditor extends MultiPageEditorPart implements
		IResourceChangeListener
// , IWebBrowser
{

	public static String ID = "at.tuwien.prip.docwrap.annotator.mpWebEditor";

	/**
	 * Constructor.
	 */
	public MpBaseEditor() {
		super();

		// override default XMIimplementation to add DOM persistance
		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().
		// put(mt._UI_WrapperEditor_FilenameExtension, new
		// XMIResourceWithDOMFactoryImpl());
	}

	/**
	 * Constructor.
	 */
	public MpBaseEditor(IEditorSite site, IEditorInput input) {
		this();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	// /**
	// * Delegate method.
	// *
	// * @param url
	// */
	// public void goToURL (URL url) {
	// try {
	// openURL(url);
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// }
	// }

	public void setEditorPartName(String title) {
		String shortTitle = title;// TextUtils.shorten(title, 15);
		setPartName(shortTitle);
		setTitleToolTip(title);
	}

	@Override
	protected void pageChange(int newPageIndex) {

		IEditorPart activeEditor = getEditor(newPageIndex);
		if (activeEditor != null) {

			IToolBarManager mgr = getEditorSite().getActionBars()
					.getToolBarManager();
			// handle the toolbar contributions per page
			// IToolBarManager mgr =
			// activeEditor.getEditorSite().getActionBars()
			// .getToolBarManager();
			mgr.removeAll();
			mgr.update(true);

			if (activeEditor instanceof DocWrapEditor) {
				// ((DocWrapEditor)activeEditor).canvas.plot(true);
				// ((DocWrapEditor)activeEditor).canvas.calibrateGraphCenter(true);
				// ((DocWrapEditor)activeEditor).canvas.plot(true);
			}
			// this fixes a null selection exception
			// ISelectionProvider selectionProvider = activeEditor.getSite()
			// .getSelectionProvider();
			// if
			// (selectionProvider==null||selectionProvider.getSelection()==null)
			// {
			// return;
			// }

		}

		super.pageChange(newPageIndex);
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		// IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {

		setSite(site);
		setInput(editorInput);

		// if (editorInput instanceof BasicBrowserEditorInput) {
		// super.init(site, editorInput);
		// } else if (editorInput instanceof MozBrowserEditorInput) {
		// super.init(site, editorInput);
		// } else
		// if (!(editorInput instanceof IFileEditorInput))
		// {
		// throw new
		// PartInitException("Invalid Input: Must be IFileEditorInput");
		// }
		//
		// Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		// Map<String,Object> m = reg.getExtensionToFactoryMap();
		// m.put("forummodel", new XMIResourceFactoryImpl());

		// ResourceSet resSet = new ResourceSetImpl();
		// Resource res =
		// resSet.getResource(URI.createURI("model/forum.forummodel"),true);
		// Object forum = res.getContents().get(0);
		// System.out.println();

	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	// @Override
	// public String getId() {
	// return ID;
	// }
}
