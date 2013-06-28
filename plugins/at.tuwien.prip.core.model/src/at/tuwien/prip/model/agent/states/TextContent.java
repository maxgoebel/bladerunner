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

public class TextContent 
{
	private Font font;

	private String text;

	public TextContent(String text, Font font) 
	{
		this.text = text;
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public String getText() {
		return text;
	}

	

}
