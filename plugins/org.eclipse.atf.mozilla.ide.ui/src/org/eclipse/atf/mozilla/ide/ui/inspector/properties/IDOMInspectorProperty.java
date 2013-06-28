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

package org.eclipse.atf.mozilla.ide.ui.inspector.properties;

import org.mozilla.interfaces.nsIDOMNode;

/*
 * This interface is used to wrap strategies used to get the value of different
 * types of properties for the selected Dom node.
 */
public interface IDOMInspectorProperty {
	String getDisplayName();
	String getValue( nsIDOMNode node );
}
