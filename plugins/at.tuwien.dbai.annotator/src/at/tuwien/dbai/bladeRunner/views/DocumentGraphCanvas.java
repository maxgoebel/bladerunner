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
package at.tuwien.dbai.bladeRunner.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapHighlighter;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor.PlotMouseListener;
import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.model.document.RectangleAdjustment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.hier.level.IGraphLevel;
import at.tuwien.prip.model.graph.hier.level.SegLevelGraph;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SinglePageSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.utils.DocGraphUtils;
import at.tuwien.prip.model.utils.Orientation;

/**
 * DocumentGraphPlotter.java
 * 
 * Handles the drawing of a graph.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 21, 2011
 */
public class DocumentGraphCanvas extends FigureCanvas {
	private Composite parent;

	// the data models
	private ISegmentGraph graph = null;

	/* translation and scaling */
	private int xTrans = 0, yTrans = 0;
	private double scale = 0.75d;
	private boolean useInternalScale = true;

	/* color defaults */
	// private Color highlightColor = ColorConstants.green;
	private Color nodeColor = ColorConstants.darkGray; // default
	private Color resultColor = new Color(getDisplay(),new RGB(255,225,0));
	private Color groundTruthColor = ColorConstants.red; // default
	private Color highlightColor = ColorConstants.red; // default
	private int highlightBorderWidth = 4;
	
	/* selections for highlighting */
	private java.awt.Rectangle highlight = null;
	
	// controls
	private boolean showNodes = true;
	private boolean showEdges = false;
	private boolean showAnnotations = true;
	private boolean showDocumentBg = false;
	private boolean showDocMatrix = false;
	private boolean showGT = false;
	private boolean showHighlight = false;
	
	//filters
	private String filter1;
	private String filter2;
	
	private ImageFigure pdfThumbFigure = null;
	private Image bgThumb = null;

	//	private java.awt.Rectangle focusRectangle = new java.awt.Rectangle(0, 0, 0,
	//			0);
	//	private java.awt.Rectangle highlightRectangle = new java.awt.Rectangle(0,0,0,0);

	private BidiMap<DocNode, IFigure> docNode2Figure;
	private BidiMap<DocEdge, IFigure> docEdge2Figure;

	private final LayeredPane rootPane;
	private ScrollPane scrollPane;

	private DocWrapHighlighter highlighter;

	private RectangleAdjustment annoAdj = new RectangleAdjustment(0, 0, 1, 1);

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param swt
	 * @param nodeColor
	 * @param dim
	 */
	public DocumentGraphCanvas(final Composite parent, int swt,
			Color nodeColor, Dimension dim) {
		super(parent, swt);

		this.parent = parent;
		this.nodeColor = nodeColor;

		this.docNode2Figure = new BidiMap<DocNode, IFigure>();
		this.docEdge2Figure = new BidiMap<DocEdge, IFigure>();

		this.rootPane = new LayeredPane();
		this.scrollPane = new ScrollPane();

		// dark gray canvas background
		scrollPane.setBackgroundColor(ColorConstants.darkGray);

		Layer layer1 = new Layer();
		layer1.add(scrollPane);
		this.rootPane.add(layer1);

		this.scrollPane.setLayoutManager(new XYLayout());
		this.setBackground(ColorConstants.white);
		this.setContents(this.scrollPane);

		/* add a highlighter */
		this.highlighter = new DocWrapHighlighter(this);
	}

	/**
	 * Plot this view's graph.
	 * 
	 * @param docDecorations
	 */
	public void plot(boolean docDecorations) {

		// if no place to plot, or no data to plot, return.
		if (null == parent
				|| DocumentController.docModel == null
				|| DocumentController.docModel.getFormat() == null
				|| DocumentController.docModel.getFormat() == DocumentFormat.PDF
				&& null == graph) {
//			return;
		}

		this.setContents(this.scrollPane);
		scrollPane.removeAll();

		if (docDecorations) {
			drawGraphAsDocument(scrollPane, graph);
		} else {
			drawGraph(scrollPane, graph);
		}
	}

//	/**
//	 * Deal with document updates.
//	 * 
//	 * @param ev
//	 */
//	public void documentUpdated(DocumentUpdateEvent ev) {
//		DocumentUpdate update = ev.getDocumentUpdate();
//
//		switch (update.getType()) {
//		case RESIZE: /* scaling */
//
//			calibrateGraphPosition();
//			this.redraw();
//
//			break;
//
//		case REPAN: /* translation */
//
//			// transform = (AffineTransform) update.getUpdate();
//			// double transX = transform.getTranslateX();
//			// double transY = transform.getTranslateY();
//			// translateContents2((int)transX, (int)transY);
//
//			break;
//
//		case NEW_DOCUMENT:
//
//			calibrateGraphCenter(true);
//			break;
//
//		case PAGE_CHANGE:
//
//			calibrateGraphCenter(true);
//			break;
//
//		default:
//
//			// fitHeight
//			break;
//		}
//
//	}

	public void setHighlightRectangle(java.awt.Rectangle highlightRectangle) {
		//		this.highlightRectangle = highlightRectangle;
	}

	/**
	 * Draw the currently active graph.
	 */
	public void drawActiveGraph(boolean page) {
		drawActiveGraph(scrollPane, page);
	}

	/**
	 * Draw the currently active graph.
	 * 
	 * @param contents
	 */
	private void drawActiveGraph(IFigure contents, boolean page) {
		clearContents();
		this.redraw();
		this.layout();

		if (page) {
			drawGraphAsDocument(contents, graph);
		} else {
			drawGraph(contents, graph);
		}
	}

	public double yOff = 0;

	/**
	 * Add document decorations.
	 * 
	 * @param contents
	 * @param graph
	 */
	private void drawGraphAsDocument(IFigure contents, ISegmentGraph graph)
	{
		// dark gray canvas background
		contents.setBackgroundColor(ColorConstants.darkGray);

		if (graph != null && graph instanceof DocumentGraph) {
			java.awt.Rectangle graphDims = graph.getDimensions();
			java.awt.Rectangle dim = docBounds;//DocumentController.docModel.getBounds();
			if (dim==null)
				return;
			
			double scale = 1d;
			if (useInternalScale) {
				scale = this.scale;
			} else {
				scale = DocumentController.docModel.getScale();
			}

			int w = (int) (scale * (dim.width));
			int h = (int) (scale * (dim.height));
			int x = (int) (scale * (dim.x ));
			int y = (int) (scale * (dim.y ));

			//			java.awt.Rectangle tmp = new java.awt.Rectangle(dim.x,dim.y,dim.width,dim.height);
			//			java.awt.Rectangle rect = DocGraphUtils.flipReverseRectangle(tmp, graph);
			if (w<h)
			{
				yOff = -1 * Math.abs(scale * (dim.height - graphDims.height - graphDims.y));//- graphDims.y
			}
			else
			{
				yOff = -20;
			}
			// draw a white page outline
			RectangleFigure bounds = new RectangleFigure();
			//			bounds.setLocation(new Point(rect.x, rect.y));
			//			bounds.setSize(rect.width, rect.height);
			bounds.setLocation(new PrecisionPoint(x, yOff));
			bounds.setSize(w, h);
			bounds.setBackgroundColor(ColorConstants.white);
			contents.add(bounds);

			// draw background image if available
			if (bgThumb != null && !bgThumb.isDisposed()) {
				int height_orig = bgThumb.getImageData().height;
				double scale_fact = new Double(h) / new Double(height_orig);
				int width_new = (int) (scale_fact * bgThumb.getImageData().width);

				Image thumb = new Image(parent.getDisplay(), bgThumb
						.getImageData().scaledTo(width_new, h));

				pdfThumbFigure = new ImageFigure();
				pdfThumbFigure.setImage(thumb);
				pdfThumbFigure.setLocation(new Point(x, y - 40));
				pdfThumbFigure.setSize(w, h);
				contents.add(pdfThumbFigure);

				if (!showDocumentBg) {
					pdfThumbFigure.setVisible(false);
				} else {
					pdfThumbFigure.setVisible(true);
				}
			}

			drawGraph(contents, graph);
			translateContents2(0, (int) (-1* yOff));
		}

	}

	/**
	 * Draw a graph.
	 * 
	 * @param contents
	 * @param graph
	 */
	private void drawGraph(IFigure contents, ISegmentGraph graph) 
	{
		this.xTrans = 0;
		this.yTrans = 0;

		if (graph == null) {
			return;
		}

		Collection<DocNode> nodes = graph.getNodes();
		for (DocNode node : nodes) {
			drawNode(contents, node);
		}

		AnnotatorEditor we = DocWrapUIUtils.getWrapperEditor();
		IDocument doc = we.getActiveDocument();
		int pageNum = we.getCurrentDocumentPageNum();
		BenchmarkDocument benchDoc = (BenchmarkDocument) doc;

		if (showGT) 
		{
			// mark the ground truth boxes
			for (Annotation annotation : benchDoc.getGroundTruth()) {
				// get all table regions on current page
				List<RegionSelection> pageRegions = new ArrayList<RegionSelection>();
				if (annotation instanceof TableAnnotation) {
					TableAnnotation tann = (TableAnnotation) annotation;
					for (TableSelection table : tann.getTables()) {
						for (AnnotationPage page : table.getPages()) {
							if (page.getPageNum() == pageNum) {
								for (AbstractSelection region : page.getItems()) {
									pageRegions.add((RegionSelection) region);
								}
							}
						}
					}
				}

				for (RegionSelection region : pageRegions)
				{
					java.awt.Rectangle bounds = region.getBounds();
					java.awt.Rectangle b = 
							DocGraphUtils.yFlipRectangle(bounds, graph);
					float x1 = (float) (b.x * 1);
					float x2 = (float) ((b.x + b.width) * 1);
					float y1 = (float) (b.y);
					float y2 = (float) (b.y + b.height);
					b = makeLarger(b);
					
					GenericSegment gs = new GenericSegment(x1, x2, y1, y2);
					DocNode node = new DocNode(gs);

					drawNode(contents, node, groundTruthColor, 100);
				}
			}
		}

		if (showAnnotations) 
		{			
			// mark the annotated boxes
			for (Annotation annotation : benchDoc.getAnnotations())
			{
				// get all table regions on current page
				List<RegionSelection> pageRegions = new ArrayList<RegionSelection>();
				if (annotation instanceof Annotation)
				{
					Annotation tann = (Annotation) annotation;
					for (AbstractSelection selection : tann.getItems()) 
					{
						if (filter1!=null && !selection.getType().equalsIgnoreCase(filter1))
						{
							continue; //skip
						}
						if (filter2!=null && !selection.getType().equalsIgnoreCase(filter2))
						{
							continue; //skip
						}
						if (selection instanceof RegionSelection)
						{
							RegionSelection region = (RegionSelection) selection;
							if (region.getPageNum() == pageNum)
							{
								pageRegions.add(region);
							}
						}
						else if (selection instanceof MultiPageSelection)
						{
							MultiPageSelection mpSelection = (MultiPageSelection) selection;
							for (AnnotationPage page : mpSelection.getPages()) 
							{
								if (page.getPageNum() == pageNum) 
								{
									for (AbstractSelection sel : page.getItems()) 
									{
										if (sel instanceof RegionSelection)
										{
											pageRegions.add((RegionSelection) sel);
										}
										else if (sel instanceof SinglePageSelection)
										{
											SinglePageSelection spsel = (SinglePageSelection) sel;
											for (AbstractSelection ss  : spsel.getItems())
											{
												if (ss instanceof RegionSelection)
												{
													pageRegions.add((RegionSelection) ss);
												}
											}
										}
									}
								}
							}
						}
						else if (selection instanceof SinglePageSelection)
						{
							SinglePageSelection spSel = (SinglePageSelection) selection;
							for (AbstractSelection sel : spSel.getItems()) 
							{
								if (sel instanceof RegionSelection)
								{
									RegionSelection region = (RegionSelection) sel;
									if (region.getPageNum() == pageNum)
									{
										pageRegions.add(region);
									}
								}
								else if (sel instanceof SinglePageSelection)
								{
									SinglePageSelection spsel = (SinglePageSelection) sel;
									for (AbstractSelection ss  : spsel.getItems())
									{
										if (ss instanceof RegionSelection)
										{
											RegionSelection region = (RegionSelection) ss;
											if (region.getPageNum() == pageNum)
											{
												pageRegions.add(region);
											}
										}
									}
								}
							}
						}
					}

					for (AnnotationPage page : tann.getPages()) 
					{
						if (page.getPageNum() == pageNum) {
							for (AbstractSelection region : page.getItems()) {
								pageRegions.add((RegionSelection) region);
							}
						}
					}
				}

				for (RegionSelection region : pageRegions) 
				{
					java.awt.Rectangle bounds = region.getBounds();
					java.awt.Rectangle b = 
							DocGraphUtils.yFlipRectangle(bounds, graph);

					java.awt.Rectangle rectangle = 
							DocGraphUtils.applyRectangleAdjustment(b, annoAdj, graph);

					rectangle = makeLarger(rectangle);
					GenericSegment gs = new GenericSegment(rectangle);
					DocNode node = new DocNode(gs);
					drawNode(contents, node, resultColor, 100);
				}
			}
		}

		//draw this last to be on top of all
		if (showHighlight && showAnnotations)
		{
			if (highlight!=null)
			{
				int alpha = 100;

				java.awt.Rectangle b = 
						DocGraphUtils.yFlipRectangle(highlight, graph);

				b = makeLarger(b);
				GenericSegment gs = new GenericSegment(b);
				DocNode node = new DocNode(gs);
				drawBorder(contents, node, highlightColor, highlightBorderWidth, alpha);
			}
		}
		
		Collection<DocEdge> edges = graph.getEdges();
		for (DocEdge edge : edges) {
			if (nodes.contains(edge.getTo())) {
				drawEdgeFull(contents, edge);
			}
		}
	}

	private java.awt.Rectangle makeLarger(java.awt.Rectangle in)
	{
		return new java.awt.Rectangle(in.x, in.y-2, in.width + 2, in.height+2);
	}
	/**
	 * 
	 * @param rectangle
	 */
	public void highlightBox (final java.awt.Rectangle rectangle) 
	{
		setHighlight(rectangle);
		showHighlight = true;
		calibrateGraphPosition();
	}
	
	/**
	 * Currently not used...
	 */
	public void adjustGraph() {
		if (this != null && this.getContents() != null
				&& this.getContents().getChildren() != null) {
			@SuppressWarnings("unchecked")
			List<IFigure> figs = this.getContents().getChildren();
			for (IFigure fig : figs) {
				fig.translate(xTrans, yTrans);
			}
		}
	}

	//	public void setFocusRectangle(java.awt.Rectangle focusRectangle) {
	//		this.focusRectangle = focusRectangle;
	//	}

	/**
	 * Translate all contents by X, Y.
	 * 
	 * @param x
	 * @param y
	 */
	@SuppressWarnings("unchecked")
	public void translateContents(int x, int y) {
		if (this != null && this.getContents() != null
				&& this.getContents().getChildren() != null) {
			List<IFigure> figs = this.getContents().getChildren();
			for (IFigure fig : figs) {
				fig.translate(xTrans, yTrans);
			}
		}
		this.redraw();
	}

	/**
	 * Translate all contents by X, Y.
	 * 
	 * @param x
	 * @param y
	 */
	public void translateContents2(int x, int y) {
		this.xTrans += x;
		this.yTrans += y;

		if (this != null && this.scrollPane != null
				&& this.scrollPane.getChildren() != null) {
			@SuppressWarnings("unchecked")
			List<IFigure> figs = this.scrollPane.getChildren();
			for (IFigure fig : figs) {
				fig.translate(x, y);
			}
		}
		this.redraw();
	}

	/**
	 * Clear the contents.
	 * 
	 * @param canvas
	 */
	public void clearContents() {
		if (this.getContents() != null
				&& this.getContents().getChildren() != null) {
			this.getContents().getChildren().clear();
		}
	}

	/**
	 * 
	 * @return
	 */
	public BidiMap<DocNode, IFigure> getDocNode2Figure() {
		return docNode2Figure;
	}

	public BidiMap<DocEdge, IFigure> getDocEdge2Figure() {
		return docEdge2Figure;
	}

	public IFigure getRootPane() {
		return scrollPane;
	}

	/**
	 * Toggle edges to be visible/invisible.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void toggleEdges() {
		showEdges = !showEdges;
		if (this.getContents() != null) {
			List<IFigure> figs = this.getContents().getChildren();
			for (IFigure fig : figs) {
				if (fig instanceof PolylineConnection) {
					if (fig.isVisible()) {
						fig.setVisible(false);
					} else {
						fig.setVisible(true);
					}
				}
			}
		}
	}

	/**
	 * Toggle edges to be visible/invisible.
	 * 
	 */
	public void toggleGT() {
		showGT = !showGT;
	}
	
	/**
	 * 
	 */
	public void toggleHighlight() {
		showHighlight = !showHighlight;
	}

	public void setHighlight(java.awt.Rectangle highlight) {
		this.highlight = highlight;
	}
	
	/**
	 * Toggle nodes to be visible/invisible.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void toggleNodes() {
		showNodes = !showNodes;
		if (this.getContents() != null) {
			List<IFigure> figs = this.getContents().getChildren();
			for (IFigure fig : figs) {
				if (fig instanceof RectangleFigure) {
					if (fig.getBackgroundColor() == ColorConstants.white) {
						continue;
					}
					if (fig.isVisible()) {
						fig.setVisible(false);
					} else {
						fig.setVisible(true);
					}
				}
			}
		}
	}

	/**
	 * Toggle document annotations to be visible/invisible.
	 * 
	 */
	public void toggleAnnotations() {
		showAnnotations = !showAnnotations;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void toggleDocMatrix() {
		showDocMatrix = !showDocMatrix;
		if (this.getContents() != null) {
			List<IFigure> figs = this.getContents().getChildren();
			for (IFigure fig : figs) {
				if (fig instanceof RectangleFigure) {
					if (fig.getBackgroundColor() == ColorConstants.white) {
						continue;
					}
					if (fig.isVisible()) {
						fig.setVisible(false);
					} else {
						fig.setVisible(true);
					}
				}
			}
		}
	}

	/**
	 * Draw a filled node.
	 * @param contents
	 * @param node
	 * @param color
	 * @param alpha
	 */
	private void drawNode(IFigure contents, DocNode node, Color color, int alpha) {
		RectangleFigure boxNode = docNode2Rectangle(node);
		boxNode.setAntialias(SWT.ON);
		if (docNode2Figure.containsKey(node)) {
			IFigure current = docNode2Figure.get(node);
			docNode2Figure.remove(node, current);
		}
		docNode2Figure.put(node, boxNode);

		boxNode.setFill(true);
		boxNode.setForegroundColor(color);
		boxNode.setBackgroundColor(color);
		boxNode.setAlpha(alpha);
		
		LineBorder border = new LineBorder(ColorConstants.black);
		border.setWidth(2);
		boxNode.setBorder(border);
		
		//		if (showNodes) {
		contents.add(boxNode);
		//		}
	}

	/**
	 * draw an empty border.
	 * @param contents
	 * @param node
	 * @param color
	 * @param alpha
	 */
	private void drawBorder(IFigure contents, DocNode node, Color color, int width, int alpha) 
	{
		RectangleFigure boxNode = docNode2Rectangle(node);
		boxNode.setAntialias(SWT.ON);
		if (docNode2Figure.containsKey(node)) {
			IFigure current = docNode2Figure.get(node);
			docNode2Figure.remove(node, current);
		}
		docNode2Figure.put(node, boxNode);

		LineBorder border = new LineBorder();
		border.setWidth(width);
		border.setColor(color);
		boxNode.setBorder(border);
		boxNode.setFill(false);
		boxNode.setForegroundColor(color);
		boxNode.setBackgroundColor(color);
		boxNode.setAlpha(alpha);

		contents.add(boxNode);
	}

	/**
	 * Draw a node.
	 * 
	 * @param contents
	 * @param node
	 */
	private void drawNode(IFigure contents, DocNode node) {
		RectangleFigure boxNode = docNode2Rectangle(node);
		boxNode.setAntialias(SWT.ON);
		if (docNode2Figure.containsKey(node)) {
			IFigure current = docNode2Figure.get(node);
			docNode2Figure.remove(node, current);
		}
		docNode2Figure.put(node, boxNode);

		if (node.getSegType().equals(SegmentType.Image)) 
		{
			boxNode.setFill(true);
			boxNode.setForegroundColor(ColorConstants.gray);
			boxNode.setBackgroundColor(ColorConstants.gray);
			boxNode.setAlpha(100);
		} 
		else if (showAnnotations && node.getSemanticAnnotations().size() > 0)
		{
			boxNode.setBackgroundColor(ColorConstants.yellow);
		}
		else if (node.getSegType().equals(SegmentType.Semantic))
		{
			boxNode.setBackgroundColor(ColorConstants.yellow);
		}
		else 
		{
			boxNode.setForegroundColor(ColorConstants.black);
			boxNode.setBackgroundColor(nodeColor);
		}

		if (showNodes) 
		{
			contents.add(boxNode);
		}
	}

	/**
	 * Clear all highlights.
	 */
	public void clearAllHighlights() {
		highlightBox(null);
	}

	public void setFilter1(String filter1) {
		this.filter1 = filter1;
	}
	
	public void setFilter2(String filter2) {
		this.filter2 = filter2;
	}
	
	/**
	 * Set the document graph to be drawn.
	 * 
	 * @param graph
	 */
	public void setDocumentGraph(ISegmentGraph graph) {
		clearContents();
		this.graph = graph;
	}

	/**
	 * Set the document graph to be drawn.
	 * 
	 * @param graph
	 * @param level
	 */
	public void setDocumentGraph(SegLevelGraph graph, int level) {
		clearContents();

		if (graph != null) {
			IGraphLevel<DocNode, DocEdge> segLevel = graph.getLevel(level);
			if (segLevel != null) {
				this.graph = (DocumentGraph) segLevel.getGraph();
			}
		}
	}

	private java.awt.Rectangle docBounds = null;
	
	/**
	 * Set the document graph to be drawn.
	 *
	 * @param graph
	 * @param docBounds
	 */
	public void setInput(ISegmentGraph graph, java.awt.Rectangle docBounds)
	{
		clearContents();
		this.docBounds = docBounds;
 		this.graph = graph;
	}
	
	/**
	 * Calibrate the graph to fit into the viewing frame.
	 * 
	 * @param scale
	 * @param x
	 * @param y
	 */
	public void calibrateGraphPosition() {

		Dimension dims = getViewport().getSize();
		int width = (int) (1 * dims.width);
		int height = (int) (1 * dims.height);

		java.awt.Rectangle bounds = DocumentController.docModel.getBounds();
		if (bounds==null)
		{
			return;
		}
		double bw = scale * bounds.width;
		double bh = scale * bounds.height;
		double bx = scale * bounds.x;
		double by = scale * bounds.y;

		double midDocX = (bx + (bw / 2));
		double midPageX = width / 2;
		double midDocY = (by + (bh / 2));
		double midPageY = height / 2;

		int xTrans = (int) (midPageX - midDocX);
		int yTrans = (int) (midPageY - midDocY);//+50;

		drawActiveGraph(false);
		translateContents2(xTrans, yTrans);
		//		parent.layout();
	}
	
	/**
	 * Calibrate the graph to fit into the viewing frame.
	 * 
	 * @param scale
	 * @param x
	 * @param y
	 */
	public void calibrateGraphPosition(double scale) {
		if (getDocumentGraph() == null) {
			return;
		}

		this.scale = scale;

		Dimension dims = getViewport().getSize();
		int width = (int) (1 * dims.width);
		int height = (int) (1 * dims.height);

		java.awt.Rectangle bounds = docBounds;
		if (bounds==null)
			return;
		
		double bw = scale * bounds.width;
		double bh = scale * bounds.height;
		double bx = scale * bounds.x;
		double by = scale * bounds.y;

		double midDocX = (bx + (bw / 2));
		double midPageX = width / 2;
		double midDocY = (by + (bh / 2));
		double midPageY = height / 2;

		int xTrans = (int) (midPageX - midDocX);
		int yTrans = (int) (midPageY - midDocY);// + 50;

		drawActiveGraph(true);
		translateContents2(xTrans, yTrans);
		parent.layout();
	}

	/**
	 * 
	 * @param remember
	 */
	public void calibrateGraphCenter(boolean remember) {
		xTrans = 0;
		yTrans = 0;

		clearContents();
		layout();
		redraw();

		Dimension dims = getViewport().getSize();
		int width = (int) (1 * dims.width);
		if (width == 0) {
			width = 400;
		}
		int height = (int) (1 * dims.height);

		ISegmentGraph g = getDocumentGraph();
		if (g == null) {
			return;
		}

		if (g instanceof DocumentGraph) {
			java.awt.Rectangle bounds = DocumentController.docModel.getBounds();
			if (bounds == null) {
				return;
			}
			double bw = scale * bounds.width;
			double bh = scale * bounds.height;
			double bx = scale * bounds.x;
			double by = scale * bounds.y;

			int xTrans = 0;
			int yTrans = 0;

			double midDocX = (bx + (bw / 2));
			double midPageX = width / 2;
			double midDocY = (by + (bh / 2));
			double midPageY = height / 2;

			xTrans = (int) (midPageX - midDocX);
			yTrans = (int) (midPageY - midDocY);

			drawActiveGraph(true);

			if (remember) {
				translateContents2(Math.max((int) -bx /*+ 30*/, xTrans),
						Math.max((int) -by /*+ 30*/, yTrans));
			} else {
				translateContents(xTrans, yTrans);//Math.max(30, yTrans));
			}
		}
	}

	/**
	 * Calibrate the position of the document graph to match the available
	 * space.
	 * 
	 * @param fitWidth
	 * @param fitHeight
	 * @param remember
	 */
	public void calibrateGraphPosition(boolean fitWidth, boolean fitHeight,
			boolean toOrigin, boolean remember) {
		xTrans = 0;
		yTrans = 0;

		clearContents();
		layout();
		redraw();

		Dimension dims = getViewport().getSize();

		int width = (int) (1 * dims.width);
		int height = (int) (1 * dims.height);

		ISegmentGraph g = getDocumentGraph();
		if (g == null) {
			return;
		}

		if (g instanceof DocumentGraph) {
			java.awt.Rectangle bounds = DocumentController.docModel.getBounds();

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
				yTrans = 0;//(int) 40;// (midPageY - midDocY);
			} else {
				scale = 1;
				// scale = Math.min(scaleWidth, scaleHeight);
				// double midDocX = scale * (bounds.x + (bounds.width/2));
				// double midPageX = width/2;
				// double midDocY = scale * ( bounds.y + (bounds.height/2) );
				// double midPageY = height/2;
				// xTrans = (int) (midPageX - midDocX);
				// yTrans = (int) (midPageY - midDocY);
			}

			/* translate graph to origin? */
			if (toOrigin) {
				xTrans -= bounds.x;
				yTrans -= bounds.y;
			}
			drawActiveGraph(true);

			if (remember) {
				translateContents2(Math.max(-bounds.x /*+ 30*/, xTrans),
						Math.max(-bounds.y /*+ 30*/, yTrans));
			} else {
				translateContents(xTrans, yTrans);//Math.max(30, yTrans));
			}
		}

		parent.layout();
	}

	/**
	 * Draw an edge.
	 * 
	 * @param contents
	 * @param edge
	 */
	protected void drawEdgeSimple(IFigure contents, DocEdge edge) {
		DocNode fromNode = edge.getFrom();
		DocNode toNode = edge.getTo();
		IFigure fromFig = docNode2Figure.get(fromNode);
		IFigure toFig = docNode2Figure.get(toNode);
		PolylineConnection wireFigure = new PolylineConnection();
		wireFigure.setAntialias(SWT.ON);
		wireFigure.setForegroundColor(ColorConstants.blue);

		// store edge
		if (docEdge2Figure.containsKey(edge)) {
			IFigure current = docEdge2Figure.get(edge);
			docEdge2Figure.remove(edge, current);
		}
		docEdge2Figure.put(edge, wireFigure);

		EllipseAnchor sourceAnchor = new EllipseAnchor(fromFig);
		EllipseAnchor targetAnchor = new EllipseAnchor(toFig);
		wireFigure.setSourceAnchor(sourceAnchor);
		wireFigure.setTargetAnchor(targetAnchor);
		if (showEdges) {
			contents.add(wireFigure);
		}
	}

	/**
	 * Draw an edge with additional information (confidence, weight, type).
	 * 
	 * @param contents
	 * @param edge
	 */
	private void drawEdgeFull(IFigure contents, DocEdge edge) {
		if (showEdges) {
			IFigure wireFigure = docEdge2LineFigure(edge);
			contents.add(wireFigure);
			// store edge
			if (docEdge2Figure.containsKey(edge)) {
				IFigure current = docEdge2Figure.get(edge);
				docEdge2Figure.remove(edge, current);
			}
			docEdge2Figure.put(edge, wireFigure);
		}
	}

	public void setBackground(Image img) {
		// this.bgThumb = img;
	}

	public void dispose() {
		docNode2Figure = new BidiMap<DocNode, IFigure>();
		parent.dispose();
	}

	/**
	 * Toggle the PDF background visible/invisible.
	 */
	public void toggleShowDocumentBG() {
		if (pdfThumbFigure != null) {
			if (pdfThumbFigure.isVisible()) {
				pdfThumbFigure.setVisible(false);
			} else {
				pdfThumbFigure.setVisible(true);
			}
		}
		this.showDocumentBg = !showDocumentBg;
	}

	public int getxTrans() {
		return xTrans;
	}

	public void setxTrans(int xTrans) {
		this.xTrans = xTrans;
	}

	public int getyTrans() {
		return yTrans;
	}

	public void setyTrans(int yTrans) {
		this.yTrans = yTrans;
	}

	@Override
	public boolean setFocus() {
		// calibrateGraphCenter(true);
		return super.setFocus();
	}

	public ISegmentGraph getDocumentGraph() {
		return graph;
	}

	@SuppressWarnings("unchecked")
	public IFigure getFigureUnderPoint(Point p) {
		if (this == null) {
			return null;
		}
		IFigure fig = this.getContents();
		if (fig == null) {
			return null;
		}
		List<IFigure> children = fig.getChildren();
		for (IFigure child : children) {
			if (child.containsPoint(p)) {
				return child;
			}
		}
		return null;
	}

	public boolean isShowDocumentBg() {
		return showDocumentBg;
	}

	public boolean removeFigure(IFigure fig) {
		return this.getContents().getChildren().remove(fig);
	}

	/**
	 * 
	 * @param ml
	 */
	public void addPlotMouseListener(IPlotMouseListener ml) {
		if (ml instanceof MouseListener) {
			this.addMouseListener((MouseListener) ml);
		}
		if (ml instanceof MouseMoveListener) {
			this.addMouseMoveListener((MouseMoveListener) ml);
		}
		if (ml instanceof PaintListener) {
			this.addPaintListener((PaintListener) ml);
		}
		if (ml instanceof MouseWheelListener) {
			this.addMouseWheelListener((MouseWheelListener) ml);
		}
	}

	public void addKeyListener(KeyListener kl) {
		// this.addKeyListener(kl);
	}

	public PlotMouseListener getPaintListener() {
		PlotMouseListener result = null;
		Listener[] listeners = this.getListeners(SWT.Paint);
		for (Listener listener : listeners) {
			if (listener instanceof PlotMouseListener) {
				result = (PlotMouseListener) listener;
				break;
			}
		}
		return result;
	}

	/**********************************************************************************
	 * Some Utilities.
	 */

	/**
	 * Convert a document node to a rectangle figure.
	 * 
	 * @param node
	 * @return
	 */
	private RectangleFigure docNode2Rectangle(DocNode node) {
		double scale = 1d;
		if (useInternalScale) {
			scale = this.scale;
		} else {
			scale = DocumentController.docModel.getScale();
		}

		Rectangle rectangle = new Rectangle();
		double width = node.getSegX2() - node.getSegX1();
		double height = node.getSegY2() - node.getSegY1();
		rectangle.width = (int) (scale * width);
		rectangle.height = (int) (scale * height);

		rectangle.x = (int) (scale * node.getSegX1());
		rectangle.y = (int) (scale * node.getSegY1());

		RectangleFigure result = new RectangleFigure();
		result.setBounds(rectangle);
		return result;
	}

	/**
	 * Convert a document edge to a graph figure.
	 * 
	 * @param edge
	 * @return
	 */
	private IFigure docEdge2LineFigure(DocEdge edge) {
		RectangleFigure figure = new RectangleFigure();

		figure.setBackgroundColor(ColorConstants.blue);
		figure.setForegroundColor(ColorConstants.blue);

		PolylineConnection wireFigure = new PolylineConnection();
		wireFigure.setForegroundColor(ColorConstants.blue);

		DocNode fromNode = edge.getFrom();
		DocNode toNode = edge.getTo();
		IFigure fromFig = docNode2Figure.get(fromNode);
		IFigure toFig = docNode2Figure.get(toNode);
		EllipseAnchor sourceAnchor = new EllipseAnchor(fromFig);
		EllipseAnchor targetAnchor = new EllipseAnchor(toFig);

		// List<EdgeRelation> relations = edge.getAttributes();
		// for (EdgeRelation er : relations) {
		//
		// wireFigure.setLineWidth(Math.min(1,(int) (er.getConfidence() *
		// er.getWeight() * scale)));
		// wireFigure.setLineStyle(SWT.LINE_SOLID);
		// wireFigure.setAntialias(SWT.ON);
		// if (er.getRelation()==EEdgeRelation.SUPERIOR_TO) {
		// wireFigure.setForegroundColor(ColorConstants.green);
		// } else if (er.getRelation()==EEdgeRelation.CENTER_ALIGNED) {
		// wireFigure.setForegroundColor(ColorConstants.red);
		// } else if (er.getRelation()==EEdgeRelation.KEY_VALUE) {
		// wireFigure.setForegroundColor(ColorConstants.orange);
		// } else {
		// wireFigure.setForegroundColor(ColorConstants.blue);
		// }

		Point start = new Point();
		Point end = new Point();

		Point fromRight = fromFig.getBounds().getRight();
		Point fromLeft = fromFig.getBounds().getLeft();
		Point fromBott = fromFig.getBounds().getBottom();
		Point fromTop = fromFig.getBounds().getTop();

		Point toRight = toFig.getBounds().getRight();
		Point toLeft = toFig.getBounds().getLeft();
		Point toBott = toFig.getBounds().getBottom();
		Point toTop = toFig.getBounds().getTop();

		// set the end points of the wireFigure
		if (edge.getOrientation() == Orientation.HORIZONTAL) {
			if (fromRight.x > toLeft.x) {
				start.x = fromLeft.x;
				end.x = toRight.x;
			} else {
				start.x = fromRight.x;
				end.x = toLeft.x;
			}

			int minY = Math.min(fromBott.y, toBott.y); // min bottom
			int maxY = Math.max(fromTop.y, toTop.y); // max top
			int midY = maxY - ((maxY - minY) / 2);
			start.y = end.y = midY;

			// if (fromBott.y>toBott.y) {
			// if (fromTop.y<toTop.y) {
			// start.y = end.y = fromFig.getBounds().getCenter().y;
			// } else {
			// start.y = end.y = toFig.getBounds().getCenter().y;
			// }
			// } else {
			// if (fromTop.y<toTop.y) {
			// start.y = end.y = fromFig.getBounds().getCenter().y;
			// } else {
			// start.y = end.y = toFig.getBounds().getCenter().y;
			// }
			// }

			wireFigure.setStart(start);
			wireFigure.setEnd(end);
			Rectangle r = new Rectangle(start, end);
			figure.setBounds(r);
		}

		else if (edge.getOrientation() == Orientation.VERTICAL) {
			if (fromBott.y > toTop.y) {
				start.y = fromTop.y;
				end.y = toBott.y;
			} else {
				start.y = fromBott.y;
				end.y = toTop.y;
			}

			// int minX = Math.min(fromBott.x, toBott.x); //min bottom
			// int maxX = Math.max(fromTop.x, toTop.x); //max top
			// int midX = maxX - ((maxX-minX)/2);
			// start.x = end.x = midX;

			if (fromRight.x > toRight.x) {
				if (fromLeft.x > toLeft.x) {
					start.x = end.x = fromLeft.x
							+ ((toRight.x - fromLeft.x) / 2);
				} else {
					start.x = end.x = toFig.getBounds().getCenter().x;
					;
				}
			} else {
				if (fromLeft.x > toLeft.x) {
					start.x = end.x = fromFig.getBounds().getCenter().x;
				} else {
					start.x = end.x = toLeft.x + ((fromRight.x - toLeft.x) / 2);
				}
			}

			wireFigure.setStart(start);
			wireFigure.setEnd(end);
			Rectangle r = new Rectangle(start, end);
			figure.setBounds(r);
		} else {
			wireFigure.setSourceAnchor(sourceAnchor);
			wireFigure.setTargetAnchor(targetAnchor);
			wireFigure.setBackgroundColor(ColorConstants.red);
			wireFigure.setForegroundColor(ColorConstants.red);
			return wireFigure;
		}

		return figure;
	}

	// /**
	// * Convert a generic segment to a rectangle figure.
	// *
	// * @param node
	// * @return
	// */
	// private RectangleFigure segment2Rectangle(GenericSegment gs)
	// {
	// Rectangle rectangle = new Rectangle();
	// double width = gs.getWidth();
	// double height = gs.getHeight();
	// rectangle.width = (int) (DocumentController.docModel.getScale() * width);
	// rectangle.height = (int) (DocumentController.docModel.getScale() *
	// height);
	//
	// rectangle.x = (int) (DocumentController.docModel.getScale() *
	// gs.getX1());
	// rectangle.y = (int) (DocumentController.docModel.getScale() *
	// gs.getY1());
	//
	// RectangleFigure result = new RectangleFigure();
	// result.setBounds(rectangle);
	// return result;
	// }

	public DocWrapHighlighter getHighlighter() {
		return highlighter;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setUseInternalScale(boolean useInternalScale) {
		this.useInternalScale = useInternalScale;
	}

	public RectangleAdjustment getAnnotationAdjustment() {
		return annoAdj;
	}

	public void setAnnotationAdjustment(RectangleAdjustment annoAdj) {
		this.annoAdj = annoAdj;
	}

}// DocumentGraphPlotter
