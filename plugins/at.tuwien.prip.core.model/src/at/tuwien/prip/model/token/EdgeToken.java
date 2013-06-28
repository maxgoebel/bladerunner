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

import at.tuwien.prip.model.graph.base.BaseEdge;

/**
 * DocEdgeToken.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Apr 2, 2011
 */
public class EdgeToken extends GraphToken 
{

	private BaseEdge<?> edge;
	
	public EdgeToken(BaseEdge<?> edge) 
	{
		super(edge.toString());
		this.edge = edge;
	}

	public BaseEdge<?> getEdge() 
	{
		return edge;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof EdgeToken)) {
			return false;
		} 

		EdgeToken other = (EdgeToken) obj;
		return this.getEdge().getRelation().equals(other.getEdge().getRelation());
	}
}
