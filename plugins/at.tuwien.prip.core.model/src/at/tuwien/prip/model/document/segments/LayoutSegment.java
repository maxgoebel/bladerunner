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
package at.tuwien.prip.model.document.segments;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.agent.labels.LayoutLabel;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.graph.DocNode;

public class LayoutSegment extends RectSegment
{

//	private LayoutLabel label;

	private double weight = 1d;
	
	/**
	 * Constructor.
	 * 
	 * @param label
	 */
	public LayoutSegment(LayoutLabel label) 
	{
		super();
		this.label = label;
		Rectangle bounds = null;
		
		/* compute dimension */
		List<DocNode> affectedNodes = new ArrayList<DocNode>();
		for (AgentState state : label.getAffectedStates())
		{
			if (state.getGraph()!=null)
			{
				affectedNodes.addAll(state.getGraph().getNodes());
			}
		}
		for (DocNode node : affectedNodes)
		{
			if (bounds==null)
			{
				bounds = node.getBoundingBox().getBounds();
			}
			else 
			{
				bounds = bounds.union(node.getBoundingBox().getBounds());
			}
		}
		
		this.weight = label.getConfidence();
		
		this.x1 = bounds.x;
		this.x2 = bounds.x + bounds.width;
		this.y1 = bounds.y;
		this.y2 = bounds.y + bounds.height;
	}

	public LayoutLabel getLabel() 
	{
		return label;
	}
	
	public double getWeight() {
		return weight;
	}
	
}
