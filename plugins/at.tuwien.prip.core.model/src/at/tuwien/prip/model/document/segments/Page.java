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

import at.tuwien.prip.model.graph.AdjacencyGraph;


/**
 * Page object
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class Page extends CompositeSegment<GenericSegment> 
implements IXHTMLSegment
{   
    private int pageNo = 0;
    private int rotation = 0;
//  removed 2011-01-24
//  DocumentGraph docGraph;
    
    private AdjacencyGraph<GenericSegment> adjGraph;
    
    /**
     * Constructor.
     */
    public Page()
    {
        super();
    }
    
    /**
     * Constructor.
     * @param items
     */
    public Page(List<GenericSegment> items)
    {
        super(items);
    }
    
    /**
     * Constructor.
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    public Page(
        float x1,
        float x2,
        float y1,
        float y2
        )
    {
		super(x1, x2, y1, y2);
    }
    
    /**
     * Constructor.
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @param items
     */
    public Page(
        float x1,
        float x2,
        float y1,
        float y2,
		List<GenericSegment> items
        )
    {
		super(x1, x2, y1, y2, items);
    }
    
    public void addAsXHTML(Document resultDocument, Element parent)
    {
        Element newPageElement = resultDocument.createElement("h2");
        
        newPageElement.appendChild
            (resultDocument.createTextNode("Page " + pageNo));
        
        parent.appendChild(newPageElement);
        
        for(GenericSegment thisItem : items)
        {
            // eventually, this line should take care of everything here...
            // thisItem.addAsXHTML(resultDocument, parent);
            
            // but for now, enable only for those elements that have
            // it defined...

            if (thisItem instanceof IXHTMLSegment)// && 
//            	thisItem.getClass() != TableColumn.class &&
//            	thisItem.getClass() != TableRow.class)
            {
            	//System.out.println("adding class: " + thisItem.getClass());
            	((IXHTMLSegment)thisItem).addAsXHTML(resultDocument, parent);
            }
        }
    }
    
    public void addAsXmillum(Document resultDocument, Element parent, 
    	GenericSegment pageDim, float resolution)
    {
    	//System.out.println("adding as XML with pageDim: " + pageDim);
        Iterator<GenericSegment> itemIter = items.iterator();
        while(itemIter.hasNext())
        {
            GenericSegment thisItem = (GenericSegment)itemIter.next();
        	thisItem.addAsXmillum(resultDocument, parent, pageDim, resolution);
        }
    }
    
    
    public void addAsJoinVision(Document resultDocument, Element parent, 
    	GenericSegment pageDim, float resolution)
    {
    	this.setElementAttributes(resultDocument, parent, pageDim, resolution);
    	//System.out.println("adding as XML with pageDim: " + pageDim);
        Iterator<GenericSegment> itemIter = items.iterator();
        while(itemIter.hasNext())
        {
            GenericSegment thisItem = (GenericSegment)itemIter.next();
            if (thisItem.getClass() == TextBlock.class)
            	((TextBlock)thisItem).addAsJoinVision(resultDocument, parent, pageDim, resolution);
            else if (thisItem.getClass() == OrderedTable.class)
            	((OrderedTable)thisItem).addAsJoinVision(resultDocument, parent, pageDim, resolution);
            else if (thisItem.getClass() == RectSegment.class)
            	thisItem.addAsXmillum(resultDocument, parent, pageDim, resolution);
            else if (thisItem.getClass() == LineSegment.class)
            	thisItem.addAsXmillum(resultDocument, parent, pageDim, resolution);
            else if (thisItem.getClass() == ImageSegment.class)
            	thisItem.addAsXmillum(resultDocument, parent, pageDim, resolution);
        }
    }
	
    /**
     * 
     * @return
     */
    public AdjacencyGraph<GenericSegment> getAdjGraph() {
		return adjGraph;
	}
    
    /**
     * 
     * @param adjGraph
     */
    public void setAdjGraph(AdjacencyGraph<GenericSegment> adjGraph) {
		this.adjGraph = adjGraph;
	}
    
    /**
     * @return Returns the pageNo.
     */
    public int getPageNo() {
        return pageNo;
    }
    

    /**
     * @param pageNo The pageNo to set.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
}
