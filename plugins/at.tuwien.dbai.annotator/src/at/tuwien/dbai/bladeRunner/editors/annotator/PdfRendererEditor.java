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
package at.tuwien.dbai.bladeRunner.editors.annotator;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class PdfRendererEditor extends EditorPart {

	private PDFViewer2 viewer;

	Graphics gx;

	/**
	 * Constructor.
	 * 
	 * @param site
	 * @param input
	 */
	public PdfRendererEditor(IEditorSite site, IEditorInput input) {

		// try {
		super.setSite(site);

		// } catch (PartInitException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub

	}

	@Override
	public IWorkbenchPartSite getSite() {
		IWorkbenchPartSite site = super.getSite();
		if (site == null) {
			site = null;
		}
		return site;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NO_BACKGROUND
				| SWT.EMBEDDED);

		/*
		 * Set a Windows specific AWT property that prevents heavyweight
		 * components from erasing their background. Note that this is a global
		 * property and cannot be scoped. It might not be suitable for your
		 * application.
		 */
		try {
			System.setProperty("sun.awt.noerasebackground", "true");
		} catch (NoSuchMethodError error) {
		}

		/* Create and setting up frame */
		Frame frame = SWT_AWT.new_Frame(composite);
		Panel panel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				/* Do not erase the background */

				// g.drawRect(10, 10, 400, 200);

				paint(g);
			}
		};
		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		java.awt.Container contentPane = root.getContentPane();

		this.viewer = new PDFViewer2("test", "/home/max/test.pdf", 1);
		JPanel pdfViewer = viewer;
		JScrollPane scrollPane = new JScrollPane(pdfViewer);
		contentPane.setLayout(new BorderLayout());
		contentPane.add(scrollPane);

		gx = pdfViewer.getGraphics();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		return "Experimental";
	}

	public void setDocument(String uri) {
		viewer.setDocument(uri.substring(5), 1);
	}

}
