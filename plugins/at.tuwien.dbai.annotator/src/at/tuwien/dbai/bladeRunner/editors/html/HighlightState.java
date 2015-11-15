package at.tuwien.dbai.bladeRunner.editors.html;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.selection.AbstractSelection;

/**
 * HighlightState.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 27, 2012
 */
public class HighlightState 
{

	private List<AbstractSelection> items;
	
	/**
	 * Constructor.
	 */
	public HighlightState() 
	{
		this.items = new ArrayList<AbstractSelection>();
	}
	
	public void clear() 
	{
		this.items = new ArrayList<AbstractSelection>();
	}
	
	public List<AbstractSelection> getItems() {
		return items;
	}
	
	public void setItems(List<AbstractSelection> items) {
		this.items = items;
	}

}
