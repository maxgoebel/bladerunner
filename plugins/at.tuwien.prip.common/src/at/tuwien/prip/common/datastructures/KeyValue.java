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
package at.tuwien.prip.common.datastructures;

/**
 * 
 * 
 * KeyValue.java
 *
 *
 *
 * Created: Jun 23, 2009 11:11:29 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class KeyValue extends Pair<String,String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1193012518530863485L;

	public KeyValue(String a, String b) {
		super(a, b);
	}

	public String getKey() {
		return getFirst();
	}

	public String getValue() {
		return getSecond();
	}
	
	@Override
	public String toString() {
		return a + ":" + b;
	}

}
