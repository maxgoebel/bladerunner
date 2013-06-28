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
package at.tuwien.prip.model.agent.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.document.semantics.SemanticText;
import at.tuwien.prip.model.document.semantics.WordSemantics;

/**
 * AccessorModel.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 28, 2012
 */
public class AccessorModel 
{
	private MapList<Integer, WordSemantics> dataTypeIndex;

	private List<Integer> accessorIndexes;

	private Map<Integer, List<SemanticText>> index2TypeMap;
	
	/**
	 * Constructor.
	 */
	public AccessorModel() 
	{
		this.dataTypeIndex = new HashMapList<Integer, WordSemantics>();
		this.accessorIndexes = new ArrayList<Integer>();
		this.index2TypeMap = new HashMap<Integer, List<SemanticText>>();
	}

	/**
	 * Constructor.
	 * @param type
	 * @param cells
	 */
	public AccessorModel (AttributeType type, List<AgentState> cells)
	{
		this();

		if (type==AttributeType.ROW_MODEL)
		{
			int index = 0;
			for (AgentState cell : cells)
			{
				index++;
				List<SemanticText> semantics = getTableCellSemantics(cell);
				index2TypeMap.put(index, semantics);
			}
		}		
		else if (type==AttributeType.COL_MODEL)
		{
			Map<Integer, List<SemanticText>> index2TypeMap = 
				new HashMap<Integer, List<SemanticText>>();
			int index = 0;
			for (AgentState cell : cells)
			{
				index++;
				List<SemanticText> semantics = getTableCellSemantics(cell);
				index2TypeMap.put(index, semantics);
			}
		}

		System.out.println();
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static List<AgentState> getTableColumnDescendents(AgentState parent)
	{
		List<AgentState> result = new ArrayList<AgentState>();
		Stack<AgentState> stack = new Stack<AgentState>();
		stack.push(parent);

		while (!stack.isEmpty())
		{
			AgentState top = stack.pop();

			List<AgentState> children = top.getChildren();
			for (AgentState child : children)
			{
				if (child.getSegType()==SegmentType.TableColumn)
				{
					result.add(child);
				}
				else if (child.getSegType()==SegmentType.TablePart)
				{
					stack.push(child);
				}
			}
		}		
		return result;
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static List<AgentState> getTableRowDescendents(AgentState parent)
	{
		List<AgentState> result = new ArrayList<AgentState>();
		Stack<AgentState> stack = new Stack<AgentState>();
		stack.push(parent);

		while (!stack.isEmpty())
		{
			AgentState top = stack.pop();

			List<AgentState> children = top.getChildren();
			for (AgentState child : children)
			{
				if (child.getSegType()==SegmentType.TableRow)
				{
					result.add(child);
				}
				else if (child.getSegType()==SegmentType.TablePart)
				{
					stack.push(child);
				}
			}
		}		
		return result;
	}

	/**
	 * 
	 * @param cell
	 * @return
	 */
	public static List<SemanticText> getTableCellSemantics (AgentState cell)
	{
		List<SemanticText> result = new ArrayList<SemanticText>();
		if (cell.getSegType()!=SegmentType.TableCell)
		{
			return result;
		}

		for (AgentState child : cell.getDescendants())
		{
			for (AgentStateAttribute att : child.getAttributes())
			{
				if (att instanceof SemanticAttribute)
				{
					SemanticAttribute satt = (SemanticAttribute) att;
					result.addAll(satt.getSemantics());
				}
				else if (att.getType()==AttributeType.DOMAIN_KEY)
				{
					result.add(new SemanticText(att.getValue(), WordSemantics.DOMAIN_KEY));
				}
				else if (att.getType()==AttributeType.DOMAIN_VAL)
				{
					result.add(new SemanticText(att.getValue(), WordSemantics.DOMAIN_VAL));
				}
				else if (att.getType()==AttributeType.HEADING)
				{
					result.add(new SemanticText(att.getValue(), WordSemantics.HEADING));
				}
			}
		}

		return result;
	}

	public List<Integer> getAccessorIndexes() {
		return accessorIndexes;
	}
	
	public MapList<Integer, WordSemantics> getDataTypeIndex() {
		return dataTypeIndex;
	}
	
	public Map<Integer, List<SemanticText>> getIndex2TypeMap() {
		return index2TypeMap;
	}
}
