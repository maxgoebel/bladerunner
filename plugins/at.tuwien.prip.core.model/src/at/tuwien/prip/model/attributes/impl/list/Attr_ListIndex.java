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
package at.tuwien.prip.model.attributes.impl.list;

import java.io.Serializable;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.AttributeNotSupportedException;
import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;

/**
 * 
 * Attr_ListIndex.java
 *
 *
 *
 * Created: Apr 27, 2009 8:56:25 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Attr_ListIndex 
implements Serializable, IAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean equals(IAttribute attr) {
		// TODO Auto-generated method stub
		return false;
	}

	public Attr_Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValidAttribute() {
		// TODO Auto-generated method stub
		return false;
	}

	public IAttribute mergeWith(IAttribute other) {
		// TODO Auto-generated method stub
		return null;
	}

	public void print() {
		// TODO Auto-generated method stub
		
	}

	public boolean test(Element e) throws AttributeNotSupportedException {
		// TODO Auto-generated method stub
		return false;
	}

}
