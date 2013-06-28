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
package at.tuwien.prip.model.project;

import java.util.List;

import at.tuwien.prip.model.project.document.AbstractDocument;

/**
 * IProject.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jul 10, 2012
 */
public interface IProject 
{
	public void addDocument(AbstractDocument document);
	
	public void removeDocument(AbstractDocument document);
	
	public List<AbstractDocument> getDocuments();
	
	public String getName();
	
	public String getProjectResource();

	public void setName(String string);
}
