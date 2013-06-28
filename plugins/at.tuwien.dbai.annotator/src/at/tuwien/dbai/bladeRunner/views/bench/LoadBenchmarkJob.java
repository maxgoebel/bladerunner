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
package at.tuwien.dbai.bladeRunner.views.bench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;

/**
 * 
 * @author max
 * 
 */
public class LoadBenchmarkJob extends Job {

	private Benchmark benchmark;

	private String fileSelected;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public LoadBenchmarkJob(String name, String fileSelected) {
		super(name);
		this.fileSelected = fileSelected;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) 
	{
		if (fileSelected != null)
		{
			// load the benchmark
			Benchmark benchmark = DiademBenchmarkEngine
					.loadBenchmark(fileSelected);

			if (benchmark.getDocuments().size()==0 && DiademBenchmarkEngine.error!=null)
			{
				if (Display.getDefault()!=null && Display.getDefault().getActiveShell()!=null)
				{
					MessageDialog.openError(
							Display.getDefault().getActiveShell(), 
							"Error during document load", DiademBenchmarkEngine.error);
				}
			}
			BenchmarkModel model = convert2BenchmarkModel(benchmark);
			Activator.modelControl.modelChanged(model);

		}
		return Status.OK_STATUS;
	}

	/**
	 * 
	 * @param bench
	 * @return
	 */
	public BenchmarkModel convert2BenchmarkModel(Benchmark bench) {
		BenchmarkModel result = new BenchmarkModel();
		result.getBenchmarks().add(bench);
		return result;
	}

	public Benchmark getBenchmark() {
		return benchmark;
	}

}
