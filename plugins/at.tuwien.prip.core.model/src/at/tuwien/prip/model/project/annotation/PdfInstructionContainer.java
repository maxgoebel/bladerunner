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

import at.tuwien.prip.model.project.selection.blade.PDFInstruction;

public class PdfInstructionContainer {

	private List<PDFInstruction> instructions;
	
	public PdfInstructionContainer() {
		this.instructions = new ArrayList<PDFInstruction>();
	}
	
	public List<PDFInstruction> getInstructions() {
		return instructions;
	}

	public void add(PDFInstruction instr)
	{
		this.instructions.add(instr);
	}
	
	public void addAll(List<PDFInstruction> instrs)
	{
		this.instructions.addAll(instrs);
	}
}
