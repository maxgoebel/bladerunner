/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+xblTabBrowser">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsIContentViewerEdit;
import org.mozilla.interfaces.nsIContentViewerFile;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIDOMXULElement;
import org.mozilla.interfaces.nsIDocShell;
import org.mozilla.interfaces.nsIDocumentCharsetInfo;
import org.mozilla.interfaces.nsIInputStream;
import org.mozilla.interfaces.nsIMarkupDocumentViewer;
import org.mozilla.interfaces.nsISHistory;
import org.mozilla.interfaces.nsISecureBrowserUI;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowserFind;
import org.mozilla.interfaces.nsIWebNavigation;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;

public interface xblTabBrowser extends nsISupports
{
  String XBLTABBROWSER_IID =
    "{31ccac19-5e00-41b9-98bf-bfcce3c5b3f4}";

  nsIDOMXULElement getXulElem();

  void setXulElem(nsIDOMXULElement arg1);

  boolean getAutocompleteenabled();

  void setAutocompleteenabled(boolean arg1);

  String getAutocompletepopup();

  void setAutocompletepopup(String arg1);

  boolean getAutoscroll();

  void setAutoscroll(boolean arg1);

  String getContentcontextmenu();

  void setContentcontextmenu(String arg1);

  String getContenttooltip();

  void setContenttooltip(String arg1);

  boolean getHandleCtrlPageUpDown();

  void setHandleCtrlPageUpDown(boolean arg1);

  void getBrowsers(nsIDOMXULElement[][] arg1, long[] arg2);

  boolean getCanGoBack();

  boolean getCanGoForward();

  nsIDOMDocument getContentDocument();

  String getContentTitle();

  nsIContentViewerEdit getContentViewerEdit();

  nsIContentViewerFile getContentViewerFile();

  nsIDOMWindow getContentWindow();

  nsIURI getCurrentURI();

  nsIDocShell getDocShell();

  nsIDocumentCharsetInfo getDocumentCharsetInfo();

  String getHomePage();

  nsIMarkupDocumentViewer getMarkupDocumentViewer();

  nsISecureBrowserUI getSecurityUI();

  nsIDOMXULElement getSelectedBrowser();

  nsIDOMXULElement getSelectedTab();

  void setSelectedTab(nsIDOMXULElement arg1);

  nsISHistory getSessionHistory();

  nsIDOMXULElement getTabContainer();

  void setTabContainer(nsIDOMXULElement arg1);

  nsIWebBrowserFind getWebBrowserFind();

  nsIWebNavigation getWebNavigation();

  nsIWebProgress getWebProgress();

  void addProgressListener(nsIWebProgressListener arg1);

  nsIDOMXULElement addTab(String arg1, nsIURI arg2, String arg3, nsIInputStream arg4, nsISupports arg5, boolean arg6);

  nsIDOMXULElement getBrowserForTab(nsIDOMXULElement arg1);

  void goBack();

  void goBackGroup();

  void goForward();

  void goHome();

  void gotoIndex(int arg1);

  void loadURI(String arg1, nsIURI arg2, String arg3);

  void loadURIWithFlags(String arg1, long arg2, nsIURI arg3, String arg4);

  void reload();

  void reloadAllTabs();

  void reloadTab(nsIDOMXULElement arg1);

  void removeAllTabsBut(nsIDOMXULElement arg1);

  void removeCurrentTab();

  void removeProgressListener(nsIWebProgressListener arg1);

  void removeTab(nsIDOMXULElement arg1);

  void stop();

}
