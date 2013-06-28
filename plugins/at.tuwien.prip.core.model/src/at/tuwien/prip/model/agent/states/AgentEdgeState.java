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
package at.tuwien.prip.model.agent.states;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.attributes.AgentStateAttribute;
import at.tuwien.prip.model.agent.relations.LayoutRelation;
import at.tuwien.prip.model.graph.base.BaseEdge;

/**
 * AgentEdgeState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 25, 2012
 */
public class AgentEdgeState extends BaseEdge<AgentState>
implements IAgentState, java.io.Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7740781892009540323L;

	protected String name;
	
	private LayoutRelation rl;
	
	/* a numeric type associated with this state */
	private int stateLevel;
	
	/* the 'owning' agent */
	private transient IAgent owner;
	
	/* a list of attributes */
	protected transient List<AgentStateAttribute> attributes;
	
	/* */
	protected List<Class<?>> processedBy;
	
	/* */
	protected List<IAgent> utilizedBy;
		
	/* */
	private double length;
	
	/**
	 * Constructor.
	 */
	public AgentEdgeState() 
	{
		this.attributes = new ArrayList<AgentStateAttribute>();
		this.processedBy = new ArrayList<Class<?>>();
		this.utilizedBy = new ArrayList<IAgent>();
	}
	
	/**
	 * Constructor.
	 * @param rl
	 * @param owner
	 */
	public AgentEdgeState(
			int stateLevel,
			LayoutRelation rl, 
			IAgent owner) 
	{
		this();
		this.stateLevel = stateLevel;
		this.rl = rl;
		this.owner = owner;
		this.from = rl.getFromState();
		this.to = rl.getToState();
		this.relation = rl.getRelation();
			
		//compute edge length
		Rectangle fromRect = from.getBounds();
		if (to==null)
			System.out.println();
		Rectangle toRect = to.getBounds();
		this.length = Point.distance(fromRect.getCenterX(),fromRect.getCenterY(), 
				toRect.getCenterX(),toRect.getCenterY());
	}
	
	public double getLength() {
		return length;
	}
	
	public IAgent getOwner() {
		return owner;
	}
	
	public LayoutRelation getRl() {
		return rl;
	}

	@Override
	public void setBlocked(boolean blocked) 
	{
			this.from.setBlocked(blocked);
			this.to.setBlocked(blocked);	
	}

	@Override
	public void setBlockedBy(IAgent agent) {
		// TODO Auto-generated method stub	
	}

	@Override
	public boolean isBlocked() {
		return from.isBlocked() || to.isBlocked();
	}

	@Override
	public int getStateLevel() {
		return stateLevel;
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
	public boolean needsProcessing(IAgent agent) 
	{
		if (getOwner()!=null && getOwner().getName().equals(agent.getName()))
		{
			return false;
		}
		return true;
	}

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();
		Rectangle.union(from.getBounds(), to.getBounds(), bounds);
		return bounds;
	}
	
	public List<AgentStateAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public void addAttribute(AgentStateAttribute attribute) {
		this.attributes.add(attribute);
	}

	@Override
	public List<Class<?>> getProcessedBy() {
		return this.processedBy;
	}
	
	@Override
	public List<IAgent> getUtilizedBy() {
		return utilizedBy;
	}
	
	@Override
	public String toString() {
		return "EdgeState " + stateLevel + "\n [from]: " + from.toString() + "\n [to]: " + to.toString();
	}
}
