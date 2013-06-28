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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.atf.compatibility.jsdt.Activator;
import org.eclipse.atf.compatibility.jsdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.core.Flags;
import org.eclipse.wst.jsdt.core.IClassFile;
import org.eclipse.wst.jsdt.core.IField;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.ITypeRoot;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.Signature;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.WorkingCopyManager;
import org.eclipse.wst.jsdt.ui.IWorkingCopyManager;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;

/**
 * Toggles a line breakpoint in a Java editor.
 * 
 * @since 3.0
 */
public class ToggleBreakpointAdapter implements IToggleBreakpointsTargetExtension {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	public ToggleBreakpointAdapter() {
	}

	/**
	 * Convenience method for printing messages to the status line
	 * @param message the message to be displayed
	 * @param part the currently active workbench part
	 */
	protected void report(final String message, final IWorkbenchPart part) {
		Activator.getStandardDisplay().asyncExec(new Runnable() {
			public void run() {
				IEditorStatusLine statusLine = (IEditorStatusLine) part.getAdapter(IEditorStatusLine.class);
				if (statusLine != null) {
					if (message != null) {
						statusLine.setMessage(true, message, null);
					} else {
						statusLine.setMessage(true, null, null);
					}
				}
				if (message != null && Activator.getActiveWorkbenchShell() != null) {
					Activator.getActiveWorkbenchShell().getDisplay().beep();
				}
			}
		});
	}

	/**
	 * Returns the <code>IType</code> for the given selection
	 * @param selection the current text selection
	 * @return the <code>IType</code> for the text selection or <code>null</code>
	 */
	/*    protected IType getType(ITextSelection selection) {
	        IMember member = ActionDelegateHelper.getDefault().getCurrentMember(selection);
	        IType type = null;
	        if (member instanceof IType) {
	            type = (IType) member;
	        } else if (member != null) {
	            type = member.getDeclaringType();
	        }
	        // bug 52385: we don't want local and anonymous types from compilation
	        // unit,
	        // we are getting 'not-always-correct' names for them.
	        try {
	            while (type != null && !type.isBinary() && type.isLocal()) {
	                type = type.getDeclaringType();
	            }
	        } catch (JavaModelException e) {
	            JDIDebugUIPlugin.log(e);
	        }
	        return type;
	    }*/

	/**
	 * Returns the IType associated with the <code>IJavaElement</code> passed in
	 * @param element the <code>IJavaElement</code> to get the type from
	 * @return the corresponding <code>IType</code> for the <code>IJavaElement</code>, or <code>null</code> if there is not one.
	 * @since 3.3
	 */
	/*   protected IType getType(IJavaElement element) {
	   	switch(element.getElementType()) {
	    	case IJavaElement.FIELD: {
	    		return ((IField)element).getDeclaringType();
	    	}	
	    	case IJavaElement.METHOD: {
	    		return ((IMethod)element).getDeclaringType();
	    	}
	    	case IJavaElement.TYPE: {
	    		return (IType)element;
	    	}
	    	default: {
	    		return null;
	    	}
	   	}
	   }*/

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection, false);
	}

	/**
	 * Toggles a line breakpoint.
	 * @param part the currently active workbench part 
	 * @param selection the current selection
	 * @param bestMatch if we should make a best match or not
	 */
	public void toggleLineBreakpoints(final IWorkbenchPart part, final ISelection selection, final boolean bestMatch) {
		Job job = new Job("Toggle Line Breakpoint") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				ITextEditor editor = getTextEditor(part);
				if (editor != null && selection instanceof ITextSelection) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					try {
						report(null, part);
						ISelection sel = selection;
						if (!(selection instanceof IStructuredSelection)) {
							sel = translateToMembers(part, selection);
						}
						if (sel instanceof IStructuredSelection) {
							IJavaScriptElement member = (IJavaScriptElement) ((IStructuredSelection) sel).getFirstElement();
							String tname = createQualifiedTypeName(member);
							IResource resource = BreakpointUtils.getBreakpointResource(member);
							int lnumber = ((ITextSelection) selection).getStartLine() + 1;
							JSLineBreakpoint existingBreakpoint = BreakpointUtils.lineBreakpointExists(resource, tname, lnumber);
							if (existingBreakpoint != null) {
								DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(existingBreakpoint, true);
								return Status.OK_STATUS;
							}
							Map attributes = new HashMap(10);
							IDocumentProvider documentProvider = editor.getDocumentProvider();
							if (documentProvider == null) {
								return Status.CANCEL_STATUS;
							}
							IDocument document = documentProvider.getDocument(editor.getEditorInput());
							try {
								IRegion line = document.getLineInformation(lnumber - 1);
								int start = line.getOffset();
								int end = start + line.getLength() - 1;
								BreakpointUtils.addJavaBreakpointAttributesWithMemberDetails(attributes, member, start, end);
							} catch (BadLocationException ble) {
								Activator.log(ble);
							}
							JSLineBreakpoint breakpoint = BreakpointUtils.createLineBreakpoint(document, editor.getEditorInput(), lnumber, -1, -1, 0, true, attributes);
							//							new BreakpointLocationVerifierJob(document, breakpoint, lnumber, bestMatch, tname, member, resource, editor).schedule();
						} else {
							report(ActionMessages.ToggleBreakpointAdapter_3, part);
							return Status.OK_STATUS;
						}
					} catch (CoreException ce) {
						return ce.getStatus();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(IWorkbenchPart,
	 *      ISelection)
	 */
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		if (isRemote(part, selection)) {
			return false;
		}
		return selection instanceof ITextSelection;
	}

	/**
	 * Returns the package qualified name, while accounting for the fact that a source file might
	 * not have a project
	 * @param type the type to ensure the package qualified name is created for
	 * @return the package qualified name
	 * @since 3.3
	 */
	private String createQualifiedTypeName(IJavaScriptElement type) {
		ITypeRoot typeRoot;
		if (type instanceof IMember)
			typeRoot = ((IMember) type).getTypeRoot();
		else
			typeRoot = (ITypeRoot) type;
		String tname = typeRoot.getElementName();

		//    	try {
		if (!type.getJavaScriptProject().exists()) {
			//				IPackageDeclaration[] pd = type.getCompilationUnit().getPackageDeclarations();
			//				if(pd.length > 0) {
			//					String pack = pd[0].getElementName();
			//					if(!pack.equals(EMPTY_STRING)) {
			//						tname =  pack+"."+tname; //$NON-NLS-1$
			//					}
			//				}
		}
		//    	} 
		//    	catch (JavaModelException e) {}
		return tname;
	}

	/**
	 * gets the <code>IJavaElement</code> from the editor input
	 * @param input the current editor input
	 * @return the corresponding <code>IJavaElement</code>
	 * @since 3.3
	 */
	private IJavaScriptElement getJavaElement(IEditorInput input) {
		IJavaScriptElement je = JavaScriptUI.getEditorInputJavaElement(input);
		if (je != null) {
			return je;
		}
		//try to get from the working copy manager
		//TODO this one depends on bug 151260
		return ((WorkingCopyManager) JavaScriptUI.getWorkingCopyManager()).getWorkingCopy(input, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		if (isRemote(part, selection)) {
			return false;
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			return getMethods(ss).length > 0;
		}
		return (selection instanceof ITextSelection) && isMethod((ITextSelection) selection, part);
	}

	/**
	 * Returns whether the given part/selection is remote (viewing a repository)
	 * 
	 * @param part
	 * @param selection
	 * @return
	 */
	protected boolean isRemote(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			if (element instanceof IMember) {
				IMember member = (IMember) element;
				return !member.getJavaScriptProject().getProject().exists();
			}
		}
		ITextEditor editor = getTextEditor(part);
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			Object adapter = Platform.getAdapterManager().getAdapter(input, "org.eclipse.team.core.history.IFileRevision"); //$NON-NLS-1$
			return adapter != null;
		}
		return false;
	}

	/**
	 * Returns the text editor associated with the given part or <code>null</code>
	 * if none. In case of a multi-page editor, this method should be used to retrieve
	 * the correct editor to perform the breakpoint operation on.
	 * 
	 * @param part workbench part
	 * @return text editor part or <code>null</code>
	 */
	protected ITextEditor getTextEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			return (ITextEditor) part;
		}
		return (ITextEditor) part.getAdapter(ITextEditor.class);
	}

	/**
	 * Returns the methods from the selection, or an empty array
	 * @param selection the selection to get the methods from
	 * @return an array of the methods from the selection or an empty array
	 */
	// TODO rename getMethods to getFunctions
	protected IFunction[] getMethods(IStructuredSelection selection) {
		if (selection.isEmpty()) {
			return new IFunction[0];
		}
		List methods = new ArrayList(selection.size());
		Iterator iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object thing = iterator.next();
			try {
				if (thing instanceof IFunction) {
					IFunction method = (IFunction) thing;
					if (!Flags.isAbstract(method.getFlags())) {
						methods.add(method);
					}
				}
			} catch (JavaScriptModelException e) {
			}
		}
		return (IFunction[]) methods.toArray(new IFunction[methods.size()]);
	}

	/**
	 * Returns if the text selection is a valid method or not
	 * @param selection the text selection
	 * @param part the associated workbench part
	 * @return true if the selection is a valid method, false otherwise
	 */
	private boolean isMethod(ITextSelection selection, IWorkbenchPart part) {
		ITextEditor editor = getTextEditor(part);
		if (editor != null) {
			IJavaScriptElement element = getJavaElement(editor.getEditorInput());
			if (element != null) {
				try {
					if (element instanceof IJavaScriptUnit) {
						element = ((IJavaScriptUnit) element).getElementAt(selection.getOffset());
					} else if (element instanceof IClassFile) {
						element = ((IClassFile) element).getElementAt(selection.getOffset());
					}
					return element != null && element.getElementType() == IJavaScriptElement.METHOD;
				} catch (JavaScriptModelException e) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of <code>IField</code> and <code>IJavaFieldVariable</code> in the given selection.
	 * When an <code>IField</code> can be resolved for an <code>IJavaFieldVariable</code>, it is
	 * returned in favour of the variable.
	 *
	 * @param selection
	 * @return list of <code>IField</code> and <code>IJavaFieldVariable</code>, possibly empty
	 * @throws CoreException
	 */
	protected List getFields(IStructuredSelection selection) throws CoreException {
		if (selection.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List fields = new ArrayList(selection.size());
		Iterator iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object thing = iterator.next();
			if (thing instanceof IField) {
				fields.add(thing);
			}
		}
		return fields;
	}

	/**
	 * Returns if the structured selection is itself or is part of an interface
	 * @param selection the current selection
	 * @return true if the selection is part of an interface, false otherwise
	 * @since 3.2
	 */
	private boolean isInterface(ISelection selection, IWorkbenchPart part) {
		return false;
	}

	/**
	 * Returns if the text selection is a field selection or not
	 * @param selection the text selection
	 * @param part the associated workbench part
	 * @return true if the text selection is a valid field for a watchpoint, false otherwise
	 * @since 3.3
	 */
	private boolean isField(ITextSelection selection, IWorkbenchPart part) {
		ITextEditor editor = getTextEditor(part);
		if (editor != null) {
			IJavaScriptElement element = getJavaElement(editor.getEditorInput());
			if (element != null) {
				try {
					if (element instanceof IJavaScriptUnit) {
						element = ((IJavaScriptUnit) element).getElementAt(selection.getOffset());
					} else if (element instanceof IClassFile) {
						element = ((IClassFile) element).getElementAt(selection.getOffset());
					}
					return element != null && element.getElementType() == IJavaScriptElement.FIELD;
				} catch (JavaScriptModelException e) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if the selection is a field or not
	 * @param selection the current selection
	 * @return true if the selection is a field false otherwise
	 */
	private boolean isFields(IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				Object thing = iterator.next();
				return (thing instanceof IField);
			}
		}
		return false;
	}

	/**
	 * Returns the resource associated with the specified editor part
	 * @param editor the currently active editor part
	 * @return the corresponding <code>IResource</code> from the editor part
	 */
	protected static IResource getResource(IEditorPart editor) {
		IEditorInput editorInput = editor.getEditorInput();
		IResource resource = (IResource) editorInput.getAdapter(IFile.class);
		if (resource == null) {
			resource = ResourcesPlugin.getWorkspace().getRoot();
		}
		return resource;
	}

	/**
	 * Returns a handle to the specified method or <code>null</code> if none.
	 * 
	 * @param editorPart
	 *            the editor containing the method
	 * @param typeName
	 * @param methodName
	 * @param signature
	 * @return handle or <code>null</code>
	 */
	protected IFunction getMethodHandle(IEditorPart editorPart, String typeName, String methodName, String signature) throws CoreException {
		IJavaScriptElement element = (IJavaScriptElement) editorPart.getEditorInput().getAdapter(IJavaScriptElement.class);
		IType type = null;
		if (element instanceof IJavaScriptUnit) {
			IType[] types = ((IJavaScriptUnit) element).getAllTypes();
			for (int i = 0; i < types.length; i++) {
				if (types[i].getFullyQualifiedName().equals(typeName)) {
					type = types[i];
					break;
				}
			}
		} else if (element instanceof IClassFile) {
			type = ((IClassFile) element).getType();
		}
		if (type != null) {
			String[] sigs = Signature.getParameterTypes(signature);
			return type.getFunction(methodName, sigs);
		}
		return null;
	}

	/**
	 * Returns the compilation unit from the editor
	 * @param editor the editor to get the compilation unit from
	 * @return the compilation unit or <code>null</code>
	 * @throws CoreException
	 */
	protected JavaScriptUnit parseCompilationUnit(ITextEditor editor) throws CoreException {
		IEditorInput editorInput = editor.getEditorInput();
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		IDocument document = documentProvider.getDocument(editorInput);
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(document.get().toCharArray());
		return (JavaScriptUnit) parser.createAST(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		if (isRemote(part, selection)) {
			return false;
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			return isFields(ss);
		}
		return (selection instanceof ITextSelection) && isField((ITextSelection) selection, part);
	}

	/**
	 * Returns a selection of the member in the given text selection, or the
	 * original selection if none.
	 * 
	 * @param part
	 * @param selection
	 * @return a structured selection of the member in the given text selection,
	 *         or the original selection if none
	 * @exception CoreException
	 *                if an exception occurs
	 */
	protected ISelection translateToMembers(IWorkbenchPart part, ISelection selection) throws CoreException {
		ITextEditor textEditor = getTextEditor(part);
		if (textEditor != null && selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			IEditorInput editorInput = textEditor.getEditorInput();
			IDocumentProvider documentProvider = textEditor.getDocumentProvider();
			if (documentProvider == null) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			IDocument document = documentProvider.getDocument(editorInput);
			int offset = textSelection.getOffset();
			if (document != null) {
				try {
					IRegion region = document.getLineInformationOfOffset(offset);
					int end = region.getOffset() + region.getLength();
					while (Character.isWhitespace(document.getChar(offset)) && offset < end) {
						offset++;
					}
				} catch (BadLocationException e) {
				}
			}
			IJavaScriptElement m = null;
			IClassFile classFile = (IClassFile) editorInput.getAdapter(IClassFile.class);
			if (classFile != null) {
				IJavaScriptElement e = classFile.getElementAt(offset);
				if (e instanceof IMember) {
					m = (IMember) e;
				} else
					m = classFile;
			} else {
				IWorkingCopyManager manager = JavaScriptUI.getWorkingCopyManager();
				IJavaScriptUnit unit = manager.getWorkingCopy(editorInput);
				if (unit != null) {
					synchronized (unit) {
						unit.reconcile(IJavaScriptUnit.NO_AST, false, null, null);
					}
				} else {
					unit = ((WorkingCopyManager) JavaScriptUI.getWorkingCopyManager()).getWorkingCopy(editorInput, false);
					if (unit != null) {
						synchronized (unit) {
							unit.reconcile(IJavaScriptUnit.NO_AST, false, null, null);
						}
					}
				}
				IJavaScriptElement e = unit.getElementAt(offset);
				if (e instanceof IMember) {
					m = (IMember) e;
				} else
					m = unit;
			}
			if (m != null) {
				return new StructuredSelection(m);
			}
		}
		return selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#toggleBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#canToggleBreakpoints(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		if (isRemote(part, selection)) {
			return false;
		}
		return canToggleLineBreakpoints(part, selection);
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {

	}
}
