

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


package org.mozilla.dom.html;

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.dom.ThreadProxy;
import org.mozilla.interfaces.nsIDOMHTMLAreaElement;


public class HTMLAreaElementImpl extends HTMLElementImpl implements org.w3c.dom.html.HTMLAreaElement
{


    public nsIDOMHTMLAreaElement getInstance()
    {
	return getInstanceAsnsIDOMHTMLAreaElement();
    }

    /***************************************************************
     *
     * HTMLAreaElement implementation code
     *
     ***************************************************************/

    public HTMLAreaElementImpl(nsIDOMHTMLAreaElement mozInst)
    {
        super( mozInst );
    }

    public static HTMLAreaElementImpl getDOMInstance(nsIDOMHTMLAreaElement mozInst)
    {
        
        HTMLAreaElementImpl node = (HTMLAreaElementImpl) instances.get(mozInst);
        return node == null ? new HTMLAreaElementImpl(mozInst) : node;
    }

    public nsIDOMHTMLAreaElement getInstanceAsnsIDOMHTMLAreaElement()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMHTMLAreaElement) moz.queryInterface(nsIDOMHTMLAreaElement.NS_IDOMHTMLAREAELEMENT_IID);
    }

    public int getTabIndex()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMHTMLAreaElement().getTabIndex();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAccessKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getAccessKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getCoords()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getCoords();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setTabIndex(final int tabIndex)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setTabIndex(tabIndex);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setShape(final String shape)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setShape(shape);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getShape()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getShape();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getTarget()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getTarget();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAccessKey(final String accessKey)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setAccessKey(accessKey);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setHref(final String href)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setHref(href);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAlt()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getAlt();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setNoHref(final boolean noHref)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setNoHref(noHref);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getNoHref()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMHTMLAreaElement().getNoHref();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setCoords(final String coords)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setCoords(coords);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getHref()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLAreaElement().getHref();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setTarget(final String target)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setTarget(target);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAlt(final String alt)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLAreaElement().setAlt(alt);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }



}
