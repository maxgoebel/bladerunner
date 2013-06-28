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
package at.tuwien.prip.model.project.annotation;

/**
 * AnnotationLabel.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
public enum AnnotationLabel 
{
	IMAGE(0), 
	HEADING(1), 
	BOILERPLATE(2), 
	LIST(3), REGION(4), 
	SECTION(5), 
	SEMANTIC(6), 
	TABLE(7), 
	KEY_VALUE(8);

	private int code;
	private AnnotationLabel(int code) {

		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public static AnnotationLabel valueOf(int i){
		for (AnnotationLabel s : values()){
			if (s.code == i){
				return s;
			}
		}
		throw new IllegalArgumentException("No matching constant for " + i);
	}
}
