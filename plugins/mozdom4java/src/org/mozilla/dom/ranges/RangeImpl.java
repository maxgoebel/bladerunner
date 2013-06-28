

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


package org.mozilla.dom.ranges;

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.dom.NodeFactory;
import org.mozilla.dom.NodeImpl;
import org.mozilla.dom.ThreadProxy;
import org.mozilla.dom.WeakValueHashMap;
import org.mozilla.interfaces.nsIDOMDocumentFragment;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMRange;
import org.mozilla.interfaces.nsISupports;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;


public class RangeImpl implements org.w3c.dom.ranges.Range
{

    protected nsISupports moz;
    protected static WeakValueHashMap instances = new WeakValueHashMap();

    public nsIDOMRange getInstance()
    {
	return getInstanceAsnsIDOMRange();
    }

    /***************************************************************
     *
     * Range implementation code
     *
     ***************************************************************/

    protected RangeImpl(nsISupports mozInst)
    {
        moz = mozInst;
        instances.put(mozInst, this);
    }
    public RangeImpl(nsIDOMRange mozInst)
    {
        this( (nsISupports) mozInst );
    }    

    public static RangeImpl getDOMInstance(nsIDOMRange mozInst)
    {
        
        RangeImpl node = (RangeImpl) instances.get(mozInst);
        return node == null ? new RangeImpl(mozInst) : node;
    }

    public nsIDOMRange getInstanceAsnsIDOMRange()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMRange) moz.queryInterface(nsIDOMRange.NS_IDOMRANGE_IID);
    }

    public void setEnd(final Node refNode, final int offset)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setEnd(mozRefnode, offset);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public DocumentFragment cloneContents()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<DocumentFragment> c = new Callable<DocumentFragment>() { public DocumentFragment call() {
            nsIDOMDocumentFragment result = getInstanceAsnsIDOMRange().cloneContents();
            return (DocumentFragment) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setEndAfter(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setEndAfter(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public short compareBoundaryPoints(final short how, final Range sourceRange)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMRange mozSourcerange = sourceRange!=null ? ((RangeImpl) sourceRange).getInstance() : null;
        Callable<Short> c = new Callable<Short>() { public Short call() {
            short result = getInstanceAsnsIDOMRange().compareBoundaryPoints(how, mozSourcerange);
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setStart(final Node refNode, final int offset)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setStart(mozRefnode, offset);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public Node getCommonAncestorContainer()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMRange().getCommonAncestorContainer();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setEndBefore(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setEndBefore(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void surroundContents(final Node newParent)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozNewparent = newParent!=null ? ((NodeImpl) newParent).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().surroundContents(mozNewparent);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void setStartAfter(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setStartAfter(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public Range cloneRange()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Range> c = new Callable<Range>() { public Range call() {
            nsIDOMRange result = getInstanceAsnsIDOMRange().cloneRange();
            return new RangeImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public String toString()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<String> c = new Callable<String>() { public String call() {
            String result = getInstanceAsnsIDOMRange().toString();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void selectNodeContents(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().selectNodeContents(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void collapse(final boolean toStart)
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().collapse(toStart);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getCollapsed()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMRange().getCollapsed();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void setStartBefore(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().setStartBefore(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void insertNode(final Node newNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozNewnode = newNode!=null ? ((NodeImpl) newNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().insertNode(mozNewnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public void selectNode(final Node refNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozRefnode = refNode!=null ? ((NodeImpl) refNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().selectNode(mozRefnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public DocumentFragment extractContents()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<DocumentFragment> c = new Callable<DocumentFragment>() { public DocumentFragment call() {
            nsIDOMDocumentFragment result = getInstanceAsnsIDOMRange().extractContents();
            return (DocumentFragment) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void detach()
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().detach();
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public Node getStartContainer()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMRange().getStartContainer();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public void deleteContents()
    {
        //METHOD-BODY-START - autogenerated code
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMRange().deleteContents();
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public Node getEndContainer()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMRange().getEndContainer();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getEndOffset()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMRange().getEndOffset();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getStartOffset()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            int result = getInstanceAsnsIDOMRange().getStartOffset();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}
