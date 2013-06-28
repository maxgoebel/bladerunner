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
package org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugScriptElement;
import org.eclipse.atf.mozilla.ide.debug.model.JSSourceLocator;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.atf.mozilla.ide.debug.ui.util.DebuggerSourceDisplayUtil;
import org.eclipse.atf.mozilla.ide.ui.util.SourceDisplayUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupResult;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class JSDebugScriptElementSourceDisplayAdapter implements ISourceDisplay {

	private JSDebugScriptElement fPrevScript;
	private SourceLookupResult fPrevResult;

	/**
	 * Constructs singleton source display adapter for stack frames.
	 */
	public JSDebugScriptElementSourceDisplayAdapter() {
		DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				for (int i = 0; i < events.length; i++) {
					DebugEvent event = events[i];
					switch (event.getKind()) {
					case DebugEvent.TERMINATE:
						clearCachedModel(event.getSource());
						// fall thru
					case DebugEvent.RESUME:
						if (!event.isEvaluation()) {
							clearSourceSelection(event.getSource());
						}
						break;
					}
				}
			}
		});
	}

	private SourceLookupJob fSourceLookupJob = new SourceLookupJob();

	/**
	 * A job to perform source lookup on the currently selected stack frame.
	 */
	class SourceLookupJob extends Job {

		private JSDebugScriptElement fTarget;
		private IWorkbenchPage fPage;

		protected SourceLocatorUtil locatorUtil = SourceLocatorUtil.getInstance();
		protected SourceDisplayUtil sourceDisplayUtil = new DebuggerSourceDisplayUtil();

		/**
		 * Constructs a new source lookup job.
		 */
		public SourceLookupJob() {
			super("Debug Source Lookup"); //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			setSystem(true);
		}

		public void setLookupInfo(JSDebugScriptElement script, IWorkbenchPage page) {
			fTarget = script;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {
				JSDebugScriptElement lookupScript = fTarget;

				String location = lookupScript.getLocation();
				try {
					if (location != null) {
						URL locationURL = new URL(location);

						String appBase = ((JSSourceLocator) lookupScript.getLaunch().getSourceLocator()).getAppBase();
						IProject project = ((JSSourceLocator) lookupScript.getLaunch().getSourceLocator()).getProject();
						IStorage sourceElement = locatorUtil.getSourceElement(locationURL, appBase, project);

						IEditorInput editorInput = sourceDisplayUtil.getEditorInput(sourceElement);
						String editorId = sourceDisplayUtil.getEditorId(editorInput, sourceElement);

						SourceLookupResult result = new SourceLookupResult(lookupScript, sourceElement, editorId, editorInput);

						synchronized (JSDebugScriptElementSourceDisplayAdapter.this) {
							fPrevResult = (SourceLookupResult) result;
							fPrevScript = lookupScript;
						}
						if (!monitor.isCanceled()) {
							fSourceDisplayJob.setDisplayInfo(result, fPage);
							fSourceDisplayJob.schedule();
						}

					}
				} catch (MalformedURLException mue) {
					IStatus status = new Status(IStatus.ERROR, MozillaDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, "Invalid URL supplied by Mozilla.  Unable to open source.", mue); //$NON-NLS-1$
					DebugPlugin.log(status);
					//fallthrough
				}
			}
			return Status.OK_STATUS;
		}

	}

	private SourceDisplayJob fSourceDisplayJob = new SourceDisplayJob();

	class SourceDisplayJob extends UIJob {

		private ISourceLookupResult fResult;
		private IWorkbenchPage fPage;

		protected SourceDisplayUtil sourceDisplayUtil = new DebuggerSourceDisplayUtil();

		public SourceDisplayJob() {
			super("Debug Source Display"); //$NON-NLS-1$
			setSystem(true);
			setPriority(Job.INTERACTIVE);
		}

		/**
		 * Constructs a new source display job
		 */
		public synchronized void setDisplayInfo(ISourceLookupResult result, IWorkbenchPage page) {
			fResult = result;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public synchronized IStatus runInUIThread(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {

				//This call assumes IFrame but has some good code for reusing Editors and setting decoration that should be nice to use
				//DebugUITools.displaySource(fResult, fPage);
				try {

					sourceDisplayUtil.openInEditor(fPage, fResult.getEditorInput(), ((JSDebugScriptElement) fResult.getArtifact()).getLineStart());

				} catch (PartInitException e) {
					MozillaDebugUIPlugin.log(e);
				}
			}
			return Status.OK_STATUS;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.ISourceDisplayAdapter#displaySource(java.lang.Object, org.eclipse.ui.IWorkbenchPage, boolean)
	 */
	public synchronized void displaySource(Object context, IWorkbenchPage page, boolean force) {
		JSDebugScriptElement script = (JSDebugScriptElement) context;

		//try to be quick
		if (!force && fPrevScript != null && script.getLocation().equals(fPrevScript.getLocation())) {
			if (!script.equals(fPrevScript))
				fPrevResult.updateArtifact(script);

			fSourceDisplayJob.setDisplayInfo(fPrevResult, page);
			fSourceDisplayJob.schedule();
		} else {
			fSourceLookupJob.setLookupInfo(script, page);
			fSourceLookupJob.schedule();
		}

	}

	/**
	 * Deselects any source decorations associated with the given thread or
	 * debug target.
	 * 
	 * @param source thread or debug target
	 */
	private void clearSourceSelection(Object source) {
		/*
		if (source instanceof IThread) {
			IThread thread = (IThread)source;
			DecorationManager.removeDecorations(thread);
			InstructionPointerManager.getDefault().removeAnnotations(thread);
		} else if (source instanceof IDebugTarget) {
			IDebugTarget target = (IDebugTarget)source;
			DecorationManager.removeDecorations(target);
			InstructionPointerManager.getDefault().removeAnnotations(target);
		}
		*/
	}

	/**
	 * Clear any cached results associated with the given object.
	 * 
	 * @param source
	 */
	private synchronized void clearCachedModel(Object source) {
		if (fPrevScript != null) {
			IDebugTarget target = null;
			if (source instanceof IDebugElement) {
				target = ((IDebugElement) source).getDebugTarget();
			}
			if (fPrevScript.getDebugTarget().equals(target)) {
				fPrevScript = null;
				fPrevResult = null;
			}
		}
	}

	private void revealLineInEditor(ITextEditor editor, int lineNumber) {

		lineNumber--; // Document line numbers are 0-based. Debug line numbers are 1-based.

		IRegion region = null;

		IDocumentProvider provider = editor.getDocumentProvider();
		IEditorInput input = editor.getEditorInput();
		try {
			provider.connect(input);
		} catch (CoreException e) {
			return;
		}
		try {
			IDocument document = provider.getDocument(input);
			if (document != null)
				region = document.getLineInformation(lineNumber);
		} catch (BadLocationException e) {
		} finally {
			provider.disconnect(input);
		}

		if (region != null) {
			editor.selectAndReveal(region.getOffset(), 0);
		}
	}

}
