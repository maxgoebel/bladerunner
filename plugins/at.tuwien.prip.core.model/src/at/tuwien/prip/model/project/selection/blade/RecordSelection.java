package at.tuwien.prip.model.project.selection.blade;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.selection.AbstractSelection;

/**
 *
 * @author max
 *
 */
public class RecordSelection extends AbstractSelection {

	private List<AbstractSelection> selections;
	
	public RecordSelection() {
		super("RECORD");
		
		this.selections = new ArrayList<AbstractSelection>();
	}

	public List<AbstractSelection> getSelections() {
		return selections;
	}

}
