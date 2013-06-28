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
 * AnnotationPage.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class AnnotationPage 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int pageNum;
	
	@OneToMany
	private List<AbstractSelection> items;
	
	/**
	 * Constructor.
	 */
	public AnnotationPage()	{ 
		this.items = new ArrayList<AbstractSelection>();
	}
	
	/**
	 * Constructor
	 * @param pageNum
	 */
	public AnnotationPage(int pageNum)
	{
		this.pageNum = pageNum;
		this.items = new ArrayList<AbstractSelection>();
	}
	
	public int getPageNum() {
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public List<AbstractSelection> getItems() {
		return items;
	}

}
