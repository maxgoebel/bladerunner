/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.netmon.model.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.mozilla.interfaces.nsIBinaryInputStream;
import org.mozilla.interfaces.nsICache;
import org.mozilla.interfaces.nsICacheEntryDescriptor;
import org.mozilla.interfaces.nsICacheService;
import org.mozilla.interfaces.nsICacheSession;
import org.mozilla.interfaces.nsIInputStream;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

public class MozillaCacheService {

	private static final long NS_ERROR_CACHE_KEY_NOT_FOUND = 0x804b003dL;

	private static MozillaCacheService instance = new MozillaCacheService();

	private nsICacheSession session;

	private MozillaCacheService() {
		// singleton
	}

	private nsICacheSession getSession() {
		if (session == null) {
			//try to load from cache
			nsICacheService cacheService = (nsICacheService) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/network/cache-service;1", nsICacheService.NS_ICACHESERVICE_IID);

			//Cache session id MUST be "HTTP" so that the entries saved by the HTTP channel are found
			nsICacheSession newSession = cacheService.createSession("HTTP", nsICache.STORE_ANYWHERE, true);
			newSession.setDoomEntriesIfExpired(false);

			session = newSession;
		}

		return session;
	}

	public static String getResponseBody(URL url) {
		//Cache session id MUST be "HTTP" so that the entries saved by the HTTP channel are found
		nsICacheSession session = instance.getSession();

		//			MozIDEUIPlugin.debug( "Storage Enabled:" + session.isStorageEnabled() );

		nsICacheEntryDescriptor cacheEntry = null;

		try {

			//				cacheEntry = session.openCacheEntry( call.getRequest().getURL(), nsICache.ACCESS_READ, true);
			//				readCacheEntry( cacheEntry );

			//earlier async mode was used. revert if needed.
			nsICacheEntryDescriptor descriptor;
			try {
				descriptor = session.openCacheEntry(url.toString(), nsICache.ACCESS_READ, false);
			} catch (XPCOMException ex) {
				if (ex.errorcode == NS_ERROR_CACHE_KEY_NOT_FOUND) {
					return null;
				} else {
					throw ex;
				}
			}

			return MozillaCacheService.readCacheEntry(descriptor);
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);
		} finally {

			if (cacheEntry != null) {
				try {
					cacheEntry.close();
				} catch (Exception e) {
					//MozIDEUIPlugin.log(e);
				}
			}
		}
		return null;
	}

	private static String readCacheEntry(nsICacheEntryDescriptor cacheEntry) {

		if (!cacheEntry.isStreamBased()) {
			return null;
		}
		//GZIPed data needs to be read with a BinaryIS because the Scriptable one
		//returns String on the read and truncates the content.

		nsIInputStream stream = null;
		nsIBinaryInputStream bIS = null;

		GZIPInputStream gin = null;
		try {

			stream = cacheEntry.openInputStream(0);

			bIS = (nsIBinaryInputStream) Mozilla.getInstance().getComponentManager().createInstanceByContractID("@mozilla.org/binaryinputstream;1", null, nsIBinaryInputStream.NS_IBINARYINPUTSTREAM_IID);
			bIS.setInputStream(stream);

			byte[] bytes = new byte[(int) cacheEntry.getDataSize()];

			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) bIS.read8();
			}

			try {
				gin = new GZIPInputStream(new ByteArrayInputStream(bytes));
			} catch (IOException ie) {
				// not gzip compressed 
			}

			if (gin != null) {
				byte[] t = new byte[0];
				byte[] BUFFER = new byte[4096];
				while (gin.available() > 0) {
					int n = gin.read(BUFFER);
					if (n > 0) {
						byte[] temp = new byte[t.length + n];
						System.arraycopy(t, 0, temp, 0, t.length);
						System.arraycopy(BUFFER, 0, temp, t.length, n);
						t = temp;
					}
				}

				return new String(t);
			} else {
				return new String(bytes);
			}
		} catch (Exception e) {
			MozIDEUIPlugin.log(e);

		} finally {

			if (cacheEntry != null) {
				try {
					cacheEntry.close();
				} catch (Exception e) {
					//MozIDEUIPlugin.log(e);
				}
			}

			if (gin != null) {

				try {
					gin.close();
				} catch (Exception e) {
					//MozIDEUIPlugin.log(e);
				}

			}

			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					//MozIDEUIPlugin.log(e);
				}
			}

			if (bIS != null) {
				try {
					bIS.close();
				} catch (Exception e) {
					//MozIDEUIPlugin.log(e);
				}
			}
		}

		return null;
	}

}
