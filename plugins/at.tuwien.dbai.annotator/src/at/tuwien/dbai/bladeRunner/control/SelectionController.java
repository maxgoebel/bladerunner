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
package at.tuwien.dbai.bladeRunner.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor;
import at.tuwien.dbai.bladeRunner.editors.annotator.PDFViewerSWT;
import at.tuwien.dbai.bladeRunner.views.SelectionImageView;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;

/**
 * 
 * SelectionController.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Oct 1, 2011
 */
public class SelectionController
implements
ISelectionProvider,
ISelectionChangedListener 
{
	private List<ISelectionChangedListener> listeners;

	private ISelection lastSelection;
	private ISelectionProvider lastProvider;

	public SelectionController() 
	{
		lastProvider = this;
		listeners = new ArrayList<ISelectionChangedListener>();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) 
	{
		lastProvider = event.getSelectionProvider();
		lastSelection = event.getSelection();

		if (event.getSelectionProvider() instanceof PDFViewerSWT) 
		{
			for (ISelectionChangedListener listener : listeners) {
				if (listener instanceof BenchmarkNavigatorView) {
					listener.selectionChanged(event);
				}
			}
		}
		if (event.getSelectionProvider() instanceof TreeViewer) 
		{
			for (ISelectionChangedListener listener : listeners) {
					listener.selectionChanged(event);
			}
		}
		else if (event.getSelectionProvider() instanceof DocWrapEditor) 
		{
			for (ISelectionChangedListener listener : listeners) 
			{
				if (listener instanceof BenchmarkNavigatorView) {
					listener.selectionChanged(event);
				}
				if (listener instanceof SelectionImageView) {
					listener.selectionChanged(event);
				}
			}
		}
	}

	/**
	 * 
	 * @param component
	 */
	public void registerComponent(ISelectionChangedListener component) 
	{
		if (component instanceof ISelectionChangedListener) {
			addSelectionChangedListener((ISelectionChangedListener) component);
		}
	}

	/**
	 * 
	 * @param component
	 */
	public void deregisterComponent(Object component) 
	{
		if (component instanceof ISelectionChangedListener) {
			removeSelectionChangedListener((ISelectionChangedListener) component);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return lastSelection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		this.listeners.remove(listener);

	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection.equals(lastSelection)) {
			return;
		}

		SelectionChangedEvent event = new SelectionChangedEvent(lastProvider, selection);
		selectionChanged(event);
//		for (ISelectionChangedListener listener : listeners) {
//			listener.selectionChanged(se);
//		}

		lastSelection = selection;
	}

}
