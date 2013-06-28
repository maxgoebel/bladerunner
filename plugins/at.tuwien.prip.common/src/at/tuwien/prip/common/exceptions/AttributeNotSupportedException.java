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


/**
 * 
 * AttributeNotSupportedException.java
 *
 *
 *
 * Created: Apr 27, 2009 2:20:00 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class AttributeNotSupportedException extends Exception{

    /**
	 *
	 */
	private static final long serialVersionUID = -2981085776873037380L;

	public AttributeNotSupportedException() {
        super();
    }

    public AttributeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeNotSupportedException(String message) {
        super(message);
    }

    public AttributeNotSupportedException(Throwable cause) {
        super(cause);
    }

}
