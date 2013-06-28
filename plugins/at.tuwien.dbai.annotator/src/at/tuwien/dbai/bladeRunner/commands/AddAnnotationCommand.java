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
import org.eclipse.ui.PlatformUI;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;

/**
 * AddAnnotationCommand.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 19, 2013
 */
public class AddAnnotationCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getShell();

		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		String home = System.getenv("HOME");
		dialog.setFilterPath(home);
		dialog.setFilterExtensions(new String[] { "*.xml" });
		dialog.setFilterNames(new String[] { "XML Files" });

		/* open dialog */
		final String fileSelected = dialog.open();
		if (fileSelected != null) 
		{
			Annotation annotation = DiademBenchmarkEngine.loadLayoutAnnotation(fileSelected);

			/* change model and notify listeners */
			BenchmarkModel model = Activator.modelControl.getModel();
			model.getCurrentDocument().getAnnotations().add(annotation);
			Activator.modelControl.modelChanged(model);
		}

		/* send selection to PDF editor */
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
		{
			BenchmarkModel model = Activator.modelControl.getModel();

			//set selection to annotation view
			IViewPart view = page.findView(BenchmarkNavigatorView.ID);
			if (view!=null)
			{
				BenchmarkNavigatorView annoView = (BenchmarkNavigatorView) view;
				annoView.setInput(model.getCurrentDocument());
			}
		}
		return null;
	}

}
