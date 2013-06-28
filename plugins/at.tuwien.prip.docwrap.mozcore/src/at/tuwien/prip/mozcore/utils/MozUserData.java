package at.tuwien.prip.mozcore.utils;

import java.awt.Rectangle;
import java.util.Map;

/**
 * MozUserData.java
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Sep 9, 2011
 */
public class MozUserData {

	private Rectangle bounds = null;
	private Map<String, String> css = null;
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	public Rectangle getBounds() {
		return bounds;
	}
	public void setCss(Map<String, String> css) {
		this.css = css;
	}
	public Map<String, String> getCss() {
		return css;
	}
	
}
