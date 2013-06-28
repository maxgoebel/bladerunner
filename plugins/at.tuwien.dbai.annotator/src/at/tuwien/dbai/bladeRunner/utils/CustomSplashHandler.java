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
package at.tuwien.dbai.bladeRunner.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.splash.EclipseSplashHandler;

import at.tuwien.dbai.bladeRunner.Activator;

public class CustomSplashHandler extends EclipseSplashHandler {

	private static final String BETA_PNG = "splash.png";
	private static final int BORDER = 10;
	private Image image;

	public CustomSplashHandler() {
		super();
	}

	@Override
	public void init(Shell splash) {
		super.init(splash);

		// here you could check some condition on which decoration to show

		ImageDescriptor descriptor = Activator.getImageDescriptor(BETA_PNG);
		if (descriptor != null)
			image = descriptor.createImage();
		if (image != null) {
			final int xposition = splash.getSize().x
					- image.getImageData().width - BORDER;
			final int yposition = BORDER;
			getContent().addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(image, xposition, yposition);
				}
			});
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (image != null)
			image.dispose();
	}
}
