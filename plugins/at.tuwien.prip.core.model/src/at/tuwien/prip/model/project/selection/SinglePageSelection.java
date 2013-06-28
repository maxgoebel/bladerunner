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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * SinglePageSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
public class SinglePageSelection extends AbstractSelection
{	
	protected List<AbstractSelection> items;
	
	private int pageNum;
	
	private String name;
	
	public SinglePageSelection(String type) {
		super(type);
		
		this.items = new ArrayList<AbstractSelection>();
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public List<AbstractSelection> getItems() {
		return items;
	}
	
	public void setItems(List<AbstractSelection> items) {
		this.items = items;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

