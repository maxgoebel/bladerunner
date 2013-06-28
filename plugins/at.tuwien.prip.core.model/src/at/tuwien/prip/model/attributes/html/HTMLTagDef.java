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

import javax.swing.text.html.HTML;

/**
 * HTMLTagDef.java
 *
 *
 * Created: Sun Jul 27 17:49:40 2003
 *
 * @author Michal Ceresna
 * @version 1.0
 */
public class HTMLTagDef {

  /**
   * tag <a> affects rendering of descendants
   * if for a tag <b> holds:
   * <b>...</b> looks differently as
   * <a>....<b>...</b>...</a>, because of
   * being enclosed in <a>...</a>
   */
  public static final int RENDERING_EFFECT_TO_DESCENDATS = 1;
  /**
   * dosn't affect rendering of descendants tags
   */
  public static final int RENDERING_EFFECT_NONE = 2;


  private final String name;
  private final int rendering_effect;

  public HTMLTagDef(HTML.Tag t,
          int rendering_effect)
  {
      this.name = t.toString();
      this.rendering_effect = rendering_effect;
  }

  public HTMLTagDef(String tagName,
                    int rendering_effect)
  {
    this.name = tagName;
    this.rendering_effect = rendering_effect;
  }

  public String getName() {
    return name;
  }

  public int getRenderingEffect() {
    return rendering_effect;
  }

  public String toString() {
    return name;
  }

} // HTMLTagDef
