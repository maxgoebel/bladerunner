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
package at.tuwien.prip.model.document;
/**
	 * 
	 * Specifies a font as the combination of <p>
	 * <ul>
	 * 	<li>font size</li>
	 * <li>font family</li>
	 * <li>font type</li>
	 * </ul>
	 *
	 */
	public class Font 
	{
		protected String name;
		protected String family;
		protected String face;
		float size;
		protected String style;
		protected int weight;

		public Font(java.awt.Font font) {
			this.family = font.getFamily();
			this.size = font.getSize();
			if (font.getStyle()==java.awt.Font.BOLD) {
				weight = 800;
			} else if (font.getStyle()==java.awt.Font.ITALIC) {
				weight = 400;
				style = "italic";				
			} else if (font.getStyle()==java.awt.Font.PLAIN) {
				weight = 400;
			} else if (font.getStyle()==(java.awt.Font.BOLD|java.awt.Font.ITALIC)) {
				weight = 800;
				style = "italic";
			}
			
		}
		
		/**
		 * 
		 * Constructor. 
		 * 
		 * @param size
		 * @param family
		 * @param style
		 * @param weight
		 */
		public Font(float size, String name, String family, String style, int weight) 
		{
			this.name = name;
			this.family = family;
			this.size = size;
			this.style = style;
			this.weight = weight;
		}

		/**
		 * 
		 * Check if this font dominates another. This
		 * is true for larger font sizes and stronger
		 * font weights. In case of equivalence, this
		 * returns false; Font sizes must be at least
		 * 2 point different!
		 * 
		 * @param other
		 * @return
		 */
		public boolean dominates (Font other) {
			if (this.size-2>other.size) return true;
			else if (this.size==other.size) {
				if (this.weight>other.weight) {
					return true;
				}
			}
			return false;
		}

		public boolean isLarger (Font other) {
			if (this.size-1>other.size) return true;
			if (this.weight>other.weight) return true;
			return false;
		}

		public boolean isSameSize (Font other) {
			return (this.size==other.size && this.weight==other.weight);
		}

		/**
		 * @return the family
		 */
		public String getFamily() {
			return family;
		}

		/**
		 * @return the size
		 */
		public float getSize() {
			return size;
		}
		
		public String getName() {
			return name;
		}

		/**
		 * @return the style
		 */
		public String getStyle() {
			return style;
		}

		@Override
		public int hashCode() {
			int hash = 0;
			if (family!=null) {
				hash+=family.hashCode();
			}
			hash+=size;
			//			if (style!=null) hash+=style.hashCode();
			hash += weight;

			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Font)) {
				return false;
			}
			Font other = (Font) obj;
			
			if (name==null || other.name==null) {
				return 
				this.size==other.size && 
				this.family.equals(other.family) &&
				this.weight==other.weight;
			}
			return 
				this.size==other.size && 
				this.name.equals(other.name) &&
				this.weight==other.weight;
		}

		@Override
		public String toString() {
			return "["+/*family+":"+*/size+":"+weight+"]";
		}

	}//VizBox$Font
