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

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import at.tuwien.prip.common.datastructures.BidiMap;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.base.BaseGraph;
import at.tuwien.prip.model.graph.comparators.DocEdgeComparator;
import at.tuwien.prip.model.utils.DocGraphUtils;
import at.tuwien.prip.model.utils.Orientation;

/**
 * The document graph
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class DocumentGraph extends BaseGraph<DocNode, DocEdge>
implements ISegmentGraph, Cloneable 
{
	protected HashMap<GenericSegment, DocNode> segNodeHash;
	protected HashMap<DocNode, GenericSegment> nodeSegHash;

	protected Rectangle dimensions;

	private int numPages = 0;

	protected AdjacencyGraph<?> adjGrap;

	protected double lineSpaceing;

	/**
	 * Constructor.
	 * 
	 * initializes a blank document graph
	 * 
	 */
	public DocumentGraph()
	{
		super();
		this.dimensions = new Rectangle();
		segNodeHash = new HashMap<GenericSegment, DocNode>();
		nodeSegHash = new HashMap<DocNode, GenericSegment>();
	}

	/**
	 * Constructor.
	 * 
	 * @param ag
	 */
	public DocumentGraph(AdjacencyGraph<?> ag)
	{
		this();

		this.adjGrap = ag;

		int minX=Integer.MAX_VALUE, maxX=0, minY=Integer.MAX_VALUE, maxY=0;

		//		ErrorDump.debug(this, "in AG with nodes: " + ag.getVertList().size() + " and edges: " + ag.getEdges().size());

		for (Object o : ag.getVertList())
		{
			GenericSegment gs = (GenericSegment)o;	// MUST be a GenericSegment

			DocNode n = new DocNode(gs);
			//			if (n.getBoundingBox().getMinY()<100)
			//			{
			//				System.out.println();
			//			}
			if (gs.getSegType()!=null) {
				n.setSegType(gs.getSegType());
			}

			nodes.add(n);
			//			addNode(n);
			segNodeHash.put(gs, n);
			nodeSegHash.put(n, gs);

			//adjust dimensions if necessary
			if (n.getSegX1()<minX) {
				minX = (int) n.getSegX1();
			}
			if (n.getSegX2()>maxX) {
				maxX = (int) n.getSegX2();
			}
			if (n.getSegY1()<minY) {
				minY = (int) n.getSegY1();
			}
			if (n.getSegY2()>maxY) {
				maxY = (int) n.getSegY2();
			}
		}

		this.dimensions = new Rectangle(minX, minY, maxX-minX, maxY-minY);

		for (AdjacencyEdge<?> ae : ag.getEdges())
		{
			GenericSegment segFrom = ae.getNodeFrom();
			GenericSegment segTo = ae.getNodeTo();
			DocNode nodeFrom = segNodeHash.get(segFrom);
			DocNode nodeTo = segNodeHash.get(segTo);

			if (ae.getDirection() == AdjacencyEdge.REL_RIGHT ||
					ae.getDirection() == AdjacencyEdge.REL_ABOVE) //getEdgesFrom
			{
				DocEdge atr = new DocEdge(ae, nodeFrom, nodeTo, this); 
				edges.add(atr);

				/* add edges to cache */
				edgeFromCache.putmore(nodeFrom, atr);
				edgeToCache.putmore(nodeTo, atr);
			}
		}

		//		calculateLineSpacing();

		//		ErrorDump.debug(this, "creating DG with nodes: " + nodes.size() + " edges: " + edges.size());
	}

	//	private void calculateLineSpacing() 
	//	{
	//		StatMap<Integer> statMap = new StatMap<Integer>();
	//		for (DocEdge edge : edges)
	//		{
	//			if (edge.getRelation()==EdgeConstants.ADJ_ABOVE ||
	//					edge.getRelation()==EdgeConstants.ADJ_BELOW)
	//			{
	//				statMap.increment((int)edge.getLength());
	//			}
	//		}
	//		this.lineSpaceing = statMap.getByStartFrequency().get(0);//getSignificant(1).get(0);
	//	}

	public double getLineSpaceing() {
		return lineSpaceing;
	}

	/**
	 * Make a graph planar by removing intersecting edges.
	 */
	public void makePlanar () 
	{
		List<Line2D> intersects = new ArrayList<Line2D>();

		/* convert all edges to lines */
		BidiMap<Line2D,DocEdge> linesMap = new BidiMap<Line2D, DocEdge>();
		List<Line2D> horizontal = new ArrayList<Line2D>();
		List<Line2D> vertical = new ArrayList<Line2D>();
		List<DocEdge> edges = getEdges();
		for (DocEdge edge : edges) 
		{
			if (edge.getOrientation().equals(Orientation.HORIZONTAL)) 
			{
				Line2D line = new Line2D.Float(
						edge.getFrom().getSegX1() + 1,
						edge.getFrom().getSegY1(),
						edge.getTo().getSegX2() + 1,
						edge.getTo().getSegY2());
				linesMap.put(line, edge);
				horizontal.add(line);
			}
			else if (edge.getOrientation().equals(Orientation.VERTICAL)) 
			{
				Line2D line = new Line2D.Float(
						edge.getFrom().getSegX1(),
						edge.getFrom().getSegY1() + 1,
						edge.getTo().getSegX2(),
						edge.getTo().getSegY2() + 1);
				linesMap.put(line, edge);
				vertical.add(line);
			}
		}

		Iterator<Line2D> hIt = horizontal.iterator();
		while (hIt.hasNext())
		{
			Line2D hor = hIt.next();		
			Iterator<Line2D> vIt = vertical.iterator();
			while (vIt.hasNext())
			{
				Line2D ver = vIt.next();
				if (hor.intersectsLine(ver)) 
				{
					if (hor.getP1().distance(ver.getP1())<5 ||
							hor.getP2().distance(ver.getP2())<5) 
					{
						continue;
					}
					vIt.remove();
					intersects.add(ver);			
				}
			}
		}

		/* finally, remove intersections from edges */
		for (Line2D line : intersects) 
		{
			DocEdge e = linesMap.get(line);
			edges.remove(e);
		}
	}

	/**
	 * Remove from this graph all vertical edges that skip segments vertically.
	 */
	public void toMinimalEdgeGraph ()
	{
		for (DocNode node : getNodes()) 
		{
			List<DocEdge> southEdges = new ArrayList<DocEdge>();
			List<DocEdge> eastEdges = new ArrayList<DocEdge>();

			List<DocEdge> nodeEdges = getEdgesFrom(node);//edgeMap.get(node);
			if (nodeEdges==null) continue;

			for (DocEdge edge : nodeEdges) 
			{
				if (edge.getRelation()==EdgeConstants.ADJ_BELOW)
				{
					southEdges.add(edge);	
				}
				if (edge.getRelation()==EdgeConstants.ADJ_RIGHT)
				{
					eastEdges.add(edge);	
				}
			}

			/* check south edges */
			if (southEdges.size()>1) 
			{
				Collections.sort(southEdges, new DocEdgeComparator());
				Collections.reverse(southEdges);

				List<DocEdge> removeList = new ArrayList<DocEdge>();

				Iterator<DocEdge> it = southEdges.iterator();
				DocEdge front = it.next();
				while (it.hasNext()) 
				{
					DocEdge next = it.next();
					DocNode nextTo = next.getTo();
					DocNode frontTo = front.getTo();
					if (DocGraphUtils.isReachable(nextTo, frontTo, this, Orientation.VERTICAL))
					{
						removeList.add(front);
						front = next;
					}
				}

				for (DocEdge edge : removeList) {
					this.edges.remove(edge);
				}
			}

			/* check east edges */
			if (eastEdges.size()>1) 
			{
				Collections.sort(eastEdges, new DocEdgeComparator());
				Collections.reverse(eastEdges);

				List<DocEdge> removeList = new ArrayList<DocEdge>();

				Iterator<DocEdge> it = eastEdges.iterator();
				DocEdge front = it.next();
				while (it.hasNext()) 
				{
					DocEdge next = it.next();
					DocNode nextTo = next.getTo();
					DocNode frontTo = front.getTo();
					if (DocGraphUtils.isReachable(nextTo, frontTo, this, Orientation.HORIZONTAL))
					{
						removeList.add(front);
						front = next;
					}
				}

				for (DocEdge edge : removeList) {
					this.edges.remove(edge);
				}
			}
		}
	}

	//	/**
	//	 * get the connected components of this graph.
	//	 */
	//	public List<List<DocNode>> getConnectedComponents () 
	//	{
	//		List<List<DocNode>> result = new ArrayList<List<DocNode>>();
	//		List<DocNode> visited = new ArrayList<DocNode>();
	//
	//		for (DocNode node : getNodes()) 
	//		{
	//			if (visited.contains(node)) continue;
	//
	//			visited.add(node);
	//
	//			//start a new component
	//			List<DocNode> component = new ArrayList<DocNode>();
	//
	//			Queue<DocNode> queue = new ArrayDeque<DocNode>();
	//			queue.add(node);
	//
	//
	//			while (!queue.isEmpty()) 
	//			{
	//				DocNode head = queue.remove();
	//				for (DocEdge edge : getEdgesFromTo(head)) {
	//					DocNode to = null;
	//					if (edge.getFrom().equals(head)) {
	//						to = edge.getTo();
	//					} else {
	//						to = edge.getFrom();
	//					}
	//					if (!visited.contains(to)) {
	//						visited.add(to);
	//						queue.add(to);
	//						component.add(to);
	//					}
	//				}
	//			}
	//
	//			result.add(component);
	//		}
	//
	//
	//		return result;
	//	}

	/**
	 * Compute the dimensions of this graph.
	 */
	public void computeDimensions () 
	{
		int minX=Integer.MAX_VALUE, maxX=0, minY=Integer.MAX_VALUE, maxY=0;
		for (DocNode n : nodes) 
		{
			//adjust dimensions if necessary

			if (n.getSegX1()<minX) {
				minX = (int) n.getSegX1();
			}
			if (n.getSegX2()>maxX) {
				maxX = (int) n.getSegX2();
			}
			if (n.getSegY1()<minY) {
				minY = (int) n.getSegY1();
			}
			if (n.getSegY2()>maxY) {
				maxY = (int) n.getSegY2();
			}
		}
		this.dimensions = new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void setNodes(List<DocNode> nodes) 
	{
		this.nodes = nodes;

		int minX=Integer.MAX_VALUE, maxX=0, minY=Integer.MAX_VALUE, maxY=0;
		for (DocNode n : nodes) {
			//adjust dimensions if necessary
			if (n.getSegX1()<minX) {
				minX = (int) n.getSegX1();
			}
			if (n.getSegX2()>maxX) {
				maxX = (int) n.getSegX2();
			}
			if (n.getSegY1()<minY) {
				minY = (int) n.getSegY1();
			}
			if (n.getSegY2()>maxY) {
				maxY = (int) n.getSegY2();
			}
		}
		this.dimensions = new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}



	//	public List<DocEdge> getEdgesTo2 (DocNode n) 
	//	{
	//		return edgeToCache.get(n);
	//	}
	//	
	//	public List<DocEdge> getEdgesTo(DocNode n)
	//	{
	//		//shortcut
	//		if (edgeToCache.size()>0) 
	//		{
	//			return getEdgesTo2(n);
	//		}
	//		
	//		List<DocEdge> retVal = new ArrayList<DocEdge>();
	//		for (Object o : edges)
	//		{
	//			DocEdge e = (DocEdge)o;
	//			if (e.getTo() == n)
	//				retVal.add(e);
	//		}
	//		return retVal;
	//	}



	public void setEdges(List<DocEdge> edges) {
		this.edges = edges;
	}

	public DocNode getNode(GenericSegment gs)
	{
		return segNodeHash.get(gs);
	}

	public GenericSegment getSegment(DocNode n)
	{
		return nodeSegHash.get(n);
	}

	@Override
	public DocumentGraph getSubGraph(DocNode[] neighNodes) 
	{
		return getSubGraph(ListUtils.toList(neighNodes, DocNode.class));
	}

	/**
	 * Get the subtree made up from a list of given nodes.
	 * 
	 * @param nodes
	 * @return
	 */
	public DocumentGraph getSubGraph(List<DocNode> nodes)
	{
		DocumentGraph retVal = new DocumentGraph();
		retVal.nodes.addAll(nodes);
		for (DocNode node : nodes) 
		{
			GenericSegment segment = this.nodeSegHash.get(node);
			if (segment!=null) {
				retVal.nodeSegHash.put(node, segment);
				retVal.segNodeHash.put(segment, node);
			}
		}
		retVal.computeDimensions();

		for (Object o : edges)
		{
			DocEdge e = (DocEdge)o;
			if (nodes.contains(e.getFrom()) && nodes.contains(e.getTo())) 
			{
				retVal.edgeToCache.putmore(e.getFrom(), e);
				retVal.edgeToCache.putmore(e.getTo(), e);
				retVal.edges.add(e);
			}
		}
		return retVal;
	}

//	/**
//	 * Get the subtree made up from a list of given nodes.
//	 * 
//	 * @param nodes
//	 * @return
//	 */
//	public DocumentGraph getSubGraph(List<DocNode> nodes, boolean createEdges)
//	{
//		DocumentGraph retVal = new DocumentGraph();
//		if (createEdges)
//		{
//			List<GenericSegment> segments = new ArrayList<GenericSegment>();
//			for (DocNode node : nodes)
//			{
//				GenericSegment gs = nodeSegHash.get(node);
//				if (gs!=null)
//				{
//					segments.add(gs);
//				}
//			}
//			AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(segments);
//			AdjacencyGraph<GenericSegment> ag = am.toAdjacencyGraph(true);
//			retVal = new DocumentGraph(ag);
//		}
//		else
//		{
//			retVal.nodes.addAll(nodes);
//			for (DocNode node : nodes) 
//			{
//				GenericSegment segment = this.nodeSegHash.get(node);
//				if (segment!=null) {
//					retVal.nodeSegHash.put(node, segment);
//					retVal.segNodeHash.put(segment, node);
//				}
//			}
//			retVal.computeDimensions();
//
//			for (Object o : edges)
//			{
//				DocEdge e = (DocEdge)o;
//				if (nodes.contains(e.getFrom()) && nodes.contains(e.getTo())) 
//				{
//					retVal.edgeToCache.putmore(e.getFrom(), e);
//					retVal.edgeToCache.putmore(e.getTo(), e);
//					retVal.edges.add(e);
//				}
//			}
//		}
//
//		return retVal;
//	}

	/**
	 * Get the subtree made up from a list of given nodes.
	 * 
	 * @param nodes
	 * @return
	 */
	public DocumentGraph getSubGraph(List<DocNode> nodes, boolean createEdges)
	{
		DocumentGraph retVal = new DocumentGraph();

		List<GenericSegment> segments = new ArrayList<GenericSegment>();
		for (DocNode node : nodes)
		{
			GenericSegment gs = nodeSegHash.get(node);
			if (gs!=null)
			{
				segments.add(gs);
			}
		}

		AdjacencyGraph<GenericSegment> ag = new AdjacencyGraph<GenericSegment>();
		ag.addList(segments);
		if (createEdges)
		{
			ag.generateEdgesSingle();
		}
		retVal = new DocumentGraph(ag);

		for (DocNode node : nodes) 
		{
			GenericSegment segment = this.nodeSegHash.get(node);
			if (segment!=null) {
				retVal.nodeSegHash.put(node, segment);
				retVal.segNodeHash.put(segment, node);
			}
		}
		retVal.computeDimensions();

		for (Object o : edges)
		{
			DocEdge e = (DocEdge)o;
			if (nodes.contains(e.getFrom()) && nodes.contains(e.getTo())) 
			{
				retVal.edgeToCache.putmore(e.getFrom(), e);
				retVal.edgeToCache.putmore(e.getTo(), e);
				retVal.edges.add(e);
			}
		}
		return retVal;
	}
	
	/**
	 * In addition to adding the node, edges are also recomputed...
	 * @param node, the node to be added to his graph
	 */
	@Override
	public void addNode(DocNode node)
	{
		if (nodes.contains(node)) return;

		super.addNode(node);

		if (nodeSegHash.get(node)==null) {
			GenericSegment gs = new GenericSegment(node.getBoundingBox().getBounds(), node.getSegType());
			nodeSegHash.put(node, gs);
			segNodeHash.put(gs, node);
		}
		List<GenericSegment> segments = new ArrayList<GenericSegment>();
		for (DocNode n : nodes) {
			GenericSegment segment = nodeSegHash.get(n);
			if (!segNodeHash.containsKey(segment)) {
				segNodeHash.put(segment, n);
			}
			segments.add(segment);
		}

		this.dimensions = this.dimensions.union(node.getBoundingBox().getBounds());
	}
	
	/**
	 * 
	 * @param node
	 */
	public void addNodeWithNeighbours (DocNode node)
	{
		if (nodes.contains(node)) return;

		super.addNode(node);

		if (nodeSegHash.get(node)==null) {
			GenericSegment gs = new GenericSegment(node.getBoundingBox().getBounds(), node.getSegType());
			nodeSegHash.put(node, gs);
			segNodeHash.put(gs, node);
		}
		List<GenericSegment> segments = new ArrayList<GenericSegment>();
		for (DocNode n : nodes) {
			GenericSegment segment = nodeSegHash.get(n);
			if (!segNodeHash.containsKey(segment)) {
				segNodeHash.put(segment, n);
			}
			segments.add(segment);
		}

		
		//recompute neighbors
		AdjacencyMatrix<GenericSegment> am = new AdjacencyMatrix<GenericSegment>(segments);
		AdjacencyGraph<GenericSegment> ag = am.toAdjacencyGraph(true);
		DocumentGraph dg = new DocumentGraph(ag);
		List<DocEdge> newEdges = new ArrayList<DocEdge>();
		for (DocEdge edge : dg.getEdges()) 
		{
			DocNode from = segNodeHash.get(dg.nodeSegHash.get(edge.getFrom()));
			DocNode to = segNodeHash.get(dg.nodeSegHash.get(edge.getTo()));
			DocEdge e = new DocEdge(from, to);
			e.setRelation(edge.getRelation());
			newEdges.add(edge);
		}
		this.edges = newEdges;
		this.dimensions = this.dimensions.union(node.getBoundingBox().getBounds());
	}

	public DocumentGraph deepCopy()
	{
		DocumentGraph retVal = new DocumentGraph();
		retVal.edgeFromCache =  new HashMapList<DocNode, DocEdge>();
		retVal.edgeToCache = new HashMapList<DocNode, DocEdge>();

		// HashMap from nodes to newly cloned nodes...
		HashMap<DocNode, DocNode> nhm = new HashMap<DocNode, DocNode>();
		for (DocNode n : nodes)
		{
			DocNode cln = (DocNode)n.clone();

			/* the generic segment reference */
			GenericSegment gs = nodeSegHash.get(n);
			if (gs!=null) {
				retVal.getNodeSegHash().put(cln, gs.clone());
				retVal.getSegNodeHash().put(gs.clone(), cln);
			}
			retVal.getNodes().add(cln);
			nhm.put(n, cln);
		}

		for (DocEdge ae : edges)
		{
			DocEdge cae = (DocEdge)ae.clone();
			cae.setFrom(nhm.get(cae.getFrom()));
			cae.setTo(nhm.get(cae.getTo()));
			retVal.getEdges().add(cae);

			/* the edge cache */
			retVal.edgeFromCache.putmore(cae.getFrom(), cae);
			retVal.edgeToCache.putmore(cae.getTo(), cae);
		}

		retVal.computeDimensions();
		return retVal;
	}

	public Rectangle getDimensions() {
		return dimensions;
	}

	public void setDimensions(Rectangle dimensions) {
		this.dimensions = dimensions;
	}

	public HashMap<GenericSegment, DocNode> getSegNodeHash() {
		return segNodeHash;
	}

	public HashMap<DocNode, GenericSegment> getNodeSegHash() {
		return nodeSegHash;
	}

	public DocNode getSeedNode() {
		DocumentMatrix dm = DocumentMatrix.newInstance(nodes);
		return dm.iterator().next();
	}

	@Override
	public DocEdge getSeedEdge() 
	{
		List<DocEdge> edges = getEdges();
		Random rand = new Random();
		return edges.get(rand.nextInt(edges.size()));
	}

	@Override
	public DocumentGraph extractNeighSubGraph(DocNode n) 
	{
		List<DocNode> neighNodes = new ArrayList<DocNode>();
		int maxLength = Math.max(getDimensions().height, getDimensions().width);
		neighNodes.add(n);
		List<DocEdge> edges = getEdgesFromTo(n);
		for (DocEdge e : edges) 
		{
			if (e.getLength()>=maxLength) {
				continue;
			}
			if (!neighNodes.contains(e.getFrom())) {
				neighNodes.add(e.getFrom());
			}
			if (!neighNodes.contains(e.getTo())) {
				neighNodes.add(e.getTo());
			}
		}
		return getSubGraph(neighNodes);
	}

	public String serializeText () 
	{
		DocumentMatrix dm = DocumentMatrix.newInstance(this);
		return dm.serializeText();
	}

	@Override
	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	//	public Rectangle getDocumentDimensions() {
	//		if (documentDimensions==null) 
	//		{
	//			 computeDimensions();
	//			 documentDimensions = dimensions;
	//		}
	//		return documentDimensions;
	//	}
	//
	//	public void setDocumentDimensions(Rectangle documentDimensions) {
	//		this.documentDimensions = documentDimensions;
	//	}

	/*
	public String toString()
	{
		StringBuffer vertices = new StringBuffer("");
		StringBuffer edges = new StringBuffer("");
		// output all vertices (think go through vert)
		for (DocNode n : nodes)
		{
			// vertices.append("\"");
			vertices.append("" + n + " " + "\"text=\'" + n.getSegText()
				+ "\' " + "x1=" + n.getSegX1() + " x2="
				+ n.getSegX2() + " y1=" + n.getSegY1() + " y2="
				+ n.getSegY2() + "\"\n");

			EdgeList neighbours = getEdges(n); // thisNode.getNeighbours();

			Iterator eIter = neighbours.iterator();

			while (eIter.hasNext())
			{
				AttributedEdge e2 = (AttributedEdge) eIter.next();
				GenericSegment node2 = e2.getNodeTo();
				// GenericSegment temp2 = node2.getSegment();

				if (node2 instanceof TextSegment)
				{
					TextSegment thisNeighbour = (TextSegment) node2;
					edges.append("" + n + " " + vert.indexOf(node2) + " "
						+ e2.getWeight() + "\n");
				}
			}
		}

		return "*Vertices\n" + vertices.toString() + "\n*Edges\n"
			+ edges.toString();
	}
	 */

}
