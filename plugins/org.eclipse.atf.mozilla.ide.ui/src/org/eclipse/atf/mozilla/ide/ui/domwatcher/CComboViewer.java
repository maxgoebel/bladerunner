package org.eclipse.atf.mozilla.ide.ui.domwatcher;

/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This is a copy of ComboViewer which is wrapping a CCombo rather than a Combo
 * 
 * 
 */
final class CComboViewer extends AbstractListViewer {

    /**
     * This viewer's list control.
     */
    private CCombo combo;

    /**
     * Creates a combo viewer on a newly-created combo control under the given parent.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param parent the parent control
     */
    public CComboViewer(Composite parent) {
        this(parent, SWT.READ_ONLY | SWT.BORDER);
    }

    /**
     * Creates a combo viewer on a newly-created combo control under the given parent.
     * The combo control is created using the given SWT style bits.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param parent the parent control
     * @param style the SWT style bits
     */
    public CComboViewer(Composite parent, int style) {
        this(new CCombo(parent, style));
    }

    /**
     * Creates a combo viewer on the given combo control.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param list the combo control
     */
    public CComboViewer(CCombo list) {
        this.combo = list;
        hookControl(list);
    }

    protected void listAdd(String string, int index) {
        combo.add(string, index);
    }

    protected void listSetItem(int index, String string) {
        combo.setItem(index, string);
    }

    protected int[] listGetSelectionIndices() {
        return new int[] { combo.getSelectionIndex() };
    }

    protected int listGetItemCount() {
        return combo.getItemCount();
    }

    protected void listSetItems(String[] labels) {
        combo.setItems(labels);
    }

    protected void listRemoveAll() {
        combo.removeAll();
    }

    protected void listRemove(int index) {
        combo.remove(index);
    }

    /* (non-Javadoc)
     * Method declared on Viewer.
     */
    public Control getControl() {
        return combo;
    }

    /**
     * Returns this list viewer's list control.
     *
     * @return the list control
     */
    public CCombo getCCombo() {
        return combo;
    }

    /*
     * Do nothing -- combos only display the selected element, so there is no way
     * we can ensure that the given element is visible without changing the selection.
     * Method defined on StructuredViewer.
     */
    public void reveal(Object element) {
        return;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listSelectAndShow(int[])
     */
    protected void listSetSelection(int[] ixs) {
        for (int idx = 0; idx < ixs.length; idx++) {
            combo.select(ixs[idx]);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listDeselectAll()
     */
    protected void listDeselectAll() {
        combo.deselectAll();
        combo.clearSelection();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listShowSelection()
     */
    protected void listShowSelection() {

    }
}
