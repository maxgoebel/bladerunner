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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HTMLDOMRuleScanner extends RuleBasedScanner {

	private static Color TAG_NAME = new Color (Display.getCurrent(), new RGB (63, 127, 127));
	private static Color TAG_ATTRIBUTE_NAME = new Color (Display.getCurrent(), new RGB (127, 0, 127));
	private static Color TAG_ATTRIBUTE_VALUE = new Color (Display.getCurrent(), new RGB (42, 0, 255));
	private static Color NORMAL_TEXT = new Color (Display.getCurrent(), new RGB (0, 0, 0));
	private static Color COMMENT_TEXT = new Color (Display.getCurrent(), new RGB (42, 0, 255));
		
    public HTMLDOMRuleScanner () {
    	IToken tagToken = new Token (new TextAttribute (TAG_NAME));
    	IToken tagAttrNameToken = new Token (new TextAttribute (TAG_ATTRIBUTE_NAME));
    	IToken tagAttrValueToken = new Token (new TextAttribute (TAG_ATTRIBUTE_VALUE));
    	IToken normalTextToken = new Token (new TextAttribute (NORMAL_TEXT));
    	IToken tagCommentToken = new Token (new TextAttribute (COMMENT_TEXT));

    	IRule[] rules = new IRule[5];
    	rules[0] = new MultiLineRule (">", "<", normalTextToken);
    	rules[1] = new MultiLineRule ("<tagname", " ", tagToken);
    	rules[2] = new MultiLineRule (" ", "=", tagAttrNameToken);
    	rules[3] = new MultiLineRule ("\"", "\"", tagAttrValueToken);
    	rules[4] = new MultiLineRule ("<!--", "-->", tagCommentToken);
    	setRules(rules);
   }
	
}
