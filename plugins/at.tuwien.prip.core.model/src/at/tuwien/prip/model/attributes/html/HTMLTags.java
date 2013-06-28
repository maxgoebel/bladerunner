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

import java.util.HashMap;

import javax.swing.text.html.HTML;

import at.tuwien.prip.common.log.ErrorDump;

/**
 * HTMLTags.java
 *
 *
 * Created: Sun Jul 27 17:49:40 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public abstract class HTMLTags {

  public static HTMLTagDef getDefinitionByName(String tag_name)
  {
	  HTMLTagDef def = name2defs.get(tag_name);
	  if (def!=null) {
		  return def;
	  }

	  //unknown tag
	  ErrorDump.error(HTMLTags.class,
			  "log_unknown_tag"+
			  new Object[] {
		  tag_name
	  });
	  return
	  new HTMLTagDef(tag_name,
			  HTMLTagDef.RENDERING_EFFECT_NONE);
  }

  public static final HTMLTagDef T_A =
    new HTMLTagDef(HTML.Tag.A,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_ADDRESS =
    new HTMLTagDef(HTML.Tag.ADDRESS,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_APPLET =
    new HTMLTagDef(HTML.Tag.APPLET,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_AREA =
    new HTMLTagDef(HTML.Tag.AREA,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_B =
    new HTMLTagDef(HTML.Tag.B,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_BASE =
    new HTMLTagDef(HTML.Tag.BASE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_BASEFONT =
    new HTMLTagDef(HTML.Tag.BASEFONT,
                   HTMLTagDef.RENDERING_EFFECT_NONE); //check me


  public static final HTMLTagDef T_BIG =
    new HTMLTagDef(HTML.Tag.BIG,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_BLOCKQUOTE =
    new HTMLTagDef(HTML.Tag.BLOCKQUOTE,
                   HTMLTagDef.RENDERING_EFFECT_NONE); //check me


  public static final HTMLTagDef T_BODY =
    new HTMLTagDef(HTML.Tag.BODY,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_BR =
    new HTMLTagDef(HTML.Tag.BR,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_CAPTION =
    new HTMLTagDef(HTML.Tag.CAPTION,
                   HTMLTagDef.RENDERING_EFFECT_NONE); //check me


  public static final HTMLTagDef T_CENTER =
    new HTMLTagDef(HTML.Tag.CENTER,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_CITE =
    new HTMLTagDef(HTML.Tag.CITE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_CODE =
    new HTMLTagDef(HTML.Tag.CODE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DD =
    new HTMLTagDef(HTML.Tag.DD,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DFN =
    new HTMLTagDef(HTML.Tag.DFN,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DIR =
    new HTMLTagDef(HTML.Tag.DIR,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DIV =
    new HTMLTagDef(HTML.Tag.DIV,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DL =
    new HTMLTagDef(HTML.Tag.DL,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DT =
    new HTMLTagDef(HTML.Tag.DT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);

  public static final HTMLTagDef T_EM =
    new HTMLTagDef(HTML.Tag.EM,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_FONT =
    new HTMLTagDef(HTML.Tag.FONT,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_FORM =
    new HTMLTagDef(HTML.Tag.FORM,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_FRAME =
    new HTMLTagDef(HTML.Tag.FRAME,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_FRAMESET =
    new HTMLTagDef(HTML.Tag.FRAMESET,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_H1 =
    new HTMLTagDef(HTML.Tag.H1,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_H2 =
    new HTMLTagDef(HTML.Tag.H2,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_H3 =
    new HTMLTagDef(HTML.Tag.H3,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_H4 =
    new HTMLTagDef(HTML.Tag.H4,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_H5 =
    new HTMLTagDef(HTML.Tag.H5,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_H6 =
    new HTMLTagDef(HTML.Tag.H6,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_HEAD =
    new HTMLTagDef(HTML.Tag.HEAD,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_HR =
    new HTMLTagDef(HTML.Tag.HR,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_HTML =
    new HTMLTagDef(HTML.Tag.HTML,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_I =
    new HTMLTagDef(HTML.Tag.I,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_IMG =
    new HTMLTagDef(HTML.Tag.IMG,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_INPUT =
    new HTMLTagDef(HTML.Tag.INPUT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_ISINDEX =
    new HTMLTagDef(HTML.Tag.ISINDEX,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_KBD =
    new HTMLTagDef(HTML.Tag.KBD,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_LI =
    new HTMLTagDef(HTML.Tag.LI,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_LINK =
    new HTMLTagDef(HTML.Tag.LINK,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_MAP =
    new HTMLTagDef(HTML.Tag.MAP,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_MENU =
    new HTMLTagDef(HTML.Tag.MENU,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_META =
    new HTMLTagDef(HTML.Tag.META,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_NOFRAMES =
    new HTMLTagDef(HTML.Tag.NOFRAMES,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_OBJECT =
    new HTMLTagDef(HTML.Tag.OBJECT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_OL =
    new HTMLTagDef(HTML.Tag.OL,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_OPTION =
    new HTMLTagDef(HTML.Tag.OPTION,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_P =
    new HTMLTagDef(HTML.Tag.P,
                   HTMLTagDef.RENDERING_EFFECT_NONE);

  public static final HTMLTagDef T_PARAM =
    new HTMLTagDef(HTML.Tag.PARAM,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_PRE =
    new HTMLTagDef(HTML.Tag.PRE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_SAMP =
    new HTMLTagDef(HTML.Tag.SAMP,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_SCRIPT =
    new HTMLTagDef(HTML.Tag.SCRIPT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_SELECT =
    new HTMLTagDef(HTML.Tag.SELECT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_SMALL =
    new HTMLTagDef(HTML.Tag.SMALL,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_SPAN =
    new HTMLTagDef(HTML.Tag.SPAN,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_STRIKE =
    new HTMLTagDef(HTML.Tag.STRIKE,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_S =
    new HTMLTagDef(HTML.Tag.S,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_STRONG =
    new HTMLTagDef(HTML.Tag.STRONG,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_STYLE =
    new HTMLTagDef(HTML.Tag.STYLE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_SUB =
    new HTMLTagDef(HTML.Tag.SUB,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_SUP =
    new HTMLTagDef(HTML.Tag.SUP,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_TABLE =
    new HTMLTagDef(HTML.Tag.TABLE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TD =
    new HTMLTagDef(HTML.Tag.TD,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TEXTAREA =
    new HTMLTagDef(HTML.Tag.TEXTAREA,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TH =
    new HTMLTagDef(HTML.Tag.TH,
                   HTMLTagDef.RENDERING_EFFECT_TO_DESCENDATS);


  public static final HTMLTagDef T_TITLE =
    new HTMLTagDef(HTML.Tag.TITLE,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TR =
    new HTMLTagDef(HTML.Tag.TR,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TT =
    new HTMLTagDef(HTML.Tag.TT,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_U =
    new HTMLTagDef(HTML.Tag.U,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_UL =
    new HTMLTagDef(HTML.Tag.UL,
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_VAR =
    new HTMLTagDef(HTML.Tag.VAR,
                   HTMLTagDef.RENDERING_EFFECT_NONE);

  //tags not known by swing
  public static final HTMLTagDef T_BUTTON =
    new HTMLTagDef("button",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_LABEL =
    new HTMLTagDef("label",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_LEGEND =
    new HTMLTagDef("legend",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_IFRAME =
    new HTMLTagDef("iframe",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_COL =
    new HTMLTagDef("col",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_COLGROUP =
    new HTMLTagDef("colgroup",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TBODY =
    new HTMLTagDef("tbody",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_TFOOT =
    new HTMLTagDef("tfoot",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_THEAD =
    new HTMLTagDef("thead",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_DEL =
    new HTMLTagDef("del",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_INS =
    new HTMLTagDef("ins",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_Q =
    new HTMLTagDef("q",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_BDO =
    new HTMLTagDef("bdo",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_OPTGROUP =
    new HTMLTagDef("optgroup",
                   HTMLTagDef.RENDERING_EFFECT_NONE);


  public static final HTMLTagDef T_CONTENT =
    new HTMLTagDef("content",
                   HTMLTagDef.RENDERING_EFFECT_NONE);

  public static final HTMLTagDef T_PIMPLIED =
    new HTMLTagDef("p-implied",
                   HTMLTagDef.RENDERING_EFFECT_NONE);

  private static final HashMap<String, HTMLTagDef> name2defs;

  static {
    HTMLTagDef[] tagdefs = new HTMLTagDef[] {
      T_A, T_ADDRESS, T_APPLET, T_AREA, T_B, T_BASE, T_BASEFONT, T_BIG,
      T_BLOCKQUOTE, T_BODY, T_BR, T_CAPTION, T_CENTER, T_CITE, T_CODE,
      T_DD, T_DFN, T_DIR, T_DIV, T_DL, T_DT, T_EM, T_FONT, T_FORM,
      T_FRAME, T_FRAMESET, T_H1, T_H2, T_H3, T_H4, T_H5, T_H6, T_HEAD,
      T_HR, T_HTML, T_I, T_IMG, T_INPUT, T_ISINDEX, T_KBD, T_LI, T_LINK,
      T_MAP, T_MENU, T_META, T_NOFRAMES, T_OBJECT, T_OL, T_OPTION, T_P,
      T_PARAM, T_PRE, T_SAMP, T_SCRIPT, T_SELECT, T_SMALL, T_SPAN,
      T_STRIKE, T_S, T_STRONG, T_STYLE, T_SUB, T_SUP, T_TABLE, T_TD,
      T_TEXTAREA, T_TH, T_TITLE, T_TR, T_TT, T_U, T_UL, T_VAR, T_BUTTON,
      T_LABEL, T_LEGEND, T_IFRAME, T_COL, T_COLGROUP, T_TBODY, T_TFOOT,
      T_THEAD, T_DEL, T_INS, T_Q, T_BDO, T_OPTGROUP, T_CONTENT,
      T_PIMPLIED
    };

    name2defs = new HashMap<String, HTMLTagDef>();
    for (int i=0; i<tagdefs.length; i++) {
      HTMLTagDef def = tagdefs[i];
      name2defs.put(def.getName(), def);
    }
  }

}// HTMLTags
