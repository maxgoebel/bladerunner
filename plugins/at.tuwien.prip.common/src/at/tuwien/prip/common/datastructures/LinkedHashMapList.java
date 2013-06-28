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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


public class LinkedHashMapList<A, B>
    extends LinkedHashMap<A, List<B>>
    implements LinkedMapList<A, B>
{

    private static final long serialVersionUID = -4859750225101489898L;

    public List<B> putmore(A key, B value) {
        List<B> bs = getsafe(key);
        bs.add(value);

        return bs;
    }

    public List<B> putmore(A key, Collection<B> values)
    {
        List<B> bs = getsafe(key);
        bs.addAll(values);

        return bs;
    }

    public List<B> getsafe(A key) {
        List<B> bs = get(key);
        if (bs==null) {
            bs = new LinkedList<B>();
            put(key, bs);
        }
        return bs;
    }

}
