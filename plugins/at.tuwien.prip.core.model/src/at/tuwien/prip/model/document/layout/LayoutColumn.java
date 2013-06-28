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
package at.tuwien.prip.model.document.layout;


/**
 * LayoutColumn.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Aug 9, 2011
 */
public class LayoutColumn extends LayoutObject 
{
	public LayoutColumn(Alignment alignment) 
	{
		this.alignment = alignment;
	}
	
	private Alignment alignment;
	
	public Alignment getAlignment() {
		return alignment;
	}
	
}//LayoutColumn
