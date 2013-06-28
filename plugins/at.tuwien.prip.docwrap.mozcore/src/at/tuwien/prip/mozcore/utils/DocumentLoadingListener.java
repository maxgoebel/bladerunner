package at.tuwien.prip.mozcore.utils;

import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserChrome;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

public abstract class DocumentLoadingListener implements nsIWebProgressListener
{

	/**
     * chrome window where we listen for load events
     */
    private final nsIWebBrowserChrome chrome;

    public DocumentLoadingListener(nsIWebBrowserChrome chrome)
    {
        this.chrome = chrome;
    }

    public nsISupports queryInterface(String iid) {
        return Mozilla.queryInterface(this, iid);
    }

    /**
     * starts monitoring reloading of documents
     * inside of the given browser window
     */
    public void attach() {
        chrome.getWebBrowser().addWebBrowserListener(this, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
    }

    /**
     * stops monitoring reloading of documents
     * inside of the given browser window
     */
    public void detach() {
        try {
        	nsIWebBrowser browser = chrome.getWebBrowser();
        	if (browser!=null) {
        		chrome.getWebBrowser().removeWebBrowserListener(
        				this, 
        				nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
        	}
        } catch (XPCOMException e) {
            //occurs when closing editor with hooked listeners, just ignore
            //ErrorDump.error(this, e);
        }
    }

    /**
     *
     * @return
     */
    public nsIWebBrowserChrome getChromeWindow() {
        return chrome;
    }

    /**
     *
     */
    public void onStateChange(nsIWebProgress aWebProgress,
                              nsIRequest aRequest,
                              long aStateFlags,
                              long aStatus)
    {
        if ((aStateFlags & nsIWebProgressListener.STATE_IS_NETWORK)!=0 &&
            (aStateFlags & nsIWebProgressListener.STATE_START)!=0)
        {
            onLoadStarted();
        }

        if ((aStateFlags & nsIWebProgressListener.STATE_IS_NETWORK)!=0 &&
            (aStateFlags & nsIWebProgressListener.STATE_STOP)!=0)
        {
//            onLoadCompleted();
        }
        System.err.println();
    }

    protected abstract void onLoadStarted();
    protected abstract void onLoadCompleted();

    public void onLocationChange(nsIWebProgress arg0, nsIRequest arg1, nsIURI arg2) {}
    public void onProgressChange(nsIWebProgress arg0, nsIRequest arg1, int arg2, int arg3, int arg4, int arg5) {}
    public void onSecurityChange(nsIWebProgress arg0, nsIRequest arg1, long arg2) {}
    public void onStatusChange(nsIWebProgress arg0, nsIRequest arg1, long arg2, String arg3) {}

}
