/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.mozilla.interfaces.nsISupports;

public class XPCOMThreadProxy implements InvocationHandler {

	protected nsISupports mozillaObject;

	protected IXPCOMThreadProxyHelper proxyHelper;

	protected XPCOMThreadProxy(nsISupports aMozillaObject, IXPCOMThreadProxyHelper aProxyHelper) {
		mozillaObject = aMozillaObject;
		proxyHelper = aProxyHelper;
	}

	private Object _value;
	private Throwable _throwable;

	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

		Object value = null;
		Throwable throwable = null;

		/*
		 * Protect against calling synchExec when the current thread is the same 
		 * that runs the event loop, which will cause a hang.
		 */
		if (Thread.currentThread() == proxyHelper.getThread()) {
			try {
				value = method.invoke(mozillaObject, args);
			} catch (IllegalAccessException iae) {
				MozideCorePlugin.log(iae);
			} catch (InvocationTargetException ite) {
				throwable = ite.getCause();
			}
		} else {
			final Object[] newArgs = deproxify(args);

			synchronized (this) {
				_throwable = null;
				_value = null;
				proxyHelper.syncExec(new Runnable() {
					public void run() {
						try {
							try {
								_value = method.invoke(mozillaObject, newArgs);
							} catch (IllegalAccessException iae) {
								MozideCorePlugin.log(iae);
							} catch (InvocationTargetException ite) {
								Throwable t = ite.getCause();
								if (t != null)
									throw t;
							}
						} catch (Throwable t) {
							_throwable = t;

							// if it's a java.lang.Error, always rethrow on the calling
							// thread (see javadoc for java.lang.Error)
							if (t instanceof Error)
								throw (Error) t;
						}
					}
				});
				value = _value;
				throwable = _throwable;
			}
		}

		if (throwable != null) {
			throw throwable;
		}

		if (value instanceof nsISupports) {
			value = createProxy((nsISupports) value, proxyHelper);
		}
		return value;
	}

	private static Object[] deproxify(Object[] args) {
		if (args == null)
			return args;

		Object[] newArgs = null;
		int i = 0;

		for (; i < args.length; i++) {
			if (args[i] != null && isXPCOMThreadProxy(args[i])) {
				newArgs = new Object[args.length];
				if (i != 0) {
					System.arraycopy(args, 0, newArgs, 0, i);
				}
				break;
			}
		}

		if (newArgs == null) {
			return args;
		}

		for (; i < args.length; i++) {
			if (args[i] != null && isXPCOMThreadProxy(args[i])) {
				XPCOMThreadProxy proxy = (XPCOMThreadProxy) Proxy.getInvocationHandler(args[i]);
				newArgs[i] = proxy.mozillaObject;
			} else {
				newArgs[i] = args[i];
			}
		}
		return newArgs;
	}

	public static boolean isXPCOMThreadProxy(Object obj) {
		if (Proxy.isProxyClass(obj.getClass()) && Proxy.getInvocationHandler(obj) instanceof XPCOMThreadProxy) {
			return true;
		}
		return false;
	}

	public static Object createProxy(nsISupports aMozillaObject, IXPCOMThreadProxyHelper aProxyHelper) {
		// Don't create proxy for a XPCOMThreadProxy
		if (isXPCOMThreadProxy(aMozillaObject)) {
			return aMozillaObject;
		}

		/*
		 * Create a proxy that implements the same interfaces as the object.  But
		 * we don't want to implement XPCOMJavaProxyBase, since that overrides
		 * finalize().
		 */
		Class[] ifaces = aMozillaObject.getClass().getInterfaces();
		ArrayList newIfaces = new ArrayList(ifaces.length);
		for (int i = 0; i < ifaces.length; i++) {
			if (!ifaces[i].getName().endsWith("XPCOMJavaProxyBase")) {
				newIfaces.add(ifaces[i]);
			}
		}
		Class[] newIfacesArray = new Class[newIfaces.size()];
		newIfaces.toArray(newIfacesArray);

		return Proxy.newProxyInstance(aMozillaObject.getClass().getClassLoader(), newIfacesArray, new XPCOMThreadProxy(aMozillaObject, aProxyHelper));
	}

}
