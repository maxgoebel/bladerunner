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
package at.tuwien.prip.model.project.document.tiff;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.eclipse.swt.graphics.Image;

import at.tuwien.prip.model.project.document.AbstractDocument;
import at.tuwien.prip.model.project.document.DocumentFormat;
import at.tuwien.prip.model.project.document.IDocument;

/**
 * TiffDocument.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 6, 2012
 */
public class TiffDocument extends AbstractDocument
implements IDocument
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Image image;

	public TiffDocument() {
		this.setFormat(DocumentFormat.TIFF);
	}

	@Override
	public Rectangle getBounds() {
		if (image!=null)
		{
			org.eclipse.swt.graphics.Rectangle b = image.getBounds();
			Rectangle result = new Rectangle(b.x,b.y,b.width,b.height);
			return result;
		}
		return null;
	}

	public Dimension getDimension() {
		if (image!=null)
		{
			return new Dimension(image.getBounds().width, image.getBounds().height);
		}
		return null;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

}
