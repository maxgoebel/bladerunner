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
package at.tuwien.prip.model.document.segments.web;

import at.tuwien.prip.model.document.segments.GenericSegment;

/**
 * FormSegment.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jul 4, 2011
 */
public class FormSegment extends GenericSegment
{
	
	private HTMLForm form;
	
	/**
	 * Constructor.
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public FormSegment(
			float x1,
			float x2,
			float y1,
			float y2
	)
	{
		super(x1, x2, y1, y2);
	}

	public void setForm(HTMLForm form) {
		this.form = form;
	}

	public HTMLForm getForm() {
		return form;
	}
	
}
