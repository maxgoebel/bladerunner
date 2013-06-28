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

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.file.FileUtils;

import com.sun.pdfview.PDFPage;

/**
 * 
 * Provides utility functions for PDF processing.
 * 
 * @author max
 * 
 */
public class PDFUtils {

	/**
	 * 
	 * Generate a thumbnail from a PDF document. Requires convert command and
	 * imagemagick library as well as poppler installed.
	 * 
	 * @param src
	 *            , the source file
	 * @param dest
	 *            , the destination file, a .png file type
	 * 
	 */
	public static Image generateThumb(File src, File dest, Rectangle dims,
			int page) {
		// convert -thumbnail x300 src[0] dest
		Image result = null;

		try {
			String args = "-thumbnail " + dims.width + "x" + dims.height + " "
					+ src + "[" + (page - 1) + "] " + dest + "";
			Process p = Runtime.getRuntime().exec("convert " + args);

			ErrorDump.debug(PDFUtils.class, args);

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			handleProcessOutput(p, false);

			java.net.URL url = URI.create("file:///" + dest.getPath()).toURL();
			ImageDescriptor idesc = ImageDescriptor.createFromURL(url);
			result = idesc.createImage();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * Convert a PDF document to an HTML document.
	 * 
	 * @param src
	 *            , the PDF document to be converted
	 * @param dest
	 *            , the HTML document to hold the conversion
	 */
	public static String pdftohtml(File src, File dest, boolean verbose) {
		String result = null;

		try {

			// rename file to tmp.pdf to avoid bad file name problems
			boolean success = false;
			File tmpFile = new File(dest, "tmp.pdf");
			if (tmpFile.exists()) {
				tmpFile.delete();
			}

			if (src.exists()) {
				try {
					success = FileUtils.copyFile(src, tmpFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// success = src.renameTo(tmpFile);
				if (!success) {
					// File was not successfully moved
					System.err.println("Error processing file: "
							+ src.getName());
					return null;
				} else {
					result = tmpFile.getAbsolutePath();
				}
			}

			// try to convert pdf to xml format
			String args = "-i -c -nodrm -xml -hidden";
			String process = "external/pdftohtml " + args + " " + tmpFile + "";
			Process proc = Runtime.getRuntime().exec(process);
			proc.waitFor();

			handleProcessOutput(proc, verbose);

			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);

			// Move generated files to new directory
			String fileRoot = result;
			fileRoot = fileRoot.substring(0, fileRoot.length() - 4);
			File genFile = new File(fileRoot + ".xml");
			success = false;

			if (genFile.exists()) {
				result = genFile.getAbsolutePath();
			}

		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * Handle the output of a process execution.
	 * 
	 * @param p
	 * @param verbose
	 */
	protected static void handleProcessOutput(Process p, boolean verbose) {
		try {
			String s = null;
			int i = 0;

			if (verbose) {
				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				// read the output from the command
				while ((s = stdInput.readLine()) != null) {
					if (i == 0) {
						System.out
								.println("Here is the standard output of the command:");
						i++;
					}
					System.out.println(s);
				}
			}

			// read any errors from the attempted command
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			i = 0;
			while ((s = stdError.readLine()) != null) {
				if (i == 0) {
					ErrorDump
							.debug(PDFUtils.class,
									"Here is the standard error of the command (if any):");
					i++;
				}
				ErrorDump.debug(PDFUtils.class, (s));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Image loadTiff(URL url) {
		ImageDescriptor idesc = ImageDescriptor.createFromURL(url);
		return idesc.createImage();
	}

	/**
	 * Extract the SWT image data from a PDF page.
	 * 
	 * @param page
	 * @return
	 */
	public static ImageData getImageFromPDFPage(PDFPage page, double zoom) {
		if (page == null) {
			return null;
		}

		if (zoom == 0)
			zoom = 1;

		Rectangle2D r2d = page.getBBox();
		double width = r2d.getWidth();
		double height = r2d.getHeight();
		// width /= 72.0;
		// height /= 72.0;
		// int res = (int) (Toolkit.getDefaultToolkit ().getScreenResolution ()
		// * zoom);
		// width *= res;
		// height *= res;

		width *= zoom;
		height *= zoom;

		return getImageFromPDFPage(page, Math.max(1, width),
				Math.max(1, height));
	}

	/**
	 * Extract the SWT image data from a PDF page with a given width and height.
	 * 
	 * @param page
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageData getImageFromPDFPage(PDFPage page, double width,
			double height) 
	{
		Rectangle2D r2d = page.getBBox();
		java.awt.Image img = page.getImage((int) width, (int) height, r2d,
				null, true, true);
		BufferedImage bimage = toBufferedImage(img);
		return convertToSWT(bimage);
	}

	/**
	 * Extract the SWT image data from a PDF page with a given clip box.
	 * 
	 * @param page
	 * @param width
	 * @param height
	 * @param clip
	 * @return
	 */
	public static ImageData getImageFromPDFPage(PDFPage page, int width, int height, Rectangle clip) 
	{
		java.awt.Image img = page.getImage(width, height, clip,	null, true, true);
		BufferedImage bimage = toBufferedImage(img);
		return convertToSWT(bimage);
	}
	
	/**
	 * This method returns a buffered image with the contents of an image.
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(java.awt.Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * This method returns true if the specified image has transparent pixels.
	 * 
	 * @param image
	 * @return
	 */
	public static boolean hasAlpha(java.awt.Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	/**
	 * Convert between an AWT to a SWT image.
	 * 
	 * @param bufferedImage
	 * @return
	 */
	static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage
					.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF,
							(rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
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

}// PDFUtils
