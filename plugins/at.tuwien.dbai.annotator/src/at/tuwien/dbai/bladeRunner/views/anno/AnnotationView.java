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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.IModelChangedListener;
import at.tuwien.dbai.bladeRunner.control.ModelChangedEvent;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor;
import at.tuwien.dbai.bladeRunner.utils.ModelUtils2;
import at.tuwien.dbai.bladeRunner.utils.benchmark.DiademBenchmarkEngine;
import at.tuwien.dbai.bladeRunner.views.BenchmarkEditorInput;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.ImageSegment;
import at.tuwien.prip.model.document.segments.OpTuple;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.segments.fragments.TextFragment;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.AnnotationType;
import at.tuwien.prip.model.project.annotation.LayoutAnnotation;
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.ExtractionResult;
import at.tuwien.prip.model.project.selection.LabelSelection;
import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SinglePageSelection;
import at.tuwien.prip.model.project.selection.blade.CaptionSelection;
import at.tuwien.prip.model.project.selection.blade.ConceptSectionSelection;
import at.tuwien.prip.model.project.selection.blade.FigureSelection;
import at.tuwien.prip.model.project.selection.blade.FunctionalSelection;
import at.tuwien.prip.model.project.selection.blade.ImageSelection;
import at.tuwien.prip.model.project.selection.blade.ListItemSelection;
import at.tuwien.prip.model.project.selection.blade.ListSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.PdfSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.SectionSelection;
import at.tuwien.prip.model.project.selection.blade.SemanticSelection;
import at.tuwien.prip.model.project.selection.blade.TableCell;
import at.tuwien.prip.model.project.selection.blade.TableColumnSelection;
import at.tuwien.prip.model.project.selection.blade.TableRowSelection;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.project.selection.blade.TextSelection;
import at.tuwien.prip.model.utils.DocGraphUtils;

/**
 * AnnotationView.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 16, 2011
 */
public class AnnotationView extends ViewPart 
implements 
ISelectionProvider,
ISelectionChangedListener, 
IModelChangedListener 
{

	public final int EXPAND_LEVEL = 2;

	public static String ID = "at.tuwien.dbai.bladeRunner.views.annotation";

	private TreeViewer annoViewer;

	private TreeViewer gtViewer;

	private PdfBenchmarkDocument document = null;

	boolean isDebugMode = false;

	// private Action saveToBenchmarkAction;
	private Action deleteItemAction;
	// private Action clearAction;
	//	private Button switchDebugModeButton;
	private Button createSelectionButton;
	private Button addResultButton;
	private Button addGTButton;
	private Button clearResultButton;
	private Button clearGTButton;
	private Button saveAnnotationButton;

	private Combo combo1, combo2;
	private String type;

	/* needs saving? */
	boolean dirty = false;

	/**
	 * Constructor.
	 */
	public AnnotationView() 
	{
		AnnotatorEditor we = DocWrapUIUtils.getWrapperEditor();
		if (we != null) {
			we.registerForSelection(this);
		}
	}

	@Override
	public void createPartControl(Composite parent) 
	{
		Activator.modelControl.addModelChangedListener(this);

		/* BEGIN CONTENT */

		final TabFolder tabFolder = new TabFolder(parent, SWT.BORDER);

		/* TAB 1: Annotations */
		TabItem annoTab = new TabItem(tabFolder, SWT.NONE);
		annoTab.setText("Results");

		Composite annoPanel = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		annoPanel.setLayout(layout);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.heightHint = 800;
		annoPanel.setLayoutData(data);

		/* add a control panel at top */
		createAnnotationControlPanel(annoPanel);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);

		/* add the viewer at bottom */
		Composite viewerPanel = new Composite(annoPanel, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10);
		viewerPanel.setLayout(new FillLayout());
		viewerPanel.setLayoutData(gd);
		viewerPanel.setEnabled(true);

		/* create a new tree viewer */
		annoViewer = createAnnoTreeViewer(viewerPanel);
		annoViewer.setAutoExpandLevel(EXPAND_LEVEL);
		annoViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionEnablement();

				TreeSelection tsel = (TreeSelection) event.getSelection();
				Object top = tsel.getFirstElement();
				if (top instanceof AbstractSelection)
				{
					DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
					int pageNum = editor.getCurrentPage();

					RegionSelection region = findRegionFromSelection((AbstractSelection) top, pageNum);
					if (region!=null)
					{
						editor.highlightBox(region.getBounds());
					}
					else
					{
						editor.highlightBox(null);
					}
				}
			}
		});

		annoTab.setControl(annoPanel);

		/* TAB 2: Ground Truth */
		TabItem gtTab = new TabItem(tabFolder, SWT.NONE);
		gtTab.setText("Ground Truth");

		Composite gtPanel = new Composite(tabFolder, SWT.NONE);
		layout = new GridLayout(1, true);
		gtPanel.setLayout(layout);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.heightHint = 800;
		gtPanel.setLayoutData(data);

		/* create a control panel at the top */
		createGtControlPanel(gtPanel);
		// GridData gd2 = new GridData(SWT.FILL, SWT.TOP, true, false);

		/* add the viewer at bottom */
		Composite gtViewerPanel = new Composite(gtPanel, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10);
		gtViewerPanel.setLayout(new FillLayout());
		gtViewerPanel.setLayoutData(gd);
		gtViewerPanel.setEnabled(true);

		/* create a new tree viewer */
		gtViewer = createGtTreeViewer(gtViewerPanel);
		gtViewer.setAutoExpandLevel(EXPAND_LEVEL);
		gtViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionEnablement();

				//set highlight
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				Object top = selection.getFirstElement();
				if (top instanceof AbstractSelection)
				{
					DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
					int pageNum = editor.getCurrentPage();

					RegionSelection region = findRegionFromSelection((AbstractSelection) top, pageNum);
					if (region!=null)
					{
						editor.highlightBox(region.getBounds());
					}
					else
					{
						editor.highlightBox(null);
					}
				}
			}
		});

		gtTab.setControl(gtPanel);

		createActions();
		createContextMenu();
	}

	/**
	 * 
	 * @return
	 */
	public String getFirstSelectionType () 
	{
		String result = null;
		if (combo1!=null)
		{
			result = combo1.getItem(combo1.getSelectionIndex());
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSecondSelectionType () 
	{
		String result = null;
		if (combo2!=null)
		{
			String selection2 = combo2.getItem(combo2.getSelectionIndex());
			result = selection2;
		}
		return result;
	}
		
	/**
	 * Create the ground truth viewer.
	 * 
	 * @param parent
	 * @return
	 */
	private TreeViewer createGtTreeViewer(Composite parent) {
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER);
		treeViewer.setContentProvider(new AnnotationViewContentProviderGT());
		treeViewer.setLabelProvider(new AnnotationViewLabelProvider());
		return treeViewer;
	}

	private RegionSelection findRegionFromSelection (AbstractSelection selection, int pageNum)
	{
		RegionSelection result = null;

		if (selection instanceof RegionSelection)
		{
			RegionSelection region = (RegionSelection) selection;
			if (region.getPageNum()==pageNum)
			{
				result = region;
			}
		}
		else if (selection instanceof SinglePageSelection)
		{
			SinglePageSelection spSel = (SinglePageSelection) selection;
			for (AbstractSelection sel : spSel.getItems())
			{
				if (sel instanceof RegionSelection)
				{
					RegionSelection region = (RegionSelection) sel;
					if (region.getPageNum()==pageNum)
					{
						result = (RegionSelection) sel;
						break;
					}
				}
			}
		}
		else if (selection instanceof MultiPageSelection)
		{
			MultiPageSelection mpSel = (MultiPageSelection) selection;
			for (AnnotationPage page : mpSel.getPages())
			{
				if (page.getPageNum()==pageNum)
				{
					for (AbstractSelection sel : page.getItems())
					{
						if (sel instanceof RegionSelection)
						{
							result = (RegionSelection) sel;
							break;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Create the annotation viewer.
	 * 
	 * @param parent
	 * @return
	 */
	private TreeViewer createAnnoTreeViewer(Composite parent) 
	{
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER);
		treeViewer.setContentProvider(new AnnotationViewContentProvider());
		treeViewer.setLabelProvider(new AnnotationViewLabelProvider());

		return treeViewer;
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	private Composite createGtControlPanel(final Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		panel.setLayout(layout);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.END;
		panel.setLayoutData(data);

		// /////////////////////////////////////////////////////////////
		// ROW 1
		//
		/* create a clear annotation button */
		Image clearImage = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_CLEAR);
		clearGTButton = new Button(panel, SWT.PUSH);
		clearGTButton.setImage(clearImage);
		clearGTButton.setToolTipText("Clear current ground truth");
		clearGTButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				document.getGroundTruth().clear();
				gtViewer.setInput(document);
				gtViewer.setAutoExpandLevel(EXPAND_LEVEL);

				dirty = false;
				updateActionEnablement();

				//refresh canvas
				DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
				editor.highlightBox(null);
				editor.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.horizontalSpan = 1;
		data.verticalAlignment = SWT.END;
		clearGTButton.setLayoutData(data);
		clearGTButton.setEnabled(false);

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.horizontalSpan = 1;
		data.verticalAlignment = SWT.END;
		addGTButton = new Button(panel, SWT.NONE);
		addGTButton.setToolTipText("Add ground truth");
		ImageDescriptor desc = Activator.getImageDescriptor("/icons/eclipseUI/obj16/add_obj.gif");
		addGTButton.setImage(desc.createImage());
		addGTButton.setLayoutData(data);
		addGTButton.setEnabled(false);
		addGTButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				// From a view you get the site which allow to get the service
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand("at.tuwien.dbai.bladeRunner.commands.addGroundTruth", null);
				} catch (Exception ex) {
					throw new RuntimeException("at.tuwien.dbai.bladeRunner.commands.addGroundTruth not found");
					// Give message
				}
				updateActionEnablement();

				AnnotatorEditor we = (AnnotatorEditor) Activator.getActiveEditor();
				DocumentModel model = we.documentControl.getDocModel();
				DocumentUpdate update = new DocumentUpdate();
				update.setType(UpdateType.DOCUMENT_CHANGE);
				update.setUpdate(model);
				we.documentControl.setDocumentUpdate(update);

			} 
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		panel.pack();
		return panel;
	}

	/**
	 * Create a control panel for the annotation type.
	 * 
	 * @param parent
	 * @return
	 */
	private Composite createAnnotationControlPanel(final Composite parent) 
	{
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		panel.setLayout(layout);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		panel.setLayoutData(data);

		combo1 = new Combo(panel, SWT.READ_ONLY | SWT.DROP_DOWN);
		data = new GridData();
		data.heightHint = 30;
		data.widthHint = 200;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		combo1.setLayoutData(data);
		combo1.add("Selection");
		combo1.add("Table");
		type = "TABLE";

		combo1.add("List");
		combo1.add("Figure");
		combo1.add("Section");
		combo1.add("Concept Section");
		combo1.add("Text");
		combo1.add("Semantic");
		combo1.add("Functional");
		combo1.select(0);

		combo2 = new Combo(panel, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo2.setEnabled(true);
		combo2.setLayoutData(data);
		combo2.removeAll();
		combo2.add("Region");
		combo2.add("Column");
		combo2.add("Row");
		combo2.add("Accessor vert.");
		combo2.add("Accessor horiz.");
		combo2.add("Cell");
		combo2.add("Caption");
		combo2.select(0);

		combo1.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				String string = combo1.getItem(combo1.getSelectionIndex());
				type = string;

				if ("Selection".equals(string))
				{

					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Extraction");
					combo2.add("Label");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				}
				else if ("Table".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Region");
					combo2.add("Column");
					combo2.add("Row");
					combo2.add("Accessor vert.");
					combo2.add("Accessor horiz.");
					combo2.add("Cell");
					combo2.add("Caption");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				}
				else if ("List".equals(string))
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("List Region");
					combo2.add("List Item");
					combo2.add("Caption");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				}
				else if ("Key-value".equals(string)) 
				{
					clearCombo2();
				} 
				else if ("Figure".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Image");
					combo2.add("Caption");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				} 
				else if ("Heading".equals(string)) 
				{
					clearCombo2();

					createSelectionButton.setEnabled(true);
				} 
				else if ("Text".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Text Column");
					combo2.add("Paragraph");
					combo2.add("Text Line");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				} 
				else if ("Section".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Title");
					combo2.add("General");
					combo2.add("Abstract");
					combo2.add("References");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				}
				else if ("Semantic".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();

					combo2.add("Name");
					combo2.add("City");
					combo2.add("Country");
					combo2.add("PO_Code");
					combo2.add("Address");
					combo2.add("Street");

					combo2.add("Date");
					combo2.add("Date Range");

					combo2.add("Institution");
					combo2.add("Email");
					combo2.add("Address");
					combo2.add("Phone");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				}
				else if ("CV Region".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Work Experience");
					combo2.add("Education");
					combo2.add("Skills");
					combo2.add("Address");
					combo2.add("Personal Information");
					combo2.add("Hobbies");
					combo2.add("Languages");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				} 
				else if ("Image".equals(string))
				{
					clearCombo2();

					createSelectionButton.setEnabled(false);
				}
				else if ("Paragraph".equals(string))
				{
					clearCombo2();

					createSelectionButton.setEnabled(false);
				}
				else if ("Functional".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Title");
					combo2.add("Author");
					combo2.add("Footnote");
					combo2.add("Citation");
					combo2.add("Keywords");
					combo2.add("TOC");
					combo2.add("Advertisment");
					combo2.add("Navigation");
					combo2.add("Header");
					combo2.add("Footer");
					combo2.add("Related");
					combo2.add("Menu");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				} 
				else if ("Concept Section".equals(string)) 
				{
					combo2.setEnabled(true);
					combo2.removeAll();
					combo2.add("Person");
					combo2.add("Place");
					combo2.add("Organization");
					combo2.add("Product");
					combo2.add("CV Education");
					combo2.add("CV Employment");
					combo2.add("CV Skills");
					combo2.select(0);

					createSelectionButton.setEnabled(true);
				} 
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ErrorDump.debug(this, "");
			}
		});

		// add a create new selection button
		createSelectionButton = new Button(panel, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 50;
		data.heightHint = 30;
		createSelectionButton.setLayoutData(data);
		createSelectionButton.setText("New");
		createSelectionButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) 
			{
				/* create a new benchmark document */
				BenchmarkDocument benchDoc = null;
				IEditorPart editor = DocWrapUIUtils.getActiveEditor();
				if (editor instanceof AnnotatorEditor)
				{
					AnnotatorEditor we = (AnnotatorEditor) editor;
					IEditorInput input = we.getEditorInput();
					if (input instanceof BenchmarkEditorInput) {
						BenchmarkEditorInput bei = (BenchmarkEditorInput) input;
						benchDoc = bei.getBenchmarkDocument();
					}
				}
				if (benchDoc == null) {
					MessageDialog.openError(parent.getShell(), "Error",
							"No benchmark document open");
					return;
				}
				
				//create the selection
				AbstractSelection selection = addNewAnnotationFromSelectionType(benchDoc, type);
				
				annoViewer.setInput(benchDoc);
				
				if (selection!=null)
				{
					TreePath path = new TreePath(new Object[]{selection});
					TreeSelection treeSel = new TreeSelection(path);
					annoViewer.setSelection(treeSel);
				}

				dirty = true;
				updateActionEnablement();
			}
		});

		/* create a clear annotation button */
		Image clearImage = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_CLEAR);
		clearResultButton = new Button(panel, SWT.PUSH);
		clearResultButton.setImage(clearImage);
		clearResultButton.setToolTipText("Clear all current annotations");
		clearResultButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				document.getAnnotations().clear();
				annoViewer.setInput(document);
				annoViewer.setAutoExpandLevel(EXPAND_LEVEL);

				dirty = false;
				updateActionEnablement();

				//refresh canvas
				DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
				editor.highlightBox(null);
				editor.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.horizontalSpan = 1;
		data.verticalAlignment = SWT.END;
		clearResultButton.setLayoutData(data);
		clearResultButton.setEnabled(false);

		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.horizontalSpan = 1;
		data.verticalAlignment = SWT.END;
		addResultButton = new Button(panel, SWT.NONE);
		addResultButton.setToolTipText("Add result");
		ImageDescriptor desc = Activator.getImageDescriptor("/icons/eclipseUI/obj16/add_obj.gif");
		addResultButton.setImage(desc.createImage());
		addResultButton.setLayoutData(data);
		addResultButton.setEnabled(false);
		addResultButton.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// From a view you get the site which allow to get the service
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try 
				{
					handlerService.executeCommand("at.tuwien.dbai.bladeRunner.commands.addAnnotation", null);
				} 
				catch (Exception ex) 
				{
					// Give message
					MessageBox messageBox = new MessageBox(getSite()
							.getShell(), SWT.ICON_WARNING | SWT.OK
							| SWT.CANCEL);

					messageBox.setText("Error");
					messageBox.setMessage("Error parsing file\n"+ex.getMessage());
					messageBox.open();
				}
			} 
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		/* create a save annotation button */
		Image saveImage = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT);
		saveAnnotationButton = new Button(panel, SWT.PUSH);
		saveAnnotationButton.setImage(saveImage);
		saveAnnotationButton.setToolTipText("Save current annotation");
		saveAnnotationButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// open dialog
				String fileName = document.getUri();
				fileName = fileName.replaceFirst("\\.pdf", ".xml");
				String[] parts = fileName.split("/");

				FileDialog dialog = new FileDialog(getSite().getShell(),
						SWT.SAVE);
				dialog.setFilterNames(new String[] { "XML Files",
				"All Files (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
				// dialog.setFilterPath(BenchmarkEngine.getBenchmarkRootDirectory());
				dialog.setFileName("bm_" + parts[parts.length - 1]);// "datafile.xml");

				fileName = null;
				fileName = dialog.open();
				ErrorDump.debug(this, "Saving to " + fileName);

				if (fileName != null) {
					if (new java.io.File(fileName).exists()) {
						MessageBox messageBox = new MessageBox(getSite()
								.getShell(), SWT.ICON_WARNING | SWT.OK
								| SWT.CANCEL);

						messageBox.setText("Warning");
						messageBox.setMessage("File exists. Overwrite?");
						int buttonID = messageBox.open();
						switch (buttonID) {
						case SWT.OK:
							// saves changes ...
							break;
						case SWT.CANCEL:
							return;
						}
					}

					// OK, save to file
					DiademBenchmarkEngine.writeBladeRegionFile(document, fileName);

					ErrorDump
					.debug(this, fileName + " written successfully...");
					dirty = false;
				}

				updateActionEnablement();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.verticalAlignment = SWT.END;
		saveAnnotationButton.setLayoutData(data);
		saveAnnotationButton.setEnabled(false);


		panel.pack();
		return panel;
	}

	private void clearCombo2() {
		combo2.setEnabled(false);
		GridData data = new GridData();
		data.widthHint = 150;
		combo2.setLayoutData(data);
		combo2.removeAll();
		combo2.add("                  ");
		combo2.select(0);
	}

	private void createActions()
	{
		deleteItemAction = new Action("Delete") {
			public void run() {
				deleteItem();
			}
		};
		Image deleteImage = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_ETOOL_DELETE);
		deleteItemAction.setImageDescriptor(ImageDescriptor
				.createFromImage(deleteImage));
	}

	/**
	 * 
	 */
	private void updateActionEnablement() 
	{
		IStructuredSelection sel = (IStructuredSelection) annoViewer
				.getSelection();
		deleteItemAction.setEnabled(sel.size() > 0);

		if (document != null) {
			//if (dirty) {
			saveAnnotationButton.setEnabled(true);
			if (document.getAnnotations().size() > 0) {
				clearResultButton.setEnabled(true);
			} else {
				clearResultButton.setEnabled(false);
			}
			if (document.getGroundTruth().size() > 0) {
				addGTButton.setEnabled(false);
				clearGTButton.setEnabled(true);
			} else {
				addGTButton.setEnabled(true);
				clearGTButton.setEnabled(false);
			}
			addResultButton.setEnabled(true);
		} else {
			saveAnnotationButton.setEnabled(false);
			clearResultButton.setEnabled(false);
			addResultButton.setEnabled(false);
			addGTButton.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	public void deleteItem() 
	{
		BenchmarkDocument benchDoc = (BenchmarkDocument) annoViewer.getInput();
		TreeSelection tsel = null;
		Object parent = null;

		IStructuredSelection sel = (IStructuredSelection) annoViewer
				.getSelection();
		if (sel instanceof TreeSelection) {
			tsel = (TreeSelection) sel;
			TreePath paths = tsel.getPathsFor(tsel.getFirstElement())[0];
			parent = paths.getParentPath().getLastSegment();
		}

		Iterator<?> iter = sel.iterator();
		while (iter.hasNext()) 
		{
			Object obj = iter.next();
			if (obj instanceof Annotation) {
				benchDoc.getAnnotations().remove(obj);
			} else if (obj instanceof AnnotationPage) {
				if (parent instanceof Annotation) {
					((Annotation) parent).getPages().remove(obj);
				} else if (parent instanceof TableSelection) {
					((TableSelection) parent).getPages().remove(obj);
				}
			} else if (obj instanceof ExtractionResult) {
				if (parent instanceof AnnotationPage) {
					((AnnotationPage) parent).getItems().remove(obj);
				}
			} else if (obj instanceof PdfSelection) {
				if (parent instanceof ExtractionResult) {
					// ((ExtractionResult)parent).getItems().remove(obj);
				} else if (parent instanceof LabelSelection) {
					// ((LabelSelection)parent).setSelection(null);
				}
			}
			else if (obj instanceof AbstractSelection) 
			{
				if (parent instanceof AnnotationPage) 
				{
					((AnnotationPage) parent).getItems().remove(obj);
				}
				else if (parent instanceof Annotation)
				{
					Annotation ann = (Annotation) parent;
					ann.getItems().remove(obj);
				}
				else if (parent instanceof SinglePageSelection)
				{
					SinglePageSelection selection = (SinglePageSelection) parent;
					selection.getItems().remove(obj);
				}
				else if (parent instanceof MultiPageSelection)
				{
					MultiPageSelection selection = (MultiPageSelection) parent;
					for (AnnotationPage page : selection.getPages())
					{
						page.getItems().remove(obj);
					}
				}
			} 
			else if (obj instanceof TableCell) 
			{
				if (parent instanceof TableCellContainer) {
					TableCellContainer cont = (TableCellContainer) parent;
					cont.getCells().remove(obj);
				}
			} 
			else if (obj instanceof RegionSelection) 
			{
				((AnnotationPage) parent).getItems().remove(obj);
			}
			else if (obj instanceof RegionSelection) 
			{
				if (parent instanceof AnnotationPage) {
					((AnnotationPage) parent).getItems().remove(obj);
				}
			}
			else if (obj instanceof TableSelection) 
			{
				if (parent instanceof TableAnnotation) {
					((TableAnnotation) parent).getTables().remove(obj);
				}
			}
		}

		AnnotatorEditor we = (AnnotatorEditor) Activator.getActiveEditor();
		DocumentModel model = we.documentControl.getDocModel();
		DocumentUpdate update = new DocumentUpdate();
		update.setType(UpdateType.DOCUMENT_CHANGE);
		update.setUpdate(model);
		we.documentControl.setDocumentUpdate(update);

		DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
		if (editor!=null)
		{
			editor.highlightBox(null);
		}
		annoViewer.refresh();
		dirty = true;
		updateActionEnablement();
	}

	/**
	 * 
	 */
	private void createContextMenu() 
	{
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});

		annoViewer.getControl().setMenu(
				menuMgr.createContextMenu(annoViewer.getControl()));

		// Register menu for extension
		getSite().registerContextMenu(menuMgr, annoViewer);
	}

	/**
	 * 
	 * @param mgr
	 */
	private void fillContextMenu(IMenuManager mgr) {
		mgr.add(deleteItemAction);
	}

	/**
	 * 
	 * @param document
	 * @param type
	 */
	private AbstractSelection addNewAnnotationFromSelectionType(BenchmarkDocument document,	String type) 
	{
		/* find or create a new layout annotation */
		Annotation annotation = (LayoutAnnotation) ModelUtils2
				.findNamedAnnotation(document, AnnotationType.LAYOUT);
		if (annotation == null) 
		{
			annotation = new LayoutAnnotation();
			document.getAnnotations().add(annotation);
		}

		AbstractSelection selection = null;
		
		/* create new selection according to selection */
		if (type.equalsIgnoreCase("TABLE"))
		{
			/* create a new table selection */
			selection = new TableSelection();

		}
		else if (type.equalsIgnoreCase("LIST"))
		{
			/* create a new list selection */
			selection = new ListSelection();
		}
		else if (type.equalsIgnoreCase("SECTION"))
		{
			/* create a new table selection */
			selection = new SectionSelection();
		}
		else if (type.equalsIgnoreCase("CONCEPT SECTION"))
		{
			/* create a new table selection */
			selection = new ConceptSectionSelection();
		}
		else if (type.equalsIgnoreCase("FUNCTIONAL"))
		{
			/* create a new table selection */
			selection = new FunctionalSelection();
		}
		else if (type.equalsIgnoreCase("SEMANTIC"))
		{
			/* create a new table selection */
			selection = new SemanticSelection();
		}
		else if (type.equalsIgnoreCase("TEXT"))
		{
			/* create a new table selection */
			selection = new TextSelection();
		}
		else if (type.equalsIgnoreCase("FIGURE"))
		{
			/* create a new table selection */
			selection = new FigureSelection();
		}

		int id = getCounter(document, type);
		selection.setId(id);
		
		//finally, add the newly created selection
		if (selection!=null)
		{
			annotation.getItems().add(selection);
		}

		return selection;
	}

	@Override
	public void setFocus() {
		annoViewer.getControl().setFocus();
	}

	/**
	 * Set the benchmark document to be displayed.
	 * 
	 * If a previous benchmark document exists, the algorithm tries to merge the
	 * two benchmark documents.
	 * 
	 * @param benchDoc
	 * @param replace
	 */
	public void setBenchmarkDocument(BenchmarkDocument document,
			boolean replace, boolean force) {
		this.document = (PdfBenchmarkDocument) document;

		// check if we have to merge annotations...
		if (annoViewer.getInput() == null || replace) {
			annoViewer.setInput(document);
			annoViewer.setAutoExpandLevel(EXPAND_LEVEL);
			return;
		}

		dirty = true;
		annoViewer.setAutoExpandLevel(EXPAND_LEVEL);
		updateActionEnablement();
	}

	/**
	 * Find the current counter for the specified layout type.
	 * 
	 * @param bdoc
	 * @param type
	 * @return
	 */
	private int getCounter(BenchmarkDocument bdoc, String type )
	{
		int counter = 0;
		List<Integer> existing = new ArrayList<Integer>();
		List<Annotation> annos = bdoc.getAnnotations();
		for (Annotation ann : annos)
		{
			if (ann instanceof LayoutAnnotation)
			{			
				LayoutAnnotation annotation = (LayoutAnnotation) ann;
				List<AbstractSelection> selections = annotation.getItems();
				for (AbstractSelection selection : selections)
				{
					if (selection.getType().equalsIgnoreCase(type))
					{
						counter ++;
						existing.add(selection.getId());
					}
				}
			}
		}

		while (existing.contains(counter))
		{
			counter ++;
		}
		return counter;
	}

	/**
	 * 
	 * @param parent
	 * @param type
	 * @return
	 */
	private int getCounter (AbstractSelection parent, String type)
	{
		int counter = 0;
		if (parent instanceof SinglePageSelection)
		{
			if (parent instanceof RegionSelection)
			{
				RegionSelection region = (RegionSelection) parent;
				return region.getItems().size();
			}
		}
		else if (parent instanceof MultiPageSelection)
		{
			MultiPageSelection selection = (MultiPageSelection) parent;
			List<AnnotationPage> pages = selection.getPages();
			for (AnnotationPage page : pages)
			{
				List<AbstractSelection> sels = page.getItems();
				for (AbstractSelection sel : sels) 
				{
					if (sel.getType().equalsIgnoreCase(type)) {
						counter++;
					}
				}
			}
		}

		return counter;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (annoViewer != null) {
			annoViewer.addSelectionChangedListener(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return annoViewer.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (annoViewer != null) {
			annoViewer.removeSelectionChangedListener(listener);
		}
	}

	@Override
	public void setSelection(ISelection selection) {

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) 
	{
		/* determine parent */
		ITreeSelection ts = (ITreeSelection) getSelection();
		if (ts.getFirstElement()==null)
			return;

		if (ts instanceof Annotation)
		{

		}
		if (!(ts.getFirstElement() instanceof AbstractSelection))
		{
			MessageDialog
			.openInformation(combo2.getShell(),
					"Information",
					"No parent selected. Please select a parent layout selection in the Annotation View.");
			return;
		}
		AbstractSelection parent = (AbstractSelection) ts.getFirstElement();
		parent = setSelectionProperties(parent);
		if (parent==null)
		{
			MessageDialog
			.openInformation(combo2.getShell(),
					"Information",
					"No parent selected. Please select a parent layout selection in the Annotation View.");
			return;
		}

		if (event.getSelection() != null
				&& event.getSelection() instanceof IStructuredSelection)
		{
			IStructuredSelection selection = 
					(IStructuredSelection) event.getSelection();
			Object obj = selection.getFirstElement();


			// get current benchmark item
			PdfBenchmarkDocument document = null;
			IEditorPart editor = DocWrapUIUtils.getActiveEditor();
			if (!(editor instanceof AnnotatorEditor))
			{
				ErrorDump.debug(this, "Cannot parse editor");
			}

			AnnotatorEditor we = (AnnotatorEditor) editor;
			document = (PdfBenchmarkDocument) we.getActiveDocument();
			if (document != null)
			{
				int pageNum = we.getCurrentDocumentPageNum();
				PdfDocumentPage page = document.getPage(pageNum);
				ISegmentGraph dg = page.getGraph();
				if (dg == null) {
					return;
				}

				/* Extract PDF operations under region */
				if (obj instanceof org.eclipse.swt.graphics.Rectangle) 
				{
					org.eclipse.swt.graphics.Rectangle rect = (org.eclipse.swt.graphics.Rectangle) obj;
					Rectangle rectangle = new Rectangle(rect.x, rect.y,	rect.width, rect.height);

					/* add a region selection */
					RegionSelection region = createRegionSelectionUnder(pageNum, rectangle, dg);
					if (region==null)
						return;

					int regionId = getCounter((AbstractSelection)parent, "REGION");
					region.setId(regionId);	
					region.setPageNum(pageNum);

					/* add region to parent */
					if (parent instanceof MultiPageSelection)
					{
						AnnotationPage p = findPageFromMultiPageSelection(
								(MultiPageSelection) parent, pageNum);
						if (p == null) 
						{
							p = new AnnotationPage();
							p.setPageNum(pageNum);
							((MultiPageSelection) parent).getPages().add(p);
						}
						p.getItems().add(region);
					}
					else if (parent instanceof SinglePageSelection)
					{
						SinglePageSelection spParent = (SinglePageSelection) parent;
						spParent.getItems().add(region);
					}
					else
					{
						System.err.println();
					}

				}
			}

			setBenchmarkDocument(document, false, false);
			annoViewer.setInput(document);
		}
		dirty = true;
		updateActionEnablement();

		//refresh canvas
		DocWrapEditor docWrapEditor = DocWrapUIUtils.getDocWrapEditor();
		if (docWrapEditor!=null){
			docWrapEditor.refresh();
		}
	}

	/**
	 * 
	 * @param selection
	 * @return
	 */
	private AbstractSelection setSelectionProperties (AbstractSelection selection)
	{
		String item1 = combo1.getItem(combo1.getSelectionIndex());
		String item2 = combo2.getItem(combo2.getSelectionIndex());

		item1 = item1.replaceAll(" ", "_");
		if (!item1.equalsIgnoreCase(selection.getType()))
		{
			return null;
		}
		else if (selection instanceof SectionSelection)
		{
			((SectionSelection) selection).setSectionType(item2);
		}
		else if (selection instanceof TextSelection)
		{
			((TextSelection) selection).setTextType(item2);
		}
		else if (selection instanceof ConceptSectionSelection)
		{
			((ConceptSectionSelection) selection).setSectionConcept(item2);
		}
		else if (selection instanceof SemanticSelection)
		{
			((SemanticSelection) selection).setSemantic(item2);
		}
		else if (selection instanceof FunctionalSelection)
		{
			((FunctionalSelection) selection).setFunction(item2);
		}
		else if (selection instanceof FigureSelection)
		{
			//add an image
			if (item2.equalsIgnoreCase("image"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((FigureSelection) selection).getPages().add(page);
				}

				//add cell selection to page
				int imgId = getCounter(selection, item2);
				ImageSelection img = new ImageSelection();
				img.setId(imgId);
				page.getItems().add(img);

				selection = img;
			}
			if (item2.equalsIgnoreCase("caption"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((FigureSelection) selection).getPages().add(page);
				}

				//add cell selection to page
				int imgId = getCounter(selection, item2);
				CaptionSelection capt = new CaptionSelection();
				capt.setId(imgId);
				page.getItems().add(capt);

				selection = capt;
			}
		}
		else if (selection instanceof TableSelection)
		{
			if (item2.equalsIgnoreCase("cell"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((TableSelection) selection).getPages().add(page);
				}

				//add cell selection to page
				int cellId = getCounter(selection, item2);
				TableCell cell = new TableCell(cellId);
				page.getItems().add(cell);

				selection = cell;
			}
			else if (item2.equalsIgnoreCase("column"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((TableSelection) selection).getPages().add(page);
				}

				//add cell selection to page
				int colId = getCounter(selection, item2);
				TableColumnSelection col = new TableColumnSelection();
				col.setId(colId);
				page.getItems().add(col);

				selection = col;
			}
			else if (item2.equalsIgnoreCase("row"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((TableSelection) selection).getPages().add(page);
				}

				//add cell selection to page
				int rowId = getCounter(selection, item2);
				TableRowSelection row = new TableRowSelection();
				row.setId(rowId);
				page.getItems().add(row);

				selection = row;
			}
		}
		else if (selection instanceof ListSelection)
		{
			if (item2.equalsIgnoreCase("List item"))
			{
				//get current page
				int pageNum = DocumentController.docModel.getPageNum();
				AnnotationPage page = findAnnotationPageUnderSelection(
						(MultiPageSelection) selection, pageNum);

				if (page == null) {
					// create a new annotation page
					page = new AnnotationPage();
					page.setPageNum(pageNum);
					((ListSelection) selection).getPages().add(page);
				}

				//add list item selection to page
				ListItemSelection item = new ListItemSelection();
				page.getItems().add(item);

				selection = item;
			}
		}
		return selection;
	}

	/**
	 * 
	 * @param pageNum
	 * @param r
	 * @param dg
	 * @return
	 */
	private RegionSelection createRegionSelectionUnder (int pageNum, Rectangle r, ISegmentGraph dg)
	{
		List<OpTuple> pdfOperators = new ArrayList<OpTuple>();
		List<AbstractSelection> sels = new ArrayList<AbstractSelection>();
		Rectangle bounds = null;

		Rectangle rectangle = new Rectangle(r.x, r.y, r.width, r.height);

		if (getSelection() != null) 
		{
			// extract sub graph
			rectangle = DocGraphUtils.yFlipRectangle(rectangle, dg);

			//get subgraph
			ISegmentGraph sub = DocGraphUtils.getDocumentSubgraphUnderRectangle(dg,	rectangle);
			ISegmentGraph seg = dg.getSubGraph(sub.getNodes());
			seg.computeDimensions();
			bounds = seg.getDimensions();

			MapList<OpTuple, GenericSegment> op2gsMap = 
					new HashMapList<OpTuple, GenericSegment>();
			for (DocNode node : sub.getNodes())
			{
				GenericSegment gs = sub.getNodeSegHash().get(node);
				List<OpTuple> ops = gs.getOperators();
				if (gs instanceof CharSegment) 
				{
					OpTuple op = ((CharSegment) gs).getSourceOp();
					pdfOperators.add(op);
					op2gsMap.putmore(op, (CharSegment) gs);
				}
				else if (gs instanceof TextFragment) 
				{
					if (ops != null && ops.size() == 1)
					{
						OpTuple op = ops.get(0);
						pdfOperators.add(op);
						op2gsMap.putmore(op, (TextFragment) gs);
					} 
				}
				else if (gs instanceof TextSegment)
				{
					if (ops != null && ops.size() == 1) 
					{
						OpTuple op = ops.get(0);
						pdfOperators.add(op);
						op2gsMap.putmore(op, (TextSegment) gs);
					}
				}
				else if (gs instanceof ImageSegment)
				{
					if (ops != null && ops.size() >= 1) 
					{
						for (OpTuple op : ops)
						{
							pdfOperators.add(op);
							op2gsMap.putmore(op, (ImageSegment) gs);
						}
					}
				}
			}
			ListUtils.unique(pdfOperators);
			if (pdfOperators.size() == 0)
			{
				MessageDialog
				.openInformation(combo2.getShell(),
						"Information",
						"No valid selection (no corresponding PDF operators selected).");
				return null;
			}

			PdfSelection pdfSel = new PdfSelection();
			for (OpTuple opTuple : pdfOperators) 
			{
				PDFInstruction idx = new PDFInstruction(
						opTuple.getOpIndex(), 
						opTuple.getArgIndex());
				Rectangle region = null;
				StringBuffer txt = new StringBuffer();
				for (GenericSegment segment : op2gsMap.get(opTuple))
				{
					if (segment instanceof TextSegment)
					{
						TextSegment tseg = (TextSegment) segment;
						txt.append(tseg.getText());
						if (region == null) {
							region = tseg.getBoundingRectangle();
						} else {
							region = region.union(tseg.getBoundingRectangle());
						}
					}
					else if (segment instanceof ImageSegment)
					{
						ImageSegment iseg = (ImageSegment) segment;
						if (region == null) {
							region = iseg.getBoundingRectangle();
						} else {
							region = region.union(iseg.getBoundingRectangle());
						}
					}
				}
				if (region!=null)
				{
					String tmp = DocGraphUtils.getTextUnderRegion(region, sub);
					idx.setText(tmp);//txt.toString()
					idx.setBounds(region);
					pdfSel.getInstructions().add(idx);
				}
			}
			pdfSel.setBounds(bounds);

			/* compute text */
			//						DocGraphUtils.flipReverseDocGraph(dg);
			String text = DocGraphUtils.getTextUnderRegion(rectangle, dg);
			pdfSel.setText(text);
			sels.add(pdfSel);
		}

		RegionSelection result = new RegionSelection();
		result.setBounds(bounds);
		result.setPageNum(pageNum);
		
		//add children
		for (AbstractSelection sel : sels)
		{
			if (sel instanceof PdfSelection)
			{	
				result.getInstructionContainer().addAll(
						((PdfSelection) sel).getInstructions());
				//									 String text = ((PdfSelection) sel).getInstructions().get(0).getText();
				TextSelection tsel = new TextSelection();
				tsel.setTextContent(((PdfSelection) sel).getText());
				tsel.setBounds(sel.getBounds());
				result.setText(tsel);
			}
			else if (sel instanceof TextSelection)
			{
				result.setText((TextSelection) sel);
			}
		}
		return result;
	}

	/**
	 * 
	 */
	public AnnotationPage findPageFromMultiPageSelection(MultiPageSelection sel, int pageNum) 
	{
		List<AnnotationPage> pages = sel.getPages();
		for (AnnotationPage page : pages) {
			if (page.getPageNum() == pageNum) {
				return page;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param b
	 * @param uri
	 * @return
	 */
	public BenchmarkDocument findBenchDoc(Benchmark b, String uri) {
		List<BenchmarkDocument> docs = b.getDocuments();
		for (BenchmarkDocument doc : docs) {
			if (doc.getUri().equals(uri)) {
				return doc;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param document
	 * @param pageNum
	 * @return
	 */
	public AnnotationPage findAnnotationPageInBenchmarkDocument(
			PdfBenchmarkDocument document, int pageNum) 
	{
		for (AnnotationPage page : document.getAnnotationPages()) 
		{
			if (page.getPageNum() == pageNum) {
				return page;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param selection
	 * @param pageNum
	 * @return
	 */
	public AnnotationPage findAnnotationPageUnderSelection(MultiPageSelection selection, int pageNum) 
	{
		for (AnnotationPage page : selection.getPages()) 
		{
			if (page.getPageNum() == pageNum) {
				return page;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param b
	 * @param sel
	 * @return
	 */
	public BenchmarkDocument findBenchDocContaining(Benchmark b,
			AnnotationPage page) {
		List<BenchmarkDocument> docs = b.getDocuments();
		for (BenchmarkDocument doc : docs) {
			List<Annotation> annos = doc.getAnnotations();
			for (Annotation ann : annos) {
				if (ann instanceof TableAnnotation) {
					TableAnnotation tann = (TableAnnotation) ann;
					List<TableSelection> selections = tann.getTables();
					for (TableSelection table : selections) {
						List<AnnotationPage> pages = table.getPages();
						for (AnnotationPage p : pages) {
							if (p.equals(page)) {
								return doc;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/*******************************************************************************************
	 * 
	 */

	public class TreeLabelProvider2 extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {

			if (element instanceof String) {
				// String value = (String) element;
				// if (value.contains(":")) {
				// return (value.split(":")[1]);
				// }
				return (String) element;
			} else if (element instanceof Rectangle) {
				Rectangle r = (Rectangle) element;
				return "(x=" + r.x + ",y=" + r.y + ",width=" + r.width
						+ ",height=" + r.height + ")";
			} else if (element instanceof PDFInstruction) {
				PDFInstruction item = (PDFInstruction) element;
				return "" + item.getIndex() + ":" + item.getSubIndex();
			} else if (element instanceof TextSelection) {
				return ((TextSelection) element).getTextContent();
			}
			return "";
		}
	}

	public static class TreeEditingSupport extends EditingSupport {
		CellEditor editor;
		int column;

		public TreeEditingSupport(TreeViewer viewer, int column) {
			super(viewer);
			this.column = column;
			editor = new TextCellEditor(viewer.getTree());
		}

		@Override
		protected boolean canEdit(Object element) {
			return false;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof BenchmarkDocument) {
				// BenchmarkDocument bdoc = (BenchmarkDocument) element;
				return element;// (column == 0) ? ((BenchmarkDocument)element) :
				// ((BenchmarkDocument)element);
			} else if (element instanceof TableSelection) {
				// TableSelection bdoc = (TableSelection) element;
				return element;
			}
			return (column == 0) ? ((String) element) : ((String) element);
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (column == 0) {
				if (element instanceof BenchmarkDocument) {
					((BenchmarkDocument) element).setUri((String) value);
				} else if (element instanceof TableSelection) {
					// ((TableSelection)element).setId(3);
				}
			} 
		}
	}

	public void setInput(BenchmarkDocument document) {
		this.document = (PdfBenchmarkDocument) document;

		this.annoViewer.setInput(document);
		this.gtViewer.setInput(document);

		updateActionEnablement();

		//refresh canvas
		DocWrapEditor editor = DocWrapUIUtils.getDocWrapEditor();
		if (editor!=null)
		{
			editor.refresh();
		}
	}

	@Override
	public void modelChanged(ModelChangedEvent event) {

	}
}
