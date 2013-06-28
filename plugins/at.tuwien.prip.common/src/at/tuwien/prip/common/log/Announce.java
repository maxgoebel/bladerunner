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
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import at.tuwien.prip.common.utils.CallStack;
import at.tuwien.prip.common.utils.D;
import at.tuwien.prip.common.utils.NumberFormatter;

/** This class is part of the
<A HREF=http://www.mhttp://www.mpi-inf.de/~suchanekpi-inf.mpg.de/~suchanek/downloads/javatools target=_blank>
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

This class can make progress announcements. The announcements are handled by an object,
but static methods exist to simplify the calls.<BR>
Example:
<PRE>
    Announce.doing("Testing 1");
    Announce.doing("Testing 2");
    Announce.message("Now testing", 3);
    Announce.warning(1,2,3);
    Announce.debug(1,2,3);
    Announce.doing("Testing 3a");
    Announce.doneDoing("Testing 3b");
    Announce.done();
    Announce.progressStart("Testing 3c",5); // 5 steps
    D.waitMS(1000);
    Announce.progressAt(1); // We're at 1 (of 5)
    D.waitMS(3000);
    Announce.progressAt(4); // We're at 4 (of 5)
    D.waitMS(1000);
    Announce.progressDone();
    Announce.done();
    Announce.done();
    Announce.done(); // This is one too much, but it works nevertheless
  -->
    Testing 1...
      Testing 2...
        Now testing 3
        Warning:1 2 3
        Announce.main(243)==> 1 2 3
        Testing 3a... done
        Testing 3b... done
        Testing 3c...........(4.00 s to go)................................ done (5.00 s)
      done
    done
</PRE>
The progress bar always walks to MAXDOTS dots. The data is written to Announce.out
(by default System.err). The Announcements can be switched on and off, also locally
with setAnnounceLocally/unLocal.
*/
public class Announce implements Closeable {
  /** Maximal number of dots */
  public static int MAXDOTS=40;
  /** Current Announce object */
  public static Announce current=new Announce();

  /** Where to write to (default: System.err) */
  public Writer out=new BufferedWriter(new OutputStreamWriter(System.err));
  /** Switches the writer on or off */
  public boolean on=true;
  /** Indentation level */
  protected int doingLevel=-1;
  /** Are we at the beginning of a line?*/
  protected boolean newLine=true;
  /** Memorizes the maximal value for progressAt(...) */
  protected double progressEnd=0;
  /** Memorizes the number of printed dots */
  protected int progressDots=0;
  /** Memorizes the process start time */
  protected long progressStart=0;
  /** Memorizes the timer */
  protected long timer;

  /** Creates a new Announce object with default settings */
  public Announce() {
  }

  /** Creates a new Announce object with the settings from the given Announce object */
  public Announce(Announce a) {
    out=a.out;
    on=a.on;
    doingLevel=a.doingLevel;
    newLine=a.newLine;
    progressEnd=a.progressEnd;
    progressDots=a.progressDots;
    progressStart=a.progressStart;
  }

  /** Sets the current announcer, returns previous */
  public static Announce setCurrent(Announce a) {
    Announce prev=current;
    current=a;
    return(prev);
  }

  /** Returns the current announcer*/
  public static Announce getCurrent() {
    return(current);
  }

  /** Starts the timer */
  public void startTimerO() {
    timer=System.currentTimeMillis();
  }

  /** Retrieves the time */
  public long getTimeO() {
    return(System.currentTimeMillis()-timer);
  }

  /** Closes the writer */
  public void closeO() throws IOException{
    out.close();
  }

  /** Switches announcing on or off */
  public void setAnnounceO(boolean o) {
    on=o;
  }

  /** Internal printer */
  protected void printO(Object... o) {
    if(!on) return;
    try {
      if(newLine) for(int i=0;i<=doingLevel;i++) out.write("  ");
      if(o==null) out.write("null");
      else {
        for(Object o1 : o) {
          if(o1==null) out.write("null");
          else out.write(o1.toString());
          if(o.length>1) out.write(" ");
        }
      }
      out.flush();
    }
    catch(IOException e) {}
    newLine=false;
  }

  /** Internal printer for new line */
  protected void newLineO() {
    if(!on || newLine) return;
    try {
      out.write("\n");
      out.flush();
    }
    catch(IOException e) {}
    newLine=true;
  }

  /** Prints an (indented) message */
  public void messageO(Object... o) {
    newLineO();
    printO(o);
    newLineO();
  }

  /** Prints a debug message with the class and method name preceeding */
  public void debugO(Object... o) {
    newLineO();
    printO(CallStack.toString(new CallStack().ret().top())+"==> ");
    printO(o);
    newLineO();
  }

  /** Prints an error message and aborts (even if Announce is off)*/
  public void errorO(Object... o) {
    try {
    out.write("\nError:");
    for(Object s : o) {
      out.write(" ");
      out.write(s.toString());
    }
    out.write("\n");
    out.flush();
    } catch(IOException e) {}
    System.exit(255);
  }

  /** Prints an exception and aborts (even if Announce is off)*/
  public void exceptionO(Exception e, Object... o) {
    try {
    e.printStackTrace(new PrintWriter(out));
    out.write("\n");
    for(Object s : o) {
      out.write(" ");
      out.write(s.toString());
    }
    out.write("\n");
    out.flush();
    } catch(IOException ex) {}
    System.exit(255);
  }

  /** Prints a warning*/
  public void warningO(Object... o) {
    newLineO();
    printO("Warning: ");
    printO(o);
    newLineO();
  }


  /** Sets the writer the data is written to */
  public void setWriterO(Writer w) {
    out=w;
  }

  /** Sets the writer the data is written to */
  public void setWriterO(OutputStream s) {
    out=new BufferedWriter(new OutputStreamWriter(s));
  }

  /** Gets the writer the data is written to (e.g. to close it)*/
  public  Writer getWriterO() {
    return(out);
  }

  /** Writes "s..."*/
  public void doingO(Object... o) {
    newLineO();
    printO(o);
    printO("... ");
    doingLevel++;
  }

  /** Writes "failed NEWLINE" */
  public void failedO() {
    if(doingLevel>=0) {
      doingLevel--;
      printO("failed");
      newLineO();
    }
  }

  /** Writes "done NEWLINE"*/
  public void doneO() {
    if(doingLevel>=0) {
      doingLevel--;
      printO("done");
      newLineO();
    }
  }

  /** Calls done() and doing(...)*/
  public void doneDoingO(Object... s) {
    doneO();
    doingO(s);
  }

  /** Writes s, prepares to make progress up to max */
  public void progressStartO(String s, double max) {
    progressEnd=max;
    progressDots=0;
    progressStart=System.currentTimeMillis();
    newLineO();
    printO(s+"...");
    doingLevel++;
  }

  /** Notes that the progress is at d, prints dots if necessary,
   * calculates and displays the estimated time at 1/10 of the progress */
  public void progressAtO(double d) {
    if(d>progressEnd || d*MAXDOTS/progressEnd<=progressDots) return;
    StringBuilder b=new StringBuilder();
    boolean printEstimate=progressDots<MAXDOTS/10;
    while(d*MAXDOTS/progressEnd>progressDots) {
      progressDots++;
      b.append(".");
    }
    if(printEstimate && progressDots>=MAXDOTS/10 &&
          System.currentTimeMillis()-progressStart>300) b.append('(').append(
          NumberFormatter.formatMS((long)((System.currentTimeMillis()-progressStart)*(progressEnd-d)/d)))
          .append(" to go)");
    printO(b);
  }

  /** Fills missing dots and writes "done NEWLINE"*/
  public void progressDoneO() {
    progressAtO(progressEnd);
    doingLevel--;
    printO(" done ("+NumberFormatter.formatMS(System.currentTimeMillis()-progressStart)+")");
    newLineO();
  }

  /** Writes "failed NEWLINE"*/
  public void progressFailedO() {
    failedO();
  }

  /** Writes a help text and exits */
  public void helpO(String... o) {
    try {
      out.write("\n");
      for(String s : o) {
        out.write(s);
        out.write("\n");
      }
      out.flush();
    } catch(IOException e) {}
    System.exit(63);
  }

  /** Switches announcing on or off */
  public static void setAnnounce(boolean o) {
    current.setAnnounceO(o);
  }

  /** Prints an (indented) message */
  public static void message(Object... o) {
    current.messageO(o);
  }

  /** Prints a debug message with the class and method name preceeding */
  public static void debug(Object... o) {
    current.newLineO();
    current.printO(CallStack.toString(new CallStack().ret().top())+"==> ");
    current.printO(o);
    current.newLineO();
  }


  /** Prints an error message and aborts (even if Announce is off)*/
  public static void error(Object... o) {
    current.errorO(o);
  }

  /** Prints an exception and aborts (even if Announce is off)*/
  public static void exception(Exception e, Object... o) {
    current.exceptionO(e, o);
  }

  /** Prints a warning*/
  public static void warning(Object... o) {
    current.warningO(o);
  }


  /** Sets the writer the data is written to */
  public static void setWriter(Writer w) {
    current.setWriterO(w);
  }

  /** Sets the writer the data is written to */
  public static void setWriter(OutputStream s) {
    current.setWriterO(s);
  }

  /** Gets the writer the data is written to (e.g. to close it)*/
  public static Writer getWriter() {
    return(current.getWriterO());
  }

  /** Writes "s..."*/
  public static void doing(Object... o) {
    current.doingO(o);
  }

  /** Writes "failed NEWLINE" */
  public static void failed() {
    current.failedO();
  }

  /** Writes "done NEWLINE"*/
  public static void done() {
    current.doneO();
  }

  /** Calls done() and doing(...)*/
  public static void doneDoing(String s) {
    current.doneDoingO(s);
  }

  /** Writes s, prepares to make progress up to max */
  public static void progressStart(String s, double max) {
    current.progressStartO(s, max);
  }

  /** Notes that the progress is at d, prints dots if necessary,
   * calculates and displays the estimated time at 1/10 of the progress */
  public static void progressAt(double d) {
    current.progressAtO(d);
  }

  /** Fills missing dots and writes "done NEWLINE"*/
  public static void progressDone() {
    current.progressDoneO();
  }

  /** Writes "failed NEWLINE"*/
  public static void progressFailed() {
    current.progressFailedO();
  }

  /** Writes a help text and exits */
  public static void help(String... o) {
    current.helpO(o);
  }

  /** Closes the writer */
  public void close() throws IOException{
    current.closeO();
  }

  /** Starts the timer */
  public static void startTimer() {
    current.startTimerO();
  }

  /** Retrieves the time */
  public static long getTime() {
    return(current.getTimeO());
  }

  /** Retrieves the time */
  public void printTimeO() {
    messageO("Time:", NumberFormatter.formatMS(getTimeO()));
  }

  /** Retrieves the time */
  public static void printTime() {
    current.printTimeO();
  }

  /** Test routine */
  public static void main(String[] args) {
    Announce.startTimer();
    Announce.doing("Testing 1");
    Announce.doing("Testing 2");
    Announce.message("Now testing", 3);
    Announce.warning(1,2,3);
    Announce.debug(1,2,3);
    Announce.doing("Testing 3a");
    Announce.doneDoing("Testing 3b");
    Announce.done();
    Announce.progressStart("Testing 3c",5); // 5 steps
    D.waitMS(1000);
    Announce.progressAt(1); // We're at 1 (of 5)
    D.waitMS(3000);
    Announce.progressAt(4); // We're at 4 (of 5)
    D.waitMS(1000);
    Announce.progressDone();
    Announce.done();
    Announce.done();
    Announce.done(); // This is one too much, but it works nevertheless
    Announce.printTime();
  }
}
