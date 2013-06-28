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
package at.tuwien.prip.common.utils;

import java.util.*;
import java.text.*;

/**
 * <p>Description:
 *
 * Simple class to measure time needed for a task
 * supports measuring more tasks
 * currently these time formats are supported:
 *
 * - milliseconds
 * - seconds : milliseconds
 * - minutes : seconds : milliseconds
 *
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>

 * @author Peter Szinek
 * @version 1.0
 */

public class SimpleTimer
{
	private HashMap<Integer, SimpleTask> taskList;

	private class SimpleTask
	{
		private long startingTime, finishTime, elapsedTime;

		public void startTimer()
		{
			startingTime = System.currentTimeMillis();
			elapsedTime = 0;
		}

		public void resumeTimer() {
			startingTime = System.currentTimeMillis();
			//keep the value of elapsedTime
		}

		public void stopTimer()
		{
			finishTime = System.currentTimeMillis();
			elapsedTime += finishTime - startingTime;
		}

		public long getElapsedTime()
		{
			return elapsedTime;
		}
	}

	public SimpleTimer()
	{
		taskList = new HashMap<Integer, SimpleTask>();
	}

	public void startTask (int taskId)
	{
		SimpleTask task = new SimpleTask();
		task.startTimer();
		taskList.put(new Integer(taskId), task);
	}

	public void resumeTask (int taskId)
	{
		SimpleTask task = taskList.get(new Integer(taskId));
		if (task==null) {
			task = new SimpleTask();
			taskList.put(new Integer(taskId), task);
		}
		task.resumeTimer();
	}

	public void stopTask (int taskId)
	{
		SimpleTask task = taskList.get(new Integer(taskId));
		if (task!=null) {
			task.stopTimer();
		}
	}

	static public String addTimes(String time1, String time2)
	{
		SimpleDateFormat formatter = new SimpleDateFormat (
				"HH:mm:ss:SS" );
		long  resultMillis = 0;
		Date time1millis;

		ParsePosition pos1 = new ParsePosition(0);

		time1millis = formatter.parse( time1, pos1 );

		StringTokenizer st = new StringTokenizer( time2, ":" );

		resultMillis = time1millis.getTime() + new Integer( st.nextToken() ).intValue() * 60000 +
		new Integer( st.nextToken() ).intValue() * 1000 +
		new Integer( st.nextToken() ).intValue();

		return formatter.format( new Date ( resultMillis ) );
	}

	static public String averageTime(String time, int count)
	{
		StringTokenizer st = new StringTokenizer( time, ":" );

		long allTime =
			new Integer( st.nextToken() ).longValue() * 360000l +
			new Integer( st.nextToken() ).longValue() * 60000l +
			new Integer( st.nextToken() ).longValue() * 1000l +
			new Integer( st.nextToken() ).longValue();

		allTime /= count;

		long hours = allTime / 360000l;
		long minutes = ( allTime - hours ) / 60000l;
		long secs = ( allTime - hours - minutes ) / 1000l;
		long millis =  allTime - hours - minutes - secs;

		String result = new Long( hours ).toString() + ":" +
		new Long( minutes ).toString() + ":" +
		new Long( secs ).toString() + ":" +
		new Long( millis ).toString();

		return result;
	}


	public long getTimeMillis(int taskId)
	{
		long result = Long.MAX_VALUE;
		SimpleTask task = taskList.get(new Integer(taskId));
		if (task!=null) {
			result = taskList.get(new Integer(taskId)).getElapsedTime();
		}
		return result;
	}

	public String getTimeSecondsMillis(int taskId)
	{
		Date endDate = new Date(getTimeMillis(taskId));
		SimpleDateFormat formatter = new SimpleDateFormat (
				"ss:SSS");
		return formatter.format( endDate );
	}

	public String getTimeMinutesSecondsMillis(int taskId)
	{
		Date endDate = new Date(getTimeMillis(taskId));
		SimpleDateFormat formatter = new SimpleDateFormat (
				"mm:ss:SSS");
		return formatter.format( endDate );
	}
}
