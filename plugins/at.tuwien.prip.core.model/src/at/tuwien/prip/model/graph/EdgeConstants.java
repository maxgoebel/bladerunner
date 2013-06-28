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
 * EdgeConstants.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 30, 2011
 */
public class EdgeConstants {

	public static final double WEIGHT_LENGTH = 0.2d;
	public static final double WEIGHT_GEO = 0.45d;
	public static final double WEIGHT_SUB_INF = 1d;
	public static final double WEIGHT_ALIGN = 0.7d;
	
    public static final String ADJ_LEFT = "rel-adjLeft";
    public static final String ADJ_RIGHT = "rel-adjRight";
    public static final String ADJ_ABOVE = "rel-adjAbove";
    public static final String ADJ_BELOW = "rel-adjBelow";
    
	public static int LENGTH_ANY = 10;
	public static int LENGTH_BLOCK = 11;
	public static int LENGTH_COLUMN = 12;
	public static int LENGTH_GREATER = 13;
	
	public static int REL_NONE = 0;
	public static int REL_LEFT_RIGHT = 1; // or top to bottom 
	public static int REL_RIGHT_LEFT = 2; // or bottom to top
	
}
