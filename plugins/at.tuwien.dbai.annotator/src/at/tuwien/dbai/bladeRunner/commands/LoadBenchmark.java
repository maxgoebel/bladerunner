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
package at.tuwien.dbai.bladeRunner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;
import at.tuwien.dbai.bladeRunner.views.bench.LoadBenchmarkJob;

/**
 * LoadBenchmark.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 26, 2011
 */
public class LoadBenchmark extends AbstractHandler {
	/**
	 * Constructor.
	 */
	public LoadBenchmark() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		String home = System.getenv("HOME");
		dialog.setFilterPath(home);
		// dialog.setFilterExtensions(new String[] {"*.xml"});
		// dialog.setFilterNames(new String[] {"Benchmark files"});

		/* open dialog */
		String fileSelected = dialog.open();
		if (fileSelected != null) {
			LoadBenchmarkJob loadJob = new LoadBenchmarkJob(
					"Loading ZIP", fileSelected);
			loadJob.schedule();
		}

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow wind[] = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			if (wind.length > 0) {
				window = wind[0];
			}
		}
		if (window != null) {
			final IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				// close all open editors
				shell.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						page.closeAllEditors(false);
					}
				});

				// set selection to annotation view
				IViewPart view = page.findView(BenchmarkNavigatorView.ID);
				if (view != null) {
					BenchmarkNavigatorView annoView = (BenchmarkNavigatorView) view;
					annoView.setInput(null);
				}
			}
		}

		return null;
	}

}
