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
package at.tuwien.prip.model.learning.example;

import org.w3c.dom.Node;

import at.tuwien.prip.model.BinaryClass;
import at.tuwien.prip.model.project.selection.NodeSelection;

public class NodeExample extends AbstractBinaryExample
{
	private NodeSelection nodeSelection;
	
	/**
	 * Constructor.
	 * @param node
	 */
	public NodeExample(NodeSelection selection, BinaryClass classification)
	{
//		this.selectedNode = node;
//		this.targetXpath = DOMHelper.XPath.getExactXPath(node);
		this.nodeSelection = selection;
		this.classification = classification;
	}
	
	public NodeSelection getNodeSelection() {
		return nodeSelection;
	}
	
	public Node getSelectedNode() {
		return nodeSelection.getSelectedNode();
	}
	
	public Node getRootNode()
	{
		return nodeSelection.getRoot();
	}
}
