/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+inIViewEventUtils">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISupports;

public interface inIViewEventUtils extends nsISupports
{
  String INIVIEWEVENTUTILS_IID =
    "{41309267-f46c-4dc0-bc12-e53c412fba58}";

  void sendMouseEvent(String arg1, nsIDOMNode arg2, double arg3, double arg4, boolean arg5, boolean arg6, boolean arg7, boolean arg8, int arg9);

  void sendKeyEvent(String arg1, nsIDOMNode arg2, long arg3, long arg4, boolean arg5, boolean arg6, boolean arg7, boolean arg8);

}
