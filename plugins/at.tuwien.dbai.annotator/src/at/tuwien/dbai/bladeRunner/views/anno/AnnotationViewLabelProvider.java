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

import java.awt.Rectangle;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import at.tuwien.prip.model.learning.example.SelectionExample;
import at.tuwien.prip.model.learning.example.TableExample;
import at.tuwien.prip.model.project.annotation.AnnotationLabel;
import at.tuwien.prip.model.project.annotation.AnnotationPage;
import at.tuwien.prip.model.project.annotation.ExtractionAnnotation;
import at.tuwien.prip.model.project.annotation.LabelAnnotation;
import at.tuwien.prip.model.project.annotation.LayoutAnnotation;
import at.tuwien.prip.model.project.annotation.PdfInstructionContainer;
import at.tuwien.prip.model.project.annotation.TableAnnotation;
import at.tuwien.prip.model.project.annotation.TableCellContainer;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.ExtractionItem;
import at.tuwien.prip.model.project.selection.ExtractionResult;
import at.tuwien.prip.model.project.selection.LabelSelection;
import at.tuwien.prip.model.project.selection.NodeSelection;
import at.tuwien.prip.model.project.selection.SegmentSelection;
import at.tuwien.prip.model.project.selection.blade.ListSelection;
import at.tuwien.prip.model.project.selection.blade.PDFInstruction;
import at.tuwien.prip.model.project.selection.blade.PdfSelection;
import at.tuwien.prip.model.project.selection.blade.RecordSelection;
import at.tuwien.prip.model.project.selection.blade.RegionSelection;
import at.tuwien.prip.model.project.selection.blade.TableCell;
import at.tuwien.prip.model.project.selection.blade.TableSelection;
import at.tuwien.prip.model.project.selection.blade.TextSelection;

/**
 * 
 * @author max
 * 
 */
public class AnnotationViewLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element)
	{
		String result = null;
		if (element instanceof BenchmarkDocument) 
		{
			result = "Benchmark Document";
		} 
		
		/* THE ANNOTATIONS */
		else if (element instanceof TableAnnotation) 
		{
			result = "Table Annotation";
		}
		else if (element instanceof LayoutAnnotation) 
		{
			result = "Layout Annotation";
		}
		else if (element instanceof LabelAnnotation) 
		{
			result = "Label Annotation ("
					+ ((LabelAnnotation) element).getLabel() + ")";
		} 
		else if (element instanceof ExtractionAnnotation)
		{
			result = "Extraction Annotation";
		} 
		
		/* ANNOTATION PAGE */
		else if (element instanceof AnnotationPage) 
		{
			AnnotationPage ePage = (AnnotationPage) element;
			result = "Page " + (ePage.getPageNum());
		} 
	
		/* THE SELECTIONS*/
		else if (element instanceof AbstractSelection) 
		{
			AbstractSelection selection = (AbstractSelection) element;
			result = selection.getType() + " (id=" + selection.getId() + ")";
			if (selection.getLabel()!=null && selection.getLabel().length()>0)
				result += " Label: " +selection.getLabel(); 
		}
		else if (element instanceof TableSelection) 
		{
			result = "Table " + (((TableSelection) element).getId());
		}
		else if (element instanceof RegionSelection) {
			result = "Region " + (((RegionSelection) element).getId());
		} 
		else if (element instanceof TableCell) 
		{
			result = "Cell " + (((TableCell) element).getCellId());
		} 
		else if (element instanceof TableExample)
		{
			result = "Table Example";
		}
		else if (element instanceof ExtractionResult) 
		{
			result = "Extraction Result";
		}
		else if (element instanceof ExtractionItem) 
		{
			ExtractionItem item = (ExtractionItem) element;
			result = item.getValue();
			if (result == null || result.length() <= 0) {
				result = "empty extraction item";
			}
		} 
		else if (element instanceof PdfSelection)
		{
			result = "Pdf Selection";
		}
		else if (element instanceof PDFInstruction)
		{
			PDFInstruction instr = (PDFInstruction) element;
			result = instr.getIndex() + " : " + instr.getSubIndex();
		}
		// else if (element instanceof PdfOperatorIndex)
		// {
		// // PdfOperatorIndex item = (PdfOperatorIndex) element;
		// result = "PdfOperator";
		// }
		// else if (element instanceof SelectionAnnotation)
		// {
		// result = "Selection Annotation";
		// }
		else if (element instanceof SelectionExample) 
		{
			result = "Selection";
		} 
		else if (element instanceof NodeSelection) 
		{
			NodeSelection ns = (NodeSelection) element;
			if (ns.getTargetXPath() != null) {
				result = ns.getTargetXPath();
			} else {
				result = "Node Selection";
			}
		} 
		else if (element instanceof SegmentSelection)
		{
			result = "Segment Selection";
		}
		else if (element instanceof RecordSelection)
		{
			result = "Record Selection";
		}
		else if (element instanceof TextSelection) 
		{
			result = "Content";// "Text Selection";
		}
		else if (element instanceof LabelSelection) 
		{
			result = "Label Annotation";
		} 
		else if (element instanceof AnnotationLabel)
		{
			result = ((AnnotationLabel) element).name();
		}
		else if (element instanceof ListSelection)
		{
			result = "List Selection";
		}

		
		/* BASIC DATATYPES */
		if (element instanceof String) 
		{
//			String value = (String) element;
//			if (value.contains(":")) {
//				return (value.split(":")[0]);
//			}
			return (String) element;
		} else if (element instanceof Integer) {
			return "" + (Integer) element;
		} else if (element instanceof Rectangle) {
			 Rectangle r = (Rectangle) element;
			return "Bounds: x=" + r.getX() +", y="+ r.getY() +", width="+ r.getWidth() +", height="+ r.getHeight();
		} else if (element instanceof TableCellContainer) {
			return "Table cells ("
					+ ((TableCellContainer) element).getCells().size() + ")";
		} else if (element instanceof PdfInstructionContainer) {
			return "PDF instructions ("
					+ ((PdfInstructionContainer) element).getInstructions()
							.size() + ")";
		}

		return result;
	};
}
