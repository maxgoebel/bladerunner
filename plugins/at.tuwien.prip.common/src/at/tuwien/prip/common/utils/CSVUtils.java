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
package at.tuwien.prip.common.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.common.datastructures.Dictionary;
import at.tuwien.prip.common.datastructures.TupleField;
import at.tuwien.prip.common.log.ErrorDump;
import au.com.bytecode.opencsv.CSVReader;

/**
 * CSVUtils.java
 *
 * 
 *
 * @author max
 * @date Jun 30, 2012
 */
public class CSVUtils 
{

	/**
	 * 
	 * Load the individual data files from the respective
	 * directory.
	 * @throws FileNotFoundException 
	 *
	 */
	public static Dictionary<String> loadCVSDataFile(String fileName) throws FileNotFoundException 
	{
		Dictionary<String> result = null;
		File file = new File(fileName);
		if (!file.exists())  throw new FileNotFoundException();

		CharsetDecoder decoder = Charset.forName("UTF8").newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		
		//load in comma-separated file
		ErrorDump.debug(CSVUtils.class, "[CSV] Loading in CVS dicitionary: " + fileName);
		long start = System.currentTimeMillis();
		CSVReader reader = 
				new CSVReader(
						new BufferedReader(
								new InputStreamReader (
										new FileInputStream(file),
										decoder)));

		Dictionary<String> dict = null;
		try 
		{
			@SuppressWarnings("unchecked")
			List<String[]> rows = (List<String[]>)reader.readAll();
			String[] header = rows.get(0);
			String[][] data = new String[rows.size()-1][header.length];
			for (int i=1; i<=rows.size()-1; i++) 
			{
				data[i-1] = rows.get(i);
			}
			dict = new Dictionary<String> (header, data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dict!=null) {
			result = dict;
		}
		ErrorDump.debug(CSVUtils.class, 
				"[CSV] Done in %d ms", System.currentTimeMillis()-start);
		return result;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static TupleField<String> loadCSV(String fileName, String encoding) 
	{
		TupleField<String> result = null;


		List<String> content = readFiletoList(fileName);

		String headerLine = content.get(0);
		//identify separator
		String separator = "\\s";
		if (headerLine.contains(";")) {
			separator = ";";
		} else if (headerLine.contains(",")) {
			separator = ",";
		} else if (headerLine.contains("\\t")) {
			separator = "\\t";
		}
		String[] index = headerLine.split(separator);
		result = new TupleField<String>(index);

		for (int i=1; i<content.size(); i++) {
			String[] row = content.get(i).split(separator);
			result.addRow(index, row, "?");
		}
		
		return result;
	}
		
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<String> readFiletoList (String fileName) 
	{
		List<String> result = new ArrayList<String>();
		try
		{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null)   
			{
				result.add(strLine);
			}
			//Close the input stream
			in.close();
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Test Driver.
	 * @param args
	 */
	public static void main(String[] args)
	{	
		String fileName = "/home/max/exam.csv";
		TupleField<String> field = loadCSV(fileName, "");
		String val = field.queryData("Matr.Nr.", "0476321", "Pt2");
		
		System.out.println(val);
	}
}
