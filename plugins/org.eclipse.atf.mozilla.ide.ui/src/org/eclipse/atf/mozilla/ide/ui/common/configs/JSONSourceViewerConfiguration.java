/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.common.configs;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Source viewer configuration for JSON formatting
 * the viewer to use JSON highlighting.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class JSONSourceViewerConfiguration extends SourceViewerConfiguration {

	private JSONRuleScanner scanner = null;
	
	private static Color DEFAULT_TAG_COLOR = new Color (Display.getCurrent(), new RGB (0, 128, 0));
	
	protected JSONRuleScanner getTagScanner () {
		if (scanner == null) {
			scanner = new JSONRuleScanner();
		    scanner.setDefaultReturnToken(
		    	new Token(
		    		new TextAttribute(
		    		DEFAULT_TAG_COLOR)));
		    }
		return scanner;
	}
		
   public IPresentationReconciler getPresentationReconciler (ISourceViewer sourceView) {
	    FontData fontData = new FontData("Courier New", 9, SWT.NORMAL);
	    Font font = new Font(sourceView.getTextWidget().getDisplay(), fontData);
	   	sourceView.getTextWidget().setFont(font);
	    PresentationReconciler reconciler = new PresentationReconciler();
	  	DefaultDamagerRepairer dr = new DefaultDamagerRepairer (getTagScanner());
	  	reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  	reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  	return reconciler;
   }
}
