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

import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.attributes.AgentStateAttribute;


public interface IAgentState 
{

	public void setBlocked(boolean blocked);
	
	public void setBlockedBy(IAgent agent);
	
	public boolean isBlocked();
	
	public IAgent getOwner();

	public int getStateLevel();

	public boolean isReconsider();

	public void setDirty(boolean b);

	public void setUtility(double utility);
	
	public double getConfidence();

	public boolean needsProcessing(IAgent agent);

	public void addAttribute(AgentStateAttribute attribute);
	
	public List<Class<?>> getProcessedBy();

	List<IAgent> getUtilizedBy();

}
