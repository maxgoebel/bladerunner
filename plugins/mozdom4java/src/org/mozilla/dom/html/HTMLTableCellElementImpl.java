

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
import org.mozilla.interfaces.nsIDOMHTMLTableCellElement;


public class HTMLTableCellElementImpl extends HTMLElementImpl implements org.w3c.dom.html.HTMLTableCellElement
{


    public nsIDOMHTMLTableCellElement getInstance()
    {
	return getInstanceAsnsIDOMHTMLTableCellElement();
    }

    /***************************************************************
     *
     * HTMLTableCellElement implementation code
     *
     ***************************************************************/

    public HTMLTableCellElementImpl(nsIDOMHTMLTableCellElement mozInst)
    {
        super( mozInst );
    }

    public static HTMLTableCellElementImpl getDOMInstance(nsIDOMHTMLTableCellElement mozInst)
    {
        
        HTMLTableCellElementImpl node = (HTMLTableCellElementImpl) instances.get(mozInst);
        return node == null ? new HTMLTableCellElementImpl(mozInst) : node;
    }

    public nsIDOMHTMLTableCellElement getInstanceAsnsIDOMHTMLTableCellElement()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMHTMLTableCellElement) moz.queryInterface(nsIDOMHTMLTableCellElement.NS_IDOMHTMLTABLECELLELEMENT_IID);
    }

    public String getCh()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getCh();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getScope()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getScope();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getHeaders()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getHeaders();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setScope(final String scope)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setScope(scope);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setWidth(final String width)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setWidth(width);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAxis()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getAxis();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getChOff()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getChOff();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getHeight()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getHeight();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setRowSpan(final int rowSpan)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setRowSpan(rowSpan);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getNoWrap()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMHTMLTableCellElement().getNoWrap();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAbbr()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getAbbr();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAbbr(final String abbr)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setAbbr(abbr);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public int getCellIndex()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMHTMLTableCellElement().getCellIndex();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setBgColor(final String bgColor)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setBgColor(bgColor);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setColSpan(final int colSpan)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setColSpan(colSpan);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAlign(final String align)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setAlign(align);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setChOff(final String chOff)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setChOff(chOff);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setVAlign(final String vAlign)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setVAlign(vAlign);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setNoWrap(final boolean noWrap)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setNoWrap(noWrap);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAlign()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getAlign();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setHeaders(final String headers)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setHeaders(headers);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setHeight(final String height)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setHeight(height);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getWidth()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getWidth();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setCh(final String ch)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setCh(ch);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public String getVAlign()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getVAlign();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getColSpan()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMHTMLTableCellElement().getColSpan();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getRowSpan()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMHTMLTableCellElement().getRowSpan();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getBgColor()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLTableCellElement().getBgColor();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAxis(final String axis)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLTableCellElement().setAxis(axis);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }



}
