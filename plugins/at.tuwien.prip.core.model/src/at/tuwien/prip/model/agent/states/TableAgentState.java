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

import java.util.List;

import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.core.model.agent.IAgent;
import at.tuwien.prip.core.model.agent.labels.LayoutLabeling;

/**
 * TableAgentState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 8, 2012
 */
public class TableAgentState extends AgentState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6286366351809362470L;

	private List<AgentState> tableCells;
	/**
	 * Constructor.
	 * @param ll
	 * @param owner
	 * @param reconsider
	 */
	public TableAgentState(LayoutLabeling ll, IAgent owner, boolean reconsider)
	{
		super(ll, owner, reconsider);
		this.tableCells = ListUtils.toList(ll.getUtilizedStates(),AgentState.class);
	}

	public List<AgentState> getTableCells() {
		return tableCells;
	}
	
	public void setTableCells(List<AgentState> tableCells) {
		this.tableCells = tableCells;
	}
}
