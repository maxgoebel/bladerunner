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
package at.tuwien.dbai.bladeRunner.views.anno;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * an activation strategy that only allows editor activation if the item under
 * the event was already selected. This has the effect of requiring a click to
 * first select the item, then a second click to edit.
 */
class SecondClickColumnViewerEditorActivationStrategy extends
		ColumnViewerEditorActivationStrategy implements
		ISelectionChangedListener {

	private Object selectedElement;

	public SecondClickColumnViewerEditorActivationStrategy(ColumnViewer viewer) {
		super(viewer);
		viewer.addSelectionChangedListener(this);
	}

	@Override
	protected boolean isEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getSelection();

		return selection.size() == 1 && super.isEditorActivationEvent(event)
				&& selectedElement == selection.getFirstElement();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection ss = (IStructuredSelection) event.getSelection();

		if (ss.size() == 1) {
			selectedElement = ss.getFirstElement();
			return;
		}

		selectedElement = null;
	}

}
