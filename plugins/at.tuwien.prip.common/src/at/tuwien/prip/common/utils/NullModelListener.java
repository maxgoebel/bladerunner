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
package at.tuwien.prip.common.utils;

public class NullModelListener implements IModelListener {
	protected static NullModelListener soleInstance = new NullModelListener();
	public static NullModelListener getSoleInstance() {
		return soleInstance;
	}

	/*
	 * @see IModelListener#add(ModelEvent)
	 */
	public void add(ModelEvent event) {}

	/*
	 * @see IModelListener#remove(ModelEvent)
	 */
	public void remove(ModelEvent event) {}

	/*
	 * @see IModelListener#remove(ModelEvent)
	 */
	public void update(ModelEvent event) {}

}//NullModelListener
