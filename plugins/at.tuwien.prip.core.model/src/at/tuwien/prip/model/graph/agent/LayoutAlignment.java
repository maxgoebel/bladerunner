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
package at.tuwien.prip.model.graph.agent;

import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.relations.AlignmentType;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.layout.Direction;

/**
 * 
 * AlignmentAgentStateEdge.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 16, 2012
 */
public class LayoutAlignment
{

	private AlignmentType type;
	 
	private Direction direction;
	
	private IAgent owner;
	
	private AgentState from;
	
	private AgentState to;
	
	/**
	 * Constructor.
	 * @param relation
	 * @param type
	 * @param dir
	 * @param owner
	 */
	public LayoutAlignment(
			AlignmentType type, 
			Direction dir, 
			AgentState from, 
			AgentState to,
			IAgent owner) 
	{
		this.type = type;
		this.direction = dir;
		this.owner = owner;
		this.from = from;
		this.to = to;	
	}

	public AlignmentType getType() {
		return type;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public boolean isVertical () 
	{
		if (getType().toString().startsWith("VERT"))
		{
			return true;
		}
		return false;
	}
	
	public boolean isHorizontal () 
	{
		if (getType().toString().startsWith("HORIZ"))
		{
			return true;
		}
		return false;
	}

	public double getLength() 
	{
		if (isHorizontal())
		{
			if (from.getBounds().getCenterX()>to.getBounds().getCenterX())
			{
				return from.getBounds().getMinX() - to.getBounds().getMaxX();
			}
			else
			{
				return to.getBounds().getMinX() - from.getBounds().getMaxX();
			}
		}
		else
		{
			if (from.getBounds().getCenterY()>to.getBounds().getCenterY())
			{
				return from.getBounds().getMinY() - to.getBounds().getMaxY();
			}
			else
			{
				return to.getBounds().getMinY() - from.getBounds().getMaxY();
			}
		}
	}
	
	public AgentState getFrom() {
		return from;
	}
	
	public AgentState getTo() {
		return to;
	}	
	
	public IAgent getOwner() {
		return owner;
	}
	
}
