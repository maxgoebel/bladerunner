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
package at.tuwien.dbai.bladeRunner.editors.annotator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import at.tuwien.dbai.bladeRunner.views.DocumentGraphCanvas;

/**
 * 
 * DocWrapHighlighter.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 28, 2011
 */
public class DocWrapHighlighter {

	private Canvas canvas;

	private IFigure highlightContent;

	/**
	 * Constructor.
	 * 
	 * @param canvas
	 */
	public DocWrapHighlighter(Canvas canvas) 
	{
		this.canvas = canvas;
		// this.highlightContent = new Panel();

		if (canvas instanceof DocumentGraphCanvas)
		{
//			LayeredPane lPane = (LayeredPane) ((DocumentGraphPlotter) canvas).getRootPane();
//			Layer highlightLayer = new Layer();
//			highlightLayer.add(highlightContent);
//			this.highlightContent = ((DocumentGraphCanvas) canvas).getRootPane();
//			ScalableLayeredPane slPane = (ScalableLayeredPane) 
//					((DocumentGraphCanvas) canvas).getRootPane();
//			slPane.addLayerAfter(highlightLayer, "highlight", after)
		}
	}

	/**
	 * 
	 * @param rectangle
	 */
	public void blinkRectangle(Rectangle rectangle) 
	{
		this.highlightContent = ((DocumentGraphCanvas) canvas).getContents();
		final RectangleFigure fig = new RectangleFigure();
		fig.setAlpha(100);
		fig.setFill(false);
		fig.setForegroundColor(ColorConstants.red);
		fig.setBounds(rectangle);
		fig.setVisible(true);
		highlightContent.add(fig);

//		DocWrapUIUtils.getActiveEditor().getSite().getShell().getDisplay()
//				.asyncExec(new Runnable() {
//					public void run() {
//						for (int i = 0; i < 15; i++) {
//							fig.setVisible(!fig.isVisible());
//							try {
//								Thread.sleep(100);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							canvas.redraw();
//						}
//					}
//
//				});

		highlightContent.remove(fig);
	}

}
