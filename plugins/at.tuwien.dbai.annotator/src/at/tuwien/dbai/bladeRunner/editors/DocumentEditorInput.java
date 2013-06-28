package at.tuwien.dbai.bladeRunner.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.prip.model.project.document.IDocument;


/**
 * DocumentEditorInput.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * May 20, 2012
 */
public class DocumentEditorInput implements IEditorInput 
{

	private IDocument document;

	public DocumentEditorInput(IDocument document) {
		this.document = document;
	}
	
	public IDocument getDocument() {
		return document;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		 return Activator.getImageDescriptor("/icons/someicon.png");
	}


	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Document Editor";
	}

	@Override
	public String getName() {
		return document.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DocumentEditorInput)
		{
			DocumentEditorInput dei = (DocumentEditorInput) obj;
			return dei.getDocument().getUri().equalsIgnoreCase(getDocument().getUri());
		}
		return false;
	}
}