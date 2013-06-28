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
package at.tuwien.dbai.bladeRunner.editors.base;

import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 * BaseEditor.java
 * 
 * Base class for EMF/Mozilla-based editors.
 */
public abstract class BaseEditor
// <MyBaseContentOutlinePage extends BaseContentOutlinePage,
// MyBasePropertySheetPage extends WrapperPropertySheetPage>
		extends MpBaseEditor implements ISelectionProvider, ISelectionListener
// ITabbedPropertySheetPageContributor
{

	/** Cache of the active workbench part. */
	protected IWorkbenchPart fActivePart;
	/** Indicates whether activation handling is currently be done. */
	protected boolean fIsHandlingActivation = false;

	/**
	 * This keeps track of the active content viewer, which may be either one of
	 * the viewers in the pages or the content outline viewer. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Viewer currentViewer;

	// /**
	// * This is the content outline page.
	// */
	// protected MyBaseContentOutlinePage contentOutlinePage;
	//
	// /**
	// * This is the property sheet page.
	// */
	// protected MyBasePropertySheetPage propertySheetPage;

	// /**
	// * This keeps track of the active viewer pane, in the book.
	// * <!-- begin-user-doc -->
	// * <!-- end-user-doc -->
	// * @generated
	// */
	// protected ViewerPane currentViewerPane;

	/**
	 * This keeps track of the selection of the editor as a whole.
	 */
	// OK
	protected ISelectionChangedListener selectionChangedListener;
	protected ListenerList selectionChangedListeners = new ListenerList();
	protected ISelection editorSelection = StructuredSelection.EMPTY;

	/**
	 * Handles activation of the editor or it's associated views.
	 */
	// OK
	protected void handleActivate() {
		// super.handleActivate();
		// // // Recompute the read only state.
		// // if (editingDomain.getResourceToReadOnlyMap() != null) {
		// // editingDomain.getResourceToReadOnlyMap().clear();

		// Refresh any actions that may become enabled or disabled.
		setSelection(getSelection());
		// }
	}

	/**
	 * Constructor.
	 */
	public BaseEditor() {
		super();

		// getSite().getPage().addPartListener(partListener);
	}

	protected void onCommandStackChanged() {
		firePropertyChange(IEditorPart.PROP_DIRTY);

		// Try to select the affected objects.
		// Command mostRecentCommand =
		// ((CommandStack)event.getSource()).getMostRecentCommand();
		// if (mostRecentCommand != null) {
		// setSelectionToViewer(mostRecentCommand.getAffectedObjects());
		// }
		// refreshPropertySheetPage();

		// validate()
	}

	// protected void refreshPropertySheetPage() {
	// if (propertySheetPage!=null &&
	// //workaround for eclipse bug
	// propertySheetPage.getCurrentTab()!=null)
	// {
	// propertySheetPage.refresh();
	// }
	// }

	/**
	 * This sets the selection into whichever viewer is active. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSelectionToViewer(Collection<?> collection) {
		final Collection<?> theSelection = collection;
		// Make sure it's okay.
		if (theSelection != null && !theSelection.isEmpty()) {
			// I don't know if this should be run this deferred
			// because we might have to give the editor a chance
			// to process the viewer update events
			// and hence to update the views first.
			Runnable runnable = new Runnable() {
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					if (currentViewer != null) {
						currentViewer.setSelection(new StructuredSelection(
								theSelection.toArray()), true);
					}
				}
			};
			runnable.run();
		}
	}

	// /**
	// * <!-- begin-user-doc -->
	// * <!-- end-user-doc -->
	// * @generated
	// */
	// public class ReverseAdapterFactoryContentProvider extends
	// AdapterFactoryContentProvider {
	// public ReverseAdapterFactoryContentProvider(AdapterFactory
	// adapterFactory) {
	// super(adapterFactory);
	// }
	//
	// public Object [] getElements(Object object) {
	// Object parent = super.getParent(object);
	// return (parent == null ? Collections.EMPTY_SET :
	// Collections.singleton(parent)).toArray();
	// }
	//
	// public Object [] getChildren(Object object) {
	// Object parent = super.getParent(object);
	// return (parent == null ? Collections.EMPTY_SET :
	// Collections.singleton(parent)).toArray();
	// }
	//
	// public boolean hasChildren(Object object) {
	// Object parent = super.getParent(object);
	// return parent != null;
	// }
	//
	// public Object getParent(Object object) {
	// return null;
	// }
	// }

	// /**
	// * <!-- begin-user-doc -->
	// * <!-- end-user-doc -->
	// * @generated
	// */
	// public void setCurrentViewerPane(ViewerPane viewerPane) {
	// if (currentViewerPane != viewerPane) {
	// if (currentViewerPane != null) {
	// currentViewerPane.showFocus(false);
	// }
	// currentViewerPane = viewerPane;
	// }
	// setCurrentViewer(currentViewerPane.getViewer());
	// }

	/**
	 * This makes sure that one content viewer, either for the current page or
	 * the outline view, if it has focus, is the current one. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setCurrentViewer(Viewer viewer) {
		// If it is changing...
		//
		if (currentViewer != viewer) {
			if (selectionChangedListener == null) {
				// Create the listener on demand.
				//
				selectionChangedListener = new ISelectionChangedListener() {
					// This just notifies those things that are affected by the
					// section.
					//
					public void selectionChanged(
							SelectionChangedEvent selectionChangedEvent) {
						setSelection(selectionChangedEvent.getSelection());
					}
				};
			}

			// Stop listening to the old one.
			//
			if (currentViewer != null) {
				currentViewer
						.removeSelectionChangedListener(selectionChangedListener);
			}

			// Start listening to the new one.
			//
			if (viewer != null) {
				viewer.addSelectionChangedListener(selectionChangedListener);
			}

			// Remember it.
			//
			currentViewer = viewer;

			// Set the editors selection based on the current viewer's
			// selection.
			//
			setSelection(currentViewer == null ? StructuredSelection.EMPTY
					: currentViewer.getSelection());
		}
	}

	/**
	 * This returns the viewer as required by the {@link IViewerProvider}
	 * interface. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Viewer getViewer() {
		return currentViewer;
	}

	/**
	 * This is used to track the active viewer. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	// protected void pageChange(int pageIndex) {
	// super.pageChange(pageIndex);
	//
	// // This is a temporary workaround... EATM
	// //
	// Control control = getControl(pageIndex);
	// if (control != null) {
	// control.setVisible(true);
	// control.setFocus();
	// }
	//
	// if (contentOutlinePage != null) {
	// handleContentOutlineSelection(contentOutlinePage.getSelection());
	// }
	// }

	// /**
	// * This is how the framework determines which interfaces we implement.
	// */
	// //OK
	// public Object getAdapter(Class key) {
	// if (key.equals(IContentOutlinePage.class)) {
	// return getContentOutlinePage();
	// }
	// else if (key.equals(IPropertySheetPage.class)) {
	// return getPropertySheetPage();
	// }
	// else if (key.equals(IGotoMarker.class)) {
	// return this;
	// }
	// else {
	// return super.getAdapter(key);
	// }
	// }
	//
	// /**
	// * This accesses a cached version of the content outliner.
	// */
	// //OK
	// public IContentOutlinePage getContentOutlinePage() {
	// if (contentOutlinePage == null) {
	// contentOutlinePage = createContentOutlinePage();
	//
	// //Listen to selection so that we can handle it is a special way.
	// contentOutlinePage.addSelectionChangedListener(new
	// ISelectionChangedListener() {
	// // This ensures that we handle selections correctly.
	// public void selectionChanged(SelectionChangedEvent event) {
	// handleContentOutlineSelection(event.getSelection());
	// }
	// });
	// }
	//
	// return contentOutlinePage;
	// }
	//
	// protected abstract MyBaseContentOutlinePage createContentOutlinePage();

	// /**
	// * This accesses a cached version of the property sheet.
	// * <!-- begin-user-doc -->
	// * <!-- end-user-doc -->
	// * @generated
	// */
	// public IPropertySheetPage getPropertySheetPage() {
	// if (propertySheetPage == null) {
	// propertySheetPage = createTabbedPropertySheetPage();
	// //propertySheetPage.setPropertySourceProvider(new
	// AdapterFactoryContentProvider(adapterFactory));
	// }
	//
	// return propertySheetPage;
	// }
	//
	// protected abstract MyBasePropertySheetPage
	// createTabbedPropertySheetPage();

	/**
	 * This deals with how we want selection in the outliner to affect the other
	 * views. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void handleContentOutlineSelection(ISelection selection) {
		// if (currentViewerPane != null && !selection.isEmpty() && selection
		// instanceof IStructuredSelection) {
		// Iterator selectedElements =
		// ((IStructuredSelection)selection).iterator();
		// if (selectedElements.hasNext()) {
		// // Get the first selected element.
		// //
		// Object selectedElement = selectedElements.next();
		//
		// // If it's the selection viewer, then we want it to select the same
		// selection as this selection.
		// //
		//
		// if (currentViewerPane.getViewer() == selectionViewer) {
		// ArrayList selectionList = new ArrayList();
		// selectionList.add(selectedElement);
		// while (selectedElements.hasNext()) {
		// selectionList.add(selectedElements.next());
		// }
		//
		// // Set the selection to the widget.
		// //
		// selectionViewer.setSelection(new StructuredSelection(selectionList));
		// }
		// else {
		// // Set the input to the widget.
		// //
		// if (currentViewerPane.getViewer().getInput() != selectedElement) {
		// currentViewerPane.getViewer().setInput(selectedElement);
		// currentViewerPane.setTitle(selectedElement);
		// }
		// }
		// }
		// }
	}

	/**
	 * Focus to the cause of the problem marker
	 */
	// //OK
	public void gotoMarker(IMarker marker) {
		// try {
		// if (marker.getType().equals(EValidator.MARKER)) {
		// String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE,
		// null);
		// if (uriAttribute != null) {
		// URI uri = URI.createURI(uriAttribute);
		// EObject eObject = editingDomain.getResourceSet().getEObject(uri,
		// true);
		// if (eObject != null) {
		// setSelectionToViewer(Collections.singleton(editingDomain.getWrapper(eObject)));
		// }
		// }
		// }
		// }
		// catch (CoreException exception) {
		// LearnUIPlugin.log(exception);
		// }
	}

	/**
	 * This is called during startup. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getName());
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	// OK
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	// OK
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
	 * return this editor's overall selection.
	 */
	// OK
	public ISelection getSelection() {
		return editorSelection;
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
	 * set this editor's overall selection. Calling this result will notify the
	 * listeners.
	 */
	// OK
	public void setSelection(ISelection selection) {
		editorSelection = selection;

		final SelectionChangedEvent event = new SelectionChangedEvent(this,
				selection);
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}

		// setStatusLineManager(selection);
	}

	// OK
	// public void setStatusLineManager(ISelection selection) {
	// IStatusLineManager statusLineManager =
	// currentViewer!=null &&
	// currentViewer==contentOutlinePage.getViewer() ?
	// contentOutlinePage.getStatusLineManager() : null;
	//
	// if (statusLineManager != null) {
	// if (selection instanceof IStructuredSelection) {
	// Collection<?> collection = ((IStructuredSelection)selection).toList();
	// switch (collection.size())
	// {
	// case 0: {
	// statusLineManager.setMessage(mt._UI_NoObjectSelected);
	// break;
	// }
	// case 1: {
	// statusLineManager.setMessage(mt.bind(mt._UI_SingleObjectSelected, ""));
	// break;
	// }
	// default:
	// {
	// statusLineManager.setMessage(mt.bind(mt._UI_MultiObjectSelected,
	// Integer.toString(collection.size())));
	// break;
	// }
	// }
	// }
	// else {
	// statusLineManager.setMessage("");
	// }
	// }
	// }

	// //OK
	// public void dispose() {
	//
	// if (propertySheetPage != null) {
	// propertySheetPage.dispose();
	// }
	//
	// if (contentOutlinePage != null) {
	// contentOutlinePage.dispose();
	// }
	//
	// super.dispose();
	// }

	/**
	 * needed for ITabbedPropertySheetPageContributor
	 */
	// OK
	public String getContributorId() {
		return getSite().getId();
	}

	public void addToTitle(String suffix) {
		this.suffix = suffix;
		setPartName(origPartName);
	}

	String suffix = null;
	String origPartName = null;

	@Override
	protected void setPartName(String partName) {
		origPartName = partName;
		if (suffix == null || suffix.length() == 0) {
			super.setPartName(origPartName);
		} else {
			String composedPartName = String.format("%s (%s)", origPartName,
					suffix);
			super.setPartName(composedPartName);
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		System.out.println("Bla");

		// TODO Auto-generated method stub

	}

}
