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
package at.tuwien.prip.model.project.document;

import java.awt.Rectangle;

/**
 * IDocument.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * May 20, 2012
 */
public interface IDocument 
{
	public void setUri(String uri);
	
	public String getUri();
	
	public String getName ();
	
	public void setName(String name);
	
	public DocumentFormat getFormat();
	
	public void setFormat(DocumentFormat format);
	
	public double getScale();
	
	public void setScale(double scale);
	
	public Rectangle getBounds();	
}
