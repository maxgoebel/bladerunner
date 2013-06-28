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
package at.tuwien.prip.model.document.segments.web;

import java.util.ArrayList;
import java.util.List;

/**
	 * HTMLFormInput
	 *
	 */
	public class HTMLFormInput
	{
		protected InputType type;
		protected String name;
		protected String value;

		/* for radio & checkbox inputs */
		protected List<String> radioOptions;
		protected int selected;

		public enum InputType {TEXT, PASSWORD, CHECKBOX, RADIO, SUBMIT, IMAGE, RESET, BUTTON, HIDDEN, FILE};

		/**
		 * Constructor.
		 * @param type
		 * @param name
		 * @param radioOptions
		 */
		public HTMLFormInput(InputType type, String name, List<String> radioOptions)
		{
			this(type,name,"");
			this.radioOptions = radioOptions;
		}

		/**
		 * Constructor.
		 * @param type
		 * @param name
		 * @param value
		 */
		public HTMLFormInput(InputType type, String name, String value)
		{
			this();
			this.type = type;
			this.name = name;
			this.value = value;
		}

		/**
		 *Constructor.
		 */
		public HTMLFormInput()
		{
			this.radioOptions = new ArrayList<String>();
		}

		public String getName() {
			return name;
		}

		public InputType getType() {
			return type;
		}

		public String getValue() {
			return value;
		}

		public List<String> getRadioOptions() {
			return radioOptions;
		}

		public void setType(InputType type) {
			this.type = type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setRadioOptions(List<String> radioOptions) {
			this.radioOptions = radioOptions;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}//HTMLInputForm
