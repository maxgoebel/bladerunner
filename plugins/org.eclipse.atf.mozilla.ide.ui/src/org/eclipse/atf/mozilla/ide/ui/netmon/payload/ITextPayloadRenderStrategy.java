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
 * This is the Interface for strategies that support text
 * 
 * It allows specifying a SourceViewerConfiguration so that the
 * content is shown formated.
 * 
 * @author Gino Bustelo
 *
 */
public interface ITextPayloadRenderStrategy extends IPayloadRenderStrategy {

	SourceViewerConfiguration getConfiguration();
	
}
