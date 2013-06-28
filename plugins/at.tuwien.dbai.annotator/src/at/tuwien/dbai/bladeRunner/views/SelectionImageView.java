package at.tuwien.dbai.bladeRunner.views;

import java.awt.Rectangle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.control.DocumentController;
import at.tuwien.dbai.bladeRunner.control.IModelChangedListener;
import at.tuwien.dbai.bladeRunner.control.ModelChangedEvent;
import at.tuwien.dbai.bladeRunner.editors.annotator.SWTImageCanvas;
import at.tuwien.dbai.bladeRunner.utils.PDFUtils;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.document.IDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;
import at.tuwien.prip.model.project.document.pdf.PdfDocumentPage;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SinglePageSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;

import com.sun.pdfview.PDFPage;

public class SelectionImageView extends ViewPart
implements ISelectionChangedListener, IModelChangedListener
{

	public static final String ID = "at.tuwien.dbai.bladeRunner.selectionView";
	
	private SWTImageCanvas canvas = null;
	
	/**
	 * Constructor.
	 */
	public SelectionImageView() 
	{
	}
	
	@Override
	public void createPartControl(Composite parent) {

		canvas = new SWTImageCanvas(parent);
		canvas.setBackground(ColorConstants.white);
		
		Activator.modelControl.addModelChangedListener(this);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}
	
	@Override
	public void dispose() 
	{
		super.dispose();
		
		canvas = null;
	}

	Rectangle lastSelection = null;
			
	@Override
	public void selectionChanged(SelectionChangedEvent event) 
	{
		java.awt.Rectangle clip = null;
		int pageNum = DocumentController.docModel.getPageNum();
		
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		Object top = selection.getFirstElement();
		if (top instanceof org.eclipse.swt.graphics.Rectangle)
		{
			org.eclipse.swt.graphics.Rectangle bounds = 
					(org.eclipse.swt.graphics.Rectangle) selection.getFirstElement();
			
			clip = new java.awt.Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		else if (top instanceof AbstractSelection)
		{
			RegionSelection region = findRegionFromSelection((AbstractSelection) top, pageNum);
			if (region!=null){
				clip = region.getBounds();
			}
		}
		
		if (clip!=null)
		{
			if (lastSelection!=null)
			{
				if (lastSelection.x==clip.x && lastSelection.y==clip.y && 
						lastSelection.width==clip.width && 
						lastSelection.height==clip.height)
				{
					return;
				}
			}
			
			lastSelection = clip;
			
			IDocument document = DocumentController.docModel.getDocument();
			
			if (document instanceof PdfBenchmarkDocument)
			{
				PdfBenchmarkDocument pdfDoc = (PdfBenchmarkDocument) document;

				PdfDocumentPage page = pdfDoc.getPage(pageNum);
				PDFPage pdfPage = page.getPage();
								
				double clientWidth = canvas.getClientArea().width * 0.9;
				double clientHeight = canvas.getClientArea().height * 0.9;
				
				double widthRatio = new Double(clientWidth/clip.width);
				double heightRatio = clientHeight/clip.height;
				double min = Math.min(widthRatio, heightRatio);
				
				int scaleWidth = (int)(min * clip.width);
				int scaleHeight = (int)(min * clip.height);			
			
				ImageData data = PDFUtils.getImageFromPDFPage(pdfPage, scaleWidth, scaleHeight, clip);
				canvas.setImageData(data, false);
			}
		}
	}

	private RegionSelection findRegionFromSelection (AbstractSelection selection, int pageNum)
	{
		RegionSelection result = null;

		if (selection instanceof RegionSelection)
		{
			RegionSelection region = (RegionSelection) selection;
			if (region.getPageNum()==pageNum)
			{
				result = region;
			}
		}
		else if (selection instanceof SinglePageSelection)
		{
			SinglePageSelection spSel = (SinglePageSelection) selection;
			for (AbstractSelection sel : spSel.getItems())
			{
				if (sel instanceof RegionSelection)
				{
					RegionSelection region = (RegionSelection) sel;
					if (region.getPageNum()==pageNum)
					{
						result = (RegionSelection) sel;
						break;
					}
				}
			}
		}
		else if (selection instanceof MultiPageSelection)
		{
			MultiPageSelection mpSel = (MultiPageSelection) selection;
			for (AnnotationPage page : mpSel.getPages())
			{
//				if (page.getPageNum()==pageNum)
//				{
					for (AbstractSelection sel : page.getItems())
					{
						if (sel instanceof RegionSelection)
						{
							result = (RegionSelection) sel;
							break;
						}
					}
//				}
			}
		}

		return result;
	}

	@Override
	public void modelChanged(ModelChangedEvent event) {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				canvas.clearImage();
				canvas.redraw();
			}
		});	
	}
}
