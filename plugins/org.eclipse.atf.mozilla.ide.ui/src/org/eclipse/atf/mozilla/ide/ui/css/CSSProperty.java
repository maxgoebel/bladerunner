/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import java.util.Arrays;

import org.eclipse.swt.graphics.RGB;

/**
 * Represents a CSS property obtained from the XPCOM domUtil service.
 * Used by the content provider to model the css rules and computed
 * styles.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CSSProperty implements Comparable {

	//Name of CSS property
	private String name;

	//Value of CSS property
	private String value;

	//Line number of enclosing CSS Rule
	private String lineNumber;

	//Path to .css file
	private String url;

	//True if property specifies a color
	private boolean isColor;

	//Name of enclosing rule
	private String rule;

	//RGB value used when color constants are used
	//Specifies constants as rgb(X,X,X) so their
	//color can be rendered as a label image
	private RGB rgbValue;

	//True if property is currently being rendered
	//Used to differentiate between overridden
	//properties
	private boolean present;

	//True if property is derived from
	//obtained the computed styles
	private boolean computed;

	//True if the property is to be added
	//to the current rule(s)
	private boolean newRule;

	//True is property is declared in style
	//dom attribute
	private boolean inline;

	//Original name for creating diff when changes occur
	private String originalName;

	//Original value for creating diff when changes occur
	private String originalValue;

	//Boolean to check if object is property or 
	//enclosing rule
	private boolean isProperty;

	/**
	 * Creates a new CSS property when the five values display
	 * in the tree viewer.
	 * @param name - Name of the property
	 * @param value - Value of the property
	 * @param url - URL of stylesheet
	 * @param lineNumber - Line number of rule
	 * @param rule - Enclosing rule of the property
	 */
	public CSSProperty(String name, String value, String url, String lineNumber, String rule) {
		this.name = name;
		this.value = value;
		this.url = url;
		this.lineNumber = lineNumber;
		this.rule = rule;
		this.originalName = name;
		this.originalValue = value;

		//Names containing the word 'color' contain
		//values of colors that will be displayed
		if (name.indexOf("color") != -1) {
			isColor = true;
		} else {
			isColor = false;
		}

		//Set through setter methods
		present = false;
		computed = false;
		newRule = false;
		isProperty = false;
	}

	/**
	 * Returns true if property is new to the
	 * rule.
	 * @return - true if property is new
	 */
	public boolean isNewRule() {
		return newRule;
	}

	/**
	 * Sets that the rule is new
	 * @param newRule - true if the rule is new
	 */
	public void setNewRule(boolean newRule) {
		this.newRule = newRule;
	}

	/**
	 * Sets that the property is computed
	 * @param computed - true if computed style
	 */
	public void setComputed(boolean computed) {
		this.computed = computed;
	}

	/**
	 * Returns true if property is a computed 
	 * style. 
	 * @return - true if computed style
	 */
	public boolean isComputed() {
		return computed;
	}

	/**
	 * Gets the enclosing rule
	 * @return - the enclosing rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * Gets the RGBValue is getValue returns
	 * a color string constant
	 * @return - converted rgb string, might throw exceptions if  property value is invalid color
	 */
	public RGB getRGBValue() {
		if (!isColor())
			return null;

		if (rgbValue == null) {
			rgbValue = parseRGB(value);
		}

		return rgbValue;
	}

	public static RGB parseRGB(String rgb) {
		char[] trimmed = new char[rgb.length()];
		int trimmedLen = 0;
		for (int i = 0; i < rgb.length(); i++) {
			char c = rgb.charAt(i);
			if (c != ' ' && c != '\t' && c != '\n') {
				trimmed[trimmedLen++] = c;
			}
		}
		rgb = new String(trimmed, 0, trimmedLen);

		if (rgb.startsWith("rgb")) { // rgb(r,g,b) or rgb(%r,%g,%b)
			int commaAfterRed = rgb.indexOf(',');
			int commaAfterGreen = rgb.indexOf(',', commaAfterRed + 1);

			String color = rgb.substring(4, commaAfterRed);
			int red;
			if (color.charAt(0) == '%') {
				red = (Integer.parseInt(color.substring(1)) * 255) / 100;
			} else {
				red = Integer.parseInt(color);
			}

			color = rgb.substring(commaAfterRed + 1, commaAfterGreen);
			int green;
			if (color.charAt(0) == '%') {
				green = (Integer.parseInt(color.substring(1)) * 255) / 100;
			} else {
				green = Integer.parseInt(color);
			}

			color = rgb.substring(commaAfterGreen + 1, rgb.length() - 1);
			int blue;
			if (color.charAt(0) == '%') {
				blue = (Integer.parseInt(color.substring(1)) * 255) / 100;
			} else {
				blue = Integer.parseInt(color);
			}

			return new RGB(red, green, blue);

		} else if (rgb.startsWith("#")) { // #rgb or #rrggbb
			if (rgb.length() == 7) {
				int red = Integer.parseInt(rgb.substring(1, 3), 16);
				int green = Integer.parseInt(rgb.substring(3, 5), 16);
				int blue = Integer.parseInt(rgb.substring(5, 7), 16);
				return new RGB(red, green, blue);
			} else if (rgb.length() == 4) {
				int red = Integer.parseInt(rgb.substring(1, 2) + rgb.substring(1, 2), 16);
				int green = Integer.parseInt(rgb.substring(2, 3) + rgb.substring(2, 3), 16);
				int blue = Integer.parseInt(rgb.substring(3, 4) + rgb.substring(3, 4), 16);
				return new RGB(red, green, blue);
			}
		} else {
			int i = Arrays.asList(CSSConstants.COLORS).indexOf(rgb);
			if (i != -1)
				return CSSConstants.COLOR_VALUES[i];
		}

		return null;
	}

	/**
	 * Sets the rgb value
	 * @param value - rgb(x,x,x) string
	 */
	public void setRGBValue(RGB value) {
		if (!isColor())
			throw new IllegalArgumentException();

		rgbValue = new RGB(value.red, value.green, value.blue);
		this.value = "rgb(" + value.red + "," + value.green + "," + value.blue + ")";
	}

	/**
	 * Sets the values of the property
	 * @param value - value property
	 */
	public void setValue(String value) {
		this.value = value;

		// reset cached rgbValue
		if (rgbValue != null)
			rgbValue = null;
	}

	/**
	 * Sets the name of the property
	 * @param name - property name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the url of the property
	 * @param url - stylesheet url
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * Sets the line number of the enclosing
	 * rule in the style sheet
	 * @param number - line number
	 */
	public void setLineNumber(String number) {
		this.lineNumber = number;
	}

	/**
	 * Returns if property is color
	 * @return - true if property is color
	 */
	public boolean isColor() {
		return isColor;
	}

	/**
	 * Returns name of property
	 * @return - property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets value of the property
	 * @return - property value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the line number of rule
	 * @return - rule line number
	 */
	public String getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the url of the style sheet
	 * @return - stylesheet url
	 */
	public String getURL() {
		return url == null ? "" : url;
	}

	/**
	 * Gets the hash of the property
	 * @return - property hash
	 */
	public String getHash() {
		return getRule() + ":" + getURL() + ":" + getLineNumber();
	}

	/**
	 * Returns if property is currently applied
	 * @return - true if property is currently applied
	 */
	public boolean isPresent() {
		return present;
	}

	/**
	 * Sets whether the property is current applied
	 * @param present - true if property is being applied
	 */
	public void setPresent(boolean present) {
		this.present = present;
	}

	/**
	 * Compares rules for alphabetical sort in first
	 * tree column
	 */
	public int compareTo(Object o) {
		return getHash().compareTo(((CSSProperty) o).getHash());
	}

	/**
	 * Gets the original name for this property
	 * @return - original name
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * Gets the original value for this property
	 * @return - original value
	 */
	public String getOriginalValue() {
		return originalValue;
	}

	/**
	 * Sets whether or not this object is a
	 * css property as opposed to an enclosing
	 * rule declaration
	 * @param isProperty - true if property, false otherwise
	 */
	public void setProperty(boolean isProperty) {
		this.isProperty = isProperty;
	}

	/**
	 * Returns true if object is a property
	 * @return - true if property
	 */
	public boolean isProperty() {
		return isProperty;
	}

	/**
	 * Returns true if object is an enclosing rule
	 * @return - true if rule
	 */
	public boolean isRule() {
		return !isProperty;
	}

	public boolean isInline() {
		return inline;
	}

	public void setInline(boolean isInline) {
		inline = isInline;
	}
}
