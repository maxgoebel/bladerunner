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
import java.util.Set;


import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.model.attributes.html.HTMLAttributeDef;
import at.tuwien.prip.model.attributes.html.HTMLAttributes;
import at.tuwien.prip.model.attributes.html.HTMLValueDomain;


/**
 * lxValueDefFactory.java
 *
 *
 * Created: Thu Aug 14 12:02:32 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class lxValueDefFactory {

    @SuppressWarnings("unchecked")
    public static lxValueDef
        generate(String tag_name,
                 String att_name,
                 List<String> poss,
                 List<String> negs)
    {
        if (att_name.equals("locFeat"))
            return lxLocationDef.generate(att_name, poss, negs);

        if (att_name.equals("txtFeat"))
            return lxTextContentDef.generate(att_name, poss, negs);

        HTMLAttributeDef ad =
            HTMLAttributes.getDefinitionByName(tag_name, att_name);
        HTMLValueDomain vd = ad.getValueDomain();

        Class<?> klass = vd.getDomainClass();
        if (klass==lxEmptyValueDef.class) {
            return lxEmptyValueDef.generate(att_name, poss, negs);
        } else if (klass==lxEnumValueDef.class) {
            Object[] params = vd.getFactoryParams();
            Set<String> en = (Set<String>) params[0];
            return lxEnumValueDef.generate(att_name, en, poss, negs);
        } else if (klass==lxFewStringsValueDef.class) {
            return lxFewStringsValueDef.generate(att_name, poss, negs);
        } else if (klass==lxManyStringsValueDef.class) {
            return lxManyStringsValueDef.generate(att_name, poss, negs);
        } else if (klass==lxURIValueDef.class) {
            return lxURIValueDef.generate(att_name, poss, negs);
        } else {
            //should not happen
            ErrorDump.errorHere(lxValueDefFactory.class);
            return null;
        }
    }

} // lxValueDefFactory
