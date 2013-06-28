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
package at.tuwien.prip.common.utils.resourceloader;

import java.security.PrivilegedAction;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * ResourceLoader.java
 *
 * class for loading resources (xml files, html template
 * files, etc.) from class repository (a jar file)
 *
 * Created: Fri Dec 14 19:01:51 2001
 *
 * @author Michal Ceresna
 * @version
 */
public class ResourceLoader {

  /**
   * object_requesting_resource is an object, which is requesting
   * for loading of the resource. Needed due security restrictions
   * e.g. when running in servlet, to resolve correct class loader.
   * Passing 'this' in caller is sufficient.
   *
   * resource_path is string with path to a resource in the
   * class repository e.g.:
   * 'org/weblearn/session/example.html'
   *
   * returns a stream for reading from the resource
   */
  public static InputStream getResourceAsStream(Object object_requesting_resource,
                                                String path_to_resource)
  {
    return getResourceAsStream(object_requesting_resource.getClass(),
                               path_to_resource);
  }
  @SuppressWarnings("unchecked")
  public static InputStream getResourceAsStream(Class clazz_requesting_resource,
                                                String path_to_resource)
  {
    ClassLoader loader =
      clazz_requesting_resource.getClassLoader();
    if (loader!=null) {
      PrivilegedAction a = new StreamResourceLoader(path_to_resource,loader);
      return (InputStream) java.security.AccessController.doPrivileged(a);
    }

    //if loader is no present,
    //try to load the resource directly
    //(may fail due security reasons)
    return clazz_requesting_resource.getResourceAsStream(path_to_resource);
  }

  /**
   * object_requesting_resource is an object, which is requesting
   * for loading of the resource. Needed due security restrictions
   * e.g. when running in servlet, to resolve correct class loader.
   * Passing 'this' in caller is sufficient.
   *
   * resource_path is string with path to a resource in the
   * class repository e.g.:
   * 'org/weblearn/session/example.html'
   *
   * returns an url to the resource
   */
  public static URL getResourceAsURL(Object object_requesting_resource,
                                     String path_to_resource)
  {
    return getResourceAsURL(object_requesting_resource.getClass(),
                            path_to_resource);
  }
  @SuppressWarnings("unchecked")
  public static URL getResourceAsURL(Class clazz_requesting_resource,
                                     String path_to_resource)
  {
    ClassLoader loader = clazz_requesting_resource.getClassLoader();
    if (loader!=null) {
      PrivilegedAction a = new URLResourceLoader(path_to_resource,loader);
      return (URL) java.security.AccessController.doPrivileged(a);
    }

    //if loader is no present,
    //try to load the resource directly
    //(may fail due security reasons)
    return clazz_requesting_resource.getResource(path_to_resource);
  }

//  /**
//   * returns a stream for reading from the resource
//   *
//   * IMPORTANT:
//   * Note, that this method may cause errors due security
//   * restrictions in servlet environment, so in case you
//   * don't understand really well security & class loading
//   * issues, please use a method that passes the
//   * 'object_requesting_resource' parameter
//   */
//  private static InputStream getResourceAsStream(ClassLoader loader,
//                                                 String path_to_resource)
//  {
//    PrivilegedAction a = new StreamResourceLoader(path_to_resource,loader);
//    return (InputStream) java.security.AccessController.doPrivileged(a);
//  }

  /**
   * object_requesting_resource is an object, which is requesting
   * for loading of the resource. Needed due security restrictions
   * e.g. when running in servlet, to resolve correct class loader.
   * Passing 'this' in caller is sufficient.
   *
   * classpath_basename is  path to a resource
   * in the class repository without suffix e.g.:
   * 'org/weblearn/session/example'
   * suffix is e.g. '.html'
   *
   * then method will search wrt. the given locale
   * for localized version of the file e.g.
   * 'org/weblearn/session/example_de.html'
   * 'org/weblearn/session/example_de_AT.html'
   * 'org/weblearn/session/example.html' (unlocalized fallback)
   *
   * returns a stream for reading from the resource
   */
  public static InputStream getResourceAsStream(Object object_requesting_resource,
                                                String classpath_basename,
                                                String suffix,
                                                Locale locale)
  {
    return getResourceAsStream(object_requesting_resource.getClass(),
                               classpath_basename,
                               suffix,
                               locale);
  }
  public static InputStream getResourceAsStream(Class<?>clazz_requesting_resource,
                                                String classpath_basename,
                                                String suffix,
                                                Locale locale)
  {
    List<String> localized_classpaths =
      generateLocalizedResourceNames(classpath_basename, suffix, locale);
    Iterator<String> it = localized_classpaths.iterator();
    while (it.hasNext()) {
      String classpath = (String) it.next();
      InputStream is = getResourceAsStream(clazz_requesting_resource,
                                           classpath);
      if (is!=null) {
        return is;
      }
    }
    //resource not found
    return null;
  }

  /**
   * Tries to collect localized resources according to
   * First in the list (aBag) is the most specific one (the closest to the
   * required one), the last is the most general one.
   *
   * @param aBag - (OUT) list of found InputStreams (the most specific first)
   * @return count of the resources added to bag
   */
  public static int getResourcesAsStreams(Class<?>clazz_requesting_resource,
                                     String aResourceBasename,
                                     String aResourceSuffix,
                                     Locale aPreferredLocale,
                                     List<InputStream> aBag)
  {
    List<String> localized_classpaths = generateLocalizedResourceNames(
      aResourceBasename, aResourceSuffix, aPreferredLocale);
    int count = 0;
    Iterator<String> it = localized_classpaths.iterator();
    while (it.hasNext()) {
      String classpath = (String) it.next();
      InputStream is = getResourceAsStream(clazz_requesting_resource,
                               classpath);
      if (is!=null)
      {
        aBag.add(is);
        count++;
      }
    }
    return count;
  }

  /**
   * object_requesting_resource is an object, which is requesting
   * for loading of the resource. Needed due security restrictions
   * e.g. when running in servlet, to resolve correct class loader.
   * Passing 'this' in caller is sufficient.
   *
   * classpath_basename is  path to a resource
   * in the class repository without suffix e.g.:
   * 'org/weblearn/session/example'
   * suffix is e.g. '.html'
   *
   * then method will search wrt. the given locale
   * for localized version of the file e.g.
   * 'org/weblearn/session/example_de.html'
   * 'org/weblearn/session/example_de_AT.html'
   * 'org/weblearn/session/example.html' (unlocalized fallback)
   *
   * returns an url to the resource
   */
  public static URL getResourceAsURL(Object object_requesting_resource,
                                     String classpath_basename,
                                     String suffix,
                                     Locale locale)
  {
    return getResourceAsURL(object_requesting_resource.getClass(),
                            classpath_basename,
                            suffix,
                            locale);
  }
  public static URL getResourceAsURL(Class<?> clazz_requesting_resource,
                                     String classpath_basename,
                                     String suffix,
                                     Locale locale)
  {
    List<String> localized_classpaths =
      generateLocalizedResourceNames(classpath_basename, suffix, locale);
    Iterator<String> it = localized_classpaths.iterator();
    while (it.hasNext()) {
      String classpath = (String) it.next();
      URL u = getResourceAsURL(clazz_requesting_resource,
                               classpath);
      if (u!=null) {
        return u;
      }
    }
    //resource not found
    return null;
  }

  /**
   * Tries to collect localized resources according to
   * First in the list (aBag) is the most specific one (the closest to the
   * required one) , the last is the most general one.
   *
   * @param aBag - (OUT) list where URLs are stored (the most specific first)
   * @return count of the resources added to bag
   */
  public static int getResourcesAsURLs(Class<?> clazz_requesting_resource,
                                     String aResourceBasename,
                                     String aResourceSuffix,
                                     Locale aPreferredLocale,
                                     List<URL> aBag)
  {
    List<String> localized_classpaths = generateLocalizedResourceNames(
      aResourceBasename, aResourceSuffix, aPreferredLocale);
    int count = 0;
    Iterator<String> it = localized_classpaths.iterator();
    while (it.hasNext()) {
      String classpath = (String) it.next();
      URL u = getResourceAsURL(clazz_requesting_resource,
                               classpath);
      if (u!=null)
      {
        aBag.add(u);
        count++;
      }
    }
    return count;
  }


  /**
   * generates list of classpath, where to search
   * for localized version of the file
   *
   * basename is path to a resource
   * in the class repository without suffix e.g.:
   * 'org/weblearn/session/example'
   * suffix is e.g. '.html'
   *
   * then method will generate wrt. the given locale e.g:
   * 'org/weblearn/session/example_de.html'
   * 'org/weblearn/session/example_de_AT.html'
   * 'org/weblearn/session/example.html' (unlocalized fallback)
   *
   * returns an url to the resource

  private static List generateLocalizedClasspaths(String basename,
                                                  String suffix,
                                                  Locale locale)
  {
    List localized_files = new LinkedList();

    final String language = locale.getLanguage();
    final int language_length = language.length();
    final String country = locale.getCountry();
    final int country_length = country.length();
    final String variant = locale.getVariant();
    final int variant_length = variant.length();

    if (language_length > 0) {
      localized_files.add(basename +
                          "_" + language +
                          suffix);
    }

    if (language_length > 0 && country_length > 0) {
      localized_files.add(basename +
                          "_" + language + "_" + country +
                          suffix);
    }

    if (language_length > 0 && variant_length > 0) {
      localized_files.add(basename +
                          "_" + language + "_" + variant +
                          suffix);
    }

    localized_files.add(basename + suffix);

    return localized_files;
  }
*/
   /**
   * Generates ordered list of localized resourceNames.
   * <pre>
   * ex:
   * aBaseName = errors
   * aPreferredLocale = new Locale("de","AT")
   * aSuffix = .txt
   *
   * then the sequence is:
   *
   * 0. errors_de_AT.txt
   * 1. errors_de.txt
   * 2. errors.txt
   * </pre>
   */
   private static List<String> generateLocalizedResourceNames(
      String aBaseName, String aSuffix, Locale aPredferredLocale)
   {
      final LinkedList<String> result = new LinkedList<String>();
      final String language = aPredferredLocale.getLanguage();
      final int languageLength = language.length();
      final String country = aPredferredLocale.getCountry();
      final int countryLength = country.length();
      final String variant = aPredferredLocale.getVariant();
      final int variantLength = variant.length();

      result.addFirst(aBaseName+aSuffix);
      final StringBuffer temp = new StringBuffer(aBaseName);

      temp.append('_');
      temp.append(language);
      if (languageLength > 0) {
         result.addFirst(temp.toString()+aSuffix);
      }

      temp.append('_');
      temp.append(country);
      if (countryLength > 0) {
         result.addFirst(temp.toString()+aSuffix);
      }

      temp.append('_');
      temp.append(variant);
      if (variantLength > 0) {
         result.addFirst(temp.toString()+aSuffix);
      }
      return result;
   }

/*   public static void main(String[] arg)
   {
      Locale loc = new Locale( "de", "AT" );
      List l = generateLocalizedResourceNames("errors",".txt",loc);
      Iterator iter = l.iterator();
      while (iter.hasNext())
      {
         ErrorDump.debug(this, iter.next().toString());
      }
   }
*/

}// ResourceLoader
