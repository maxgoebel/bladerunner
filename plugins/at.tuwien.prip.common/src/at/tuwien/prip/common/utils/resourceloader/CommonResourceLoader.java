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

import java.security.PrivilegedAction;

/**
 * CommonResourceLoader.java
 *
 * base class for all specialised (url, stream)
 * ResourceLoader classes
 *
 * Created: Fri Dec 14 20:04:31 2001
 *
 * @author Michal Ceresna
 * @version
 */
abstract class CommonResourceLoader
  implements PrivilegedAction<Object>
{

    private String path_to_resource;
    private ClassLoader loader;

  public CommonResourceLoader(String path_to_resource,
                              ClassLoader loader)
  {
    this.path_to_resource = path_to_resource;
    this.loader = loader;
  }


  ClassLoader getLoader() {
    return loader;
  }

  String getPathToResource() {
    return path_to_resource;
  }

}// CommonResourceLoader
