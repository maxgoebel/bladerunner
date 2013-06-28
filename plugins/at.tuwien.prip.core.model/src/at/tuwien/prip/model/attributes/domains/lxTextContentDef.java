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
package at.tuwien.prip.model.attributes.domains;

import java.util.List;

public class lxTextContentDef implements lxValueDef {

    public final String att_name;

    public final String text_content;

    public lxTextContentDef (String att_name,
                             String text_content) {
        this.att_name = att_name;
        this.text_content = text_content;
    }

    public String getAttName() {
        return att_name;
    }

    public boolean matches(String v) {
        if (v.equals(text_content)) return true;
        if (text_content.equals("*")) return true;
        return false;
    }

    public static lxTextContentDef generate(String att_name,
            List<String> poss,
            List<String> negs)
    {
        if (poss.size()==0) return null;

        String value = poss.get(0).toString();

        // the positive examples
        for (int i=1; i<poss.size(); i++) {

            if (poss.get(i).toString().equals(value))
                continue;

            value = "*";
            break;

        }

        //FIXME: deal with negative examples
        // the negative examples
//        if (negs.size()>0) {
//            StringBuffer sbNeg = new StringBuffer(negs.get(0));
//            for (int i=1; i<negs.size(); i++) {
//
//            }
//        }
        return new lxTextContentDef(att_name, value);
    }

    public String toString () {
        return text_content;
    }
}
