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
package at.tuwien.prip.model.project.document;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.learning.example.SelectionExample;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;

import com.sun.pdfview.PDFFile;


/**
 * DocumentModel.java
 *
 *
 * created: Sep 13, 2009
 * @author Max Goebel
 */
public class DocumentModel 
{
	private String uri;
	
	private int x, y;
	
	private double scale = 0.6d;
	
	private Image thumb;
	
	private int pageNum;
	
	private int numPages;
	
	private ISegmentGraph graph;
	
	private DocumentFormat documentFormat;
	
	/* the document entry */
	private BenchmarkDocument document;
	
	private List<SelectionExample> examples;
	
	private PDFFile pdfFile;

	
	
	/**
	 * Constructor.
	 */
	public DocumentModel()
	{
		this.examples = new ArrayList<SelectionExample>();
	}
			
	public List<SelectionExample> getExamples() {
		return examples;
	}
	
	public void setExamples(List<SelectionExample> examples) {
		this.examples = examples;
	}

	public int getPageNum() {
		return pageNum;
	}

	public IDocument getDocument() {
		return document;
	}

	public DocumentFormat getFormat() {
		return documentFormat;
	}

	public int getNumPages() {
		return numPages;
	}
	
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	
	public void setDocument(BenchmarkDocument document) {
		this.document = document;
	}
	
	public void setFormat(DocumentFormat documentFormat) {
		this.documentFormat = documentFormat;
	}
	
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setDocumentGraph(ISegmentGraph graph) {
		this.graph = graph;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	public PDFFile getPdfFile() {
		return pdfFile;
	}

	/**
	 * 
	 * @return
	 */
	public Rectangle getBounds() 
	{
		if (document instanceof HTMLBenchmarkDocument)
		{
			return document.getBounds();
		}
		else if (document instanceof PdfBenchmarkDocument)
		{
			PdfBenchmarkDocument pdfDocument = (PdfBenchmarkDocument) document;
			if (pdfDocument.getPage(pageNum)==null)
				return null;
			Rectangle bbox = pdfDocument.getPage(pageNum).getBounds();
			return bbox;
		}
		return null;
	}

	public ISegmentGraph getDocumentGraph() {
		return graph;
	}

	public double getScale() {
		return scale;
	}

	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}

	public Image getThumb() {
		return thumb;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setPdfFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;		
	}

	public void setThumb(Image thumb) {
		this.thumb = thumb;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

}
