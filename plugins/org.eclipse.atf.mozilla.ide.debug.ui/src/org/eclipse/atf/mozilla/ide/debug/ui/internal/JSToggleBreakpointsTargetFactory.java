/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.debug.ui.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.atf.mozilla.ide.debug.ui.JSLineBreakpointAdapter;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

public class JSToggleBreakpointsTargetFactory implements IToggleBreakpointsTargetFactory {

	private static final String ID = "org.eclipse.atf.mozilla.ide.debug.ui.JStoggleTargetFactory"; //$NON-NLS-1$

	public JSToggleBreakpointsTargetFactory() {
		// anonymous constructor for instantiation from extension point
	}

	public IToggleBreakpointsTarget createToggleTarget(String targetID) {
		return new JSLineBreakpointAdapter();
	}

	public String getDefaultToggleTarget(IWorkbenchPart part, ISelection selection) {
		return null;
	}

	public String getToggleTargetDescription(String targetID) {
		return "JavaScript";
	}

	public String getToggleTargetName(String targetID) {
		return "JavaScript";
	}

	public Set getToggleTargets(IWorkbenchPart part, ISelection selection) {
		return Collections.singleton(ID);
	}

}
