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
package org.eclipse.atf.mozilla.ide.ui.jseval;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.UIJob;
import org.mozilla.interfaces.nsIDOMNode;

public class EvalDomSelectionAction extends DOMSelectionAction {

	/*
	 * This job is what actually does the eval. It checks if the page is ready
	 * to recieve the eval and reschedules itself with a delay if not. I tries
	 * a maximum number of times before returning an Error status.
	 * 
	 * Has to be in the UI thread to be able to use the xpcom instances directly
	 */
	protected class EvalNodeJob extends UIJob {

		private static final int MAX_RESHEDULE = 3;
		private static final long DELAY = 500;

		private IJSEvalPage evalPage;
		private nsIDOMNode evalNode;

		private int rescheduleCount = 0;

		public EvalNodeJob(IJSEvalPage evalPage, nsIDOMNode evalNode) {
			super("Eval Node Job");
			this.evalPage = evalPage;
			this.evalNode = evalNode;
			setRule(mutexRule);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (evalPage.isReady()) {
				evalPage.evalElement(evalNode);
				return Status.OK_STATUS;
			} else if (rescheduleCount < MAX_RESHEDULE) {
				rescheduleCount++;
				this.schedule(DELAY);
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "JavaScript eval not available.", null);
			}
		}

	}

	//assure that Eval Jobs do not occur concurrently (not sure if this applies to UI Jobs)
	private static ISchedulingRule mutexRule = new ISchedulingRule() {

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

	};

	public void run() {
		try {
			if (getSelection() != null && !getSelection().isEmpty()) {
				IWorkbenchPage page = MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

				//show the view if not visible
				/*
				 * It is IMPORTANT to show the View before querying the editor for the
				 * adapter. This way if the view had not been activated yet, it had a
				 * chance to do so.
				 * 
				 * If this is not done, there is a change that the adapter used in the
				 * view and the one from the getAdapter call would not the same.
				 */
				page.showView(JSEvalView.ID);

				IEditorPart editor = page.getActiveEditor(); //should be the BrowserEditor

				IJSEvalPage evalPage = (IJSEvalPage) editor.getAdapter(IJSEvalPage.class);
				if (evalPage != null) {

					/*
					 * schedule a job to perform the eval
					 * 
					 * It is important to perform in a Job because there is a chance that
					 * the JavaScript View is being activate for the first time, in
					 * which case it is going to setup the web page with the necessary
					 * JavaScript. This process is asynchronous so we us a Job that
					 * checks the ready state of the IJSEvalPage and reschedules itselt
					 * if it is not ready. (Bug #177058)
					 */
					Job evalNodeJob = new EvalNodeJob(evalPage, getSelection().getSelectedNode());
					evalNodeJob.schedule();

				}
			}
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		}

	}

}
