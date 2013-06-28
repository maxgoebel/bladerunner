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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.tuwien.prip.model.utils.Utils;


/**
 * This represents a text segment (base class)
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class TextSegment extends GenericSegment
{
	// only for str conversions -- is not saved or represented in graph
	protected boolean isUnderlined;
	protected int superSubscript = 0; // -1 for subscript, 1 for superscript, 0 for normal

    protected String text;
//  protected PDFont segFont;
    protected float fontSize;
    protected String fontName;

    protected Color color;

    public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

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
    public TextSegment(
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
		super(x1, x2, y1, y2);
    	this.text = text;
//		this.segFont = font;
		// TODO: remove subsetting preceding chars
		this.fontName = fontName;
		this.color = color;
		this.setFontSize(fontSize); // also sets node text font
    }

    /**
     * Constructor.
     *
     * @param bounds
     * @param text
     */
    public TextSegment(Rectangle bounds, String text) {
    	super(bounds);
    	this.text = text;
    }

    public TextSegment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
        this.text = Utils.EMPTY_STRING;
    }

    public TextSegment()
    {
        super();
        this.text = Utils.EMPTY_STRING;
    }

	/**
     * This will get the text of the segment.
     *
     * @return The text.
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param scale The text to set.
     */
    public void setText(String text)
    {
    	this.text = text;
    }

    /**
     * This will get the font size of the segment.
     *
     * @return The font size of the segment.
     */
    public float getFontSize()
    {
        return fontSize;
    }

    /**
     * @param scale The font size to set.
     */
    public void setFontSize(float fontSize)
    {
        this.fontSize = fontSize;
    }

    /**
     * This will return whether the text segment is 'empty', i.e.
	 * contains just an empty string or a string with only spaces
     *
     * @return TRUE if empty
     */
    public boolean isEmpty()
    {
        if (text != null)
            return (text.trim().length() == 0);
        else
            return false;
    }

    public String getAttributes()
    {
    	// modified 6.01.06
    	//return ("text: " + text + " " + super.getAttributes() +
    	//	" fontsize: " + fontSize + " font: " + font);

    	return (super.getAttributes() +
    			" \n    text: " + text + " " +
    			" \n    fontsize: " + fontSize + " font: " + fontName +
    			" bold: " + isBold() + " italic: " + isItalic() + " hc: " + hashCode());
    }

    public boolean isBold()
    {
    	if (fontName == null) return false;

    	if (Utils.containsSubstring(fontName, "Bold") ||
    		Utils.containsSubstring(fontName, "bold") ||
    		Utils.containsSubstring(fontName, "Black") ||
    		Utils.containsSubstring(fontName, "black") ||
    		Utils.containsSubstring(fontName, "Heavy") ||
    		Utils.containsSubstring(fontName, "heavy"))
    		return true;
    	else return false;
    }

    public boolean isItalic()
    {
    	if (fontName == null) return false;

    	if (Utils.containsSubstring(fontName, "Italic") ||
    		Utils.containsSubstring(fontName, "italic") ||
    		Utils.containsSubstring(fontName, "Cursive") ||
    		Utils.containsSubstring(fontName, "cursive") ||
    		Utils.containsSubstring(fontName, "Kursiv") ||
    		Utils.containsSubstring(fontName, "kursiv"))
    		return true;
    	else return false;
    }

    public boolean isCapitals()
    {
    	// TODO: 22.01.2011 include regexp or remove this method! used anywhere?
    	// doesn't include lower-case A-Z
    	return false;
    }

    public boolean isNumeric()
    {
    	// TODO: 22.01.2011 include regexp or remove this method! used anywhere?
    	// doesn't include A-Z/a-z
    	return false;
    }

	/*
    public void addAsXML(Document resultDocument, Element parent, GenericSegment pageDim,
    	float resolution)
    {
    	// TODO: inelegant solution
    	//if (potentialTable) tagName = "potential-table-cell";

        //TODO: find a better name for this element?
        Element newSegmentElement = resultDocument.createElement(getTagName());
        this.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
        parent.appendChild(newSegmentElement);
    }
    */

    protected void setElementAttributes
        (Document resultDocument, Element newSegmentElement, GenericSegment pageDim,
        float resolution)
    {
//    	float fontSizeRatio = (resolution / SCREEN_RESOLUTION) *
//    		FONT_SIZE_RATIO;
//    	22.01.2011 why font size ratio?

    	newSegmentElement.setAttribute
        	("font-name", this.getFontName());
        newSegmentElement.setAttribute
            ("font-size", Float.toString(this.getFontSize())); // * fontSizeRatio));
        newSegmentElement.setAttribute
        	("bold", Boolean.toString(isBold()));
        newSegmentElement.setAttribute
        	("italic", Boolean.toString(isItalic()));
        //newSegmentElement.setAttribute
        //    ("text-ratio", Float.toString(this.getTextRatio()));

        // XMIllum does not like 0-length text
        // this method will allow us to easily see when this might happen!
        //System.out.println("foobar");
        if (this.isEmpty())
        {
            if (this.getText().length() > 0)
            {
                newSegmentElement.appendChild
                    (resultDocument.createTextNode("[empty:spaces]"));
            }
            else
            {
                newSegmentElement.appendChild
                    (resultDocument.createTextNode("[empty:empty]"));
            }
        }
        else
        {
        	//if (this.getClass() == TableCell.class)
        	//System.out.println("class: " + this.getClass());
        	//System.out.println("appending text: " + Utils.removeInvalidXMLCharacters(this.getSegText()));
            //resultDocument.removeTextNode();
        	newSegmentElement.appendChild
                (resultDocument.createTextNode(Utils.removeInvalidXMLCharacters(this.getText())));
        }
        super.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
    }

    public boolean isUnderlined() {
		return isUnderlined;
	}

	public void setUnderlined(boolean isUnderlined) {
		this.isUnderlined = isUnderlined;
	}

	public int getSuperSubscript() {
		return superSubscript;
	}

	public void setSuperSubscript(int superSubscript) {
		this.superSubscript = superSubscript;
	}

	public Color getColor() {
		return color;
	}
}
