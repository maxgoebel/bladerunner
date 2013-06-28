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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import at.tuwien.prip.model.BinaryClass;

/**
 * 
 * AbstractBinaryExample.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 3, 2012
 */
@Entity
public class AbstractBinaryExample 
implements IExample 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	protected BinaryClass classification;
	
	/**
	 * Constructor.
	 */
	public AbstractBinaryExample() {
		// TODO Auto-generated constructor stub
	}
	
	public BinaryClass getClassification() {
		return classification;
	}
	
	public void setClassification(BinaryClass classification) {
		this.classification = classification;
	}
	
}
