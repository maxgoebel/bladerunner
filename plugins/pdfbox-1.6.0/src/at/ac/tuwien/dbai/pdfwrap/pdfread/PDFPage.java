package at.ac.tuwien.dbai.pdfwrap.pdfread;

import java.util.Iterator;
import java.util.List;

import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.TextSegment;


/**
 * Element to represent the intermediary page, after
 * the objects have been extracted via PDFObjectExtractor
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class PDFPage<T extends GenericSegment> extends CompositeSegment<T>
{
	int rotation = 0;
	
	/**
	 * Constructor.
	 * 
	 */
    public PDFPage()
    {
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param items
     */
    public PDFPage(List<T> items)
    {
        super(items);
    }
    
    /**
     * Constructor.
     * 
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    public PDFPage(
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
     * 
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @param items
     */
    public PDFPage(
        float x1,
        float x2,
        float y1,
        float y2,
		List<T> items
        )
    {
		super(x1, x2, y1, y2, items);
    }
    
    public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void reverseYCoordinatesPDF()
	// pre: Y co-ordinates represent those of page bounding box
	{
/////		ErrorDump.debug(this, "page is now: " + this);
		Iterator<T> nIter = items.iterator();
		while (nIter.hasNext())
		{
			T thisSegment = (T) nIter.next();
			//ErrorDump.debug(this, "this segment: " + thisSegment);
			
			if (thisSegment instanceof TextSegment)
			{
				float currentY1 = thisSegment.getY1();
				float height = thisSegment.getHeight();
				float reversedY1 = super.getHeight() - currentY1;
				//float reversedY1 = super.getSegHeight() - currentY1 - super.getY1();
				thisSegment.setY1(reversedY1);
				thisSegment.setY2(reversedY1 + height);
			}
			else
			{
				float currentY1 = thisSegment.getY1();
				float currentY2 = thisSegment.getY2();
				thisSegment.setY1(super.getHeight() - currentY2);
				//thisSegment.setY1(super.getSegHeight() - currentY2 - super.getY1());
				thisSegment.setY2(super.getHeight() - currentY1);
				//thisSegment.setY2(super.getSegHeight() - currentY1 - super.getY1());
			}
		}
	}
	
	public void reverseYCoordinatesPNG()
	// pre: Y co-ordinates represent those of page bounding box
	{
/////		ErrorDump.debug(this, "page is now: " + this);
		Iterator<T> nIter = items.iterator();
		while (nIter.hasNext())
		{
			T thisSegment = nIter.next();
			//ErrorDump.debug(this, "this segment: " + thisSegment);
			
			float currentY1 = thisSegment.getY1();
			float currentY2 = thisSegment.getY2();
			thisSegment.setY1(super.getHeight() - currentY2);
			//thisSegment.setY1(super.getSegHeight() - currentY2 - super.getY1());
			thisSegment.setY2(super.getHeight() - currentY1);
			//thisSegment.setY2(super.getSegHeight() - currentY1 - super.getY1());
		}
	}
	
	/**
	 * for use when pageRotation == 270 || pageRotation == -90
	 */
	public void reverseXCoordinates()
	// pre: X co-ordinates represent those of page bounding box
	{
		Iterator<T> nIter = items.iterator();
		while (nIter.hasNext())
		{
			// ErrorDump.debug(this, "y: " + currentY1 + "total height: " + height
			// + "new y: " + )
			T thisSegment = nIter.next();
			//ErrorDump.debug(this, "this segment: " + thisSegment);
			float currentX1 = thisSegment.getX1();
			float width = thisSegment.getWidth();
			float reversedX1 = super.getWidth() - currentX1;
			thisSegment.setX1(reversedX1);
			thisSegment.setX2(reversedX1 + width);
		}
	}
	
	/**
	 * translates all coordinates so that the page's (x1, y1) is at (0, 0)
	 */
	public void normalizeCoordinates()
	{
		Iterator<T> nIter = items.iterator();
		while (nIter.hasNext())
		{
			T thisSegment = nIter.next();
			thisSegment.setX1(thisSegment.getX1() - super.getX1());
			thisSegment.setX2(thisSegment.getX2() - super.getX1());
			thisSegment.setY1(thisSegment.getY1() - super.getY1());
			thisSegment.setY2(thisSegment.getY2() - super.getY1());
		}
		// and the page itself...
		super.setX2(super.getX2() - super.getX1());
		super.setY2(super.getY2() - super.getY1());
		super.setX1(0); super.setY1(0);
	}
}
