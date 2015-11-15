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
package at.tuwien.dbai.bladeRunner.editors;

import java.awt.geom.AffineTransform;
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
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapHighlighter;
import at.tuwien.dbai.bladeRunner.editors.annotator.PDFViewerSWT;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor.PlotMouseListener;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.dbai.bladeRunner.utils.SWT2Dutil;
import at.tuwien.dbai.bladeRunner.views.IPlotMouseListener;
import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.hier.ISegHierGraph;
import at.tuwien.prip.model.graph.hier.level.IGraphLevel;
import at.tuwien.prip.model.graph.hier.level.SegLevelGraph;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.utils.Orientation;

/**
 * MultiCanvas.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 19, 2013
 */
public class MultiCanvas extends FigureCanvas {
	private Composite parent;

	// the data models
	private ISegmentGraph graph = null;

	/* translation and scaling */
	private int xTrans = 0, yTrans = 0;
	private double scale = 1d;
	private boolean useInternalScale = false;

	/* color defaults */
	// private Color highlightColor = ColorConstants.green;
	private Color nodeColor = ColorConstants.darkGray; // default

	// controls
	private boolean showNodes = true;
	private boolean showEdges = true;
	private boolean showAnnotations = false;
	private boolean showDocumentBg = false;
	private boolean showDocMatrix = false;
	private boolean showGT = false;
	private ImageFigure pdfThumbFigure = null;
	private Image bgThumb = null;

	private java.awt.Rectangle focusRectangle = new java.awt.Rectangle(0, 0, 0,
			0);

	private BidiMap<DocNode, IFigure> docNode2Figure = new BidiMap<DocNode, IFigure>();
	private BidiMap<DocEdge, IFigure> docEdge2Figure = new BidiMap<DocEdge, IFigure>();

	// private final LayeredPane rootPane = new LayeredPane();
//	private final ScrollPane scrollPane = new ScrollPane();
	
	private final ScalableLayeredPane layerPane = new ScalableLayeredPane();
	
	private final Layer graphLayer = new Layer();
	
	private final Layer pdfLayer = new Layer();

	private DocWrapHighlighter highlighter;

	/* zooming rates in x and y direction are equal. */
	final float ZOOMIN_RATE = 1.1f; /* zoomin rate */
	final float ZOOMOUT_RATE = 0.9f; /* zoomout rate */

	private Image sourceImage; /* original image */
	private Image screenImage; /* screen image */

	public Rectangle selectionRectangle = null;

	public AffineTransform transform = new AffineTransform();
	public AffineTransform selTransform = new AffineTransform();

	private String currentDir = ""; /* remembering file open directory */

	private float zoomLevel = 1f;
	public double dtx = 0, dty = 0;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 * @param parent
	 */
	public MultiCanvas(final Composite parent) {
		this(parent, SWT.NULL);
	}

	/**
	 * Constructor for ScrollableCanvas.
	 * 
	 * @param viewer
	 * @param parent
	 *            the parent of this control.
	 * @param style
	 *            the style of this control.
	 */
	public MultiCanvas(final Composite parent, int style) {
		super(parent, style | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.NO_BACKGROUND);

		addControlListener(new ControlAdapter() { /* resize listener. */
			public void controlResized(ControlEvent event) {
				syncScrollBars();
			}
		});
		addPaintListener(new PaintListener() { /* paint listener. */
			public void paintControl(final PaintEvent event) {
				paint(event.gc);
			}
		});
		
		layerPane.add(graphLayer);

		setBackground(ColorConstants.darkGray);
		initScrollBars();
	}

	/**
	 * 
	 * @param ev
	 */
	public void documentUpdated(DocumentUpdateEvent ev) {
		DocumentUpdate update = ev.getDocumentUpdate();
		DocumentModel model = update.getUpdate();

		switch (update.getType()) {
		case RESIZE: /* scaling */

			double scale = model.getScale();
			zoomLevel = (float) scale;
			ImageData data = PDFUtils.getImageFromPDFPage(PDFViewerSWT.page,
					zoomLevel);
			setImageData(data, true);
			this.redraw();
			calibrateGraphCenter(true);

			break;

		case REPAN: /* translation */

			break;

		case NEW_DOCUMENT:

			double s = model.getScale();
			zoomLevel = (float) s;
//			data = PDFUtils.getImageFromPDFPage(PDFViewerSWT.page, 1.1);
//			setImageData(data, false);
//			calibrateGraphCenter(true);

			this.redraw();
			break;

		case PAGE_CHANGE:

			setImage(model.getThumb());
			calibrateGraphCenter(true);
			break;

		default:

			// fitHeight
			break;
		}
	}

	// /**
	// * Deal with document updates.
	// *
	// * @param ev
	// */
	// public void documentUpdated(DocumentUpdateEvent ev)
	// {
	// DocumentUpdate update = ev.getDocumentUpdate();
	//
	// switch (update.getType())
	// {
	// case RESIZE: /* scaling */
	//
	// calibrateGraphCenter(true);
	// this.redraw();
	//
	// break;
	//
	// case REPAN: /* translation */
	//
	// // transform = (AffineTransform) update.getUpdate();
	// // double transX = transform.getTranslateX();
	// // double transY = transform.getTranslateY();
	// // translateContents2((int)transX, (int)transY);
	//
	// break;
	//
	// case DOCUMENT_CHANGE:
	//
	// calibrateGraphCenter(true);
	// break;
	//
	// case PAGE_CHANGE:
	//
	// calibrateGraphCenter(true);
	// break;
	//
	// default:
	//
	// // fitHeight
	// break;
	// }
	// }

	public AffineTransform getTransform() {
		return transform;
	}

	/* Paint function */
	private void paint(GC gc) {
		Rectangle clientRect = getClientArea(); /* Canvas' painting area */
		setBackground(ColorConstants.darkGray);

		if (sourceImage != null) {
			Rectangle imageRect = SWT2Dutil.inverseTransformRect(transform,
					clientRect);
			int gap = 2; /* find a better start point to render */
			imageRect.x -= gap;
			imageRect.y -= gap;
			imageRect.width += 2 * gap;
			imageRect.height += 2 * gap;

			if (sourceImage.isDisposed())
				return;

			Rectangle imageBound = sourceImage.getBounds();
			imageRect = imageRect.intersection(imageBound);
			Rectangle destRect = SWT2Dutil.transformRect(transform, imageRect);

			if (screenImage != null)
				screenImage.dispose();
			screenImage = new Image(getDisplay(), clientRect.width,
					clientRect.height);

			// draw image
			GC newGC = new GC(screenImage);

			// draw background
			newGC.setBackground(ColorConstants.darkGray);
			newGC.setForeground(ColorConstants.darkGray);
			newGC.fillRectangle(clientRect);

			newGC.setClipping(clientRect);
			newGC.drawImage(sourceImage, imageRect.x, imageRect.y,
					imageRect.width, imageRect.height, destRect.x, destRect.y,
					destRect.width, destRect.height);

			// draw selection
			if (selectionRectangle != null) {
				Rectangle selRect = SWT2Dutil.transformRect(selTransform,
						selectionRectangle);

				// draw selection
				newGC.setForeground(ColorConstants.yellow);
				newGC.setBackground(ColorConstants.yellow);
				newGC.setAlpha(120);
				newGC.fillRectangle(selRect.x, selRect.y, selRect.width,
						selRect.height);
			}
			newGC.dispose();

			gc.setBackground(ColorConstants.darkGray);
			gc.setForeground(ColorConstants.darkGray);
			gc.drawRectangle(clientRect);
			gc.drawImage(screenImage, 0, 0);
		} else {
			gc.setClipping(clientRect);
			gc.fillRectangle(clientRect);
			initScrollBars();
		}
	}

	/* Initialize the scrollbar and register listeners. */
	private void initScrollBars() {
		ScrollBar horizontal = getHorizontalBar();
		horizontal.setEnabled(false);
		horizontal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollHorizontally((ScrollBar) event.widget);
			}
		});
		ScrollBar vertical = getVerticalBar();
		vertical.setEnabled(false);
		vertical.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollVertically((ScrollBar) event.widget);
			}
		});
	}

	/* Scroll horizontally */
	private void scrollHorizontally(ScrollBar scrollBar) {
		if (sourceImage == null)
			return;

		AffineTransform af = transform;
		double tx = af.getTranslateX();
		double select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
		transform = af;

		af = selTransform;
		tx = af.getTranslateX();
		select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
		selTransform = af;
		syncScrollBars();
	}

	/* Scroll vertically */
	private void scrollVertically(ScrollBar scrollBar) {
		if (sourceImage == null)
			return;

		AffineTransform af = transform;
		double ty = af.getTranslateY();
		double select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
		transform = af;

		af = selTransform;
		ty = af.getTranslateY();
		select = -scrollBar.getSelection();
		af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
		selTransform = af;

		syncScrollBars();
	}

	/**
	 * Source image getter.
	 * 
	 * @return sourceImage.
	 */
	public Image getSourceImage() {
		return sourceImage;
	}

	/**
	 * Synchronize the scrollbar with the image. If the transform is out of
	 * range, it will correct it. This function considers only following factors
	 * :<b> transform, image size, client area</b>.
	 */
	public void syncScrollBars() {
		if (sourceImage == null || sourceImage.isDisposed()) {
			redraw();
			return;
		}

		AffineTransform af = transform;
		double sx = af.getScaleX(), sy = af.getScaleY();
		double tx = af.getTranslateX(), ty = af.getTranslateY();
		if (tx > 0)
			tx = 0;
		if (ty > 0)
			ty = 0;

		ScrollBar horizontal = getHorizontalBar();
		horizontal.setIncrement((int) (getClientArea().width / 100));
		horizontal.setPageIncrement(getClientArea().width);
		Rectangle imageBound = sourceImage.getBounds();
		int cw = getClientArea().width, ch = getClientArea().height;
		if (imageBound.width * sx > cw) { /* image is wider than client area */
			horizontal.setMaximum((int) (imageBound.width * sx));
			horizontal.setEnabled(true);
			if (((int) -tx) > horizontal.getMaximum() - cw)
				tx = -horizontal.getMaximum() + cw;
		} else { /* image is narrower than client area */
			horizontal.setEnabled(false);
			tx = (cw - imageBound.width * sx) / 2; // center if too small.
		}

		ScrollBar vertical = getVerticalBar();
		vertical.setIncrement((int) (getClientArea().height / 100));
		vertical.setPageIncrement((int) (getClientArea().height));
		if (imageBound.height * sy > ch) { /* image is higher than client area */
			vertical.setMaximum((int) (imageBound.height * sy));
			vertical.setEnabled(true);
			if (((int) -ty) > vertical.getMaximum() - ch)
				ty = -vertical.getMaximum() + ch;
		} else { /* image is less higher than client area */
			vertical.setEnabled(false);
			ty = (ch - imageBound.height * sy) / 2; // center if too small.
		}

		/* update transform. */
		af = AffineTransform.getScaleInstance(sx, sy);
		af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
		transform = af;
		// selTransform = af;

		AffineTransform af2 = transform;
		sx = af2.getScaleX();
		sy = af2.getScaleY();
		tx = af2.getTranslateX();
		ty = af2.getTranslateY();
		if (tx > 0)
			tx = 0;
		if (ty > 0)
			ty = 0;
		if (imageBound.width * sx > cw) { /* image is wider than client area */
			horizontal.setMaximum((int) (imageBound.width * sx));
			horizontal.setEnabled(true);
			if (((int) -tx) > horizontal.getMaximum() - cw)
				tx -= horizontal.getMaximum() + cw;
		} else { /* image is narrower than client area */
			horizontal.setEnabled(false);
			tx = (cw - imageBound.width * sx) / 2; // center if too small.
		}
		if (imageBound.height * sy > ch) { /* image is higher than client area */
			vertical.setMaximum((int) (imageBound.height * sy));
			vertical.setEnabled(true);
			if (((int) -ty) > vertical.getMaximum() - ch)
				ty -= vertical.getMaximum() + ch;
		} else { /* image is less higher than client area */
			vertical.setEnabled(false);
			ty = (ch - imageBound.height * sy) / 2; // center if too small.
		}
		double x = tx - dtx;
		double y = ty - dty;

		af2 = AffineTransform.getScaleInstance(sx, sy);
		af2.preConcatenate(AffineTransform.getTranslateInstance(x, y));
		selTransform = af2;

		vertical.setSelection((int) (-ty));
		vertical.setThumb((int) (getClientArea().height));

		horizontal.setSelection((int) (-tx));
		horizontal.setThumb((int) (getClientArea().width));

		redraw();
	}

	/**
	 * Reload image from a file
	 * 
	 * @param filename
	 *            image file
	 * @return swt image created from image file
	 */
	public Image loadImage(String filename) {
		if (sourceImage != null && !sourceImage.isDisposed()) {
			sourceImage.dispose();
			sourceImage = null;
		}
		sourceImage = new Image(getDisplay(), filename);
		showOriginal();
		return sourceImage;
	}

	/**
	 * Call back function of button "open". Will open a file dialog, and choose
	 * the image file. It supports image formats supported by Eclipse.
	 */
	public void onFileOpen() 
	{
		FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
		fileChooser.setText("Open image file");
		fileChooser.setFilterPath(currentDir);
		fileChooser
				.setFilterExtensions(new String[] { "*.gif; *.jpg; *.png; *.ico; *.bmp" });
		fileChooser.setFilterNames(new String[] { "SWT image"
				+ " (gif, jpeg, png, ico, bmp)" });
		String filename = fileChooser.open();
		if (filename != null) {
			loadImage(filename);
			currentDir = fileChooser.getFilterPath();
		}
	}

	/**
	 * Get the image data. (for future use only)
	 * 
	 * @return image data of canvas
	 */
	public ImageData getImageData() {
		return sourceImage.getImageData();
	}

	/**
	 * Reset the image data and update the image
	 * 
	 * @param data
	 *            image data to be set
	 */
	public void setImageData(ImageData data, boolean keepHighlights)
	{
		if (!keepHighlights) {
			selectionRectangle = null;
		}

		// if (sourceImage != null)
		// sourceImage.dispose();
		if (data != null) 
		{
			java.awt.Rectangle dim = DocumentController.docModel.getBounds();
			java.awt.Rectangle graphDims = graph.getDimensions();

			int w = (int) (scale * (dim.width));// dim.width/4.5));
			int h = (int) (scale * (dim.height));// dim.height/20));
			int x = (int) (scale * (dim.x));
			int y = (int) (scale * (dim.y - (graphDims.y)));// - graphDims.y

			Image sourceImage = new Image(getDisplay(), data);

			pdfThumbFigure = new ImageFigure();
			pdfThumbFigure.setImage(sourceImage);
			pdfThumbFigure.setLocation(new Point(0, 0));
			pdfThumbFigure.setSize(w, h);
			 
			pdfLayer.add(pdfThumbFigure);
		}

		syncScrollBars();
	}

	public void setImage(Image img) {
		sourceImage = img;
		syncScrollBars();
	}

	/**
	 * Fit the image onto the canvas
	 */
	public void fitCanvas() {
		if (sourceImage == null)
			return;
		Rectangle imageBound = sourceImage.getBounds();
		Rectangle destRect = getClientArea();
		double sx = (double) destRect.width / (double) imageBound.width;
		double sy = (double) destRect.height / (double) imageBound.height;
		double s = Math.min(sx, sy);
		double dx = 0.5 * destRect.width;
		double dy = 0.5 * destRect.height;
		centerZoom(dx, dy, s, new AffineTransform());
	}

	/**
	 * Show the image with the original size
	 */
	public void showOriginal() {
		if (sourceImage == null)
			return;
		transform = new AffineTransform();
		syncScrollBars();
	}

	/**
	 * Perform a zooming operation centered on the given point (dx, dy) and
	 * using the given scale factor. The given AffineTransform instance is
	 * preconcatenated.
	 * 
	 * @param dx
	 *            center x
	 * @param dy
	 *            center y
	 * @param scale
	 *            zoom rate
	 * @param af
	 *            original affinetransform
	 */
	public void centerZoom(double dx, double dy, double scale,
			AffineTransform af) {
		af.preConcatenate(AffineTransform.getTranslateInstance(-dx, -dy));
		af.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
		af.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
		transform = af;
		syncScrollBars();
	}

	public void zoomIn2() {
		zoomLevel += 0.2;
		ImageData data = PDFUtils.getImageFromPDFPage(PDFViewerSWT.page,
				zoomLevel);
		setImageData(data, true);

		Rectangle rect = getClientArea();
		int w = rect.width, h = rect.height;
		double dx = ((double) w) / 2;
		double dy = ((double) h) / 2;
		centerZoom(dx, dy, 1, transform);
	}

	public void zoomOut2() {
		zoomLevel -= 0.2;
		ImageData data = PDFUtils.getImageFromPDFPage(PDFViewerSWT.page,
				zoomLevel);
		setImageData(data, true);

		Rectangle rect = getClientArea();
		int w = rect.width, h = rect.height;
		double dx = ((double) w) / 2;
		double dy = ((double) h) / 2;
		centerZoom(dx, dy, 1, transform);
	}

	/**
	 * Zoom in around the center of client Area.
	 */
	public void zoomIn() {
		if (sourceImage == null) {
			return;
		}
		zoomLevel *= ZOOMIN_RATE;

		Rectangle rect = getClientArea();
		int w = rect.width, h = rect.height;
		double dx = ((double) w) / 2;
		double dy = ((double) h) / 2;
		centerZoom(dx, dy, ZOOMIN_RATE, transform);
	}

	/**
	 * Zoom out around the center of client Area.
	 */
	public void zoomOut() {
		if (sourceImage == null) {
			return;
		}
		zoomLevel *= ZOOMOUT_RATE;

		Rectangle rect = getClientArea();
		int w = rect.width, h = rect.height;
		double dx = ((double) w) / 2;
		double dy = ((double) h) / 2;
		centerZoom(dx, dy, ZOOMOUT_RATE, transform);
	}

	public float getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * 
	 * @param ml
	 */
	public void addPlotMouseListener(IPlotMouseListener ml) {
		if (ml instanceof MouseListener) {
			addMouseListener((MouseListener) ml);
		}
		if (ml instanceof MouseMoveListener) {
			addMouseMoveListener((MouseMoveListener) ml);
		}
		if (ml instanceof PaintListener) {
			addPaintListener((PaintListener) ml);
		}
		if (ml instanceof MouseWheelListener) {
			addMouseWheelListener((MouseWheelListener) ml);
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	//
	//
	//
	// ///////////////////////////////////////////////////////////////////////
	/**
	 * Plot this view's graph.
	 * 
	 * @param docDecorations
	 */
	public void plot(boolean docDecorations) 
	{
		graphLayer.removeAll();

		if (docDecorations) 
		{
			drawGraphAsDocument(graphLayer, graph);
		} 
		else 
		{
			drawGraph(graphLayer, graph);
		}

//		this.setContents(this.scrollPane);
	}

	/**
	 * Draw the currently active graph.
	 */
	public void drawActiveGraph(boolean page)
	{
		drawActiveGraph(graphLayer, page);
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
			if (DocumentController.docModel.getDocumentGraph() == null)
				return;

			java.awt.Rectangle dim = DocumentController.docModel.getBounds();

			DocumentController.docModel.setY((int) (scale * graphDims.y));

			double scale = 1d;
			if (useInternalScale) {
				scale = this.scale;
			} else {
				scale = DocumentController.docModel.getScale();
			}

			int w = (int) (scale * (dim.width));// dim.width/4.5));
			int h = (int) (scale * (dim.height));// dim.height/20));
			int x = (int) (scale * (dim.x));
			int y = (int) (scale * (dim.y - (graphDims.y)));// - graphDims.y

			// draw a white page outline
			RectangleFigure bounds = new RectangleFigure();
			bounds.setLocation(new Point(x, y));
			bounds.setSize(w, h);
			bounds.setBackgroundColor(ColorConstants.white);
			contents.add(bounds);

			// draw background image if available
			if (bgThumb != null && !bgThumb.isDisposed()) 
			{
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
			translateContents2(0, graphDims.y);
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

		double scale = 1d;
		if (useInternalScale) {
			scale = this.scale;
		} else {
			scale = DocumentController.docModel.getScale();
		}

		// mark the selection...
		RectangleFigure highlight = new RectangleFigure();
		{
			int x1 = (int) (scale * (new Double(focusRectangle.x)));// - xTrans;
			int y1 = (int) (scale * (new Double(focusRectangle.y)));// - yTrans;
			int w1 = (int) (scale * (new Double(focusRectangle.width)));
			int h1 = (int) (scale * (new Double(focusRectangle.height)));

			highlight.setLocation(new Point(x1, y1));
			highlight.setSize(w1, h1);
			highlight.setBackgroundColor(ColorConstants.yellow);
			highlight.setAlpha(100);
			highlight.setAntialias(SWT.ON);
			contents.add(highlight);
		}

		if (graph == null) {
			return;
		}

		Collection<DocNode> nodes = graph.getNodes();
		for (DocNode node : nodes) {
			drawNode(contents, node);
		}

		if (showGT) 
		{
			// mark the ground truth boxes
			AnnotatorEditor we = DocWrapUIUtils.getWrapperEditor();
			IDocument doc = we.getActiveDocument();
			int pageNum = we.getCurrentDocumentPageNum();
			PdfBenchmarkDocument benchDoc = (PdfBenchmarkDocument) doc;
			for (Annotation annotation : benchDoc.getAnnotations()) 
			{
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
					java.awt.Rectangle b = region.getBounds();
					float x1 = b.x;
					float x2 = b.x + b.width;
					float y1 = b.y;
					float y2 = b.y + b.height;
					GenericSegment gs = new GenericSegment(x1, x2, y1, y2);
					DocNode node = new DocNode(gs);
					node.setSegType(SegmentType.Semantic);
					drawNode(contents, node);
				}
			}
		}

		Collection<DocEdge> edges = graph.getEdges();
		for (DocEdge edge : edges) {
			if (nodes.contains(edge.getTo())) {
				drawEdgeFull(contents, edge);
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

	public void setFocusRectangle(java.awt.Rectangle focusRectangle) {
		this.focusRectangle = focusRectangle;
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

	public IFigure getRootPane() 
	{
		return layerPane;
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

		if (node.getSegType().equals(SegmentType.Image)) {
			boxNode.setFill(true);
			boxNode.setForegroundColor(ColorConstants.red);
			boxNode.setBackgroundColor(ColorConstants.red);
			boxNode.setAlpha(100);
		} else if (showAnnotations && node.getSemanticAnnotations().size() > 0) {
			boxNode.setBackgroundColor(ColorConstants.yellow);
		} else if (node.getSegType().equals(SegmentType.Semantic)) {
			boxNode.setBackgroundColor(ColorConstants.yellow);
		} else {
			boxNode.setForegroundColor(ColorConstants.black);
			boxNode.setBackgroundColor(nodeColor);
		}

		if (showNodes) {
			contents.add(boxNode);
		}
	}

	/**
	 * Clear all highlights.
	 */
	public void clearAllHighlights() {
		focusRectangle = new java.awt.Rectangle();
	}

	/**
	 * Set the document graph to be drawn.
	 * 
	 * @param graph
	 */
	public void setDocumentGraph(ISegmentGraph graph) {
		clearContents();
		this.graph = graph;
		if (graph != null) {
			if (graph instanceof ISegHierGraph) {
				graph = (DocumentGraph) ((ISegHierGraph) graph).getBaseGraph();
				this.graph = graph;
				// calibrateGraphCenter(true);
			} else if (graph instanceof DocumentGraph) {
				this.graph = graph;
				// calibrateGraphCenter(true);
			}
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
				this.graph = (DocumentGraph) segLevel.getGraph();
			}
		}
	}

	/**
	 * Calibrate the graph to fit into the viewing frame.
	 * 
	 * @param scale
	 * @param x
	 * @param y
	 */
	public void calibrateGraphPosition(double scale)
	{
		if (getDocumentGraph() == null) {
			return;
		}

		Dimension dims = getViewport().getSize();
		int width = (int) (1 * dims.width);
		int height = (int) (1 * dims.height);

		java.awt.Rectangle bounds = DocumentController.docModel.getBounds();
		double bw = scale * bounds.width;
		double bh = scale * bounds.height;
		double bx = scale * bounds.x;
		double by = scale * bounds.y;

		double midDocX = (bx + (bw / 2));
		double midPageX = width / 2;
		double midDocY = (by + (bh / 2));
		double midPageY = height / 2;

		int xTrans = (int) (midPageX - midDocX);
		int yTrans = (int) (midPageY - midDocY);

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
			double bw = DocumentController.docModel.getScale() * bounds.width;
			double bh = DocumentController.docModel.getScale() * bounds.height;
			double bx = DocumentController.docModel.getScale() * bounds.x;
			double by = DocumentController.docModel.getScale() * bounds.y;

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
				translateContents2(Math.max((int) -bx + 30, xTrans),
						Math.max((int) -by + 30, yTrans));
			} else {
				translateContents(xTrans, Math.max(30, yTrans));
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
				yTrans = (int) 40;// (midPageY - midDocY);
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
				translateContents2(Math.max(-bounds.x + 30, xTrans),
						Math.max(-bounds.y + 30, yTrans));
			} else {
				translateContents(xTrans, Math.max(30, yTrans));
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
		 this.bgThumb = img;
	}

	/**
	 * 
	 */
	public void dispose() {
		if (sourceImage != null && !sourceImage.isDisposed()) {
			sourceImage.dispose();
		}
		if (screenImage != null && !screenImage.isDisposed()) {
			screenImage.dispose();
		}

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

		org.eclipse.draw2d.geometry.Rectangle rectangle = new org.eclipse.draw2d.geometry.Rectangle();
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
			org.eclipse.draw2d.geometry.Rectangle r = new org.eclipse.draw2d.geometry.Rectangle(
					start, end);
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
			org.eclipse.draw2d.geometry.Rectangle r = new org.eclipse.draw2d.geometry.Rectangle(
					start, end);
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
		org.eclipse.draw2d.geometry.Rectangle rectangle = new org.eclipse.draw2d.geometry.Rectangle();
		double width = gs.getWidth();
		double height = gs.getHeight();
		rectangle.width = (int) (DocumentController.docModel.getScale() * width);
		rectangle.height = (int) (DocumentController.docModel.getScale() * height);

		rectangle.x = (int) (DocumentController.docModel.getScale() * gs
				.getX1());
		rectangle.y = (int) (DocumentController.docModel.getScale() * gs
				.getY1());

		RectangleFigure result = new RectangleFigure();
		result.setBounds(rectangle);
		return result;
	}

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
}
