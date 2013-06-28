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
package at.tuwien.dbai.bladeRunner.utils;

import static at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants.PREF_ANNO_DETAIL;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.ac.tuwien.dbai.pdfwrap.analysis.PageProcessor;
import at.ac.tuwien.dbai.pdfwrap.exceptions.DocumentProcessingException;
import at.ac.tuwien.dbai.pdfwrap.utils.ProcessFile;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.ImageSegment;
import at.tuwien.prip.model.document.segments.OpTuple;
import at.tuwien.prip.model.document.segments.Page;
import at.tuwien.prip.model.document.segments.TextBlock;
import at.tuwien.prip.model.document.segments.TextLine;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.segments.fragments.LineFragment;
import at.tuwien.prip.model.document.segments.fragments.TextFragment;
import at.tuwien.prip.model.document.segments.web.FormSegment;
import at.tuwien.prip.model.graph.AdjacencyGraph;
import at.tuwien.prip.model.graph.AdjacencyMatrix;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.hier.DocHierGraph;
import at.tuwien.prip.model.graph.hier.level.SegLevelGraph;
import at.tuwien.prip.model.graph.hier.level.WordLevel;
import at.tuwien.prip.model.token.StringTokenSequence;
import at.tuwien.prip.model.utils.DOMHelper;
import at.tuwien.prip.model.utils.DocGraphUtils;
import at.tuwien.prip.mozcore.utils.CSSException;
import at.tuwien.prip.mozcore.utils.MozCssUtils;

/**
 * DocumentGraphFactory.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Jun 8, 2011
 */
public class DocumentGraphFactory {

	public static class HTML
	{		
		/**
		 *
		 * @param document
		 * @return
		 */
		public static ISegmentGraph generateDocumentGraph (Document document)
		{
			ISegmentGraph result = null;

			SimpleTimer timer = new SimpleTimer();
			timer.startTask(0);
			Element root = document.getDocumentElement();

			List<GenericSegment> segments = new LinkedList<GenericSegment>();

			timer.startTask(1);
			List<Node> nodes = DOMHelper.Tree.Descendant.getDescendantsAndSelfNodes(root, 10000);

			List<Node> newNodes = new ArrayList<Node>();
			Iterator<Node> it = nodes.iterator();

			while (it.hasNext())
			{
				Node n = it.next();
				if (n.getNodeType()==Node.TEXT_NODE)
				{
					newNodes.add(n);
				}
				else if (n.getNodeType()==Node.COMMENT_NODE)
				{
					continue;
				}
				else 
				{
					Element e = (Element)n;
					NodeList children = e.getChildNodes();
					if (children.getLength()==0)
					{
						newNodes.add(e);
					}
					else if (containsTextNode(children))
					{
						newNodes.add(e);
					}
					else if ("word".equalsIgnoreCase(e.getNodeName()))
					{
						newNodes.add(e);
					}
					else if ("img".equalsIgnoreCase(e.getNodeName()))
					{
						newNodes.add(e);
					}
					else if ("input".equalsIgnoreCase(e.getNodeName()))
					{
						newNodes.add(e);
					}
					else if ("div".equalsIgnoreCase(e.getNodeName()))
					{
						newNodes.add(e);
					}
				}
			}

			ListUtils.unique(newNodes);
			nodes.addAll(newNodes);

			try
			{
				segments = w3cNode2GenericSegments(newNodes);
			}
			catch (CSSException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			timer.stopTask(1);


			//generate the document graph
			timer.startTask(4);

			AdjacencyGraph<GenericSegment> ag = new AdjacencyGraph<GenericSegment>();
			ag.addList(segments);
			ag.generateEdgesSingle();
			result = new DocumentGraph(ag);

			return result;
		}

		/**
		 * 
		 * @param items
		 * @return
		 */
		public static boolean containsTextNode(NodeList items)
		{
			for (int i=0; i<items.getLength(); i++)
			{
				if (items.item(i).getNodeType()==Node.TEXT_NODE)
					return true;
			}
			return false;
		}

		/**
		 * Convert W3C nodes to generic segments for layout graph generation...
		 *
		 * @param nodes
		 * @throws InternalAnalysisException
		 * @throws AnalyticsDomException
		 * @return
		 */
		public static List<GenericSegment> w3cNode2GenericSegments (List<Node> nodes)
		throws CSSException
		{
			List<GenericSegment> segments = new ArrayList<GenericSegment>();
			Map<Node,GenericSegment> most_general = new HashMap<Node,GenericSegment> ();

			for (Node n : nodes)
			{
				/*
				 * check that we don't already have this node or
				 * that this node is not a specialization of an
				 * already existent node...
				 */
				boolean exists = false;
				if (most_general.keySet().contains(n))
				{
					continue;
				}

				/*
				 * check that the element's bounding box is not too small
				 */
				if (n.getNodeType()==Node.COMMENT_NODE)
				{
					continue;
				}

				Iterator<Node> it = most_general.keySet().iterator();
				while (it.hasNext())
				{
					Element mg = (Element) it.next();

					if (DOMHelper.Tree.Ancestor.isAncestorOrSelfOf(mg, n)) {
						exists = true;
						break;
					}
					else if (DOMHelper.Tree.Ancestor.isAncestorOrSelfOf(n, mg)) {
						it.remove();
						segments.remove(mg);
						break;
					}
				}

				if (exists)
				{
					continue;
				}

				Rectangle2D r = DOMUtils.getNodeDimensions(n);
				if (r.getWidth()<5 || r.getHeight()<5 || r.getMinX()<0 || r.getMinY()<0)
				{
					continue;
				}
				else if (r.getWidth()>500 && r.getHeight()>800)
				{
					continue;
				}

				GenericSegment segment = null;
				if (n instanceof Element)
				{
					Element e = (Element) n;
					String tagName = e.getTagName();
					if ("div".equalsIgnoreCase(tagName))
					{
						continue;
					}
				}

				switch (n.getNodeType())
				{

				case Element.ATTRIBUTE_NODE:

					break;

				case Element.ELEMENT_NODE:

					Element e = (Element) n;

					//compute font
					String nodeText = e.getTextContent();
					Font font = null;
					if (nodeText!=null && nodeText.replaceAll(" ", "").length()>0)
					{
						font = MozCssUtils.getFont(n);
					}

					if ("img".equalsIgnoreCase(e.getTagName()))
					{
						ImageSegment imgSegment = new ImageSegment(
								(float)r.getMinX(), (float)r.getMaxX(),
								(float)r.getMinY(), (float)r.getMaxY());
						segment = imgSegment;
					}
					else if ("input".equalsIgnoreCase(e.getTagName()))
					{
						FormSegment formSegment = new FormSegment(
								(float)r.getMinX(), (float)r.getMaxX(),
								(float)r.getMinY(), (float)r.getMaxY());
						segment = formSegment;
					}
					else if ("div".equalsIgnoreCase(e.getTagName()))
					{
						if (e.getAttribute("style")!=null)
						{
							String val = e.getAttribute("style");
							System.out.print(val);
						}
					}
					else if ("word".equalsIgnoreCase(e.getTagName()))
					{
						if (nodeText==null || nodeText.trim().length()==0 || font==null) {
							continue;
						}
						TextSegment txtSegment = new TextSegment(
								(float)r.getMinX(), (float)r.getMaxX(),
								(float)r.getMinY(), (float)r.getMaxY());
						txtSegment.setText(nodeText);
						txtSegment.setFontName(font.getName());
						txtSegment.setFontSize(font.getSize());
						segment = txtSegment;
					}
					else
					{
						continue;
					}
					break;

				case Element.COMMENT_NODE:
					break;

				default:

					break;
				}

				// create the segment
				if (segment!=null) {
					segments.add(segment);
				}
			}
			return segments;
		}

		

}	

public static class PDF 
{
	/**
	 * Generate a flat document graph.
	 * 
	 * @param inFile
	 * @param page
	 * @param useSemantics
	 * @return
	 */
	public static DocumentGraph generateSimpleDocumentGraph(URI inFile,
			int page, boolean useSemantics) 
	{
		DocumentGraph result = new DocumentGraph();

		try
		{
			File inputDocFile = new File(inFile);
			byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);

			// process the PDF
			List<AdjacencyGraph<GenericSegment>> adjGraphList = 
				new ArrayList<AdjacencyGraph<GenericSegment>>();
			ProcessFile.processPDF(inputDoc,
					PageProcessor.PP_CHAR, // block contains all
					null, 
					true, //compute edges
					false, false, page, page, "", "", 0,
					adjGraphList, false);


			AdjacencyGraph<GenericSegment> g = adjGraphList.get(0);

			// split all segments according to their segmentation level...
			List<GenericSegment> blocks = new ArrayList<GenericSegment>();
			List<GenericSegment> lines = new ArrayList<GenericSegment>();
			List<GenericSegment> words = new ArrayList<GenericSegment>();
			List<GenericSegment> chars = new ArrayList<GenericSegment>();
			List<GenericSegment> images = new ArrayList<GenericSegment>();

			List<GenericSegment> segments = g.getVertList();
			for (GenericSegment segment : segments) {
				if (segment instanceof TextBlock) {
					TextBlock block = (TextBlock) segment;
					for (TextLine line : block.getItems()) {
						// words.addAll(convertTextLineToWordFragments(line));

						for (LineFragment lineFrag : line.getItems()) {
							List<TextFragment> textFrags = lineFrag
							.getItems();
							for (TextFragment tFrag : textFrags) {
								chars.addAll(tFrag.getItems());

							}
							lines.add(lineFrag);
						}

					}
					blocks.add(block);
				} else if (segment instanceof ImageSegment) {
					ImageSegment img = (ImageSegment) segment;
					images.add(img);
				}
			}

			// AdjacencyGraph<GenericSegment> g2 =
			// generate the TEXTBLOCK level graph
			AdjacencyGraph<GenericSegment> g1 = new AdjacencyGraph<GenericSegment>();
			g1.addList(words);
			g1.addList(images);
			g1.generateEdgesSingle();
			result = new DocumentGraph(g1);
			DocGraphUtils.flipReverseDocGraph(result);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentProcessingException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private static List<TextSegment> convertTextLineToWordFragments(
			TextLine line) {
		List<TextSegment> result = new ArrayList<TextSegment>();
		if (line.getText() == null)
			return result;

		List<String> wordFrags = new ArrayList<String>();// .splitIntoWords(line.getText());
		List<StringTokenSequence> seqs = TextUtils.splitIntoSentences(line
				.getText());
		for (StringTokenSequence sts : seqs) {
			String input = sts.toString().trim();
			List<String> words = TextUtils.splitIntoWords(input);
			// String[] wordsArr = sts.toString().trim().split("\\s");
			wordFrags.addAll(words); // ListUtils.toList(wordsArr,
			// String.class)
		}

		Color color = null;
		List<CharSegment> chars = new ArrayList<CharSegment>();
		for (LineFragment lineFrag : line.getItems()) {
			List<TextFragment> textFrags = lineFrag.getItems();
			for (TextFragment tFrag : textFrags) {
				color = tFrag.getColor();
				chars.addAll(tFrag.getItems());
			}
		}
		chars.add(new CharSegment(0, 0, 0, 0, "END", "", 0, color));

		List<CharSegment> special = new ArrayList<CharSegment>();
		List<CharSegment> segs = new ArrayList<CharSegment>();
		for (String wordFrag : wordFrags) {
			if (wordFrag.trim().length() == 0)
				continue;

			boolean done = false;
			chars.removeAll(segs);
			chars.removeAll(special);
			special = new ArrayList<CharSegment>();
			segs = new ArrayList<CharSegment>();

			for (CharSegment cs : chars) {
				String txt = cs.getText();
				if (txt == null) {
					continue;
				}

				if (txt != null && !txt.equals(" ")) {
					txt = txt.trim();
				}
				int index = wordFrag.indexOf(txt);
				if (!done && index >= 0
						&& wordFrag.indexOf(txt) < wordFrag.length()) {
					segs.add(cs);

					if (txt.length() == wordFrag.length()) {
						Rectangle bounds = null;
						List<OpTuple> operators = new ArrayList<OpTuple>();
						for (CharSegment seg : segs) {
							// operators.addAll(seg.getOperators());
							operators.add(seg.getSourceOp());
							if (bounds == null) {
								bounds = seg.getBoundingRectangle();
							} else {
								bounds = bounds.union(seg
										.getBoundingRectangle());
							}
						}
						if (bounds == null) {
							System.err.println();
						}
						float x1 = bounds.x;
						float x2 = bounds.x + bounds.width;
						float y1 = bounds.y;
						float y2 = bounds.y + bounds.height;
						String fontName = segs.get(0).getFontName();
						float fontSize = segs.get(0).getFontSize();
						TextSegment ts = new TextSegment(x1, x2, y1, y2,
								wordFrag, fontName, fontSize, color);
						ts.setOperators(operators); // include pdf operators
						result.add(ts);
						break;
					}
				} else {
					// if (segs.size()>0) {
					// done = true; //done with this word
					// special.add(cs);
					// }
					// else
					if (txt.trim().length() == 0) {
						special.add(cs);
						continue;
					}

					Rectangle bounds = null;
					for (CharSegment seg : segs) {
						if (bounds == null) {
							bounds = seg.getBoundingRectangle();
						} else {
							bounds = bounds.union(seg
									.getBoundingRectangle());
						}
					}
					if (bounds == null) {
						// System.err.println();
						continue;
					}
					float x1 = bounds.x;
					float x2 = bounds.x + bounds.width;
					float y1 = bounds.y;
					float y2 = bounds.y + bounds.height;
					String fontName = segs.get(0).getFontName();
					float fontSize = segs.get(0).getFontSize();
					TextSegment ts = new TextSegment(x1, x2, y1, y2,
							wordFrag, fontName, fontSize, color);
					result.add(ts);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Generate a hierarchical document graph from a PDF file. This is the
	 * dynamic granularity version (no levels).
	 * 
	 * @param inFile
	 * @param page
	 * @return
	 */
	public static DocHierGraph generateDocumentHGraph(URI inFile, int page,
			boolean useSemantics) {
		DocHierGraph result = new DocHierGraph();

		try {
			File inputDocFile = new File(inFile);
			if (!inputDocFile.exists()) {
				ErrorDump.error(DocumentGraphFactory.class,
						"File does not exist: " + inFile.toString());
				return null;
			}

			byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);

			List<AdjacencyGraph<GenericSegment>> adjGraphList = new ArrayList<AdjacencyGraph<GenericSegment>>();

			// process the PDF
			List<Page> pages = ProcessFile.processPDF(
					inputDoc,
					PageProcessor.PP_BLOCK, // block contains all
					null,
					true, //compute edges
					false, false, page, page, "", "", 0,
					adjGraphList, false);

			result.setDocumentURI(inFile.toString());
			result.setNumPages(pages.size()); // remember page count

			AdjacencyGraph<GenericSegment> g = adjGraphList.get(0);

			// split all segments according to their segmentation level...
			List<GenericSegment> blocks = new ArrayList<GenericSegment>();
			List<GenericSegment> lines = new ArrayList<GenericSegment>();
			List<GenericSegment> words = new ArrayList<GenericSegment>();
			List<GenericSegment> chars = new ArrayList<GenericSegment>();
			List<GenericSegment> imgs = new ArrayList<GenericSegment>();

			List<GenericSegment> segments = g.getVertList();
			for (GenericSegment segment : segments) {
				if (segment instanceof TextBlock) {
					TextBlock block = (TextBlock) segment;
					for (TextLine line : block.getItems()) {
						for (LineFragment lineFrag : line.getItems()) {
							List<TextFragment> textFrags = lineFrag
							.getItems();
							for (TextFragment tFrag : textFrags) {
								chars.addAll(tFrag.getItems());
								words.add(tFrag);
							}
							lines.add(lineFrag);
						}

					}
					blocks.add(block);
				} else if (segment instanceof ImageSegment) {
					ImageSegment img = (ImageSegment) segment;
					imgs.add(img);
				}
			}

			// generate the TEXTBLOCK level graph
			AdjacencyGraph<GenericSegment> g1 = new AdjacencyGraph<GenericSegment>();
			g1.addList(blocks);
			g1.addList(imgs);
			g1.generateEdgesSingle();
			DocumentGraph dg1 = new DocumentGraph(g1);
			// dg1.makePlanar();
			result.addDocumentGraph(dg1, false, false);

			// generate the TEXTLINE level graph
			AdjacencyGraph<GenericSegment> g2 = new AdjacencyGraph<GenericSegment>();
			g2.addList(lines);
			g2.addList(imgs);
			g2.generateEdgesSingle();
			DocumentGraph dg2 = new DocumentGraph(g2);
			// dg2.makePlanar();
			result.addDocumentGraph(dg2, false, false);

			// generate the WORD level graph
			AdjacencyGraph<GenericSegment> g3 = new AdjacencyGraph<GenericSegment>();
			g3.addList(words);
			g3.addList(imgs);
			g3.generateEdgesSingle();
			DocumentGraph dg3 = new DocumentGraph(g3);
			// dg3.makePlanar();
			result.addDocumentGraph(dg3, true, true);

			// //generate the CHAR level
			// AdjacencyGraph<CharSegment> g3 = new
			// AdjacencyGraph<CharSegment>();
			// g3.addList(chars);
			// g3.generateEdgesSingle();
			// DocumentGraph dg3 = new DocumentGraph(g3);
			// result.addDocumentGraph(dg3);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Generate a hierarchical document graph from a PDF file. This is the
	 * fixed level version.
	 * 
	 * @param inFile
	 * @param page
	 * @param planar
	 * @return
	 */
	public static SegLevelGraph generateDocumentHierarchy(String inFile,
			int page, boolean images, boolean planar, boolean clean) {
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);
		SegLevelGraph result = new SegLevelGraph();

		try {
			File inputDocFile;
			if (inFile.startsWith("file://")) {
				inputDocFile = new File(inFile.substring(5));
			} else if (inFile.startsWith("file:/")) {
				inputDocFile = new File(inFile.substring(5));
			} else {
				inputDocFile = new File(inFile);
			}

			byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);

			List<AdjacencyGraph<GenericSegment>> adjGraphList = new ArrayList<AdjacencyGraph<GenericSegment>>();

			/***********************************************************************
			 * process the PDF
			 */
			List<Page> pages = ProcessFile.processPDF(
					inputDoc,
					PageProcessor.PP_FRAGMENT,// PP_FRAGMENT,//PP_BLOCK,//PP_BLOCK,
					// //block contains all
					null, 
					true,//compute edges
					true, false, page, page, "", "", 0, adjGraphList,
					false);

			result.setDocumentURI(inFile);

			int pgCnt = pages.size();
			result.setNumPages(pgCnt); // remember page count

			AdjacencyGraph<GenericSegment> g = adjGraphList.get(0);

			/***********************************************************************
			 * split all segments according to their segmentation level...
			 */
			List<GenericSegment> blocks = new ArrayList<GenericSegment>();
			List<GenericSegment> lines = new ArrayList<GenericSegment>();
			List<GenericSegment> words = new ArrayList<GenericSegment>();
			List<GenericSegment> chars = new ArrayList<GenericSegment>();
			List<GenericSegment> imgs = new ArrayList<GenericSegment>();
			List<GenericSegment> segments = g.getVertList();

			List<OpTuple> pdfOps4 = new ArrayList<OpTuple>();

			for (GenericSegment segment : segments) {
				// TEXT BLOCK
				if (segment instanceof TextBlock) {
					List<OpTuple> pdfOps3 = new ArrayList<OpTuple>();
					TextBlock block = (TextBlock) segment;
					for (TextLine line : block.getItems()) {
						List<OpTuple> pdfOps2 = new ArrayList<OpTuple>();

						for (LineFragment lineFrag : line.getItems()) {
							List<OpTuple> pdfOps1 = new ArrayList<OpTuple>();
							List<TextFragment> textFrags = lineFrag
							.getItems();
							for (TextFragment tFrag : textFrags) {
								// System.out.println(tFrag.toExtendedString());
								chars.addAll(tFrag.getItems());

								/* remember PDF operators */
								List<OpTuple> pdfOps = new ArrayList<OpTuple>();
								for (CharSegment cs : tFrag.getItems()) {
									List<OpTuple> pdfOp = new ArrayList<OpTuple>();
									pdfOp.add(cs.getSourceOp());
									cs.setOperators(pdfOp);
									pdfOps.add(cs.getSourceOp());
								}
								// words.add(tFrag);
								tFrag.setOperators(pdfOps);
								pdfOps1.addAll(pdfOps);
							}
							lines.add(lineFrag);
							lineFrag.setOperators(pdfOps1);
							pdfOps2.addAll(pdfOps1);
						}
						line.setOperators(pdfOps2);
						pdfOps3.addAll(pdfOps2);

						// words.addAll(convertTextLineToWordFragments(line));
					}
					pdfOps4.addAll(pdfOps3);
					block.setOperators(pdfOps3);
					blocks.add(block);
				}

				// TEXT LINE
				else if (segment instanceof TextLine) {
					TextLine line = (TextLine) segment;
					List<OpTuple> pdfOps2 = new ArrayList<OpTuple>();

					for (LineFragment lineFrag : line.getItems()) {
						List<OpTuple> pdfOps1 = new ArrayList<OpTuple>();
						List<TextFragment> textFrags = lineFrag.getItems();
						for (TextFragment tFrag : textFrags) {
							// System.out.println(tFrag.toExtendedString());
							chars.addAll(tFrag.getItems());

							/* remember PDF operators */
							List<OpTuple> pdfOps = new ArrayList<OpTuple>();
							for (CharSegment cs : tFrag.getItems()) {
								List<OpTuple> pdfOp = new ArrayList<OpTuple>();
								pdfOp.add(cs.getSourceOp());
								cs.setOperators(pdfOp);
								pdfOps.add(cs.getSourceOp());
							}
							// words.add(tFrag);
							tFrag.setOperators(pdfOps);
							pdfOps1.addAll(pdfOps);
						}
						lines.add(lineFrag);
						lineFrag.setOperators(pdfOps1);
						pdfOps2.addAll(pdfOps1);
					}
					line.setOperators(pdfOps2);
					// words.addAll(convertTextLineToWordFragments(line));
				}

				// IMAGE SEGMENT
				else if (segment instanceof ImageSegment) {
					ImageSegment img = (ImageSegment) segment;
					imgs.add(img);
				}

				// CHAR SEGMENT
				else if (segment instanceof CharSegment) {
					CharSegment cs = (CharSegment) segment;
					List<OpTuple> pdfOp = new ArrayList<OpTuple>();
					pdfOp.add(cs.getSourceOp());
					cs.setOperators(pdfOp);
					chars.add(cs);
				}

				if (segment.getOperators().isEmpty()) {
					segment.setOperators(pdfOps4);
				}
			}

			/***********************************************************************
			 * generate the TEXTBLOCK level
			 */
			// AdjacencyGraph<GenericSegment> g1 = new
			// AdjacencyGraph<GenericSegment>();
			// g1.addList(blocks);
			//
			// if (images) { g1.addList(imgs); }
			//
			// AdjacencyMatrix<GenericSegment> am = new
			// AdjacencyMatrix<GenericSegment>(blocks);
			// AdjacencyGraph<GenericSegment> g1 = am.toAdjacencyGraph();
			// g1.generateEdgesSingle();
			// DocumentGraph dg1 = new DocumentGraph(g1);
			//
			// if (planar) { dg1.makePlanar(); }
			// if (clean) { dg1.toMinimalEdgeGraph(); }
			//
			// TextBlockLevel tbLevel = new TextBlockLevel(result);
			// tbLevel.setGraph(dg1);
			// result.addLevel(tbLevel);

			// /***********************************************************************
			// * generate the TEXTLINE level
			// */
			// AdjacencyGraph<GenericSegment> g2 = new
			// AdjacencyGraph<GenericSegment>();
			// g2.addList(lines);
			//
			// if (images) { g2.addList(imgs); }
			//
			// am = new AdjacencyMatrix<GenericSegment>(lines);
			// AdjacencyGraph<GenericSegment> g2 = am.toAdjacencyGraph();
			// g2.generateEdgesSingle();
			// DocumentGraph dg2 = new DocumentGraph(g2);
			//
			// if (planar) { dg2.makePlanar(); }
			// if (clean) { dg2.toMinimalEdgeGraph(); }
			//
			// TextLineLevel tlLevel = new TextLineLevel(result);
			// tlLevel.setGraph(dg2);
			// result.addLevel(tlLevel);
			//
			// /***********************************************************************
			// * generate the WORD level
			// */
			// AdjacencyGraph<GenericSegment> g3 = new
			// AdjacencyGraph<GenericSegment>();
			// g3.addList(words);
			//
			// if (images) { g3.addList(imgs); }
			AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(
					words);
			AdjacencyGraph<GenericSegment> g3 = am.toAdjacencyGraph(false);
			//
			// g3.generateEdgesSingle();
			DocumentGraph dg3 = new DocumentGraph(g3);
			//
			if (planar) {
				dg3.makePlanar();
			}
			// if (clean) { dg3.toMinimalEdgeGraph(); }
			//
			WordLevel wordLevel = new WordLevel(result);
			wordLevel.setGraph(dg3);

			result.addLevel(wordLevel);

			/***********************************************************************
			 * generate the CHAR level (this takes time)
			 */
			// AdjacencyGraph<GenericSegment> g4 = new
			// AdjacencyGraph<GenericSegment>();
			// g4.addList(chars);
			// g4.generateEdgesSingle();
			// DocumentGraph dg4 = new DocumentGraph(g4);
			// if (planar) { dg4.makePlanar(); }
			// CharLevel charLevel = new CharLevel(result);
			// charLevel.setGraph(dg4);
			//
			// result.addLevel(charLevel);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentProcessingException e) {
			e.printStackTrace();
		}

		result.precomputeContractionMaps();

		timer.stopTask(0);
		ErrorDump
		.info(DocumentGraphFactory.class,
				"Loaded document graph in "
				+ timer.getTimeMillis(0) + "ms");
		return result;
	}

	/**
	 * Generate a 1D document graph from a page segmentation.
	 * 
	 * @param page
	 * @return
	 */
	public static DocumentGraph generateDocumentGraph(Page page) {
		DocumentGraph result = new DocumentGraph();

		AdjacencyGraph<GenericSegment> ag = page.getAdjGraph();

		/***********************************************************************
		 * split all segments according to their segmentation level...
		 */
		List<GenericSegment> blocks = new ArrayList<GenericSegment>();
		List<GenericSegment> lines = new ArrayList<GenericSegment>();
		List<GenericSegment> words = new ArrayList<GenericSegment>();
		List<GenericSegment> chars = new ArrayList<GenericSegment>();
		List<GenericSegment> imgs = new ArrayList<GenericSegment>();

		// TextBlock -- output of segmentation
		// TB contains TextLine, contains LineFragment
		// contains TextFragment, contains CharSegment

		List<GenericSegment> segments = ag.getVertList();
		List<OpTuple> pdfOps4 = new ArrayList<OpTuple>();
		for (GenericSegment segment : segments) {
			if (segment instanceof TextBlock) {
				List<OpTuple> pdfOps3 = new ArrayList<OpTuple>();
				TextBlock block = (TextBlock) segment;
				for (TextLine line : block.getItems()) {
					List<OpTuple> pdfOps2 = new ArrayList<OpTuple>();
					// words.addAll(convertTextLineToWordFragments(line));

					for (LineFragment lineFrag : line.getItems()) {
						List<OpTuple> pdfOps1 = new ArrayList<OpTuple>();
						List<TextFragment> textFrags = lineFrag.getItems();
						for (TextFragment tFrag : textFrags) {
							// System.out.println(tFrag.toExtendedString());
							chars.addAll(tFrag.getItems());

							/* remember PDF operators */
							List<OpTuple> pdfOps = new ArrayList<OpTuple>();
							for (CharSegment cs : tFrag.getItems()) {
								pdfOps.add(cs.getSourceOp());
							}
							tFrag.setOperators(pdfOps);
							pdfOps1.addAll(pdfOps);
						}
						lines.add(lineFrag);
						lineFrag.setOperators(pdfOps1);
						pdfOps2.addAll(pdfOps1);
					}
					line.setOperators(pdfOps2);
					pdfOps3.addAll(pdfOps2);
				}
				pdfOps4.addAll(pdfOps3);
				block.setOperators(pdfOps3);
				blocks.add(block);
			} else if (segment instanceof ImageSegment) {
				ImageSegment img = (ImageSegment) segment;
				imgs.add(img);
			}
			segment.setOperators(pdfOps4);
		}

		// generate the WORD level graph
		AdjacencyGraph<GenericSegment> g1 = new AdjacencyGraph<GenericSegment>();
		g1.addList(words);
		g1.addList(imgs);
		g1.generateEdgesSingle();
		result = new DocumentGraph(g1);
		DocGraphUtils.flipReverseDocGraph(result);

		return result;
	}

	/**
	 * 
	 * @param inFile
	 * @param page
	 * @return
	 * @throws PdfDocumentProcessingException
	 * @throws DocumentProcessingException
	 */
	public static DocumentGraph generateDocumentGraphNew(String inFile,	int page) 
	throws PdfDocumentProcessingException 
	{
		DocumentGraph result = new DocumentGraph();

		try 
		{
			File inputDocFile;
			if (inFile.startsWith("file://")) {
				inputDocFile = new File(inFile.substring(5));
			} else if (inFile.startsWith("file:/")) {
				inputDocFile = new File(inFile.substring(5));
			} else {
				inputDocFile = new File(inFile);
			}

			byte[] inputDoc = ProcessFile.getBytesFromFile(inputDocFile);

			List<AdjacencyGraph<GenericSegment>> adjGraphList = 
				new ArrayList<AdjacencyGraph<GenericSegment>>();

			//read detail level from preferences
			int detailLevel = PageProcessor.PP_LINE;
			boolean wordLevel = false;

			IPreferenceStore prefStore = LearnUIPlugin.getDefault().getPreferenceStore();
			if (prefStore.getString(PREF_ANNO_DETAIL).equals(
					PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_INSTR)) 
			{
				detailLevel = PageProcessor.PP_CHAR;//FRAGMENT;
			}
			else if (prefStore.getString(PREF_ANNO_DETAIL).equals(
					PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_WORD)) 
			{
				detailLevel = PageProcessor.PP_LINE;
				wordLevel = true;
			} 
			else if (prefStore.getString(PREF_ANNO_DETAIL).equals(
					PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_CHAR)) 
			{
				detailLevel = PageProcessor.PP_CHAR;
			}
			else if (prefStore.getString(PREF_ANNO_DETAIL).equals(
					PreferenceConstants.PREF_ANNO_VAL_GRAPHDETAIL_LINE)) 
			{
				detailLevel = PageProcessor.PP_LINE;
			}

			// process the PDF
			List<Page> pages = null;
			try 
			{
				pages = ProcessFile.processPDF(inputDoc,
						detailLevel,// PageProcessor.PP_LINE,//LINE, //block
						// contains all
						null, 
						false, //compute edges
						false, false, page, page, "", "", 0,
						adjGraphList, false);
			} 
			catch (DocumentProcessingException e)
			{
				throw new PdfDocumentProcessingException(e);
			}

			if (pages == null || adjGraphList == null
					|| adjGraphList.size() == 0) {
				return null;
			}

			AdjacencyGraph<GenericSegment> g = adjGraphList.get(0);

			/***********************************************************************
			 * split all segments according to their segmentation level...
			 */
			List<GenericSegment> blocks = new ArrayList<GenericSegment>();
			List<GenericSegment> lines = new ArrayList<GenericSegment>();
			List<GenericSegment> words = new ArrayList<GenericSegment>();
			List<GenericSegment> chars = new ArrayList<GenericSegment>();
			List<GenericSegment> imgs = new ArrayList<GenericSegment>();
			List<GenericSegment> instr = new ArrayList<GenericSegment>();

			List<GenericSegment> segments = g.getVertList();
			List<OpTuple> pdfOps4 = new ArrayList<OpTuple>();
			for (GenericSegment segment : segments) 
			{
				if (segment instanceof TextBlock)
				{
					List<OpTuple> pdfOps3 = new ArrayList<OpTuple>();
					TextBlock block = (TextBlock) segment;
					for (TextLine line : block.getItems()) 
					{
						List<OpTuple> pdfOps2 = new ArrayList<OpTuple>();
						words.addAll(convertTextLineToWordFragments(line));

						for (LineFragment lineFrag : line.getItems()) 
						{
							List<OpTuple> pdfOps1 = new ArrayList<OpTuple>();
							List<TextFragment> textFrags = lineFrag.getItems();

							for (TextFragment tFrag : textFrags)
							{
								// System.out.println(tFrag.toExtendedString());
								chars.addAll(tFrag.getItems());

								/* remember PDF operators */
								List<OpTuple> pdfOps = new ArrayList<OpTuple>();

								for (CharSegment cs : tFrag.getItems()) 
								{
									instr.add(cs);
									pdfOps.add(cs.getSourceOp());
								}
								tFrag.setOperators(pdfOps);
								pdfOps1.addAll(pdfOps);
							}
							lines.add(lineFrag);
							lineFrag.setOperators(pdfOps1);
							pdfOps2.addAll(pdfOps1);
						}
						line.setOperators(pdfOps2);
						pdfOps3.addAll(pdfOps2);
					}
					pdfOps4.addAll(pdfOps3);
					block.setOperators(pdfOps3);
					blocks.add(block);
				} 
				else if (segment instanceof ImageSegment) 
				{
					ImageSegment img = (ImageSegment) segment;
					imgs.add(img);
				}
				else
				{
					System.err.println();
				}
				segment.setOperators(pdfOps4);
			}

			// generate the TEXTBLOCK level graph
			AdjacencyGraph<GenericSegment> g1 = new AdjacencyGraph<GenericSegment>();
			if (wordLevel)
			{
				g1.addList(words);
			} 
			else if (detailLevel == PageProcessor.PP_LINE) 
			{
				g1.addList(lines);
			}
			else if (detailLevel == PageProcessor.PP_CHAR) 
			{
				g1.addList(chars);
			}
			else if (detailLevel == PageProcessor.PP_INSTRUCTION) 
			{
				g1.addList(instr);
			}

			// add images...
			g1.addList(imgs);

			// compute edges
			//				g1.generateEdgesSingle();
			result = new DocumentGraph(g1);

			// result = new DocumentGraph(g);
			DocGraphUtils.flipReverseDocGraph(result);
			result.setNumPages(pages.size()); // remember page count

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}

// class HTML
// {
//
// /**
// * Construct a document graph from a document.
// *
// * @param document
// * @return
// */
// public static DocHierGraph generateDocumentHGraph (Document document)
// {
// Element root = document.getDocumentElement();
//
// List<GenericSegment> nodes = new LinkedList<GenericSegment>();
// List<Element> elems = DOMHelper.Tree.Descendant.getLeafDescendants(root);
// List<Node> all_nodes = new ArrayList<Node>();
//
// for (Element elem : elems) {
// all_nodes.add(elem);
// }
//
// try
// {
// nodes = w3cNode2GenericSegments(all_nodes);
//
// } catch (CSSException e) {
// e.printStackTrace();
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// //generate the document graph
// DocHierGraph result = new DocHierGraph();
//
// AdjacencyGraph<GenericSegment> adjGraph = new
// AdjacencyGraph<GenericSegment>();
// adjGraph.addList(nodes);
// adjGraph.generateEdgesSingle();
// DocumentGraph dg1 = new DocumentGraph(adjGraph);
// result.addDocumentGraph(dg1, true, false);
//
// // //do segmentation if necessary
// // DocumentFragmentView fragView = FragmentViewEngine.setup(document);
// // List<GenericSegment> fragSegs =
// FragmentUtils.fragmentView2Segments(fragView);
// // if (fragSegs.size()>0)
// // {
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(fragSegs);
// // adjGraph.generateEdgesSingle();
// // DocumentGraph dg = new DocumentGraph(adjGraph);
// // result.addDocumentGraph(dg, false, true);
// // }
//
// //search for form segments
// List<GenericSegment> forms =
// FormEngine.generateAllFormSegments(document);
// if (forms.size()>0) {
// adjGraph = new AdjacencyGraph<GenericSegment>();
// adjGraph.addList(forms);
// adjGraph.generateEdgesSingle();
// DocumentGraph dg = new DocumentGraph(adjGraph);
// result.addDocumentGraph(dg, false, true);
// }
//
// //extract semantic segments
// List<GenericSegment> sems = SemanticsEngine.extractSemanticSegments(dg1);
// if (sems.size()>0) {
// adjGraph = new AdjacencyGraph<GenericSegment>();
// adjGraph.addList(sems);
// adjGraph.generateEdgesSingle();
// DocumentGraph dg = new DocumentGraph(adjGraph);
// result.addDocumentGraph(dg, false, true);
// }
//
// result.computeDimensions();
// return result;
// }
// /**
// *
// * @param document
// * @return
// */
// public static SegLevelGraph generateDocumentHierarchySimple (Document
// document)
// {
// SimpleTimer timer = new SimpleTimer();
// timer.startTask(0);
// SegLevelGraph result = new SegLevelGraph();
// Element root = document.getDocumentElement();
//
// List<GenericSegment> segments = new LinkedList<GenericSegment>();
//
// timer.startTask(1);
// List<Node> nodes =
// DOMHelper.Tree.Descendant.getDescendantsAndSelfNodes(root, 1000);
// //getAllLeafs(root);
//
// List<Node> newNodes = new ArrayList<Node>();
// Iterator<Node> it = nodes.iterator();
//
// while (it.hasNext())
// {
// Node n = it.next();
// if (n.getNodeType()==Node.TEXT_NODE)
// {
// newNodes.add(n);
// }
// else if (n instanceof Element)
// {
// Element e = (Element)n;
// String content = DOMHelper.Text.getElementWithChildrenText(e);
// if (content.length()==0)
// {
// continue;
// }
//
// if (e.getNodeType()==Node.TEXT_NODE)
// {
// newNodes.add(e);
// }
// else if ("p".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h1".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h2".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h3".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("li".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("a".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("emph".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("em".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// // // else
// // if ("strong".equalsIgnoreCase(e.getNodeName()))
// // {
// // newNodes.add(e);
// // }
// // // else if ("i".equalsIgnoreCase(e.getNodeName()))
// // // {
// // // newNodes.add(e);
// // // }
// // // else if ("b".equalsIgnoreCase(e.getNodeName()))
// // // {
// // // newNodes.add(e);
// // // }
// // else
// // {
// // String nodeName = e.getNodeName();
// // System.out.println(nodeName);
// // }
// }
// }
//
// ListUtils.unique(newNodes);
// nodes.addAll(newNodes);
//
// try
// {
// segments = w3cNode2GenericSegments(newNodes);
// }
// catch (CSSException e)
// {
// e.printStackTrace();
// }
// catch (Exception e)
// {
// e.printStackTrace();
// }
//
// timer.stopTask(1);
//
// //add form segments
// timer.startTask(2);
// List<GenericSegment> forms =
// FormEngine.generateAllFormSegments(document);
// if (forms.size()>0) {
// segments.addAll(forms);
// }
// timer.stopTask(2);
//
// //generate the document graph
// timer.startTask(4);
//
// AdjacencyMatrix<GenericSegment> am = new
// AdjacencyMatrix<GenericSegment>(segments);
// AdjacencyGraph<GenericSegment> ag = am.toAdjacencyGraph();
// DocumentGraph dg2 = new DocumentGraph(ag);
//
// WordLevel level = new WordLevel(result);
// level.setGraph(dg2);
// result.addLevel(level);
// timer.stopTask(4);
//
// result.setDefaultLevel(2);
// result.computeDimensions();
// return result;
// }
//
// /**
// * Generate a document hierarchy from Web documents.
// *
// * @param document
// * @return
// */
// public static SegLevelGraph generateDocumentHierarchy (Document document)
// {
// SimpleTimer timer = new SimpleTimer();
// timer.startTask(0);
// SegLevelGraph result = new SegLevelGraph();
// Element root = document.getDocumentElement();
//
// List<GenericSegment> segments = new LinkedList<GenericSegment>();
//
// timer.startTask(1);
// List<Node> nodes =
// DOMHelper.Tree.Descendant.getDescendantsAndSelfNodes(root, 1000);
// //getAllLeafs(root);
//
// List<Node> newNodes = new ArrayList<Node>();
// Iterator<Node> it = nodes.iterator();
//
// while (it.hasNext())
// {
// Node n = it.next();
// if (n.getNodeType()==Node.TEXT_NODE)
// {
// newNodes.add(n);
// }
// else if (n instanceof Element)
// {
// Element e = (Element)n;
// String content = DOMHelper.Text.getElementWithChildrenText(e);
// if (content.length()==0)
// {
// continue;
// }
//
// if (e.getNodeType()==Node.TEXT_NODE)
// {
// newNodes.add(e);
// }
// else if ("p".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h1".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h2".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("h3".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("li".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("a".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("emph".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// else if ("em".equalsIgnoreCase(e.getNodeName()))
// {
// newNodes.add(e);
// }
// // // else
// // if ("strong".equalsIgnoreCase(e.getNodeName()))
// // {
// // newNodes.add(e);
// // }
// // // else if ("i".equalsIgnoreCase(e.getNodeName()))
// // // {
// // // newNodes.add(e);
// // // }
// // // else if ("b".equalsIgnoreCase(e.getNodeName()))
// // // {
// // // newNodes.add(e);
// // // }
// // else
// // {
// // String nodeName = e.getNodeName();
// // System.out.println(nodeName);
// // }
// }
// }
//
// ListUtils.unique(newNodes);
// nodes.addAll(newNodes);
//
// try
// {
// segments = w3cNode2GenericSegments(newNodes);
// }
// catch (CSSException e)
// {
// e.printStackTrace();
// }
// catch (Exception e)
// {
// e.printStackTrace();
// }
//
// timer.stopTask(1);
//
// //add form segments
// timer.startTask(2);
// List<GenericSegment> forms =
// FormEngine.generateAllFormSegments(document);
// if (forms.size()>0) {
// segments.addAll(forms);
// }
// timer.stopTask(2);
//
// //generate the document graph
// timer.startTask(4);
//
// AdjacencyMatrix<GenericSegment> am = new
// AdjacencyMatrix<GenericSegment>(segments);
// AdjacencyGraph<GenericSegment> ag = am.toAdjacencyGraph();
// DocumentGraph dg2 = new DocumentGraph(ag);
// // AdjacencyGraph<GenericSegment> adjGraph = new
// AdjacencyGraph<GenericSegment>();
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(segments);
// // adjGraph.generateEdgesSingle();
//
//
// // DocumentMatrix dm = DocumentMatrix.newInstance(dg2);
// // dm.addReadingOrderRelations(dg2);
//
// WordLevel level = new WordLevel(result);
// level.setGraph(dg2);
// result.addLevel(level);
// timer.stopTask(4);
//
// // //split words
// // timer.startTask(3);
// // List<GenericSegment> words = new ArrayList<GenericSegment>();
// // for (GenericSegment node : segments) {
// // if (node instanceof TextSegment) {
// // TextSegment ts = (TextSegment) node;
// // String text = ts.getText();
// // float height = ts.getY2() - ts.getY1();
// // float width = ts.getX2() - ts.getX1();
// // int numLines = (int) (height % ts.getFontSize());
// // int lineNum = 1;
// // List<String> wds = TextUtils.splitIntoWords(text);
// // if (wds.size()<=1) {
// //
// // } else {
// // int xOffset = 0;
// // int yOffset = 0;
// // for (String word : wds) {
// // float wordLength = (width/(text.length()*numLines))*word.length();
// // if (xOffset + wordLength > width) {
// // xOffset = 0;
// // lineNum ++;
// // yOffset = (int) (lineNum * ts.getFontSize());
// // }
// // TextSegment seg = new TextSegment(
// // ts.getX1() + xOffset,
// // ts.getX1() + xOffset + wordLength,
// // ts.getY1() + yOffset,
// // ts.getY2() + yOffset);
// // xOffset+=(width/(text.length()*numLines))*word.length() + 1;
// // seg.setText(word);
// // words.add(seg);
// // }
// // }
// // }
// // }
// //
// // //generate the document graph
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(words);
// // adjGraph.generateEdgesSingle();
// // DocumentGraph dg1 = new DocumentGraph(adjGraph);
// // CustomLevel l1 = new CustomLevel(1, result);
// // l1.setGraph(dg1);
// // result.addLevel(l1);
// // timer.stopTask(3);
//
// //generate the block level document graph
// timer.startTask(5);
// List<GenericSegment> textSegs = new ArrayList<GenericSegment>();
// List<LayoutColumn> cols = DocumentLayoutEngine.extractTables(dg2);
// for (LayoutColumn col : cols)
// {
// boolean valid = true;
// double dist = -1;
// double prevBottom = -1;
// List<DocNode> colElems = col.getElements();
// for (DocNode colElem : colElems)
// {
// double top = colElem.getSegY1();
// if (prevBottom!=-1 && dist==-1)
// {
// dist = top - prevBottom;
// }
// else if (dist!=-1)
// {
// if (top - prevBottom != dist) {
// valid = false;
// break;
// }
// }
// prevBottom = colElem.getSegY2();
// }
// if (valid)
// {
// //add layout column as segment
// Rectangle r = col.getBounds();
// textSegs.add(new TextBlock(
// (float)r.getMinX(),
// (float)r.getMaxX(),
// (float)r.getMinY(),
// (float)r.getMaxY()));
//
// }
// }
// if (textSegs.size()>0) {
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(textSegs);
// // adjGraph.generateEdgesSingle();
// // DocumentGraph dg = new DocumentGraph(adjGraph);
// // CustomLevel l3 = new CustomLevel(3, result);
// // l3.setGraph(dg);
// // result.addLevel(l3);
// }
// timer.stopTask(5);
//
// // //do segmentation if necessary
// // timer.startTask(6);
// // DocumentFragmentView fragView = FragmentViewEngine.setup(document);
// // List<GenericSegment> fragSegs =
// FragmentUtils.fragmentView2Segments(fragView);
// // if (fragSegs.size()>0) {
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(fragSegs);
// // adjGraph.generateEdgesSingle();
// // DocumentGraph dg = new DocumentGraph(adjGraph);
// // CustomLevel l4 = new CustomLevel(4, result);
// // l4.setGraph(dg);
// // result.addLevel(l4);
// // }
// // timer.stopTask(6);
//
// //extract semantic segments
// timer.startTask(7);
// // List<GenericSegment> sems = SemanticsEngine.extractSegments(dg2);
// // if (sems.size()>0) {
// // adjGraph = new AdjacencyGraph<GenericSegment>();
// // adjGraph.addList(sems);
// // adjGraph.generateEdgesSingle();
// // DocumentGraph dg = new DocumentGraph(adjGraph);
// // CustomLevel l5 = new CustomLevel(5, result);
// // l5.setGraph(dg);
// // result.addLevel(l5);
// // }
// timer.stopTask(7);
//
// timer.stopTask(0);
// ErrorDump.debug(DocumentGraphFactory.class,
// "\nTask 1 (generate segments): "+timer.getTimeMillis(1) +
// "\nTask 2 (forms): "+timer.getTimeMillis(2) +
// // "\nTask 3 (words): "+timer.getTimeMillis(3) +
// "\nTask 4 (generate graph): "+timer.getTimeMillis(4) +
// "\nTask 5 (block): "+timer.getTimeMillis(5) +
// "\nTask 6 (segmentation): "+timer.getTimeMillis(6) +
// "\nTask 7 (semantics): "+timer.getTimeMillis(7) +
// "\nTotal: "+timer.getTimeMillis(0)
//
// );
//
// result.setDefaultLevel(2);
// result.computeDimensions();
// return result;
// }
//
// /**
// * Convert W3C nodes to generic segments for layout graph generation...
// *
// * @param nodes
// * @throws InternalAnalysisException
// * @throws AnalyticsDomException
// * @return
// */
// public static List<GenericSegment> w3cNode2GenericSegments (List<Node>
// nodes)
// throws CSSException
// {
// List<GenericSegment> segments = new ArrayList<GenericSegment>();
// Map<Node,GenericSegment> most_general = new HashMap<Node,GenericSegment>
// ();
//
// for (Node n : nodes)
// {
// /*
// * check that we don't already have this node or
// * that this node is not a specialization of an
// * already existent node...
// */
// boolean exists = false;
// if (most_general.keySet().contains(n))
// {
// continue;
// }
//
// /*
// * check that the element's bounding box is not too small
// */
// if (n.getNodeType()==Node.COMMENT_NODE)
// {
// continue;
// }
//
// Iterator<Node> it = most_general.keySet().iterator();
// while (it.hasNext())
// {
// Element mg = (Element) it.next();
//
// if (DOMHelper.Tree.Ancestor.isAncestorOrSelfOf(mg, n)) {
// exists = true;
// break;
// }
// else if (DOMHelper.Tree.Ancestor.isAncestorOrSelfOf(n, mg)) {
// it.remove();
// segments.remove(mg);
// break;
// }
// }
//
// if (exists)
// {
// continue;
// }
//
// // String txt = n.getTextContent();
// Rectangle2D r = CSSUtils.getNodeDimension(n);
// if (r.getWidth()<5 || r.getHeight()<5 || r.getMinX()<0 || r.getMinY()<0)
// {
// continue;
// }
// else if (r.getWidth()>500 && r.getHeight()>800)
// {
// continue;
// }
//
// GenericSegment segment = null;
// if (n instanceof Element)
// {
// Element e = (Element) n;
// String tagName = e.getTagName();
// if ("div".equalsIgnoreCase(tagName))
// {
// continue;
// }
// }
//
// switch (n.getNodeType())
// {
//
// case Element.ATTRIBUTE_NODE:
//
// break;
//
// case Element.ELEMENT_NODE:
//
// Element e = (Element) n;
//
// //compute font
// String nodeText = DOMHelper.Text.getElementWithChildrenText(e);
// Font font = null;
// if (nodeText!=null && nodeText.length()>0)
// {
// font = CSSUtils.getFont(n);
// }
//
// if ("img".equalsIgnoreCase(e.getTagName()))
// {
// ImageSegment imgSegment = new ImageSegment(
// (float)r.getMinX(), (float)r.getMaxX(),
// (float)r.getMinY(), (float)r.getMaxY());
// segment = imgSegment;
// }
// else if ("a".equalsIgnoreCase(e.getTagName()))
// {
// if (nodeText==null || nodeText.length()==0 || font==null) {
// continue;
// }
// TextSegment txtSegment = new TextSegment(
// (float)r.getMinX(), (float)r.getMaxX(),
// (float)r.getMinY(), (float)r.getMaxY());
// txtSegment.setText(nodeText);
// txtSegment.setFontName(font.getName());
// txtSegment.setFontSize(font.getSize());
// segment = txtSegment;
// }
// else if ("input".equalsIgnoreCase(e.getTagName()))
// {
// if (nodeText==null || nodeText.length()==0 || font==null) {
// continue;
// }
// TextSegment txtSegment = new TextSegment(
// (float)r.getMinX(), (float)r.getMaxX(),
// (float)r.getMinY(), (float)r.getMaxY());
// txtSegment.setText(nodeText);
// txtSegment.setFontName(font.getName());
// txtSegment.setFontSize(font.getSize());
// segment = txtSegment;
// }
// else
// {
// if (nodeText==null || nodeText.length()==0 || font==null) {
// continue;
// }
// TextSegment txtSegment = new TextSegment(
// (float)r.getMinX(), (float)r.getMaxX(),
// (float)r.getMinY(), (float)r.getMaxY());
// txtSegment.setText(nodeText);
// txtSegment.setFontName(font.getName());
// txtSegment.setFontSize(font.getSize());
// segment = txtSegment;
// }
// break;
//
// case Element.TEXT_NODE:
//
// //compute font
// nodeText = n.getTextContent();
// font = null;
// if (nodeText!=null && nodeText.length()>0)
// {
// font = CSSUtils.getFont(n);
// }
//
// if (nodeText==null || nodeText.length()==0 || font==null) {
// continue;
// }
// TextSegment txtSegment = new TextSegment(
// (float)r.getMinX(), (float)r.getMaxX(),
// (float)r.getMinY(), (float)r.getMaxY());
// txtSegment.setText(nodeText);
// txtSegment.setFontName(font.getName());
// txtSegment.setFontSize(font.getSize());
// segment = txtSegment;
// break;
//
// case Element.COMMENT_NODE:
// break;
//
// default:
//
// // if (nodeText==null || nodeText.length()==0) {
// // continue;
// // }
// // txtSegment = new TextSegment(
// // (float)r.getMinX(), (float)r.getMaxX(),
// // (float)r.getMinY(), (float)r.getMaxY());
// // txtSegment.setText(nodeText);
// // txtSegment.setFontName(font.getName());
// // txtSegment.setFontSize(font.getSize());
// // segment = txtSegment;
// break;
// }
//
// // create the segment
// if (segment!=null) {
// segments.add(segment);
// }
// }
// return segments;
// }
//
// }

/**
 * 
 * @param file
 * @return
 */
public static SegLevelGraph loadDocumentHierarchyFromGraphML(String file) {
	SegLevelGraph result = new SegLevelGraph();

	//		try {
	//			XMLDocument xmlDoc = new XMLDocument();
	//			Document doc = xmlDoc.parse2document(new URI(file));
	//			 doc = doc.parse();
	// DomUtils.printDOMtags(doc.getDocumentElement(), "   ");
	System.out.println();

	//		} catch (URISyntaxException e) {
	//			e.printStackTrace();
	//		}

	return result;
}

/**
 * Test driver.
 * 
 * @param args
 */
public static void main(String[] args) {
	String file = "/home/max/cvModel.graphml";
	DocumentGraphFactory.loadDocumentHierarchyFromGraphML(file);
}

}// DocumentGraphFactory
