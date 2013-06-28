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
package at.tuwien.prip.model.graph;

/**
 * EEdgeRelation.java
 *
 * Describes an edge relation between two nodes as an 
 * concrete relationship.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Apr 8, 2011
 */
public enum EEdgeRelation 
{

	LEFT_OF, RIGHT_OF, ABOVE, BELOW, 
	INFERIOR_TO, SUPERIOR_TO, 
	KEY_VALUE, SYMBOL_REL,
	CENTER_ALIGNED, LEFT_ALIGNED, RIGHT_ALIGNED,
	EQUAL_HEIGHT, EQUAL_WIDTH,
	EQUAL_SEGMENT, EQUAL_SEMANTIC, EQUAL_TEXT,
	READING_ORDER_BEFORE, READING_ORDER_AFTER

}
