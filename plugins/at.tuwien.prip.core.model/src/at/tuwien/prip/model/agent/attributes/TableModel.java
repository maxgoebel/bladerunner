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

public class TableModel extends AgentStateAttribute
{

	
	/**
	 * Constructor.
	 * @param value
	 * @param owner
	 */
	public TableModel(String value, IAgent owner) {
		super(AttributeType.TABEL_MODEL, value, owner);
		
	}

}
