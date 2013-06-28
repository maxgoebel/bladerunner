package org.eclipse.atf.ui.debug;

import org.eclipse.atf.mozilla.ide.debug.INestedEventLoop;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;

/**
 * Provides an event loop so that when you are on the same thread as an event
 * loop, you can block the event you're in, but continue to process other
 * scheduled system events.
 * 
 * Note on runEventLoop/stopEventLoop: These methods are meant to be called in
 * pairs. However, since they may be called by different threads, the ordering
 * constraints are a bit relaxed. Basically, for a given runEventLoop
 * (<b>rEL</b>) / stopEventLoop (<b>sEL</b>) call pair, this implementation
 * guarantees correct results even if the sEL call is called before its
 * corresponding rEL. There cannot be, however, two consecutive calls to either
 * rEL or sEL in the same pair of calls. For instance, an example of a valid
 * sEL/rEL call sequence is: <BR>
 * <BR>
 * (sEL,rEL);(rEL,sEL);(rEL,sEL);(sEL,rEL)<BR>
 * <BR>
 * An example of an invalid sEL/rEL call sequence:<BR>
 * <BR>
 * (sEL,rEL)<b>(sEL,sEL)</b><BR>
 * <b>(rEL,rEL)</b><BR>
 * <BR>
 * Both will result in an IllegalStateException being thrown.
 * 
 * @author peller
 */
public class NestedEventLoop implements INestedEventLoop {

	private static final Object INITIAL = new Object();
	private static final Object RUN = new Object();
	private static final Object STOPPING = new Object();

	private final Display _display;
	private volatile Object _state = INITIAL;
	private volatile long _result;

	public NestedEventLoop(Display display) {
		_display = display;
	}

	/**
	 * Blocks the current thread in the event loop until its corresponding
	 * {@link #stopEventLoop(long)} call is performed.
	 * 
	 * @throws SWTException
	 *             if this thread is not the UI thread.
	 * 
	 * @throws IllegalStateException
	 *             if the current state doesn't allow this method to be called
	 *             (see class docs).
	 * 
	 * @return a numerical value communicated by the thread that calls
	 *         {@link #stopEventLoop(long)}.
	 */
	public long runEventLoop() {

		synchronized (this) {
			if (_state == RUN) {
				// A start->start pair is illegal.
				throw new IllegalStateException("Nested event loop already running.");
			} else if (_state == STOPPING) {
				// End of stop->start call pair.
				_state = INITIAL;
				return _result;
			}
			_state = RUN;
		}

		while (_state == RUN && !Workbench.getInstance().isClosing()) {
			try {
				if (!_display.readAndDispatch()) {
					_display.sleep();
				}
			} catch (Throwable t) {
				MozillaDebugUIPlugin.log(t);
				if (t instanceof ThreadDeath)
					throw (ThreadDeath) t;

				if (t instanceof RuntimeException)
					;

				if (t instanceof Error)
					throw (Error) t;

				// TODO ExceptionHandler.getInstance().handleException(t);
			}
		}

		synchronized (this) {
			// End of start->stop call pair.
			_state = INITIAL;
		}

		return _result;
	}

	/**
	 * Signals to the thread blocked in {@link #runEventLoop()} that it may quit
	 * the event loop.
	 * 
	 * @param rv
	 *            A numerical value that will be returned by the
	 *            {@link #stopEventLoop(long)} call that is the pair of this
	 *            call.
	 * 
	 * @throws IllegalStateException
	 *             if the current state doesn't allow this method to be called
	 *             (see class docs).
	 */
	public synchronized void stopEventLoop(long rv) {
		// A stop->stop pair is illegal.
		if (_state == STOPPING) {
			// throw new
			// IllegalStateException("Stop already issued for this start/stop cycle.");
			return;
		}
		_state = STOPPING;
		_result = rv;
	}
}