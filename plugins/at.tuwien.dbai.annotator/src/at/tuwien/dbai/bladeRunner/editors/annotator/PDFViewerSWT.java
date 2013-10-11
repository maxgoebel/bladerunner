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

import java.awt.geom.AffineTransform;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.control.IDocumentUpdateListener;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.dbai.bladeRunner.views.IPlotMouseListener;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;
import at.tuwien.prip.model.utils.DocGraphUtils;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * 
 * PDFViewerSWT.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Oct 1, 2011
 */
public class PDFViewerSWT extends EditorPart
implements ISelectionProvider,
IDocumentUpdateListener,
ISelectionChangedListener
{
	private int currentPage = 1, maxPage = 1;

	public static PDFPage page = null;

	private SWTImageCanvas canvas = null;

	private IStructuredSelection activeSelection;

	private List<ISelectionChangedListener> listeners;

	private double zoomLevel = 0.8d;

	private PDFFile pdfFile;

	private AnnotatorEditor we;

	private Composite parent;

	/* buttons */
	private Button prevPageButton = null;
	private Button nextPageButton = null;

	/* agents */
	// private Button runSemanticAgentButton = null;
	// private Button runLayoutAgentButton = null;
	// private Button runAllAgentButton = null;

	/**********************************************************
	 * Actions
	 */
	Action showDocumentAction;
	Action toggleEdgesAction;
	Action toggleNodesAction;
	Action selectAllAction;
	Action nextPageAction;
	Action prevPageAction;
	Action resizeAction;
	org.eclipse.swt.widgets.Label showPageNums;

	private Composite group1;
	// private Group group4;

	boolean isAnno = false;
	boolean isWrapper = false;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param site
	 * @param input
	 */
	public PDFViewerSWT(IEditorSite site, IEditorInput input) {
		super.setSite(site);
		setPartName("Document View");

		if (input instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) input).getFile();
			String fileName = file.getName();
			if (fileName.endsWith(".pdf")) {
				// init(fileName);
			}
		}

		listeners = new ArrayList<ISelectionChangedListener>();
	}

	@Override
	public void dispose() {
		super.dispose();
		// we.deregisterForSelection(this);
		we.deregisterFromDocumentUpdate(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		setPartName("Document View");

		/* BEGIN CONTENT */
		GridLayout layout = new GridLayout();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 160;
		gridData.heightHint = 680;
		layout.numColumns = 2;

		layout.makeColumnsEqualWidth = false;
		parent.setLayout(layout);

		// add a control panel
		Composite controlPanel = createControlPanel(parent);
		controlPanel.setLayoutData(gridData);
		controlPanel.pack();

		// add the PDF canvas
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 900;
		gridData.heightHint = 680;

		canvas = new SWTImageCanvas(parent, ColorConstants.darkGray, SWT.NO_BACKGROUND
				| SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);

		canvas.setLayoutData(gridData);
		canvas.pack();
		canvas.addPlotMouseListener(new PlotMouseListener(canvas));

		if (page != null) {
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ImageData data = PDFUtils.getImageFromPDFPage(page,
							zoomLevel);
					canvas.setImageData(data, true);
				}
			});
		}
	}

	/**
	 * 
	 * @param uri
	 */
	public void setDocument(String uri) {
		if (page != null) {
			zoomLevel = 0.6;
			canvas.setImageData(PDFUtils.getImageFromPDFPage(page, zoomLevel),
					false);
		}

		canvas.update();
	}

	/**
	 * 
	 * @param image
	 */
	public void setDocument(Image image) {
		canvas.setImage(image);
		canvas.update();
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
		updateActionEnablement();
	}

	public void setWrapperEditor(AnnotatorEditor we) {
		this.we = we;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return activeSelection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		// inverse transform to rect
		Rectangle selRect = (Rectangle) ((IStructuredSelection) selection)
				.getFirstElement();
		AffineTransform transform = canvas.getTransform();
		Rectangle imageRect = SWT2Dutil
				.inverseTransformRect(transform, selRect);

		selection = new StructuredSelection(imageRect);
		SelectionChangedEvent sce = new SelectionChangedEvent(this, selection);
		if (selection != activeSelection)
		{
			for (ISelectionChangedListener listener : listeners) {
				listener.selectionChanged(sce);
			}
			activeSelection = (IStructuredSelection) selection;
		}
	}

	/**
	 * 
	 * @param parent
	 */
	private Composite createControlPanel(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData rdata = new GridData(SWT.CENTER, SWT.TOP, true, true);
		panel.setLayoutData(rdata);
		panel.setLayout(layout);

		// //////////////////////////////////////////////////////////////
		group1 = new Composite(panel, SWT.NONE);
		GridLayout glayout = new GridLayout(4, true);
		group1.setLayout(glayout);
		group1.setEnabled(false);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 40;
		gridData.widthHint = 160;
		group1.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = 30;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.CENTER;

		prevPageButton = new Button(group1, SWT.ARROW | SWT.LEFT);
		prevPageButton.setText("Previous");
//		prevPageButton.setVisible(maxPage > currentPage);
		prevPageButton.setEnabled(currentPage > 1);
		prevPageButton.setLayoutData(gridData);
		prevPageButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				we.documentControl.prevPage();
			}
		});

		showPageNums = new org.eclipse.swt.widgets.Label(group1, SWT.NONE);
		showPageNums.setEnabled(false);
//		showPageNums.setVisible(false);
		showPageNums.setText((currentPage) + "/" + (maxPage));

		GridData data2 = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		data2.horizontalSpan = 2;
		data2.widthHint = 40;
		data2.horizontalAlignment = SWT.CENTER;
		showPageNums.setLayoutData(data2);

		nextPageButton = new Button(group1, SWT.ARROW | SWT.RIGHT);
		nextPageButton.setText("Next");
//		nextPageButton.setVisible(maxPage > currentPage);
		nextPageButton.setEnabled(currentPage < maxPage);
		nextPageButton.setLayoutData(gridData);
		nextPageButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				we.documentControl.nextPage();
			}
		});
		group1.pack();

		updateActionEnablement();

		panel.pack();
		return panel;
	}

	/**
	 * 
	 */
	private void updateActionEnablement() {
		if (group1 != null) {
			group1.setEnabled(true);
		}

		if (showPageNums != null) {
//			showPageNums.setVisible(true);
			showPageNums.setEnabled(true);
			parent.getDisplay().syncExec(new Runnable() {
				public void run() {
					showPageNums.setText((currentPage) + "/" + (maxPage));
					showPageNums.redraw();
				}
			});
		}

//		prevPageButton.setVisible(maxPage > 1);
		prevPageButton.setEnabled(currentPage > 1);
//		nextPageButton.setVisible(maxPage > 1);
		nextPageButton.setEnabled(currentPage < maxPage);

		if (nextPageAction != null) {
			nextPageAction.setEnabled(currentPage != maxPage);
		}
		if (prevPageAction != null) {
			prevPageAction.setEnabled(currentPage != 1);
		}

		parent.redraw();
		parent.update();
	}

	/****************************************************************
	 * 
	 * Delegate editor functionality to parent...
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// parent.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setPartName("Document View");
	}

	@Override
	public boolean isDirty() {
		// return parent.isDirty();
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// return parent.isSaveAsAllowed();
		return false;
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

	public double getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * Deal with document update events.
	 */
	@Override
	public void documentUpdated(DocumentUpdateEvent ev) {
		// do whatever needs doing...
		DocumentUpdate update = ev.getDocumentUpdate();
		DocumentModel model = update.getUpdate();

		// finally, notify canvas
		if (update.getProvider() != null
				&& !update.getProvider().equals(canvas)) {
			// canvas.documentUpdated(ev);
		}

		if (ev.getDocumentUpdate().getType() == UpdateType.NEW_DOCUMENT) 
		{
			currentPage = model.getPageNum();
			maxPage = model.getNumPages();
			if (model.getPdfFile() != null) {
				pdfFile = model.getPdfFile();
				page = pdfFile.getPage(model.getPageNum());
				pdfFile = model.getPdfFile();
//				PDFPage page = model.getPdfFile().getPage(model.getPageNum());
//				ImageData imgData = PDFUtils.getImageFromPDFPage(page, zoomLevel);
//				canvas.setImageData(imgData, false);
			}
			if (model.getPageNum() != 0) {
				maxPage = model.getNumPages();
				currentPage = model.getPageNum();
			}
		}
		else if (ev.getDocumentUpdate().getType() == UpdateType.PAGE_CHANGE) 
		{
			currentPage = model.getPageNum();
			maxPage = model.getNumPages();

			if (model.getPdfFile() != null) {
				try
				{
					page = model.getPdfFile().getPage(currentPage, true);
				}
				catch (Exception e) {
	
				}
			}
			else 
			{
				// load PDF page and image
				// PDFPage page = null;
				try 
				{
					if (pdfFile == null) {
						pdfFile = we.documentControl.getDocModel()
								.getPdfFile();
						RandomAccessFile raf = new RandomAccessFile(
								we.documentControl.getDocModel()
										.getUri(), "r");
						FileChannel fc = raf.getChannel();
						ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY,
								0, fc.size());
						pdfFile = new PDFFile(buf);
					}
					page = pdfFile.getPage(currentPage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (page!=null)
			{
				getSite().getShell().getDisplay().asyncExec(
						new Runnable()
						{
							public void run()
							{
								ImageData data = PDFUtils.getImageFromPDFPage(page,zoomLevel);
								canvas.setImageData(data, false);
							}
						}
				);
			}
		}
		updateActionEnablement();
	}

	/**
	 * 
	 * A custom mouse and paint Listener for the plotter.
	 */
	public class PlotMouseListener implements MouseListener, MouseMoveListener,
			MouseWheelListener, PaintListener, IPlotMouseListener {
		IFigure lastFig = null;
		Color lastFgColor;
		Color lastBgColor;
		Label lastLabel;
		SWTImageCanvas canvas;

		private Rectangle focusRectangle = null;

		private boolean dragOn = false;
		int mouseX = 0;
		int mouseY = 0;

		/**
		 * Constructor.
		 */
		public PlotMouseListener(SWTImageCanvas canvas) {
			this.canvas = canvas;
		}

		@Override
		public void mouseMove(MouseEvent e) {
			if (dragOn) {
				focusRectangle.width = (-1) * (focusRectangle.x - e.x);
				focusRectangle.height = (-1) * (focusRectangle.y - e.y);
				mouseX = e.x;
				mouseY = e.y;
				canvas.redraw();
			}
		}

		@Override
		public void mouseScrolled(MouseEvent e) {
			canvas.setBackground(ColorConstants.darkGray);
			if (e.count >= 0) {
				zoomLevel = DocumentController.docModel.getScale()
						+ DocumentController.docModel.getScale() / 9;
			} else {
				zoomLevel = DocumentController.docModel.getScale()
						- DocumentController.docModel.getScale() / 9;
			}

			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (DocumentController.docModel.getFormat() == DocumentFormat.PDF) {
						ImageData data = PDFUtils.getImageFromPDFPage(page,
								zoomLevel);
						canvas.setImageData(data, true);
					} else if ((DocumentController.docModel.getFormat() == DocumentFormat.TIFF)) {
						try {
							URL url = new URI(DocumentController.docModel
									.getUri()).toURL();
							Image img = PDFUtils.loadTiff(url);
							canvas.setImage(img);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			});

			canvas.redraw();

			/* notify document listeners */
			DocumentModel model = new DocumentModel();
			model.setScale(zoomLevel);

			DocumentUpdate update = new DocumentUpdate();
			update.setType(UpdateType.RESIZE);
			update.setUpdate(model);
			update.setProvider(canvas);
			we.setDocumentUpdated(update);
		}

		@Override
		public void mouseDown(MouseEvent e) {
			canvas.selectionRectangle = null;
			if (e.button == 1) {
				mouseX = e.x;
				mouseY = e.y;
				focusRectangle = new Rectangle(mouseX, mouseY, 0, 0);
				dragOn = true;
			}

			// the trick is to remember the original
			// document transform here as calibration...
			canvas.dtx = canvas.transform.getTranslateX();
			canvas.dty = canvas.transform.getTranslateY();

			canvas.selTransform = new AffineTransform();
			canvas.redraw();
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (e.button == 1) {
				dragOn = false;

				if (focusRectangle != null && focusRectangle.width > 0
						&& focusRectangle.height > 0) {
					canvas.selectionRectangle = focusRectangle;

					java.awt.Rectangle r = new java.awt.Rectangle(
							focusRectangle.x, 
							focusRectangle.y, 
							focusRectangle.width, 
							focusRectangle.height);
					
//					double scale = we.documentControl.getDocModel().getScale();
					int pageNum = we.documentControl.getDocModel().getPageNum();
					PdfBenchmarkDocument document = 
						(PdfBenchmarkDocument) we.documentControl.getDocModel().getDocument();
					PdfDocumentPage page = document.getPage(pageNum);
					r = DocGraphUtils.yFlipRectangle(r, page.getGraph());
					
					Rectangle selRect = new Rectangle(
							(int)(r.x ), 
							(int)(r.y ), 
							(int)(r.width), 
							(int)(r.height));
							
					// notify the listeners...
					ISelection selection = new StructuredSelection(selRect);
					setSelection(selection);
					focusRectangle = null;
				}
			}
			canvas.redraw();
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) { /* not implemented */
		}

		public void paintControl(PaintEvent e) {
			if (focusRectangle != null && focusRectangle.width > 0
					&& focusRectangle.height > 0) {
				// draw focus
				e.gc.setForeground(ColorConstants.black);
				e.gc.drawFocus(focusRectangle.x, focusRectangle.y,
						focusRectangle.width, focusRectangle.height);
			} else {
				if (canvas.selectionRectangle != null
						&& canvas.selectionRectangle.width > 0
						&& canvas.selectionRectangle.height > 0) {
					focusRectangle = null;
				}
			}
		}

		public Rectangle getFocusRectangle() {
			return focusRectangle;
		}

	}

	public SWTImageCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

}// PDFViewerSWT
