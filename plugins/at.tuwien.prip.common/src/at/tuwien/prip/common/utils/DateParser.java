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

The DateParser normalizes date expressions in english natural language text
to ISO-dates <BR>
<CENTER><i>year-month-day</i></CENTER><BR>
where <i>year</i> is either positive negative.
The DateParser understands expressions like "4th century BC" or "3rd of November 2004".
Dates may be underspecified: The character '#' stands for "at least one digit".<BR>
Example:
   <PRE>
         DateParser.normalize("It was November 23rd to 24th 1998.")
         --> "It was 1998-11-23 to 1998-11-24."
         DateParser.getDate("It was 1998-11-23 to 1998-11-24.")
         -->  1998, 11, 23
         NumberFormatter.ISOtime(DateParser.getCalendar("November 24th 1998"))
         --> 1998-12-24 T 00:00:00.00
   </PRE>
*/

public class DateParser {

  /** Creates a date-string of the form "year-month-day"
    * from a day, month and year */
  public static final String newDate(String d,String m,String y) {
    return(y+"-"+m+"-"+d);
  }

  /** Creates a date-string from a day, month and year as ints */
  public static final String newDate(int d,int m,int y) {
    return(newDate(""+d,""+m,""+y));
  }

  /** A Date as a capturing RegEx */
  public static final String DATE=newDate("([0-9#]+)","([0-9#]+)","(-?[0-9#]+)");
  public static final Pattern DATEPATTERN=Pattern.compile(DATE);

  /** A year as a pattern */
  public static final Pattern SIMPLEYEARPATTERN=Pattern.compile("\\W(\\d{3,4})\\W");

  /** Just a pair of a Pattern and a replacement string */
  private static class FindReplace {
    public String pattern;
    public String replacement;
    public FindReplace(String f,String r) {
      pattern=f;
      replacement=r;
    }
  }
  /** Contains the month short names */
  private static final String MONTHS="JanFebMarAprMayJunJulAugSepOctNovDec";

  /** A blank as a RegEx */
  private static final String B="[\\W_&&[^-]]*";
  /** A forced blank as a RegEx */
  private static final String FB="[\\W_&&[^-]]+";
  /** A hyphen as a RegEx with blanks*/
  private static final String H=B+"(?:-+|to|until)"+B;
  /** BC as a RegEx with blank*/
  private static final String BC=B+"(?:BC|B\\.C\\.|BCE|AC|A\\.C\\.)";
  /** AD as a RegEx with blank*/
  private static final String AD="AD|A\\.D\\."+B;
  /** CE as a RegEx with blank*/
  private static final String CE=B+"CE|C\\.E\\.";
  /** ##th as a capturing RegEx with blank*/
  private static final String NTH="(\\d{1,2})[a-z]{2}"+B;
  /** ##[th] as a capturing RegEx*/
  private static final String N="(\\d{1,2})[a-z]{0,2}";
  /** #### as a capturing RegEx */
  private static final String Y4="(\\d{4})";
  /** ###...# as a capturing RegEx */
  private static final String Y="(-?\\d{1,10})";
  /** MONTH## as a capturing RegEx */
  private static final String M="MONTH(\\d\\d)";
  /** A "the" as a RegEx with blank*/
  private static final String THE="(?:the)?"+B;
  /** century as a RegEx */
  private static final String CENTURY="[cC]entur(?:y|(?:ies))";
  /** millenium as a RegEx */
  private static final String MILENNIUM="[mM]ill?enn?ium";

  /** Holds the date patterns */
  // The internal order of the patterns is essential!
  //    We always process periods of time first in order to capture the part of it
  //    that lacks explicit date identification
  //    Furthermore, we first process "BC" because we might loose it else
  private static final FindReplace[] patterns=new FindReplace[]{

    //  --------- Process ISO8601 ------------------
    new FindReplace(Y+"[-\\|](\\d{1,2})[-\\|](\\d{1,2})",newDate("$3","$2","$1")),

    //  --------- Process BC, CE and AD ------------
    // 2267 - 2213 BC
    new FindReplace(Y+H+Y+BC,newDate("#","#","-$1")+" to "+newDate("#","#","-$2")),
    // 2267 - 2213 CE
    new FindReplace(Y+H+Y+CE,newDate("#","#","$1")+" to "+newDate("#","#","$2")),
    // 1000 BC - AD 120
    new FindReplace(Y+BC+H+AD+Y,newDate("#","#","-$1")+" to "+newDate("#","#","$2")),
    // 1000 BC - 120 CE
    new FindReplace(Y+BC+H+Y+CE,newDate("#","#","-$1")+" to "+newDate("#","#","$2")),
    // AD 46 - 120
    new FindReplace(AD+Y+H+Y,newDate("#","#","$1")+" to "+newDate("#","#","$2")),
    // 1000 BC
    new FindReplace(Y+BC,newDate("#","#","-$1")),
    // 1000 CE
    new FindReplace(Y+CE,newDate("#","#","$1")),
    // AD 1000
    new FindReplace(AD+Y,newDate("#","#","$1")),

    //  --------- Process complete dates ----------
    // 23rd - 24th of November 1998
    new FindReplace(FB+N+H+N+B+"(?:of)?"+B+M+B+Y," "+newDate("$1","$3","$4")+" to "+newDate("$2","$3","$4")),
    // November 23rd - 24th 1998
    new FindReplace(M+B+N+H+N+FB+Y,newDate("$2","$1","$4")+" to "+newDate("$3","$1","$4")),
    // November 23rd - March 24th 1998
    new FindReplace(M+B+N+H+M+B+N+FB+Y,newDate("$2","$1","$5")+" to "+newDate("$4","$3","$5")),
    // November 23[rd] 1998
    new FindReplace(M+B+N+FB+Y,newDate("$2","$1","$3")),
    // November 23, 1998
    new FindReplace(M+B+N+B+","+B+Y,newDate("$2","$1","$3")),
    // 23[rd] November 1998
    new FindReplace(N+B+M+B+Y,newDate("$1","$2","$3")),
    // 23[rd] of November 1998
    new FindReplace(N+B+"of"+B+M+B+Y,newDate("$1","$2","$3")),
    // 12-Sep-1970
    new FindReplace(N+H+M+H+Y,newDate("$1","$2","$3")),

    //  --------- Process dates with months ----------
    // June - April 1980
    new FindReplace(M+H+M+B+Y4,newDate("#","$1","$3")+" to "+newDate("#","$2","$3")),
    // April 1980
    new FindReplace(M+B+Y4,newDate("#","$1","$2")),

    //  --------- Process days of the year ----------
    // June 14th - July 17th
    new FindReplace(M+B+N+H+M+B+N,newDate("$2","$1","#")+" to "+newDate("$4","$3","#")),
    // June 14th - 17th
    new FindReplace(M+B+N+H+N,newDate("$2","$1","#")+" to "+newDate("$3","$1","#")),
    // June 14th
    new FindReplace(M+B+N,newDate("$2","$1","#")),

    // ----------- Process centuries (add subtraction marker) -----------
    // ##th - ##th millennium BC
    new FindReplace(THE+NTH+H+NTH+B+MILENNIUM+BC,' '+newDate("#","#","-&DEC$1###")+" to "+newDate("#","#","-&DEC$2###")),
    // ##th - ##th century BC
    new FindReplace(THE+NTH+H+NTH+B+CENTURY+BC,' '+newDate("#","#","-&DEC$1##")+" to "+newDate("#","#","-&DEC$2##")),
    // ##th - ##th century
    new FindReplace(THE+NTH+H+NTH+B+CENTURY,' '+newDate("#","#","&DEC$1##")+" to "+newDate("#","#","&DEC$2##")),
    // ##th millennium BC
    new FindReplace(THE+NTH+B+MILENNIUM+BC,' '+newDate("#","#","-&DEC$1###")),
    // ##th century BC
    new FindReplace(THE+NTH+B+CENTURY+BC,' '+newDate("#","#","-&DEC$1##")),
    // ##th millennium
    new FindReplace(THE+NTH+B+MILENNIUM,' '+newDate("#","#","&DEC$1###")),
    // ##th century
    new FindReplace(THE+NTH+B+CENTURY,' '+newDate("#","#","&DEC$1##")),

    // ------------ Process special constructions -----------
    // 1850s
    new FindReplace("(\\d{3})0'?s",newDate("#","#","$1#")),
  };

  /** Holds the pattern seeking for month names */
  private static final Pattern monthPattern=Pattern.compile(
    "(Jan|January|Feb|February|Febr|Mar|March|Apr|April|May|Jun|June|Jul|July|Aug|August|Sep|September|Sept"+
    "|Oct|October|Nov|November|Dec|December)[^a-z]");

  /** Holds the pattern that determines whether a string contains a year expression */
  private static final Pattern yearPattern=Pattern.compile(
        "\\d{2,4}|\\d{4}s|B\\.?C\\.?|A\\.?C\\.?|A\\.?D\\.?|"+CENTURY+"|\\d{2,4}s");

  /** Normalizes all dates in a String */
  public static String normalize(String s) {

    // If it does not contain years or months, return it unchanged
    if(!monthPattern.matcher(s).find() && !yearPattern.matcher(s).find()) return(s);

    StringBuffer resultB=new StringBuffer();

    // Replace all the months
    Matcher m=monthPattern.matcher(s);
    while(m.find()) {
        int monthNum=MONTHS.indexOf(m.group().substring(0,3))/3+1;
        m.appendReplacement(resultB, "MONTH"+(monthNum/10)+(monthNum%10));
    }
    m.appendTail(resultB);
    String result=resultB.toString();

    // Apply the patterns
    for(FindReplace p : patterns) {
      result=result.replaceAll(p.pattern, p.replacement);
    }

    // If there are no subtractions, return
    if(!result.contains("&DEC")) return(result);

    // Apply the subtractions
    m=Pattern.compile("&DEC(\\d+)").matcher(result);
    resultB=new StringBuffer();
    while(m.find()) {
      m.appendReplacement(resultB, ""+(Integer.parseInt(m.group(1))-1));
    }
    m.appendTail(resultB);
    return(resultB.toString());
  }

  /** Returns the year of a normalized Date String (or null) */
  public static String getYear(String d) {
    if(d==null) return(null);
    Matcher m=DATEPATTERN.matcher(d);
    if(m.find()) return(m.group(1));
    m=SIMPLEYEARPATTERN.matcher(' '+d+' ');
    if(m.find()) return(m.group(1));
    return(null);
  }

  /** Returns the month of a normalized Date String (or null) */
  public static String getMonth(String d) {
    if(d==null) return(null);
    Matcher m=DATEPATTERN.matcher(d);
    if(m.find()) return(m.group(2));
    return(null);
  }

  /** Returns the day of a normalized Date String (or null) */
  public static String getDay(String d) {
    if(d==null) return(null);
    Matcher m=DATEPATTERN.matcher(d);
    if(m.find()) return(m.group(3));
    return(null);
  }

  /** Returns the components of the date (year, month, day) in a normalized date string (or null)
   * and writes the start and end position in pos[0] and pos[1]*/
  public static String[] getDate(String d, int[] pos) {
    if(d==null) return(null);
    Matcher m=DATEPATTERN.matcher(d);
    if(!m.find()) {
      m=SIMPLEYEARPATTERN.matcher(' '+d+' ');
      if(!m.find()) return(null);
      pos[0]=m.start();
      pos[1]=m.end()-2;
      return(new String[]{m.group(1),"##","##"});
    }
    pos[0]=m.start();
    pos[1]=m.end();
    String[] result=new String[]{m.group(1), m.group(2), m.group(3)};
    for(int i=1;i<3;i++) {
      if(result[i].length()==1) {
        if(result[i].charAt(0)=='#') result[i]="##";
        else result[i]='0'+result[i];
      }
    }
    return(result);
  }

  /** Tells whether this string is a normlized date (and nothing else)*/
  public static boolean isDate(String s) {
    return(DATEPATTERN.matcher(s).matches());
  }

  /** Returns the components of the date (year, month, day) in a normalized date string (or null)*/
  public static String[] getDate(String d) {
    return(getDate(d,new int[2]));
  }

  /** Converts a normalized Date-String to a Calendar (or null if it's not a normalized date)*/
  public static Calendar asCalendar(String s) {
    s=s.replace('#', '0');
    Matcher m=DATEPATTERN.matcher(s);
    if(!m.find()) {
      String year=getYear(s);
      if(year==null) return(null);
      Calendar c=Calendar.getInstance();
      c.clear();
      c.set(Calendar.YEAR, Integer.parseInt(year));
      return(c);
    }
    Calendar c=Calendar.getInstance();
    c.clear();
    int year=Integer.parseInt(m.group(1));
    if(year<0) {
      year=-year;
      c.set(Calendar.ERA, GregorianCalendar.BC);
    }
    c.set(Calendar.YEAR, year);
    if(Integer.parseInt(m.group(2))!=0) c.set(Calendar.MONTH, Integer.parseInt(m.group(2))-1);
    if(Integer.parseInt(m.group(3))!=0) c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)));
    return(c);
  }

  /** Test routine */
  public static void main(String[] argv) throws Exception {
    System.out.println(NumberFormatter.ISOtime(DateParser.asCalendar(DateParser.normalize("November 24th 1998"))));
    System.out.println("Enter a string containing a date expression and hit ENTER. Press CTRL+C to abort");
    while(true) {
      String in=D.r();
      System.out.println(normalize(in));
      System.out.println(NumberFormatter.ISOtime(asCalendar(normalize(in))));
    }
  }

}
