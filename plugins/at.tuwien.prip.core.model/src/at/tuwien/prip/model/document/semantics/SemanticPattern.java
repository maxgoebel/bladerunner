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
package at.tuwien.prip.model.document.semantics;

public class SemanticPattern {

	private WordSemantics semantic;
	
	private String text;
	
	/**
	 * 
	 * @param text
	 * @param semantic
	 */
	public SemanticPattern(String text, WordSemantics semantic)
	{
		this.semantic = semantic;
		this.text = text;
	}
	
	public WordSemantics getSemantic() {
		return semantic;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return semantic + ":" + text;
	}
}
