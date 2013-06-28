/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMClientRect;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNSElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMViewCSS;

/**
 * 
 * Creates a graphical representation of the CSS Box model.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public class BoxModelPaintListener implements PaintListener {

	private boolean set = false;

	// Dimension info
	private String x_val;
	private String y_val;
	private String width;
	private String height;

	// Margin info
	private String margintop;
	private String marginbottom;
	private String marginright;
	private String marginleft;

	// Padding info
	private String paddingtop;
	private String paddingbottom;
	private String paddingright;
	private String paddingleft;

	// Border info
	private String bordertopwidth;
	private String bordertopstyle;
	private String bordertopcolor;
	private String borderbottomwidth;
	private String borderbottomstyle;
	private String borderbottomcolor;
	private String borderleftwidth;
	private String borderleftstyle;
	private String borderleftcolor;
	private String borderrightwidth;
	private String borderrightstyle;
	private String borderrightcolor;

	private Canvas canvas;

	private Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private Color blue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

	private Map colors = new HashMap();

	private static final int TOP = 0;
	private static final int RIGHT = 1;
	private static final int BOTTOM = 2;
	private static final int LEFT = 3;

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void paintControl(PaintEvent e) {
		if (set) {
			GC gc = e.gc;
			gc.setLineWidth(3);

			int x = this.canvas.getSize().x / 2 - 50;
			int y = this.canvas.getSize().y / 2 - 25;
			int width = 100;
			int height = 50;

			// Draw width and height
			gc.setBackground(white);
			gc.drawRectangle(x, y, width, height);
			String dimensions = this.width + " x " + this.height;
			int topoffset = gc.getFontMetrics().getAverageCharWidth()
					* dimensions.length();
			gc.drawString("Dimensions", x + gc.getLineWidth() + 2,
					y + gc.getLineWidth() + 2);
			gc.drawString(dimensions, x + width / 2 - topoffset / 2, y + height
					/ 2 - gc.getFontMetrics().getHeight() / 2);

			// Draw padding strings
			gc.setForeground(blue);
			topoffset = gc.getFontMetrics().getAverageCharWidth()
					* paddingtop.length();
			int leftoffset = gc.getFontMetrics().getAverageCharWidth()
					* paddingleft.length();
			int rightoffset = gc.getFontMetrics().getAverageCharWidth()
					* paddingright.length();
			int bottomoffset = gc.getFontMetrics().getAverageCharWidth()
					* paddingbottom.length();

			int margin = Math.max(leftoffset, rightoffset) + 4;

			gc.drawString(paddingtop, x + width / 2 - topoffset / 2, y
					- gc.getFontMetrics().getHeight() - 4);
			gc.drawString(paddingleft, x - margin - gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(paddingright, x + width + gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(paddingbottom, x + width / 2 - bottomoffset / 2, y
					+ height + gc.getLineWidth());

			gc.setForeground(black);
			x = x - margin - gc.getLineWidth() - 10;
			y = y - gc.getFontMetrics().getHeight() - gc.getLineWidth() - 5;
			width = width + margin * 2 + 2 * gc.getLineWidth() + 20;
			height = height + 2 * gc.getFontMetrics().getHeight() + 2
					* gc.getLineWidth() + 10;
			gc.setLineDash(new int[] { 5 });
			gc.drawRectangle(x, y, width, height);
			gc.drawString("Padding", x + gc.getLineWidth() + 2,
					y + gc.getLineWidth() + 2);
			gc.setLineDash(null);

			// Draw border
			topoffset = gc.getFontMetrics().getAverageCharWidth()
					* bordertopwidth.length();
			leftoffset = gc.getFontMetrics().getAverageCharWidth()
					* borderleftwidth.length();
			rightoffset = gc.getFontMetrics().getAverageCharWidth()
					* borderrightwidth.length();
			bottomoffset = gc.getFontMetrics().getAverageCharWidth()
					* borderbottomwidth.length();

			margin = Math.max(leftoffset, rightoffset) + 4;

			gc.setForeground(blue);
			gc.drawString(bordertopwidth, x + width / 2 - topoffset / 2, y
					- gc.getFontMetrics().getHeight() - 4);
			gc.drawString(borderleftwidth, x - margin - gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(borderrightwidth, x + width + gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(borderbottomwidth, x + width / 2 - bottomoffset / 2,
					y + height + gc.getLineWidth());

			gc.setForeground(black);
			x = x - margin - gc.getLineWidth() - 10;
			y = y - gc.getFontMetrics().getHeight() - gc.getLineWidth() - 5;
			width = width + margin * 2 + 2 * gc.getLineWidth() + 20;
			height = height + 2 * gc.getFontMetrics().getHeight() + 2
					* gc.getLineWidth() + 10;
			gc.drawString("Border", x + gc.getLineWidth() + 2,
					y + gc.getLineWidth() + 2);

			String value = bordertopcolor
					.substring(bordertopcolor.indexOf('(') + 1);
			int red = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			int green = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			int blue = Integer.parseInt(value.substring(0, value.indexOf(')'))
					.trim());
			RGB topcolor = new RGB(red, green, blue);

			gc.setForeground(getColor(topcolor));
			gc.drawLine(x, y, x + width, y);

			value = borderleftcolor.substring(borderleftcolor.indexOf('(') + 1);
			red = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			green = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			blue = Integer.parseInt(value.substring(0, value.indexOf(')'))
					.trim());
			RGB leftcolor = new RGB(red, green, blue);

			gc.setForeground(getColor(leftcolor));
			gc.drawLine(x, y, x, y + height);

			value = borderrightcolor
					.substring(borderrightcolor.indexOf('(') + 1);
			red = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			green = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			blue = Integer.parseInt(value.substring(0, value.indexOf(')'))
					.trim());
			RGB rightcolor = new RGB(red, green, blue);

			gc.setForeground(getColor(rightcolor));
			gc.drawLine(x + width, y, x + width, y + height);

			value = borderbottomcolor
					.substring(borderbottomcolor.indexOf('(') + 1);
			red = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			green = Integer.parseInt(value.substring(0, value.indexOf(','))
					.trim());
			value = value.substring(value.indexOf(',') + 1);
			blue = Integer.parseInt(value.substring(0, value.indexOf(')'))
					.trim());
			RGB bottomcolor = new RGB(red, green, blue);

			gc.setForeground(getColor(bottomcolor));
			gc.drawLine(x, y + height, x + width, y + height);

			// Draw margin
			topoffset = gc.getFontMetrics().getAverageCharWidth()
					* margintop.length();
			leftoffset = gc.getFontMetrics().getAverageCharWidth()
					* marginleft.length();
			rightoffset = gc.getFontMetrics().getAverageCharWidth()
					* marginright.length();
			bottomoffset = gc.getFontMetrics().getAverageCharWidth()
					* marginbottom.length();

			margin = Math.max(leftoffset, rightoffset) + 4;

			gc.setForeground(this.blue);
			gc.drawString(margintop, x + width / 2 - topoffset / 2, y
					- gc.getFontMetrics().getHeight() - 4);
			gc.drawString(marginleft, x - margin - gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(marginright, x + width + gc.getLineWidth(), y
					+ height / 2 - gc.getFontMetrics().getHeight() / 2);
			gc.drawString(marginbottom, x + width / 2 - bottomoffset / 2, y
					+ height + gc.getLineWidth());

			gc.setForeground(this.black);
			x = x - margin - gc.getLineWidth() - 10;
			y = y - gc.getFontMetrics().getHeight() - gc.getLineWidth() - 5;
			width = width + margin * 2 + 2 * gc.getLineWidth() + 20;
			height = height + 2 * gc.getFontMetrics().getHeight() + 2
					* gc.getLineWidth() + 10;
			gc.setLineDash(new int[] { 3 });
			gc.drawString("Margin", x + gc.getLineWidth() + 2,
					y + gc.getLineWidth() + 2);
			gc.drawRectangle(x, y, width, height);

		}
	}

	public void setNode(nsIDOMNode currentNode) {
		set = true;
		nsIDOMElement element = (nsIDOMElement) currentNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMNSElement nselement = (nsIDOMNSElement) element
				.queryInterface(nsIDOMNSElement.NS_IDOMNSELEMENT_IID);
		nsIDOMClientRect elementBox = nselement.getBoundingClientRect();
		nsIDOMDocumentView documentView = (nsIDOMDocumentView) element
				.getOwnerDocument().queryInterface(
						nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
		nsIDOMViewCSS cssView = (nsIDOMViewCSS) documentView.getDefaultView()
				.queryInterface(nsIDOMViewCSS.NS_IDOMVIEWCSS_IID);
		nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle(
				element, "");

		// Dimension info
		x_val = String.valueOf(elementBox.getTop());
		y_val = String.valueOf(elementBox.getLeft());
		width = String.valueOf(elementBox.getWidth());
		height = String.valueOf(elementBox.getHeight());

		// Margin info
		margintop = computedStyle.getPropertyCSSValue("margin-top")
				.getCssText();
		marginbottom = computedStyle.getPropertyCSSValue("margin-bottom")
				.getCssText();
		marginright = computedStyle.getPropertyCSSValue("margin-right")
				.getCssText();
		marginleft = computedStyle.getPropertyCSSValue("margin-left")
				.getCssText();

		// Padding info
		paddingtop = computedStyle.getPropertyCSSValue("padding-top")
				.getCssText();
		paddingbottom = computedStyle.getPropertyCSSValue("padding-bottom")
				.getCssText();
		paddingright = computedStyle.getPropertyCSSValue("padding-right")
				.getCssText();
		paddingleft = computedStyle.getPropertyCSSValue("padding-left")
				.getCssText();

		// Border info
		bordertopwidth = computedStyle.getPropertyCSSValue("border-top-width")
				.getCssText();
		bordertopstyle = computedStyle.getPropertyCSSValue("border-top-style")
				.getCssText();
		bordertopcolor = computedStyle.getPropertyCSSValue("border-top-color")
				.getCssText();

		borderbottomwidth = computedStyle.getPropertyCSSValue(
				"border-bottom-width").getCssText();
		borderbottomstyle = computedStyle.getPropertyCSSValue(
				"border-bottom-style").getCssText();
		borderbottomcolor = computedStyle.getPropertyCSSValue(
				"border-bottom-color").getCssText();

		borderleftwidth = computedStyle
				.getPropertyCSSValue("border-left-width").getCssText();
		borderleftstyle = computedStyle
				.getPropertyCSSValue("border-left-style").getCssText();
		borderleftcolor = computedStyle
				.getPropertyCSSValue("border-left-color").getCssText();

		borderrightwidth = computedStyle.getPropertyCSSValue(
				"border-right-width").getCssText();
		borderrightstyle = computedStyle.getPropertyCSSValue(
				"border-right-style").getCssText();
		borderrightcolor = computedStyle.getPropertyCSSValue(
				"border-right-color").getCssText();

	}

	// clean up (dipose colors)
	public void dispose() {
		for (Iterator i = colors.values().iterator(); i.hasNext();) {
			Color color = (Color) i.next();
			if (!color.isDisposed())
				color.dispose();
		}

		colors.clear();
	}

	private Color getColor(RGB color) {
		Color theColor = (Color) colors.get(color);
		if (theColor == null) {
			theColor = new Color(Display.getCurrent(), color);
			colors.put(color, theColor);
		}
		return theColor;
	}

}
