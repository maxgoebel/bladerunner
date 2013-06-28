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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.tuwien.prip.common.datastructures.Pair;
import at.tuwien.prip.common.log.ErrorDump;

public class SmartString
{
    private static final String delimiterRegExp = "\\s|\t|,|;|\n";
    private static final char specialChars[] =
        {'\"', '.', '^', '$', '*', '+', '?', '{', '[', ']', '\\', '|', '(', ')'};
    private static LinkedHashMap<String,String> simplifyingStrings=
        new LinkedHashMap<String,String>();
    private static LinkedHashMap<String,String> deSimplifyingStrings=
        new LinkedHashMap<String,String>();

    static
    {
        Arrays.sort(specialChars);
        simplifyingStrings.put("\\[a-z\\]\\+","v");
        simplifyingStrings.put("\\[a-z\\]","l");
        simplifyingStrings.put("\\[A-Z\\]\\+","V");
        simplifyingStrings.put("\\[A-Z\\]","L");
        simplifyingStrings.put("\\[a-z\\A-Z\\]\\+","m");
        simplifyingStrings.put("d\\+","N");
        simplifyingStrings.put("d","n");
        deSimplifyingStrings.put("v","[a-z]+");
        deSimplifyingStrings.put("l","[a-z]");
        deSimplifyingStrings.put("V","[A-Z]+");
        deSimplifyingStrings.put("L","[A-Z]");
        deSimplifyingStrings.put("m","[a-zA-Z]+");
        deSimplifyingStrings.put("N","\\\\d+");
        deSimplifyingStrings.put("n","\\\\d");
    }

    public static String splitString(final String s)
    {
        try
        {
            //ErrorDump.log(this, s);
            StringBuffer regexp = new StringBuffer();
            String finalResult = s;
            String tokens[] = generateTokens(s);
            java.util.List<String> regexps = new LinkedList<String>();
            for (String token : tokens)
            {
                String processedToken = processToken(token);
                if (processedToken != null)
                {
                    if (mixedToken(token,processedToken))
                        processedToken = ".*";
                    finalResult = finalResult.replace(token,processedToken);
                    regexps.add(processedToken);
                    regexp.append(processedToken);
                }
            }
            return deSimplifyToken(finalResult);
        } catch (Exception e)
          {
            ErrorDump.error(SmartString.class, e);
            return s;
          }
    }

    private static boolean mixedToken(String original, String general)
    {
        LinkedList<Character> differentChars = new LinkedList<Character>();
        int specCharCount = 0;
        if (original.length() <= 2)
            return false;
        for (Character c : general.toCharArray())
        {
            if (isAlpha(c))
            {
                if (!differentChars.contains(c))
                {
                    differentChars.add(c);
                }
            }
            else
            {
                specCharCount++;
            }
        }
        if (Float.compare((float) general.length() /
                          (float) original.length(),0.6f) < 0)
            return false;
        if (differentChars.size() - specCharCount <= 0 )
            return false;

        return true;
    }

    private static boolean isAlpha(Character c)
    {
        return ((c >= 97 && c <= 122) ||
               (c >= 65 && c <= 90));
    }

    private static String processToken(String token)
    {
        token = token.trim();
        if (token.equals(""))
            return null;
        token = escapeString(token);
        int originalLength = token.length();

        String ucl = getUppercaseLetters(token);
        String lcl = getLowerCaseLetters(token);
        String letters = getLetters(token);
        String numbers = getNumbers(token);
        String nonAlphaChars = getNonAlphaChars(token);

        //Homogenous types
        if (ucl.length() == originalLength)
            return("V");
        else if (lcl.length() == originalLength)
            return("v");
        else if (letters.length() == originalLength)
            return("m");
        else if (numbers.length() == originalLength)
            return("\\d+");
        else if (nonAlphaChars.length() == originalLength)
            return processNonAlphaChars(token);
        //mixed types - 2 components
        else if (letters.length()+nonAlphaChars.length() == originalLength ||
                 letters.length()+numbers.length() == originalLength ||
                 numbers.length()+nonAlphaChars.length() == originalLength)
            return processMixedType(token);
        else
            return processMixedType(token);
    }

    private static String escapeString(String s)
    {
        StringBuffer result = new StringBuffer();
        for (Character c : s.toCharArray())
        {
            if (Arrays.binarySearch(specialChars,c) >= 0)
                result.append("\\");
            result.append(c);
        }
        return result.toString();
    }

    private static String preProcessMixedType(String s)
    {
        StringBuffer result = new StringBuffer();
        String lastAdded = null;
        boolean plussed = false;
        String lastPlussed = null;

        for (Character c : s.toCharArray())
        {
            String toAdd = c.toString();
            if (c >= 48 && c <= 57)
                toAdd = "n";
            else if (c >= 97 && c <= 122)
                toAdd = "l";
            else if (c >= 65 && c <= 90)
                toAdd = "L";

            if (toAdd.equals(lastAdded) || toAdd.equals(lastPlussed))
            {
                if (!plussed)
                {
                    plussed = true;
                    lastPlussed = lastAdded;
                    result.append("+");
                }
            }
            else
            {
                plussed = false;
                lastPlussed = null;
            }

            if (!plussed)
            {
                result.append(toAdd);
                lastAdded = toAdd;
            }
        }
        return result.toString();
    }

    private static String processMixedType(String s)
    {
        s = preProcessMixedType(s);
        return findEqualChunks(s);
    }

    private static String findEqualChunks(String s)
    {
        s = simplifyToken(s);
        Pair<String,Integer> p = finddCharactersWithLowestCount(s);
        return equalChunkSearch(s,p);
    }

    private static String equalChunkSearch(String s, Pair<String,Integer> p)
    {
        if (everythingOnce(s))
        {

            return s;
        }
        else if (p.getSecond()==1)
        {
            String splitString = p.getFirst().toString();
            splitString = escapeString(splitString);
            //ErrorDump.debug(this, "s: " + s);
            //ErrorDump.debug(this, "splitString: " + splitString);
            //if (Arrays.binarySearch(specialChars,p.getFirst()) >= 0)
            //	splitString = "\\" + splitString;
            //ErrorDump.debug(this, "s: " + s);
            String parts[] = s.split(splitString);
            String first = parts[0];
            if (parts.length == 1)
            {
                //ErrorDump.debug(this, "splitString: " + splitString);
                //ErrorDump.debug(this, "first: " + first);
                return equalChunkSearch(
                		first,
                		finddCharactersWithLowestCount(first)) +
                		p.getFirst().toString();
            }


            String second = parts[1];
            //ErrorDump.debug(this, "first: " + first);
            //ErrorDump.debug(this, "second: " + second);
            return equalChunkSearch(
            		first,
            		finddCharactersWithLowestCount(first)) +
            		p.getFirst().toString() +
            		equalChunkSearch(
            				second,
            				finddCharactersWithLowestCount(second));

        }
        else
        {
            //ErrorDump.debug(this, p.getFirst() + "-->" + p.getSecond());
            //ErrorDump.debug(this, "variations");
            HashSet<String> variations = new HashSet<String>();
            //String result = null;
            for (int i = 2; i <= s.length()/2 ; i++)
                for (int j = 0; j < s.length()-i; j++)
                {
                    String proposedVariation = s.substring(j,j+i);
                    //ErrorDump.debug(this, "Proposed variation: " + proposedVariation);
                    //check if the proposed variation 'cuts off' a backslash at the beginning
                    if (j-1 >= 0)
                        if ( s.charAt(j-1) == '\\' )
                        {
                            //ErrorDump.debug(this, "proposedVariation.charAt(0): " + proposedVariation.charAt(0));
                            continue;
                        }
                    //check if the last char of the variation is a backslash, i.e. we would
                    //cut off a backslash from the next escaped char

                    if (proposedVariation.charAt(0) == '+')
                        continue;
                    if (proposedVariation.charAt(proposedVariation.length()-1) == '\\')
                    {
                        //ErrorDump.debug(this, "proposedVariation.charAt(proposedVariation.length()-1: " +
                        //		proposedVariation.charAt(proposedVariation.length()-1));
                        continue;
                    }
                    //ErrorDump.debug(this, "Adding variation: " + s.substring(j,j+i));
                    variations.add(s.substring(j,j+i));
                }

            LinkedHashMap<Integer,String> candidates = new LinkedHashMap<Integer,String>();
            TreeSet<Integer> groupStarts = new TreeSet<Integer>();

            for (String x : variations)
            {
                //ErrorDump.debug(this, "variation: " + x);
                //ErrorDump.debug(this, "Escaping this: " + x + " to " + escapeString(x));
                String patternString = "(" + escapeString(x) + "){2,}";
                //patternString = patternString.replaceAll("\\+", "\\\\+");
                //patternString = escapeString(patternString);
                //ErrorDump.debug(this, "DA Pattrnstring: " + patternString);
                //ErrorDump.debug(this, "( " + s + ", " + patternString  + ")");
                //ErrorDump.debug(this, "patternString: " + patternString);
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(s);
                //ErrorDump.debug(this, "matching this: " + s +  " with: " + patternString);
                //ErrorDump.debug(this, ");
                if (matcher.find())
                {
                        candidates.put(matcher.start(),patternString);
                        //ErrorDump.debug(this, "matcher.start(): " + matcher.start());
                        groupStarts.add(matcher.start());
                }

            }
            //for (Integer i : groupStarts)
            //	ErrorDump.debug(this, i + candidates.get(i));
            return generalizeChunks(s,candidates,groupStarts);
        }
    }

    private static String generalizeChunks(String s,
                                           LinkedHashMap<Integer,String> candidates,
                                           TreeSet<Integer> groupStarts)
    {
        String result = s;
        //ErrorDump.debug(this, "RESULT: " + result);
        @SuppressWarnings("unchecked")
        LinkedHashMap<Integer,String> currentCandidates =
            (LinkedHashMap<Integer,String>)candidates.clone();
        if (currentCandidates.isEmpty())
            return ".*";
        for(;;)
        {
            //ErrorDump.debug(this, "GROUPSTART: " + groupStarts.first());
            String firstRep = currentCandidates.remove(groupStarts.first());
            candidates.remove(groupStarts.first());
            String generalized = firstRep.substring(0,firstRep.length()-4) + "+";
            result = result.replaceAll(firstRep,generalized);
            //ErrorDump.debug(this, result);
            groupStarts.clear();
            currentCandidates.clear();
            boolean foundCandidate = false;
            for (Integer i : candidates.keySet())
            {
                //ErrorDump.debug(this, i);
                String currentRep = candidates.get(i);
                //currentRep = currentRep.substring(1,currentRep.length()-5);
                //ErrorDump.debug(this, currentRep)

                Pattern pattern = Pattern.compile(currentRep);
                Matcher matcher = pattern.matcher(result);
                if (matcher.find())
                {
                        //ErrorDump.debug(this, matcher.start()+":"+currentRep);
                        currentCandidates.put(matcher.start(),currentRep);
                        groupStarts.add(matcher.start());
                        foundCandidate = true;
                }
            }
            if (!foundCandidate)
                break;
        }

        return result;
    }

    private static Pair<String,Integer> finddCharactersWithLowestCount(String s)
    {
        LinkedHashMap<String, Integer> cardinality = new LinkedHashMap<String, Integer>();
        char asChar[] = s.toCharArray();
        for (int i = 0; i < asChar.length; i++)
        {
            if (Arrays.binarySearch(specialChars,asChar[i]) >= 0)
                continue;
            String nextToken = new Character(asChar[i]).toString();
            if (i < asChar.length-1)
            {
                if (asChar[i+1] == '+')
                    nextToken += "+";
            }

            if (cardinality.containsKey(nextToken))
                cardinality.put(nextToken,cardinality.get(nextToken)+1);
            else
                cardinality.put(nextToken,1);
        }

        int min = 100;
        String result = null;
        for (String hash : cardinality.keySet())
        {
            int i = cardinality.get(hash);
            //ErrorDump.debug(this, "hash: " + hash);
            //ErrorDump.debug(this, "i: " + i + ", c:" + c);
            if (i < min)
            {
                min = i;
                result = hash;
            }
        }
        return new Pair<String,Integer>(result,min);
    }

    private static String simplifyToken(String s)
    {
        return substitute(s,simplifyingStrings);
    }

    private static String deSimplifyToken(String s)
    {
        return substitute(s,deSimplifyingStrings);
    }

    private static String substitute(String s, LinkedHashMap<String,String> replaceMap)
    {
        String result = s;
        for (String s0 : replaceMap.keySet())
        {
            result = result.replaceAll(s0,replaceMap.get(s0));
        }
        return result;
    }

    private static boolean everythingOnce(String s)
    {
        LinkedList<Character> previousLetters = new LinkedList<Character>();
        for (Character c : s.toCharArray())
        {
            if (previousLetters.contains(c))
                return false;
            previousLetters.add(c);
        }
        return true;
    }

//    private static String processLettersAndNonAlphaChars(String s)
//    {
//        //TODO: implement me
//        return s + "processLettersAndNonAlphaChars";
//    }
//
//    private static String processLettersAndNumbers(String s)
//    {
//        //TODO: implement me
//        return s + " - processLettersAndNumbers";
//    }
//
//    private static String processNumbersAndNonAlphaChars(String s)
//    {
//        //TODO: implement me
//        return s + " - processNumbersAndNonAlphaChars";
//    }

    private static String processNonAlphaChars(String s)
    {
        return s;
    }

    private static String getLetters(String s)
    {
        return s.replaceAll("[^a-zA-Z]","");
    }

    private static String getLowerCaseLetters(String s)
    {
        return s.replaceAll("[^a-z]","");
    }

    private static String getUppercaseLetters(String s)
    {
        return s.replaceAll("[^A-Z]","");
    }


    private static String getNumbers(String s)
    {
        return s.replaceAll("[^\\d]","");
    }

    private static String getNonAlphaChars(String s)
    {
        return s.replaceAll("\\d|[a-z]|[A-Z]","");
    }

    private static String[] generateTokens(String s)
    {
        final String multipleSpacesRemoved = s.replaceAll("\n"," ").
           replaceAll("\\s\\s+"," ").
           replaceAll("&nbsp;","");
        return multipleSpacesRemoved.split(delimiterRegExp);
    }


    public static void main(String[] args)
    {
        ErrorDump.debug(SmartString.class, splitString("mozilla.12.7"));
        ErrorDump.debug(SmartString.class, splitString("heya[*]"));
        ErrorDump.debug(SmartString.class, splitString("123-453-23"));
        ErrorDump.debug(SmartString.class, splitString("Tel # 123-453-23"));
        ErrorDump.debug(SmartString.class, splitString("Telephone nr. 123-453-23"));
        ErrorDump.debug(SmartString.class, splitString("Szinek, PETER"));
        ErrorDump.debug(SmartString.class, splitString("BAUMGARTNER, Robert"));
        ErrorDump.debug(SmartString.class, splitString("Dr. Georg Gottlob"));
        ErrorDump.debug(SmartString.class, splitString("THE WASHINGTON POST"));
        ErrorDump.debug(SmartString.class, splitString("There were 15 people."));
        ErrorDump.debug(SmartString.class, splitString("123-5678-xxx-yyy-910-223-yyy-910-223"));
        ErrorDump.debug(SmartString.class, splitString("szinek@dbai.tuwien.ac.at"));
        ErrorDump.debug(SmartString.class, splitString("http://www.google.com"));
        ErrorDump.debug(SmartString.class, splitString("www.google.com"));

        Pattern p = Pattern.compile("(l\\+\\\\.){2,}");
        Matcher m = p.matcher("l+\\.l+\\.l+");
        ErrorDump.debug(SmartString.class, String.valueOf(m.find()));

        ErrorDump.debug(SmartString.class, splitString("SmartString.class is a longer text."));
        ErrorDump.debug(SmartString.class, splitString("123456789"));
        ErrorDump.debug(SmartString.class, splitString("mIx12eD"));
        ErrorDump.debug(SmartString.class, splitString("1+1=3"));
    }

}
