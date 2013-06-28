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

import java.util.ArrayList;
import java.util.List;

import at.tuwien.dbai.bladeRunner.control.DocumentUpdate.UpdateType;
import at.tuwien.prip.model.project.document.DocumentModel;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;

/**
 * 
 * DocumentController.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date Oct 1, 2011
 */
public class DocumentController implements IDocumentUpdateProvider {

	/* the SINGLETON document model contains all document relevant information */
	public static final DocumentModel docModel = new DocumentModel();

	private List<IDocumentUpdateListener> listeners;

	private DocumentUpdate lastUpdate;

	/**
	 * Constructor.
	 */
	public DocumentController() {
		listeners = new ArrayList<IDocumentUpdateListener>();
	}

	public int getCurrentDocumentPage() {
		return docModel.getPageNum();
	}

	public DocumentModel getDocModel() {
		return docModel;
	}

	public IDocument getCurrentDocumentEntry() {
		return docModel.getDocument();
	}

	public void nextPage() {
		docModel.setPageNum(docModel.getPageNum() + 1);
		DocumentUpdate update = new DocumentUpdate();
		update.setType(UpdateType.PAGE_CHANGE);
		update.setUpdate(docModel);
		setDocumentUpdate(update);
	}

	public void prevPage() {
		docModel.setPageNum(docModel.getPageNum() - 1);
		DocumentUpdate update = new DocumentUpdate();
		update.setType(UpdateType.PAGE_CHANGE);
		update.setUpdate(docModel);
		setDocumentUpdate(update);
	}

	/**
	 * 
	 */
	@Override
	public void setDocumentUpdate(DocumentUpdate update)
	{
		DocumentModel model = update.getUpdate();
		if (!update.equals(lastUpdate)) 
		{
			if (update.getType() == UpdateType.NEW_DOCUMENT) 
			{
				docModel.setNumPages(model.getNumPages());
				if (model.getPageNum() == 0) {
					docModel.setPageNum(1); // reset for new documents
				} else {
					docModel.setPageNum(model.getPageNum());
					docModel.setNumPages(model.getNumPages());
				}

				/* update document format */
				docModel.setFormat(model.getFormat());

				/* update scale */
				docModel.setScale(0.8);

				/* update PDF file */
				docModel.setPdfFile(model.getPdfFile());

				/* update thumbnail image */
				docModel.setThumb(model.getThumb());

				/* update document graph */
				docModel.setDocumentGraph(model.getDocumentGraph());
				
				docModel.setDocument((BenchmarkDocument) model.getDocument());
			} 
			else if (update.getType() == UpdateType.PAGE_CHANGE)
			{
				docModel.setPageNum(model.getPageNum());
			} 
			else if (update.getType() == UpdateType.RESIZE) 
			{
				docModel.setScale(model.getScale());
			} 
			else if (update.getType() == UpdateType.DOCUMENT_CHANGE)
			{
				/* update document format */
				docModel.setFormat(model.getFormat());
				
				/* update thumbnail image */
				docModel.setThumb(model.getThumb());
				
				/* update document graph */
				docModel.setDocumentGraph(model.getDocumentGraph());
				
				docModel.setDocument((BenchmarkDocument) model.getDocument());
			}

			update.setUpdate(docModel);

			DocumentUpdateEvent ev = new DocumentUpdateEvent();
			ev.setDocumentUpdate(update);
			ev.setDocumentUpdatedProvider(this);
			
			for (IDocumentUpdateListener listener : listeners) {
				listener.documentUpdated(ev);
			}

		}

		lastUpdate = update;
	}

	@Override
	public DocumentUpdate getDocumentUpdate() {
		return lastUpdate;
	}

	@Override
	public void addDocumentUpdateListener(IDocumentUpdateListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeDocumentUpdateListener(IDocumentUpdateListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
}
