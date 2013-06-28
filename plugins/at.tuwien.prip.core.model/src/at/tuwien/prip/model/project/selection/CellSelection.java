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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;


/**
 * CellSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class CellSelection extends SinglePageSelection
{
	private String text;

	private int startRow;

	private int startCol;

	private int endCol;
	
	private int endRow;

	@OneToMany
	private List<PDFInstruction> instructions;

	public List<PDFInstruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<PDFInstruction> instructions) {
		this.instructions = instructions;
	}
	
	public int getStartCol() {
		return startCol;
	}
	
	public int getStartRow() {
		return startRow;
	}
	
	public int getEndCol() {
		return endCol;
	}
	
	public int getEndRow() {
		return endRow;
	}
	
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}
	
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
