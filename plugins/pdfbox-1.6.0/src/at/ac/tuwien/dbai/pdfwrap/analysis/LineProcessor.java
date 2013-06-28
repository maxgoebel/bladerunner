package at.ac.tuwien.dbai.pdfwrap.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.dbai.pdfwrap.utils.SegmentUtils;
import at.ac.tuwien.dbai.pdfwrap.utils.Utils;
import at.tuwien.prip.model.document.segments.CharSegment;
import at.tuwien.prip.model.document.segments.CompositeSegment;
import at.tuwien.prip.model.document.segments.TextLine;
import at.tuwien.prip.model.document.segments.TextSegment;
import at.tuwien.prip.model.document.segments.fragments.LineFragment;
import at.tuwien.prip.model.document.segments.fragments.TextFragment;
import at.tuwien.prip.model.graph.comparators.XYTextComparator;

/**
 * Methods to find lines of text from fragments on a page
 * and within text blocks/candidate clusters
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class LineProcessor
{


	/**
	 * 
	 * (take LineFragment -- find line)
	 * take TextFragment OR any other TextSegment(e.g. Char) -- create LineFragments and Lines
	 * 
	 */
	public static List<TextLine> findLinesFromLineFragments (
			List<LineFragment> textBlocks, float maxX, boolean postNG, boolean ignoreFontsize)
	{
		List<TextLine> retVal = new ArrayList<TextLine>();

		List<CompositeSegment<? extends TextSegment>> foundLines = 
			findLines(textBlocks, maxX, postNG, ignoreFontsize);

		for (CompositeSegment<? extends TextSegment> cs : foundLines)
		{
			TextLine tl = new TextLine();
			tl.getItems().addAll((List<? extends LineFragment>) cs.getItems());
			tl.setCalculatedFields(cs);

			retVal.add(tl);
		}

		return retVal;
	}

	/**
	 * 
	 * @param textBlocks
	 * @param maxX
	 * @param postNG
	 * @param ignoreFontsize
	 * @return
	 */
	public static List<TextLine> findLinesFromTextFragments (
			List<TextFragment> textBlocks, float maxX, boolean postNG, boolean ignoreFontsize)
	{
		List<TextLine> retVal = new ArrayList<TextLine>();

		List<CompositeSegment<? extends TextSegment>> foundLines = 
			findLines(textBlocks, maxX, postNG, ignoreFontsize);

		for (CompositeSegment<? extends TextSegment> cs : foundLines)
		{
			LineFragment lf = new LineFragment();
			lf.getItems().addAll((List<? extends TextFragment>) cs.getItems());
			lf.setCalculatedFields(cs);

			TextLine tl = new TextLine();
			tl.getItems().add(lf);
			tl.setCalculatedFields(lf);

			retVal.add(tl);
		}

		return retVal;
	}

	/**
	 * 
	 * @param textBlocks
	 * @param maxX
	 * @param postNG
	 * @param ignoreFontsize
	 * @return
	 */
	public static List<TextLine> findLinesFromCharacters (
			List<CharSegment> textBlocks, float maxX, boolean postNG, boolean ignoreFontsize)
	{
		List<TextLine> retVal = new ArrayList<TextLine>();

		List<CompositeSegment<? extends TextSegment>> foundLines = 
			findLines(textBlocks, maxX, postNG, ignoreFontsize);

		for (CompositeSegment<? extends TextSegment> cs : foundLines)
		{
			TextFragment tf = new TextFragment();
			tf.getItems().addAll((List<? extends CharSegment>) cs.getItems());
			tf.setCalculatedFields(cs);

			LineFragment lf = new LineFragment();
			lf.getItems().add(tf);
			lf.setCalculatedFields(tf);

			TextLine tl = new TextLine();
			tl.getItems().add(lf);
			tl.setCalculatedFields(lf);

			retVal.add(tl);
		}

		return retVal;
	}

	/**
	 * 
	 * @param textBlocks
	 * @param maxX
	 * @param postNG
	 * @param ignoreFontsize
	 * @return
	 */
	public static List<TextLine> findLinesFromTextLines(List<TextLine> textBlocks, 
			float maxX, boolean postNG, boolean ignoreFontsize)
			{
		List<TextLine> retVal = new ArrayList<TextLine>();

		List<CompositeSegment<? extends TextSegment>> foundLines = 
			findLines(textBlocks, maxX, postNG, ignoreFontsize);

		for (CompositeSegment<? extends TextSegment> cs : foundLines)
		{
			TextLine tl = new TextLine();
			for (TextSegment ts : cs.getItems())
			{
				TextLine tl2 = (TextLine)ts;
				tl.getItems().addAll(tl2.getItems());
			}
			tl.setCalculatedFields(cs);

			retVal.add(tl);
		}

		return retVal;
			}

	// 2011-01-26: changed to public -- called directly by CandidateCluster.findLines()
	/**
	 * 
	 */
	public static List<CompositeSegment<? extends TextSegment>> findLines(
			List<? extends TextSegment> textBlocks, float maxX, boolean postNG, boolean ignoreFontsize) //throws Exception
		{
		// TODO: support super/subscript natively -- or allow misc segments ...

		// pre: textBlocks in collection must be sorted in y-then-x order
		Collections.sort(textBlocks, new XYTextComparator());

		// pre: all items in textBlocks must be TextPosition objects
		// TODO: create a specific exception here

		List<CompositeSegment<? extends TextSegment>> retVal = 
			new ArrayList<CompositeSegment<? extends TextSegment>>();

		TextSegment lastBlock = null;
		List<TextSegment> newItems = new ArrayList<TextSegment>();

		// variables for controlling new line objects to be added
		// these can be generated later -- not for the preNG cluster
		//String newString = "";

		boolean merge = false;

		Iterator<?> iter = textBlocks.iterator();
		while (iter.hasNext())
		{
			TextSegment thisBlock = null;

			// if empty text block, try again :)
			// (required so that empty text blocks do not interfere with processing)
			while (iter.hasNext() && (thisBlock == null || thisBlock.isEmpty()))
			{
				thisBlock = (TextSegment)iter.next();
			}

			boolean skip = false;
			if (lastBlock != null)
			{		
				
				//if a new line is started
				if (!sameLine(lastBlock, thisBlock, maxX, postNG, ignoreFontsize)) 
				{
					String txt = thisBlock.getText();
					
					if (txt!=null && txt.length()<=2) 
					{
						String output = "";						
						char[] charArray = txt.toCharArray();

						for (int i = 0; i < charArray.length; ++i) {
							char a = charArray[i];
							if ((int) a > 255) {
//								String f = Integer.toHexString((int) a);
								output += " * ";//\\u" + Integer.toHexString((int) a);
							} else {
								output += a;
							}
						}
						if (output.matches("^[\\p{Punct}]+$")) {
							skip = true;
						}
						
					}
				}
				
				// should return null if no lastBlock...?
				if (!skip && sameLine(lastBlock, thisBlock, maxX, postNG, ignoreFontsize)) // we "merge"
				{
					// TODO: delete!
					// System.out.println("merging " + newString + " withspace " + thisBlock.getCharacter());
					if (merge)
					{
						newItems.add(thisBlock);
					}
					else
					{
						newItems = new ArrayList<TextSegment>();
						newItems.add(thisBlock);
						merge = true;
					}
				}
				else // we don't merge
				{
					// TODO: add all sub-objects, and fix font! (not null)
					CompositeSegment<TextSegment> newLine = new CompositeSegment<TextSegment>();
					newLine.setItems(newItems);
					newLine.setCalculatedFields();
					retVal.add(newLine);

					// nothing to merge with =>
					// simply assign all new variables
					newItems = new ArrayList<TextSegment>();
					newItems.add(thisBlock);
					// TODO: replace with a proper average (mode?)
					// newFontSize = fontSize;
					//first = false;
//					if (!skip) {
//						merge = true;
//					} else {
//						merge = false;
//					}
					merge = true;
				}                           

			}
			else
			{
				// nothing to merge with =>
				// simply assign all new variables
				newItems = new ArrayList<TextSegment>();
				newItems.add(thisBlock);
				// TODO: replace with a proper average (mode?)
				// newFontSize = fontSize;
				//first = false;
				merge = true;
			}
			
			if (!skip) {
				lastBlock = thisBlock;
			}
			//first = false;
		}

		// add last block if appropriate

		if (newItems.size() > 0)
		{
			CompositeSegment<TextSegment> newLine = new CompositeSegment<TextSegment>();
			newLine.setItems(newItems);
			newLine.setCalculatedFields();
			retVal.add(newLine);
		}
		return retVal;
			}

	// TODO: rewrite to make clearer -- it all works with TextSegments now!
	private static boolean sameLine(TextSegment lastBlock, TextSegment thisBlock, float maxX, boolean postNG, boolean ignoreFontsize)
	{
		// added 12.06.07
		if(thisBlock.getX1() < lastBlock.getXmid()) return false;

		if (postNG) return (SegmentUtils.vertIntersect(lastBlock, thisBlock.getYmid()) ||
				SegmentUtils.vertIntersect(thisBlock, lastBlock.getYmid()));
		// problem with atomic line finding on tm_03dec08_p04z.pdf
		// changed 4.05.09
		//GenericSegment.vertIntersect(lastBlock, thisBlock);

		float fontSize;
		boolean sameFontSize;
		boolean xGuard;

		if (lastBlock instanceof TextSegment && thisBlock instanceof TextSegment)
		{
			fontSize = (lastBlock.getFontSize() + 
					thisBlock.getFontSize()) / 2.0f;
			sameFontSize = Utils.within(lastBlock.getFontSize(), 
					thisBlock.getFontSize(), 
					fontSize * 0.15f);
			//System.out.println("fontSize: " + fontSize + " maxX: " + maxX + " product: " + fontSize * maxX);
			xGuard = Utils.within(lastBlock.getX2(), thisBlock.getX1(), fontSize * maxX);
		}
		else
		{
			// completely nonsensical to line-find on GenericSegments(!)
			fontSize = -1.0f;
			sameFontSize = false;
			xGuard = false;
		}

		if (ignoreFontsize) sameFontSize = true;

		// joinVision: was 0.1f; changed to 0.25f (Bernd Kemmler)
		return (Utils.within(lastBlock.getY1(), thisBlock.getY1(), fontSize * Utils.sameLineTolerance)
				//&& !crosses(lastBlock, thisBlock, pageDivs)
				&& sameFontSize
				&& xGuard);
	}
}
