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

public class Triple<A,B,C> {

    private A a;
    private B b;
    private C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    public C getThird() {
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Triple<?,?,?>)) return false;
        Triple<?,?,?> p = (Triple<?,?,?>) obj;

        if (a==null && p.a==null && c==null) {
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

        if (c==null && p.c==null) {
            //ok
        } else if (c!=null && p.c!=null) {
            if (!c.equals(p.c)) return false;
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
        int i = 0;

        if (a!=null) i+=a.hashCode();
        if (b!=null) i+=b.hashCode();
        if (c!=null) i+=c.hashCode();

        return i;
    }

}
