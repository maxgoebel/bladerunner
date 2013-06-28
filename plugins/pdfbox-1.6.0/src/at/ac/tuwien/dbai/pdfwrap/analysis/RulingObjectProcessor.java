package at.ac.tuwien.dbai.pdfwrap.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.comparators.ReverseComparator;

import at.ac.tuwien.dbai.pdfwrap.utils.SegmentUtils;
import at.ac.tuwien.dbai.pdfwrap.utils.Utils;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.LineSegment;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.graph.comparators.XComparator;
import at.tuwien.prip.model.graph.comparators.YComparator;


/**
 * Methods to obtain and process ruling lines found on the page
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class RulingObjectProcessor
{
    //AdjacencyGraph ng;
    //EdgeList edges;
	
	protected List<LineSegment> rulingLines;
    
    public RulingObjectProcessor()
        //AdjacencyGraph ng)
    {
		//this.ng = ng;
        //edges = ng.getEdges();
    	rulingLines = new ArrayList<LineSegment>();
    }
    
    // TODO: Interface IRulingObject
    public RulingObjectProcessor(List<? extends GenericSegment> rulingObjects)
    {
    	rulingLines = new ArrayList<LineSegment>();
    	this.addRulingObjects(rulingObjects);
    }
    
    public void addRulingObjects(List<? extends GenericSegment> rulingObjects)
    {
    	Iterator roIter = rulingObjects.iterator();
    	while(roIter.hasNext())
    	{
    		GenericSegment thisObj = (GenericSegment)roIter.next();
    		if (thisObj instanceof LineSegment)
    		{
    			rulingLines.add((LineSegment)thisObj);
    		}
    		else if (thisObj instanceof RectSegment)
    		{
    			rulingLines.addAll(((RectSegment)thisObj).toLines());
    		}
    	}
    }
    
    public void removeDuplicateLines()//(SegmentList theLines)
    {
    	List<LineSegment> theLines = this.getRulingLines();
    	float tolerance = 6.0f;
    	
    	List<LineSegment> hRetVal = new ArrayList<LineSegment>();
    	List<LineSegment> vRetVal = new ArrayList<LineSegment>();
    	List<LineSegment> horizLines = new ArrayList<LineSegment>();
    	List<LineSegment> vertLines = new ArrayList<LineSegment>();
    	
    	Iterator lineIter = theLines.iterator();
    	while(lineIter.hasNext())
    	{
    		LineSegment thisLine = (LineSegment)lineIter.next();
    		if (thisLine.getDirection() == LineSegment.DIR_HORIZ)
    		{
    			horizLines.add(thisLine);
    		}
    		else if (thisLine.getDirection() == LineSegment.DIR_VERT)
    		{
    			vertLines.add(thisLine);
    		}
    		else
    		{
    			// DIR_OTHER... dunno what we would do here
    			// think this would not usually occur in our
    			// application.
    		}
    	}
    	
    	// note: all the Comparators use X1 or Y1
    	// don't think this matters... we wanna avoid
    	// the lines being added in the order:
    	// left, right, middle; this way we would
    	// end up with two separate lines even
    	// if they join...
    	
    	// sort all horizontal lines in x order
    	Collections.sort(horizLines, new XComparator());
    	// and all vertical lines in y order
    	//Collections.sort(vertLines, Collections.reverseOrder(new YComparator()));
    	Collections.sort(vertLines, new ReverseComparator(new YComparator()));
    	
    	// todo: replace all 'tolerance' lines with GenericSegment.getDilatedSegment
    	// method. (this includes the -6.0f / +6.0f as well).
    	
    	Iterator hIter = horizLines.iterator();
    	while(hIter.hasNext())
    	{
    		LineSegment thisLine = (LineSegment)hIter.next();
    		
    		boolean addedToExistingLine = false;
    		for (int n = 0; n < hRetVal.size(); n ++)
    		{
    			LineSegment l = (LineSegment)hRetVal.get(n);
    			if (Utils.within(thisLine.getYmid(), l.getYmid(), tolerance))
    			{
    				if (SegmentUtils.horizIntersect(l, thisLine.getX1() - 6.0f, 
    					thisLine.getX2() + 6.0f))
    				{
    					l.setX1(Utils.minimum(thisLine.getX1(), l.getX1()));
    					l.setX2(Utils.maximum(thisLine.getX2(), l.getX2()));
    					addedToExistingLine = true;
    				}
    			}
    		}
    		if (!addedToExistingLine)
    		{
    			hRetVal.add(thisLine);
    		}
    	}
    	
    	Iterator vIter = vertLines.iterator();
    	while(vIter.hasNext())
    	{
    		LineSegment thisLine = (LineSegment)vIter.next();
    		
    		boolean addedToExistingLine = false;
    		for (int n = 0; n < vRetVal.size(); n ++)
    		{
    			LineSegment l = (LineSegment)vRetVal.get(n);
    			if (Utils.within(thisLine.getXmid(), l.getXmid(), tolerance))
    			{
    				if (SegmentUtils.vertIntersect(l, thisLine.getY1() - 6.0f, 
    					thisLine.getY2() + 6.0f))
    				{
    					l.setY1(Utils.minimum(thisLine.getY1(), l.getY1()));
    					l.setY2(Utils.maximum(thisLine.getY2(), l.getY2()));
    					addedToExistingLine = true;
    				}
    			}
    		}
    		if (!addedToExistingLine)
    		{
    			vRetVal.add(thisLine);
    		}
    	}
    	List<LineSegment> retVal = new ArrayList<LineSegment>();
    	retVal.addAll(hRetVal);
    	retVal.addAll(vRetVal);
    	//return retVal;
    	
    	this.setRulingLines(retVal);
    }
    
    public List<LineSegment> getRulingLines() {
		return rulingLines;
	}

	public void setRulingLines(List<LineSegment> rulingLines) {
		this.rulingLines = rulingLines;
	}
}
