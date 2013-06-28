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
package at.tuwien.prip.common.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.StringUtils;

public class FileUtils  {
	
	public static boolean copyFile(File in, File out) throws Exception {
		FileInputStream fis  = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} 
		catch (Exception e) {
			throw e;
		}
		finally {
			if (fis != null) fis.close();
			if (fos != null) fos.close();
		}
		
		return true;
	}

    public static String replaceExtensionInFileName(String file_name,
                                                    String in_ext,
                                                    String out_ext)
    {
        int idx = file_name.lastIndexOf('.');
        if (idx>=0 && (in_ext==null ||
            file_name.substring(idx+1).equalsIgnoreCase(in_ext)))
            return file_name.substring(0, idx+1) + out_ext;

        return file_name + "." + out_ext;
    }

    public static String joinPaths(String path1, String path2) {
        if (path1.endsWith(""+File.separatorChar))
            return path1+path2;

        return path1+File.separatorChar+path2;
    }

    public static String getFileName(String path) {
        int idx = path.lastIndexOf(File.separatorChar);
        return (idx>=0 ? path.substring(idx+1) : path);
    }

    /**
     *
     * @param file
     * @param name
     * @return
     */
    public static File getLastNamedFile (File parent, String name) {
    	String[] files = parent.list();
    	Map<Integer,String> indices2files =
    		new HashMap<Integer,String>();
    	for (int i=0; i<files.length; i++) {
    		String curr = files[i];
        	String[] splits = curr.split("\\.");
        	String fileName = splits[0];

        	if (fileName.startsWith(name)){
        		List<String> chars = StringUtils.splitChars(fileName);
        		LinkedList<String>indexList = new LinkedList<String>();
        		for (int j=chars.size()-1; j>=0; j--) {
        			try {
        				Integer.parseInt(chars.get(j));
        				indexList.addFirst(chars.get(j));
        			} catch (NumberFormatException e) {
        				break;
        			}
        		}
        		if (indexList.size()>0) {
        			StringBuffer sb = new StringBuffer();
        			for (String c : indexList) {
        				sb.append(c);
        			}
        			try {
        				indices2files.put(Integer.parseInt(sb.toString()),curr);
        			} catch (NumberFormatException e) {
        				System.err.println("FileUtils::getLastFileName: cannot format string");
        			}
        		} else {
        			indices2files.put(-1,curr);
        		}
        	}
    	}
    	LinkedList<Integer> sortedList = new LinkedList<Integer>(indices2files.keySet());
    	Collections.sort(sortedList);

    	if (sortedList.size()==0) return null;

    	return new File(
    			parent.getAbsolutePath() +
    			File.separator +
    			indices2files.get(sortedList.getLast())
    			);
    }

    public static void createZip(File f, OutputStream os)
        throws IOException
    {
        ZipOutputStream zos = new ZipOutputStream(os);
        zos.setMethod(ZipOutputStream.DEFLATED);
        recZip(zos, f, "");
        zos.close();
    }

    private static void recZip(ZipOutputStream zos, File zipBaseDir, String relPath)
        throws IOException
    {
        File f = new File(zipBaseDir, relPath);
        if (f.exists()) {
            if (f.isDirectory()) {
                //if dir is found, delete all children first
                String [] flist = f.list();
                for (int i=0; i<flist.length; i++) {
                    String childRelPath =
                        relPath.length()>0?
                        relPath+File.separator+flist[i]:
                        flist[i];
                    recZip(zos, zipBaseDir, childRelPath);
                }
            }
            else {
                //ErrorDump.debug(FileUtils.class, "zipping: "+relPath);

                InputStream in = new BufferedInputStream(new FileInputStream(f));
                //add zip entry to output stream.
                zos.putNextEntry(new ZipEntry(relPath));
                //transfer bytes from the file to the ZIP file
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }

                //complete the entry
                zos.closeEntry();
                in.close();
            }
        }
    }

    public static byte[] readFile(File f)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        FileInputStream fis = new FileInputStream(f);
        InputStream is = new BufferedInputStream(fis);
        int len;
        byte[] buf = new byte[1024];
        while ((len=is.read(buf))!=-1) {
            bos.write(buf,0, len);
        }
        is.close();
        fis.close();

        return bos.toByteArray();
    }

    public static byte[] readStream(InputStream is) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1000];
            int n;
            while ((n=is.read(buf, 0, buf.length))!=-1) {
                bos.write(buf, 0, n);
            }
            is.close();
            return bos.toByteArray();
        } catch (IOException e) {
            ErrorDump.error(StringUtils.class, e);
            return new byte[0];
        }
    }

    /**
     * Safely converts a 'file:...' url into a File,
     * (correcly handles spaces in the url)
     */
    public static File toFile(URL url) {
        try {
            String path = URLDecoder.decode(url.getPath(), "UTF-8");
            String proto = url.getProtocol();
            return toFile(path, proto);
        } catch (UnsupportedEncodingException e) {
            //should not happen
            ErrorDump.error(FileUtils.class, e);
            throw new RuntimeException(e);
        }
    }
    public static File toFile(URI uri) {
        try {
            String path = URLDecoder.decode(uri.getPath(), "UTF-8");
            String proto = uri.getScheme();
            return toFile(path, proto);
        } catch (UnsupportedEncodingException e) {
            //should not happen
            ErrorDump.error(FileUtils.class, e);
            throw new RuntimeException(e);
        }
    }
    public static File urlToFile(String uri) {
        try {
            if (uri.startsWith("file:")) {
                URL u = new URL(uri);
                return toFile(u);
            }
            return null;

        } catch (MalformedURLException e) {
            ErrorDump.error(FileUtils.class, e);
            throw new RuntimeException(e);
        }
    }
    private static File toFile(String path, String proto) {
        if (File.separatorChar != '/')
            path = path.replace('/', File.separatorChar);

        //on win32 remove the leading '/'
        if (File.separatorChar == '\\' &&
            proto!=null && proto.equals("file") &&
            path.startsWith("\\"))
        {
            path = path.substring(1);
        }

        return new File(path);
   }

    public static String getRelativePath(File home, File f) {
        return RelativePath.getRelativePath(home, f);
    }

    /**
     * Find and return all files matching the filefilter filter which
     * are anywhere under the given root file. Traverses all subdirectories.
     * @param dir
     * @param name
     * @return
     */
    public static List<File> getAllNamedSubFiles (File dir, FileFilter filter) {
    	List<File> result = new LinkedList<File> ();
    	Stack<File> dirStack = new Stack<File> ();
    	if (dir.isDirectory()) dirStack.add(dir);

    	while(!dirStack.isEmpty()) {
    		File d = dirStack.pop();
    		File[] subdirs =
    			d.listFiles(
    				new FileFilter() {
    					public boolean accept(File file) {
    						return file.isDirectory();
    					}
    				});
    		File[] htmlFiles = d.listFiles(filter);
    		for (int i=0; i<subdirs.length; i++)
    			dirStack.add(subdirs[i]);
    		for (int i=0; i<htmlFiles.length; i++)
    			result.add(htmlFiles[i]);
    	}
    	return result;
    }

//    /**
//     * Same as locate, but stop after the first occurance.
//     * @param filename
//     * @return
//     */
//    public static URL locateFirst (String filename) {
//    	return locate(filename,true)[0];
//    }
//
//    /**
//     * Try to locate a file within the scope of this installation.
//     * @param filename
//     * @return the file URL or null if no such file exists
//     */
//    public static URL[] locate (String filename, boolean prune) {
//
//    	List<URL> resultList = new LinkedList<URL>();
//    	URL installDir = null;
//
//    	installDir = BasePropertiesLoader.getPluginInstallDirectory();//getBaseInstallDirectory();
//    	if (installDir==null) {
//    		return ListUtils.toArray(resultList, URL.class);
//    	}
//
//		ArrayQueue fileQueue = new ArrayQueue();
//		File f = new File(installDir.getPath());
//
//		fileQueue.enqueue(f);
//
//		while (!fileQueue.isEmpty()) {
//
//			File top = (File) fileQueue.dequeue();
//			if (top.getName().equals(filename)) {
//				try {
//					resultList.add(top.getAbsoluteFile().toURI().toURL());
//					if (prune)
//						break; //really only want one, faster...
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				}
//			}
//			for (File file : f.listFiles())
//				fileQueue.enqueue(file);
//		}
//
//    	return ListUtils.toArray(resultList, URL.class);
//    }

    /**
     * 
     * Write a string to file.
     * 
     * @param input
     * @param fileName
     * @throws IOException
     */
    public static void writeStringToFile (String input, String fileName) 
    throws IOException {
    	
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    	writer.write(input);
    	writer.flush();
    	writer.close();
    }

//    public List<String> getDirectoryFiles(File directory) {
//        List<String> result = new LinkedList<String>();
//       // result = directory.list();
//        return result;
//    }
}
