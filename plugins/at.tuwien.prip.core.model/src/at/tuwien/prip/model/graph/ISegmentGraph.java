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

import java.util.List;
import java.util.Map;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * ISegmentGraph.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 21, 2012
 */
public interface ISegmentGraph extends IGraph<DocNode,DocEdge> 
{
//	public Rectangle getDimensions();
		
	public void computeDimensions();
	
	public Map<DocNode, GenericSegment> getNodeSegHash();

	public Map<GenericSegment, DocNode> getSegNodeHash();
	
	public GenericSegment getSegment (DocNode node);
	
	public int getNumPages();
	
	public double getLineSpaceing();
	
	public String serializeText();
	
	@Override
	public ISegmentGraph getSubGraph(List<DocNode> neighNodes);
	
	public ISegmentGraph getSubGraph(List<DocNode> neighNodes, boolean edges);
}
