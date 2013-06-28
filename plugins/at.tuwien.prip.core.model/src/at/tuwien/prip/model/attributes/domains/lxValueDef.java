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
package at.tuwien.prip.model.attributes.domains;


/**
 * lxValueDef.java
 *
 *
 * Created: Tue Aug 12 14:48:17 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public interface lxValueDef 
{
  public abstract boolean matches(String v);

  public String toString();

  public String getAttName();
  
} // lxValueDef
