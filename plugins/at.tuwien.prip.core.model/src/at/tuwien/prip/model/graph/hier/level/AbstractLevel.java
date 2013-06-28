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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * AbstractLevel.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 5, 2011
 */
public abstract class AbstractLevel<S extends BaseNode, T extends BaseEdge<S>>
{
	protected MapList<S,T> expansionMap = new HashMapList<S,T>();
	protected Map<S,T> contractionMap = new HashMap<S,T>();
	
	protected IGraph<S,T> graph;
	protected LevelGraph<S,T> parent;
	
	public abstract int getLevel();

	public MapList<S, T> getExpansionMap() {
		return expansionMap;
	}
	
	public void addNode (S node) {
		this.graph.addNode(node);
	}
	
	public void setExpansionMap(MapList<S, T> expansionMap) {
		this.expansionMap = expansionMap;
	}

	public Map<S, T> getContractionMap() {
		return contractionMap;
	}

	public void setContractionMap(Map<S, T> contractionMap) {
		this.contractionMap = contractionMap;
	}

	public IGraph<S,T> getGraph() {
		return graph;
	}

	public void setGraph(IGraph<S,T> graph) {
		this.graph = graph;
	}

	public void setParent(LevelGraph<S,T> parent) {
		this.parent = parent;
	}

	public LevelGraph<S,T> getParent() {
		return parent;
	}
	
	/**
	 * By contraction family is understood the set of nodes
	 * that contract to the same parent level node as the
	 * parameter node n. The resulting nodes therefore are
	 * on the same stack level as n.
	 */
	public List<S> getContractionFamily (S n) 
	{
		List<S> result = new ArrayList<S>();
		
		T contractionEdge = contractionMap.get(n);
		if (contractionEdge==null) {
			return result; //node not found on this level...
		}
		S contractedNode = contractionEdge.getTo().equals(n)?contractionEdge.getFrom():contractionEdge.getTo();
		IGraphLevel<S,T> contractedLevel = parent.getLevel(getLevel()+1);
		if (contractedLevel==null) {
			return result; //no higher level available, should be caught above
		}
		List<T> edges = contractedLevel.getExpansionMap().get(contractedNode);
		for (T edge : edges) {
			result.add(edge.getTo());
		}
		return result;
	}
	
}//AbstractLevel
