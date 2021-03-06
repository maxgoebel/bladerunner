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
package org.eclipse.atf.mozilla.ide.debug.model;

import java.util.List;

public interface IJSDebugScriptElement {
	
	String getName();
	IJSDebugScriptElement getParent();
	List getChildren();
	String getLocation();
	int getLineStart();
	int getLineTotal();
}
