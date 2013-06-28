package org.mozilla.dom;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.interfaces.*;
import org.w3c.dom.html.HTMLElement;

/**
 * HTMLElementFactory.java
 *
 *
 */
public class HTMLElementFactory
{

	private static HTMLElementFactory instance;

	private Map<String, String> corresp;

	private HTMLElementFactory() {
		initCorrespondence();
	}

	public static HTMLElementFactory getInstance(){
		if(instance == null){
			instance = new HTMLElementFactory();
		}
		return instance;
	}

	public static HTMLElement getHTMLElement(nsIDOMNode nsNode) {
		return getInstance().getConcreteNode(nsNode);
	}

	private void initCorrespondence() {

		corresp = new HashMap<String, String>();
		corresp.put("a", "Anchor");

		corresp.put("applet", "Applet");
		corresp.put("area", "Area");
		corresp.put("base", "Base");
		corresp.put("basefont", "BaseFont");
		corresp.put("body", "Body");
		corresp.put("br", "BR");
		corresp.put("button", "Button");
		corresp.put("dir", "Directory");
		corresp.put("div", "Div");
		corresp.put("dl", "DList");
		corresp.put("fieldset", "FieldSet");
		corresp.put("font", "Font");
		corresp.put("form", "Form");
		corresp.put("frame", "Frame");
		corresp.put("frameset", "FrameSet");
		corresp.put("head", "Head");
		corresp.put("h1", "Heading");
		corresp.put("h2", "Heading");
		corresp.put("h3", "Heading");
		corresp.put("h4", "Heading");
		corresp.put("h5", "Heading");
		corresp.put("h6", "Heading");
		corresp.put("hr", "HR");
		corresp.put("html", "Html");
		corresp.put("iframe", "IFrame");
		corresp.put("img", "Image");
		corresp.put("input", "Input");
		corresp.put("isindex", "IsIndex");
		corresp.put("label", "Label");
		corresp.put("legend", "Legend");
		corresp.put("li", "LI");
		corresp.put("link", "Link");
		corresp.put("map", "Map");
		corresp.put("menu", "Menu");
		corresp.put("meta", "Meta");
		corresp.put("ins", "Mod");
		corresp.put("del", "Mod");
		corresp.put("object", "Object");
		corresp.put("ol", "OList");
		corresp.put("optgroup", "OptGroup");
		corresp.put("option", "Option");
		corresp.put("p", "Paragraph");
		corresp.put("param", "Param");
		corresp.put("pre", "Pre");
		corresp.put("q", "Quote");
		corresp.put("script", "Script");
		corresp.put("select", "Select");
		corresp.put("style", "Style");
		corresp.put("caption", "TableCaption");
		corresp.put("td", "TableCell");
		corresp.put("col", "TableCol");
		corresp.put("table", "Table");
		corresp.put("tr", "TableRow");
		corresp.put("thead", "TableSection");
		corresp.put("tfoot", "TableSection");
		corresp.put("tbody", "TableSection");
		corresp.put("textarea", "TextArea");
		corresp.put("title", "Title");
		corresp.put("ul", "UList");

	}

	/**
	 * Try to convert a Mozilla DOM node into W3C DOM element.
	 *
	 * @param nsNode        node to convert into W3C DOM element.
	 * @return      W3C HTML element corresponding to a Mozilla DOM node.
	 */
	public HTMLElement getConcreteNode(nsIDOMNode nsNode) {

		// Only converts element nodes. If the mozilla node
		// isn't a Mozilla DOM element, we cannot convert into
		// an W3C DOM element
		if (nsNode.getNodeType() == nsIDOMNode.ELEMENT_NODE) {

			// We use a hashmap to obtain element names from node names
			String htmlElementType = corresp.get(nsNode.getNodeName()
					.toLowerCase());

			// If we don't know the element type, we cannot transform
			// that node into W3C DOM element
			if(htmlElementType == null){
				return null;
			}

			// Compose the class name for the Mozilla DOM element.
			String nsClassName = "org.mozilla.interfaces.nsIDOMHTML"
					+ htmlElementType + "Element";

			// Compose the field name for the element IID
			String nsFieldInterfaceName = "NS_IDOMHTML"
					+ htmlElementType.toUpperCase() + "ELEMENT_IID";

			try {
				// Once we have their names, obtain the class and the field
				Class nsClass = Class.forName(nsClassName);
				Field field = nsClass.getField(nsFieldInterfaceName);

				// Get the field value (is a static field, so the argumentis ignored)
				String iid = (String) field.get(null);

				// Get the appropriate node interface
				Object nsElement = nsNode.queryInterface(iid);

				// Build the W3C DOM Element implementation class name
				// (the package org.mozilla.dom.html contains concrete implementations
				// for the W3C HTML element interfaces)
				String w3cClassName = "org.mozilla.dom.html.HTML"
						+ htmlElementType + "ElementImpl";

				// Obtain the class for the corresponding W3C DOM Element implementation
				Class w3cClass = Class.forName(w3cClassName);

				// Extract the method that must be invoked to transform the element
				Method creationMethod = w3cClass.getMethod("getDOMInstance", nsClass);

				// Invokes getDOMInstance method of corresponding W3C HTML element
				//  which returns an instance of corresponding W3C HTML element
				HTMLElement node = (HTMLElement) creationMethod.invoke(null, nsElement);
				return node;

			} catch (Exception e) {
//				throw new Error(e);
			}
		}

		return null;
	}
}