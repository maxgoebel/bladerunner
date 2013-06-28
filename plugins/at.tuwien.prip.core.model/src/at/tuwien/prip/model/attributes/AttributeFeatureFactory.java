/**
 * AttributeCondition
 */

package at.tuwien.prip.model.attributes;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import at.tuwien.prip.model.attributes.impl.*;
import at.tuwien.prip.model.attributes.impl.table.Attr_ExactTableCell;
import at.tuwien.prip.model.attributes.impl.tag.Attr_TagExactImage;
import at.tuwien.prip.model.attributes.impl.tag.Attr_TagLinkWithSubURL;
import at.tuwien.prip.model.attributes.impl.txt.Attr_TextAlphaNum;
import at.tuwien.prip.model.attributes.impl.txt.Attr_TextNoWS;
import at.tuwien.prip.model.attributes.impl.txt.Attr_TextSubset;
import at.tuwien.prip.model.attributes.impl.txt.Attr_TextTimeDate;

/**
 * 
 * AttributeFeatureFactory.java
 *
 *
 *
 * Created: Apr 27, 2009 2:20:16 AM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class AttributeFeatureFactory{


    /**
     *
     */
    public static final int MAX_VALUE_SIZE = 40;

    /**
     *
     */
    public enum Attr_Type {anyText, no_WS_Text, exactValue, exactTableCell, exactHeaderCell,
        subsetText, alphaNumText, time_dateText, exactImage, subURL, other};

//        private static Element sourceElement = null;

        /**
//         * Constructor
//         */
//        public AttributeFeatureFactory(Element sourceElement) {
////            this.sourceElement = sourceElement;
//        }

        /**
         * Builds the features for the sourceElement node.
         * @return
         */
        public static List<IAttribute> buildFeatures (Element sourceElement) {

            if (sourceElement==null) return null;

            List<IAttribute> featureList = new LinkedList<IAttribute>();

            /**
             * begin with all attributes of the node.
             */
            NamedNodeMap nnm = sourceElement.getAttributes();
//			ErrorDump.debug(this, "node has "+nnm.getLength()+" attributes");
            for (int i=0; i<nnm.getLength(); i++) {
                Node attr = nnm.item(i);
                if (attr.getNodeType()==Node.ELEMENT_NODE) {
                	featureList.addAll(getAllAttributeFeatures((Element)attr));
                }
            }

            /**
             * table features.
             */
            IAttribute testFeature = new Attr_ExactTableCell(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

//			testFeature = new Attr_ExactHeaderCell(sourceElement);
//			if (testFeature.isValidFeature())
//				featureList.add(testFeature);

            /**
             * textcontent features.
             */
            testFeature = new Attr_TextNoWS(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

            testFeature = new Attr_TextSubset(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

            testFeature = new Attr_TextAlphaNum(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

            testFeature = new Attr_TextTimeDate(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

            /**
             * Link and image features.
             */
            testFeature = new Attr_TagExactImage(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);
            testFeature = new Attr_TagLinkWithSubURL(sourceElement);
            if (testFeature.isValidAttribute())
                featureList.add(testFeature);

//			ErrorDump.debug(this, "++++++++++ featureList size: "+featureList.size());
//			for (int i=0; i<featureList.size(); i++) {
//				ErrorDump.debug(this, "Feature: "+featureList.get(i).getType());
//				featureList.get(i).print();
//			}
            return featureList;
        }

        /**
         * Returns all possible features for the attribute 'attr'.
         * These are exactValue, and existence features.
         * @param attr
         * @return
         */
        private static List<IAttribute> getAllAttributeFeatures(Element attr) {
            List<IAttribute> attList = new LinkedList<IAttribute>();
            attList.add(new Attr_Exists(attr));
            if (attr.getNodeValue()!=null)
                attList.add(new Attr_ExistsWithExactValue(attr));
            return attList;
        }
}
