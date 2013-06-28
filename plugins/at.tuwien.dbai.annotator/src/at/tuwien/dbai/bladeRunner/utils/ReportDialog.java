package at.tuwien.dbai.bladeRunner.utils;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;

public class ReportDialog extends Dialog {

	private Button cancelButton;
	private Button saveButton;
	private Text textField;

	private final Shell shell;
	
	public ReportDialog(Shell parent) {
		super(parent);
		
		shell = new Shell(getParent());
	}

	public void open() 
	{
		shell.setText("Benchmark Report");
		shell.setMinimumSize(500, 600);
		shell.setSize(500, 700);
		if (draw(shell)) // Contents of Dialog
		{
			shell.pack();
			shell.open();

			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
	}

	private boolean draw(Composite parent) {

		parent.setLayout(new RowLayout(SWT.VERTICAL));
		parent.setSize(600, 500);
		BenchmarkModel model = Activator.modelControl.getModel();
		if (model==null) 
		{
			MessageDialog.openError(parent.getShell(),
					"Error", 
					"Please load a benchmark first");
			return false;
		}
		Benchmark benchmark = model.getBenchmarks().get(0);
		if (benchmark==null) 
		{
			MessageDialog.openError(parent.getShell(),
					"Error", 
					"Please load a benchmark first");
			return false;
		}

		String reportText = DiademBenchmarkEngine.createReportString (benchmark);
		RowData data = new RowData();
		data.height = 600;
		textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textField.setText(reportText);
		textField.setEnabled(true);
		textField.setEditable(false);
		textField.setLayoutData(data);
		textField.setSize(600, 400);

		Composite buttonGroup = new Composite(parent, SWT.NONE);
		buttonGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		cancelButton = new Button(buttonGroup, SWT.PUSH);
		cancelButton.setEnabled(true);
		cancelButton.setText("&Cancel");
		cancelButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				shell.close();
			}
		});

		saveButton = new Button(buttonGroup, SWT.PUSH);
		saveButton.setEnabled(true);
		saveButton.setText("&Save");
		saveButton.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event) 
			{
				FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(),
						SWT.SAVE);
				String home = System.getenv("HOME");
				dialog.setFilterPath(home);


				/* open dialog */
				String fileSelected = dialog.open();
				if (fileSelected != null) 
				{
					BenchmarkModel model = Activator.modelControl.getModel();
					Benchmark benchmark = model.getBenchmarks().get(0);
					//Activator.modelControl.getModel().getCurrentBenchmark();
					DiademBenchmarkEngine.writeReport(benchmark, fileSelected);
				}
				shell.close();
			}
		});
		return true;
	}

}
