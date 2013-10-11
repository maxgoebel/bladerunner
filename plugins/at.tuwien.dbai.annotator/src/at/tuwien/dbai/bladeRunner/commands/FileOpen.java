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

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.editors.annotator.ProcessDocumentJob;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;

/**
 * FileOpen.java
 * 
 * A simple open file command
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 3, 2011
 */
public class FileOpen extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		String home = System.getenv("HOME");
		dialog.setFilterPath(home);
		dialog.setFilterExtensions(new String[] { "*.html", "*.pdf" });
		dialog.setFilterNames(new String[] { "HTML Files", "PDF Files" });

		/* open dialog */
		final String fileSelected = dialog.open();
		if (fileSelected != null) {
			final ProcessDocumentJob pdj = new ProcessDocumentJob(
					"Loading Document " + fileSelected, fileSelected, 0);
			pdj.addJobChangeListener(new IJobChangeListener() {
				@Override
				public void sleeping(IJobChangeEvent event) {

				}

				@Override
				public void scheduled(IJobChangeEvent event) {

				}

				@Override
				public void running(IJobChangeEvent event) {

				}

				@Override
				public void done(IJobChangeEvent event) 
				{
					BenchmarkDocument document = null;
				
					if (fileSelected.endsWith(".pdf"))
					{
						document = new PdfBenchmarkDocument();
					}
					else if (fileSelected.endsWith(".html"))
					{
						document = new HTMLBenchmarkDocument();
					}
					if (document==null)
					{
						return;
					}
					document.setFileName(fileSelected);
					document.setUri(fileSelected);

					//check for result file:
					String resultFile = fileSelected.substring(0,fileSelected.lastIndexOf("."));
					resultFile += "-reg-result.xml";
					if (new File(resultFile).exists())
					{
						Annotation ann = DiademBenchmarkEngine.loadLayoutAnnotation(resultFile);
						document.getAnnotations().add(ann);
					}
					
					//check for GT:
					String gtRegFileName = fileSelected.substring(0,fileSelected.lastIndexOf("."));
					gtRegFileName += "-reg.xml";
					File gtRegFile = new File(gtRegFileName);
					if (gtRegFile.exists())
					{
						Annotation ann = DiademBenchmarkEngine.parseBladeFile(gtRegFile);
						document.getGroundTruth().add(ann);
					}
					
					BenchmarkModel model = Activator.modelControl.getModel();
					if (model == null) {
						model = new BenchmarkModel();
					}

					/* add to benchmark */
					Benchmark benchmark = null;
					if (model.getBenchmarks() == null
							|| model.getBenchmarks().size() == 0) {
						benchmark = new Benchmark(""
								+ System.currentTimeMillis());
						benchmark.getDocuments().add(document);
						model.getBenchmarks().add(benchmark);
					} else {
						benchmark = model.getBenchmarks().get(0);
						benchmark.getDocuments().add(document);
					}

					model.setCurrentBenchmark(benchmark);
					model.setCurrentDocument(document);

					Activator.modelControl.modelChanged(model);
				}

				@Override
				public void awake(IJobChangeEvent event) {

				}

				@Override
				public void aboutToRun(IJobChangeEvent event) {

				}
			});
			pdj.schedule();
		}
		return null;
	}
}
