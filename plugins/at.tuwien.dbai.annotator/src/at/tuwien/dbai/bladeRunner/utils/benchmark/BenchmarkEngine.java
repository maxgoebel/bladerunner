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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import at.tuwien.dbai.bladeRunner.utils.ModelUtils2;
import at.tuwien.prip.common.exceptions.PropertyLoadException;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.BasePropertiesLoader;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.project.annotation.AnnotationLabel;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.AnnotationType;
import at.tuwien.prip.model.project.annotation.ExampleAnnotation;
import at.tuwien.prip.model.project.annotation.ExtractionAnnotation;
import at.tuwien.prip.model.project.annotation.LabelAnnotation;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.ExtractionItem;
import at.tuwien.prip.model.project.selection.ExtractionResult;
import at.tuwien.prip.model.project.selection.LabelSelection;
import at.tuwien.prip.model.project.selection.NodeSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.PdfSelection;
import at.tuwien.prip.model.utils.SegmentUtils;

/**
 * BenchmarkEngine.java
 * 
 * Generator for benchmark files...
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 15, 2011
 */
public class BenchmarkEngine {

	private static String BENCHMARK_ROOT = "";

	static {
		try {
			BENCHMARK_ROOT = BasePropertiesLoader.loadPath("benchmarkRoot");
		} catch (PropertyLoadException e) {
			e.printStackTrace();
		}
	}

	private static final String TAB = "   ";

	public static String getBenchmarkRootDirectory() {
		if (!new File(BENCHMARK_ROOT).exists()) {
			new File(BENCHMARK_ROOT).mkdirs();
		}
		return BENCHMARK_ROOT;
	}

	/**
	 * 
	 * The standard header
	 * 
	 * @param name
	 * @return
	 */
	protected static String getHeader(String name) {
		String header = "<!-- \n" + "This is the datafile for the\n"
				+ name
				+ "\n"
				+ "benchmark...\n"
				+ "Annotated are valid tables and fragments and extraction items\n"
				+ "Author Max Goebel\n"
				+ "TU Wien, Vienna, Austria\n"
				+ "\n"
				+ "Syntax:\n"
				+ "<benchmark>\n"
				+ TAB
				+ "<name></name>\n"
				+
				// TAB + "<url></url>" +
				TAB + "<examples>\n" + TAB + TAB + "<example>\n" + TAB + TAB
				+ TAB + "<document>\n" + TAB + TAB + TAB + TAB
				+ "<url></url>\n" + TAB + TAB + TAB + "</document>\n" + TAB
				+ TAB + TAB + "<annotation>\n" + TAB + TAB + TAB + TAB
				+ "<tables>\n" + TAB + TAB + TAB + TAB + TAB
				+ "<table class=''></table>\n" + TAB + TAB + TAB + TAB + TAB
				+ "<table class=''></table>\n" + TAB + TAB + TAB + TAB
				+ "</tables>\n" + TAB + TAB + TAB + TAB + "<extractions>\n"
				+ TAB + TAB + TAB + TAB + TAB
				+ "<extraction class=''></extraction>\n" + TAB + TAB + TAB
				+ TAB + TAB + "<extraction class=''></extraction>\n" + TAB
				+ TAB + TAB + TAB + "</extractions>\n" + TAB + TAB + TAB + TAB
				+ "<segments>\n" + TAB + TAB + TAB + TAB + TAB
				+ "<segment class=''></segment>\n" + TAB + TAB + TAB + TAB
				+ TAB + "<segment class=''></segment>\n" + TAB + TAB + TAB
				+ TAB + "</segments>\n" + TAB + TAB + TAB + TAB
				+ "<selection>\n" + TAB + TAB + TAB + TAB + TAB
				+ "<xptr class=''></xptr>\n" + TAB + TAB + TAB + TAB + TAB
				+ "<xptr class=''></xptr>\n" + TAB + TAB + TAB + TAB
				+ "</selection> \n" + TAB + TAB + TAB + "</annotation>\n" + TAB
				+ TAB + "</example>\n" + TAB + "</examples>\n" + "</benchmark>"
				+ "-->\n\n";
		return header;
	}

	/**
	 * 
	 * Write an 'empty' datafile.xml with only the template skeleton into the
	 * 'root' directory.
	 * 
	 * @param root
	 */
	public static void writeDatafileTemplate(String root) {
		writeBenchmark(root, new Benchmark(""));
	}

	/**
	 * 
	 * Create a benchmark datafile with a given name from the benchmark
	 * collection. Writes an XML file in the given root directory named
	 * datafile.xml.
	 * <p>
	 * <p>
	 * 
	 * !!!WARNING: this DELETES the old benchmark!!!
	 * <p>
	 * 
	 * @param root
	 */
	public static void writeBenchmark(String root, Benchmark benchmark) {
		File rootFile = new File(root);
		if (!rootFile.exists()) {
			try {
				rootFile.createNewFile();
			} catch (IOException e1) {
				return;
			}

			// ready to write...
			try {
				OutputStream fout = new FileOutputStream(root);
				OutputStream bout = new BufferedOutputStream(fout);
				OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

				/* write XML head */
				writeXMLHead(out, rootFile.getAbsolutePath(),
						benchmark.getName());

				List<BenchmarkDocument> documents = benchmark.getDocuments();
				for (BenchmarkDocument benchDoc : documents) {
					String url = benchDoc.getUri();

					out.write(TAB + TAB + "<example>\r\n");
					out.write(TAB + TAB + TAB + "<document>\r\n");
					out.write(TAB + TAB + TAB + TAB + "<url>"
							+ url.replace("\n", "").replace("\r", "")
							+ "</url>\r\n");
					out.write(TAB + TAB + TAB + "</document>\r\n");
					out.write(TAB + TAB + TAB + "<annotation>\r\n");

					int tabs = 4;

					/***********************************************************************************************************************************
					 * 
					 * write extraction annotations
					 */

					/* load extraction annotation */
					ExtractionAnnotation exAnn = (ExtractionAnnotation) ModelUtils2
							.findNamedAnnotation(benchDoc,
									AnnotationType.EXTRACTION);

					/* write extractions */
					if (exAnn != null) {
						writeExtractionAnnotation(out, tabs, exAnn);
					}

					/***********************************************************************************************************************************
					 * 
					 * write all label annotations
					 */

					/* load all label annotations */
					for (AnnotationLabel label : AnnotationLabel.values()) {
						LabelAnnotation labelAnn = (LabelAnnotation) ModelUtils2
								.getLabelAnnotation(benchDoc, label);

						/* write label annotation */
						if (labelAnn != null) {
							writeLabelAnnotation(out, tabs, labelAnn);
						}
					}

					// /***********************************************************************************************************************************
					// *
					// * write segment annotations
					// */
					//
					// /* load segment annotation */
					// FragmentAnnotation annotation =
					// (FragmentAnnotation) ModelUtils.findNamedAnnotation(
					// benchDoc,
					// AnnotationType.FRAGMENT);
					//
					// if (annotation!=null)
					// {
					// writeFragmentAnnotation(out, tabs, annotation);
					// }

					/***********************************************************************************************************************************
					 * 
					 * write selection annotations
					 */

					/* load selection annotation */
					ExampleAnnotation sAnn = (ExampleAnnotation) ModelUtils2
							.findNamedAnnotation(benchDoc,
									AnnotationType.EXAMPLE);

					if (sAnn != null) {
						writeSelectionAnnotation(out, tabs, sAnn);
					}

					// done
					out.write(TAB + TAB + TAB + "</annotation>\r\n");
					out.write(TAB + TAB + "</example>\r\n");

				}
				out.write(TAB + "</examples>\r\n");
				out.write("</benchmark>\n");

				out.flush(); // Don't forget to flush!
				out.close();
			} catch (UnsupportedEncodingException e) {
				System.out
						.println("This VM does not support the Latin-1 character set.");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	static String schemaSourceFileName;

	/**
	 * 
	 * @param out
	 * @param fileName
	 * @param benchName
	 * @throws IOException
	 */
	private static void writeXMLHead(OutputStreamWriter out, String fileName,
			String benchName) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); // header
																		// first
		out.write(getHeader(fileName)); // comment
		out.write("<benchmark>\n");
		out.write(TAB + "<name>" + benchName + "</name>\n");
		out.write(TAB + "<examples \n"
				+ "	xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
				+ "	xsi:noNamespaceSchemaLocation='benchmark.xsd' " + "	>\r\n");
	}

	// /**
	// *
	// * @param out
	// * @param tabs
	// * @param annotation
	// * @throws IOException
	// */
	// @SuppressWarnings("unchecked")
	// private static void writeFragmentAnnotation (OutputStreamWriter out, int
	// tabs, FragmentAnnotation annotation)
	// throws IOException
	// {
	// List<FragmentExample> fragments = annotation.getExamples();
	// out.write(getTabString(tabs) + "<segments>\r\n");
	// for (FragmentExample segmentExample : fragments)
	// {
	// NodeSelection selection = (NodeSelection)
	// segmentExample.getSelections().get(0);
	// String label = segmentExample.getFragmentType().getName();
	// out.write(getTabString(tabs+1) + "<segment class='"+label+"'");
	// // if (fragmentExample.getStart()!=-1 && fragmentExample.getEnd()!=-1) {
	// //
	// out.write(" start='"+fragmentExample.getStart()+"' end='"+fragmentExample.getEnd()+"'");
	// // }
	// out.write(">"+selection.getTargetXPath().replace("\n","").replace("\r","")+"</segment>\r\n");
	// }
	// out.write(getTabString(tabs) + "</segments>\r\n");
	// }

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param annotation
	 * @throws IOException
	 */
	private static void writeSelectionAnnotation(OutputStreamWriter out,
			int tabs, ExampleAnnotation annotation) throws IOException {
		// List<ExampleSelection> selections = annotation.getExampleItems();
		out.write(getTabString(tabs) + "<selection>\r\n");
		// for (ExampleSelection selection : selections)
		// {
		// if (selection instanceof SelectionExample)
		// {
		// SelectionExample ssel = (SelectionExample) selection;
		// NodeSelection nsel = (NodeSelection) ssel.getSelections().get(0);
		// String label =
		// (ssel.getClassification().equals(BinaryClass.POSITIVE)) ? "1" : "-1";
		// out.write(getTabString(tabs+1) +
		// "<xptr class='"+label+"'>"+nsel.getTargetXPath().replace("\n","").replace("\r","")+"</xptr>\r\n");
		// }
		// }
		out.write(getTabString(tabs) + "</selection>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param annotation
	 * @throws IOException
	 */
	private static void writeExtractionAnnotation(OutputStreamWriter out,
			int tabs, ExtractionAnnotation annotation) throws IOException {
		List<ExtractionResult> results = annotation.getExtractionItems();
		out.write(getTabString(tabs) + "<extractions>\r\n");
		for (ExtractionResult extraction : results) {
			// out.write(getTabString(tabs+1) +
			// "<page num='"+annotationPage.getPageNum()+"'>\r\n");
			// for (ISelection result : annotationPage.getItems())
			// {
			writeExtractionResult(out, 7, extraction);
			// }
			// out.write(getTabString(tabs+1) + "</page>\r\n");
		}
		out.write(getTabString(tabs) + "</extractions>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param result
	 * @throws IOException
	 */
	private static void writeExtractionResult(OutputStreamWriter out, int tabs,
			ExtractionResult result) throws IOException {
		out.write(getTabString(tabs) + "<extractionResult>\n");
		List<AnnotationPage> pages = result.getPages();
		if (pages != null && pages.size() > 0) {
			for (AnnotationPage page : pages) {
				for (AbstractSelection item : page.getItems()) {
					if (item instanceof NodeSelection) {
						writeNodeSelection(out, tabs + 1, (NodeSelection) item);
					} else if (item instanceof ExtractionItem) {
						writeExtractionItem(out, tabs + 1,
								(ExtractionItem) item);
					}
				}
			}
		}
		out.write(getTabString(tabs) + "</extractionResult>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param item
	 * @throws IOException
	 */
	private static void writeExtractionItem(OutputStreamWriter out, int tabs,
			ExtractionItem item) throws IOException {
		if (item.getKey() != null) {
			out.write(getTabString(tabs) + "<extraction " + "x='"
					+ (int) item.getBounds().getX() + "' y='"
					+ (int) item.getBounds().getY() + "' w='"
					+ (int) item.getBounds().getWidth() + "' h='"
					+ (int) item.getBounds().getHeight() + "'>\r\n");
		} else {
			out.write(getTabString(tabs) + "<extraction>" + item.getValue()
					+ "</extraction>\r\n");
		}
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param annotation
	 * @throws IOException
	 */
	private static void writeLabelAnnotation(OutputStreamWriter out, int tabs,
			LabelAnnotation annotation) throws IOException {
		List<LabelSelection> labelAnnotations = annotation.getLabelItems();
		out.write(getTabString(tabs) + "<labelAnnotation label='"
				+ annotation.getLabel() + "'>\r\n");
		for (LabelSelection label : labelAnnotations) {
			writeLabelSelection(out, 6, label);
		}
		out.write(getTabString(tabs) + "</labelAnnotation>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param sel
	 * @throws IOException
	 */
	private static void writeLabelSelection(OutputStreamWriter out, int tabs,
			LabelSelection sel) throws IOException {
		out.write(getTabString(tabs) + "<labelSelection label='"
				+ sel.getLabel() + "' sublabel='" + sel.getSubLabel()
				+ "'>\r\n");

		writeBounds(out, tabs + 1, sel.getBounds());

		if (sel.getSelection() instanceof NodeSelection) {
			writeNodeSelection(out, tabs + 1,
					(NodeSelection) sel.getSelection());
		} else if (sel.getSelection() instanceof PdfSelection) {
			writePdfSelection(out, tabs + 1, (PdfSelection) sel.getSelection());
		} else {
			ErrorDump.debug(BenchmarkEngine.class,
					"Unsupported selection type: " + sel.getClass());

		}
		out.write(getTabString(tabs) + "</labelSelection>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param sel
	 * @throws IOException
	 */
	private static void writeNodeSelection(OutputStreamWriter out, int tabs,
			NodeSelection sel) throws IOException {
		out.write(getTabString(tabs) + "<nodeSelection document='"
				+ sel.getDocumentUri() + "' target='" + sel.getTargetXPath()
				+ "' root='" + sel.getRootXPath() + "'/>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param sel
	 * @throws IOException
	 */
	private static void writePdfSelection(OutputStreamWriter out, int tabs,
			PdfSelection sel) throws IOException {
		out.write(getTabString(tabs) + "<pdfSelection>\r\n");
		List<PDFInstruction> pdfOperators = sel.getInstructions();
		for (PDFInstruction ox : pdfOperators) {
			writePDFInstructionIndex(out, tabs + 1, ox);
		}
		out.write(getTabString(tabs) + "</pdfSelection>\r\n");
	}

	/**
	 * 
	 * @param out
	 * @param tabs
	 * @param ox
	 * @throws IOException
	 */
	private static void writePDFInstructionIndex(OutputStreamWriter out,
			int tabs, PDFInstruction ox) throws IOException {
		out.write(getTabString(tabs) + "<pdfOperator index='" + ox.getIndex()
				+ "' subindex='" + ox.getSubIndex() + "'/>\r\n");
	}

	private static void writeBounds(OutputStreamWriter out, int tabs,
			Rectangle bounds) throws IOException {
		out.write(getTabString(tabs) + "<bounds x='" + (int) bounds.getX()
				+ "' y='" + (int) bounds.getY() + "' width='"
				+ (int) bounds.getWidth() + "' height='"
				+ (int) bounds.getHeight() + "'/>\r\n");
	}

	/**
	 * 
	 * @param tabs
	 * @return
	 */
	private static String getTabString(int tabs) {
		StringBuffer tabBuffer = new StringBuffer();
		for (int i = 0; i < tabs; i++) {
			tabBuffer.append(TAB);
		}
		return tabBuffer.toString();
	}

	/**
	 * 
	 * Load a datafile. Validates against the schema file specified herein if
	 * validating is set.
	 * 
	 * @param filename
	 * @param validating
	 * @return
	 */
	protected static Document loadDataFile(String filename,
			final boolean validating) {

		try {

			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(validating);
			domFactory.setValidating(validating);
			// domFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			// domFactory.setAttribute(JAXP_SCHEMA_SOURCE, new
			// File(schemaSourceFileName));

			// Set up an XML Schema validator, using the supplied schema
			// Source schemaSource = new StreamSource(new
			// File(schemaSourceFileName));
			// SchemaFactory schemaFactory = SchemaFactory.newInstance(
			// XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// Schema schema = schemaFactory.newSchema(schemaSource);

			// Instead of explicitly validating, assign the Schema to the
			// factory
			// domFactory.setSchema(schema);

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

			return domBuilder.parse(filename);
		} catch (Exception e) {
			e.printStackTrace();
			if (validating)
				return null;
		}
		return null;
	}

	/**
	 * Create a new benchmark set with a given name
	 * 
	 * @param categoryName
	 */
	public static void createNewBenchmarkCategory(String categoryName) {
		//
		// File root = new File(BENCHMARK_ROOT);
		// if (!root.exists()||!root.isDirectory()) {
		// root.mkdir();
		// }
		//
		File benchmark = new File(BENCHMARK_ROOT + File.separator
				+ categoryName);
		if (!benchmark.exists() || !benchmark.isDirectory()) {
			benchmark.mkdirs();
		}

	}

	// /**
	// * Generate a ground truth annotated document from a list of selections.
	// *
	// * @param selection
	// * @param uri
	// * @param page
	// * @return
	// */
	// private static BenchmarkDocument buildTrainingGroundTruthFromXYSelection
	// (
	// Map<Integer,List<SelectionExample>> pageMap,
	// URI uri,
	// MetaInfo info) {
	//
	// BenchmarkDocument result = new BenchmarkDocument();
	// result.setUri(uri.toString());
	//
	// for (int pg: pageMap.keySet()) {
	//
	// List<SelectionExample> selections = pageMap.get(pg);
	//
	// List<GenericSegment> gsList = new ArrayList<GenericSegment>();
	// for (SelectionExample selEx : selections) {
	// for (Selection sel : selEx.getSelections()) {
	// int x = sel.getX();
	// int y = sel.getY();
	// int h = sel.getHeight();
	// int w = sel.getHeight();
	//
	// GenericSegment gs = new GenericSegment();
	// gs.setX1(x);
	// gs.setX2(x+w);
	// gs.setY1(y);
	// gs.setY2(y+h);
	// gsList.add(gs);
	// }
	// }
	//
	// // depending on the ground truth type, we perform the annotation
	// switch (info) {
	// case EXTRACTION:
	//
	// ExtractionAnnotation exAnn = new ExtractionAnnotation(result);
	//
	// try {
	// //process the PDF
	// File inputDocFile = new File(uri.toString());
	// byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);
	//
	// List<AdjacencyGraph<GenericSegment>> adjGraphList =
	// new ArrayList<AdjacencyGraph<GenericSegment>>();
	// List<Page> pages =
	// ProcessFile.processPDF(
	// inputDoc,
	// PageProcessor.PP_BLOCK, //block contains all
	// false, false,
	// pg, pg,
	// "", "", 0, adjGraphList, false);
	//
	// DocumentGraph dg = new DocumentGraph(adjGraphList.get(0));
	//
	// List<ExtractionItem> exItems = new ArrayList<ExtractionItem>();
	// for (GenericSegment bBox : gsList) {
	// List<DocNode> nodes = getNodesWithIntersectingCentres(dg, bBox);
	// for (DocNode node : nodes) {
	// ExtractionItem exItem = new ExtractionItem();
	// exItem.setValue(node.getNodeText());
	// exItems.add(exItem);
	// }
	// }
	// result.getAnnotations().add(exAnn);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// break;
	//
	// case SELECTION:
	//
	// break;
	// default:
	// break;
	// }
	//
	//
	// }
	//
	// return result;
	// }

	public static List<DocNode> getNodesWithIntersectingCentres(
			DocumentGraph dg, GenericSegment bBox) {
		ArrayList<DocNode> retVal = new ArrayList<DocNode>();
		for (Object o : dg.getNodes()) {
			DocNode n = (DocNode) o;
			if (SegmentUtils.horizIntersect(bBox, n.toGenericSegment()
					.getXmid())
					&& SegmentUtils.vertIntersect(bBox, n.toGenericSegment()
							.getYmid()))
				retVal.add(n);
		}
		return retVal;
	}

	/**
	 * 
	 * Get all valid benchmark root diretories under the root file.
	 * 
	 * @param root
	 * @return
	 */
	protected static List<File> findAllBenchmarkFilesUnder(String root) {
		ErrorDump.info(BenchmarkEngine.class,
				"Searching for available benchmarks under " + root);
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(1);
		List<File> result = new LinkedList<File>();
		Stack<File> stack = new Stack<File>();
		stack.push(new File(root));

		while (!stack.isEmpty()) {
			File file = stack.pop();
			if (!file.isDirectory())
				continue;
			if (checkContainsCorpus(file))
				result.add(file);
			else
				stack.addAll(ListUtils.toList(file.listFiles()));
		}
		timer.stopTask(1);
		ErrorDump.info(BenchmarkEngine.class, "Result: " + result.size()
				+ " benchmarks found in " + timer.getTimeMillis(1) + "ms");
		return result;
	}

	/**
	 * 
	 * Returns true IFF<br>
	 * - current file contains a datafiles and a src directory<br>
	 * - 1+ .htm/.html file exists in src dir, as well <br>
	 * - 1+ .xml file exists in datafiles directory.
	 * 
	 * @param file
	 * @return
	 */
	protected static boolean checkContainsCorpus(File file) {
		if (!file.isDirectory())
			return false;
		List<String> subDirs = ListUtils.toList(file.list());

		if (!subDirs.contains("src") || !subDirs.contains("datafiles"))
			return false;

		if (new File(file.getPath() + File.separator + "datafiles")
				.list(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						if (name.endsWith(".xml"))
							return true;
						return false;
					}

				}).length < 1)
			return false;

		if (new File(file.getPath() + File.separator + "src")
				.list(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						if (name.endsWith(".html") || name.endsWith(".htm"))
							return true;
						return false;
					}

				}).length < 1) {
			File[] files = new File(file.getPath() + File.separator + "src")
					.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].list(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						if (name.endsWith(".html") || name.endsWith(".htm"))
							return true;
						return false;
					}

				}).length > 1)
					return true;
			}
		}
		return true;
	}

	/**
	 * 
	 * Get a named benchmark located under the root file. Only files ending in
	 * 'name' are considered.
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	protected static List<File> findNamedBenchmarkFilesUnder(String root,
			String name) {

		List<File> allBenchmarks = findAllBenchmarkFilesUnder(root);
		for (File file : allBenchmarks) {
			if (file.getName().toUpperCase().endsWith(name)) {
				List<File> result = new LinkedList<File>();
				result.add(file);
				return result;
			}
		}
		return null;
	}

	/**
	 * 
	 * Load a benchmark by its name and category. Accepts wildcard '*' as either
	 * argument.
	 * 
	 * @param name
	 * @param category
	 * 
	 */
	public static Benchmark processBenchmark(String name, String category) {
		Benchmark benchmark = null;
		String benchDir = getBenchmarkRootDirectory();

		File benchSrc = new File(benchDir);
		if (benchSrc.exists()) {
			List<File> benchRoots = new LinkedList<File>();
			if (name == null || name == "" || name == "*") {
				benchRoots = findAllBenchmarkFilesUnder(benchDir);
			} else {
				benchDir = benchDir + File.separator + name;
				if (category == null || category == "" || category == "*") {
					benchRoots = findAllBenchmarkFilesUnder(benchDir);
				} else {
					benchRoots = findNamedBenchmarkFilesUnder(benchDir,
							category);
				}
			}

			for (File rootDir : benchRoots) {
				List<Benchmark> benches = BenchmarkLoader
						.loadBenchmarksUnderRoot(rootDir.getAbsolutePath());
				if (benches.size() > 1) {
					ErrorDump.error(BenchmarkEngine.class,
							"multiple benchmarks returend");
				} else if (benches.size() == 1) {
					benchmark = benches.get(0);
				}
			}
		}
		return benchmark;
	}

}// BenchmarkEngine
