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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import at.tuwien.prip.model.project.selection.MultiPageSelection;

/**
 * TableSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class TableSelection extends MultiPageSelection
{	
	private List<TableCell> cells;
	
	/**
	 * Constructor.
	 */
	public TableSelection() 
	{
		super("TABLE");
		cells = new ArrayList<TableCell>();
	}
	
	public List<TableCell> getCells() {
		return cells;
	}
}
