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

import at.tuwien.prip.model.utils.Utils;



/**
 * Line segment document element; represents a ruling line on the page
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class LineSegment extends GenericSegment
{
    public static final int DIR_HORIZ = 1;
    public static final int DIR_VERT = 2;
    public static final int DIR_OTHER = 0;
    
    protected Color color;
    boolean isCurve = false;
    
    protected int direction;
    
	/**
     * Constructor.
     *
     * @param x1 The x1 coordinate of the segment.
     * @param x2 The x2 coordinate of the segment.
     * @param y1 The y1 coordinate of the segment.
     * @param y2 The y2 coordinate of the segment.
     */
    public LineSegment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
        
        // TODO: variance is hard-coded as 1pt -- make it relative to something...
        
        if (Utils.within(y1, y2, 1.0f)) // horizontal
        {
            direction = DIR_HORIZ;
//            l1 = x1;
//            l2 = x2;
//            t = (y1 + y2) / 2;
        }
        else if (Utils.within(x1, x2, 1.0f)) // vertical
        {
            direction = DIR_VERT;
//            l1 = y1;
//            l2 = y2;
//            t = (x1 + x2) / 2;
        }
        else // angled
        {
            // TODO: throw exception here, if an attempt is made to
            // access the co-ordinates?
            direction = DIR_OTHER;
//            l1 = -1;
//            l2 = -1;
//            t = -1;
        }
    }

    public String getNodeText()
    {
    	return "[Line]";
    }
    

    /**
     * @return Returns the direction.
     */
    public int getDirection() {
        return direction;
    }
    

    /**
     * @param direction The direction to set.
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
    

    /**
     * @return Returns the l1.
     */
    public float getL1() {
    	if (direction == DIR_HORIZ) // horizontal
        {
        	return x1;
        }
        else if (direction == DIR_VERT) // vertical
        {
        	return y1;
        }
        else // angled
        {
        	return -1;
        }
    }
    

    /**
     * @return Returns the l2.
     */
    public float getL2() {
    	if (direction == DIR_HORIZ) // horizontal
        {
        	return x2;
        }
        else if (direction == DIR_VERT) // vertical
        {
        	return y2;
        }
        else // angled
        {
            // TODO: throw exception here, if an attempt is made to
            // access the co-ordinates?
        	return -1;
        }
    }
    

    /**
     * @return Returns the t.
     */
    public float getT() {
    	if (direction == DIR_HORIZ) // horizontal
        {
    		return (y1 + y2) / 2;
        }
        else if (direction == DIR_VERT) // vertical
        {
        	return (x1 + x2) / 2;
        }
        else // angled
        {
            // TODO: throw exception here, if an attempt is made to
            // access the co-ordinates?
        	return -1;
        }
    }
    

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public boolean isCurve() {
		return isCurve;
	}

	public void setCurve(boolean isCurve) {
		this.isCurve = isCurve;
	}
}
