package at.tuwien.dbai.bladeRunner.editors.annotator;

import static at.tuwien.prip.mozcore.utils.MozJavaDocumentMap2.javaElement2mozElement;

import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.ui.console.IJavaScriptConsole;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mozilla.dom.DocumentImpl;
import org.mozilla.dom.html.HTMLDocumentImpl;
import org.mozilla.interfaces.nsIContentViewer;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIDocShell;
import org.mozilla.interfaces.nsIInterfaceRequestor;
import org.mozilla.interfaces.nsIMarkupDocumentViewer;
import org.mozilla.interfaces.nsIWebBrowser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdateEvent;
import at.tuwien.dbai.bladeRunner.control.IDOMDocumentListener;
import at.tuwien.dbai.bladeRunner.control.IDocumentUpdateListener;
import at.tuwien.dbai.bladeRunner.control.IModelChangedListener;
import at.tuwien.dbai.bladeRunner.control.ModelChangedEvent;
import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.dbai.bladeRunner.utils.BrowserMonitor;
import at.tuwien.dbai.bladeRunner.utils.DOMUtils;
import at.tuwien.dbai.bladeRunner.utils.DocumentGraphFactory;
import at.tuwien.dbai.bladeRunner.utils.SelectionBox2;
import at.tuwien.dbai.bladeRunner.views.SelectionImageView;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;
import at.tuwien.prip.model.project.document.html.HTMLDocument;
import at.tuwien.prip.mozcore.NodeFactory;
import at.tuwien.prip.mozcore.utils.CSSException;
import at.tuwien.prip.mozcore.utils.MozCssUtils;


/**
 * WeblearnEditor.java
 *
 *
 *
 * @author max <mcgoebel@gmail.com>
 * @date Jan 28, 2011
 */
public class WeblearnEditor extends MozBrowserEditor
implements
ISelectionChangedListener,
IDocumentUpdateListener,
IModelChangedListener
{
	protected AnnotatorEditor we;

	protected List<BrowserMonitor> monitors;

	protected SelectionBox2 sbox2;

	/**
	 * This keeps track of the selection of the editor as a whole.
	 */
	//OK
	protected ISelectionChangedListener selectionChangedListener;
	protected ListenerList selectionChangedListeners = new ListenerList();
	protected ISelection editorSelection = StructuredSelection.EMPTY;

	protected List<IDOMDocumentListener> documentListeners;

	protected SimpleTimer timer = new SimpleTimer();

	private Composite composite;

	private int zoomLevel = 7;

	protected boolean ctrlKey;
	protected boolean altKey;
	protected boolean shiftKey;

	public static final String SCRIPT = "" +
	" walk(document.body);" +
	"function walk(root)" +
	"{  " +
	" if (root.tagName == 'PRE' || root.tagName == 'pre')" +
	" { " +
	"	root.parentNode.replaceChild(document.createElement('span'), root);" +
	" }" +
	" if (root.nodeType == 3)    " +
	" { " +
	"	 doReplace(root); " +
	"	 return;   " +
	" } " +
	" var children = root.childNodes;" +
	" for (var i = children.length - 1 ; i >= 0 ; i--)" +
	" {" +
	" 	walk(children[i]);" +
	" }" +
	"}" +
	"function doReplace(text)" +
	"{" +
	"    var div = document.createElement('div');" +
	"    div.innerHTML = text.nodeValue.replace(/\\b(\\w+)\\b/g, '<strong>$1</strong>');" +
	"    var parent = text.parentNode;" +
	"    var children = div.childNodes;" +
	"    for (var i = children.length - 1 ; i >= 0 ; i--)" +
	"    {" +
	"        parent.insertBefore(children[i], text.nextSibling);" +
	"    }" +
	"    parent.removeChild(text);" +
	"}";

	/**
	 *
	 * @param parent
	 * @param standalone
	 */
	public WeblearnEditor(Composite parent, boolean standalone)
	{
		super(standalone);
		this.setPartName("Rendered Document");

		this.monitors = new ArrayList<BrowserMonitor>();
		this.documentListeners = new ArrayList<IDOMDocumentListener>();
		this.browserListener = new WLMozBrowserListener(this);

		super.addSelectionChangedListener(this);
		
		//register with model controller
//		DocWrapController.registerForModel(this);
	}

	/**
	 * Constructor.
	 *
	 * @param site
	 * @param input
	 */
	public WeblearnEditor(AnnotatorEditor parent)
	{
		this(null, false);

		this.we = parent;

		//check if we are in annotation mode...
		BenchmarkNavigatorView annotationView = null;
		SelectionImageView selectionView = null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page!=null)
		{
			IViewReference ref = page.findViewReference(BenchmarkNavigatorView.ID);
			if (ref!=null)
			{
				IViewPart viewPart = ref.getView(true);

				if (viewPart instanceof BenchmarkNavigatorView) {
					annotationView = (BenchmarkNavigatorView) viewPart;
				}

				if (annotationView!=null) {
					addSelectionChangedListener(annotationView);
				}
			}
			ref = page.findViewReference(SelectionImageView.ID);
			if (ref!=null)
			{
				IViewPart viewPart = ref.getView(true);

				if (viewPart instanceof SelectionImageView) {
					selectionView = (SelectionImageView) viewPart;
				}

				if (selectionView!=null) {
					addSelectionChangedListener(selectionView);
				}
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		// remove from listeners
		we.deregisterFromDocumentUpdate(this);
		
//		DocWrapController.deregisterFromSelection(this);
//		DocWrapController.deregisterFromModel(this);
	}

	@Override
	public String getTitle() {
		return "Document View";
	}

	@Override
	protected IJavaScriptConsole createJavaScriptConsole() {
		return super.createJavaScriptConsole();
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		composite = parent;

		super.createPartControl(parent);
		
//		parent.setLayout(new GridLayout(1, true));
		
		//add a top panel
//		Composite panel = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout(1, true);
//		panel.setLayout(layout);
		GridData data = new GridData();
//		data.grabExcessHorizontalSpace = true;
//		data.heightHint = 50;
//		panel.setLayoutData(data);
		
		//add browser
//		createBrowserPage(parent);
		getMozillaBrowser().setJavascriptEnabled(false); //speed...
		setPartName("Document View");

//		data = new GridData();
//		data.grabExcessHorizontalSpace = true;
//		data.grabExcessVerticalSpace = true;
//		browser.setLayoutData(data);
		
		//add horizontal bar
//		parent.setLayout(new RowLayout(SWT.HORIZONTAL));
		

		
		browser.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e) {

				if ((e.stateMask & SWT.CTRL) == SWT.CTRL)
				{
					zoomLevel += e.count/2;
					setZoom(zoomLevel);
				}
			}
		});

		//add a global key listener for CTRL key
		getSite().getShell().addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				ctrlKey = false;
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (((e.stateMask & SWT.CTRL) == SWT.CTRL) || e.keyCode==262144)
				{
					ctrlKey = true;
				}
				else
				{
					ctrlKey = false;
				}
			}
		});

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page!=null)
		{
//			IViewReference ref = page.findViewReference(AnnotationView.ID);
//			if (ref!=null)
//			{
//				IViewPart viewPart = ref.getView(true);
//				if (viewPart instanceof AnnotationView)
//				{
//					AnnotationView annotationView = (AnnotationView) viewPart;
//					DocWrapController.registerForSelection(annotationView);
//				}
//			}
//			ref = page.findViewReference(PatternViewClassic.ID);
//			if (ref!=null)
//			{
//				IViewPart viewPart = ref.getView(true);
//				if (viewPart instanceof PatternViewClassic)
//				{
//					PatternViewClassic patternViewClassic = (PatternViewClassic) viewPart;
//					DocWrapController.registerForSelection(patternViewClassic);
//				}
//			}
//			ref = page.findViewReference(PatternViewZest.ID);
//			if (ref!=null)
//			{
//				IViewPart viewPart = ref.getView(true);
//				if (viewPart instanceof PatternViewZest)
//				{
//					PatternViewZest patternViewZest = (PatternViewZest) viewPart;
//					DocWrapController.registerForSelection(patternViewZest);
//				}
//			}
		}

		createActions();
		contributeActions();
//		navBar.setVisible(false);
//		navBar.addExtensionAction( clickSelectAction );
	}

	private void contributeActions()
    {
        IActionBars bars = getEditorSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
    }

    protected void fillLocalPullDown(IMenuManager manager)
    {
//    	manager.add(clickSelectAction);
    }

	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException
	{
		timer.startTask(1);
		setSite(site);
		setInput(input);
		setPartName("Document View");
		site.setSelectionProvider(this);
	}

	/**
	 *
	 * @param url
	 */
	public void openURL (URL url)
	{
		zoomLevel = 7;
		setZoom(zoomLevel);

		timer.stopTask(1);
		timer.startTask(1);

		try
		{
			getMozillaBrowser().setUrl(url.toString());
		}
		catch (Exception e)
		{
			MessageDialog messageDialog = new MessageDialog(getSite().getShell(),
					"Error", null,
					"The selected document could not be loaded.\n\n"+e.getMessage(), MessageDialog.ERROR,
					new String[] { "Close" }, 1);
			if (messageDialog.open() == 1) {

			}
		}
	}

	@Override
	protected void changeSelection(nsIDOMNode selectedNode)
	{
		if (selectedNode==null)
		{
			return;
		}

		IStructuredSelection sel = new StructuredSelection(selectedNode);

		final SelectionChangedEvent event = new SelectionChangedEvent(this, sel);
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}

		super.changeSelection(selectedNode);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	//OK
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
	 */
	//OK
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}
	 * to return this editor's overall selection.
	 */
	//OK
	public ISelection getSelection() {
		return editorSelection;
	}

	/**
	 * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}
	 * to set this editor's overall selection.
	 * Calling this result will notify the listeners.
	 */
	//OK
	public void setSelection(ISelection selection)
	{
		editorSelection = selection;

		final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	public Action getClickSelectAction() {
		return clickSelectAction;
	}

	public AnnotatorEditor getParent() {
		return we;
	}

	@Override
	protected void revealSelection() {
		super.revealSelection();
	}

	/**
	 *
	 * @param event
	 */
	public void fireDocumentLoaded(ProgressEvent event)
	{
		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run()
			{
				HTMLBenchmarkDocument doc = null;
				Document dom = getW3CDocument();
				String uriString = dom.getBaseURI();
				if (uriString.endsWith("/"))
				{
					uriString = uriString.substring(0,uriString.length()-1);
				}
				else if (uriString.endsWith(".wrapper"))
				{
					return;
				}
				else if (uriString.startsWith("file:///"))
				{
					uriString = uriString.replaceAll("file:///", "file:/");
				}

				/* */
				if (doc==null)
				{
					doc = new HTMLBenchmarkDocument();
				}

				//set file type
				DocumentFormat format = null;
				if (uriString!=null && uriString.lastIndexOf(".")>0)
				{
					String fileType = uriString.substring(uriString.lastIndexOf(".")+1);
					if (fileType.equalsIgnoreCase("PDF"))
					{
						format = DocumentFormat.PDF;
					}
					else if (fileType.equalsIgnoreCase("HTML"))
					{
						format = DocumentFormat.HTML;
					}
					else if (uriString.startsWith("http:"))
					{
						format = DocumentFormat.HTML;
					}
				}
				else
				{
					format = DocumentFormat.HTML;
				}

				if (doc.getFormat()==null || doc.getFormat()==DocumentFormat.UNKNOWN)
				{
					doc.setFormat(format);
				}

				if (doc.getUri()==null)
				{
					doc.setUri(uriString);
				}

				//compute bounds
				Rectangle bounds;
				try {
					bounds = DOMUtils.getElementDimension(dom.getDocumentElement());
					doc.setBounds(bounds);
				} catch (CSSException e) {
					e.printStackTrace();
				}

				doc.setCachedJavaDOM(dom);

				//set graph
				ISegmentGraph graph = DocumentGraphFactory.HTML.generateDocumentGraph(dom);
				doc.setDocumentGraph(graph);
				doc.setBounds(graph.getDimensions());
					
				/* notify document listeners */
				DocumentModel model = new DocumentModel();
				model.setDocument(doc);
				DocumentUpdate update = new DocumentUpdate();
				update.setType(UpdateType.DOCUMENT_CHANGE);
				update.setUpdate(model);
				update.setProvider(this);
				we.setDocumentUpdated(update);

				timer.stopTask(1);
				ErrorDump.info(this, "Finished loading document "+uriString+" in " + timer.getTimeMillis(1) + "ms");

				we.setStatusLine("Ready");
			}
		});

	}

	@Override
	public nsIDOMDocument getDocument() {
		return super.getDocument();
	}

	public boolean isLoading() {
		return loading;
	}

	/**
	 * WLMozBrowserListener.java
	 *
	 * Extend the standard Mozilla browser listener
	 *
	 * @author max <mcgoebel@gmail.com>
	 * @date Jan 29, 2011
	 */
	public class WLMozBrowserListener extends MozillaBrowserListener
//	implements IWebBrowser
	{

		WeblearnEditor parent;

		/**
		 *
		 * @param parent
		 */
		public WLMozBrowserListener(WeblearnEditor parent) {
			this.parent = parent;
		}

		/**
		 * Initialization for listeners to the Browser
		 * @see org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditor.MozillaBrowserListener#init()
		 */
		@Override
		public void init() {
			super.init();
		}

		/**
		 * We only care about the completed case.
		 *
		 * notify all of the browser's observers about this event...
		 */
		@Override
		public void completed(ProgressEvent event)
		{
			//propagate this event up to the editor.
			parent.fireDocumentLoaded(event);

			for (IDOMDocumentListener listener : documentListeners) {
				listener.newDocumentLoaded();
			}
		}

//		@Override
		public nsIDOMDocument getDocument() {
			return null;
		}

//		@Override
		public boolean isDocumentLoading() {
			return false;
		}

//		@Override
		public void setSelection(IDOMNodeSelection selection) {
			System.out.println();
		}

//		@Override
//		public WebBrowserType getType() {
//			return null;
//		}
//
//		@SuppressWarnings("rawtypes")
//		@Override
//		public Object getAdapter(Class adapter) {
//			return null;
//		}
	}

	public Browser getBrowser ()
	{
		if (browser==null ||browser.isDisposed())
		{
			browser = new Browser(composite, SWT.MOZILLA);
		}
		return browser;
	}

	/**
	 *
	 * Convert a W3C HTML Document implementation corresponding to
	 * the Mozilla DOM HTML document currently loaded in the browser.
	 *
	 * @return the Mozilla document
	 * @throws SimpleBrowserException
	 */
	public Document getW3CDocument()
	{
		/**
		 * Convert the nsIDOMDocument to a W3CDocument...
		 */
		class DocumentGetter implements Runnable
		{
			public DocumentImpl d;

			public void run()
			{
//				if (browser.getUrl().endsWith(".txt"))
//				{
//					//inject JS to split words
//					boolean success = browser.execute(SCRIPT);
//					if (!success)
//					{
//						ErrorDump.error(this,"Could not inject JavaScript");
//					}
//				}
				nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
				if (webBrowser == null) {
					ErrorDump.error(this,"Could not get the nsIWebBrowser from the Browser widget");
				}

				nsIDOMWindow dw = webBrowser.getContentDOMWindow();
				nsIDOMDocument nsDoc = dw.getDocument();

				sbox2 = new SelectionBox2(nsDoc); //instantiate sbox2

				d = HTMLDocumentImpl.getDOMInstance(nsDoc);

				MozCssUtils.setUserData(d);
//				nsIDOMElement mozElement =
//						(nsIDOMElement)
//						((NodeImpl) d.getDocumentElement()).getInstance().
//						queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
//
//					//access CSS information
//					nsIDOMDocumentView documentView = (nsIDOMDocumentView)mozElement.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
//					nsIDOMViewCSS cssView =  (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
//					nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( mozElement, "" );
//
//					Map<String,String> result = new HashMap<String, String>();
//					for( int i = 0; i < computedStyle.getLength(); i++ ) {
//						result.put(computedStyle.item(i),computedStyle.getPropertyCSSValue(computedStyle.item(i)).getCssText());
//					}
				
//				TreeDomHelper.printDOMtags(d.getDocumentElement(), "  ");
				System.out.println("User data set for "+d.getDocumentURI());
			}
		}

		DocumentGetter dg = new DocumentGetter();
		Display.getDefault().syncExec(dg);

		return (Document) dg.d;
	}

	/**
	 *
	 * @param zoom
	 */
	public void setZoom(int zoom)
	{
		nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
		nsIInterfaceRequestor req = (nsIInterfaceRequestor)webBrowser.queryInterface(nsIInterfaceRequestor.NS_IINTERFACEREQUESTOR_IID);
		nsIDocShell docShell = (nsIDocShell)req.getInterface(nsIDocShell.NS_IDOCSHELL_IID);
		nsIContentViewer contentView = docShell.getContentViewer();
		nsIMarkupDocumentViewer docView = (nsIMarkupDocumentViewer)contentView.queryInterface(nsIMarkupDocumentViewer.NS_IMARKUPDOCUMENTVIEWER_IID);
		float value = zoom / 10f;
		docView.setFullZoom(value);
	}

	/**
	 *
	 * WLMozBrowserEditor.java
	 *
	 * Listen to document events
	 *
	 * @author max <mcgoebel@gmail.com>
	 * @date Jan 29, 2011
	 */
	public class WLDOMDocumentListener extends DOMDocumentListener
	{

		@Override
		public void documentLoaded(DOMDocumentEvent event) {
			super.documentLoaded(event);
		}

		@Override
		public void documentUnloaded(DOMDocumentEvent event) {
			super.documentUnloaded(event);
		}

		@Override
		public void onEvent(IApplicationEvent event) {
			super.onEvent(event);
		}
	}

	public void registerBrowserMonitor (BrowserMonitor monitor) {
		if (!this.monitors.contains(monitor)) {
			this.monitors.add(monitor);
		}
	}

	public void unregisterBrowserMonitor (BrowserMonitor monitor) {
		if (this.monitors.contains(monitor)) {
			this.monitors.remove(monitor);
		}
	}

	public HTMLDocument getCurrentDocument () {
//		return (HTMLDocument) we.getCurrentDocument();
		return null;
	}

//	public void removeDOMNodeSelectionChangedListener (ExampleListener listener) {
//		selectionChangedListeners.remove(listener);
//	}

//	public void addDOMNodeSelectionChangedListener(ExampleListener listener) {
//		selectionChangedListeners.add(listener);
//	}

	public void removeDOMDocumentListener (IDOMDocumentListener listener) {
		if (documentListeners.contains(listener)) {
			documentListeners.remove(listener);
		}

	}

	public void addDOMDocumentListener(IDOMDocumentListener listener) {
		if (!documentListeners.contains(listener)) {
			documentListeners.add(listener);
		}
	}

	public void hookDOMEventListener(String name, nsIDOMEventListener listener, nsIDOMDocument document, boolean what) {

	}
	public void unhookDOMEventListener(String name, nsIDOMEventListener listener, nsIDOMDocument document, boolean what) {

	}

	/**
	 * This should be called on addPages method:
	 * e.g.:
	 * createPages() { addPages() { Composite bp = createBrowserPage(); addPage(bp); ...}; };
	 * @return
	 */
	public Composite createBrowserPage(Composite parent)
	{
		Composite pageArea = new Composite(parent, SWT.NONE);
		GridLayout gridLayout1 = new GridLayout();
		pageArea.setLayout(gridLayout1);

		GridData data;

		SashForm sf = new SashForm(pageArea, SWT.VERTICAL);
		GridLayout gridLayout2 = new GridLayout();
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.heightHint = 200;
		sf.setLayoutData(data);
		sf.setLayout(gridLayout2);

		Composite displayArea = new Composite(sf, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.verticalSpacing = 2;
		displayArea.setLayout(gridLayout);

		createRenderingArea(displayArea);

		return displayArea;
	}

	protected void createRenderingArea(Composite displayArea)
	{
		GridData data;

		//Navigation Bar
//		NavigationBar navBar;
//		navBar = new NavigationBar( displayArea, SWT.NONE );
//		data = new GridData(GridData.FILL_HORIZONTAL);
//		data.horizontalSpan = 3;
//		navBar.setLayoutData(data);
//
//		//Actions for the Nav Bar
//		createActions(); //add the actions to the navBar
//		navBar.setBackAction(backAction);
//		navBar.setForwardAction(forwardAction);
//		navBar.setRefreshAction(refreshAction);
//		navBar.setStopAction(stopAction);
//		navBar.setGoAction(goAction);
//		navBar.addExtensionAction( clickSelectAction );
//		navBar.addExtensionAction( clearCacheAction );

		//Browser
		this.browser = new Browser(displayArea, SWT.MOZILLA);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.browser.setLayoutData(data);
	}

	@Override
	public void documentUpdated(DocumentUpdateEvent ev)
	{
		if (ev.getDocumentUpdate()==null)
		{
			return;
		}

		String s1 = this.getClass().getCanonicalName();
		String s2 = ev.getDocumentUpdate().getProvider().getClass().getCanonicalName();

		//do whatever needs doing...
		if (this.equals(ev.getDocumentUpdateProvider()) || s1.equals(s2))
		{
			return; //consume event
		}

		DocumentUpdate update = ev.getDocumentUpdate();
		DocumentModel model = update.getUpdate();
		if (update.getProvider()!=null && update.getProvider().equals(this))
		{
			return; //consume event
		}

		if (update.getType()==UpdateType.DOCUMENT_LOAD)
		{
			if (model.getDocument() instanceof HTMLDocument)
			{
				HTMLDocument doc = (HTMLDocument) model.getDocument();
				if (doc!=null && doc.getCachedJavaDOM()!=null) {
					//try accessing the cached version
				}
				if (doc!=null && doc.getFormat()==DocumentFormat.HTML && !doc.getUri().endsWith(".wrapper"))
				{
					goToURL(doc.getUri());
				}
			}
		}
	}

	/**
	 *
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		ISelection selection = event.getSelection();
		if (selection instanceof DOMNodeSelection)
		{
			DOMNodeSelection domSelection = (DOMNodeSelection) selection;
			IDocument document = DocumentController.docModel.getDocument();

			nsIDOMNode nsIDOMNode = domSelection.getSelectedNode();
			Node node = NodeFactory.getNodeInstance(nsIDOMNode);
//			String nodeURI = node.getOwnerDocument().getBaseURI();
			
//			if (!nodeURI.equalsIgnoreCase(document.getUri()))
//			{
////				System.out.println();
////				return;
//			}
			ctrlKey = isCtrl();

//			HTMLDocument doc = null;//(HTMLDocument) we.getCurrentDocument();
//			NodeSelection nsel = new NodeSelection(node, doc.getCachedJavaDOM());
//			NodeExample example = new NodeExample(nsel, ctrlKey?BinaryClass.NEGATIVE:BinaryClass.POSITIVE);


//			final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//			IPerspectiveDescriptor activePerspective = workbenchWindow.getActivePage().getPerspective();
//			if (activePerspective.getId().equals(WrapperPerspective.ID))
//			{
				//if we are in the wrapper perspective, add example to learner
//				we.getLearningFilterControl().addExample(document, example, !isCtrlKey());
//			}
//			else
//			{
				//else, add example to annotation view
//				
//			}
		}
	}

	/**
	 *
	 * @param element
	 */
	public void blinkElement(Element element)
	{
    	nsIDOMDocument nsdoc = null;//we.getDocument();//wb.bl.findMozDoc(e.getOwnerDocument());
        if (nsdoc==null)
        {
        	//mcg: nsidomdocument conversion
        	nsdoc = (nsIDOMDocument)
        	NodeFactory.getnsIDOMNode(
        			element.getOwnerDocument()).queryInterface(
        					nsIDOMDocument.NS_IDOMDOCUMENT_IID);
        }
        nsIDOMElement moze = javaElement2mozElement(element, nsdoc);
        if (moze!=null)
        {
        	sbox2.flash(moze);
        }
	}

	/*
	 * highlight element by showing an outline around it. This method is called
	 * while clickSelect is enabled.
	 */
	protected void highlightElement2( nsIDOMElement element )
	{
		//		String test = element.getAttribute("id");
		//		if (!("rowSelector").equals(element.getAttribute("id")))

		sbox2.highlight(element, "#008000");
		//    	sbox2.flash(element);
		//        selectionBox.highlight(element);
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public boolean isAltKey() {
		return altKey;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	@Override
	public void modelChanged(ModelChangedEvent event) {
		
		goToURL("about:blank");
		System.out.println();
		
	}

}
