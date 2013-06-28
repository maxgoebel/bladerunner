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
package at.tuwien.prip.model.project.document;


/**
 * DocumentFormat.java
 *
 *
 *
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 2, 2011
 */
public enum DocumentFormat {

	PDF(0),
	HTML(1),
	TIFF(2),
	UNKNOWN(3);

	private int code;
	private DocumentFormat(int code) {

		this.code = code;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public static DocumentFormat valueOf(int i){
		for (DocumentFormat s : values()){
			if (s.code == i){
				return s;
			}
		}
		throw new IllegalArgumentException("No matching constant for " + i);
	}
}
