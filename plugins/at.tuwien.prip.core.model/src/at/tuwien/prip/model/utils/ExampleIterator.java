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
package at.tuwien.prip.model.utils;

import java.util.Collections;
import java.util.Iterator;

import at.tuwien.prip.model.learning.example.IExample;
import at.tuwien.prip.model.project.document.WrapperDocument;
import at.tuwien.prip.model.project.document.WrapperDocumentCollection;


/**
 * Iterates over all examples in an example document
 *
 * @author ceresna
 */
public class ExampleIterator implements Iterator<IExample> 
{

    private Iterator<WrapperDocument> docIt;
    private Iterator<IExample> userIt;
//    private Iterator<SelectionExample> annotIt;

    private IExample next;

    public ExampleIterator(WrapperDocumentCollection benchCol) {
        this.docIt = benchCol.iterator();
        next = findNext();
    }

    public ExampleIterator(WrapperDocument edoc) {
        this.docIt = Collections.singletonList(edoc).iterator();
        next = findNext();
    }

    public boolean hasNext() {
        return next!=null;
    }

    public IExample next() {
    	IExample curr = next;
        next = findNext();

        return curr;
    }

    /**
     * 
     * @return
     */
    private IExample findNext() 
    {
        if (userIt!=null && userIt.hasNext()) {
            return userIt.next();
        }

        if (docIt.hasNext()) 
        {
        	WrapperDocument doc = docIt.next();    
        	
        	if (doc instanceof WrapperDocument) {
        		userIt = ((WrapperDocument)doc).getExamples().iterator();
        	} 
//        	else if (doc instanceof BenchmarkItem) {
//        		userIt = ExampleUtils.getExamplesFromAnnotation(
//        				(BenchmarkDocument)de,
//        				EAnnotationType.SELECTION_LITERAL).iterator();
//        	}

        	IExample ne = findNext();
            if (ne!=null) return ne;
        }

        return null;
    }

    public void remove() {
        throw new RuntimeException();
    }

}
