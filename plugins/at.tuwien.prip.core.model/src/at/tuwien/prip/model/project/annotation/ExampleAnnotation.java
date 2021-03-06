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
import at.tuwien.prip.model.project.selection.ExampleSelection;

/**
 * ExampleAnnotation.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
public class ExampleAnnotation extends Annotation 
{
	/**
	 * 
	 * @param uri
	 */
	public ExampleAnnotation(String uri) 
	{
		super(uri, AnnotationType.EXAMPLE);
	}

	public List<ExampleSelection> getExampleItems() {
		List<ExampleSelection> examples = new ArrayList<ExampleSelection>();
		for (AbstractSelection item : items)
		{
			if (item instanceof ExampleSelection)
			{
				examples.add((ExampleSelection) item);
			}
		}
		return examples;
	}
	
}
