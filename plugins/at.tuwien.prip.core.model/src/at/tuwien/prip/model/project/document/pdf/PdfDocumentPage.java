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
package at.tuwien.prip.model.project.document.pdf;

import java.awt.Image;
import java.awt.Rectangle;

import at.tuwien.prip.model.graph.ISegmentGraph;

import com.sun.pdfview.PDFPage;

/**
 * PdfDocumentPage.java
 *
 * An individual page of a PDF Document.
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 16, 2013
 */
public class PdfDocumentPage {

	private int pageNum;
	
	private Image thumb;
	
	private PDFPage page;
	
	private ISegmentGraph graph;

	private Rectangle bounds;
	
	/**
	 * 
	 */
	public PdfDocumentPage(int pageNum, PDFPage page, Rectangle bounds) 
	{
		this.pageNum = pageNum;
		this.page = page;
		this.bounds = bounds;
	}
	
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public Image getThumb() {
		return thumb;
	}

	public void setThumb(Image thumb) {
		this.thumb = thumb;
	}

	public PDFPage getPage() {
		return page;
	}

	public void setPage(PDFPage page) {
		this.page = page;
	}
	
	public ISegmentGraph getGraph() {
		return graph;
	}
	
	public void setGraph(ISegmentGraph graph) {
		this.graph = graph;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
}
