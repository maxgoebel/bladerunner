package at.tuwien.dbai.bladeRunner.editors.html;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The <code>FloatingDiv</code> class represents a low level implementation for
 * a floating div. It provides methods to insert and update the specified
 * floating div.
 *
 * @see org.mozilla.navigator.ui.editors.navigation.wrap.marking.RectangleSelection
 *
 * @author Ondrej Jaura
 */
public class FloatingDiv {

    /**
     * The common prefix for all divs created with this class.
     */
    public static final String WEBLEARN_ID_PREFIX = "__org_weblearn_floating_div";

    private Rectangle rectangle;
    private RGB backgroundColor, borderColor;
    private String backgroundColorText, borderColorText;
    private int zIndex;
    private float opacity;

    private Element divElement, parentElement;

    /**
     * 
     * Creates an instance of the <code>FloatingDiv</code> class.
     *
     * @param aRectangle a rectangle.
     * @param aBackgroundColor a background color.
     * @param aBorderColor a border color.
     * @param aZIndex a z-index.
     * @param anOpacity an opacity. Negative number means no opacity will be set.
     */
    public FloatingDiv(Rectangle aRectangle, RGB aBackgroundColor,
        RGB aBorderColor, int aZIndex, float anOpacity)
    {
        rectangle = aRectangle;
        backgroundColor = aBackgroundColor;
        borderColor = aBorderColor;
        zIndex = aZIndex;
        opacity = anOpacity;

        backgroundColorText = null;
        borderColorText = null;
    }

    /**
     * 
     * Creates an instance of the <code>FloatingDiv</code> class.
     *
     * @param aRectangle a rectangle.
     * @param aBackgroundColor a background color. E.g. <code>#000000</code> or
     * <code>silver</code>.
     * @param aBorderColor a border color.  E.g. <code>#000000</code> or
     * <code>silver</code>.
     * @param aZIndex a z-index.
     * @param anOpacity an opacity. Negative number means no opacity will be set.
     */
    public FloatingDiv(Rectangle aRectangle, String aBackgroundColor,
            String aBorderColor, int aZIndex, float anOpacity)
    {
        rectangle = aRectangle;
        zIndex = aZIndex;
        opacity = anOpacity;
        backgroundColorText = aBackgroundColor;
        borderColorText = aBorderColor;
    }

    /**
     * 
     * Creates a new floating div with the specified document to the specified
     * parent element with the specified id. It returns the element of the
     * inserted floating div.
     *
     * <p>It also updates the inserted floating div.</p>
     *
     * @param document a document where the floating div will be created.
     * @param aParentElement an element where the floating div will be inserted.
     * @param id an id.
     *
     * @return the element of the inserted floating div.
     *
     * @see #update()
     */
    public Element insert(Element aParentElement, String id)
    {
        parentElement = aParentElement;
        Document doc = aParentElement.getOwnerDocument();
        divElement = doc.createElement("div");
        divElement.setAttribute("id", WEBLEARN_ID_PREFIX +  id);
        parentElement.appendChild(divElement);

        update();

        return divElement;
    }

    /**
     * 
     * Updates the specified floating div element with
     * new rectangle coordinates.
     *
     * @param divElement a floating div element.
     * @param newRectangle a new rectangle.
     *
     * @throws NoFloatingDivElement if no element has been inserted.
     *
     * @see #update()
     * @see #insert(nsIDOMDocument, nsIDOMElement, String)
     */
    public void update(Rectangle newRectangle)
    {
        rectangle = newRectangle;
        update();
    }

    /**
     * 
     * Updates the specified floating div element.
     *
     * @param divElement a floating div element.
     * @throws NoFloatingDivElement if no element has been inserted.
     *
     * @see #insert(nsIDOMDocument, nsIDOMElement, String)
     */
    public void update()
    {
        assert divElement!=null;

        //TODO how to set just the with and the height? javascript?
        divElement.setAttribute("style", "position: absolute;" +
            "z-index: " + zIndex + ";" +
            "top: " + rectangle.y + "px;" +
            "left: " + rectangle.x + "px;" +
            "width: " + rectangle.width  + "px;" +
            "height: " + rectangle.height + "px;" +
            "overflow: visible;" +
            "background-color: " +
                (backgroundColorText == null ?
                    colorToHTMLColor(backgroundColor) :
                    backgroundColorText) +
                ";" +
            "/*background-color: transparent;*/" +
            "border: " +
                (borderColorText == null ?
                    colorToHTMLColor(borderColor):
                    borderColorText) +
                " 1px solid;" +
            "padding: 0px;" +
            ( opacity < 0 ? "" : "opacity: " + opacity + ";") );
    }

    /**
     * 
     * Removes the already inserted floating div element.
     *
     * @throws NoFloatingDivElement if no element has been inserted.
     */
    public void remove()
    {
        assert divElement!=null;

        parentElement.removeChild(divElement);

        divElement = null;
        parentElement = null;
    }

    /**
     * 
     * Translate a RGB color to a HTML color.
     * 
     * @param c
     * @return
     */
    public static String colorToHTMLColor(RGB c)
    {
        return
            String.format("#%02x%02x%02x",
                          c.red, c.green, c.blue);
    }

}//FloatingDiv