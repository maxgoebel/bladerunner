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
package at.tuwien.prip.model.project.selection.blade;

import javax.persistence.Entity;

import at.tuwien.prip.model.project.selection.SinglePageSelection;

/**
 * TextSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class TextSelection extends SinglePageSelection 
{
	protected String textContent;
	
	protected String textType;
	
	public TextSelection() {
		super("TEXT");
	}
	
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	
	public String getTextContent() {
		return textContent;
	}
	
	public String getTextType() {
		return textType;
	}
	
	public void setTextType(String textType) {
		this.textType = textType;
	}
}
