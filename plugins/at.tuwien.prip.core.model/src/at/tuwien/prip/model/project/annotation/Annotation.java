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
package at.tuwien.prip.model.project.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import at.tuwien.prip.model.project.selection.AbstractSelection;

/**
 * Annotation.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class Annotation 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
	
	protected String uri;
	
	protected AnnotationType type;
	
	@OneToMany
	protected List<AbstractSelection> items;
	
	private List<AnnotationPage> pages;
	
	public Annotation() 
	{
		this.items = new ArrayList<AbstractSelection>();
		this.pages = new ArrayList<AnnotationPage>();
	}
	
	/**
	 * Constructor.
	 */
	public Annotation(String uri, AnnotationType type) 
	{
		this();
		this.uri = uri;
		this.type = type;
	}
		
	public AnnotationType getType() {
		return type;
	}
	
	public void setItems(List<AbstractSelection> items) {
		this.items = items;
	}
	
	public List<AbstractSelection> getItems() {
		return items;
	}
	
	public void addSelection (AbstractSelection selection)
	{
		items.add(selection);
	}

	public List<AnnotationPage> getPages() {
		return pages;
	}
}
