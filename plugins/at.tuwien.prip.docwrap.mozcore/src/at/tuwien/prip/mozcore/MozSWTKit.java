package at.tuwien.prip.mozcore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.dom.html.HTMLDocumentImpl;
import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsILocalFile;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserPersist;
import org.mozilla.interfaces.nsIWebBrowserSetup;
import org.mozilla.interfaces.nsIWebNavigation;
import org.mozilla.xpcom.Mozilla;
import org.w3c.dom.Document;

import at.tuwien.prip.mozcore.utils.KitGecko;
import at.tuwien.prip.mozcore.utils.MozCssUtils;

/**
 * MozSWTKit.java
 *
 * Call with Run->Run Configuration
 * New java app
 * under arguments, include in bottom dialog:
 * -Dorg.eclipse.swt.browser.XULRunnerPath=/opt/xulrunner/xulrunner-1.9.0.5
 *
 * @author max <mcgoebel@gmail.com>
 * @date Jan 28, 2011
 */
public class MozSWTKit {

	final Browser browser;

	protected static boolean isLoaded = false;

	private Document loadedDocument = null;

    private static boolean initialized = false;

	private Display display;
	private static Shell shell;

	private static File xulRunnerPath;


    /**
     * Set directory where xulrunner binaries are located.
     *
     * If set to null, jbrowser will use default xulrunner directory
     *
     * @param path directory with XULRunner binaries
     */
    public void setXulRunnerPath(File path) {
        xulRunnerPath = path.getAbsoluteFile();
	}

	public static Shell getShell() {
		return shell;
	}

	public static void setShell(Shell shell) {
		MozSWTKit.shell = shell;
	}

	/**
	 * Constructor.
	 *
	 * @param shell
	 * @param url
	 */
	private MozSWTKit(Shell shell, String url) {

		/**
		 * Check for standalone
		 */
		if (shell==null) {
			shell = new Shell();
			this.display = shell.getDisplay();
			shell.setSize(800, 600);
		} else {
			setShell(shell);
			this.display = shell.getDisplay();
		}
//		Mozilla.getInstance().initialize();

		browser = new Browser(shell, SWT.MOZILLA);
		browser.setVisible(false);

		//disallow javascript, as this is time consuming...
		browser.setJavascriptEnabled(false);
		disableImages(browser); //mcg: test disable images

		// Adapt browser size to shell size
		browser.setBounds(shell.getClientArea());

		// Listens for page loading status.
		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
			}

			public void completed(ProgressEvent event) {

//				String test = browser.getWebBrowser().toString();
				if (browser instanceof nsIWebBrowser) {

					nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
					if (webBrowser == null) {
						System.out.println("Could not get the nsIWebBrowser from the Browser widget");
					}
					nsIDOMWindow window = webBrowser.getContentDOMWindow();
					nsIDOMDocument doc = window.getDocument();
					setLoadedDocument(HTMLDocumentImpl.getDOMInstance(doc));
					isLoaded = true;

				}

				else {
					isLoaded = true;
				}
			}
		});

		// Load an URL into the web browser
		browser.setUrl(url);

		while (!shell.isDisposed() && !isLoaded) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

//		observerService.removeObserver(httpObserver, "http-on-modify-request");
	}

    /**
     *
     * Initialize.
     *
     * @param moz
     * @param grePath
     */
    protected static void initialize(Mozilla moz, File grePath)  {
        if (initialized) return;
        initialized = true;
    }

	/**
	 *
	 * @param url
	 */
	public void goToUrl (String url) {
		isLoaded = false;
		browser.setUrl(url);
	}

//	/**
//	 * Simple HTTP observer listen to requests and responses.
//	 *
//	 * @author alpgarcia
//	 *
//	 */
//	class SimpleHTTPObserver implements nsIObserver {
//
//		private int nRequests = 0;
//		private int nResponses = 0;
//
//		public void observe(nsISupports aSubject, String aTopic, String aData) {
//
//			// You can read corresponding javadoc for this method, here we have pasted
//			// some interesting lines from there:
//			//
//			//    Observe will be called when there is a notification for the topic.
//			//    This assumes that the object implementing this interface has been registered
//			//    with an observer service such as the nsIObserverService.
//			//    If you expect multiple topics/subjects, the impl is responsible for filtering.
//			//
//			//    You should not modify, add, remove, or enumerate notifications in the
//			//    implementation of observe.
//
//
//			// Get the channel for listen to from notification specific interface pointer.
//			nsIHttpChannel httpChannel =
//				(nsIHttpChannel) aSubject.queryInterface(nsIHttpChannel.NS_IHTTPCHANNEL_IID);
//
//			// Our observer can listen to request or responses, it depends on the
//			// notification topic or subject.
//			if (aTopic.equals("http-on-modify-request")) {
//
//				nRequests++;
//
//				System.out.println("\n---- BEGIN REQUEST NUMBER " + nRequests + " ----\n");
//
//				httpChannel.visitRequestHeaders(new nsIHttpHeaderVisitor() {
//					public void visitHeader(String header, String value) {
//						System.out.println("Header: " + header + " -- Value: "
//								+ value);
//					}
//
//					public nsISupports queryInterface(String arg0) {
//						return null;
//					}
//				});
//
//				System.out.println("  Method: " + httpChannel.getRequestMethod());
//				System.out.println("  Name: " + httpChannel.getName());
//				System.out.println("  Host: " + getRequestHeader(httpChannel, "host"));
//				System.out.println("  User Agent: " + getRequestHeader(httpChannel, "user-agent"));
//				System.out.println("  Accept: " + httpChannel.getRequestHeader("accept"));
//				System.out.println("  Accept Language: " + getRequestHeader(httpChannel, "accept-language"));
//				System.out.println("  Accept Encoding: " + getRequestHeader(httpChannel, "accept-encoding"));
//				System.out.println("  Accept Charset: " + getRequestHeader(httpChannel, "accept-charset"));
//				System.out.println("  Keep Alive: " + getRequestHeader(httpChannel, "keep-alive"));
//				System.out.println("  Connection: " + getRequestHeader(httpChannel, "connection"));
//				System.out.println("  Cookie: " + getRequestHeader(httpChannel, "cookie"));
//
//				System.out.println("\n---- END REQUEST NUMBER " + nRequests + " ----\n");
//
//			} else if (aTopic.equals("http-on-examine-response")) {
//
//				nResponses++;
//
//				System.out.println("\n---- BEGIN RESPONSE NUMBER " + nResponses + " ----\n");
//
//				httpChannel.visitResponseHeaders(new nsIHttpHeaderVisitor() {
//
//					public void visitHeader(String header, String value) {
//						System.out.println("Header: " + header + " -- Value: "
//								+ value);
//					}
//
//					public nsISupports queryInterface(String arg0) {
//						return null;
//					}
//
//				});
//
//				System.out.println("  Status: " + httpChannel.getResponseStatus());
//				System.out.println("  Status Text: " + httpChannel.getResponseStatusText());
//				System.out.println("  Content Type: " + httpChannel.getContentType());
//				System.out.println("  Content Length: " + httpChannel.getContentLength());
//				System.out.println("  Content Encoding: " + getResponseHeader(httpChannel, "content-encoding"));
//				System.out.println("  Server: " + getResponseHeader(httpChannel, "server"));
//
//				System.out.println("\n---- END RESPONSE NUMBER " + nResponses + " ----\n");
//
//			}
//
//		}
//
//		public nsISupports queryInterface(String uuid) {
//			return Mozilla.queryInterface(this, uuid);
//		}
//
//		private String getRequestHeader(nsIHttpChannel httpChannel, String header) {
//			try {
//				return httpChannel.getRequestHeader(header);
//			} catch (Exception e) {
//				return "Header Not Found";
//			}
//		}
//
//		private String getResponseHeader(nsIHttpChannel httpChannel, String header) {
//			try {
//				return httpChannel.getResponseHeader(header);
//			} catch (Exception e) {
//				return "Header Not Found";
//			}
//		}
//
//	}

	/**
	 * Load a document from Mozilla.
	 *
	 * @param url
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Document loadDOMFromMozilla (String url)
	throws FileNotFoundException
	{
		return MozSWTKit.loadDOMFromMozilla(url, null);
	}

//	/**
//	 * Load document from existing browser component.
//	 *
//	 * @param url
//	 * @param g
//	 * @return
//	 */
//	public static Document loadDOMFromMozilla (String url, IWebBrowser g) {
//		ErrorDump.info(MozSWTKit.class, "Loading Document "+url);
//		SimpleTimer timer = new SimpleTimer();
//		timer.startTask(1);
//		if (g instanceof Browser) {
//			Browser browser = (Browser) g;
//		} else {
//			MozSWTKit browser = MozSWTKit.loadDOMFromMozilla(url);
//		}
//
//		timer.stopTask(1);
//		ErrorDump.info(MozSWTKit.class,url+" loaded in "+ timer.getTimeMillis(1) +" seconds");
//		return browser.getLoadedDocument();
//	}

	/**
	 * Do the DOM loading with a given shell to avoid multiple display
	 * not implemented problem...
	 * @param url
	 * @param shell, the shell to be used for loading the Mozilla browser...
	 * @return
	 */
	public static Document loadDOMFromMozilla (String url, Shell shell)
	{
//		ClassLoader loader = null;
//		String fileName = "/home/max/dev/projects/docwrap/src/plugins/at.tuwien.prip.docwrap.mozcore/xulrunner/xulrunner";
//		File f = new File("").getAbsoluteFile();
		Mozilla.getInstance().initialize(getXulRunnerPath());

//		ErrorDump.info(MozSWTKit.class, "Loading Document "+url);
//		SimpleTimer timer = new SimpleTimer();
//		timer.startTask(1);
		MozSWTKit browser = new MozSWTKit(shell, url);
//		timer.stopTask(1);
//		ErrorDump.info(MozSWTKit.class,url+" loaded in "+ timer.getTimeMillis(1) +" seconds");
		return browser.getLoadedDocument();
	}

	/**
     * Returns directory, where Xulrunner binaries are located.
     *
     * @return directory with XULRunner binaries
     */
    public static File getXulRunnerPath()
    {
        if (xulRunnerPath == null) {
        	xulRunnerPath = new File("").getAbsoluteFile();
        }

        File xulRunner = new File(xulRunnerPath, "xulrunner");

        if (!xulRunnerPath.exists()) {
            xulRunnerPath.mkdirs();
        }

        if (!(new File(xulRunner, "javaxpcom.jar")).exists()) {
            new XulExtractor().extract(xulRunnerPath);
        }

        return xulRunner;
    }

	public void setLoadedDocument(Document loadedDocument) {
		this.loadedDocument = loadedDocument;
	}

	public Document getLoadedDocument() {
		return loadedDocument;
	}

//	public static void guiExec(final Runnable r)
//	throws KitException
//	{
//		final IRef<Throwable> guiException = new Ref<Throwable>();
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				try {
//					r.run();
//				} catch (Throwable t) {
//					guiException.set(t);
//				}
//			}
//		});
//		if (guiException.get()!=null)
//			throw new KitException(guiException.get());
//	}

//	/**
//	 *
//	 * @param <V>
//	 * @param c
//	 * @return
//	 * @throws KitException
//	 */
//	public static <V> V guiExec(final Callable<V> c)
//	throws KitException
//	{
//		final IRef<Throwable> excRef = new Ref<Throwable>();
//		final IRef<V> retRef = new Ref<V>();
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				try {
//					V ret = c.call();
//					retRef.set(ret);
//				} catch (Throwable t) {
//					excRef.set(t);
//				}
//			}
//		});
//		if (excRef.get()!=null)
//			throw new KitException(excRef.get());
//
//		return retRef.get();
//	}

	/**
	 *
	 * @param doc
	 * @return
	 */
	public static Document mozDocument2javaDocument (nsIDOMDocument doc) {
		return HTMLDocumentImpl.getDOMInstance(doc);
	}

	/**
	 *
	 * @param g
	 */
	public static void closeWindow(IWebBrowser g) {
		Shell s = ((Browser)g).getShell();
		s.close();
		MozSWTKit.runMozillaThread(100);
	}

	/**
	 * runs the SWT thread for the given number of miliseconds
	 */
	public static void runMozillaThread(int runMilis) {
		Display display = Display.getDefault();
		long stamp = System.currentTimeMillis();
		while (System.currentTimeMillis()-stamp<=runMilis)
		{
			if (!display.readAndDispatch()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//this variant hangs if no wakeup message arrives
			//if (!display.readAndDispatch ()) display.sleep();
		}

	}

	public static File saveDocument(nsIDOMDocument doc, File toDir)
	throws IOException
	{
//		if (!MozDOMHelper.isFromMozillaDOM(mozDoc)) throw new IOException("not a mozilla DOM");

//		nsIDOMDocument doc =
//		(nsIDOMDocument)
//		((NodeImpl) mozDoc).getInstance().
//		queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);

		nsIComponentManager cm = Mozilla.getInstance().getComponentManager();
		nsIWebBrowserPersist persist = (nsIWebBrowserPersist)
		cm.createInstanceByContractID("@mozilla.org/embedding/browser/nsWebBrowserPersist;1",
				null,
				nsIWebBrowserPersist.NS_IWEBBROWSERPERSIST_IID);

		//create target file (for html) and subdir (for images)
		nsILocalFile file = (nsILocalFile)
		cm.createInstanceByContractID("@mozilla.org/file/local;1",
				null,
				nsILocalFile.NS_ILOCALFILE_IID);
		file.initWithPath(toDir.getAbsolutePath());

		// count .html files
		String[] fileList = toDir.list();
		int indexNo = 1;
		for (int i=0; i<fileList.length; i++)
			if (fileList[i].endsWith(".html"))
				indexNo++;

//		int indexNo = toDir.list().length+1;
		file.append("index"+indexNo+".html");

		nsILocalFile subdir = (nsILocalFile)
		cm.createInstanceByContractID("@mozilla.org/file/local;1",
				null,
				nsILocalFile.NS_ILOCALFILE_IID);
		subdir.initWithPath(toDir.getAbsolutePath());
		subdir.append("res");

		//start saving
		long flags =
			nsIWebBrowserPersist.PERSIST_FLAGS_NO_CONVERSION |
			nsIWebBrowserPersist.PERSIST_FLAGS_REPLACE_EXISTING_FILES |
			//don't download if data is already in cache
			nsIWebBrowserPersist.PERSIST_FLAGS_FROM_CACHE;
		//nsIWebBrowserPersist::PERSIST_FLAGS_BYPASS_CACHE
		//nsIWebBrowserPersist::PERSIST_FLAGS_CLEANUP_ON_FAILURE
		persist.saveDocument(doc, file, subdir, null, flags, 0);

		//wait until the saving finishes, but
		//spin the event loop, to run the saver

		while (persist.getCurrentState()!=nsIWebBrowserPersist.PERSIST_STATE_FINISHED) {
			runMozillaThread(100);
		}

		long code = persist.getResult();
		if (code!=Mozilla.NS_OK) {
			throw new IOException(String.format("Document saving failed (%d)", code));
		}
		return new File(file.getPath());

	}

	private static boolean initializedPrompt = false;

	public static KitGecko openNewWindow() {

		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		KitGecko gecko = new KitGecko(shell, SWT.CENTER);
		gecko.setBounds(shell.getClientArea ());
		gecko.setUrl("about:blank");

		disableImages(gecko);

		GeckoListener gl = new GeckoListener(gecko);
		gecko.addOpenWindowListener(gl);
		gecko.addVisibilityWindowListener(gl);
		gecko.addCloseWindowListener(gl);

//		shell.open();  //uncomment to make the widget visible

		if (!initializedPrompt) {
			initializedPrompt = true;

			//get original Prompt service
			//nsIServiceManager sm = XPCOM.getServiceManager();
			//nsIPromptService oldPS =
			//    (nsIPromptService)
			//    sm.getServiceByContractID(
			//        "@mozilla.org/embedcomp/prompt-service;1",
			//       nsIPromptService.NS_IPROMPTSERVICE_IID);
			//register my service
//			nsIComponentRegistrar cr = Mozilla.getInstance().getComponentRegistrar();
//			nsIFactory factory = new PromptServiceFactory();
//			cr.registerFactory("{125ce301-132e-41cc-ad8c-3da4de10477b}",
//					"WebLearn Prompt Service",
//					"@mozilla.org/embedcomp/prompt-service;1",
//					factory);

			//GeckoEmbed.startupProfile(new File("/tmp"), ".wrapkit");

			/*
            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");
            if (proxyHost!=null && proxyPort!=null) {
                int port = Integer.parseInt(proxyPort);
                Kit.setProxy(proxyHost, port);
            }
			 */

		}

		return gecko;
	}

	public static void disableImages(Browser g) {
		nsIWebBrowser brow = (nsIWebBrowser) g.getWebBrowser();
		disableImages(brow);
	}

	public static void enableImages(Browser g) {
		nsIWebBrowser brow = (nsIWebBrowser) g.getWebBrowser();
		enableImages(brow);
	}

	public static void closeWindow(KitGecko g) {
		Shell s = g.getShell();
		s.close();
		MozSWTKit.runMozillaThread(100);
	}

    public static void destroy () {
        initialized = false;
    }

    public static void disableImages(nsIWebBrowser brow) {
        nsIWebBrowserSetup bset =
            (nsIWebBrowserSetup) brow.queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 0);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, 0);
    }

    public static void enableImages(nsIWebBrowser brow) {
        nsIWebBrowserSetup bset =
            (nsIWebBrowserSetup) brow.queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 1);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, 1);
    }

    public static void stopLoading(nsIWebBrowser brow) {
        nsIWebNavigation nav =
            (nsIWebNavigation)
            brow.queryInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
        nav.stop(nsIWebNavigation.STOP_ALL);
    }

	/**
	 * Driver.
	 *
	 * @param args
	 */
	public static void main(String args[])
	{
		Document doc = null;
		String url = //"http://news.ycombinator.com/item?id=1723683";
			"http://www.wired.com";
		try
		{
			doc = loadDOMFromMozilla(url);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		Map<String,String> cssProps = MozCssUtils.getCSSProperties(doc.getDocumentElement());
		for (String key : cssProps.keySet()) {
			System.out.println(key + " " + cssProps.get(key));
		}

	}

	/**
	 * A Window Listener for the browser window.
	 *
	 *
	 * @author mcg <mcgoebel@gmail.com>
	 * @date May 27, 2011
	 */
	private static class GeckoListener
	implements
	OpenWindowListener,
	CloseWindowListener,
	VisibilityWindowListener
	{

		private final KitGecko owner;

		public GeckoListener(KitGecko owner) {
			this.owner = owner;
		}

		public void show(WindowEvent event) {}
		public void hide(WindowEvent event) {}
		public void open(WindowEvent event) {
			//ErrorDump.debug(Kit.class, "open popup");
			KitGecko g = MozSWTKit.openNewWindow();
			event.browser = g;
			owner.getChildGeckos().add(g);
		}
		public void close(WindowEvent event) {
			//ErrorDump.debug(Kit.class, "close popup");
			KitGecko g = (KitGecko) event.browser;
			if (g==null) return;
			MozSWTKit.closeWindow(g);
			owner.getChildGeckos().remove(g);
		}
	}
}
