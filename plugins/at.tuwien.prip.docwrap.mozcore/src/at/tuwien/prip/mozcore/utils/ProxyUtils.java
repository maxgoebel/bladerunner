package at.tuwien.prip.mozcore.utils;

import java.lang.reflect.Field;

import org.mozilla.interfaces.nsIComponentManager;
import org.mozilla.interfaces.nsIProxyObjectManager;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIThreadManager;
import org.mozilla.xpcom.Mozilla;

import at.tuwien.prip.common.log.ErrorDump;

/**
 * Works with patched version xulrunner-1.9
 *
 * @author ceresna
 */
public class ProxyUtils {

    private static String guessIID(Class<?> c) {
        try {
            String name = c.getName();
            String baseName = c.getSimpleName();
            final String iidFieldName;
            if (name.startsWith("org.mozilla.interfaces.ns")) {
                iidFieldName = String.format("NS_%s_IID", baseName.substring(2).toUpperCase());
            } else {
                iidFieldName = String.format("%s_IID", baseName.toUpperCase());
            }

            Field f = c.getDeclaredField(iidFieldName);
            //String iid = (String) f.gc.getField()f.get(obj);
            String iid = (String) f.get(c);
            return iid;
        } catch (Throwable e) {
            ErrorDump.error(ProxyUtils.class, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends nsISupports> T proxy(nsISupports obj, Class<T> c) {
        try {
            Mozilla moz = Mozilla.getInstance();
            String iid = guessIID(c);

            nsIServiceManager sm = moz.getServiceManager();
            nsIThreadManager tm = (nsIThreadManager) sm.getServiceByContractID("@mozilla.org/thread-manager;1", nsIThreadManager.NS_ITHREADMANAGER_IID);
            nsIProxyObjectManager pm = (nsIProxyObjectManager) sm.getService("{eea90d41-b059-11d2-915e-c12b696c9333}", nsIProxyObjectManager.NS_IPROXYOBJECTMANAGER_IID);

            T t = (T)
                pm.getProxyForObject(tm.getMainThread(),
                                     iid, obj,
                                     nsIProxyObjectManager.INVOKE_SYNC);

            return t;
        } catch (Throwable e) {
            ErrorDump.error(ProxyUtils.class, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends nsISupports> T create(String contractID, Class<T> c) 
    {
        try 
        {
            Mozilla moz = Mozilla.getInstance();
            String iid = guessIID(c);
            nsIComponentManager componentManager =
                proxy(moz.getComponentManager(), nsIComponentManager.class);
            T t = (T)
                componentManager.
                createInstanceByContractID(contractID, null, iid);
            return t;
        } 
        catch (Throwable e) {
            ErrorDump.error(ProxyUtils.class, e);
            return null;
        }
    }

    public static <T extends nsISupports> T createProxied(String contractID, Class<T> c) 
    {
        T t1 = create(contractID, c);
        T t2 = proxy(t1, c);
        return t2;
    }

    @SuppressWarnings("unchecked")
    public static <T extends nsISupports> T getService(String contractID, Class<T> c) {
        try {
            Mozilla moz = Mozilla.getInstance();
            String iid = guessIID(c);
            nsIServiceManager serviceManager =
                proxy(moz.getServiceManager(), nsIServiceManager.class);
            T t = (T)
                serviceManager.
                getServiceByContractID(contractID, iid);
            return t;
        } catch (Throwable e) {
            ErrorDump.error(ProxyUtils.class, e);
            return null;
        }
    }

    public static <T extends nsISupports> T getProxiedService(String contractID, Class<T> c) {
        T t1 = getService(contractID, c);
        T t2 = proxy(t1, c);
        return t2;
    }

    @SuppressWarnings("unchecked")
    public static <T extends nsISupports> T qi(nsISupports obj, Class<T> c) {
        try {
            if (obj==null) return null;
            String iid = guessIID(c);
            T t = (T) obj.queryInterface(iid);
            return t;
        } catch (Throwable e) {
            //ErrorDump.error(ProxyUtils.class, e);
            return null;
        }
    }

}