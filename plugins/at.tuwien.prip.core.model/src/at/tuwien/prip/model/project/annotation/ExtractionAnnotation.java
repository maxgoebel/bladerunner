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
package at.tuwien.prip.model.project.annotation;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.ExtractionResult;

public class ExtractionAnnotation extends Annotation 
{
	/**
	 * Constructor.
	 * @param uri
	 */
	public ExtractionAnnotation(String uri) 
	{
		super(uri, AnnotationType.EXTRACTION);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<ExtractionResult> getExtractionItems()
	{
		List<ExtractionResult> extractions = new ArrayList<ExtractionResult>();
		for (AbstractSelection item : items)
		{
			if (item instanceof ExtractionResult)
			{
				extractions.add((ExtractionResult) item);
			}
		}
		return extractions;
	}
	
}
