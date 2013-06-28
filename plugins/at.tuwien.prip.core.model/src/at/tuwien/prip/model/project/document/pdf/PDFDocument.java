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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import at.tuwien.prip.model.project.document.AbstractDocument;
import at.tuwien.prip.model.project.document.DocumentFormat;

import com.sun.pdfview.PDFFile;

/**
 * PDFDocument.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * May 20, 2012
 */
public class PDFDocument extends AbstractDocument
implements IPdfDocument
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7892878062409340943L;
	
	protected int numPages;
	
	@Transient
	private PDFFile pdfFile;

	private List<PdfDocumentPage> pages;
	
	/**
	 * Constructor.
	 */
	public PDFDocument() 
	{
		this.setFormat(DocumentFormat.PDF);
		this.pages = new ArrayList<PdfDocumentPage>();
	}
	
	@Override
	public Rectangle getBounds()
	{
		if (pdfFile==null) 
			return null;
		Rectangle2D pageBox = pdfFile.getPage(0).getPageBox();
		return new Rectangle(
				(int)pageBox.getX(), 
				(int)pageBox.getY(), 
				(int)pageBox.getWidth(), 
				(int)pageBox.getHeight());
	}

	public int getNumPages() {
		return numPages;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public PDFFile getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
	}
	
	public List<PdfDocumentPage> getPages() {
		return pages;
	}
	
}
