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
import at.tuwien.prip.model.document.semantics.SemanticText;

/**
 * 
 * SemanticAttribute.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Oct 8, 2012
 */
public class SemanticAttribute extends AgentStateAttribute 
{

	private List<SemanticText> semantics;

	/**
	 * Constructor.
	 * @param value
	 * @param owner
	 */
	public SemanticAttribute(String text, List<SemanticText> semantics,  IAgent owner)
	{
		super(AttributeType.SEMANTIC, text, owner);
		this.semantics = semantics;
	}
	
	public List<SemanticText> getSemantics() {
		return semantics;
	}
	
	public void setSemantics(List<SemanticText> semantics) {
		this.semantics = semantics;
	}

}
