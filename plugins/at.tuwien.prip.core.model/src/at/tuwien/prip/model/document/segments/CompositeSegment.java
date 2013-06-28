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

// import java.util.Collection;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Segment which contains other sub-segments; base class
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class CompositeSegment<T extends GenericSegment> extends TextSegment 
implements Cloneable
{

	protected List<T> items;

	/**
	 * Constructor.
	 * 
	 * @param x1
	 *            The x1 coordinate of the segment.
	 * @param x2
	 *            The x2 coordinate of the segment.
	 * @param y1
	 *            The y1 coordinate of the segment.
	 * @param y2
	 *            The y2 coordinate of the segment.
	 * @param text
	 *            The textual contents of the segment.
	 * @param font
	 *            The (main) font of the segment.
	 * @param fontSize
	 *            The (main) font size in the segment.
	 */
	/* 30.11.06: these constructors appear to be useless*/
	// 1.12.06: but they are used by TextBlock...
	public CompositeSegment(float x1, float x2, float y1, float y2,
			String text, String fontName, float fontSize, Color color)
	{
		super(x1, x2, y1, y2, text, fontName, fontSize, null);
		this.items = new ArrayList<T>();
	}
	

	public CompositeSegment(float x1, float x2, float y1, float y2)
	{
		super(x1, x2, y1, y2);
		this.items = new ArrayList<T>();
	}

	public CompositeSegment(float x1, float x2, float y1, float y2,
			String text, String fontName, float fontSize, List<T> items, Color color)
	{
		super(x1, x2, y1, y2, text, fontName, fontSize, color);
		this.items = items;
	}

	public CompositeSegment(float x1, float x2, float y1, float y2,
			List<T> items)
	{
		super(x1, x2, y1, y2);
		this.items = items;
	}

	public CompositeSegment(List<T> items)
	{
		super();
		this.items = items;
	}

	public CompositeSegment()
	{
		// most common method if initialization now
		// the fields are filled once all the items have
		// been added...
		super();
		this.items = new ArrayList<T>();
	}

	public List<T> getItems() {
		return items;
	}


	public void setItems(List<T> items) {
		this.items = items;
	}


	/**
	 * @return Returns a clone of this segment, i.e.
	 * the co-ordinates and other attributes and a
	 * _shallowly cloned_ list sub-objects
	 */
	@SuppressWarnings("unchecked")
	public GenericSegment clone()
	{
		CompositeSegment<T> retVal = (CompositeSegment<T>) super.clone();
		
		// 2011-01-24 List is not cloneable
		List<T> cloneList = new ArrayList<T>();
		cloneList.addAll(this.items);
		
		retVal.items = cloneList;
		
		return retVal;
	}

	// overrides super with number of items
	public String toString()
	{
		return generateSegmentName() + " no. items: " + items.size() + " - " + getAttributes(); 
	}
	
	public void printSubItems()
    {
		System.out.println(this);
		printSubItems(0);
    }
	
	public void printSubItems(int indent)
    {
//    	System.out.println(this);
    	for (GenericSegment gs : items)
    	{
    		for (int n = 0; n < indent; n ++)
				System.out.print("    ");
    		System.out.println(gs);
    		if (gs instanceof CompositeSegment<?>)
    		{
    			CompositeSegment<?> cs = (CompositeSegment<?>)gs;
    			cs.printSubItems(indent + 1);
    		}
    	}
    }
	
	/**
	 * returns string representation including sub-items
	 */
	public String toExtendedString()
	{
		StringBuffer sb = new StringBuffer(toString() + "\nSub-items:\n");
		for (GenericSegment gs : items)
		{
			sb.append(gs.toString() + "\n");
		}
		sb.append("\n");
		
		return(sb.toString());
	}
	
	/**
	 * 
	 */
	public void setCalculatedFields()
	{
		findBoundingBox();
		findText();
		findFontName();
		findFontSize();
	}
	
	/**
	 * 
	 * @param ts
	 */
	public void setCalculatedFields(TextSegment ts)
	{
		this.setBoundingBox(ts.getBoundingBox());

		this.setText(ts.getText());
		this.setFontName(ts.getFontName());
		this.setFontSize(ts.getFontSize());
	}
		
	/**
	 * 	// TODO: remove following method? or move to utils? count mode etc.
	 */
	public void findFontName()
	{
		HashMap<String,Integer> fontHash = new HashMap<String,Integer>();
		ArrayList<String> numList = new ArrayList<String>(items.size() + 1);
		for (int n = 0; n < (items.size() + 1); n ++)
		{
			//numList.add(new GenericSegment(0, 0, 0, 0));
			numList.add(null);
			// dummy value; null doesn't work (doesn't increase the size)
		}
		
		for (GenericSegment thisSegment : items)
		{
			if (thisSegment instanceof TextSegment &&
				!(thisSegment instanceof IBlankSegment))
			{
				TextSegment thisTextSegment = (TextSegment)thisSegment;
				if (fontHash.containsKey(thisTextSegment.getFontName()))
				{
					int count = fontHash.get(thisTextSegment.getFontName());
					count ++;
					fontHash.put(thisTextSegment.getFontName(), new Integer(count));
					
					if (numList.get(count) == null)//(numList.get(count) instanceof GenericSegment)
						numList.set(count, thisTextSegment.getFontName());
				}
				else
				{
					fontHash.put(thisTextSegment.getFontName(), new Integer(1));
					if (numList.get(1) == null)//(numList.get(1) instanceof GenericSegment)
						numList.set(1, thisTextSegment.getFontName());
				}
			}
		}
		// loop through items in the hash and find the modal frequency
		Collection<Integer> counts = fontHash.values();
		int maxCount = 0;
		Iterator<Integer> cIter = counts.iterator();
		while(cIter.hasNext())
		{
			Integer countObj = (Integer)cIter.next();
			int count = countObj.intValue();
			if (count > maxCount)
				maxCount = count;
		}
		
		// now find the first font object with this frequency and set that as the font
		if (maxCount > 0) // otherwise no font!
		{
			this.fontName = (String)numList.get(maxCount);
		}
	}
	
	/**
	 * 
	 */
	public void findBoundingBox()
	{
		boolean first = true;

		int noItems = 0;
		double fontSizeTotal = 0.0;

		for (GenericSegment thisSegment : items)
		{
			if (!(thisSegment instanceof IBlankSegment))
			{
				if (thisSegment instanceof TextSegment)
				{
					noItems++;
					fontSizeTotal += ((TextSegment) thisSegment).getFontSize();
				}
	
				if (first)
				{
					x1 = thisSegment.getX1();
					x2 = thisSegment.getX2();
					y1 = thisSegment.getY1();
					y2 = thisSegment.getY2();
					first = false;
				} else
				{
					growBoundingBox(thisSegment);
				}
			}
		}
		if (noItems >= 0)
		{
			fontSize = (float) (fontSizeTotal / noItems);
		} else
		{
			fontSize = -1.0f;
		}
	}

	/**
	 * 
	 */
	public void findText()
	{
		text = "";
		
		for (GenericSegment gs : items)
		{
			if (gs instanceof TextLine) 
			{
				TextSegment ts = (TextSegment)gs;
				// add CR between line objects
				if (text.length()>0) {
					text += "\n";
				}
				text += ts.getText();
			} 
			else if (gs instanceof TextSegment)
			{
				TextSegment ts = (TextSegment)gs;
				// add space between line objects
				if (text.length()>0) 
				{
					text += " ";
				}
				text += ts.getText();
				
			}
		}
	}

	/**
	 * 
	 */
	// TODO: replace in TextBlock to count modal size
	// at character level
	public void findFontSize()
	{
		// what if no text seg?  avg is screwed up...
		int size = getItems().size();
		float sum = 0;
		for (GenericSegment gs : items)
		{
			if (gs instanceof TextSegment)
			{
				TextSegment ts = (TextSegment)gs;
				sum += ts.getFontSize();
			}
		}
		setFontSize(sum/size);
	}
	
}//CompositeSegment
