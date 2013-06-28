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
import java.util.LinkedHashSet;
import java.util.Set;


public class HashMap2Set<A, B, C>
    extends HashMap2<A, B, Set<C>>
    implements Map2Set<A, B, C>
{

    private static final long serialVersionUID = 1181808461975597969L;

    public HashMap2Set(TObjectHashingStrategy<A> key1HashingStrategy,
                       TObjectHashingStrategy<B> key2HashingStrategy,
                       TObjectHashingStrategy<Set<C>> valHashingStrategy)
    {
        super(key1HashingStrategy, key2HashingStrategy, valHashingStrategy);
    }

    public Set<C> putmore(A key1, B key2, C value) {
        Set<C> bs = getsafe(key1, key2);
        bs.add(value);

        return bs;
    }

    public Set<C> putmore(A key1, B key2, Collection<C> values)
    {
        Set<C> bs = getsafe(key1, key2);
        bs.addAll(values);

        return bs;
    }

    public Set<C> getsafe(A key1, B key2) {
        Set<C> bs = get(key1, key2);
        if (bs==null) {
            bs = new LinkedHashSet<C>();
            put(key1, key2, bs);
        }
        return bs;
    }

}
