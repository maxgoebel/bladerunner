

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


package org.mozilla.dom.events;

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.dom.NodeFactory;
import org.mozilla.dom.NodeImpl;
import org.mozilla.dom.ThreadProxy;
import org.mozilla.dom.views.AbstractViewImpl;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMMouseEvent;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.views.AbstractView;


public class MouseEventImpl extends UIEventImpl implements org.w3c.dom.events.MouseEvent
{


    public nsIDOMMouseEvent getInstance()
    {
	return getInstanceAsnsIDOMMouseEvent();
    }

    /***************************************************************
     *
     * MouseEvent implementation code
     *
     ***************************************************************/

    public MouseEventImpl(nsIDOMMouseEvent mozInst)
    {
        super( mozInst );
    }

    public static MouseEventImpl getDOMInstance(nsIDOMMouseEvent mozInst)
    {
        
        MouseEventImpl node = (MouseEventImpl) instances.get(mozInst);
        return node == null ? new MouseEventImpl(mozInst) : node;
    }

    public nsIDOMMouseEvent getInstanceAsnsIDOMMouseEvent()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMMouseEvent) moz.queryInterface(nsIDOMMouseEvent.NS_IDOMMOUSEEVENT_IID);
    }

    public short getButton()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Short> c = new Callable<Short>() { public Short call() {
            int result = getInstanceAsnsIDOMMouseEvent().getButton();
            return (short) result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getAltKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMMouseEvent().getAltKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void initMouseEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg, final int screenXArg, final int screenYArg, final int clientXArg, final int clientYArg, final boolean ctrlKeyArg, final boolean altKeyArg, final boolean shiftKeyArg, final boolean metaKeyArg, final short buttonArg, final EventTarget relatedTargetArg)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMAbstractView mozViewarg = viewArg!=null ? ((AbstractViewImpl) viewArg).getInstance() : null;
        final nsIDOMNode casted_Relatedtargetarg = relatedTargetArg!=null ? ((NodeImpl) relatedTargetArg).getInstance() : null;
        final nsIDOMEventTarget mozRelatedtargetarg = (nsIDOMEventTarget) casted_Relatedtargetarg.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMMouseEvent().initMouseEvent(typeArg, canBubbleArg, cancelableArg, mozViewarg, detailArg, screenXArg, screenYArg, clientXArg, clientYArg, ctrlKeyArg, altKeyArg, shiftKeyArg, metaKeyArg, buttonArg, mozRelatedtargetarg);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public int getClientY()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMMouseEvent().getClientY();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getClientX()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMMouseEvent().getClientX();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getMetaKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMMouseEvent().getMetaKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getCtrlKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMMouseEvent().getCtrlKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getShiftKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMMouseEvent().getShiftKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getScreenX()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMMouseEvent().getScreenX();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getScreenY()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMMouseEvent().getScreenY();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public EventTarget getRelatedTarget()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<EventTarget> c = new Callable<EventTarget>() { public EventTarget call() {
            nsIDOMEventTarget result = getInstanceAsnsIDOMMouseEvent().getRelatedTarget();
            return (EventTarget) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}