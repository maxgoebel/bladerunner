/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+xblBrowser">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsIAccessible;
import org.mozilla.interfaces.nsIContentViewerEdit;
import org.mozilla.interfaces.nsIContentViewerFile;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIDOMXULElement;
import org.mozilla.interfaces.nsIDocShell;
import org.mozilla.interfaces.nsIDocumentCharsetInfo;
import org.mozilla.interfaces.nsIMarkupDocumentViewer;
import org.mozilla.interfaces.nsISHistory;
import org.mozilla.interfaces.nsISecureBrowserUI;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebBrowserFind;
import org.mozilla.interfaces.nsIWebNavigation;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;

public interface xblBrowser extends nsISupports
{
  String XBLBROWSER_IID =
    "{80169748-7667-4389-917f-645644554d60}";

  nsIDOMXULElement getXulElem();

  void setXulElem(nsIDOMXULElement arg1);

  boolean getAutocompleteenabled();

  void setAutocompleteenabled(boolean arg1);

  String getAutocompletepopup();

  void setAutocompletepopup(String arg1);

  boolean getAutoscroll();

  void setAutoscroll(boolean arg1);

  boolean getDisablehistory();

  void setDisablehistory(boolean arg1);

  boolean getDisablesecurity();

  void setDisablesecurity(boolean arg1);

  nsIAccessible getAccessible();

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

  nsISHistory getSessionHistory();

  nsIWebBrowserFind getWebBrowserFind();

  nsIWebNavigation getWebNavigation();

  nsIWebProgress getWebProgress();

  void addProgressListener(nsIWebProgressListener arg1);

  void goBack();

  void goForward();

  void goHome();

  void gotoIndex(int arg1);

  void loadURI(String arg1, nsIURI arg2, String arg3);

  void loadURIWithFlags(String arg1, long arg2, nsIURI arg3, String arg4);

  void reload();

  void removeProgressListener(nsIWebProgressListener arg1);

  void removeTab(nsIDOMXULElement arg1);

  void stop();

}
