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
package at.tuwien.prip.common.exceptions;

public class PropertyLoadException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PropertyLoadException() {
		super();
	}

	public PropertyLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyLoadException(String message) {
		super(message);
	}

	public PropertyLoadException(Throwable cause) {
		super(cause);
	}

}
