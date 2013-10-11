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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import at.tuwien.prip.common.log.ErrorDump;

/**
 *
 * Helper class for string operations.
 *
 */
public class StringUtils {

//	static List<Character> bulletpoints;
//	static {
//		try {
//			bulletpoints = new ArrayList<Character>();
//			String bullets = BasePropertiesLoader.loadProperty("bullets");
//			for (String s : bullets.split("\\s")) {
//				bulletpoints.add(s.toCharArray()[0]);
//			}
//		} catch (PropertyLoadException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static String getTimestamp()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String cleanText (String input) 
	{
		if (input==null || input.trim().length()<=0) {
			return input;
		}
		
		char[] charArray = input.toCharArray();

		String output = "";
		for (int i = 0; i < charArray.length; ++i) {
			char a = charArray[i];
			if ((int) a > 255) 
			{

				int g = (int) a;
				if (g>10000) {
					output += " * ";
				} else {
					output += a;
				}

			} 
			else 
			{
				output += a;
			}
		}
		return output;
	}
	
//	public static String replaceBullets (String input, Character symbol)
//	{
//		for (Character bullet : bulletpoints)
//		{
//			input = input.replace(bullet, symbol);
//		}
//		
//		return input;
//	}
    /**
     * concatenates to single string
     */
    public static String concat(Collection<String> sl, String delim) {
        StringBuffer sb = new StringBuffer();
        if (sl!=null) {
            boolean first = true;
            for (String s : sl) {
                if (first) first = false; else sb.append(delim);
                sb.append(s);
            }
        }
        return sb.toString();
    }
    public static String concat(Collection<String> sl) {
        return concat(sl, "");
    }

    /**
     * concatenates to single string
     */
    public static String concat(String[] sl, String delim) {
        StringBuffer sb = new StringBuffer();
        if (sl!=null) {
            for (int i=0; i<sl.length; i++) {
                if (i>0) sb.append(delim);
                sb.append(sl[i]);
            }
        }
        return sb.toString();
    }

    public static List<String> split(String input, String delim) {
        List<String> ret = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(input, delim, false);
        while (st.hasMoreElements()) {
            String t = st.nextToken();
            ret.add(t);
        }

        return ret;
    }

    public static List<String> splitChars(String input) {
        char[] cs = input.toCharArray();
        List<String> ret = new LinkedList<String>();
        for (char c : cs) {
            ret.add(""+c);
        }
        return ret;
    }

    public static String trimAll(String str) {
    	return str.replaceAll("\\s+", "");

    }

    public static String leftTrim(String str) {
        return str.replaceAll("^\\s*", "");
    }

    public static String rightTrim(String str) {
        return str.replaceAll("\\s*$", "");
    }

    /**
     * 
     * @param str
     * @param size
     * @return
     */
    public static String trimTo(String str, int size) {
    	int presuf = (size-3)/2 +1;
    	StringBuilder builder = new StringBuilder();
    	builder.append(str.substring(0, presuf));
    	builder.append("...");
    	builder.append(str.substring(str.length()-presuf));
        return builder.toString();
    }
    
    public static String join(String s[], String delimiter)
    {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length; i++)
        {
            buffer.append(s[i]);
            if (i < s.length -1)
                buffer.append(delimiter);

        }
        return buffer.toString();
    }

    public static String lastXPathComponent(String XPathExpression)
    {
        try
        {
            String parts[] = XPathExpression.split("/");
            String lastPart = parts[parts.length-1];
            return lastPart.split("\\[")[0];
        } catch (PatternSyntaxException e)
          {
              ErrorDump.error(StringUtils.class, e);
              return null;
          }
    }

    public static int getLastPathComponentIndex(String XpathExp) {
        try
        {
            String parts[] = XpathExp.split("/");
            String lastPart = parts[parts.length-1];
            return Integer.parseInt(lastPart.substring(lastPart.indexOf("[")+1, lastPart.indexOf("]")));

        }
        catch (PatternSyntaxException e)
          {   // should not happen
              ErrorDump.error(StringUtils.class, e);
          }
        catch(NumberFormatException e) {
            // should not happen
            ErrorDump.error(StringUtils.class, e);
        }
        return 1;
    }


    public static String getXPathElements(String XPathExpression)
    {  // for general path type
        try
        {
            Matcher matcher =
                Pattern.compile("([^/])/([^/])").matcher(XPathExpression);
            String relpaceStr = ("$1//$2");
            String replaced = matcher.replaceAll(relpaceStr);
            matcher.reset(replaced);
            return matcher.replaceAll(relpaceStr).
                   replaceAll("\\[.*?\\]","");
        } catch (PatternSyntaxException e)
          {
              ErrorDump.error(StringUtils.class, e);
              return null;
          }
    }

    /**
     * 
     * @author max
     *
     */
	public static class Levenshtein {

		//****************************
		// Get minimum of three values
		//****************************

		private static int Minimum (int a, int b, int c) {
			int mi;

			mi = a;
			if (b < mi) {
				mi = b;
			}
			if (c < mi) {
				mi = c;
			}
			return mi;

		}

		//*****************************
		// Compute Levenshtein distance
		//*****************************

		public static int LD (String s, String t) {
			int d[][]; // matrix
			int n; // length of s
			int m; // length of t
			int i; // iterates through s
			int j; // iterates through t
			char s_i; // ith character of s
			char t_j; // jth character of t
			int cost; // cost

			// Step 1

			n = s.length ();
			m = t.length ();
			if (n == 0) {
				return m;
			}
			if (m == 0) {
				return n;
			}
			d = new int[n+1][m+1];

			// Step 2

			for (i = 0; i <= n; i++) {
				d[i][0] = i;
			}

			for (j = 0; j <= m; j++) {
				d[0][j] = j;
			}

			// Step 3

			for (i = 1; i <= n; i++) {

				s_i = s.charAt (i - 1);

				// Step 4

				for (j = 1; j <= m; j++) {

					t_j = t.charAt (j - 1);

					// Step 5

					if (s_i == t_j) {
						cost = 0;
					}
					else {
						cost = 1;
					}

					// Step 6

					d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

				}

			}

			// Step 7

			return d[n][m];

		}
	}

	/**
	 * 
	 * @param hexCode
	 */
	public static String hex2Utf8String (String hexCode) {
		String[] hexCodes = hexCode.split("%");
		StringBuilder sb = new StringBuilder("");
		for (int i=1; i<hexCodes.length; i++) {
			sb.append( (char) Integer.parseInt(hexCodes[i], 16) );
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String test = "%D9%85%D9%8A%D9%88%D9%86%D8%AE";
		try {
			test = URLDecoder.decode(test, "UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		test = hex2Utf8String(test);
		System.err.println(test);
	}

}
