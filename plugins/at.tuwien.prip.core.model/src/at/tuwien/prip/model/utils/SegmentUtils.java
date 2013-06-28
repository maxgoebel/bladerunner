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
package at.tuwien.prip.model.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.AdjacencyEdge;
import at.tuwien.prip.model.graph.comparators.XComparator;

/**
 * SegmentUtils.java
 *
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 */
public class SegmentUtils {

	public static boolean horizMinIntersect(GenericSegment seg1,
			GenericSegment seg2, float percent)
	{
		// calculate intersectionWidth as the * of the
		// smaller width dimension
		float intersectionWidth = seg1.getWidth() * percent;
		if (seg2.getWidth() < seg1.getWidth())
			intersectionWidth = seg2.getWidth() * percent;

		// seg1.getX1() lies within bounds of seg2
		//    ###      ####  seg1
		//  #######  ####    seg2
		if (seg1.getX1() >= seg2.getX1() && seg1.getX1() <= seg2.getX2())
		{
			// intersection between seg1.getX1() and min(seg1.getX2(), seg2.getX2())
			return (Utils.minimum(seg1.getX2(), seg2.getX2()) - seg1.getX1() 
					>= intersectionWidth);
		}
		// seg2.getX1() lies within bounds of seg1
		//	#######  ####    seg1
		//    ###      ####  seg2
		else if (seg2.getX1() >= seg1.getX1() && seg2.getX1() <= seg1.getX2())
		{
			// intersection between seg2.getX1() and min(seg1.getX2(), seg2.getX2())
			//	System.out.println("actual: " + (Utils.minimum(seg1.getX2(), seg2.getX2()) - seg1.getX1()));
			//	System.out.println("required 0.2: " + intersectionWidth);
			return (Utils.minimum(seg1.getX2(), seg2.getX2()) - seg2.getX1() 
					>= intersectionWidth);
		}
		return false;
	}

	public static boolean horizIntersect(GenericSegment seg1,
			GenericSegment seg2)
	{
		return (seg1.getX1() >= seg2.getX1() && seg1.getX1() <= seg2.getX2())  //seg1.getX1() edge lies along seg2 boundary
		|| (seg2.getX1() >= seg1.getX1() && seg2.getX1() <= seg1.getX2()); //or seg2.getX1() edge lies along seg1 boundary
		// 13.07.10 seems sufficient
	}

	public static boolean horizIntersect(GenericSegment seg1, float sx1,
			float sx2)
	{
		GenericSegment seg2 = new GenericSegment(sx1, sx2, -1, -1);
		return (seg1.getX1() >= seg2.getX1() && seg1.getX1() <= seg2.getX2())
		|| (seg2.getX1() >= seg1.getX1() && seg2.getX1() <= seg1.getX2());
	}

	public static boolean horizIntersect(GenericSegment seg, float val)
	{
		return (seg.getX1() <= val && seg.getX2() >= val)
		|| (seg.getX1() >= val && seg.getX2() <= val);
	}

	public static boolean vertMinIntersect(GenericSegment seg1,
			GenericSegment seg2, float percent)
	{
		// calculate intersectionWidth as the * of the
		// smaller width dimension
		float intersectionHeight = seg1.getHeight() * percent;
		if (seg2.getHeight() < seg1.getHeight())
			intersectionHeight = seg2.getHeight() * percent;

		//			System.out.println("intersection height limit: " + intersectionHeight);
		// seg1.getY1() lies within bounds of seg2
		if (seg1.getY1() >= seg2.getY1() && seg1.getY1() <= seg2.getY2())
		{
			// intersection between seg1.getY1() and min(seg1.getY2(), seg2.getY2())
			//				System.out.println("actual intersection height: "+(Utils.minimum(seg1.getY2(), seg2.getY2()) - seg1.getY1()));
			return (Utils.minimum(seg1.getY2(), seg2.getY2()) - seg1.getY1() 
					>= intersectionHeight);
		}
		// seg2.getY1() lies within bounds of seg1
		else if (seg2.getY1() >= seg1.getY1() && seg2.getY1() <= seg1.getY2())
		{
			// intersection between seg2.getX1() and min(seg1.getX2(), seg2.getX2())
			//				System.out.println("actual intersection height: "+(Utils.minimum(seg1.getY2(), seg2.getY2()) - seg1.getY1()));
			return (Utils.minimum(seg1.getY2(), seg2.getY2()) - seg2.getY1() 
					>= intersectionHeight);
		}
		return false;
	}

	public static boolean vertIntersect(GenericSegment seg1, GenericSegment seg2)
	{
		return (seg1.getY1() >= seg2.getY1() && seg1.getY1() <= seg2.getY2())
		|| (seg2.getY1() >= seg1.getY1() && seg2.getY1() <= seg1.getY2());
	}

	public static boolean vertIntersect(GenericSegment seg1, float sy1,
			float sy2)
	{
		GenericSegment seg2 = new GenericSegment(-1, -1, sy1, sy2);
		return (seg1.getY1() >= seg2.getY1() && seg1.getY1() <= seg2.getY2())
		|| (seg2.getY1() >= seg1.getY1() && seg2.getY1() <= seg1.getY2());
	}

	public static boolean vertIntersect(GenericSegment seg, float val)
	{
		return (seg.getY1() <= val && seg.getY2() >= val)
		|| (seg.getY1() >= val && seg.getY1() <= val);
	}

	public static boolean intersects(GenericSegment seg1, GenericSegment seg2)
	{
		//System.out.println("seg1: " + seg1 + "\nseg2: " + seg2);
		//System.out.println("H: " + horizIntersect(seg1, seg2) + " V: " + vertIntersect(seg1, seg2));
		return horizIntersect(seg1, seg2) && vertIntersect(seg1, seg2);
	}

	public static boolean intersects(AdjacencyEdge<? extends GenericSegment> e1,
			AdjacencyEdge<? extends GenericSegment> e2)
	{
		/* 2011-01-26 variables never read...
			GenericSegment a1 = e1.getNodeFrom();
	        GenericSegment b1 = e1.getNodeTo();
	        GenericSegment a2 = e2.getNodeFrom();
	        GenericSegment b2 = e2.getNodeTo();
		 */

		// returns FALSE if a nonsensical comparison is requested
		// TODO: throw exception if a horizontal edge
		// is compared with a vertical?
		if ((e1.isHorizontal() == false && e2.isHorizontal() == true) ||
				(e1.isHorizontal() == true && e2.isHorizontal() == false))
			return false;

		float hi1, low1, hi2, low2;

		switch(e1.getDirection())
		{
		case AdjacencyEdge.REL_LEFT:
			hi1 = e1.getNodeFrom().getX1();
			low1 = e1.getNodeTo().getX2();
			break;
		case AdjacencyEdge.REL_RIGHT:
			hi1 = e1.getNodeTo().getX1();
			low1 = e1.getNodeFrom().getX2();
			break;
		case AdjacencyEdge.REL_ABOVE:
			hi1 = e1.getNodeTo().getY1();
			low1 = e1.getNodeFrom().getY2();
			break;
		case AdjacencyEdge.REL_BELOW:
			hi1 = e1.getNodeFrom().getY1();
			low1 = e1.getNodeTo().getY2();
			break;
		default:
			// yet another nonsensical comparison!
			return false;
		}

		switch(e2.getDirection())
		{
		case AdjacencyEdge.REL_LEFT:
			hi2 = e2.getNodeFrom().getX1();
			low2 = e2.getNodeTo().getX2();
			break;
		case AdjacencyEdge.REL_RIGHT:
			hi2 = e2.getNodeTo().getX1();
			low2 = e2.getNodeFrom().getX2();
			break;
		case AdjacencyEdge.REL_ABOVE:
			hi2 = e2.getNodeTo().getY1();
			low2 = e2.getNodeFrom().getY2();
			break;
		case AdjacencyEdge.REL_BELOW:
			hi2 = e2.getNodeFrom().getY1();
			low2 = e2.getNodeTo().getY2();
			break;
		default:
			// yet another nonsensical comparison!
			return false;
		}

		return (low1 >= low2 && low1 <= hi2) ||
		(hi1 >= low2 && hi1 <= hi2) ||
		(low1 < low2 && hi1 > hi2);
	}

	public static GenericSegment getLeftMostSegment(List<? extends GenericSegment> l)
	{
		List<GenericSegment> sortedList = new ArrayList<GenericSegment>();
		for (GenericSegment gs : l)
			sortedList.add(gs);
		Collections.sort(l, new XComparator());
		return l.get(0);
	}
	
	public static GenericSegment getRightMostSegment(List<? extends GenericSegment> l)
	{
		List<GenericSegment> sortedList = new ArrayList<GenericSegment>();
		for (GenericSegment gs : l)
			sortedList.add(gs);
		Collections.sort(l, new XComparator());
		return l.get(l.size() - 1);
	}
	
}
