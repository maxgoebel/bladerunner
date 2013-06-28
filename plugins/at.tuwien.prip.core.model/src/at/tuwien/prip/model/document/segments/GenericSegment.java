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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.tuwien.prip.model.document.segments.fragments.LineFragment;
import at.tuwien.prip.model.document.segments.web.FormSegment;
import at.tuwien.prip.model.document.segments.web.VisBoxSegment;
import at.tuwien.prip.model.utils.SegmentUtils;
import at.tuwien.prip.model.utils.Utils;

/**
 * Base class to represent a generic segment 
 * on the page 
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
@Entity
public class GenericSegment implements Cloneable, IXmillumSegment 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/* mcg: store associated PDF operators*/
	private List<OpTuple> operators;
	
	protected float x1, x2, y1, y2;
	boolean zeroSize = false;

	/* mcg: allow segments to determine their type, needed by web docs...*/
	private SegmentType segType;

	/**
	 * Constructor.
	 * 
	 * @param x1
	 *            The x1 coordinate of the segment.
	 * @param x2
	 *            The x2 coordinate of the segment.
	 * @param y1
	 *            The y1 coordinate of the segment.
	 * @param y2
	 *            The y2 coordinate of the segment.
	 */
	public GenericSegment()
	{
		operators = new ArrayList<OpTuple>();
	}

//	/**
//	 * Constructor.
//	 * @param as
//	 */
//	public GenericSegment(AgentState as)
//	{
//		this();
//		Rectangle dims = as.getGraph().getDimensions();
//		this.x1 = dims.x+2;
//		this.x2 = dims.x + dims.width-4;
//		this.y1 = dims.y;
//		this.y2 = dims.y + dims.height;
//		segType = as.getSegType();
//	}
	
	/**
	 * Constructor.
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public GenericSegment(float x1, float x2, float y1, float y2)
	{
		this();
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 * Constructor.
	 * @param boundingBox
	 */
	public GenericSegment(float[] boundingBox)
	{
		this();
		this.setBoundingBox(boundingBox);
	}
	
	/**
	 * Constructor.
	 * @param rectangle
	 */
	public GenericSegment(Rectangle rectangle) 
	{
		this();
		this.setBoundingBox(rectangle);
	}
	
	/**
	 * Constructor.
	 * @param rectangle
	 * @param type
	 */
	public GenericSegment(Rectangle rectangle, SegmentType type) 
	{
		this();
		this.setBoundingBox(rectangle);
		this.segType = type;
	}
	
	/**
	 * This will get the x1 position of the segment.
	 * 
	 * @return The x1 coordinate of the segment.
	 */
	public float getX1()
	{
		return x1;
	}

	/**
	 * @param scale
	 *            The x1 to set.
	 */
	public void setX1(float x1)
	{
		this.x1 = x1;
	}

	/**
	 * This will get the x2 position of the segment.
	 * 
	 * @return The x2 coordinate of the segment.
	 */
	public float getX2()
	{
		return x2;
	}

	/**
	 * @param scale
	 *            The x2 to set.
	 */
	public void setX2(float x2)
	{
		this.x2 = x2;
	}

	/**
	 * This will get the y1 position of the segment.
	 * 
	 * @return The y1 coordinate of the segment.
	 */
	public float getY1()
	{
		return y1;
	}

	/**
	 * @param scale
	 *            The y1 to set.
	 */
	public void setY1(float y1)
	{
		this.y1 = y1;
	}

	/**
	 * This will get the y2 position of the segment.
	 * 
	 * @return The y2 coordinate of the segment.
	 */
	public float getY2()
	{
		return y2;
	}

	/**
	 * @param scale
	 *            The y2 to set.
	 */
	public void setY2(float y2)
	{
		this.y2 = y2;
	}

	public float getXmid()
	{
		return (x1 + x2) / 2;
	}

	public float getYmid()
	{
		return (y1 + y2) / 2;
	}

	/**
	 * This will get the width of the segment.
	 * 
	 * @return The width of the segment.
	 */
	public float getWidth()
	{
		return (x2 - x1);
	}

	/**
	 * This will get the height of the segment.
	 * 
	 * @return The height of the segment.
	 */
	public float getHeight()
	{
		return (y2 - y1);
	}

	/**
	 * This will return the area of the segment.
	 * 
	 * @return The area of the segment (in pt^2).
	 */
	public float getArea()
	{
		return getHeight() * getWidth();
	}

	// note: these methods assume no negative widths!
	// TODO: implement all Allen relations

	public String toString()
	{
		return generateSegmentName() + " - " + getAttributes();
	}

	public String getAttributes()
	{
		return ("x1: " + x1 + " x2: " + x2 + " Xcen: " + getXmid() + " y1: " + y1
				+ " y2: " + y2 + " Ycen: " + getYmid() + " hc: " + hashCode());
	}

	protected void setElementAttributes(Document resultDocument, Element newSegmentElement,
			GenericSegment pageDim, float resolution)
	{
		float pageHeight = pageDim.getHeight();
		//float pageHeight = pageDim.getY2() + pageDim.getY1();
		newSegmentElement.setAttribute("x", Float.toString(this.getX1() * 
				resolution / Utils.PDF_POINT_RESOLUTION));
		newSegmentElement.setAttribute("y", Float.toString((pageHeight
				- this.getY1() - this.getHeight()) * resolution / Utils.PDF_POINT_RESOLUTION));
		newSegmentElement.setAttribute("w", Float.toString(this.getWidth() * 
				resolution / Utils.PDF_POINT_RESOLUTION));
		newSegmentElement.setAttribute("h", Float.toString(this.getHeight() * 
				resolution / Utils.PDF_POINT_RESOLUTION));
	}

	public void correctNegativeDimensions()
	{
		if (x1 > x2)
		{
			float temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2)
		{
			float temp = y1;
			y1 = y2;
			y2 = temp;
		}
	}

	public void enlargeCoordinates(float amount)
	{
		x1 = x1 - amount;
		x2 = x2 + amount;
		y1 = y1 - amount;
		y2 = y2 + amount;
	}

	public void scaleCoordinates(float multiple)
	{
		x1 = x1 * multiple;
		x2 = x2 * multiple;
		y1 = y1 * multiple;
		y2 = y2 * multiple;
	}

	public void invertYCoordinates(GenericSegment page)
	{
		float pageHeight = page.getHeight();
		y1 = pageHeight - y1;
		y2 = pageHeight - y2;
	}

	public void rotate(float pointX, float pointY, int amount)
	{
		// if (amount == null) return;

		float px1 = x1 - pointX, px2 = x2 - pointX;
		float py1 = y1 - pointY, py2 = y2 - pointY;

		if (amount == 90 || amount == -270)
		{
			x1 = pointX - py2; x2 = pointX - py1;
			y1 = pointY + px1; y2 = pointY + px2;
		}
		else if (amount == 270 || amount == -90)
		{
			x1 = pointX + py1; x2 = pointX + py2;
			y1 = pointY - px2; y2 = pointY - px1;
		}
	}

//	public void rotate(PDPage page)
//	{
//		if (page.getMediaBox()!=null) {
//			GenericSegment mediaBox = new GenericSegment(page.getMediaBox());
//			if (page.getRotation() != null)
//			{
//				rotate(mediaBox.getX1(), mediaBox.getY1(), page.getRotation());
//
//				if (page.getRotation() == 90 || page.getRotation() == -270)
//				{
//					x1 = x1 + mediaBox.getHeight();
//					x2 = x2 + mediaBox.getHeight();
//				}
//				else if (page.getRotation() == 270 || page.getRotation() == -90)
//				{
//					y1 = y1 + mediaBox.getWidth();
//					y2 = y2 + mediaBox.getWidth();
//				}
//			}
//		}
//	}

	/*
	 * 22.01.07
	 * added to make processing faster when recalculating
	 * bbox of a composite segment after only one element
	 * has been added (clustering)
	 * 
	 */
	public void growBoundingBox(GenericSegment thisSegment)
	{
		if (thisSegment.getX1() < x1)
			x1 = thisSegment.getX1();
		if (thisSegment.getY1() < y1)
			y1 = thisSegment.getY1();
		if (thisSegment.getX2() > x2)
			x2 = thisSegment.getX2();
		if (thisSegment.getY2() > y2)
			y2 = thisSegment.getY2();
	}

	public void shrinkBoundingBox(GenericSegment thisSegment)
	{
		if (SegmentUtils.intersects(this, thisSegment))
		{
			if (thisSegment.getX1() > x1)
				x1 = thisSegment.getX1();
			if (thisSegment.getY1() > y1)
				y1 = thisSegment.getY1();
			if (thisSegment.getX2() < x2)
				x2 = thisSegment.getX2();
			if (thisSegment.getY2() < y2)
				y2 = thisSegment.getY2();
		}
		else
			this.setZeroSize(true); // intersection is 0
	}

	public boolean isZeroSize() {
		return zeroSize;
	}

	public void setZeroSize(boolean zeroSize) {
		this.zeroSize = zeroSize;
	}

	public Rectangle getBoundingRectangle()
	{
		int x = (int) x1;
		int y = (int) y1;
		int w = (int) this.getWidth();
		int h = (int) this.getHeight();

		return new Rectangle(x, y, w, h);
	}

	public void setBoundingBox(float[] bBox)
	{
		// TODO: not sure if we should bother
		// with exception stuff?
		x1 = bBox[0]; x2 = bBox[1];
		y1 = bBox[2]; y2 = bBox[3];
	}

	public void setBoundingBox(Rectangle bBox) 
	{
		x1 = bBox.x;
		x2 = bBox.x + bBox.width;
		y1 = bBox.y;
		y2 = bBox.y + bBox.height;
	}
	
	public void setBoundingBox(float x1, float x2, float y1, float y2) 
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

//	public void setBoundingBox(PDRectangle bBox)
//	{
//		x1 = bBox.getLowerLeftX(); x2 = bBox.getUpperRightX();
//		y1 = bBox.getLowerLeftY(); y2 = bBox.getUpperRightY();
//	}

	public float[] getBoundingBox()
	{
		float[] bBox =
		{ x1, x2, y1, y2 };
		return bBox;
	}

//	public PDRectangle toPDRectangle()
//	{
//		PDRectangle retVal = new PDRectangle();
//		retVal.setLowerLeftX(x1);
//		retVal.setLowerLeftY(y1);
//		retVal.setUpperRightX(x2);
//		retVal.setUpperRightY(y2);
//		return retVal;
//	}

	/**
	 * @return Returns a clone of this segment, i.e.
	 * the co-ordinates and level
	 * TODO: implement for sub-objects
	 */
	public GenericSegment clone()
	{
		try 
		{
			return (GenericSegment) super.clone();
		}
		catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

	public SegmentType generateSegmentType () 
	{
		if (this instanceof TextBlock) {
			return SegmentType.Block;
		} 
		if (this instanceof LineFragment) {
			return SegmentType.Textline;
		} 
		if (this instanceof TextSegment) {
			return SegmentType.Word;
		} 
		if (this instanceof ImageSegment) {
			return SegmentType.Image;
		} 
		if (this instanceof ParagraphSegment) {
			return SegmentType.Paragraph;
		} 
		if (this instanceof FormSegment) {
			return SegmentType.Form;
		} 
		if (this instanceof VisBoxSegment) {
			return SegmentType.Visual;
		} 
		if (this instanceof TableSegment) {
			return SegmentType.Table;
		} 
		if (this instanceof ListSegment) {
			return SegmentType.List;
		} 
		if (this instanceof SemanticSegment) {
			return SegmentType.Semantic;
		} 
		
		return SegmentType.Rectangle;
	}
	/**
	 * Generate the segment type as a string from camel-case class name 
	 * (before camel bump, hyphen, segment).
	 * 
	 * @return
	 */
	public String generateSegmentName ()
	{
		String className =  Utils.stripClassName(this.getClass().getName());
		StringBuffer retVal = new StringBuffer(className.length());

		boolean afterHyphen = true;
		for (int n = 0; n < className.length(); n ++)
		{
			String chr = className.substring(n, n + 1);
			if (chr.toLowerCase().equals(chr))
			{
				// lower case; just add
				retVal.append(chr);
				afterHyphen = false;
			}
			else
			{
				if (!afterHyphen)
				{
					retVal.append("-");
					retVal.append(chr.toLowerCase());
					afterHyphen = true;
				}
				else
				{
					retVal.append(chr.toLowerCase());
					afterHyphen = true;
				}
			}
		}

		return retVal.toString();
	}

	public void addAsXmillum(Document resultDocument, Element parent,
			GenericSegment pageDim, float resolution)
	{
		//System.out.println(this);
		Element newSegmentElement = resultDocument
		.createElement(generateSegmentName());

		this.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
		parent.appendChild(newSegmentElement);
	}

	public void setSegType(SegmentType segType) {
		this.segType = segType;
	}

	public SegmentType getSegType() {
		return segType;
	}
	
	public void setOperators(List<OpTuple> operators) {
		this.operators = operators;
	}

	public List<OpTuple> getOperators() {
		return operators;
	}
	
}
