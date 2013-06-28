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
 * Strong reference
 * 
 * @author ceresna
 */
public class Ref<T> 
    implements IRef<T> 
{

    private T t;
    
    public Ref() {}
    
    public Ref(T t) {
        this.t = t;
    }

    public void set(T t) {
        this.t = t;
    }
    
    public T get() {
        return t;
    }

}
