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

import at.tuwien.prip.model.graph.EEdgeRelation;
import at.tuwien.prip.model.graph.EdgeRelation;

public class BaseEdge<S extends BaseNode> 
implements Cloneable
{
	protected S from;
	
	protected S to;
	
    protected String relation;
    protected List<EdgeRelation> edgeRelations;
//    protected List<EdgeConstraint> constraints;
    protected double confidence;
	
    /**
     * Constructor.
     */
    public BaseEdge() 
    { 
    	this.edgeRelations = new ArrayList<EdgeRelation>();
//    	this.constraints = new ArrayList<EdgeConstraint>();
    }
    
    /**
     * Constructor.
     * 
     * @param from
     * @param to
     * @param relation
     */
    public BaseEdge (S from, S to, String relation)
    {
    	this.from = from;
    	this.to = to;
    	this.relation = relation;
    }
    
    /**
     * Copy constructor.
     * 
     * @param other
     */
    public BaseEdge (BaseEdge<S> other) 
    {
    	this.edgeRelations = new ArrayList<EdgeRelation>(other.getEdgeRelations());
    	this.confidence = other.confidence;
//    	this.constraints = new ArrayList<EdgeConstraint>();
    	this.relation = other.relation;
    	this.from = other.getFrom();
    	this.to = other.getTo();
    }
    
    public S getFrom() {
		return from;
	}

	public void setFrom(S from) {
		this.from = from;
	}

	public S getTo() {
		return to;
	}

	public void setTo(S to) {
		this.to = to;
	}
	
	public boolean hasRelation (EEdgeRelation relation) {
		for (EdgeRelation rel : edgeRelations) {
			if (rel.getRelation().equals(relation)) {
				return true;
			}
		}
		return false;
	}
	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public List<EdgeRelation> getEdgeRelations() {
		return edgeRelations;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	@Override
	protected Object clone() 
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
