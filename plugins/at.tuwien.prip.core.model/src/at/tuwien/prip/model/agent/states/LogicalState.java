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

import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.labels.LayoutLabel;

public class LogicalState extends AgentState 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2003715998224607812L;


	public LogicalState(LayoutLabel ll, IAgent owner, boolean reconsider) {
		super(ll, owner, reconsider);
	}
	
	@Override
	public String toString() {
		return "Logical State - "+getSegType()+": "+getTextContent().getText();
	}
	
}
