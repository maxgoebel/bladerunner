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

import java.util.List;
import java.util.Map;

import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * ISegmentLevel.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 4, 2011
 */
public interface IGraphLevel<S extends BaseNode, T extends BaseEdge<S>>
{
	public int getLevel () ;
	
	public IGraph<S, T> getGraph ();
	
	public void setGraph(IGraph<S, T> graph);
	
	public Map<S, T> getContractionMap();

	public MapList<S, T> getExpansionMap();
	
	public void setContractionMap(Map<S, T> map);
	
	public void setExpansionMap(MapList<S, T> map);
	
	public List<S> getContractionFamily (S n);
	
	public void addNode (S node);
	
}
