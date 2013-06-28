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
package org.eclipse.atf.mozilla.ide.ui.netmon.payload;

import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * This implementation provides no SourceViewerConfiguration
 * 
 * @author Gino Bustelo
 *
 */
public class SimpleTextPayloadRenderStrategy implements ITextPayloadRenderStrategy {

	public static final String RENDER_TYPE = "TEXT";
	
	final public String getRenderType() {
		return RENDER_TYPE;
	}

	/**
	 * Has no configuration
	 */
	public SourceViewerConfiguration getConfiguration() {
		return null;
	}
	
	

}