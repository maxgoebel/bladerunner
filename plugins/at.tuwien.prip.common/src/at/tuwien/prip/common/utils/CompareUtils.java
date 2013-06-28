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

import gnu.trove.TObjectHashingStrategy;

public class CompareUtils {

    /**
     * null-safe equals method
     */
    public static <T> boolean eq(T o1, T o2) {
        if (o1==null && o2==null) return true;
        if (o1==null || o2==null) return false;
        return o1==o2 || o1.equals(o2);
    }
    public static <T> boolean eq(T o1, T o2, TObjectHashingStrategy<T> s) {
        if (o1==null && o2==null) return true;
        if (o1==null || o2==null) return false;
        return
            o1==o2 ||
            (s==null ? o1.equals(o2) : s.equals(o1, o2));
    }

    @SuppressWarnings("unchecked")
    public static int compareTo(Comparable o1, Comparable o2) {
        if (o1==null && o2==null) return 0;
        if (o1!=null && o2!=null) {
            return o1.compareTo(o2);
        }
        if (o1==null) return -1;
        if (o2==null) return 1;
        return 0;
    }


    public static <T1,T2> int hashCode2(T1 o1, T2 o2) {
        if (o1==null) {
            if (o2==null)
                return 0;
            return o2.hashCode();
        }
        else
            if (o2==null)
                return o1.hashCode();
            else
                return o1.hashCode()^o2.hashCode();
    }

    public static <T1,T2> int hashCode(T1 o1, T2 o2, TObjectHashingStrategy<T1> s1, TObjectHashingStrategy<T2> s2) {
        if (o1==null) {
            if (o2==null)
                return 0;
            return (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
        }
        else
            if (o2==null)
                return
                    (s1==null ? o1.hashCode() : s1.computeHashCode(o1));
            else
                return
                    (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
                    (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
    }

    public static <T1,T2,T3> int hashCode(T1 o1, T2 o2, T3 o3)
    {
        if (o1==null)
            if (o2==null) {
            	if (o3==null)
            		return 0;
            	return o3.hashCode();
            }
            else
                if (o3==null)
                    return o2.hashCode();
                else
                    return o2.hashCode()^o3.hashCode();
        else
            if (o2==null) {
                if (o3==null)
                	return o1.hashCode();
                return o1.hashCode()^o3.hashCode();
            }
            else
                if (o3==null)
                    return o1.hashCode()^o2.hashCode();
                else
                    return o1.hashCode()^o2.hashCode()^o3.hashCode();
    }

    public static <T1,T2,T3> int
        hashCode(T1 o1, T2 o2, T3 o3,
                 TObjectHashingStrategy<T1> s1,
                 TObjectHashingStrategy<T2> s2,
                 TObjectHashingStrategy<T3> s3)
    {
        if (o1==null)
            if (o2==null) {
                if (o3==null)
                    return 0;
                return (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
            }
            else
                if (o3==null)
                    return
                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
                else
                    return
                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2)) ^
                        (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
        else
            if (o2==null) {
                if (o3==null)
                    return
                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1));

                return
                (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
                (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
            }
            else
                if (o3==null)
                    return
                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2));
                else
                    return
                        (s1==null ? o1.hashCode() : s1.computeHashCode(o1)) ^
                        (s2==null ? o2.hashCode() : s2.computeHashCode(o2)) ^
                        (s3==null ? o3.hashCode() : s3.computeHashCode(o3));
    }

}
