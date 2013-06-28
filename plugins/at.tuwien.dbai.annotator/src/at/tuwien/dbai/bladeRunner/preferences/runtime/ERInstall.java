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

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.PropertyChangeEvent;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;

/**
 * An implementation of a ER install.
 */
public class ERInstall implements IERInstall {

	private String fId;
	private String fName;
	private File fInstallLocation;
	private String fERArgs;

	// whether change events should be fired
	private boolean fNotify = true;

	/**
	 * Constructs a new ER install.
	 * 
	 * @param type
	 *            The type of this ER install. Must not be <code>null</code>
	 * @param id
	 *            The unique identifier of this ER instance Must not be
	 *            <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if any of the required parameters are <code>null</code>.
	 */
	public ERInstall(String id) {
		if (id == null)
			throw new IllegalArgumentException(
					"LaunchingMessages.erInstall_assert_idNotNull");
		fId = id;
	}

	/**
	 * Constructs a copy of the specified ER with the given identifier.
	 * 
	 * @param sourceER
	 * @param id
	 * @since 3.2
	 */
	public ERInstall(IERInstall sourceER, String id) {
		this(id);
		setNotify(false);
		init(sourceER);
	}

	/**
	 * Constructs a copy of the specified ER
	 */
	public ERInstall(IERInstall realER) {
		this(realER.getId());
		init(realER);
	}

	/**
	 * Initializes the settings of this standin based on the settings in the
	 * given ER install.
	 * 
	 * @param realER
	 *            ER to copy settings from
	 */
	private void init(IERInstall realER) {
		setName(realER.getName());
		setInstallLocation(realER.getInstallLocation());
		setERArgs(realER.getERArgs());
	}

	/*
	 * (non-Javadoc) Subclasses should not override this method.
	 * 
	 * @see IERInstall#getId()
	 */
	public String getId() {
		return fId;
	}

	/*
	 * (non-Javadoc) Subclasses should not override this method.
	 * 
	 * @see IERInstall#getName()
	 */
	public String getName() {
		return fName;
	}

	/*
	 * (non-Javadoc) Subclasses should not override this method.
	 * 
	 * @see IERInstall#setName(String)
	 */
	public void setName(String name) {
		if (!name.equals(fName)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this,
					IERInstallChangedListener.PROPERTY_NAME, fName, name);
			fName = name;
			if (fNotify) {
				ExtractionRuntime.fireERChanged(event);
			}
		}
	}

	/*
	 * (non-Javadoc) Subclasses should not override this method.
	 * 
	 * @see IERInstall#getInstallLocation()
	 */
	public File getInstallLocation() {
		return fInstallLocation;
	}

	/*
	 * (non-Javadoc) Subclasses should not override this method.
	 * 
	 * @see IERInstall#setInstallLocation(File)
	 */
	public void setInstallLocation(File installLocation) {
		if (!installLocation.equals(fInstallLocation)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this,
					IERInstallChangedListener.PROPERTY_INSTALL_LOCATION,
					fInstallLocation, installLocation);
			fInstallLocation = installLocation;
			if (fNotify) {
				ExtractionRuntime.fireERChanged(event);
			}
		}
	}

	// /* (non-Javadoc)
	// * @see IERInstall#getERRunner(String)
	// */
	// public IERRunner getERRunner(String mode) {
	// return null;
	// }

	/**
	 * Whether this ER should fire property change notifications.
	 * 
	 * @param notify
	 * @since 2.1
	 */
	protected void setNotify(boolean notify) {
		fNotify = notify;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @since 2.1
	 */
	public boolean equals(Object object) {
		if (object instanceof IERInstall) {
			IERInstall er = (IERInstall) object;
			return getId().equals(er.getId());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 * 
	 * @since 2.1
	 */
	public int hashCode() {
		return getId().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IERInstall2#getERArgs()
	 */
	public String getERArgs() {
		return fERArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IERInstall2#setERArgs(java.lang.String)
	 */
	public void setERArgs(String erArgs) {
		if (fERArgs == null) {
			if (erArgs == null) {
				// No change
				return;
			}
		} else if (fERArgs.equals(erArgs)) {
			// No change
			return;
		}
		PropertyChangeEvent event = new PropertyChangeEvent(this,
				IERInstallChangedListener.PROPERTY_ER_ARGUMENTS, fERArgs,
				erArgs);
		fERArgs = erArgs;
		if (fNotify) {
			ExtractionRuntime.fireERChanged(event);
		}
	}

	// /* (non-Javadoc)
	// * Subclasses should override.
	// * @see org.eclipse.jdt.launching.IERInstall2#getJavaVersion()
	// */
	// public String getXUlRunnerVersion() {
	// return null;
	// }

	// /* (non-Javadoc)
	// * @see
	// org.eclipse.jdt.launching.IERInstall3#evaluateSystemProperties(java.lang.String[],
	// org.eclipse.core.runtime.IProgressMonitor)
	// */
	// public Map evaluateSystemProperties(String[] properties, IProgressMonitor
	// monitor) throws CoreException {
	// //locate the launching support jar - it contains the main program to run
	// if (monitor == null) {
	// monitor = new NullProgressMonitor();
	// }
	// Map map = new HashMap();
	//
	// // first check cache (preference store) to avoid launching ER
	// Preferences preferences = JavaRuntime.getPreferences();
	// boolean cached = true;
	// for (int i = 0; i < properties.length; i++) {
	// String property = properties[i];
	// String key = getSystemPropertyKey(property);
	// if (preferences.contains(key)) {
	// String value = preferences.getString(key);
	// map.put(property, value);
	// } else {
	// map.clear();
	// cached = false;
	// break;
	// }
	// }
	// if (!cached) {
	// // launch ER to evaluate properties
	//            File file = LaunchingPlugin.getFileInPlugin(new Path("lib/launchingsupport.jar")); //$NON-NLS-1$
	// if (file.exists()) {
	// String javaVersion = getJavaVersion();
	// boolean hasXMLSupport = false;
	// if (javaVersion != null) {
	// hasXMLSupport = true;
	// if (javaVersion.startsWith(JavaCore.VERSION_1_1) ||
	// javaVersion.startsWith(JavaCore.VERSION_1_2) ||
	// javaVersion.startsWith(JavaCore.VERSION_1_3)) {
	// hasXMLSupport = false;
	// }
	// }
	// String mainType = null;
	// if (hasXMLSupport) {
	//                    mainType = "org.eclipse.jdt.internal.launching.support.SystemProperties"; //$NON-NLS-1$
	// } else {
	//                    mainType = "org.eclipse.jdt.internal.launching.support.LegacySystemProperties"; //$NON-NLS-1$
	// }
	// ERRunnerConfiguration config = new ERRunnerConfiguration(mainType, new
	// String[]{file.getAbsolutePath()});
	// IERRunner runner = getERRunner(ILaunchManager.RUN_MODE);
	// if (runner == null) {
	// abort(LaunchingMessages.AbstractERInstall_0, null,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// }
	// config.setProgramArguments(properties);
	// Launch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
	// if (monitor.isCanceled()) {
	// return map;
	// }
	// monitor.beginTask(LaunchingMessages.AbstractERInstall_1, 2);
	// runner.run(config, launch, monitor);
	// IProcess[] processes = launch.getProcesses();
	// if (processes.length != 1) {
	// abort(LaunchingMessages.AbstractERInstall_0, null,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// }
	// IProcess process = processes[0];
	// try {
	// int total = 0;
	// int max =
	// JavaRuntime.getPreferences().getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
	// while (!process.isTerminated()) {
	// try {
	// if (total > max) {
	// break;
	// }
	// Thread.sleep(50);
	// total+=50;
	// } catch (InterruptedException e) {
	// }
	// }
	// } finally {
	// if (!launch.isTerminated()) {
	// launch.terminate();
	// }
	// }
	// monitor.worked(1);
	// if (monitor.isCanceled()) {
	// return map;
	// }
	//
	// monitor.subTask(LaunchingMessages.AbstractERInstall_3);
	// IStreamsProxy streamsProxy = process.getStreamsProxy();
	// String text = null;
	// if (streamsProxy != null) {
	// text = streamsProxy.getOutputStreamMonitor().getContents();
	// }
	// if (text != null && text.length() > 0) {
	// try {
	// DocumentBuilder parser = LaunchingPlugin.getParser();
	// Document document = parser.parse(new
	// ByteArrayInputStream(text.getBytes()));
	// Element envs = document.getDocumentElement();
	// NodeList list = envs.getChildNodes();
	// int length = list.getLength();
	// for (int i = 0; i < length; ++i) {
	// Node node = list.item(i);
	// short type = node.getNodeType();
	// if (type == Node.ELEMENT_NODE) {
	// Element element = (Element) node;
	//                                if (element.getNodeName().equals("property")) { //$NON-NLS-1$
	//                                    String name = element.getAttribute("name"); //$NON-NLS-1$
	//                                    String value = element.getAttribute("value"); //$NON-NLS-1$
	// map.put(name, value);
	// }
	// }
	// }
	// } catch (SAXException e) {
	// abort(LaunchingMessages.AbstractERInstall_4, e,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// } catch (IOException e) {
	// abort(LaunchingMessages.AbstractERInstall_4, e,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// }
	// } else {
	// abort(LaunchingMessages.AbstractERInstall_0, null,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// }
	// monitor.worked(1);
	// } else {
	// abort(LaunchingMessages.AbstractERInstall_0, null,
	// IJavaLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
	// }
	// // cache for future reference
	// Iterator keys = map.keySet().iterator();
	// while (keys.hasNext()) {
	// String property = (String)keys.next();
	// String value = (String) map.get(property);
	// String key = getSystemPropertyKey(property);
	// preferences.setValue(key, value);
	// }
	// }
	// monitor.done();
	// return map;
	// }
	//
	// /**
	// * Generates a key used to cache system property for this ER in this
	// plug-ins
	// * preference store.
	// *
	// * @param property system property name
	// * @return preference store key
	// */
	// private String getSystemPropertyKey(String property) {
	// StringBuffer buffer = new StringBuffer();
	// buffer.append(PREF_ER_INSTALL_SYSTEM_PROPERTY);
	//        buffer.append("."); //$NON-NLS-1$
	// buffer.append(getId());
	//        buffer.append("."); //$NON-NLS-1$
	// buffer.append(property);
	// return buffer.toString();
	// }

	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message
	 *            the status message
	 * @param exception
	 *            lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code
	 *            error code
	 * @throws CoreException
	 *             the "abort" core exception
	 * @since 3.2
	 */
	protected void abort(String message, Throwable exception, int code)
			throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR,
				DocWrapUIUtils.getPlugindId(), code, message, exception));
	}

}
