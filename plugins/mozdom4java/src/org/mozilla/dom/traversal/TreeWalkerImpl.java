

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


package org.mozilla.dom.traversal;

//Java imports
import java.util.concurrent.Callable;

import org.mozilla.dom.NodeFactory;
import org.mozilla.dom.NodeImpl;
import org.mozilla.dom.ThreadProxy;
import org.mozilla.dom.WeakValueHashMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeFilter;
import org.mozilla.interfaces.nsIDOMTreeWalker;
import org.mozilla.interfaces.nsISupports;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;


public class TreeWalkerImpl implements org.w3c.dom.traversal.TreeWalker
{

    protected nsISupports moz;
    protected static WeakValueHashMap instances = new WeakValueHashMap();

    public nsIDOMTreeWalker getInstance()
    {
	return getInstanceAsnsIDOMTreeWalker();
    }

    /***************************************************************
     *
     * TreeWalker implementation code
     *
     ***************************************************************/

    protected TreeWalkerImpl(nsISupports mozInst)
    {
        moz = mozInst;
        instances.put(mozInst, this);
    }
    public TreeWalkerImpl(nsIDOMTreeWalker mozInst)
    {
        this( (nsISupports) mozInst );
    }    

    public static TreeWalkerImpl getDOMInstance(nsIDOMTreeWalker mozInst)
    {
        
        TreeWalkerImpl node = (TreeWalkerImpl) instances.get(mozInst);
        return node == null ? new TreeWalkerImpl(mozInst) : node;
    }

    public nsIDOMTreeWalker getInstanceAsnsIDOMTreeWalker()
    {
        if (moz==null)
            return null;
        else
            return (nsIDOMTreeWalker) moz.queryInterface(nsIDOMTreeWalker.NS_IDOMTREEWALKER_IID);
    }

    public void setCurrentNode(final Node currentNode)
    {
        //METHOD-BODY-START - autogenerated code
        final nsIDOMNode mozCurrentnode = currentNode!=null ? ((NodeImpl) currentNode).getInstance() : null;
        final Runnable r = new Runnable() { public void run() {
            getInstanceAsnsIDOMTreeWalker().setCurrentNode(mozCurrentnode);
        }};
        ThreadProxy.getSingleton().syncExec(r);
        //METHOD-BODY-END - autogenerated code
    }

    public NodeFilter getFilter()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<NodeFilter> c = new Callable<NodeFilter>() { public NodeFilter call() {
            nsIDOMNodeFilter result = getInstanceAsnsIDOMTreeWalker().getFilter();
            return new NodeFilterImpl(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node nextNode()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().nextNode();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node nextSibling()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().nextSibling();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node parentNode()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().parentNode();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node firstChild()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().firstChild();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node getRoot()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().getRoot();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node getCurrentNode()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().getCurrentNode();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node previousNode()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().previousNode();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node lastChild()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().lastChild();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public Node previousSibling()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Node> c = new Callable<Node>() { public Node call() {
            nsIDOMNode result = getInstanceAsnsIDOMTreeWalker().previousSibling();
            return (Node) NodeFactory.getNodeInstance(result);
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public boolean getExpandEntityReferences()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Boolean> c = new Callable<Boolean>() { public Boolean call() {
            boolean result = getInstanceAsnsIDOMTreeWalker().getExpandEntityReferences();
            return result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }

    public int getWhatToShow()
    {
        //METHOD-BODY-START - autogenerated code
        Callable<Integer> c = new Callable<Integer>() { public Integer call() {
            long result = getInstanceAsnsIDOMTreeWalker().getWhatToShow();
            return (int) result;
        }};
        return ThreadProxy.getSingleton().syncExec(c);
        //METHOD-BODY-END - autogenerated code
    }



}
