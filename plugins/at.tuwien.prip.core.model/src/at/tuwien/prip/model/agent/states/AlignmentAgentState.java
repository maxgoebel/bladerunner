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

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.core.model.agent.AC;
import at.tuwien.prip.core.model.agent.IAgent;
import at.tuwien.prip.core.model.agent.relations.AlignmentType;
import at.tuwien.prip.core.model.agent.relations.LayoutRelation;
import at.tuwien.prip.core.model.graph.agent.LayoutAlignment;

/**
 * AlignmentAgentState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 14, 2012
 */
public class AlignmentAgentState extends AgentEdgeState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4231979801972656493L;
	
	private List<AgentState> elements;
	
	private List<LayoutAlignment> alignments;
	
	/**
	 * Constructor.
	 * 
	 * @param rl
	 * @param owner
	 * @param atype
	 * @param reconsider
	 */
	public AlignmentAgentState(
			LayoutRelation lr, 
			IAgent owner, 
			List<LayoutAlignment> alignments) 
	{
		super(AC.B.ALIGNMENT,lr, owner);
		
		this.elements = new ArrayList<AgentState>();
		this.alignments = alignments;
		
//		//compute alignment type
//		AgentState fromState = lr.getFromState();
//		AgentState toState = lr.getToState();
//		
//		if (fromState instanceof AlignmentAgentState)
//		{
//			
//		}
//		
//		for (AgentState state : lr.getAffectedStates())
//		{
//			if (state instanceof AlignmentAgentState)
//			{
//				elements.addAll(((AlignmentAgentState) state).getElements());
//				this.getConstraints().addAll(state.getConstraints()); //inherit constraints
//				
//				if (alignType==AlignmentType.MIXED) {
//					continue;
//				}
//				AlignmentAgentState as = (AlignmentAgentState) state;
//				if (as.getAlignType()==AlignmentType.MIXED)
//				{
//					alignType = AlignmentType.MIXED;
//					continue;
//				}
//				else if (alignType==null) 
//				{
//					alignType = as.getAlignType();
//				}
//				else
//				{
//					if (alignType.toString().contains("VERT") && 
//							as.getAlignType().toString().startsWith("VERT"))
//					{
//						alignType = AlignmentType.ALL_VERT;
//					}
//					else if (alignType.toString().contains("HORIZ") && 
//							as.getAlignType().toString().startsWith("HORIZ"))
//					{
//						alignType = AlignmentType.ALL_HORIZ;
//					}
//					else
//					{
//						alignType = AlignmentType.MIXED;
//						continue;
//					}
//				}			
//			}	
//			else 
//			{
//				elements.add(state);
//				if (alignType==null)
//				{
//					for (AlignmentAgentStateEdge aase : alignments)
//					{
//						if (alignType==null)
//						{ 
//							alignType = aase.getType();
//						}
//						else if (alignType==aase.getType())
//						{
//							continue;
//						}
//						else if (alignType.toString().contains("VERT") && 
//								aase.getType().toString().startsWith("VERT"))
//						{
//							alignType = AlignmentType.ALL_VERT;
//						}
//						else if (alignType.toString().contains("HORIZ") && 
//								aase.getType().toString().startsWith("HORIZ"))
//						{
//							alignType = AlignmentType.ALL_HORIZ;
//						}
//						else
//						{
//							alignType = AlignmentType.MIXED;
//							continue;
//						}
//					}
//				}
//			}
//		}
//		if (alignType==null)
//		{
//			alignType = AlignmentType.MIXED;
//		}
//		ListUtils.unique(elements);
		name = "";
		for(AgentState e : elements)
		{
			name += e.name+"-";
		}
		name = name.substring(0, name.length()-1);
		ErrorDump.debug(this, "done");	
	}
	
//	public AlignmentType getAlignType() {
//		return alignType;
//	}
	
	public List<AgentState> getElements() {
		return elements;
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
}
