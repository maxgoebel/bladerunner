package at.tuwien.prip.ocr;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.graph.Pixel;
import at.tuwien.prip.model.graph.PixelMatrix;
import at.tuwien.prip.model.utils.Orientation;

/**
 * OcrMain.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 30, 2012
 */
public class OcrEngine 
{

	public static void main(String[] args) 
	{
		String fileName = 
//						"/home/max/docwrap/fraktur/templates/v01.gif";
//					"/home/max/docwrap/fraktur/samples/wuerzburg_ABC-schuetz_1811-02-25.jpg";
			"/home/max/docwrap/fraktur/samples/excerpt.png";
//			"/home/max/docwrap/fraktur/confusingfraktur.jpg";
//					"/home/max/docwrap/fraktur/samples/title.gif";
		//			"/home/max/docwrap/negoescu09/south.jpg";
//		"/home/max/docwrap/cvs_tam/test-1.jpg";
		highlightCharacters(fileName);
		
//		findRois(fileName);


	}
	
	public static List<Rectangle> findRois (String input)
	{
		List<Rectangle> result = new ArrayList<Rectangle>();
		
		//create pixel matrix
		List<Pixel> pixels = new ArrayList<Pixel>();
		Picture pic = new Picture(input);
		int w=pic.width();
		int h=pic.height();
		for (int i=0; i<h; i++)
		{
			for (int j=0; j<w; j++)
			{
				Color color=pic.get(j,i);
				Pixel pixel = new Pixel(i, j, color.getRGB());
				pixels.add(pixel);
			}
		}
		PixelMatrix pm = new PixelMatrix(pixels);
		List<RectSegment> segments = 
			pm.getBoxesOfWhitespace(3, Orientation.BOTH);
		return result;
	}

	/**
	 * 
	 * @param input
	 */
	public static void highlightCharacters (String input)
	{
		//load image
		ImagePlus myImPlus = IJ.openImage(input);

		String output = "/home/max/docwrap/tmp.jpg";

		try
		{
			//preprocessing
			ImageConverter convert = new ImageConverter(myImPlus);
			convert.convertToGray16();
			
			ImageProcessor myIp = myImPlus.getProcessor();
			myIp.threshold(95);
//			myIp.medianFilter();
//			myIp.dilate();
//			myIp.erode();
			myIp.invert();
			IJ.save(myImPlus, output);
			
			myIp.setColor (Color.GREEN);
			
			List<Rectangle> rois = findCharacterCandidates(output);
			for (Rectangle roi : rois)
			{
				myIp.drawRect(roi.x, roi.y, roi.width, roi.height);
			}
			myIp.invert();
			Image img = myIp.createImage();
//			img.
//			myImPlus.updateAndDraw();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		myImPlus.show();		
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<Rectangle> findCharacterCandidates (String fileName)
	{
		List<Rectangle> result = new ArrayList<Rectangle>();
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);

		//		//load image
		//		double[][] imgArr = ImageUtils.readImageFromFile(fileName);
		////		ConnectedComponent.print2DimensionArrayPretty(imgArr);
		//
		//		//do connected component analysis
		//		result = ConnectedComponent.connectedComponents(imgArr, 43, false);
		//		
		//		//merge where areas too small

		//read the file to be scanned
		Picture pic = new Picture(fileName);
//		pic.show();

//		Picture pic2 = Img.blackwhite(fileName);
//		pic2.show();

		//create a matrix of the same dimension as the picture, storing
		//the labels that say to which object each pixel corresponds 
		int[][] a = new int[pic.height()][pic.width()];
		int k = 1;
		Stack s = new Stack();                  

		//floodfill until every pixel has been visited
		for (int i=0; i<pic.height(); i++)
		{
			for (int j=0; j<pic.width(); j++)
			{  
				if (a[i][j]==0) 
				{
					Color c=pic.get(j,i);
					if (c.equals(Color.WHITE))
					{
						s.push(i,j);
						Img.floodfillstack(pic, a, s, c,k);      
						k++;
					}
				}
			}
		}

		//generate the array of letters
		Letter[] letters=new Letter[k-1];
		letters = Img.extract(a,pic.height(),pic.width(),k);
		for (Letter letter : letters)
		{
			result.add(new Rectangle(letter.getX(), letter.getY(), letter.width(), letter.height()));
		}

		timer.stopTask(0);
		System.err.println("Candidate extraction in "+timer.getTimeMillis(0)+" ms. "+result.size()+" characters found...");
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String runOcr (String input)
	{		
		//load image
		ImagePlus myImPlus = IJ.openImage(input);

		//This command opens a window with the corresponding ImagePlus
		ImageProcessor myIp = myImPlus.getProcessor();
		myImPlus.updateAndDraw();

		//sharpen and binarize (if necessary)
		//		myIp.sharpen();

		//		myIp.invert();
		//		myIp.autoThreshold();
		myIp.threshold(95);
		myIp.medianFilter();



		//		myIp.dilate();
		myIp.dilate();
		myIp.erode();
		//		
		//		myIp.findEdges();
		//find region of interest
		Rectangle roi = myIp.getRoi();


		myIp.setColor (Color.GREEN);
		//		List<Rectangle> rois = findCharacterCandidates(input);
		//		for (Rectangle roi : rois)
		//		{
		myIp.drawRect(roi.x+2, roi.y-3, roi.width+2, roi.height+2);
		//		}

		myImPlus.show();		

		//		IJ.saveAs("Tiff", "/home/max/inverted.tiff");		//This method saves the active image in a given format (like Tiff) in the provided folder
		//		IJ.log("Completed "+myDir2+myListSources[Dzuzio]+"_Inverted");																//PRECAUTION! It will replace an image in the folder if it has the same name. It will not warn you of this
		//		myImPlus.close();								
		return "";
	}


}
