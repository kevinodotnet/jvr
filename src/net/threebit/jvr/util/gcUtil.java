package net.threebit.jvr.util;

/*
 * $Id: gcUtil.java,v 1.5 2004/07/02 18:11:38 kevino Exp $
 *
 * Copyright (c) 2003,2004 Kevin O'Donnell
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,  USA.
 */

import java.util.*;
import java.util.logging.Logger;
import net.threebit.jvr.*;

/**
 * A collection of Global Call convenience functions.
 */

public class gcUtil {

	public static Logger logger = Logger.getLogger("net.threebit.jvr.util");

	/**
	 * Synchronous hangup method.
	 */
	public static void hangup (final long linedev, final long crn, final long timeout) throws JVRException {
		// logger.info("Hanging up (linedev="+linedev+" crn="+crn+" timeout="+timeout);

		/**
		 * Event listener that implements global call hangup for a single linedev/crn.
		 */
		class MyHandler extends JVRMetaEventListener {
			public Throwable error = null;
			public boolean finished = false;
			public boolean handleEvent (JVREvent event) {
				try {
					if (event.target == this) {
						gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
					}
				}
				catch (Throwable e) {
					error = new JVRException("Encountered an error during call termination",e);
					synchronized (this) { notifyAll(); }
				}
				return true;
			}
			public boolean handleEvent (MetaEvent event) {
				try {
					if (event.evtdev != linedev) { return true; }
					if (event.evttype == gc.GCEV_DROPCALL) {
						gc.ReleaseCallEx(event.crn,gc.EV_ASYNC);
					}
					else if (event.evttype == gc.GCEV_RELEASECALL) {
						gc.ResetLineDev(event.linedev,gc.EV_ASYNC);
					}
					else if (event.evttype == gc.GCEV_RESETLINEDEV) {
						gc.WaitCall(event.linedev,0,gc.EV_ASYNC);
						finished = true;
						synchronized (this) { notifyAll(); }
					}
					else if ((event.evttype == gc.GCEV_TASKFAIL) ||
						(event.evttype == gc.GCEV_RESTARTFAIL) ||
						(event.evttype == gc.GCEV_RELEASECALL_FAIL)) {
						error = new JVRException("Dialogic task failed: " + event);
						synchronized (this) { notifyAll(); }
					}
				}
				catch (Throwable e) {
					error = new JVRException("Encountered an error during call termination",e);
					synchronized (this) { notifyAll(); }
				}
				return true;
			}
		}
		MyHandler my = new MyHandler();
		jvr.addEventListener(my);
		new JVREvent(my).fire();
		synchronized (my) {
			try {
				if (timeout == 0) { my.wait(); }
				else { my.wait(timeout); }
			}
			catch (Throwable ignore) { logger.throwing("","",ignore); }
		}
		jvr.removeEventListener(my);
		if (my.error != null) { throw (new JVRException("Error while hanging up.",my.error)); }
		if (! my.finished) { 
			throw new JVRException("Timeout waiting for operation to complete."); 
		}
	}
}
