package at.tuwien.dbai.bladeRunner.editors.html;

import static at.tuwien.prip.mozcore.utils.MozJavaDocumentMap2.javaElement2mozElement;

import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.mozilla.dom.NodeFactory;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.editors.AnnotatorEditor;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.project.selection.AbstractSelection;
import at.tuwien.prip.model.project.selection.NodeSelection;
import at.tuwien.prip.model.project.selection.blade.RecordSelection;
import at.tuwien.prip.model.utils.DOMHelper;

/**
 * LearningSystemHighlighter.java
 *
 *
 * @author mcgoebel, mcg <mcgoebel@gmail.com>
 * @date Aug 6, 2011
 */
public class LearningSystemHighlighter extends Highlighter2
{
	private final RGB COLOR_HIGHLIGHT = new RGB(255, 255, 153);
	
	/**
	 * reference to editor that owns the highlight
	 */
	private HighlightState hs;
	
	private MapList<RGB, Element> color2Nodes;

	/**
	 * Constructor.
	 *
	 * @param wb
	 * @param hs
	 */
	public LearningSystemHighlighter(HighlightState hs)
	{
		this.hs = hs;
		color2Nodes = new HashMapList<RGB, Element>();
	}

	/**
	 * 
	 */
	public void clearHighlightState ()
	{
		this.hs.clear();
		rehighlightAll();
	}

	/**
	 * 
	 * @param color
	 * @param nodes
	 */
	public void addColor2Nodes(RGB color, List<Element> nodes)
	{
		color2Nodes.put(color, nodes);
		
		rehighlightAll();
	}
	
	/**
	 * Refresh highlights of all example instances.
	 */
	public void rehighlightAll()
	{
		ErrorDump.debug(this, "rehighlighting");

		AnnotatorEditor we = DocWrapUIUtils.getWrapperEditor();
		WeblearnEditor wlEditor = we.getHtmlEditor();
		if (wlEditor==null) return;
		nsIDOMDocument nsdoc = wlEditor.getDocument();
		super.removeAll();

		//for each result
		for (AbstractSelection result : hs.getItems())
		{
			if (result instanceof RecordSelection)
			{
				RecordSelection container =(RecordSelection) result;
				for (AbstractSelection sel : container.getSelections())
				{
					if (sel instanceof NodeSelection)
					{
						NodeSelection selection = (NodeSelection) sel;
						Element e = (Element) selection.getSelectedNode();

						if (nsdoc==null)
						{
							//mcg: nsidomdocument conversion
							nsdoc = (nsIDOMDocument)
									NodeFactory.getnsIDOMNode(
											e.getOwnerDocument()).queryInterface(
													nsIDOMDocument.NS_IDOMDOCUMENT_IID);
						}
						nsIDOMElement moze = javaElement2mozElement(e, nsdoc);
						if (moze==null) {
							ErrorDump.error(this, String.format("unable to map node back to mozilla DOM, skipping: %s", DOMHelper.XPath.getExactXPath(e)));
							continue;
						}

						addElement(moze, COLOR_HIGHLIGHT, COLOR_HIGHLIGHT);
					}
				}
			}
			else if (result instanceof NodeSelection)
			{
				NodeSelection selection = (NodeSelection) result;
				Element e = (Element) selection.getSelectedNode();

				if (nsdoc==null)
				{
					//mcg: nsidomdocument conversion
					nsdoc = (nsIDOMDocument)
							NodeFactory.getnsIDOMNode(
									e.getOwnerDocument()).queryInterface(
											nsIDOMDocument.NS_IDOMDOCUMENT_IID);
				}
				nsIDOMElement moze = javaElement2mozElement(e, nsdoc);
				if (moze==null) {
					ErrorDump.error(this, String.format("unable to map node back to mozilla DOM, skipping: %s", DOMHelper.XPath.getExactXPath(e)));
					continue;
				}

				addElement(moze, COLOR_HIGHLIGHT, COLOR_HIGHLIGHT);
			}
		}
		
		for (RGB color : color2Nodes.keySet())
		{
			for (Element e : color2Nodes.get(color))
			{
				if (nsdoc==null)
				{
					//mcg: nsidomdocument conversion
					nsdoc = (nsIDOMDocument)
							NodeFactory.getnsIDOMNode(
									e.getOwnerDocument()).queryInterface(
											nsIDOMDocument.NS_IDOMDOCUMENT_IID);
				}
				nsIDOMElement moze = javaElement2mozElement(e, nsdoc);
				if (moze==null) {
					ErrorDump.error(this, String.format("unable to map node back to mozilla DOM, skipping: %s", DOMHelper.XPath.getExactXPath(e)));
					continue;
				}

				addElement(moze, color, color);
			}
		}
	}

	public void setHS(HighlightState hs2)
	{
		this.hs = hs2;
	}
	
	public HighlightState getHs() {
		return hs;
	}

}
