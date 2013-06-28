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

/**
 * HTMLTagDefSet.java
 *
 * immutable (constant) set of tag definitions
 *
 * Created: Sun Jul 27 17:56:05 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public interface HTMLTagDefSet {

  public boolean contains(String tag_name);

} // HTMLTagDefSet
