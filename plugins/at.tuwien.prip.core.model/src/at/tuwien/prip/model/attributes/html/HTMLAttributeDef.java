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

/**
 * HTMLAttributeDef.java
 *
 *
 * Created: Sun Jul 27 19:05:26 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class HTMLAttributeDef {

  /**
   * each value implies differently rendered content
   */
  public static final int RENDERING_EFFECT_BY_EACH_VALUE = 1;
  /**
   * affects rendering, but looks same for different values
   */
  public static final int RENDERING_EFFECT_BY_PRESENCE = 2;
  /**
   * dosn't affect rendering
   */
  public static final int RENDERING_EFFECT_NONE = 3;


  /**
   * each value implies differently rendered content
   */
  public static final String DEFAULT_VALUE_IMPLIED = "default_value_implied";
  /**
   * affects rendering, but looks same for different values
   */
  public static final String DEFAULT_VALUE_REQUIRED = "default_value_required";


  private final String name;
  private final int rendering_effect;
  private final HTMLValueDomain domain;
  private final String default_value;
  private final HTMLTagDefSet tags;

  public HTMLAttributeDef(String name,
                        int rendering_effect,
                        HTMLValueDomain domain,
                        String default_value,
                        Comment comment,
                        HTMLTagDefSet tags)
  {
    this.name = name;
    this.rendering_effect = rendering_effect;
    this.domain = domain;
    this.default_value = default_value;
    this.tags = tags;
  }

  public String getName() {
    return name;
  }

  public int getRenderingEffect() {
    return rendering_effect;
  }

  public HTMLValueDomain getValueDomain() {
    return domain;
  }


  public String getDefaultValue() {
    if (default_value.equals(DEFAULT_VALUE_IMPLIED) ||
        default_value.equals(DEFAULT_VALUE_REQUIRED)) {
      return "";
    }

      return default_value;
  }

  public HTMLTagDefSet getTags() {
    return tags;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<attdef:");
    sb.append(" name="+name);
    sb.append(" tags="+tags.toString());
    sb.append(">");
    return sb.toString();
  }

} // HTMLAttributeDef
