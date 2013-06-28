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
package at.tuwien.dbai.bladeRunner;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The images provided by the external tools plugin.
 */
public class LearnUIImages {

	/**
	 * The image registry containing <code>Image</code>s.
	 */
	private static ImageRegistry imageRegistry;

	/**
	 * The registry for composite images
	 */
	// private static ImageDescriptorRegistry imageDescriptorRegistry;

	// /* Declare Common paths */
	// private static URL ICON_BASE_URL = null;
	//
	// static {
	//		String pathSuffix = "icons/full/"; //$NON-NLS-1$
	// ICON_BASE_URL=
	// LearnUIPlugin.getDefault().getBundle().getEntry(pathSuffix);
	// }

	/**
	 * Declare all images
	 */
	private static void declareImages() {
		// Objects
		declareRegistryImage("icons/full/obj16/mozilla"); //$NON-NLS-1$
	}

	/**
	 * Declare an Image in the registry table.
	 * 
	 * @param key
	 *            The key to use when registering the image
	 * @param path
	 *            The path where the image can be found. This path is relative
	 *            to where this plugin class is found (i.e. typically the
	 *            packages directory)
	 */
	private static final String[] imgExts = (Platform.getOS().equals(
			Platform.OS_WIN32) ?
	// on win32 prefer gif images over png images,
	// because of the win32 bug with transparent pngs
	new String[] { "gif", "png" } : //$NON-NLS-1$ //$NON-NLS-2$
			new String[] { "png", "gif" }); //$NON-NLS-1$ //$NON-NLS-2$

	private final static ImageDescriptor declareRegistryImage(String key) {

		boolean addExt = true;
		for (String ext : imgExts) {
			if (key.endsWith(ext)) {
				addExt = false;
				break;
			}
		}

		ImageDescriptor desc = null;
		String plugId = LearnUIPlugin.getDefault().getBundle()
				.getSymbolicName();
		if (addExt) {
			for (String ext : imgExts) {
				String path = key + "." + ext; //$NON-NLS-1$
				desc = LearnUIPlugin.imageDescriptorFromPlugin(plugId, path);
				if (desc != null)
					break;
			}
		} else {
			String path = key;
			desc = LearnUIPlugin.imageDescriptorFromPlugin(plugId, path);
		}

		if (desc == null) {
			// image not found
			desc = ImageDescriptor.getMissingImageDescriptor();
		}
		imageRegistry.put(key, desc);

		return desc;
	}

	/*
	 * private final static void declareRegistryImage(String key) { String path
	 * = key; ImageDescriptor desc =
	 * ImageDescriptor.getMissingImageDescriptor(); try { desc=
	 * ImageDescriptor.createFromURL(makeIconFileURL(path)); } catch
	 * (MalformedURLException me) { } imageRegistry.put(key, desc); }
	 */

	/**
	 * Returns the ImageRegistry.
	 */
	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return imageRegistry;
	}

	/**
	 * Initialize the image registry by declaring all of the required graphics.
	 * This involves creating JFace image descriptors describing how to
	 * create/find the image should it be needed. The image is not actually
	 * allocated until requested.
	 * 
	 * Prefix conventions Wizard Banners WIZBAN_ Preference Banners PREF_BAN_
	 * Property Page Banners PROPBAN_ Color toolbar CTOOL_ Enable toolbar ETOOL_
	 * Disable toolbar DTOOL_ Local enabled toolbar ELCL_ Local Disable toolbar
	 * DLCL_ Object large OBJL_ Object small OBJS_ View VIEW_ Product images
	 * PROD_ Misc images MISC_
	 * 
	 * Where are the images? The images (typically gifs) are found in the same
	 * location as this plugin class. This may mean the same package directory
	 * as the package holding this class. The images are declared using
	 * this.getClass() to ensure they are looked up via this plugin class.
	 * 
	 * @see org.eclipse.jface.resource.ImageRegistry
	 */
	public static ImageRegistry initializeImageRegistry() {
		imageRegistry = new ImageRegistry(LearnUIPlugin.getStandardDisplay());
		declareImages();
		return imageRegistry;
	}

	/**
	 * Returns the <code>Image<code> identified by the given key,
	 * or <code>null</code> if it does not exist.
	 */
	public static Image getImage(String key) {
		Image img = getImageRegistry().get(key);
		if (img == null) {
			// not in cache yet
			ImageDescriptor desc = declareRegistryImage(key);
			img = desc.createImage();
			// getImageRegistry().put(key, img);
		}

		return img;
	}

	/**
	 * Returns the <code>ImageDescriptor<code> identified by the given key,
	 * or <code>null</code> if it does not exist.
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor desc = getImageRegistry().getDescriptor(key);
		if (desc == null) {
			// not in cache yet
			desc = declareRegistryImage(key);
		}

		return desc;
	}

	/*
	 * private static URL makeIconFileURL(String iconPath) throws
	 * MalformedURLException { if (ICON_BASE_URL == null) { throw new
	 * MalformedURLException(); }
	 * 
	 * return new URL(ICON_BASE_URL, iconPath); }
	 */

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *lcl16 folders.
	 */
	/*
	 * public static void setLocalImageDescriptors(IAction action, String
	 * iconName) { setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	 * }
	 */

	/*
	 * private static void setImageDescriptors(IAction action, String type,
	 * String relPath) {
	 * 
	 * try { ImageDescriptor id=
	 * ImageDescriptor.createFromURL(makeIconFileURL("d" + type, relPath));
	 * //$NON-NLS-1$ if (id != null) action.setDisabledImageDescriptor(id); }
	 * catch (MalformedURLException e) { LearnUIPlugin.log(e); }
	 * 
	 * try { ImageDescriptor id=
	 * ImageDescriptor.createFromURL(makeIconFileURL("c" + type, relPath));
	 * //$NON-NLS-1$ if (id != null) action.setHoverImageDescriptor(id); } catch
	 * (MalformedURLException e) { LearnUIPlugin.log(e); }
	 * 
	 * action.setImageDescriptor(create("e" + type, relPath)); //$NON-NLS-1$ }
	 */

	/*
	 * private static URL makeIconFileURL(String prefix, String name) throws
	 * MalformedURLException { if (ICON_BASE_URL == null) { throw new
	 * MalformedURLException(); }
	 * 
	 * StringBuffer buffer= new StringBuffer(prefix); buffer.append('/');
	 * buffer.append(name); return new URL(ICON_BASE_URL, buffer.toString()); }
	 */

	/*
	 * private static ImageDescriptor create(String prefix, String name) { try {
	 * return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name)); }
	 * catch (MalformedURLException e) { LearnUIPlugin.log(e); return
	 * ImageDescriptor.getMissingImageDescriptor(); } }
	 */

	/**
	 * Returns the image for the given composite descriptor.
	 */
	/*
	 * public static Image getImage(CompositeImageDescriptor imageDescriptor) {
	 * if (imageDescriptorRegistry == null) { imageDescriptorRegistry = new
	 * ImageDescriptorRegistry(); } return
	 * imageDescriptorRegistry.get(imageDescriptor); }
	 */

	/*
	 * public static void disposeImageDescriptorRegistry() { if
	 * (imageDescriptorRegistry != null) { imageDescriptorRegistry.dispose(); }
	 * }
	 */
}
