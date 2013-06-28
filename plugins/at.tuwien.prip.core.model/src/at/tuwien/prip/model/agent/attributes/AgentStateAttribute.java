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

import at.tuwien.prip.model.agent.IAgent;

public class AgentStateAttribute {

	private AttributeType type;
	
	private String value;
	
	private IAgent owner;
	
	/**
	 * Constructor.
	 * @param type
	 * @param value
	 * @param owner
	 */
	public AgentStateAttribute(AttributeType type, String value, IAgent owner) 
	{
		this.type = type;
		this.value = value;
		this.owner = owner;
	}
	
	public IAgent getOwner() {
		return owner;
	}
	
	public AttributeType getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}
