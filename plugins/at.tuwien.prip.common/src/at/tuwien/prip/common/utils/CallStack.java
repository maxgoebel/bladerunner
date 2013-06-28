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
package at.tuwien.prip.common.utils;

import java.util.*;

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

This class represents the current position of a program, i.e. the stack of methods that
have been called together with the line numbers.<BR>
Example:<BR>
<PRE>
   public class Blah {
     public m2() {
       System.out.println(new CallStack());   // (1)
       System.out.println(CallStack.here());  // (2)
     }

     public m1() {
       m2();
     }

     public static void main(String[] args) {
       m1();
     }
   }
   -->
      Blah.main(12)->Blah.m1(8)->Blah.m2(2)  // Call Stack at (1)
      Blah.m2(3)                             // Method and line number at (2)
</PRE>
*/
public class CallStack {
  /** Holds the call stack */
  public Stack<StackTraceElement> callstack=new Stack<StackTraceElement>();

  /** Constructs a call stack from the current program position (without the constructor call)*/
  public CallStack() {
    try {
      throw new Exception();
    } catch(Exception e) {
      StackTraceElement[] s=e.getStackTrace();
      for(int i=s.length-1;i!=0;i--) callstack.push(s[i]);
    }
  }

  /** Returns TRUE if the two call stacks have the same elements*/
  public boolean equals(Object o) {
    return(o instanceof CallStack && ((CallStack)o).callstack.equals(callstack));
  }

  /** Returns a nice String for a Stacktraceelement*/
  public static String toString(StackTraceElement e) {
    String cln=e.getClassName();
    if(cln.lastIndexOf('.')!=-1) cln=cln.substring(cln.lastIndexOf('.')+1);
    return(cln+"."+e.getMethodName()+'('+e.getLineNumber()+')');
  }

  /** Returns "method(line)->method(line)->..." */
  public String toString() {
    StringBuilder s=new StringBuilder();
    for(int i=0;i<callstack.size()-1;i++) {
      s.append(toString(callstack.get(i))).append("->");
    }
    s.append(toString(callstack.get(callstack.size()-1)));
    return(s.toString());
  }

  /** Gives the calling position as a StackTraceElement */
  public StackTraceElement top() {
    return(callstack.peek());
  }

  /** Gives the calling position as a nice String */
  public static String here() {
    CallStack p=new CallStack();
    p.callstack.pop();
    return(toString(p.callstack.peek()));
  }

  /** Returns the callstack */
  public Stack<StackTraceElement> getCallstack() {
    return callstack;
  }

  /** Pops the top level of this callstack, returns this callstack */
  public CallStack ret() {
    callstack.pop();
    return(this);
  }

  /** Test routine */
  public static void main(String[] args) {
    D.p(new CallStack());
    D.p(here());
  }

}
