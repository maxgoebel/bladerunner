package at.tuwien.dbai.bladeRunner.utils;
import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.atf.mozilla.ide.core.XPCOMThreadProxy;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.mozilla.interfaces.nsIBoxObject;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMElementCSSInlineStyle;
import org.mozilla.interfaces.nsIDOMNSDocument;


/*
 * This class manages the creation of Jobs that control the flashing of the
 * DIV. It appends the flashing DIV to the current Document.
 */
public class SelectionBox2
{
	static final String DIV_NS="http://www.w3.org/1999/xhtml";

	//number of times that the SelectionBox will flash
	protected static int TOTAL_FLASH_COUNT = 3;

	//color used during flashing
	protected static String FLASH_COLOR = "#FF0000";

	//color use during hover
	protected static String HOVER_COLOR = "#0000FF";

	//width of the selection box
	protected static int BOX_WIDTH = 2;

	//delay between flash on and off
	protected static int DELAY = 250;

	//the Z level where the SelectionBox is rendered
	/*
	 * From mozilla code we can assume that the Max int is the max value of z-index
	 * http://lxr.mozilla.org/seamonkey/source/toolkit/content/widgets/browser.xml#774
	 */
	protected static int BOX_ZINDEX = Integer.MAX_VALUE;

	protected nsIDOMElement mainDiv = null;
	protected nsIDOMCSSStyleDeclaration mainDivStyleDecl = null;

	protected nsIDOMElement northDiv, eastDiv, southDiv, westDiv = null;
	protected nsIDOMCSSStyleDeclaration northDivStyleDecl, eastDivStyleDecl, southDivStyleDecl, westDivStyleDecl = null;

	protected Object _lock = new Object(); //use to protect the check for isFlashing()
	protected FlasherJob currentFlashJob = null;

	public SelectionBox2( nsIDOMDocument document )
	{
		//create the flasher element
		mainDiv = document.createElementNS(DIV_NS,"DIV");
		mainDiv.setAttribute("id", MozIDEUIPlugin.ATF_INTERNAL + "_SelectionBox" );
		mainDiv.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL ); //used to filter out elements

		//htmlDocument.getBody().appendChild( mainDiv ); //adding to the body did not support framesets
		document.getDocumentElement().appendChild( mainDiv ); //adding to the root of the document (Mozilla still renders when outside of body)

		northDiv = document.createElementNS(DIV_NS, "DIV" );
		northDiv.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL ); //used to filter out elements
		mainDiv.appendChild( northDiv );

		eastDiv = document.createElementNS(DIV_NS, "DIV" );
		eastDiv.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL ); //used to filter out elements
		mainDiv.appendChild( eastDiv );

		southDiv = document.createElementNS(DIV_NS, "DIV" );
		southDiv.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL ); //used to filter out elements
		mainDiv.appendChild( southDiv );

		westDiv = document.createElementNS(DIV_NS, "DIV" );
		westDiv.setAttribute("class", MozIDEUIPlugin.ATF_INTERNAL ); //used to filter out elements
		mainDiv.appendChild( westDiv );

		mainDivStyleDecl = ((nsIDOMElementCSSInlineStyle)mainDiv.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID )).getStyle();

		northDivStyleDecl = ((nsIDOMElementCSSInlineStyle)northDiv.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID )).getStyle();
		eastDivStyleDecl = ((nsIDOMElementCSSInlineStyle)eastDiv.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID )).getStyle();
		southDivStyleDecl = ((nsIDOMElementCSSInlineStyle)southDiv.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID )).getStyle();
		westDivStyleDecl = ((nsIDOMElementCSSInlineStyle)westDiv.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID )).getStyle();

		initDIV();
	}

	protected void initDIV()
	{
		//set the initial CSS (i.e. borders)
		mainDivStyleDecl.setProperty( "position", "absolute", "" );
		mainDivStyleDecl.setProperty( "z-index", String.valueOf(SelectionBox2.BOX_ZINDEX), "" );
		mainDivStyleDecl.setProperty( "visibility", "hidden", "" );
		mainDivStyleDecl.setProperty( "width", "0px", "" );
		mainDivStyleDecl.setProperty( "height", "0px", "" );
		mainDivStyleDecl.setProperty( "overflow", "visible", "" );

		//set absolute position
		northDivStyleDecl.setProperty( "position", "absolute", "" );
		eastDivStyleDecl.setProperty( "position", "absolute", "" );
		southDivStyleDecl.setProperty( "position", "absolute", "" );
		westDivStyleDecl.setProperty( "position", "absolute", "" );

		//bounds
		northDivStyleDecl.setProperty( "left", "0px", "" );
		northDivStyleDecl.setProperty( "top", "0px", "" );
		//northDivStyleDecl.setProperty( "width", "0px", "" ); //dynamic
		northDivStyleDecl.setProperty( "height", SelectionBox2.BOX_WIDTH+"px", "" );

		//eastDivStyleDecl.setProperty( "left", "0px", "" ); //dynamic
		eastDivStyleDecl.setProperty( "top", "0px", "" );
		eastDivStyleDecl.setProperty( "width", SelectionBox2.BOX_WIDTH+"px", "" );
		//eastDivStyleDecl.setProperty( "height", "0px", "" ); //dynamic

		southDivStyleDecl.setProperty( "left", SelectionBox2.BOX_WIDTH+"px", "" );
		//southDivStyleDecl.setProperty( "top", "0px", "" ); //dynamic
		//southDivStyleDecl.setProperty( "width", "0px", "" ); //dynamic
		southDivStyleDecl.setProperty( "height", SelectionBox2.BOX_WIDTH+"px", "" );

		westDivStyleDecl.setProperty( "left", "0px", "" );
		westDivStyleDecl.setProperty( "top", SelectionBox2.BOX_WIDTH+"px", "" );
		westDivStyleDecl.setProperty( "width", SelectionBox2.BOX_WIDTH+"px", "" );
		//westDivStyleDecl.setProperty( "height", "0px", "" ); //dynamic


	}

	public void highlight( nsIDOMElement element, String color )
	{
		synchronized (_lock) {
			if( !isFlashing() ){

				Rectangle bounds = getElementBounds(element);
				positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
				colorDiv( color );
				showDiv();
			}
		}
	}

	public void highlight( nsIDOMElement element )
	{
		synchronized (_lock) {
			if( !isFlashing() ){

				Rectangle bounds = getElementBounds(element);
				positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
				colorDiv( SelectionBox2.HOVER_COLOR );
				showDiv();
			}
		}
	}

	public void hide(){
		synchronized (_lock) {
			if( !isFlashing() ){

				hideDiv();
			}
		}
	}

	public void flash( nsIDOMElement element ){

		synchronized (_lock) {

			if( isFlashing() ){
				currentFlashJob.cancel(); // cancel the currently running job
			}

			//create a new job
			currentFlashJob = new FlasherJob( "FLASHING DIV", mainDiv );

			Rectangle bounds = getElementBounds(element);
			positionDiv(bounds.x, bounds.y, bounds.width, bounds.height);
			colorDiv( SelectionBox2.FLASH_COLOR );

			currentFlashJob.setRule( mutexRule ); //avoid jobs to run concurrently (if there are multiple in the queue)
			currentFlashJob.setPriority( Job.INTERACTIVE );

			currentFlashJob.schedule();

		}
	}


	protected boolean isFlashing(){

		//should already have lock
		if( currentFlashJob == null ){
			return false;
		}
		else{
			return currentFlashJob.getState() == Job.WAITING | currentFlashJob.getState() == Job.SLEEPING | currentFlashJob.getState() == Job.RUNNING;

		}
	}

	protected void showDiv(){
		mainDivStyleDecl.setProperty( "visibility", "visible", "" );
	}

	protected void hideDiv(){
		mainDivStyleDecl.setProperty( "visibility", "hidden", "" );
	}

	protected void positionDiv( int x, int y, int width, int height ){

		//adjust for borders (DIVS)
		width-=SelectionBox2.BOX_WIDTH;
		height-=SelectionBox2.BOX_WIDTH;

		//check for negative values
		/*
if( x<0 )
x=0;
if( y<0 )
y=0;
		 */
		if( width<0 )
			width=0;
		if( height<0 )
			height=0;

		mainDivStyleDecl.setProperty( "left", x+"px", "important" );
		mainDivStyleDecl.setProperty( "top", y+"px", "important" );

		//bounds
		northDivStyleDecl.setProperty( "width", width+"px", "" ); //dynamic

		eastDivStyleDecl.setProperty( "left", width+"px", "" ); //dynamic
		eastDivStyleDecl.setProperty( "height", height+"px", "" ); //dynamic

		southDivStyleDecl.setProperty( "top", height+"px", "" ); //dynamic
		southDivStyleDecl.setProperty( "width", width+"px", "" ); //dynamic

		westDivStyleDecl.setProperty( "height", height+"px", "" ); //dynamic

	}

	protected void colorDiv( String color ){
		//set background
		northDivStyleDecl.setProperty( "background-color", color, "" );
		eastDivStyleDecl.setProperty( "background-color", color, "" );
		southDivStyleDecl.setProperty( "background-color", color, "" );
		westDivStyleDecl.setProperty( "background-color", color, "" );
//		mainDivStyleDecl.setProperty( "background-color", color, "" );

	}

	/*
	 * This method is used to determine the bounds of a DOM element in the
	 * rendered area. It used SCREEN based coordinates to determine the correct
	 * X,Y.
	 *
	 * This seems to be the only way to get correct Coordinates when the elements are
	 * inside a scrollable area.
	 *
	 * It is actually riding on the assumption that the ROOT tag is at 0,0 of the document.
	 *
	 * Calculations do not include MARGINS.
	 */
	protected Rectangle getElementBounds( nsIDOMElement element )
	{
		nsIDOMNSDocument nsdocument = (nsIDOMNSDocument)element.getOwnerDocument().queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );
		nsIBoxObject box = nsdocument.getBoxObjectFor( element );

		/*
		 * Getting the root element of the document (assuming HTML) and using its Screen coordinates as the origin.
		 */
		nsIDOMDocument rootdocument = mainDiv.getOwnerDocument(); //since the SelectionBox DIV is in the root document
		nsIDOMElement rootElement = rootdocument.getDocumentElement(); //should be the HTML element
		nsIDOMNSDocument rootnsdocument = (nsIDOMNSDocument)rootdocument.queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );
		nsIBoxObject rootBox = rootnsdocument.getBoxObjectFor( rootElement );
		int originX = rootBox.getScreenX();
		int originY = rootBox.getScreenY();

		return new Rectangle( (box.getScreenX()-originX), (box.getScreenY()-originY), box.getWidth(), box.getHeight() );//, getSite().getShell().getDisplay() );
	}

	/*
	 * @GINO
	 * EXPERIMENTING WITH ANOTHER WAY OF DOING THIS
	 */
	/*
protected Rectangle getElementBounds( nsIDOMElement element ){

Point p = new Point( 0, 0 );
getRootLocation( element, p );
nsIDOMNSDocument nsdocument = (nsIDOMNSDocument)element.getOwnerDocument().queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );
nsIBoxObject box = nsdocument.getBoxObjectFor( element );
return new Rectangle( p.x, p.y, box.getWidth(), box.getHeight() );//, getSite().getShell().getDisplay() );
}


protected void getRootLocation( nsIDOMElement element, Point p ){

if( element == null )
return;

nsIDOMNSDocument nsdocument = (nsIDOMNSDocument)element.getOwnerDocument().queryInterface( nsIDOMNSDocument.NS_IDOMNSDOCUMENT_IID );
nsIBoxObject box = nsdocument.getBoxObjectFor( element );

if( box != null ){
p.x += box.getX();
p.y += box.getY();
nsIDOMDocumentView documentView = (nsIDOMDocumentView)element.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
nsIDOMViewCSS cssView = (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );

nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( element, "" );
nsIDOMCSS2Properties cssProps = (nsIDOMCSS2Properties)computedStyle.queryInterface( nsIDOMCSS2Properties.NS_IDOMCSS2PROPERTIES_IID );

if( !"none".equals(cssProps.getBorderTopStyle()) ){
int topBorder = 0;
String topBorderString = cssProps.getBorderTopWidth();
if( topBorderString != null || !"".equals(topBorderString) ){
Pattern pattern = Pattern.compile("(-?[\\d]+)([a-z%]*)", Pattern.CASE_INSENSITIVE );

Matcher m = pattern.matcher(topBorderString);
if( m.matches() ){
topBorder = Integer.parseInt( m.group(1) );
}
}

p.y -= topBorder;
}

if( !"none".equals(cssProps.getBorderLeftStyle()) ){
int leftBorder = 0;
String leftBorderString = cssProps.getBorderTopWidth();
if( leftBorderString != null || !"".equals(leftBorderString) ){
Pattern pattern = Pattern.compile("(-?[\\d]+)([a-z%]*)", Pattern.CASE_INSENSITIVE );

Matcher m = pattern.matcher(leftBorderString);
if( m.matches() ){
leftBorder = Integer.parseInt( m.group(1) );
}
}

p.x -= leftBorder;
}


System.out.println("foo");


//nsIDOMElement parent = box.getParentBox();
//if( parent != element )
//  getRootLocation( parent, p );


//add borders into account
}


}
	 */

	/*
	 * This is an implementation of a Job used to Flash the DIV. It schedules
	 * itself in order to Flash ON and OFF.
	 */
	class FlasherJob extends Job
	{
		protected nsIDOMElement flashingDIV = null;
		protected nsIDOMCSSStyleDeclaration styleDecl = null;

		//used to call XPCOM calls in the UI thread
		protected XPCOMThreadProxyHelper proxyHelper = new XPCOMThreadProxyHelper( Display.getDefault() );

		protected int flashCount = SelectionBox2.TOTAL_FLASH_COUNT;
		public boolean isOn = false;

		public FlasherJob(String name, nsIDOMElement flashingDIV )
		{
			super(name);

			this.flashingDIV = (nsIDOMElement)XPCOMThreadProxy.createProxy( mainDiv, proxyHelper );;

//			nsIDOMViewCSS cssView = MozCssUtils.getCSSView(flashingDIV);
//			nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( flashingDIV, "" );
//			this.styleDecl = computedStyle;

			nsIDOMElementCSSInlineStyle flasherElementStyles =
					(nsIDOMElementCSSInlineStyle)
					this.flashingDIV.queryInterface( nsIDOMElementCSSInlineStyle.NS_IDOMELEMENTCSSINLINESTYLE_IID );

			this.styleDecl = flasherElementStyles.getStyle();
		}

		protected IStatus run(IProgressMonitor monitor)
		{
			//System.out.println( flashCount );
			if( isOn ){
				flashOff();
				flashCount--; //decrement after turning off
			}
			else
				flashOn();

			if( flashCount > 0 )
				schedule( SelectionBox2.DELAY );

			return Status.OK_STATUS;
		}

		private void flashOn()
		{
			styleDecl.setProperty( "visibility", "visible", "" );
			isOn = true;
		}

		private void flashOff()
		{
			styleDecl.setProperty( "visibility", "hidden", "" );
			isOn = false;
		}
	};

	//avoid concurrent runs
	protected ISchedulingRule mutexRule = new ISchedulingRule(){

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

	};

	/*
	 * Implementation of IXPCOMThreadProxyHelper to make sure XPCOM class happen
	 * on the Display thread.
	 */
	class XPCOMThreadProxyHelper implements IXPCOMThreadProxyHelper {
		private Display _display;

		public XPCOMThreadProxyHelper(Display display) {
			_display = display;
		}

		public Thread getThread() {
			return _display.getThread();
		}

		public void syncExec(Runnable runnable) {
			_display.syncExec(runnable);
		}
	};
}