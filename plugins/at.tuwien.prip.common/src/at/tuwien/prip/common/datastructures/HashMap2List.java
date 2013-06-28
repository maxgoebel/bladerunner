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


import gnu.trove.TObjectHashingStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class HashMap2List<A, B, C>
    extends HashMap2<A, B, List<C>>
    implements Map2List<A, B, C>
{

    private static final long serialVersionUID = 674609506063470381L;

    public HashMap2List(TObjectHashingStrategy<A> key1HashingStrategy,
                        TObjectHashingStrategy<B> key2HashingStrategy,
                        TObjectHashingStrategy<List<C>> valHashingStrategy)
    {
        super(key1HashingStrategy, key2HashingStrategy, valHashingStrategy);
    }

    public List<C> putmore(A key1, B key2, C value) {
        List<C> bs = getsafe(key1, key2);
        bs.add(value);

        return bs;
    }

    public List<C> putmore(A key1, B key2, Collection<C> values)
    {
        List<C> bs = getsafe(key1, key2);
        bs.addAll(values);

        return bs;
    }

    public List<C> getsafe(A key1, B key2) {
        List<C> bs = get(key1, key2);
        if (bs==null) {
            bs = new LinkedList<C>();
            put(key1, key2, bs);
        }
        return bs;
    }

}
