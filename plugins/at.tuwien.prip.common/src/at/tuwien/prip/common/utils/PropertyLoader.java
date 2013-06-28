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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyLoader
{
   public static Properties loadProperties(InputStream is) throws IOException
   {
      return loadProperties(is, "unicode");
   }

   public static Properties loadProperties(InputStream is, String encoding) throws IOException
   {
      StringBuilder sb = new StringBuilder();
      InputStreamReader isr = new InputStreamReader(is, encoding);
      while(true)
      {
         int temp = isr.read();
         if(temp < 0)
            break;

         char c = (char) temp;
         sb.append(c);
      }

      String inputString = escapifyStr(sb.toString());
      byte[] bs = inputString.getBytes("ISO-8859-1");
      ByteArrayInputStream bais = new ByteArrayInputStream(bs);

      Properties ps = new Properties();
      ps.load(bais);
      return ps;
   }

   private static char hexDigit(char ch, int offset)
   {
      int val = (ch >> offset) & 0xF;
      if(val <= 9)
         return (char) ('0' + val);

      return (char) ('A' + val - 10);
   }


   private static String escapifyStr(String str)
   {
      StringBuilder result = new StringBuilder();

      int len = str.length();
      for(int x = 0; x < len; x++)
      {
         char ch = str.charAt(x);
         if(ch <= 0x007e)
         {
            result.append(ch);
            continue;
         }

         result.append('\\');
         result.append('u');
         result.append(hexDigit(ch, 12));
         result.append(hexDigit(ch, 8));
         result.append(hexDigit(ch, 4));
         result.append(hexDigit(ch, 0));
      }
      return result.toString();
   }
}
