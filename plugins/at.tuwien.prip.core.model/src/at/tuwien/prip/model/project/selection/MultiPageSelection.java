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
package at.tuwien.prip.model.project.selection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import at.tuwien.prip.model.project.annotation.AnnotationPage;

/**
 * MultiPageSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
public class MultiPageSelection extends AbstractSelection
{
	@OneToMany
	private List<AnnotationPage> pages;
	
	private String name;
	
	public MultiPageSelection(String type)
	{
		super(type);
		this.pages = new ArrayList<AnnotationPage>();
	}
	
	public List<AnnotationPage> getPages() {
		return pages;
	}
	
	public void setPages(List<AnnotationPage> pages) {
		this.pages = pages;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
