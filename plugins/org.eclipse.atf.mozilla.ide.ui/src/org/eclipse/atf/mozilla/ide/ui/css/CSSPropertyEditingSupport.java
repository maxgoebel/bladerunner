/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Item;

public class CSSPropertyEditingSupport extends EditingSupport {

	private TextCellEditor textEditor;
	private ColorCellEditor colorEditor;
	private StyleRulesContentProvider ruleViewerContentProvider;

	public CSSPropertyEditingSupport(TreeViewer viewer, StyleRulesContentProvider ruleViewerContentProvider) {
		super(viewer);
		textEditor = new TextCellEditor(viewer.getTree());
		colorEditor = new ColorCellEditor(viewer.getTree());
		this.ruleViewerContentProvider = ruleViewerContentProvider;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (!(element instanceof CSSProperty))
			return false;

		CSSProperty cssprop = (CSSProperty) element;
		return ((!cssprop.isRule()) && (!cssprop.getURL().startsWith("resource")));
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		CSSProperty cssprop = (CSSProperty) element;
		if (cssprop.isColor())
			return colorEditor;

		return textEditor;
	}

	@Override
	protected Object getValue(Object element) {
		CSSProperty cssprop = (CSSProperty) element;
		if (cssprop.isColor()) {
			return cssprop.getRGBValue();
		}

		return cssprop.getValue();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}

		if (!(element instanceof CSSProperty)) {
			return;
		}
		CSSProperty cssprop = (CSSProperty) element;

		if (value instanceof RGB) {
			RGB old = cssprop.getRGBValue();
			RGB newColor = (RGB) value;

			if (!old.equals(newColor)) {
				cssprop.setRGBValue(newColor);
				ruleViewerContentProvider.updateProperty(cssprop);
				getViewer().refresh(cssprop);
			}
		}

		if (value instanceof String) {
			String str = (String) value;
			if (!cssprop.getValue().equals(value)) {
				cssprop.setValue(str);
				ruleViewerContentProvider.updateProperty(cssprop);
				getViewer().refresh(cssprop);
			}
		}
	}
}
