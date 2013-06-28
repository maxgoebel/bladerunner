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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import at.tuwien.dbai.bladeRunner.views.SelectionImageView;
import at.tuwien.dbai.bladeRunner.views.bench.BenchmarkNavigatorView;

/**
 * AnnotationPerspective.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 24, 2011
 */
public class AnnotationPerspective implements IPerspectiveFactory 
{
	public static String ID = "at.tuwien.dbai.bladeRunner.annotationPerspective";

	public AnnotationPerspective() {
	}

	@Override
	public void createInitialLayout(IPageLayout layout) 
	{
		DocWrapUIUtils.activePerspective = ID;

		String editorArea = layout.getEditorArea();

		layout.setEditorAreaVisible(true);
		layout.addStandaloneView(BenchmarkNavigatorView.ID, false,	IPageLayout.LEFT, 0.4f, editorArea);
		layout.addStandaloneView(SelectionImageView.ID, false, IPageLayout.BOTTOM, 0.35f, BenchmarkNavigatorView.ID);
		layout.setFixed(true);
	}
}
