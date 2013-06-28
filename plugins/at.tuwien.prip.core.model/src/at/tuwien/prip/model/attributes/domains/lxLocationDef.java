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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class lxLocationDef
implements lxValueDef {

    private final String att_name;
    /**
     * enumeration of allowed positive values
     * (if positive-definition part is a finite
     * set, null otherwise)
     */
    private final String location;

    public lxLocationDef (String att_name,
                          String location) {
        this.att_name = att_name;
        this.location = location;
    }

    public String getAttName() {
        return att_name;
    }

    public boolean matches(String v) {

        if (v.equals(location)) return true;

        String[] sArrV = v.split("\\/");
        String[] sArrW = location.split("\\/");

        for (int i=1; i<sArrV.length; i++) {
            if (i<sArrW.length) {
                if (sArrW[i].equals("*")) continue;         // check tag for '*'
                if (sArrW[i].contains("*")) continue;       // check index for '*'
                if (!sArrW[i].equals(sArrV[i])) return false;
            }
        }
        return true;
    }

    public static lxLocationDef generate(String att_name,
            List<String> poss,
            List<String> negs)
    {
        if (poss.size()==0) return null;

        StringBuffer sbPos = new StringBuffer(poss.get(0));

        // the positive examples
        for (int i=1; i<poss.size(); i++) {

            String[] sArrV = sbPos.toString().split("\\/");
            String[] sArrW = poss.get(i).split("\\/");

            sbPos = new StringBuffer("");

            for (int j=1; j<sArrW.length; j++) {
                if (j<sArrV.length) {

                    if (sArrV[j].contains("*")) {
                        sbPos.append("/"+sArrV[j]);
                        continue;
                    }

                    // break up into composites
                    Pattern pattern_index = Pattern.compile("\\d");
                    Matcher matcherIndexV = pattern_index.matcher(sArrV[j]);
                    Matcher matcherIndexW = pattern_index.matcher(sArrW[j]);

                    Pattern pattern_tag = Pattern.compile("\\w*");
                    Matcher matcherTagV = pattern_tag.matcher(sArrV[j]);
                    Matcher matcherTagW = pattern_tag.matcher(sArrW[j]);

                    // do index comparison
                    int vI = -1, wI = -1;
                    if (matcherIndexV.find())
                        vI = Integer.parseInt(matcherIndexV.group());
                    if (matcherIndexW.find())
                        wI = Integer.parseInt(matcherIndexW.group());

                    if (vI>=0 && wI>=0){
                        if (vI!=wI) {
                            sArrW[j] = sArrW[j].substring(0, sArrW[j].indexOf(String.valueOf(wI)));
                            sArrW[j] = sArrW[j].concat("*]");
                        }
                    }

                    // do tag comparison
                    String vT = null, wT = null;
                    if (matcherTagV.find())
                        vT = matcherTagV.group();
                    if (matcherTagW.find())
                        wT = matcherTagW.group();

                    if (wT.equals(vT))
                        sbPos.append("/"+sArrW[j]);
                    else
                        sbPos.append("/*");
                }
            }
        }

        if (negs.size()>0) {

        }
        return new lxLocationDef(att_name, sbPos.toString());
    }

    public String toString () {
        return location;
    }
}
