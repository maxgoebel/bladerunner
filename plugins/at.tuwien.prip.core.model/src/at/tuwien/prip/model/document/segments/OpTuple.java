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
package at.tuwien.prip.model.document.segments;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * OpTuple.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class OpTuple
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	protected int opIndex;
	protected int argIndex;
	
	public OpTuple(int opIndex, int argIndex)
	{
		this.opIndex = opIndex;
		this.argIndex = argIndex;
	}
	
	public int getOpIndex() {
		return opIndex;
	}
	public void setOpIndex(int opIndex) {
		this.opIndex = opIndex;
	}
	public int getArgIndex() {
		return argIndex;
	}
	public void setArgIndex(int argIndex) {
		this.argIndex = argIndex;
	}
	
}
