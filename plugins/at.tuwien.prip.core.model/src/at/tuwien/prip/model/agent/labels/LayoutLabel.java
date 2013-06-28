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
package at.tuwien.prip.model.agent.labels;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.agent.constraint.EdgeConstraint;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.segments.SegmentType;

/**
 * LayoutLabeling.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Oct 30, 2011
 */
public class LayoutLabel implements ILabel
{

	/* the layout annotation label */
	private SegmentType segmentType;
	
	private LabelType labelType; 
	
	private String subLabel;

	private int stateLevel;
	
	/*
	 *  a 0-1 confidence for the proposed 
	 * layout to be good, with 1 being best
	 */
	private double confidence;

	/* the complexity of this labeling */
	private double complexity;
	
	/* */
	private AgentState[] affectedStates;
	
	/* */
	private AgentState[] utilizedStates;

	/* */
	private List<EdgeConstraint> constraints;

	/**
	 * Constructor.
	 * @param label
	 * @param confidence
	 * @param impact
	 * @param affectedNodes
	 * @param constraints
	 */
	public LayoutLabel(
			SegmentType segmentType,
			LabelType labelType,
			int stateLevel,
			String subLabel,
			double confidence, 
			double complexity,
			AgentState[] affectedStates,
			AgentState[] utilizedStates,
			List<EdgeConstraint> constraints)
	{
		this.segmentType = segmentType;
		this.labelType = labelType;
		this.stateLevel = stateLevel;
		this.subLabel = subLabel;
		this.confidence = confidence;
		this.complexity = complexity;
		this.utilizedStates = utilizedStates;
		this.affectedStates = affectedStates;
		this.constraints = new ArrayList<EdgeConstraint>(constraints);
	}

	public LabelType getLabelType() {
		return labelType;
	}
	
	public void setLabelType(LabelType labelType) {
		this.labelType = labelType;
	}
	
	public SegmentType getSegmentType() {
		return segmentType;
	}
	public void setSegmentType(SegmentType segType) {
		this.segmentType = segType;
	}

	public double getConfidence() {
		return confidence;
	}

	public AgentState[] getAffectedStates() {
		return affectedStates;
	}
	
	public AgentState[] getUtilizedStates() {
		return utilizedStates;
	}
	
	public void setConstraints(List<EdgeConstraint> constraints) {
		this.constraints = constraints;
	}

	public List<EdgeConstraint> getConstraints() {
		return constraints;
	}
	
	public String getSubLabel() {
		return subLabel;
	}
	
	public void setSubLabel(String subLabel) {
		this.subLabel = subLabel;
	}
	
	public double getComplexity() {
		return complexity;
	}
	
	public int getStateLevel() {
		return stateLevel;
	}

	@Override
	public String toString() {
		return "LayoutLabel "+getLabelType()+" "+getSegmentType()+ " "+getAffectedStates();
	}
}
