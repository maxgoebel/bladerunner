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
package at.tuwien.dbai.bladeRunner.utils;

import java.awt.Color;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 * Helper class for color related issues.
 * 
 * @author ceresna
 */
public class ColorUtils {

	/**
	 * 
	 * rMake color slightly darker.
	 * 
	 * 
	 */
	public static RGB makeDarker(RGB c) {
		int DARK_JUMP = 0x60;
		int[] hsv = rgb2hsv(c.red, c.green, c.blue);
		int v2 = hsv[2] > DARK_JUMP ? hsv[2] - DARK_JUMP : 0;
		int[] rgb = hsv2rgb(hsv[0], hsv[1], v2);
		return new RGB(rgb[0], rgb[1], rgb[2]);
	}

	/**
	 * 
	 * rMake color slightly darker.
	 * 
	 * 
	 */
	public static String makeDarker(String s) {
		RGB c = hex2rgb(s);
		int DARK_JUMP = 0x60;
		int[] hsv = rgb2hsv(c.red, c.green, c.blue);
		int v2 = hsv[2] > DARK_JUMP ? hsv[2] - DARK_JUMP : 0;
		int[] rgb = hsv2rgb(hsv[0], hsv[1], v2);
		return String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
	}

	/**
	 * 
	 * Converts HSV color value ro RGB color value. h, hue (-1,0..360). -1 means
	 * achromatic. s, saturation (0..255). v, value (0..255).
	 * 
	 * 
	 */
	public static int[] hsv2rgb(int h, final int s, final int v) {
		if (h < -1 || s > 255 || v > 255) {
			throw new RuntimeException("HSV parameters out of range");
		}
		int r = v, g = v, b = v;
		if (s == 0 || h == -1) {// achromatic case
			;
		} else {// chromatic case
			if (h >= 360)
				h %= 360;
			int f = h % 60;
			h /= 60;
			int p = (2 * v * (255 - s) + 255) / 510;
			if (h % 2 == 1) {
				int q = (2 * v * (15300 - s * f) + 15300) / 30600;
				switch (h) {
				case 1:
					r = q;
					g = v;
					b = p;
					break;
				case 3:
					r = p;
					g = q;
					b = v;
					break;
				case 5:
					r = v;
					g = p;
					b = q;
					break;
				}
			} else {
				int t = (2 * v * (15300 - (s * (60 - f))) + 15300) / 30600;
				switch (h) {
				case 0:
					r = v;
					g = t;
					b = p;
					break;
				case 2:
					r = p;
					g = v;
					b = t;
					break;
				case 4:
					r = t;
					g = p;
					b = v;
					break;
				}
			}
		}
		return new int[] { r, g, b };
	}

	/**
	 * 
	 * Converts RGB color value as HSV color value. h, hue. s, saturation. v,
	 * value.
	 * 
	 * The hue defines the color. Its range is 0..359 if the color is chromatic
	 * and -1 if the color is achromatic. The saturation and value both vary
	 * between 0 and 255 inclusive.
	 * 
	 */
	public static int[] rgb2hsv(int r, int g, int b) {
		int max = r; // maximum RGB component
		int whatmax = 0; // r=>0, g=>1, b=>2
		if (g > max) {
			max = g;
			whatmax = 1;
		}
		if (b > max) {
			max = b;
			whatmax = 2;
		}

		int min = r; // find minimum value
		if (g < min)
			min = g;
		if (b < min)
			min = b;
		int delta = max - min;
		int v = max; // calc value
		int s = max != 0 ? (510 * delta + max) / (2 * max) : 0;
		int h = -1;
		if (s == 0) {
			h = -1; // undefined hue
		} else {
			switch (whatmax) {
			case 0: // red is max component
				if (g >= b)
					h = (120 * (g - b) + delta) / (2 * delta);
				else
					h = (120 * (g - b + delta) + delta) / (2 * delta) + 300;
				break;
			case 1: // green is max component
				if (b > r)
					h = 120 + (120 * (b - r) + delta) / (2 * delta);
				else
					h = 60 + (120 * (b - r + delta) + delta) / (2 * delta);
				break;
			case 2: // blue is max component
				if (r > g)
					h = 240 + (120 * (r - g) + delta) / (2 * delta);
				else
					h = 180 + (120 * (r - g + delta) + delta) / (2 * delta);
				break;
			}
		}
		return new int[] { h, s, v };
	}

	/**
	 * 
	 * Convert a RGB color to a HTML color.
	 * 
	 * @param c
	 *            , a RGB color
	 * @return the HTML color
	 */
	public static String rgb2hex(RGB c) {
		return String.format("#%02x%02x%02x", c.red, c.green, c.blue);
	}

	/**
	 * 
	 * Convert a HTML color to a RGB color.
	 * 
	 * @param c
	 * @return
	 */
	public static RGB hex2rgb(String c) {
		int rgb = Color.HSBtoRGB(0.58333f, 0.66667f, 0.6f);
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		return new RGB(red, green, blue);
	}

	// private static String dump(int[] x) {
	// return String.format("%d %d %d",x[0],x[1]*100/255,x[2]*100/255);
	// }
	// public static void main(String[] args) {
	// ErrorDump.debug(ColorUtils.class, dump(hsv2rgb(159, 67, 76)));
	// ErrorDump.debug(ColorUtils.class, dump(rgb2hsv(56, 76, 69)));
	// }

}
