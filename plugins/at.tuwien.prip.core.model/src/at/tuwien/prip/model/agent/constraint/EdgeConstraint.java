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
package at.tuwien.prip.model.agent.constraint;

import at.tuwien.prip.model.graph.base.BaseEdge;

/**
 * 
 * EdgeConstraint.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 12, 2011
 */
public class EdgeConstraint implements IConstraint
{
	private ConstraintType type;
	
	private double confidence = 0d;
	
	private ConstraintSource source;
	
	private BaseEdge<?> edge;
	
	/**
	 * Constructor.
	 */
	public EdgeConstraint(BaseEdge<?> edge, 
			ConstraintType type, 
			double confidence, 
			ConstraintSource source) 
	{
		this.edge = edge;
		this.type = type;
		this.confidence = confidence;
		this.source = source;
	}

	/**
	 * Returns whether two constraints conflict on their
	 * respective edges.
	 * 
	 * @param other
	 * @return
	 */
	public boolean isConflicting (IConstraint other) 
	{
		if (other instanceof EdgeConstraint) 
		{
			EdgeConstraint c2 = (EdgeConstraint) other;
			if (this.edge.equals(c2.edge) && this.type!=c2.type) {
				return true;
			}
		}
		return false;
	}
	
	public double getConfidence() {
		return confidence;
	}

	public ConstraintSource getSource() {
		return source;
	}
	
	public ConstraintType getType() {
		return type;
	}

	public BaseEdge<?> getEdge () {
		return edge;
	}
}
