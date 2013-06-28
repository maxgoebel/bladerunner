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
package org.eclipse.atf.mozilla.ide.debug.ui.scriptview;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugThread;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.contexts.DebugContextManager;
import org.eclipse.debug.internal.ui.viewers.model.provisional.PresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.TreeModelViewer;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScriptView extends AbstractDebugView implements IDebugContextListener {

	public static final String ID = "org.eclipse.atf.mozilla.ide.debug.ui.scriptview";

	protected Viewer createViewer(Composite parent) {
		//set as listener when the Debug context changes
		DebugContextManager.getDefault().addDebugContextListener(this);

		TreeModelViewer treeViewer = new TreeModelViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION, new PresentationContext(ID));

		configureDblClickBehavior(treeViewer);
		return treeViewer;
	}

	protected void configureToolBar(IToolBarManager tbm) {

	}

	protected void createActions() {

	}

	protected void fillContextMenu(IMenuManager menu) {

	}

	protected String getHelpContextId() {
		return null;
	}

	protected void configureDblClickBehavior(TreeViewer treeViewer) {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

				ISelection selection = event.getSelection();

				if (!selection.isEmpty() && (selection instanceof StructuredSelection)) {

					Object selectedObject = ((StructuredSelection) selection).getFirstElement();

					//look for the adapter ISourceDisplayAdapter
					if (selectedObject instanceof IAdaptable) {

						IAdaptable adaptable = (IAdaptable) selectedObject;
						ISourceDisplay adapter = (ISourceDisplay) adaptable.getAdapter(ISourceDisplay.class);
						if (adapter != null) {

							adapter.displaySource(selectedObject, getSite().getPage(), true);
						}

					}
				}
			}

		});
	}

	public void dispose() {
		//remove as listener
		DebugContextManager.getDefault().removeDebugContextListener(this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesHidden()
	 */
	protected void becomesHidden() {
		setViewerInput(null);
		super.becomesHidden();
	}

	/**
	 * Tries to find the current context on the Debug view and sets the input of the viewer accordingly.
	 * @see org.eclipse.debug.ui.AbstractDebugView#becomesVisible()
	 */
	protected void becomesVisible() {
		super.becomesVisible();
		ISelection selection = DebugContextManager.getDefault().getContextService(getSite().getWorkbenchWindow()).getActiveContext();
		Object contextElement = extractDebugElement(selection);
		setViewerInput(contextElement);
	}

	protected void setViewerInput(Object context) {

		if (context == null) {

			showViewer();
			getViewer().setInput(null);
		} else if (context instanceof JSDebugElement) {

			Object currentThread = getViewer().getInput();

			//need to find the Thread object in the model
			//We are assuminng on Thread for JavaScript
			JSDebugThread thread;
			try {
				thread = findJSDebugThread((JSDebugElement) context);
			} catch (DebugException e) {
				MozillaDebugUIPlugin.log(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, "ScriptView could not locate the JavaScript Thread from selection... <" + e.getMessage() + ">!", e));
				showMessage("ScriptView could not locate the JavaScript Thread from selection...");
				return;
			}

			//only change when different
			if (thread != currentThread) {
				getViewer().setInput(thread);
			}
			showViewer();
			getViewer().refresh();
		}
	}

	protected JSDebugThread findJSDebugThread(JSDebugElement selectedElement) throws DebugException {

		ILaunch launch = selectedElement.getLaunch();
		if (!launch.isTerminated()) {
			return (JSDebugThread) launch.getDebugTarget().getThreads()[0];
		} else
			return null;
	}

	/*
	 * IDebugContextListener
	 */
	/**
	 * This sets the input of the viewer when the context is activated
	 * 
	 * @param event a DebugContextEvent instance
	 * 
	 * @see org.eclipse.debug.ui.contexts.IDebugContextListener
	 */
	public void debugContextChanged(DebugContextEvent event) {
		if (!isAvailable() || !isVisible()) {
			return;
		}

		if ((event.getFlags() & DebugContextEvent.ACTIVATED) != 0) {
			Object contextElement = extractDebugElement(event.getContext());

			/*
			 * View is currently only valid when in a Mozilla Debug session
			 */
			if (contextElement instanceof JSDebugElement) {

				setViewerInput(contextElement);
			} else {
				showMessage("View only valid for Browser Debug context.");
			}
		}
	}

	/**
	 * This object will always return the first selected object from an IStructuredSelection.
	 * Otherwise it returns null.
	 * 
	 * @param selection a ISelection (this method only handles IStructuredSelection)
	 * @return An object or null
	 */
	protected Object extractDebugElement(ISelection selection) {
		Object debugElement = null;
		if (selection instanceof IStructuredSelection)
			debugElement = ((IStructuredSelection) selection).getFirstElement();

		return debugElement;
	}

}
