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
package at.tuwien.prip.model.document.semantics;

/**
 * WordSemantic.java
 *
 * A type of word semantics.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 17, 2011
 */
public enum WordSemantics 
{
	//general word semantics
	ALPHA, ALPHANUM, ALPHACAPS, TEXT, WHITESPACE, PUNCTUATION, DIGIT, BRACKET, BRACKET_OPEN, BRACKET_CLOSE, PARAGRAPH, TEXT_LINE, RECORD,
	
	//data and time related semantics
	DATE, DATE_RANGE, TIME_SPAN, TIME, DAY, YEAR, MONTH,
	FROM, UNTIL, AT_TIME, ON_DAY, CAL_DAY,
	
	//address related semantics
	ADDRESS, PHONE, EMAIL, STREET, PBOX, QUAL_PLACE,
	
	ORGANIZATION,
	
	//symbols
	DASH, COLON, SEMICOLON, AMPERSAND,
	
	//other semantics
	PERSON, LOCATION, PERCENTAGE, PRIZE, URI, ISBN, NAME, TITLE,
	AUTHOR, SCORE, ENUM, PROPER_NAME,
	DOB, NATIONALITY, 
	
	FUNCTION, SKILLS,
	
	CITY, COUNTY, COUNTRY, LANGUAGE,
	
	FIRST_NAME, FIRST_NAME_MALE, FIRST_NAME_FEMALE,
	
	REFERENCE,
	
	DOMAIN_KEY, DOMAIN_VAL,
	
	HEADING
}
