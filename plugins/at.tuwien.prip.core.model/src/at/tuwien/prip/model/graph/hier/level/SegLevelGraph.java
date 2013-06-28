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
package at.tuwien.prip.model.graph.hier.level;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentConstants;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.hier.ISegHierGraph;
import at.tuwien.prip.model.utils.DocGraphUtils;

/**
 * SegLevelStack.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 4, 2011
 */
public class SegLevelGraph extends LevelGraph<DocNode,DocEdge> 
implements ISegHierGraph, ISegmentGraph
{
	private String documentURI;

	private int numPages = 0;

	private Rectangle dimensions;
	
	private boolean isFlipReversed = false;

	protected HashMap<GenericSegment, DocNode> segNodeHash;
	protected HashMap<DocNode, GenericSegment> nodeSegHash;


	/**
	 * Constructor
	 */
	public SegLevelGraph() 
	{
		super();

		defaultLevel = DocumentConstants.WORD_LEVEL;
		
		this.segNodeHash = new HashMap<GenericSegment, DocNode>();
		this.nodeSegHash = new HashMap<DocNode, GenericSegment>();

		this.dimensions = new Rectangle();
	}

	public SegLevelGraph(ISegmentGraph baseGraph)
	{
		this();

		CustomLevel level = new CustomLevel(0, this);
		level.setGraph(baseGraph);
		addLevel(level);
	}

	public ISegmentGraph getBaseGraph() 
	{
		return (ISegmentGraph) super.getBaseGraph();
	}


	@Override
	public SegLevelGraph getSubGraph(DocNode[] neighNodes) 
	{
		return getSubGraph(ListUtils.toList(neighNodes, DocNode.class));
	}

	@Override
	public double getLineSpaceing() {
		return getBaseGraph().getLineSpaceing();
	}
	
	/**
	 * Get the subtree made up from a list of given nodes.
	 * 
	 * @param nodes
	 * @return
	 */
	public SegLevelGraph getSubGraph(List<DocNode> nodes)
	{
		SegLevelGraph result = new SegLevelGraph();

		Stack<DocNode> open = new Stack<DocNode>();
		List<DocNode> visited = new ArrayList<DocNode>();
		MapList<Integer,DocNode> level2NodeMap = new HashMapList<Integer, DocNode>();

		open.addAll(nodes);
		while (!open.isEmpty()) 
		{
			DocNode node = open.pop();
			visited.add(node);
			int level = node2LevelMap.get(node);
			level2NodeMap.putmore(level, node);

			//check contraction and expansion nodes
			DocNode above = contractNode(node);
			if (above!=null && !visited.contains(above) && !open.contains(above)) {
				open.add(above);
			}
			List<DocNode> belows = expandNode(node);
			for (DocNode below : belows) {
				if (below!=null && !visited.contains(below) && !open.contains(below)) {
					open.add(below);
				}	
			}
		}

		for (int key : level2NodeMap.keySet()) 
		{
			DocumentGraph dg = new DocumentGraph();
			List<DocNode> levelNodes = level2NodeMap.get(key);
			dg.setNodes(levelNodes);
			for (DocNode node : dg.getNodes()) 
			{
				GenericSegment segment = this.nodeSegHash.get(node);
				if (segment!=null) {
					dg.getNodeSegHash().put(node, segment);
					dg.getSegNodeHash().put(segment, node);
				}
			}
			for (Object o : getAllEdges())
			{
				DocEdge e = (DocEdge)o;
				if (levelNodes.contains(e.getFrom()) && levelNodes.contains(e.getTo())) {
					dg.getEdges().add(e);
				}
			}
			dg.computeDimensions();

			IGraphLevel<DocNode,DocEdge> level = null;
			switch (key)
			{
			case DocumentConstants.TEXTBLOCK_LEVEL:

				level = new TextBlockLevel(result);
				level.setGraph(dg);
				break;

			case DocumentConstants.TEXTLINE_LEVEL:

				level = new TextLineLevel(result);
				level.setGraph(dg);
				break;

			case DocumentConstants.WORD_LEVEL:

				level = new WordLevel(result);
				level.setGraph(dg);
				break;

			case DocumentConstants.CHAR_LEVEL:

				level = new CharLevel(result);
				level.setGraph(dg);
				break;

			default:
				break;
			}

			if (level!=null) {
				result.addLevel(level);	
			}

			result.computeDimensions();
		}

		result.contractionCache = new HashMap<DocNode, DocNode>(contractionCache);
		result.contractionFamily = new HashMapList<DocNode, DocNode>();
		for (DocNode node : result.getAllNodes()) 
		{
			List<DocNode> conFam = contractionFamily.get(node);
			if (conFam!=null && conFam.size()>0) {
				result.contractionFamily.putmore(node, conFam);
			}
		}
		return result;
	}

	/**
	 * Generate an expansion/contraction mapping between 
	 * two segmentation levels.
	 * 
	 * @param g1
	 * @param g2
	 */
	protected void map(IGraphLevel<DocNode, DocEdge> g1, IGraphLevel<DocNode,DocEdge> g2) 
	{
		MapList<DocNode, DocEdge> expansionMap = new HashMapList<DocNode, DocEdge>();
		Map<DocNode,DocEdge> contractionMap = new HashMap<DocNode,DocEdge>();

		IGraphLevel<DocNode,DocEdge> upper = null, lower = null;
		if (g1.getLevel()>g2.getLevel()) {
			upper = g1; lower = g2;
		} else {
			upper = g2; lower = g1;
		}

		List<DocNode> lVisited = new ArrayList<DocNode>();
		List<DocNode> uVisited = new ArrayList<DocNode>();

		Iterator<DocNode> upperIt = new ArrayList<DocNode>(upper.getGraph().getNodes()).iterator();
		while (upperIt.hasNext())
		{
			DocNode uNode = upperIt.next();
			if (uVisited.contains(uNode)) {
				continue;
			}

			Iterator<DocNode> lowerIt = lower.getGraph().getNodes().iterator();
			while (lowerIt.hasNext()) 
			{
				DocNode lNode = lowerIt.next();
				if (lVisited.contains(lNode)) 
				{
					continue;
				}

				if (DocGraphUtils.contains(uNode, lNode)) {

					//if (SegmentUtils.intersects(uNode, lNode)) {

					//found an intersection
					DocEdge e1 = new DocEdge(uNode, lNode);
					DocEdge e2 = new DocEdge(lNode, uNode);
					expansionMap.putmore(uNode, e1); //associate with node
					contractionMap.put(lNode, e2);
					lVisited.add(lNode);
					uVisited.add(uNode);
				}

			}//end while
		}//end for

		//update contraction edges
		lower.getContractionMap().clear();
		lower.setContractionMap(contractionMap);

		//update expansion edges
		upper.getExpansionMap().clear();
		upper.setExpansionMap(expansionMap);
	}

	/**
	 * Add a node to a given level of this graph.
	 * @param node
	 * @param level
	 */
	public void addNode(DocNode node, int level) 
	{
		nodes.add(node);
		
		
		/* add node to appropriate level */
		IGraphLevel<DocNode, DocEdge> lv = getLevel(level);
		if (lv==null) 
		{
			lv = new CustomLevel(level, this);
			lv.setGraph(new DocumentGraph());
			addLevel(lv);
		}
		lv.addNode(node);
		
		/* recompute expansion and contractions for level */
//		recomputeHGraph(lv);
		
		node2LevelMap.put(node, level);
	}
	
	/**
	 * 
	 */
	@Override
	public void addLevel(IGraphLevel<DocNode, DocEdge> level) 
	{
		super.addLevel(level);

		//also remember segment mapping
		ISegmentGraph segGraph = (ISegmentGraph) level.getGraph();
		if (!stack.keySetB().contains(segGraph) && 
				!stack.keySetA().contains(segGraph)) 
		{
			for(DocNode n : segGraph.getNodes()) 
			{

				GenericSegment seg = segGraph.getSegment(n);
				this.segNodeHash.put(seg, n);
				this.nodeSegHash.put(n, seg);
			}
		}

		//and also recompute dimension
		Rectangle newDims = segGraph.getDimensions();
		if (this.dimensions==null) {
			this.dimensions = new Rectangle(newDims);
		} else {
			if (newDims.x<dimensions.x) {
				dimensions.x=newDims.x;
			}
			if (newDims.y>dimensions.y) {
				dimensions.y=newDims.y;
			}
			if (newDims.width>dimensions.width) {
				dimensions.width=newDims.width;
			}
			if (newDims.height>dimensions.height) {
				dimensions.height=newDims.height;
			}
		}
		recomputeHGraph(level);
	}

	/**
	 * Recompute the hierarchical edges for a given level.
	 * 
	 * @param level
	 */
	protected void recomputeHGraph (IGraphLevel<DocNode,DocEdge> level) 
	{
		int higherLevelIdx = getHigherLevel(level);
		int lowerLevelIdx = getLowerLevel(level);

		IGraphLevel<DocNode,DocEdge> upper = stack.get(higherLevelIdx);
		IGraphLevel<DocNode,DocEdge> lower = stack.get(lowerLevelIdx);

		if (upper!=null) {
			map(upper, level);
		} if (lower!=null) {
			map(level, lower);
		}		
	}

	/**
	 * Precompute a cache for contraction operations on the segmentation
	 * hierarchy.
	 * 
	 * @param stack
	 */
	public void precomputeContractionMaps () 
	{
		this.contractionCache = new HashMap<DocNode, DocNode>();
		this.contractionFamily = new HashMapList<DocNode, DocNode>();

		SimpleTimer timer = new SimpleTimer();
		timer.startTask(1);
		for (DocNode n : getAllNodes()) 
		{
			int idx = getNode2LevelMap().get(n);
			IGraphLevel<DocNode,DocEdge> level = getLevel(idx);
			DocEdge conEdge = level.getContractionMap().get(n);
			if (conEdge!=null) {
				contractionCache.put(n, conEdge.getTo());
			}
			List<DocNode> conNodes = level.getContractionFamily(n);				
			for (DocNode conNode : conNodes) {
				this.contractionFamily.putmore(n, conNode);
			}
		}
		timer.stopTask(1);
		ErrorDump.debug(this, "Contraction map precomputed in " + 
				timer.getTimeMinutesSecondsMillis(1));
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public boolean isFlipReversed() {
		return isFlipReversed;
	}

	public void setFlipReversed(boolean isFlipReversed) {
		this.isFlipReversed = isFlipReversed;
	}

	public Map<DocNode, Integer> getNode2LevelMap() {
		return node2LevelMap;
	}

	public void setNode2LevelMap(Map<DocNode, Integer> node2LevelMap) 
	{
		this.node2LevelMap = node2LevelMap;
	}

	/**
	 * 
	 * @return
	 */
	public String getDocumentURI() 
	{
		return documentURI;
	}

	public void setDocumentURI(String documentURI)
	{
		this.documentURI = documentURI;
	}

	@Override
	public Rectangle getDimensions() 
	{
		return getBaseGraph().getDimensions();
//		return dimensions;
	}

	@Override
	public void computeDimensions()
	{
		getBaseGraph().computeDimensions();
		this.dimensions = getBaseGraph().getDimensions();
	}

	@Override
	public ISegmentGraph deepCopy()
	{
		SegLevelGraph retVal = new SegLevelGraph();
		for(int level : stack.keySetA()) 
		{
			IGraphLevel<DocNode,DocEdge> newLevel = null;
			switch (level) 
			{
			case DocumentConstants.TEXTBLOCK_LEVEL:

				newLevel = new TextBlockLevel(retVal);
				break;

			case DocumentConstants.TEXTLINE_LEVEL:

				newLevel = new TextLineLevel(retVal);
				break;

			case DocumentConstants.WORD_LEVEL:

				newLevel = new WordLevel(retVal);
				break;

			case DocumentConstants.CHAR_LEVEL:

				newLevel = new CharLevel(retVal);
				break;

			default:
				break;
			}

			if (newLevel!=null) 
			{
				IGraphLevel<DocNode,DocEdge> segLev = stack.get(level);
				newLevel.setGraph((ISegmentGraph) segLev.getGraph().deepCopy());
				retVal.addLevel(newLevel);	
			}
		}

		return retVal;
	}

	@Override
	public GenericSegment getSegment(DocNode n)
	{
		return nodeSegHash.get(n);
	}

	@Override
	public Map<DocNode, GenericSegment> getNodeSegHash() 
	{
		return this.nodeSegHash;
	}

	@Override
	public Map<GenericSegment, DocNode> getSegNodeHash() 
	{
		return this.segNodeHash;
	}

	@Override
	public void removeNode(DocNode node)
	{
		if (!this.nodes.contains(node))
		{
			this.nodes.add(node);
		}
	}

	@Override
	public String serializeText() {
		return getBaseGraph().serializeText();
	}

	@Override
	public ISegmentGraph getSubGraph(List<DocNode> neighNodes, boolean edges) {
		// TODO Auto-generated method stub
		return null;
	}

}//SegLevelStack
