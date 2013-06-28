package at.tuwien.prip.model.project.selection.blade;

import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SelectionType;

public class FunctionalSelection extends MultiPageSelection {

	private String function;
	
	public FunctionalSelection() 
	{
		super("FUNCTIONAL");
	}
	
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}

}
