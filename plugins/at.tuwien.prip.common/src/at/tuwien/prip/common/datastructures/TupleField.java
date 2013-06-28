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
package at.tuwien.prip.common.datastructures;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import at.tuwien.prip.common.utils.Counter;

/**
 * 
 * Data collection to hold two-dimensional
 * relational data sets. Base container is
 * a <a>Tuple</a>.
 * 
 * @author max
 *
 * @param <T>
 */
public class TupleField<T> {

	/* */
	private ArrayList<Tuple<T>> rows;
	
	/* */
	private final BidiMap<String,Integer> index;
	
	/* */
	private final static Counter colCounter = new Counter();
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param index
	 */
	public TupleField(String[] index) {
		this.index = new BidiMap<String, Integer>();
		for (String i : index) {
			this.index.put(i, (int) colCounter.get());
		}
		rows = new ArrayList<Tuple<T>>();
	}
	
	/**
	 * 
	 * Add a row.
	 * 
	 * @param index
	 * @param row
	 * @param defVal
	 */
	@SuppressWarnings("unchecked")
	public void addRow (
			ArrayList<String> index, 
			ArrayList<T> row, 
			T defVal) 
	{
		assert index.size()==row.size();
		
		String[] arg1 = new String[index.size()];
		Object[] arg2 = new Object[row.size()];
		arg1 = index.toArray(arg1);
		arg2 = row.toArray(arg2);
		Tuple<T> tuple = new Tuple<T>(arg1,(T[]) arg2);
		
		/* find and add new column headers */
		if (index.size()!=this.index.size()) {
			
			if (index.size()>this.index.size()) {
				for (String s : index) {
					if (!this.index.keySetA().contains(s)) {
						addColumn(s, defVal);
					}
				}
			} else {
				for (String key : this.index.keySetA()) {
					if (!tuple.index.containsKey(key)) {
						tuple.addColumn(key, defVal);
					}
				}
			}
		}

		tuple.arrangeBy(this.index);
		rows.add(tuple);
	}
	
	/**
	 * 
	 *  Add a column and fill new column of existing rows 
	 *  with a default value.
	 *  
	 * @param colName
	 * @param defVal
	 */
	public void addColumn (String colName, T defVal) {
		if (index.containsKey(colName)) return; //already exists
		
		int colIndex; 	
		do {
			colIndex = (int) colCounter.get();
		} while (	index.values().contains(colIndex));
		
		index.put(colName, colIndex);
		
		for (Tuple<T> row : rows) {
			row.addColumn(colName, defVal);
		}
	}
	
	public Tuple<T> getRow (int i) {
		return rows.get(i);
	}
	
	/**
	 * 
	 * Write this tuple field to a comma-separated file.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void toCSV (String fileName, String encoding) 
	throws IOException {

		/* the writer */
	     CSVWriter writer = 
	    	 new CSVWriter(	
	    		 new OutputStreamWriter(
	    				 new FileOutputStream(fileName), 
	    		 encoding), 
	    	 '\t');
	     
	     /* write headers */
	     String[] entries = new String[rows.get(0).index.keySetA().size()];
	     for (int i=0; i<entries.length; i++) {
    		 entries[i] = index.reverseGet(i);
    	 }
	     writer.writeNext(entries);
		
	     /* write data */
	     for (Tuple<T> row : rows) {
	    	 entries = new String[row.array.size()];
	    	 for (int i=0; i<row.array.size(); i++) {
	    		 entries[i] = row.get(i).toString();
	    	 }
	    	 writer.writeNext(entries);
	     }
		writer.close();	
	}
	
	public int getRowCount () {
		return rows.size();
	}
	
	public int getColumnCount () {
		if (getRowCount()>0) {
			return rows.get(0).length();
		} else {
			return 0;
		}
	}
	
	public int getMaxIndex () {
		int result = 0;
		for (Integer i : index.m2.keySet()) {
			if (i>result) {
				result = i;
			}
		}
		return result;
	}
	
}//TupleField
