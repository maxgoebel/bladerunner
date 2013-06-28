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
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.document.DocumentCollection;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.LabelSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.PdfSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.TableCell;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.project.selection.blade.TextSelection;

/**
 * AnnotationViewContentProviderGT.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 20, 2013
 */
public class AnnotationViewContentProviderGT implements ITreeContentProvider {

	/**
	 * 
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof BenchmarkDocument) {
			BenchmarkDocument bdoc = (BenchmarkDocument) parentElement;
			return ListUtils.toArray(bdoc.getGroundTruth(), Annotation.class);
		} else if (parentElement instanceof TableAnnotation) {
			TableAnnotation eAnn = (TableAnnotation) parentElement;
			return ListUtils.toArray(eAnn.getTables(), TableSelection.class);
		} else if (parentElement instanceof Annotation) {
			Annotation eAnn = (Annotation) parentElement;
			return ListUtils.toArray(eAnn.getItems(), AbstractSelection.class);
		} else if (parentElement instanceof AnnotationPage) {
			AnnotationPage page = (AnnotationPage) parentElement;
			return ListUtils.toArray(page.getItems(), AbstractSelection.class);
		} else if (parentElement instanceof DocumentCollection) {
			DocumentCollection dc = (DocumentCollection) parentElement;
			return dc.toArray();
		} else if (parentElement instanceof PdfSelection) {
			PdfSelection selection = (PdfSelection) parentElement;
			return ListUtils.toArray(selection.getInstructions(),
					PDFInstruction.class);
		} else if (parentElement instanceof LabelSelection) {
			LabelSelection selection = (LabelSelection) parentElement;
			return new Object[] { selection.getLabel(),
					selection.getSelection() };
		} else if (parentElement instanceof RegionSelection) {
			RegionSelection region = (RegionSelection) parentElement;
			List<Object> resultList = new ArrayList<Object>();
			resultList.add(region.getBounds());
			if (region.getCellContainer().getCells().size() > 0) {
				resultList.add(region.getCellContainer());
			}
			if (region.getInstructionContainer().getInstructions().size() > 0) {
				resultList.add(region.getInstructionContainer());
			}
			return ListUtils.toArray(resultList, Object.class);
		} else if (parentElement instanceof TableSelection) {
			TableSelection selection = (TableSelection) parentElement;
			return ListUtils
					.toArray(selection.getPages(), AnnotationPage.class);
		} else if (parentElement instanceof TableCell) {
			TableCell selection = (TableCell) parentElement;
			return new Object[] { selection.getBounds(),
					"start-col: " + selection.getStartCol(),
					"end-col: " + selection.getEndCol(),
					"start-row: " + selection.getStartRow(),
					"end-row: " + selection.getEndRow(),
					selection.getInstructions(), selection.getContent() };
		} else if (parentElement instanceof PdfInstructionContainer) {
			return ListUtils
					.toArray(((PdfInstructionContainer) parentElement)
							.getInstructions(), PDFInstruction.class);
		} else if (parentElement instanceof TableCellContainer) {
			return ListUtils.toArray(
					((TableCellContainer) parentElement).getCells(),
					TableCell.class);
		}
		return null;
	}

	/**
	 * 
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof DocumentCollection) {
			DocumentCollection bdoc = (DocumentCollection) element;
			return bdoc.size() > 0;
		} else if (element instanceof BenchmarkDocument) {
			BenchmarkDocument bdoc = (BenchmarkDocument) element;
			return bdoc.getGroundTruth().size() > 0;
		} else if (element instanceof Annotation) {
			Annotation ann = (Annotation) element;
			return ann.getItems().size() > 0;
		} else if (element instanceof AnnotationPage) {
			AnnotationPage exRes = (AnnotationPage) element;
			return exRes.getItems().size() > 0;
		} else if (element instanceof PdfSelection) {
			return ((PdfSelection) element).getInstructions().size() > 0;
		} else if (element instanceof LabelSelection) {
			return true;
		} else if (element instanceof TableSelection) {
			return ((TableSelection) element).getPages().size() > 0;
		} else if (element instanceof RegionSelection) {
			RegionSelection region = (RegionSelection) element;
			if (region.getCellContainer().getCells().size() > 0)
				return true;
			if (region.getInstructionContainer().getInstructions().size() > 0)
				return true;
			return false;
		} else if (element instanceof TextSelection) {
			return false;
		} else if (element instanceof TableCell) {
			return true;
		} else if (element instanceof PdfInstructionContainer) {
			return ((PdfInstructionContainer) element).getInstructions().size() > 0;
		} else if (element instanceof TableCellContainer) {
			return ((TableCellContainer) element).getCells().size() > 0;
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
