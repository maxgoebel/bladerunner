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

import java.util.List;

import at.tuwien.prip.model.agent.AC;
import at.tuwien.prip.model.agent.constraint.EdgeConstraint;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.document.semantics.WordSemantics;

public class SemanticLabeling extends LayoutLabel
{

	private WordSemantics semantics;
	
	private String textContent;
	
	/**
	 * Constructor.
	 * @param label
	 * @param stateLevel
	 * @param subLabel
	 * @param confidence
	 * @param complexity
	 * @param affectedStates
	 * @param utilizedStates
	 * @param constraints
	 */
	public SemanticLabeling(
			WordSemantics semantics,
			String textContent,
			double confidence, double complexity, AgentState[] affectedStates,
			AgentState[] utilizedStates, List<EdgeConstraint> constraints)
	{
		super(SegmentType.Semantic, 
				LabelType.LOGICAL,
				AC.D.SEMANTIC, "", 
				confidence, 
				complexity, 
				affectedStates,
				utilizedStates, 
				constraints);

		this.semantics = semantics;
		this.textContent = textContent;
	}
	
	public WordSemantics getSemantics() {
		return semantics;
	}
	
	public String getTextContent() {
		return textContent;
	}

}
