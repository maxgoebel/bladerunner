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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ScrollBar;

import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.dbai.bladeRunner.views.IPlotMouseListener;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.project.document.DocumentModel;

/**
 * A scrollable image canvas that extends org.eclipse.swt.graphics.Canvas.
 * <p/>
 * It requires Eclipse (version >= 2.1) on Win32/win32; Linux/gtk;
 * MacOSX/carbon.
 * <p/>
 * This implementation using the pure SWT, no UI AWT package is used. For
 * convenience, I put everything into one class. However, the best way to
 * implement this is to use inheritance to create multiple hierarchies.
 * 
 * @author Chengdong Li: cli4@uky.edu
 */
public class SWTImageCanvas extends Canvas {
	/* zooming rates in x and y direction are equal. */
	final float ZOOMIN_RATE = 1.1f; /* zoomin rate */
	final float ZOOMOUT_RATE = 0.9f; /* zoomout rate */

	private Image sourceImage; /* original image */
	private Image screenImage; /* screen image */

	Rectangle selectionRectangle = null;

	AffineTransform transform = new AffineTransform();
	AffineTransform selTransform = new AffineTransform();

	private String currentDir = ""; /* remembering file open directory */

	private Color backgroundColor;
	
	private float zoomLevel = 0.8f;
	double dtx = 0, dty = 0;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 * @param parent
	 */
	public SWTImageCanvas(final Composite parent) {
		this(parent, ColorConstants.white, SWT.NULL);
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
	public SWTImageCanvas(final Composite parent, Color background, int style) 
	{
		super(parent, style | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.NO_BACKGROUND);

		this.backgroundColor = background;
		
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

		setBackground(this.backgroundColor);
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

			break;

		case REPAN: /* translation */

			break;

		case NEW_DOCUMENT:

			double s = model.getScale();
			zoomLevel = (float) s;
			data = PDFUtils.getImageFromPDFPage(PDFViewerSWT.page, 1.1);
			setImageData(data, false);

			this.redraw();
			break;

		case PAGE_CHANGE:

			setImage(model.getThumb());
			break;

		default:

			// fitHeight
			break;
		}
	}

	public AffineTransform getTransform() {
		return transform;
	}

	/**
	 * Dispose the garbage here
	 */
	public void dispose() {
		if (sourceImage != null && !sourceImage.isDisposed()) {
			sourceImage.dispose();
		}
		if (screenImage != null && !screenImage.isDisposed()) {
			screenImage.dispose();
		}
	}

	/* Paint function */
	private void paint(GC gc) {
		Rectangle clientRect = getClientArea(); /* Canvas' painting area */
		setBackground(this.backgroundColor);

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
			newGC.setBackground(this.backgroundColor);
			newGC.setForeground(this.backgroundColor);
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

			gc.setBackground(this.backgroundColor);
			gc.setForeground(this.backgroundColor);
			gc.drawRectangle(clientRect);
			gc.drawImage(screenImage, 0, 0);
		} else {
			gc.setClipping(clientRect);
			gc.fillRectangle(clientRect);
			initScrollBars();
		}
	}
	
	public void clearImage() 
	{
//		Rectangle clientRect = getClientArea(); /* Canvas' painting area */
		if (sourceImage==null) return;
		
//		screenImage.dispose();
//		screenImage = new Image(getDisplay(), clientRect.width,
//				clientRect.height);

		sourceImage.dispose();
		
		showOriginal();
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
	public void syncScrollBars() 
	{
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
	 * Call back funtion of button "open". Will open a file dialog, and choose
	 * the image file. It supports image formats supported by Eclipse.
	 */
	public void onFileOpen() {
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
		setBackground(this.backgroundColor);
		if (!keepHighlights) {
			selectionRectangle = null;
		}

		if (sourceImage != null)
			sourceImage.dispose();
		if (data != null) {
			sourceImage = new Image(getDisplay(), data);
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

}
