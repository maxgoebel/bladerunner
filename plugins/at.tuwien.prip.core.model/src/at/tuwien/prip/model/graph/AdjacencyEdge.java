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

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.utils.Utils;

/*
import edu.uci.ics.jung.graph.impl.SimpleUndirectedSparseVertex;
*/

/**
 * This represents an edge in the neighbourhood graph
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class AdjacencyEdge<T extends GenericSegment> 
{
	protected T nodeFrom, nodeTo;
	
    public static final int REL_LEFT = 0;
    public static final int REL_RIGHT = 1;
    public static final int REL_ABOVE = 2;
    public static final int REL_BELOW = 3;
    
    protected float weight;
    
    protected boolean ruled;
    
    protected int direction;

    /**
     * Constructor.
     *
     * @param todo: add parameters :)
     */
    
    public AdjacencyEdge(T nodeFrom, T nodeTo, int direction)
    {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.direction = direction;
        ruled = false;
    }
    
    public AdjacencyEdge(T nodeFrom, T nodeTo, int direction, float weight)
    {
    	this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.direction = direction;
        this.weight = weight;
        ruled = false;
    }

    public T getNodeFrom() {
		return nodeFrom;
	}

	public void setNodeFrom(T nodeFrom) {
		this.nodeFrom = nodeFrom;
	}

	public T getNodeTo() {
		return nodeTo;
	}

	public void setNodeTo(T nodeTo) {
		this.nodeTo = nodeTo;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getLowCoord()
    {
        switch(direction)
        {
            case REL_LEFT:
                return nodeTo.getX2();
            case REL_RIGHT:
                return nodeFrom.getX2();
            case REL_ABOVE:
                return nodeFrom.getY2();
            case REL_BELOW:
                return nodeTo.getY2();
            default:
                // yet another nonsensical comparison!
                return -1.0f;
        }   
    }
    
    public float getHiCoord()
    {
        switch(direction)
        {
            case REL_LEFT:
                return nodeFrom.getX1();
            case REL_RIGHT:
                return nodeTo.getX1();
            case REL_ABOVE:
                return nodeTo.getY1();
            case REL_BELOW:
                return nodeFrom.getY1();
            default:
                // yet another nonsensical comparison!
                return -1.0f;
        }
    }
    
    public float getMidCoord()
    {
    	return (getLowCoord() + getHiCoord()) / 2.0f;
    }

    public boolean isHorizontal()
    {
        if (direction == REL_LEFT ||
            direction == REL_RIGHT)
            return true;
        else
            return false;
    }
    
    public boolean isVertical()
    {
        if (direction == REL_ABOVE ||
            direction == REL_BELOW)
            return true;
        else
            return false;
    }
    
    // 2011-01-26: TODO: CLEAN UP ALL THIS!
    
    public float getPhysicalLength()
    {
//    	2011-01-26 -- now always uses bounding box measurements
//    	baseline comparisons to be moved elsewhere
//    	float topLineBaseline, bottomLineBaseline;
    	
        switch(direction)
        {
            case REL_LEFT:
               return Math.abs(nodeFrom.getX1() - nodeTo.getX2());
            case REL_RIGHT:
                return Math.abs(nodeTo.getX1() - nodeFrom.getX2());
            case REL_ABOVE:
            	return Math.abs(nodeTo.getY1() - nodeFrom.getY2());
            	
            	/*
            	if (nodeFrom instanceof Cluster)
            	{
            		Cluster c = (Cluster)nodeFrom;
            		if (c.getFoundLines() != null)
            		{
	            		Collections.sort(c.getFoundLines(), new YComparator());
	            		TextLine topLine = (TextLine)c.getFoundLines().getFirst();
	            		topLineBaseline = topLine.getY1();
            		}
            		else topLineBaseline = nodeFrom.getY1();
            	}
            	else
            	{
            		//System.out.println("nodeFrom: " + nodeFrom);
            		//System.out.println("nodeTo: " + nodeTo);
            		// error: nodeFrom is null
            		topLineBaseline = nodeFrom.getY1();
            	}
            	if (nodeTo instanceof Cluster)
            	{
            		Cluster c = (Cluster)nodeTo;
            		if (c.getFoundLines() != null)
            		{
	            		Collections.sort(c.getFoundLines(), new YComparator());
	            		TextLine bottomLine = (TextLine)c.getFoundLines().getLast();
	            		bottomLineBaseline = bottomLine.getY1();
            		}
            		else bottomLineBaseline = nodeTo.getY1();
            	}
            	else
            	{
            		bottomLineBaseline = nodeTo.getY1();
            	}
            	
            	return bottomLineBaseline - topLineBaseline;
            	*/
                
            case REL_BELOW:
                return Math.abs(nodeFrom.getY1() - nodeTo.getY2());
            	/*
            	if (nodeFrom instanceof Cluster)
            	{
            		Cluster c = (Cluster)nodeFrom;
            		if (c.getFoundLines() != null)
            		{
	            		Collections.sort(c.getFoundLines(), new YComparator());
	            		TextLine topLine = (TextLine)c.getFoundLines().getLast();
	            		topLineBaseline = topLine.getY1();
            		}
            		else topLineBaseline = nodeFrom.getY1();
            	}
            	else
            	{
            		topLineBaseline = nodeFrom.getY1();
            	}
            	if (nodeTo instanceof Cluster)
            	{
            		Cluster c = (Cluster)nodeTo;
            		if (c.getFoundLines() != null)
            		{
	            		Collections.sort(c.getFoundLines(), new YComparator());
	            		TextLine bottomLine = (TextLine)c.getFoundLines().getFirst();
	            		bottomLineBaseline = bottomLine.getY1();
            		}
            		else bottomLineBaseline = nodeTo.getY1();
            	}
            	else
            	{
            		bottomLineBaseline = nodeTo.getY1();
            	}
            	
            	return topLineBaseline - bottomLineBaseline;
            	*/
            	
            default:
                return -1.0f;
        }
    }

    // in case ruling line intersects the block of text itself
    // (asiafrontpage/ihtfrontpage examples)
    public GenericSegment toEnlargedBoundingSegment()
    {
    	float tolerance = 0.0f;
    	if (nodeFrom instanceof TextSegment && nodeTo instanceof TextSegment)
    	{
    		float afs = Utils.avg(((TextSegment)nodeFrom).getFontSize(),
    				((TextSegment)nodeTo).getFontSize());
    		tolerance = afs * 0.25f;
    	}
    	// note: in comparison to the next method, max and min
    	// are swapped for yo and xo calculations.
    	
    	float newX1, newX2, newY1, newY2; //, xo1, xo2, yo1, yo2;
    	//System.out.println("direction: " + direction);
    	switch(direction)
        {
            case REL_LEFT:
                newX1 = nodeTo.getX2(); newX2 = nodeFrom.getX1();

            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                newY1 = Utils.minimum(nodeFrom.getY1(), nodeTo.getY1());
                newY2 = Utils.maximum(nodeFrom.getY2(), nodeTo.getY2());
                //newY1 = (yo1 + yo2) / 2;
            	//newY2 = newY1;
                break;
            case REL_RIGHT:
            	newX2 = nodeTo.getX1(); newX1 = nodeFrom.getX2();
                
            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                newY1 = Utils.minimum(nodeFrom.getY1(), nodeTo.getY1());
                newY2 = Utils.maximum(nodeFrom.getY2(), nodeTo.getY2());
                //newY1 = (yo1 + yo2) / 2;
            	//newY2 = newY1;
                break;
            case REL_ABOVE:
                newY1 = nodeFrom.getY2() - tolerance; 
                newY2 = nodeTo.getY1() + tolerance;

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                newX1 = Utils.minimum(nodeFrom.getX1(), nodeTo.getX1());
                newX2 = Utils.maximum(nodeFrom.getX2(), nodeTo.getX2());
                //newX1 = (xo1 + xo2) / 2;
                //newX2 = newX1;
                break;
            case REL_BELOW:
            	newY2 = nodeFrom.getY1() + tolerance; 
            	newY1 = nodeTo.getY2() - tolerance;

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                newX1 = Utils.minimum(nodeFrom.getX1(), nodeTo.getX1());
                newX2 = Utils.maximum(nodeFrom.getX2(), nodeTo.getX2());
                //newX1 = (xo1 + xo2) / 2;
                //newX2 = newX1;
                break;
            default:
            	//System.out.println("whoops!");
                newX1 = -1; newX2 = -1; newY1 = -1; newY2 = -1;
        }
    	return new GenericSegment(newX1, newX2, newY1, newY2);
    }
    
    public GenericSegment toBoundingSegment()
    {
    	// note: in comparison to the next method, max and min
    	// are swapped for yo and xo calculations.
    	
    	float newX1, newX2, newY1, newY2; //, xo1, xo2, yo1, yo2;
    	//System.out.println("direction: " + direction);
    	switch(direction)
        {
            case REL_LEFT:
                newX1 = nodeTo.getX2(); newX2 = nodeFrom.getX1();

            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                newY1 = Utils.minimum(nodeFrom.getY1(), nodeTo.getY1());
                newY2 = Utils.maximum(nodeFrom.getY2(), nodeTo.getY2());
                //newY1 = (yo1 + yo2) / 2;
            	//newY2 = newY1;
                break;
            case REL_RIGHT:
            	newX2 = nodeTo.getX1(); newX1 = nodeFrom.getX2();
                
            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                newY1 = Utils.minimum(nodeFrom.getY1(), nodeTo.getY1());
                newY2 = Utils.maximum(nodeFrom.getY2(), nodeTo.getY2());
                //newY1 = (yo1 + yo2) / 2;
            	//newY2 = newY1;
                break;
            case REL_ABOVE:
                newY1 = nodeFrom.getY2(); newY2 = nodeTo.getY1();

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                newX1 = Utils.minimum(nodeFrom.getX1(), nodeTo.getX1());
                newX2 = Utils.maximum(nodeFrom.getX2(), nodeTo.getX2());
                //newX1 = (xo1 + xo2) / 2;
                //newX2 = newX1;
                break;
            case REL_BELOW:
            	newY2 = nodeFrom.getY1(); newY1 = nodeTo.getY2();

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                newX1 = Utils.minimum(nodeFrom.getX1(), nodeTo.getX1());
                newX2 = Utils.maximum(nodeFrom.getX2(), nodeTo.getX2());
                //newX1 = (xo1 + xo2) / 2;
                //newX2 = newX1;
                break;
            default:
            	//System.out.println("whoops!");
                newX1 = -1; newX2 = -1; newY1 = -1; newY2 = -1;
        }
    	return new GenericSegment(newX1, newX2, newY1, newY2);
    }
    
    public float getFontSize()
    {
        // TODO: exception or -1 returned if a horizontal edge?
        if (!(nodeFrom instanceof TextSegment) || 
            !(nodeTo instanceof TextSegment)) return -1.0f;
        
        TextSegment nFrom = (TextSegment)nodeFrom;
        TextSegment nTo = (TextSegment)nodeTo;
        
        return (nFrom.getFontSize() + nTo.getFontSize()) / 2;
    }
    
    /**
     * @return Returns the direction.
     */
    public int getDirection() {
        return direction;
    }
    
    /**
     * @param direction The direction to set.
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public String toString()
    {
    	String direction_text = "?";
    	if (direction == REL_ABOVE) direction_text = "above";
    	if (direction == REL_BELOW) direction_text = "below";
    	if (direction == REL_LEFT) direction_text = "left";
    	if (direction == REL_RIGHT) direction_text = "right";
        return ("AdjacencyEdge:  Direction: " + direction_text + "\n NodeFrom: " + nodeFrom + "\nNodeTo: " + nodeTo + "direction: " + direction + "\n");
    }
    
    public Line2D toLine2D () 
    {
    	if (direction==REL_RIGHT) {
    		return new Line2D.Double(
        			nodeFrom.getX2(), 
        			nodeFrom.getYmid(), 
        			nodeTo.getX1(), 
        			nodeTo.getYmid());
    	} else if (direction==REL_BELOW) {
    		return new Line2D.Double(
        			nodeFrom.getXmid(), 
        			nodeFrom.getY2(), 
        			nodeTo.getXmid(), 
        			nodeTo.getY1());
    	}
    	else if (direction==REL_LEFT) {
    		return new Line2D.Double(
    				nodeTo.getX1(), 
        			nodeTo.getYmid(),
        			nodeFrom.getX2(), 
        			nodeFrom.getYmid());
    	}
    	else if (direction==REL_ABOVE) {
    		return new Line2D.Double(
    				nodeTo.getXmid(), 
        			nodeTo.getY1(),
        			nodeFrom.getXmid(), 
        			nodeFrom.getY2());
    	}
    	return null;
    }
    
    public Rectangle2D toBox () 
    {
    	float minX = Math.min(nodeFrom.getX1(), nodeTo.getX1());
    	float maxX = Math.max(nodeFrom.getX2(), nodeTo.getX2());
    	float minY = Math.min(nodeFrom.getY1(), nodeTo.getY1());
    	float maxY = Math.max(nodeFrom.getY2(), nodeTo.getY2());

    	return new Rectangle2D.Double(
    			minX,
    			minY,
    			Math.abs(maxX - minX), 
    			Math.abs(maxY-minY));
    }
    
	public boolean isRuled()
	{
		return ruled;
	}

	public void setRuled(boolean ruled)
	{
		this.ruled = ruled;
	}

}
