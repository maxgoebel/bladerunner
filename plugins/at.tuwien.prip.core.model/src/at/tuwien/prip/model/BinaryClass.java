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
package at.tuwien.prip.model;


/**
 * BinaryClass.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 17, 2011
 */
public enum BinaryClass {
	
	POSITIVE(0), 
	NEGATIVE(1);
	
	private int code;
	private BinaryClass(int code) {

		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public static BinaryClass valueOf(int i){
		for (BinaryClass s : values()){
			if (s.code == i){
				return s;
			}
		}
		throw new IllegalArgumentException("No matching constant for " + i);
	}
}
