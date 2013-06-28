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
package at.tuwien.prip.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

public class URLReader {
	
	public static void main(String[] args) throws Exception {
		URL yahoo = new URL("http://www.yahoo.com/");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yahoo.openStream()));

		String inputLine;

		File file = new File("/tmp/tmp.html");
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(sb.toString());
		out.close();
	}
	

}
