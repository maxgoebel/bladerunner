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

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;

import at.tuwien.prip.common.utils.D;
import at.tuwien.prip.common.utils.PeekIterator;

/** This class is part of the
 <A HREF=http://www.mpi-inf.mpg.de/~suchanek/downloads/javatools target=_blank>
 Java Tools
 </A> by <A HREF=http://www.mpi-inf.mpg.de/~suchanek target=_blank>
 Fabian M. Suchanek</A>
 You may use this class if (1) it is not for commercial purposes,
 (2) you give credit to the author and (3) you use the class at your own risk.
 If you use the class for scientific purposes, please cite our paper
 "Combining Linguistic and Statistical Analysis to Extract Relations from Web Documents"
 (<A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.pdf target=_blank>pdf</A>,
 <A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.bib target=_blank>bib</A>,
 <A HREF=http://www.mpi-inf.mpg.de/~suchanek/publications/kdd2006.ppt target=_blank>ppt</A>
 ). If you would like to use the class for commercial purposes, please contact
 <A HREF=http://www.mpi-inf.de/~suchanek>Fabian M. Suchanek</A><P>

 This class represents a set of files as given by a wildcard string.
 It can also recurse into subfolders.
 It does not return folders and is not case-sensitive.
 The class can be used as an Iterator or Iterable (e.g. in a for-each-loop).<BR>
 Example:
 <PRE>
 for(File f : new DeepFileSet("c:\\myfiles","*.jaVa"))
 System.out.println(f);
 -->
 c:\myfiles\FileSet.java
 c:\myfiles\HTMLReader.java
 c:\myfiles\mysubfolder\OtherFile.java
 ...
 </PRE>

 */
public class DeepFileSet extends PeekIterator<File> {

  protected final Stack<File> paths = new Stack<File>();

  protected final Pattern wildcard;

  protected Iterator<File> currentIterator;

  public Pattern patternForWildcard(String wildcard) {
    return (Pattern.compile("(?i)" + wildcard.replace(".", "\\.").replace("*",".*").replace('?', '.')));
  }

  /** Constructs a DeepFileSet from a path that ends in a wildcard*/
  public DeepFileSet(File folderPlusWildcard) {
    this(folderPlusWildcard.getParentFile()==null?
        new File("."):folderPlusWildcard.getParentFile(),folderPlusWildcard.getName());
  }

  /** Constructs a DeepFileSet from a path that ends in a wildcard*/
  public DeepFileSet(String folderPlusWildcard) {
    this(new File(folderPlusWildcard));
  }

  /** Constructs a DeepFileSet from path and wildcard */
  public DeepFileSet(File folder, String wildcard) {
    File path = folder;
    paths.push(path);
    this.wildcard = patternForWildcard(wildcard);
    setIterator();
  }

  /** Pops a path, sets the iterator to the files in the path*/
  protected boolean setIterator() {
    if (paths.size() == 0) return (false);
    File folder = paths.pop();
    currentIterator = Arrays.asList(folder.listFiles(new FileFilter() {

      public boolean accept(File pathname) {
        if (pathname.isDirectory()) paths.push(pathname);
        return (wildcard.matcher(pathname.getName()).matches());
      }
    })).iterator();
    return (true);
  }

  @Override
  protected File internalNext() throws Exception {
    while (!currentIterator.hasNext()) {
      if (!setIterator()) return (null);
    }
    return (currentIterator.next());
  }

  /** Returns the current state of this DeepFileSet */
  public String toString() {
    return ("DeepFileSet at " + paths);
  }

  /** Test routine */
  public static void main(String argv[]) {
    D.p("Enter a filename with wildcards and hit ENTER. Press CTRL+C to abort");
    while (true) {
      for (File f : new DeepFileSet(D.r()))
        D.p(f);
    }
  }
}
