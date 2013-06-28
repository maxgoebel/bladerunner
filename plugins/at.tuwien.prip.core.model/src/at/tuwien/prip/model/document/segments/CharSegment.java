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


/**
 * Text fragment element; represents a single character
 * This class is identical in functionality to TextFragment!
 * ... however, does not extend it in order to allow typing
 * of lists ...
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class CharSegment extends TextSegment
{
	boolean overprint = false;

	private OpTuple sourceOp;

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
	public CharSegment(
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

	/**
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param text
	 * @param fontName
	 * @param fontSize
	 * @param sourceOp
	 */
	public CharSegment(
			float x1,
			float x2,
			float y1,
			float y2,
			String text,
			String fontName,
			float fontSize,
			Color color,
			OpTuple sourceOp
	)
	{
		super(x1, x2, y1, y2, text, fontName, fontSize, color);
		this.sourceOp = sourceOp;
	}

	/**
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public CharSegment(
			float x1,
			float x2,
			float y1,
			float y2
	)
	{
		super(x1, x2, y1, y2);
	}

	public String getAttributes()
	{
		return super.getAttributes() + " opIndex: " + sourceOp.getOpIndex() + 
		" argIndex: " + sourceOp.getArgIndex();
	}

	//	// not in current use (I think) -- WRONG
	//    // now by default sets level to zero (primitive)
	//    public CharSegment(TextPosition tPos)
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

	//	/**
	//     * This will create a TextFragment object from a TextPosition object.
	//     * As of PDFBox 0.7.2, this is the method currently in use, which
	//	 * converts co-ordinates back to the original system.
	//	 *
	//     * @param tPos - the TextPosition object; pageDim - page dimensions in order to
	//	 * convert co-ordinates
	//	 * @return The new TextFragment object
	//     */
	//    public CharSegment(TextPosition tPos, GenericSegment pageDim)
	//    {
	//		super(tPos.getX(),
	//			  tPos.getX() + tPos.getWidth(),
	//			  pageDim.getY2() - tPos.getY(),
	//			  pageDim.getY2() - tPos.getY() + (tPos.getFontSize() * tPos.getYScale()),
	//			  tPos.getCharacter(),
	//			  tPos.getFont().getBaseFont(),
	//			  tPos.getFontSize() * tPos.getYScale());
	//
	//		// uncomment to print the contents of all text fragments to the screen
	//		// System.out.println("Created text fragment: x1: " + tPos.getX() + " x2: " + (tPos.getX() + tPos.getWidth()) + " y1: " + tPos.getY() + " y2: " + (tPos.getY() + (tPos.getFontSize() * tPos.getYScale())) + " Text: " + text + " Font size: " + tPos.getFontSize() + " X Scale: " + tPos.getYScale() + " Y Scale: " + tPos.getYScale());
	//
	//    	// todo: trim the name of the font
	//    	String fontName = tPos.getFont().getBaseFont();
	//		/*
	//		this.xScale = tPos.getXScale();
	//		this.yScale = tPos.getYScale();
	//		this.widthOfSpace = tPos.getWidthOfSpace();
	//		this.wordSpacing = tPos.getWordSpacing();
	//		*/
	//    }

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

	public void setSourceOp(OpTuple sourceOp) {
		this.sourceOp = sourceOp;
	}

	public OpTuple getSourceOp() {
		return sourceOp;
	}
}
