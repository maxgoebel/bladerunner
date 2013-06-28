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
package at.tuwien.prip.model.token;

import at.tuwien.prip.model.graph.base.BaseNode;

/**
 * DocNodeToken.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Apr 2, 2011
 */
public class NodeToken extends GraphToken 
{

	private BaseNode node;
	
	public NodeToken(BaseNode node) {
		super(node.toString());
		this.node = node;
	}

	public BaseNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node.toString();
	}
	
//	@Override
//	public boolean equals(Object obj) 
//	{
//		if (!(obj instanceof NodeToken)) 
//		{
//			return false;
//		}
//
//		NodeToken other = (NodeToken) obj;
//		double score = NodeMatcher.matchNodesSim(other.node, this.node);
//		return score>0.5;
//	}
	
}
