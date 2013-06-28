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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class LabeledFigure extends Figure {

	private final Figure shapeFigure;
	private final Label labelFigure;
	private Rectangle customShapeConstraint;

	public LabeledFigure(String label) {
		setLayoutManager(new XYLayout());
		setBackgroundColor(ColorConstants.lightGray);
		setOpaque(true);

		shapeFigure = new Ellipse();
		this.add(shapeFigure);
		shapeFigure.setBackgroundColor(ColorConstants.yellow);

		shapeFigure.addMouseListener(new MouseListener.Stub() {
			@Override
			public void mousePressed(MouseEvent me) {
				customShapeConstraint = new Rectangle(
						(Rectangle) LabeledFigure.this.getLayoutManager()
								.getConstraint(shapeFigure));
				customShapeConstraint.width -= 6;
				customShapeConstraint.x += 3;
				LabeledFigure.this.getLayoutManager().setConstraint(
						shapeFigure, customShapeConstraint);
				LabeledFigure.this.revalidate();
			}
		});

		labelFigure = new Label(label);
		labelFigure.setOpaque(true);
		labelFigure.setBackgroundColor(ColorConstants.green);
		labelFigure.addMouseListener(new MouseListener.Stub() {
			@Override
			public void mousePressed(MouseEvent me) {
				Rectangle shapeFigureConstraint = new Rectangle(0, 0,
						bounds.width, bounds.height - 15);
				LabeledFigure.this.getLayoutManager().setConstraint(
						shapeFigure, shapeFigureConstraint);
				LabeledFigure.this.revalidate();
			}
		});
		this.add(labelFigure);

		this.addFigureListener(new FigureListener() {

			@Override
			public void figureMoved(IFigure source) {

				Rectangle bounds = LabeledFigure.this.getBounds();
				Rectangle shapeFigureConstraint = new Rectangle(0, 0,
						bounds.width, bounds.height - 15);
				LabeledFigure.this.getLayoutManager().setConstraint(
						shapeFigure, shapeFigureConstraint);

				Rectangle labelFigureConstraint = new Rectangle(0,
						bounds.height - 15, bounds.width, 15);
				if (customShapeConstraint != null) {
					labelFigureConstraint = customShapeConstraint;
				}
				LabeledFigure.this.getLayoutManager().setConstraint(
						labelFigure, labelFigureConstraint);
			}
		});
	}
}
