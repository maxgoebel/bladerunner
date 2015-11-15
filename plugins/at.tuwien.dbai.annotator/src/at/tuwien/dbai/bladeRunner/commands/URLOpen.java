package at.tuwien.dbai.bladeRunner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.utils.AddURLDialog;
import at.tuwien.dbai.bladeRunner.utils.ProcessDocumentJob;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;

public class URLOpen extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		AddURLDialog dialog = new AddURLDialog(shell, "");
		String home = System.getenv("HOME");
//		dialog.setFilterPath(home);
//		dialog.setFilterExtensions(new String[] { "*.html", "*.pdf" });
//		dialog.setFilterNames(new String[] { "HTML Files", "PDF Files" });

		/* open dialog */
		if (dialog.open()==0)
		{
			final String url = dialog.getURL();
		
			final ProcessDocumentJob pdj = new ProcessDocumentJob(
					"Loading Document " + url, url, 0);
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
					BenchmarkDocument document = new HTMLBenchmarkDocument();
					document.setFileName(url);
					document.setUri(url);

//					//check for result file:
//					String resultFile = fileSelected.substring(0,fileSelected.lastIndexOf("."));
//					resultFile += "-reg-result.xml";
//					if (new File(resultFile).exists())
//					{
//						Annotation ann = DiademBenchmarkEngine.loadLayoutAnnotation(resultFile);
//						document.getAnnotations().add(ann);
//					}
//					
//					//check for GT:
//					String gtRegFileName = fileSelected.substring(0,fileSelected.lastIndexOf("."));
//					gtRegFileName += "-reg.xml";
//					File gtRegFile = new File(gtRegFileName);
//					if (gtRegFile.exists())
//					{
//						Annotation ann = DiademBenchmarkEngine.parseBladeFile(gtRegFile);
//						document.getGroundTruth().add(ann);
//					}
					
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
