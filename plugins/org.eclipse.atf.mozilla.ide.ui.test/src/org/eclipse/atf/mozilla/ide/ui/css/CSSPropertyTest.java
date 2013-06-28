/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.css;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

public class CSSPropertyTest extends TestCase {

	public void testParseRGB() {
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("#000"));
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("  #000"));
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("  #000  "));
		assertEquals(new RGB(0x55, 0xFF, 0xAA), CSSProperty.parseRGB("#5FA"));
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("#000000"));
		assertEquals(new RGB(0x57, 255, 0xA3), CSSProperty.parseRGB("#57FFA3"));
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("rgb(0,0,0)"));
		assertEquals(new RGB(0, 0, 0), CSSProperty.parseRGB("rgb ( 0, 0, 0 )"));
		assertEquals(new RGB(125, 234, 212), CSSProperty.parseRGB(" rgb ( 125 , 234 , 212 ) "));
		assertEquals(new RGB(0, 255, 25), CSSProperty.parseRGB(" rgb ( %0 , %100 , %10 ) "));

		assertNull(CSSProperty.parseRGB("transparent"));

		assertNull(CSSProperty.parseRGB("transparent"));
		assertNull(CSSProperty.parseRGB(" "));
		assertNull(CSSProperty.parseRGB(" #"));
		assertNull(CSSProperty.parseRGB(" ##"));
	}

	public void testParseRGBConstants() {

		assertEquals(new RGB(0x80, 0x00, 0x00), CSSProperty.parseRGB("maroon"));
		assertEquals(new RGB(0xFF, 0x00, 0x00), CSSProperty.parseRGB("red"));
		assertEquals(new RGB(0xFF, 0xA5, 0x00), CSSProperty.parseRGB("orange"));
		assertEquals(new RGB(0xFF, 0xFF, 0x00), CSSProperty.parseRGB("yellow"));
		assertEquals(new RGB(0x80, 0x80, 0x00), CSSProperty.parseRGB("olive"));
		assertEquals(new RGB(0x80, 0x00, 0x80), CSSProperty.parseRGB("purple"));
		assertEquals(new RGB(0xFF, 0x00, 0xFF), CSSProperty.parseRGB("fuchsia"));
		assertEquals(new RGB(0xFF, 0xFF, 0xFF), CSSProperty.parseRGB("white"));
		assertEquals(new RGB(0x00, 0xFF, 0x00), CSSProperty.parseRGB("lime"));
		assertEquals(new RGB(0x00, 0xFF, 0x00), CSSProperty.parseRGB("green"));
		assertEquals(new RGB(0x00, 0x00, 0x80), CSSProperty.parseRGB("navy"));
		assertEquals(new RGB(0x00, 0x00, 0xFF), CSSProperty.parseRGB("blue"));
		assertEquals(new RGB(0x00, 0xFF, 0xFF), CSSProperty.parseRGB("aqua"));
		assertEquals(new RGB(0x00, 0x80, 0x80), CSSProperty.parseRGB("teal"));
		assertEquals(new RGB(0x00, 0x00, 0x00), CSSProperty.parseRGB("black"));
		assertEquals(new RGB(0xc0, 0xc0, 0xc0), CSSProperty.parseRGB("silver"));
		assertEquals(new RGB(0x80, 0x80, 0x80), CSSProperty.parseRGB("gray"));
	}
}
