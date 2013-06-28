package at.ac.tuwien.dbai.pdfwrap.utils;

//import CandidateTable;
//import TableColumn;
//import TableRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.ImageSegment;
import at.tuwien.prip.model.document.segments.LineSegment;
import at.tuwien.prip.model.document.segments.OrderedTable;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.document.segments.TextBlock;
import at.tuwien.prip.model.document.segments.TextLine;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.segments.fragments.TextFragment;

/**
 * Static utility methods for lists
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class ListUtils
{
	// adpated from http://briankuhn.com/?p=47
	public static void removeDuplicates(List<?> l) {
	    Set set = new LinkedHashSet();
	    set.addAll(l);
	    l.clear();
	    l.addAll(set);
	}
	
	/*
	public static void test()
	{
		CompositeSegment ts = new CompositeSegment();
		ts.getItems().add(new GenericSegment());
		
		List<GenericSegment> genericSegments = new ArrayList<GenericSegment>();
		List<TextSegment> textSegments = new ArrayList<TextSegment>();
		
		getItemsByClass(genericSegments, TextSegment.class, textSegments);
	}
	*/
	
	public static void getItemsByClass(
			List inputList,
			Class c, List resultList)
	{
		for(Object gs : inputList)
		{
			if (gs.getClass() == c)
			{
				resultList.add(gs);
			}
		}
	}
	
	public static List<CharSegment> getCharacters(List<GenericSegment> l)
	{
		List<CharSegment> retVal = new ArrayList<CharSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == CharSegment.class)
				retVal.add((CharSegment)thisSegment);
		}
		return retVal;
	}
	
	public static List<TextFragment> getTextFragments(List<GenericSegment> l)
	{
		List<TextFragment> retVal = new ArrayList<TextFragment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == TextFragment.class)
				retVal.add((TextFragment)thisSegment);
		}
		return retVal;
	}
	
	public static List<TextSegment> getTextSegments(List<GenericSegment> l)
	{
		List<TextSegment> retVal = new ArrayList<TextSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment instanceof TextSegment)
				retVal.add((TextSegment)thisSegment);
		}
		return retVal;
	}
	
	public static List<TextBlock> getTextBlocks(List<GenericSegment> l)
	{
		List<TextBlock> retVal = new ArrayList<TextBlock>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == TextBlock.class)
				retVal.add((TextBlock)thisSegment);
		}
		return retVal;
	}
	
	public static List<TextLine> getTextLines(List<GenericSegment> l)
	{
		List<TextLine> retVal = new ArrayList<TextLine>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == TextLine.class)
				retVal.add((TextLine)thisSegment);
		}
		return retVal;
	}

	public static List<LineSegment> getLineSegments(List<GenericSegment> l)
	{
		List<LineSegment> retVal = new ArrayList<LineSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == LineSegment.class)
				retVal.add((LineSegment)thisSegment);
		}
		return retVal;
	}
	
	public static List<LineSegment> getHorizLineSegments(List<GenericSegment> l)
	{
		List<LineSegment> retVal = new ArrayList<LineSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == LineSegment.class &&
				((LineSegment)thisSegment).getDirection() == LineSegment.DIR_HORIZ)
				retVal.add((LineSegment)thisSegment);
		}
		return retVal;
	}
	
	public static List<LineSegment> getVertLineSegments(List<GenericSegment> l)
	{
		List<LineSegment> retVal = new ArrayList<LineSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == LineSegment.class &&
				((LineSegment)thisSegment).getDirection() == LineSegment.DIR_VERT)
				retVal.add((LineSegment)thisSegment);
		}
		return retVal;
	}
	
	public static List<OrderedTable> getOrderedTables(List<GenericSegment> l)
	{
		List<OrderedTable> retVal = new ArrayList<OrderedTable>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == OrderedTable.class)
				retVal.add((OrderedTable)thisSegment);
		}
		return retVal;
	}
	
	/*
	public static List<TableColumn> getTableColumns(List<GenericSegment> l)
	{
		List<TableColumn> retVal = new ArrayList<TableColumn>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == TableColumn.class)
				retVal.add((TableColumn)thisSegment);
		}
		return retVal;
	}
	
	public static List<TableRow> getTableRows(List<GenericSegment> l)
	{
		List<TableRow> retVal = new ArrayList<TableRow>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == TableRow.class)
				retVal.add((TableRow)thisSegment);
		}
		return retVal;
	}
	*/
	
	public static List<RectSegment> getRectSegments(List<GenericSegment> l)
	{
		List<RectSegment> retVal = new ArrayList<RectSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == RectSegment.class)
				retVal.add((RectSegment)thisSegment);
		}
		return retVal;
	}

	public static List<ImageSegment> getImageSegments(List<GenericSegment> l)
	{
		List<ImageSegment> retVal = new ArrayList<ImageSegment>();
		Iterator<GenericSegment> iter = l.iterator();
		while (iter.hasNext())
		{
			GenericSegment thisSegment = iter.next();
			if (thisSegment.getClass() == ImageSegment.class)
				retVal.add((ImageSegment)thisSegment);
		}
		return retVal;
	}
	
    /*
     * note: returns elements that are _fully covered_ by the
     * bBox; partial intersections are NOT returned.
     * Caution: the method within GenericSegment (which is
     * build upon horizIntersect/vertIntersect) returns
     * all intersections, whether full or partial.
     */
    public static List<GenericSegment> getElementsFullyWithinBBox
    	(List<? extends GenericSegment> l, GenericSegment bBox)
    {
    	List<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for(GenericSegment s : l)
    	{
    		if (s.getX1() >= bBox.getX1() &&
    			s.getX2() <= bBox.getX2() &&
    			s.getY1() >= bBox.getY1() &&
    			s.getY2() <= bBox.getY2())
    			retVal.add(s);
    	}
    	return retVal;
    }
    
    
    
    public static List<GenericSegment> getElementsIntersectingBBox
    	(List<? extends GenericSegment> l, GenericSegment bBox)
    {
    	List<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for(GenericSegment s : l)
    	{
    		if (SegmentUtils.intersects(bBox, s))
    			retVal.add(s);
    	}
    	return retVal;
    }
    
    public static List<GenericSegment> getElementsWithCentresWithinBBox
    	(List<? extends GenericSegment> l, GenericSegment bBox)
    {
    	List<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for(GenericSegment s : l)
    	{
    		if (s.getXmid() >= bBox.getX1() &&
    			s.getXmid() <= bBox.getX2() &&
    			s.getYmid() >= bBox.getY1() &&
    			s.getYmid() <= bBox.getY2())
    			retVal.add(s);
    	}
    	return retVal;
    }
    
    public static List<GenericSegment> getElementsWithCentresWithinBBoxOrViceVersa
    	(List<? extends GenericSegment> l, GenericSegment bBox)
    {
    	List<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for(GenericSegment s : l)
    	{
    		if ((s.getXmid() >= bBox.getX1() &&
    			s.getXmid() <= bBox.getX2() &&
    			s.getYmid() >= bBox.getY1() &&
    			s.getYmid() <= bBox.getY2()) ||
    			(bBox.getXmid() >= s.getX1() &&
    			bBox.getXmid() <= s.getX2() &&
    			bBox.getYmid() >= s.getY1() &&
    			bBox.getYmid() <= s.getY2()))
    			retVal.add(s);
    	}
    	return retVal;
    }
    
    public static List<GenericSegment> getElementsFullyWithinBBox
    	(List<? extends GenericSegment> l, float[] bBox)
    {
    	return getElementsFullyWithinBBox(l, new GenericSegment(bBox));
    }
    
    /* GENERIC PROBLEMS -- METHOD MOVED TO ADJACENCYGRAPH
    public static List<AdjacencyEdge<GenericSegment>> 
    	getVertEdges(List<AdjacencyEdge<? extends GenericSegment>> l)
    {
    	List<AdjacencyEdge<GenericSegment>> retVal = 
    		new ArrayList<AdjacencyEdge<GenericSegment>>();
    		
    	for (AdjacencyEdge<? extends GenericSegment> ae : l)
    		if (ae.isVertical())
    			retVal.add((AdjacencyEdge<GenericSegment>) l);
    	
    	return retVal;
    }
    
    public static List<AdjacencyEdge<GenericSegment>> 
	getHorizEdges(List<AdjacencyEdge<? extends GenericSegment>> l)
	{
		List<AdjacencyEdge<GenericSegment>> retVal = 
			new ArrayList<AdjacencyEdge<GenericSegment>>();
			
		for (AdjacencyEdge<? extends GenericSegment> ae : l)
			if (ae.isHorizontal())
				retVal.add((AdjacencyEdge<GenericSegment>) l);
		
		return retVal;
	}
    */
    
    public static void printList(List<?> l)
    {
//    	System.out.println("List: " + l + " items: " + l.size());
    	System.out.println("List with items: " + l.size());
    	for (Object o : l)
    		System.out.println(o);
    }
    
    /*
    public static void printEdgeList(List<? extends AdjacencyEdge> l)
    {
    	System.out.println("Edge list: " + l + " items: " + l.size());
    	for (AdjacencyEdge<? extends GenericSegment> e : l)
    		System.out.println(e);
    }
    */
    
    // TODO: move printItems to CS.extendedString
    public static void printListWithSubItems(List<? extends GenericSegment> l)
    {
    	System.out.println("Segment list: " + l + " items: " + l.size());
    	for (GenericSegment gs : l)
    	{
    		System.out.println(gs);
    		if (gs instanceof CompositeSegment<?>)
    		{
    			CompositeSegment<?> cs = (CompositeSegment<?>)gs;
//    			for (GenericSegment item : cs.getItems())
//    				System.out.println("    " + item);
    			cs.printSubItems(1);
    		}
    	}
    }
    
    public static void printListWithOneLevelSubItems(List<? extends GenericSegment> l)
    {
    	System.out.println("Segment list: " + l + " items: " + l.size());
    	for (GenericSegment gs : l)
    	{
    		System.out.println(gs);
    		if (gs instanceof CompositeSegment<?>)
    		{
    			CompositeSegment<?> cs = (CompositeSegment<?>)gs;
    			for (GenericSegment item : cs.getItems())
    				System.out.println("    " + item);
//    			cs.printSubItems(0);
    		}
    	}
    }
    
    public static List<GenericSegment> shallowCopy(List<? extends GenericSegment> l)
    {
    	ArrayList<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for (GenericSegment gs : l)
    		retVal.add(gs);
    	return retVal;
    }
    
    public static List<GenericSegment> deepCopy(List<? extends GenericSegment> l)
    {
    	ArrayList<GenericSegment> retVal = new ArrayList<GenericSegment>();
    	for (GenericSegment gs : l)
    		retVal.add((GenericSegment)(gs.clone()));
    	return retVal;
    }
    
    // method here to enable apache.ListUtils to be called in an elegant way!
	public static List<GenericSegment> getIntersection(List<GenericSegment> l1, List<GenericSegment> l2)
	{
		List<GenericSegment> retVal = new ArrayList<GenericSegment>();
		List intersect = 
			org.apache.commons.collections.ListUtils.intersection(l1, l2);
		for (Object o : intersect)
		{
			GenericSegment gs = (GenericSegment)o;
			retVal.add(gs);
		}
		return retVal;
	}
}
