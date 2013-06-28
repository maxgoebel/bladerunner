/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM
 * wlIWrapService.idl
 */

package org.weblearn.idl;

import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsISupportsArray;

/**
 * service for wrapping content from a browser tab
 */
public interface wlIWrapService extends nsISupports {

    String WLIWRAPSERVICE_IID =
        "{f91020b1-8951-40c7-8621-238d5d1ea099}";

    /**
     * Called to extract all information from the tab.
     *
     * The navigation sequence might close the tab after this method returns.
     */
    void extract(String aPatternName, xblBrowser inTab, nsIDOMNode in);

    /**
     * Returns the first DOM node matched by the given pattern.
     *
     * The navigation sequence might close the tab after this method returns.
     */
    nsIDOMNode find(String aPatternName, xblBrowser inTab);

    /**
     * Returns all DOM nodes matched by the given pattern.
     *
     * The navigation sequence might close the tab after this method returns.
     */
    //nsIDOMNode[] findAll(String aPatternName, xblBrowser inTab, long[] count);
    nsISupportsArray findAll(String aPatternName, xblBrowser inTab);

    /**
     * Classifies the given document into the most similar document cluster
     * and returns its name, or returns 'unknown' if no cluster is similar
     * enough.
     */
    String classify(nsIDOMDocument inDoc);

}