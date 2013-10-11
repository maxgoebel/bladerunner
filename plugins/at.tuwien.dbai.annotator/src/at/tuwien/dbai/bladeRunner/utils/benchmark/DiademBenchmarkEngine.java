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
package at.tuwien.dbai.bladeRunner.utils.benchmark;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory;
import at.tuwien.dbai.bladeRunner.utils.PdfDocumentProcessingException;
import at.tuwien.prip.common.datastructures.HashMap2List;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.Map2List;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.datastructures.Pair;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.common.utils.StringUtils;
import at.tuwien.prip.model.document.RectangleAdjustment;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.AnnotationType;
import at.tuwien.prip.model.project.annotation.LayoutAnnotation;
import at.tuwien.prip.model.project.annotation.PdfInstructionContainer;
import at.tuwien.prip.model.project.annotation.Region;
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.NodeSelection;
import at.tuwien.prip.model.project.selection.SinglePageSelection;
import at.tuwien.prip.model.project.selection.blade.ConceptSectionSelection;
import at.tuwien.prip.model.project.selection.blade.FunctionalSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.SectionSelection;
import at.tuwien.prip.model.project.selection.blade.SelectionContainer;
import at.tuwien.prip.model.project.selection.blade.SemanticSelection;
import at.tuwien.prip.model.project.selection.blade.TableCell;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.project.selection.blade.TextSelection;
import at.tuwien.prip.model.utils.DOMHelper;
import at.tuwien.prip.model.utils.DocGraphUtils;

/**
 * DiademBenchmarkEngine
 * 
 * A utility class to load the diadem benchmark.
 * 
 * 
 * @author mcg <mcgoebel@gmail.com> Aug 2, 2012
 */
public class DiademBenchmarkEngine {
	public static final String regEnding = "-reg.xml";
	public static final String fncEnding = "-fnc.csv";
	public static final String strEnding = "-str.xml";
	public static final String resultEnding = "-reg-result.xml";

	public static final String TAB = "   ";

	/**
	 * 
	 * @param benchmark
	 * @return
	 */
	public static String createReportString(Benchmark benchmark)
	{
		StringBuffer sb = new StringBuffer();
		for (BenchmarkDocument document : benchmark.getDocuments())
		{
			List<TableSelection> userTables = new ArrayList<TableSelection>();
			List<Annotation> userAnnotations = document.getAnnotations();
			for (Annotation ann : userAnnotations)
			{
				if (ann instanceof TableAnnotation)
				{
					TableAnnotation tann = (TableAnnotation)ann;
					List<AbstractSelection> sels = tann.getItems();
					for (AbstractSelection sel : sels)
					{
						if (sel instanceof TableSelection)
						{
							userTables.add((TableSelection) sel);
						}
					}
				}
			}

			List<TableSelection> gtTables = new ArrayList<TableSelection>();
			List<Annotation> gtAnnotations = document.getGroundTruth();
			for (Annotation ann : gtAnnotations)
			{
				if (ann instanceof TableAnnotation)
				{
					TableAnnotation tann = (TableAnnotation)ann;
					List<AbstractSelection> sels = tann.getItems();
					for (AbstractSelection sel : sels)
					{
						if (sel instanceof TableSelection)
						{
							gtTables.add((TableSelection) sel);
						}
					}
				}
			}

			Pair<Double,Double> purCom = calcPurityCompleteness((PdfBenchmarkDocument) document, null);
			double purity = purCom.getFirst();
			double completeness = purCom.getSecond();

			sb.append("\n\n================================================\n");
			sb.append("File: "+document.getFileName()+"\n");
			sb.append("Number of GT table regions: " + gtTables.size()+"\n");
			sb.append("Number of result table regions: " + userTables.size()+"\n");
			if (purity==-1)
			{
				sb.append("Purity: --\n");
			}
			else
			{
				sb.append("Purity: " + purity + "%\n");
			}
			if (completeness==-1)
			{
				sb.append("Completeness: --\n");
			}
			else
			{
				sb.append("Completeness: " + completeness + "%\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Write a report.
	 * @param benchmark
	 * @param fileName
	 */
	public static void writeReport(Benchmark benchmark, String outFileName) 
	{
		if (!outFileName.endsWith(".txt")) {
			outFileName = outFileName + ".txt";
		}
		File outFile = new File(outFileName);
		if (outFile.exists()) 
		{
			outFile.delete();
		}
		try 
		{
			new File(outFileName).createNewFile();
		} catch (IOException e1) {
			return;
		}

		// ready to write...
		try 
		{
			OutputStream fout = new FileOutputStream(outFileName);
			OutputStream bout = new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

			for (BenchmarkDocument document : benchmark.getDocuments())
			{
				List<TableSelection> userTables = new ArrayList<TableSelection>();
				List<Annotation> userAnnotations = document.getAnnotations();
				for (Annotation ann : userAnnotations)
				{
					if (ann instanceof TableAnnotation)
					{
						TableAnnotation tann = (TableAnnotation)ann;
						List<AbstractSelection> sels = tann.getItems();
						for (AbstractSelection sel : sels)
						{
							if (sel instanceof TableSelection)
							{
								userTables.add((TableSelection) sel);
							}
						}
					}
				}

				List<TableSelection> gtTables = new ArrayList<TableSelection>();
				List<Annotation> gtAnnotations = document.getGroundTruth();
				for (Annotation ann : gtAnnotations)
				{
					if (ann instanceof TableAnnotation)
					{
						TableAnnotation tann = (TableAnnotation)ann;
						List<AbstractSelection> sels = tann.getItems();
						for (AbstractSelection sel : sels)
						{
							if (sel instanceof TableSelection)
							{
								gtTables.add((TableSelection) sel);
							}
						}
					}
				}

				Pair<Double,Double> purCom = calcPurityCompleteness((PdfBenchmarkDocument) document, null);
				double purity = purCom.getFirst();
				double completeness = purCom.getSecond();

				out.write("\n\n================================================\n");
				out.write("File: "+document.getFileName()+"\n");
				out.write("Number of GT table regions: " + gtTables.size()+"\n");
				out.write("Number of result table regions: " + userTables.size()+"\n");
				if (purity==-1)
				{
					out.write("Purity: --\n");
				}
				else
				{
					out.write("Purity: " + purity + "%\n");
				}
				if (completeness==-1)
				{
					out.write("Completeness: --\n");
				}
				else
				{
					out.write("Completeness: " + completeness + "%\n");
				}
			}

			out.flush(); // Don't forget to flush!
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out
			.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static String unpackBenchmarkArchive(String fileName) {
		File input = new File(fileName);
		if (!input.exists()) {
			return null;
		}

		String parentDir = null;
		if (input.isFile() && fileName.endsWith(".zip")) 
		{
			try 
			{
				parentDir = unZip(fileName);
			} 
			catch (ZipException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (input.isFile() && fileName.endsWith(".tar.gz")) 
		{
			String destFile = fileName.substring(0,fileName.lastIndexOf("."));
			File dest = new File(destFile);
			try 
			{
				unTar(fileName, dest);
				parentDir = dest.getAbsolutePath();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return parentDir;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Benchmark processInfoFile(String dirName,
			final boolean validating) {
		Benchmark benchmark = null;
		String[] dirFiles = new File(dirName).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.equalsIgnoreCase("infoFile.xml")) {
					return true;
				}
				return false;
			}
		});

		if (dirFiles.length > 1) {
			String filename = dirFiles[0];

			try {
				DocumentBuilderFactory domFactory = DocumentBuilderFactory
						.newInstance();
				domFactory.setNamespaceAware(validating);
				domFactory.setValidating(validating);

				DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
				domBuilder.setErrorHandler(new ErrorHandler() {
					public void error(SAXParseException arg0)
							throws SAXException {
						if (validating)
							throw arg0; // pass through...
						return; // ignore
					}

					public void fatalError(SAXParseException arg0)
							throws SAXException {
						if (validating)
							throw arg0; // pass through...
						return; // ignore
					}

					public void warning(SAXParseException arg0)
							throws SAXException {
						// catch this...
					}
				});

				// parse info file
				Document doc = domBuilder.parse(filename);
				if (doc == null)
					return benchmark; // failsafe

				benchmark = new Benchmark();

				// read name
				String name = "unknown";
				List<Element> nameList = DOMHelper.Tree.Descendant
						.getNamedDescendantsAndSelfElements(
								doc.getDocumentElement(), "name");
				if (nameList != null && nameList.size() > 0) {
					name = nameList.get(0).getTextContent();
				}
				benchmark.setName(name);
			} catch (Exception e) {
				e.printStackTrace();
				if (validating)
					return null;
			}
		}

		return benchmark;
	}

	public static String error = null;
	public String getError() 
	{
		return null;
	}
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<String> loadBenchmarkFiles(String dirName)
	{
		error = null;
		List<String> pdfFiles = new ArrayList<String>();
		String innerDir = null;
		String[] pdfs = new File(dirName).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".pdf") || name.endsWith(".PDF")) {
					return true;
				}
				return false;
			}
		});

		if (pdfs.length==0)
		{
			error = "No PDFs found, trying to locate directories...";
			System.out.println(error);

			//look for inner dir
			String[] dirFiles = new File(dirName).list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (new File(dir.getAbsolutePath() + File.separator + name).isDirectory())
					{
						return true;
					}
					return false;
				}
			});

			if (dirFiles.length>=1)
			{
				pdfs = new File(dirName + File.separator + dirFiles[0]).list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith(".pdf") || name.endsWith(".PDF")) {
							return true;
						}
						return false;
					}
				});
				innerDir = dirFiles[0];
				System.out.println("Using subdirectory "+innerDir+ "("+pdfs.length+" PDFs found)");
			}
		}


		// load each PDF file in directory
		for (String dirFile : pdfs) {
			if (innerDir!=null)
			{
				String fileName = dirName + File.separator + innerDir + File.separator + dirFile;
				System.out.println("fileName "+fileName);
				pdfFiles.add(fileName);
			}
			else
			{
				String fileName = dirName + File.separator + dirFile;
				System.out.println("fileName "+fileName);
				pdfFiles.add(fileName);
			}
		}

		return pdfFiles;
	}

	/**
	 * 
	 * @param pdfFileString
	 */
	public static void processBenchmarkTableFile(String pdfFileString,
			Benchmark benchmark) 
	{
		// process document
		PdfBenchmarkDocument item = new PdfBenchmarkDocument();
		item.setUri(pdfFileString);

		// set file name
		String fileName = (String) pdfFileString.subSequence(
				pdfFileString.lastIndexOf(File.separatorChar) + 1,
				pdfFileString.length());
		item.setFileName(fileName);

		benchmark.getDocuments().add(item);

		// find respective model files and add as annotations
		String fileRoot = pdfFileString.substring(0,
				pdfFileString.lastIndexOf("."));

		List<TableAnnotation> tableAnnotations = new ArrayList<TableAnnotation>();

		// the region model
		File regFile = new File(fileRoot + regEnding);
		if (regFile.exists()) {
			TableAnnotation regAnno = parseRegionModel(regFile);
			tableAnnotations.add(regAnno);
			item.getGroundTruth().add(regAnno);
		}

		//process the result file if available
		//check for GT:
		File resFile = new File(fileRoot + resultEnding);
		if (resFile.exists())
		{
			//			TableAnnotation ann = 
			//					DiademBenchmarkEngine.parseRegionModel(resFile);
			Annotation ann = DiademBenchmarkEngine.loadLayoutAnnotation(fileRoot + resultEnding);
			item.getAnnotations().add(ann);
		}
	}

	/**
	 * 
	 * @param annos
	 * @return
	 */
	public static TableAnnotation mergeTableAnnotations(
			List<TableAnnotation> annos) {
		TableAnnotation result = new TableAnnotation("");
		MapList<Integer, TableSelection> tableMap = new HashMapList<Integer, TableSelection>();
		for (TableAnnotation ann : annos) {
			for (TableSelection table : ann.getTables()) {
				tableMap.putmore(table.getId(), table);
			}
		}

		for (int key : tableMap.keySet()) {
			TableSelection mergeTable = new TableSelection();
			MapList<Integer, AnnotationPage> pageMap = new HashMapList<Integer, AnnotationPage>();
			Map2List<Integer, Integer, RegionSelection> regionMap = new HashMap2List<Integer, Integer, RegionSelection>(
					null, null, null);

			List<TableSelection> tables = tableMap.get(key);
			for (TableSelection table : tables) {
				for (AnnotationPage page : table.getPages()) {
					pageMap.putmore(page.getPageNum(), page);
					for (AbstractSelection selection : page.getItems()) {
						if (selection instanceof RegionSelection) {
							RegionSelection regSel = (RegionSelection) selection;
							regionMap.putmore(page.getPageNum(),
									regSel.getId(), regSel);
						}
					}
				}
			}

			for (int pageNum : pageMap.keySet()) {
				AnnotationPage page = new AnnotationPage();
				page.setPageNum(pageNum);

				for (int id : regionMap.getSecondFromFirstKeys(pageNum)) {
					RegionSelection regSel = new RegionSelection();
					regSel.setId(id);

					List<RegionSelection> regions = regionMap.get(pageNum, id);
					for (RegionSelection region : regions) {
						regSel.getCellContainer().addAll(
								region.getCellContainer().getCells());
						regSel.getInstructionContainer().addAll(
								region.getInstructionContainer()
								.getInstructions());
					}
					regSel.setBounds(computeBounds(regSel.getCellContainer()));
					page.getItems().add(regSel);
				}
				mergeTable.getPages().add(page);
			}

			result.getItems().add(mergeTable);
		}
		return result;
	}

	/**
	 * 
	 * @param cellContainer
	 * @return
	 */
	private static Rectangle computeBounds(TableCellContainer cellContainer) {
		Rectangle result = null;
		for (TableCell cell : cellContainer.getCells()) {
			if (result == null) {
				result = cell.getBounds();
			}
			result = result.union(cell.getBounds());
		}
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static LayoutAnnotation parseBladeFile (File input)
	{
		LayoutAnnotation result = new LayoutAnnotation();

		// parse document
		Document doc = parseDOM(input.getAbsolutePath(), false);
		Element root = doc.getDocumentElement();
		String fileName = null;
		if ("document".equals(root.getTagName())) {
			fileName = root.getAttribute("filename");
		}
		if (fileName == null) {
			return null;
		}

		List<AbstractSelection> selections = new ArrayList<AbstractSelection>();

		// parse the tables
		List<Element> tableElems = DOMHelper.Tree.Children
				.getNamedChildElements(root, "table");
		for (Element tableE : tableElems) 
		{
			TableSelection table = parseTableElement(tableE);
			selections.add(table);
		}

		// parse the sections
		String[] parseNames = new String[]{
				"SECTION", 
				"TABLE", 
				"FUNCTIONAL",
				"FIGURE",
		"LIST"};
		for (String parseName : parseNames)
		{
			List<Element> parseElems = DOMHelper.Tree.Children
					.getNamedChildElements(root, parseName);
			for (Element element : parseElems) 
			{
				AbstractSelection section = parseSelectionElement(element, true, parseName);
				selections.add(section);
			}
		}

		result.getItems().addAll(selections);
		return result;
	}

	/**
	 * parse the region model.
	 * 
	 * @param regFile
	 */
	public static TableAnnotation parseRegionModel(File regFile) 
	{
		TableAnnotation result = new TableAnnotation(regFile.getAbsolutePath());

		// parse document
		Document doc = parseDOM(regFile.getAbsolutePath(), false);
		Element root = doc.getDocumentElement();
		String fileName = null;
		if ("document".equals(root.getTagName())) {
			fileName = root.getAttribute("filename");
		}
		if (fileName == null) {
			return null;
		}

		// parse the tables
		List<TableSelection> tables = new ArrayList<TableSelection>();
		List<Element> tableElems = DOMHelper.Tree.Children
				.getNamedChildElements(root, "table");
		for (Element tableE : tableElems) 
		{
			TableSelection table = parseTableElement(tableE);
			tables.add(table);
		}

		result.getItems().addAll(tables);
		return result;
	}

	/**
	 * Parse a special element.
	 * @param element
	 * @return
	 */
	public static SectionSelection parseMultiPageElement(Element element)
	{
		SectionSelection result = new SectionSelection();
		int id = Integer.parseInt(element.getAttribute("id"));
		String type = element.getAttribute("type");

		//parse the regions
		// parse the regions
		List<Element> regionElems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "region");
		for (Element regionE : regionElems)
		{
			RegionSelection region = parseRegionElement(regionE);

			int pageNum = Integer.parseInt(regionE.getAttribute("page"));
			AnnotationPage page = null;
			for (AnnotationPage ap : result.getPages())
			{
				if (ap.getPageNum()==pageNum)
				{
					page = ap;
					break;
				}
			}
			if (page == null) 
				page = new AnnotationPage(pageNum);


			page.getItems().add(region);

			if (!result.getPages().contains(page))
				result.getPages().add(page);
		}
		result.setId(id);
		result.setSectionType(type);
		return result;
	}

	/**
	 * Parse a single page element.
	 * @param element
	 * @return
	 */
	public static AbstractSelection parseSelectionElement(Element element, boolean isMulti, String name)
	{
		AbstractSelection result = null;
		if (isMulti)
		{
			result = new MultiPageSelection(name);
		}
		else
		{
			result = new SinglePageSelection(name);
		}

		if (name=="image")
		{
			System.err.println();
		}

		//parse ID and type
		int id = Integer.parseInt(element.getAttribute("id"));
		String type = element.getAttribute("type");

		result.setId(id);

		List<AbstractSelection> children = new ArrayList<AbstractSelection>();

		//parse images
		List<Element> elems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "image");
		for (Element elem : elems)
		{
			children.add(parseSelectionElement(elem, false, "image"));
		}

		//parse captions
		elems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "caption");
		for (Element elem : elems)
		{
			children.add(parseSelectionElement(elem, false, "caption"));
		}

		//parse list items
		elems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "list item");
		for (Element elem : elems)
		{
			children.add(parseSelectionElement(elem, false, "list item"));
		}

		//parse the regions
		elems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "region");
		for (Element elem : elems)
		{
			AbstractSelection sel = parseRegionElement(elem);
			if (result instanceof SinglePageSelection && ((SinglePageSelection) result).getPageNum()==0 &&
					sel!=null && sel instanceof RegionSelection)
			{
				RegionSelection regSel = (RegionSelection) sel;
				((SinglePageSelection)result).setPageNum(regSel.getPageNum());
			}
			children.add(parseRegionElement(elem));		
		}

		//
		for (AbstractSelection selection : children)
		{			
			//multi page selection?
			if (result instanceof MultiPageSelection)
			{
				int pageNum = -1;
				if (selection instanceof SinglePageSelection)
				{
					SinglePageSelection sps = (SinglePageSelection) selection;
					pageNum = sps.getPageNum();
				}
				MultiPageSelection multi = (MultiPageSelection) result;
				AnnotationPage page = null;
				for (AnnotationPage ap : multi.getPages())
				{
					if (ap.getPageNum()==pageNum)
					{
						page = ap;
						break;
					}
				}
				if (page == null) 
					page = new AnnotationPage(pageNum);

				page.getItems().add(selection);

				if (!multi.getPages().contains(page))
					multi.getPages().add(page);

				multi.setName(type);
			}
			else if (result instanceof SinglePageSelection)
			{
				SinglePageSelection single = (SinglePageSelection) result;
				single.getItems().add(selection);
				single.setName(type);
			}
		}

		return result;
	}

	/**
	 * Parse a text element.
	 * @param element
	 * @return
	 */
	public static TextSelection parseTextElement(Element element)
	{
		TextSelection result = new TextSelection();
		int id = Integer.parseInt(element.getAttribute("id"));
		String type = element.getAttribute("type");

		List<Element> contents = DOMHelper.Tree.Children
				.getNamedChildElements(element, "content");
		Element textContent = contents.get(0);

		result.setTextContent(textContent.getTextContent());
		result.setTextType(type);
		result.setId(id);
		return result;
	}

	//	/**
	//	 * Parse a text element.
	//	 * @param element
	//	 * @return
	//	 */
	//	public static FigureSelection parseFigureElement(Element element)
	//	{
	//		FigureSelection result = new FigureSelection();
	//		int id = Integer.parseInt(element.getAttribute("id"));
	//		String type = element.getAttribute("type");
	//
	//		List<Element> contents = DOMHelper.Tree.Children
	//				.getNamedChildElements(element, "image");
	//		Element imgContent = contents.get(0);
	//
	//		contents = DOMHelper.Tree.Children
	//				.getNamedChildElements(element, "caption");
	//		Element captContent = contents.get(0);
	//		RegionSelection region = parseRegionElement(captContent);
	//		
	////		result.g
	////		result.setId(id);
	//		return result;
	//	}

	/**
	 * Parse a region element.
	 * @param element
	 * @return
	 */
	public static RegionSelection parseRegionElement(Element element)
	{	
		int regID = Integer.parseInt(element.getAttribute("id"));
		int pageNum = Integer.parseInt(element.getAttribute("page"));

		// parse the instructions
		List<PDFInstruction> instructions = new ArrayList<PDFInstruction>();
		List<Element> instrElems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "instruction");
		for (Element instruction : instrElems) {
			int instructionID = -1, subinstructionID = -1;
			String instrID = instruction.getAttribute("instr-id");
			if (instrID != null) {
				instructionID = Integer.parseInt(instrID);
				String subinstrID = instruction
						.getAttribute("subinstr-id");
				if (subinstrID != null && subinstrID.length() > 0) {
					subinstructionID = Integer.parseInt(subinstrID);
				}
				instructions.add(new PDFInstruction(instructionID,
						subinstructionID));
			}
		}

		//parse the bounding box
		List<Element> boundsElems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "bounding-box");
		Element bb = boundsElems.get(0);
		int x1 = Integer.parseInt(bb.getAttribute("x1"));
		int y1 = Integer.parseInt(bb.getAttribute("y1"));
		int x2 = Integer.parseInt(bb.getAttribute("x2"));
		int y2 = Integer.parseInt(bb.getAttribute("y2"));
		Rectangle bounds = new Rectangle(x1,y1,x2-x1,y2-y1);

		RegionSelection region = new RegionSelection();
		region.setId(regID);
		region.setPageNum(pageNum);
		region.setBounds(bounds);
		region.getInstructionContainer().getInstructions().addAll(instructions);

		//parse the text selection
		List<Element> texts = DOMHelper.Tree.Children
				.getNamedChildElements(element, "text");
		if (texts.size()>0)
		{
			Element textElem = texts.get(0);
			TextSelection tsel = parseTextElement(textElem);
			region.setText(tsel);
		}

		return region;
	}

	//	/**
	//	 * Parse a table element.
	//	 * @param element
	//	 * @return
	//	 */
	//	public static ImageSelection parseImageElement(Element element) 
	//	{
	//		int id = -1;
	//		id = Integer.parseInt(element.getAttribute("id"));
	//		
	//		ImageSelection image = new ImageSelection();
	//		image.setId(id);
	//
	//		List<RegionSelection> regions = new ArrayList<RegionSelection>();
	//
	//		// parse the regions
	//		List<Element> regionElems = DOMHelper.Tree.Children
	//				.getNamedChildElements(element, "region");
	//		for (Element regionE : regionElems)
	//		{
	//			RegionSelection region = parseRegionElement(regionE);
	//			regions.add(region);
	//
	//			int pageNum = Integer.parseInt(regionE.getAttribute("page"));
	//			AnnotationPage page = null;
	//			for (AnnotationPage ap : table.getPages())
	//			{
	//				if (ap.getPageNum()==pageNum)
	//				{
	//					page = ap;
	//					break;
	//				}
	//			}
	//			if (page == null) 
	//				page = new AnnotationPage(pageNum);
	//
	//
	//			page.getItems().add(region);
	//
	//			if (!table.getPages().contains(page))
	//				table.getPages().add(page);
	//		}
	//		return table;
	//	}

	/**
	 * Parse a table element.
	 * @param element
	 * @return
	 */
	public static TableSelection parseTableElement(Element element) 
	{
		int tableID = -1;
		String tableIDString = element.getAttribute("id");
		if (tableIDString != null && tableIDString.length() > 0)
			tableID = Integer.parseInt(element.getAttribute("id"));
		TableSelection table = new TableSelection();
		table.setId(tableID);

		List<RegionSelection> regions = new ArrayList<RegionSelection>();

		// parse the regions
		List<Element> regionElems = DOMHelper.Tree.Children
				.getNamedChildElements(element, "region");
		for (Element regionE : regionElems)
		{
			RegionSelection region = parseRegionElement(regionE);
			regions.add(region);

			int pageNum = Integer.parseInt(regionE.getAttribute("page"));
			AnnotationPage page = null;
			for (AnnotationPage ap : table.getPages())
			{
				if (ap.getPageNum()==pageNum)
				{
					page = ap;
					break;
				}
			}
			if (page == null) 
				page = new AnnotationPage(pageNum);


			page.getItems().add(region);

			if (!table.getPages().contains(page))
				table.getPages().add(page);
		}
		return table;
	}

	/**
	 * 
	 * @param outFile
	 */
	public static void writeTableBenchmark(PdfBenchmarkDocument document,
			String outDir) {
		String outFileName = outDir.substring(0, outDir.lastIndexOf("."));

		writeTableRegionFile(document, outFileName);
		writeTableStructureFile(document, outFileName);
	}

	/**
	 * 
	 * @param document
	 * @param outFileName
	 */
	public static void writeBladeRegionFile(BenchmarkDocument document, String outFileName)
	{
		outFileName = outFileName + regEnding;
		File outFile = new File(outFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			new File(outFileName).createNewFile();
		} catch (IOException e1) {
			return;
		}

		// ready to write...
		try 
		{
			OutputStream fout = new FileOutputStream(outFileName);
			OutputStream bout = new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

			/* write XML head */
			writeXMLHead(out);

			String fileName = outFileName.substring(
					outFileName.lastIndexOf("/") + 1, outFileName.length());
			out.write("<document filename='" + fileName + "' ref='"+document.getUri()+"' timestamp='"+StringUtils.getTimestamp()+"'>\r\n");

			for (Annotation annotation : document.getAnnotations()) 
			{
				if (annotation.getType() != AnnotationType.LAYOUT) {
					continue;
				}

				LayoutAnnotation layoutAnnotation = (LayoutAnnotation) annotation;
				List<AbstractSelection> items = layoutAnnotation.getItems();
				for (AbstractSelection item : items)
				{
					//CASE 1: region selection
					if (item instanceof RegionSelection)
					{
						RegionSelection region = (RegionSelection) item;
						out.write(regionSelection(region, false, TAB));
					}
					//CASE 2: text selection
					else if (item instanceof TextSelection)
					{
						TextSelection text = (TextSelection) item;
						out.write(textSelection(text, TAB ));
					}

					//CASE 3: multi page selection
					if (item instanceof MultiPageSelection)
					{
						out.write(multiPageSelection((MultiPageSelection) item, TAB));
					}
					//CASE 4: single page selection
					else if (item instanceof SinglePageSelection)
					{
						out.write(singlePageSelection((SinglePageSelection) item, TAB));
					}
					//CASE 5: selection container
					else if (item instanceof SelectionContainer)
					{
						SelectionContainer container = (SelectionContainer) item;
						out.write(containerSelection(container, TAB));
					}
				}
			}

			out.write("</document>\r\n");

			out.flush(); // Don't forget to flush!
			out.close();
		} 
		catch (UnsupportedEncodingException e) 
		{
			System.out
			.println("This VM does not support the Latin-1 character set.");
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	private static String getSelectionAttributes (AbstractSelection selection)
	{
		StringBuffer sb = new StringBuffer();

		if (selection instanceof SectionSelection)
		{
			SectionSelection section = (SectionSelection) selection;
			sb.append("type='"+section.getSectionType()+"'");
		}
		else if (selection instanceof ConceptSectionSelection)
		{
			ConceptSectionSelection section = (ConceptSectionSelection) selection;
			sb.append("type='"+section.getSectionConcept()+"'");
		}
		else if (selection instanceof TextSelection)
		{
			TextSelection text = (TextSelection) selection;
			sb.append("type='"+text.getTextType()+"'");
		}
		else if (selection instanceof FunctionalSelection)
		{
			FunctionalSelection functional = (FunctionalSelection) selection;
			sb.append("type='"+functional.getFunction()+"'");
		}
		else if (selection instanceof SemanticSelection)
		{
			SemanticSelection semantic = (SemanticSelection) selection;
			sb.append("type='"+semantic.getSemantic()+"'");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param sel
	 * @param indent
	 * @return
	 */
	private static String singlePageSelection(SinglePageSelection sel, String indent)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "<"+sel.getType()+" " +
				"id='" + (sel.getId() + 1) + "' " 
				+ getSelectionAttributes(sel) +">\r\n");

		for (AbstractSelection selection : sel.getItems())
		{
			if (selection instanceof RegionSelection)
			{
				RegionSelection region = (RegionSelection) selection;
				sb.append(regionSelection(region, false, indent + TAB));
			}
			else if (selection instanceof TextSelection)
			{
				TextSelection text = (TextSelection) selection;
				sb.append(textSelection(text, indent + TAB));
			}
			else if (selection instanceof SinglePageSelection)
			{
				SinglePageSelection ssel = (SinglePageSelection) selection;
				sb.append(singlePageSelection(ssel, indent + TAB));
			}
			else if (selection instanceof MultiPageSelection)
			{
				MultiPageSelection msel = (MultiPageSelection) selection;
				sb.append(multiPageSelection(msel, indent + TAB));
			}
			else
			{

				//other selections
				sb.append(TAB + TAB + "<"+selection.getType()+" id='"
						+ (selection.getId() + 1) + "'>\r\n");
				sb.append(TAB + TAB + "</"+selection.getType()+">\r\n");
				System.out.println();
			}
		}
		sb.append(indent + "</"+sel.getType()+">\r\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param sel
	 * @param indent
	 * @return
	 */
	private static String multiPageSelection(MultiPageSelection sel, String indent)
	{
		StringBuffer sb = new StringBuffer();

		sb.append(indent + "<"+sel.getType()+" " +
				"id='" + (sel.getId() + 1) + "' " 
				+ getSelectionAttributes(sel) +">\r\n");
		for (AnnotationPage page : sel.getPages()) 
		{
			for (AbstractSelection selection : page.getItems())
			{
				if (selection instanceof RegionSelection)
				{
					RegionSelection region = (RegionSelection) selection;
					sb.append(regionSelection(region, false, indent + TAB));
				}
				else if (selection instanceof TextSelection)
				{
					TextSelection text = (TextSelection) selection;
					sb.append(textSelection(text, indent + TAB));
				}
				else if (selection instanceof SinglePageSelection)
				{
					SinglePageSelection ssel = (SinglePageSelection) selection;
					sb.append(singlePageSelection(ssel, indent + TAB));
				}
				else if (selection instanceof MultiPageSelection)
				{
					MultiPageSelection msel = (MultiPageSelection) selection;
					sb.append(multiPageSelection(msel, indent + TAB));
				}
				else
				{
					//other selections
					sb.append(indent + TAB + "<"+selection.getType()+" id='"
							+ (selection.getId() + 1) + "' page='"
							+ page.getPageNum() + "'>\r\n");

					sb.append(indent + TAB + "</"+selection.getType()+">\r\n");
				}
			}
		}
		sb.append(indent + "</"+sel.getType()+">\r\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param text
	 * @param indent
	 * @return
	 */
	private static String textSelection (TextSelection text, String indent)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "<"+text.getType()+
				" id='" + (text.getId() + 1) +
				"' type='"+text.getTextType()+"'>\r\n");
		sb.append(indent + TAB + "<content>\r\n");
		String textContent = text.getTextContent();
		if (textContent!=null)
		{
			textContent = textContent.replaceAll("\n", "\n" + indent + TAB + TAB);
		}
		sb.append(indent + TAB + TAB + textContent + "\r\n");
		sb.append(indent + TAB + "</content>\r\n");
		sb.append(indent + "</"+text.getType()+">\r\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param container
	 * @param indent
	 * @return
	 */
	private static String containerSelection(SelectionContainer container, String indent)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "<container id='"
				+ (container.getId() + 1) + "'>\r\n");
		for(AbstractSelection selection : container.getSelections())
		{
			if(selection instanceof NodeSelection)
			{
				sb.append(nodeSelection((NodeSelection)selection, indent + TAB));
			}
		}
		sb.append(indent + "</container>\r\n");
		return sb.toString();
	}
	
	private static String nodeSelection(NodeSelection node, String indent)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "<node id='"
				+ (node.getId() + 1) + "'>\r\n");
		sb.append(indent + TAB + node.getTargetXPath() + "\r\n");
		sb.append(indent + "</node>\r\n");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param region
	 * @param indent
	 * @return
	 */
	private static String regionSelection (RegionSelection region, boolean instr, String indent)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "<region id='"
				+ (region.getId() + 1) + "' page='"+region.getPageNum()+"'>\r\n");

		if (instr)
		{
			PdfInstructionContainer container = region
					.getInstructionContainer();
			for (PDFInstruction instruction : container
					.getInstructions()) {
				if (instruction.getSubIndex() > 0) {
					sb.append(indent + TAB
							+ "<instruction instr-id='"
							+ instruction.getIndex()
							+ "' subinstr-id='"
							+ instruction.getSubIndex()
							+ "'/>\r\n");
				} else {
					sb.append(indent + TAB
							+ "<instruction instr-id='"
							+ instruction.getIndex()
							+ "'/>\r\n");
				}
			}
		}
		Rectangle b = region.getBounds();
		sb.append(indent + TAB
				+ "<bounding-box x1='" + b.x + "' y1='"
				+ b.y + "' x2='" + (b.x + b.width)
				+ "' y2='" + (b.y + b.height)
				+ "'/>\r\n");

		TextSelection text = region.getText();
		sb.append(textSelection(text, indent + TAB));
		sb.append(indent + "</region>\r\n");
		return sb.toString();
	}

	/**
	 * 
	 * @param document
	 * @param outFileName
	 */
	private static void writeTableRegionFile(BenchmarkDocument document,
			String outFileName) {
		outFileName = outFileName + regEnding;
		File outFile = new File(outFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			new File(outFileName).createNewFile();
		} catch (IOException e1) {
			return;
		}

		// ready to write...
		try {
			OutputStream fout = new FileOutputStream(outFileName);
			OutputStream bout = new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

			/* write XML head */
			writeXMLHead(out);

			String fileName = outFileName.substring(
					outFileName.lastIndexOf("/") + 1, outFileName.length());
			out.write("<document filename='" + fileName + "'>\r\n");

			for (Annotation annotation : document.getAnnotations()) {
				annotation.getItems();
				if (annotation.getType() != AnnotationType.TABLE) {
					continue;
				}
				//
				TableAnnotation tableAnnotation = (TableAnnotation) annotation;
				List<TableSelection> tables = tableAnnotation.getTables();
				for (TableSelection table : tables) {
					out.write(TAB + "<table id='" + (table.getId() + 1)
							+ "'>\r\n");
					//
					for (AnnotationPage page : table.getPages()) {
						for (AbstractSelection selection : page.getItems()) {
							if (selection instanceof RegionSelection) {
								RegionSelection region = (RegionSelection) selection;
								out.write(TAB + TAB + "<region id='"
										+ (region.getId() + 1) + "' page='"
										+ page.getPageNum() + "'>\r\n");

								PdfInstructionContainer container = region
										.getInstructionContainer();
								for (PDFInstruction instruction : container
										.getInstructions()) {
									if (instruction.getSubIndex() > 0) {
										out.write(TAB + TAB + TAB
												+ "<instruction instr-id='"
												+ instruction.getIndex()
												+ "' subinstr-id='"
												+ instruction.getSubIndex()
												+ "'/>\r\n");
									} else {
										out.write(TAB + TAB + TAB
												+ "<instruction instr-id='"
												+ instruction.getIndex()
												+ "'/>\r\n");
									}
								}
								Rectangle b = region.getBounds();
								out.write(TAB + TAB + TAB
										+ "<bounding-box x1='" + b.x + "' y1='"
										+ b.y + "' x2='" + (b.x + b.width)
										+ "' y2='" + (b.y + b.height)
										+ "'/>\r\n");
								out.write(TAB + TAB + "</region>\r\n");
							}
						}
					}
					out.write(TAB + "</table>\r\n");
				}
			}

			out.write("</document>\r\n");

			out.flush(); // Don't forget to flush!
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out
			.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param documen
	 * @param outFileName
	 */
	private static void writeTableStructureFile(BenchmarkDocument document,
			String outFileName) {
		outFileName = outFileName + strEnding;
		File outFile = new File(outFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			new File(outFileName).createNewFile();
		} catch (IOException e1) {
			return;
		}

		// ready to write...
		try {
			OutputStream fout = new FileOutputStream(outFileName);
			OutputStream bout = new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

			/* write XML head */
			writeXMLHead(out);

			String fileName = outFileName.substring(
					outFileName.lastIndexOf("/") + 1, outFileName.length());
			out.write("<document filename='" + fileName + "'>\r\n");

			for (Annotation annotation : document.getAnnotations()) {
				annotation.getItems();
				if (annotation.getType() != AnnotationType.TABLE) {
					continue;
				}

				TableAnnotation tableAnnotation = (TableAnnotation) annotation;
				List<TableSelection> tables = tableAnnotation.getTables();
				for (TableSelection table : tables) {
					out.write(TAB + "<table id='" + (table.getId() + 1)
							+ "'>\r\n");

					for (AnnotationPage page : table.getPages()) {
						for (AbstractSelection selection : page.getItems()) {
							if (selection instanceof RegionSelection) {
								RegionSelection region = (RegionSelection) selection;
								out.write(TAB
										+ TAB
										+ "<region id='"
										+ (region.getId() + 1)
										+ "' col-increment='0' row-increment='0'>\r\n");

								TableCellContainer container = region
										.getCellContainer();
								for (TableCell cell : container.getCells()) {
									out.write(TAB + TAB + TAB + "<cell id='"
											+ (cell.getId() + 1)
											+ "' startRow='"
											+ cell.getStartRow()
											+ "' startCol='"
											+ cell.getStartCol() + "' endRow='"
											+ cell.getEndRow() + "' endCol='"
											+ cell.getEndCol() + "'>\r\n");
									Rectangle b = cell.getBounds();
									out.write(TAB + TAB + TAB + TAB
											+ "<bounding-box x1='" + b.x
											+ "' y1='" + b.y + "' x2='"
											+ (b.x + b.width) + "' y2='"
											+ (b.y + b.height) + "'/>\r\n");
									out.write(TAB + TAB + TAB + TAB
											+ "<content>" + cell.getContent()
											+ "</content>\r\n");
									PdfInstructionContainer cont = cell
											.getInstructions();
									for (PDFInstruction instr : cont
											.getInstructions()) {
										out.write(TAB + TAB + TAB + TAB
												+ "<instruction instr-id='"
												+ instr.getIndex()
												+ "' subinstr-id='"
												+ instr.getSubIndex()
												+ "'/>\r\n");
									}
									out.write(TAB + TAB + TAB + "</cell>\r\n");
								}
								// PdfInstructionContainer container =
								// region.getInstructionContainer();
								// for (PDFInstruction instruction :
								// container.getInstructions())
								// {
								// if (instruction.getSubIndex()>0)
								// {
								// out.write(TAB + TAB + TAB +
								// "<instruction instr-id='"+instruction.getIndex()+"' subinstr-id='"+instruction.getSubIndex()+"'/>\r\n");
								// }
								// else
								// {
								// out.write(TAB + TAB + TAB +
								// "<instruction instr-id='"+instruction.getIndex()+"'/>\r\n");
								// }
								// }
								// Rectangle b = region.getBounds();
								// out.write(TAB + TAB +
								// "<bounding-box x1='"+b.x+"' y1='"+b.y+"' x2='"+(b.x+b.width)+"' y2='"+(b.y+b.height)+"'/>\r\n");
								out.write(TAB + TAB + "</region>\r\n");
							}
						}
					}
					out.write(TAB + "</table>\r\n");
				}
			}

			out.write("</document>\r\n");

			out.flush(); // Don't forget to flush!
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out
			.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void writeXMLHead(OutputStreamWriter out) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); // header
		// first
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Annotation loadLayoutAnnotation(String fileName) {
		return parseBladeFile(new File(fileName));
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static TableAnnotation loadTableAnnotation(String fileName) {
		return parseRegionModel(new File(fileName));
	}

	/**
	 * parse the functional model.
	 * 
	 * @param fncFile
	 */
	public static TableAnnotation parseFunctionModel(File fncFile) {
		TableAnnotation result = new TableAnnotation(fncFile.getAbsolutePath());
		return result;
	}

	/**
	 * parse the structure model.
	 * 
	 * @param strFile
	 */
	public static TableAnnotation parseStructureModel(File strFile) {
		// parse document
		Document doc = parseDOM(strFile.getAbsolutePath(), false);
		Element root = doc.getDocumentElement();
		String fileName = null;
		if ("document".equals(root.getTagName())) {
			fileName = root.getAttribute("filename");
		}

		if (fileName == null) {
			return null;
		}

		TableAnnotation result = new TableAnnotation(strFile.getAbsolutePath());

		Map<Integer, AnnotationPage> pageMap = new HashMap<Integer, AnnotationPage>();

		// parse the tables
		List<TableSelection> tables = new ArrayList<TableSelection>();
		List<Element> tableElems = DOMHelper.Tree.Children
				.getNamedChildElements(root, "table");
		for (Element tableE : tableElems) {
			int tableID = Integer.parseInt(tableE.getAttribute("id"));
			TableSelection table = new TableSelection();
			table.setId(tableID);
			List<Region> regions = new ArrayList<Region>();

			// parse the regions
			List<Element> regionElems = DOMHelper.Tree.Children
					.getNamedChildElements(tableE, "region");
			for (Element regionE : regionElems) {
				int regID = Integer.parseInt(regionE.getAttribute("id"));
				int pageNum = Integer.parseInt(regionE.getAttribute("page"));

				AnnotationPage page = pageMap.get(pageMap);
				if (page == null) {
					page = new AnnotationPage(pageNum);
					pageMap.put(pageNum, page);
				}

				Region region = new Region(pageNum, regID);

				int colIncrement = Integer.parseInt(regionE
						.getAttribute("col-increment"));
				int rowIncrement = Integer.parseInt(regionE
						.getAttribute("row-increment"));
				region.setColIncrement(colIncrement);
				region.setRowIncrement(rowIncrement);

				RegionSelection regionSel = new RegionSelection();
				regionSel.setPageNum(pageNum);
				regionSel.setId(regID);

				List<TableCell> cells = new ArrayList<TableCell>();

				// parse the cells
				List<Element> cellElems = DOMHelper.Tree.Children
						.getNamedChildElements(regionE, "cell");
				for (Element cellE : cellElems) {
					int cellID = Integer.parseInt(cellE.getAttribute("id"));
					int startRow = -1;
					if (!cellE.getAttribute("start-row").startsWith("-")) {
						startRow = Integer.parseInt(cellE
								.getAttribute("start-row"));
					}
					int startCol = -1;
					if (!cellE.getAttribute("start-col").startsWith("-")) {
						startCol = Integer.parseInt(cellE
								.getAttribute("start-col"));
					}
					int endCol = -1;
					if (cellE.getAttribute("end-col").length() > 0) {
						endCol = Integer
								.parseInt(cellE.getAttribute("end-col"));
					}

					TableCell cell = new TableCell(cellID);
					cell.setStartRow(startRow);
					cell.setStartCol(startCol);
					cell.setEndCol(endCol);

					Element bboxE = DOMHelper.Tree.Children
							.getNamedChildElements(cellE, "bounding-box")
							.get(0);
					if (bboxE != null) {
						int x1 = Integer.parseInt(bboxE.getAttribute("x1"));
						int x2 = Integer.parseInt(bboxE.getAttribute("x2"));
						int y1 = Integer.parseInt(bboxE.getAttribute("y1"));
						int y2 = Integer.parseInt(bboxE.getAttribute("y2"));
						cell.setBounds(new Rectangle(x1, y1, x2 - x1, y2 - y1));
					}

					Element contentE = DOMHelper.Tree.Children
							.getNamedChildElements(cellE, "content").get(0);
					if (contentE != null) {
						cell.setContent(contentE.getTextContent());
					}
					cells.add(cell);
				}
				region.setCells(cells);

				TableCellContainer cellContainer = new TableCellContainer();
				cellContainer.getCells().addAll(cells);
				regionSel.setCellContainer(cellContainer);

				pageMap.get(region.getPageNum()).getItems().add(regionSel);
				regions.add(region);
			}
			table.setPages(new ArrayList<AnnotationPage>(pageMap.values()));
			tables.add(table);
		}
		result.getItems().addAll(tables);
		return result;
	}

	/**
	 * Parse a XML document.
	 * 
	 * @param fileName
	 * @param validating
	 * @return
	 */
	public static Document parseDOM(final String fileName, final boolean validating) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(validating);
			domFactory.setValidating(validating);

			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			domBuilder.setErrorHandler(new ErrorHandler() {

				public void error(SAXParseException arg0) throws SAXException {
					if (validating)
						throw arg0; // pass through...
					return; // ignore
				}

				public void fatalError(SAXParseException arg0)
						throws SAXException {
					if (validating)
						throw arg0; // pass through...
					return; // ignore
				}

				public void warning(SAXParseException arg0) throws SAXException {
					// catch this...
				}

			});

			return domBuilder.parse(fileName);
		} 
		catch (Exception e) 
		{
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(
							Display.getDefault().getActiveShell(), 
							"Error", "Error parsing XML " + fileName);
				}
			});

			//			e.printStackTrace();
			if (validating)
				return null;
		}

		return null;
	}

	/**
	 * 
	 * @param tarFile
	 * @param dest
	 * @throws IOException
	 */
	public static void unTar (String tarFile, File dest)
			throws IOException 
			{

		String tarFileName = tarFile +".tar";
		FileInputStream instream= new FileInputStream(tarFile);
		GZIPInputStream ginstream =new GZIPInputStream(instream);
		FileOutputStream outstream = new FileOutputStream(tarFileName);
		byte[] buf = new byte[1024]; 
		int len;
		while ((len = ginstream.read(buf)) > 0) 
		{
			outstream.write(buf, 0, len);
		}
		ginstream.close();
		outstream.close();
		//There should now be tar files in the directory
		//extract specific files from tar
		TarArchiveInputStream myTarFile=new TarArchiveInputStream(new FileInputStream(tarFileName));
		TarArchiveEntry entry = null;
		int offset;
		FileOutputStream outputFile=null;

		//read every single entry in TAR file
		while ((entry = myTarFile.getNextTarEntry()) != null) 
		{
			//the following two lines remove the .tar.gz extension for the folder name
			String fileName = tarFile.substring(0, tarFile.lastIndexOf('.'));
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			File outputDir =  new File(tarFile).getParentFile();
			if(! outputDir.getParentFile().exists()){ 
				outputDir.getParentFile().mkdirs();
			}
			//if the entry in the tar is a directory, it needs to be created, only files can be extracted
			if(entry.isDirectory()){
				outputDir.mkdirs();
			}
			else
			{
				byte[] content = new byte[(int) entry.getSize()];
				offset=0;
				myTarFile.read(content, offset, content.length - offset);
				outputFile=new FileOutputStream(outputDir);
				outputFile.write(content);
				outputFile.close();
			}
		}
		//close the tar files, leaving the original .tar.gz and the extracted folders
		myTarFile.close();
			}

	/**
	 * Unzip a ZIP file.
	 * 
	 * @param zipFile
	 * @throws ZipException
	 * @throws IOException
	 */
	public static String unZip(String zipFile) 
			throws ZipException, IOException 
			{
		int BUFFER = 2048;
		File file = new File(zipFile);

		ZipFile zip = new ZipFile(file);
		String newPath = zipFile.substring(0, zipFile.length() - 4);

		new File(newPath).mkdir();
		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);
			// destFile = new File(newPath, destFile.getName());
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(
						zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}

			if (currentEntry.endsWith(".zip")) {
				// found a zip file, try to open
				unZip(destFile.getAbsolutePath());
			}
		}
		return newPath;
			}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Benchmark loadBenchmark(String fileName)
	{
		error = null; //reset error
		Benchmark benchmark = null;

		// extract ZIP archive if necessary
		String parentDir = null;
		if (fileName.endsWith(".zip") || fileName.endsWith(".ZIP") || fileName.endsWith(".tar.gz") || fileName.endsWith(".TAR.GZ"))
		{
			parentDir = DiademBenchmarkEngine.unpackBenchmarkArchive(fileName);
		}
		else if (new File(fileName).isDirectory())
		{
			parentDir = fileName;
		}
		else
		{
			//load from file

		}
		if (parentDir != null) 
		{
			// load info file
			benchmark = DiademBenchmarkEngine.processInfoFile(parentDir, false);
			if (benchmark == null) {
				// no info file found
				benchmark = new Benchmark();
				benchmark.setName(parentDir);
			}
			benchmark.setUri(fileName);

			System.out.println("parentDir: " + parentDir);

			// load benchmark PDF files
			List<String> pdfFiles = DiademBenchmarkEngine
					.loadBenchmarkFiles(parentDir);

			// process each PDF file
			for (String pdfFileName : pdfFiles) 
			{
				DiademBenchmarkEngine.processBenchmarkTableFile(pdfFileName,
						benchmark);
			}
		}
		else 
		{
			/* an individual file */
		}
		return benchmark;
	}

	/**
	 * Test driver.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);
		String fileName = "/home/max/docwrap/benchmark/eu-dataset2.zip";
		Benchmark b = DiademBenchmarkEngine.loadBenchmark(fileName);
		timer.stopTask(0);
		System.out.println(b.getUri() + ": " + b.getDocuments().size()
				+ " documents loaded in " + timer.getTimeMillis(0) + "ms");
	}

	/**
	 * 
	 * @param benchmark
	 * @return
	 */
	public static Pair<Double,Double> calcPurityCompleteness (PdfBenchmarkDocument document, RectangleAdjustment adj)
	{
		double numComplete = 0d;
		double numPure = 0d;

		int numResTables = 0;
		int numGtTables = 0;

		//count result tables
		for (Annotation an : document.getAnnotations())
		{
			if (an instanceof TableAnnotation)
			{
				TableAnnotation annotation = (TableAnnotation) an;
				numResTables += annotation.getTables().size();
			}
		}

		//map table associations
		MapList<TableSelection, TableSelection> assocMap = new HashMapList<TableSelection, TableSelection>();
		for (Annotation an : document.getGroundTruth())
		{
			if (an instanceof TableAnnotation)
			{
				TableAnnotation gtAnnotation = (TableAnnotation) an;
				for (TableSelection gtTable : gtAnnotation.getTables())
				{				
					for (AnnotationPage gtPage : gtTable.getPages())
					{
						for (AbstractSelection reg1 : gtPage.getItems())
						{
							numGtTables ++;
							RegionSelection gtTableRegion = (RegionSelection) reg1;

							//now we do the same on the result side
							for (Annotation an2 : document.getAnnotations())
							{
								TableAnnotation resAnnotation = (TableAnnotation) an2;
								for (TableSelection resTable : resAnnotation.getTables())
								{
									for (AnnotationPage resPage : resTable.getPages())
									{
										for (AbstractSelection reg2 : resPage.getItems())
										{
											RegionSelection resTableRegion = (RegionSelection) reg2;
											if (gtTableRegion.getBounds().intersects(resTableRegion.getBounds()))
											{
												assocMap.putmore(gtTable, resTable);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		//check each pair for purity / completeness scores
		for (TableSelection gtTable : assocMap.keySet())
		{
			List<Character> gtChars = getAllCharsFromTableSelection(gtTable, document, null);
			for (TableSelection resTable : assocMap.get(gtTable))
			{
				List<Character> resChars = getAllCharsFromTableSelection(resTable, document, adj);

				//check complete
				boolean isComplete = true;
				for (Character c : gtChars)
				{
					if (resChars.contains(c))
					{
						resChars.remove(c);
					}
					else
					{
						isComplete = false;
						break;
					}
				}

				if (isComplete)
				{
					numComplete ++;
				}

				//check pure
				if (resChars.size()==0)
				{
					numPure ++;
				}

				break; //only consider first match
			}
		}

		//finally, compute purity and completeness
		double purityScore = -1d;
		double completenessScore = -1d;

		if (numGtTables>0)
		{
			completenessScore = 0d;
			if (numComplete>0)
			{
				//Completeness = completely identified tables / total GT tables
				completenessScore = Math.min(1, (numComplete / numGtTables)) * 100;
			}
		}
		if (numResTables>0)
		{
			purityScore = 0d;
			if (numPure>0)
			{
				//Purity = purely identified tables / total identified tables
				purityScore = Math.min(1, (numPure / numResTables)) * 100;
			}
		}

		return new Pair<Double,Double>(purityScore,completenessScore);
	}

	/**
	 * 
	 * @param benchmark
	 * @return
	 */
	public static Pair<Double,Double> calcPurityCompleteness (PdfBenchmarkDocument document, int pageNum, RectangleAdjustment adj)
	{
		double numComplete = 0d;
		double numPure = 0d;

		int numResTables = 0;
		int numGtTables = 0;

		//count result tables
		for (Annotation an : document.getAnnotations())
		{
			if (an instanceof TableAnnotation)
			{
				TableAnnotation annotation = (TableAnnotation) an;
				numResTables += annotation.getTables().size();
			}
		}

		//map table associations
		MapList<TableSelection, TableSelection> assocMap = new HashMapList<TableSelection, TableSelection>();
		for (Annotation an : document.getGroundTruth())
		{
			Annotation gtAnnotation = (Annotation) an;
			//			for (TableSelection gtTable : gtAnnotation.getTables())
			//			{
			//
			//				for (AnnotationPage gtPage : gtTable.getPages())
			//				{
			//					if (gtPage.getPageNum()!=pageNum)
			//					{
			//						continue;
			//					}
			//					for (AbstractSelection reg1 : gtPage.getItems())
			//					{
			//						numGtTables ++;
			//						RegionSelection gtTableRegion = (RegionSelection) reg1;
			//
			//						//now we do the same on the result side
			//						for (Annotation an2 : document.getAnnotations())
			//						{
			//							if (an2 instanceof TableAnnotation)
			//							{
			//								TableAnnotation resAnnotation = (TableAnnotation) an2;
			//								for (TableSelection resTable : resAnnotation.getTables())
			//								{
			//									for (AnnotationPage resPage : resTable.getPages())
			//									{
			//										if (resPage.getPageNum()!=pageNum)
			//										{
			//											continue;
			//										}
			//										for (AbstractSelection reg2 : resPage.getItems())
			//										{
			//											RegionSelection resTableRegion = (RegionSelection) reg2;
			//											if (gtTableRegion.getBounds().intersects(resTableRegion.getBounds()))
			//											{
			//												assocMap.putmore(gtTable, resTable);
			//											}
			//										}
			//									}
			//								}
			//							}
			//						}
			//					}
			//				}
			//			}
		}

		//check each pair for purity / completeness scores
		for (TableSelection gtTable : assocMap.keySet())
		{
			List<Character> gtChars = getAllCharsFromTableSelection(gtTable, document, null);
			for (TableSelection resTable : assocMap.get(gtTable))
			{
				List<Character> resChars = getAllCharsFromTableSelection(resTable, document, adj);

				//check complete
				boolean isComplete = true;
				for (Character c : gtChars)
				{
					if (resChars.contains(c))
					{
						resChars.remove(c);
					}
					else
					{
						isComplete = false;
						break;
					}
				}

				if (isComplete)
				{
					numComplete ++;
				}

				//check pure
				if (resChars.size()==0)
				{
					numPure ++;
				}

				break; //only consider first match
			}
		}

		//finally, compute purity and completeness
		double purityScore = -1d;
		double completenessScore = -1d;

		if (numGtTables>0)
		{
			completenessScore = 0d;
			if (numComplete>0)
			{
				//Completeness = completely identified tables / total GT tables
				completenessScore = Math.min(1, (numComplete / numGtTables)) * 100;
			}
		}
		if (numResTables>0)
		{
			purityScore = 0d;
			if (numPure>0)
			{
				//Purity = purely identified tables / total identified tables
				purityScore = Math.min(1, (numPure / numResTables)) * 100;
			}
		}

		return new Pair<Double,Double>(purityScore,completenessScore);
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	public static List<PDFInstruction> getAllPdfInstructionsFromTableSelection(TableSelection selection)
	{
		List<PDFInstruction> result = new ArrayList<PDFInstruction>();

		for (AnnotationPage page : selection.getPages())
		{
			List<AbstractSelection> items = page.getItems();
			for (AbstractSelection item : items)
			{
				if (item instanceof RegionSelection)
				{
					RegionSelection region = (RegionSelection) item;

					PdfInstructionContainer container = region.getInstructionContainer();
					if (container.getInstructions().size()>0)
					{
						result.addAll(container.getInstructions());
					}
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	public static List<Character> getAllCharsFromTableSelection(
			TableSelection selection, 
			PdfBenchmarkDocument document, 
			RectangleAdjustment aa)
			{
		List<Character> result = new ArrayList<Character>();

		for (AnnotationPage page : selection.getPages())
		{
			List<AbstractSelection> items = page.getItems();
			for (AbstractSelection item : items)
			{
				if (item instanceof RegionSelection)
				{
					RegionSelection region = (RegionSelection) item;

					//					//consider adjustment
					//					if (aa!=null)
					//					{
					//						bounds = DocGraphUtils.applyRectangleAdjustment(bounds, aa, dg);
					//					}
					//					String text = DocGraphUtils.getTextUnderRegion(bounds, dg);
					//					TextSelection tsel = region.getText();
					//					if (tsel!=null)
					//					{
					//						String text = tsel.getText();
					//						if (text!=null)
					//						{
					//							char [] arr = text.toCharArray();
					//							for (Character c : arr)
					//							{
					//								result.add(c);
					//							}
					//						}
					//					}
					//					else
					//					{
					//lookup document graph
					ISegmentGraph dg = getGraphFromDocumentPage(document, page.getPageNum());
					Rectangle bounds = region.getBounds();

					bounds = DocGraphUtils.yFlipRectangle(bounds, dg);

					//consider adjustment
					if (aa!=null)
					{
						bounds = DocGraphUtils.applyRectangleAdjustment(bounds, aa, dg);
					}

					String text = DocGraphUtils.getTextUnderRegion(bounds, dg);
					if (text!=null)
					{
						char [] arr = text.toCharArray();
						for (Character c : arr)
						{
							result.add(c);
						}
					}
					//					}
				}
			}
		}
		return result;
			}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public static List<TableSelection> getAllResultTables (BenchmarkDocument document)
	{
		List<TableSelection> result = new ArrayList<TableSelection>();
		for (Annotation annotation : document.getAnnotations())
		{
			if (annotation instanceof TableAnnotation)
			{
				TableAnnotation tann = (TableAnnotation) annotation;
				for (TableSelection selection : tann.getTables())
				{
					result.add(selection);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public static List<TableSelection> getAllGtTables (BenchmarkDocument document)
	{
		List<TableSelection> result = new ArrayList<TableSelection>();
		for (Annotation annotation : document.getGroundTruth())
		{
			if (annotation instanceof TableAnnotation)
			{
				TableAnnotation tann = (TableAnnotation) annotation;
				for (TableSelection selection : tann.getTables())
				{
					result.add(selection);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param table
	 * @return
	 */
	public static List<RegionSelection> getAllRegionsFromTable (TableSelection table)
	{
		List<RegionSelection> result = new ArrayList<RegionSelection>();
		for (AnnotationPage page : table.getPages())
		{
			for (AbstractSelection selection : page.getItems())
			{
				if (selection instanceof RegionSelection)
				{
					result.add((RegionSelection) selection);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param document
	 * @param result
	 */
	public static void adjustBenchmarkDocument (
			PdfBenchmarkDocument document, RectangleAdjustment aa, boolean result)
	{
		List<TableSelection> tables = new ArrayList<TableSelection>();
		if (result)
		{
			tables = getAllResultTables(document);
		}
		else
		{
			tables = getAllGtTables(document);
		}

		for (TableSelection table : tables)
		{
			for (AnnotationPage page : table.getPages())
			{
				for (AbstractSelection selection : page.getItems())
				{
					if (selection instanceof RegionSelection)
					{
						RegionSelection region = (RegionSelection) selection;
						Rectangle b = region.getBounds();
						int xoff = aa.getxOffset();
						int yoff = aa.getyOffset();
						double adjXScale = aa.getXScale();
						double adjYScale = aa.getYScale();

						double xScale = Math.abs(adjXScale);
						double yScale = Math.abs(adjYScale);

						if (xScale==0)
						{
							xScale = 1;
						}
						if (yScale==0)
						{
							yScale = 1;
						}
						float x1 = (float) (((b.x + xoff) * 1) * xScale);
						float x2 = (float) (((b.x + b.width + xoff) * 1) * xScale);
						float y1 = (float) (((b.y + yoff) * 1) * yScale);
						float y2 = (float) (((b.y + b.height + yoff) * 1) * yScale);

						ISegmentGraph dg = null;
						java.awt.Rectangle rectangle = new java.awt.Rectangle((int)x1,(int)y1,(int)(x2-x1),(int)(y2-y1));
						if (adjXScale<0)
						{
							//flip horizontal along page
							dg = getGraphFromDocumentPage(document, page.getPageNum());
							rectangle = DocGraphUtils.xFlipRectangle(rectangle, dg);
						}
						if (adjYScale<0)
						{
							//flip vertical along page
							if (dg==null)
							{
								dg = getGraphFromDocumentPage(document, page.getPageNum());
							}
							rectangle = DocGraphUtils.yFlipRectangle(rectangle, document.getPage(page.getPageNum()).getGraph());
						}

						region.setBounds(rectangle);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param doc
	 * @param pageNum
	 * @return
	 */
	public static ISegmentGraph getGraphFromDocumentPage (PdfBenchmarkDocument doc, int pageNum)
	{
		ISegmentGraph dg = null;
		PdfDocumentPage pg = doc.getPage(pageNum);
		if (pg!=null)
		{
			dg = pg.getGraph();
		}
		if (dg==null)
		{
			try 
			{
				dg = DocumentGraphFactory.PDF.generateDocumentGraphNew(doc.getUri(), pageNum);
			} 
			catch (PdfDocumentProcessingException e) 
			{
				e.printStackTrace();
			}
		}
		return dg;
	}
}


//def CalculatePurity( aGTTables, aCompTables):
//    #Purity = purely identified tables / total _identified_ tables 
//    
//    comp_tables = len( aCompTables)
//    pure_tables = 0
//       
//    #Count pure tables
//    for comp_table in aCompTables:
//        if debug():
//            print "Table id %d, P: %d, %d" % ( comp_table.table_id, comp_table.pure_elems, len( comp_table.elements))
//        if comp_table.pure_elems == len( comp_table.elements):            
//            pure_tables += 1
//    
//            
//    purity = 0.0
//        
//    #avoid division by zero    
//    if comp_tables != 0:
//        purity = float( pure_tables)/float( comp_tables)  
//         
//    print "PURITY: %d/%d = %f" % (pure_tables, comp_tables, purity),
//    print ("" if comp_tables != 0 else " (null)")
//            
//    return [purity,comp_tables]
//
//
//def CalculateCompleteness( aGTTables, aCompTables):
//    #Completeness = completely identified tables / total GT tables 
//      
//    GT_tables = len( aGTTables)
//    complete_tables = 0
//                 
//    #Count pure tables
//    for comp_table in aCompTables:
//
//        #skip if table is not associated at all
//        if comp_table.association < 0:
//            continue
//        
//        if debug():            
//            print "Table id %d, C: %d, %d" % ( comp_table.table_id, comp_table.pure_elems, len( aGTTables[ comp_table.association].elements))            
//        if comp_table.pure_elems == len( aGTTables[ comp_table.association].elements):            
//            complete_tables += 1            
//    
//            
//    completeness = 0.0
//            
//    #failsafe
//    if GT_tables != 0:        
//        completeness = float( complete_tables)/float( GT_tables)
//        
//    print "COMPLETENESS: %d/%d = %f" % (complete_tables, GT_tables, completeness),    
//    print ("" if GT_tables != 0 else " (null)")   
//    
//            
//    return [completeness,GT_tables]