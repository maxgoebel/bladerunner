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

public class LoadFailureException extends Exception {


	private static final long serialVersionUID = -2346226577650329847L;

	public LoadFailureException() {
		super();
	}

	public LoadFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadFailureException(String message) {
		super(message);
	}

	public LoadFailureException(Throwable cause) {
		super(cause);
	}
}
