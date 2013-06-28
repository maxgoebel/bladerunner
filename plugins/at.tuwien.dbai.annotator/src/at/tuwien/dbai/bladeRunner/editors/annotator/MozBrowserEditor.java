package at.tuwien.dbai.bladeRunner.editors.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.common.WebBrowserType;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditorInput;
import org.eclipse.atf.mozilla.ide.ui.browser.toolbar.IBrowserToolbar;
import org.eclipse.atf.mozilla.ide.ui.browser.toolbar.StatusBar;
import org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserViewExtensionManager;
import org.eclipse.atf.mozilla.ide.ui.console.IJavaScriptConsole;
import org.eclipse.atf.mozilla.ide.ui.console.IJavaScriptConsoleViewAdapter;
import org.eclipse.atf.mozilla.ide.ui.console.JavaScriptConsoleListener;
import org.eclipse.atf.mozilla.ide.ui.console.JavaScriptConsolePage;
import org.eclipse.atf.mozilla.ide.ui.inspector.ATFInternalNodeFilter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IPage;
import org.mozilla.interfaces.nsICacheService;
import org.mozilla.interfaces.nsIConsoleListener;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLFrameElement;
import org.mozilla.interfaces.nsIDOMHTMLIFrameElement;
import org.mozilla.interfaces.nsIDOMKeyEvent;
import org.mozilla.interfaces.nsIDOMMutationEvent;
import org.mozilla.interfaces.nsIDOMNSHTMLElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

import at.tuwien.dbai.bladeRunner.control.IDOMDocumentListener;
import at.tuwien.dbai.bladeRunner.control.IDOMMutationListener;
import at.tuwien.dbai.bladeRunner.utils.SelectionBox2;


/**
 * MozBrowserEditor.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jun 26, 2012
 */
public class MozBrowserEditor extends EditorPart
implements IWebBrowser, ISelectionProvider, ISelectionListener
{

	public static final String ID = "org.eclipse.atf.mozilla.ide.ui.MozBrowserEditor";
	public static final WebBrowserType MOZILLA_BROWSER = new WebBrowserType("Mozilla","*");

	public static final String DEFAULT_URL = "about:blank";

	//actions
	protected Action backAction = null;
	protected Action forwardAction = null;
	protected Action refreshAction = null;
	protected Action stopAction = null;
	protected Action goAction = null;

	protected Action clearCacheAction = null;
	protected Action clickSelectAction = null;

	//Editor widgets
//	protected NavigationBar navBar = null;
	protected StatusBar statusBar = null;
//	protected MozillaBrowser browser = null;
	protected Browser browser = null;
	protected List<?> toolbars = new ArrayList();


	//filter to exclude internal ATF generated DOM nodes
	protected ATFInternalNodeFilter internalFilter = new ATFInternalNodeFilter();

	//currently loaded document
	//during a new load, this document will point to the old document until
	//the load is completed.
	protected nsIDOMDocument document = null;
	protected boolean loading = true;

	protected boolean standalone = false;

	//used for collecting network information
//	protected INetworkMonitorAdapter netMonAdapter = null;

	/**
	 * Singleton that provides the main modifier key for the platform.  For Linux
	 * and Windows, that is the 'Ctrl' key.  For Mac OS X, it is 'Cmd' (Apple) key.
	 */
	protected static class OSModifierKey {
		private static OSModifierKey instance = new OSModifierKey();
		private long osModifierKeyCode;

		private OSModifierKey() {
			if (SWT.getPlatform() == "carbon")
				osModifierKeyCode = nsIDOMKeyEvent.DOM_VK_META;
			else
				osModifierKeyCode = nsIDOMKeyEvent.DOM_VK_CONTROL;
		}

		public static OSModifierKey getInstance() {
			return instance;
		}

		public long getKeyCode() {
			return osModifierKeyCode;
		}
	}

	public MozBrowserEditor(boolean standalone) {
		this.standalone = standalone;
	}

	/*
	 * Selection Support
	 */
	protected class DOMNodeSelection implements IDOMNodeSelection
	{
		protected nsIDOMNode selectedNode = null;

		public boolean isEmpty() {
			return selectedNode == null;
		}

		public nsIDOMNode getSelectedNode() {
			return selectedNode;
		}

		public void setSelectedNode( nsIDOMNode selectedNode ){
			this.selectedNode = selectedNode;
		}
	}

	protected DOMNodeSelection selection = new DOMNodeSelection();
	protected ListenerList selectionListeners = new ListenerList();

	//used to provide keyboard navigation of the DOM
	//protected nsIDOMTreeWalker treeWalker = null;

	/*
	 * DOMDocumentContainer Support
	 */
	protected ListenerList domDocumentListeners = new ListenerList(); //notifies of document loading and loaded
	protected ListenerList domMutationListeners = new ListenerList(); //notifies changes in the document's structure

	/*
	 * This enables the use of the mouse to click on an element in the browser
	 * and set it as the Selection.
	 */
	protected boolean isCtrl = false;
	protected boolean controlSelectEnabled = true;
	protected SelectionBox2 selectionBox = null; //a new instance created per Document loaded

	/*
	 * Navigation support
	 */

	public boolean isCtrl()
	{
		return isCtrl;
	}

	protected class MozillaBrowserListener implements ProgressListener, LocationListener, StatusTextListener, nsIDOMEventListener{

		public void init(){
			getMozillaBrowser().addLocationListener( this );
			getMozillaBrowser().addStatusTextListener( this );
			getMozillaBrowser().addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					getMozillaBrowser().removeLocationListener(MozillaBrowserListener.this);
					getMozillaBrowser().removeStatusTextListener(MozillaBrowserListener.this);
					getMozillaBrowser().removeDisposeListener(this);
				}

			});
		}

		/*
		 * This event is not used because it's proven unreliable and the information provided is not enough
		 * to do anything useful.
		 */
		public void changing(LocationEvent event) {}

		/*
		 * LocationEvent:changed is the main event that notifies that a new request is
		 * being done. In this event, need to make sure that we are dealing with the
		 * top window (and document)
		 *
		 * 1. hook progress listener to keep track of the loading progress
		 * 2. hook to listen when the content document of the window is loaded.
		 *
		 */
		public void changed(LocationEvent event)
		{
			/*
			 * Need to ignore "about:blank". When opening a new browser this event is for some reason fired
			 * for about:blank before setting the correct url.
			 *
			 */
			if( "about:blank".equals(event.location) )
				return;

			if( event.top )
			{
				//hook listeners to window
//				Object tmp = browser.getWebBrowser();
//				Class<?> clss = tmp.getClass();
				nsIDOMWindow window = ((nsIWebBrowser)browser.getWebBrowser()).getContentDOMWindow();
				nsIDOMEventTarget windowEventTarget = (nsIDOMEventTarget)window.queryInterface( nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID );

				//add progress listener
				getMozillaBrowser().removeProgressListener(this);
				getMozillaBrowser().addProgressListener( this );
				//show progress in status bar
				statusBar.showProgress(0);

				//whichever happens first
				windowEventTarget.addEventListener( "pageshow", this, true );
				windowEventTarget.addEventListener( "DOMContentLoaded", this, true );

				//whichever happens first
				windowEventTarget.addEventListener( "pagehide", this, true );
				windowEventTarget.addEventListener( "unload", this, true );

				//Set the tool tip to the browser url address
				setTitleToolTip( browser.getUrl() );

				//show the current URL in the navBar
//				navBar.setLocationURL( getMozillaBrowser().getUrl() );

				//this is call once after a URL change so it is a good place for this
				stopAction.setEnabled(true);

				//save the current url in the memento object
				MozBrowserEditorInput input = (MozBrowserEditorInput)MozBrowserEditor.this.getEditorInput();
				input.setURL( event.location );

				//clear all the selection info
				changeSelection(null);
			}
		}

		public void changed(ProgressEvent event) {

			//refresh the progress bar
			if (event.total > 0){

				int ratio = event.current * 100 / event.total;

				//show progress in status bar
				statusBar.showProgress(ratio);
			}
			else{
				//@GINO: Temp Hack because total may come as -1
				statusBar.showProgress( 50 );
			}
		}

		/*
		 * This method has the potential of getting called more than once (when there are Frames). The problem is that
		 * it does not provide enough context information so the only things that are done here are things that can be
		 * performed more than once without getting undesirable behavoir.
		 *
		 * We are no using the data member of the event to send additional info
		 *
		 */
		public void completed(ProgressEvent event) {

			//the event.data should have information about whether the progress has to do with the top document
			if( event.widget instanceof Browser ){

				//remove the progress listener
				getMozillaBrowser().removeProgressListener( this );

				//change state of actions
				backAction.setEnabled( browser.isBackEnabled() );
				forwardAction.setEnabled( browser.isForwardEnabled() );
				stopAction.setEnabled(false);

				//show done in the progress bar
				statusBar.progressDone(); //done
				statusBar.setStatusText( "" );

				//this fixes a problem with giving the browser focus after entering an URL
				//in the NavBar
				setFocus();

				//enable the cache clear action
				clearCacheAction.setEnabled(true);
			}

		}
		/*
		 * Updating the status bar with new text of the activity in the browser
		 */
		public void changed( StatusTextEvent event ) {
			statusBar.setStatusText( event.text );

		}

		/*
		 * Here we handle the real Load and Unload events for the window. When the target is the top
		 * document, the handle is removed.
		 *
		 * Also, when it is the top document, the browser will adds/remove all event handlers that it
		 * attaches.
		 *
		 * It seems that it is only getting called for the top frame.
		 */
		public void handleEvent(nsIDOMEvent event) {
			String eventType = event.getType();

			if( "pageshow".equals(eventType) || "DOMContentLoaded".equals(eventType) ){
				//do page show


				nsIDOMWindow topWindow = ((nsIWebBrowser)browser.getWebBrowser()).getContentDOMWindow();


				nsIDOMEventTarget eventTarget = event.getTarget();
				nsIDOMDocument documentEventTarget = (nsIDOMDocument)eventTarget.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);

				//check if top
				boolean isTop = topWindow.getDocument().equals(documentEventTarget);
				if( isTop ){
					//done loading, document is ready for use
					loading = false;
					document = topWindow.getDocument();

					//Set title of tab
					if( document != null & document.getElementsByTagName("TITLE") != null ) {
						nsIDOMNodeList titleNodes = document.getElementsByTagName("TITLE");
						if( titleNodes.getLength() == 1 ) {
							nsIDOMNode node = titleNodes.item(0);
							if( node.getFirstChild() != null ) {
								String title = node.getFirstChild().getNodeValue();
								if( !title.equals("") ) {
									//Show first 20 characters of page title or append "..." if greater than 20
									title = title.length() > 20 ? title.substring(0,20)+"..." : title;
											setPartName(title);
								}
							}
						}
					}

					//detach listeners

					try{
						event.getCurrentTarget().removeEventListener( "pageshow", this, true );
					}
					catch( XPCOMException e ){
						e.printStackTrace();
						//ignore
					}
					try{
						event.getCurrentTarget().removeEventListener( "DOMContentLoaded", this, true );
					}
					catch( XPCOMException e ){
						//ignore
						e.printStackTrace();
					}

					//create a selection box for the new document
					selectionBox = new SelectionBox2( getDocument() );

					//hook the nsIDOMMutationEvent handlers
					hookDOMMutationEvents();

					//need to maintain the feature enabled if the document changes
					hookAllMouseEvents();
					hookKeyEvents();

				}

				//notify all listeners of document loaded
//				final DOMDocumentEvent e = new DOMDocumentEvent( documentEventTarget, isTop );
//				Object[] listeners = domDocumentListeners.getListeners();
//				for (int i = 0; i < listeners.length; ++i) {
//					final IDOMDocumentListener l = (IDOMDocumentListener) listeners[i];
//					SafeRunnable.run(new SafeRunnable() {
//						public void run() {
//							l.documentLoaded( e );
//						}
//					});
//				}

			}
			else if( "pagehide".equals(eventType) || "unload".equals(eventType) ){
				//do page hide

				//detach listeners
				nsIDOMWindow topWindow = ((nsIWebBrowser)browser.getWebBrowser()).getContentDOMWindow();


				nsIDOMEventTarget eventTarget = event.getTarget();
				nsIDOMDocument documentEventTarget = (nsIDOMDocument)eventTarget.queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);

				//check if top
				boolean isTop = topWindow.getDocument().equals(documentEventTarget);

				if( isTop ){
					//set the state to loading so that clients know to not
					//use the current document for anything but cleanup
					loading = true;
				}

				/*
				 * notify before the document is set to null
				 */
				//notify all listeners of document unloaded
//				final DOMDocumentEvent e = new DOMDocumentEvent( documentEventTarget, isTop );
//				Object[] listeners = domDocumentListeners.getListeners();
//				for (int i = 0; i < listeners.length; ++i) {
//					final IDOMDocumentListener l = (IDOMDocumentListener) listeners[i];
//					SafeRunnable.run(new SafeRunnable() {
//						public void run() {
//							l.documentUnloaded(e);
//						}
//					});
//				}

				if( isTop ){
					try{
						event.getCurrentTarget().removeEventListener( "pagehide", this, true );
					}
					catch( XPCOMException xpe ){
						//ignore
					}

					try{
						event.getCurrentTarget().removeEventListener( "unload", this, true );
					}
					catch( XPCOMException xpe ){
						//ignore
					}

					//remove all listeners
					if( document != null ){
						unhookDOMMutationEvents();
						unhookAllMouseEvents();
						unhookKeyEvents();

						document = null;
					}
				}
			}
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}
	}

	protected MozillaBrowserListener browserListener = new MozillaBrowserListener();

//	protected OpenWindowListener openManager = new PopupWindowBrowserListener( new IShellProvider(){
//
//		public Shell getShell() {
//			return MozBrowserEditor.this.getSite().getShell();
//		}
//
//	});

	/*
	 * This class is used to encapsulate the logic for handling closing this editor
	 * because it can be close by the user or as a side effect of the browser
	 * widget getting a close call.
	 */
	protected class CloseBrowserManager implements CloseWindowListener, IPartListener{

		protected boolean partClosing = false;

		public void close(WindowEvent event) {
			IWorkbenchPage page = getSite().getPage();

			// Close the editor if the browser widget was instructed to close.  However,
			// don't call closeEditor() if the browser's destruction was a result of the
			// editor already being closed.

			if ( !partClosing ) {
				page.closeEditor(MozBrowserEditor.this, false);
			}
		}

		//detecting the close by the user or the platform and setting a flag to
		//be able to ignore the notification from the browser
		public void partClosed(IWorkbenchPart part) {
			partClosing = part == MozBrowserEditor.this;
		}

		public void partActivated(IWorkbenchPart part) {}

		public void partBroughtToTop(IWorkbenchPart part) {}

		public void partDeactivated(IWorkbenchPart part) {}

		public void partOpened(IWorkbenchPart part) {}

	}

	protected CloseBrowserManager closeManager = new CloseBrowserManager();

	public Browser getMozillaBrowser() {
		return browser;
	}

	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	public void doSaveAs() {
		// do nothing
	}

	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {

		if( input instanceof MozBrowserEditorInput ){
			setSite(site);
			setInput(input);
		}
		else if( input instanceof IFileEditorInput){
			//create a file url
			try{
				setSite( site );
				setInput( new MozBrowserEditorInput((IFileEditorInput)input) );
			}
			catch( Exception e ){
				throw new PartInitException( "Error opening IFileEditorInput!" );
			}

		}
		else {
			throw new PartInitException( "Invalid input to MozBrowserEditor!" );
		}
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void createPartControl(Composite parent)
	{			
		// In some cases createPartcontrol is called be the
		// early startup code sets the XULRunner path
		//MozillaHelper.definedContributedXulRunner(null);
		Composite displayArea = new Composite( parent, SWT.NONE );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.verticalSpacing = 1;
		displayArea.setLayout(gridLayout);

		GridData data;

		if (!standalone)
		{
			//Navigation Bar
//			Composite controlArea = new Composite (parent, SWT.NONE);
//			controlArea.setLayout(new GridLayout(1, false));
			
//			navBar = new NavigationBar( displayArea, SWT.NONE );
//			data = new GridData(GridData.FILL_HORIZONTAL);
//			data.horizontalSpan = 1;
//			data.heightHint = 40;
//			controlArea.setLayoutData(data);
//			navBar.setLayoutData(data);
//			Label test = new Label(controlArea, SWT.NONE);
//			test.setText("test");
			
			//separator
			Label upperBarSeparator = new Label( displayArea, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 1;
			upperBarSeparator.setLayoutData(data);

			//Actions for the Nav Bar
			createActions(); //add the actions to the navBar
//			navBar.setBackAction(backAction);
//			navBar.setForwardAction(forwardAction);
//			navBar.setRefreshAction(refreshAction);
//			navBar.setStopAction(stopAction);
//			navBar.setGoAction(goAction);
			//		navBar.addMenuExtensionAction( clickSelectAction );
//			navBar.addMenuExtensionAction( clearCacheAction );
		}
		//Browser
		this.browser = new Browser(displayArea, SWT.MOZILLA);

		//setting up the network observer (needs to be setup and connected even
		//if the view is not active so that all net calls are registered).
//		netMonAdapter = new MozNetworkMonitorAdapter( this );
//		netMonAdapter.connect();

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.browser.setLayoutData(data);

//		toolbars = ToolbarExtensionManager.create(navBar, displayArea, this );

		//separator
		Label lowerBarSeparator = new Label( displayArea, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		lowerBarSeparator.setLayoutData(data);

		//Status Bar
		statusBar = new StatusBar( displayArea, SWT.NONE );
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		statusBar.setLayoutData(data);

		/*
		 * This object is an instance of an inner class that takes care of adapting events from the
		 * browser that deal with Loading and Progress
		 */
		browserListener.init();

//		browser.addOpenWindowListener( openManager );

		/*
		 * There are several ways to initiate the close of this editor so the
		 * closeManager handles it.
		 */
		browser.addCloseWindowListener( closeManager );
		getSite().getPage().addPartListener( closeManager );

		//needed to disconnect the listener
		/*
		 * Has to be done as a direct listener to the widget rather than in the
		 * local dispose method because by the time the local dispose method is
		 * called, it is too late.
		 */
		browser.addDisposeListener( new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
//				netMonAdapter.disconnect();
//				browser.removeOpenWindowListener(openManager);
				browser.removeCloseWindowListener( closeManager );
				getSite().getPage().removePartListener( closeManager );
				browser.removeDisposeListener(this);
			}

		});

		String url = ((MozBrowserEditorInput)getEditorInput()).getURL();
		if (url!=null) //&& url.startsWith("http://"
		{
			goToURL( url );
		}

		//navBar.setStopButtonEnabled( true );
		stopAction.setEnabled(true);

		//set as selection provider and listener
		getSite().setSelectionProvider(this);
		getSite().getPage().addSelectionListener( this );

	}

	/*
	 * This method creates all the actions that are added to the NavigationBar. These
	 * actions control the navigation aspects of the Browser, plus others.
	 */
	protected void createActions(){

		backAction = new Action( null, Action.AS_PUSH_BUTTON){
			public void run(){
				browser.back();
			}
		};

		backAction.setImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/e_back.gif") );
		backAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_back.gif") );
		backAction.setEnabled( false );

		forwardAction = new Action( null, Action.AS_PUSH_BUTTON){
			public void run(){
				browser.forward();
			}
		};

		forwardAction.setImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/e_forward.gif") );
		forwardAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_forward.gif") );
		forwardAction.setEnabled( false );

		refreshAction = new Action( null, Action.AS_PUSH_BUTTON){
			public void run(){
				browser.refresh();
			}
		};

		refreshAction.setImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/e_refresh.gif") );
		refreshAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_refresh.gif") );

		stopAction = new Action( null, Action.AS_PUSH_BUTTON){
			public void run(){
				browser.stop();
			}
		};

		stopAction.setImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/e_stop.gif") );
		stopAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_stop.gif") );
		stopAction.setEnabled( false );

		goAction = new Action( null, Action.AS_PUSH_BUTTON){
			public void run(){
//				goToURL(navBar.getLocationURL());
			}
		};

		goAction.setImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/e_go.gif") );
		goAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_go.gif") );


		//click select action - This action will go in the extension side of the Navigation Bar
		clickSelectAction = new Action(null, Action.AS_CHECK_BOX){
			public void run(){
				controlSelectEnabled = false;
				if (selectionBox!=null)
				{
					selectionBox.hide();
				}
			}
		};
		clickSelectAction.setText("Select");
		clickSelectAction.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor("icons/inspector/revealBySelection.gif"));
		String modifierKeyText;
		if (OSModifierKey.getInstance().getKeyCode() == nsIDOMKeyEvent.DOM_VK_CONTROL) {
			modifierKeyText = "CTRL";
		} else {
			modifierKeyText = "CMD";
		}
		clickSelectAction.setToolTipText("Enable/Disable " + modifierKeyText + "+Click element selection in the browser.");
		clickSelectAction.setChecked(false);

		//clear cache action - This action wipes clean the browser cache (both RAM and Memory) bug 140877
		clearCacheAction = new Action(null, Action.AS_PUSH_BUTTON){
			public void run(){
				clearCache();
				this.setEnabled(false);
			}
		};

		clearCacheAction.setText( "Clear Cache" );
		clearCacheAction.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor("icons/browser/e_clearCache.gif"));
		clearCacheAction.setDisabledImageDescriptor( MozIDEUIPlugin.getImageDescriptor("icons/browser/d_clearCache.gif") );
		clearCacheAction.setToolTipText("Clear the Browser's cache.");
		clearCacheAction.setEnabled(true);

	}

	/*
	 * This method is used to programatically change the URL pointed to by the
	 * embedded browser. It defaults to "about:blank" in the case of an error.
	 *
	 * @TODO: Handle errors by instead displaying, for example, a 404 message
	 */
	public void goToURL(String url) {

		try{
			browser.setUrl( url );
		}
		catch( XPCOMException xpcome ){
			//might be a bad URL so try opening with "about:blank"
			browser.setUrl( DEFAULT_URL );
		}

	}

	nsICacheService cacheService = null;
	public void clearCache(){

		if( cacheService == null )
			cacheService = (nsICacheService)Mozilla.getInstance().getServiceManager().getServiceByContractID( "@mozilla.org/network/cache-service;1", nsICacheService.NS_ICACHESERVICE_IID );

		/*
		 * for now since the NSI interface for nsICache in Java does not provide access to
		 * the nsICache.STORE_ON_DISK and nsICache.STORE_IN_MEMORY, need to pass the actual
		 * values. (Got values from LXR)
		 *
		 * const nsCacheStoragePolicy STORE_IN_MEMORY       = 1;
		 * const nsCacheStoragePolicy STORE_ON_DISK         = 2;
		 */
		cacheService.evictEntries( 1 );
		cacheService.evictEntries( 2 );

	}

	public void setFocus() {
		if( !browser.isDisposed() )
			browser.setFocus(); //important to be able to interact with browser
	}

	/*
	 * Some of the adapters are cached to reuse the instance.
	 *
	 * Most notably the Browser views adapters
	 */
	protected HashMap adapterCache = new HashMap();

	public Object getAdapter( Class adapterType ) {
		Object adapterObj = null;

		if( adapterCache.containsKey(adapterType) ){
			adapterObj = adapterCache.get( adapterType );

			if( adapterObj instanceof IPage ){
				IPage pageAdapter = (IPage)adapterObj;
				/*
				 * For adapters that are Pages in a PageBookView, need to check if
				 * the view has been closed and if that case, clear it from the
				 * cache and force the creation of a new instance.
				 */
				if( pageAdapter.getControl() != null && pageAdapter.getControl().isDisposed() ){
					adapterCache.remove( adapterType );
					adapterObj = null;
				}
			}
		}

		if( adapterObj == null )
		{
			adapterObj = BrowserViewExtensionManager.getInstance().getAdapter( this, adapterType);
			if( adapterObj != null ) {
				adapterCache.put( adapterType, adapterObj );

			}
			else if (adapterType == IJavaScriptConsoleViewAdapter.class) {
				adapterObj =  new IJavaScriptConsoleViewAdapter() {

					public IJavaScriptConsole getJavaScriptConsole() {
						return createJavaScriptConsole();
					}

				};

				//adapterCache.put( adapterType, adapterObj );

			}
			//support to get the underlying nsIWebBrowser object
			else if( adapterType == nsIWebBrowser.class ){
				return browser.getWebBrowser();
			}
			else if( adapterType == nsIDOMWindow.class ){
				return ((nsIWebBrowser)browser.getWebBrowser()).getContentDOMWindow();
			}
			else if (adapterType == Browser.class) {
				return browser;
			}
			//support to get selection
			else if( adapterType == IDOMNodeSelection.class ){
				adapterObj = this.selection;
			}

			//support for network monitoring
//			else if( adapterType == INetworkMonitorAdapter.class ){
//				adapterObj = netMonAdapter;
//			}

			else{
				adapterObj = super.getAdapter( adapterType );
			}

		}

		return adapterObj;
	}


	/*
	 * Currently not called because Console is a SINGLETON for all browser windows
	 */
	protected IJavaScriptConsole createJavaScriptConsole(){
		final IJavaScriptConsole jsConsole = new JavaScriptConsolePage();

		nsIConsoleListener consoleListener = new JavaScriptConsoleListener( jsConsole );

		try{
			//registering the listener
			nsIConsoleService consoleService = (nsIConsoleService)Mozilla.getInstance().getServiceManager().getServiceByContractID( "@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID );
			consoleService.registerListener( consoleListener );

			//populate cons0le with initial items
			nsIConsoleMessage [][] messageArray = new nsIConsoleMessage[1][];
			consoleService.getMessageArray( messageArray, null );

			for( int i=0; i<messageArray[0].length; i++ )
				jsConsole.logConsoleMessage( messageArray[0][i] );

		}
		catch( Exception e ){
			e.printStackTrace();
		}

		//dom listener for the FIREBUG event (this is supported by DOJO 0.3)
		/*
		 * In DOJO there is a JS that enables support for FIREBUG ( dojo.require("dojo.debug.Firebug") ).
		 * This changes the implementation for println in DOJO and creates an event of type
		 * "printfire"

final nsIDOMEventListener atfConsoleLogListener = new nsIDOMEventListener(){

public void handleEvent(nsIDOMEvent event) {

try{
//System.out.println( "print firebug!" );
event.stopPropagation(); //nobody else gets to see the event
event.preventDefault(); //cancels the event (no default action is taken)

nsIDOMMutationEvent mutEvent = (nsIDOMMutationEvent)event.queryInterface(nsIDOMMutationEvent.NS_IDOMMUTATIONEVENT_IID);
nsIDOMNode node = mutEvent.getRelatedNode();

//System.out.println( node.getFirstChild().getNodeValue() );

jsConsole.logConsoleMessage(node.getFirstChild().getNodeValue());

}
catch( Exception e ){
//do nothing if QI fails
e.printStackTrace();
}
}

public nsISupports queryInterface(String id) {
return Mozilla.queryInterface( this, id );
}

};

addDOMDocumentListener( new IDOMDocumentListener(){

public void newDocumentLoaded() {
//nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)browser.getWebBrowser().getContentDOMWindow().queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
docEventTarget.addEventListener("ATFConsoleLog", atfConsoleLogListener, false);
}

public void newDocumentLoading(int current, int total) {
if( document != null ){
//nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)browser.getWebBrowser().getContentDOMWindow().queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
docEventTarget.removeEventListener("ATFConsoleLog", atfConsoleLogListener, false);
}
}

});

if( !loading && document != null ){
//nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)browser.getWebBrowser().getContentDOMWindow().queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
docEventTarget.addEventListener("ATFConsoleLog", atfConsoleLogListener, false);
}
		 */

		return jsConsole;
	}

	public void dispose()
	{
//		navBar.removeExtensionAction( clickSelectAction );

		if( document != null ){
			unhookAllMouseEvents();
			unhookKeyEvents();
			unhookDOMMutationEvents();
		}

//		netMonAdapter = null;

		//remove all documentContainer listeners
		domDocumentListeners.clear();
		domMutationListeners.clear();

		changeSelection(null);
		selectionBox = null;

		//REMOVE AS SELECTION LISTENER
		getSite().setSelectionProvider(null);
		getSite().getPage().removeSelectionListener( this );

		super.dispose();
	}

	/**
	 * highlight element by showing an outline around it. This method is called
	 * if clickSelect is enabled.
	 * @param element
	 */
	protected void highlightElement( nsIDOMElement element )
	{
		selectionBox.highlight(element);//showDiv();//flash(element);

		final nsIDOMNode node = element;
		fireHoverSelection(new IDOMNodeSelection()
		{
			public boolean isEmpty() {
				return false;
			}

			public nsIDOMNode getSelectedNode() {
				return node;
			}
		});
	}

	/**
	 *
	 * @param hovered
	 */
	protected void fireHoverSelection(IDOMNodeSelection hovered)
	{
		for( int i = 0; i < toolbars.size(); i++ )
		{
			((IBrowserToolbar)toolbars.get(i)).setHoverSelection(hovered);
		}
	}

	/*
	 * Reveals the selection in the browser by flashing a box around the
	 * rendered representation in the browser.
	 */
	protected void revealSelection(){
		nsIDOMNode selectedNode = selection.getSelectedNode();
		if( selectedNode.getNodeType() == nsIDOMNode.ELEMENT_NODE ){

			try {
				//scroll the element into view
				nsIDOMNSHTMLElement nsElement = (nsIDOMNSHTMLElement) selectedNode
				.queryInterface(nsIDOMNSHTMLElement.NS_IDOMNSHTMLELEMENT_IID);
				nsElement.scrollIntoView(false);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			selectionBox.flash( (nsIDOMElement)selectedNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID) );
		}
	}

	/*
	 * Sets the input nsIDOMNode as the current selection. Check if the selectedNode
	 * is different from the current selection and only sends out an event if so.
	 *
	 * Also supports clearing the selection by passing null
	 */
	protected void changeSelection( final nsIDOMNode selectedNode ){

		if( selectedNode == null ){

			//clear the selection only if it is not empty already
			if( !this.selection.isEmpty() ){
				this.selection.setSelectedNode(null); //clearing selection
				fireSelectionChanged( new SelectionChangedEvent(this, this.selection) );

			}
		}
		else{
			//if the current selection is empty, set and fire
			if( this.selection.isEmpty() ){
				this.selection.setSelectedNode(selectedNode);

				//notify listeners
				fireSelectionChanged( new SelectionChangedEvent(this,this.selection));
			}
			else{
				nsIDOMNode currentSelection = this.selection.getSelectedNode();

				if( !currentSelection.equals(selectedNode) ){
					this.selection.setSelectedNode(selectedNode);

					fireSelectionChanged( new SelectionChangedEvent(this,this.selection));
				}
			}

			//always reveal the selection locally even if it is the same node
//			revealSelection();
		}
	}

	/*
	 * Selection changed coming from outside
	 */
	public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
		//ignore when the selection originated locally
		if( part == this || selection == null )
			return;

		//if this editor is not active then the selection can be ignored because the DOMInspector
		//will not work without a visible/active EditorPart
//		if( this != this.getSite().getPage().getActiveEditor() )
//			return;

		//only process this type of selection
		if( selection instanceof IDOMNodeSelection ){

			if( selection.isEmpty() ){
				changeSelection(null);
			}
			else{
				nsIDOMNode selectedNode = ((IDOMNodeSelection)selection).getSelectedNode();

				if( selectedNode == null )
					changeSelection(null);
				else
					changeSelection( selectedNode );
			}

		}

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);

	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	public ISelection getSelection() {
		return null;
	}

	/*
	 * This is the external way of changing the current selection. For example,
	 * the corresponding IDOMInspector will call this method to control the
	 * selection of the browser.
	 */
	public void setSelection(ISelection selection) {
		//TODO: check that the selection can is part of the document rendered by
		//the browser
		if( selection == null || selection.isEmpty() ){
			changeSelection(null); //clears the selection
		}
		else{
			if( selection instanceof IDOMNodeSelection ){
				//only concerned with one selection at a time
				nsIDOMNode externalSelection = ((IDOMNodeSelection)selection).getSelectedNode();
				changeSelection( externalSelection );
			}
		}
	}

	/*
	 * notify changes to selection to all listeners
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event)
	{
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
		for( int i = 0; i < toolbars.size(); i++ ) {
			((IBrowserToolbar)toolbars.get(i)).setNodeSelection((IDOMNodeSelection)event.getSelection());
		}
	}

	/*
	 * IDOMDocumentContainer Interface
	 */
	public nsIDOMDocument getDocument() {
		return document;
	}

	public boolean isDocumentLoading(){
		return loading;
	}

	public void addDOMDocumentListener(IDOMDocumentListener listener) {
		domDocumentListeners.add(listener);
	}

	public void removeDOMDocumentListener(IDOMDocumentListener listener){
		domDocumentListeners.remove(listener);
	}

	public void addDOMMutationListener(IDOMMutationListener listener) {
		domMutationListeners.add(listener);
	}

	public void removeDOMMutationListener(IDOMMutationListener listener){
		domMutationListeners.remove(listener);
	}

	/*
	 * This is a helper method used to hook the DOMEventListener to the DOMDocument. In the
	 * case of Frames and IFrames, the boolean hookToSubDocs controls the recursive calls to
	 * any hook the listener to the sub documents.
	 */
	private void hookDOMEventListener( String eventType, nsIDOMEventListener listener, nsIDOMDocument document, boolean hookSubDocs ){

		nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		docEventTarget.addEventListener( eventType, listener, true );

		if( !hookSubDocs )
			return;

		//going to have to search for FRAME and IFRAME in order to find subdocuments
		nsIDOMNodeList frames = document.getElementsByTagName( "FRAME" );
		for( int i=0; i < frames.getLength(); i++ ){
			nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement)frames.item(i).queryInterface(nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID );
			if( frame.getContentDocument() != null )
				hookDOMEventListener( eventType, listener, frame.getContentDocument(), hookSubDocs );
		}

		nsIDOMNodeList iframes = document.getElementsByTagName( "IFRAME" );
		for( int i=0; i < iframes.getLength(); i++ ){
			nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement)iframes.item(i).queryInterface(nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID );
			if( iframe.getContentDocument() != null )
				hookDOMEventListener( eventType, listener, iframe.getContentDocument(), hookSubDocs );
		}
	}

	/*
	 * Same as above but for removing the listeners.
	 */
	private void unhookDOMEventListener( String eventType, nsIDOMEventListener listener, nsIDOMDocument document, boolean unhookSubDocs )
	{
		nsIDOMEventTarget docEventTarget = (nsIDOMEventTarget)document.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		docEventTarget.removeEventListener( eventType, listener, true );

		if( !unhookSubDocs )
			return;

		//going to have to search for FRAME and IFRAME in order to find subdocuments
		nsIDOMNodeList frames = document.getElementsByTagName( "FRAME" );
		for( int i=0; i < frames.getLength(); i++ ){
			nsIDOMHTMLFrameElement frame = (nsIDOMHTMLFrameElement)frames.item(i).queryInterface(nsIDOMHTMLFrameElement.NS_IDOMHTMLFRAMEELEMENT_IID );
			if( frame.getContentDocument() != null )
				unhookDOMEventListener( eventType, listener, frame.getContentDocument(), unhookSubDocs );
		}

		nsIDOMNodeList iframes = document.getElementsByTagName( "IFRAME" );
		for( int i=0; i < iframes.getLength(); i++ ){
			nsIDOMHTMLIFrameElement iframe = (nsIDOMHTMLIFrameElement)iframes.item(i).queryInterface(nsIDOMHTMLIFrameElement.NS_IDOMHTMLIFRAMEELEMENT_IID );
			if( iframe.getContentDocument() != null )
				unhookDOMEventListener( eventType, listener, iframe.getContentDocument(), unhookSubDocs );
		}
	}

	/*
	 * The following section of code is used to block Mouse events into the Browser in
	 * order to detect the receiver of the MOUSE_DOWN and reveal on the DOM Inspector.
	 */

	//This method attaches listener that stop the propagation of the mouse related event
	protected void hookAllMouseEvents(){

		hookDOMEventListener("mousedown", revealSelectionListener, document, true );
		hookDOMEventListener("mouseover", highlightElementListener, document, true );
		hookDOMEventListener("mousemove", highlightElementListener, document, true );
		hookDOMEventListener("mouseup", cancelPropagationListener, document, true );
		hookDOMEventListener("click", cancelPropagationListener, document, true );
		hookDOMEventListener("mouseout", highlightElementListener, document, true );
	}

	//This method attaches listener that stop the propagation of the mouse related event
	protected void unhookAllMouseEvents(){

		unhookDOMEventListener("mousedown", revealSelectionListener, document, true );
		unhookDOMEventListener("mouseover", highlightElementListener, document, true );
		unhookDOMEventListener("mousemove", highlightElementListener, document, true );
		unhookDOMEventListener("mouseup", cancelPropagationListener, document, true );
		unhookDOMEventListener("click", cancelPropagationListener, document, true );
		unhookDOMEventListener("mouseout", highlightElementListener, document, true );
	}

	protected nsIDOMEventListener cancelPropagationListener = new nsIDOMEventListener()
	{
		public void handleEvent(nsIDOMEvent event)
		{
			try
			{
				if( ( clickSelectAction.isChecked() && controlSelectEnabled ) ){
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)
				}

			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

	};

	protected nsIDOMEventListener highlightElementListener = new nsIDOMEventListener(){

		public void handleEvent(nsIDOMEvent event)
		{
			try
			{
				controlSelectEnabled = true;
				if ( clickSelectAction.isChecked() && controlSelectEnabled )
				{
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)

					if( ("mouseover".equals(event.getType()) || "mousemove".equals(event.getType())) )
					{
						nsIDOMNode target = (nsIDOMNode)event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

						if( target.getNodeType() == nsIDOMNode.ELEMENT_NODE )
						{
							nsIDOMElement element = (nsIDOMElement)target.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

							//filter out the flashing divs
							if( !MozIDEUIPlugin.ATF_INTERNAL.equals(element.getAttribute("class")) ){
								highlightElement( element );
							}
						}
					}
					else if( "mouseout".equals(event.getType()) )
					{
						selectionBox.hide();
					}
				}
			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

	};

	protected nsIDOMEventListener revealSelectionListener = new nsIDOMEventListener(){

		public void handleEvent(nsIDOMEvent event)
		{
			try
			{
				if( clickSelectAction.isChecked() && controlSelectEnabled ){
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)

					final nsIDOMNode target = (nsIDOMNode)event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

					if( target.getNodeType() == nsIDOMNode.ELEMENT_NODE ){

						nsIDOMElement element = (nsIDOMElement)target.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

						//filter out the flashing divs
						if( !MozIDEUIPlugin.ATF_INTERNAL.equals(element.getAttribute("class")) ){
							//setting the selection
							changeSelection( target );
						}
					}
				}
			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}

		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

	};

	protected void hookKeyEvents(){
		//hookDOMEventListener( "keypress" , keyEventListener, document, false );
		hookDOMEventListener("keydown", keyDownListener, document, false);
		hookDOMEventListener("keyup", keyUpListener, document, false);
	}

	protected void unhookKeyEvents(){
		//unhookDOMEventListener( "keypress" , keyEventListener, document, false );
		unhookDOMEventListener("keydown", keyDownListener, document, false);
		unhookDOMEventListener("keyup", keyUpListener, document, false);
	}

	protected nsIDOMEventListener keyDownListener = new nsIDOMEventListener(){

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

		public void handleEvent(nsIDOMEvent event) {
			try{
				nsIDOMKeyEvent keyEvent = (nsIDOMKeyEvent)event.queryInterface( nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID );
				if( isDocumentLoading() )
					return;
				if( clickSelectAction.isChecked() && keyEvent.getKeyCode() == OSModifierKey.getInstance().getKeyCode() ) {
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)
					controlSelectEnabled = true;
					isCtrl = true;
				}

			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

	};

	protected nsIDOMEventListener keyUpListener = new nsIDOMEventListener(){

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

		public void handleEvent(nsIDOMEvent event) {
			isCtrl = false;
			try{
				nsIDOMKeyEvent keyEvent = (nsIDOMKeyEvent)event.queryInterface( nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID );
				if( isDocumentLoading() )
					return;
				if( clickSelectAction.isChecked() && keyEvent.getKeyCode() == OSModifierKey.getInstance().getKeyCode() ) {
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)
					controlSelectEnabled = false;
					selectionBox.hide();
				}
			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

	};

	/*
	 * The current implementation does not support the
	 */
	/*
protected nsIDOMEventListener keyEventListener = new nsIDOMEventListener(){

public void handleEvent(nsIDOMEvent event) {

try{
event.stopPropagation(); //nobody else gets to see the event
event.preventDefault(); //cancels the event (no default action is taken)

nsIDOMKeyEvent keyEvent = (nsIDOMKeyEvent)event.queryInterface( nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID );

if( isDocumentLoading() )
return;

//From here down, assume that there is a current highlightedElement
//just highlight Element types, ignore and skip others
switch( (int)keyEvent.getKeyCode() ){

case 13: //ENTER
changeSelection( treeWalker.getCurrentNode() );
break;

case 27: //ESC
clickSelectAction.setChecked(false);
clickSelectAction.run();
break;

case 38: //UP
treeWalker.parentNode();
highlightElement( (nsIDOMElement)treeWalker.getCurrentNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID ) );
break;

case 40: //DOWN
treeWalker.firstChild();
highlightElement( (nsIDOMElement)treeWalker.getCurrentNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID ) );
break;

case 37: //LEFT
treeWalker.previousSibling();
highlightElement( (nsIDOMElement)treeWalker.getCurrentNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID ) );
break;

case 39: //RIGHT
treeWalker.nextSibling();
highlightElement( (nsIDOMElement)treeWalker.getCurrentNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID ) );
break;

}
}
catch( Exception e ){
//do nothing if QI fails
15printStackTrace();
}

}

public nsISupports queryInterface(String id) {
return Mozilla.queryInterface( this, id );
}

};
	 */

	/*
	 * The following set of listeners are used to detect changes on the DOM structure and
	 * relay it to listeners. This hides the details of the DOM Event from the interested
	 * parties under a simpler interface.
	 */
	protected void hookDOMMutationEvents(){

//		hookDOMEventListener( "DOMNodeInserted" , insertNodeListener, document, true );
//		hookDOMEventListener( "DOMNodeRemoved" , removeNodeListener, document, true );
//		hookDOMEventListener( "DOMAttrModified" , modifyAttrListener, document, true );

	}

	protected void unhookDOMMutationEvents(){

//		unhookDOMEventListener( "DOMNodeInserted" , insertNodeListener, document, true );
//		unhookDOMEventListener( "DOMNodeRemoved" , removeNodeListener, document, true );
//		unhookDOMEventListener( "DOMAttrModified" , modifyAttrListener, document, true );

	}

	protected nsIDOMEventListener insertNodeListener = new nsIDOMEventListener(){

		public void handleEvent(nsIDOMEvent event)
		{
			try
			{
				nsIDOMMutationEvent mutEvent = (nsIDOMMutationEvent)event.queryInterface(nsIDOMMutationEvent.NS_IDOMMUTATIONEVENT_IID);

				//ignore and cancel events related to internal
				if( internalFilter.isATFInternal(mutEvent.getTarget()) ){
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)
				}
				else{
//					fireNodeInserted( mutEvent );
				}
			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

	};

	protected nsIDOMEventListener removeNodeListener = new nsIDOMEventListener(){

		public void handleEvent(nsIDOMEvent event)
		{
			try
			{
				nsIDOMMutationEvent mutEvent = (nsIDOMMutationEvent)event.queryInterface(nsIDOMMutationEvent.NS_IDOMMUTATIONEVENT_IID);

				//ignore and cancel events related to internal
				if( internalFilter.isATFInternal(mutEvent.getTarget()) ){
					event.stopPropagation(); //nobody else gets to see the event
					event.preventDefault(); //cancels the event (no default action is taken)
				}
				else{
//					fireNodeRemoved( mutEvent );
				}
			}
			catch( Exception e ){
				//do nothing if QI fails
				e.printStackTrace();
			}
		}

		public nsISupports queryInterface(String id) {
			return Mozilla.queryInterface( this, id );
		}

	};

//	protected nsIDOMEventListener modifyAttrListener = new nsIDOMEventListener(){
//
//		public void handleEvent(nsIDOMEvent event) {
//
//			try{
//				nsIDOMMutationEvent mutEvent = (nsIDOMMutationEvent)event.queryInterface(nsIDOMMutationEvent.NS_IDOMMUTATIONEVENT_IID);
//
//				//ignore and cancel events related to internal
//				if( internalFilter.isATFInternal(mutEvent.getTarget()) ){
//					event.stopPropagation(); //nobody else gets to see the event
//					event.preventDefault(); //cancels the event (no default action is taken)
//				}
//				else{
//					if( mutEvent.getAttrChange() == nsIDOMMutationEvent.ADDITION ){
//						fireAttributeAdded( mutEvent );
//					}
//					else if( mutEvent.getAttrChange() == nsIDOMMutationEvent.REMOVAL ){
//						fireAttributeRemoved( mutEvent );
//					}
//					else if( mutEvent.getAttrChange() == nsIDOMMutationEvent.MODIFICATION ){
//						fireAttributeModified( mutEvent );
//					}
//				}
//
//			}
//			catch( Exception e ){
//				//do nothing if QI fails
//				e.printStackTrace();
//			}
//		}
//
//		public nsISupports queryInterface(String id) {
//			return Mozilla.queryInterface( this, id );
//		}
//
//	};

//	protected void fireNodeInserted( final nsIDOMMutationEvent event) {
//		Object[] listeners = domMutationListeners.getListeners();
//		for (int i = 0; i < listeners.length; ++i) {
//			final IDOMMutationListener l = (IDOMMutationListener) listeners[i];
//			SafeRunnable.run(new SafeRunnable() {
//				public void run() {
//					l.nodeInserter(event.getRelatedNode(), (nsIDOMNode)event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID));
//				}
//			});
//		}
//	}
//
//	protected void fireNodeRemoved(final nsIDOMMutationEvent event) {
//		Object[] listeners = domMutationListeners.getListeners();
//		for (int i = 0; i < listeners.length; ++i) {
//			final IDOMMutationListener l = (IDOMMutationListener) listeners[i];
//			SafeRunnable.run(new SafeRunnable() {
//				public void run() {
//					l.nodeRemoved(event.getRelatedNode(), (nsIDOMNode)event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID));
//				}
//			});
//		}
//	}

	protected void fireAttributeAdded(final nsIDOMMutationEvent event) {
		Object[] listeners = domMutationListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final IDOMMutationListener l = (IDOMMutationListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
//					l.attributeAdded((nsIDOMElement)event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID), event.getAttrName());
				}
			});
		}
	}

	protected void fireAttributeRemoved(final nsIDOMMutationEvent event) {
//		Object[] listeners = domMutationListeners.getListeners();
//		for (int i = 0; i < listeners.length; ++i) {
//			final IDOMMutationListener l = (IDOMMutationListener) listeners[i];
//			SafeRunnable.run(new SafeRunnable() {
//				public void run() {
//					l.attributeRemoved((nsIDOMElement)event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID), event.getAttrName());
//				}
//			});
//		}
	}

	protected void fireAttributeModified(final nsIDOMMutationEvent event) {
//		Object[] listeners = domMutationListeners.getListeners();
//		for (int i = 0; i < listeners.length; ++i) {
//			final IDOMMutationListener l = (IDOMMutationListener) listeners[i];
//			SafeRunnable.run(new SafeRunnable() {
//				public void run() {
////					l.attributeModified((nsIDOMElement)event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID), event.getAttrName(), event.getNewValue(), event.getPrevValue() );
//				}
//			});
//		}
	}

	public void setSelection(IDOMNodeSelection selection) {
		if( !selection.isEmpty() ) {
			changeSelection( selection.getSelectedNode() );
		}
	}

	public WebBrowserType getType() {
		return MOZILLA_BROWSER;
	}

	/*
	 *  <svg:svg width="600px" height="400px">
	 *      <svg:polygon id="triangle" points="50,50 50,300 300,300"
	 *          style=" fill:blue; stroke:black;"/>
	 *  </svg:svg>
	 */
//	  private void TEST_SVG_HIGHLIGHTER(){
//	
//	      nsIDOMDocument document = (nsIDOMDocument)getMozillaBrowser().getDocument().queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);
//	
//	      nsIDOMElement svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg" );
//	      svgElement.setAttribute("width", "600px" );
//	      svgElement.setAttribute("height", "400px" );
//	
//	      nsIDOMElement polylineElement = document.createElementNS( "http://www.w3.org/2000/svg", "polyline" );
//	      polylineElement.setAttribute("points", "50, 50 50, 300 300, 300" );
//	      polylineElement.setAttribute( "style", "fill: blue; stroke: black;" );
//	      svgElement.appendChild( polylineElement );
//	
//	      nsIDOMNode bodyElement = document.getElementsByTagName( "body" ).item(0);
//	      bodyElement.appendChild(svgElement);
//	  }
}