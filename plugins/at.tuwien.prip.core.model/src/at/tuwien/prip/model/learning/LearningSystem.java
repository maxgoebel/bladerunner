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
package at.tuwien.prip.model.learning;

import java.util.List;

import at.tuwien.prip.model.project.document.IDocument;

/**
 * LearningSystem.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jun 30, 2012
 */
public class LearningSystem
{
	private List<IDocument> documentCollection;
	
//	private ILearner learner;
	
	public List<IDocument> getDocumentCollection() {
		return documentCollection;
	}
	
//	public ILearner getLearner() {
//		return learner;
//	}
	
	public void setDocumentCollection(List<IDocument> documentCollection) {
		this.documentCollection = documentCollection;
	}
	
//	public void setLearner(ILearner learner) {
//		this.learner = learner;
//	}
}
