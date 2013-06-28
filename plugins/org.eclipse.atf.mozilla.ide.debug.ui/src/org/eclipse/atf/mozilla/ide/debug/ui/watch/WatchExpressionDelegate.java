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
package org.eclipse.atf.mozilla.ide.debug.ui.watch;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugStackFrame;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

public class WatchExpressionDelegate implements IWatchExpressionDelegate {

	private Job fRunDispatch;

	/**
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#getValue(java.lang.String, org.eclipse.debug.core.model.IDebugElement)
	 */
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {
		// find a stack frame context if possible.
		IStackFrame frame = null;
		if (context instanceof IStackFrame) {
			frame = (IStackFrame) context;
		} else if (context instanceof IThread) {
			try {
				frame = ((IThread) context).getTopStackFrame();
			} catch (DebugException e) {
				//               Logger.logException(e);
			}
		}
		if (frame == null) {
			listener.watchEvaluationFinished(null);
		} else {
			if (frame instanceof JSDebugStackFrame) {
				fRunDispatch = new EvaluationRunnable(expression, listener, (JSDebugStackFrame) frame);
				fRunDispatch.schedule();
			} else {
				listener.watchEvaluationFinished(null);
			}
		}
	}

	/**
	 * Runnable used to evaluate the expression.
	 */
	private final class EvaluationRunnable extends Job {

		private String fExpressionText;
		private IWatchExpressionListener fListener;
		private JSDebugStackFrame fFrame;
		private boolean fHasErrors;
		private String[] fErrorMessage;

		public EvaluationRunnable(String expressionText, IWatchExpressionListener listener, JSDebugStackFrame frame) {
			super("EvaluationRunnable");
			setSystem(true);
			fExpressionText = expressionText;
			fListener = listener;
			fFrame = frame;
			fHasErrors = false;
			fErrorMessage = null;

		}

		public IStatus run(IProgressMonitor monitor) {

			try {
				final IValue value = fFrame.eval(fExpressionText);

				IWatchExpressionResult watchResult = new IWatchExpressionResult() {
					public IValue getValue() {
						return value;
					}

					// Eclipse calls hasErrors() before calling getValue so hasErrors() can't
					// be used to return errors from evaluating expression
					public boolean hasErrors() {
						return fHasErrors;
					}

					public String[] getErrorMessages() {
						return fErrorMessage;
					}

					public String getExpressionText() {
						return fExpressionText;
					}

					public DebugException getException() {
						return null;
					}
				};
				fListener.watchEvaluationFinished(watchResult);
			} catch (Exception e) {
				//               Logger.logException(e);
				fListener.watchEvaluationFinished(null);
				// TODo fix
			}
			DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { new DebugEvent(WatchExpressionDelegate.this, DebugEvent.SUSPEND, DebugEvent.EVALUATION_IMPLICIT) });
			return Status.OK_STATUS;
		}
	}
}