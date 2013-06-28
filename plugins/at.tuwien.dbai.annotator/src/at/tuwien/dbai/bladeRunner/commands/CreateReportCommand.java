package at.tuwien.dbai.bladeRunner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import at.tuwien.dbai.bladeRunner.utils.ReportDialog;

public class CreateReportCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event)
	throws ExecutionException 
	{
		ReportDialog dialog = new ReportDialog(Display.getDefault().getActiveShell());
		dialog.open();
//		FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(),
//				SWT.SAVE);
//		String home = System.getenv("HOME");
//		dialog.setFilterPath(home);
//
//		
//		/* open dialog */
//		String fileSelected = dialog.open();
//		if (fileSelected != null) 
//		{
//			BenchmarkModel model = Activator.modelControl.getModel();
//			Benchmark benchmark = model.getBenchmarks().get(0);
//			//Activator.modelControl.getModel().getCurrentBenchmark();
//			DiademBenchmarkEngine.writeReport(benchmark, fileSelected);
//		}

		return null;
	}

}
