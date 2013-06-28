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
package org.eclipse.atf.mozilla.ide.ui.common;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Maintains a list of selection listeners and
 * fire selection changes to all current
 * listeners.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public  class SelectionProviderHandler implements ISelectionProvider {
	
	private ListenerList listeners = new ListenerList();
	private ISelection selection = null;
	
	/**
	 * Fires a selection changed event to all listeners.
	 * @param selection - selection to fire
	 */
	public void fireSelection( ISelection selection ) {
		SelectionChangedEvent event = new SelectionChangedEvent(this,selection);
		fireSelectionChanged(event);
	}
	
	/*
	 * notify changes to selection to all listeners
	 */
	private void fireSelectionChanged(final SelectionChangedEvent event) {
	    Object[] _listeners = listeners.getListeners();
	    for (int i = 0; i < _listeners.length; ++i) {
	        final ISelectionChangedListener l = (ISelectionChangedListener) _listeners[i];
	        SafeRunnable.run(new SafeRunnable() {
	            public void run() {
	                l.selectionChanged(event);
	            }
	        });
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return selection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		this.selection = selection;
	}
	
}