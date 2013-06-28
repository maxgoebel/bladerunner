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
package at.tuwien.prip.model.graph.comparators;

import java.util.Comparator;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.AdjacencyEdge;
import at.tuwien.prip.model.utils.Utils;

/**
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class EdgeAttributeComparator implements Comparator<AdjacencyEdge<? extends GenericSegment>>
{
	public int compare(AdjacencyEdge<? extends GenericSegment> ae1,
		AdjacencyEdge<? extends GenericSegment> ae2)
	{
		int retval;
		
		// > means 'before'
		
		// shorter edge (line spacing) > longer edge
		// smaller width difference > larger width difference (forget about alignment for now)
		// smaller font > larger font
		// same font size > differing font sizes
		// edges that contain non-text segments
		
		if (ae1.isHorizontal() && ae2.isHorizontal())
		{
			int lengthRetVal = (int)((ae1.getPhysicalLength() / ae1.getFontSize()
					- (ae2.getPhysicalLength() / ae2.getFontSize())) * 10f);
				// if e.g. 0.9 and 1.1 get rounded, still within 10%...
				if (Utils.within(ae1.getPhysicalLength() / ae1.getFontSize(),
					ae2.getPhysicalLength() / ae2.getFontSize(), 0.1f))
				{
					retval = 0;
				}
				else 
				{
					retval = lengthRetVal;
				}
				// i.e. error tolerance of 0.1 due to int rounding
				
			//return 0;
		}
		else if (ae1.isVertical() && ae2.isHorizontal())
		{
			// first element comes before second
			retval = -1;
		}
		else if (ae1.isHorizontal() && ae2.isVertical())
		{
			// second element comes before first
			retval = 1;
		}
		else // this is where it gets exciting :)
		{
			

			/*
			 * mcg: this is a fix to
			java.lang.IllegalArgumentException: Comparison method violates its general contract!
				at java.util.TimSort.mergeHi(TimSort.java:868)
				at java.util.TimSort.mergeAt(TimSort.java:485)
				at java.util.TimSort.mergeForceCollapse(TimSort.java:426)
				at java.util.TimSort.sort(TimSort.java:223)
				at java.util.TimSort.sort(TimSort.java:173)
				at java.util.Arrays.sort(Arrays.java:659)
				at java.util.Collections.sort(Collections.java:217)
				at at.ac.tuwien.dbai.pdfwrap.analysis.PageSegmenter.orderedEdgeCluster(PageSegmenter.java:456)
			*/
			retval = 0;
			
			/*
			GenericSegment f1 = ae1.getNodeFrom();
			GenericSegment t1 = ae1.getNodeTo();
			GenericSegment f2 = ae2.getNodeFrom();
			GenericSegment t2 = ae2.getNodeTo();
			
			boolean text1 = false, text2 = false;
			if (f1 instanceof TextSegment && t1 instanceof TextSegment)
				text1 = true;
			if (f2 instanceof TextSegment && t2 instanceof TextSegment)
				text2 = true;
			
			if (text1 && text2)
			{	
				TextSegment ft1 = (TextSegment)f1;
				TextSegment tt1 = (TextSegment)t1;
				TextSegment ft2 = (TextSegment)f2;
				TextSegment tt2 = (TextSegment)t2;
				
				boolean sfs1 = false, sfs2 = false;
				if (Utils.sameFontSize(ft1, tt1)) sfs1 = true;
				if (Utils.sameFontSize(ft2, tt2)) sfs2 = true;
				
				if (sfs1 && sfs2)
				{
					// smaller font/larger font
					if (Utils.sameFontSize(ft1, ft2))
					{
						// line spacing
						if (ae1.isVertical() && ae2.isVertical())
						{
							int lengthRetVal = (int)((ae1.getPhysicalLength() / ae1.getFontSize()
								- (ae2.getPhysicalLength() / ae2.getFontSize())) * 10f);
							// if e.g. 0.9 and 1.1 get rounded, still within 10%...
							if (Utils.within(ae1.getPhysicalLength() / ae1.getFontSize(),
								ae2.getPhysicalLength() / ae2.getFontSize(), 0.1f))
								lengthRetVal = 0;
							// i.e. error tolerance of 0.1 due to int rounding
							//if (retVal == 0)
							//{
							int widthRetVal;
	//							 width difference
							float width1, width2;
							if (ft1.getWidth() > tt1.getWidth())
								width1 = ft1.getWidth() / tt1.getWidth();
							else
								width1 = tt1.getWidth() / ft1.getWidth();
							if (ft2.getWidth() > tt2.getWidth())
								width2 = ft2.getWidth() / tt2.getWidth();
							else
								width2 = tt2.getWidth() / ft2.getWidth();
							
							if (Utils.within(width1, width2, 0.1f))
							{
								//return 0; // no further tasks...
								widthRetVal = 0;
							}
							else if (width1 < width2) widthRetVal = -1;
							else widthRetVal = 1;
							
							// changed 6.06.07
							// was the other way round; i.e. length had priority over width
							// 7.07.07: changed back
							// 29.07.08: changed again :)
							if (lengthRetVal == 0)
							{
								retval = widthRetVal;
							}
							else 
							{
								retval = lengthRetVal;
							}
							
							//}
							//else return retVal;
						}
						else retval = 0; // edges not vertical; academic
					}
					else if (ft1.getFontSize() < ft2.getFontSize()) retval = -1;
					else retval = 1;
				}
				else if (sfs1) retval = -1;
				else if (sfs2) retval = 1;
				else retval = 0;
			}
			else if (text1)
			{
				// first element comes before second
				retval = -1;
			}
			else if (text2)
			{
				// second element comes before first
				retval = 1;
			}
			else
			{
				// neither is a text segment; equal
				retval = 0;
			}
			*/
		}
		return retval;
	}

	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
}
