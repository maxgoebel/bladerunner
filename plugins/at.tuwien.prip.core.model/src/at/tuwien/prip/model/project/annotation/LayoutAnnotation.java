package at.tuwien.prip.model.project.annotation;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.blade.TableSelection;


/**
 * @author max
 *
 */
public class LayoutAnnotation extends Annotation 
{

	public LayoutAnnotation() 
	{
		this.type = AnnotationType.LAYOUT;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TableSelection> getTables() 
	{
		List<TableSelection> result = new ArrayList<TableSelection>();
		
		for (AbstractSelection selection : getItems())
		{
			if (selection instanceof TableSelection)
			{
				result.add((TableSelection) selection);
			}
		}
		return result;
	}
}
