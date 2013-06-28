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
import at.tuwien.prip.model.project.selection.blade.TableSelection;

/**
 * TableAnnotation.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 19, 2012
 */
public class TableAnnotation extends Annotation 
{

	public enum TableAnnotationType { STRUCTURE, FUNCTIONAL, REGIONAL }
	
	private TableAnnotationType tableType;
	
	/**
	 * Constructor.
	 * @param uri
	 */
	public TableAnnotation(String uri) {
		super(uri, AnnotationType.TABLE);
	}
	
	/**
	 * Constructor.
	 */
	public TableAnnotation(String uri, TableAnnotationType tableType) 
	{
		this(uri);
		this.tableType = tableType;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TableSelection> getTables() 
	{
		List<TableSelection> tables = new ArrayList<TableSelection>();
		for (AbstractSelection item : items)
		{
			if (item instanceof TableSelection)
			{
				tables.add((TableSelection) item);
			}
		}
		return tables;
	}
	
	public TableAnnotationType getTableType() {
		return tableType;
	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	public List<TableSelection> getAllTables() 
//	{
//		List<TableSelection> tables = new ArrayList<TableSelection>();
//		for (AnnotationPage page : pages)
//		{
//			for (AbstractSelection sel : page.getItems())
//			{
//				if (sel instanceof TableSelection)
//				{
//					tables.add((TableSelection) sel);
//				}
//			}
//		}
//		return tables;
//	}
}
