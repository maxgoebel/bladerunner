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
package at.tuwien.prip.model.document;

public class RectangleAdjustment {

	private int xOffset = 0;
	private int yOffset = 0;

	private double xScale = -1;
	private double yScale = -1;
	

	public RectangleAdjustment(int xOff, int yOff, double xScale, double yScale) {
		this.xOffset = xOff;
		this.yOffset = yOff;
		this.xScale = xScale;
		this.yScale = yScale;
	}

	public double getYScale() {
		return yScale;
	}
	
	public double getXScale() {
		return xScale;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}
}
