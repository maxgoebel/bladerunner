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
package at.tuwien.prip.model.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.tuwien.prip.model.learning.example.IExample;
import at.tuwien.prip.model.learning.example.NodeExample;
import at.tuwien.prip.model.learning.example.SelectionExample;
import at.tuwien.prip.model.project.document.WrapperDocument;
import at.tuwien.prip.model.project.document.WrapperDocumentCollection;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.NodeSelection;

/**
 * ExampleUtils.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Aug 18, 2012
 */
public class ExampleUtils 
{

	/**
	 * 
	 * @param doc
	 * @return
	 */
	public static List<SelectionExample> getSelectionExamples (WrapperDocument doc)
	{
		List<SelectionExample> result = new ArrayList<SelectionExample>();
		for (IExample example : doc.getExamples())
		{
			if (example instanceof SelectionExample)
			{
				result.add((SelectionExample) example);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public static List<Element> getSelectedElements(IExample ex) 
	{
		List<Element> result = new ArrayList<Element>();
		if (ex instanceof SelectionExample)
		{
			SelectionExample selEx = (SelectionExample) ex;
			for (AbstractSelection selection : selEx.getSelections())
			{
				if(selection instanceof NodeSelection)
				{
					NodeSelection nodeSel = (NodeSelection) selection;
					result.add((Element) nodeSel.getSelectedNode());
				}
				else 
				{
					System.err.println("Error here, unsupported selection type: "+ 
							ex.getClass().getName());
				}
			}
		}
		else if (ex instanceof NodeExample)
		{
			NodeExample nodeEx = (NodeExample) ex;
			result.add((Element)nodeEx.getSelectedNode());
		}
		else 
		{
			System.err.println("Error here, unsupported example type: "+ 
					ex.getClass().getName());
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public static List<Node> getSelectedNodes(IExample ex) 
	{
		List<Node> result = new ArrayList<Node>();
		if (ex instanceof SelectionExample)
		{
			SelectionExample selEx = (SelectionExample) ex;
			for (AbstractSelection selection : selEx.getSelections())
			{
				if(selection instanceof NodeSelection)
				{
					NodeSelection nodeSel = (NodeSelection) selection;
					result.add(nodeSel.getSelectedNode());
				}
				else 
				{
					System.err.println("Error here, unsupported selection type: "+ 
							ex.getClass().getName());
				}
			}
		}
		else if (ex instanceof NodeExample)
		{
			NodeExample nodeEx = (NodeExample) ex;
			NodeSelection nodeSel = nodeEx.getNodeSelection();
			result.add(nodeSel.getSelectedNode());
		}
		else 
		{
			System.err.println("Error here, unsupported example type: "+ 
					ex.getClass().getName());
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public static Element getSelectedElementsRoot(IExample ex) 
	{
		List<Node> selectedNodes = getSelectedNodes(ex);
		return DOMHelper.Tree.Ancestor.getClosestCommonAncestor(selectedNodes);
	}

	/**
	 * 
	 * @param ex
	 * @return
	 */
	public static Element getCommonRootElement(IExample ex) 
	{
		List<Node> selectedNodes = getSelectedNodes(ex);
		return DOMHelper.Tree.Ancestor.getClosestCommonAncestor(selectedNodes);
	}

	/**
	 * 
	 * @param exampleSet
	 */
	public static void assertExamplesInCorrectBenchmarkDocument(
			WrapperDocumentCollection exampleSet) 
	{
		// TODO Auto-generated method stub
		
	}
}

