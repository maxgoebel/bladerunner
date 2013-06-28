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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * Rectangular segment document element; represents
 * a rectangle drawn using ruling lines on the page
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class RectSegment extends GenericSegment
{
	/**
     * Constructor.
     *
     * @param x1 The x1 coordinate of the segment.
     * @param x2 The x2 coordinate of the segment.
     * @param y1 The y1 coordinate of the segment.
     * @param y2 The y2 coordinate of the segment.
     */
	
	protected Color fillColor;
	protected Color lineColor;
	protected boolean isFilled;
	
	/**
	 * Constructor.
	 */
	public RectSegment() {
	}
	
	/**
	 * Constructor.
	 * @param r
	 */
	public RectSegment(Rectangle r)
	{
		this(r.x, r.x+r.width, r.y, r.y+r.height);
	}
	
    public RectSegment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
		this.isFilled = false;
    }
    
    public List<LineSegment> toLines()
    {
    	ArrayList<LineSegment> retVal = new ArrayList<LineSegment>();
    	
    	// if this rectangle is just a THICK LINE
    	if (this.getWidth() < 3.0f || this.getHeight() < 3.0f)
    	{
    		// the constructor of Line automatically
    		// determines its direction.
            retVal.add(new LineSegment(this.getX1(), this.getX2(), 
                    this.getY1(), this.getY2()));
            return retVal;
    	}
    	else
    	{
    		// add top line
    		retVal.add(new LineSegment(x1, x2, y2, y2));
    		// add bottom line
    		retVal.add(new LineSegment(x1, x2, y1, y1));
    		// add left line
    		retVal.add(new LineSegment(x1, x1, y1, y2));
    		// add right line
    		retVal.add(new LineSegment(x2, x2, y1, y2));
    		return retVal;
    	}
    }

	public Color getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(Color fillColor)
	{
		this.fillColor = fillColor;
	}

	public boolean isFilled()
	{
		return isFilled;
	}

	public void setFilled(boolean isFilled)
	{
		this.isFilled = isFilled;
	}

	public Color getLineColor()
	{
		return lineColor;
	}

	public void setLineColor(Color lineColor)
	{
		this.lineColor = lineColor;
	}
	
	public String generateSegmentName()
	{
		if (isFilled)
			return "filled-rect";
		else return super.generateSegmentName();
	}
}
