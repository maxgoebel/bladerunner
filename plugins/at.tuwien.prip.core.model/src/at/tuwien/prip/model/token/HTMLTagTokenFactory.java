/**
 *
 */
package at.tuwien.prip.model.token;


/**
 * 
 * HTMLTagTokenFactory.java
 *
 *
 *
 * Created: Apr 26, 2009 6:04:00 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class HTMLTagTokenFactory {

	/**
	 *
	 */
	public static boolean useIndices = true;

	/**
	 *
	 */
	public static int attributeSelection = 2;


	// 0 = no attributes
	// 1 = plain attributes
	// 2 = +path features
	// 3 = +path +text
//
//	/**
//	 * 
//	 * Creates a generalized HTML_Token (over the example set).
//	 * 
//	 * @param e
//	 * @param benchCol
//	 */
//	public static HTMLTagToken createToken (
//			Element e, 
//			DocumentCollection benchCol) 
//	{
//
//		HTMLTagToken newTok = new HTMLTagToken(e, benchCol, useIndices, attributeSelection);
//		if (benchCol!=null) {
//			for (HTMLTagToken tok : tokenize(benchCol)) {
//				if (newTok.compareTo(tok)==0) {// if generalisation-consistent
//					return tok;
//				}
//			}
//		}
//		return newTok;
//	}

//	/**
//	 * 
//	 * Tokenize a document recursively *WITH* example-generalization.
//	 * 
//	 * @param e
//	 * @return
//	 */
//	public static HTMLTokenSequence tokenizeDoc (
//			Element e, 
//			DocumentCollection benchCol) 
//	{
//		if (e==null) return null;
//		HTMLTokenSequence tokList = new HTMLTokenSequence();
//
//		tokList.add(createToken(e, benchCol));
//		tokenizeDocRec(e, benchCol, tokList);
//		return tokList;
//	}

//	/**
//	 * 
//	 * Tokenize a document recursively *WITH* example-generalization.
//	 * 
//	 * @param e
//	 * @param tokList
//	 */
//	private static void tokenizeDocRec (
//			Element e,
//			DocumentCollection benchCol,
//			HTMLTokenSequence tokList) 
//	{
//		if (DOMHelper.Tree.Children.getChildElementCount(e)>0) {
//			List<Element> children =
//				DOMHelper.Tree.Children.getChildElements(e);
//			for (Element child : children) {
//				tokList.add(createToken(child, benchCol));
//				tokenizeDocRec(child, benchCol, tokList);
//			}
//		}
//	}

//	/**
//	 *
//	 * Tokenize a document collection.
//	 * 
//	 * @param collection, a document collection
//	 * @return list of tokens
//	 */
//	public static HTMLTokenSequence tokenize (DocumentCollection collection) {
//		return tokenize (collection, null);
//	}
//
//	/**
//	 *
//	 * Tokenize a document collection.
//	 *
//	 * @param collection
//	 * @param isPositive
//	 * @return
//	 */
//	public static HTMLTokenSequence tokenize (DocumentCollection collection, boolean isPositive) 
//	{
//		BinaryClass exType;
//		if (isPositive)
//			exType=BinaryClass.POSITIVE_LITERAL;
//		else
//			exType=BinaryClass.NEGATIVE_LITERAL;
//		return tokenize (collection, exType);
//	}
//
//	/**
//	 * 
//	 * Flatten a subtree structure specified by a
//	 * root element into an ordered array of HTMLTagToken
//	 * objects.
//	 * 
//	 * @param selection
//	 * @param indices
//	 * @return
//	 */
//	public static HTMLTokenSequence tokenize (Element root, boolean indices) 
//	{
//		HTMLTokenSequence result = new HTMLTokenSequence();
//		Stack<Node> stack = new Stack<Node>();
//		stack.push(root);
//
//		while (!stack.isEmpty()) {
//			Node n = stack.pop();
//			if (n.getNodeType()==Node.ELEMENT_NODE) {
//				result.add(new HTMLTagToken((Element) n, indices));
//			}
//
//			List<Element> children = 
//				DOMHelper.Tree.Children.getProperTagChildElements((Element) n);
//			for (Element child : children) {
//				stack.push(child);
//			}
//		}
//		return result;
//	}
	
//	/**
//	 * 
//	 * Flatten a subtree structure specified by a
//	 * selection into an ordered array of HTMLTagToken
//	 * objects.
//	 * 
//	 * @param selection
//	 * @param indices
//	 * @return
//	 */
//	public static HTMLTokenSequence tokenize (Selection selection, boolean indices) 
//	{
//		HTMLTokenSequence result = new HTMLTokenSequence();
//		List<Element>elements = ModelUtils.getSelectedElements(selection);
//		for (Element element : elements) {
//			result.append(tokenize(element, indices));
//		}
//		return result;
//	}
	
//	/**
//	 * 
//	 * Flatten a document into an ordered array of 
//	 * HTMLTagToken objects.
//	 * 
//	 * @param doc
//	 * @param indices
//	 * @return
//	 */
//	public static HTMLTokenSequence tokenize (
//			Document doc, boolean indices) 
//	{
//		return tokenize(doc.getDocumentElement(), indices);
//	}
	
//	/**
//	 * 
//	 * Tokenize all examples in the exampleSet:
//	 * based on their paths, all attributes of each example
//	 * are grouped and generalized, accordingly. If exType
//	 * is set to null, both positive and negative examples
//	 * are used.
//	 * 
//	 * @param collection
//	 * @return
//	 */
//	private static HTMLTokenSequence tokenize (
//			DocumentCollection collection, 
//			BinaryClass exType) 
//	{
//		HTMLTokenSequence result = new HTMLTokenSequence();
////		if (es==null) return result;
//
////		Map<String, List<Example>> path2Example =
////		new HashMap<String, List<Example>>();
//
////		/**
////		* generalize the example set
////		*/
////		// group here according to path
////		for (ExampleDocument ed : (List<ExampleDocument>)es.getDocuments()) {
////		for (Example ex : (List<Example>)ed.getUserExamples()) {
////		List<Element> elements = ex.getSelectedElements();
//
////		/*
////		* For each example, this should normally be 1.
////		* Check how to handle multislot examples...
////		*/
////		for (Element e : elements) {
////		if (ex.getType()==exType || exType==null) {
//
////		String path =
////		DOMHelper.XPath.getExactXPath(
////		e,
////		false, // never use indices for PATH grouping!!!
////		false);
//
////		List<Example> val = path2Example.get(path);
////		if (val==null) val = new LinkedList<Example>();
//
////		// make sure that only equal tag names in list...
////		if (val.size()>0) {
////		if (val.get(val.size()-1).getSelectedElement().getNodeName().
////		compareTo(ex.getSelectedElement().getNodeName())==0) {
////		val.add(ex);
////		path2Example.put(path, val);
////		}
////		}
////		else {
////		val.add(ex);
////		path2Example.put(path, val);
////		}
////		}
////		}
////		}
////		}
//
////		Map<String, Map<String, Pair<List<String>, List<String>>>> dblMap =
////		new HashMap<String, Map<String, Pair<List<String>, List<String>>>>();
//
////		// generalize each attribute per path grouping
////		for (String p : path2Example.keySet()) {
//
////		// initialize the double map...
////		Map<String, Pair<List<String>, List<String>>> attName2Vals =
////		dblMap.get(p);
////		if (attName2Vals==null) {
////		attName2Vals =
////		new HashMap<String, Pair<List<String>, List<String>>>();
////		dblMap.put(p, attName2Vals);
////		}
////		// populate each list entrance of attName2Vals
////		for (Example ex : path2Example.get(p)) {
////		Element e = ex.getSelectedElement();
////		NamedNodeMap nnm = e.getAttributes();
////		for (int i=0; i<nnm.getLength(); i++) {
////		Node n = nnm.item(i);
////		String aName = n.getNodeName();
////		Pair<List<String>, List<String>> lPair =
////		attName2Vals.get(aName);
////		if (lPair==null) {
////		lPair = new Pair<List<String>, List<String>> (
////		new LinkedList<String>(),
////		new LinkedList<String>());
////		attName2Vals.put(aName, lPair);
////		}
////		if (ex.getType()==ExampleType.POSITIVE_LITERAL) {
////		lPair.getFirst().add(n.getNodeValue());
////		}
////		else {
////		lPair.getSecond().add(n.getNodeValue());
////		}
////		}
////		// add path feature
////		if (attributeSelection>1) {
////		Pair<List<String>, List<String>> pathPair =
////		attName2Vals.get("locFeat");
////		if (pathPair==null){
////		pathPair = new Pair<List<String>, List<String>> (
////		new LinkedList<String>(),
////		new LinkedList<String>());
////		attName2Vals.put("locFeat", pathPair);
////		}
////		if (ex.getType()==ExampleType.POSITIVE_LITERAL) {
////		pathPair.getFirst().add(
////		DOMHelper.XPath.getExactXPath(e, useIndices, false));
////		}
////		else {
////		pathPair.getSecond().add(
////		DOMHelper.XPath.getExactXPath(e, useIndices, false));
////		}
////		// add textContent feature
////		if (attributeSelection>2) {
////		Pair<List<String>, List<String>> textPair =
////		attName2Vals.get("txtFeat");
////		if (textPair==null){
////		textPair = new Pair<List<String>, List<String>> (
////		new LinkedList<String>(),
////		new LinkedList<String>());
////		attName2Vals.put("txtFeat", textPair);
////		}
////		if (ex.getType()==ExampleType.POSITIVE_LITERAL) {
////		textPair.getFirst().add(
////		e.getTextContent());
////		}
////		else {
////		textPair.getSecond().add(
////		e.getTextContent());
////		}
////		}
////		}
////		}
////		}
//
////		// tie everything together
////		for (String p : dblMap.keySet()) {
//
////		// take first example in list (shld all be the same tag names!!)
////		Element firstTag =
////		path2Example.get(p).get(0).getSelectedElement();
////		assert firstTag!=null;
//
////		String tag_name = firstTag.getNodeName();
////		Map<String, lxValueDef> lxList =
////		new HashMap<String, lxValueDef>();
//
////		for (String att_name : dblMap.get(p).keySet()) {
//
////		List<String> poss =
////		dblMap.get(p).get(att_name).getFirst();
////		List<String> negs =
////		dblMap.get(p).get(att_name).getSecond();
//
////		lxValueDef lvd = lxValueDefFactory.generate(
////		tag_name,
////		att_name,
////		poss,
////		negs);
//
////		if(lvd!=null)
////		lxList.put(att_name, lvd);
////		}
//
////		HTML_Token tok =
////		new HTML_Token(firstTag, useIndices, lxList);
////		result.add(tok);
////		}
//		return result;
//	}

	/**
	 * 
	 * Returns a window from the input list, where the
	 * element from 'tokens' at 'middlePos' is in the middle
	 * of window.
	 * 
	 * @param tokens
	 * @param middlePos
	 * @param k, the window size
	 */
	public static HTMLTokenSequence getWindowAround(
			HTMLTokenSequence tokens, 
			int middlePos,
			int k)
	{
		assert k%2==1;
		int half_win = k/2;
		assert middlePos>=0 && middlePos<tokens.length();

		int from = middlePos-half_win;
		int addStart = Math.max(-from, 0);
		from = Math.max(0, from);

		int to = middlePos+half_win;
		int addEnd = Math.max(to-tokens.length()+1, 0);
		to = Math.min(to+1, tokens.length());

		HTMLTokenSequence win = 
			(HTMLTokenSequence) tokens.subSequence(from, to);

		for (int i=0; i<addStart; i++) {
			win.prepend(HTMLTagToken.SENTINEL);
		}
		for (int i=0; i<addEnd; i++) {
			win.add(HTMLTagToken.SENTINEL);
		}
		assert win.length()==k;

		return win;
	}

} //HTML_Tokenizer
