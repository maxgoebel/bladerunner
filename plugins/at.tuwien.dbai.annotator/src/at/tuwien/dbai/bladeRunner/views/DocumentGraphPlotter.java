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
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapHighlighter;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor.PlotMouseListener;
import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.DocumentMatrix;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.base.IGraph;
import at.tuwien.prip.model.graph.hier.IHierGraph;
import at.tuwien.prip.model.graph.hier.level.IGraphLevel;
import at.tuwien.prip.model.graph.hier.level.SegLevelGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.selection.ExtractionResult;
import at.tuwien.prip.model.utils.Orientation;

/**
 * DocumentGraphPlotter.java
 * 
 * Handles the drawing of a graph.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 21, 2011
 */
public class DocumentGraphPlotter extends FigureCanvas {
	private Composite parent;

	// the data models
	private ISegmentGraph documentGraph = null;

	private DocumentFormat format = DocumentFormat.PDF;

	private Color highlightColor = ColorConstants.yellow;

	private List<DocNode> highlightNodes = new ArrayList<DocNode>();
	private List<GenericSegment> posMatchSegments = new ArrayList<GenericSegment>();
	// private List<LayoutColumn> columns = new ArrayList<LayoutColumn>();
	// private List<LayoutList> lists = new ArrayList<LayoutList>();
	// private List<LayoutSection> sections = new ArrayList<LayoutSection>();
	private List<RectSegment> whitespace;
	private List<RectSegment> docMatrix;
	private List<ExtractionResult> selections = new ArrayList<ExtractionResult>();

	// private DocumentEntry de = null;

	// translation and scaling
	private int xTrans = 0, yTrans = 0;
	private double scale = 0.5d;

	// controls
	private boolean showNodes = true;
	private boolean showEdges = true;
	private boolean showAnnotations = false;
	private boolean showDocumentBg = false;
	private boolean showDocMatrix = false;
	private ImageFigure pdfThumbFigure = null;
	private Image bgThumb = null;

	// private java.awt.Rectangle focusRectangle = new
	// java.awt.Rectangle(0,0,0,0);

	private BidiMap<DocNode, IFigure> docNode2Figure;
	private BidiMap<DocEdge, IFigure> docEdge2Figure;

	private final LayeredPane rootPane;
	private ScrollPane scrollPane;

	private DocWrapHighlighter highlighter;

	public DocWrapHighlighter getHighlighter() {
		return highlighter;
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param view
	 */
	public DocumentGraphPlotter(final Composite parent, int swt) {
		super(parent, swt);
		this.parent = parent;
		this.docNode2Figure = new BidiMap<DocNode, IFigure>();
		this.docEdge2Figure = new BidiMap<DocEdge, IFigure>();

		this.rootPane = new LayeredPane();
		this.scrollPane = new ScrollPane();

		Layer layer1 = new Layer();
		layer1.add(scrollPane);
		this.rootPane.add(layer1);

		this.scrollPane.setLayoutManager(new XYLayout());
		this.setBackground(ColorConstants.white);
		this.setContents(this.scrollPane);

		/* add a highlighter */
		this.highlighter = new DocWrapHighlighter(this);

		this.whitespace = new ArrayList<RectSegment>();
		// this.setScrollBarVisibility(FigureCanvas.ALWAYS);
	}

	/**
	 * Plot this view's graph.
	 * 
	 * @param docDecorations
	 */
	public void plot(boolean docDecorations) {

		// if no place to plot, or no data to plot, return.
		if (null == parent || format == DocumentFormat.PDF
				&& null == documentGraph) {
			return;
		}

		scrollPane.removeAll();

		if (docDecorations) {
			drawGraphAsDocument(scrollPane, documentGraph);
		} else {
			drawGraph(scrollPane, documentGraph);
		}

		this.setContents(this.scrollPane);

	}

	public IFigure getRootPane() {
		return scrollPane;
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
			drawGraphAsDocument(contents, documentGraph);
		} else {
			drawGraph(contents, documentGraph);
		}
	}

	/**
	 * Add document decorations.
	 * 
	 * @param contents
	 * @param graph
	 */
	private void drawGraphAsDocument(IFigure contents, IGraph graph) {
		// dark gray canvas background
		contents.setBackgroundColor(ColorConstants.darkGray);

		// draw a white page outline
		if (graph != null && graph instanceof ISegmentGraph) {
			java.awt.Rectangle dim = ((ISegmentGraph) graph).getDimensions();
			RectangleFigure bounds = new RectangleFigure();
			int w = (int) (scale * (dim.width + 40));// dim.width/4.5));
			int h = (int) (scale * (dim.height + 40));// dim.height/20));
			int x = (int) (scale * (dim.x - 20));
			int y = (int) (scale * (dim.y - 20));
			bounds.setLocation(new Point(x, y));
			bounds.setSize(w, h);
			bounds.setBackgroundColor(ColorConstants.white);
			contents.add(bounds);

			// draw background image if available
			// if (bgThumb!=null) {// && showDocumentBg) {
			// Image thumb =
			// new Image(parent.getDisplay(),
			// bgThumb.getImageData().scaledTo(w-20, h-20));
			//
			// pdfThumbFigure = new ImageFigure();
			// pdfThumbFigure.setImage(thumb);
			// pdfThumbFigure.setLocation(new Point(x - 20, y - 105));
			// pdfThumbFigure.setSize(w - 50, h + 280);
			// contents.add(pdfThumbFigure);
			//
			// if (!showDocumentBg) {
			// pdfThumbFigure.setVisible(false);
			// }
			// }
			drawGraph(contents, graph);
		}
	}

	/**
	 * Draw a graph.
	 * 
	 * @param contents
	 * @param graph
	 */
	@SuppressWarnings("unchecked")
	private void drawGraph(IFigure contents, IGraph graph) {
		this.xTrans = 0;
		this.yTrans = 0;
		// mark the selection...
		// RectangleFigure highlight = new RectangleFigure();
		//
		// int x1 = (int) (scale) * (focusRectangle.x);// - xTrans;
		// int y1 = (int) (scale) * (focusRectangle.y);// - yTrans;
		// int w1 = (int) (scale) * (focusRectangle.width);
		// int h1 = (int) (scale) * (focusRectangle.height);
		//
		// highlight.setLocation(new Point(x1, y1));
		// highlight.setSize(w1, h1);
		// highlight.setBackgroundColor(ColorConstants.green);
		// highlight.setAlpha(100);
		// highlight.setAntialias(SWT.ON);
		// contents.add(highlight);

		if (graph instanceof IHierGraph) {
			graph = ((IHierGraph) graph).getBaseGraph();
		}
		Collection<DocNode> nodes = graph.getNodes();
		for (DocNode node : nodes) {
			drawNode(contents, node);
		}

		// if (showAnnotations)
		// {
		// if (de instanceof WrapperDocument) {
		// WrapperDocument wd = (WrapperDocument) de;
		// List<IDocumentView> docViews = wd.getDocumentViews();
		// for (IDocumentView dv : docViews) {
		// if (dv instanceof DocumentAnnotationView) {
		// DocumentAnnotationView deView = (DocumentAnnotationView) dv;
		// List<AnnotationPage> pages = deView.getAnnotations();
		// for (AnnotationPage page : pages) {
		// List<ExtractionResult> eItems = page.getItems();
		// for (ExtractionResult ei : eItems) {
		// java.awt.Rectangle b = ei.getBounds();
		// float x1 = b.x;
		// float x2 = b.x + b.width;
		// float y1 = b.y;
		// float y2 = b.y + b.height;
		// GenericSegment gs = new GenericSegment(x1, x2, y1, y2);
		// DocNode node = new DocNode(gs);
		// node.setSegType(SegmentType.Semantic);
		// drawNode(contents, node);
		// }
		// }
		// }
		// }
		// }
		// // else if (de instanceof BenchmarkDocument) {
		// // BenchmarkDocument bd = (BenchmarkDocument) de;
		// // List<Annotation> annotations = bd.getAnnotations();
		// // for (Annotation a : annotations) {
		// // if (a.getType().equals (EAnnotationType.EXTRACTION_LITERAL)) {
		// // ExtractionAnnotation exAnn = (ExtractionAnnotation) a;
		// // } else if (a.getType().equals (EAnnotationType.FRAGMENT_LITERAL))
		// {
		// // FragmentAnnotation frAnn = (FragmentAnnotation) a;
		// // } else if (a.getType().equals(EAnnotationType.TABLE_LITERAL)) {
		// // TableAnnotation tbAnn = (TableAnnotation) a;
		// // } else if (a.getType().equals(EAnnotationType.SELECTION_LITERAL))
		// {
		// // SelectionAnnotation slAnn = (SelectionAnnotation) a;
		// // }
		// // }
		// // }
		//
		// }

		Collection<DocEdge> edges = graph.getEdges();
		for (DocEdge edge : edges) {
			if (nodes.contains(edge.getTo())) {
				drawEdgeFull(contents, edge);
			}
		}

		// mark the positive matches
		for (GenericSegment seg : posMatchSegments) {
			RectangleFigure boxNode = segment2Rectangle(seg);
			boxNode.setFill(true);
			boxNode.setBackgroundColor(ColorConstants.orange);
			boxNode.setForegroundColor(ColorConstants.black);
			boxNode.setAlpha(200);
			contents.add(boxNode);
		}

		// //mark the columns
		// for (LayoutColumn col : columns)
		// {
		// RectangleFigure colBox = new RectangleFigure();
		// Rectangle bounds = new Rectangle(
		// (int) (scale * col.getBounds().x),
		// (int) (scale * col.getBounds().y),
		// (int) (scale * col.getBounds().width),
		// (int) (scale * col.getBounds().height));
		// colBox.setBounds(bounds);
		// colBox.setFill(true);
		// colBox.setBackgroundColor(ColorConstants.lightGreen);
		// colBox.setForegroundColor(ColorConstants.black);
		// colBox.setAlpha(400);
		// contents.add(colBox);
		// }

		// //mark the lists
		// for (LayoutList list : lists)
		// {
		// RectangleFigure colBox = new RectangleFigure();
		// Rectangle bounds = new Rectangle(
		// (int) (scale * list.getBounds().x),
		// (int) (scale * list.getBounds().y),
		// (int) (scale * list.getBounds().width),
		// (int) (scale * list.getBounds().height));
		// colBox.setBounds(bounds);
		// colBox.setFill(true);
		// colBox.setBackgroundColor(ColorConstants.lightGreen);
		// colBox.setForegroundColor(ColorConstants.black);
		// colBox.setAlpha(400);
		// contents.add(colBox);
		// }

		// //mark the sections
		// for (LayoutSection section : sections)
		// {
		// RectangleFigure colBox = new RectangleFigure();
		// Rectangle bounds = new Rectangle(
		// (int) (scale * section.getBounds().x),
		// (int) (scale * section.getBounds().y),
		// (int) (scale * section.getBounds().width),
		// (int) (scale * section.getBounds().height));
		// colBox.setBounds(bounds);
		// colBox.setFill(true);
		// colBox.setBackgroundColor(ColorConstants.lightGreen);
		// colBox.setForegroundColor(ColorConstants.black);
		// colBox.setAlpha(400);
		// contents.add(colBox);
		// }

		// mark the whitespace
		for (GenericSegment list : whitespace) {
			RectangleFigure boxNode = segment2Rectangle(list);
			boxNode.setFill(true);
			boxNode.setBackgroundColor(ColorConstants.cyan);
			boxNode.setForegroundColor(ColorConstants.black);
			boxNode.setAlpha(400);
			contents.add(boxNode);
		}

		// //mark selections
		// for (Selection selection : selections)
		// {
		// RectangleFigure boxNode = new RectangleFigure();
		// Rectangle bounds = new Rectangle(
		// (int) (scale * selection.getBounds().x),
		// (int) (scale * selection.getBounds().y),
		// (int) (scale * selection.getBounds().width),
		// (int) (scale * selection.getBounds().height));
		// boxNode.setBounds(bounds);
		// boxNode.setFill(true);
		// boxNode.setBackgroundColor(ColorConstants.blue);
		// boxNode.setForegroundColor(ColorConstants.black);
		// boxNode.setAlpha(400);
		// contents.add(boxNode);
		// }

		// mark the document matrix
		if (showDocMatrix) {
			for (RectSegment mat : docMatrix) {
				RectangleFigure boxNode = segment2Rectangle(mat);
				boxNode.setFill(true);
				if (mat.isFilled()) {
					boxNode.setBackgroundColor(ColorConstants.red);
				} else {
					boxNode.setBackgroundColor(ColorConstants.white);
				}
				boxNode.setForegroundColor(ColorConstants.black);
				boxNode.setAlpha(400);
				contents.add(boxNode);
			}
		}
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
		if (this != null && this.getContents() != null
				&& this.getContents().getChildren() != null) {
			@SuppressWarnings("unchecked")
			List<IFigure> figs = this.getContents().getChildren();
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
			// parent.redraw();
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
	@SuppressWarnings("unchecked")
	public void toggleAnnotations() {
		showAnnotations = !showAnnotations;
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

		// mark the highlight matches...
		if (highlightNodes.contains(node)) {
			boxNode.setFill(true);
			boxNode.setForegroundColor(highlightColor);
			boxNode.setBackgroundColor(highlightColor);
		} else {
			if (node.getSegType().equals(SegmentType.Image)) {
				boxNode.setFill(true);
				boxNode.setForegroundColor(ColorConstants.red);
				boxNode.setBackgroundColor(ColorConstants.red);
				boxNode.setAlpha(100);
			} else if (showAnnotations
					&& node.getSemanticAnnotations().size() > 0) {
				boxNode.setBackgroundColor(ColorConstants.yellow);
			} else if (node.getSegType().equals(SegmentType.Semantic)) {
				boxNode.setBackgroundColor(ColorConstants.yellow);
			} else {
				boxNode.setForegroundColor(ColorConstants.black);
				boxNode.setBackgroundColor(ColorConstants.lightGray);
			}
		}

		if (showNodes) {
			contents.add(boxNode);
		}
	}

	/**
	 * Clear all highlights.
	 */
	public void clearAllHighlights() {
		highlightNodes = new ArrayList<DocNode>();
		posMatchSegments = new ArrayList<GenericSegment>();
		// columns = new ArrayList<LayoutColumn>();
		// lists = new ArrayList<LayoutList>();
		// sections = new ArrayList<LayoutSection>();
		whitespace = new ArrayList<RectSegment>();
		selections = new ArrayList<ExtractionResult>();
	}

	public void clearHighlightNodes() {
		highlightNodes.clear();
	}

	public void clearMatchedNodes() {
		posMatchSegments.clear();
	}

	/**
	 * Highlight a set of nodes in yellow.
	 * 
	 * @param nodes
	 */
	public void highlightNodes(List<DocNode> nodes) {
		highlightNodes.clear();
		highlightNodes.addAll(nodes);
	}

	/**
	 * Highlight a set of nodes in yellow.
	 * 
	 * @param nodes
	 */
	public void highlightNodes(List<DocNode> nodes, Color color) {
		highlightColor = color;
		highlightNodes.clear();
		highlightNodes.addAll(nodes);
	}

	/**
	 * Set the document graph to be drawn.
	 * 
	 * @param graph
	 */
	public void setDocumentGraph(ISegmentGraph graph) {
		clearContents();
		this.documentGraph = graph;
		if (graph != null) {
			if (graph instanceof IHierGraph) {
				graph = (ISegmentGraph) ((IHierGraph) graph).getBaseGraph();
			}
			DocumentMatrix dm = DocumentMatrix.newInstance(graph);
			this.docMatrix = dm.toSegments();
		}
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
				this.documentGraph = (ISegmentGraph) segLevel.getGraph();

				DocumentMatrix dm = DocumentMatrix.newInstance(documentGraph);
				this.docMatrix = dm.toSegments();
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
			boolean remember) {
		setxTrans(0);
		setyTrans(0);

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

		java.awt.Rectangle bounds = g.getDimensions();
		if (bounds == null) {
			if (g instanceof DocumentGraph) {
				g.computeDimensions();
				bounds = g.getDimensions();
			} else {
				return;
			}
		}

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

		setScale(scale); // was 0.7
		drawActiveGraph(true);

		if (remember) {
			translateContents2(xTrans, Math.max(30, yTrans));
		} else {
			translateContents(xTrans, Math.max(30, yTrans));
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
		this.bgThumb = img;
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

	public List<GenericSegment> getPosMatchSegments() {
		return posMatchSegments;
	}

	public void setPosMatchSegments(List<GenericSegment> posMatchSegments) {
		this.posMatchSegments = posMatchSegments;
	}

	// public List<LayoutList> getLists () {
	// return lists;
	// }
	//
	// public void setLists(List<LayoutList> lists) {
	// this.lists = lists;
	// }
	//
	// public List<LayoutSection> getSections() {
	// return sections;
	// }
	//
	// public void setSections(List<LayoutSection> sections) {
	// this.sections = sections;
	// }
	//
	// public List<LayoutColumn> getColumns() {
	// return columns;
	// }
	//
	// public void setColumns(List<LayoutColumn> columns) {
	// this.columns = columns;
	// }

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
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

	public ISegmentGraph getDocumentGraph() {
		return documentGraph;
	}

	public DocumentFormat getFormat() {
		return format;
	}

	public void setFormat(DocumentFormat format) {
		this.format = format;
	}

	public void getDimension() {

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

	/**
	 * Convert a generic segment to a rectangle figure.
	 * 
	 * @param node
	 * @return
	 */
	private RectangleFigure segment2Rectangle(GenericSegment gs) {
		Rectangle rectangle = new Rectangle();
		double width = gs.getWidth();
		double height = gs.getHeight();
		rectangle.width = (int) (scale * width);
		rectangle.height = (int) (scale * height);

		rectangle.x = (int) (scale * gs.getX1());
		rectangle.y = (int) (scale * gs.getY1());

		RectangleFigure result = new RectangleFigure();
		result.setBounds(rectangle);
		return result;
	}

	// public DocumentEntry getDe() {
	// return de;
	// }
	//
	// public void setDe(DocumentEntry de) {
	// this.de = de;
	// }

	public void setWhitespace(List<RectSegment> whitespace) {
		this.whitespace = whitespace;
	}

	public void setDocMatrix(List<RectSegment> docMatrix) {
		this.docMatrix = docMatrix;
	}

	public void setSelections(List<ExtractionResult> selections) {
		this.selections = selections;
	}

}// DocumentGraphPlotter
