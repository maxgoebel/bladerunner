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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;


/**
 * HTMLForm.java
 *
 *
 * A container for a HTML form, holding all
 * necessary parameters.
 *
 * Created: May 6, 2009 9:08:55 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class HTMLForm
{

	private Element root;

	private FormMethod method;
	private String action;
	private String name;
	private URL url;
	private List<HTMLFormInput> inputs = new ArrayList<HTMLFormInput>();

	private boolean submittable = false;

	public enum FormMethod {GET, POST};


	public boolean isSubmittable() {
		return submittable;
	}

	public FormMethod getMethod() {
		return method;
	}

	public String getName() {
		return name;
	}

	public URL getUrl() {
		return url;
	}

	public String getAction() {
		return action;
	}

	public List<HTMLFormInput> getInputs() {
		return inputs;
	}

	public void setRoot(Element root) {
		this.root = root;
	}

	public Element getRoot() {
		return root;
	}

	public void setMethod(FormMethod method) {
		this.method = method;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setSubmittable(boolean submittable) {
		this.submittable = submittable;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setName(String name) {
		this.name = name;
	}


}//HTMLForm
