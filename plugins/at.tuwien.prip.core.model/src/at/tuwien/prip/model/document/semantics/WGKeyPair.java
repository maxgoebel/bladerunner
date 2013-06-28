/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.model.document.semantics;

import at.tuwien.prip.common.datastructures.Pair;


/**
 * 
 * WGKeyPair.java
 *
 *
 *
 * Created: Jul 8, 2009 8:15:23 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class WGKeyPair extends Pair<SemanticText, SemanticText> {

	public WGKeyPair(SemanticText a, SemanticText b) {
		super(a, b);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6175525552762000971L;

}
