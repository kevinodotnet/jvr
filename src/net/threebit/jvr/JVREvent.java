package net.threebit.jvr;

/*
 * $Id: JVREvent.java,v 1.19 2004/12/15 02:13:22 kevino Exp $
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

/**
 * Non-dialogic event class.
 */

public class JVREvent extends JVREventBaseClass {

	/** The target of the event - ie: what is this event about? */
	public Object target = null;

	/** Optional data that is event specific. */
	public Object data = null;

	/** Default event type. */
	public static final int EV_DEFAULT = 1;

	/** 
	 * The type of event is specific to each target class, 
	 * except that '1' is reserved for the default 'no type'
	 * type.
	 */
	public int evttype = EV_DEFAULT;

	/**
	 *
	 */
	public JVREvent() {
	}

	/**
	 * Creates a new event.
	 * @param target The target of the new event.
	 */
	public JVREvent (Object target) {
		this.target = target;
	}

	/**
	 * Creates a new event.
	 * @param target The target of the new event.
	 * @param data The data of the new event.
	 */
	public JVREvent (Object target, Object data) {
		this.target = target;
		this.data = data;
	}

	/**
	 * Causes the event to be inserted into the JVR event queue.
	 * Just a short form for calling {@link jvr#handleEvent(JVREvent)} manually.
	 */
	public void fire() {
		eventTime = System.currentTimeMillis();
		jvr.handleEvent(this);
	}

	/**
	 *
	 */
	public void fire (final int millis) {
		JVRFireThread.fire(millis,this);
		/*
		Thread t = new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				long when = start+millis;
				// logger.info("fire("+millis+")");
				// logger.info("start: " + start);
				// logger.info("when: " + when);
				while (System.currentTimeMillis() < when) {
					try {
						long t = millis - (System.currentTimeMillis() - start);
						logger.info("firing in " + t + " milliseconds");
						sleep(t);
					} catch (Exception ignore) { }
				}
				// Reset 'age' to be 'now' because the event doesn't become
				// alive until it enters the queueing system.  Otherwise have
				// mess up the 'time to process' counters.
				eventTime = System.currentTimeMillis();
				jvr.handleEvent(JVREvent.this);
			}
		};
		t.setDaemon(true);
		t.start();
		*/
	}

	/**
	 *
	 */
	public String toString() {
		return "JVREvent{target={"+target+"};evttype="+evttype+";data={"+data+"};age="+age()+"}";
	}

}
