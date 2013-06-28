/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozdom4java
 *
 * The Initial Developer of the Original Code is
 * Peter Szinek, Lixto Software GmbH, http://www.lixto.com.
 * Portions created by the Initial Developer are Copyright (C) 2005-2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *  Peter Szinek (peter@rubyrailways.com)
 *  Michal Ceresna (michal.ceresna@gmail.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.mozilla.dom;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

public class WeakValueHashMap extends LinkedHashMap 
{
    private ReferenceQueue reaped = new ReferenceQueue();
    
    private static class ValueRef extends WeakReference  
    {
        private final Object key;
        
        ValueRef(Object val, Object key, ReferenceQueue q)
        {
            super(val, q);
            this.key = key;
        }
    }
    
    public Object put(Object key, Object value) 
    {
        reap();
        ValueRef vr = new ValueRef(value, key, reaped);
        return super.put(key, vr);
    }
    
    public Object get(Object key)
    {
        reap();
        ValueRef vr = (ValueRef) super.get(key);
        Object result = null;
        if (vr != null)
            result = vr.get();
        return result;
    }
    
    public Object remove(Object key) 
    {
        reap();
        ValueRef vr = (ValueRef) super.get(key);
        if (vr == null) 
        {
            return null;
        } 
        else 
        {
            vr.clear();
            super.remove(key);
            return null;
        }
    }
    
    public int size()
    {
        reap();
        return super.size();
    }
    
    public void reap()
    {
        ValueRef ref;
        while ((ref = (ValueRef) reaped.poll()) != null)
        {
            super.remove(ref.key);
        }
    }
}
