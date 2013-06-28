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
package at.tuwien.dbai.bladeRunner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Composite;

import at.tuwien.dbai.bladeRunner.utils.SWTImageUtils;

/**
 * TakeScreenshot.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 25, 2011
 */
public class TakeScreenshot extends AbstractHandler implements IHandler {

	Composite parent;

	public TakeScreenshot(Composite parent) {
		this.parent = parent;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		org.eclipse.swt.graphics.Rectangle region = new org.eclipse.swt.graphics.Rectangle(
				0, 0, 400, 400);
		SWTImageUtils.takeScreenshot(parent, region);
		return null;
	}

}
