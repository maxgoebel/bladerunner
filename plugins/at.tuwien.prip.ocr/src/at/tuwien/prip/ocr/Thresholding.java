package at.tuwien.prip.ocr;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author m.thitithamasak
 */
public class Thresholding 
{

	public static double thresholdValue = 0.0;
	public static int width =0;
	public static int height = 0;
	private static BufferedReader bReader;
	//private static FileOutputStream fWriter;
	private static FileReader fReader;
	//   private static PrintWriter pWriter;

	/**
	 * 
	 */
	public static String thresholdOutBinaryImage(String fileName,
			double _thresholdValue, 
			int _width)
	{
		thresholdValue = _thresholdValue;
		width = _width;
		String outputFile="BinaryImage.txt";
		try
		{
			fReader = new FileReader(fileName);
			bReader = new BufferedReader(fReader);
			String oneLine;

			double[][] binaryImage = new double[width][width];
			int lineCount = 0;
			while ((oneLine = bReader.readLine())!=null)
			{
				++lineCount;
				String[] pixelValue = new String[width];
				if (oneLine.length() != 0)
				{
					if (lineCount == 1)
					{
						String[]widthHeight = oneLine.trim().split(" ");
						width = Integer.parseInt(widthHeight[0]);
						height = Integer.parseInt(widthHeight[1]);
						print("width : "+ width);
						print("height : "+ height);
						binaryImage = new double[height+1][width];
					}
					else
					{
						pixelValue = oneLine.split(" ");

						for (int i = 0; i < pixelValue.length; ++i)
						{
							if (lineCount == 45)
							{
								//                                int z = 0;
							}
							if (Double.parseDouble(pixelValue[i]) < thresholdValue)
							{
								binaryImage[lineCount-1][i]= 0;
							}
							else
							{
								binaryImage[lineCount-1][i]= 1;
							}
						}                      
						//                        System.out.println("");
					}
				}
			}
			//            ConnectedComponent cc = new ConnectedComponent();
//			ConnectedComponent.writeToFile(outputFile, binaryImage);
			
		}//end try block
		catch(IOException e)
		{
			System.out.println(e);
		}//end catch
		catch(Exception e)
		{
			e.printStackTrace();
		}
		closeIO();
		return outputFile;
	}

	public static void print(String s)
	{
		System.out.println(s);
	}

	public static void print(int i)
	{
		System.out.println(i);
	}

	/**
	 * This method close IO after reading
	 */
	public static void closeIO()
	{
		try
		{
			fReader.close();
			//pWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
