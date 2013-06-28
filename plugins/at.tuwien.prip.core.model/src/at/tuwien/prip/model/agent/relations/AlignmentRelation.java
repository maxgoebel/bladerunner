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

import java.util.List;

import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.layout.Direction;
import at.tuwien.prip.model.document.layout.LayoutRelationType;
import at.tuwien.prip.model.graph.agent.LayoutAlignment;

/**
 * 
 * AlignmentLabeling.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 15, 2012
 */
public class AlignmentRelation extends LayoutRelation
{
	private List<LayoutAlignment> alignments;
	
	private AlignmentType type; 
	
	private Direction direction;
	
	/**
	 * Constructor.
	 * @param type
	 * @param dir, the alignment direction
	 * @param confidence
	 * @param complexity
	 * @param fromState
	 * @param toState
	 * @param alignments
	 */
	public AlignmentRelation(
			AlignmentType type, 
			Direction dir,
			double confidence, 
			double complexity,
			AgentState fromState,
			AgentState toState,
			List<LayoutAlignment> alignments) 
	{
		super(LayoutRelationType.Alignment,  
				"",
				confidence, 
				fromState, 
				toState);
		
		this.type = type;
		this.direction = dir;
		this.alignments = alignments;
	}
	
	public List<LayoutAlignment> getAlignments() {
		return alignments;
	}
	
	public boolean containsHorizontalAlignment () 
	{
		for (LayoutAlignment ase : alignments)
		{
			if (ase instanceof LayoutAlignment)
			{
				LayoutAlignment aase = (LayoutAlignment) ase;
				if (aase.getType().toString().startsWith("HORI"))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public LayoutAlignment getAlignment (AlignmentType type) 
	{
		for (LayoutAlignment ase : alignments)
		{
			if (ase instanceof LayoutAlignment)
			{
				LayoutAlignment aase = (LayoutAlignment) ase;
				if (aase.getType()==type)
				{
					return aase;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public boolean containsAlignment (AlignmentType type) 
	{
		for (LayoutAlignment ase : alignments)
		{
			if (ase instanceof LayoutAlignment)
			{
				LayoutAlignment aase = (LayoutAlignment) ase;
				if (aase.getType()==type)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean containsVerticalAlignment () 
	{
		for (LayoutAlignment ase : alignments)
		{
			if (ase instanceof LayoutAlignment)
			{
				LayoutAlignment aase = (LayoutAlignment) ase;
				if (aase.getType().toString().startsWith("VERT"))
				{
					return true;
				}
			}
		}
		return false;
	}

	public Direction getDirection() {
		return direction;
	}
	
	public AlignmentType getType() {
		return type;
	}

}
