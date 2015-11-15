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
import java.awt.geom.Rectangle2D;
import java.io.File;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.control.IDocumentUpdateListener;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.editors.MultiCanvas;
import at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants;
import at.tuwien.dbai.bladeRunner.utils.DocWrapConstants;
import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.dbai.bladeRunner.utils.SWT2Dutil;
import at.tuwien.dbai.bladeRunner.views.IPlotMouseListener;
import at.tuwien.prip.common.datastructures.HashMap2;
import at.tuwien.prip.common.datastructures.Map2;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.hier.DocHierGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * @author max
 *
 */
public class AnnotatorEditor2 extends EditorPart
implements ISelectionProvider, IDocumentUpdateListener 
{
	/* the preference store */
	protected IPreferenceStore prefStore;

	private String inFile;

	private int currentPage = 1, maxPage = 1;

	public static PDFPage page = null;

	private MultiCanvas canvas = null;

	private IStructuredSelection activeSelection;

	private List<ISelectionChangedListener> listeners;

	private double zoomLevel = 1d;

	private PDFFile pdfFile;

	private AnnotatorEditor we;

	private Composite parent;

	private ISegmentGraph hGraph = null;
	private int documentRepresentation = DocWrapConstants.DOC_REP_HIER;

	private Map2<String, Integer, ISegmentGraph> page2hGraphMap;
	private Map2<String, Integer, Image> page2ImageMap;

	// flags
	private boolean isDragActivated = false;
	boolean fitWidth = true;
	boolean fitHeight = true;

	/**********************************************************
	 * GUI related fields
	 */

	/**********************************************************
	 * Buttons
	 */
	private Button toggleShowEdgesButton = null;
	private Button toggleShowPdfButton = null;
	private Button toggleShowGTButton = null;
	private Button toggleShowAnnotationsButton = null;
	private Button toggleSelectButton = null;
	private Button fitWidthButton = null;
	private Button zoomInButton = null;
	private Button zoomOutButton = null;
	private Button prevPageButton = null;
	private Button nextPageButton = null;
	private Button xOffMinusButton = null;
	private Button xOffPlusButton = null;
	private Button yOffMinusButton = null;
	private Button yOffPlusButton = null;
	private Button scaleButton = null;

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

	private Composite group1, topAdj, botAdj;
	private Group group2, adjGroup;

	private Label showPageNums, offsetLabel, xOffLabel, yOffLabel, denLabel,
			nomLabel, scaleLabel;
	private Text yOffText, xOffText, denText, nomText;

	boolean isAnno = false;
	boolean isWrapper = false;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param site
	 * @param input
	 */
	public AnnotatorEditor2(AnnotatorEditor we, IEditorSite site,
			IEditorInput input) {
		super.setSite(site);

		this.we = we;

		setPartName("Document View");

		if (input instanceof FileEditorInput) 
		{
			IFile file = ((FileEditorInput) input).getFile();
			String fileName = file.getName();
			if (fileName.endsWith(".pdf")) {
				// init(fileName);
			}
		}

		page2hGraphMap = new HashMap2<String, Integer, ISegmentGraph>(null,
				null, null);
		page2ImageMap = new HashMap2<String, Integer, Image>(null, null, null);

		listeners = new ArrayList<ISelectionChangedListener>();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) 
	{
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

		// create the canvas
		canvas = new MultiCanvas(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		canvas.setLayoutData(gridData);
		canvas.pack();
		canvas.addPlotMouseListener(new PlotMouseListener(canvas));

//		if (page != null)
//		{
//			getSite().getShell().getDisplay().asyncExec(new Runnable() {
//				public void run()
//				{
//					ImageData data = PDFUtils.getImageFromPDFPage(page,
//							zoomLevel);
//					 canvas.setImageData(data, true);
//				}
//			});
//		}
	}

	/**
	 * 
	 * @param uri
	 */
	public void setDocument(String uri) 
	{
		if (page != null) 
		{
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
	public void setDocument(Image image) 
	{
		canvas.setImage(image);
		canvas.update();
	}

	public void setMaxPage(int maxPage)
	{
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
	public void setSelection(ISelection selection) 
	{
		// inverse transform to rect
		Rectangle selRect = (Rectangle) ((IStructuredSelection) selection)
				.getFirstElement();
		AffineTransform transform = canvas.getTransform();
		Rectangle imageRect = SWT2Dutil.inverseTransformRect(transform, selRect);

		selection = new StructuredSelection(imageRect);
		SelectionChangedEvent sce = new SelectionChangedEvent(this, selection);
		if (selection != activeSelection)
		{
			for (ISelectionChangedListener listener : listeners) 
			{
				listener.selectionChanged(sce);
			}
			activeSelection = (IStructuredSelection) selection;
		}
	}

	/**
	 * 
	 * @param parent
	 */
	private Composite createControlPanel(Composite parent)
	{
		// load preference store
		this.prefStore = LearnUIPlugin.getDefault().getPreferenceStore();

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, true);
		data.widthHint = 160;
		layout.numColumns = 1;
		panel.setLayoutData(data);
		panel.setLayout(layout);

		// //////////////////////////////////////////////////////////////
		group1 = new Composite(panel, SWT.NONE);
		layout = new GridLayout(4, true);
		group1.setLayout(layout);
		group1.setEnabled(false);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 70;
		gridData.widthHint = 160;
		gridData.verticalAlignment = SWT.TOP;
		group1.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = 30;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.TOP;

		prevPageButton = new Button(group1, SWT.ARROW | SWT.LEFT);
		prevPageButton.setText("Previous");
		prevPageButton.setVisible(maxPage > currentPage);
		prevPageButton.setEnabled(currentPage > 1);
		prevPageButton.setLayoutData(gridData);
		prevPageButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				prevPage();
			}
		});

		showPageNums = new org.eclipse.swt.widgets.Label(group1, SWT.NONE);
		showPageNums.setEnabled(false);
		showPageNums.setVisible(false);
		showPageNums.setText((currentPage) + "/" + (maxPage));

		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		showPageNums.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = 30;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.TOP;

		nextPageButton = new Button(group1, SWT.ARROW | SWT.RIGHT);
		nextPageButton.setText("Next");
		nextPageButton.setVisible(maxPage > currentPage);
		nextPageButton.setEnabled(currentPage < maxPage);
		nextPageButton.setLayoutData(gridData);
		nextPageButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				nextPage();
			}

		});

		toggleSelectButton = new Button(group1, SWT.TOGGLE);
		toggleSelectButton.setSize(30, 30);
		toggleSelectButton.setEnabled(false);
		toggleSelectButton.setToolTipText("Toggle manual document placement");
		toggleSelectButton.setImage(getImageDescriptor("resize.png")
				.createImage());
		toggleSelectButton.setLayoutData(gridData);
		toggleSelectButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				toggleDragActivated();
			}
		});
		toggleSelectButton.pack();

		fitWidthButton = new Button(group1, SWT.PUSH);
		fitWidthButton.setSize(30, 30);
		fitWidthButton.setEnabled(false);
		fitWidthButton.setToolTipText("Fit document width");
		fitWidthButton.setImage(getImageDescriptor("but-doc-width.png")
				.createImage());
		fitWidthButton.setLayoutData(gridData);
		fitWidthButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				fitWidth();
			}
		});
		fitWidthButton.pack(true);

		zoomInButton = new Button(group1, SWT.PUSH);
		zoomInButton.setSize(30, 30);
		zoomInButton.setEnabled(false);
		zoomInButton.setToolTipText("Zoom in");
		zoomInButton.setText("+");
		zoomInButton.setLayoutData(gridData);
		zoomInButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.zoomIn2();
			}
		});
		zoomInButton.pack(true);

		zoomOutButton = new Button(group1, SWT.PUSH);
		zoomOutButton.setSize(30, 30);
		zoomOutButton.setEnabled(false);
		zoomOutButton.setToolTipText("Zoom out");
		zoomOutButton.setText("-");
		zoomOutButton.setLayoutData(gridData);
		zoomOutButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.zoomOut2();
			}
		});
		zoomOutButton.pack(true);

		group1.pack();
		// END GROUP 1
		// /////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////
		// GROUP2 (LAYERS)
		group2 = new Group(panel, SWT.SHADOW_ETCHED_IN);
		group2.setSize(130, 130);
		group2.setText("Layers");
		group2.setEnabled(false);

		layout = new GridLayout();
		layout.numColumns = 1;
		group2.setLayout(layout);

		gridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gridData.heightHint = 120;
		gridData.widthHint = 160;
		gridData.verticalIndent = 10;
		group2.setLayoutData(gridData);

		toggleShowEdgesButton = new Button(group2, SWT.CHECK);
		toggleShowEdgesButton.setText("Edges");
		toggleShowEdgesButton.setEnabled(false);
		toggleShowEdgesButton.setSelection(true);
		toggleShowEdgesButton.setToolTipText("Toggle the display of edges");
		toggleShowEdgesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// canvas.toggleEdges();
				// canvas.calibrateGraphCenter(true);
			}
		});

		toggleShowPdfButton = new Button(group2, SWT.CHECK);
		toggleShowPdfButton.setText("PDF");
		toggleShowPdfButton.setEnabled(false);
		toggleShowPdfButton.setSelection(false);
		toggleShowPdfButton.setToolTipText("Toggle the display of the PDF");
		toggleShowPdfButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// canvas.toggleNodes();
				// canvas.calibrateGraphCenter(true);
			}
		});

		toggleShowAnnotationsButton = new Button(group2, SWT.CHECK);
		toggleShowAnnotationsButton.setText("Annotations");
		toggleShowAnnotationsButton.setEnabled(false);
		toggleShowAnnotationsButton.setSelection(false);
		toggleShowAnnotationsButton
				.setToolTipText("Toggle the display of annotations");
		toggleShowAnnotationsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// canvas.toggleAnnotations();
				// canvas.calibrateGraphCenter(true);
			}
		});

		toggleShowGTButton = new Button(group2, SWT.CHECK);
		toggleShowGTButton.setText("GT");
		toggleShowGTButton.setEnabled(false);
		toggleShowGTButton.setSelection(false);
		toggleShowGTButton
				.setToolTipText("Toggle the display of the ground truth");
		toggleShowGTButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// canvas.toggleGT();
				// canvas.calibrateGraphCenter(true);
			}
		});

		group2.pack();
		// END GROUP 2
		// ///////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////
		/* begin new controls for PDF annotation */
		adjGroup = new Group(panel, SWT.SHADOW_ETCHED_IN);
		adjGroup.setText("GT Adjustment");
		adjGroup.setLayout(new RowLayout(SWT.VERTICAL));

		topAdj = new Composite(adjGroup, SWT.SHADOW_ETCHED_IN);
		layout = new GridLayout(2, false);
		topAdj.setLayout(layout);

		/* rescaling */
		scaleLabel = new Label(topAdj, SWT.NONE);
		scaleLabel.setText("Scale:");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.verticalIndent = 5;
		gridData.grabExcessHorizontalSpace = true;
		scaleLabel.setLayoutData(gridData);

		nomLabel = new Label(topAdj, SWT.NONE);
		gridData = new GridData();
		// gridData.widthHint = 25;
		// gridData.heightHint = 12;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.horizontalAlignment = SWT.BEGINNING;
		nomLabel.setLayoutData(gridData);
		nomLabel.setText("Nominator:");

		nomText = new Text(topAdj, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint = 17;
		gridData.heightHint = 12;
		// gridData.horizontalSpan = 3;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.horizontalAlignment = SWT.END;
		nomText.setLayoutData(gridData);
		nomText.setTextLimit(3);

		denLabel = new Label(topAdj, SWT.NONE);
		gridData = new GridData();
		// griButtondData.widthHint = 25;
		// gridData.heightHint = 12;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.horizontalAlignment = SWT.BEGINNING;
		denLabel.setLayoutData(gridData);
		denLabel.setText("Denominator:");

		denText = new Text(topAdj, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint = 17;
		gridData.heightHint = 12;
		// gridData.horizontalSpan = 3;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.horizontalAlignment = SWT.END;
		denText.setLayoutData(gridData);
		denText.setTextLimit(3);

		scaleButton = new Button(topAdj, SWT.PUSH);
		scaleButton.setText("apply");
		scaleButton.setToolTipText("Apply rescaling");
		scaleButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// rescale

			}
		});

		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = SWT.END;
		gridData.verticalIndent = 5;
		scaleButton.setLayoutData(gridData);

		/* offset */
		botAdj = new Composite(adjGroup, SWT.NONE);
		layout = new GridLayout(4, false);
		botAdj.setLayout(layout);

		offsetLabel = new Label(botAdj, SWT.NONE);
		offsetLabel.setText("Offset:");
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.verticalIndent = 5;
		offsetLabel.setLayoutData(gridData);

		xOffLabel = new Label(botAdj, SWT.NONE);
		xOffLabel.setText("X offset:");

		xOffMinusButton = new Button(botAdj, SWT.PUSH);
		xOffMinusButton.setText("-");
		gridData = new GridData();
		gridData.widthHint = 21;
		gridData.heightHint = 21;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		xOffMinusButton.setLayoutData(gridData);

		xOffText = new Text(botAdj, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint = 24;
		gridData.heightHint = 12;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		xOffText.setTabs(2);
		xOffText.setLayoutData(gridData);
		xOffText.setTextLimit(3);
		xOffText.setSize(30, 20);
		xOffText.setText("0");
		xOffText.setEditable(false);

		xOffMinusButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int xOff = Integer.parseInt(xOffText.getText());
				xOffText.setText(xOff - 1 + "");
			}
		});

		xOffPlusButton = new Button(botAdj, SWT.PUSH);
		xOffPlusButton.setText("+");
		gridData = new GridData();
		gridData.widthHint = 21;
		gridData.heightHint = 21;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		xOffPlusButton.setLayoutData(gridData);

		xOffPlusButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int xOff = Integer.parseInt(xOffText.getText());
				xOffText.setText(xOff + 1 + "");
			}
		});

		yOffLabel = new Label(botAdj, SWT.NONE);
		yOffLabel.setText("Y offset:");

		yOffMinusButton = new Button(botAdj, SWT.PUSH);
		yOffMinusButton.setText("-");
		gridData = new GridData();
		gridData.widthHint = 21;
		gridData.heightHint = 21;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		yOffMinusButton.setLayoutData(gridData);

		yOffText = new Text(botAdj, SWT.NONE);
		gridData = new GridData();
		gridData.widthHint = 24;
		gridData.heightHint = 12;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		yOffText.setLayoutData(gridData);
		yOffText.setTextLimit(3);
		yOffText.setText("0");
		yOffText.setEditable(false);

		yOffMinusButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int yOff = Integer.parseInt(yOffText.getText());
				yOffText.setText(yOff - 1 + "");
			}
		});

		yOffPlusButton = new Button(botAdj, SWT.PUSH);
		yOffPlusButton.setText("+");
		gridData = new GridData();
		gridData.widthHint = 21;
		gridData.heightHint = 21;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		yOffPlusButton.setLayoutData(gridData);
		yOffPlusButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int yOff = Integer.parseInt(yOffText.getText());
				yOffText.setText(yOff + 1 + "");
			}
		});
		// /////////////////////////////////////////////////////////////////

		panel.pack();
		return panel;
	}

	/**
	 * 
	 */
	private void enableAdjustmentControl(boolean enable) 
	{
		adjGroup.setEnabled(enable);
		denLabel.setEnabled(enable);
		denText.setEnabled(enable);
		nomLabel.setEnabled(enable);
		nomText.setEnabled(enable);
		scaleLabel.setEnabled(enable);
		offsetLabel.setEnabled(enable);
		xOffLabel.setEnabled(enable);
		yOffLabel.setEnabled(enable);
		xOffText.setEnabled(enable);
		yOffText.setEnabled(enable);
		xOffMinusButton.setEnabled(enable);
		xOffPlusButton.setEnabled(enable);
		yOffMinusButton.setEnabled(enable);
		yOffPlusButton.setEnabled(enable);
		scaleButton.setEnabled(enable);
		topAdj.setEnabled(enable);
		botAdj.setEnabled(enable);

		toggleShowGTButton.setEnabled(enable);
	}

	/**
	 * 
	 */
	private void updateActionEnablement() 
	{
		if (maxPage == 0)
		{
			this.maxPage = we.documentControl.getDocModel()
					.getNumPages();
		}

		IDocument document = Activator.modelControl.getModel().getCurrentDocument();
		if (document != null) 
		{
			if (group1 != null) {
				group1.setEnabled(true);
			}
			if (group2 != null) {
				group2.setEnabled(true);
			}
			if (document.getFormat().equals(DocumentFormat.PDF)) {
				if (showPageNums != null) {
					showPageNums.setVisible(true);
					showPageNums.setEnabled(true);
					parent.getDisplay().syncExec(new Runnable() {
						public void run() {
							showPageNums.setText((currentPage) + "/"
									+ (maxPage));
							showPageNums.redraw();
						}
					});
				}

				prevPageButton.setVisible(maxPage > 1);
				prevPageButton.setEnabled(currentPage > 1);
				nextPageButton.setVisible(maxPage > 1);
				nextPageButton.setEnabled(currentPage < maxPage);
			} else {
				showPageNums.setVisible(false);
				prevPageButton.setVisible(false);
				nextPageButton.setVisible(false);
			}

			if (fitWidthButton != null) {
				fitWidthButton.setEnabled(true);
			}
			if (toggleSelectButton != null) {
				toggleSelectButton.setEnabled(true);
			}
			if (zoomOutButton != null) {
				zoomOutButton.setEnabled(true);
			}
			if (zoomInButton != null) {
				zoomInButton.setEnabled(true);
			}
			if (nextPageAction != null) {
				nextPageAction.setEnabled(currentPage != maxPage);
			}
			if (prevPageAction != null) {
				prevPageAction.setEnabled(currentPage != 1);
			}

			if (toggleShowPdfButton != null) {
				toggleShowPdfButton.setEnabled(true);
			}
			if (toggleShowGTButton != null) {
				toggleShowGTButton.setEnabled(true);
			}
			if (toggleShowAnnotationsButton != null) {
				toggleShowAnnotationsButton.setEnabled(true);
			}
			if (toggleShowEdgesButton != null) {
				toggleShowEdgesButton.setEnabled(true);
			}
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
	public void documentUpdated(DocumentUpdateEvent ev)
	{
		// do whatever needs doing...
		DocumentUpdate update = ev.getDocumentUpdate();
		DocumentModel model = update.getUpdate();
		IDocument document = model.getDocument();

		// finally, notify canvas
		if (update.getProvider() != null
				&& !update.getProvider().equals(canvas))
		{
			// canvas.documentUpdated(ev);
		}

		if (ev.getDocumentUpdate().getType() == UpdateType.NEW_DOCUMENT) 
		{
			currentPage = model.getPageNum();
			maxPage = model.getNumPages();

			/* load PDF image */
			if (model.getPdfFile() != null) 
			{
				pdfFile = model.getPdfFile();
				page = pdfFile.getPage(model.getPageNum());
				pdfFile = model.getPdfFile();
				PDFPage page = model.getPdfFile().getPage(model.getPageNum());
//				ImageData imgData = PDFUtils.getImageFromPDFPage(page, 1);
//				canvas.setImageData(imgData, false);
			}
			if (model.getPageNum() != 0) {
				maxPage = model.getNumPages();
				currentPage = model.getPageNum();
			}

			DocumentGraph dg = (DocumentGraph) model.getDocumentGraph();
			if (dg == null) {
				if (!document.getUri().endsWith(".wrapper")) {
					processDocument(document);
				} else {
					canvas.setDocumentGraph(null);
				}
			} else {
				canvas.setDocumentGraph(dg);
			}

			if (canvas != null) {
				canvas.setDocumentGraph(hGraph);
				canvas.plot(true);
				canvas.calibrateGraphCenter(true);

				canvas.documentUpdated(ev);
			}
		} 
		else if (ev.getDocumentUpdate().getType() == UpdateType.PAGE_CHANGE) 
		{
			currentPage = model.getPageNum();
			maxPage = model.getNumPages();

			if (model.getPdfFile() != null) {
				page = model.getPdfFile().getPage(currentPage, true);
			} else {
				// load PDF page and image
				try {
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

			currentPage = model.getPageNum();
			processPdf(inFile, currentPage);

			if (canvas != null) {
				canvas.clearAllHighlights();
				canvas.setDocumentGraph(hGraph);
				canvas.plot(true);
				canvas.calibrateGraphCenter(true);
			}
		}

		updateActionEnablement();

		// lookup new document in benchmark model
		BenchmarkDocument currentDoc = Activator.modelControl.getModel()
				.getCurrentDocument();
		enableAdjustmentControl(currentDoc.getGroundTruth().size() > 0);
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
		MultiCanvas canvas;

		private Rectangle focusRectangle = null;

		private boolean dragOn = false;
		int mouseX = 0;
		int mouseY = 0;

		/**
		 * Constructor.
		 */
		public PlotMouseListener(MultiCanvas canvas) {
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

			getSite().getShell().getDisplay().asyncExec(new Runnable() 
			{
				public void run()
				{
					if (DocumentController.docModel.getFormat() == DocumentFormat.PDF)
					{
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

					// notify the listeners...
					ISelection selection = new StructuredSelection(
							canvas.selectionRectangle);
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

	public MultiCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Switch to next page, if available.
	 * 
	 */
	private void nextPage() {
		if (currentPage == maxPage)
			return;

		currentPage++;

		we.documentControl.nextPage();
	}

	/**
	 * Calibrate the graph's position to be centered.
	 * 
	 */
	public void calibrateGraphPosition(boolean fitWidth, boolean fitHeight,
			boolean remember) {
		// canvas.setxTrans(0);
		// canvas.setyTrans(0);
		//
		// canvas.clearContents();
		canvas.layout();
		canvas.redraw();

		// Dimension dims = getSize();
		//
		// int width = (int) (1 * dims.width);
		// int height = (int) (1 * dims.height);
		//
		// ISegmentGraph g = canvas.getDocumentGraph();
		// if (g==null) { return; }
		//
		// if (g instanceof SegLevelGraph)
		// {
		// g = ((SegLevelGraph) g).getBaseGraph();
		// }
		//
		// if (g instanceof DocumentGraph)
		// {
		// java.awt.Rectangle bounds = DocumentController.docModel.getBounds();
		//
		// // bounds.x *= DocumentController.docModel.getScale();
		// // bounds.y *= DocumentController.docModel.getScale();
		// // bounds.width *= DocumentController.docModel.getScale();
		// // bounds.height *= DocumentController.docModel.getScale();
		//
		// double scaleWidth = (new Double(0.9*width)) / (bounds.width) ;
		// double scaleHeight = (new Double (0.9* height)) / (bounds.height);
		//
		// int xTrans = 0;
		// int yTrans = 0;
		// double scale = 0d;
		//
		// if (fitHeight && fitWidth) {
		// scale = Math.min(scaleWidth, scaleHeight);
		// double midDocX = scale * (bounds.x + (bounds.width/2));
		// double midPageX = width/2;
		// double midDocY = scale * ( bounds.y + (bounds.height/2) );
		// double midPageY = height/2;
		// xTrans = (int) (midPageX - midDocX);
		// yTrans = (int) (midPageY - midDocY);
		// } else if (fitHeight) {
		// scale = scaleHeight;
		// double midDocX = scale * (bounds.x + (bounds.width/2));
		// double midPageX = width/2;
		// double midDocY = scale * ( bounds.y + (bounds.height/2) );
		// double midPageY = height/2;
		// xTrans = (int) (midPageX - midDocX);
		// yTrans = (int) Math.abs(midPageY - midDocY);
		// } else if (fitWidth) {
		// scale = scaleWidth;
		// double midDocX = scale * (bounds.x + (bounds.width/2));
		// double midPageX = width/2;
		// xTrans = (int) (midPageX - midDocX) ;
		// yTrans = (int) 40;//(midPageY - midDocY);
		// } else {
		// scale = Math.min(scaleWidth, scaleHeight);
		// double midDocX = scale * (bounds.x + (bounds.width/2));
		// double midPageX = width/2;
		// double midDocY = scale * ( bounds.y + (bounds.height/2) );
		// double midPageY = height/2;
		// xTrans = (int) (midPageX - midDocX);
		// yTrans = (int) (midPageY - midDocY);
		// }
		//
		// // /* notify document listeners */
		// // DocumentUpdate update = new DocumentUpdate();
		// // update.setType(UpdateType.RESIZE);
		// // DocumentModel model = new DocumentModel();
		// // model.setScale(scale);
		// // update.setUpdate(model);
		// // update.setProvider(canvas);
		// // we.setDocumentUpdated(update);
		//
		// canvas.drawActiveGraph(true);
		//
		// if (remember) {
		// canvas.translateContents2(xTrans, Math.max(30,yTrans));
		// } else {
		// canvas.translateContents(xTrans, Math.max(30,yTrans));
		// }
		//
		// }
		parent.layout();
	}

	/**
	 * Switch to previous page, if available.
	 * 
	 */
	private void prevPage() {
		if (currentPage == 1) {
			return;
		}

		currentPage--;

		we.documentControl.prevPage();
	}

	/**
	 * 
	 */
	private void fitWidth() {
		fitHeight = false;
		fitWidth = true;
		calibrateGraphPosition(fitWidth, fitHeight, true);
	}

	/**
	 * toggle drag activated true/false
	 */
	public void toggleDragActivated() {
		this.isDragActivated = !isDragActivated;
		if (isDragActivated) {
			Cursor c = new Cursor(getSite().getShell().getDisplay(),
					SWT.CURSOR_HAND);
			canvas.setCursor(c);
		} else {
			Cursor c = new Cursor(getSite().getShell().getDisplay(),
					SWT.CURSOR_CROSS);
			canvas.setCursor(c);
		}
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 */
	private ImageDescriptor getImageDescriptor(String relativePath) {
		String iconPath = "icons/full/docwrap/";
		try {

			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			URL installURL = bundle.getEntry("/");
			URL url = new URL(installURL, iconPath + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/**
	 * Process the document entry and draw to canvas.
	 * 
	 * @param de
	 */
	public void processDocument(IDocument document) {
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);

		// construct the hierarchical document graph
		String pref_rep = prefStore.getString(PreferenceConstants.PREF_DW_REP);
		if (pref_rep.equals(PreferenceConstants.PREF_DW_VALUE_REP_HIER)) {
			documentRepresentation = DocWrapConstants.DOC_REP_HIER;
		} else if (pref_rep.equals(PreferenceConstants.PREF_DW_VALUE_REP_LEVEL)) {
			documentRepresentation = DocWrapConstants.DOC_REP_SEG_LEVEL;
		}

		// set cursor to busy...
		DocWrapUIUtils.getWrapperEditor().showBusy(true);

		// PDF case
		if (document.getFormat().equals(DocumentFormat.PDF)) {
			processPdf(document.getUri(), 1);
		}
		// TIFF case
		else if (document.getFormat().equals(DocumentFormat.TIFF)) {
			URL url;
			try {
				url = new URI(document.getUri()).toURL();
				Image img = PDFUtils.loadTiff(url);
				// TODO: needs scaling

				/* update document model */
				DocumentModel model = new DocumentModel();
				model.setPageNum(currentPage);
				model.setNumPages(maxPage);
				model.setThumb(img);

				model.setFormat(DocumentFormat.TIFF);
				DocumentUpdate update = new DocumentUpdate();
				update.setUpdate(model);
				update.setType(UpdateType.NEW_DOCUMENT);
				update.setProvider(this);
				we.setDocumentUpdated(update);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		if (hGraph == null) {
			// unset busy cursor
			DocWrapUIUtils.getWrapperEditor().showBusy(false);
			return;
		}

		// horizontal view?
		if (hGraph != null
				&& hGraph.getDimensions().width > hGraph.getDimensions().height) {
			fitHeight = false;
			fitWidth = true;
		} else {
			fitHeight = true;
			fitWidth = false;
		}

		// save segmentation
		// DocumentSegmentation seg = segf.createDocumentSegmentation();
		// seg.setGraph(dg);
		// de.setSegmentation(seg);

		// List<String> words = DocumentUtils.extractWords(de);
		// TextUtils.removeStopWords(words);

		timer.stopTask(0);
		ErrorDump.debug(this,
				"Loaded document graph in " + timer.getTimeMillis(0) + "ms");

		// //do semantic annotation?
		// if (hGraph!=null && useSemantics &&
		// de.getFileType().equals(DocumentFormat.PDF_LITERAL)) {
		// timer.startTask(1);
		// SemanticsEngine.extractSemanticSegments(hGraph);
		// timer.stopTask(1);
		// ErrorDump.debug(this,
		// "Semantic annotation finished in "+timer.getTimeMillis(1)+"ms");
		// }

		if (canvas != null) {
			this.canvas.clearAllHighlights();
			this.canvas.setDocumentGraph(hGraph);

			canvas.plot(false);
		}

		// unset cursor from busy...
		DocWrapUIUtils.getWrapperEditor().showBusy(false);
		we.graph = hGraph;
		updateActionEnablement();
	}

	/**
	 * Process a PDF document (one page only).
	 * 
	 * @param inFile
	 * @param page
	 *            , the page number to process
	 */
	public void processPdf(String inFile, int page) {
		this.inFile = inFile;
		this.currentPage = page;

		IDocument document = DocumentController.docModel.getDocument();
		String pref_rep = prefStore.getString(PreferenceConstants.PREF_DW_REP);

		// setStatusLine("Opening file: "+inFile);

		try {
			URL url = null;
			if (inFile.startsWith("file:")) {
				url = new URL(inFile);
			} else {
				url = new File(inFile).toURI().toURL();
			}

			if (url == null) {
				return;
			}

			File file = new File(url.toURI());
			if (file.exists()) {
				File tmpDir = new File(System.getenv("HOME") + File.separator
						+ ".docwrap");
				if (!tmpDir.exists()) {
					tmpDir.mkdirs();
				}
				File dest = new File(tmpDir, "thumb.png");
				if (!dest.exists()) {
					dest.createNewFile();
				}

				ISegmentGraph cache = page2hGraphMap.get(inFile, currentPage);
				String cName = null;
				if (cache != null) {
					Class<?> c = cache.getClass();
					cName = c.getCanonicalName();
				}

				if (cName != null)// && cName.equals(pref_rep))
				{
					hGraph = page2hGraphMap.get(inFile, currentPage);
					maxPage = hGraph.getNumPages();
				} else {
					ISegmentGraph dg = null;
					if (dg == null) {
						ErrorDump.info(this, "Processing file: " + inFile);

						SimpleTimer timer = new SimpleTimer();
						timer.startTask(1);

						if (pref_rep
								.equals(PreferenceConstants.PREF_DW_VALUE_REP_FLAT)) {
							documentRepresentation = DocWrapConstants.DOC_REP_PLAIN;
						} else if (pref_rep
								.equals(PreferenceConstants.PREF_DW_VALUE_REP_HIER)) {
							documentRepresentation = DocWrapConstants.DOC_REP_HIER;
						} else if (pref_rep
								.equals(PreferenceConstants.PREF_DW_VALUE_REP_LEVEL)) {
							documentRepresentation = DocWrapConstants.DOC_REP_SEG_LEVEL;
						}

						this.documentRepresentation = DocWrapConstants.DOC_REP_PLAIN;

						switch (documentRepresentation) {
						case DocWrapConstants.DOC_REP_PLAIN:

							// this is the non-hierarchical case

							dg = DocumentGraphFactory.PDF
									.generateDocumentGraphNew(inFile, page);// ,
																			// useSemantics);

							maxPage = dg.getNumPages();
							page2hGraphMap.put(inFile, currentPage, dg);
							canvas.setDocumentGraph(dg);
							hGraph = dg;
							break;
						}// end switch

						timer.stopTask(1);
						ErrorDump.info(
								this,
								"Processed hierarchy in "
										+ timer.getTimeMillis(1) / 1000
										+ " seconds");
					}
				}

				// remember page count
				if (hGraph instanceof DocHierGraph) {
					this.maxPage = ((DocHierGraph) hGraph).getNumPages();
				}

				/* load PDF file */
				RandomAccessFile raf = new RandomAccessFile(file, "r");// new
																		// File
																		// (fileName.substring(5)),
																		// "r");
				FileChannel fc = raf.getChannel();
				ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
						fc.size());
				PDFFile pdfFile = new PDFFile(buf);

				/* update document model */
				if (document instanceof PdfBenchmarkDocument) {
					PdfBenchmarkDocument benchDoc = (PdfBenchmarkDocument) document;
					Rectangle2D bounds = pdfFile.getPage(0).getBBox();
					java.awt.Rectangle dims = hGraph.getDimensions();
					benchDoc.setBounds(bounds.getBounds());
					benchDoc.setPdfFile(pdfFile);
					benchDoc.setNumPages(maxPage);
				}

//				DocumentModel model = new DocumentModel();
//				model.setBounds(document.getBounds());
//				model.setPdfFile(pdfFile);
//				model.setDocument(document);
//				model.setPageNum(currentPage);
//				model.setNumPages(maxPage);
//				// model.setThumb(img);
//				model.setDocumentGraph(hGraph);
//				model.setFormat(DocumentFormat.PDF);
//				DocumentUpdate update = new DocumentUpdate();
//				update.setUpdate(model);
//				update.setType(UpdateType.DOCUMENT_CHANGE);
//				update.setProvider(this);
//				we.setDocumentUpdated(update);
			} 
			else 
			{
				Shell parent = getSite().getShell();
				MessageDialog dialog = new MessageDialog(
						parent,
						"File not found",
						null,
						"The file "
								+ inFile
								+ " could not be found. Please make sure that the path is correct.",
						MessageDialog.ERROR, new String[] { "Close" }, 1);
				dialog.open();

			}

			// setStatusLine("Ready");

			we.graph = hGraph;

		} catch (Exception e) {
			e.printStackTrace();
			Shell parent = getSite().getShell();
			MessageDialog dialog = new MessageDialog(parent,
					"Processing Error", null, e.getMessage(),
					MessageDialog.ERROR, new String[] { "Close" }, 1);
			dialog.open();

		}
	}
}
