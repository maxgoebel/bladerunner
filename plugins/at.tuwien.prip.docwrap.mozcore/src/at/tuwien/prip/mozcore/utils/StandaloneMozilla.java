package at.tuwien.prip.mozcore.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.dom.DocumentImpl;
import org.mozilla.dom.html.HTMLDocumentImpl;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserSetup;
import org.w3c.dom.Document;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.SimpleTimer;

/**
 * 
 * StandaloneMozilla.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Sep 11, 2012
 */
public class StandaloneMozilla 
{
	Document document = null;

	public Display display;

	private Browser browser;

	private static final String SCRIPT = "" +
	" walk(document.body);" +
	"function walk(root)" +
	"{  " +
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


	private boolean isRunning = true;

	/**
	 * 
	 * @param display
	 * @param url
	 */
	public StandaloneMozilla(Display display, String url)
	{
		this.display = display;

		final SimpleTimer timer = new SimpleTimer();
		isRunning = true;

		String xulPath = "/home/max/dev/projects/docwrap/dev/plugins/at.tuwien.jBrowser/xulrunner/xulrunner";
		System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulPath);

		if (this.display==null && display==null)
		{
			this.display = new Display();
		}

		Shell shell = this.display.getActiveShell();
		if (shell==null)
		{
			shell = new Shell(this.display);
		}

		browser = new Browser(shell, SWT.MOZILLA);
		nsIWebBrowser nsiWebBrowser = (nsIWebBrowser)browser.getWebBrowser();
		disableImages(nsiWebBrowser);

		browser.setVisible(true);
		browser.setJavascriptEnabled(false);
		browser.addProgressListener(new ProgressListener() 
		{
			@Override
			public void completed(ProgressEvent event) 
			{
				timer.stopTask(0);
				System.out.println("["+timer.getTimeMillis(0)+"ms]");

				Display.getCurrent().asyncExec(new Runnable() 
				{

					@Override
					public void run() 
					{
						document = getW3CDocument();
					}
				});
			}

			@Override
			public void changed(ProgressEvent event) 
			{
				System.out.print(".");
			}	
		});

		timer.startTask(0);
		System.out.print(url);
		browser.setUrl(url);

		while (!shell.isDisposed() && isRunning) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
	}

	/**
	 * Constructor.
	 */
	public StandaloneMozilla(String url) 
	{			
		this(null, url);
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Document loadDocument (String url)
	{
		StandaloneMozilla moz = new StandaloneMozilla(url);
		return moz.document;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Document loadDocument (Display display, String url)
	{
		StandaloneMozilla moz = new StandaloneMozilla(display, url);
		return moz.document;
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
				//inject JS to split words
				boolean success = browser.execute(SCRIPT);
				if (!success)
				{
					ErrorDump.error(this,"Could not inject JavaScript");
				}
				
				//obtain document from native code
				nsIWebBrowser webBrowser = (nsIWebBrowser)browser.getWebBrowser();
				if (webBrowser == null) {
					ErrorDump.error(this,"Could not get the nsIWebBrowser from the Browser widget");
					return;
				}
				nsIDOMWindow dw = webBrowser.getContentDOMWindow();
				nsIDOMDocument nsDoc = dw.getDocument();

				//convert to local document
				d = HTMLDocumentImpl.getDOMInstance(nsDoc);
				isRunning = false;
			}			
		}

		DocumentGetter dg = new DocumentGetter();

		dg.run();
		return (Document) dg.d;
	}

	public static void disableImages(nsIWebBrowser brow)
	{
		nsIWebBrowserSetup bset =
			(nsIWebBrowserSetup) brow.queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
		bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 0);
		bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, 0);
	}

	public static void main(String[] args)
	{
		//		StandaloneMozilla moz = new StandaloneMozilla();

		Document doc = StandaloneMozilla.loadDocument("http://www.google.com");
//		doc = StandaloneMozilla.loadDocument("http://www.kicker.de");
		System.out.println(doc.getBaseURI());
	}

}
