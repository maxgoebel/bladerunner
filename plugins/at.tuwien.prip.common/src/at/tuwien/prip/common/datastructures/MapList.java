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
import java.util.List;
import java.util.Map;

public interface MapList<A, B> extends Map<A, List<B>> {

    public List<B> putmore(A key, B value);
    public List<B> putmore(A key, Collection<B> value);
    public List<B> getsafe(A key);

}
