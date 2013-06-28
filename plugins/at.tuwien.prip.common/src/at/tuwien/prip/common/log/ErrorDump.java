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
package at.tuwien.prip.common.log;

import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import at.tuwien.prip.common.utils.resourceloader.ResourceLoader;

/**
 * Centralized handler for log messages and exceptions.
 *
 * @author ceresna
 */
public class ErrorDump {

    static {
        try {
            //initialize log4j
            URL u = ResourceLoader.getResourceAsURL(ErrorDump.class, "at/tuwien/prip/common/log/logging.properties-log4j");
            PropertyConfigurator.configure(u);

            //initialize the java.logging subsystem to forward
            //all log messages to log4j
            InputStream is = ErrorDump.class.getResourceAsStream("logging.properties-java");
            java.util.logging.LogManager.getLogManager().readConfiguration(is);
            //set the .file property to non-null value to force mallet
            //skip initilization of its configuration
            System.setProperty("java.util.logging.config.file", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Logger getLogger(Class<?>c) {
        return Logger.getLogger(c.getName());
    }

    public static void info(Class<?> c, String message) {
        Logger l = getLogger(c);
        l.info(message);
    }
    public static void info(Object object, String message) {
        info(object.getClass(), message);
    }
    public static void info(Class<?> c, String message, Object... args) {
        info(c, String.format(message, args));
    }
    public static void info(Object object, String message, Object... args) {
        info(object.getClass(), message, args);
    }


    public static void debug(Class<?>c, String message) {
        Logger l = getLogger(c);
        //l.log(Level.FINE, message);
        l.log(Level.DEBUG, message);
    }
    public static void debug(Object object, String message) {
        debug(object.getClass(), message);
    }
    public static void debug(Class<?>c, String message, Object... args) {
        debug(c, String.format(message, args));
    }
    public static void debug(Object object, String message, Object... args) {
        debug(object.getClass(), message, args);
    }


    public static void warn(Class<?>c, String message) {
        Logger l = getLogger(c);
        //l.log(Level.WARNING, message);
        l.log(Level.WARN, message);
    }
    public static void warn(Object object, String message) {
        warn(object.getClass(), message);
    }
    public static void warn(Class<?>c, String message, Object... args) {
        warn(c, String.format(message, args));
    }
    public static void warn(Object object, String message, Object... args) {
        warn(object.getClass(), String.format(message, args));
    }
    public static void warn(Class<?>c, Throwable t, String message) {
        Logger l = getLogger(c);
        //l.log(Level.WARNING, message, t);
        l.log(Level.WARN, message, t);
    }
    public static void warn(Object object, Throwable t, String message) {
        warn(object.getClass(), t, message);
    }
    public static void warn(Class<?>c, Throwable t, String message, Object... args) {
        warn(c, t, String.format(message, args));
    }
    public static void warn(Object object, Throwable t, String message, Object... args) {
        warn(object.getClass(), t, String.format(message, args));
    }


    public static void error(Class<?>c, String message) {
        Logger l = getLogger(c);
        //l.log(Level.SEVERE, message);
        l.log(Level.ERROR, message);
    }
    public static void error(Object object, String message) {
        error(object.getClass(), message);
    }
    public static void error(Class<?>c, String message, Object... args) {
        error(c, String.format(message, args));
    }
    public static void error(Object object, String message, Object... args) {
        error(object.getClass(), message, args);
    }


    public static void error(Class<?>c, Throwable t, String message) {
        Logger l = getLogger(c);
        //l.log(Level.SEVERE, message, t);
        l.log(Level.ERROR, message, t);
    }
    public static void error(Object object, Throwable t, String message) {
        error(object.getClass(), t, message);
    }
    public static void error(Class<?>c, Throwable t, String message, Object... args) {
        error(c, t, String.format(message, args));
    }
    public static void error(Object object, Throwable t, String message, Object... args) {
        error(object.getClass(), t, String.format(message, args));
    }
    public static void error(Class<?>c, Throwable t) {
        error(c, t, t.getMessage());
    }
    public static void error(Object object, Throwable t) {
        error(object.getClass(), t);
    }
    public static void errorHere(Class<?>c) {
        Throwable t = new Throwable();
        error(c, t);
    }
    public static void errorHere(Object object) {
        errorHere(object.getClass());
    }


    public static String getRootCauseMessage(Throwable t) {
        //find the cause
        String m = null;
        while (t!=null) {
            m = t.getMessage();
            t = t.getCause();
        }

        return m;
    }

    public static void infoMemUsage() {
        //try to really execute garbage collector
        for (int i=0;i<10;i++) System.gc();

        long free  = Runtime.getRuntime().freeMemory()/(1024*1024);
        long total = Runtime.getRuntime().totalMemory()/(1024*1024);
        long used  = total-free;
        info(ErrorDump.class,
             "memory used: %dMB free: %dMB total: %dMB",
             used, free, total);
    }

}
