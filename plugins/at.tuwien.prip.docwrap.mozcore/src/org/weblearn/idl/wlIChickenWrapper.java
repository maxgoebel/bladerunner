/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+wlIChickenWrapper">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsIDOMChromeWindow;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMWindowInternal;
import org.mozilla.interfaces.nsISupports;

public interface wlIChickenWrapper extends nsISupports
{
  String WLICHICKENWRAPPER_IID =
    "{2471e202-2da9-48d9-92ef-4746030eb91d}";

  short CORRECT_MATCH = 1;

  short NO_MATCH = -1;

  short AMBIGUOUS_MATCH = -2;

  short INCORRECT_MATCH = -3;

  short GENERAL_ERROR = -4;

  int checkRecordable(nsIDOMNode arg1);

  boolean isClickable(nsIDOMNode arg1);

  void findKeywordsOfNode(nsIDOMNode arg1, long[] arg2, String[][] arg3);

  xblTabBrowser getTabBrowser(nsIDOMChromeWindow arg1);

  nsIDOMWindowInternal getVisibleHtmlWindow(nsIDOMChromeWindow arg1);

  void setupWindow(nsIDOMChromeWindow chromeWindow);

}
