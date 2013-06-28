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
package at.tuwien.prip.model.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.IAttribute;
import at.tuwien.prip.model.utils.DOMHelper;



/**
 * 
 * HTML token implementation.
 * 
 * @author mcg <goebel@gmail.com>
 *
 */
public class HTMLTagToken extends Token
implements Comparable<HTMLTagToken>, Serializable {

	private static final long serialVersionUID = -1132395568882795437L;

	public static HTMLTagToken SENTINEL = new HTMLTagToken("");

	private Map<String, IAttribute> att_list = null;
	private String tagName = null;
	private String path = null;
	private Element element = null;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param value
	 */
	protected HTMLTagToken(String value) {
		super(value);
	}
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param e
	 */
	public HTMLTagToken(Element e) {
		this(e, true);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param e
	 * @param useIndices
	 */
	public HTMLTagToken(Element e, boolean useIndices) {
		this(e.getTagName());
		this.element = e;
		this.tagName = e.getNodeName();
		this.path = DOMHelper.XPath.getExactXPath(e, useIndices, false);
		if (useIndices) {
			String lastIndex = path.substring(path.lastIndexOf("["));
			this.tagName += lastIndex;
		} 
		this.att_list = new HashMap<String, IAttribute>();
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param e
	 * @param useIndices
	 * @param lxList
	 */
	public HTMLTagToken (Element e, boolean useIndices, Map<String, IAttribute> lxList) {
		this (e, useIndices);
		this.att_list = lxList;
	}

//	/**
//	 * 
//	 * Constructor.
//	 * 
//	 * @param e
//	 * @param useIndices
//	 * @param attributeSelection
//	 */
//	public HTMLTagToken (
//			Element e,
//			DocumentCollection benchCol,
//			boolean useIndices, 
//			int attributeSelection) 
//	{
//
//		this (e, useIndices);
//
//		// positive examples for generating attributes
//		HTMLTokenSequence posToks = HTMLTagTokenFactory.tokenize(benchCol, true);
//		List<String> poss = null;
//		String ePath = DOMHelper.XPath.getExactXPath(e, false, false);
//
//		if (attributeSelection>0) {
//			NamedNodeMap nnm = e.getAttributes();
//			for (int i=0; i<nnm.getLength(); i++) {
//				Node n = nnm.item(i);
//				String att_name = n.getNodeName();
//
//				// get all att_names of positive tokens on same path
//				poss = new LinkedList<String>();
//				for (HTMLTagToken posTok : posToks.tokenList) {
//					String tokPath = DOMHelper.XPath.getExactXPath(posTok.element, false, false);
//					if (tokPath.compareTo(ePath)==0)
//						if (posTok.getAtt_list().containsKey(att_name))
//							poss.add(posTok.getAtt_list().get(att_name).toString());
//
//				}
//				poss.add(n.getNodeValue());
//
//				lxValueDef lvd = lxValueDefFactory.generate(
//						e.getNodeName(),
//						att_name,
//						poss,
//						new LinkedList<String>());
//
//				if(lvd!=null) {
//					this.att_list.put(att_name, null);//lvd
//				}
//			}
//		}
//
//		// add path feature
//		if (attributeSelection>1) {
//
//			// get all att_names of positive tokens on same path
//			poss = new LinkedList<String>();
//			for (HTMLTagToken posTok : posToks) {
//				String tokPath = DOMHelper.XPath.getExactXPath(posTok.element, false, false);
//				if (tokPath.compareTo(ePath)==0)
//					if (posTok.getAtt_list().containsKey("locFeat"))
//						poss.add(posTok.getAtt_list().get("locFeat").toString());
//
//			}
//			poss.add(DOMHelper.XPath.getExactXPath(e, useIndices, false));
//
//			/*           this.att_list.put(
//                    "locFeat",
//                    lxValueDefFactory.generate(
//                            e.getNodeName(),
//                            "locFeat",
//                            poss,
//                            new LinkedList<String>()));
//			 */
//
//			// add text feature
//			if (attributeSelection>2) {
//
//				// get all att_names of positive tokens on same path
//				poss = new LinkedList<String>();
//				for (HTMLTagToken posTok : posToks) {
//					String tokPath = DOMHelper.XPath.getExactXPath(posTok.element, false, false);
//					if (tokPath.compareTo(ePath)==0)
//						if (posTok.getAtt_list().containsKey("txtFeat"))
//							poss.add(posTok.getAtt_list().get("txtFeat").toString());
//
//				}
//				poss.add(e.getTextContent());
//				/*               this.att_list.put(
//                        "txtFeat",
//                        lxValueDefFactory.generate(
//                                e.getNodeName(),
//                                "txtFeat",
//                                poss,
//                                new LinkedList<String>()));
//				 */
//			}
//		}
//	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static HTMLTagToken getTerminusInstance() {
		HTMLTagToken result = new HTMLTagToken("###"); //terminus symbol
		return result;
	}
	
	/**
	 * 
	 * @return Returns the string representation of this entity.
	 */
	public String toString () {
		String retString = "#HTML("+tagName+"){";
		for (String attName : this.att_list.keySet())
			retString += attName+":"+this.att_list.get(attName).toString()+",";
		if (this.att_list.size()>0)
			retString = retString.substring(0,retString.length()-1) + "}";
		else
			retString = retString.substring(0,retString.length()) + "}";
		return retString;
	}

	/**
	 * 
	 * Returns true if the two tokens are equal.
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals (Object other) {
		if (this.toString().compareTo(other.toString())==0)
			return true;
		return false;
	}

	@Override
	public HTMLTagToken clone() {
		HTMLTagToken copy = new HTMLTagToken(this.getTagName());
		copy.element = this.getElement();
		copy.path = this.getPath();
		copy.att_list = this.getAtt_list();
		copy.tagName = this.getTagName();
		return copy;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * 
	 * Compare this token to another token. Comparison
	 * is done alphabetically, depending on the tags.
	 * 
	 * @param o
	 */
	@Override
	public int compareTo(HTMLTagToken o) {

		return this.tagName.compareTo(o.getTagName());

//		if (checkAttributeConsistencyAgainst(o)) return 0;
//		return -1;
	}

//	/**
//	* 
//	* Checks each of the current node's attributes against the
//	* corresponding attribute of the provided 'other' node.
//	* Checking conforms to 'lxValueDef.match(...)'.
//	* 
//	* @param other
//	* @return
//	*/
//	public boolean checkAttributeConsistencyAgainst (HTMLTagToken other) 
//	{
//	// check that same tags
//	if (this.getTagName().compareTo(other.getTagName())!=0) return false;

//	// check if OTHER's attlist empty
//	if (other.getAtt_list().keySet().size()==0) return true;

////	lxValueDef pathLVD = null;

//	// for every attribute
//	for (String att_name : other.getAtt_list().keySet()) {
//	IAttribute lvd = other.getAtt_list().get(att_name);
//	String att_val  = null;

//	if (att_name.equals("locFeat")) {
//	att_val = this.getPath();
//	} else if (att_name.equals("txtFeat")) {
//	att_val = this.getElement().getTextContent();
//	} else {
//	Node corrNode = this.getElement().getAttributeNode(att_name);
//	if (corrNode==null) return false;
//	att_val = corrNode.getNodeValue();
//	}

//	// match attribute
////	if (!lvd.matches(att_val)) return false;
//	}
////	// finally, match location feature
////	for (lxValueDef lvd : other.att_list) {
////	if (lvd instanceof lxLocationDef) {
////	pathLVD = lvd;
////	if (!pathLVD.matches(
////	DOMHelper.XPath.getExactXPath(this.element,
////	HTML_TokenFactory.useIndices,
////	false)))
////	return false;
////	}
////	}
//	return true;
//	}

	/**
	 * @return Returns the tagName.
	 */
	public String getTagName() {
		return tagName;
	}
	/**
	 * @return Returns the element.
	 */
	public Element getElement() {
		return element;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the att_list
	 */
	public Map<String, IAttribute> getAtt_list() {
		return this.att_list;
	}

	/**
	 * @param att_list the att_list to set
	 */
	public void setAtt_list(HashMap<String, IAttribute> att_list) {
		this.att_list = att_list;
	}

} // HTML_Token

