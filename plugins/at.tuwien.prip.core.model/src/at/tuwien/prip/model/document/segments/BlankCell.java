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



/**
 * Blank table cell document element
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class BlankCell extends TableCell implements IBlankSegment
{
    
    /**
     * Constructor.
     */
    public BlankCell(int colspan, int rowspan)
    {
		this.setColspan(colspan);
		this.setRowspan(rowspan);
	}
    
    public String getText()
    {
    	//return ("<blank" + colspan +"," + rowspan + ">");
    	return ("");//"<blank>");
    }
    
    public String toString()
    {
    	return "BlankCell";
    }
}
