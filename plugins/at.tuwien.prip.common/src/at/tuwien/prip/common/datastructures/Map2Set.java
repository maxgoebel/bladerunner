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
import java.util.Set;


public interface Map2Set<A, B, C> extends Map2<A, B, Set<C>> {

    public Set<C> putmore(A key1, B key2, C value);
    public Set<C> putmore(A key1, B key2, Collection<C> values);
    public Set<C> getsafe(A key1, B key2);

}
