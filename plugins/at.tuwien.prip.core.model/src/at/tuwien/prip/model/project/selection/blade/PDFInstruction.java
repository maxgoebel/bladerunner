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

import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.SelectionType;


/**
 * PDFInstruction.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class PDFInstruction extends AbstractSelection
{
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
	
	private int instructionID;
	
	private int subinstructionID;
	
	private String text;
	
	/**
	 * Constructor.
	 */
	public PDFInstruction()
	{
		super("PDF_INSTRUCTION");
	}
	
	/**
	 * Constructor.
	 * @param instructionID
	 * @param subinstructionID
	 */
	public PDFInstruction(int instructionID, int subinstructionID) 
	{
		this();
		this.instructionID = instructionID;
		this.subinstructionID = subinstructionID;
	}
	
	public int getIndex()
	{
		return instructionID;
	}
	
	public int getSubIndex()
	{
		return subinstructionID;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
