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
package at.tuwien.dbai.bladeRunner.editors;

import java.io.File;
import java.net.MalformedURLException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditorInput;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.control.IDocumentUpdateListener;
import at.tuwien.dbai.bladeRunner.control.SelectionController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.annotator.DocWrapEditor;
import at.tuwien.dbai.bladeRunner.editors.annotator.PDFViewerSWT;
import at.tuwien.dbai.bladeRunner.editors.annotator.WeblearnEditor;
import at.tuwien.dbai.bladeRunner.editors.base.BaseEditor;
import at.tuwien.dbai.bladeRunner.views.BenchmarkEditorInput;
import at.tuwien.dbai.bladeRunner.views.SelectionImageView;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;

/**
 * WrapperEditor.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 25, 2011
 */
public class AnnotatorEditor extends BaseEditor 
implements 
IMenuListener,
IDocumentUpdateListener
{
	public static String ID = "at.tuwien.dbai.bladeRunner.wrapperEditor";

	private Map<EditorPart, Integer> editor2PageIndex;
	private BitSet activeEditors = new BitSet();

	/* A MOZILLA editor */
	protected WeblearnEditor webEditor;
	/* A Graph editor */
	protected DocWrapEditor graphEditor;
	/* A PDF viewer */
	protected PDFViewerSWT pdfSWTViewer;
	/* A Combi editor */
	protected DocWrapEditor annoEditor;
	
	/* SINGLETON selection and document controllers */
	public final DocumentController documentControl = new DocumentController();

	public final SelectionController selectionControl = new SelectionController();

	public ISegmentGraph graph;

	/**
	 * Constructor.
	 * 
	 * @param site
	 * @param input
	 */
	public AnnotatorEditor(IEditorSite site, IEditorInput input) {
		this();

		try {
			init(site, input);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		if (input instanceof BenchmarkEditorInput) {
			BenchmarkEditorInput eei = (BenchmarkEditorInput) input;
			PdfBenchmarkDocument benchDoc = (PdfBenchmarkDocument) eei
					.getBenchmarkDocument();
//			this.activeDocument = benchDoc;

			DocumentModel model = new DocumentModel();
			model.setDocument(benchDoc);
			model.setNumPages(benchDoc.getNumPages());
			model.setPdfFile(benchDoc.getPdfFile());

			DocumentUpdate update = new DocumentUpdate();
			update.setUpdate(model);
			update.setType(UpdateType.NEW_DOCUMENT);
			documentControl.setDocumentUpdate(update);

			pdfSWTViewer.setMaxPage(benchDoc.getNumPages());
		}
	}

	/**
	 * Constructor.
	 */
	public AnnotatorEditor() {
		super();

		documentControl.addDocumentUpdateListener(this);

		editor2PageIndex = new HashMap<EditorPart, Integer>();
	}

	@Override
	public String getTitle() {
		return "Wrapper Editor Test";
	}

	/**
	 * Set the status line.
	 * 
	 * @param message
	 */
	public void setStatusLine(String message) {
		// Get the status line and set the text
		IActionBars bars = getEditorSite().getActionBars();
		bars.getStatusLineManager().setMessage(message);
	}

	@Override
	public void showBusy(boolean busy) {
		super.showBusy(busy);
		/* change cursor to/from busy */
		Cursor busyCursor;
		if (busy) {
			busyCursor = new Cursor(getSite().getShell().getDisplay(),
					SWT.CURSOR_WAIT);
		} else {
			busyCursor = new Cursor(getSite().getShell().getDisplay(),
					SWT.CURSOR_ARROW);
		}
		getSite().getShell().setCursor(busyCursor);
	}

	/**
	 * Change the visible pages of this editor.
	 */
	private void setForHTML ()
	{
		/* remove the PDF page */
		Object obj = editor2PageIndex.get(pdfSWTViewer);
		if (obj!=null)
		{
			int bitIndex = (Integer) obj;

			if (activeEditors.get(bitIndex)) {
				if (getPageCount()>2) {
					removePage(bitIndex);
				} else {
					removePage(0);
				}
				activeEditors.set(bitIndex, false);
			}
		}

		obj = editor2PageIndex.get(webEditor);
		if (obj!=null)
		{
			int bitIndex = (Integer) obj;

			if (!activeEditors.get(bitIndex))
			{
				/* add Web Editor page */
				try
				{
					MozBrowserEditorInput mozInput = null;
					if (getEditorInput() instanceof IFileEditorInput) {
						IFileEditorInput fileIn = (IFileEditorInput) getEditorInput();
						mozInput = new MozBrowserEditorInput(fileIn);
					}

					addPage(0, webEditor, mozInput);
					activeEditors.set(bitIndex);

				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		setPageText(0, "Document View");
		setActivePage(0);
	}

	/**
	 * Change the visible pages of this editor.
	 */
	private void setForPDF ()
	{
		/* remove the WebEditor page */
		int bitIndex = editor2PageIndex.get(webEditor);
		if (activeEditors.get(bitIndex)) {
			if (getPageCount()>2) {
				removePage(bitIndex);
			} else if (getPageCount()>0)
			{
				removePage(0);
			}
			activeEditors.set(bitIndex, false);
		}

		bitIndex = editor2PageIndex.get(pdfSWTViewer);
		if (!activeEditors.get(bitIndex))
		{
			/* add PDF viewer page */
			try
			{
				addPage(0, pdfSWTViewer, getEditorInput());
				activeEditors.set(bitIndex);

			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		//		setPageText(0, "Document View");
		//		setActivePage(0);
	}

	@Override
	public void dispose() {
		super.dispose();

		deregisterFromDocumentUpdate(pdfSWTViewer);
		documentControl.removeDocumentUpdateListener(pdfSWTViewer);
		documentControl.removeDocumentUpdateListener(this);
		selectionControl.deregisterComponent(pdfSWTViewer);
	}

	@Override
	protected void createPages()
	{
		createPage1();
		createPage2();
		createPage3();
		
		setPartName("Loading...");
	}

	/**
	 * Setup graph editor.
	 */
	private void createPage1() 
	{
		try 
		{
			/* create a new graph editor */
			annoEditor = new DocWrapEditor(this, getEditorSite(),
					getEditorInput());

			selectionControl.registerComponent(annoEditor);
			documentControl.addDocumentUpdateListener(annoEditor);

			int index = addPage(annoEditor, getEditorInput());
			setPageText(index, "Combi Editor");

			editor2PageIndex.put(graphEditor, index);
			activeEditors.set(index);
		} 
		catch (PartInitException e) 
		{
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}
	
	/**
	 * Setup PDF editor.
	 */
	private void createPage2() 
	{
		try
		{
			pdfSWTViewer = new PDFViewerSWT(getEditorSite(), getEditorInput());
			pdfSWTViewer.setWrapperEditor((AnnotatorEditor) this);
			documentControl.addDocumentUpdateListener(pdfSWTViewer);
			selectionControl.registerComponent(pdfSWTViewer);

			int index = addPage(pdfSWTViewer, getEditorInput());
			setPageText(index, "PDF Viewer");

			editor2PageIndex.put(pdfSWTViewer, index);
			activeEditors.set(index);
		}
		catch (PartInitException e) 
		{
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Create the MOZ browser page.
	 */
	private void createPage3 ()
	{
		try
		{
			webEditor = new WeblearnEditor(this);
			IEditorInput in =  getEditorInput();
			MozBrowserEditorInput mozInput = null;
			if (in instanceof MozBrowserEditorInput)
			{
				mozInput = (MozBrowserEditorInput) getEditorInput();
			}
			else if (in instanceof DocumentEditorInput)
			{
				mozInput = new MozBrowserEditorInput(((DocumentEditorInput) in).getDocument().getUri());
			}
			else if (in instanceof BenchmarkEditorInput)
			{
				mozInput = new MozBrowserEditorInput(((BenchmarkEditorInput) in).getBenchmarkDocument().getUri());
			}
			if (mozInput==null)
			{
				mozInput = new MozBrowserEditorInput("about:blank");
			}
			
			int index = addPage(webEditor, mozInput);
			setPageText(index, webEditor.getTitle());

			editor2PageIndex.put(webEditor, index);
			activeEditors.set(index);

			documentControl.addDocumentUpdateListener(webEditor);			
			getSite().setSelectionProvider(webEditor);
		}
		catch (PartInitException e)
		{
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		} 
	}

	/**
	 * 
	 * @param update
	 */
	public void setDocumentUpdated(DocumentUpdate update) {
		documentControl.setDocumentUpdate(update);
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well
	 * registering the menu for extension.
	 */
	public Menu createAndRegisterContextMenuFor(StructuredViewer viewer) 
	{
		MenuManager contextMenu = new MenuManager("#PopUp");
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(this);
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		return menu;
	}


	public DocWrapEditor getGraphEditor() {
		return annoEditor;
	}

	public void registerForSelection(ISelectionChangedListener listener) {
		selectionControl.registerComponent(listener);
	}

	public void deregisterFromSelection(ISelectionChangedListener listener) {
		selectionControl.removeSelectionChangedListener(listener);
	}

	public void registerForDocumentUpdate(IDocumentUpdateListener listener) {
		documentControl.addDocumentUpdateListener(listener);
	}

	public void deregisterFromDocumentUpdate(IDocumentUpdateListener listener) {
		documentControl.removeDocumentUpdateListener(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help
	 * fill the context menus with contributions from the Edit menu.
	 */
	// OK
	public void menuAboutToShow(IMenuManager menuManager) {
		((IMenuListener) getEditorSite().getActionBarContributor())
				.menuAboutToShow(menuManager);
	}

	public IDocument getActiveDocument() {
		return documentControl.getCurrentDocumentEntry();
	}

	public int getCurrentDocumentPageNum() {
		return documentControl.getCurrentDocumentPage();
	}

	/**
	 * Returns the required view if it is available
	 * @return
	 */
	public ViewPart findView (String id) 
	{
		ViewPart result = null;
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			IViewReference ref = page.findViewReference(id);
			if (ref != null) {
				result = (ViewPart) ref.getPart(true);
			}
		}
		return result;
	}
	/**
	 * 
	 * @param input
	 */
	public void setInput2(IEditorInput input) 
	{
		if (input instanceof BenchmarkEditorInput) {
			/* create a new document model */
			DocumentModel model = new DocumentModel();

			BenchmarkEditorInput eei = (BenchmarkEditorInput) input;
			BenchmarkDocument benchmark = (BenchmarkDocument) eei.getBenchmarkDocument();
			if (benchmark instanceof PdfBenchmarkDocument) 
			{
				PdfBenchmarkDocument benchDoc = (PdfBenchmarkDocument) benchmark;
				model.setDocument(benchDoc);
				model.setNumPages(benchDoc.getNumPages());
				model.setPdfFile(benchDoc.getPdfFile());
				model.setFormat(DocumentFormat.PDF);

				pdfSWTViewer.setMaxPage(benchDoc.getNumPages());
				
				setForPDF();
			} 
			else 
			{
				model.setFormat(DocumentFormat.HTML);
				model.setUri(benchmark.getFileName());	
				
				setForHTML();
			}

			model.setDocument(benchmark);
			
			String fileName = benchmark.getFileName();
			fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.length());
			setPartName(fileName);

			DocumentUpdate update = new DocumentUpdate();
			update.setUpdate(model);
			update.setProvider(this);
			update.setType(UpdateType.NEW_DOCUMENT);
			setDocumentUpdated(update); // tell self
		}

		// register annotation view as selection listener...
		IWorkbenchPage page = getSite().getPage();
		if (page != null) 
		{
			IViewReference ref = page.findViewReference(BenchmarkNavigatorView.ID);
			if (ref != null) {
				BenchmarkNavigatorView av = (BenchmarkNavigatorView) ref.getPart(true);
				selectionControl.addSelectionChangedListener(av);
			}
			ref = page.findViewReference(SelectionImageView.ID);
			if (ref != null) {
				SelectionImageView av = (SelectionImageView) ref.getPart(true);
				selectionControl.addSelectionChangedListener(av);
			}
		}
	}

	@Override
	public void documentUpdated(DocumentUpdateEvent ev) {
		// DocumentModel model = ev.getDocumentUpdate().getUpdate();
		// DocumentGraph dg = null;
		// ISegmentGraph sg = model.getDocumentGraph();
		// if (sg instanceof ISegHierGraph) {
		// dg = (DocumentGraph) ((ISegHierGraph) sg).getBaseGraph();
		// } else {
		// dg = (DocumentGraph) sg;
		// }

		// this.activeDocument.setPdfFile(model.getPdfFile());
		// this.activeDocument.setFileName(model.getDocument().getUri());
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub

	}
	
}// WrapperEditor
