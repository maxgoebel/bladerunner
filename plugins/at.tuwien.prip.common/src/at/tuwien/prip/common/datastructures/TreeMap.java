/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.common.datastructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import at.tuwien.prip.common.utils.Counter;


/**
 * 
 * TreeMap.java
 *
 *
 *
 * Created: Jun 13, 2009 8:18:08 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class TreeMap<T> {

	private BidiMap<T,TreeNode> content2node;

	private Counter nodeIDCount = new Counter();

	/**
	 * 
	 * Constructor.
	 *
	 */
	public TreeMap() {
		content2node = new BidiMap<T, TreeNode>();
	}

	/**
	 * 
	 * The size of this tree map.
	 * 
	 * @return
	 */
	public int size () {
		return content2node.keySetA().size();
	}

	public void addAsRoot(T root) {
		if (content2node.get(root)==null) {
			TreeNode p = new TreeNode(root);
			content2node.put(root, p);
		}
	}

	/**
	 * 
	 * Add object child as a child to 
	 * object parent.
	 * 
	 * @param parent
	 * @param child
	 */
	public void addChildBelow (T child, T parent) {

		TreeNode p = null;
		if (parent!=null) {
			p = content2node.get(parent);
			if (p==null) {
				p = new TreeNode(parent);
				content2node.put(parent, p);
			}
		}

		TreeNode c =null;
		if (child!=null) {
			c = content2node.get(child);
			if (c==null) {
				c = new TreeNode(child);
				content2node.put(child, c);
			} 
		}

		//connect
		if (p!=null && c!=null) {
			p.addChild(c);
		}
	}

	/**
	 * 
	 * Get the child objects of an object.
	 * 
	 * @param node
	 * @return
	 */
	public List<T> getChildrenOf(T object) {
		List<T> result = new LinkedList<T>();
		TreeNode n = content2node.get(object);
		if (n!=null) {
			for (TreeNode child : n.children) {
				result.add(child.content);
			}
		}
		return result;
	}

	public T getParentOf (T node) {
		return null;
	}

	/**
	 * Get all root nodes in this forest.
	 * @return
	 */
	public List<TreeNode> getRoots() {
		List<TreeNode> result = new LinkedList<TreeNode>();
		for (TreeNode node : content2node.keySetB()) {

			while (node.getParent()!=null) {
				node = node.getParent();
			}
			if (node.getParent()==null) {
				if (!result.contains(node)) {
					result.add(node);
				}
			}

		}
		return result;
	}

	/**
	 * Get the contents of the root nodes.
	 * @return
	 */
	public List<T> getRootContents() {
		List<T> result = new LinkedList<T>();
		for (TreeNode node : content2node.keySetB()) {
			if (node.parent==null) {
				result.add(node.content);
			}
		}
		return result;
	}

	/**
	 * Get all leaf nodes in this tree.
	 * @return
	 */
	public List<T> getLeafs() {
		List<T> result = new LinkedList<T>();
		Iterator<T> it = depthFirstSearchContent();
		while (it.hasNext()) {
			T next = it.next();
			TreeNode node = content2node.get(next);
			if (!node.hasChildren()) {
				result.add(next);
			}
		}
		return result;
	}

	/**
	 * Get all nodes in this tree.
	 * @return
	 */
	public List<T> getAllNodes() {
		List<T> result = new LinkedList<T>();
		Iterator<T> it = depthFirstSearchContent();
		while (it.hasNext()) {
			T next = it.next();
			result.add(next);
		}
		return result;
	}
	/**
	 * Get the unique ID of a content node.
	 * @param content
	 * @return
	 */
	public int getID (T content) {
		TreeNode n = content2node.get(content);
		if (n!=null) return n.id;

		return -1;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		List<TreeNode> nodeHist = new LinkedList<TreeNode>();
		Stack<TreeNode> stack = new Stack<TreeNode>();
		stack.addAll(getRoots());

		while (!stack.isEmpty()) {
			TreeNode node = stack.pop();
			if (nodeHist.size()>0 && node.equals(nodeHist.get(nodeHist.size()-1))) {
				nodeHist.remove(nodeHist.size()-1);
				sb.append("}\n");
				continue;
			}
			sb.append(node.toString() + "\n");

			if (node.getChildren().size()>0) {
				sb.append("{");
				stack.push(node);
				nodeHist.add(node); //readd as marker
			}

			stack.addAll(node.getChildren());
		}
		return sb.toString();
	}


	public String toString2() {
		StringBuffer sb = new StringBuffer();

		List<TreeNode> nodeHist = new LinkedList<TreeNode>();
		Stack<TreeNode> stack = new Stack<TreeNode>();
		stack.addAll(getRoots());

		while (!stack.isEmpty()) {
			TreeNode node = stack.pop();
			if (nodeHist.size()>0 && node.equals(nodeHist.get(nodeHist.size()-1))) {
				nodeHist.remove(nodeHist.size()-1);
				sb.append("}\n");
				continue;
			}
			if (node.getChildren().size()==1) {
				if (node.getParent()==null) {
					sb.append("$");	
				}
				sb.append(node.toString());
				sb.append("::"+node.getChildren().get(0).toString());
				sb.append("\n");
			}
			else if (node.getChildren().size()>1) {
				if (node.getParent()==null) {
					sb.append("$");	
				}
				sb.append(node.toString());
				sb.append("::{");
				stack.push(node);
				nodeHist.add(node); //readd as marker
				sb.append("\n");
			} else { //leaf
				if (node.getParent()==null) {
					sb.append("$");	
				}
				sb.append(node.toString()+"$\n");
			}

			stack.addAll(node.getChildren());
		}
		return sb.toString();
	}

	/**
	 *
	 * A node instance hiding away the trees
	 * object. This gets encapsulated by the
	 * tree accessor.
	 * 
	 */
	public class TreeNode {

		T content; //the content object of this node
		int id; //a unique id
		int depth; //the depth of this node
		double weight; //a weight associated with this node
		TreeNode parent;
		List<TreeNode> children;

		//		TreeNode next; //defines link to successor node
		//		TreeNode previous; //defines link to predecessor node


		public TreeNode () {
			content = null;
			parent = null;
			children = new LinkedList<TreeNode>();
		}

		public TreeNode(T content) {
			this();
			this.id = (int) nodeIDCount.get();
			this.content = content;
			this.depth = 0;
		}

		public boolean hasChildren() {
			return this.children==null||this.children.size()==0?false:true;
		}

		public void addChild (TreeNode child) {
			if (!children.contains(child)) {
				children.add(child);
				child.parent = this;
				child.depth = depth+1;
			}
		}

		@Override
		public boolean equals(Object obj) {
			return this.hashCode()==obj.hashCode();
		}

		@Override
		public int hashCode() {
			return content==null? 0 : content.hashCode();
		}

		/**
		 * @return the children
		 */
		public List<TreeNode> getChildren() {
			return children;
		}

		/**
		 * @return the parent
		 */
		public TreeNode getParent() {
			return parent;
		}

		@Override
		public String toString() {
			return content.toString();
		}

		public T getContent() {
			return content;
		}

		public int getId() {
			return id;
		}

		public int getDepth() {
			return depth;
		}

		public double getWeight() {
			return weight;
		}

	}//TreeNode

	private List<T> dfsList;
	private List<TreeNode> dfsNodeList;

	public Iterator<T> depthFirstSearchContent () {
		dfsList = new ArrayList<T>();
		dfsNodeList = new ArrayList<TreeMap<T>.TreeNode>();
		for (TreeNode node : getRoots()) {
			depthFirstSearchRec(node);
			dfsList.add(node.content);
			dfsNodeList.add(node);
		}
		return dfsList.iterator();
	}

	public Iterator<TreeNode> depthFirstSearchNode () {
		dfsList = new ArrayList<T>();
		dfsNodeList = new ArrayList<TreeMap<T>.TreeNode>();
		for (TreeNode node : getRoots()) {
			depthFirstSearchRec(node);
			dfsList.add(node.content);
			dfsNodeList.add(node);
		}
		return dfsNodeList.iterator();
	}

	private void depthFirstSearchRec (TreeNode node) {

		for (TreeNode child : node.getChildren()) {
			if (child.hasChildren()) {
				depthFirstSearchRec(child);//recurse
			}
			dfsList.add(child.content);
			dfsNodeList.add(child);
		}
	}

	public static void main(String[] args) {
		TreeMap<String> tree = new TreeMap<String>();
		tree.addChildBelow(null, "a");
		tree.addChildBelow("b", "a");
		tree.addChildBelow("c", "b");
		tree.addChildBelow(null, "d");
		tree.addChildBelow("d", "f");
		tree.addChildBelow(null, "g");
		tree.addAsRoot("f");
		tree.addAsRoot("h");
		
		List<?> roots = tree.getRoots();
		int size = tree.content2node.size();
		List<String> stringList = new ArrayList<String>();
		Iterator<String> it = tree.depthFirstSearchContent();
		while (it.hasNext()) {
			stringList.add(it.next());
		}
		String tree2 = tree.toString2();
		System.out.println(tree2);

	}

}//Tree
