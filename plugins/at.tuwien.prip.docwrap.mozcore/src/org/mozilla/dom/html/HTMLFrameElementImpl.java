

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

import org.mozilla.dom.NodeFactory;
import org.mozilla.dom.ThreadProxy;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.w3c.dom.Document;


public class HTMLFrameElementImpl extends HTMLElementImpl implements org.w3c.dom.html.HTMLFrameElement
{


    public nsIDOMHTMLFrameElement getInstance()
    {
	return getInstanceAsnsIDOMHTMLFrameElement();
    }

    /***************************************************************
     *
     * HTMLFrameElement implementation code
     *
     ***************************************************************/

    public HTMLFrameElementImpl(nsIDOMHTMLFrameElement mozInst)
    {
        super( mozInst );
    }

    public static HTMLFrameElementImpl getDOMInstance(nsIDOMHTMLFrameElement mozInst)
    {
        
        HTMLFrameElementImpl node = (HTMLFrameElementImpl) instances.get(mozInst);
        return node == null ? new HTMLFrameElementImpl(mozInst) : node;
    }

    public nsIDOMHTMLFrameElement getInstanceAsnsIDOMHTMLFrameElement()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMHTMLFrameElement) moz.queryInterface(nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID);
    }

    public Document getContentDocument()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Document> c = new Callable<Document>() { public Document call() {
            nsIDOMDocument result = getInstanceAsnsIDOMHTMLFrameElement().getContentDocument();
            return (Document) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getMarginHeight()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getMarginHeight();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setName(final String name)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setName(name);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getSrc()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getSrc();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getName()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getName();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setSrc(final String src)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setSrc(src);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setMarginWidth(final String marginWidth)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setMarginWidth(marginWidth);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getLongDesc()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getLongDesc();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getScrolling()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getScrolling();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setNoResize(final boolean noResize)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setNoResize(noResize);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setScrolling(final String scrolling)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setScrolling(scrolling);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setMarginHeight(final String marginHeight)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setMarginHeight(marginHeight);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getMarginWidth()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getMarginWidth();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setFrameBorder(final String frameBorder)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setFrameBorder(frameBorder);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getNoResize()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMHTMLFrameElement().getNoResize();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setLongDesc(final String longDesc)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLFrameElement().setLongDesc(longDesc);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getFrameBorder()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLFrameElement().getFrameBorder();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}
