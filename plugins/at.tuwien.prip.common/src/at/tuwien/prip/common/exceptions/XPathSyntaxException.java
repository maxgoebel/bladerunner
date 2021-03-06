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
 * DOMValueException.java
 *
 * thrown when a value is missing or invalid
 *
 * Created: Thu Mar 13 10:07:40 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class XPathSyntaxException
    extends Exception
{

    private static final long serialVersionUID = 7812790392297375610L;

    public XPathSyntaxException () {
    }

    public XPathSyntaxException (String message) {
        super(message);
    }

    public XPathSyntaxException (Throwable parentex) {
        super(parentex);
    }

} // DOMValueException
