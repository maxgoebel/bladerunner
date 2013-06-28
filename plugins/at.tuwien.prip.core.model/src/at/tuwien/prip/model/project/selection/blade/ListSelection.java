package at.tuwien.prip.model.project.selection.blade;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.selection.MultiPageSelection;

/**
 * @author max
 *
 */
public class ListSelection extends MultiPageSelection {

	private List<ListItemSelection> items;
	
	public ListSelection() {
		super("LIST");
		this.items = new ArrayList<ListItemSelection>();
	}
	
	public List<ListItemSelection> getItems() {
		return items;
	}
	
	public void setItems(List<ListItemSelection> items) {
		this.items = items;
	}
}

