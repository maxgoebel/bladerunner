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

import java.util.List;

import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * IHierGraph.java
 * 
 * An interface for hierarchical graph representations.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: May 10, 2011
 */
public interface IHierGraph<S extends BaseNode, T extends BaseEdge<S>> 
extends IGraph<S,T>
{
	public List<S> getAllNodes();
	
	public IGraph<S,T> getBaseGraph ();
	
	public List<S> getContractionFamily(S a);
	
	public S contractNode (S a);
	
	public List<S> expandNode (S a);
	
	public IHierGraph<S,T> extractNeighSubGraph(T e);
	
	public IHierGraph<S,T> extractNeighSubGraph(S n);
	
}
