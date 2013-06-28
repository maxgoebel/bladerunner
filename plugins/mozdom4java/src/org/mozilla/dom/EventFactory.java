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

import org.w3c.dom.events.*;
import org.mozilla.dom.events.*;
import org.mozilla.xpcom.*;
import org.mozilla.interfaces.*;

public class EventFactory
{
    public static final int MOUSE_EVENT = 0x01;
    public static final int KEY_EVENT = 0x02;    
    
    private EventFactory() {}
   
    public static Event getNodeInstance(nsIDOMEvent ev, int evType)
    {
        if (ev == null) return null;
            
        switch (evType)
        {
            case MOUSE_EVENT:
                return MouseEventImpl.getDOMInstance((nsIDOMMouseEvent) ev.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID));        
            case KEY_EVENT:
                return KeyEventImpl.getDOMInstance((nsIDOMKeyEvent) ev.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID));        
            default:
                return null;
        }
    }
}
