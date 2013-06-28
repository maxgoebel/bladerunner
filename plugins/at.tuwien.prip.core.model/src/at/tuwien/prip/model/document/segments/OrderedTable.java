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
package at.tuwien.prip.model.document.segments;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Table document element
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class OrderedTable extends CompositeSegment<CompositeSegment<TableCell>> 
implements IXHTMLSegment
{
//	List<CompositeSegment> items;
   
    public OrderedTable(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
    }
    
    public OrderedTable(
        float x1,
        float x2,
        float y1,
        float y2,
		List<CompositeSegment<TableCell>> items
        )
    {
//		super(x1, x2, y1, y2, items);
    	super(x1, x2, y1, y2);
    	this.items = items;
    }
    
    public OrderedTable(List<CompositeSegment<TableCell>> items)
    {
//      super(items);
    	super();
    	this.items = items;
    }
    
    public OrderedTable(
            )
    {
        super();
    }
    
    public void addAsXHTML(Document resultDocument, Element parent)//, GenericSegment pageDim)
    {
        Element newTableElement = resultDocument.createElement("table");
        
        Iterator<CompositeSegment<TableCell>> rowIter = items.iterator();
        while(rowIter.hasNext())
        {
            CompositeSegment<TableCell> thisRow = rowIter.next();
            Iterator<TableCell> colIter = thisRow.getItems().iterator();
            
            Element newRowElement = resultDocument.createElement("tr");
            
            //System.out.println("in rowiter with " + thisRow.toExtendedString());
            
            while(colIter.hasNext())
            {
                TableCell thisCell = 
                    (TableCell)colIter.next();
                Element newColumnElement = resultDocument.createElement("td");
                
                //System.out.println("in colIter with: " + thisCell);
                
                if (thisCell.getColspan() > 1) {
                	newColumnElement.setAttribute
                    ("colspan", Integer.toString(thisCell.getColspan()));
                }
                
                if (thisCell.getRowspan() > 1) {
                	newColumnElement.setAttribute
                    ("rowspan", Integer.toString(thisCell.getRowspan()));
                }
                
                // this bit added 22.11.06
                // to replace every occurrence of "\n" in the string
                // with a <br/> tag.
                
                // TODO: refactor and move to e.g. Utils method
                // so that it can be used for other segments/elements
                // containing text.
                
                String theText = thisCell.getText();
                // the following lines would just add the string
                // without <br/>s
                //newColumnElement.appendChild
    			//(resultDocument.createTextNode(theText));
                String textSection = new String();
                
                for (int n = 0; n < theText.length(); n ++)
                {
                	String thisChar = theText.substring(n, n + 1);
                	if (thisChar.equals("\n"))
                	{
                		newColumnElement.appendChild
                			(resultDocument.createTextNode(textSection));
                        newColumnElement.appendChild
                        	(resultDocument.createElement("br"));
                        textSection = "";
                	}
                	else
                	{
                		textSection = textSection.concat(thisChar);
                	}
                }
                
                if (textSection.length() > 0)
                	newColumnElement.appendChild
        			(resultDocument.createTextNode(textSection));
                
                newRowElement.appendChild(newColumnElement);
            }
            newTableElement.appendChild(newRowElement);
        }
        
        parent.appendChild(newTableElement);
    }
    
    public void addAsJoinVision(Document resultDocument, Element parent, GenericSegment pageDim, float resolution)
    {
    	System.out.println("in table join vision");
        Element newTableElement = resultDocument.createElement("table");
        this.setElementAttributes(resultDocument, newTableElement, pageDim, resolution);
        
        Iterator<CompositeSegment<TableCell>> rowIter = items.iterator();
        while(rowIter.hasNext())
        {
            CompositeSegment<TableCell> thisRow = rowIter.next();
            Iterator<TableCell> colIter = thisRow.getItems().iterator();
            
            Element newRowElement = resultDocument.createElement("tr");
            
            //System.out.println("in rowiter with " + thisRow.toExtendedString());
            
            while(colIter.hasNext())
            {
                TableCell thisCell = 
                    (TableCell)colIter.next();
                Element newColumnElement = resultDocument.createElement("td");
                thisCell.setElementAttributes(resultDocument, newColumnElement, pageDim, resolution);
                System.out.println("thisCell: " + thisCell.toExtendedString());
                
                //System.out.println("in colIter with: " + thisCell);
                
                if (thisCell.getColspan() > 1) {
                	newColumnElement.setAttribute
                    ("colspan", Integer.toString(thisCell.getColspan()));
                }
                
                if (thisCell.getRowspan() > 1) {
                	newColumnElement.setAttribute
                    ("rowspan", Integer.toString(thisCell.getRowspan()));
                }
                
                /*
                
                // this bit added 22.11.06
                // to replace every occurrence of "\n" in the string
                // with a <br/> tag.
                
                // TODO: refactor and move to e.g. Utils method
                // so that it can be used for other segments/elements
                // containing text.
                
                String theText = thisCell.getText();
                // the following lines would just add the string
                // without <br/>s
                //newColumnElement.appendChild
    			//(resultDocument.createTextNode(theText));
                String textSection = new String();
                
                for (int n = 0; n < theText.length(); n ++)
                {
                	String thisChar = theText.substring(n, n + 1);
                	if (thisChar.equals("\n"))
                	{
                		newColumnElement.appendChild
                			(resultDocument.createTextNode(textSection));
                        newColumnElement.appendChild
                        	(resultDocument.createElement("br"));
                        textSection = "";
                	}
                	else
                	{
                		textSection = textSection.concat(thisChar);
                	}
                }
                
                if (textSection.length() > 0)
                	newColumnElement.appendChild
        			(resultDocument.createTextNode(textSection));
                */
                
                newRowElement.appendChild(newColumnElement);
            }
            newTableElement.appendChild(newRowElement);
        }
        
        parent.appendChild(newTableElement);
    }
    
    @SuppressWarnings("unchecked")
	public void addAsXmillum(Document resultDocument, Element parent, 
    		GenericSegment pageDim, float resolution)
    {
        // first add the whole table object
    	Element newSegmentElement = resultDocument.createElement(generateSegmentName());
    	this.setElementAttributes(resultDocument, newSegmentElement, pageDim, resolution);
    	parent.appendChild(newSegmentElement);
    	
    	// then add the individual cells
        Iterator<CompositeSegment<TableCell>> rowIter = items.iterator();
        while(rowIter.hasNext())
        {
        	Object o = rowIter.next();
        	if (o instanceof CompositeSegment)
        	{
	            //CompoundTextSegment thisRow =
	            //    (CompoundTextSegment)rowIter.next();
        		CompositeSegment<TableCell> thisRow = (CompositeSegment<TableCell>)o;
	            Iterator<TableCell> colIter = thisRow.getItems().iterator();
	            
	            while(colIter.hasNext())
	            {   
	                TableCell thisCell = 
	                    (TableCell)colIter.next();
	                thisCell.addAsXmillum(resultDocument, parent, pageDim, resolution);
	                
	            }
        	}
        }
    }
    
    public String toCSV()  //TODO: add span support
    {
        StringBuffer retVal = new StringBuffer();
        Iterator<CompositeSegment<TableCell>> rowIter = items.iterator();
        while(rowIter.hasNext())
        {
        	CompositeSegment<TableCell> thisRow =
                (CompositeSegment<TableCell>)rowIter.next();
            Iterator<TableCell> colIter = thisRow.getItems().iterator();
            while(colIter.hasNext())
            {
                // TODO: think about a TableCell object?
                TextSegment thisCell = 
                    (TextSegment)colIter.next();
                if (colIter.hasNext())
                    retVal.append("\"" + thisCell.getText() + "\",");
                else
                    retVal.append("\"" + thisCell.getText() + "\"\n");
            }
        }
        return retVal.toString();
    }
    
    public String toString()
    {
        // TODO: improve presentation
        StringBuffer retVal = new StringBuffer();
        retVal.append("\n****Table: - " + super.getAttributes() + "\n");
        Iterator<CompositeSegment<TableCell>> rowIter = items.iterator();
        while(rowIter.hasNext())
        {
        	Object o = rowIter.next();
        	if (o instanceof CompositeSegment)
        	{
        		// 30.11.06  TODO: separate variable for
        		// rows and other objects within table
        		// (e.g. ruling lines).
	            //CompoundTextSegment thisRow =
	            //    (CompoundTextSegment)rowIter.next();
        		@SuppressWarnings("unchecked")
				CompositeSegment<TableCell> thisRow = (CompositeSegment<TableCell>) o;
        		retVal.append(thisRow.getItems().size() + "/ ");
	            Iterator<TableCell> colIter = thisRow.getItems().iterator();
	            while(colIter.hasNext())
	            {
	                // TODO: think about a TableCell object?
	                TextSegment thisCell = (TextSegment) colIter.next();
	                if (colIter.hasNext())
	                    retVal.append(thisCell.getText() + " | ");
	                else
	                    retVal.append(thisCell.getText() + "\n");
	            }
        	}
        }
        return retVal.toString();
    }
    

    /*
     * returns the cells in the table as a flat list...
     */
    
/*  2011-01-24 reincarnate these methods or not?
    public List<TableCellY getCells()
    {
    	List <TableCell> retVal = new List<TableCell>();
       
        Iterator rowIter = items.iterator();
        while(rowIter.hasNext())
        {
        	Object o = rowIter.next();
        	if (o instanceof CompositeSegment)
        	{
            //CompoundTextSegment thisRow =
             //   (CompoundTextSegment)rowIter.next();
    		CompositeSegment thisRow =
                (CompositeSegment)o;
            Iterator colIter = thisRow.getItems().iterator();
            while(colIter.hasNext())
            {
                // TODO: think about a TableCell object?
            	// check if the object at this point is a TableCell?
            	// (it should be...)
                retVal.add((TableCell)colIter.next());
            }
        	}
        }
        return retVal;
    }
    
    public List jvGetCellItems()
    {
    	ArrayList retVal = new ArrayList();
       
        Iterator rowIter = items.iterator();
        while(rowIter.hasNext())
        {
        	Object o = rowIter.next();
        	if (o instanceof CompositeSegment)
        	{
            //CompoundTextSegment thisRow =
             //   (CompoundTextSegment)rowIter.next();
    		CompositeSegment thisRow =
                (CompositeSegment)o;
            Iterator colIter = thisRow.getItems().iterator();
            while(colIter.hasNext())
            {
                // TODO: think about a TableCell object?
            	// check if the object at this point is a TableCell?
            	// (it should be...)
            	TableCell thisCell = (TableCell)colIter.next();
                retVal.addAll(thisCell.getItems());
            }
        	}
        }
        return retVal;
    }
*/

    public void findBoundingBox()
    {
    	for(CompositeSegment<TableCell> row : items)
    	{
    		row.findBoundingBox();
//    		Iterator<TableCell> iter2 = row.getItems().iterator();
//    		while(iter2.hasNext())
//    		{
//    			TableCell cell = iter2.next();
//    			cell.findBoundingBox();
//    		}
    	}
    	super.findBoundingBox();
    }
}
