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
package at.tuwien.dbai.bladeRunner.preferences.runtime;

import static at.tuwien.prip.model.utils.DOMHelper.Tree.Children.getChildElements;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;
import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.file.FileUtils;
import at.tuwien.prip.model.utils.DOMHelper;

public class ExtractionRuntime {

	private static final String PREF_ER_XML = "";
	// private static final String PREF_ER_XML =
	// LearnUIUtils.getUniqueIdentifier() + ".PREF_ER_XML";

	/**
	 * ER change listeners
	 */
	private static ListenerList fgERListeners = new ListenerList();

	private static List<IERInstall> fERs = new LinkedList<IERInstall>();
	private static IERInstall fDefaultER = null;
	private static IERInstall fBuiltInER = null;

	static {
		fBuiltInER = findBuiltInRuntime();
		loadERInstalls();
	}

	/**
	 * Notifies all ER install changed listeners of the given property change.
	 * 
	 * @param event
	 *            event describing the change.
	 * @since 2.0
	 */
	public static void fireERChanged(PropertyChangeEvent event) {
		Object[] listeners = fgERListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IERInstallChangedListener listener = (IERInstallChangedListener) listeners[i];
			listener.erChanged(event);
		}
	}

	public static IERInstall findERInstall(String id) {
		for (int i = 0; i < fERs.size(); i++) {
			IERInstall er = fERs.get(i);
			if (er.getId().equals(id)) {
				return er;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.launching.IVMInstallType#validateInstallLocation(java
	 * .io.File)
	 */
	public static IStatus validateInstallLocation(File erHome) {
		IStatus status = null;
		File erExecutable = findERExecutable(erHome);
		if (erExecutable == null) {
			status = new Status(
					IStatus.ERROR,
					DocWrapUIUtils.getUniqueIdentifier(),
					0,
					mt.lb_Runtime_Not_a_ER_root_runtime_executable_was_not_found,
					null);
		} else {
			if (canDetectERSubdirs(erHome, erExecutable)) {
				status = new Status(IStatus.OK,
						DocWrapUIUtils.getUniqueIdentifier(), 0, mt.lb_ok, null);
			} else {
				status = new Status(
						IStatus.ERROR,
						DocWrapUIUtils.getUniqueIdentifier(),
						0,
						mt.lb_Runtime_Not_a_ER_root_Plugins_subdir_was_not_found,
						null);
			}
		}

		return status;
	}

	private static File findERExecutable(File erInstallLocation) {
		try {
			String progName = getERExecutableName();
			File path = new File(erInstallLocation, progName);
			if (!path.exists())
				return null;

			return path;
		} catch (Exception e) {
			ErrorDump.error(ExtractionRuntime.class, e);
			return null;
		}
	}

	public static String getERExecutableName() {
		String name = "wlruntime";
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			name += ".exe";
		}
		return name;
	}

	private static boolean canDetectERSubdirs(File erHome, File erExecutable) {
		File f = new File(erHome, "plugins");
		return f.exists();
	}

	/**
	 * Return the default ER set with <code>setDefaultER()</code>.
	 * 
	 * @return Returns the default ER. May return <code>null</code> when no
	 *         default ER was set or when the default ER has been disposed.
	 */
	public static IERInstall getDefaultERInstall() {
		return fDefaultER;
	}

	public static IERInstall[] getERInstalls() {
		return fERs.toArray(new IERInstall[fERs.size()]);
	}

	private static void loadERInstalls() {
		try {
			String xml =
			// new InstanceScope().getNode(PREF_ER_XML).name();
			LearnUIPlugin.getDefault().getPluginPreferences()
					.getString(PREF_ER_XML);
			readFromXML(xml);

			if (fERs.isEmpty() && fBuiltInER != null) {
				fERs.add(fBuiltInER);
				fDefaultER = fBuiltInER;
			}

		} catch (Exception e) {
			ErrorDump.error(ExtractionRuntime.class, e);
		}
	}

	public static boolean saveERInstalls(IERInstall[] ers, IERInstall defaultER) {
		String xml = writeToXML(ers, defaultER);
		LearnUIPlugin.getDefault().getPluginPreferences()
				.setValue(PREF_ER_XML, xml);

		fERs.clear();
		fERs.addAll(Arrays.asList(ers));
		fDefaultER = defaultER;

		return true;
	}

	private static String writeToXML(IERInstall[] ers, IERInstall defaultER) {
		// Create the Document and the top-level node
		Document doc = DOMHelper.LoadSave.createEmptyDocument("erSettings"); //$NON-NLS-1$
		Element root = doc.getDocumentElement();

		if (defaultER != null) {
			root.setAttribute("defaultER", defaultER.getId()); //$NON-NLS-1$
		}

		// Create a node for each install type represented in this container
		for (IERInstall er : ers) {
			Element element = doc.createElement("er"); //$NON-NLS-1$
			element.setAttribute("id", er.getId()); //$NON-NLS-1$
			element.setAttribute("name", er.getName()); //$NON-NLS-1$
			element.setAttribute("args", er.getERArgs()); //$NON-NLS-1$
			element.setAttribute(
					"loc", er.getInstallLocation().getAbsolutePath()); //$NON-NLS-1$
			root.appendChild(element);
		}

		return DOMHelper.LoadSave.writeDOMToString(doc);
	}

	private static void readFromXML(String xml) throws Exception {
		fERs.clear();
		fDefaultER = null;

		if (xml.length() == 0)
			return;

		// Create the Document and the top-level node
		Document doc = DOMHelper.LoadSave.readDOMFromString(xml, null);
		Element root = doc.getDocumentElement();
		if (root == null)
			return;

		List<IERInstall> ers = new LinkedList<IERInstall>();
		for (Element element : getChildElements(root, "er")) {
			String id = element.getAttribute("id"); //$NON-NLS-1$
			String name = element.getAttribute("name"); //$NON-NLS-1$
			String args = element.getAttribute("args"); //$NON-NLS-1$
			String loc = element.getAttribute("loc"); //$NON-NLS-1$
			root.appendChild(element);
			IERInstall er = new ERInstall(id);
			er.setName(name);
			er.setERArgs(args);
			er.setInstallLocation(new File(loc));
			ers.add(er);
		}
		fERs.addAll(ers);

		String defId = root.getAttribute("defaultER"); //$NON-NLS-1$
		if (defId != null && defId.length() > 0) {
			for (IERInstall er : ers) {
				if (er.getId().equals(defId)) {
					fDefaultER = er;
					break;
				}
			}
		}

	}

	public static boolean isContributedERInstall(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * search the shipped runtime
	 */
	private static IERInstall findBuiltInRuntime() {
		try {
			// search relative to export ide directory
			{
				URL u = Platform.getInstallLocation().getURL();
				if (u == null)
					return null;
				File ideDir = FileUtils.toFile(u);

				IERInstall er = findBuiltInRuntimeRelativeToIDE(ideDir);
				if (er != null)
					return er;
			}

			// search relative to java sourcecode workspace
			// (when launched from eclipse when developing
			{
				Bundle plugin = LearnUIPlugin.getDefault().getBundle();
				if (plugin == null)
					return null;
				URL u = FileLocator.find(plugin, new Path("."), null);
				if (u == null)
					return null;

				u = FileLocator.toFileURL(u);
				File path = FileUtils.toFile(u);
				if (path == null || !path.exists())
					return null;

				File ideUIDir = path.getParentFile();
				if (ideUIDir == null || !ideUIDir.isDirectory())
					return null;
				File idePluginsDir = ideUIDir.getParentFile();
				if (idePluginsDir == null || !idePluginsDir.isDirectory())
					return null;
				File ideDir = idePluginsDir.getParentFile();

				IERInstall er = findBuiltInRuntimeRelativeToIDE(ideDir);
				if (er != null) {
					ErrorDump.debug(ExtractionRuntime.class,
							"Found runtime in %s", er.getInstallLocation()
									.getAbsolutePath());
					return er;
				}

				ErrorDump.debug(ExtractionRuntime.class,
						"Default runtime not found");
				return null;

			}
		} catch (Exception e) {
			ErrorDump.error(ExtractionRuntime.class, e);
			return null;
		}
	}

	private static IERInstall findBuiltInRuntimeRelativeToIDE(File ideDir) {
		if (ideDir == null || !ideDir.isDirectory())
			return null;
		File someDir = ideDir.getParentFile();
		if (someDir == null || !someDir.isDirectory())
			return null;

		ErrorDump.debug(ExtractionRuntime.class,
				"Looking for default runtime in %s", someDir.getAbsolutePath());
		File runtimeDir = new File(someDir, "wlruntime");
		if (runtimeDir == null || !runtimeDir.isDirectory())
			return null;

		File runtimePluginsDir = runtimeDir.getParentFile();
		if (runtimePluginsDir == null || !runtimePluginsDir.isDirectory())
			return null;

		// check if the ide dir contains also the
		// 'wlruntime' or 'wlruntime.exe' binary
		String progName = ExtractionRuntime.getERExecutableName();
		File runtimeProg = new File(runtimeDir, progName);
		if (runtimeProg == null || !runtimeProg.isFile())
			return null;

		ERInstall er = new ERInstall(AddERDialog.createUniqueId());
		er.setName("Default");
		er.setInstallLocation(runtimeDir);

		return er;
	}

	public static IERInstall getBuiltInERInstall() {
		return fBuiltInER;
	}

	/*
	 * private boolean isAvailableRuntimeInIDE() { return
	 * findRuntimeExecutable()!=null; }
	 */

}
