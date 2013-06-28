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
package at.tuwien.prip.model.graph;

/**
 * EdgeRelation.java
 *
 * An Edge Relation with confidence and weight.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Apr 14, 2011
 */
public class EdgeRelation {

	private double confidence = 0d;
	
	private double weight = 0d;
	
	private EEdgeRelation relation;

	/**
	 * Constructor.
	 * @param relation
	 */
	public EdgeRelation(EEdgeRelation relation) {
		this.relation = relation;
	}
	
	/**
	 * Constructor.
	 * @param relation
	 * @param weight
	 */
	public EdgeRelation(EEdgeRelation relation, double weight) {
		this(relation);
		this.weight = weight;
	}
	
	/**
	 * Constructor.
	 * @param relation
	 * @param weight
	 * @param confidence
	 */
	public EdgeRelation(EEdgeRelation relation, double weight, double confidence) {
		this(relation, weight);
		this.confidence = confidence;
	}
	
	
	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public EEdgeRelation getRelation() {
		return relation;
	}

	public void setRelation(EEdgeRelation relation) {
		this.relation = relation;
	}
	
	@Override
	public String toString() {
		
		return relation.name() + " (confidence="+confidence+", weight="+weight+")";
	}
}
