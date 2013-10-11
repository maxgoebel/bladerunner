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
package at.tuwien.dbai.bladeRunner.views.anno;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.PdfInstructionContainer;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.document.DocumentCollection;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.LabelSelection;
import at.tuwien.prip.model.project.selection.MultiPageSelection;
import at.tuwien.prip.model.project.selection.SinglePageSelection;
import at.tuwien.prip.model.project.selection.blade.CaptionSelection;
import at.tuwien.prip.model.project.selection.blade.FigureSelection;
import at.tuwien.prip.model.project.selection.blade.FunctionalSelection;
import at.tuwien.prip.model.project.selection.blade.ImageSelection;
import at.tuwien.prip.model.project.selection.blade.ListItemSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.PdfSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.SectionSelection;
import at.tuwien.prip.model.project.selection.blade.SelectionContainer;
import at.tuwien.prip.model.project.selection.blade.SemanticSelection;
import at.tuwien.prip.model.project.selection.blade.TableCell;
import at.tuwien.prip.model.project.selection.blade.TableColumnSelection;
import at.tuwien.prip.model.project.selection.blade.TableRowSelection;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.project.selection.blade.TextSelection;

/**
 * 
 * AnnotationViewContentProvider
 * 
 */
public class AnnotationViewContentProvider implements ITreeContentProvider {

	/**
	 * 
	 */
	public Object[] getChildren(Object parentElement) 
	{
		if (parentElement instanceof BenchmarkDocument)
		{
			BenchmarkDocument bdoc = (BenchmarkDocument) parentElement;
			return ListUtils.toArray(bdoc.getAnnotations(), Annotation.class);
		}
		else if (parentElement instanceof Annotation)
		{
			Annotation ann = (Annotation) parentElement;
			return ListUtils.toArray(ann.getItems(), AbstractSelection.class);
		}
		else if (parentElement instanceof AnnotationPage)
		{
			AnnotationPage page = (AnnotationPage) parentElement;
			return ListUtils.toArray(page.getItems(), AbstractSelection.class);
		}
		else if (parentElement instanceof DocumentCollection) 
		{
			DocumentCollection dc = (DocumentCollection) parentElement;
			return dc.toArray();
		} 
		/* SELECTIONS */
		else if (parentElement instanceof TableSelection)
		{
			TableSelection table = (TableSelection) parentElement;
			List<Object> resultList = new ArrayList<Object>();
			if (table.getPages().size()>0)
				resultList.addAll(table.getPages());
			if (table.getCells().size()>0)
				resultList.add(table.getCells());
			return ListUtils.toArray(resultList, Object.class);
		}
		else if (parentElement instanceof SectionSelection)
		{
			SectionSelection selection = (SectionSelection) parentElement;
			List<Object> resultList = new ArrayList<Object>();
			resultList.add(selection.getSectionType());
			resultList.addAll(selection.getPages());
			return ListUtils.toArray(resultList, Object.class);
		}
		else if (parentElement instanceof PdfSelection) 
		{
			PdfSelection selection = (PdfSelection) parentElement;
			return ListUtils.toArray(selection.getInstructions(),
					PDFInstruction.class);
		} 
		else if (parentElement instanceof LabelSelection) 
		{
			LabelSelection selection = (LabelSelection) parentElement;
			return new Object[] { selection.getLabel(),
					selection.getSelection() };
		} 
		else if (parentElement instanceof RegionSelection) 
		{
			RegionSelection region = (RegionSelection) parentElement;
			List<Object> resultList = new ArrayList<Object>();
			resultList.add(region.getBounds());
			resultList.add(region.getText());
			if (region.getCellContainer().getCells().size() > 0) {
				resultList.add(region.getCellContainer());
			}
			if (region.getInstructionContainer().getInstructions().size() > 0) {
				resultList.add(region.getInstructionContainer());
			}
			return ListUtils.toArray(resultList, Object.class);
		} 
		else if (parentElement instanceof SemanticSelection)
		{
			List<Object> children = new ArrayList<Object>();
			children.add(((SemanticSelection) parentElement).getSemantic());
			children.addAll(((SemanticSelection) parentElement).getItems());
			return ListUtils
					.toArray(children, Object.class);
		}
		else if (parentElement instanceof FunctionalSelection)
		{
			FunctionalSelection function = (FunctionalSelection) parentElement;
			List<Object> children = new ArrayList<Object>();
			children.add(function.getFunction());
			children.addAll(function.getPages());
			return ListUtils
					.toArray(children, Object.class);
		}
		/* OTHERS */
		else if (parentElement instanceof ListItemSelection)
		{
			ListItemSelection selection = (ListItemSelection) parentElement;
			List<Object> children = new ArrayList<Object>();
			children.addAll(selection.getItems());
			return ListUtils
					.toArray(children, Object.class);
		}
		else if (parentElement instanceof TableCell) 
		{
			TableCell selection = (TableCell) parentElement;
			return new Object[] { selection.getBounds(),
					"start-col: " + selection.getStartCol(),
					"end-col: " + selection.getEndCol(),
					"start-row: " + selection.getStartRow(),
					"end-row: " + selection.getEndRow(),
					selection.getInstructions(), selection.getContent() };
		} 
		else if (parentElement instanceof TextSelection)
		{
			TextSelection selection = (TextSelection) parentElement;
			List<Object> children = new ArrayList<Object>();
			children.add("Type: "+selection.getTextType());
			if (selection.getTextContent()!=null){
				children.add("Content:\n"+selection.getTextContent());
			}
			children.addAll(selection.getItems());
			return ListUtils.toArray(children, Object.class);
		}
		else if (parentElement instanceof PdfInstructionContainer) 
		{
			return ListUtils
					.toArray(((PdfInstructionContainer) parentElement)
							.getInstructions(), PDFInstruction.class);
		} 
		else if (parentElement instanceof TableCellContainer) 
		{
			return ListUtils.toArray(
					((TableCellContainer) parentElement).getCells(),
					TableCell.class);
		}
		
		//as last instance, catch all those not specialized above
		else if (parentElement instanceof SinglePageSelection)
		{
			SinglePageSelection selection = (SinglePageSelection) parentElement;
			List<Object> children = new ArrayList<Object>();
			children.addAll(selection.getItems());
			return ListUtils
					.toArray(children, Object.class);
		}
		else if (parentElement instanceof MultiPageSelection)
		{
			MultiPageSelection selection = (MultiPageSelection) parentElement;
			return ListUtils
					.toArray(selection.getPages(), AnnotationPage.class);
		} 
		else if (parentElement instanceof SelectionContainer)
		{
			SelectionContainer selection = (SelectionContainer) parentElement;
			return ListUtils
					.toArray(selection.getSelections(), AbstractSelection.class);
		} 
		
		return null;
	}

	/**
	 * 
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof DocumentCollection) 
		{
			DocumentCollection bdoc = (DocumentCollection) element;
			return bdoc.size() > 0;
		} 
		else if (element instanceof BenchmarkDocument)
		{
			BenchmarkDocument bdoc = (BenchmarkDocument) element;
			return bdoc.getAnnotations().size() > 0;
		} 
		else if (element instanceof Annotation)
		{
			Annotation ann = (Annotation) element;
			return ann.getItems().size() > 0;
		} 
		else if (element instanceof AnnotationPage) 
		{
			AnnotationPage exRes = (AnnotationPage) element;
			return exRes.getItems().size() > 0;
		}
		/* SELECTIONS */
		else if (element instanceof TableSelection) 
		{
			return true;
		} 
		else if (element instanceof PdfSelection) 
		{
			return ((PdfSelection) element).getInstructions().size() > 0;
		} 
		else if (element instanceof LabelSelection) 
		{
			return true;
		} 
		else if (element instanceof SinglePageSelection) 
		{
			return ((SinglePageSelection) element).getItems().size() > 0;
		}
		else if (element instanceof MultiPageSelection) 
		{
			return ((MultiPageSelection) element).getPages().size() > 0;
		}
		else if (element instanceof RegionSelection)
		{
			RegionSelection region = (RegionSelection) element;
			if (region.getItems().size()>0)
				return true;
			if (region.getCellContainer().getCells().size() > 0)
				return true;
			if (region.getInstructionContainer().getInstructions().size() > 0)
				return true;
			if (region.getText()!=null)
				return true;
			return false;
		} 
		else if (element instanceof TableColumnSelection) 
		{
			return true;
		} 
		else if (element instanceof TableRowSelection) 
		{
			return true;
		} 
		else if (element instanceof TextSelection) 
		{
			return true;
		} 
		else if (element instanceof FigureSelection) 
		{
			return true;
		} 
		else if (element instanceof ImageSelection) 
		{
			return true;
		} 
		else if (element instanceof CaptionSelection) 
		{
			return true;
		} 
		else if (element instanceof ListItemSelection)
		{
			return true;
		}
		else if (element instanceof TableCell)
		{
			return true;
		}
		else if (element instanceof PdfInstructionContainer) 
		{
			return ((PdfInstructionContainer) element).getInstructions().size() > 0;
		}
		else if (element instanceof TableCellContainer)
		{
			return ((TableCellContainer) element).getCells().size() > 0;
		}
		else if (element instanceof SemanticSelection)
		{
			return true;
		}
		else if (element instanceof SelectionContainer)
		{
			return ((SelectionContainer) element).getSelections().size()>0;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

}
