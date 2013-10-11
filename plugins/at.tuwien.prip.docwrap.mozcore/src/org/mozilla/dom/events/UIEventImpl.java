

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

import org.mozilla.dom.ThreadProxy;
import org.mozilla.dom.views.AbstractViewImpl;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMUIEvent;
import org.w3c.dom.views.AbstractView;


public class UIEventImpl extends EventImpl implements org.w3c.dom.events.UIEvent
{


    public nsIDOMUIEvent getInstance()
    {
	return getInstanceAsnsIDOMUIEvent();
    }

    /***************************************************************
     *
     * UIEvent implementation code
     *
     ***************************************************************/

    public UIEventImpl(nsIDOMUIEvent mozInst)
    {
        super( mozInst );
    }

    public static UIEventImpl getDOMInstance(nsIDOMUIEvent mozInst)
    {
        
        UIEventImpl node = (UIEventImpl) instances.get(mozInst);
        return node == null ? new UIEventImpl(mozInst) : node;
    }

    public nsIDOMUIEvent getInstanceAsnsIDOMUIEvent()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMUIEvent) moz.queryInterface(nsIDOMUIEvent.NS_IDOMUIEVENT_IID);
    }

    public AbstractView getView()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<AbstractView> c = new Callable<AbstractView>() { public AbstractView call() {
            nsIDOMAbstractView result = getInstanceAsnsIDOMUIEvent().getView();
            return new AbstractViewImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getDetail()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMUIEvent().getDetail();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void initUIEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMAbstractView mozViewarg = viewArg!=null ? ((AbstractViewImpl) viewArg).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMUIEvent().initUIEvent(typeArg, canBubbleArg, cancelableArg, mozViewarg, detailArg);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }



}