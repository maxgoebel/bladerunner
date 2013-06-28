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
package at.tuwien.prip.model.learning.example;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import at.tuwien.prip.model.project.selection.AbstractSelection;

/**
 * SelectionExample.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
@Entity
public class SelectionExample extends AbstractBinaryExample
{
	@OneToMany
	protected List<AbstractSelection> selections;

	public SelectionExample() {
		this.selections = new ArrayList<AbstractSelection>();
	}
	
	public List<AbstractSelection> getSelections() {
		return selections;
	}
	
	public void setSelections(List<AbstractSelection> selections) {
		this.selections = selections;
	}
	
}
