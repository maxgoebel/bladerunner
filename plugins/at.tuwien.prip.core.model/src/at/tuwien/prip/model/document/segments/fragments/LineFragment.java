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
package at.tuwien.prip.model.document.segments.fragments;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.document.segments.CompositeSegment;


/**
 * LineFragment document element
 * largest granularity with a single font/size combination
 * contains TextSegments (including indents...)
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class LineFragment extends CompositeSegment<TextFragment> // <TextSegment>?
{
//	List<TextSegment> items;
	
	public LineFragment()
	{
		super();
		this.items = new ArrayList<TextFragment>();
	}
	
    public LineFragment(
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
		super(x1, x2, y1, y2, text, fontName, fontSize,color);
		this.items = new ArrayList<TextFragment>();
    }
    
    public LineFragment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
		this.items = new ArrayList<TextFragment>();
    }

    public LineFragment(
        float x1,
        float x2,
        float y1,
        float y2,
        String text,
        String fontName,
        float fontSize,
        Color color,
		List<TextFragment> items
        )
    {
//		super(x1, x2, y1, y2, text, fontName, fontSize, items);
    	super(x1, x2, y1, y2, text, fontName, fontSize, color);
    	this.items = items;
    }
    
    public LineFragment(
        float x1,
        float x2,
        float y1,
        float y2,
		List<TextFragment> items
        )
    {
//		super(x1, x2, y1, y2, items);
    	super(x1, x2, y1, y2);
    	this.items = items;
    }
}
