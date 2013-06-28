package at.tuwien.prip.ocr;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * 
 * ImageUtils.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 30, 2012
 */
public class ImageUtils 
{

	/**
	 * 
	 * @param input
	 * @param threshold
	 * @return
	 */
	public static double[][] threshold (double[][] input, int threshold)
	{
		double[][] result = new double[input.length][input[0].length];
		for (int i=0; i<input.length; i++)
			for (int j=0; j<input[i].length; j++)
				if (input[i][j]<threshold)
					result[i][j] = 0d;
				else
					result[i][j] = 1d;
		return result;
	}
	/**
	 * Reads in an image as grayscale. Result is a 2d pixel array.
	 * @param fileName
	 */
	public static double[][] readImageFromFile (String fileName)
	{
		double[][] result = null;
		BufferedImage image = null;
		InputStream is = null;
		try 
		{
			// Read from an input stream as gray scale image<
			is = new BufferedInputStream(
					new FileInputStream(fileName));
			image = ImageIO.read(is);
			result = convertTo2Dgrayscale(image);
	
			is.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static int[] convertToPixelArray(BufferedImage image)
	{
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		return pixels;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static BufferedImage convertToGrayscale (BufferedImage input)
	{
		BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		output = op.filter(input, output);
		return output;
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	public static double[][] convertTo2DUsingGetRGB(BufferedImage image) 
	{
		int width = image.getWidth();
		int height = image.getHeight();
		double[][] result = new double[width][height];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				result[row][col] = image.getRGB(col, row);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static double[][] convertTo2Dgrayscale (BufferedImage image)
	{
		if (image.getType()!=ColorSpace.CS_GRAY)
		{
			image = convertToGrayscale(image);
		}
		int width = image.getWidth();
		int height = image.getHeight();
		double[][] result = new double[width][height];
		int[] pixels = image.getData().getPixels(
				0, 0, width, height, new int[width*height]);
		
		int j = 0;
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				result[row][col] = pixels[j];
				j++;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage stripAlpha(BufferedImage image)
	{
		try
		{
			BufferedImage raw_image=image;
			image = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), BufferedImage.TYPE_INT_RGB); //CHANGE THIS TO TYPE YOU NEED
			ColorConvertOp xformOp=new ColorConvertOp(null);
			xformOp.filter(raw_image, image);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return image;
	} 
}
