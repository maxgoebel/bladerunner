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
package at.tuwien.dbai.bladeRunner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor;
import at.tuwien.prip.common.utils.IRef;
import at.tuwien.prip.common.utils.Ref;

/**
 * 
 * DocWrapUIUtils.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 25, 2011
 */
public final class DocWrapUIUtils extends AbstractUIPlugin {

	/**
	 * Status code indicating an unexpected internal error (value
	 * <code>150</code>).
	 */
	public static final int INTERNAL_ERROR = 150;
	public static final int USER_ERROR = 250;

	public static String activePerspective;

	// public static PatternEngine patternEngine = new PatternEngine();
	// public static SemanticsEngine semanticsEngine = new SemanticsEngine();

	static {

	}

	public static AnnotatorEditor getWrapperEditor() {
		IEditorPart iep = getActiveEditor();

		if (iep != null && iep instanceof AnnotatorEditor)
			return (AnnotatorEditor) iep;

		return null;
	}

	// public static WeblearnEditor getWeblearnEditor() {
	// IEditorPart iep = getActiveEditor();
	//
	// if (iep instanceof WeblearnEditor)
	// return (WeblearnEditor) iep;
	//
	// else if (iep instanceof WrapperEditor)
	// return ((WrapperEditor)iep).getWlEditor();
	// return null;
	// }

	public static DocWrapEditor getDocWrapEditor() {
		IEditorPart iep = getActiveEditor();

		if (iep instanceof DocWrapEditor)
			return (DocWrapEditor) iep;

		else if (iep instanceof AnnotatorEditor)
			return ((AnnotatorEditor) iep).getGraphEditor();
		return null;
	}

	public static IEditorPart getActiveEditor() {
		if (Display.getCurrent() != null) {
			IWorkbench wb = LearnUIPlugin.getDefault().getWorkbench();
			if (wb == null)
				return null;

			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			if (win == null)
				return null;

			IWorkbenchPage page = win.getActivePage();
			if (page == null)
				return null;

			return page.getActiveEditor();
		}

		// workaround for getActiveWorkbenchWindow()
		// intentionally returning null when not on gui thread
		final IRef<IEditorPart> ret = new Ref<IEditorPart>();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				ret.set(getActiveEditor());
			}
		});
		return ret.get();

	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbench wb = LearnUIPlugin.getDefault().getWorkbench();
		if (wb == null)
			return null;

		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		return win;
	}

	public static Shell getActiveWorkbenchWindowShell() {
		IWorkbenchWindow win = getActiveWorkbenchWindow();
		if (win == null)
			return null;

		return win.getShell();
	}

	public static String getUniqueIdentifier() {
		return LearnUIPlugin.getDefault().getBundle().getSymbolicName();
	}

	public static String getPlugindId() {
		return getUniqueIdentifier();
	}

	public static IStatus userError(String detailsMsg) {
		return new Status(IStatus.ERROR, getPlugindId(), USER_ERROR,
				detailsMsg, null);
	}

}
