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
import at.tuwien.prip.model.project.selection.LabelSelection;

/**
 * 
 * LabelAnnotation.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
public class LabelAnnotation extends Annotation 
{
	private AnnotationLabel label;
	
	public LabelAnnotation(String uri) 
	{
		super(uri, AnnotationType.LABEL);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<LabelSelection> getLabelItems()
	{
		List<LabelSelection> labels = new ArrayList<LabelSelection>();
		for (AbstractSelection item : items)
		{
			if (item instanceof LabelSelection)
			{
				labels.add((LabelSelection) item);
			}
		}
		return labels;
	}
	
	public void setLabel(AnnotationLabel label) {
		this.label = label;
	}
	
	public AnnotationLabel getLabel() {
		return label;
	}
}
