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
import java.io.IOException;
import java.io.InputStreamReader;

import at.tuwien.prip.common.log.ErrorDump;

/**
 * CommandExec.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 18, 2011
 */
public class CommandExec {

	public static void runCommand (String command, String args, boolean verbose) {

		try {

			String process = command + " " + args;
			ErrorDump.info(CommandExec.class, "Running " + process);
			Process proc = Runtime.getRuntime().exec(process);

			int exitVal = proc.waitFor();

			if (exitVal==0) {
				ErrorDump.info(CommandExec.class, "Finished GnuPlot [SUCCESS]");
			} else {
				ErrorDump.warn(CommandExec.class, "Finished GnuPlot [FAILED]");
			}
			handleProcessOutput(proc, verbose);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Handle the output of a process execution.
	 * 
	 * @param p
	 * @param verbose
	 */
	protected static void handleProcessOutput (Process p, boolean verbose) {

		try {
			String s = null;
			int i=0;

			if (verbose) {
				BufferedReader stdInput = new BufferedReader(new 
						InputStreamReader(p.getInputStream()));

				// read the output from the command
				while ((s = stdInput.readLine()) != null) {
					if (i==0) {
						System.out.println("Here is the standard output of the command:");
						i++;
					}
					System.out.println(s);
				}
			}

			// read any errors from the attempted command
			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			i=0;
			while ((s = stdError.readLine()) != null) {
				if (i==0) {
					System.out.println("Here is the standard error of the command (if any):");
					i++;
				}
				System.err.println(s);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
