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
package at.tuwien.dbai.bladeRunner.utils;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class DebugLayout {

	private static final boolean debug = false;

	public static void setBackground(Control cmp) {
		if (debug) {
			RGB rgb = new RGB(rand04() * 63, rand04() * 63, rand04() * 63);
			Color c = new Color(Display.getCurrent(), rgb);
			cmp.setBackground(c);
		}
	}

	private static int rand04() {
		double d = Math.random();
		if (d >= 0.8f)
			return 4;
		if (d >= 0.6f)
			return 3;
		if (d >= 0.4f)
			return 2;
		if (d >= 0.2f)
			return 1;
		return 0;
	}

}
