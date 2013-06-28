package at.tuwien.prip.model.project.selection.blade;

import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SelectionType;

public class SectionSelection extends MultiPageSelection {

	private String sectionType;
	
	public SectionSelection() 
	{
		super("SECTION");
	}
	
	public String getSectionType() {
		return sectionType;
	}
	
	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}
	
}
