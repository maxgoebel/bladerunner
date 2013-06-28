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

/**
 * SegmentationConstraint.java
 * 
 * Segmentation constraint implementation.
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jan 22, 2012
 */
public class SegmentationConstraint implements IConstraint
{
	private ConstraintType type;

	private double confidence = 0d;

	private ConstraintSource source;
	
	/**
	 * Constructor.
	 */
	public SegmentationConstraint() 
	{

	}
	
	@Override
	public boolean isConflicting(IConstraint other) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
