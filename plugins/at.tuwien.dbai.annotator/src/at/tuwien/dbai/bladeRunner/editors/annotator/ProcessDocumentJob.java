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
package at.tuwien.dbai.bladeRunner.editors.annotator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.ImageData;

import at.tuwien.prip.model.graph.DocumentGraph;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * ProcessDocumentJob.java
 * 
 * 
 * 
 * @author mcg <mcgoebel@gmail.com> Jan 18, 2012
 */
public class ProcessDocumentJob extends Job {
	private final String inFile;

	private final int pageNum;

	private int numPages;

	private PDFFile pdfFile;

	private PDFPage pdfPage;

	private ImageData data;

	private PDDocument pdDocument = null;
	private DocumentGraph document = null;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param inFile
	 * @param pageNum
	 */
	public ProcessDocumentJob(String name, String inFile, int pageNum) {
		super(name);
		this.inFile = inFile;
		this.pageNum = pageNum;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// document = new DocHierGraph();
		//
		// try
		// {
		// File inputDocFile = new File(inFile);
		// if (!inputDocFile.exists()) {
		// ErrorDump.error(DocumentGraphFactory.class,
		// "File does not exist: "+inFile.toString());
		// return Status.OK_STATUS;
		// }
		//
		// byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);
		// RandomAccessFile raf = new RandomAccessFile (inputDocFile, "r");//new
		// File (fileName.substring(5)), "r");
		//
		// //load PDF data, image
		// FileChannel fc = raf.getChannel ();
		// ByteBuffer buf = fc.map (FileChannel.MapMode.READ_ONLY, 0, fc.size
		// ());
		// this.pdfFile = new PDFFile (buf);
		// this.pdfPage = pdfFile.getPage(pageNum);
		// this.data = PDFUtils.getImageFromPDFPage(
		// pdfFile.getPage(pageNum),
		// DocumentController.docModel.getScale());
		//
		// List<AdjacencyGraph<GenericSegment>> adjGraphList =
		// new ArrayList<AdjacencyGraph<GenericSegment>>();
		//
		// //process the PDF
		// List<Page> pages =
		// ProcessFile.processPDF(
		// inputDoc,
		// PageProcessor.PP_CHAR, //block contains all
		// // pdDocument,
		// null,
		// false, false,
		// 1, 1,
		// "", "", 0, adjGraphList, false);
		//
		// pdDocument = ProcessFile.pdDoc;
		//
		// // document.setDocumentURI(inFile.toString());
		// document.setNumPages(pages.size()); //remember page count
		// this.numPages = pages.size();
		//
		// AdjacencyGraph<GenericSegment> g = adjGraphList.get(0);
		//
		// /***********************************************************************
		// * split all segments according to their segmentation level...
		// */
		// List<GenericSegment> blocks = new ArrayList<GenericSegment>();
		// List<GenericSegment> lines = new ArrayList<GenericSegment>();
		// // List<GenericSegment> words = new ArrayList<GenericSegment>();
		// List<GenericSegment> text = new ArrayList<GenericSegment>();
		// List<GenericSegment> chars = new ArrayList<GenericSegment>();
		// List<GenericSegment> imgs = new ArrayList<GenericSegment>();
		//
		// // TextBlock -- output of segmentation
		// // TB contains TextLine, contains LineFragment
		// // contains TextFragment, contains CharSegment
		//
		// List<GenericSegment> segments = g.getVertList();
		// List<OpTuple> pdfOps4 = new ArrayList<OpTuple>();
		// for (GenericSegment segment : segments)
		// {
		// if (segment instanceof TextBlock)
		// {
		// List<OpTuple> pdfOps3 = new ArrayList<OpTuple>();
		// TextBlock block = (TextBlock) segment;
		// for (TextLine line : block.getItems())
		// {
		// List<OpTuple> pdfOps2 = new ArrayList<OpTuple>();
		// // words.addAll(convertTextLineToWordFragments(line));
		//
		// for (LineFragment lineFrag : line.getItems())
		// {
		// List<OpTuple> pdfOps1 = new ArrayList<OpTuple>();
		// List<TextFragment> textFrags = lineFrag.getItems();
		// for (TextFragment tFrag : textFrags)
		// {
		// // System.out.println(tFrag.toExtendedString());
		// chars.addAll(tFrag.getItems());
		//
		// /* remember PDF operators */
		// List<OpTuple> pdfOps = new ArrayList<OpTuple>();
		// for (CharSegment cs : tFrag.getItems())
		// {
		// pdfOps.add(cs.getSourceOp());
		// }
		// tFrag.setOperators(pdfOps);
		// text.add(tFrag);
		// pdfOps1.addAll(pdfOps);
		// }
		// lines.add(lineFrag);
		// lineFrag.setOperators(pdfOps1);
		// pdfOps2.addAll(pdfOps1);
		// }
		// line.setOperators(pdfOps2);
		// pdfOps3.addAll(pdfOps2);
		// }
		// pdfOps4.addAll(pdfOps3);
		// block.setOperators(pdfOps3);
		// blocks.add(block);
		// }
		// else if (segment instanceof ImageSegment) {
		// ImageSegment img = (ImageSegment) segment;
		// imgs.add(img);
		// }
		// else if (segment instanceof TextFragment)
		// {
		// TextFragment tFrag = (TextFragment) segment;
		// chars.addAll(tFrag.getItems());
		//
		// /* remember PDF operators */
		// List<OpTuple> pdfOps = new ArrayList<OpTuple>();
		// for (CharSegment cs : tFrag.getItems())
		// {
		// pdfOps.add(cs.getSourceOp());
		// }
		// tFrag.setOperators(pdfOps);
		// }
		// else
		// {
		// ErrorDump.debug(DocumentGraphFactory.class, "unknown segment type: "
		// + segment.getClass());
		// }
		// segment.setOperators(pdfOps4);
		// }
		//
		// //generate the TEXTBLOCK level graph
		// AdjacencyGraph<GenericSegment> g1 = new
		// AdjacencyGraph<GenericSegment>();
		// // g1.addList(words);
		// g1.addList(chars);
		// g1.addList(imgs);
		// g1.generateEdgesSingle();
		//
		// document = new DocumentGraph(g1);
		// DocGraphUtils.flipReverseDocGraph(document);
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (DocumentProcessingException e) {
		// e.printStackTrace();
		// }
		//
		return Status.OK_STATUS;
	}

	public DocumentGraph getDocument() {
		return document;
	}

	public String getInFile() {
		return inFile;
	}

	public int getNumPages() {
		return numPages;
	}

	public int getPageNum() {
		return pageNum;
	}

	public PDFFile getPdfFile() {
		return pdfFile;
	}

	public PDFPage getPdfPage() {
		return pdfPage;
	}

	public ImageData getData() {
		return data;
	}

	public PDDocument getPdDocument() {
		return pdDocument;
	}

}// ProcessDocumentJob
