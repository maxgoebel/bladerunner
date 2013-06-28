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

import java.awt.Rectangle;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.control.IDocumentUpdateListener;
import at.tuwien.dbai.bladeRunner.control.IModelChangedListener;
import at.tuwien.dbai.bladeRunner.control.ModelChangedEvent;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants;
import at.tuwien.dbai.bladeRunner.utils.BrowserMonitor;
import at.tuwien.dbai.bladeRunner.utils.DocWrapConstants;
import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.dbai.bladeRunner.views.DocumentGraphCanvas;
import at.tuwien.dbai.bladeRunner.views.IPlotMouseListener;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;
import at.tuwien.prip.common.datastructures.Pair;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.RectangleAdjustment;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.DocumentMatrix;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.base.IGraph;
import at.tuwien.prip.model.graph.hier.DocHierGraph;
import at.tuwien.prip.model.graph.hier.ISegHierGraph;
import at.tuwien.prip.model.graph.hier.level.IGraphLevel;
import at.tuwien.prip.model.graph.hier.level.SegLevelGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;
import at.tuwien.prip.model.utils.DocGraphUtils;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * DocWrapEditor.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 15, 2011
 */
public class DocWrapEditor extends EditorPart implements Observer,
ISelectionProvider, ISelectionChangedListener, 
IDocumentUpdateListener,
IModelChangedListener 
{

	public static final String ID = "at.tuwien.prip.docwrap.annotator.docEditor";

	private String inFile;

	private Composite parent;

	private IMemento memento;

	/* the preference store */
	protected IPreferenceStore prefStore;

	private boolean isDragActivated = false;
	private boolean isEvaluateMode = false;

	private ISegmentGraph hGraph = null;

	private int activeLevel = 1;

	public DocumentGraphCanvas canvas;

	private BrowserMonitor monitor;

	private AnnotatorEditor we;

	private ResizeListener sizeListener = null;

	// 0 for line level doc (non-hierarchical)
	// 1 for segLevelStack (level-hierarchical)
	// 2 for hierarchical document (freely hierarchical)
	private int documentRepresentation = DocWrapConstants.DOC_REP_HIER;

	private Map<Integer, ISegmentGraph> page2hGraphMap;
	private Map<Integer, Image> page2ImageMap;

	private ISegmentGraph graphSelection;

	private ISelection activeSelection;
	private List<ISelectionChangedListener> listeners;

	private int currentPage = 1;
	private int maxPage = currentPage;
	
	private RectangleAdjustment adj = null;

	// private IGraphMatcher matcher;
	// private IGraphDistance distance;

	private boolean useSemantics = true;

	boolean fitWidth = true;
	boolean fitHeight = true;

	/* a scale for the zoom level */
	private Scale zoomScale = null;

	/**********************************************************
	 * GUI related fields
	 */

	/**********************************************************
	 * Buttons
	 */
	private Button toggleFilterAllButton = null;
	private Button toggleFilterCombo1Button = null;
	private Button toggleFilterCombo2Button = null;
	private Button toggleShowNodesButton = null;
	private Button toggleShowEdgesButton = null;
	private Button toggleShowPdfButton = null;
	private Button toggleShowGTButton = null;
	private Button toggleShowAnnotationsButton = null;
	private Button toggleShowDocMatrix = null;
	private Button toggleSelectButton = null;
	private Button fitWidthButton = null;
	private Button fitHeightButton = null;
	private Button zoomInButton = null;
	private Button zoomOutButton = null;
	private Button prevPageButton = null;
	private Button nextPageButton = null;
	private Button toggleShowBgButton = null;
//	private Button xOffMinusButton = null;
//	private Button xOffPlusButton = null;
//	private Button yOffMinusButton = null;
//	private Button yOffPlusButton = null;
//	private Button scaleButton = null;
//	private Button previewScaleButton = null;

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

	private Scale scale = null;

	private Composite group1;
	private Group group2, group3, group4, filterGroup;

//	private Composite topAdj, botAdj;
//	private Group adjGroup;
//	private Label offsetLabel, xOffLabel, yOffLabel, 
//	denXLabel, nomXLabel,
//	scaleXLabel, 
//	denYLabel, nomYLabel,
//	scaleYLabel, 
	private Label 
	xCoordLabel, xCoordValue, yCoordLabel, yCoordValue,
	purityLabel, completenessLabel, purityValue, completenessValue;

//	private Text yOffText, xOffText, denXText, nomXText, denYText, nomYText;

	/**
	 * Constructor.
	 */
	public DocWrapEditor(final AnnotatorEditor we)
	{
		this.we = we;

		page2hGraphMap = new HashMap<Integer, ISegmentGraph>();
		page2ImageMap = new HashMap<Integer, Image>();

		this.monitor = new BrowserMonitor();
		monitor.addObserver(this);

		listeners = new ArrayList<ISelectionChangedListener>();
		
		we.registerForSelection(this);
	}

	/**
	 * Constructor.
	 * 
	 * @param site
	 * @param input
	 */
	public DocWrapEditor(AnnotatorEditor we, IEditorSite site, IEditorInput input)
	{
		this(we);
		try 
		{
			super.setSite(site);
			init(site, input);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
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
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName("Layout Graph");
		setTitleToolTip("Layout Graph");
	}

	@Override
	public void createPartControl(Composite parent) {

		Activator.modelControl.addModelChangedListener(this);

		// load preference store
		this.prefStore = LearnUIPlugin.getDefault().getPreferenceStore();

		/* BEGIN CONTENT */
		this.parent = parent;

		// add a control panel
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayout(new FillLayout());

		Composite controlPanel = createControlPanel(sash);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 160;
		controlPanel.setLayoutData(gridData);
		controlPanel.pack();

		// add the document graph
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 800;
		gridData.heightHint = 680;

		canvas = new DocumentGraphCanvas(sash, SWT.DOUBLE_BUFFERED
				| SWT.H_SCROLL | SWT.V_SCROLL, ColorConstants.lightBlue,
				new Dimension(0, 0));

		canvas.setViewport(new Viewport(true));
		canvas.setLayoutData(gridData);
		canvas.pack();
		canvas.addPlotMouseListener(new PlotMouseListener(this));
		canvas.addKeyListener(new PlotKeyListener());
		Cursor c = new Cursor(getSite().getShell().getDisplay(),
				SWT.CURSOR_CROSS);
		canvas.setCursor(c);

		/* END CONTENT */

		sizeListener = new ResizeListener();
		parent.addListener(SWT.Resize, sizeListener);

		sash.setWeights(new int[] { 4, 15 }); // this needs doing last...

		// Restore state from the previous session.
		restoreState();
	}

	/**
	 * 
	 * @param parent
	 */
	private Composite createControlPanel(Composite parent) 
	{
		if (inFile != null)
		{
			setStatusLine("File: " + inFile);
		}

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, true);
		data.widthHint = 140;
		layout.numColumns = 1;
		panel.setLayoutData(data);
		panel.setLayout(layout);

		////////////////////////////////////////////////////////////////
		group1 = new Composite(panel, SWT.NONE);
		layout = new GridLayout(4, true);
		group1.setLayout(layout);
		group1.setEnabled(false);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		gridData.widthHint = 160;
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalAlignment = SWT.CENTER;
		group1.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = 30;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.TOP;

		prevPageButton = new Button(group1, SWT.ARROW | SWT.LEFT);
		prevPageButton.setText("Previous");
		//		prevPageButton.setVisible(maxPage > currentPage);
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
		showPageNums.setText((currentPage) + "/" + (maxPage));

		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 40;
		showPageNums.setLayoutData(gridData);

		gridData = new GridData();
		gridData.heightHint = 30;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.TOP;

		nextPageButton = new Button(group1, SWT.ARROW | SWT.RIGHT);
		nextPageButton.setText("Next");
		nextPageButton.setEnabled(currentPage < maxPage);
		nextPageButton.setLayoutData(gridData);
		nextPageButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				nextPage();
			}

		});

		gridData = new GridData();
		gridData.widthHint = 30;
		gridData.horizontalSpan = 1;
		xCoordLabel = new Label(group1, SWT.NONE);
		xCoordLabel.setText("X: ");
		xCoordLabel.setLayoutData(gridData);

		xCoordValue = new Label(group1, SWT.NONE);
		xCoordValue.setText("");
		xCoordValue.setLayoutData(gridData);

		toggleSelectButton = new Button(group1, SWT.TOGGLE);
		toggleSelectButton.setSize(30, 30);
		toggleSelectButton.setEnabled(false);
		toggleSelectButton.setToolTipText("Toggle manual document placement");
		toggleSelectButton.setImage(getImageDescriptor("hand.png")
				.createImage());//resize
		toggleSelectButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				toggleDragActivated();
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		gridData.widthHint = 30;
		gridData.heightHint = 30;
		gridData.grabExcessHorizontalSpace = true;
		toggleSelectButton.setLayoutData(gridData);
		toggleSelectButton.pack();
		
		fitWidthButton = new Button(group1, SWT.TOGGLE);
		fitWidthButton.setSize(30, 30);
		fitWidthButton.setEnabled(false);
		fitWidthButton.setToolTipText("Center document");
		fitWidthButton.setImage(getImageDescriptor("resize.png")
				.createImage());//resize
		fitWidthButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.calibrateGraphPosition();
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		gridData.widthHint = 30;
		gridData.heightHint = 30;
		gridData.grabExcessHorizontalSpace = true;
		fitWidthButton.setLayoutData(gridData);
		fitWidthButton.pack();
		
		gridData = new GridData();
		gridData.widthHint = 30;
		gridData.horizontalSpan = 1;
		
		yCoordLabel = new Label(group1, SWT.NONE);
		yCoordLabel.setText("Y: ");
		yCoordLabel.setLayoutData(gridData);

		yCoordValue = new Label(group1, SWT.NONE);
		yCoordValue.setText("");
		yCoordValue.setLayoutData(gridData);
		//		org.eclipse.swt.graphics.Rectangle clientArea = group1.getClientArea();
		//		zoomScale = createZoomScale(group1, clientArea);
		

		group1.pack();
		// END GROUP 1
		// /////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////
		// GROUP2 (FILTER)
		filterGroup = new Group(panel, SWT.SHADOW_ETCHED_IN);
		filterGroup.setSize(160, 90);
		filterGroup.setText("Filter");
		filterGroup.setEnabled(false);

		layout = new GridLayout();
		layout.numColumns = 1;
		filterGroup.setLayout(layout);

		gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.heightHint = 90;
		gridData.widthHint = 160;
		gridData.verticalIndent = 1;
		filterGroup.setLayoutData(gridData);

		toggleFilterAllButton = new Button(filterGroup, SWT.RADIO);
		toggleFilterAllButton.setText("All");
		toggleFilterAllButton.setEnabled(false);
		toggleFilterAllButton.setSelection(true);
		toggleFilterAllButton.setToolTipText("Set filter to ALL");
		toggleFilterAllButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.setFilter1(null);
				canvas.setFilter2(null);
				canvas.calibrateGraphPosition();
			}
		});
		
		toggleFilterCombo1Button = new Button(filterGroup, SWT.RADIO);
		toggleFilterCombo1Button.setText("Same Type");
		toggleFilterCombo1Button.setEnabled(false);
		toggleFilterCombo1Button.setSelection(false);
		toggleFilterCombo1Button.setToolTipText("Set filter to SAME TYPE");
		toggleFilterCombo1Button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				ViewPart view = we.findView(BenchmarkNavigatorView.ID);
				if (view!=null && view instanceof BenchmarkNavigatorView)
				{
					BenchmarkNavigatorView annoView = (BenchmarkNavigatorView) view;
					String filter1 = annoView.getFirstSelectionType();
					canvas.setFilter1(filter1);
					canvas.calibrateGraphPosition();
				}
			}
		});
		
		toggleFilterCombo2Button = new Button(filterGroup, SWT.RADIO);
		toggleFilterCombo2Button.setText("Exact");
		toggleFilterCombo2Button.setEnabled(false);
		toggleFilterCombo2Button.setSelection(false);
		toggleFilterCombo2Button.setToolTipText("Set filter to EXACT");
		toggleFilterCombo2Button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				ViewPart view = we.findView(BenchmarkNavigatorView.ID);
				if (view!=null && view instanceof BenchmarkNavigatorView)
				{
					BenchmarkNavigatorView annoView = (BenchmarkNavigatorView) view;
					String filter1 = annoView.getSecondSelectionType();
					canvas.setFilter1(filter1);
					canvas.calibrateGraphPosition();
				}
			}
		});
		
		// /////////////////////////////////////////////////////////////////////
		// GROUP2 (LAYERS)
		group2 = new Group(panel, SWT.SHADOW_ETCHED_IN);
		group2.setSize(160, 90);
		group2.setText("Layers");
		group2.setEnabled(false);

		layout = new GridLayout();
		layout.numColumns = 1;
		group2.setLayout(layout);

		gridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gridData.heightHint = 90;
		gridData.widthHint = 160;
		gridData.verticalIndent = 3;
		group2.setLayoutData(gridData);

		toggleShowNodesButton = new Button(group2, SWT.CHECK);
		toggleShowNodesButton.setText("Nodes");
		toggleShowNodesButton.setEnabled(false);
		toggleShowNodesButton.setSelection(true);
		toggleShowNodesButton.setToolTipText("Toggle the display of nodes");
		toggleShowNodesButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.toggleNodes();
				canvas.calibrateGraphPosition();
			}
		});

		//		toggleShowPdfButton = new Button(group2, SWT.CHECK);
		//		toggleShowPdfButton.setText("PDF");
		//		toggleShowPdfButton.setEnabled(false);
		//		toggleShowPdfButton.setSelection(false);
		//		toggleShowPdfButton.setToolTipText("Toggle the display of the PDF");
		//		toggleShowPdfButton.addListener(SWT.Selection, new Listener() {
		//			@Override
		//			public void handleEvent(Event event) {
		//				canvas.toggleShowDocumentBG();
		//				canvas.calibrateGraphPosition();
		//			}
		//		});

		toggleShowAnnotationsButton = new Button(group2, SWT.CHECK);
		toggleShowAnnotationsButton.setText("Results");
		toggleShowAnnotationsButton.setEnabled(false);
		toggleShowAnnotationsButton.setSelection(true);
		toggleShowAnnotationsButton
		.setToolTipText("Toggle the display of results");
		toggleShowAnnotationsButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.toggleAnnotations();
				canvas.calibrateGraphPosition();
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
				canvas.toggleGT();
				canvas.calibrateGraphPosition();
			}
		});

		group2.pack();
		// END GROUP 2
		// ///////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////
		// GROUP3 (EVALUATION)
		group3 = new Group(panel, SWT.SHADOW_ETCHED_IN);
		group3.setSize(160, 55);
		group3.setText("Evaluation");
		group3.setEnabled(false);

		layout = new GridLayout();
		layout.numColumns = 2;
		group3.setLayout(layout);

		gridData = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gridData.heightHint = 55;
		gridData.widthHint = 160;
		gridData.verticalIndent = 10;
		group3.setLayoutData(gridData);

		gridData = new GridData();
		gridData.widthHint = 55;
		gridData.horizontalAlignment = SWT.RIGHT;

		purityLabel = new Label(group3, SWT.NONE);
		purityLabel.setText("Purity: ");
		purityLabel.setEnabled(true);

		purityValue = new Label(group3, SWT.NONE);
		purityValue.setText(" ");
		purityValue.setLayoutData(gridData);
		purityValue.setEnabled(true);

		completenessLabel = new Label(group3, SWT.NONE);
		completenessLabel.setText("Completeness: ");
		completenessLabel.setEnabled(true);

		completenessValue = new Label(group3, SWT.NONE);
		completenessValue.setText("  ");
		completenessValue.setLayoutData(gridData);
		completenessValue.setEnabled(true);
		
//		// /////////////////////////////////////////////////////////////////
//		/* begin new controls for PDF annotation */
//		adjGroup = new Group(panel, SWT.SHADOW_ETCHED_IN);
//		adjGroup.setText("Result Adjustment");
//		adjGroup.setLayout(new RowLayout(SWT.VERTICAL));
//		adjGroup.setSize(160, 260);
//
////		RowData rowData = new RowData();
////		rowData.height = 260;
////		rowData.width = 160;
////		adjGroup.setLayoutData(rowData);
//		
//		topAdj = new Composite(adjGroup, SWT.SHADOW_ETCHED_IN);
//		topAdj.setLayout(new GridLayout(2, false));
//
//		/* X rescaling */
//		scaleXLabel = new Label(topAdj, SWT.NONE);
//		scaleXLabel.setText("X Scale:");
//		gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.verticalIndent = 5;
//		gridData.grabExcessHorizontalSpace = true;
//		scaleXLabel.setLayoutData(gridData);
//
//		nomXLabel = new Label(topAdj, SWT.NONE);
//		gridData = new GridData();
//		// gridData.widthHint = 25;
//		// gridData.heightHint = 12;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.BEGINNING;
//		nomXLabel.setLayoutData(gridData);
//		nomXLabel.setText("Nominator:");
//
//		nomXText = new Text(topAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 40;
//		gridData.heightHint = 12;
//		// gridData.horizontalSpan = 3;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.END;
//		nomXText.setLayoutData(gridData);
//		nomXText.setTextLimit(5);
//		nomXText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i]!='.' && chars[i]!='-') {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		denXLabel = new Label(topAdj, SWT.NONE);
//		gridData = new GridData();
//
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.BEGINNING;
//		denXLabel.setLayoutData(gridData);
//		denXLabel.setText("Denominator:");
//
//		denXText = new Text(topAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 40;
//		gridData.heightHint = 12;
//		// gridData.horizontalSpan = 3;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.END;
//		denXText.setLayoutData(gridData);
//		denXText.setTextLimit(5);
//		denXText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i]!='.') {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		/* Y rescaling */
//		scaleYLabel = new Label(topAdj, SWT.NONE);
//		scaleYLabel.setText("Y Scale:");
//		gridData = new GridData();
//		gridData.horizontalSpan = 2;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.verticalIndent = 5;
//		gridData.grabExcessHorizontalSpace = true;
//		scaleYLabel.setLayoutData(gridData);
//
//		nomYLabel = new Label(topAdj, SWT.NONE);
//		gridData = new GridData();
//		// gridData.widthHint = 25;
//		// gridData.heightHint = 12;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.BEGINNING;
//		nomYLabel.setLayoutData(gridData);
//		nomYLabel.setText("Nominator:");
//
//		nomYText = new Text(topAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 40;
//		gridData.heightHint = 12;
//		// gridData.horizontalSpan = 3;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.END;
//		nomYText.setLayoutData(gridData);
//		nomYText.setTextLimit(5);
//		nomYText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i]!='.' && chars[i]!='-') {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		denYLabel = new Label(topAdj, SWT.NONE);
//		gridData = new GridData();
//
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.BEGINNING;
//		denYLabel.setLayoutData(gridData);
//		denYLabel.setText("Denominator:");
//
//		denYText = new Text(topAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 40;
//		gridData.heightHint = 12;
//		// gridData.horizontalSpan = 3;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.horizontalAlignment = SWT.END;
//		denYText.setLayoutData(gridData);
//		denYText.setTextLimit(5);
//		denYText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9') && chars[i]!='.') {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		previewScaleButton = new Button(topAdj, SWT.PUSH);
//		previewScaleButton.setText("Preview");
//		previewScaleButton.setToolTipText("Preview adjustment");
//		previewScaleButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) 
//			{
//				// rescale
//				double xNom = 0;
//				if (nomXText.getText()!=null && nomXText.getText().length()>0)
//				{
//					if (nomXText.getText().startsWith("."))
//					{
//						nomXText.setText("0"+nomXText.getText());
//					}
//					xNom = Double.parseDouble(nomXText.getText());
//				}
//				double xDenom = 0;
//				if (denXText.getText()!=null && denXText.getText().length()>0)
//				{
//					if (denXText.getText().startsWith("."))
//					{
//						denXText.setText("0"+denXText.getText());
//					}
//					xDenom = Double.parseDouble(denXText.getText());
//				}
//
//				double xScale = 0d;
//				if (xNom!=0 && xDenom!=0)
//				{
//					xScale = xNom/xDenom;
//				}
//
//				// rescale
//				double yNom = 0;
//				if (nomYText.getText()!=null && nomYText.getText().length()>0)
//				{
//					if (nomYText.getText().startsWith("."))
//					{
//						nomYText.setText("0"+nomYText.getText());
//					}
//					yNom = Double.parseDouble(nomYText.getText());
//				}
//				double yDenom = 0;
//				if (denYText.getText()!=null && denYText.getText().length()>0)
//				{
//					if (denYText.getText().startsWith("."))
//					{
//						denYText.setText("0"+denYText.getText());
//					}
//					yDenom = Double.parseDouble(denYText.getText());
//				}
//
//				double yScale = 0d;
//				if (yNom!=0 && yDenom!=0)
//				{
//					yScale = yNom/yDenom;
//				}
//
//				int xoff = 0;
//				int yoff = 0;
//				String xoffText = xOffText.getText();
//				String yoffText = yOffText.getText();
//				if (xoffText.length()>0)
//				{
//					xoff = Integer.parseInt(xOffText.getText());
//				}
//				if (yoffText.length()>0)
//				{
//					yoff = Integer.parseInt(yOffText.getText());
//				}
//				adj = new RectangleAdjustment(xoff, yoff, xScale, yScale);
//				canvas.setAnnotationAdjustment(adj);
//				canvas.calibrateGraphPosition();
//				updateActionEnablement();
//			}
//		});
//
//		gridData = new GridData();
//		gridData.horizontalSpan = 1;
//		gridData.horizontalAlignment = SWT.END;
//		gridData.verticalIndent = 5;
//		previewScaleButton.setLayoutData(gridData);
//		previewScaleButton.setEnabled(false);
//
//		scaleButton = new Button(topAdj, SWT.PUSH);
//		scaleButton.setText("Apply");
//		scaleButton.setToolTipText("Apply adjustment");
//		scaleButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) 
//			{
//				// rescale
//				double xNom = 0;
//				if (nomXText.getText()!=null && nomXText.getText().length()>0)
//				{
//					if (nomXText.getText().startsWith("."))
//					{
//						nomXText.setText("0"+nomXText.getText());
//					}
//					xNom = Double.parseDouble(nomXText.getText());
//				}
//				double xDenom = 0;
//				if (denXText.getText()!=null && denXText.getText().length()>0)
//				{
//					if (denXText.getText().startsWith("."))
//					{
//						denXText.setText("0"+denXText.getText());
//					}
//					xDenom = Double.parseDouble(denXText.getText());
//				}
//
//				double xScale = 0d;
//				if (xNom!=0 && xDenom!=0)
//				{
//					xScale = xNom/xDenom;
//				}
//
//				// rescale
//				double yNom = 0;
//				if (nomYText.getText()!=null && nomYText.getText().length()>0)
//				{
//					if (nomYText.getText().startsWith("."))
//					{
//						nomYText.setText("0"+nomYText.getText());
//					}
//					yNom = Double.parseDouble(nomYText.getText());
//				}
//				double yDenom = 0;
//				if (denYText.getText()!=null && denYText.getText().length()>0)
//				{
//					if (denYText.getText().startsWith("."))
//					{
//						denYText.setText("0"+denYText.getText());
//					}
//					yDenom = Double.parseDouble(denYText.getText());
//				}
//
//				double yScale = 0d;
//				if (yNom!=0 && yDenom!=0)
//				{
//					yScale = yNom/yDenom;
//				}
//
//				int xoff = 0;
//				int yoff = 0;
//				String xoffText = xOffText.getText();
//				String yoffText = yOffText.getText();
//				if (xoffText.length()>0)
//				{
//					xoff = Integer.parseInt(xOffText.getText());
//				}
//				if (yoffText.length()>0)
//				{
//					yoff = Integer.parseInt(yOffText.getText());
//				}
//				RectangleAdjustment aa = new RectangleAdjustment(xoff, yoff, xScale, yScale);
//
//				//also, change the actual result boxes
//				BenchmarkModel model = Activator.modelControl.getModel();
//				for (Benchmark benchmark : model.getBenchmarks())
//				{
//					for (BenchmarkDocument document : benchmark.getDocuments())
//					{
//						DiademBenchmarkEngine.adjustBenchmarkDocument((PdfBenchmarkDocument) document, aa, true);
//					}
//				}
//
//				denYText.setText("");
//				denXText.setText("");
//				nomYText.setText("");
//				nomXText.setText("");
//				xOffText.setText("");
//				yOffText.setText("");
//				adj = new RectangleAdjustment(0, 0, 1d, 1d);
//				canvas.setAnnotationAdjustment(aa);
//				canvas.calibrateGraphPosition();
//				updateActionEnablement();
//			}
//		});
//
//		gridData = new GridData();
//		gridData.horizontalSpan = 1;
//		gridData.horizontalAlignment = SWT.END;
//		gridData.verticalIndent = 5;
//		scaleButton.setLayoutData(gridData);
//		scaleButton.setEnabled(false);
//
//		/* offset */
//		botAdj = new Composite(adjGroup, SWT.NONE);
//		layout = new GridLayout(4, false);
//		botAdj.setLayout(layout);
//
//		offsetLabel = new Label(botAdj, SWT.NONE);
//		offsetLabel.setText("Offset:");
//		gridData = new GridData();
//		gridData.horizontalSpan = 4;
//		gridData.verticalAlignment = SWT.BEGINNING;
//		gridData.verticalIndent = 5;
//		offsetLabel.setLayoutData(gridData);
//
//		xOffLabel = new Label(botAdj, SWT.NONE);
//		xOffLabel.setText("X offset:");
//
//		xOffMinusButton = new Button(botAdj, SWT.PUSH);
//		xOffMinusButton.setText("-");
//		gridData = new GridData();
//		gridData.widthHint = 21;
//		gridData.heightHint = 21;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		xOffMinusButton.setLayoutData(gridData);
//
//		xOffText = new Text(botAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 24;
//		gridData.heightHint = 12;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		xOffText.setTabs(2);
//		xOffText.setLayoutData(gridData);
//		xOffText.setTextLimit(3);
//		xOffText.setSize(30, 20);
//		xOffText.setText("0");
//		xOffText.setEditable(true);
//		xOffText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9')) {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		xOffMinusButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				int xoff = 0;
//				String xoffText = xOffText.getText();
//				if (xoffText.length()>0)
//				{
//					xoff = Integer.parseInt(xOffText.getText());
//				}
//
//				xOffText.setText(xoff - 1 + "");
//
//				RectangleAdjustment is = canvas.getAnnotationAdjustment();
//				RectangleAdjustment aa = new RectangleAdjustment(xoff - 1, is
//						.getyOffset(), is.getXScale(), is.getYScale());
//				canvas.setAnnotationAdjustment(aa);
//				canvas.calibrateGraphPosition();
//			}
//		});
//
//		xOffPlusButton = new Button(botAdj, SWT.PUSH);
//		xOffPlusButton.setText("+");
//		gridData = new GridData();
//		gridData.widthHint = 21;
//		gridData.heightHint = 21;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		xOffPlusButton.setLayoutData(gridData);
//
//		xOffPlusButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				int xoff = 0;
//				String xoffText = xOffText.getText();
//				if (xoffText.length()>0)
//				{
//					xoff = Integer.parseInt(xOffText.getText());
//				}
//				xOffText.setText(xoff + 1 + "");
//
//				RectangleAdjustment is = canvas.getAnnotationAdjustment();
//				RectangleAdjustment aa = new RectangleAdjustment(xoff + 1, is
//						.getyOffset(), is.getXScale(), is.getYScale());
//				canvas.setAnnotationAdjustment(aa);
//				canvas.calibrateGraphPosition();
//			}
//		});
//
//		yOffLabel = new Label(botAdj, SWT.NONE);
//		yOffLabel.setText("Y offset:");
//
//		yOffMinusButton = new Button(botAdj, SWT.PUSH);
//		yOffMinusButton.setText("-");
//		gridData = new GridData();
//		gridData.widthHint = 21;
//		gridData.heightHint = 21;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		yOffMinusButton.setLayoutData(gridData);
//
//		yOffText = new Text(botAdj, SWT.NONE);
//		gridData = new GridData();
//		gridData.widthHint = 24;
//		gridData.heightHint = 12;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		yOffText.setLayoutData(gridData);
//		yOffText.setTextLimit(3);
//		yOffText.setText("0");
//		yOffText.setEditable(true);
//		yOffText.addListener(SWT.Verify, new Listener() {
//			public void handleEvent(Event e) {
//				String string = e.text;
//				char[] chars = new char[string.length()];
//				string.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (!('0' <= chars[i] && chars[i] <= '9')) {
//						e.doit = false;
//						return;
//					}
//				}
//			}
//		});
//
//		yOffMinusButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				int yoff = 0;
//				String yoffText = yOffText.getText();
//				if (yoffText.length()>0)
//				{
//					yoff = Integer.parseInt(yOffText.getText());
//				}
//				yOffText.setText(yoff - 1 + "");
//
//				RectangleAdjustment is = canvas.getAnnotationAdjustment();
//				RectangleAdjustment aa = new RectangleAdjustment(is
//						.getxOffset(), yoff - 1, is.getXScale(), is.getYScale());
//				canvas.setAnnotationAdjustment(aa);
//				canvas.calibrateGraphPosition();
//			}
//		});
//
//		yOffPlusButton = new Button(botAdj, SWT.PUSH);
//		yOffPlusButton.setText("+");
//		gridData = new GridData();
//		gridData.widthHint = 21;
//		gridData.heightHint = 21;
//		gridData.horizontalAlignment = SWT.CENTER;
//		gridData.verticalAlignment = SWT.CENTER;
//		yOffPlusButton.setLayoutData(gridData);
//		yOffPlusButton.addListener(SWT.Selection, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				int yoff = 0;
//				String yoffText = yOffText.getText();
//				if (yoffText.length()>0)
//				{
//					yoff = Integer.parseInt(yOffText.getText());
//				}
//				yOffText.setText(yoff + 1 + "");
//
//				RectangleAdjustment is = canvas.getAnnotationAdjustment();
//				RectangleAdjustment aa = new RectangleAdjustment(is
//						.getxOffset(), yoff + 1, is.getXScale(), is.getYScale());
//				canvas.setAnnotationAdjustment(aa);
//				canvas.calibrateGraphPosition();
//			}
//		});
		// /////////////////////////////////////////////////////////////////

//		enableAdjustmentControl(false);

		panel.pack();
		return panel;
	}

	/**
	 * 
	 * @return
	 */
	public BrowserMonitor getMonitor() {
		return monitor;
	}

	/**
	 * Process a PDF document (one page only).
	 * 
	 * @param inFile
	 * @param page
	 *            , the page number to process
	 */
	public void processPdf(String inFile, int page) 
	{
		this.inFile = inFile;
		this.currentPage = page;

		PdfBenchmarkDocument document = 
			(PdfBenchmarkDocument) DocumentController.docModel.getDocument();
		String pref_rep = prefStore.getString(PreferenceConstants.PREF_DW_REP);

		setStatusLine("Opening file: " + inFile);

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

				ISegmentGraph cache = page2hGraphMap.get(currentPage);
				String cName = null;
				if (cache != null) {
					Class<?> c = cache.getClass();
					cName = c.getCanonicalName();
				}

				if (cName != null)// && cName.equals(pref_rep))
				{
					hGraph = page2hGraphMap.get(currentPage);
					maxPage = hGraph.getNumPages();
				}
				else 
				{
					ISegmentGraph dg = null;
					if (dg == null) 
					{
						ErrorDump.info(this, "Processing file: " + inFile + ", page number: " + page);

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

						switch (documentRepresentation) 
						{
						case DocWrapConstants.DOC_REP_PLAIN:

							// this is the non-hierarchical case
							dg = DocumentGraphFactory.PDF
							.generateDocumentGraphNew(inFile, page);// ,
							// useSemantics);
							maxPage = dg.getNumPages();
							page2hGraphMap.put(currentPage, dg);
							hGraph = dg;
							break;

						case DocWrapConstants.DOC_REP_SEG_LEVEL:

							// generate the segmentation hierarchy
							SegLevelGraph stack = DocumentGraphFactory.PDF
							.generateDocumentHierarchy(inFile, page,
									true, true, true);
							this.activeLevel = stack.getDefaultLevel();
							scale.setSelection((activeLevel * 10) - 1);
							this.maxPage = stack.getNumPages();// remember page
							// count

							// flip reverse all stack level graphs:
							Iterator<Integer> levelIt = stack
							.getAvailableLevelIterator();
							while (levelIt.hasNext()) {
								int levelIdx = levelIt.next();
								dg = (ISegmentGraph) stack.getLevel(levelIdx)
								.getGraph();
								flipReverseDocGraph(dg);
							}

							// add reading order relations to default level
							// graph
							DocumentMatrix dm = DocumentMatrix
							.newInstance(stack);
							dm.addReadingOrderRelations(stack);

							hGraph = stack;
							page2hGraphMap.put(currentPage, hGraph);
							canvas.setDocumentGraph(hGraph);
							break;

						case DocWrapConstants.DOC_REP_HIER:

							// generate the segmentation hierarchy
							timer.startTask(1);
							DocHierGraph g = DocumentGraphFactory.PDF
							.generateDocumentHGraph(url.toURI(), page,
									useSemantics);
							if (g instanceof DocumentGraph) {
								DocumentGraph tmp = (DocumentGraph) g;
								tmp.computeDimensions();
								flipReverseDocGraph(tmp);
								tmp.computeDimensions();
							}
							if (g instanceof DocHierGraph) {
								DocHierGraph dgh = (DocHierGraph) g;
								this.maxPage = dgh.getNumPages();
							}

							hGraph = g;
							page2hGraphMap.put(currentPage, hGraph);
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
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				FileChannel fc = raf.getChannel();
				ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
						fc.size());
				PDFFile pdfFile = new PDFFile(buf);
				raf.close();
				
				PDFPage pg = pdfFile.getPage(page);
				Rectangle bbox = pg.getBBox().getBounds();

				//create a new document page
				PdfDocumentPage docPage = new PdfDocumentPage(page, pg, bbox);
				docPage.setGraph(hGraph);
				document.addPage(docPage);

				/* update document model */
				if (document instanceof PdfBenchmarkDocument) 
				{
					PdfBenchmarkDocument benchDoc = (PdfBenchmarkDocument) document;
					benchDoc.setPdfFile(pdfFile);
					benchDoc.setNumPages(maxPage);
				}

				DocumentModel model = new DocumentModel();
				model.setPdfFile(pdfFile);
				model.setDocument((PdfBenchmarkDocument) document);
				model.setPageNum(currentPage);
				model.setNumPages(maxPage);
				model.setDocumentGraph(hGraph);
				model.setFormat(DocumentFormat.PDF);
				DocumentUpdate update = new DocumentUpdate();
				update.setUpdate(model);
				update.setType(UpdateType.NEW_DOCUMENT);
				update.setProvider(this);
				we.setDocumentUpdated(update);
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

			setStatusLine("Ready");

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

	/**
	 * Switch to next page, if available.
	 * 
	 */
	private void nextPage() 
	{
		if (currentPage == maxPage)
			return;

		currentPage++;

		ISegmentGraph g = page2hGraphMap.get(currentPage);
		if (g==null)
		{
			// set cursor to busy...
			DocWrapUIUtils.getWrapperEditor().showBusy(true);
			processPdf(inFile, currentPage);
			DocWrapUIUtils.getWrapperEditor().showBusy(false);
		} 
		else 
		{
			hGraph = g;
			
			/* notify document listeners */
			DocumentModel model = new DocumentModel();
			model.setPageNum(currentPage);
			model.setNumPages(maxPage);
			model.setDocumentGraph(hGraph);
			DocumentUpdate update = new DocumentUpdate();
			update.setType(UpdateType.PAGE_CHANGE);
			update.setUpdate(model);
			update.setProvider(this);
			we.setDocumentUpdated(update);
			we.graph = hGraph;
		}

		if (canvas != null) {
			canvas.clearAllHighlights();
			canvas.setDocumentGraph(hGraph);
			canvas.plot(false);
			fitWidth = true;
			canvas.calibrateGraphPosition();
		}

		updateActionEnablement();		
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

		ISegmentGraph g = page2hGraphMap.get(currentPage);
		if (g==null)
		{
			// set cursor to busy...
			DocWrapUIUtils.getWrapperEditor().showBusy(true);
			processPdf(inFile, currentPage);
			DocWrapUIUtils.getWrapperEditor().showBusy(false);

			page2hGraphMap.put(currentPage, hGraph);
		} 
		else 
		{
			hGraph = g;
			
			/* notify document listeners */
			DocumentModel model = new DocumentModel();
			model.setPageNum(currentPage);
			model.setNumPages(maxPage);
			DocumentUpdate update = new DocumentUpdate();
			update.setType(UpdateType.PAGE_CHANGE);
			update.setUpdate(model);
			update.setProvider(this);
			we.setDocumentUpdated(update);
			we.graph = hGraph;
		}

		if (canvas != null) {
			canvas.clearAllHighlights();
			canvas.setDocumentGraph(hGraph);
			// canvas.setColumns(new ArrayList<LayoutColumn>());
			canvas.plot(false);
			canvas.calibrateGraphPosition();
		}

		if (showPageNums != null) {
			showPageNums.setText((currentPage) + "/" + (maxPage));
		}

		updateActionEnablement();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		page2hGraphMap = null;
		page2ImageMap  = null;

		Activator.modelControl.removeModelChangedListener(this);

		/* do not forget to unregister the listeners, otherwise we get problems */
		we.deregisterFromSelection(this);
		we.deregisterFromDocumentUpdate(this);

		if (canvas != null) {
			canvas.dispose();
		}

		try
		{
			group1.dispose();
			group2.dispose();
			filterGroup.dispose();
			if (group3 != null) {
				group3.dispose();
			}
			if (group4 != null) {
				group4.dispose();
			}
		} catch (NullPointerException e) {
			// in case some have not been initialized
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
		//HTML case
		else if (document.getFormat().equals(DocumentFormat.HTML)) {
			//nothing to do (this gets done when a HTML load is triggered in the Weblearn editor
		}
		// TIFF case
		else if (document.getFormat().equals(DocumentFormat.TIFF)) {
			URL url;
			try {
				url = new URI(document.getUri()).toURL();
				Image img = PDFUtils.loadTiff(url);

				/* update document model */
				DocumentModel model = new DocumentModel();
				model.setPageNum(currentPage);
				model.setNumPages(maxPage);
				model.setThumb(img);
				model.setDocument((PdfBenchmarkDocument) document);

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

		timer.stopTask(0);
		ErrorDump.debug(this,
				"Loaded document graph in " + timer.getTimeMillis(0) + "ms");

		if (canvas != null) {
			this.canvas.clearAllHighlights();
			this.canvas.setDocumentGraph(hGraph);
			this.canvas.plot(true);
			this.canvas.calibrateGraphPosition(0.75d);
		}

		// unset cursor from busy...
		DocWrapUIUtils.getWrapperEditor().showBusy(false);
		we.graph = hGraph;
		updateActionEnablement();
	}

	/**
	 * 
	 */
	@Override
	public void update(Observable o, Object arg) {
		try {
			if (we != null) {
				we.showBusy(true);
			}

			IDocument document = we.getActiveDocument();
			if (document == null
					|| (document.getUri() != null && document.getUri()
							.endsWith(".wrapper")))
				return;

			processDocument(document);

			if (we != null) {
				we.showBusy(false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (canvas != null) {
			canvas.plot(false);
			canvas.calibrateGraphPosition();
		}

		updateActionEnablement();
	}

	/**
	 * 
	 * @param newLevel
	 */
	public void changeActiveLevel(int newLevel) {
		if (newLevel == activeLevel) {
			return;
		}
		this.activeLevel = newLevel;
		switch (documentRepresentation) {

		case DocWrapConstants.DOC_REP_PLAIN:

			break;

		case DocWrapConstants.DOC_REP_SEG_LEVEL:

			if (!(hGraph instanceof SegLevelGraph)) {
				return;
			}
			SegLevelGraph stack = (SegLevelGraph) hGraph;
			IGraphLevel<DocNode, DocEdge> level = stack.getLevel(newLevel);
			if (level != null) {
				activeLevel = newLevel;
				IGraphLevel<DocNode, DocEdge> lv = stack.getLevel(activeLevel);
				if (lv != null) {
					stack.setDefaultLevel(activeLevel);
					scale.setSelection((activeLevel * 10) - 1);
					canvas.setDocumentGraph(stack);
					canvas.calibrateGraphCenter(true);
				}
			} else {
				// write to status bar...
			}
			break;

		case DocWrapConstants.DOC_REP_HIER:

			// if (!(hGraph instanceof DocumentGraphHier)) {
			// return;
			// }
			// DocumentGraphHier g = (DocumentGraphHier) hGraph;
			// // g.g

		default:
			break;
		}

	}

	/**
	 * Get viewport dimension.
	 * 
	 * @return
	 */
	public Dimension getSize() {
		return canvas.getViewport().getSize();
	}

	/**
	 * Calibrate the graph's position to be centered.
	 * 
	 */
	public void calibrateGraphPosition(boolean fitWidth, boolean fitHeight,
			boolean remember) {
		canvas.setxTrans(0);
		canvas.setyTrans(0);

		canvas.clearContents();
		canvas.layout();
		canvas.redraw();

		Dimension dims = getSize();

		int width = (int) (1 * dims.width);
		int height = (int) (1 * dims.height);

		ISegmentGraph g = canvas.getDocumentGraph();
		if (g == null) {
			return;
		}

		if (g instanceof SegLevelGraph) {
			g = ((SegLevelGraph) g).getBaseGraph();
		}

		if (g instanceof DocumentGraph) {
			Rectangle bounds = DocumentController.docModel.getBounds();
			double scaleWidth = (new Double(0.9 * width)) / (bounds.width);
			double scaleHeight = (new Double(0.9 * height)) / (bounds.height);

			int xTrans = 0;
			int yTrans = 0;
			double scale = 0d;

			if (fitHeight && fitWidth) {
				scale = Math.min(scaleWidth, scaleHeight);
				double midDocX = scale * (bounds.x + (bounds.width / 2));
				double midPageX = width / 2;
				double midDocY = scale * (bounds.y + (bounds.height / 2));
				double midPageY = height / 2;
				xTrans = (int) (midPageX - midDocX);
				yTrans = (int) (midPageY - midDocY);
			} else if (fitHeight) {
				scale = scaleHeight;
				double midDocX = scale * (bounds.x + (bounds.width / 2));
				double midPageX = width / 2;
				double midDocY = scale * (bounds.y + (bounds.height / 2));
				double midPageY = height / 2;
				xTrans = (int) (midPageX - midDocX);
				yTrans = (int) Math.abs(midPageY - midDocY);
			} else if (fitWidth) {
				scale = scaleWidth;
				double midDocX = scale * (bounds.x + (bounds.width / 2));
				double midPageX = width / 2;
				xTrans = (int) (midPageX - midDocX);
				yTrans = (int) 40;// (midPageY - midDocY);
			} else {
				scale = Math.min(scaleWidth, scaleHeight);
				double midDocX = scale * (bounds.x + (bounds.width / 2));
				double midPageX = width / 2;
				double midDocY = scale * (bounds.y + (bounds.height / 2));
				double midPageY = height / 2;
				xTrans = (int) (midPageX - midDocX);
				yTrans = (int) (midPageY - midDocY);
			}

			canvas.drawActiveGraph(true);

			/* translate graph */
			if (remember) {
				canvas.translateContents2(xTrans, yTrans);//Math.max(30, yTrans));
			} else {
				canvas.translateContents(xTrans, yTrans);//Math.max(30, yTrans));
			}
		}
		parent.layout();
	}

	// /**
	// * Evaluate a given pattern
	// */
	// public void evaluatePattern(IGraph<DocNode,DocEdge> s)
	// {
	// ISegmentGraph dg = canvas.getDocumentGraph();
	//
	// if (s==null) {
	// MessageBox mbox = new MessageBox(parent.getShell(), SWT.ICON_WARNING);
	// mbox.setMessage("Please select a pattern first");
	// mbox.open();
	// return;
	// }
	//
	// String pref_distance =
	// prefStore.getString(PreferenceConstants.PREF_DW_DISTANCE);
	// String pref_rep = prefStore.getString(PreferenceConstants.PREF_DW_REP);
	// // String pref_matcher =
	// prefStore.getString(PreferenceConstants.PREF_DW_SEARCH);
	//
	// //update the matching constants
	// String gm_nodes =
	// prefStore.getString(PreferenceConstants.PREF_DW_GED_NODES);
	// String gm_edges =
	// prefStore.getString(PreferenceConstants.PREF_DW_GED_EDGES);
	// GMConstants mc = GMConstants.NODES_ONLY;//use default;
	// if ("true".equals(gm_nodes) && "true".equals(gm_edges)) {
	// mc = GMConstants.NODES_EDGES;
	// } else if ("true".equals(gm_nodes) && "false".equals(gm_edges)) {
	// mc = GMConstants.NODES_ONLY;
	// } else if ("false".equals(gm_nodes) && "true".equals(gm_edges)) {
	// mc = GMConstants.EDGES_ONLY;
	// }
	//
	// if (matcher==null) {
	// matcher = new ScanLineMatcher(mc);//make default
	// }
	//
	// //fetch preference values
	// if
	// (pref_distance.equals(PreferenceConstants.PREF_DW_DIST_VALUE_GED_EXACT))
	// {
	// distance = new GED_H_Exact(new ECF());
	// } else if
	// (pref_distance.equals(PreferenceConstants.PREF_DW_DIST_VALUE_GED_APPROX))
	// {
	// if (pref_rep.equals(PreferenceConstants.PREF_DW_VALUE_REP_LEVEL)) {
	// distance = new GED_H_Approx(mc);
	// } else if (pref_rep.equals(PreferenceConstants.PREF_DW_VALUE_REP_HIER)) {
	// distance = new GED_H_ApproxNew(mc);
	// }
	// } else if
	// (pref_distance.equals(PreferenceConstants.PREF_DW_DIST_VALUE_PROBING)) {
	// distance = new GraphProbing(mc);
	// } else if
	// (pref_distance.equals(PreferenceConstants.PREF_DW_DIST_VALUE_RAND_WALK))
	// {
	// distance = new RandWalkDistance(mc);
	// } else if
	// (pref_distance.equals(PreferenceConstants.PREF_DW_DIST_VALUE_TOPO_WALK))
	// {
	// distance = new TopoWalkDistance();
	// }
	//
	// if (distance==null) {
	// //set the distance manually
	// IGraphDistance dist =
	// new GED_H_Approx(mc);
	// // new GraphProbing();
	// // new RandWalkDistance();
	// matcher.setDistance(dist);
	// } else {
	// matcher.setDistance(distance);
	// }
	//
	// SimpleTimer timer = new SimpleTimer();
	// timer.startTask(1);
	// List<WrappingInstance> instances = matcher.findInstances(dg, s); //was
	// graph_selection
	// timer.stopTask(1);
	// String timeUsed = timer.getTimeMinutesSecondsMillis(1);
	//
	// String msg = "Finished running matching in " + timeUsed + ". " +
	// instances.size() + " matches found";
	// setStatusLine(msg);
	// ErrorDump.debug(this, msg);
	//
	// List<GenericSegment> posMatchSegments = new ArrayList<GenericSegment>();
	// for (WrappingInstance wi : instances) {
	// GenericSegment gs = new GenericSegment();
	// gs.setBoundingBox(wi.getBoundingRectangle());
	// posMatchSegments.add(gs);
	// }
	//
	// // plotter.setPosMatchSegments(posMatchSegments);
	// // plotter.plot(true);
	// // calibrateGraphPosition(fitWidth, fitHeight, true);
	// // parent.redraw();
	// }

	/**
	 * Document graphs are inverted for GUI presentation. Revert back to
	 * original...
	 * 
	 * @param g
	 */
	private void flipReverseDocGraph(ISegmentGraph g) {
		java.awt.Rectangle r = g.getDimensions();
		int y2 = r.y + r.height;
		// int x2 = r.x + r.width;

		for (DocNode node : g.getNodes()) {
			float a = Math.abs(y2 - node.getSegY2());
			float b = Math.abs(y2 - node.getSegY1());
			// float c = Math.abs(x2 - node.getSegX2());
			// float d = Math.abs(x2 - node.getSegX1());
			node.setSegY1(a);
			node.setSegY2(b);
			// node.setSegX1(c);
			// node.setSegX2(d);
		}
	}

	/**
	 * Create all actions.
	 */
	public void createActions() {

		// dictDropDownAction = new DictDropDownAction(this);
		// dictDropDownAction.setToolTipText("Choose Domain");
		// dictDropDownAction.setDescription("Domain");

		// Take screenshot action
		// takeScreenshotAction = new Action("Screenshot", SWT.NONE) {
		// public void run() {
		// org.eclipse.swt.graphics.Rectangle region = new
		// org.eclipse.swt.graphics.Rectangle(0, 0, 400, 400);
		// SWTImageUtils.takeScreenshot(parent, region);
		// }
		// };
		// takeScreenshotAction.setToolTipText("Take screenshot");
		// takeScreenshotAction.setText("Screenshot");
		// takeScreenshotAction.setDescription("Take screenshot");

		// Show document button
		showDocumentAction = new Action("Show Document", SWT.TOGGLE) {
			public void run() {
				canvas.toggleShowDocumentBG();
			}
		};
		showDocumentAction.setToolTipText("Show document");
		if (canvas.isShowDocumentBg()) {
			showDocumentAction.setChecked(true);
		} else {
			showDocumentAction.setChecked(false);
		}
		showDocumentAction.setText("Show Document");
		showDocumentAction.setDescription("Show document");

		// Toggle Evaluate Action
		// toggleEvaluateAction = new Action("Evaluate", SWT.TOGGLE) {
		// public void run() {
		// plotter.toogleEvaluateMode();
		// if (plotter.isInEvaluateMode()) {
		// openEvaluateView();
		// } else {
		// closeEvaluateView();
		// }
		// }
		// };
		// toggleEvaluateAction.setToolTipText("Toggle Evaluation Mode");
		// toggleEvaluateAction.setChecked(false);
		// toggleEvaluateAction.setText("Evaluation");
		// toggleEvaluateAction.setDescription("Toggle Evaluation Mode");

		// Toggle Edges Action
		toggleEdgesAction = new Action("Show Edges", SWT.TOGGLE) {
			public void run() {
				canvas.toggleEdges();
			}
		};
		toggleEdgesAction.setToolTipText("Toggle edge display");
		toggleEdgesAction.setChecked(true);
		toggleEdgesAction.setText("Show Edges");
		toggleEdgesAction.setDescription("Show Edges");

		// Toggle Nodes Action
		toggleNodesAction = new Action("Show Nodes", SWT.TOGGLE) {
			public void run() {
				canvas.toggleNodes();
			}
		};

		// toggleNodesAction.setImageDescriptor(getImageDescriptor("nodes.png"));
		toggleNodesAction.setToolTipText("Toggle node display");
		toggleNodesAction.setChecked(true);
		toggleNodesAction.setText("Show Nodes");
		toggleNodesAction.setDescription("Show Nodes");

		// Go to Next Page Action
		nextPageAction = new Action("Next Page") {
			public void run() {
				nextPage();
			};
		};
		nextPageAction.setImageDescriptor(getImageDescriptor("next.png"));
		nextPageAction.setToolTipText("Show next page");
		nextPageAction.setDescription("Next Page");

		// Go to Previous Page Action
		prevPageAction = new Action("Previous Page") {
			public void run() {
				prevPage();
			};
		};
		prevPageAction.setImageDescriptor(getImageDescriptor("prev.png"));
		prevPageAction.setToolTipText("Show previous page");
		prevPageAction.setDescription("Previous Page");

		//
		// showPageNums = new Action("") { };
		// showPageNums.setChecked(false);
		// showPageNums.setEnabled(false);
		// showPageNums.setText((currentPage)+"/"+(maxPage));

		//
		resizeAction = new Action("Resize") {
			public void run() {
				toggleDragActivated();
				// plotter.calibrateGraphPosition();
			}
		};
		resizeAction.setImageDescriptor(getImageDescriptor("resize.png"));
		resizeAction.setToolTipText("Calibrate Document Display");
		resizeAction.setDescription("Calibrate Document Display");

		updateActionEnablement();
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 */
	private ImageDescriptor getImageDescriptor(String relativePath) {
		String iconPath = "icons/annotator/docwrap/";
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
	 * 
	 */
	public void updateActionEnablement() {
		if (maxPage == 0) {
			this.maxPage = we.documentControl.getDocModel().getNumPages();
		}

		BenchmarkModel model = Activator.modelControl.getModel();
		BenchmarkDocument document = model.getCurrentDocument();
		if (document != null) {
			if (group1 != null) {
				group1.setEnabled(true);
			}
			if (group2 != null) {
				group2.setEnabled(true);
			}
			if (filterGroup != null) {
				filterGroup.setEnabled(true);
				toggleFilterAllButton.setEnabled(true);
				toggleFilterCombo1Button.setEnabled(true);
				toggleFilterCombo2Button.setEnabled(true);
			}
			if (group3 != null) {
				group3.setEnabled(true);
			}
			if (group4 != null) {
				group4.setEnabled(true);
			}
			if (document.getFormat().equals(DocumentFormat.PDF)) {
				if (showPageNums != null) {
					showPageNums.setEnabled(true);
					parent.getDisplay().syncExec(new Runnable() {
						public void run() {
							showPageNums.setText((currentPage) + "/"
									+ (maxPage));
							showPageNums.redraw();
						}
					});
				}

				prevPageButton.setEnabled(currentPage > 1);
				nextPageButton.setEnabled(currentPage < maxPage);
			}

			if (scale != null) {
				scale.setEnabled(true);
			}

			if (zoomScale != null) {
				zoomScale.setEnabled(true);
			}

			if (fitHeightButton != null) {
				fitHeightButton.setEnabled(true);
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
			if (toggleShowBgButton != null
					&& page2ImageMap.get(currentPage) != null) {
				toggleShowBgButton.setEnabled(true);
			}

			if (nextPageAction != null) {
				nextPageAction.setEnabled(currentPage != maxPage);
			}
			if (prevPageAction != null) {
				prevPageAction.setEnabled(currentPage != 1);
			}

			if (toggleShowPdfButton != null) {
				// toggleShowPdfButton.setEnabled(true);
			}
			if (toggleShowGTButton != null) {
				toggleShowGTButton.setEnabled(true);
			}
			if (toggleShowNodesButton != null) {
				toggleShowNodesButton.setEnabled(true);
			}
			if (toggleShowAnnotationsButton != null) {
				toggleShowAnnotationsButton.setEnabled(true);
			}
			if (toggleShowDocMatrix != null) {
				toggleShowDocMatrix.setEnabled(true);
			}
			if (toggleShowEdgesButton != null) {
				toggleShowEdgesButton.setEnabled(true);
			}

			if (document instanceof PdfBenchmarkDocument)
			{
				PdfBenchmarkDocument bdoc = (PdfBenchmarkDocument) document;
//				enableAdjustmentControl(bdoc.getGroundTruth().size() > 0);

				Pair<Double,Double>  pureComp = 
					DiademBenchmarkEngine.calcPurityCompleteness(bdoc, currentPage, adj);
				double totalPurity = pureComp.getFirst();
				double totalCompleteness = pureComp.getSecond();

				if (totalPurity>=0)
				{
					purityValue.setText(""+totalPurity+"%");
				}
				else 
				{
					purityValue.setText("--");
				}
				if (totalCompleteness>=0)
				{
					completenessValue.setText(""+totalCompleteness+"%");
				}
				else
				{
					completenessValue.setText("--");
				}
			}
		}
		parent.redraw();
		parent.update();
	}

	/**
	 * What to do when somebody changes the current document.
	 * 
	 * @param ev
	 */
	@Override
	public void documentUpdated(DocumentUpdateEvent ev)
	{
		DocumentUpdate update = ev.getDocumentUpdate();
		DocumentModel model = update.getUpdate();
		BenchmarkDocument document = (BenchmarkDocument) model.getDocument();

		if (inFile == null) {
			inFile = document.getUri();
		}

		if (update.getProvider() != this) 
		{	
			if (ev.getDocumentUpdate().getType() == UpdateType.DOCUMENT_CHANGE)
			{
				//redraw
				
				if (hGraph==null)
				{
					if (document instanceof HTMLBenchmarkDocument)
					{
						hGraph = ((HTMLBenchmarkDocument) document).getDocumentGraph();
					}
					
				}
				canvas.plot(true);
				canvas.calibrateGraphPosition();
			}
			else if (ev.getDocumentUpdate().getType() == UpdateType.NEW_DOCUMENT) 
			{
				page2hGraphMap.clear(); //clear cache on new document

				currentPage = model.getPageNum();
				maxPage = model.getNumPages();
				ISegmentGraph dg = page2hGraphMap.get(currentPage);
				if (dg == null) 
				{
					if (!document.getUri().endsWith(".wrapper")) {
						processDocument(document);
					} else {
						canvas.setDocumentGraph(null);
					}
				} 
				else 
				{
					canvas.setDocumentGraph(dg);
					canvas.plot(true);
					canvas.calibrateGraphPosition();
				}

				if (document instanceof PdfBenchmarkDocument)
				{
					PdfBenchmarkDocument bdoc = (PdfBenchmarkDocument) document;
					Pair<Double,Double>  pureComp = 
						DiademBenchmarkEngine.calcPurityCompleteness(bdoc, model.getPageNum(), null);
					double totalPurity = pureComp.getFirst();
					double totalCompleteness = pureComp.getSecond();

					if (totalPurity>=0)
					{
						purityValue.setText(""+totalPurity+"%");
					}
					else 
					{
						purityValue.setText("--");
					}
					if (totalCompleteness>=0)
					{
						completenessValue.setText(""+totalCompleteness+"%");
					}
					else
					{
						completenessValue.setText("--");
					}

				}	

				updateActionEnablement();
			} 
			else if (ev.getDocumentUpdate().getType() == UpdateType.PAGE_CHANGE) 
			{
				currentPage = model.getPageNum();
				ISegmentGraph g = page2hGraphMap.get(currentPage);
				if (g==null)
				{
					// set cursor to busy...
					DocWrapUIUtils.getWrapperEditor().showBusy(true);
					processPdf(inFile, currentPage);
					DocWrapUIUtils.getWrapperEditor().showBusy(false);
				} 
				else 
				{
					hGraph = g;
				}

				if (canvas != null) {
					canvas.clearAllHighlights();
					canvas.setDocumentGraph(hGraph);
					canvas.plot(true);
					canvas.calibrateGraphPosition();
				}
			} 
			else if (ev.getDocumentUpdate().getType() == UpdateType.GRAPH_CHANGE) 
			{
				IGraph<?, ?> dg = model.getDocumentGraph();
				if (dg instanceof ISegmentGraph) {
					canvas.setDocumentGraph((ISegmentGraph) dg);
					canvas.plot(true);
					canvas.calibrateGraphPosition();
				}
			}
			
			//update graph
			if (canvas!=null)
			{
				canvas.setInput(hGraph, DocumentController.docModel.getBounds());
				if (hGraph!=null)
				{
//					canvas.clearHighlightNodes();
//					canvas.clearMatchedNodes();
					canvas.clearAllHighlights();

					canvas.plot(true);
					canvas.calibrateGraphPosition();
				}
			}
		}

		updateActionEnablement();

		// lookup new document in benchmark model
//		BenchmarkDocument currentDoc = document;
//		enableAdjustmentControl(currentDoc.getGroundTruth().size() > 0);
	}

	public ISegmentGraph getGraph() {
		return hGraph;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (event.getSource() instanceof PDFViewerSWT) 
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			org.eclipse.swt.graphics.Rectangle rect = 
					(org.eclipse.swt.graphics.Rectangle) ssel.getFirstElement();

			double scale = DocumentController.docModel.getScale();
			int offset = (int) (DocumentController.docModel.getY());

			/* convert to graph */
			int x = (int) ((rect.x / scale/*- 190)* * scale / zoom*/));
			int y = (int) ((rect.y / scale/*- 60)* scale / zoom*/)) - offset;
			int w = (int) (rect.width / scale/* scale/zoom */);
			int h = (int) (rect.height / scale/* scale/zoom */);
			Rectangle r = new Rectangle();
			r.setBounds(x, y, w, h);

			ISegmentGraph base = null;
			if (hGraph instanceof ISegHierGraph) {
				base = ((ISegHierGraph) hGraph).getBaseGraph();
			} else {
				base = hGraph;
			}
			ISegmentGraph sub = DocGraphUtils
			.getDocumentSubgraphUnderRectangle(base, r);

			canvas.calibrateGraphPosition();

			ssel = new StructuredSelection(sub);
			setSelection(ssel);
		}
	}

	/**
	 * Get the active level graph.
	 * 
	 * @return
	 */
	public ISegmentGraph getActiveGraph() {
		if (!(hGraph instanceof SegLevelGraph)) {
			return null;
		}
		SegLevelGraph stack = (SegLevelGraph) hGraph;
		IGraphLevel<DocNode, DocEdge> level = stack.getLevel(activeLevel);
		if (level != null) {
			IGraphLevel<?, ?> lv = stack.getLevel(activeLevel);
			if (lv != null) {
				return (ISegmentGraph) lv.getGraph();
			}
		}
		return null;
	}

	// private void hookGlobalActions() {
	// IActionBars bars = getEditorSite().getActionBars();
	// bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
	// selectAllAction);
	// bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
	// toggleNodesAction);
	// // viewer.getControl().addKeyListener(new KeyAdapter() {
	// // public void keyPressed(KeyEvent event) {
	// // if (event.character == SWT.DEL &&
	// // event.stateMask == 0 &&
	// // toggleNodesAction.isEnabled())
	// // {
	// // toggleNodesAction.run();
	// // }
	// // }
	// // });
	// }

	private void restoreState() {
		if (memento == null)
			return;
		memento = memento.getChild("selection");
		if (memento != null) {
			IMemento descriptors[] = memento.getChildren("descriptor");
			if (descriptors.length > 0) {
				// ArrayList<IMemento> objList = new
				// ArrayList<IMemento>(descriptors.length);
				// for (int nX = 0; nX < descriptors.length; nX ++) {
				// String id = descriptors[nX].getID();
				// Word word = input.find(id);
				// if (word != null)
				// objList.add(word);
				// }
				// viewer.setSelection(new StructuredSelection(objList));
			}
		}
		memento = null;
		updateActionEnablement();
	}

	public void saveState(IMemento memento) {
		// IStructuredSelection sel =
		// (IStructuredSelection)viewer.getSelection();
		// if (sel.isEmpty())
		// return;
		memento = memento.createChild("selection");
		// Iterator iter = sel.iterator();
		// while (iter.hasNext()) {
		// Word word = (Word)iter.next();
		// memento.createChild("descriptor", word.toString());
		// }
	}

	/**
	 * Set the status line.
	 * 
	 * @param message
	 */
	private void setStatusLine(String message) {
		// Get the status line and set the text
		IActionBars bars = getEditorSite().getActionBars();
		bars.getStatusLineManager().setMessage(message);
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
			
//			canvas.calibrateGraphPosition();
		}
	}

	public boolean isInEvaluateMode() {
		return isEvaluateMode;
	}

	public void toogleEvaluateMode() {
		this.isEvaluateMode = !isEvaluateMode;
	}

	public void setWrapperEditor(AnnotatorEditor we) {
		this.we = we;
	}

	public String getInFile() {
		return inFile;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	/************************************************************************************
	 ************************************************************************************ 
	 * 
	 * Editor functionality
	 * 
	 ************************************************************************************ 
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		we.doSave(new NullProgressMonitor());
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isDirty() {
		// return we.isDirty();
		return false;
	}

	/************************************************************************************
	 ************************************************************************************ 
	 * 
	 * Listeners
	 * 
	 ************************************************************************************ 
	 * 
	 * ResizeListener
	 */
	private class ResizeListener implements Listener {
		public void handleEvent(Event e) {
			// canvas.calibrateGraphCenter(true);
			// fitHeight();
		}

	}

	/**
	 * PlotKeyListener
	 * 
	 */
	public class PlotKeyListener implements KeyListener {

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {

			// digits control level
			if (e.keyCode >= 48 && e.keyCode <= 57) {
				switch (e.keyCode) {
				case 48:
					changeActiveLevel(0);
					break;

				case 49:
					changeActiveLevel(1);
					break;

				case 50:
					changeActiveLevel(2);
					break;

				case 51:
					changeActiveLevel(3);
					break;

				default:
					break;
				}
			}
			if (e.keyCode == 16777218) {
				// canvas.setScale(canvas.getScale()+0.1d);
				Panel contents = new Panel();
				parent.getClientArea();
				contents.setLayoutManager(new XYLayout());
				canvas.drawActiveGraph(true);
				parent.layout();

			} else if (e.keyCode == 16777217) {
				// canvas.setScale(canvas.getScale()-0.1d);
				Panel contents = new Panel();
				parent.getClientArea();
				contents.setLayoutManager(new XYLayout());

				canvas.drawActiveGraph(true);
				parent.layout();
			}
		}
	}

	/**
	 * 
	 * A Mouse and Paint Listener for the plotter.
	 */
	public class PlotMouseListener implements MouseListener, MouseMoveListener,
	PaintListener, IPlotMouseListener, MouseWheelListener {

		IFigure lastFig = null;
		Color lastFgColor;
		Color lastBgColor;
		org.eclipse.draw2d.Label lastLabel;
		DocWrapEditor parent;

		/**
		 * Constructor.
		 */
		public PlotMouseListener(DocWrapEditor parent) {
			this.parent = parent;
		}

		private Rectangle focusRectangle = new Rectangle();
		private boolean dragOn = false;
		int mouseX = 0;
		int mouseY = 0;

		@Override
		public void mouseUp(MouseEvent e) {
			if (e.button == 1) {
				dragOn = false;

				if (focusRectangle.width > 0 && focusRectangle.height > 0) {
					ISegmentGraph dg = null;
					switch (documentRepresentation) {
					case DocWrapConstants.DOC_REP_PLAIN:
						dg = hGraph;
						break;
					case DocWrapConstants.DOC_REP_SEG_LEVEL:
						if (hGraph instanceof SegLevelGraph) {
							dg = ((SegLevelGraph) hGraph).getBaseGraph();
						}
						break;
					case DocWrapConstants.DOC_REP_HIER:
						if (hGraph instanceof DocHierGraph) {
							dg = ((DocHierGraph) hGraph).getBaseGraph();
						}
						break;
					}

					if (dg == null) {
						dg = hGraph;
					}
					// Houston, we have a selection, lets notify pattern view...
					double scale = canvas.getScale();

					java.awt.Rectangle rect = new java.awt.Rectangle();
					focusRectangle.setBounds(
							Math.max(0,focusRectangle.x - canvas.getxTrans()),
							Math.max(0, focusRectangle.y - canvas.getyTrans()),
							focusRectangle.width, focusRectangle.height);

					int x = (int) (focusRectangle.x / scale);
					int y = (int) (((focusRectangle.y ) / scale));
					int w = (int) (focusRectangle.width / scale);
					int h = (int) (focusRectangle.height / scale);
					rect.setBounds(x, y, w, h);

					rect = DocGraphUtils.yFlipRectangle(rect, dg);

					ISegmentGraph sub = DocGraphUtils
					.getDocumentSubgraphUnderRectangle(dg, rect);
					ISegmentGraph sel = hGraph.getSubGraph(sub.getNodes());

					graphSelection = sel;
					if (graphSelection == null) {
						return;
					}

					// notify listeners
					org.eclipse.swt.graphics.Rectangle rectangle = new org.eclipse.swt.graphics.Rectangle(
							rect.x, rect.y, rect.width, rect.height);
					ISelection selection = new StructuredSelection(rectangle);
					setSelection(selection);
					
					canvas.calibrateGraphPosition();
				}
			}
		}

		@Override
		public void mouseDown(MouseEvent e) {
			focusRectangle = new Rectangle();
			focusRectangle.setLocation(e.x, e.y);
			if (e.button == 1) {
				mouseX = e.x;
				mouseY = e.y;
				dragOn = true;
			}
			canvas.redraw();
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseMove(MouseEvent e) {
			if (canvas == null || hGraph == null) {
				return;
			}

			// show x/y position in status bar

			DocumentModel model = we.documentControl.getDocModel();

			double scale = canvas.getScale();//DocumentController.docModel.getScale();
			int x = new Integer((int) ((e.x - canvas.getxTrans()) / scale)) ;//;
			int y = new Integer((int) ((e.y - canvas.getyTrans() - canvas.yOff) / scale)) ;

			Rectangle bounds = model.getBounds();
			if (bounds==null)
			{
				return;
			}
			int maxY = (int) model.getBounds().getMaxY();

			xCoordValue.setText(x+"");
			yCoordValue.setText(maxY - y+"");

			group1.update();

			if (dragOn) {
				if (isDragActivated) {
					canvas.translateContents2(-1 * (mouseX - e.x), -1
							* (mouseY - e.y));
				} else {
					focusRectangle.setSize((-1) * (focusRectangle.x - e.x),
							(-1) * (focusRectangle.y - e.y));
				}

				mouseX = e.x;
				mouseY = e.y;
				canvas.redraw();
			} else {
				Point point = new Point(e.x, e.y);

				IFigure fig = canvas.getFigureUnderPoint(point);
				if (fig != null
						&& !fig.getBackgroundColor().equals(
								ColorConstants.white)) {
					if (lastFig != null) {
						lastFig.setForegroundColor(lastFgColor);
						lastFig.setBackgroundColor(lastBgColor);
					}
					lastFig = fig;
					lastFgColor = fig.getForegroundColor();
					lastBgColor = fig.getBackgroundColor();
					fig.setForegroundColor(org.eclipse.draw2d.ColorConstants.darkBlue);
					fig.setBackgroundColor(org.eclipse.draw2d.ColorConstants.lightBlue);

					DocNode docNode = canvas.getDocNode2Figure()
					.reverseGet(fig);
					if (docNode != null) {
						org.eclipse.draw2d.Label text = new org.eclipse.draw2d.Label();
						text.setBounds(new org.eclipse.draw2d.geometry.Rectangle(
								0, 0, 400, 100));
						text.setForegroundColor(ColorConstants.black);
						text.setText(docNode.getLabel());
						lastLabel = text;
						fig.add(text);
					}
				} else if (lastFig != null) {
					lastFig.setForegroundColor(lastFgColor);
					lastFig.setBackgroundColor(lastBgColor);
					// if (lastLabel!=null &&
					// lastFig.getChildren().contains(lastFig)) {
					// lastFig.remove(lastLabel);
					// }
					lastFig = null;
					canvas.removeFigure(lastLabel);
				}
			}
		}

		public void paintControl(PaintEvent e) {
			if (dragOn) {
				// e.gc.setAlpha(128);
				// e.gc.setAntialias(SWT.ON);
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
				// e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_DARK_GREEN));
				// System.out.println("Dims: "+focusRectangle.x+","+focusRectangle.y+","+focusRectangle.width+","+focusRectangle.height);
				e.gc.drawFocus(focusRectangle.x, focusRectangle.y,
						focusRectangle.width, focusRectangle.height);
			} else {

			}
		}

		public Rectangle getFocusRectangle() {
			return focusRectangle;
		}

		@Override
		public void mouseScrolled(MouseEvent e) {
			double scale = DocumentController.docModel.getScale()
			+ (new Double(e.count) / 30);
			canvas.calibrateGraphPosition(scale);
			// fitHeight();

			/* notify document listeners */
			DocumentUpdate update = new DocumentUpdate();
			update.setType(UpdateType.RESIZE);
			DocumentModel model = new DocumentModel();
			model.setScale(scale);
			update.setUpdate(model);

			update.setProvider(canvas);
			we.setDocumentUpdated(update);
		}
	}

	/*****************************************************************************************
	 * 
	 * 
	 * Provide selection provider interface
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
//		if (!listeners.contains(listener)) {
//			listeners.add(listener);
//		}
		we.selectionControl.addSelectionChangedListener(listener);
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
		we.selectionControl.setSelection(selection);
	}

//	/**
//	 * 
//	 */
//	private void enableAdjustmentControl(boolean enable)
//	{
//		adjGroup.setEnabled(enable);
//		denXLabel.setEnabled(enable);
//		denXText.setEnabled(enable);
//		nomXLabel.setEnabled(enable);
//		nomXText.setEnabled(enable);
//		scaleXLabel.setEnabled(enable);
//		denYLabel.setEnabled(enable);
//		denYText.setEnabled(enable);
//		nomYLabel.setEnabled(enable);
//		nomYText.setEnabled(enable);
//		scaleYLabel.setEnabled(enable);
//		offsetLabel.setEnabled(enable);
//		xOffLabel.setEnabled(enable);
//		yOffLabel.setEnabled(enable);
//		xOffText.setEnabled(enable);
//		yOffText.setEnabled(enable);
//		xOffMinusButton.setEnabled(enable);
//		xOffPlusButton.setEnabled(enable);
//		yOffMinusButton.setEnabled(enable);
//		yOffPlusButton.setEnabled(enable);
//		scaleButton.setEnabled(enable);
//		previewScaleButton.setEnabled(enable);
//		topAdj.setEnabled(enable);
//		botAdj.setEnabled(enable);
//
//		toggleShowGTButton.setEnabled(enable);
//	}

	//	/**
	//	 * 
	//	 * @param parent
	//	 * @param clientArea
	//	 * @return
	//	 */
	//	private Scale createZoomScale(Composite parent,
	//			org.eclipse.swt.graphics.Rectangle clientArea) {
	//		zoomScale = new Scale(parent, SWT.HORIZONTAL | SWT.BORDER);
	//		zoomScale.setBounds(clientArea.x, clientArea.y, 175, 40);
	//		zoomScale.setEnabled(false);
	//		zoomScale.setMinimum(5);
	//		zoomScale.setMaximum(55);
	//		zoomScale.setSelection(20);
	//		zoomScale.setPageIncrement(50);
	//		zoomScale.setIncrement(10);
	//
	//		GridData data3 = new GridData();
	//		data3.horizontalSpan = 5;
	//		data3.grabExcessHorizontalSpace = true;
	//		data3.heightHint = 20;
	//		data3.widthHint = 160;
	//		zoomScale.setLayoutData(data3);
	//
	//		Composite labelBar = new Composite(parent, SWT.NONE);
	//		labelBar.setLayoutData(data3);
	//
	//		GridData data4 = new GridData();
	//		data4.horizontalAlignment = SWT.CENTER;
	//		data4.verticalAlignment = SWT.TOP;
	//		data4.heightHint = 20;
	//		FontData fontData = new FontData("Arial", 6, SWT.NONE);
	//		Font font = new Font(getSite().getShell().getDisplay(), fontData);
	//		final Display display = Display.getCurrent();
	//		final Color gray = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	//		final Color black = display.getSystemColor(SWT.COLOR_BLACK);
	//
	//		final Text t1 = new Text(labelBar, SWT.NONE);
	//		t1.setBackground(gray);
	//		t1.setText("30%");
	//		t1.setFont(font);
	//		t1.setEditable(false);
	//		t1.setDoubleClickEnabled(false);
	//		t1.setLayoutData(data4);
	//
	//		final Text t2 = new Text(labelBar, SWT.NONE);
	//		t2.setBackground(gray);
	//		t2.setText("50%");
	//		t2.setFont(font);
	//		t2.setEditable(false);
	//		t2.setDoubleClickEnabled(false);
	//		t2.setLayoutData(data4);
	//
	//		final Text t3 = new Text(labelBar, SWT.NONE);
	//		t3.setBackground(gray);
	//		t3.setText("75%");
	//		t3.setFont(font);
	//		t3.setEditable(false);
	//		t3.setDoubleClickEnabled(false);
	//		t3.setLayoutData(data4);
	//
	//		final Text t4 = new Text(labelBar, SWT.NONE);
	//		t4.setBackground(gray);
	//		t4.setText("100%");
	//		t4.setFont(font);
	//		t4.setEditable(false);
	//		t4.setDoubleClickEnabled(false);
	//		t4.setLayoutData(data4);
	//
	//		final Text t5 = new Text(labelBar, SWT.NONE);
	//		t5.setBackground(gray);
	//		t5.setText("150%");
	//		t5.setFont(font);
	//		t5.setEditable(false);
	//		t5.setDoubleClickEnabled(false);
	//		t5.setLayoutData(data4);
	//		zoomScale.addListener(SWT.Selection, new Listener() {
	//			@Override
	//			public void handleEvent(Event event) {
	//				switch (zoomScale.getSelection()) {
	//
	//				case 10:
	//
	//					t1.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
	//					t2.setForeground(black);
	//					t3.setForeground(black);
	//					t4.setForeground(black);
	//					t5.setForeground(black);
	//					// canvas.setScale(0.3d);
	//					canvas.calibrateGraphPosition(0.5d);
	//					break;
	//
	//				case 20:
	//
	//					t2.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
	//					t1.setForeground(black);
	//					t3.setForeground(black);
	//					t4.setForeground(black);
	//					t5.setForeground(black);
	//					// canvas.setScale(0.5d);
	//					canvas.calibrateGraphPosition(0.75d);
	//					break;
	//
	//				case 30:
	//					t3.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
	//					t1.setForeground(black);
	//					t2.setForeground(black);
	//					t4.setForeground(black);
	//					t5.setForeground(black);
	//					// canvas.setScale(0.75d);
	//					canvas.calibrateGraphPosition(1d);
	//					break;
	//
	//				case 40:
	//
	//					t4.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
	//					t1.setForeground(black);
	//					t2.setForeground(black);
	//					t3.setForeground(black);
	//					t5.setForeground(black);
	//					// canvas.setScale(1d);
	//					canvas.calibrateGraphPosition(1.5d);
	//					break;
	//
	//				case 50:
	//					t5.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
	//					t1.setForeground(black);
	//					t2.setForeground(black);
	//					t3.setForeground(black);
	//					t4.setForeground(black);
	//					// canvas.setScale(1.5d);
	//					canvas.calibrateGraphPosition(2d);
	//					break;
	//
	//				default:
	//					break;
	//				}
	//			}
	//		});
	//
	//		return zoomScale;
	//	}

	@Override
	public void modelChanged(ModelChangedEvent event) 
	{
		BenchmarkDocument doc = event.getModel().getCurrentDocument();
		if (doc==null)
		{
			canvas.setDocumentGraph(null);
			canvas.plot(true);
			canvas.calibrateGraphPosition();
		}
	}

	public void refresh()
	{
		canvas.calibrateGraphPosition();

		updateActionEnablement();
	}

	public void highlightBox (Rectangle rect) 
	{
		this.canvas.highlightBox(rect);
		this.canvas.calibrateGraphPosition();
	}

}// GraphView
