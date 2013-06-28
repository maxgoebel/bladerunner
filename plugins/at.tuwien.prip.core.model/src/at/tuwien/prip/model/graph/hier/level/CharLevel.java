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
package at.tuwien.prip.model.graph.hier.level;

import at.tuwien.prip.model.graph.DocEdge;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentConstants;

/**
 * CharLevel.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 4, 2011
 */
public class CharLevel extends AbstractLevel<DocNode,DocEdge>
implements IGraphLevel<DocNode,DocEdge> 
{
	
	/**
	 * Constructor.
	 * @param stack
	 */
	public CharLevel(LevelGraph<DocNode,DocEdge> stack) {
		this.parent = stack;
	}
	
	@Override
	public int getLevel() {
		return DocumentConstants.CHAR_LEVEL;
	}

}
