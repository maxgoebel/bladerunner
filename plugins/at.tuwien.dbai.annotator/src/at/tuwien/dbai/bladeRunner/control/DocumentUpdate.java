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

import at.tuwien.prip.model.project.document.DocumentModel;

public class DocumentUpdate {

	/* describe possible document updates */
	public enum UpdateType {

		RESIZE, PAGE_CHANGE, DOCUMENT_CHANGE, REPAN, GRAPH_CHANGE, NEW_DOCUMENT, DOCUMENT_LOAD

	}

	private DocumentModel update;

	private UpdateType type;

	private Object provider;

	public Object getProvider() {
		return provider;
	}

	public void setProvider(Object provider) {
		this.provider = provider;
	}

	public void setUpdate(DocumentModel update) {
		this.update = update;
	}

	public DocumentModel getUpdate() {
		return update;
	}

	public void setType(UpdateType type) {
		this.type = type;
	}

	public UpdateType getType() {
		return type;
	};

}
