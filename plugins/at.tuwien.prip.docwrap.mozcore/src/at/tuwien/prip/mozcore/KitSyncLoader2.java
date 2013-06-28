package at.tuwien.prip.mozcore;

import static at.tuwien.prip.mozcore.utils.ProxyUtils.qi;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIHttpChannel;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

import at.tuwien.prip.common.log.ErrorDump;

public class KitSyncLoader2 {

    private final Browser g;
    private final String uri;
    private final Semaphore sem;

    private boolean loadFailed = false;

    public KitSyncLoader2(Browser g,
                         String uri)
    {
        this.g = g;
        this.uri = uri;
        this.sem = new Semaphore(2);
    }

    public boolean getLoadFailed() {
        return loadFailed;
    }

    private class GeckoAdapter implements nsIWebProgressListener {

        private int loadsNum = 0;

        public void started() {
            //ErrorDump.debug(this, "load started");
            sem.release();
            loadsNum++;
        }

        public void stopped(nsIRequest aRequest,
                            long aStatus)
        {
            //ErrorDump.debug(this, "load stopped");
            if (loadsNum>0) { //sometimes we get a load end without any load start
                if (aStatus!=0) {
                    loadFailed = true;
                } else {
                    nsIHttpChannel hc = qi(aRequest, nsIHttpChannel.class);
                    if (hc!=null) {
                        long s = hc.getResponseStatus();
                        if (s!=200) {
                            loadFailed = true;
                        }
                    }
                }

                sem.release();
                loadsNum--;
            }
        }
        public void onStateChange(nsIWebProgress aWebProgress,
                                  nsIRequest aRequest,
                                  long aStateFlags,
                                  long aStatus)
        {
            if ((aStateFlags & nsIWebProgressListener.STATE_IS_NETWORK)!=0 &&
                (aStateFlags & nsIWebProgressListener.STATE_START)!=0) {
                started();
            }

            if ((aStateFlags & nsIWebProgressListener.STATE_IS_NETWORK)!=0 &&
                (aStateFlags & nsIWebProgressListener.STATE_STOP)!=0) {
                stopped(aRequest, aStatus);
            }
        }

        public void onLocationChange(nsIWebProgress arg0, nsIRequest arg1, nsIURI arg2) {}
        public void onProgressChange(nsIWebProgress arg0, nsIRequest arg1, int arg2, int arg3, int arg4, int arg5) {}
        public void onSecurityChange(nsIWebProgress arg0, nsIRequest arg1, long arg2) {}
        public void onStatusChange(nsIWebProgress arg0, nsIRequest arg1, long arg2, String arg3) {}


        public nsISupports queryInterface(String iid) {
            return Mozilla.queryInterface(this, iid);
        }
    }

    public void load() {
        final GeckoAdapter ga = new GeckoAdapter();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
               ((nsIWebBrowser) g.getWebBrowser()).
                addWebBrowserListener(ga, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
            }
        });

        sem.acquireUninterruptibly(2);

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                try {
                    g.setUrl(uri);
                } catch (XPCOMException e) {
                    loadFailed = true;
                }
            }
        });

        if (!loadFailed) {
            if (Display.getCurrent()==null) {
                //not on ui thread
                try {
                    if (!sem.tryAcquire(2, 60, TimeUnit.SECONDS)) {
                        ErrorDump.debug(this, "load timeout");
                    }
                } catch (InterruptedException e) {
                    ErrorDump.error(this, e);
                }
            } else {
                //run the mozilla ui thread while the
                //thread waiting for loading-end finishes
                long start = System.currentTimeMillis();
                Display display = g.getDisplay();
                while (!sem.tryAcquire(2))
                {
                    if (!display.readAndDispatch()) display.sleep();
                    if (System.currentTimeMillis()-start>=60*1000) {
                        ErrorDump.debug(this, "load timeout");
                        break;
                    }
                }
            }
        }

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
            	((nsIWebBrowser) g.getWebBrowser()).
                removeWebBrowserListener(ga, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);
            }
        });
    }

}