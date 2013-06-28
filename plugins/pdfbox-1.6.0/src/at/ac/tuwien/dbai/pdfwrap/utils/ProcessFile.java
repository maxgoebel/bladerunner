/**
 * Copyright (c) 2003-2004, www.pdfbox.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdfbox; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.pdfbox.org
 *
 */
package at.ac.tuwien.dbai.pdfwrap.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import at.ac.tuwien.dbai.pdfwrap.analysis.PageProcessor;
import at.ac.tuwien.dbai.pdfwrap.exceptions.DocumentProcessingException;
import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFObjectExtractor;
import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFPage;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.IXHTMLSegment;
import at.tuwien.prip.model.document.segments.Page;
import at.tuwien.prip.model.graph.AdjacencyGraph;





/**
 * This is the main program that parses the pdf document and transforms it.
 * Based upon PDFBox code example from Ben Litchfield
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 * @author Ben Litchfield (ben@csh.rit.edu)
 */
public class ProcessFile
{
	// TODO: move somewhere sensible!  this is a global var, at least for GUI
	// moved to GUI 30.11.06
	//public static float XML_RESOLUTION = 150;


	/**
	 * This is the default encoding of the text to be output.
	 */
	public static final String DEFAULT_ENCODING =
		//null;
		//"ISO-8859-1";
		//"ISO-8859-6"; //arabic
		//"US-ASCII";
		"UTF-8";
	//"UTF-16";
	//"UTF-16BE";
	//"UTF-16LE";

	//private static Document resultDocument;

	/**
	 * The stream to write the output to.
	 */
	//protected static Writer output;

	// 27.12.08 changed to public due to GraphMatcher.java
	public static final String PASSWORD = "-password";
	public static final String ENCODING = "-encoding";
	public static final String CONSOLE = "-console";
	public static final String START_PAGE = "-startPage";
	public static final String END_PAGE = "-endPage";
	public static final String TABLE = "-table";
	public static final String AUTOTABLE = "-autotable";
	public static final String GECKO = "-gecko";
	public static final String XHTML = "-xhtml";
	public static final String NOBORDERS = "-noborders";
	public static final String RULINGLINES = "-rulinglines";

	private static boolean borders = false;

	public static String STR_INFILE = "";
	public static String STR_OUTPUT_PATH = ".";
	public static int STR_CURR_PAGE_NO = -1;

	public static PDDocument pdDoc;
	
	public static final String STR_IMAGE_PREFIX = "-imgPrefix";

	/**
	 * private constructor.
	 * publicized 5.03.06
	 */
	private ProcessFile()
	{

	}

	/**
	 * Process a PDF file.
	 * @param inFile
	 * @param processType
	 * @param rulingLines
	 * @param ignoreSpaces
	 * @param startPage
	 * @param endPage
	 * @param encoding
	 * @param password
	 * @param maxIterations
	 * @return
	 */
	public static List<Page> processFile (String inFile, 
			int processType,
			boolean computeEdges,
			boolean rulingLines, 
			boolean ignoreSpaces, 
			int startPage, int endPage, 
			String encoding, String password, 
			int maxIterations)
	{
		List<Page> result = new ArrayList<Page>();
		File inputDocFile = new File(inFile);
		
		try 
		{
			byte[] byteArr = ProcessFile.getBytesFromFile(inputDocFile);
			
			PDDocument pdDoc = loadPDDocument(byteArr, password);
			for (int i=startPage; i<=endPage; i++)
			{
				PDFPage<GenericSegment> pdfPage = loadPDFPage(pdDoc, i);
				result.add(processPage(
						pdfPage, processType, rulingLines, 
						ignoreSpaces, 
						computeEdges,
						maxIterations));
			}
		} catch (DocumentProcessingException e)
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Load a PDFPage from a document.
	 * @param pdDoc
	 * @param pageNum
	 * @return
	 */
	public static PDFPage<GenericSegment> loadPDFPage (PDDocument pdDoc, int pageNum)
	{
		PDFPage<GenericSegment> pdfPage = null;
		try 
		{
			PDFObjectExtractor extractor = new PDFObjectExtractor();
			pdfPage = extractor.getObject(pdDoc, pageNum);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return pdfPage;
	}
	
	/**
	 * Load a PDDocument from a  byte array.
	 * @param theFile
	 * @param password
	 * @return
	 * @throws DocumentProcessingException
	 */
	public static PDDocument loadPDDocument(byte[] theFile, String password) 
	throws DocumentProcessingException
	{
		PDDocument document = null;
		try 
		{
			ByteArrayInputStream inStream = new ByteArrayInputStream(theFile);
			document = PDDocument.load( inStream );

			if( document.isEncrypted() )
			{
				try
				{
					document.decrypt( password );
				}
				catch( InvalidPasswordException e )
				{
					if(!(password == null || password == ""))//wrong password
					{
						throw new DocumentProcessingException(
						"Error: The supplied password is incorrect.");
					}
					else
					{
						//no password and the default of "" was wrong.
						throw new DocumentProcessingException(
						"Error: The document is encrypted.");
					}
				} catch (CryptographyException e) {
					throw new DocumentProcessingException(e);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new DocumentProcessingException(e);
		} 

		finally {
			// move to finally block somewhere?
			if( document != null )
			{
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return document;
	}
	
	/**
	 * Process a single PDF page.
	 * @param pdfPage
	 * @param processType
	 * @param rulingLines
	 * @param ignoreSpaces
	 * @param maxIterations
	 * @return
	 */
	public static Page processPage (PDFPage<GenericSegment> pdfPage,
			int processType,
			boolean rulingLines, 
			boolean ignoreSpaces,
			boolean computeEdges,
			int maxIterations)
	{
		PageProcessor pp = new PageProcessor();

		//mcg: this is quicker...
		Page resultPage = pp.processPageNew(
				pdfPage, processType, rulingLines, 
				ignoreSpaces, 
				computeEdges,
				maxIterations);

		resultPage.setAdjGraph(pp.getAdjGraph());
		
		return resultPage;
	}

	/*
	 * possible conversions:
	 * pdf -> xml, pdf -> xhtml,
	 * gecko -> xml, gecko -> xhtml
	 */
	public static List<Page> processPDF (
			byte[] theFile, 
			int processType,
			PDDocument doc, 
			boolean computeEdges,
			boolean rulingLines, 
			boolean ignoreSpaces, 
			int startPage, int endPage, 
			String encoding, String password, 
			int maxIterations, 
			List<AdjacencyGraph<GenericSegment>> adjGraphList, 
			boolean GUI)
			throws DocumentProcessingException 	
			{

		List<Page> theResult = new ArrayList<Page>();

		boolean toConsole = false;
		if (password == null)
			password = "";
		if (encoding == null || encoding == "")
			encoding = DEFAULT_ENCODING;

		if (startPage == 0)
			startPage = 1;
		if (endPage == 0)
			endPage = Integer.MAX_VALUE;


		PDDocument document = null;

		try 
		{
			PDFObjectExtractor extractor = new PDFObjectExtractor();
			ByteArrayInputStream inStream = new ByteArrayInputStream(theFile);
			document = PDDocument.load( inStream );

			if( document.isEncrypted() )
			{
				try
				{
					document.decrypt( password );
				}
				catch( InvalidPasswordException e )
				{
					if(!(password == null || password == ""))//wrong password
					{
						throw new DocumentProcessingException(
						"Error: The supplied password is incorrect.");
					}
					else
					{
						//no password and the default of "" was wrong.
						throw new DocumentProcessingException(
						"Error: The document is encrypted.");
					}
				} catch (CryptographyException e) {
					throw new DocumentProcessingException(e);
				}
			}

			int currentPage = -1;

			int numPages = extractor.countNumPages(document);

			if (startPage==endPage) 
			{
				PDFPage<GenericSegment> pdfPage = extractor.getObject(document, startPage);
				PageProcessor pp = new PageProcessor();

				//mcg: this is quicker...
				Page resultPage = pp.processPageNew(
						pdfPage, processType, rulingLines, 
						ignoreSpaces, 
						computeEdges,
						maxIterations);

				//				Page resultPage = pp.processPage(
				//						pdfPage, processType, rulingLines, 
				//						ignoreSpaces, maxIterations);

				theResult.add(resultPage);
				if (adjGraphList != null) {
					adjGraphList.add(pp.getAdjGraph());
				}
				currentPage = extractor.countNumPages(document);
			} 
			else 
			{
				extractor.setStartPage( startPage );
				extractor.setEndPage( endPage );
				// stripper.writeText( document, output );

				List<PDFPage> thePages = extractor.getObjects(document);

				startPage = 0;
				endPage = numPages;//extractor.getEndPage();

				Iterator<PDFPage> pageIter = thePages.iterator();
				while(pageIter.hasNext())
				{
					currentPage ++;

					if (currentPage<startPage-1) {
						continue;//skip
					}

					if (currentPage>endPage-1) {
						continue;//skip
					}

					PDFPage<GenericSegment> thePage = pageIter.next();

					PageProcessor pp = new PageProcessor();
					Page resultPage = pp.processPage(
							thePage, 
							processType, 
							rulingLines, 
							ignoreSpaces, 
							computeEdges,
							maxIterations);
					theResult.add(resultPage);
					if (adjGraphList != null) {
						adjGraphList.add(pp.getAdjGraph());
					}
				}
			}

			if (startPage==endPage) {
				for (int i=1; i<numPages; i++) {
					theResult.add(new Page()); //placeholder for page count
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new DocumentProcessingException(e);
		} 

		finally {
			// move to finally block somewhere?
			if( document != null )
			{
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return theResult;
			}

	public static org.w3c.dom.Document processSinglePageToXMLDocument
	(Page resultPage, int toXHTML, boolean borders)
	throws DocumentProcessingException
	{
		List<Page> theResult = new ArrayList<Page>();
		theResult.add(resultPage);
		return processResultToXMLDocument(theResult, toXHTML, borders);
	}

	public static org.w3c.dom.Document processResultToXMLDocument
	(List<Page> theResult, int toXHTML, boolean borders)
	throws DocumentProcessingException
	{
		org.w3c.dom.Document resultDocument;

		// only used in the case of XHTML
		Element newBodyElement = null;
		Element docElement = null;

		// set up the XML file
		try
		{
			if (toXHTML == 1)
			{
				resultDocument = setUpXML("html");
				docElement = resultDocument.getDocumentElement();
				if (borders)
				{
					// add borders stuff here
					Element newHeadElement = resultDocument.createElement("head");
					Element newStyleElement = resultDocument.createElement("style");
					newStyleElement.setAttribute("type", "text/css");
					Text newTextElement = resultDocument.createTextNode
					("table {border-collapse: collapse;}");
					Text newTextElement2 = resultDocument.createTextNode
					("td, th {border: 1px solid grey; padding: 2px 4px;}");
					newStyleElement.appendChild(newTextElement);
					newStyleElement.appendChild(newTextElement2);
					newHeadElement.appendChild(newStyleElement);
					docElement.appendChild(newHeadElement);
				}
				newBodyElement = resultDocument.createElement("body");
			}
			else
			{
				resultDocument = setUpXML("PDFResult");
				docElement = resultDocument.getDocumentElement();
			}
		}
		catch (ParserConfigurationException e)
		{
			throw new DocumentProcessingException(e);
		}

		// add the new page element
		//docElement = resultDocument.getDocumentElement();

		int pageNo = 0;
		Iterator<Page> resultIter = theResult.iterator();
		while(resultIter.hasNext())
		{
			GenericSegment gs = (GenericSegment)resultIter.next();
			if (gs.getClass() == Page.class)
			{
				Page resultPage = (Page)gs;
				pageNo ++;
				if (toXHTML == 1)
				{
					resultPage.setPageNo(pageNo);
					resultPage.addAsXHTML(resultDocument, newBodyElement);
				}
				else
				{
					Element newPageElement = resultDocument.createElement("page");
					newPageElement.setAttribute("page_number", Integer.toString(pageNo));
					//we want to use the MediaBox!
					//resultPage.findBoundingBox();
					// ErrorDump.debug(this, "Result page: " + resultPage);
					if (toXHTML == 0)
					{
						resultPage.addAsXmillum(resultDocument, newPageElement, 
								resultPage, Utils.XML_RESOLUTION);

					}
					else if (toXHTML == 2)
					{
						resultPage.addAsJoinVision(resultDocument, newPageElement, 
								resultPage, Utils.XML_RESOLUTION);
					}
					docElement.appendChild(newPageElement);
				}
			}
			else if (gs instanceof IXHTMLSegment)//(gs.getClass() == Cluster.class || gs.getClass() == strRasterSegment.class)
			{
				IXHTMLSegment c = (IXHTMLSegment)gs;
				if (toXHTML == 1)
				{
					c.addAsXHTML(resultDocument, newBodyElement);
				}
				else if (toXHTML == 0)
				{
					// does not work due to page dimensions
					//	                gs.addAsXML(resultDocument, newBodyElement, 
					//	                	gs, Utils.XML_RESOLUTION);
				}
				else if (toXHTML == 2)
				{
					// does not work due to page dimensions
					//                	gs.addAsJoinVision(resultDocument, newBodyElement, 
					//	                	gs, Utils.XML_RESOLUTION);
				}
			}
			// run NG on page
			// output page (cluster-wise) to ontology
		}

		if (toXHTML == 1)
			docElement.appendChild(newBodyElement);

		return resultDocument;
	}

	public static org.w3c.dom.Document processPDFToXMLDocument(byte[] theFile,
			int toXHTML, boolean computeEdges, boolean borders, int processType, boolean rulingLines, 
			int startPage, int endPage, String encoding, String password)
	throws DocumentProcessingException
	{
		List<Page> theResult = processPDF(theFile, processType, null, 
				computeEdges,
				rulingLines,
				false, startPage, endPage, encoding, password, 0, null, false);

		return processResultToXMLDocument(theResult, toXHTML, borders);
	}

	public static byte[] processPDFToByteArray(byte[] theFile, int toXHTML, 
			int processType, boolean computeEdges,boolean rulingLines, boolean ignoreSpaces,
			int startPage, int endPage, String encoding, String password, 
			float thresholdX, float thresholdY)
	throws DocumentProcessingException
	{
		boolean table = false;
		if (processType == 2) table = true;

		// this.borders = borders;
		org.w3c.dom.Document resultDocument;
		// calls the above and returns a byte[] from the XML Document.
		List<Page> theResult =
			processPDF(theFile, processType, null, computeEdges, rulingLines, ignoreSpaces,
					startPage, endPage, encoding, password, 0, null, false);
		resultDocument = processResultToXMLDocument(theResult, toXHTML, borders);

		return serializeXML(resultDocument);
	}

	public static byte[] processPDFToByteArray(byte[] theFile, int toXHTML, boolean computeEdges, 
			boolean table, 
			boolean borders, int startPage, int endPage, String encoding, String password)
	throws DocumentProcessingException
	{
		// calls the above and returns a byte[] from the XML Document.

		org.w3c.dom.Document resultDocument =
			processPDFToXMLDocument(theFile, toXHTML, computeEdges, borders, PageProcessor.PP_BLOCK, true,
					startPage, endPage, encoding, password);

		return serializeXML(resultDocument);
	}

	public static byte[] processPDFToByteArray(byte[] theFile, int toXHTML, 
			boolean computeEdges, boolean table, boolean borders, boolean autotable,
			int startPage, int endPage, String encoding, String password)
	throws DocumentProcessingException
	{
		// calls the above and returns a byte[] from the XML Document.

		org.w3c.dom.Document resultDocument =
			processPDFToXMLDocument(theFile, toXHTML, computeEdges, borders, PageProcessor.PP_BLOCK, true,
					startPage, endPage, encoding, password);

		return serializeXML(resultDocument);
	}

	//  Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}


	/**
	 * Infamous main method.
	 *
	 * @param args Command line arguments, should be one and a reference to a file.
	 *
	 * @throws Exception If there is an error parsing the document.
	 */
	public static void main(String[] args) throws Exception
	{
		ErrorDump.debug(ProcessFile.class, "in main method");
		boolean toConsole = false;
		boolean table = false;
		boolean autotable = false;
		int toXHTML = 0;
		boolean borders = true;
		boolean rulingLines = false;
		int currentArgumentIndex = 0;
		String password = "";
		String encoding = DEFAULT_ENCODING;
		PDFObjectExtractor extractor = new PDFObjectExtractor();
		String inFile = null;
		String outFile = null;
		int startPage = 1;
		int endPage = Integer.MAX_VALUE;
		for( int i=0; i<args.length; i++ )
		{
			if( args[i].equals( PASSWORD ) )
			{
				i++;
				if( i >= args.length )
				{
					usage();
				}
				password = args[i];
			}
			else if( args[i].equals( ENCODING ) )
			{
				i++;
				if( i >= args.length )
				{
					usage();
				}
				encoding = args[i];
			}
			else if( args[i].equals( START_PAGE ) )
			{
				i++;
				if( i >= args.length )
				{
					usage();
				}
				startPage = Integer.parseInt( args[i] );
			}
			else if( args[i].equals( END_PAGE ) )
			{
				i++;
				if( i >= args.length )
				{
					usage();
				}
				endPage = Integer.parseInt( args[i] );
			}
			else if( args[i].equals( CONSOLE ) )
			{
				toConsole = true;
			}
			else if( args[i].equals( AUTOTABLE ))
			{
				autotable = true;
			}
			else if( args[i].equals( TABLE ))
			{
				table = true;
			}
			else if( args[i].equals( NOBORDERS ))
			{
				borders = false;
			}
			else if( args[i].equals( XHTML ) )
			{
				toXHTML = 1;
			}
			else if( args[i].equals( RULINGLINES ))
			{
				rulingLines = true;
			}
			else
			{
				if( inFile == null )
				{
					inFile = args[i];
				}
				else
				{
					outFile = args[i];
				}
			}
		}

		if( inFile == null )
		{
			usage();
		}

		if( outFile == null && inFile.length() >4 )
		{
			outFile = inFile.substring( 0, inFile.length() -4 ) + ".txt";
		}

		// decide whether we have a pdf or image (TODO: command-line override)
		boolean pdf = true;
		if (inFile.endsWith("png") ||
				inFile.endsWith("tif") ||
				inFile.endsWith("tiff")||
				inFile.endsWith("jpg") ||
				inFile.endsWith("jpeg")||
				inFile.endsWith("PNG") ||
				inFile.endsWith("TIF") ||
				inFile.endsWith("TIFF") ||
				inFile.endsWith("JPG") ||
				inFile.endsWith("JPEG")) pdf = false;

		//		System.err.println("Processing: " + inFile);

		// load the input file
		File inputFile = new File(inFile);
		STR_INFILE = inputFile.getCanonicalPath();
		File tempOutFile = new File(outFile); // tmp for str only
		if (tempOutFile.getParent() != null)
			STR_OUTPUT_PATH = tempOutFile.getParent();
		byte[] inputDoc = getBytesFromFile(inputFile);

		org.w3c.dom.Document resultDocument = null;

		// find the process type
		int processType = PageProcessor.PP_BLOCK;
		// do the processing
		resultDocument =
			processPDFToXMLDocument(inputDoc, toXHTML, true, borders,
					processType, rulingLines, startPage, endPage,
					encoding, password);

		// now output the XML Document by serializing it to output
		Writer output = null;
		if( toConsole )
		{
			output = new OutputStreamWriter( System.out );
		}
		else
		{
			if( encoding != null )
			{
				output = new OutputStreamWriter(
						new FileOutputStream( outFile ), encoding );
			}
			else
			{
				//use default encoding
				output = new OutputStreamWriter(
						new FileOutputStream( outFile ) );
			}
			//ErrorDump.debug(this, "using out put file: " + outFile);
		}
		//ErrorDump.debug(this, "resultDocument: " + resultDocument);
		serializeXML(resultDocument, output);

		if( output != null )
		{
			output.close();
		}
	}

	// this method to be called by lixto...
	// don't think so any more 4.11.06
	public static byte[] convertToXHTML(byte[] theFile, boolean table, boolean borders,
			boolean fromGecko, int startPage, int endPage, String encoding, 
			String password, float thresholdX, float thresholdY)
	throws DocumentProcessingException
	{
		// TODO: will we use the threshold modifiers or not?

		return processPDFToByteArray(theFile, 1, table, true, borders,
				startPage, endPage, encoding, password);

	}

	// try/catch moved to calling method 9.04.06
	protected static org.w3c.dom.Document setUpXML(String nodeName) 
	throws ParserConfigurationException
	{
		//try
		//{
		DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder myDocBuilder = myFactory.newDocumentBuilder();
		DOMImplementation myDOMImpl = myDocBuilder.getDOMImplementation();
		// resultDocument = myDOMImpl.createDocument("at.ac.tuwien.dbai.pdfwrap", "PDFResult", null);
		org.w3c.dom.Document resultDocument = 
			myDOMImpl.createDocument("at.ac.tuwien.dbai.pdfwrap", nodeName, null);
		return resultDocument;
		//}
		//catch (ParserConfigurationException e)
		//{
		//   e.printStackTrace();
		//   return null;
		//}

	}

	public static byte[] serializeXML(org.w3c.dom.Document resultDocument)
	throws DocumentProcessingException
	{
		// calls the above and returns a byte[] from the XML Document.

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try
		{
			Writer output = new OutputStreamWriter(outStream, DEFAULT_ENCODING);
			serializeXML(resultDocument, output);
		}
		catch (IOException e)
		{
			throw new DocumentProcessingException(e);
		}

		return outStream.toByteArray();
	}

	public static void serializeXML(org.w3c.dom.Document resultDocument, OutputStream outStream)
	throws DocumentProcessingException
	{
		try
		{
			Writer output = new OutputStreamWriter(outStream, DEFAULT_ENCODING);
			serializeXML(resultDocument, output);
		}
		catch (IOException e)
		{
			throw new DocumentProcessingException(e);
		}
	}

	public static void serializeXML
	(org.w3c.dom.Document resultDocument, Writer output)
	throws IOException
	{
		// The third parameter in the constructor method for
		// _OutputFormat_ controls whether indenting should be
		// used.  Unfortunately, I have found some bugs in the
		// indenting implementation that have corrupted the text
		// so I have switched it off. 

		OutputFormat myOutputFormat =
			new OutputFormat(resultDocument,
					"UTF-8",
					true);

		// output used to be replaced with System.out
		XMLSerializer s = 
			new XMLSerializer(output, 
					myOutputFormat);

		try {
			s.serialize(resultDocument);
			// next line added by THA 21.03.05
			output.flush();
		}
		catch (IOException e) {
			System.err.println("Couldn't serialize document: "+
					e.getMessage());
			throw e;
		}        

		// end of addition
	}

	/**
	 * This will print the usage requirements and exit.
	 */
	private static void usage()
	{
		System.err.println( "Usage: java at.ac.tuwien.dbai.pdfwrap.ProcessFile [OPTIONS] <PDF file> [Text File]\n" +
				"  -password  <password>        Password to decrypt document\n" +
				"  -encoding  <output encoding> (ISO-8859-1,UTF-16BE,UTF-16LE,...)\n" +
				"  -xhtml                       output XHTML (instead of XMillum-XML)\n" +
				"  -console                     Send text to console instead of file\n" +
				"  -startPage <number>          The first page to start extraction(1 based)\n" +
				"  -endPage <number>            The last page to extract(inclusive)\n" +
				"  <PDF file>                   The PDF document to use\n" +
				"  [Text File]                  The file to write the text to\n"
		);
		System.exit( 1 );
	}
}

// the above taken from: 
// http://userpage.fu-berlin.de/~ram/pub/pub_jf47htqHHt/java_sax_parser_en

/** utility class */

final class XML
{ /** create a new XML reader */
	final public static org.xml.sax.XMLReader makeXMLReader()  
	throws Exception 
	{ final javax.xml.parsers.SAXParserFactory saxParserFactory   =  
		javax.xml.parsers.SAXParserFactory.newInstance(); 
	final javax.xml.parsers.SAXParser        saxParser = saxParserFactory.newSAXParser(); 
	final org.xml.sax.XMLReader              parser    = saxParser.getXMLReader(); 
	return parser; }}

