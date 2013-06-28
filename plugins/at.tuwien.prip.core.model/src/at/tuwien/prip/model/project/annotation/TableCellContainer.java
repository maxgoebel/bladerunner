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

import at.tuwien.prip.model.project.selection.blade.TableCell;

/**
 * TableCellContainer.java
 *
 *
 * @author mcgoebel@gmail.com
 * @date Feb 18, 2013
 */
public class TableCellContainer {

	private List<TableCell> cells;
	
	public TableCellContainer() 
	{
		this.cells = new ArrayList<TableCell>();
	}
	
	public List<TableCell> getCells() {
		return cells;
	}
	
	public void add(TableCell cell)
	{
		this.cells.add(cell);
	}
	
	public void addAll(List<TableCell> cells)
	{
		this.cells.addAll(cells);
	}
	
	public void remove (TableCell cell)
	{
		this.cells.remove(cell);
	}
}
