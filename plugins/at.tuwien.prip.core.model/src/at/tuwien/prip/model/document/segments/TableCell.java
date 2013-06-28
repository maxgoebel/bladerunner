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
package at.tuwien.prip.model.document.segments;

import java.awt.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * This represents a table cell and a position on the screen of the cell
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 *
 */
public class TableCell extends TextBlock 
{
	//instance variables
	private int colspan = 1;
	private int rowspan = 1;
	
	public TableCell(
        float x1,
        float x2,
        float y1,
        float y2,
        String text,
        String fontName,
        float fontSize,
        Color color,
        int colspan,
        int rowspan
        )
    {
		super(x1, x2, y1, y2,text,fontName,fontSize,color);
		
		this.colspan = colspan;
		this.rowspan = rowspan;
    }

    public TableCell(
        float x1,
        float x2,
        float y1,
        float y2,
        String text,
        String fontName,
        float fontSize,
        Color color
        )
    {
		super(x1, x2, y1, y2,text,fontName,fontSize,color);
    }
        
    public TableCell(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
    }
        

/*  2011-01-24 TODO: decide how to create constructors
 *  or factory methods to "convert" e.g. TextBlock to TableCell, etc.  
    public TableCell(
		TextSegment seg,
		int colspan,
		int rowspan)
    {
//    	super(seg);
    	super();
    	this.colspan = colspan;
        this.rowspan = rowspan;
    }
*/
    
    public TableCell()
    {
        super();
    }


	public int getColspan() 
	{
		return colspan;
	}


	public int getRowspan() 
	{
		return rowspan;
	}


	public void setColspan(int colspan) 
	{
		this.colspan = colspan;
	}


	public void setRowspan(int rowspan) 
	{
		this.rowspan = rowspan;
	}
	
	public String toString() 
	{
		return ("TableCell - text: " + text + 
			" x1: " + super.x1 + 
			" x2: " + super.x2 +
			" y1: " + super.y1 +
			" y2: " + super.y2);
	}
	
	
	public String getAttributes()
	{
		return ("colspan: " + colspan + " rowspan: " + rowspan + super.getAttributes());
	}
	
    protected void setElementAttributes
        (Document resultDocument, Element newSegmentElement, GenericSegment pageDim, 
        	float resolution)
    {
        if (this.getColspan() > 1) {
        	newSegmentElement.setAttribute
            ("colspan", Integer.toString(this.getColspan()));
        }
        
        if (this.getRowspan() > 1) {
        	newSegmentElement.setAttribute
            ("rowspan", Integer.toString(this.getRowspan()));
        }

        super.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
    }
}
