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


/**
 * 
 * A dictionary data type.
 * 
 * 
 * @author max
 *
 */
public class Dictionary<T> 
{

	protected String id;
	protected Map2<String,T,Tuple<T>> index;
	
	private Tuple<T>[] data;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param index
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public Dictionary(String[] index, T[][] data) 
	{
//		assert index.length==data.length;
		this.data = new Tuple[data.length];
		/* insert data */
		for (int i=0; i<data.length;i++) {
			this.data[i] = new Tuple<T>(index, data[i]);
		}
		
		/* build index */
		this.index = 
			new HashMap2<String,T,Tuple<T>>(null,null,null);
		for (Tuple<T> tuple2 : this.data) {
			for (int i=0; i<tuple2.length(); i++) {
				T value = tuple2.get(i);
				String key = tuple2.getIndex().reverseGet(i); 
				this.index.put(key, value, tuple2);
			}			
		}
	}
	
	@SuppressWarnings("unchecked")
	public T lookUp(Pair<String,T> keyValue, String colName)
{
		Tuple<T> t =  this.index.get(keyValue.getFirst(), keyValue.getSecond());
		if (t==null) 
			return null;
		Object obj = t.get(colName);
		if (obj==null)
			return null;
		return (T) obj;
	}
	
	public boolean lookupValue (String colName, T query)
	{
		return this.index.get(colName, query)!=null;
	}
	
	public static void main(String[] args)
	{
		String[] index = {"code", "city"};
		String[][] data = {{"aaa","aachen"},{"bbb","berlin"}};
		Dictionary<String> dict = new Dictionary<String>(index,data);
		
		String sol = dict.lookUp(new Pair<String, String>("code","aaa"), "city");
		System.err.println(sol);
	}
	
}//Dictionary
