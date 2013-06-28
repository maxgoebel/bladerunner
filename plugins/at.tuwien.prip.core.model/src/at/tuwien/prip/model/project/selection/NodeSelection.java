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
package at.tuwien.prip.model.project.selection;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.tuwien.prip.model.utils.DOMHelper;

/**
 * NodeSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class NodeSelection extends AbstractSelection
{	
	@Transient
	private Document document;
	
	private String documentUri;
	
	private String targetXPath;

	private String rootXPath;
	
	@Transient
	private Node selectedNode;
	
	@Transient
	private Node root;
	
	public NodeSelection() 
	{
		super("NODE");
	}
	
	/**
	 * Constructor.
	 * @param node
	 * @param doc
	 */
	public NodeSelection(Node node, Document doc)
	{
		this();
		this.document = doc;
		this.documentUri = doc.getDocumentURI();
		this.root = doc.getDocumentElement();
		this.rootXPath = DOMHelper.XPath.getExactXPath(root);
		this.selectedNode = node;
		this.targetXPath = DOMHelper.XPath.getExactXPath(node);
	}
	
	public void setTargetXPath(String targetXpath) {
		this.targetXPath = targetXpath;
	}

	public String getTargetXPath() {
		return targetXPath;
	}

	public void setSelectedNode(Node selectedNode) {
		this.selectedNode = selectedNode;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}

	public String getRootXPath() {
		return rootXPath;
	}
	
	public void setRootXPath(String rootXPath) {
		this.rootXPath = rootXPath;
	}
	
	public String getDocumentUri() {
		return documentUri;
	}
	
	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}

	public void setDocument(Document doc) {
		this.document = doc;
	}
	
	public Document getDocument() {
		return document;
	}

	public void setRoot(Element root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
}
