package at.tuwien.dbai.bladeRunner.utils;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HoverExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		GridLayout gridLayout = new GridLayout(2, true);
		shell.setLayout(gridLayout);

//		Label label1 = new Label(shell, SWT.NONE);
//		label1.setText("First Name");
//		Text text1 = new Text(shell, SWT.BORDER);
//		Label label2 = new Label(shell, SWT.NONE);
//		label2.setText("Last Name");
//		Text text2 = new Text(shell, SWT.BORDER);
		shell.pack();

		final HoverShell hShell = new HoverShell(shell, "test");
//		text1.addListener(SWT.MouseHover, new Listener(){
//
//			@Override
//			public void handleEvent(Event event) {
//				hShell.label.setText("Enter First Name");
//				hShell.hoverShell.pack();
//				hShell.hoverShell.open();
//			}
//		});
//
//		text1.addListener(SWT.MouseExit, new Listener(){
//
//			@Override
//			public void handleEvent(Event event) {
//				hShell.hoverShell.setVisible(false);
//			}
//		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}

class HoverShell
{
	Shell hoverShell;
	Label label;
	
	public HoverShell(Shell shell, String message)
	{
		shell.setSize(500, 300);
		shell.setText("Help");
		shell.setLayout(new GridLayout());
		GridData data = new GridData();
		data.horizontalIndent = 30;
		data.verticalIndent = 30;
		data.horizontalAlignment = SWT.CENTER;
		data.verticalAlignment = SWT.CENTER;
		shell.setLayoutData(data);
		label = new Label(shell, SWT.NONE);
		label.setText(message);
	
		hoverShell = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
		hoverShell.setLayout(new FillLayout());
		hoverShell.setSize(400, 300);
		hoverShell.pack();
	}
}
