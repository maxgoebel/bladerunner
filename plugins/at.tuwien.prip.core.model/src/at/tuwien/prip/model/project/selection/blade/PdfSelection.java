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
import javax.persistence.OneToMany;

import at.tuwien.prip.model.project.selection.AbstractSelection;


/**
 * PdfSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class PdfSelection extends AbstractSelection 
{
	@OneToMany
	protected List<PDFInstruction> instructions;
	
	protected String text;
	
	/**
	 * Constructor.
	 */
	public PdfSelection()
	{
		super("PDF");
		this.instructions = new ArrayList<PDFInstruction>();
	}
	
	public List<PDFInstruction> getInstructions() {
		return instructions;
	}
	
	public void setInstructions(List<PDFInstruction> instructions) {
		this.instructions = instructions;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
