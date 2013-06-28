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
package at.tuwien.prip.model.graph.hier;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.DocumentMatrix;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.utils.DocGraphUtils;

/**
 * DocumentGraphHier.java
 * 
 * A loosely hierarchical document representation.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: May 10, 2011
 */
public class DocHierGraph extends DocumentGraph 
implements IHierGraph<DocNode,DocEdge>, ISegmentGraph
{
	protected List<DocEdgeHier> hEdges;

	protected DocumentGraph baseGraph;
	
	protected MapList<DocNode, DocExpansionEdge> expansionMap;
	protected Map<DocNode,DocContractionEdge> contractionMap;

	//general document info
	protected String documentUri;

	protected int pageCnt;

	/**
	 * Constructor.
	 */
	public DocHierGraph() 
	{
		this.expansionMap = new HashMapList<DocNode,DocExpansionEdge>();
		this.contractionMap = new HashMap<DocNode,DocContractionEdge>();
		this.hEdges = new ArrayList<DocEdgeHier>();
	}

	/**
	 * 
	 * @param dg
	 */
	public void addDocumentGraph (DocumentGraph dg, 
			boolean isBase, boolean recompute) 
	{
		if (isBase) 
		{
			this.baseGraph = dg;
			this.baseGraph.computeDimensions();
		}
		//TODO: add z-index for better visualization
		
		this.nodes.addAll(dg.getNodes());
		this.edges.addAll(dg.getEdges());

		if (dg instanceof DocumentGraph) 
		{
			DocumentGraph d = (DocumentGraph) dg;
			this.nodeSegHash.putAll(d.getNodeSegHash());
			this.segNodeHash.putAll(d.getSegNodeHash());
		}
		
		if (recompute)
		{
			recomputeHEdges(); //hierarchical edges need recomputing...
		}
	}

	/**
	 * Recomputes all hierarchical edges via node 
	 * inclusion detection. Runs quadratic in |nodes|.
	 */
	private void recomputeHEdges ()
	{
		this.expansionMap = new HashMapList<DocNode,DocExpansionEdge>();
		this.contractionMap = new HashMap<DocNode,DocContractionEdge>();
		
		hEdges = new ArrayList<DocEdgeHier>();

		//split nodes into text-fragments, line-fragments, and text-blocks
		List<DocNode> textFragments = new ArrayList<DocNode>();
		List<DocNode> lineFragments = new ArrayList<DocNode>();
		List<DocNode> blockFragments = new ArrayList<DocNode>();
		for (DocNode node : nodes) 
		{
			if (SegmentType.Word.equals(node.getSegType()))
			{ //"text-fragment"
				textFragments.add(node);
			} 
			else if (SegmentType.Textline.equals(node.getSegType())) 
			{ //"line-fragment"
				lineFragments.add(node);
			}
			else if (SegmentType.Block.equals(node.getSegType()))
			{//"text-block"
				blockFragments.add(node);
			}
		}
		
		//sort nodes by volume ascending
		Collections.sort(textFragments, new NodeVolumeComparator());
		Collections.sort(lineFragments, new NodeVolumeComparator());
	
		List<DocNode> tmpNodes = new ArrayList<DocNode>(); //not-found
		
		Iterator<DocNode> textIt = textFragments.iterator();
		while(textIt.hasNext()) 
		{
			boolean found = false;
			DocNode n1 = textIt.next();
			Iterator<DocNode> lineIt = lineFragments.iterator();
			while (lineIt.hasNext()&&!found) 
			{
				DocNode n2 = lineIt.next();
				if (DocGraphUtils.contains(n2, n1)) 
				{
					boolean exists = false;
					if (expansionMap.get(n2)!=null)
					{
						for (DocExpansionEdge dee : expansionMap.get(n2))
						{
							if (dee.getTo().equals(n1)) {
								exists = true;
								break;
							}
						}
					}
					if (!exists) 
					{
						DocExpansionEdge e2 = new DocExpansionEdge(n2, n1);
						expansionMap.putmore(n2, e2); //associate with node
						DocContractionEdge e1 = new DocContractionEdge(n1, n2);
						contractionMap.put(n1, e1);
						
						hEdges.add(e1);
						hEdges.add(e2);
					}
					found = true; //next node
				}
			}
			
			//no node was found
			tmpNodes.add(n1);
		}
		
		//sort nodes by volume ascending
//		lineFragments.addAll(tmpNodes); //add not found nodes
		Collections.sort(lineFragments, new NodeVolumeComparator());
		Collections.sort(blockFragments, new NodeVolumeComparator());
		
		Iterator<DocNode> lineIt = lineFragments.iterator();
		while(lineIt.hasNext()) 
		{
			boolean found = false;
			DocNode n1 = lineIt.next();
			Iterator<DocNode> blockIt = blockFragments.iterator();
			while (blockIt.hasNext()&&!found) 
			{
				DocNode n2 = blockIt.next();
				if (DocGraphUtils.contains(n2, n1)) 
				{
					boolean exists = false;
					if (expansionMap.get(n2)!=null) {
						for (DocExpansionEdge dee : expansionMap.get(n2)) {
							if (dee.getTo().equals(n1)) {
								exists = true;
								break;
							}
						}
					}
					if (!exists) 
					{
						DocExpansionEdge e2 = new DocExpansionEdge(n2, n1);
						expansionMap.putmore(n2, e2); //associate with node
						DocContractionEdge e1 = new DocContractionEdge(n1, n2);
						contractionMap.put(n1, e1);
						
						hEdges.add(e1);
						hEdges.add(e2);
					}
					found = true; //next node
				}
			}
		}
	}

	
	@Override
	public List<DocNode> getContractionFamily(DocNode a) 
	{
		List<DocNode> result = new ArrayList<DocNode>();
		DocNode contracted = contractNode(a);
		List<DocExpansionEdge> edges = expansionMap.get(contracted);
		if (edges!=null) 
		{
			for (DocEdge edge : edges) 
			{
				result.add(edge.getTo());
			}
		}
		return result;
	}

	@Override
	public Rectangle getDimensions() 
	{
		return getBaseGraph().getDimensions();
	}
	
	@Override
	public DocNode contractNode(DocNode a) 
	{
		DocEdge b = contractionMap.get(a);
		if (b==null) 
		{
			return null;
		}
		return (b.getFrom()==a)?b.getTo():b.getFrom();
	}

	@Override
	public List<DocNode> expandNode(DocNode a) 
	{
		List<DocNode> result = new ArrayList<DocNode>();
		List<DocExpansionEdge> edges = expansionMap.get(a);
		if (edges!=null)
		{
			for (DocEdge edge : edges) 
			{
				result.add(edge.getTo());
			}
		}
		return result;
	}
	
	@Override
	public DocHierGraph extractNeighSubGraph(DocNode n)
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
			if (!neighNodes.contains(e.getFrom())) 
			{
				neighNodes.add(e.getFrom());
			}
			if (!neighNodes.contains(e.getTo())) 
			{
				neighNodes.add(e.getTo());
			}
		}
		return getSubGraph(neighNodes);
	}
	
	@Override
	public DocHierGraph extractNeighSubGraph(DocEdge e)
	{
		List<DocNode> neighNodes = new ArrayList<DocNode>();
		DocNode n = e.getFrom();
		if (!neighNodes.contains(n)) {
			neighNodes.add(n); //add self
		}
		List<DocEdge> edges = getEdgesFromTo(n);
		for (DocEdge ed : edges)
		{
			if (!neighNodes.contains(ed.getFrom()))
			{
				neighNodes.add(ed.getFrom());
			}
			if (!neighNodes.contains(ed.getTo()))
			{
				neighNodes.add(ed.getTo());
			}
		}
		n = e.getTo();
		if (!neighNodes.contains(n)) {
			neighNodes.add(n); //add self
		}
		edges = getEdgesFromTo(n);
		for (DocEdge ed : edges)
		{	
			if (!neighNodes.contains(ed.getFrom()))
			{
				neighNodes.add(ed.getFrom());
			}
			if (!neighNodes.contains(ed.getTo()))
			{
				neighNodes.add(ed.getTo());
			}
		}
		return getSubGraph(neighNodes);
	}
	
	/**
	 * Get a subgraph of this graph containing a given set 
	 * of nodes. The full sub-tree in the hierarchy spanning
	 * the given nodes is returned.
	 * 
	 * @param nodes
	 * @return
	 */
	@Override
	public DocHierGraph getSubGraph(List<DocNode> nodes) 
	{
		DocHierGraph result = new DocHierGraph();
		DocumentGraph dg = new DocumentGraph();		
		dg.setNodes(nodes);
		//add edges
		for (DocEdge e : edges)
		{
			if (nodes.contains(e.getFrom()) && nodes.contains(e.getTo())) {
				dg.getEdges().add(e);
			}
		}
		result.addDocumentGraph(dg, true, false);
		
		//copy the full contraction and expansion maps...
		result.contractionMap = contractionMap;
		result.expansionMap = expansionMap;
		
		List<DocNode> visited = new ArrayList<DocNode>();
		Stack<DocNode> open = new Stack<DocNode>();

		
		//also add contraction nodes of each node 
		List<DocNode> cons = new ArrayList<DocNode>();
		for (DocNode n : nodes)
		{
			List<DocNode> conFam = getContractionFamily(n);
			for (DocNode c : conFam)
			{
				if (!cons.contains(c)) 
				{
					cons.add(c);
				}
			}
		}
		for (DocNode con : cons) 
		{
			if (!nodes.contains(con)) 
			{
				nodes.add(con);
			}
		}
		
		open.addAll(nodes);
		while (!open.isEmpty())
		{
			DocNode top = open.pop();
			if (top==null) {
				continue;
			}
			if (visited.contains(top)) {
				continue;
			} 
			visited.add(top);
			
			DocNode contractedNode = contractNode(top);
			if (contractedNode!=null) {
				open.add(contractedNode);
			}
			List<DocNode> expansionList = expandNode(top);
			if (expansionList!=null && expansionList.size()>0) {
				open.addAll(expansionList);
			}
		}
		
		//add edges
		for (DocEdge e : edges)
		{
			if (visited.contains(e.getFrom()) && visited.contains(e.getTo())) 
			{
				result.getEdges().add(e);
			}
		}
		
		//add hierarchical edges		
		for (DocEdgeHier he : hEdges) {
			if (visited.contains(he.getFrom()) && visited.contains(he.getTo())) 
			{
				result.hEdges.add(he);
			}
		}
		
		//add segment cache
		for (DocNode node : visited) 
		{
			GenericSegment segment = this.nodeSegHash.get(node);
			if (segment!=null) 
			{
				result.nodeSegHash.put(node, segment);
				result.segNodeHash.put(segment, node);
			}
		}
		visited.removeAll(nodes);
		result.nodes.addAll(visited); //add remaining nodes...
		result.computeDimensions();
		return result;
	}
	
	@Override
	public List<DocNode> getAllNodes() {
		return nodes;
	}
	
	@Override
	public DocNode getSeedNode() 
	{
		ISegmentGraph activeGraph = getBaseGraph();
		DocumentMatrix dm = DocumentMatrix.newInstance(activeGraph);
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
	public DocumentGraph getBaseGraph() 
	{
		return baseGraph;
	}
	
	/**
	 * 
	 */
	@Override
	public DocHierGraph deepCopy()
	{
		DocHierGraph retVal = new DocHierGraph();
		
		// HashMap from nodes to newly cloned nodes...
		HashMap<DocNode, DocNode> nhm = new HashMap<DocNode, DocNode>();
		for (Object o : nodes)
		{
			DocNode n = (DocNode)o;
			DocNode cln = (DocNode)n.clone();
			retVal.getNodeSegHash().put(cln, nodeSegHash.get(n));
			retVal.getSegNodeHash().put(nodeSegHash.get(n), cln);
			retVal.getNodes().add(cln);
			nhm.put(n, cln);
		}

		for (Object o : edges)
		{
			DocEdge ae = (DocEdge)o;
			DocEdge cae = (DocEdge)ae.clone();
			cae.setFrom(nhm.get(cae.getFrom()));
			cae.setTo(nhm.get(cae.getTo()));
			retVal.getEdges().add(cae);
		}
		
		for (Object o : hEdges)
		{
			DocEdgeHier ae = (DocEdgeHier)o;
			DocEdgeHier cae = (DocEdgeHier)ae.clone();
			cae.setFrom(nhm.get(cae.getFrom()));
			cae.setTo(nhm.get(cae.getTo()));
			retVal.hEdges.add(cae);
		}
		
		//clone expansion and contraction mappings...
		retVal.expansionMap = new HashMapList<DocNode, DocExpansionEdge>();
		for (DocNode n : expansionMap.keySet()) 
		{
			for (DocExpansionEdge dee : expansionMap.get(n)) 
			{
				DocExpansionEdge newEdge = 
					new DocExpansionEdge(nhm.get(dee.getFrom()), nhm.get(dee.getTo()));
				retVal.expansionMap.putmore(nhm.get(n), newEdge);
			}
		}
		retVal.contractionMap = new HashMap<DocNode, DocContractionEdge>();
		for (DocNode n : contractionMap.keySet()) 
		{
			DocContractionEdge dce = contractionMap.get(n);
			DocContractionEdge newEdge = 
				new DocContractionEdge(nhm.get(dce.getFrom()), nhm.get(dce.getTo()));
			retVal.contractionMap.put(nhm.get(n), newEdge);
		}

		retVal.baseGraph = (DocumentGraph) getBaseGraph().deepCopy();
		retVal.computeDimensions();
		return retVal;
	}

	@Override
	public int getSize() 
	{
		return getBaseGraph().getNodes().size();
	}
	
	public void setDocumentURI(String inFile) 
	{
		this.documentUri = inFile;
	}

	public int getNumPages()
	{
		return this.pageCnt;
	}
	
	public void setNumPages(int pgCnt)
	{
		this.pageCnt = pgCnt;
	}

	public List<DocEdgeHier> gethEdges() {
		return hEdges;
	}

	/**
	 * Get the active nodes of the hierarchical graph.
	 * 
	 * @return
	 */
	public List<DocNode> getActiveNodes ()
	{
		List<DocNode> result = new ArrayList<DocNode>();
		for (DocNode node : getBaseGraph().getNodes()) 
		{ //its important to start at the base graph...
			if (contractionMap.get(node)==null || !contractionMap.get(node).isActive)
			{
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * Get the roots of the hierarchical graph.
	 * 
	 * @return
	 */
	public List<DocNode> getRootNodes ()
	{
		List<DocNode> result = new ArrayList<DocNode>();
		for (DocNode node : nodes)
		{
			if (contractionMap.get(node)==null)
			{
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * Get the fringe of the hierarchical graph.
	 * 
	 * @return
	 */
	public List<DocNode> getFringeNodes () 
	{
		List<DocNode> result = new ArrayList<DocNode>();
		for (DocNode node : nodes)
		{
			if (expansionMap.get(node)==null || expansionMap.get(node).size()==0) 
			{
				result.add(node);
			}
		}
		return result;
	}
	
	private class NodeVolumeComparator implements Comparator<DocNode> 
	{

		@Override
		public int compare(DocNode n1, DocNode n2) 
		{
			if (n1.getVolume()==n2.getVolume())
			{
				return 0;
			} 
			else if (n1.getVolume()>n2.getVolume())
			{
				return 1;
			} 
			else 
			{
				return -1;
			}				
		}
		
	}



}//DocumentGraphHier
