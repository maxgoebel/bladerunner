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
package at.tuwien.prip.model.document.semantics;

public class OrderedPattern 
{
	public WordSemantics[] semantics;
	
	public OrderedPattern(int size) 
	{
		semantics = new WordSemantics[size];
	}
	
	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		if (semantics==null) return sb.toString();
		
		for (WordSemantics ws : semantics) {
			sb.append(ws.name() + " ");
		}
		return sb.toString().trim();
	}
}
