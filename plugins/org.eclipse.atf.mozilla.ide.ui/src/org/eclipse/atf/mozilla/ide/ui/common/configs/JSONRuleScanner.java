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

/**
 * Rule scanner for JSON code
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class JSONRuleScanner extends RuleBasedScanner {

	private static Color TAG_ATTRIBUTE_NAME = new Color (Display.getCurrent(), new RGB (128, 0, 0));
	private static Color TAG_ATTRIBUTE_VALUE = new Color (Display.getCurrent(), new RGB (0, 128, 0));
		
    public JSONRuleScanner () {
    	IToken tagAttrNameToken = new Token (new TextAttribute (TAG_ATTRIBUTE_NAME));
    	IToken tagAttrValueToken = new Token (new TextAttribute (TAG_ATTRIBUTE_VALUE));

    	IRule[] rules = new IRule[2];
    	rules[0] = new MultiLineRule ("\"", "\"", tagAttrNameToken);
    	rules[1] = new MultiLineRule (":", " ", tagAttrValueToken);
    	setRules(rules);
   }
    
}
