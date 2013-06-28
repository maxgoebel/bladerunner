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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.pdf.IPdfDocument;
import at.tuwien.prip.model.project.document.pdf.PDFDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;

import com.sun.pdfview.PDFFile;

/**
 * PdfBenchmarkItem.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 3, 2012
 */
@Entity
public class PdfBenchmarkDocument extends BenchmarkDocument
implements IPdfDocument
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1474288718668348609L;
	
	private int numPages;

	private PDFFile pdfFile;
	
	private List<PdfDocumentPage> pages;
		
	private List<AnnotationPage> annotationPages;
		
	/**
	 * Constructor.
	 */
	public PdfBenchmarkDocument (PDFDocument doc)
	{
		this();
		this.numPages = doc.getNumPages();
		this.pdfFile = doc.getPdfFile();
		this.bounds = doc.getBounds();
		this.name = doc.getName();
		this.uri = doc.getUri();
	}
	
	/**
	 * 
	 */
	public PdfBenchmarkDocument() 
	{
		this.format = DocumentFormat.PDF;
		this.annotationPages = new ArrayList<AnnotationPage>();
		this.pages = new ArrayList<PdfDocumentPage>();
	}
	
	
	public void setPdfFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
	}
	
	public PDFFile getPdfFile() {
		return pdfFile;
	}

	@Override
	public String toString() {
		return "PDF Benchmark Item: " + name;
	}
	
	public List<AnnotationPage> getAnnotationPages() {
		return annotationPages;
	}

	@Override
	public int getNumPages() {
		return numPages;
	}
	
	@Override
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	
	public void addPage(PdfDocumentPage page)
	{
		if (!pages.contains(page))
		{
			pages.add(page);
		}
	}
	
	public PdfDocumentPage getPage(int num) {
		for (PdfDocumentPage page : pages)
		{
			if (page.getPageNum()==num)
				return page;
		}
		return null;
	}

}
