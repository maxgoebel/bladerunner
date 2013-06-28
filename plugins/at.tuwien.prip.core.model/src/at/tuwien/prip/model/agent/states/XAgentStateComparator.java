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


import java.util.Comparator;



/**
 * XDocNodeComparator.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 9, 2011
 */
public class XAgentStateComparator implements Comparator<AgentState> {

	@Override
	public int compare(AgentState s1, AgentState s2) {
		// sorts in x order
		double x1 = s1.getBounds().getMinX();
		double x2 = s2.getBounds().getMinX();

		return (int) (x1 - x2);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj.equals(this);
	}
	
}
