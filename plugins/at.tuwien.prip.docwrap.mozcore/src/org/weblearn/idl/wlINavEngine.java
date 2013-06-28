/**
 * NOTE: THIS IS A GENERATED FILE. PLEASE CONSULT THE ORIGINAL IDL FILE
 * FOR THE FULL DOCUMENTION AND LICENSE.
 *
 * @see <a href="http://lxr.mozilla.org/mozilla/search?string=interface+wlINavEngine">
 **/

package org.weblearn.idl;

import org.mozilla.interfaces.nsISupports;

public interface wlINavEngine extends nsISupports
{
  String WLINAVENGINE_IID =
    "{c08134f1-01be-4a63-b6b5-2f72f8998223}";

  void execute(xblBrowser inTab,
               String script,
               long[] outTabsNum, xblBrowser[][] outTabs);

}
