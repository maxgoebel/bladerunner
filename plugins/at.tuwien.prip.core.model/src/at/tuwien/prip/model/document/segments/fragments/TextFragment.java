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

import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;




/**
 * Text fragment element; represents an atomic fragment corresponding
 * to one COS instruction
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class TextFragment extends CompositeSegment<CharSegment>
{

	boolean overprint = false;

    /**
     * Constructor.
     *
     * @param x1 The x1 coordinate of the segment.
     * @param x2 The x2 coordinate of the segment.
     * @param y1 The y1 coordinate of the segment.
     * @param y2 The y2 coordinate of the segment.
     * @param text The textual contents of the segment.
     * @param font The (main) font of the segment.
     * @param fontSize The (main) font size in the segment.
     */
    public TextFragment(
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
		super(x1, x2, y1, y2, text, fontName, fontSize, color);
    }
    
//    public TextFragment(
//            float x1,
//            float x2,
//            float y1,
//            float y2,
//            String text,
//            PDFont fontName,
//            float fontSize
//            )
//        {
//    		super(x1, x2, y1, y2, text, findFontName(font), fontSize);
//        }
    
    public TextFragment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
    }
    
    public TextFragment()
    {
		super();
    }
    
    public TextFragment(CharSegment c)
    {
		super(c.getX1(), c.getX2(), c.getY1(), c.getY2());
		items.add(c);
		text = c.getText();
		fontName = c.getFontName();
		fontSize = c.getFontSize();
		color = c.getColor();
    }

//	// not in current use (I think) -- WRONG
//    // now by default sets level to zero (primitive)
//    public TextFragment(TextPosition tPos)
//    {
//		super(tPos.getX(),
//			  tPos.getX() + (tPos.getWidth()),
//			  tPos.getY(),
//			  tPos.getY() + (tPos.getFontSize() * tPos.getYScale()),
//			  tPos.getCharacter(),
//			  findFontName(tPos.getFont()),
//			  tPos.getFontSize() * tPos.getYScale());
//
//
//    	// todo: trim the name of the font
//    	String fontName = tPos.getFont().getBaseFont();
//    }

	/**
     * This will create a TextFragment object from a TextPosition object.
     * As of PDFBox 0.7.2, this is the method currently in use, which
	 * converts co-ordinates back to the original system.
	 *
     * @param tPos - the TextPosition object; pageDim - page dimensions in order to
	 * convert co-ordinates
	 * @return The new TextFragment object
     */
    public TextFragment(
    		float x1, float x2, float y1, float y2,
    		String text,
    		String fontName,
    		float fontSize,
    		Color color,
    		GenericSegment pageDim)
    {
		super(x1, x2, y1, y2, text, fontName, fontSize, color);

//		tPos.getX(),
//		  tPos.getX() + tPos.getWidth(),
//		  pageDim.getY2() - tPos.getY(),
//		  pageDim.getY2() - tPos.getY() + (tPos.getFontSize() * tPos.getYScale()),
//		  tPos.getCharacter(),
//		  tPos.getFont().getBaseFont(),
//		  tPos.getFontSize() * tPos.getYScale()
//		);
		// uncomment to print the contents of all text fragments to the screen
		// System.out.println("Created text fragment: x1: " + tPos.getX() + " x2: " + (tPos.getX() + tPos.getWidth()) + " y1: " + tPos.getY() + " y2: " + (tPos.getY() + (tPos.getFontSize() * tPos.getYScale())) + " Text: " + text + " Font size: " + tPos.getFontSize() + " X Scale: " + tPos.getYScale() + " Y Scale: " + tPos.getYScale());

    	// todo: trim the name of the font
//    	String fontName = tPos.getFont().getBaseFont();
		/*
		this.xScale = tPos.getXScale();
		this.yScale = tPos.getYScale();
		this.widthOfSpace = tPos.getWidthOfSpace();
		this.wordSpacing = tPos.getWordSpacing();
		*/
    }

//    protected static String findFontName(PDFont font)
//    {
//    	if (font.getBaseFont().matches("^[A-Z]{6}\\+.+"))
//    		return font.getBaseFont().substring(7);
//    	else return font.getBaseFont();
//    }
    
	public boolean isOverprint() {
		return overprint;
	}

	public void setOverprint(boolean overprint) {
		this.overprint = overprint;
	}
    
    /*
     * with the tagName stuff this should not be necessary
     * 
    public void addAsXML(Document resultDocument, Element parent, GenericSegment pageDim,
    	float resolution)
    {
        //TODO: find a better name for this element?
        Element newSegmentElement = resultDocument.createElement("text-fragment");
        super.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
        parent.appendChild(newSegmentElement);
    }
    */
}
