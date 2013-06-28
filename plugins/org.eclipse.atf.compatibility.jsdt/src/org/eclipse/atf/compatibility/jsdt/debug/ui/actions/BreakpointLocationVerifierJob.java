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
package org.eclipse.atf.compatibility.jsdt.debug.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.compatibility.jsdt.Activator;
import org.eclipse.atf.compatibility.jsdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;

import com.ibm.icu.text.MessageFormat;

/**
 * Job used to verify the position of a breakpoint
 */
public class BreakpointLocationVerifierJob extends Job {

	/**
	 * The document which contains the code source.
	 */
	private IDocument fDocument;

	/**
	 * The temporary breakpoint that has been set. Can be <code>null</code> if the callee was not able
	 * to check if a breakpoint was already set at this position.
	 */
	private JSLineBreakpoint fBreakpoint;

	/**
	 * The number of the line where the breakpoint has been requested.
	 */
	private int fLineNumber;

	/**
	 * The qualified type name of the class where the temporary breakpoint as been set.
	 * Can be <code>null</code> if fBreakpoint is null.
	 */
	private String fTypeName;

	/**
	 * The type in which should be set the breakpoint.
	 */
	private IJavaScriptElement fType;

	/**
	 * Indicate if the search for a valid location should be limited to a line
	 * or expanded to field and method declaration.
	 */
	private boolean fBestMatch;

	/**
	 * The resource in which should be set the breakpoint.
	 */
	private IResource fResource;

	/**
	 * The current IEditorPart
	 */
	private IEditorPart fEditorPart;

	/**
	 * The status line to use to display errors
	 */
	private IEditorStatusLine fStatusLine;

	public BreakpointLocationVerifierJob(IDocument document, JSLineBreakpoint breakpoint, int lineNumber, boolean bestMatch, String typeName, IJavaScriptElement type, IResource resource, IEditorPart editorPart) {
		super(ActionMessages.BreakpointLocationVerifierJob_breakpoint_location);
		fDocument = document;
		fBreakpoint = breakpoint;
		fLineNumber = lineNumber;
		fBestMatch = bestMatch;
		fTypeName = typeName;
		fType = type;
		fResource = resource;
		fEditorPart = editorPart;
		fStatusLine = (IEditorStatusLine) editorPart.getAdapter(IEditorStatusLine.class);
		setSystem(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		char[] source = fDocument.get().toCharArray();
		parser.setSource(source);
		IJavaScriptElement javaElement = JavaScriptCore.create(fResource);
		IJavaScriptProject project = null;
		if (javaElement != null) {
			Map options = JavaScriptCore.getDefaultOptions();
			project = javaElement.getJavaScriptProject();
			String compilerCompliance = JavaScriptCore.VERSION_1_5;
			String compilerSource = JavaScriptCore.VERSION_1_5;
			if (project != null) {
				compilerCompliance = project.getOption(JavaScriptCore.COMPILER_COMPLIANCE, true);
				compilerSource = project.getOption(JavaScriptCore.COMPILER_SOURCE, true);
			}
			options.put(JavaScriptCore.COMPILER_COMPLIANCE, compilerCompliance);
			options.put(JavaScriptCore.COMPILER_SOURCE, compilerSource);
			parser.setCompilerOptions(options);
		}
		JavaScriptUnit compilationUnit = (JavaScriptUnit) parser.createAST(null);
		ValidBreakpointLocationLocator locator = new ValidBreakpointLocationLocator(compilationUnit, fLineNumber, false, fBestMatch);
		compilationUnit.accept(locator);
		if (locator.isBindingsRequired()) {
			if (javaElement != null) {
				// try again with bindings if required and available
				String unitName = null;
				if (fType == null) {
					String name = fResource.getName();
					if (JavaScriptCore.isJavaScriptLikeFileName(name)) {
						unitName = name;
					}
				} else if (fType instanceof IJavaScriptUnit) {
					unitName = ((IJavaScriptUnit) fType).getElementName();
				} else if (fType instanceof IMember) {
					IMember member = (IMember) fType;
					if (member.isBinary()) {
						String className = member.getClassFile().getElementName();
						int nameLength = className.indexOf('$');
						if (nameLength < 0) {
							nameLength = className.indexOf('.');
						}
						unitName = className.substring(0, nameLength) + ".java"; //$NON-NLS-1$
					} else {
						unitName = member.getJavaScriptUnit().getElementName();
					}
				}
				if (unitName != null) {
					parser = ASTParser.newParser(AST.JLS3);
					parser.setSource(source);
					parser.setProject(project);
					parser.setUnitName(unitName);
					parser.setResolveBindings(true);
					compilationUnit = (JavaScriptUnit) parser.createAST(null);
					locator = new ValidBreakpointLocationLocator(compilationUnit, fLineNumber, true, fBestMatch);
					compilationUnit.accept(locator);
				}
			}
		}
		int lineNumber = locator.getLineLocation();
		String typeName = locator.getFullyQualifiedTypeName();

		try {
			switch (locator.getLocationType()) {
			case ValidBreakpointLocationLocator.LOCATION_LINE:
				return manageLineBreakpoint(typeName, lineNumber);
			case ValidBreakpointLocationLocator.LOCATION_METHOD:
				if (fBreakpoint != null) {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fBreakpoint, true);
				}
				new ToggleBreakpointAdapter().toggleMethodBreakpoints(fEditorPart, new TextSelection(locator.getMemberOffset(), 0));
				break;
			case ValidBreakpointLocationLocator.LOCATION_FIELD:
				if (fBreakpoint != null) {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fBreakpoint, true);
				}
				new ToggleBreakpointAdapter().toggleWatchpoints(fEditorPart, new TextSelection(locator.getMemberOffset(), 0));
				break;
			default:
				// cannot find a valid location
				report(ActionMessages.BreakpointLocationVerifierJob_not_valid_location);
				if (fBreakpoint != null) {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fBreakpoint, true);
				}
				return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.ERROR, ActionMessages.BreakpointLocationVerifierJob_not_valid_location, null);
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, ActionMessages.BreakpointLocationVerifierJob_breakpoint_set, null);

	}

	/**
	 * Determines the placement of the line breakpoint, and ensures that duplicates are not created
	 * and that notification is sent in the event of collisions
	 * @param typeName the fully qualified name of the type to add the line breakpoint to
	 * @param lineNumber the number we wish to put the breakpoint on
	 * @return the status of the line breakpoint placement
	 */
	public IStatus manageLineBreakpoint(String typeName, int lineNumber) {
		try {
			boolean differentLineNumber = lineNumber != fLineNumber;
			JSLineBreakpoint breakpoint = BreakpointUtils.lineBreakpointExists(fResource, typeName, lineNumber);
			boolean breakpointExist = breakpoint != null;
			if (fBreakpoint == null) {
				if (breakpointExist) {
					if (differentLineNumber) {
						// There is already a breakpoint on the valid line.
						report(MessageFormat.format(ActionMessages.BreakpointLocationVerifierJob_0, new String[] { Integer.toString(lineNumber) }));
						return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.ERROR, ActionMessages.BreakpointLocationVerifierJob_not_valid_location, null);
					}
					// There is already a breakpoint on the valid line, but it's also the requested line.
					// Removing the existing breakpoint.
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(breakpoint, true);
					return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, ActionMessages.BreakpointLocationVerifierJob_breakpointRemoved, null);
				}
				createNewBreakpoint(lineNumber, typeName);
				return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, ActionMessages.BreakpointLocationVerifierJob_breakpoint_set, null);
			}
			if (differentLineNumber) {
				if (breakpointExist) {
					// there is already a breakpoint on the valid line.
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fBreakpoint, true);
					report(MessageFormat.format(ActionMessages.BreakpointLocationVerifierJob_0, new String[] { Integer.toString(lineNumber) }));
					return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.ERROR, ActionMessages.BreakpointLocationVerifierJob_not_valid_location, null);
				}
				replaceBreakpoint(lineNumber, typeName);
				return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.WARNING, ActionMessages.BreakpointLocationVerifierJob_breakpointMovedToValidPosition, null);
			}
			if (!typeName.equals(fTypeName)) {
				replaceBreakpoint(lineNumber, typeName);
				return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.WARNING, ActionMessages.BreakpointLocationVerifierJob_breakpointSetToRightType, null);
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, ActionMessages.BreakpointLocationVerifierJob_breakpoint_set, null);
	}

	/**
	 * Remove the temporary breakpoint and create a new breakpoint at the right position.
	 */
	private void replaceBreakpoint(int lineNumber, String typeName) throws CoreException {
		createNewBreakpoint(lineNumber, typeName);
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fBreakpoint, true);
	}

	/**
	 * Create a new breakpoint at the right position.
	 */
	private void createNewBreakpoint(int lineNumber, String typeName) throws CoreException {
		Map newAttributes = new HashMap(10);
		if (fType != null) {
			try {
				IRegion line = fDocument.getLineInformation(lineNumber - 1);
				int start = line.getOffset();
				int end = start + line.getLength() - 1;
				BreakpointUtils.addJavaBreakpointAttributesWithMemberDetails(newAttributes, fType, start, end);
			} catch (BadLocationException ble) {
				Activator.log(ble);
			}
		}
		BreakpointUtils.createLineBreakpoint(fDocument, fEditorPart.getEditorInput(), lineNumber, -1, -1, 0, true, newAttributes);
	}

	/**
	 * Reports any status to the current active workbench shell
	 * @param message the message to display
	 */
	protected void report(final String message) {
		Activator.getStandardDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fStatusLine != null) {
					fStatusLine.setMessage(true, message, null);
				}
				if (message != null && Activator.getActiveWorkbenchShell() != null) {
					Display.getCurrent().beep();
				}
			}
		});
	}

	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
}
