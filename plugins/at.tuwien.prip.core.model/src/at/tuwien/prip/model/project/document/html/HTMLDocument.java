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
package at.tuwien.prip.model.project.document.html;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.w3c.dom.Document;

import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.document.AbstractDocument;
import at.tuwien.prip.model.project.document.DocumentFormat;

/**
 * WebDocument.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * May 20, 2012
 */
@Entity
public class HTMLDocument extends AbstractDocument
implements IHtmlDocument
{
	/**
	 *
	 */
	@Transient
	private static final long serialVersionUID = 416499213966733310L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private Document cachedJavaDOM;

//	/* browser triggers */
//	private IBrowserTrigger[] triggers;

	@Transient
	private ISegmentGraph documentGraph;

	/**
	 * Constructor.
	 */
	public HTMLDocument()
	{
		this.setFormat(DocumentFormat.HTML);
	}

//	@Override
//	public Rectangle getBounds()
//	{
//		Document doc = getCachedJavaDOM();
//		if (doc==null) {
//			return null;
//		}
//		java.awt.Rectangle bounds = MozCssUtils.getBBoxRectangle(doc.getDocumentElement());
//		return new Rectangle(
//				(int)bounds.getX(),
//				(int)bounds.getY(),
//				(int)bounds.getWidth(),
//				(int)bounds.getHeight());
//	}
//
//	public Dimension getDimension()
//	{
//		if (bounds==null)
//		{
//			Document doc = getCachedJavaDOM();
//			java.awt.Rectangle bounds = MozCssUtils.getBBoxRectangle(doc.getDocumentElement());
//			return new Dimension(
//					(int)bounds.getWidth(),
//					(int)bounds.getHeight());
//		}
//		return null;
//	}

	public Document getCachedJavaDOM() {
		return cachedJavaDOM;
	}

	public void setCachedJavaDOM(Document cachedJavaDOM) {
		this.cachedJavaDOM = cachedJavaDOM;
	}

	public ISegmentGraph getDocumentGraph() {
		return documentGraph;
	}

	public void setDocumentGraph(ISegmentGraph graph) {
		this.documentGraph = graph;
	}

}
