/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.debug.ui;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugElement;
import org.eclipse.atf.mozilla.ide.debug.model.JSBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.ui.util.DebuggerSourceDisplayUtil;
import org.eclipse.atf.mozilla.ide.ui.util.SourceDisplayUtil;
import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**
 * @author Adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MozillaDebugModelPresentation extends LabelProvider implements IDebugModelPresentation {

	private boolean _showTypes = false;

	protected SourceDisplayUtil sourceDisplayUtil = new DebuggerSourceDisplayUtil();

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attribute, Object value) {
		if (value == null) {
			return;
		}
		if (IDebugModelPresentation.DISPLAY_VARIABLE_TYPE_NAMES.equals(attribute))
			_showTypes = ((Boolean) value).booleanValue();

		return;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		//TODO special case variables with flags

		if (element instanceof Breakpoint) {
			Breakpoint breakpoint = (Breakpoint) element;
			element = breakpoint.getMarker();
		}

		if (element instanceof IMarker) {
			IMarker marker = (IMarker) element;
			return DebugUITools.getImage(marker.getAttribute(IBreakpoint.ENABLED, true) ? IDebugUIConstants.IMG_OBJS_BREAKPOINT : IDebugUIConstants.IMG_OBJS_BREAKPOINT_DISABLED);
		}
		return super.getImage(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof JSDebugElement) {
			StringBuffer buf = new StringBuffer();

			if (element instanceof IVariable) {
				// add the type information to the variable label if its 
				// button is pushed in the variables view.
				if (_showTypes) {
					try {
						IVariable variable = (IVariable) element;
						String type = variable.getReferenceTypeName();
						if (type != null) {
							buf.append(type);
							buf.append(" ");
						}
					} catch (DebugException ex) {
						MozillaDebugUIPlugin.log(ex);
					}
				}
			}

			buf.append(((JSDebugElement) element).getLabel());
			return buf.toString();
		}

		if (element instanceof IExpression) {
			IExpression expr = (IExpression) element;
			IValue val = expr.getValue();
			StringBuffer buf = new StringBuffer(expr.getExpressionText());
			if (val != null) {
				buf.append('=');
				buf.append(getText(val));
			}
			return buf.toString();
		}

		if (element instanceof JSBreakpoint) {
			return ((JSBreakpoint) element).getLabel();
		}

		return super.getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener) {
		try {
			String result = value.getValueString();
			listener.detailComputed(value, result);
		} catch (DebugException de) {
			MozillaDebugUIPlugin.log(de);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		return sourceDisplayUtil.getEditorInput(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		return sourceDisplayUtil.getEditorId(input, element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}
}
