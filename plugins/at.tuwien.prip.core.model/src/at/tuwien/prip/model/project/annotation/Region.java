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

import java.util.List;

import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.TableCell;

public class Region {

	List<PDFInstruction> instructions;
	
	List<TableCell> cells;
	
	int pageNum;
	
	int id;
	
	int colIncrement;
	
	int rowIncrement;
	
	public Region(int pageNum, int id) 
	{
		this.pageNum = pageNum;
		this.id = id;
		this.colIncrement = -1;
		this.rowIncrement = -1;
	}
	
	public void setInstructions(List<PDFInstruction> instructions) {
		this.instructions = instructions;
	}
	
	public void setColIncrement(int colIncrement) {
		this.colIncrement = colIncrement;
	}
	
	public void setRowIncrement(int rowIncrement) {
		this.rowIncrement = rowIncrement;
	}
	
	public void setCells(List<TableCell> cells) {
		this.cells = cells;
	}
	
	public List<TableCell> getCells() {
		return cells;
	}
	
	public int getColIncrement() {
		return colIncrement;
	}
	
	public int getRowIncrement() {
		return rowIncrement;
	}
	
	public int getId() {
		return id;
	}
	
	public List<PDFInstruction> getInstructions() {
		return instructions;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	
}
