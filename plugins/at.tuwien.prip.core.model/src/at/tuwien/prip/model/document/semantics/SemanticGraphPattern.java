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
package at.tuwien.prip.model.document.semantics;

import java.awt.Rectangle;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.SemanticSegment;
import at.tuwien.prip.model.graph.base.IGraph;

/**
 * GraphPattern.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Dec 15, 2011
 */
public class SemanticGraphPattern
{
	private IGraph<?, ?> graph;
	
	private WordSemantics patternName;
	
	private GenericSegment segment;
	
	private String textContent;
	
	/**
	 * Constructor.
	 * 
	 * @param graph
	 * @param txt
	 * @param name
	 */
	public SemanticGraphPattern(IGraph<?, ?> graph, String txt, WordSemantics name) 
	{
		this.graph = graph;
		this.patternName = name;
		this.textContent = txt;
		
		/* create semantic segment */	
		Rectangle bounds = graph.getDimensions();
		segment = new SemanticSegment(
				bounds.x,
				bounds.x + bounds.width, 
				bounds.y, 
				bounds.y + bounds.height);
		SemanticText st = new SemanticText(txt, name);
		((SemanticSegment) segment).addSemanticAnnotation(st);
	}
	
	public String getTextContent() {
		return textContent;
	}
	
	public IGraph<?,?> getGraph() {
		return graph;
	}
	
	public WordSemantics getPatternName() {
		return patternName;
	}
	
	public GenericSegment getSegment() {
		return segment;
	}
	
}
