package at.tuwien.prip.ocr;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

/**
 * Adapted from M. Thitithamasak.
 *
 * ConnectedComponent (8 connected), reads a grey scale image and perform thresholding 
 * with value = 43 and outputs a binary image. Then, this class will take the binary image
 * as an input and produces screen print out with labels. 
 * 
 * @author Max Goebel
 * @author Monkgol Thitithamasak 
 */
public class ConnectedComponent 
{
	public static ArrayList<ArrayList<Integer>> eqList;

	public static ArrayList<Integer> neighborArray;

	public static int currentLabel = 0;   

	/**
	 * 
	 * @param fileName
	 * @param threshold
	 * @param verbose
	 * @return
	 */
	public static List<Rectangle> connectedComponents(String fileName,
			int threshold, boolean verbose)
			{
		//read input file that contains pixel values
		double[][] img = ImageUtils.readImageFromFile(fileName);

		//run connected component analysis
		return connectedComponents(img, threshold, verbose);
			}

	/**
	 * 
	 * @param imgArr
	 * @param threshold
	 * @param verbose
	 * @return
	 */
	public static List<Rectangle> connectedComponents(double[][] imgArr, 
			int threshold, boolean verbose)
			{
		List<Rectangle> result = new ArrayList<Rectangle>();

		int width = imgArr.length;

		//threshold
		double[][] thresh = ImageUtils.threshold(imgArr, 45);
		if (verbose)
		{
			print("Binary Image: ");
			print("");
			print2DimensionArray(thresh);
		}

		//first pass
		double[][] firstPassImage = new double[width][width];
		firstPassImage = firstPass(thresh);
		if (verbose)
		{
			print("");
			print("First Pass Image: ");
			print("");
			print2DimensionArrayPretty(firstPassImage);
		}

		//second pass
		double[][] secondPassImage = new double[width][width];
		secondPassImage = secondPass(firstPassImage);
		if (verbose)
		{
			print("Second Pass Image: ");
			print("");
			print("");            
			print2DimensionArrayPretty(secondPassImage);
		}

		//extract connected components
//		result = thirdPass(secondPassImage);
		return result;
			}

	/**
	 * a b c
	 * d x
	 * scan from left to right and top to bottom
	 * for non-zeros values, if all neighbors are zeros, get a new label
	 * if all non-zeros neighbors have the same label, take the label from a 
	 * neighbor.
	 * if non-zeros neighbors have different labels, get the min label and 
	 * update equivalent table.
	 * 
	 * I implement equivalent table using adjacency list to simplify updating
	 * of multiple labels. I keep track of labels that are equal in the same list.
	 * To get min, I merely sort the list and pick the first item.
	 * 
	 * @param _image
	 * @return double[][]image
	 */    
	public static double[][] firstPass(double[][] _image)
	{       
		eqList = new ArrayList<ArrayList<Integer>>();

		int nw,n,ne,w,c = 0;
		for (int i = 0; i < _image.length; ++i)
		{           
			for (int j = 0; j < _image[0].length; ++j)
			{
				neighborArray = new ArrayList<Integer>();
				try
				{
					nw = (int)_image[i-1][j-1];
					neighborArray.add(nw);
				}
				catch(Exception e)
				{
					nw = 0;
				} 

				try
				{
					n = (int)_image[i-1][j];
					neighborArray.add(n);
				}
				catch(Exception e)
				{
					n = 0;
				} 

				try
				{
					ne = (int)_image[i-1][j+1];
					neighborArray.add(ne);
				}
				catch(Exception e)
				{
					ne = 0;
				} 


				try
				{
					w = (int)_image[i][j-1];
					neighborArray.add(w);
				}
				catch(Exception e)
				{
					w = 0;
				} 

				c = (int)_image[i][j];              

				if (c > 0)
				{
					if (nw == 0 && n == 0 && ne == 0 && w == 0)//case 1 : all are zeros
					{
						ArrayList<Integer> al = new ArrayList<Integer>();
						al.add(++currentLabel);
						eqList.add(al);               

						_image[i][j] = currentLabel;                      
						//                       print2DimensionArray(_image);
						//                       print("");
					}
					else if (!neighborArray.isEmpty() && nonZeroAreTheSameLabel(neighborArray))// case 2 : all the same values
					{                   
						_image[i][j] = findMin(neighborArray); //take value from one of the neighbors
						//                       print2DimensionArray(_image);
						//                       print("");
					}
					else if (!neighborArray.isEmpty() && !nonZeroAreTheSameLabel(neighborArray)) //case 3 : not all are the same
					{
						_image[i][j] = findMin(neighborArray);
						//                       print2DimensionArray(_image);
						//                       print("");
					}     
				}
			}
		}
		return _image;
	}

	/**
	 *   x a
	 * b c d
	 * 
	 * This method scans from right to left and bottom to top.
	 * only when pixel x is not zero, if all neighbors are zeros, do nothing
	 * if all non-zero neighbors have the same label, get label from a neighbor
	 * if neighbors have different labels, get min from neighbors and x
	 * and also look up equivalent table to get the same label with less value.
	 * 
	 * @param _image
	 * @return double[][] image
	 */
	public static double[][] secondPass(double[][] _image)
	{
		eqList = new ArrayList<ArrayList<Integer>>();

		int ea,sw,s,se,c = 0;
		for (int i = _image.length-1; i > 0 ; --i)
		{           
			for (int j = _image[0].length-1; j > 0; --j)
			{
				neighborArray = new ArrayList<Integer>();
				try
				{
					ea = (int)_image[i][j+1];
					neighborArray.add(ea);
				}
				catch(Exception e)
				{
					ea = 0;
				}                
				try
				{
					sw = (int)_image[i+1][j-1];
					neighborArray.add(sw);
				}
				catch(Exception e)
				{
					sw = 0;
				} 

				try
				{
					s = (int)_image[i+1][j];
					neighborArray.add(s);
				}
				catch(Exception e)
				{
					s = 0;
				} 

				try
				{
					se = (int)_image[i+1][j+1];
					neighborArray.add(se);
				}
				catch(Exception e)
				{
					se = 0;
				} 

				c = (int)_image[i][j];                            

				if (c > 0)
				{
					if (ea == 0 && sw == 0 && s == 0 && se == 0)//case 1 : all are zeros
					{
						//do nothing
					}
					else if (!neighborArray.isEmpty() && nonZeroAreTheSameLabel(neighborArray))// case 2 : all the same values
					{                   
						_image[i][j] = findMin(neighborArray); //take value from one of the neighbors
						//print2DimensionArray(_image);
						//print("");
					}
					else if (!neighborArray.isEmpty() && !nonZeroAreTheSameLabel(neighborArray)) //case 3 : not all are the same
					{
						neighborArray.add(c);
						_image[i][j] = findMinEqual(findMin(neighborArray));
						//print2DimensionArray(_image);
						//print("");
					}     
				}
			}
		}
		return _image;
	}

	/**
	 * Cluster connected zero-components into bins.
	 * @param _image
	 */
	public static List<Rectangle> thirdPass (double[][] image)
	{
		Stack<Coordinate> stack = new Stack<Coordinate>();
		List<Coordinate> done = new ArrayList<Coordinate>();

		List<CoordinateBin> bins = new ArrayList<CoordinateBin>();

		//scan over image
		for (int i = image.length-1; i > 0 ; --i)
		{           
			for (int j = image[0].length-1; j > 0; --j)
			{
				if (image[i][j]==0)
				{
					if (!done.contains(new Coordinate(i,j)))
					{
						CoordinateBin bin = new CoordinateBin();
						Coordinate current = new Coordinate(i,j);

						stack.add(current);

						while (!stack.isEmpty())
						{
							Coordinate top = stack.pop();
							bin.add(top);

							//look in all 16-neigh directions
							try
							{
								if (image[top.x-1][top.y-1]==0)
								{
									Coordinate c = new Coordinate(top.x-1, top.y-1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x-1][top.y]==0)
								{
									Coordinate c = new Coordinate(top.x-1, top.y);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x-1][top.y+1]==0)
								{
									Coordinate c = new Coordinate(top.x-1, top.y+1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x][top.y-1]==0)
								{
									Coordinate c = new Coordinate(top.x, top.y-1);
									if (!done.contains(c))
									{
										stack.add(c);
									}	
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try 
							{
								if (image[top.x][top.y+1]==0)
								{
									Coordinate c = new Coordinate(top.x, top.y+1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+1][top.y-1]==0)
								{
									Coordinate c = new Coordinate(top.x+1, top.y-1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+1][top.y]==0)
								{
									Coordinate c = new Coordinate(top.x+1, top.y);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+1][top.y+1]==0)
								{
									Coordinate c = new Coordinate(top.x+1, top.y+1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								//second 8 for 16 neighborhood
								if (image[top.x-2][top.y-1]==0)
								{
									Coordinate c = new Coordinate(top.x-2, top.y-1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x-2][top.y+1]==0)
								{
									Coordinate c = new Coordinate(top.x-2, top.y+1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+2][top.y-1]==0)
								{
									Coordinate c = new Coordinate(top.x+2, top.y-1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try{
								if (image[top.x+2][top.y+1]==0)
								{
									Coordinate c = new Coordinate(top.x+2, top.y+1);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x-1][top.y-2]==0)
								{
									Coordinate c = new Coordinate(top.x-1, top.y-2);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+1][top.y-2]==0)
								{
									Coordinate c = new Coordinate(top.x+1, top.y-2);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x-1][top.y+2]==0)
								{
									Coordinate c = new Coordinate(top.x-1, top.y+2);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}
							try
							{
								if (image[top.x+1][top.y+2]==0)
								{
									Coordinate c = new Coordinate(top.x+1, top.y+2);
									if (!done.contains(c))
									{
										stack.add(c);
									}
								}
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								//do nothing
							}

							done.add(top);
						}

						bins.add(bin);
						bin = new CoordinateBin();					
					}
				}
			}
		}

		//calculate dimensions of each bin
		List<Rectangle> result = new ArrayList<Rectangle>();
		for (CoordinateBin bin : bins)
		{
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			for (Coordinate c : bin)
			{
				if (c.x<minX)
				{
					minX = c.x;
				}
				if (c.x>maxX)
				{
					maxX = c.x;
				}
				if (c.y<minY)
				{
					minY = c.y;
				}
				if (c.y>maxY)
				{
					maxY = c.y;
				}
			}
			result.add(new Rectangle(minX, minY, maxX-minX, maxY-minY));
		}
		return result;
	}

	/**
	 * This method checks whether neighbors have the same label.
	 * @param al
	 * @return true if have the same values otherwise returns false.
	 */
	public static boolean nonZeroAreTheSameLabel(ArrayList<Integer> al)
	{
		boolean allTheSame = true;
		Collections.sort(al); //sort ascending
		for (int i = 0; i < al.size(); ++i)
		{
			if (i < al.size()-1 && al.get(i)!=0)
			{
				if (al.get(i)!= al.get(i+1))
				{
					allTheSame = false;
					break;
				}               
			}
		}
		return allTheSame;
	}

	/**
	 * This method traverse a neighbor list and return the minimum value.
	 * @param neighborArray
	 * @return int minimum value
	 */
	public static int findMin(ArrayList<Integer> neighborArray)
	{
		int min = 0;
		Collections.sort(neighborArray);
		for (int i =0; i < neighborArray.size(); ++i)
		{
			if (neighborArray.get(i)!=0)
			{
				min = neighborArray.get(i);
				break;
			}
		}
		return min;
	}

	/**
	 * this method takes label number as an input and look up the same label with
	 * less value.
	 * @param label
	 * @return equal min value
	 */
	public static int findMinEqual(double label)
	{
		int min = 0;       
		for (int i =0; i < eqList.size(); ++i)
		{
			if (eqList.get(i).get(0) == label)
			{
				Collections.sort(eqList.get(i));
				min = eqList.get(i).get(0);               
				break;
			}
		}
		return min;
	}

	public static void print(String s)
	{
		System.out.println(s);
	}

	/**
	 * I have this method but not using it because I think it costs more than
	 * handling exception for each neighbor value and assign 0 to it.
	 * 
	 * @param oriImage
	 * @return 
	 */
	public static double[][] enlargeImage(double[][] oriImage)
	{
		int _width = 0;
		int _height = 0;
		_height = oriImage.length+2 ;
		_width = oriImage[0].length+2 ;

		double[][] enlargeImage = new double[_width][_height];

		for (int i = 0; i < oriImage.length; ++i)
		{
			for (int j = 0; j < oriImage[0].length; ++j)
			{
				if (i == 0)
				{
					if (i==0 && j==0)
					{
						enlargeImage[0][0] = oriImage[0][0];
						enlargeImage[1][0] = oriImage[0][0];
					}
					enlargeImage[i][j+1] = oriImage[i][j];
					enlargeImage[i+1][j+1] = oriImage[i][j];
					if (i==0 && j==oriImage.length-1)
					{
						enlargeImage[0][enlargeImage[0].length-1] = oriImage[0][oriImage.length-1];
						enlargeImage[1][enlargeImage[0].length-1] = oriImage[0][oriImage.length-1];
					}                    
				}
				else //i!=0
				{
					if (j==0)
					{
						enlargeImage[i+1][j] = oriImage[i][j];
						enlargeImage[i+1][j+1] = oriImage[i][j];
					}
					else if (j==oriImage[0].length-1) //last column
					{
						enlargeImage[i+1][j+1] = oriImage[i][j];
						enlargeImage[i+1][j+2] = oriImage[i][j];
					}
					else
					{
						enlargeImage[i+1][j+1] = oriImage[i][j];                       
					}
				}

				if (i == oriImage.length-1)
				{
					if (i==oriImage.length-1 && j==0)
					{
						enlargeImage[enlargeImage.length-1][0] = oriImage[oriImage.length-1][0];
						enlargeImage[enlargeImage.length-2][0] = oriImage[oriImage.length-1][0];
					}
					enlargeImage[i+2][j+1] = oriImage[i][j];
					enlargeImage[i+1][j+1] = oriImage[i][j];
					if (i==oriImage.length-1 && j==oriImage.length-1)
					{
						enlargeImage[enlargeImage.length-1][enlargeImage[0].length-1] = oriImage[oriImage.length-1][oriImage.length-1];
						enlargeImage[enlargeImage.length-2][enlargeImage[0].length-1] = oriImage[oriImage.length-1][oriImage.length-1];
					}                    
				}
			}
		}
		return enlargeImage;
	}

	//	public static void writeToFile(String fileName,double[][] image) {
	//		try {
	//			File myFile = new File(fileName);
	//			myFile.delete();
	//			bWriter = new BufferedWriter(new FileWriter(fileName, true));
	//			for(int i = 0; i < image.length; ++i) 
	//			{               
	//				String oneLine="";
	//				for(int j = 0; j < image[1].length; ++j)
	//				{                   
	//					if (i==0 && j==0)
	//					{
	//						oneLine = image[1].length + " "+ (image.length-1);
	//						bWriter.write(oneLine);
	//						bWriter.newLine();
	//						bWriter.flush();
	//						oneLine ="";
	//					}
	//					oneLine += image[i][j]+" ";                                       
	//				}
	//
	//				bWriter.write(oneLine);
	//				bWriter.newLine();
	//				bWriter.flush();
	//			}
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
	public static void print2DimensionArray(double[][] twodimenstionArray)
	{
		for (int i = 0; i < twodimenstionArray.length; ++i)
		{
			for (int j = 0; j < twodimenstionArray[0].length; ++j)
			{
				System.out.print(String.valueOf((int)twodimenstionArray[i][j])+" ");
			}
			print("");
		}
	}
	public static void print2DimensionArrayPretty(double[][] twodimenstionArray)
	{
		for (int i = 0; i < twodimenstionArray.length; ++i)
		{
			for (int j = 0; j < twodimenstionArray[0].length; ++j)
			{

				if (twodimenstionArray[i][j]!=0)
				{
					System.out.print(String.valueOf((int)twodimenstionArray[i][j])+" ");
				}
				else
				{
					System.out.print("  ");
				}                
			}
			print("");
		}
	}
	//
	//	/**
	//	 * This method read a (text) file one line at a time.
	//	 * Split data on each line with " ". 
	//	 * The first  line must contain the image dimensions
	//	 * (width, height).
	//	 * use double[][] to store value in each pixel
	//	 * Continue reading until the end of the file.
	//	 * @param fileName
	//	 */
	//	public static void readFromFile(String fileName)
	//	{       
	//		try
	//		{
	//			fReader = new FileReader(fileName);
	//			bReader = new BufferedReader(fReader);
	//			String oneLine;
	//			int lineCount = 0;
	//
	//			while ((oneLine = bReader.readLine())!=null)
	//			{
	//				++lineCount; 
	//				if (lineCount==21)
	//				{
	//					//					int z = 0;
	//				}
	//				if (lineCount == 1)
	//				{
	//					String[]widthHeight = oneLine.trim().split(" ");
	//					width = Integer.parseInt(widthHeight[0]);
	//					height = Integer.parseInt(widthHeight[1]);
	//					//print("width : "+ width);
	//					//print("height : "+ height);
	//					ori2DImage = new double[height+2][width];
	//				}
	//				else
	//				{                   
	//					String[] pixelValueArray = new String[width];
	//					if (oneLine.length() != 0)
	//					{
	//						pixelValueArray = oneLine.split(" ");
	//
	//						for (int i = 0; i < pixelValueArray.length; ++i)
	//						{                            
	//							ori2DImage[lineCount-1][i]= Double.valueOf(pixelValueArray[i]);
	//						}
	//					}
	//				}
	//			}
	//		}//end try block
	//		catch(IOException e)
	//		{
	//			System.out.println(e);
	//		}//end catch
	//		catch (Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//		closeIO();
	//	}
	//
	//	/**
	//	 * This method closes IO after reading
	//	 */
	//	public static void closeIO()
	//	{
	//		try
	//		{
	//			fReader.close();
	//			//pWriter.close();
	//		}
	//		catch(IOException e)
	//		{
	//			e.printStackTrace();
	//		} 
	//	}

	/**
	 * This program takes no arguments in command line.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) 
	{
		int thresholdValue = 43;
		String fileName = "/home/max/Desktop/fraktur/templates/g01.gif"; //starTest.txt";
		ConnectedComponent.connectedComponents(fileName, thresholdValue, true);
	}
}
