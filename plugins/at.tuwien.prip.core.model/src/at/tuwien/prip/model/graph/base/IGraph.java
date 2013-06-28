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
package at.tuwien.prip.model.graph.base;

import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;

/**
 * IGraph.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jul 3, 2011
 */
public interface IGraph<S extends BaseNode, T extends BaseEdge<S>>
{
	public Rectangle getDimensions();
	
	public int getSize();
	
	
	public void addEdge(T edge);
	
	public void addNode (S node);
	
	public void addNodes (List<S> nodes);
	
	public void removeNode (S node);
	
	public List<S> getNodes ();
	
	public List<T> getEdges ();
	
	public List<T> getEdgesFrom(S node);
	
	public List<T> getEdgesTo(S n);
	
	public List<T> getEdgesFromTo(S n);
	
	public T getEdgeBetween(S from, S to);
	

	
	public IGraph<S,T> extractNeighSubGraph(T e);
	
	public IGraph<S,T> extractNeighSubGraph(S n);
	

	public S getSeedNode();
	
	public T getSeedEdge();
	

	public IGraph<S,T> getSubGraph(List<S> neighNodes);
	
	public IGraph<S,T> getSubGraph(S[] neighNodes);
	

	public IGraph<S,T> deepCopy();
	
	public Vector<Vector<Integer>> asAdjacencyMatrix ();
	
	public int[][] asAdjacencyMatrixArr ();
	
	/**
	 * get the connected components of this graph.
	 */
	public List<List<S>> getConnectedComponents ();

	public String serializeText();
}
