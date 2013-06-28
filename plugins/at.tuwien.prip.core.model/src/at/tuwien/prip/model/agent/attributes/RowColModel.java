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

import java.util.List;

import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.states.AgentState;
import at.tuwien.prip.model.document.layout.Alignment;

/**
 * RowColModel.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 5, 2012
 */
public class RowColModel extends AgentStateAttribute 
{
	/* */
	private Alignment alignment;
	
	/* */
	private AccessorModel accessorModel; 
	
	
	/**
	 * Constructor.
	 * 
	 * @param attType
	 * @param rowRoot
	 * @param alignment
	 * @param owner
	 */
	public RowColModel(AttributeType attType, 
			List<AgentState> cells,
			Alignment alignment, 
			IAgent owner) 
	{
		super(attType, "", owner);
		
		this.alignment = alignment;
		this.accessorModel = new AccessorModel(attType, cells);
	}
		
	public Alignment getAlignment() {
		return alignment;
	}
	
	public AccessorModel getAccessorModel() {
		return accessorModel;
	}
}
