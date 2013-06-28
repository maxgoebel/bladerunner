

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
import org.mozilla.interfaces.nsIDOMHTMLFormElement;
import org.mozilla.interfaces.nsIDOMHTMLLegendElement;
import org.w3c.dom.html.HTMLFormElement;


public class HTMLLegendElementImpl extends HTMLElementImpl implements org.w3c.dom.html.HTMLLegendElement
{


    public nsIDOMHTMLLegendElement getInstance()
    {
	return getInstanceAsnsIDOMHTMLLegendElement();
    }

    /***************************************************************
     *
     * HTMLLegendElement implementation code
     *
     ***************************************************************/

    public HTMLLegendElementImpl(nsIDOMHTMLLegendElement mozInst)
    {
        super( mozInst );
    }

    public static HTMLLegendElementImpl getDOMInstance(nsIDOMHTMLLegendElement mozInst)
    {
        
        HTMLLegendElementImpl node = (HTMLLegendElementImpl) instances.get(mozInst);
        return node == null ? new HTMLLegendElementImpl(mozInst) : node;
    }

    public nsIDOMHTMLLegendElement getInstanceAsnsIDOMHTMLLegendElement()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMHTMLLegendElement) moz.queryInterface(nsIDOMHTMLLegendElement.NS_IDOMHTMLLEGENDELEMENT_IID);
    }

    public void setAlign(final String align)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLLegendElement().setAlign(align);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public HTMLFormElement getForm()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<HTMLFormElement> c = new Callable<HTMLFormElement>() { public HTMLFormElement call() {
            nsIDOMHTMLFormElement result = getInstanceAsnsIDOMHTMLLegendElement().getForm();
            return (HTMLFormElement) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAlign()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLLegendElement().getAlign();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getAccessKey()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMHTMLLegendElement().getAccessKey();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setAccessKey(final String accessKey)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMHTMLLegendElement().setAccessKey(accessKey);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }



}
