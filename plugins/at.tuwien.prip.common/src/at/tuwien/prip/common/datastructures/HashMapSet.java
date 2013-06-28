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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;


public class HashMapSet<A, B>
    extends HashMap<A, Set<B>>
    implements MapSet<A, B>
{

    private static final long serialVersionUID = 8472466666366662246L;

    public Set<B> putmore(A key, B value) {
        Set<B> bs = getsafe(key);
        bs.add(value);

        return bs;
    }

    public Set<B> putmore(A key, Collection<B> values)
    {
        Set<B> bs = getsafe(key);
        bs.addAll(values);

        return bs;
    }

    public Set<B> getsafe(A key) {
        Set<B> bs = get(key);
        if (bs==null) {
            bs = new LinkedHashSet<B>();
            put(key, bs);
        }
        return bs;
    }

}
