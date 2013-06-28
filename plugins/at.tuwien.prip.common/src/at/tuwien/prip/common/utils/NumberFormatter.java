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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

   This class is a simple number formatter. Furthermore, this class offers some methods for the
   <A HREF=http://www.cl.cam.ac.uk/~mgk25/iso-time.html>ISO8601 time format</A><BR>
   Example:
   <PRE>
         System.out.println(new NumberFormatter("+##.###").format(3.1119));
         -->      +03.112
         System.out.println(NumberFormatter.ISOtime());
         -->      2006-01-17 T 21:01:00.000
         System.out.println(NumberFormatter.ISOweekTime());
         -->      2006-w03-2 T 21:01:00.000
   </PRE>
   The pattern for numbers may look like                                   <BR>
   <PRE>

        [+|-|+/-]  #*[x]#* [.#*]

   </PRE>
   without blanks. 'x' is a separator character for groups of 3 digits. Only one
   x will be recognized and it will always separate groups of 3, no matter where it
   appears. Examples:
   <PRE>
       +###.#####
       -#
       +/-#.######
       ##
       +#'###'###.##
   </PRE>
   If the formatted number is negative, a negative sign is always output. If the number
   is positive, a sign will only be output if the pattern starts with "+" or "+/-".
   The output will contain at least as many digits as specified in the pattern.
   If the integer part of a number contains more digits than specified in the pattern,
   the digits will be output nevertheless.
   If the fractional part of a number contains more digits than specified in the pattern,
   the number will be rounded and the digits will not be output.
   Instead of '#', you may write digits.<BR>
  */
public class NumberFormatter {
  /** Holds the number of integer digits*/
  protected int integers=0;
  /** Holds the number of fraction digits*/
  protected int fractions=2;
  /** TRUE if positive sign shall be displayed*/
  protected boolean showPosSign=false;
  /** Separates groups of 3 digits */
  protected char separator=(char)0;
  /** Pattern for the pattern string*/
  protected Pattern FORMAT=Pattern.compile("(\\[?(?:\\+/\\-|\\+|\\-)\\]?)?([^\\.]+)(\\.[0-9#]+)?( .*)?");
  /** What shall be appended*/
  protected String append="";

  /** Creates a NumberFormatter for a pattern */
  public NumberFormatter(String f) {
    Matcher m=FORMAT.matcher(f);
    if(!m.matches()) return;
    showPosSign=m.group(1)!=null && m.group(1).indexOf('+')!=-1;
    integers=m.group(2).length();
    for(int i=0;i<m.group(2).length();i++) {
      if("0123456789#".indexOf(m.group(2).charAt(i))==-1) {
        separator=m.group(2).charAt(i);
        integers--;
      }
    }
    fractions=m.group(3)==null?-1:m.group(3).length()-1;
    if(m.group(4)!=null) append=m.group(4);
  }
  /** Converts a double to a String */
  public String format(double d) {
    String sign=d>=0.0?showPosSign?"+":"":"-";
    if(Double.isNaN(d)) return("NaN");
    if(Double.isInfinite(d)) return(sign+"INF");
    String result="";
    d=Math.rint(Math.pow(10,fractions>0?fractions:0)*Math.abs(d));
    for(int digits=0;digits<integers+(fractions<0?0:fractions) || d>0.0;digits++,d=Math.floor(d/10)) {
         if(separator!=(char)0 && digits>fractions && (digits-fractions)%3==0) {
           result=separator+result;
         }
         result=((int)(d%10))+result;
    }
    if(fractions>=0)
         result=result.substring(0,result.length()-fractions)+'.'+result.substring(result.length()-fractions);
    return(sign+result+append);
  }

  /** Predefined NumberFormatter for ######*/
  public static NumberFormatter sixDigits=new NumberFormatter("######");
  /** Predefined NumberFormatter for #####*/
  public static NumberFormatter fiveDigits=new NumberFormatter("#####");
  /** Predefined NumberFormatter for ####*/
  public static NumberFormatter fourDigits=new NumberFormatter("####");
  /** Predefined NumberFormatter for ###*/
  public static NumberFormatter threeDigits=new NumberFormatter("###");
  /** Predefined NumberFormatter for ##*/
  public static NumberFormatter twoDigits=new NumberFormatter("##");
  /** Predefined NumberFormatter for #*/
  public static NumberFormatter oneDigit=new NumberFormatter("#");
  /** Predefined NumberFormatter for #.####*/
  public static NumberFormatter fourFractions=new NumberFormatter("#.####");
  /** Predefined NumberFormatter for #.##*/
  public static NumberFormatter twoFractions=new NumberFormatter("#.##");

  /** Returns an ISO8601 string representation of the current time */
  public static String ISOtime() {
    return(ISOtime(new GregorianCalendar()));
  }

  /** Returns an ISO8601 string representation of the time given
      by the calendar*/
  public static String ISOtime(Calendar c) {
    return((c.get(Calendar.ERA)==GregorianCalendar.BC?"-":"")+
           fourDigits.format(c.get(Calendar.YEAR))+'-'+
           twoDigits.format(c.get(Calendar.MONTH)+1)+'-'+
           twoDigits.format(c.get(Calendar.DAY_OF_MONTH))+" T "+
           twoDigits.format(c.get(Calendar.HOUR_OF_DAY))+":"+
           twoDigits.format(c.get(Calendar.MINUTE))+":"+
           twoDigits.format(c.get(Calendar.SECOND))+"."+
           fourDigits.format(c.get(Calendar.MILLISECOND)));
  }

  /** Returns an ISO8601 string representation of the current time,
      using a week-oriented representation*/
  public static String ISOweekTime() {
    return(ISOweekTime(new GregorianCalendar()));
  }

  /** Returns an ISO8601 string representation of the time given
      by the calendar, using a week-oriented representation*/
  public static String ISOweekTime(Calendar c) {
    return((c.get(Calendar.ERA)==GregorianCalendar.BC?"-":"")+
           fourDigits.format(c.get(Calendar.YEAR))+"-w"+
           twoDigits.format(c.get(Calendar.WEEK_OF_YEAR))+'-'+
           oneDigit.format(c.get(Calendar.DAY_OF_WEEK)-1)+" T "+
           twoDigits.format(c.get(Calendar.HOUR_OF_DAY))+":"+
           twoDigits.format(c.get(Calendar.MINUTE))+":"+
           twoDigits.format(c.get(Calendar.SECOND))+"."+
           fourDigits.format(c.get(Calendar.MILLISECOND)));
  }

  /** Returns a timestamp from an ISOweekTime or an ISOtime */
  public static String timeStamp(String isoTime) {
    return(isoTime.substring(0,isoTime.indexOf('.')).replaceAll("\\D", ""));
  }

  /** Returns a timestamp from a Calendar*/
  public static String timeStamp(Calendar c) {
    return(timeStamp(ISOtime(c)));
  }

  /** Returns a current timestamp*/
  public static String timeStamp() {
    return(timeStamp(ISOtime()));
  }

  /** Converts milliseconds to a nice String */
  public static String formatMS(long ms) {
    if(ms<100) return(oneDigit.format(ms)+" ms");
    if(ms<1000*60) return(twoFractions.format(ms/1000.0)+" s");
    if(ms<1000*60*60) return(oneDigit.format(ms/1000/60)+" min, "+oneDigit.format((ms/1000.0)%60.0)+" s");
    return(oneDigit.format(ms/1000/60/60)+" h, "+oneDigit.format((ms/1000.0/60.0)%60.0)+" min");
  }

  /** Test routine */
  public static void main(String[] argv) {
    D.p(ISOtime());
    D.p(ISOweekTime());
    D.p(formatMS(2));
    D.p(formatMS(200));
    D.p(formatMS(1000*3+200));
    D.p(formatMS(1000*60*4+1000*3+200));
    D.p(formatMS(1000*60*60*5+1000*60*4+1000*3+200));
    D.p("Enter a number to be formatted as #'#.###. Press CTRL+C to abort");
    while(true) {
     D.p(new NumberFormatter("#'#.###").format(Double.parseDouble(D.r())));
    }
  }

}
