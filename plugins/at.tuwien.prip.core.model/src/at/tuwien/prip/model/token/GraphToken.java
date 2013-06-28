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




/**
 * GraphToken.java
 *
 * A GraphToken can either represent a DocNode or a DocEdge.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Apr 2, 2011
 */
public abstract class GraphToken extends Token {

	public GraphToken(String name) {
		super(name);
	}
	
	/**
	 * Delegate to respective method of class docnode/docedge.
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this.getClass().equals(obj.getClass()) && obj instanceof NodeToken)
		{
			return ((NodeToken)obj).equals(this);
		} 
		else if (this.getClass().equals(obj.getClass()) && obj instanceof EdgeToken) 
		{
			return ((EdgeToken)obj).equals(this);
		}
		return false;
	}
	
}
