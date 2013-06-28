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
 * StreamResourceLoader.java
 *
 * Finds a resource and opens a stream
 * for reading from the resource
 *
 * Created: Fri Dec 14 20:02:33 2001
 *
 * @author Michal Ceresna
 * @version
 */
class StreamResourceLoader extends CommonResourceLoader {

  public StreamResourceLoader(String path_to_resource,
                              ClassLoader loader) {
    super(path_to_resource, loader);
  }

  /**
   * opens a stream to the given resource
   */
  public Object run() {
    return getLoader().getResourceAsStream(getPathToResource());
  }

}// StreamResourceLoader
