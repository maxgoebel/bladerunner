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

import org.eclipse.atf.mozilla.ide.ui.common.configs.CSSViewerConfiguration;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class CSSPayloadRenderStrategy extends SimpleTextPayloadRenderStrategy {

	private SourceViewerConfiguration config = null;

	public SourceViewerConfiguration getConfiguration() {
		if( config == null )
			config = new CSSViewerConfiguration();
		
		return config;
	}
	
	
}
