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
package at.tuwien.dbai.bladeRunner.control;

public interface IDocumentUpdateProvider {

	public void addDocumentUpdateListener(IDocumentUpdateListener listener);

	public void removeDocumentUpdateListener(IDocumentUpdateListener listener);

	public void setDocumentUpdate(DocumentUpdate update);

	public DocumentUpdate getDocumentUpdate();

}
