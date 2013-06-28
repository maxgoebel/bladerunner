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
package at.tuwien.prip.model.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.semantics.SemanticText;
import at.tuwien.prip.model.graph.base.BaseNode;
import at.tuwien.prip.model.utils.Utils;



/**
 * Node in the document graph
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDFAnalyser 0.9
 */
public class DocNode extends BaseNode 
implements Cloneable
{

	// fields from Generic/TextSegment, which are copied:
	protected SegmentType segType;
	protected float segX1, segX2, segY1, segY2;
	protected float volume;

	protected String segText;
	protected String segFontName;
	protected float segFontSize;

	private Font font;

	protected Color color;
	
	/**
	 * Constructor.
	 * 
	 * @param gs
	 */
	public DocNode(GenericSegment gs)
	{
		super();
		this.setFields(gs);
	}

//	/**
//	 * Constructor.
//	 * @param state
//	 */
//	public DocNode(AgentState state) 
//	{
//		Rectangle dim = state.getBounds();
//		segX1 = (int)dim.getMinX();
//		segX2 = (int)dim.getMaxX();
//		segY1 = (int)dim.getMinY();
//		segY2 = (int)dim.getMaxY();
//		segText = state.getGraph().serializeText();
//		segType = state.getSegType();
//	}
//	
	@Override
	public boolean equals(Object obj) 
	{
		DocNode other = (DocNode) obj;
		return (this.segType.equals(other.segType) && 
				this.getSegX1()==other.getSegX1() &&
				this.getSegX2()==other.getSegX2() &&
				this.getSegY1()==other.getSegY1() &&
				this.getSegY2()==other.getSegY2());
	}
	
	/**
	 * also sets text fields for text segments
	 * @param gs
	 */
	public void setFields(GenericSegment gs)
	{
		segX1 = gs.getX1();
		segX2 = gs.getX2();
		segY1 = gs.getY1();
		segY2 = gs.getY2();

//		String tmp = gs.generateSegmentName();
		segType = gs.generateSegmentType();

		volume = (segX2-segX1) * (segY2-segY1);

		semanticAnnotations = new ArrayList<SemanticText>();
		layoutAnnotations = new ArrayList<SegmentType>();

		if (gs instanceof TextSegment)
		{
			TextSegment ts = (TextSegment)gs;
			segText = ts.getText();
			this.label = segText;
			segFontName = ts.getFontName();
			segFontSize = ts.getFontSize();
			int segFontStyle = Font.PLAIN;
			if (segFontName!=null) {
				if (segFontName.toUpperCase().indexOf("BOLD")>0) 
				{
					segFontStyle = Font.BOLD;
				}
				else if (segFontName.toUpperCase().indexOf("ITALIC")>0) 
				{
					segFontStyle = Font.ITALIC;
				}
			}
			
			Font f = new Font(segFontName, segFontStyle, (int)segFontSize);
			setFont(f);
			
			this.color = ts.getColor();
		}
	}

	public List<SegmentType> getLayoutAnnotations() {
		return layoutAnnotations;
	}

	/**
	 * 
	 */
	public String toString()
	{

		return ("\nDocNode: text: " + segText + " \nx1: " + segX1 + " x2: " + segX2 + " y1: " + segY1
				+ " y2: " + segY2 + " \nsegType: " + segType);
	}

	/**
	 * 
	 * @return
	 */
	public String toShortString() {
		return "["+segText.substring(0,30) + "...]";
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTextSegment2 () 
	{
		if (SegmentType.Word.equals(getSegType()) ||
				SegmentType.Block.equals(getSegType()) ||
				SegmentType.Textline.equals(getSegType())) {
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isTextSegment()
	{
		if (segType==SegmentType.Image|/* segType == "line-segment" || */segType==SegmentType.Rectangle)
			return false;
		else return true;
	}

	public String getNodeText()
	{
		if (segType.equals("text-block") || 
				segType.equals("text-segment") ||
				segType.equals("text-line"))
		{
			// limit length to 40 characters
			if (segText==null) {
				return "no text available";
			} else if (segText.length() > 40) {
				return segText.substring(0, 36) + "...";
			}else {
				return segText;
			}
		}
		else
		{
			return "[" + segType + "]";
		}
	}

	public Object clone() 
	{
		return super.clone();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isBold()
	{
		if (segFontName == null) return false;

		if (Utils.containsSubstring(segFontName, "Bold") ||
				Utils.containsSubstring(segFontName, "bold") ||
				Utils.containsSubstring(segFontName, "Black") ||
				Utils.containsSubstring(segFontName, "black") ||
				Utils.containsSubstring(segFontName, "Heavy") ||
				Utils.containsSubstring(segFontName, "heavy"))
			return true;
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isItalic()
	{
		if (segFontName == null) return false;

		if (Utils.containsSubstring(segFontName, "Italic") ||
				Utils.containsSubstring(segFontName, "italic") ||
				Utils.containsSubstring(segFontName, "Cursive") ||
				Utils.containsSubstring(segFontName, "cursive") ||
				Utils.containsSubstring(segFontName, "Kursiv") ||
				Utils.containsSubstring(segFontName, "kursiv"))
			return true;
		else return false;
	}

	/**
	 * 
	 * @return
	 */
	public GenericSegment toGenericSegment()
	{
		if (isTextSegment())
			return new TextSegment(segX1, segX2, segY1, segY2, segText, segFontName, segFontSize, color);
		else
			return new GenericSegment(segX1, segX2, segY1, segY2);
	}

	public Rectangle2D getBoundingBox () {
		return new Rectangle2D.Float(segX1, segY1,  Math.abs(segX2-segX1), Math.abs(segY2-segY1));
	}
	
	public SegmentType getSegType() {
		return segType;
	}

	public void setSegType(SegmentType segType) {
		this.segType = segType;
	}

	public float getSegX1() {
		return segX1;
	}

	public void setSegX1(float segX1) {
		this.segX1 = segX1;
	}

	public float getSegX2() {
		return segX2;
	}

	public void setSegX2(float segX2) {
		this.segX2 = segX2;
	}

	public float getSegXmid()
	{
		return Utils.avg(segX1, segX2);
	}

	public float getSegY1() {
		return segY1;
	}

	public void setSegY1(float segY1) {
		this.segY1 = segY1;
	}

	public float getSegY2() {
		return segY2;
	}

	public void setSegY2(float segY2) {
		this.segY2 = segY2;
	}

	public float getSegYmid()
	{
		return Utils.avg(segY1, segY2);
	}

	public float getVolume() {
		return volume;
	}

	public String getSegText() {
		return segText;
	}

	public void setSegText(String segText) 
	{
		this.segText = segText;
		this.label = segText;
	}

	public String getSegFontName() {
		return segFontName;
	}

	public void setSegFontName(String segFontName) {
		this.segFontName = segFontName;
	}

	public float getSegFontSize() {
		return segFontSize;
	}

	public void setSegFontSize(float segFontSize) {
		this.segFontSize = segFontSize;
	}

	public float getWidth() {
		return getSegX2() - getSegX1();
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public Color getColor() {
		return color;
	}


//	public void setSemAnnotation(String semAnnotation) {
//		this.semAnnotation = semAnnotation;
//	}
//
//	public String getSemAnnotation() {
//		return semAnnotation;
//	}

}//DocNode
