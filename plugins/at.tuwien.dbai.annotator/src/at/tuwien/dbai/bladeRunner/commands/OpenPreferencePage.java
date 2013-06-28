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
package at.tuwien.dbai.bladeRunner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.preferences.BenchmarkPreferencePage;
import at.tuwien.dbai.bladeRunner.preferences.DocWrapPreferencePage;
import at.tuwien.dbai.bladeRunner.preferences.WeblearnPreferencePage;
import at.tuwien.dbai.bladeRunner.preferences.WrapperPreferencePage;

/**
 * OpenPreferencePage.java
 * 
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 23, 2011
 */
public class OpenPreferencePage extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// the DocWrap preferences page
		IPreferencePage pageA = new DocWrapPreferencePage();
		pageA.setTitle("DocWrap Preferences");

		// the WebLearn preferences page
		IPreferencePage pageB = new WeblearnPreferencePage();
		pageB.setTitle("Weblearn Preferences");

		// the Wrapper preference page
		IPreferencePage pageC = new WrapperPreferencePage();
		pageC.setTitle("Wrapper Preferences");

		// the Benchmark preference page
		IPreferencePage pageD = new BenchmarkPreferencePage();
		pageD.setTitle("Benchmark Preferences");

		// create the new PreferenceNodes that will appear in the Preference
		// window
		PreferenceNode nodeA = new PreferenceNode("1", pageA);
		PreferenceNode nodeB = new PreferenceNode("2", pageB);
		PreferenceNode nodeC = new PreferenceNode("3", pageC);
		PreferenceNode nodeD = new PreferenceNode("4", pageD);

		// add the nodes to the PreferenceManager
		PreferenceManager pm = new PreferenceManager();
		// pm.addToRoot(nodeA);
		// pm.addToRoot(nodeB);
		// pm.addToRoot(nodeC);
		pm.addToRoot(nodeD);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		PreferenceDialog pd = new PreferenceDialog(shell, pm);

		pd.setPreferenceStore(LearnUIPlugin.getDefault().getPreferenceStore());
		pd.create();
		pd.open();

		return null;
	}

}
