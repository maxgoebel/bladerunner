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
package at.tuwien.prip.model.attributes.html;

import java.util.*;

/**
 * HTMLValueDomain.java
 *
 *
 * Created: Sun Jul 27 18:20:34 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class HTMLValueDomain {

  private final Class<?> domain_klass;
  private final Object[] factory_params;

  public HTMLValueDomain(Class<?> klass,
                         Comment c)
  {
    this(klass, new Object[] {}, c);
  }

  public HTMLValueDomain(Class<?> klass,
                         Set<?> s,
                         Comment c)
  {
    this(klass, new Object[] { s }, c);
  }

  private HTMLValueDomain(Class<?> klass,
                          Object[] factory_params,
                          Comment c)
  {
    this.domain_klass = klass;
    this.factory_params = factory_params;
  }

  public Class<?> getDomainClass() {
    return domain_klass;
  }

  public Object[] getFactoryParams() {
    return factory_params;
  }

} // HTMLValueDomain
