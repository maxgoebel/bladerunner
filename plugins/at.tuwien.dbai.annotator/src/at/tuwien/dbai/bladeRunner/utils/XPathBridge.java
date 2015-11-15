package at.tuwien.dbai.bladeRunner.utils;

public class XPathBridge {

	private String path;
	
	private boolean isPositive = true;
	
	/**
	 * 
	 * @param path
	 * @param isPositive
	 */
	public XPathBridge(String path, boolean isPositive) {
		this.path = path;
		this.isPositive = isPositive;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isPositive() {
		return isPositive;
	}
}

