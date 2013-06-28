/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.dbai.bladeRunner;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * ApplicationWorkbenchAdvisor.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 25, 2011
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = AnnotationPerspective.ID;

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public IAdaptable getDefaultPageInput() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public void preStartup() 
	{
		super.preStartup();

		// You need the IDE for this one
//		WorkbenchAdapterBuilder.registerAdapters();
	}

	@Override
	public void postStartup() 
	{
		super.postStartup();

//		// register controls
//		IWorkbenchPage page = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getActivePage();
//		if (page != null) {
//			IEditorReference[] refs = page.findEditors(null, AnnotatorEditor.ID,
//					WorkbenchPage.MATCH_ID);
//			if (refs != null && refs.length > 0) {
//				for (int i = 0; i < refs.length; i++) {
//					IEditorReference ref = refs[i];
//					if (ref.getId().equals(AnnotatorEditor.ID)) {
//						AnnotatorEditor we = (AnnotatorEditor) ref.getEditor(false);
//
////						IViewReference viewRef = page.findViewReference(AnnotationView.ID);
////						if (viewRef != null) {
////							IViewPart viewPart = viewRef.getView(true);
////							if (viewPart instanceof AnnotationView) {
////								AnnotationView view = (AnnotationView) viewPart;
////								we.registerForSelection(view);
////							}
////						}
////						viewRef = page.findViewReference(SelectionImageView.ID);
////						if (viewRef != null) {
////							IViewPart viewPart = viewRef.getView(true);
////							if (viewPart instanceof SelectionImageView) {
////								SelectionImageView view = (SelectionImageView) viewPart;
////								we.registerForSelection(view);
////							}
////						}
//						// viewRef =
//						// page.findViewReference(PatternViewClassic.ID);
//						// if (viewRef!=null)
//						// {
//						// IViewPart viewPart = viewRef.getView(true);
//						// if (viewPart instanceof PatternViewClassic)
//						// {
//						// PatternViewClassic patternViewClassic =
//						// (PatternViewClassic) viewPart;
//						// we.registerForSelection(patternViewClassic);
//						// }
//						// }
//						// viewRef = page.findViewReference(PatternViewZest.ID);
//						// if (viewRef!=null)
//						// {
//						// IViewPart viewPart = viewRef.getView(true);
//						// if (viewPart instanceof PatternViewZest)
//						// {
//						// PatternViewZest patternViewZest = (PatternViewZest)
//						// viewPart;
//						// we.registerForSelection(patternViewZest);
//						// }
//						// }
//					}
//				}
//			}
//		}
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
//		configurer.setSaveAndRestore(true); // remember user layout
	}

//	@Override
//	public IStatus saveState(IMemento memento) {
//		IMemento child = memento.createChild("annotator");
//		child.putString("lastOpenedDate", DateFormat.getDateTimeInstance()
//				.format(new Date()));
//		IWorkbenchWindow iww = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow();
//		if (iww != null) {
//			IPerspectiveDescriptor ipr = iww.getActivePage().getPerspective();
//			child.putString("activePerspective", ipr.getId());
//		}
//		return super.saveState(memento);
//	}
//
//	@Override
//	public IStatus restoreState(IMemento memento) {
//		if (memento != null) {
//			IMemento myAppMemento = memento.getChild("annotator");
//			// String[] atts = memento.getAttributeKeys();
//
//			if (myAppMemento != null) {
//				String id = myAppMemento.getString("activePerspective");
//				DocWrapUIUtils.activePerspective = id;
//				System.out.println("Last opened on: "
//						+ myAppMemento.getString("lastOpenedDate"));
//			}
//		}
//		return super.restoreState(memento);
//	}
}
