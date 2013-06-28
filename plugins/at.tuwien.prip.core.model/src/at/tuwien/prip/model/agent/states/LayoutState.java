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

import at.tuwien.prip.model.agent.AC;
import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.labels.LayoutLabel;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.ISegmentGraph;

public class LayoutState extends AgentState 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4870987154475899012L;

	/**
	 * 
	 * @param ll
	 * @param owner
	 * @param reconsider
	 */
	public LayoutState(LayoutLabel ll, IAgent owner, boolean reconsider) {
		super(ll, owner, reconsider);
	}

	/**
	 * 
	 * @param node
	 */
	public LayoutState(DocNode node) 
	{
		super(node);
	}

	/**
	 * 
	 * @param graph
	 */
	public LayoutState(ISegmentGraph graph) 
	{
		super(graph);
		stateLevel = AC.A.PAGE;
	}

	@Override
	public String toString() {
		return "Layout State - "+getSegType()+": "+getTextContent().getText();
	}
}
