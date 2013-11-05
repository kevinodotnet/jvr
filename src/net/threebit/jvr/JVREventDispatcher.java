package net.threebit.jvr;

/*
 * $Id: JVREventDispatcher.java,v 1.4 2005/01/26 01:34:04 kevino Exp $
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

/**
 * A thread that monitors the event queue for new events, then dispatches them
 * to registered listeners as they are received.
 */
public class JVREventDispatcher extends Thread {

	public Logger logger = Logger.getLogger("net.threebit.jvr");

	/** 
	 * Milliseconds that an event can wait in the event queue before
	 * a warning is generated.
	 */
	long slowQueueThreshold = 100;
	public void run() {
		JVREventBaseClass event = null;
		while (true) {
			try {
				// TODO: ability to turn logging on/off
				event = null;
				synchronized (jvr.eventQueue) {
					// If there are no events queue, wait for one to arrive.
					// This is a FIFO queue.
					if (jvr.eventQueue.size() == 0) {
						jvr.eventQueue.wait(); 
					}
					event = (JVREventBaseClass) jvr.eventQueue.remove(0);
				}
				long eventStart = System.currentTimeMillis();
				// How long was the event in the queue?  Warn if it is above the "threshhold".
				if (event.age() > slowQueueThreshold) {
					logger.info("WARNING: event was queued for "+event.age()+"ms.");
				}
				synchronized (jvr.listeners) {
					if (jvr.listeners.size() == 0) { 
					}
					else {
						// block modifications to listeners by this thread.
						jvr.iteratingOnListeners = true;
						try {
							long listenerStart = -1;
							long listenerEnd = -1;
							for (Iterator i = jvr.listeners.iterator(); i.hasNext(); ) {
								listenerStart = System.currentTimeMillis();
								Object o = i.next();
								if (o == null) { continue; }
								try {
									MetaEventListener listener = (MetaEventListener) o;
									if (event instanceof MetaEvent) {
										if (!listener.handleEvent((MetaEvent)event)) {
											// handler told us to stop processing the event.
											break;
										}
									}
									else {
										if (!listener.handleEvent((JVREvent)event)) {
											// handler told us to stop processing the event.
											break;
										}
									}
								}
								catch (Throwable e) {
									// If a handler shows a Throwable then still keep trying the next 
									// handler.  It would be bad to allow one bad handler to make other handlers
									// 'lose' events.  Log the exception (for the stack trace) but don't rethrow it.
									logger.throwing(getClass().getName(),"run",new RuntimeException("Exception within handler {" + o + "} during event " + event,e));
								}
								finally {
									listenerEnd = System.currentTimeMillis();
									int handlerWarnTime = 100;
									if ((listenerEnd-listenerStart) > handlerWarnTime) {
										logger.info(
											"WARNING: slow event handler detected. " +
											"class: " + o.getClass().getName() + " " +
											"ms: " + (listenerEnd-listenerStart)
										);
									}
								}
							}
						}
						finally {
							jvr.iteratingOnListeners = false;
							if (jvr.addListeners.size() > 0) {
								for (Iterator i = jvr.addListeners.iterator(); i.hasNext(); ) { jvr.addEventListener((MetaEventListener)i.next()); }
								jvr.addListeners.clear();
							}
							if (jvr.removeListeners.size() > 0) {
								for (Iterator i = jvr.removeListeners.iterator(); i.hasNext(); ) { jvr.removeEventListener((MetaEventListener)i.next()); }
								jvr.removeListeners.clear();
							}
						}
					}
				}
				long eventEnd = System.currentTimeMillis();
			}
			catch (Throwable t) {
				RuntimeException e = new RuntimeException("Exception detected during event dispatching. Should never happen.",t);
				logger.throwing(getClass().getName(),"run",e);
				// Always sleep in while/true/catch to avoid racing the CPU.
				try { Thread.sleep(100); } catch (Throwable ignore) { }
			} 
		} // while()
	} // run()
} // class
