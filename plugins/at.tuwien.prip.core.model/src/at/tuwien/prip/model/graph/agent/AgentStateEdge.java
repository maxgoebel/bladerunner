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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import at.tuwien.prip.core.model.agent.IAgent;
import at.tuwien.prip.core.model.agent.states.AgentState;
import at.tuwien.prip.core.model.agent.states.IAgentState;
import at.tuwien.prip.core.model.graph.base.BaseEdge;

/**
 * AgentStateEdge.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 16, 2012
 */
public class AgentStateEdge extends BaseEdge<AgentState>
implements IAgentState, java.io.Serializable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243067111699658256L;

	/**
	 * Constructor.
	 * @param a
	 * @param b
	 * @param relation
	 */
	public AgentStateEdge(AgentState a, AgentState b, String label /*LayoutLabeling edgeLabel*/) 
	{
		super(a, b, label);
	}

	public Double getLength() 
	{
		Rectangle fromRect = from.getBounds();
		Rectangle toRect = to.getBounds();
		return Point.distance(fromRect.getCenterX(),fromRect.getCenterY(), 
				toRect.getCenterX(),toRect.getCenterY());
	}

	public IAgent getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();
		Rectangle.union(from.getBounds(), to.getBounds(), bounds);
		return bounds;
	}

	@Override
	public void setBlocked(boolean blocked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlockedBy(IAgent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStateLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isReconsider() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDirty(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUtility(double utility) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IAgent> getAcceptors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsProcessing(IAgent agent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IAgent> getProcessedBy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
