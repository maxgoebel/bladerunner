package at.tuwien.prip.model.project.selection.blade;

import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SelectionType;

public class ConceptSectionSelection extends MultiPageSelection {

	private String sectionConcept;
	
	public ConceptSectionSelection() {
		super("CONCEPT_SECTION");
	}
	
	public void setSectionConcept(String sectionType) {
		this.sectionConcept = sectionType;
	}
	
	public String getSectionConcept() {
		return sectionConcept;
	}
}
