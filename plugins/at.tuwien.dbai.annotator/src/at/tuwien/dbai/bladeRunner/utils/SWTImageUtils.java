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
package at.tuwien.dbai.bladeRunner.utils;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * ImageUtils.java
 * 
 * 
 * Utility class for image related issues.
 * 
 * created: Sep 3, 2009
 * 
 * @author mcg <mcgoebel@gmail.com>
 */
public class SWTImageUtils {

	/**
	 * 
	 * Take a screenshot of a given region within a composite.
	 * 
	 * @param parent
	 * @param region
	 * @return
	 */
	public static Image takeScreenshot(Composite parent, Rectangle region) {
		return takeScreenshot(parent, region.x, region.y, region.width,
				region.height);
	}

	/**
	 * 
	 * Take a screenshot of given dimensions within a composite.
	 * 
	 * @param parent
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static Image takeScreenshot(Composite parent, int x, int y,
			int width, int height) {
		Point browserSize = parent.getSize();
		GC gc = new GC(parent);
		final Image image = new Image(parent.getDisplay(), browserSize.x,
				browserSize.y);
		gc.copyArea(image, x, y);
		gc.dispose();

		Shell popup = new Shell(parent.getShell());
		popup.setText("Image");
		popup.setBounds(50, 50, width, height);
		popup.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				image.dispose();
			}
		});

		Canvas canvas = new Canvas(popup, SWT.NONE);
		canvas.setBounds(10, 10, width, height);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});
		popup.open();

		return image;
	}

	/**
	 * 
	 * Save an image to file using a given format.
	 * 
	 * @param img
	 * @param filename
	 * @param format
	 */
	public static void saveImage(Image img, String filename, int format) {
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { img.getImageData() };
		loader.save(filename, format);
	}

	/**
	 * 
	 * Load an image from file.
	 * 
	 * @param filename
	 * @return
	 */
	public Image loadImage(String filename) {
		ImageLoader loader = new ImageLoader();
		loader.load(getClass().getResourceAsStream(filename));
		return null;
	}

	// public Image loadImage(String filename) {
	// if(sourceImage!=null && !sourceImage.isDisposed()){
	// sourceImage.dispose();
	// sourceImage=null;
	// }
	// sourceImage= new Image(getDisplay(),filename);
	// showOriginal();
	// return sourceImage;
	// }

	/**
	 * 
	 * @param image
	 * @param display
	 * @return
	 */
	static public Image scaleTo16x16(Image image, Display display) {
		Image result = new Image(display, 16, 16);
		Rectangle bounds = image.getBounds();
		double scaleX = bounds.width <= 16 ? 1.0 : 16.0 / bounds.width;
		double scaleY = bounds.height <= 16 ? 1.0 : 16.0 / bounds.height;
		double scale = Math.min(scaleX, scaleY);
		int width = (int) (scale * bounds.width);
		int height = (int) (scale * bounds.height);
		GC gc = new GC(result);
		gc.drawImage(image, 0, 0, bounds.width, bounds.height,
				(16 - width) / 2, (16 - height) / 2, width, height);
		gc.dispose();
		return result;
	}

	/**
	 * 
	 * @param display
	 * @param width
	 * @param height
	 * @return
	 */
	static public Image createImage(Display display, int width, int height) {
		Image image = new Image(display, width, height);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillOval(0, 0, width, height);
		gc.dispose();
		return image;
	}

	/**
	 * 
	 * @param dataA
	 * @param dataB
	 * @param samplingRatio
	 * @return
	 */
	public static double compareTwoImageData(ImageData dataA, ImageData dataB,
			double samplingRatio) {

		assert dataA.width == dataB.width;
		assert dataA.height == dataB.height;

		int samplingSize = (int) (samplingRatio * dataA.width * dataA.height);
		byte[] bArrA = dataA.data;
		byte[] bArrB = dataB.data;

		assert bArrA.length == bArrB.length; // must be equal length

		// take n random samples
		Random r = new Random(System.currentTimeMillis());
		int[] idxs = new int[samplingSize];
		for (int i = 0; i < samplingSize; i++) {
			idxs[i] = r.nextInt(bArrA.length);
		}

		int matches = 0;

		// compare the chosen indexes of the two images
		for (int i = 0; i < samplingSize; i++) {
			if (bArrA[i] == bArrB[i]) {
				matches++;
			}
		}
		return matches / samplingSize;
	}

	/**
	 * 
	 * Compares two images and returns based on radom sampling with sampling
	 * size N.
	 * 
	 * Returns the ratio of matched pixels per comparisons
	 * 
	 */
	public static double compareTwoImages(Image imgA, Image imgB,
			double samplingRatio) {
		ImageData dataA = imgA.getImageData();
		ImageData dataB = imgB.getImageData();

		if (!equalsInSize(imgA, imgB)) {
			return -1d; // must match in size...
		}
		return compareTwoImageData(dataA, dataB, samplingRatio);
	}

	private static boolean equalsInSize(Image imgA, Image imgB) {

		Rectangle r1 = imgA.getBounds();
		Rectangle r2 = imgB.getBounds();

		if (r1.width != r2.width || r1.height != r2.height) {
			return false;
		}
		return true;
	}

	/**
	 * Given an arbitrary rectangle, get the rectangle with the given transform.
	 * The result rectangle is positive width and positive height.
	 * 
	 * @param af
	 *            AffineTransform
	 * @param src
	 *            source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle transformRect(AffineTransform af, Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		src = absRect(src);
		Point p1 = new Point(src.x, src.y);
		p1 = transformPoint(af, p1);
		dest.x = p1.x;
		dest.y = p1.y;
		dest.width = (int) (src.width * af.getScaleX());
		dest.height = (int) (src.height * af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary rectangle, get the rectangle with the inverse given
	 * transform. The result rectangle is positive width and positive height.
	 * 
	 * @param af
	 *            AffineTransform
	 * @param src
	 *            source rectangle
	 * @return rectangle after transform with positive width and height
	 */
	public static Rectangle inverseTransformRect(AffineTransform af,
			Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		src = absRect(src);
		Point p1 = new Point(src.x, src.y);
		p1 = inverseTransformPoint(af, p1);
		dest.x = p1.x;
		dest.y = p1.y;
		dest.width = (int) (src.width / af.getScaleX());
		dest.height = (int) (src.height / af.getScaleY());
		return dest;
	}

	/**
	 * Given an arbitrary point, get the point with the given transform.
	 * 
	 * @param af
	 *            affine transform
	 * @param pt
	 *            point to be transformed
	 * @return point after tranform
	 */
	public static Point transformPoint(AffineTransform af, Point pt) {
		Point2D src = new Point2D.Float(pt.x, pt.y);
		Point2D dest = af.transform(src, null);
		Point point = new Point((int) Math.floor(dest.getX()),
				(int) Math.floor(dest.getY()));
		return point;
	}

	/**
	 * Given an arbitrary point, get the point with the inverse given transform.
	 * 
	 * @param af
	 *            AffineTransform
	 * @param pt
	 *            source point
	 * @return point after transform
	 */
	public static Point inverseTransformPoint(AffineTransform af, Point pt) {
		Point2D src = new Point2D.Float(pt.x, pt.y);
		try {
			Point2D dest = af.inverseTransform(src, null);
			return new Point((int) Math.floor(dest.getX()),
					(int) Math.floor(dest.getY()));
		} catch (Exception e) {
			e.printStackTrace();
			return new Point(0, 0);
		}
	}

	/**
	 * Given arbitrary rectangle, return a rectangle with upper-left start and
	 * positive width and height.
	 * 
	 * @param src
	 *            source rectangle
	 * @return result rectangle with positive width and height
	 */
	public static Rectangle absRect(Rectangle src) {
		Rectangle dest = new Rectangle(0, 0, 0, 0);
		if (src.width < 0) {
			dest.x = src.x + src.width + 1;
			dest.width = -src.width;
		} else {
			dest.x = src.x;
			dest.width = src.width;
		}
		if (src.height < 0) {
			dest.y = src.y + src.height + 1;
			dest.height = -src.height;
		} else {
			dest.y = src.y;
			dest.height = src.height;
		}
		return dest;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask,
					palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	/**
	 * 
	 * @param bufferedImage
	 * @return
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage
					.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0],
							pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage
					.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF,
						blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

}// ImageUtils
