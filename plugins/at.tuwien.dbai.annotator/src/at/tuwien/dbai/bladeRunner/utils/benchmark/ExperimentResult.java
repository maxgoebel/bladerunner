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
package at.tuwien.dbai.bladeRunner.utils.benchmark;


/**
 * ExperimentResult.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 17, 2011
 */
public class ExperimentResult 
{

	private int numDocuments;
	
	private int numPages;
	
	private int numFP;
	
	private int numTP;
	
	private int numTN;
	
	private int numFN;
	
	private double precision;
	
	private double recall;
	
	private int numAnnotations;

	public void setNumDocuments(int numDocuments) {
		this.numDocuments = numDocuments;
	}

	public int getNumDocuments() {
		return numDocuments;
	}

	public void setNumAnnotations(int numAnnotations) {
		this.numAnnotations = numAnnotations;
	}

	public int getNumAnnotations() {
		return numAnnotations;
	}

	public int getNumFP() {
		return numFP;
	}

	public void setNumFP(int numFP) {
		this.numFP = numFP;
	}

	public int getNumTP() {
		return numTP;
	}

	public void setNumTP(int numTP) {
		this.numTP = numTP;
	}

	public int getNumTN() {
		return numTN;
	}

	public void setNumTN(int numTN) {
		this.numTN = numTN;
	}

	public int getNumFN() {
		return numFN;
	}

	public void setNumFN(int numFN) {
		this.numFN = numFN;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}

	public int getNumPages() {
		return numPages;
	}
	
}//ExperimentResult
