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

import at.tuwien.prip.model.project.selection.SinglePageSelection;

/**
 * SemanticSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class SemanticSelection extends SinglePageSelection
{
	private String semantic;
	
	public SemanticSelection() {
		super("SEMANTIC");
	}
	
	public void setSemantic(String semantic) {
		this.semantic = semantic;
	}
	
	public String getSemantic() {
		return semantic;
	}
}
