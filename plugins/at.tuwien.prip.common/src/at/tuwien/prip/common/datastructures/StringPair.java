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


public class StringPair extends Pair<String, String> {

    private static final long serialVersionUID = -6129301962872962365L;

    public StringPair(String a, String b) {
        super(a, b);
    }

    public StringPair(String a, char b) {
        this(a, ""+b);
    }

}

