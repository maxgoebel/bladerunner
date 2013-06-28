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
package at.tuwien.dbai.bladeRunner.preferences.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.utils.SWTUtil;
import at.tuwien.prip.common.log.ErrorDump;

/**
 * A composite that displays installed JRE's in a table. JREs can be added,
 * removed, edited, and searched for.
 * <p>
 * This block implements ISelectionProvider - it sends selection change events
 * when the checked JRE in the table changes, or when the "use default" button
 * check state changes.
 * </p>
 */
@SuppressWarnings("restriction")
public class InstalledERsBlock implements IAddERDialogRequestor,
		ISelectionProvider {

	/**
	 * This block's control
	 */
	private Composite fControl;

	/**
	 * Runtimes being displayed
	 */
	private List<IERInstall> fERs = new ArrayList<IERInstall>();

	/**
	 * The main list control
	 */
	private CheckboxTableViewer fERList;

	// Action buttons
	private Button fAddButton;
	private Button fRemoveButton;
	private Button fEditButton;
	private Button fCopyButton;
	private Button fSearchButton;

	// index of column used for sorting
	private int fSortColumn = 0;

	/**
	 * Selection listeners (checked JRE changes)
	 */
	private ListenerList fSelectionListeners = new ListenerList();

	/**
	 * Previous selection
	 */
	private ISelection fPrevSelection = new StructuredSelection();

	private Table fTable;

	// Make sure that VMStandin ids are unique if multiple calls
	// to System.currentTimeMillis() happen very quickly
	private static String fgLastUsedID;

	/**
	 * Content provider to show a list of ERs
	 */
	class ERsContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object input) {
			return fERs.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}
	}

	/**
	 * Label provider for installed ERs table.
	 */
	class ERLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * @see ITableLabelProvider#getColumnText(Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IERInstall) {
				IERInstall vm = (IERInstall) element;
				switch (columnIndex) {
				case 0:
					if (isContributed(vm)) {
						return mt.bind(mt.lbfmt_Locked_ER, vm.getName());
					}
					return vm.getName();
				case 1:
					return vm.getInstallLocation().getAbsolutePath();
				}
			}
			return element.toString();
		}

		/**
		 * @see ITableLabelProvider#getColumnImage(Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				// return PDEPluginImages.DESC_RUNTIME_OBJ.createImage();
			}
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return new StructuredSelection(fERList.getCheckedElements());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(fPrevSelection)) {
				fPrevSelection = selection;
				Object er = ((IStructuredSelection) selection)
						.getFirstElement();
				if (er == null) {
					fERList.setCheckedElements(new Object[0]);
				} else {
					fERList.setCheckedElements(new Object[] { er });
					fERList.reveal(er);
				}
				fireSelectionChanged();
			}
		}
	}

	/**
	 * Creates this block's control in the given control.
	 * 
	 * @param ancestor
	 *            containing control
	 * @param useManageButton
	 *            whether to present a single 'manage...' button to the user
	 *            that opens the installed JREs pref page for JRE management, or
	 *            to provide 'add, remove, edit, and search' buttons.
	 */
	public void createControl(Composite ancestor) {

		Composite parent = new Composite(ancestor, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		Font font = ancestor.getFont();
		parent.setFont(font);
		fControl = parent;

		GridData data;

		Label tableLabel = new Label(parent, SWT.NONE);
		tableLabel.setText(mt.lb_Installed_runtimes);
		data = new GridData();
		data.horizontalSpan = 2;
		tableLabel.setLayoutData(data);
		tableLabel.setFont(font);

		fTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION);

		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 450;
		fTable.setLayoutData(data);
		fTable.setFont(font);

		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		TableColumn column1 = new TableColumn(fTable, SWT.NULL);
		column1.setText(mt.lb_Column_Name);
		column1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByName();
			}
		});

		TableColumn column2 = new TableColumn(fTable, SWT.NULL);
		column2.setText(mt.lb_Column_Location);
		column2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByLocation();
			}
		});

		fERList = new CheckboxTableViewer(fTable);
		fERList.setLabelProvider(new ERLabelProvider());
		fERList.setContentProvider(new ERsContentProvider());
		// by default, sort by name
		sortByName();

		fERList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});

		fERList.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedER((IERInstall) event.getElement());
				} else {
					setCheckedER(null);
				}
			}
		});

		fERList.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (!fERList.getSelection().isEmpty()) {
					editER();
				}
			}
		});
		fTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					removeERs();
				}
			}
		});

		Composite buttons = new Composite(parent, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttons.setFont(font);

		fAddButton = createPushButton(buttons, mt.btn_Add);
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addER();
			}
		});

		fEditButton = createPushButton(buttons, mt.btn_Edit);
		fEditButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editER();
			}
		});

		fCopyButton = createPushButton(buttons, mt.btn_Copy);
		fCopyButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				copyRuntime();
			}
		});

		fRemoveButton = createPushButton(buttons, mt.btn_Remove);
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeERs();
			}
		});

		// copied from ListDialogField.CreateSeparator()
		Label separator = new Label(buttons, SWT.NONE);
		separator.setVisible(false);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.heightHint = 4;
		separator.setLayoutData(gd);

		fSearchButton = createPushButton(buttons, mt.btn_Search);
		fSearchButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				search();
			}
		});

		fillWithWorkspaceERs();
		enableButtons();
	}

	/**
	 * Adds a duplicate of the selected VM to the block
	 * 
	 * @since 3.2
	 */
	protected void copyRuntime() {
		IStructuredSelection selection = (IStructuredSelection) fERList
				.getSelection();
		Iterator<?> it = selection.iterator();

		ArrayList<IERInstall> newEntries = new ArrayList<IERInstall>();
		while (it.hasNext()) {
			IERInstall selectedER = (IERInstall) it.next();

			// duplicate & add VM
			ERInstall standin = new ERInstall(selectedER, createUniqueId());
			standin.setName(generateName(selectedER.getName()));
			AddERDialog dialog = new AddERDialog(this, getShell(), standin);
			dialog.setTitle(mt.dlg_Copy_ER);
			if (dialog.open() != Window.OK) {
				return;
			}
			newEntries.add(standin);
			fERs.add(standin);
		}
		fERList.refresh();
		fERList.setSelection(new StructuredSelection(newEntries.toArray()));
	}

	/**
	 * Compares the given name against current names and adds the appropriate
	 * numerical suffix to ensure that it is unique.
	 * 
	 * @param name
	 *            the name with which to ensure uniqueness
	 * @return the unique version of the given name
	 * @since 3.2
	 */
	public String generateName(String name) {
		if (!isDuplicateName(name)) {
			return name;
		}

		if (name.matches(".*\\(\\d*\\)")) { //$NON-NLS-1$
			int start = name.lastIndexOf('(');
			int end = name.lastIndexOf(')');
			String stringInt = name.substring(start + 1, end);
			int numericValue = Integer.parseInt(stringInt);
			String newName = name.substring(0, start + 1) + (numericValue + 1)
					+ ")"; //$NON-NLS-1$
			return generateName(newName);
		}
		return generateName(name + " (1)"); //$NON-NLS-1$
	}

	/**
	 * Fire current selection
	 */
	private void fireSelectionChanged() {
		SelectionChangedEvent event = new SelectionChangedEvent(this,
				getSelection());
		Object[] listeners = fSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
			listener.selectionChanged(event);
		}
	}

	/**
	 * Sorts by VM name.
	 */
	private void sortByName() {
		fERList.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IERInstall) && (e2 instanceof IERInstall)) {
					IERInstall left = (IERInstall) e1;
					IERInstall right = (IERInstall) e2;
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}

			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});
		fSortColumn = 1;
	}

	/**
	 * Sorts by VM location.
	 */
	private void sortByLocation() {
		fERList.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IERInstall) && (e2 instanceof IERInstall)) {
					IERInstall left = (IERInstall) e1;
					IERInstall right = (IERInstall) e2;
					return left
							.getInstallLocation()
							.getAbsolutePath()
							.compareToIgnoreCase(
									right.getInstallLocation()
											.getAbsolutePath());
				}
				return super.compare(viewer, e1, e2);
			}

			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});
		fSortColumn = 2;
	}

	private void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) fERList
				.getSelection();
		int selectionCount = selection.size();
		fEditButton.setEnabled(selectionCount == 1);
		fCopyButton.setEnabled(selectionCount > 0);
		if (selectionCount > 0
				&& selectionCount <= fERList.getTable().getItemCount()) {
			Iterator<?> iterator = selection.iterator();
			while (iterator.hasNext()) {
				IERInstall install = (IERInstall) iterator.next();
				if (isContributed(install)) {
					fRemoveButton.setEnabled(false);
					return;
				}
			}
			fRemoveButton.setEnabled(true);
		} else {
			fRemoveButton.setEnabled(false);
		}
	}

	private boolean isContributed(IERInstall install) {
		// true if shipped in binaries
		return ExtractionRuntime.isContributedERInstall(install.getId());
	}

	protected Button createPushButton(Composite parent, String label) {
		return SWTUtil.createPushButton(parent, label, null);
	}

	/**
	 * Returns this block's control
	 * 
	 * @return control
	 */
	public Control getControl() {
		return fControl;
	}

	/**
	 * Sets the ERs to be displayed in this block
	 * 
	 * @param ers
	 *            ERs to be displayed
	 */
	protected void setERs(IERInstall[] ers) {
		fERs.clear();
		for (int i = 0; i < ers.length; i++) {
			fERs.add(ers[i]);
		}
		fERList.setInput(fERs);
		fERList.refresh();
	}

	/**
	 * Returns the runtimes currently being displayed in this block
	 * 
	 * @return runtimes currently being displayed in this block
	 */
	public IERInstall[] getERs() {
		return fERs.toArray(new IERInstall[fERs.size()]);
	}

	/**
	 * Bring up a dialog that lets the user create a new VM definition.
	 */
	private void addER() {
		AddERDialog dialog = new AddERDialog(this, getShell(), null);
		dialog.setTitle(mt.dlg_Add_ER);
		if (dialog.open() != Window.OK) {
			return;
		}
		fERList.refresh();
	}

	/**
	 * @see IAddERDialogRequestor#vmAdded(IERInstall)
	 */
	public void erAdded(IERInstall er) {
		fERs.add(er);
		fERList.refresh();
		if (getCheckedER() == null)
			setCheckedER(er);
	}

	/**
	 * @see IAddERDialogRequestor#isDuplicateName(String)
	 */
	public boolean isDuplicateName(String name) {
		for (int i = 0; i < fERs.size(); i++) {
			IERInstall er = fERs.get(i);
			if (er.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void editER() {
		IStructuredSelection selection = (IStructuredSelection) fERList
				.getSelection();
		IERInstall er = (IERInstall) selection.getFirstElement();
		if (er == null) {
			return;
		}
		if (isContributed(er)) {
			ERDetailsDialog dialog = new ERDetailsDialog(getShell(), er);
			dialog.open();
		} else {
			AddERDialog dialog = new AddERDialog(this, getShell(), er);
			dialog.setTitle(mt.dlg_Edit_ER);
			if (dialog.open() != Window.OK) {
				return;
			}
			fERList.refresh(er);
		}
	}

	private void removeERs() {
		IStructuredSelection selection = (IStructuredSelection) fERList
				.getSelection();
		IERInstall[] ers = new IERInstall[selection.size()];
		Iterator<?> iter = selection.iterator();
		int i = 0;
		while (iter.hasNext()) {
			ers[i] = (IERInstall) iter.next();
			i++;
		}
		removeERs(ers);
	}

	/**
	 * Removes the given ERs from the table.
	 * 
	 * @param ers
	 */
	public void removeERs(IERInstall[] ers) {
		IStructuredSelection prev = (IStructuredSelection) getSelection();
		for (int i = 0; i < ers.length; i++) {
			fERs.remove(ers[i]);
		}

		if (fERs.size() == 0) {
			// re-add the default runtime
			IERInstall er = ExtractionRuntime.getBuiltInERInstall();
			if (er != null)
				erAdded(er);
		}

		fERList.refresh();
		IStructuredSelection curr = (IStructuredSelection) getSelection();
		if (!curr.equals(prev)) {
			IERInstall[] installs = getERs();
			if (curr.size() == 0 && installs.length == 1) {
				// pick a default runtime automatically
				setSelection(new StructuredSelection(installs[0]));
			} else {
				fireSelectionChanged();
			}
		}
	}

	/**
	 * Search for installed ERs in the file system
	 */
	protected void search() {

		// choose a root directory for the search
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage(mt.lb_Select_a_directory_to_search_in);
		dialog.setText(mt.lb_Directory_Selection);
		String path = dialog.open();
		if (path == null) {
			return;
		}

		// ignore installed locations
		final Set<File> exstingLocations = new HashSet<File>();
		Iterator<IERInstall> iter1 = fERs.iterator();
		while (iter1.hasNext()) {
			exstingLocations.add(iter1.next().getInstallLocation());
		}

		// search
		final File rootDir = new File(path);
		final List<File> locations = new ArrayList<File>();

		IRunnableWithProgress r = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask(mt.lb_Searching, IProgressMonitor.UNKNOWN);
				search(rootDir, locations, exstingLocations, monitor);
				monitor.done();
			}
		};

		try {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(
					getShell());
			progress.run(true, true, r);
		} catch (InvocationTargetException e) {
			ErrorDump.error(this, e);
		} catch (InterruptedException e) {
			// cancelled
			return;
		}

		if (locations.isEmpty()) {
			MessageDialog.openInformation(getShell(), mt.dlg_Information,
					mt.bind(mt.lbfmt_No_ERs_found_in, path));
		} else {
			Iterator<File> iter = locations.iterator();
			while (iter.hasNext()) {
				File location = iter.next();
				ERInstall er = new ERInstall(createUniqueId());
				String name = location.getName();
				String nameCopy = name;
				int i = 1;
				while (isDuplicateName(nameCopy)) {
					nameCopy = name + '(' + i++ + ')';
				}
				er.setName(nameCopy);
				er.setInstallLocation(location);
				erAdded(er);
			}
		}

	}

	protected Shell getShell() {
		return getControl().getShell();
	}

	/**
	 * Find a unique VM id. Check existing 'real' VMs, as well as the last id
	 * used for a VMStandin.
	 */
	private String createUniqueId() {
		String id = null;
		do {
			id = String.valueOf(System.currentTimeMillis());
		} while (ExtractionRuntime.findERInstall(id) != null
				|| id.equals(fgLastUsedID));
		fgLastUsedID = id;
		return id;
	}

	/**
	 * Searches the specified directory recursively for installed ERs, adding
	 * each detected ER to the <code>found</code> list. Any directories
	 * specified in the <code>ignore</code> are not traversed.
	 * 
	 * @param directory
	 * @param found
	 * @param types
	 * @param ignore
	 */
	protected void search(File directory, List<File> found, Set<File> ignore,
			IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}

		String[] names = directory.list();
		if (names == null) {
			return;
		}
		List<File> subDirs = new ArrayList<File>();
		for (int i = 0; i < names.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}
			File file = new File(directory, names[i]);
			try {
				monitor.subTask(mt.bind(mt.lbfmt_Found_Searching, found.size(),
						file.getCanonicalPath()));
			} catch (IOException e) {
			}
			if (file.isDirectory()) {
				if (!ignore.contains(file)) {
					boolean validLocation = false;

					// Take the first VM install type that claims the location
					// as a
					// valid VM install. VM install types should be smart enough
					// to not
					// claim another type's VM, but just in case...
					if (monitor.isCanceled()) {
						return;
					}
					IStatus status = ExtractionRuntime
							.validateInstallLocation(file);
					if (status.isOK()) {
						found.add(file);
						validLocation = true;
						break;
					}
					if (!validLocation) {
						subDirs.add(file);
					}
				}
			}
		}
		while (!subDirs.isEmpty()) {
			File subDir = subDirs.remove(0);
			search(subDir, found, ignore, monitor);
			if (monitor.isCanceled()) {
				return;
			}
		}

	}

	/**
	 * Sets the checked ER, possible <code>null</code>
	 * 
	 * @param er
	 *            ER or <code>null</code>
	 */
	public void setCheckedER(IERInstall er) {
		if (er == null) {
			setSelection(new StructuredSelection());
		} else {
			setSelection(new StructuredSelection(er));
		}
	}

	/**
	 * Returns the checked ER or <code>null</code> if none.
	 * 
	 * @return the checked ER or <code>null</code> if none
	 */
	public IERInstall getCheckedER() {
		Object[] objects = fERList.getCheckedElements();
		if (objects.length == 0) {
			return null;
		}
		return (IERInstall) objects[0];
	}

	/**
	 * Persist table settings into the give dialog store, prefixed with the
	 * given key.
	 * 
	 * @param settings
	 *            dialog store
	 * @param qualifier
	 *            key qualifier
	 */
	public void saveColumnSettings(IDialogSettings settings, String qualifier) {
		int columnCount = fTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			settings.put(
					qualifier + ".columnWidth" + i, fTable.getColumn(i).getWidth()); //$NON-NLS-1$
		}
		settings.put(qualifier + ".sortColumn", fSortColumn); //$NON-NLS-1$
	}

	/**
	 * Restore table settings from the given dialog store using the given key.
	 * 
	 * @param settings
	 *            dialog settings store
	 * @param qualifier
	 *            key to restore settings from
	 */
	public void restoreColumnSettings(IDialogSettings settings, String qualifier) {
		fERList.getTable().layout(true);
		restoreColumnWidths(settings, qualifier);
		try {
			fSortColumn = settings.getInt(qualifier + ".sortColumn"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			fSortColumn = 1;
		}
		switch (fSortColumn) {
		case 1:
			sortByName();
			break;
		case 2:
			sortByLocation();
			break;
		}
	}

	private void restoreColumnWidths(IDialogSettings settings, String qualifier) {
		int columnCount = fTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			int width = -1;

			try {
				width = settings.getInt(qualifier + ".columnWidth" + i); //$NON-NLS-1$
			} catch (NumberFormatException e) {
			}

			if (width <= 0) {
				fTable.getColumn(i).pack();
			} else {
				fTable.getColumn(i).setWidth(width);
			}
		}
	}

	/**
	 * Populates the JRE table with existing JREs defined in the workspace.
	 */
	protected void fillWithWorkspaceERs() {
		// fill with runtimes
		List<IERInstall> standins = new ArrayList<IERInstall>();
		IERInstall[] installs = ExtractionRuntime.getERInstalls();
		for (int j = 0; j < installs.length; j++) {
			IERInstall install = installs[j];
			standins.add(new ERInstall(install));
		}
		setERs(standins.toArray(new IERInstall[standins.size()]));
	}

}
