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
package at.tuwien.prip.model.agent;

import java.util.List;

import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.agent.states.IAgentState;

/**
 * IAgent.java
 *
 * Contract of all agents.
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 19, 2011
 */
public interface IAgent extends Runnable
{
	public void start ();
	
	public void pause ();
	
	public void resume ();
	
	public void stop();

	public String getName();

	public List<IAgentState> getOpen();
	
	public boolean isActive();
	
	public boolean hasFinished();

	public void computeUtility(AgentState state);

	public void runOnce();
	
	public int getNumIterations();
	
	public int getStatesInput();
	
	public int getStatesCreated();
	
	public int getEdgesCreated();
	
}
