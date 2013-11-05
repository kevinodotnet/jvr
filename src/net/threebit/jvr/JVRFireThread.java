package net.threebit.jvr;

/*
 * $Id: JVRFireThread.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
import java.util.logging.*;

/**
 * Manages queue JVREvents that have yet to be fired.
 */

public class JVRFireThread extends Thread {

	private static Logger logger = Logger.getLogger("net.threebit.jvr");
	private static JVRFireThread singleton = null;
	private static ArrayList queue = new ArrayList();

	/** This is a singleton class. */
	private JVRFireThread() {
	}

	/** Runs forever and fires events when their time has come. */
	public void run() {
		// logger.info("Starting...");
		while (true) {
			try {
				synchronized (queue) {
					// If there is nothing to do, then block until something comes in.
					// We may receive a notify without the size changing so use a while() loop.
					while (queue.size() == 0) {
						// logger.info("Waiting for queued events");
						queue.wait(); 
					}
					// Fire all of the events that need to be fired right now.
					long when = 0;
					long now = System.currentTimeMillis();
					while (queue.size() > 0) {
						// logger.info("There are events in the queue:\n"+printQueue());
						// logger.info("now : " + now);
						Object[] o = (Object[]) queue.get(0);
						when = ((Long)o[0]).longValue();
						// logger.info("when: " + when);
						JVREvent target = (JVREvent) o[1];
						if (now >= when) {
							// logger.info("Firing " + target);
							queue.remove(0);
							target.fire();
							continue;
						}
						else {
							// We are into a part of the queue that is not fireable at this time.
							break;
						}
					}
					// If there are no more events in the queue then wrap and wait for the
					// next event.
					if (queue.size() == 0) { continue; }
					// Sleep until the next event is due to be run.
					when = ((Long)((Object[]) queue.get(0))[0]).longValue();
					long dur = when-now;
					// logger.info("Sleeping for " + dur + " ms");
					queue.wait(dur);
					// logger.info("Woke up...");
				}
			}
			catch (Throwable t) {
				logger.throwing(getClass().getName(),"run",t);
				// Sleep to avoid consuming all resources in case of an error.
				try { Thread.sleep(100); } catch (Throwable ignore) { }
			}
		}
	}

	/** Get the singleton.  If it does not exist then create it and start it. */
	private static synchronized JVRFireThread singleton() {
		if (singleton == null) {
			singleton = new JVRFireThread();
			singleton.setDaemon(true);
			singleton.setName(JVRFireThread.class.getName());
			singleton.start();
		}
		return singleton;
	}

	/** Register an event to be fired later. 
	 * @param millis Milliseconds until the event should be fired.
	 * @param target The event to fire when it's time has come.
	 */
	public static void fire (long millis, JVREvent target) {
		// logger.info("Fire " + target + " in " + millis + "ms");
		if (millis < 0) { throw new IllegalArgumentException("millis must be greater than or equal to 0"); }
		// Make sure the thread is alive.
		singleton();
		// Register the event in the queue.
		long when = System.currentTimeMillis() + millis;
		synchronized (queue) {
			if (queue.size() == 0) {
				// trivial edge-case.  No ordering needs to be done.
				queue.add(new Object[]{new Long(when),target});
				queue.notifyAll();
				// logger.info("return --> A \n" + printQueue());
				return;
			}
			// Bubble sort used here.  Technically this is O(n) but we assert
			// there there will never be that many events queued for firing
			// less than 100, so this should not be a problem.
			if (queue.size() > 100) { logger.info("WARNING: queue size has exceeded 100; possible performance issue."); }
			for (int x = 0; x < queue.size(); x++) {
				Object[] o = (Object[]) queue.get(x);
				long l = ((Long)o[0]).longValue();
				if (l > when) {
					queue.add(x,new Object[]{new Long(when),target});
					queue.notifyAll();
					// logger.info("return --> B \n" + printQueue());
					return;
				}
			}
			// Must be the last entry.
			queue.add(new Object[]{new Long(when),target});
			queue.notifyAll();
			// logger.info("return --> C \n" + printQueue());
			return;
		}
	}

	/**
	 *
	 */
	public static String printQueue() {
		synchronized (queue) {
			String s = "";
			for (int x = 0; x < queue.size(); x++) {
				Object[] o = (Object[]) queue.get(x);
				s += "["+x+"] " + o[0] + "ms " + o[1] + "\n";
			}
			return s;
		}
	}

}
