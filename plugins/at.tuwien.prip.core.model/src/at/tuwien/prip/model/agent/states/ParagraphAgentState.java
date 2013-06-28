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

import at.tuwien.prip.common.utils.MathUtils;
import at.tuwien.prip.core.model.agent.IAgent;
import at.tuwien.prip.core.model.agent.labels.LayoutLabeling;

/**
 * ParagraphAgentState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 22, 2012
 */
public class ParagraphAgentState extends AgentState
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3578291607194470393L;

	private List<Double> distList = new ArrayList<Double>();
	
	/**
	 * Constructor.
	 * 
	 * @param ll
	 * @param stateLevel
	 * @param type
	 * @param owner
	 * @param reconsider
	 * @param lineDist
	 */
	public ParagraphAgentState(LayoutLabeling ll, 
			IAgent owner, 
			boolean reconsider, 
			List<Double> distList) 
	{
		super(ll, owner, reconsider);
		this.distList = distList;
	}
	
	public double computeAvgLineDist() {
		return MathUtils.cumsum(distList)/distList.size();
	}
	
	public double computeDistanceIntegrity() {
		return MathUtils.cumsum(distList)/distList.size();
	}
}
