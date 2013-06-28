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
package at.tuwien.prip.model.document.layout;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.graph.DocNode;

public class LayoutObject {

	protected List<DocNode> elements = new ArrayList<DocNode>();

	private Rectangle bounds = new Rectangle(0,0,0,0);

	public List<DocNode> getElements() {
		return elements;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void addElement (DocNode node) {
		if (!elements.contains(node)) {

			if (elements.size()==0) {
				Rectangle2D r2d = node.getBoundingBox();
				bounds = new Rectangle(r2d.getBounds());
			} else {
				bounds.add(node.getBoundingBox());
			}
			elements.add(node);
		}
	}
}
