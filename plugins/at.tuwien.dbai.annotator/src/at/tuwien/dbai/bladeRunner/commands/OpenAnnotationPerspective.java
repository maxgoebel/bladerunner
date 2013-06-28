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
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import at.tuwien.dbai.bladeRunner.AnnotationPerspective;

/**
 * OpenAnnotationPerspective.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 24, 2011
 */
public class OpenAnnotationPerspective extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {

			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

			// close wrapper perspective
			IPerspectiveDescriptor[] pers = workbench.getPerspectiveRegistry()
					.getPerspectives();
			for (IPerspectiveDescriptor desc : pers) {
				if (desc.getId()
						.equals("at.tuwien.prip.docwrap.annotator.perspectives.wrapper")) {
					window.getActivePage().closePerspective(desc, true, true);
					break;
				}
			}

			workbench.showPerspective(AnnotationPerspective.ID, window);

		} catch (WorkbenchException we) {
			we.printStackTrace();
		}

		return null;
	}

}
