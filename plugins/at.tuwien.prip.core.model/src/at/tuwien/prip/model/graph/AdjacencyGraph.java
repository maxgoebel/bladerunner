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
package at.tuwien.prip.model.graph;

// todo: linear segments method only for first build on one level
// (i.e. with getElementsAbove, etc)

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.comparators.XComparator;
import at.tuwien.prip.model.graph.comparators.YComparator;
import at.tuwien.prip.model.utils.SegmentUtils;
import at.tuwien.prip.model.utils.Utils;

/**
 * AdjacencyGraph -- the neighbourhood graph
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class AdjacencyGraph<T extends GenericSegment> 
implements Cloneable
{   
	//	protected List<T> nodes;
	protected List<AdjacencyEdge<T>> edges;

	protected AdjacencyMatrix<T> am;
	
	/*
	protected List<T> neighboursLeft;
	protected List<T> neighboursRight;
	protected List<T> neighboursAbove;
	protected List<T> neighboursBelow;
	 */

	protected List<T> horiz;
	protected List<T> vert;

	protected HashMap<T, List<AdjacencyEdge<T>>> edgesFrom;
	protected HashMap<T, List<AdjacencyEdge<T>>> edgesTo;

	/**
	 * Constructor.
	 *
	 */
	public AdjacencyGraph()
	{
		edges = new ArrayList<AdjacencyEdge<T>>();

		horiz = new ArrayList<T>();
		vert = new ArrayList<T>();

		//    	2011-01-27 TODO: implement HashMap
		//    	edgesFrom = new HashMap<T, List<AdjacencyEdge<T>>>();
		//    	edgesTo = new HashMap<T, List<AdjacencyEdge<T>>>();
	}


	/**
	 * Add a list of nodes to this adjacency graph.
	 * @param nodes
	 */
	public void addList(List<T> nodes)
	{
		horiz.addAll(nodes);
		vert.addAll(nodes);
	}

	/**
	 * 
	 * @param thisSegment
	 * @param findFirst
	 * @return
	 */
	protected List<T> findNeighboursBelow(T thisSegment, boolean findFirst)
	{
		List<T> retVal = new ArrayList<T>();
		float threshold = 2.0f; // 2pt threshold

		// the next line

		// TODO: a quicker (binary-chop) searching method instead of using indexOf?
		// or a hash map?
		
		/*  returns -1 if the given node is not in the list */
		int index = vert.indexOf(thisSegment);
		if (index==-1) 
			return retVal;

		// look for neighbors above, return the first or null!
		for (int n = index + 1; n < vert.size(); n ++)
		{
			T o = vert.get(n);

			if (SegmentUtils.horizIntersect(o, thisSegment) &&
					(SegmentUtils.horizIntersect(thisSegment, o.getXmid()) ||
							SegmentUtils.horizIntersect(o, thisSegment.getXmid())))
			{
				// if there's already a neighbour...
				if (retVal.size() > 0)
				{
					if (o.getY1() <= (retVal.get(0).getY1()+threshold))
						retVal.add(o);
					else
						return retVal;
				}
				else
				{
					retVal.add(o);
					if (findFirst) 
						return retVal;
				}
			}
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSegment
	 * @param findFirst
	 * @return
	 */
	protected List<T> findNeighboursAbove(T thisSegment, boolean findFirst)
	{
		List<T> retVal = new ArrayList<T>();
		float threshold = 2.0f; // 2pt threshold

		// TODO: a quicker (binary-chop) searching method instead of using indexOf?
		// or a hash map?
		
		/* returns -1 if the given node is not in the list */
		int index = vert.indexOf(thisSegment);
		if (index==-1) 
			return retVal;


		// look for neighbors below, return the first or null!
		for (int n = index - 1; n >= 0; n --)
		{
			T o = vert.get(n);
			// 
			if (SegmentUtils.horizIntersect(o, thisSegment) 
					&& (SegmentUtils.horizIntersect(thisSegment, o.getXmid()) 
							|| SegmentUtils.horizIntersect(o, thisSegment.getXmid())))
			{
				// if there's already a neighbor...
				if (retVal.size() > 0)
				{
					if (o.getY2() >= (retVal.get(0).getY2()-threshold))
						retVal.add(o);
					else
						return retVal;
				}
				else
				{
					retVal.add(o);
					if (findFirst) 
						return retVal;
				}
			}
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSegment
	 * @param findFirst
	 * @return
	 */
	protected List<T> findNeighboursLeft(T thisSegment, boolean findFirst)
	{
		List<T> retVal = new ArrayList<T>();
		float threshold = 1.0f; // 1pt threshold

		// TODO: a quicker (binary-chop) searching method instead of using indexOf?
		// or a hash map?
		
		/* returns -1 if the given node is not in the list */
		int index = horiz.indexOf(thisSegment);
		if (index==-1) 
			return retVal;


		// look for neighbours below, return the first or null!
		for (int n = index - 1; n >= 0; n --)
		{
			T o = horiz.get(n);
			// 
			//          if (SegmentUtils.vertIntersect(o, thisSegment))
			if (SegmentUtils.vertMinIntersect(o, thisSegment, Utils.neighbourLOSMin) &&
					!SegmentUtils.horizMinIntersect(o, thisSegment, Utils.neighbourOverlapTolerance))
			{
				// if there's already a neighbour...
				if (retVal.size() > 0)
				{
					if (o.getX2()>=(retVal.get(0).getX2()-threshold))
						retVal.add(o);
					else
						return retVal;
				}
				else
				{
					retVal.add(o);
					if (findFirst) 
						return retVal;
				}
			}
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSegment
	 * @param findFirst
	 * @return
	 */
	protected List<T> findNeighboursRight(T thisSegment, boolean findFirst)
	{
		List<T> retVal = new ArrayList<T>();
		float threshold = 1.0f; // 1pt threshold

		// TODO: a quicker (binary-chop) searching method instead of using indexOf?
		// or a hash map?
		
		/* returns -1 if the given node is not in the list */
		int index = horiz.indexOf(thisSegment);
		if (index==-1) 
			return retVal;

		// look for neighbors below, return the first or null!
		for (int n = index + 1; n < horiz.size(); n ++)
		{
			T o = horiz.get(n);
			// 
			//          if (SegmentUtils.vertIntersect(o, thisSegment))
			if (SegmentUtils.vertMinIntersect(o, thisSegment, Utils.neighbourLOSMin) &&
					!SegmentUtils.horizMinIntersect(o, thisSegment, Utils.neighbourOverlapTolerance))
			{
				if (retVal.size() > 0)
				{
					if (o.getX1()<=(retVal.get(0).getX1()+threshold))
						retVal.add(o);
					else
						return retVal;
				}
				else
				{
					retVal.add(o);
					if (findFirst) return retVal;
				}
			}
		}
		return retVal;
	}

	/**
	 * Return all neighbors above.
	 * 
	 * @param thisSegment
	 * @return
	 */
	private List<T> findNeighboursAbove(T thisSegment)
	{
		return findNeighboursAbove(thisSegment, false);
	}

	/**
	 * Return all neighbors below.
	 * 
	 * @param thisSegment
	 * @return
	 */
	private List<T> findNeighboursBelow(T thisSegment)
	{
		return findNeighboursBelow(thisSegment, false);
	}
	
	/**
	 * Return all neighbors left.
	 * 
	 * @param thisSegment
	 * @return
	 */
	private List<T> findNeighboursLeft(T thisSegment)
	{
		return findNeighboursLeft(thisSegment, false);
	}

	/**
	 * Return all neighbors right.
	 * 
	 * @param thisSegment
	 * @return
	 */
	private List<T> findNeighboursRight(T thisSegment)
	{
		return findNeighboursRight(thisSegment, false);
	}

	/**
	 * Just returns the first neighbor.
	 * probably all that we need!
	 * 
	 * @param thisSegment
	 * @return
	 */
	private T findNeighbourAbove(T thisSegment)
	{
		List<T> l = findNeighboursAbove(thisSegment, true);
		if (l.size() > 0) return l.get(0);
		else return null;
	}

	/**
	 * Just returns the first neighbor.
	 * probably all that we need!
	 * 
	 * @param thisSegment
	 * @return
	 */
	private T findNeighbourBelow(T thisSegment)
	{
		List<T> l = findNeighboursBelow(thisSegment, true);
		if (l.size() > 0) return l.get(0);
		else return null;
	}
	
	/**
	 * Just returns the first neighbor.
	 * probably all that we need!
	 * 
	 * @param thisSegment
	 * @return
	 */
	private T findNeighbourLeft(T thisSegment)
	{
		List<T> l = findNeighboursLeft(thisSegment, true);
		if (l.size() > 0) return l.get(0);
		else return null;
	}

	/**
	 * Just returns the first neighbor.
	 * probably all that we need!
	 * 
	 * @param thisSegment
	 * @return
	 */
	private T findNeighbourRight(T thisSegment)
	{
		List<T> l = findNeighboursRight(thisSegment, true);
		if (l.size() > 0) return l.get(0);
		else return null;
	}


	// TODO: replace these crazy methods with methods that simply
	// add edges?

	/**
	 * 
	 * @param thisBlock
	 */
	private void generateEdgesSingle(T thisBlock)
	{
//		List<T> neighboursLeft = new ArrayList<T>();
//		T neighbourLeft = findNeighbourLeft(thisBlock);
//		if (neighbourLeft != null)
//		{
//			neighboursLeft.add(neighbourLeft);
//			edges.add(new AdjacencyEdge<T>(thisBlock, neighbourLeft, AdjacencyEdge.REL_LEFT));
//			edges.add(new AdjacencyEdge<T>(neighbourLeft, thisBlock, AdjacencyEdge.REL_RIGHT));
//		}
		
		List<T> neighboursRight = new ArrayList<T>();
		T neighbourRight = findNeighbourRight(thisBlock);
		if (neighbourRight != null)
		{
			neighboursRight.add(neighbourRight);
			edges.add(new AdjacencyEdge<T>(thisBlock, neighbourRight, AdjacencyEdge.REL_RIGHT));
			edges.add(new AdjacencyEdge<T>(neighbourRight, thisBlock, AdjacencyEdge.REL_LEFT));
		}
		
//		List<T> neighboursAbove = new ArrayList<T>();
//		T neighbourAbove = findNeighbourAbove(thisBlock);
//		if (neighbourAbove != null)
//		{
//			neighboursAbove.add(neighbourAbove);
//			edges.add(new AdjacencyEdge<T>(thisBlock, neighbourAbove, AdjacencyEdge.REL_ABOVE));
//			edges.add(new AdjacencyEdge<T>(neighbourAbove, thisBlock, AdjacencyEdge.REL_BELOW));
//		}
		
		List<T> neighboursBelow = new ArrayList<T>();
		T neighbourBelow = findNeighbourBelow(thisBlock);
		if (neighbourBelow != null)
		{
			neighboursBelow.add(neighbourBelow);
			edges.add(new AdjacencyEdge<T>(thisBlock, neighbourBelow, AdjacencyEdge.REL_BELOW));
			edges.add(new AdjacencyEdge<T>(neighbourBelow, thisBlock, AdjacencyEdge.REL_ABOVE));
		}
		
	}

	/**
	 * 
	 * @param thisBlock
	 */
	private void generateEdgesMultiple(T thisBlock)
	{
		List<T> neighboursLeft = findNeighboursLeft(thisBlock);
		List<T> neighboursRight = findNeighboursRight(thisBlock);
		List<T> neighboursAbove = findNeighboursAbove(thisBlock);
		List<T> neighboursBelow = findNeighboursBelow(thisBlock);

		// and create edges for each neighbouring direction
		for (T theNode : neighboursLeft)
		{
			edges.add(new AdjacencyEdge<T>(thisBlock, theNode, AdjacencyEdge.REL_LEFT));
			edges.add(new AdjacencyEdge<T>(theNode, thisBlock, AdjacencyEdge.REL_RIGHT));
			//theNode.getEdges().add(new Edge(theNode, lookupNode, Edge.DIR_RIGHT, level));   
		}
		for (T theNode : neighboursRight)
		{
			edges.add(new AdjacencyEdge<T>(thisBlock, theNode, AdjacencyEdge.REL_RIGHT));
			edges.add(new AdjacencyEdge<T>(theNode, thisBlock, AdjacencyEdge.REL_LEFT));
			//edges.add(new Edge(lookupNode, (Node)nIter.next(), Edge.DIR_RIGHT, level));
			//theNode.getEdges().add(new Edge(theNode, lookupNode, Edge.DIR_LEFT, level));
		}
		for (T theNode : neighboursAbove)
		{
			edges.add(new AdjacencyEdge<T>(thisBlock, theNode, AdjacencyEdge.REL_ABOVE));
			edges.add(new AdjacencyEdge<T>(theNode, thisBlock, AdjacencyEdge.REL_BELOW));
			//edges.add(new Edge(lookupNode, (Node)nIter.next(), Edge.DIR_ABOVE, level));
			//theNode.getEdges().add(new Edge(theNode, lookupNode, Edge.DIR_BELOW, level));
		}
		for (T theNode : neighboursBelow)
		{
			edges.add(new AdjacencyEdge<T>(thisBlock, theNode, AdjacencyEdge.REL_BELOW));
			edges.add(new AdjacencyEdge<T>(theNode, thisBlock, AdjacencyEdge.REL_ABOVE));
			//edges.add(new Edge(lookupNode, (Node)nIter.next(), Edge.DIR_BELOW, level));
			//theNode.getEdges().add(new Edge(theNode, lookupNode, Edge.DIR_ABOVE, level));
		}
	}

	/**
	 * Generate the edges for this graph.
	 */
	public void generateEdgesSingle()
	{
		edges.clear();

		/* sort horizontal left to right */
		Collections.sort(horiz, new XComparator());
		/* sort vertical top to bottom */
		Collections.sort(vert, new YComparator());

		//iterate over each block
		for (T thisBlock: vert)
		{
			generateEdgesSingle(thisBlock);
		}
		
//		removeDuplicateEdges();
	}

	/**
	 * Generate the edges for this graph.
	 */
	public void generateEdgesMultiple()
	{
		edges.clear();

		Collections.sort(horiz, new XComparator());
		Collections.sort(vert, new YComparator());

		// TODO: clear hash maps too

		for (T thisBlock: vert)
		{
			generateEdgesMultiple(thisBlock);
		}
		removeDuplicateEdges();
	}

	// TODO: to speed this up (and other operations...)
	// implement a hash map lookup for edges in the DocumentGraph method
	// or, rather EdgeList?
	/**
	 * Removes extra instances of an edge between two
	 * given nodes (even if they are distinct objects)
	 * 
	 * TODO: move to ListUtils?
	 */
	protected void removeDuplicateEdges()
	{
		List<AdjacencyEdge<T>> edgesToRemove = 
			new ArrayList<AdjacencyEdge<T>>();

		for (AdjacencyEdge<T> e1 : edges)
		{
			for (AdjacencyEdge<T> e2 : edges)
			{
				if (e1.getDirection() == e2.getDirection() &&
						e1.getNodeFrom() == e2.getNodeFrom() &&
						e1.getNodeTo() == e2.getNodeTo() &&
						e1 != e2 && !edgesToRemove.contains(e1))
				{
					edgesToRemove.add(e2);
				}
			}
		}
		edges.removeAll(edgesToRemove);
	}

	/**
	 * 
	 * @param thisSeg
	 * @return
	 */
	public List<T> getNeighboursLeft(T thisSeg)
	{
		List<T> retVal = new ArrayList<T>();
		for (AdjacencyEdge<T> e : edges)
		{
			if (e.getDirection() == AdjacencyEdge.REL_LEFT &&
					e.getNodeFrom() == thisSeg)
				retVal.add(e.getNodeTo());
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSeg
	 * @return
	 */
	public List<T> getNeighboursRight(GenericSegment thisSeg)
	{
		List<T> retVal = new ArrayList<T>();
		for (AdjacencyEdge<T> e : edges)
		{
			if (e.getDirection() == AdjacencyEdge.REL_RIGHT &&
					e.getNodeFrom() == thisSeg)
				retVal.add(e.getNodeTo());
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSeg
	 * @return
	 */
	public List<T> getNeighboursAbove(GenericSegment thisSeg)
	{
		List<T> retVal = new ArrayList<T>();
		for (AdjacencyEdge<T> e : edges)
		{
			if (e.getDirection() == AdjacencyEdge.REL_ABOVE &&
					e.getNodeFrom() == thisSeg)
				retVal.add(e.getNodeTo());
		}
		return retVal;
	}

	/**
	 * 
	 * @param thisSeg
	 * @return
	 */
	public List<T> getNeighboursBelow(GenericSegment thisSeg)
	{
		List<T> retVal = new ArrayList<T>();
		for (AdjacencyEdge<T> e : edges)
		{
			if (e.getDirection() == AdjacencyEdge.REL_BELOW &&
					e.getNodeFrom() == thisSeg)
				retVal.add(e.getNodeTo());
		}
		return retVal;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AdjacencyEdge<T>> getEdges() 
	{
		return edges;
	}

	/**
	 * 
	 * @param edges
	 */
	public void setEdges(List<AdjacencyEdge<T>> edges) 
	{
		this.edges = edges;
	}

	public List<T> getHorizList() {
		return horiz;
	}

	public List<T> getVertList() {
		return vert;
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		StringBuffer retVal = new StringBuffer("");
		for (T seg : vert)
		{
			retVal.append(seg.toString() + "\n");
			List<T> neighboursLeft = getNeighboursLeft(seg);
			List<T> neighboursRight = getNeighboursRight(seg);
			List<T> neighboursAbove = getNeighboursAbove(seg);
			List<T> neighboursBelow = getNeighboursBelow(seg);
			retVal.append("     Neighbours left: " + neighboursLeft.size() +
					" right: " + neighboursRight.size() +
					" above: " + neighboursAbove.size() +
					" below: " + neighboursBelow.size() + "\n");
		}
		return retVal.toString();
	}
}
