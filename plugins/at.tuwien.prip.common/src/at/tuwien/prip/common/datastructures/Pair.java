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

import java.io.Serializable;

public class Pair<A,B> implements Serializable{

    private static final long serialVersionUID = -4544108291853994903L;

    protected A a;
    protected B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?,?>)) return false;
        Pair<?,?> p = (Pair<?,?>) obj;

        if (a==null && p.a==null) {
            //ok
        } else if (a!=null && p.a!=null) {
            if (!a.equals(p.a)) return false;
            //else ok
        }
        else {
            //null and not-null
            return false;
        }

        if (b==null && p.b==null) {
            //ok
        } else if (b!=null && p.b!=null) {
            if (!b.equals(p.b)) return false;
            //else ok
        }
        else {
            //null and not-null
            return false;
        }

        //all tests passed
        return true;
    }

    @Override
    public int hashCode() {
        if (a==null && b==null) return 0;
        else if (a!=null && b!=null) return a.hashCode()+b.hashCode();
        else if (a!=null && b==null) return  a.hashCode();
        else if (a==null && b!=null) return  b.hashCode();
        return -1;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>",a,b);
    }

}
