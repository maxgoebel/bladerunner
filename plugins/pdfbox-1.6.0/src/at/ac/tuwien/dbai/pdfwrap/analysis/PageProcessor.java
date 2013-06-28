/**
 * Copyright (c) 2003-2005, www.pdfbox.org
 * All rights reserved.
 *S
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
// altered by TH
// package org.pdfbox.util;
package at.ac.tuwien.dbai.pdfwrap.analysis;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dbai.pdfwrap.gui.EdgeSegment;
import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFPage;
import at.ac.tuwien.dbai.pdfwrap.utils.ListUtils;
import at.ac.tuwien.dbai.pdfwrap.utils.Utils;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.ImageSegment;
import at.tuwien.prip.model.document.segments.LineSegment;
import at.tuwien.prip.model.document.segments.Page;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.document.segments.TextBlock;
import at.tuwien.prip.model.document.segments.TextLine;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.segments.fragments.LineFragment;
import at.tuwien.prip.model.document.segments.fragments.TextFragment;
import at.tuwien.prip.model.graph.AdjacencyEdge;
import at.tuwien.prip.model.graph.AdjacencyGraph;
import at.tuwien.prip.model.graph.AdjacencyMatrix;
import at.tuwien.prip.model.graph.comparators.YComparator;

/**
 * General class to take a PDFPage and return a processed Page object,
 * according to the given processType
 *import at.tuwien.prip.core.model.document.segments.GenericSegment;
import at.tuwien.prip.core.model.graph.AdjacencyEdge;

 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class PageProcessor // extends PDFStreamEngine
{
	// types of clustering -- while clustering is still being perfected...
	//	public final static int PP_MONOSPACE = 0;	//seems to be catered for by PP_BMW
	public final static int PP_INSTRUCTION = 1;
	public final static int PP_FRAGMENT = 2;
	public final static int PP_CHAR = 3;
	public final static int PP_LINE = 4;
	public final static int PP_BLOCK = 5;

	public final static int PP_MERGED_LINES = 16;

	//	public final static int PP_DEFAULT = 100;

	//	public static Page STR_CURR_PAGE = new Page();

	// added so that the table understander could be called via a separate method
	protected Page retVal;
	protected List<CharSegment> charList; // was: charList
	protected List<TextFragment> fragList;
	protected List<ImageSegment> imageList;
	protected List<LineSegment> lineList;
	protected List<RectSegment> rectList;
	// the chosen granularity for graph matching
	protected List<GenericSegment> processingResult;

	protected List<EdgeSegment> edgeSegmentList; // the edges that will be finally displayed
	protected List<TextLine> textLines;
	protected List<TextBlock> mergedLines;
	protected List<TextBlock> textBlocks;
	protected RulingObjectProcessor rop;

	protected AdjacencyGraph<GenericSegment> adjGraph;

	float currentX = 0.0f;
	float currentY = 0.0f;
	// end of addition

	// added by TH
	//	private Document resultDocument;
	// end of addition

	//    private static Logger log = Logger.getLogger(PDFTextStripper.class);

	/*
    private int currentPageNo = 0;
    private int startPage = 1;
    private int endPage = Integer.MAX_VALUE;
    private PDOutlineItem startBookmark = null;
    private int startBookmarkPageNumber = -1;
    private PDOutlineItem endBookmark = null;
    private int endBookmarkPageNumber = -1;
    private boolean suppressDuplicateOverlappingText = true;
    private PDDocument document;
    private boolean shouldSeparateByBeads = true;

    private List pageArticles = null;
	 */
	/**
	 * The charactersByArticle is used to extract text by article divisions.  For example
	 * a PDF that has two columns like a newspaper, we want to extract the first column and
	 * then the second column.  In this example the PDF would have 2 beads(or articles), one for
	 * each column.  The size of the charactersByArticle would be 5, because not all text on the 
	 * screen will fall into one of the articles.  The five divisions are shown below
	 * 
	 * Text before first article
	 * first article text
	 * text between first article and second article
	 * second article text
	 * text after second article
	 * 
	 * Most PDFs won't have any beads, so charactersByArticle will contain a single entry.
	 */

	// currently not used!
	//    private Vector charactersByArticle = new Vector();
	//    
	//    private Map characterListMapping = new HashMap();
	//    
	//    private String lineSeparator = System.getProperty("line.separator");
	//    private String pageSeparator = System.getProperty("line.separator");
	//    private String wordSeparator = " ";

	//    private DocumentGraph documentGraph;

	/**
	 * Instantiate a new PageProcessor object.
	 */
	public PageProcessor() // throws IOException -- I don't think there's any need for it now
	{
		// super( ResourceLoader.loadProperties( "Resources/PDFTextStripper.properties" ) );
	}

	public static List<Page> processDocPages(List<Page> thePages, int processType, boolean rulingLines, BufferedImage pageImage)
	{
		// methods to run AFTER all pages have been understood
		return thePages;
	}

	/**
	 * 
	 * @param thisPage
	 * @param processType
	 * @param rulingLines
	 * @param ignoreSpaces
	 * @param noIterations
	 * @return
	 */
	public Page processPageNew (
			PDFPage<GenericSegment> thisPage, 
			int processType, 
			boolean rulingLines, 
			boolean ignoreSpaces, 
			boolean computeEdges,
			int noIterations) 
	{
		long startProcess = System.currentTimeMillis();

		retVal = new Page();
		retVal.setBoundingBox(thisPage.getBoundingBox());
		retVal.setRotation(thisPage.getRotation());

		fragList = ListUtils.getTextFragments(thisPage.getItems());
		imageList = ListUtils.getImageSegments(thisPage.getItems());
		lineList = ListUtils.getLineSegments(thisPage.getItems());
		rectList = ListUtils.getRectSegments(thisPage.getItems());

		// the chosen granularity for graph matching
		processingResult = new ArrayList<GenericSegment>();

		edgeSegmentList = new ArrayList<EdgeSegment>(); // the edges that will be finally displayed
		textLines = new ArrayList<TextLine>();

		mergedLines = new ArrayList<TextBlock>();
		textBlocks = new ArrayList<TextBlock>();

		rop = new RulingObjectProcessor();

		//mcg: show images?
		processingResult.addAll(imageList);
		
		if (processType==PP_CHAR) 
		{
			charList = ListUtils.getCharacters(thisPage.getItems());
			for (CharSegment c : charList)
			{
				TextFragment tf = new TextFragment();
				tf.getItems().add(c);
				tf.setCalculatedFields(c);

				LineFragment lf = new LineFragment();
				lf.getItems().add(tf);
				lf.setCalculatedFields(tf);

				TextLine tl = new TextLine();
				tl.getItems().add(lf);
				tl.setCalculatedFields(lf);

				TextBlock tb = new TextBlock();
				tb.getItems().add(tl);
				tb.setCalculatedFields(tl);

				processingResult.add(tb);
			}
		} 
		else 
		{
			/* build text lines */
			if (ignoreSpaces)
			{
				textLines = LineProcessor.findLinesFromCharacters(charList, 0.70f, false, false); 
			}
			else 
			{
				textLines = LineProcessor.findLinesFromTextFragments(fragList, 0.80f, false, false);
			}

			/* construct text line adjacency graph */
			AdjacencyMatrix<TextLine> lineAM = new AdjacencyMatrix<TextLine>(textLines);
			AdjacencyGraph<TextLine> lineAG = lineAM.toAdjacencyGraph(computeEdges);

			if (processType == PP_LINE)
			{
				for (TextLine tl : textLines)
				{
					TextBlock tb = new TextBlock();
					tb.getItems().add(tl);
					tb.setCalculatedFields(tl);

					processingResult.add(tb);
				}
			}
			else if (processType == PP_MERGED_LINES || processType == PP_BLOCK)
			{
				int blockIterations = noIterations;
				if (processType != PP_BLOCK) blockIterations = 0;

				HashMap<GenericSegment, CandidateCluster> clustHash = 
					new HashMap<GenericSegment, CandidateCluster>();

				textBlocks = PageSegmenter.clusterLinesIntoTextBlocks(
						lineAG, 
						blockIterations, 
						clustHash);
			}
			
			/* add merged lines */
			if (processType==PP_MERGED_LINES) 
			{
				/* find atomic lines */
				for (TextBlock c : textBlocks)
				{
					CandidateCluster cc = new CandidateCluster();
					// generic problems -- cannot simply add list
					for (TextSegment t : c.getItems())
						cc.getItems().add(t);
					cc.findLinesWidth();
					for (CompositeSegment<? extends TextSegment> l : cc.getFoundLines())
					{
						TextBlock lineBlock = new TextBlock();
						lineBlock.setCalculatedFields(l);

						for (TextSegment i : l.getItems())
						{
							if (i.getClass() == TextLine.class)
							{
								lineBlock.getItems().add((TextLine)i);
							}
							if (i.getClass() == LineFragment.class)
							{
								TextLine tl = new TextLine();
								tl.getItems().add((LineFragment)i);
								tl.setCalculatedFields(i);

								lineBlock.getItems().add(tl);
							}
							else if (i.getClass() == TextFragment.class)
							{
								LineFragment lf = new LineFragment();
								lf.getItems().add((TextFragment)i);
								lf.setCalculatedFields(i);

								TextLine tl = new TextLine();
								tl.getItems().add(lf);
								tl.setCalculatedFields(lf);

								lineBlock.getItems().add(tl);
							}
							else if (i.getClass() == CharSegment.class)
							{
								TextFragment tf = new TextFragment();
								tf.getItems().add((CharSegment)i);
								tf.setCalculatedFields(i);

								LineFragment lf = new LineFragment();
								lf.getItems().add((TextFragment)tf);
								lf.setCalculatedFields(tf);

								TextLine tl = new TextLine();
								tl.getItems().add(lf);
								tl.setCalculatedFields(lf);

								lineBlock.getItems().add(tl);
							}
						}
						mergedLines.add(lineBlock);
					}
				}
				processingResult.addAll(mergedLines);
			}
			
			/* add text fragments */
			else if (processType==PP_FRAGMENT) 
			{
				for (TextFragment tf : fragList)
				{
					LineFragment lf = new LineFragment();
					lf.getItems().add(tf);
					lf.setCalculatedFields(tf);

					TextLine tl = new TextLine();
					tl.getItems().add(lf);
					tl.setCalculatedFields(lf);

					TextBlock tb = new TextBlock();
					tb.getItems().add(tl);
					tb.setCalculatedFields(tl);

					processingResult.add(tb);
				}	
			}
			
			/* add text blocks */
			else if (processType==PP_BLOCK)
			{
				processingResult.addAll(textBlocks);
			}
		}


		AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(processingResult);
		adjGraph = am.toAdjacencyGraph(computeEdges);

		// RULING OBJECT PROCESSING
		if(rulingLines)
		{
			rop.addRulingObjects(lineList);

			rop.addRulingObjects(rectList); // here they will be automatically processed
			// into their constituent lines
			rop.removeDuplicateLines();		// this removes duplicate lines and 'joins'
			// touching lines
			lineList = rop.getRulingLines();

			rectList = new ArrayList<RectSegment>(); // empty rectList

			// added 6.01.11 for str conversions
			//	        commented out 2011-01-26 for execution
			//	        RulingObjectProcessor.strDetectUnderlinedText(textLines, lineList);
		}

		retVal.getItems().addAll(processingResult);
		retVal.getItems().addAll(textLines);

		retVal.getItems().addAll(imageList);
		retVal.getItems().addAll(lineList);
		retVal.getItems().addAll(rectList);

		retVal.getItems().addAll(edgeSegmentList);

		if (Utils.DISPLAY_TIMINGS)
			ErrorDump.debug(this, "total pp time: " + (System.currentTimeMillis() - startProcess));

		Collections.sort(retVal.getItems(), new YComparator());//.reverseOrder(new YComparator()));

		return retVal;
	}

	/**
	 * This will process the contents of a page.
	 * modified by TH
	 *
	 * @param page The page to process.
	 * @param content The contents of the page.
	 *
	 * @throws IOException If there is an error processing the page.
	 */
	public Page processPage(
			PDFPage<GenericSegment> thisPage, 
			int processType, 
			boolean rulingLines, 
			boolean ignoreSpaces, 
			boolean computeEdges,
			int noIterations) 
	{
		long startProcess = System.currentTimeMillis();

		retVal = new Page();
		retVal.setBoundingBox(thisPage.getBoundingBox());
		retVal.setRotation(thisPage.getRotation());

		charList = ListUtils.getCharacters(thisPage.getItems()); // was: charList
		fragList = ListUtils.getTextFragments(thisPage.getItems());
		imageList = ListUtils.getImageSegments(thisPage.getItems());
		lineList = ListUtils.getLineSegments(thisPage.getItems());
		rectList = ListUtils.getRectSegments(thisPage.getItems());

		// the chosen granularity for graph matching
		processingResult = new ArrayList<GenericSegment>();

		edgeSegmentList = new ArrayList<EdgeSegment>(); // the edges that will be finally displayed
		textLines = new ArrayList<TextLine>();

		mergedLines = new ArrayList<TextBlock>();
		textBlocks = new ArrayList<TextBlock>();

		rop = new RulingObjectProcessor();

		if (Utils.DISPLAY_TIMINGS) {
			ErrorDump.debug(this, "time A: " + (System.currentTimeMillis() - startProcess));
		}
		//        if (processType == PP_STRUCT)
		//        	PageSegmenter.LINE_SPACING_TOLERANCE = 0.20f;

		//        else // lines or coarser granular levels
		if (processType != PP_CHAR && processType != PP_FRAGMENT)
		{
			if (Utils.DISPLAY_TIMINGS) 
			{
				ErrorDump.debug(this, "time E: " + (System.currentTimeMillis() - startProcess));
			}
			if (ignoreSpaces)
			{
				textLines = LineProcessor.findLinesFromCharacters(charList, 0.70f, false, false); 
				//pageImage != null); // that is charList in the calling method
			}
			else 
			{
				//	        	textLines = lxLineFinder.findLines(fragmentList, 0.20f, false);
				//	        	19.10.10
				//	        	changed for JoinVision!!! Eva_Gasser.pdf
				textLines = LineProcessor.findLinesFromTextFragments(fragList, 0.80f, false, false);
				//pageImage != null);
			}

			//	    	AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(processingResult);
			//        	adjGraph = am.toAdjacencyGraph();

			AdjacencyGraph<TextLine> lineAG = new AdjacencyGraph<TextLine>();
			lineAG.addList(textLines);
			ErrorDump.debug(this, "number of items pageFromLines: " + textLines.size());

			if (Utils.DISPLAY_TIMINGS)
				ErrorDump.debug(this, "Time for preprocessing: " + (System.currentTimeMillis() - startProcess));

			// Generate NG
			long before = System.currentTimeMillis();
			lineAG.generateEdgesSingle();
			if (Utils.DISPLAY_TIMINGS)
				ErrorDump.debug(this, "Time for AG generation: " + (System.currentTimeMillis() - before));

			before = System.currentTimeMillis();

			// RULING OBJECT PROCESSING
			if(rulingLines)
			{
				rop.addRulingObjects(lineList);

				rop.addRulingObjects(rectList); // here they will be automatically processed
				// into their constituent lines
				rop.removeDuplicateLines();		// this removes duplicate lines and 'joins'
				// touching lines
				lineList = rop.getRulingLines();

				rectList = new ArrayList<RectSegment>(); // empty rectList

				// added 6.01.11 for str conversions
				//		        commented out 2011-01-26 for execution
				//		        RulingObjectProcessor.strDetectUnderlinedText(textLines, lineList);
			}


			// BEST FIRST CLUSTERING (BLOCK FINDING)
			// return only the blocks -- second level clustering takes place separately

			// noIterations affects block-finding unless processType == PP_COLUMN
			int blockIterations = noIterations;
			if (processType != PP_BLOCK) blockIterations = 0;

			HashMap<GenericSegment, CandidateCluster> clustHash = 
				new HashMap<GenericSegment, CandidateCluster>();
			//        	textBlocks = PageSegmenter.orderedEdgeCluster
			//        	TODO: Why is the HashMap being passed?
			textBlocks = PageSegmenter.clusterLinesIntoTextBlocks
			(lineAG, blockIterations, clustHash);//, null);

			if (Utils.DISPLAY_TIMINGS)
				ErrorDump.debug(this, "Time for ordered edge cluster: " + (System.currentTimeMillis() - before));

			//        	ListUtils.printListWithSubItems(textBlocks);

			before = System.currentTimeMillis();
			// FIND ATOMIC LINES
			for (TextBlock c : textBlocks)
			{
				{
					CandidateCluster cc = new CandidateCluster();
					// generic problems -- cannot simply add list
					for (TextSegment t : c.getItems())
						cc.getItems().add(t);
					cc.findLinesWidth();
					for (CompositeSegment<? extends TextSegment> l : cc.getFoundLines())
					{
						TextBlock lineBlock = new TextBlock();
						lineBlock.setCalculatedFields(l);

						for (TextSegment i : l.getItems())
						{
							if (i.getClass() == TextLine.class)
							{
								lineBlock.getItems().add((TextLine)i);
							}
							if (i.getClass() == LineFragment.class)
							{
								TextLine tl = new TextLine();
								tl.getItems().add((LineFragment)i);
								tl.setCalculatedFields(i);

								lineBlock.getItems().add(tl);
							}
							else if (i.getClass() == TextFragment.class)
							{
								LineFragment lf = new LineFragment();
								lf.getItems().add((TextFragment)i);
								lf.setCalculatedFields(i);

								TextLine tl = new TextLine();
								tl.getItems().add(lf);
								tl.setCalculatedFields(lf);

								lineBlock.getItems().add(tl);
							}
							else if (i.getClass() == CharSegment.class)
							{
								TextFragment tf = new TextFragment();
								tf.getItems().add((CharSegment)i);
								tf.setCalculatedFields(i);

								LineFragment lf = new LineFragment();
								lf.getItems().add((TextFragment)tf);
								lf.setCalculatedFields(tf);

								TextLine tl = new TextLine();
								tl.getItems().add(lf);
								tl.setCalculatedFields(lf);

								lineBlock.getItems().add(tl);
							}
							else
							{
								//TODO:
								// TextBlock: add its items, not the block itself?

								//???
								//	        					throw new DocumentProcessingException
								//	        						("Invalid objects found in line");
							}
						}
						mergedLines.add(lineBlock);
					}
				}
			}

			customProcessing(processType);
		}

		//mcg: show images?
		processingResult.addAll(imageList);

		if (processType == PP_CHAR)
		{
			//        	This processing mode just for debugging view ...

			for (CharSegment c : charList)
			{
				TextFragment tf = new TextFragment();
				tf.getItems().add(c);
				tf.setCalculatedFields(c);

				LineFragment lf = new LineFragment();
				lf.getItems().add(tf);
				lf.setCalculatedFields(tf);

				TextLine tl = new TextLine();
				tl.getItems().add(lf);
				tl.setCalculatedFields(lf);

				TextBlock tb = new TextBlock();
				tb.getItems().add(tl);
				tb.setCalculatedFields(tl);

				processingResult.add(tb);
			}
		}
		else if (processType == PP_FRAGMENT) // characters
		{
			//        	This processing mode just for debugging view ...

			for (TextFragment tf : fragList)
			{
				LineFragment lf = new LineFragment();
				lf.getItems().add(tf);
				lf.setCalculatedFields(tf);

				TextLine tl = new TextLine();
				tl.getItems().add(lf);
				tl.setCalculatedFields(lf);

				TextBlock tb = new TextBlock();
				tb.getItems().add(tl);
				tb.setCalculatedFields(tl);

				processingResult.add(tb);
			}
		}
		else if (processType == PP_LINE)
		{
			//        	This processing mode also for debugging view; 
			//        	for wrapping generally PP_MERGED_LINES should be used ...
			for (TextLine tl : textLines)
			{
				TextBlock tb = new TextBlock();
				tb.getItems().add(tl);
				tb.setCalculatedFields(tl);

				processingResult.add(tb);
			}
		}
		else if (processType == PP_MERGED_LINES) 
		{
			processingResult.addAll(mergedLines);
		}
		else if (processType == PP_BLOCK)
		{
			processingResult.addAll(textBlocks);
		}

		if (processType == PP_LINE || processType == PP_MERGED_LINES ||
				processType == PP_BLOCK || processType == PP_CHAR)
		{
			AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(processingResult);
			adjGraph = am.toAdjacencyGraph(computeEdges);

			//        	adjGraph = new AdjacencyGraph<GenericSegment>();
			////        		processingResult.setFontNames();
			//	        adjGraph.addList(processingResult);
			////    	        ListFactory.create imageList?!?
			////    	        clusterNG.addList(imageList);
			//	        adjGraph.generateEdgesSingle();
			//	        //GraphMatcher.removeLongEdges(clusterNG, 25.0f);

			ErrorDump.debug(this, "PP.edges: " + adjGraph.getEdges().size());

			List<EdgeSegment> edgeList = new ArrayList<EdgeSegment>();
			for (AdjacencyEdge<GenericSegment> ae : adjGraph.getEdges())
				edgeList.add(new EdgeSegment(ae));
			//        	

			//	        edgeList = clusterNG.getEdges().toSegmentList();

			//	        2011-01-27 TEMPORARILY COMMENTED OUT
			//	        rop.labelEdges(clusterNG.getEdges(), rop.getRulingLines());
		}

		/*
        ErrorDump.debug(this, "processingResult:");
        ListUtils.printList(processingResult);
		 */

		// add the text clusters (segments) to the page object

		retVal.getItems().addAll(processingResult);
		retVal.getItems().addAll(textLines);

		retVal.getItems().addAll(imageList);
		retVal.getItems().addAll(lineList);
		retVal.getItems().addAll(rectList);

		retVal.getItems().addAll(edgeSegmentList);

		if (Utils.DISPLAY_TIMINGS)
			ErrorDump.debug(this, "total pp time: " + (System.currentTimeMillis() - startProcess));

		Collections.sort(retVal.getItems(), new YComparator());//.reverseOrder(new YComparator()));

		return retVal;
	}

	public void customProcessing(int processType)
	{
		// for custom processing, override this method!
	}

	public AdjacencyGraph<GenericSegment> getAdjGraph() {
		return adjGraph;
	}

	public void setAdjGraph(AdjacencyGraph<GenericSegment> adjGraph) {
		this.adjGraph = adjGraph;
	}

}
