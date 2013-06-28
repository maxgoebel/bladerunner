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
package at.tuwien.prip.model.project.document.benchmark;

import javax.persistence.Entity;

import org.w3c.dom.Document;

import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.html.IHtmlDocument;

/**
 * HTMLBenchmarkItem.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 9, 2012
 */
@Entity
public class HTMLBenchmarkDocument extends BenchmarkDocument
implements IHtmlDocument
{
	private Document cachedJavaDOM;
	
	private ISegmentGraph documentGraph;
	
	/**
	 * 
	 */
	public HTMLBenchmarkDocument() 
	{
		this.format = DocumentFormat.HTML;
	}
	
	public Document getCachedJavaDOM() {
		return cachedJavaDOM;
	}
	
	public void setCachedJavaDOM(Document cachedJavaDOM) {
		this.cachedJavaDOM = cachedJavaDOM;
	}
	
	public ISegmentGraph getDocumentGraph() {
		return documentGraph;
	}
	
	public void setDocumentGraph(ISegmentGraph documentGraph) {
		this.documentGraph = documentGraph;
	}
}
