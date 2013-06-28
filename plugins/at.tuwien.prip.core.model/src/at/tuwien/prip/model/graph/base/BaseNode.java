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
package at.tuwien.prip.model.graph.base;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.document.semantics.SemanticText;
import at.tuwien.prip.model.document.semantics.WordSemantics;

public class BaseNode 
implements Cloneable
{
	protected String label;
	
	/* annotations */
	protected List<SemanticText> semanticAnnotations;
	protected List<SegmentType> layoutAnnotations;
	protected String jvSemanticAnnotation;
	
	/**
	 * Constructor.
	 */
	public BaseNode() 
	{	
		this.semanticAnnotations = new ArrayList<SemanticText>();
		this.layoutAnnotations = new ArrayList<SegmentType>();
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public BaseNode(BaseNode other) 
	{
		this.semanticAnnotations = new ArrayList<SemanticText>(other.semanticAnnotations);
		this.layoutAnnotations = new ArrayList<SegmentType>(other.layoutAnnotations);
		this.jvSemanticAnnotation = other.jvSemanticAnnotation;
		this.label = other.label;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean containsSemantics (WordSemantics semantics) 
	{
		for (SemanticText st : semanticAnnotations)
		{
			if (st.containsSemantics(semantics)) {
				return true;
			}
		}
		return false;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Add a single semantic annotation to this node.
	 * 
	 * @param semanticAnnotation
	 */
	public void addSemanticAnnotation(SemanticText semanticAnnotation) 
	{
		if (this.semanticAnnotations==null) 
		{
			this.semanticAnnotations = new ArrayList<SemanticText>();
		}
		if (!semanticAnnotations.contains(semanticAnnotation))
		{
			semanticAnnotations.add(semanticAnnotation);
		}
	}
	public void setSemanticAnnotations(List<SemanticText> semanticAnnotations) {
		this.semanticAnnotations = semanticAnnotations;
	}

	public List<SemanticText> getSemanticAnnotations() {
		return semanticAnnotations;
	}
	
	public String getJvSemanticAnnotation() {
		return jvSemanticAnnotation;
	}

	public void setJvSemanticAnnotation(String jvSemanticAnnotation) {
		this.jvSemanticAnnotation = jvSemanticAnnotation;
	}
	
	public Object clone()
	{
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
	
}
