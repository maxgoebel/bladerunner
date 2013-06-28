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
package at.tuwien.dbai.bladeRunner;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;

public class DocWrapStartUp implements IStartup {

	@Override
	public void earlyStartup() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (workbenchWindow != null) {
			IPerspectiveRegistry pr = workbenchWindow.getWorkbench()
					.getPerspectiveRegistry();
			IPerspectiveDescriptor ipr = workbenchWindow.getActivePage()
					.getPerspective();
			workbenchWindow.addPerspectiveListener(new PerspectiveAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.ui.PerspectiveAdapter#perspectiveActivated(org
				 * .eclipse.ui.IWorkbenchPage,
				 * org.eclipse.ui.IPerspectiveDescriptor)
				 */
				@Override
				public void perspectiveActivated(IWorkbenchPage page,
						IPerspectiveDescriptor perspectiveDescriptor) {
					super.perspectiveActivated(page, perspectiveDescriptor);

					// WrapperEditor we = DocWrapUIUtils.getWrapperEditor();
					// System.err.println("test");
				}
			});
		}

	}

}
