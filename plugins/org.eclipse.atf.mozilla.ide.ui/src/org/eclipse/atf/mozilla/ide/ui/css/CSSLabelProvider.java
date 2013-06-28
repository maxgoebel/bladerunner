/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements.
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Label provider for CSS Tree.  Returns an images and text
 * for each column for the properties for each CSS rule and 
 * computed style from a element DOM node.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CSSLabelProvider implements ITableLabelProvider {
	//Labels for each tree column value
	public static final String[] COLUMNS = { "Rule", "Property", "Value", "URL", "Line Number" };

	//Device used when creating images
	private Device device;

	private Map colors = new HashMap();

	/**
	 * Creates a new label provider with a device for
	 * image creations
	 * @param device - device to used when creating images
	 */
	public CSSLabelProvider(Device device) {
		super();
		this.device = device;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {

		/**
		 * Only return images for the 2nd and 3rd columns
		 */
		if (element instanceof CSSProperty && columnIndex == 2) {
			/**
			 * Paints a color image matching the color specified
			 * by the property value
			 */
			if (((CSSProperty) element).isColor()) {
				CSSProperty property = ((CSSProperty) element);
				RGB color = property.getRGBValue();
				if (color != null) {
					return getImage(color, 50, 10);
				}
			}

			/**
			 * Creates a green square if the property is current being applied to the selected node
			 * Creates a red square if the property has been overwritten and not current applied
			 * to the node
			 */
		} else if (element instanceof CSSProperty && columnIndex == 1 && !((CSSProperty) element).getName().equals("")) {
			if (((CSSProperty) element).isPresent()) {
				return getImage(new RGB(0, 200, 0), 10, 10);
			} else {
				return getImage(new RGB(200, 0, 0), 10, 10);
			}
		}
		return null;
	}

	private Image getImage(RGB color, int width, int height) {
		Image img = (Image) colors.get(color);
		if (img == null) {
			PaletteData pd = new PaletteData(new RGB[] { color });
			ImageData id = new ImageData(width, height, 1, pd);
			img = new Image(device, id);
			colors.put(color, img);
		}
		return img;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof CSSProperty) {
			CSSProperty property = (CSSProperty) element;
			if (columnIndex == 0) {
				if (property.isProperty()) {
					return "";
				} else if (property.isRule()) {
					return property.getRule();
				}
			} else if (columnIndex == 1) {
				return property.getName();

			}
			if (columnIndex == 2) {
				if (property.isColor()) {
					return property.getValue();
				} else {
					return property.getValue();
				}
			}
			if (columnIndex == 3) {
				if (property.isRule() || property.isComputed()) {
					return property.getURL();
				}
			}
			if (columnIndex == 4) {
				if (property.isRule() || property.isComputed()) {
					return property.getLineNumber();
				} else {
					return "";
				}
			}
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		for (Iterator i = colors.values().iterator(); i.hasNext();) {
			Image img = (Image) i.next();
			if (!img.isDisposed())
				img.dispose();
		}

		colors.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {

	}

}
