/**
 * Token.java
 *
 *
 *
 * Created: Apr 30, 2009 5:37:34 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
package at.tuwien.prip.model.token;

/**
 * Token.java
 *
 *
 *
 * Created: Apr 30, 2009 5:37:34 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class Token {

	protected String name;

	public Token(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int compareTo(Token b) {
		return name.compareTo(b.name);
	}
	
}//Token
