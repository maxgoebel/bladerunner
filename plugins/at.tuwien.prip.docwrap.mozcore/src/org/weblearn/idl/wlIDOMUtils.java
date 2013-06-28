/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+wlIDOMUtils">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISupports;

public interface wlIDOMUtils extends nsISupports
{
  String WLIDOMUTILS_IID =
    "{0ce713bd-c397-4773-9cf3-78463bb4eacb}";

  /* const unsigned short DOM_ONLY = 1; */
  public static final short DOM_ONLY = 1;

  /* const unsigned short DOM_AND_SELECTED_VISUAL_PROPERTIES = 2; */
  public static final short DOM_AND_SELECTED_VISUAL_PROPERTIES = 2;

  /* const unsigned short DOM_AND_ALL_VISUAL_PROPERTIES = 3; */
  public static final short DOM_AND_ALL_VISUAL_PROPERTIES = 3;

  /* wlIJavaObjContainer clone (in nsIDOMNode source, in wlIJavaObjContainer jdoc, in unsigned short copyMode); */
  wlIJavaObjContainer _clone(nsIDOMNode source, wlIJavaObjContainer jdoc, short copyMode);

}
