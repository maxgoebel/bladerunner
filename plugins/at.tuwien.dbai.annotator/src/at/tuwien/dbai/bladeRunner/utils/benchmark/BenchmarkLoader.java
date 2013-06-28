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

import static at.tuwien.prip.model.utils.DOMHelper.Tree.Children.getChildElements;
import static at.tuwien.prip.model.utils.DOMHelper.Tree.Children.getNamedChildElements;
import static at.tuwien.prip.model.utils.DOMHelper.Tree.Descendant.getNamedDescendantsAndSelfElements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory;
import at.tuwien.dbai.bladeRunner.utils.PdfDocumentProcessingException;
import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory.PDF;
import at.tuwien.prip.common.exceptions.LoadFailureException;
import at.tuwien.prip.common.exceptions.XPathSyntaxException;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.BinaryClass;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.learning.example.SelectionExample;
import at.tuwien.prip.model.project.annotation.AnnotationLabel;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentType;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.selection.NodeSelection;
import at.tuwien.prip.model.utils.DOMHelper;

/**
 * BenchmarkLoader.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 14, 2011
 */
public class BenchmarkLoader {

	/**
	 * Generate a single training document from the example element.
	 * 
	 * @param root
	 * @return
	 * @throws LoadFailureException
	 * @throws FileNotFoundException
	 * @throws XPathSyntaxException
	 */
	private static BenchmarkDocument buildTrainingDocumentFromExampleElement(
			Element root) throws LoadFailureException, FileNotFoundException {

		/* set uri */
		Element documentRoot = getNamedChildElements(root, "document").get(0); // exactly
																				// one
		String uriString = getNamedChildElements(documentRoot, "url").get(0)
				.getFirstChild().getNodeValue().replace("\n", "")
				.replace("\t", "");

		String absoluteURI;
		URI uri = null;
		try {
			uri = new URI(uriString);
			uriString = uri.toASCIIString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File fileURI = null;
		if (new File(uriString).exists()) {
			absoluteURI = uriString;
		} else if (uri != null) {
			absoluteURI = uri.toASCIIString();
		} else {
			fileURI = new File(root.getOwnerDocument().getDocumentURI()
					.substring(7));
			while (!fileURI.getName().endsWith("datafiles")) {
				fileURI = fileURI.getParentFile();
			}
			absoluteURI = fileURI.getParent() + "/src/" + uriString;
		}
		if (absoluteURI == null) {
			throw new FileNotFoundException();
		}

		BenchmarkDocument result = null;

		// determine file type
		DocumentType dt = DocumentType.PDF;
		int dotIndex = uriString.lastIndexOf(".");
		if (dotIndex > 0) {
			String extension = uriString.substring(dotIndex + 1);
			if ("html".equalsIgnoreCase(extension)) {
				dt = DocumentType.HTML;
			} else if (uriString.startsWith("http://")) {
				dt = DocumentType.HTML;
			} else if ("pdf".equalsIgnoreCase(extension)) {
				dt = DocumentType.PDF;
			}
		}

		/* set document type */
		Document doc = null;
		// if (dt==DocumentType.HTML)
		// {
		// result = new HTMLBenchmarkDocument();
		// doc = MozSWTKit.loadDOMFromMozilla(absoluteURI);
		// ((HTMLBenchmarkDocument)result).setCachedJavaDOM(doc);
		// result.setFormat(DocumentFormat.HTML);
		// }
		if (dt == DocumentType.PDF) {
			// make sure file exists
			if (!new File(absoluteURI).exists()) {
				// benchmark item does not exist...
				throw new FileNotFoundException(
						"Could not find specified file:\n" + absoluteURI);
			}

			result = new PdfBenchmarkDocument();

			DocumentGraph dg = null;
			try {
				dg = DocumentGraphFactory.PDF.generateDocumentGraphNew(
						absoluteURI, 1);
			} catch (PdfDocumentProcessingException e) {
				e.printStackTrace();
			}
			if (dg != null) {
				// ((PdfBenchmarkDocument) result).setDocumentGraph(1, dg);
			}
			result.setFormat(DocumentFormat.PDF);
		}

		result.setUri(absoluteURI);

		/* create all annotations and examples */
		Element annotationRoot = getNamedChildElements(root, "annotation").get(
				0); // exactly one
		for (Element annotation : getChildElements(annotationRoot)) {

			// table annotations
			if ("tables".equalsIgnoreCase(annotation.getNodeName())) {
				// TableAnnotation ta = (TableAnnotation)
				// ModelUtils.findNamedAnnotation(result, MetaInfo.TABLE);
				//
				// List<Element> tables =
				// getNamedDescendantsAndSelfElements(annotation, "table");
				// for (Element table : tables) {
				// TableExample exTable = ef.createTableExample();
				// SimpleSelection selection = new SimpleSelection();
				// selection.setDocument(result.getCachedJavaDOM());
				//
				// /** set root element with xpath */
				// selection.setRoot(result.getCachedJavaDOM().getDocumentElement());
				// selection.setRootXPath(DOMHelper.XPath.getExactXPath(selection.getRoot()));
				//
				// /** set element target with xpath */
				// String path =
				// table.getFirstChild().getNodeValue().replace("\n","").replace("\t","");
				// selection.setTargetXPath(path);
				// List<Node> elems;
				// try {
				//
				// elems =
				// DOMHelper.XPath.evaluateXPath(doc.getDocumentElement(),path);
				// if (elems.size()>0) {
				// root = (Element) elems.get(0);
				// selection.setTarget(root);
				// }
				//
				// } catch (XPathSyntaxException e) {
				// e.printStackTrace();
				// }
				//
				// /** set classification, document, and selection */
				// String binaryClass = table.getAttribute("class");
				// exTable.setClassification(
				// (binaryClass.equals("1")?
				// BinaryClass.POSITIVE:
				// BinaryClass.NEGATIVE));
				// exTable.setDocument(result);
				// exTable.getSelections().add(selection);
				// ta.getTables().add(exTable);
				// }
				//
				// result.getAnnotations().add(ta);

			}

			// segment annotations
			else if ("segments".equals(annotation.getNodeName())) {
				// SegmentAnnotation fa = (SegmentAnnotation)
				// ModelUtils.findNamedAnnotation(result, MetaInfo.SEGMENT);
				//
				// List<Element> fragments =
				// getNamedDescendantsAndSelfElements(annotation, "segment");
				// for (Element fragment : fragments) {
				// SegmentExample exFragment = ef.createSegmentExample();
				// SimpleSelection selection = new SimpleSelection();
				// selection.setDocument(doc);
				//
				// /** set xpath and element root */
				// String path =
				// fragment.getFirstChild().getNodeValue().replace("[\n|\t]",
				// "");
				// selection.setTargetXPath(path);
				// List<Node> elems;
				// try {
				//
				// elems =
				// DOMHelper.XPath.evaluateXPath(doc.getDocumentElement(),path);
				// if (elems.size()>0) {
				// root = (Element) elems.get(0);
				// selection.setTarget(root);
				// }
				//
				// } catch (XPathSyntaxException e) {
				// e.printStackTrace();
				// }
				//
				// /** set classification, document, and selection */
				// String binaryClass = fragment.getAttribute("class");
				// exFragment.setClassification(
				// (binaryClass.equals("1")?
				// BinaryClass.POSITIVE:
				// BinaryClass.NEGATIVE));
				// exFragment.setDocument(result);
				// exFragment.getSelections().add(selection);
				// fa.getSegments().add(exFragment);
				// }
				//
				// result.getAnnotations().add(fa);

			}

			/* selection annotations */
			else if ("selection".equals(annotation.getNodeName())) {
				// /** create user selection */
				// SelectionAnnotation sAnn =
				// (SelectionAnnotation)
				// ModelUtils2.findNamedAnnotation(result,
				// AnnotationType.SELECTION);

				List<Element> selections = getNamedDescendantsAndSelfElements(
						annotation, "xptr");
				for (Element userSelections : selections) {
					SelectionExample exSelection = new SelectionExample();
					NodeSelection selection = new NodeSelection();
					selection.setDocument(doc);

					/** set XPATH and element root */
					String path = userSelections.getFirstChild().getNodeValue()
							.replace("[\n|\t]", "");
					selection.setTargetXPath(path);
					List<Node> elems;
					try {

						elems = DOMHelper.XPath.evaluateXPath(
								doc.getDocumentElement(), path);
						if (elems.size() > 0) {
							root = (Element) elems.get(0);
							selection.setRoot(root);
						}

					} catch (XPathSyntaxException e) {
						e.printStackTrace();
					}

					/** set classification, document, and selection */
					String binaryClass = userSelections.getAttribute("class");
					exSelection
							.setClassification((binaryClass.equals("1") ? BinaryClass.POSITIVE
									: BinaryClass.NEGATIVE));
					// exSelection.setDocument(result);
					exSelection.getSelections().add(selection);
					// sAnn.getPages().add(exSelection);
				}
			}

			// /* extraction annotations */
			// else if ("extractions".equals(annotation.getNodeName()))
			// {
			// ExtractionAnnotation exAnn = new
			// ExtractionAnnotation(fileURI.getAbsolutePath());
			// List<AnnotationPage> extractionPages = new
			// ArrayList<AnnotationPage>();
			//
			// List<Element> pages =
			// getNamedDescendantsAndSelfElements(annotation, "page");
			// for (Element pageRoot : pages)
			// {
			// int pageNum = Integer.parseInt(pageRoot.getAttribute("num"));
			// AnnotationPage exPage = new AnnotationPage(pageNum);
			//
			// List<ExtractionResult> exResults = new
			// ArrayList<ExtractionResult>();
			// List<Element> results =
			// getNamedDescendantsAndSelfElements(annotation,
			// "extractionResult");
			// for (Element resultRoot : results)
			// {
			// ExtractionResult exResult = new ExtractionResult();
			// int x = Integer.parseInt(resultRoot.getAttribute("x"));
			// int y = Integer.parseInt(resultRoot.getAttribute("y"));
			// int w = Integer.parseInt(resultRoot.getAttribute("w"));
			// int h = Integer.parseInt(resultRoot.getAttribute("h"));
			// Rectangle bounds = new Rectangle(x,y,w,h);
			// exResult.setBounds(bounds);
			//
			// List<AbstractSelection> exItems = new
			// ArrayList<AbstractSelection>();
			// List<Element> items =
			// getNamedDescendantsAndSelfElements(resultRoot, "extraction");
			//
			// if (items.size()==0)
			// {
			// /* look for node selections */
			// items = getNamedDescendantsAndSelfElements(resultRoot,
			// "nodeSelection");
			// for (Element itemRoot : items)
			// {
			// NodeSelection nsItem = new NodeSelection();
			// nsItem.setRootXPath(itemRoot.getAttributes().getNamedItem("root").getNodeValue());
			// nsItem.setTargetXPath(itemRoot.getAttributes().getNamedItem("target").getNodeValue());
			// exItems.add(nsItem);
			// }
			// }
			// else
			// {
			// for (Element itemRoot : items)
			// {
			// NodeList children = itemRoot.getChildNodes();
			// for (int i=0; i<children.getLength(); i++)
			// {
			// Node item = children.item(i);
			// ExtractionItem exItem = new ExtractionItem();
			// exItem.setValue(item.getNodeValue());
			// exItems.add(exItem);
			// }
			// }
			// }
			// for (Element itemRoot : items) {
			// NodeList children = itemRoot.getChildNodes();
			// for (int i=0; i<children.getLength(); i++) {
			// Node item = children.item(i);
			// ExtractionItem exItem = new ExtractionItem();
			// exItem.setValue(item.getNodeValue());
			// exItems.add(exItem);
			// }
			// }
			// exResult.getItems().addAll(exItems);
			// exResults.add(exResult);
			// }
			// exPage.getItems().addAll(exResults);
			// extractionPages.add(exPage);
			// }
			// exAnn.getPages().addAll(extractionPages);
			// result.getAnnotations().add(exAnn);
			// }

			// /* extraction annotations */
			// else if ("labelAnnotation".equals(annotation.getNodeName()))
			// {
			// LabelAnnotation labAnn = new
			// LabelAnnotation(fileURI.getAbsolutePath());
			//
			//
			// List<AnnotationPage> labelPages = new
			// ArrayList<AnnotationPage>();
			//
			// List<Element> pages =
			// getNamedDescendantsAndSelfElements(annotation, "page");
			// for (Element pageRoot : pages)
			// {
			// int pageNum = Integer.parseInt(pageRoot.getAttribute("num"));
			// AnnotationPage page = new AnnotationPage(pageNum);
			//
			// List<LabelSelection> selResults = new
			// ArrayList<LabelSelection>();
			// List<Element> results =
			// getNamedDescendantsAndSelfElements(annotation, "labelSelection");
			// for (Element labelSelection : results)
			// {
			// LabelSelection labelSel = new LabelSelection();
			// String label = labelSelection.getAttribute("label");
			// labelSel.setLabel(stringToAnnotationLabel(label));
			// labelSel.setSubLabel(labelSelection.getAttribute("sublabel"));
			//
			// //load bounds
			// Element bounds = getNamedChildElements(labelSelection,
			// "bounds").get(0);
			// int x = Integer.parseInt(bounds.getAttribute("x"));
			// int y = Integer.parseInt(bounds.getAttribute("y"));
			// int w = Integer.parseInt(bounds.getAttribute("width"));
			// int h = Integer.parseInt(bounds.getAttribute("height"));
			// Rectangle bbounds = new Rectangle(x,y,w,h);
			// labelSel.setBounds(bbounds);
			//
			// //load PDF selection
			// List<PdfSelection> pdfSelections = new ArrayList<PdfSelection>();
			// List<Element> pdfSelRoots =
			// getNamedDescendantsAndSelfElements(annotation, "pdfSelection");
			// for (Element pdfSelection : pdfSelRoots)
			// {
			// PdfSelection pdfSel = new PdfSelection();
			//
			// //load PDF operators
			// List<PDFInstruction> pdfOps = new ArrayList<PDFInstruction>();
			// List<Element> pdfOpsRoots =
			// getNamedDescendantsAndSelfElements(pdfSelection, "pdfOperator");
			// for (Element pdfOpEl : pdfOpsRoots)
			// {
			// int index = Integer.parseInt(pdfOpEl.getAttribute("index"));
			// int subIndex =
			// Integer.parseInt(pdfOpEl.getAttribute("subindex"));
			// pdfOps.add(new PDFInstruction(index, subIndex));
			// }
			// pdfSel.getInstructions().addAll(pdfOps);
			// pdfSelections.add(pdfSel);
			// }
			// //add to result list
			// selResults.add(labelSel);
			// }
			//
			// page.getItems().addAll(selResults);
			// labelPages.add(page);
			// }
			//
			// labAnn.getPages().addAll(labelPages);
			// result.getAnnotations().add(labAnn);
			// }

			else {
				throw new LoadFailureException(annotation.getNodeValue()
						+ " annotation not yet supported... ");
			}
		}

		return result;
	}

	/**
	 * 
	 * @param label
	 * @return
	 */
	private static AnnotationLabel stringToAnnotationLabel(String label) {
		if ("boilerplate".equalsIgnoreCase(label)) {
			return AnnotationLabel.BOILERPLATE;
		} else if ("heading".equalsIgnoreCase(label)) {
			return AnnotationLabel.HEADING;
		} else if ("image".equalsIgnoreCase(label)) {
			return AnnotationLabel.IMAGE;
		} else if ("key_value".equalsIgnoreCase(label)) {
			return AnnotationLabel.KEY_VALUE;
		} else if ("list".equalsIgnoreCase(label)) {
			return AnnotationLabel.LIST;
		} else if ("region".equalsIgnoreCase(label)) {
			return AnnotationLabel.REGION;
		} else if ("section".equalsIgnoreCase(label)) {
			return AnnotationLabel.SECTION;
		} else if ("semantic".equalsIgnoreCase(label)) {
			return AnnotationLabel.SEMANTIC;
		} else if ("table".equalsIgnoreCase(label)) {
			return AnnotationLabel.TABLE;
		}
		return null;
	}

	/**
	 * 
	 * Load all benchmark documents under a given root.
	 * 
	 * @param rootDir
	 * @return
	 * @throws LoadFailureException
	 */
	protected static List<Benchmark> loadBenchmarksUnderRoot(String rootDir) {
		List<Benchmark> result = new ArrayList<Benchmark>();

		List<File> dirs = BenchmarkEngine.findAllBenchmarkFilesUnder(rootDir);
		int i = 1;
		for (File dir : dirs) {
			ErrorDump.debug(BenchmarkEngine.class,
					String.format("Loading document %d/%d", i, dirs.size()));
			i++;
			File[] xmls = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.equals("datafiles"))
						return true;
					return false;
				}
			})[0].listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml"))
						return true;
					return false;
				}
			}); // all .xml files in datafiles

			for (File xml : xmls) {
				try {
					result.add(loadBenchmarkFromDataFile(xml.getAbsolutePath()));
				} catch (LoadFailureException e) {
					continue; // skip this one...
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param datafile
	 * @return
	 * @throws LoadFailureException
	 */
	public static Benchmark loadBenchmarkTableAnnotator(String datafile)
			throws LoadFailureException {
		Benchmark benchmark = new Benchmark(datafile);

		Document data = loadDataFile(datafile, false);

		/* extract name */
		String name = "unknown"; // default
		List<Element> nameList = getNamedDescendantsAndSelfElements(
				data.getDocumentElement(), "document");
		if (nameList != null && nameList.size() > 0) {
			name = nameList.get(0).getAttribute("filename");
		}
		benchmark.setName(name);

		/* extract tables */
		List<Element> tableList = getNamedDescendantsAndSelfElements(
				data.getDocumentElement(), "table");
		for (Element table : tableList) {

		}

		return benchmark;
	}

	/**
	 * 
	 * Load a benchmark document from a data file (XML)
	 * 
	 * @param file
	 * @return
	 * @throws LoadFailureException
	 */
	public static Benchmark loadBenchmarkFromDataFile(String datafile)
			throws LoadFailureException {
		Benchmark benchmark = new Benchmark(datafile);

		List<BenchmarkDocument> bDocs = new LinkedList<BenchmarkDocument>();

		Document data = null;

		try {
			if (datafile.endsWith(".zip") || datafile.endsWith(".ZIP")) {
				// benchmark = DiademBenchmarkLoader.loadBenchmark(datafile);
			} else {
				if (data == null)
					data = loadDataFile(datafile, false);

				if (data == null)
					return benchmark; // failsafe

				/* extract name */
				String name = "unknown"; // default
				List<Element> nameList = getNamedDescendantsAndSelfElements(
						data.getDocumentElement(), "name");
				if (nameList != null && nameList.size() > 0) {
					name = nameList.get(0).getTextContent();
				}
				benchmark.setName(name);

				/* extract examples */
				List<Element> exNodes = getNamedDescendantsAndSelfElements(
						data.getDocumentElement(), "example");
				for (Element exNode : exNodes) {
					BenchmarkDocument ed = buildTrainingDocumentFromExampleElement(exNode);
					bDocs.add(ed);
				}

				benchmark.getDocuments().addAll(bDocs);
			}

		} catch (IOException e) {
			throw new LoadFailureException(e.getMessage());
		}

		return benchmark;
	}

	/**
	 * 
	 * Load a datafile with SAX. Validates against the schema file specified
	 * herein if validating is set.
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

}
