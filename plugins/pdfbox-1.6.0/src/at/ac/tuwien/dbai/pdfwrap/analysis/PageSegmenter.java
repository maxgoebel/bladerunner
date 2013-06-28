package at.ac.tuwien.dbai.pdfwrap.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.ac.tuwien.dbai.pdfwrap.utils.ListUtils;
import at.ac.tuwien.dbai.pdfwrap.utils.SegmentUtils;
import at.ac.tuwien.dbai.pdfwrap.utils.Utils;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.TextBlock;
import at.tuwien.prip.model.document.segments.TextLine;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.graph.AdjacencyEdge;
import at.tuwien.prip.model.graph.AdjacencyGraph;
import at.tuwien.prip.model.graph.comparators.EdgeAttributeComparator;
import at.tuwien.prip.model.graph.comparators.YComparator;


/**
 * Page segmentation methods
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class PageSegmenter
{
	// passed variable at the mo... HashMap vertNeighbourMap;
	//    AdjacencyGraph ng;
	//    HashMap colHash;
	//EdgeList edges;
	// the following line commented out 6.08.08, due to first- and second-level
	// clusterers requiring their own local variables
	//EdgeList priorityEdges;
	//SegmentList waitingList;


	float maxH = 0.0f;

	public static float MAX_CLUST_LINE_SPACING = 1.75f; // 5524.pdf i-cite
	public static float MIN_CLUST_LINE_SPACING = 0.25f; // Baghdad problem! 30.07.08
	public static float MAX_COL_LINE_THRESHOLD = 3.5f;
	//    final static float LINE_SPACING_TOLERANCE = 0.25f;
	public static float LINE_SPACING_TOLERANCE = 0.05f; // changed 30.10.10
	// NOTE! This linespacing tolerance does not apply to OCR; 
	// 9.01.11 also does not apply to str conversions;
	// PageProcessor changes this value if a page image is used

	public PageSegmenter(
			AdjacencyGraph ng)
	{
		//		this.ng = ng;
		//edges = ng.getEdges();
		//maxH = 0.5f;
		// new value from 14.03.07
		//        maxH = 0.0f;
	}

	private boolean checkHashes(Collection cols, Collection values)
	{
		if (cols.size() == values.size())
		{
			Iterator itemIter = cols.iterator();
			while(itemIter.hasNext())
			{
				GenericSegment item = (GenericSegment)itemIter.next();
				if (!values.contains(item)) return true;
			}
			//ErrorDump.debug(this, "same size:");
			//ErrorDump.debug(this, "cols: " + cols.size());
			//ErrorDump.debug(this, "values: " + values.size());
			return false;
		}
		else 
		{
			//ErrorDump.debug(this, "different size:");
			//ErrorDump.debug(this, "cols: " + cols.size());
			//ErrorDump.debug(this, "values: " + values.size());

			return true;
		}
	}

	protected static boolean inSwallowGroup(CandidateCluster c, 
			List<GenericSegment> swallowedItems, List<AdjacencyEdge<GenericSegment>> unusedEdges)
	{
		List<GenericSegment> swallowedSegments = new ArrayList<GenericSegment>();
		for (GenericSegment o : swallowedItems)
		{
			if (!c.getItems().contains(o))
				swallowedSegments.add((TextSegment)o);
		}

		// now we need to make sure that each new segment
		// can (will) be added to c anyway

		for (GenericSegment gs : swallowedItems)
		{
			TextSegment s = (TextSegment)gs;
			// we need to see whether unusedEdges contains
			// an edge between any item in c and s

			//			EdgeList subList = (EdgeList)unusedEdges.getEdges(s);
			// s should not be a member of c.getItems() (can't see how that could happen...)
			boolean foundMemberOfCGetItems = false; 
			//			Iterator j = subList.iterator();
			//			while(j.hasNext())
			for (AdjacencyEdge<GenericSegment> e : unusedEdges)
			{
				//				AdjacencyEdge e = (AdjacencyEdge)j.next();

				if (e.getNodeFrom() == s || e.getNodeTo() == s)
				{	
					if (c.getItems().contains(e.getNodeFrom())) foundMemberOfCGetItems = true;
					if (c.getItems().contains(e.getNodeTo())) foundMemberOfCGetItems = true;
				}
			}
			if (foundMemberOfCGetItems == false) return false;
		}

		return true;
	}

	/*
    protected static boolean clusterTogether(AdjacencyEdge<GenericSegment> ae, 
    		CandidateCluster clustFrom, CandidateCluster clustTo, 
    		List<AdjacencyEdge<GenericSegment>> allEdges, HashMap vertNeighbourMap, 
    		List<? extends GenericSegment> items, int processPhase)
    {
    	if (processPhase == 2)
    		return clusterTogether2(ae, clustFrom, clustTo, allEdges, items);
    	else
    		return clusterTogether1(ae, clustFrom, clustTo, allEdges, vertNeighbourMap);
    }
	 */

	// TODO: only vertical edges actually need to be passed here...?
	protected static boolean clusterTogether(AdjacencyEdge<GenericSegment> ae, 
			CandidateCluster clustFrom, CandidateCluster clustTo, 
			List<AdjacencyEdge<GenericSegment>> allEdges, 
			HashMap<GenericSegment,List<GenericSegment>> vertNeighbourMap, 
			List<?> items) 
	// the final parameter is ignored (2nd level clustering only)
	{	
		TextSegment segFrom = (TextSegment)ae.getNodeFrom();
		TextSegment segTo = (TextSegment)ae.getNodeTo();

		//		30.10.10 -- Cluster.lineSpacing is not a multiple, but rather the absolute linespacing
		//		float lineSpacing = ae.getEdgeLength();

		// caution: do not confuse segFrom with clustFrom :)

		// don't cluster the same segment together(!)
		// (should not happen anyway...)
		if (segFrom == segTo) return false;

		if (ae.isHorizontal())
		{
			if (clustFrom == null)
			{
				clustFrom = new CandidateCluster();
				clustFrom.getItems().add(segFrom);
				clustFrom.findLinesWidth();
				clustFrom.findBoundingBox(); // precondition for findNVN
			}

			if (clustTo == null)
			{
				clustTo = new CandidateCluster();
				clustTo.getItems().add(segFrom);
				clustTo.findLinesWidth();
				clustFrom.findBoundingBox(); // precondition for findNVN
			}

			// don't cluster the same cluster together(!)
			if (clustFrom == clustTo) return false;

			long t = System.currentTimeMillis();

			// changed on 30.04.09 to use segments rather than clusters
			List<GenericSegment> neighboursFrom = findNearestVerticalNeighbours(segFrom, allEdges, vertNeighbourMap);
			List<GenericSegment> neighboursTo = findNearestVerticalNeighbours(segTo, allEdges, vertNeighbourMap);

			TextSegment closestNeighbourFrom = null;
			if (neighboursFrom.get(0) != null && neighboursFrom.get(1) != null)
			{
				float distanceAbove = 
					((TextSegment)neighboursFrom.get(0)).getY1() - segFrom.getY2();
				float distanceBelow = 
					segFrom.getY1() - ((TextSegment)neighboursFrom.get(1)).getY2();

				if (distanceAbove < distanceBelow)
					closestNeighbourFrom = (TextSegment)neighboursFrom.get(0);
				else
					closestNeighbourFrom = (TextSegment)neighboursFrom.get(1);
			}
			else if (neighboursFrom.get(0) != null)
			{
				closestNeighbourFrom = (TextSegment)neighboursFrom.get(0);
			}
			else if (neighboursFrom.get(1) != null)
			{
				closestNeighbourFrom = (TextSegment)neighboursFrom.get(1);
			}

			TextSegment closestNeighbourTo = null;
			if (neighboursTo.get(0) != null && neighboursTo.get(1) != null)
			{
				float distanceAbove = 
					((TextSegment)neighboursTo.get(0)).getY1() - segTo.getY2();
				float distanceBelow = 
					segTo.getY1() - ((TextSegment)neighboursTo.get(1)).getY2();

				if (distanceAbove < distanceBelow)
					closestNeighbourTo = (TextSegment)neighboursTo.get(0);
				else
					closestNeighbourTo = (TextSegment)neighboursTo.get(1);
			}
			else if (neighboursTo.get(0) != null)
			{
				closestNeighbourTo = (TextSegment)neighboursTo.get(0);
			}
			else if (neighboursTo.get(1) != null)
			{
				closestNeighbourTo = (TextSegment)neighboursTo.get(1);
			}

			TextSegment closestNeighbour = null;
			float neighbourDistance = -1;
			if (closestNeighbourFrom != null && closestNeighbourTo != null)
			{
				float distanceFrom;
				if (closestNeighbourFrom.getYmid() < segFrom.getYmid())
					distanceFrom = segFrom.getY1() - closestNeighbourFrom.getY2();
				else
					distanceFrom = closestNeighbourFrom.getY1() - segFrom.getY2();

				float distanceTo;
				if (closestNeighbourTo.getYmid() < segTo.getYmid())
					distanceTo = segTo.getY1() - closestNeighbourTo.getY2();
				else
					distanceTo = closestNeighbourTo.getY1() - segTo.getY2();

				if (distanceFrom < distanceTo)
				{
					closestNeighbour = closestNeighbourFrom;
					neighbourDistance = distanceFrom;
				}
				else
				{
					closestNeighbour = closestNeighbourTo;
					neighbourDistance = distanceTo;
				}
			}
			else if (closestNeighbourFrom != null)
			{
				closestNeighbour = closestNeighbourFrom;
				float distanceFrom;
				if (closestNeighbourFrom.getYmid() < segFrom.getYmid())
					distanceFrom = segFrom.getY1() - closestNeighbourFrom.getY2();
				else
					distanceFrom = closestNeighbourFrom.getY1() - segFrom.getY2();
				neighbourDistance = distanceFrom;
			}
			else if (closestNeighbourTo != null)
			{
				closestNeighbour = closestNeighbourTo;
				float distanceTo;
				if (closestNeighbourTo.getYmid() < segTo.getYmid())
					distanceTo = segTo.getY1() - closestNeighbourTo.getY2();
				else
					distanceTo = closestNeighbourTo.getY1() - segTo.getY2();
				neighbourDistance = distanceTo;
			}

			// TODO: neighbourDistance is not used at all!

			float max_horiz_edge_width = 0.75f;

			if (!(clustFrom.getFoundLines().size() <= 2 
					|| clustTo.getFoundLines().size() <= 2))
				max_horiz_edge_width = 0.85f;

			if (!(clustFrom.getFoundLines().size() <= 1 
					|| clustTo.getFoundLines().size() <= 1))
				max_horiz_edge_width = 1.0f;

			// if baseline of both segs doesn't match, reduce to 0.3
			// addition of 30.04.09
			boolean sameBaseline = 
				Utils.within(segFrom.getY1(), segTo.getY1(), 
						Utils.calculateThreshold(segFrom, segTo, 0.20f));

			if (!sameBaseline)
				max_horiz_edge_width = 0.3f;

			//float d = neighbourDistance / ae.getFontSize();

			// 29.04.09: we recalculate (at least for horiz. edges)
			// the lineSpacing (i.e. relative edge length)
			// using the smallest of both fontsize values...

			float smallestFontSize = 
				((TextSegment)ae.getNodeFrom()).getFontSize();
			if (((TextSegment)ae.getNodeFrom()).getFontSize() >
			((TextSegment)ae.getNodeTo()).getFontSize())
				smallestFontSize = ((TextSegment)ae.getNodeTo()).getFontSize();

			float horizGap = ae.getPhysicalLength() / smallestFontSize;

			if (horizGap > max_horiz_edge_width) return false;

			return true;
		}

		float lineSpacing;

		if (ae.getDirection() == AdjacencyEdge.REL_ABOVE)
			lineSpacing = ae.getNodeTo().getY1() - ae.getNodeFrom().getY1();
		else // REL_BELOW
			lineSpacing = ae.getNodeFrom().getY1() - ae.getNodeTo().getY1();

		lineSpacing = lineSpacing/ae.getFontSize();

		//		ErrorDump.debug(this, "eins");
		if (!(Utils.sameFontSize(segFrom, segTo)))
			return false;
		//		ErrorDump.debug(this, "lineSpacing: " + lineSpacing);
		//		ErrorDump.debug(this, "zwei");
		if (!(lineSpacing <= MAX_CLUST_LINE_SPACING && lineSpacing >= MIN_CLUST_LINE_SPACING))
			return false;
		//		ErrorDump.debug(this, "drei");
		if (clustFrom == null && clustTo == null)
		{
			//			ErrorDump.debug(this, "drei punkt eins");
			return true;
		}
		else if (clustFrom == null)
		{
			//			ErrorDump.debug(this, "drei punkt zwei");
			// check if line spacing matches that of cluster, or has not yet been
			// assigned
			if (clustTo.getLineSpacing() == 0.0f || 
					Utils.within(lineSpacing, clustTo.getLineSpacing(), LINE_SPACING_TOLERANCE))
				return true;
		}
		else if (clustTo == null)
		{
			//			ErrorDump.debug(this, "drei punkt drei");
			// check if line spacing matches that of cluster, or has not yet been
			// assigned
			if (clustFrom.getLineSpacing() == 0.0f || 
					Utils.within(lineSpacing, clustFrom.getLineSpacing(), LINE_SPACING_TOLERANCE))
				return true;
		}
		else
		{
			//			ErrorDump.debug(this, "drei punkt vier");
			// don't cluster the same segments together!
			if (clustFrom == clustTo) return false;
			// check that the line spacings are the same and ?within the threshold?
			boolean sameLineSpacing = 
				(Utils.within(clustFrom.getLineSpacing(), clustTo.getLineSpacing(), 
						LINE_SPACING_TOLERANCE));

			//			ErrorDump.debug(this, "drei punkt fünf");
			//			ErrorDump.debug(this, "ls: " + clustFrom.getLineSpacing() + " clustFrom: " + clustFrom);
			//			ErrorDump.debug(this, "ls: " + clustTo.getLineSpacing() + " clustTo: " + clustFrom);
			//			ErrorDump.debug(this, "LINE_SPACING_TOLERANCE = " + LINE_SPACING_TOLERANCE);
			// highly unlikely that it will succeed with sameLineSpacing but 
			// fail here but just in case...
			return (sameLineSpacing && 
					Utils.within(lineSpacing, clustFrom.getLineSpacing(), LINE_SPACING_TOLERANCE) &&
					Utils.within(lineSpacing, clustTo.getLineSpacing(), LINE_SPACING_TOLERANCE));
		}
		//		ErrorDump.debug(this, "vier");
		return false;
	}

	public static List<TextBlock> clusterLinesIntoTextBlocks(AdjacencyGraph<? extends GenericSegment> lineAG,
			int maxIterations, HashMap<GenericSegment, CandidateCluster> clustHash)
			{
		List<TextBlock> retVal = new ArrayList<TextBlock>();
		List<CandidateCluster> l = orderedEdgeCluster(lineAG, maxIterations, clustHash);

		for (CandidateCluster c : l)
		{
			TextBlock tb = new TextBlock(
					c.getX1(), c.getX2(), c.getY1(), c.getY2(),
					c.getText(), c.getFontName(), c.getFontSize(), null);
			for (TextSegment ts : c.getItems())
			{
				tb.getItems().add((TextLine)ts);
			}
			retVal.add(tb);
		}
		return retVal;
			}

	// clustHash is returned to calling method so that columns can be built efficiently later
	public static List<CandidateCluster> orderedEdgeCluster (
			AdjacencyGraph<? extends GenericSegment> lineAG,
			int maxIterations,
			HashMap<GenericSegment, CandidateCluster> clustHash)//, HashMap colHash)
		{
		
		//		ErrorDump.debug(this, "in orderedEdgeCluster with tolerance: " + LINE_SPACING_TOLERANCE);

		if (maxIterations <= 0) maxIterations = Integer.MAX_VALUE;
		else ErrorDump.debug(PageSegmenter.class, "running with " + maxIterations + " iterations");

		long startProcess = System.currentTimeMillis();
		long t = System.currentTimeMillis();

		//	SegmentList unusedSegments = (SegmentList)pageFromLines.getItems().clone();
		List<CandidateCluster> retVal = 
			new ArrayList<CandidateCluster>();

		List<GenericSegment> unusedSegments = 
			new ArrayList<GenericSegment>();
		List<GenericSegment> allSegments = 
			new ArrayList<GenericSegment>();

		for (GenericSegment s : lineAG.getVertList())
		{
			allSegments.add(s);
			unusedSegments.add(s);
		}

		// now, with vertical edges, seems to give different results without clone :)
		//    	EdgeList priorityEdges = (EdgeList)allEdges.clone(); // don't need to clone? maybe do?
		//    	2011-01-26: why can't you do this?
		//    	List<AdjacencyEdge<? extends GenericSegment>> priorityEdges = lineAG.getEdges();//.clone();
		List<AdjacencyEdge<GenericSegment>> priorityEdges = 
			new Vector<AdjacencyEdge<GenericSegment>>();
		List<AdjacencyEdge<GenericSegment>> allEdges = 
			new Vector<AdjacencyEdge<GenericSegment>>();
		for (AdjacencyEdge<?> e : lineAG.getEdges())
		{
			AdjacencyEdge<GenericSegment> aegs =
				new AdjacencyEdge<GenericSegment>
			(e.getNodeFrom(), e.getNodeTo(),
					e.getDirection(), e.getWeight());

			priorityEdges.add(aegs);
			allEdges.add(aegs);
		}

		Collections.sort(priorityEdges, new EdgeAttributeComparator());
		//priorityEdges.removeDuplicateEdges();

		HashMap<GenericSegment,List<GenericSegment>> vertNeighbourMap = 
			new HashMap<GenericSegment,List<GenericSegment>>();

		ErrorDump.debug(PageSegmenter.class, "priorityEdges.size: " + priorityEdges.size());
		ErrorDump.debug(PageSegmenter.class, "lineAGEdges.size: " + lineAG.getEdges().size());

		int iteration = 0;

		t = System.currentTimeMillis();

		while(priorityEdges.size() > 0 && iteration < maxIterations)
		{
			long start = System.currentTimeMillis();

			AdjacencyEdge<GenericSegment> ae = priorityEdges.remove(0);

			if (ae.getNodeFrom() instanceof TextSegment &&
					ae.getNodeTo() instanceof TextSegment)
			{
				TextSegment segFrom = (TextSegment)ae.getNodeFrom();
				TextSegment segTo = (TextSegment)ae.getNodeTo();
				float lineSpacing = ae.getPhysicalLength() / ae.getFontSize();

				iteration ++;
				if (iteration % 1000 == 0)
					ErrorDump.debug(PageSegmenter.class, "Iteration: " + iteration + " of " + allEdges.size());

				//	    		ErrorDump.debug(this, "Examining edge: " + ae + " with length: " + ae.getPhysicalLength());

				if (clustHash.get(segFrom) == null && clustHash.get(segTo) == null)
				{
					//	    			ErrorDump.debug(this, "one");
					if (clusterTogether(ae, null, null, allEdges, vertNeighbourMap, allSegments))
					{
						//	    				ErrorDump.debug(this, "two");
						List<GenericSegment> swallowedSegments = 
							swallow(createList(segFrom), createList(segTo), allSegments, clustHash);

						if (ae.isVertical() || ae.isHorizontal() && swallowedSegments.size() <= 2)
						{
							//							ErrorDump.debug(this, "three");
							CandidateCluster newc = makeCluster(swallowedSegments);

							if (isValidCluster(newc))    					
							{
								//								ErrorDump.debug(this, "four");
								t = System.currentTimeMillis();
								updateHashes(newc, allSegments, clustHash, retVal, priorityEdges, allEdges, ae.isVertical(), vertNeighbourMap);
								t = System.currentTimeMillis();

								newc.findBoundingBox();
								newc.setFontSize(ae.getFontSize());
								//newc.setLineSpacing(lineSpacing); // removed 29.10.10
								newc.setCalculatedFields(); // 29.10.10
							}
						}
					}
					// else do nothing
				}
				else if (clustHash.get(segFrom) == null)
				{
					//	    			ErrorDump.debug(this, "five");
					CandidateCluster c = clustHash.get(segTo);
					if (clusterTogether(ae, null, c, allEdges, vertNeighbourMap, allSegments))
					{
						//	    				ErrorDump.debug(this, "six");
						List<GenericSegment> swallowedSegments = 
							swallow(createList(c.getItems()), createList(segFrom), allSegments, clustHash);

						if (ae.isVertical() || ae.isHorizontal() && inSwallowGroup(c, swallowedSegments, priorityEdges))
						{
							//							ErrorDump.debug(this, "seven");
							CandidateCluster newc = makeCluster(swallowedSegments);
							if (isValidCluster(newc))    					
							{
								//	    						ErrorDump.debug(this, "eight");
								updateHashes(newc, allSegments, clustHash, retVal, priorityEdges, allEdges, ae.isVertical(), vertNeighbourMap);
								t = System.currentTimeMillis();
								retVal.remove(c);
								newc.findBoundingBox();
								newc.setFontSize(ae.getFontSize());
								//newc.setLineSpacing(lineSpacing); // removed 29.10.10
								newc.setCalculatedFields(); // 29.10.10
								t = System.currentTimeMillis();
							}
						}
					}
				}
				else if (clustHash.get(segTo) == null)
				{
					//	    			ErrorDump.debug(this, "nine");
					CandidateCluster c = clustHash.get(segFrom);
					if (clusterTogether(ae, c, null, allEdges, vertNeighbourMap, allSegments))
					{
						//	    				ErrorDump.debug(this, "ten");
						List<GenericSegment> swallowedSegments =
							swallow(createList(c.getItems()), createList(segTo), allSegments, clustHash);

						// check if the addition doesn't swallow any additional elements
						if (ae.isVertical() || ae.isHorizontal() && inSwallowGroup(c, swallowedSegments, priorityEdges))
						{
							//							ErrorDump.debug(this, "eleven");
							CandidateCluster newc = makeCluster(swallowedSegments);
							if (isValidCluster(newc))    					
							{
								//	    						ErrorDump.debug(this, "twelve");
								updateHashes(newc, allSegments, clustHash, retVal, priorityEdges, allEdges, ae.isVertical(), vertNeighbourMap);
								retVal.remove(c);

								newc.findBoundingBox();
								newc.setFontSize(ae.getFontSize());
								//newc.setLineSpacing(lineSpacing); // removed 29.10.10
								newc.setCalculatedFields(); // 29.10.10
								t = System.currentTimeMillis();
							}
						}
					}
				}
				else // both segments already used, merge
				{
					//	    			ErrorDump.debug(this, "thirteen");
					// only possibility for horizontal edge, as all other segments added
					// as singletons by now

					t = System.currentTimeMillis();
					// merge the two clusters if compatible
					CandidateCluster c1 = clustHash.get(segFrom);
					CandidateCluster c2 = clustHash.get(segTo);

					boolean skip = false;
					/*
	    			if (ae.isHorizontal())
	        		{
	            		if (c1 != c2) // taken care of anyway in clusterTogether
	            		{
	        	    		TableColumn colFrom = (TableColumn)colHash.get(c1);
	        	    		TableColumn colTo = (TableColumn)colHash.get(c2);

	        	    		if (colFrom != colTo)
	        	    		{
	        		    		boolean substantial = false;
	        		    		c1.findText(false);
	        		    		c2.findText(false);
	        		    		if (colFrom.getFoundLines().size() >= 3 &&
	        			    		colTo.getFoundLines().size() >= 3) substantial = true;

	    			    		if (substantial)
	    			    		{
	    			    			skip = true;
	    			    		}
	        	    		}
	            		}
	        		}
					 */
					// commented out JoinVision 17.07.10

					// correction JoinVision 21.07.10
					if (ae.isHorizontal()) skip = true;

					if (!skip)
					{
						//	    				ErrorDump.debug(this, "thirteenandahalf");
						if (clusterTogether(ae, c1, c2, allEdges, vertNeighbourMap, allSegments))
						{
							//		    				ErrorDump.debug(this, "fourteen");
							// check if the addition doesn't swallow any additional elements
							List<GenericSegment> swallowedSegments =
								swallow(createList(c1.getItems()), createList(c2.getItems()), allSegments, clustHash);
							if (ae.isVertical() || ae.isHorizontal() && swallowedSegments.size() <= c1.getItems().size() + c2.getItems().size())
								//if (ae.isVertical() || ae.isHorizontal() && inSwallowGroup(c1, swallowedSegments, priorityEdges))
							{
								//								ErrorDump.debug(this, "fifteen");
								CandidateCluster newc = makeCluster(swallowedSegments);
								if (isValidCluster(newc))    					
								{
									//									ErrorDump.debug(this, "sixteen");
									updateHashes(newc, allSegments, clustHash, retVal, priorityEdges, allEdges, ae.isVertical(), vertNeighbourMap);
									newc.findBoundingBox();
									newc.setFontSize(ae.getFontSize());
									//newc.setLineSpacing(lineSpacing); // removed 29.10.10
									newc.setCalculatedFields(); // 29.10.10
									retVal.remove(c2);
									retVal.remove(c1);

									if (ae.isHorizontal())
									{
										//			    						2011-01-26 TEMPORARILY COMMENTED OUT

										/*
	//		    						ErrorDump.debug(this, "seventeen");
			    						TableColumn colFrom = (TableColumn)colHash.get(c1);
			            	    		TableColumn colTo = (TableColumn)colHash.get(c2);

			            	    		//if (colFrom != colTo) // otherwise result in duplicate items!
			            	    		//{
				            	    		ListUtils newColSegments = new ListUtils();
				            	    		newColSegments.addAll(colFrom.getItems());
				            	    		if (colFrom != colTo) // to stop duplicates
				            	    			newColSegments.addAll(colTo.getItems());

				            	    		newColSegments.remove(c1);
				            	    		newColSegments.remove(c2);
				            	    		newColSegments.add(newc);

				    						TableColumn newCol = makeColumn(newColSegments);
				    						updateHashes(newCol, null, colHash, retVal, null, null, false, null);

				    						retVal.remove(colFrom);
				    						retVal.remove(colTo);
			            	    		//}
			            	    		//else
			            	    		//{
			            	    		//	colFrom.getItems().add(newc);
			            	    		//	updateHashes(colFrom, null, colHash, retVal, null, null, false, null);
			            	    		//}
										 */
									}
								}
							}
						}
					}
					else
					{
						// part of same cluster; not merging :)
					}
				}
			}
		}

		if (priorityEdges.size() == 0) // don't add singletons if in 'watch' mode :)
		{
			//    		ErrorDump.debug(this, "remaining singletons: " + priorityEdges.size());
			// add remaining singletons
			for (GenericSegment s : allSegments)
			{
				if (clustHash.get(s) == null)
				{
					CandidateCluster c = makeCluster(createList(s));
					//newc.setLineSpacing(lineSpacing); // removed 29.10.10
					c.setCalculatedFields();
					clustHash.put(s, c);
					retVal.add(c); //only for testing when bailing out of method here...
				}
			}
		}

		if (Utils.DISPLAY_TIMINGS)
			ErrorDump.debug(PageSegmenter.class, "Total time for clustering: " + (System.currentTimeMillis() - startProcess));

		/*
    	ErrorDump.debug(this, "retVal");
    	ListUtils.printListWithSubItems(retVal);
		 */

		return retVal;
			}

	protected static void updateHashes(CandidateCluster c, List<GenericSegment> items, 
			HashMap clustHash, List<CandidateCluster> callingRetVal, 
			List<AdjacencyEdge<GenericSegment>> priorityEdges, List<AdjacencyEdge<GenericSegment>> allEdges, 
			boolean performNeighbourFinding, HashMap vertNeighbourMap)
	{
		// c instanceof Cluster

		//   	ErrorDump.debug(this, "in updateHashes with c: " + c.toExtendedString());

		long t = System.currentTimeMillis();

		// go through all swallowedItems
		// if not part of c.items & if not newSegment
		// if unused, add to c
		// if used, look them up in colHash & merge col with c

		//ErrorDump.debug(this, "newSegment: " + newSegment);
		//ErrorDump.debug(this, "adding to cluster c: " + c.toExtendedString());

		//Cluster c = new Cluster();

		Iterator siIter = c.getItems().iterator();
		while(siIter.hasNext())
		{
			TextSegment ts = (TextSegment)siIter.next();

			if (clustHash.get(ts) != null) // item belongs to another cluster
			{

				CandidateCluster clust = (CandidateCluster)clustHash.get(ts);
				//c.getItems().addAll(clust.getItems());
				Iterator itemIter = clust.getItems().iterator();
				while(itemIter.hasNext())
				{
					TextSegment item = (TextSegment)itemIter.next();
					clustHash.remove(item);
					// all of the items within the cluster should already be
					// in the list of swallowedItems...
					// clustHash.put(item, c);
					callingRetVal.remove(clust);   
				}
			}

			clustHash.put(ts, c);

		}
		callingRetVal.add(c);

		t = System.currentTimeMillis();

		if (performNeighbourFinding)
		{
			// if skipNeighbourFinding:
			// c is a new cluster with 2 sub-elements
			// as the situation is uncomplicated (swallow is not allowed,
			// else no cluster generation), we do not need to move edges around.  At all.

			// find lowest neighbourAbove and highest neighbourBelow
			TextSegment lowestNeighbourAbove = null;
			TextSegment highestNeighbourBelow = null;



			// if size one, no need even to do this

			List<GenericSegment> foo = findNearestVerticalNeighbours
			(c, allEdges, vertNeighbourMap);

			//			ErrorDump.debug(this, "STh findNVN " + (System.currentTimeMillis() - t));
			t = System.currentTimeMillis();

			lowestNeighbourAbove = (TextSegment)foo.get(0);
			highestNeighbourBelow = (TextSegment)foo.get(1);

			//			ErrorDump.debug(this, "lowestNeighbourAbove: " + lowestNeighbourAbove);
			//			ErrorDump.debug(this, "highestNeighbourBelow: " + highestNeighbourBelow);

			// if neighbours are the same, no need to look further

			// now we have found the lowest neighbourAbove and
			// highest neighbourBelow :)

			// convert foundLines to new items -- no, don't, it's too complicated
			// particularly with regards to left and right...

			// find the top item and bottom item, which matches the font size of its found line
			// pre: lines have been found already AND sort has taken place
			Collections.sort(c.getItems(), new YComparator());

			//			ErrorDump.debug(this, "STh sort " + (System.currentTimeMillis() - t));
			t = System.currentTimeMillis();

			// these could conceivably be NULL, which would cause the algorithm to CRASH!
			//TextSegment topItem = (TextSegment) c.getItems().getFirst();
			TextSegment topItem = (TextSegment) c.getTopElementMatchingFontsizeAfterSorting();
			//TextSegment bottomItem = (TextSegment) c.getItems().getLast();
			TextSegment bottomItem = (TextSegment) c.getBottomElementMatchingFontsizeAfterSorting();

			//			ErrorDump.debug(this, "c.getFontSize(): " + c.getFontSize());

			//			ErrorDump.debug(this, "c.getFoundLines(): " + c.getFoundLines());

			//			ErrorDump.debug(this, "topItem: " + topItem);
			//			ErrorDump.debug(this, "bottomItem: " + bottomItem);

			//			ErrorDump.debug(this, "STh get top & bottom elements " + (System.currentTimeMillis() - t));
			t = System.currentTimeMillis();

			// TODO: what if there are no items at all?  Could crash here...
			if (topItem != null && bottomItem != null)
			{
				// connect these to their respective neighbouring elements
				// TODO: speedup -- if new edge already exists...
				if (highestNeighbourBelow != null) // in this loop, about 100ms lost
				{
					//		ErrorDump.debug(this, "in highestNeighbourBelow section");
					// don't have to worry about symmetrical edges
					AdjacencyEdge edgeToAdd = new AdjacencyEdge
					(bottomItem, highestNeighbourBelow, AdjacencyEdge.REL_BELOW);
					// remove edges between highestNeighbourBelow and other items in segment
					long st = System.currentTimeMillis();
					t = System.currentTimeMillis();
					List<AdjacencyEdge<GenericSegment>> edgesToRemove = 
						new ArrayList<AdjacencyEdge<GenericSegment>>();
					Iterator i = priorityEdges.iterator();
					while(i.hasNext())
					{
						AdjacencyEdge ae = (AdjacencyEdge)i.next();
						if (ae.isVertical())
						{
							if (ae.getNodeFrom() == highestNeighbourBelow)
							{
								if (c.getItems().contains(ae.getNodeTo()))
								{
									//									ErrorDump.debug(this, "1removing existing edge: " + ae);
									edgesToRemove.add(ae);
								}
							}
							else if (ae.getNodeTo() == highestNeighbourBelow)
							{
								if (c.getItems().contains(ae.getNodeFrom()))
								{
									//									ErrorDump.debug(this, "2removing existing edge: " + ae);
									edgesToRemove.add(ae);
								}
							}
						}
					}
					priorityEdges.removeAll(edgesToRemove);

					//					ErrorDump.debug(this, "STh whole process " + (System.currentTimeMillis() - st));
					// TODO: add in sequence, don't just add anywhere and sort ;)
					//					ErrorDump.debug(this, "3adding in sequence: " + edgeToAdd);
					//					priorityEdges.addInSequence(edgeToAdd, new EdgeAttributeComparator());

					priorityEdges.add(edgeToAdd);
					Collections.sort(priorityEdges, new EdgeAttributeComparator());

				}
				//	ErrorDump.debug(this, "o107: " + System.currentTimeMillis());
				if (lowestNeighbourAbove != null) // in this loop, about 90ms lost
				{
					//		ErrorDump.debug(this, "in lowestNeighbourAbove section");
					// don't have to worry about symmetrical edges
					AdjacencyEdge edgeToAdd = new AdjacencyEdge
					(topItem, lowestNeighbourAbove, AdjacencyEdge.REL_ABOVE);
					// remove edges between highestNeighbourBelow and other items in segment
					List<AdjacencyEdge<GenericSegment>> edgesToRemove = 
						new ArrayList<AdjacencyEdge<GenericSegment>>();
					Iterator i = priorityEdges.iterator();
					while(i.hasNext())
					{
						AdjacencyEdge ae = (AdjacencyEdge)i.next();
						if (ae.isVertical())
						{
							if (ae.getNodeFrom() == lowestNeighbourAbove)
							{
								if (c.getItems().contains(ae.getNodeTo()))
								{
									//									ErrorDump.debug(this, "4removing existing edge: " + ae);
									edgesToRemove.add(ae);
								}
							}
							else if (ae.getNodeTo() == lowestNeighbourAbove)
							{
								if (c.getItems().contains(ae.getNodeFrom()))
								{
									//									ErrorDump.debug(this, "5removing existing edge: " + ae);
									edgesToRemove.add(ae);
								}
							}
						}
					}
					priorityEdges.removeAll(edgesToRemove);

					// TODO: add in sequence, don't just add anywhere and sort ;)
					//					ErrorDump.debug(this, "6adding in sequence: " + edgeToAdd);
					//					priorityEdges.addInSequence(edgeToAdd, new EdgeAttributeComparator());

					priorityEdges.add(edgeToAdd);
					Collections.sort(priorityEdges, new EdgeAttributeComparator());
				}
			}
			//			ErrorDump.debug(this, "STh finished " + (System.currentTimeMillis() - t));
			t = System.currentTimeMillis();
			//	ErrorDump.debug(this, "o108: " + System.currentTimeMillis());
			// prob. unnecessary to worry about removing other connections...??? better to do it

			// any comparisons should compare to cluster, and not just to the segment :)
			// this also allows comparisons with e.g. alignment and width


			// map nearest up-edge (nearest neighbour) to top component (line)
			// and nearest down-edge (nn) to bottom component (line)
			// this way the line spacing/font size matches :)
			// any newly-created edges must be added in order, irrespective of whether they have been already visited?
			// (unlikely that they would have been visited if the ordering is correct) -- otherwise they are already gone!
			// so, the method requires:
			// edge list (priorityEdges)
			// and that's all? :)
		}
	}

	protected static List<GenericSegment> findNearestVerticalNeighbours
	(GenericSegment c, List<AdjacencyEdge<GenericSegment>> allEdges, 
			HashMap<GenericSegment,List<GenericSegment>> vertNeighbourMap)
	{
		//	    	ErrorDump.debug(this, "in fNVN with c: " + c);
		//    	c is the whole cluster...
		if (vertNeighbourMap.containsKey(c))
		{
			return vertNeighbourMap.get(c);
		}
		else
		{
			// find lowest neighbourAbove and highest neighbourBelow
			GenericSegment lowestNeighbourAbove = null;
			GenericSegment highestNeighbourBelow = null;

			//Iterator edgeIter = priorityEdges.iterator();
			Iterator edgeIter = allEdges.iterator();
			while(edgeIter.hasNext())
			{
				AdjacencyEdge ae = (AdjacencyEdge)edgeIter.next();
				GenericSegment segFrom = (GenericSegment) ae.getNodeFrom();
				GenericSegment segTo = (GenericSegment) ae.getNodeTo();

				if (ae.isVertical())
				{
					// we can assume that the bounding box of the current cluster is correct
					// edge points into or out of cluster
					if (c == segFrom && c != segTo)
					{
						// segTo is the outside element
						if (segTo.getYmid() > c.getY2())
						{
							// segTo is above the cluster
							if (lowestNeighbourAbove == null ||
									segTo.getYmid() < lowestNeighbourAbove.getYmid())
							{
								lowestNeighbourAbove = segTo;
							}
						}
						else if (segTo.getYmid() < c.getY1())
						{
							// segTo is below the cluster
							if (highestNeighbourBelow == null ||
									segTo.getYmid() > highestNeighbourBelow.getYmid())
							{
								highestNeighbourBelow = segTo;
							}
						}
						else
						{
							// do nothing if within boundary of cluster
							// but not swallowed for some reason
						}
					}
					else if (c != segFrom && c == segTo)
					{
						// segFrom is the outside element
						if (segFrom.getYmid() > c.getY2())
						{
							// segTo is above the cluster
							if (lowestNeighbourAbove == null ||
									segFrom.getYmid() < lowestNeighbourAbove.getYmid())
							{
								lowestNeighbourAbove = segFrom;
							}
						}
						else if (segFrom.getYmid() < c.getY1())
						{
							// segTo is below the cluster
							if (highestNeighbourBelow == null ||
									segFrom.getYmid() > highestNeighbourBelow.getYmid())
							{
								highestNeighbourBelow = segFrom;
							}
						}
						else
						{
							// do nothing if within boundary of cluster
							// but not swallowed for some reason
						}
					}
				}
			}

			//ErrorDump.debug(this, "in foo section");
			//ErrorDump.debug(this, "lowestNeighbourAbove: " + lowestNeighbourAbove);
			//ErrorDump.debug(this, "highestNeighbourBelow: " + highestNeighbourBelow);

			List<GenericSegment> retVal = new ArrayList<GenericSegment>();
			retVal.add(lowestNeighbourAbove);
			retVal.add(highestNeighbourBelow);
			vertNeighbourMap.put(c, retVal);
			return retVal;
		}
	}

	/*
	protected static ListUtils findNearestVerticalNeighbours
    	(Cluster c, EdgeList allEdges, HashMap vertNeighbourMap)
    {
//    	ErrorDump.debug(this, "in fNVN with c: " + c);
//    	c is the whole cluster...
    	if (vertNeighbourMap.containsKey(c))
    	{
    		return (ListUtils)vertNeighbourMap.get(c);
    	}
    	else
    	{
		 // find lowest neighbourAbove and highest neighbourBelow
			TextSegment lowestNeighbourAbove = null;
			TextSegment highestNeighbourBelow = null;

			//Iterator edgeIter = priorityEdges.iterator();
			Iterator edgeIter = allEdges.iterator();
			while(edgeIter.hasNext())
			{
				AdjacencyEdge ae = (AdjacencyEdge)edgeIter.next();
				TextSegment segFrom = (TextSegment) ae.getNodeFrom();
				TextSegment segTo = (TextSegment) ae.getNodeTo();

				if (ae.isVertical())
				{
					// we can assume that the bounding box of the current cluster is correct
					// edge points into or out of cluster
					if (c.getItems().contains(segFrom) && !c.getItems().contains(segTo))
					{
						// segTo is the outside element
						if (segTo.getYmid() > c.getY2())
						{
							// segTo is above the cluster
							if (lowestNeighbourAbove == null ||
								segTo.getYmid() < lowestNeighbourAbove.getYmid())
							{
								lowestNeighbourAbove = segTo;
							}
						}
						else if (segTo.getYmid() < c.getY1())
						{
							// segTo is below the cluster
							if (highestNeighbourBelow == null ||
								segTo.getYmid() > highestNeighbourBelow.getYmid())
							{
								highestNeighbourBelow = segTo;
							}
						}
						else
						{
							// do nothing if within boundary of cluster
							// but not swallowed for some reason
						}
					}
					else if (!c.getItems().contains(segFrom) && c.getItems().contains(segTo))
					{
						// segFrom is the outside element
						if (segFrom.getYmid() > c.getY2())
						{
							// segTo is above the cluster
							if (lowestNeighbourAbove == null ||
								segFrom.getYmid() < lowestNeighbourAbove.getYmid())
							{
								lowestNeighbourAbove = segFrom;
							}
						}
						else if (segFrom.getYmid() < c.getY1())
						{
							// segTo is below the cluster
							if (highestNeighbourBelow == null ||
								segFrom.getYmid() > highestNeighbourBelow.getYmid())
							{
								highestNeighbourBelow = segFrom;
							}
						}
						else
						{
							// do nothing if within boundary of cluster
							// but not swallowed for some reason
						}
					}
				}
			}

			//ErrorDump.debug(this, "in foo section");
			//ErrorDump.debug(this, "lowestNeighbourAbove: " + lowestNeighbourAbove);
			//ErrorDump.debug(this, "highestNeighbourBelow: " + highestNeighbourBelow);

			ListUtils retVal = new ListUtils();
			retVal.add(lowestNeighbourAbove);
			retVal.add(highestNeighbourBelow);
			vertNeighbourMap.put(c, retVal);
			return retVal;
    	}
	}
	 */

	protected static List<GenericSegment> swallow
	(List<GenericSegment> l1, List<GenericSegment> l2,
			List<GenericSegment> items, HashMap clustHash)
			{
		//return false;

		// TODO: RETURN LIST OF SWALLOWED ITEMS
		CompositeSegment<GenericSegment> temp = 
			new CompositeSegment<GenericSegment>();
		temp.getItems().addAll(l1);
		temp.getItems().addAll(l2);
		temp.findBoundingBox();

		List<GenericSegment> swallowedItems = 
			new ArrayList<GenericSegment>();

		boolean loop = true;
		while(loop)
		{
			swallowedItems = 
				//				items.getElementsWithCentresWithinBBoxOrViceVersa(temp);
				ListUtils.getElementsIntersectingBBox(items, temp);

			List<GenericSegment> newItems = new ArrayList<GenericSegment>();

			for(GenericSegment gs : swallowedItems)
			{
				if (clustHash.get(gs) != null) // if belongs to another cluster
				{
					CandidateCluster clust = (CandidateCluster)clustHash.get(gs);
					newItems.addAll(clust.getItems());
				}
			}

			swallowedItems.addAll(newItems);
			ListUtils.removeDuplicates(swallowedItems);

			if (temp.getItems().size() == swallowedItems.size())
			{
				// no items swallowed
				loop = false;
			}
			else  // elsepart added 18.05.07
			{
				// commented out 6.06.07
				//if (newSegment instanceof Cluster) return true;
			}
			temp.setItems(swallowedItems);
			temp.findBoundingBox();
		}

		// return false if ok
		//temp.flattenByOneLevel();

		// instead of flattening (so that text fragments are items), make items the lines...

		return swallowedItems;

		//temp.findLinesOld();

		//return temp.checkForChasms();
			}

	// TODO: This method will crash if the input list contains non-TextSegments
	protected static CandidateCluster makeCluster(List<GenericSegment> items)
	{
		CandidateCluster retVal = new CandidateCluster();
		for (GenericSegment gs : items)
			retVal.getItems().add((TextSegment)gs);
		retVal.findFontSize(); // added 13.08.08
		//retVal.findLinesWidth();  // this method uses pnglf and the entire width
		//Collections.sort(retVal.getFoundLines(), new YComparator());

		CandidateCluster tempClust = new CandidateCluster();
		for (GenericSegment gs : items)
			tempClust.getItems().add((TextSegment)gs);
		//	tempClust.flattenByOneLevel();
		tempClust.findLines(Float.MAX_VALUE); // 14.08.08 this method ensures that the resulting lines are SORTED
		// but IntegrateLines destroys this... :(
		retVal.setFoundLines(tempClust.getFoundLines());
		// above doesn't work due to NPE, but need to fix it soon TODO

		//		TODO: Integrate lines?
		//		retVal.integrateLines();

		Collections.sort(retVal.getFoundLines(), new YComparator());
		retVal.findBoundingBox();
		return retVal;
	}

	// pre: foundLines and fontSize are set
	// TODO: what when the line spacing is different? replace fontSize for vertical with lineSpacing?
	// TODO: consider the shape of the gap and generate a score?
	public static boolean checkForChasms(CandidateCluster cts)
	{
		float minChasmHeight = 3.5f;
		float minChasmWidth = 0.5f;

		List<List<GenericSegment>> lineGaps = findLineGaps(cts, minChasmWidth * cts.getFontSize());

		// TODO: sort out this static shit!
		List<GenericSegment> gaps = 
			mergeLineGaps(lineGaps, minChasmWidth * cts.getFontSize(), minChasmHeight * cts.getFontSize());

		for(GenericSegment gap : gaps)
		{
			if ((gap.getWidth() > minChasmWidth * cts.getFontSize()) && 
					(gap.getHeight() > minChasmHeight * cts.getFontSize()))
				return true;
		}
		return false;
	}

	// pre: foundLines must be set
	// returns a SegmentList of SegmentLists of GenericSegments
	public static List<List<GenericSegment>> findLineGaps(CandidateCluster cts, float minWidth)
	{
		List<List<GenericSegment>> retVal = new ArrayList<List<GenericSegment>>();
		for (CompositeSegment<? extends GenericSegment> l : cts.getFoundLines())
		{
			List<GenericSegment> lineGaps = new ArrayList<GenericSegment>();
			for (int n = 1; n < l.getItems().size(); n ++)
			{
				GenericSegment a = l.getItems().get(n - 1);
				GenericSegment b = l.getItems().get(n);

				// assume that a and b intersect; and that b is to the right of a
				if ((b.getX1() - a.getX2()) > minWidth)
				{
					float newY1 = Utils.maximum(a.getY1(), b.getY1());
					float newY2 = Utils.minimum(a.getY2(), b.getY2());
					GenericSegment gapSeg = 
						new GenericSegment(a.getX2(), b.getX1(), newY1, newY2);
					lineGaps.add(gapSeg);
				}
			}
			retVal.add(lineGaps);
		}
		return retVal;
	}

	// TODO: for speedup, find a way to make this work with iterators instead
	// of for/next loops.
	public static List<GenericSegment> mergeLineGaps
	(List<List<GenericSegment>> lineGaps, float minWidth, float minHeight)
	{
		List<GenericSegment> retVal = new ArrayList<GenericSegment>();
		for (int n = 1; n <= lineGaps.size(); n ++)
		{
			List<GenericSegment> thisLineGaps = lineGaps.get(n-1);
			List<GenericSegment> nextLineGaps = new ArrayList<GenericSegment>();
			if (n < lineGaps.size())
				nextLineGaps = lineGaps.get(n);

			// compare this with next
			//Iterator i = thisLineGaps.iterator();
			//while(i.hasNext())
			boolean potentialNewGap = false;
			float lastX2 = -1.0f;
			float lastY1 = -1.0f;
			float lastY2 = -1.0f;
			int lastIndex = -1;
			for (int i = 0; i < thisLineGaps.size(); i ++)
			{
				GenericSegment thisGap = (GenericSegment)thisLineGaps.get(i);//i.next();
				boolean intersects = false;
				boolean addedGap = false;
				if (potentialNewGap)
				{
					if (lastX2 >= thisGap.getX1())
					{
						float newX2 = lastX2;
						if (lastX2 <= thisGap.getX2())
						{
							potentialNewGap = false;
							newX2 = thisGap.getX2();
						}
						GenericSegment newGap = new GenericSegment
						(thisGap.getX1(), newX2, lastY1, thisGap.getY2());
						nextLineGaps.add(lastIndex + 1, newGap);
						addedGap = true;
						intersects = true;
					}
				}
				if (!addedGap)
				{
					for (int j = 0; j < nextLineGaps.size(); j ++)
						//Iterator j = nextLineGaps.iterator();
						//while(j.hasNext())
					{
						GenericSegment nextGap = nextLineGaps.get(j);//j.next();

						if (SegmentUtils.horizIntersect(thisGap, nextGap))
						{
							intersects = true;
							// update next with vertical co-ordinates of this; shrink x if necc.

							nextGap.setY2(thisGap.getY2());
							if (thisGap.getX1() > nextGap.getX1())
								nextGap.setX1(thisGap.getX1());
							if (thisGap.getX2() < nextGap.getX2())
							{
								lastX2 = nextGap.getX2();
								lastY1 = nextGap.getY1();
								lastY2 = nextGap.getY2();
								nextGap.setX2(thisGap.getX2());
								potentialNewGap = true;
								lastIndex = j;
							}
							else
							{
								potentialNewGap = false;
							}
						}

					}
				}
				// the last row doesn't intersect with anything anyway :)
				if (!intersects)// || n == (lineGaps.size()))
				{
					// add the last one to the result
					// add to result
					retVal.add(thisGap);
				}
			}

			// here add remaining last row of gaps (nextLineGaps)
			//if (n == lineGaps.size() - 1)
			//	retVal.addAll(nextLineGaps);
		}
		return retVal;
	}

	protected static List<GenericSegment> createList(GenericSegment gs)
	{
		List<GenericSegment> retVal = new ArrayList<GenericSegment>();
		retVal.add(gs);
		return(retVal);
	}

	protected static List<GenericSegment> createList(List<? extends GenericSegment> l)
	{
		List<GenericSegment> retVal = new ArrayList<GenericSegment>();
		for (GenericSegment gs : l)
			retVal.add(gs);
		return(retVal);
	}


	protected static boolean isValidCluster(CandidateCluster c)
	{
		// prerequisite for calling this method is that the lines have been found ...
		// and that the average linespacing has been found
		//c.findLinesWidth();
		c.setCalculatedFields();
		// now, we check that the linespacing is constant by comparing the
		// spacing of each consecutive line with the average linespacing
		boolean clashingLines = false;
		CompositeSegment<? extends GenericSegment> prevLine = null;

		for (CompositeSegment<? extends GenericSegment> l : c.getFoundLines())
		{
			if (prevLine != null)
			{
				float lineSpacing = (prevLine.getY1() - l.getY1()) / c.getFontSize();
				if (SegmentUtils.vertIntersect(prevLine, l.getYmid())) clashingLines = true;
				//    			ErrorDump.debug(this, "lineSpacing: " + lineSpacing);
				if (!Utils.within(lineSpacing, c.getLineSpacing(), LINE_SPACING_TOLERANCE))
					return false;
				// fontsize check too
			}
			prevLine = l;
		}

		//		ErrorDump.debug(this, "returning: " + !checkForChasms(c));
		return !checkForChasms(c);
	}
}