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

public class DocumentUpdateEvent {

	private DocumentUpdate documentUpdate;

	private IDocumentUpdateProvider provider;

	public DocumentUpdateEvent() {
	}

	public DocumentUpdate getDocumentUpdate() {
		return documentUpdate;
	}

	public void setDocumentUpdate(DocumentUpdate update) {
		this.documentUpdate = update;
	}

	public IDocumentUpdateProvider getDocumentUpdateProvider() {
		return provider;
	}

	public void setDocumentUpdatedProvider(IDocumentUpdateProvider provider) {
		this.provider = provider;
	}

}
