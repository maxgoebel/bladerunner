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
import javax.persistence.OneToOne;

import at.tuwien.prip.model.project.annotation.PdfInstructionContainer;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.selection.SinglePageSelection;


/**
 * RegionSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class RegionSelection extends SinglePageSelection
{	
	@OneToOne
	private TableCellContainer cells;
	
	@OneToOne
	private PdfInstructionContainer instructions;
	
	@OneToOne
	private TextSelection text;
	
	public RegionSelection()
	{
		super("REGION");
		this.cells = new TableCellContainer();
		this.instructions = new PdfInstructionContainer();
	}
	
	public TableCellContainer getCellContainer() {
		return cells;
	}
	
	public void setCellContainer(TableCellContainer cells) {
		this.cells = cells;
	}
	
	public TextSelection getText() {
		return text;
	}
	
	public void setText(TextSelection text) {
		this.text = text;
	}
	
	public PdfInstructionContainer getInstructionContainer() {
		return instructions;
	}
	
	public void setInstructionContainer(PdfInstructionContainer instructions) {
		this.instructions = instructions;
	}
}
