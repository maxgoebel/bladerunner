/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.ui.netmon;

import java.util.Date;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.IHTTPResponse;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.network.IRequest;
import org.eclipse.atf.mozilla.ide.network.IResponse;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class NetworkCallLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * Returns the image based on the state of the specified IXHRCall.
	 * 
	 * @param call
	 * @return
	 */
	private Image getImage(INetworkCall call) {
		if (call.getState().equals(INetworkCall.ACTIVE_STATE))
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.RUNNING_IMAGE);
		else if (call.getState().equals(INetworkCall.ERROR_STATE))
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.ERROR_IMAGE);
		else if (call.getState().equals(INetworkCall.WARNING_STATE))
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.WARNING_IMAGE);
		else if (call.getState().equals(INetworkCall.SUCCESS_STATE))
			return MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.DONE_IMAGE);

		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		INetworkCall call = (INetworkCall) element;
		switch (columnIndex) {
		case 0:
			return getImage(call);
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		INetworkCall call = (INetworkCall) element;
		switch (columnIndex) {
		case 0: // STATE_COLUMN
			break;

		case 1: // URL COLUMN
			result = call.getRequest().getURL().toString();
			break;

		case 2: // METHOD COLUMN
		{
			IRequest req = call.getRequest();
			if (req instanceof IHTTPRequest)
				result = ((IHTTPRequest) req).getMethod();

			break;
		}
		case 3: //START TIME COLUMN
			long startTime = call.getStartTime();
			if (startTime != -1)
				result = new Date(startTime).toString();
			break;

		case 4: //END TIME COLUMN
			long endTime = call.getEndTime();
			if (endTime != -1)
				result = new Date(endTime).toString();

			break;

		case 5: //TOTAL TIME COLUMN
			long totalTime = call.getTotalTime();

			if (totalTime != -1)
				result = totalTime + " ms";
			break;

		case 6: //STATUS COLUMN
		{
			IResponse res = call.getResponse();
			if (res instanceof IHTTPResponse)
				result = ((IHTTPResponse) res).getStatus();
			break;
		}
		default:
			break;
		}
		return result;
	}

}
