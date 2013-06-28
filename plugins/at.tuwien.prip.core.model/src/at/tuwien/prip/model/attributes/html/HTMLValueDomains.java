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
package at.tuwien.prip.model.attributes.html;


import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.attributes.domains.lxEmptyValueDef;
import at.tuwien.prip.model.attributes.domains.lxEnumValueDef;
import at.tuwien.prip.model.attributes.domains.lxFewStringsValueDef;
import at.tuwien.prip.model.attributes.domains.lxManyStringsValueDef;
import at.tuwien.prip.model.attributes.domains.lxURIValueDef;


/**
 * HTMLValueDomains.java
 *
 *
 * Created: Sun Jul 27 18:21:19 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public interface HTMLValueDomains {

  //AAA [class]
  public static final HTMLValueDomain D_CDATA =
    new HTMLValueDomain(lxManyStringsValueDef.class,
                        new Comment("character data"));

  public static final HTMLValueDomain D_EMPTY =
    new HTMLValueDomain(lxEmptyValueDef.class,
                        null);

  public static final HTMLValueDomain D_SCRIPT =
    new HTMLValueDomain(lxManyStringsValueDef.class,
                        new Comment("script expression"));

  public static final HTMLValueDomain D_URI =
    new HTMLValueDomain(lxURIValueDef.class,
                        new Comment("a Uniform Resource Identifier"));

  public static final HTMLValueDomain D_COLOR =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a color using sRGB: #RRGGBB as Hex values"));

  public static final HTMLValueDomain D_LENGTH =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("nn for pixels or nn% for percentage length"));

  public static final HTMLValueDomain D_NUMBER =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("tokens must contain at least one digit ([0-9])"));

  public static final HTMLValueDomain D_TEXT =
    new HTMLValueDomain(lxManyStringsValueDef.class,
                        new Comment("text"));

  public static final HTMLValueDomain D_PIXELS =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("integer representing length in pixels"));

  public static final HTMLValueDomain D_LINKTYPES =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("space-separated list of link types"));

  public static final HTMLValueDomain D_CONTENTTYPE =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("media type, as per [RFC2045]"));

  public static final HTMLValueDomain D_MULTILENGTH =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("pixel, percentage, or relative"));

  public static final HTMLValueDomain D_MULTILENGTHS =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("comma-separated list of MultiLength"));

  public static final HTMLValueDomain D_ID =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("identifier"));

  public static final HTMLValueDomain D_NAME =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("name"));

  public static final HTMLValueDomain D_IDREF =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("identifier reference"));

  public static final HTMLValueDomain D_IDREFS =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a space-separated list of identifier references"));

  public static final HTMLValueDomain D_CHARSETS =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a space-separated list of character encodings, as per [RFC2045]"));

  public static final HTMLValueDomain D_CONTENTTYPES =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("comma-separated list of media types, as per [RFC2045]"));

  public static final HTMLValueDomain D_CHARACTER =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a single character from [ISO10646]"));

  public static final HTMLValueDomain D_CHARSET =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a character encoding, as per [RFC2045]"));

  public static final HTMLValueDomain D_COORDS =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("comma-separated list of lengths"));

  public static final HTMLValueDomain D_DATETIME =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("date and time information. ISO date format"));

  public static final HTMLValueDomain D_FRAMETARGET =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("render in this frame"));

  public static final HTMLValueDomain D_LANGUAGECODE =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("a language code, as per [RFC1766]"));

  public static final HTMLValueDomain D_MEDIADESC =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("single or comma-separated list of media descriptors"));

  public static final HTMLValueDomain D_STYLESHEET =
    new HTMLValueDomain(lxFewStringsValueDef.class,
                        new Comment("style sheet data"));

  public static final HTMLValueDomain D_SHAPE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "rect", "circle", "poly", "default"
                        }),
                        null);

  public static final HTMLValueDomain D_SCOPE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "row", "col", "rowgroup", "colgroup"
                        }),
                        null);

  public static final HTMLValueDomain D_ULSTYLE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "disc", "square", "circle"
                        }),
                        null);

  public static final HTMLValueDomain D_OLSTYLE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "1", "a", "A", "i", "I"
                        }),
                        null);

  public static final HTMLValueDomain D_LISTYLE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "disc", "square", "circle",
                          "1", "a", "A", "i", "I"
                        }),
                        null);

  public static final HTMLValueDomain D_INPUTTYPE =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "text", "password", "checkbox", "radio",
                          "submit", "reset", "file", "hidden",
                          "image", "button"
                        }),
                        null);

  public static final HTMLValueDomain D_TFRAME =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "void", "above", "below", "hsides", "lhs",
                          "rhs", "vsides", "box", "border"
                        }),
                        null);

  public static final HTMLValueDomain D_TRULES =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "none", "groups", "rows", "cols", "all"
                        }),
                        null);

  public static final HTMLValueDomain D_CALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "top", "bottom", "left", "right"
                        }),
                        new Comment("relative to table"));

  public static final HTMLValueDomain D_IALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "top", "middle", "bottom", "left", "right",
                        }),
                        new Comment("center?"));

  public static final HTMLValueDomain D_LALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "top", "bottom", "left", "right"
                        }),
                        null);

  public static final HTMLValueDomain D_TALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "left", "center", "right"
                        }),
                        null);

  public static final HTMLValueDomain D_HALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "left", "center", "right"
                        }),
                        null);

  public static final HTMLValueDomain D_PALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "left", "center", "right", "justify"
                        }),
                        null);

  public static final HTMLValueDomain D_AALIGN =
    new HTMLValueDomain(lxEnumValueDef.class,
                        ListUtils.toSet(new String[] {
                          "left", "center", "right", "justify", "char",
                        }),
                        null);


} // HTMLValueDomains
