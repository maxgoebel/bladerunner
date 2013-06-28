package at.ac.tuwien.dbai.pdfwrap.gui;

import at.ac.tuwien.dbai.pdfwrap.utils.Utils;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.graph.AdjacencyEdge;


/**
 * 
 * This represents an edge for the XMillum view
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 *
 */
public class EdgeSegment extends GenericSegment {
	
        
    public EdgeSegment(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
    }
    
    public EdgeSegment()
    {
        super();
        
    }
    
    public EdgeSegment(AdjacencyEdge<? extends GenericSegment> e)
    {
    	super();
    	float newX1, newX2, newY1, newY2, xo1, xo2, yo1, yo2;
    	//System.out.println("direction: " + direction);
    	switch(e.getDirection())
        {
            case AdjacencyEdge.REL_LEFT:
                newX1 = e.getNodeTo().getX2(); newX2 = e.getNodeFrom().getX1();

            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                yo1 = Utils.maximum(e.getNodeFrom().getY1(), e.getNodeTo().getY1());
                yo2 = Utils.minimum(e.getNodeFrom().getY2(), e.getNodeTo().getY2());
                newY1 = (yo1 + yo2) / 2;
            	newY2 = newY1;
                break;
            case AdjacencyEdge.REL_RIGHT:
            	newX2 = e.getNodeTo().getX1(); newX1 = e.getNodeFrom().getX2();
                
            	// newY1 = (nodeFrom.getYcen() + nodeTo.getYcen()) / 2;
                // find overlap coordinates yo1, yo2:
                yo1 = Utils.maximum(e.getNodeFrom().getY1(), e.getNodeTo().getY1());
                yo2 = Utils.minimum(e.getNodeFrom().getY2(), e.getNodeTo().getY2());
                newY1 = (yo1 + yo2) / 2;
            	newY2 = newY1;
                break;
            case AdjacencyEdge.REL_ABOVE:
                newY1 = e.getNodeFrom().getY2(); newY2 = e.getNodeTo().getY1();

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                xo1 = Utils.maximum(e.getNodeFrom().getX1(), e.getNodeTo().getX1());
                xo2 = Utils.minimum(e.getNodeFrom().getX2(), e.getNodeTo().getX2());
                newX1 = (xo1 + xo2) / 2;
                newX2 = newX1;
                break;
            case AdjacencyEdge.REL_BELOW:
            	newY2 = e.getNodeFrom().getY1(); newY1 = e.getNodeTo().getY2();

                // newX1 = (nodeFrom.getXcen() + nodeTo.getXcen()) / 2;
                // find overlap coordinates xo1, xo2:
                xo1 = Utils.maximum(e.getNodeFrom().getX1(), e.getNodeTo().getX1());
                xo2 = Utils.minimum(e.getNodeFrom().getX2(), e.getNodeTo().getX2());
                newX1 = (xo1 + xo2) / 2;
                newX2 = newX1;
                break;
            default:
            	//System.out.println("whoops!");
                newX1 = -1; newX2 = -1; newY1 = -1; newY2 = -1;
        }
//    	return new EdgeSegment(newX1, newX2, newY1, newY2);
    }
}
