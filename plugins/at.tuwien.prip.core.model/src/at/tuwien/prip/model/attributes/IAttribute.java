/**
 *
 */
package at.tuwien.prip.model.attributes;

import org.w3c.dom.Element;

import at.tuwien.prip.model.attributes.AttributeFeatureFactory.Attr_Type;


/**
 * 
 * IAttributeFeature.java
 *
 *
 *
 * Created: Apr 26, 2009 6:05:01 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public interface IAttribute 
{
    /**
     * Returns true if element e qualifies 'this' attribute.
     * @param other
     * @return
     */
    public boolean test (Element e) throws AttributeNotSupportedException;
    /**
     * Returns the type of the feature
     * @return
     */
    public Attr_Type getType ();
    /**
     * Checks if the feature is valid by some means.
     * @return
     */
    public boolean isValidAttribute ();
    /**
     * Returns if two attributes are equal.
     * @param attr
     * @return
     */
    public boolean equals (IAttribute attr);
    /**
     * Prints the attribute.
     * @return
     */
    public void print ();
    /**
     * Returns the string representation of the attribute.
     * @return
     */
    public String toString ();

    /**
     * Returns a new IAttributeFeature from merging 'this' with
     * the passed in feature. If no merging possible, returns null.
     * @param other
     * @return
     */
    public IAttribute mergeWith (IAttribute other);

}
