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

import at.tuwien.prip.model.graph.DocNode;

/**
 * 
 * LayoutSection.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Aug 31, 2011
 */
public class LayoutSection extends LayoutObject 
{
	private String title;
	
	private DocNode sectionHeader;

	/**
	 * Constructor.
	 */
	public LayoutSection() {
		// TODO Auto-generated constructor stub
	}
	
	public void setSectionHeader(DocNode sectionHeader) {
		this.sectionHeader = sectionHeader;
	}

	public DocNode getSectionHeader() {
		return sectionHeader;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
}
