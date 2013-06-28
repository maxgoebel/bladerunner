/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.common.utils.resourceloader;

/**
 * URLResourceLoader.java
 *
 * Finds a resource and returns an url
 * pointing at it
 *
 * Created: Fri Dec 14 19:57:29 2001
 *
 * @author Michal Ceresna
 * @version
 */
class URLResourceLoader extends CommonResourceLoader {

  public URLResourceLoader(String path_to_resource,
                           ClassLoader loader) {
    super(path_to_resource, loader);
  }

  /**
   * returns an url to the given resource
   */
  public Object run() {
    return getLoader().getResource(getPathToResource());
  }

}// URLResourceLoader
