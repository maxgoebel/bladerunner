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
package at.tuwien.prip.model.project.result;

import at.tuwien.prip.model.project.selection.AbstractSelection;

/**
 * 
 * SelectionResult.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 27, 2012
 */
public class SelectionResult implements IResult
{

	private AbstractSelection selection;
	
	/**
	 * Constructor.
	 * @param selection
	 */
	public SelectionResult(AbstractSelection selection)
	{
		this.selection = selection;
	}
	
	public AbstractSelection getSelection() {
		return selection;
	}
	
}
