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
package at.tuwien.prip.common.utils;

/**
 * Counter.java
 *
 * This class implements simple, thread-safe
 * counter
 *
 * Created: Mon Oct 15 17:33:21 2001
 *
 * @author Michal Ceresna
 * @version
 */

public class Counter {

    private long count=0;

    public Counter() {
    }

    public Counter(int start) {
        count = start;
    }

    public synchronized long get() {
        long c = count;
        count++;
        return c;
    }

}// Counter
