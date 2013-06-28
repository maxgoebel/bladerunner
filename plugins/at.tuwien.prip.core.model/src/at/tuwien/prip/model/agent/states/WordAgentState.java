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

import java.awt.Font;

import at.tuwien.prip.core.model.graph.DocNode;

/**
 * WordAgentState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 8, 2012
 */
public class WordAgentState extends AgentState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isSalient = false;
	
	private int headingLevel = -1;
	

	
	/**
	 * Constructor.
	 * @param node
	 */
	public WordAgentState(DocNode node) 
	{
		super(node);
		this.font = node.getFont();
		this.textContent = node.getSegText();
	}

	public boolean isSalient() {
		return isSalient;
	}
	
	public void setSalient(boolean salient)
	{
		this.isSalient = true;
	}
	
	public int getHeadingLevel() {
		return headingLevel;
	}
	
	public void setHeadingLevel(int headingLevel) {
		this.headingLevel = headingLevel;
	}
	
	public Font getFont() {
		return font;
	}

}
