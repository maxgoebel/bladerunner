

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

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.interfaces.nsIDOMDocumentType;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.w3c.dom.NamedNodeMap;


public class DocumentTypeImpl extends NodeImpl implements org.w3c.dom.DocumentType
{


    public nsIDOMDocumentType getInstance()
    {
	return getInstanceAsnsIDOMDocumentType();
    }

    /***************************************************************
     *
     * DocumentType implementation code
     *
     ***************************************************************/

    public DocumentTypeImpl(nsIDOMDocumentType mozInst)
    {
        super( mozInst );
    }

    public static DocumentTypeImpl getDOMInstance(nsIDOMDocumentType mozInst)
    {
        
        DocumentTypeImpl node = (DocumentTypeImpl) instances.get(mozInst);
        return node == null ? new DocumentTypeImpl(mozInst) : node;
    }

    public nsIDOMDocumentType getInstanceAsnsIDOMDocumentType()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMDocumentType) moz.queryInterface(nsIDOMDocumentType.NS_IDOMDOCUMENTTYPE_IID);
    }

    public String getInternalSubset()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMDocumentType().getInternalSubset();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getPublicId()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMDocumentType().getPublicId();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getName()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMDocumentType().getName();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String getSystemId()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMDocumentType().getSystemId();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public NamedNodeMap getNotations()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<NamedNodeMap> c = new Callable<NamedNodeMap>() { public NamedNodeMap call() {
            nsIDOMNamedNodeMap result = getInstanceAsnsIDOMDocumentType().getNotations();
            return new NamedNodeMapImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public NamedNodeMap getEntities()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<NamedNodeMap> c = new Callable<NamedNodeMap>() { public NamedNodeMap call() {
            nsIDOMNamedNodeMap result = getInstanceAsnsIDOMDocumentType().getEntities();
            return new NamedNodeMapImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}
