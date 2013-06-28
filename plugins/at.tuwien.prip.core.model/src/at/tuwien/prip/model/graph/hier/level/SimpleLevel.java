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

import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.graph.base.BaseNode;

public class SimpleLevel extends AbstractLevel<BaseNode, BaseEdge<BaseNode>> 
implements IGraphLevel<BaseNode, BaseEdge<BaseNode>> 
{
	int level = -1;

	@Override
	public int getLevel() 
	{
		return level;
	}
	
	public void setLevel(int level) 
	{
		this.level = level;
	}

}
