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
package at.tuwien.prip.model.agent.relations;

import at.tuwien.prip.model.agent.labels.ILabel;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.layout.LayoutRelationType;

/**
 * LayoutRelation.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 25, 2012
 */
public abstract class LayoutRelation implements ILabel
{
	private LayoutRelationType relType;
	
	private String relation;
	/*
	 *  a 0-1 confidence for the proposed 
	 * layout to be good, with 1 being best
	 */
	private double confidence;	
	
	private AgentState fromState;
	
	private AgentState toState;
	
	/**
	 * Constructor
	 */
	public LayoutRelation(LayoutRelationType relType,
			String relation,
			double confidence,
			AgentState fromState,
			AgentState toState) 
	{
		this.relType = relType;
		this.relation = relation;
		this.confidence = confidence;
		this.fromState = fromState;
		this.toState = toState;
	}
	
	public String getRelation() {
		return relation;
	}
	
	public double getConfidence() {
		return confidence;
	}
	
	public LayoutRelationType getRelType() {
		return relType;
	}
	
	public AgentState getFromState() {
		return fromState;
	}
	
	public AgentState getToState() {
		return toState;
	}
}
