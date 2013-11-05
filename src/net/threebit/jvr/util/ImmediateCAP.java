package net.threebit.jvr.util;

/*
 * $Id: ImmediateCAP.java,v 1.10 2004/07/02 18:11:38 kevino Exp $
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
 * Use this class to begin immediate Call Progress and Analysis
 * on a voice resource by dialing an empty string with CAP enabled.
 * Usefull as a followup to ISDNOutbound to determine what type of
 * connection was made.
 */

public class ImmediateCAP extends JVRMetaEventListener {

	public boolean connected = false;

	/**
	 * When true at start time, we have been asked to just 
	 * wait for the pamd silence to elapse. Hack!
	 */
	public boolean jumpToPamdDelay = false;

	/** Voice resource. */
	private int dxDev = -1;

	/** DX_CAP settings used during dialing. */
	private DX_CAP cap = null;

	/** Set to true until this class is ready to start processing. */
	private boolean notReady = true;

	/** 
	 * Set to true when this class is finished handling requests.
	 * Necessary because the class may still be registered as an
	 * event listener.
	 */
	public boolean finished = false;

	/**
	 *
	 */
	public boolean stopped = false;

	/** Saved exception if one happens during dialing. */
	private Throwable failureException = null;

	/** dx.dial termination cause. */
	public long cpTerm = -1;

	/** type of connection. */
	public long connType = -1;

	/** Milliseconds of silence after dx.dial() returns CON_PAMD. */
	private int pamdDelay = 0;
	private long pamdDelayStarted = 0;

	/** True when silence is on. */
	private boolean silon = false;

	/** Time when silence started. */
	private long siltime = -1;

	/**
	 * @param dxDev The voice resource to use for call analysis.
	 * @param cap Call Analysis parameters to pass to {@link dx#dial}.
	 * @param pamdDelay If PAMD is detected, this class will wait until
	 * 	this many milliseconds of silence has happenen before reporting
	 * 	PAMD.  Ie: wait for the answering machine message to finish
	 * 	playing.
	 */
	public ImmediateCAP (int dxDev, DX_CAP cap, int pamdDelay) {
		this.dxDev = dxDev;
		this.cap = cap;
		this.pamdDelay = pamdDelay;
	}

	/**
	 * After initialization, call this method to start dialing/cap.
	 */
	public void start() throws JVRException {
		notReady = false;
		if (jumpToPamdDelay) {
			// implied state.
			cpTerm = dx.CR_CNCT;
			connType = dx.CON_PAMD;
			connected = true;
			startPamdDelayThread();
		}
		else {
			dx.dial(dxDev,"",cap,dx.EV_ASYNC|dx.DX_CALLP);
		}
	}

	/**
	 *
	 */
	private void startPamdDelayThread () throws JVRException {
		// start the silence detection.
		dx.setevtmsk(dxDev,dx.DM_SILOF|dx.DM_SILON);
		pamdDelayStarted = System.currentTimeMillis();
		Thread t = new Thread() {
			public void run() {
				try {
					while (true) {
						synchronized (ImmediateCAP.this) {
							if (finished) { return; }
							// TODO: make the maximum amount of time configurable.
							if ((System.currentTimeMillis()-pamdDelayStarted) >= (120*1000)) {
								logger.info("WARNING: running for too long during PAMD silence delay.");
								finished();
								return;
							}
							if (silon) {
								if ((System.currentTimeMillis()-siltime) >= pamdDelay) {
									// The required amount of silence has been satisfied.
									finished();
									return;
								}
								logger.info("pamdDelay: " + pamdDelay + "; need another " + (System.currentTimeMillis()-siltime));
							}
						}
						try {
							sleep(200);
						} catch (Exception ignore) { }
					}
				}
				catch (Exception e) {
					logger.throwing(getClass().getName(),"run",e);
				}
			}
		};
		t.start();
	}

	/**
	 * Handler for events on the linedev.  All other events are ignored.  All events
	 * are ignored until this components {@link #start} method is called.
	 */
	public boolean handleEvent (MetaEvent e) throws JVRException {
		synchronized (this) {
			if (notReady) { return true; }
			if (finished) { return true; }
			if (e.isGC()) { return true; }
			if (e.evtdev != dxDev) { return true; }
			try {
				if (e.evttype == dx.TDX_CST) {
					DX_CST cst = (DX_CST) e.evtdata;
					if (cst.cst_event == dx.DE_SILOFF) {
						silon = false;
					}
					else if (cst.cst_event == dx.DE_SILON) {
						silon = true;
						siltime = System.currentTimeMillis();
					}
					return true;
				}
				if (e.evttype == dx.TDX_CALLP) {
					// Call analysis has finished.
					cpTerm = dx.ATDX_CPTERM((int)e.evtdev);
					if (cpTerm == dx.CR_CNCT) {
						connected = true;
						connType = dx.ATDX_CONNTYPE((int)e.evtdev);
						logger.info("Connected due to: " + jvr.symbolName("CON_",connType));
						if (connType == dx.CON_PAMD) {
							// Started the thread that will wait for the configured amount
							// of silence to elapse before returning CON_PAMD.  This is required
							// because CON_PAMD can be reported before the end of the answering
							// machine greeting.
						 	logger.info("CON_PAMD");
							startPamdDelayThread();
							return true;
					 	}
					}
					else {
						logger.info("Not connected due to: " + jvr.symbolName("CR_",cpTerm));
						if (cpTerm == dx.CR_STOPD) {
							// Remember that call analysis did not completed.
							stopped = true;
						}
					}
					finished();
					return true;
				}
			}
			catch (JVRException ex) {
				failureException = ex;
				logger.throwing(getClass().getName(),"handleEvent",ex);
				finished();
			}
		}
		return true;
	}

	/**
	 * Called when the class decides that it is no longer responsible
	 * for the line device and/or call reference number.
	 */
	public void finished() throws JVRException {
		// Mark ourselves as finished.
		if (finished) { logger.info("Called finished() twice.  Ignoring second request."); return; }
		finished = true;
		// SILON / SILOFF seems to have the side effect of
		// cancelling the dx_playiottdata(ASYNC) function.
		// Ergo, "unset" all events.
		dx.setevtmsk(dxDev,dx.DM_DIGITS);
		dx.setevtmsk(dxDev,dx.DM_DIGOFF);
		// dx.setevtmsk(dxDev,dx.DM_SILOF|dx.DM_SILON);
		// Notify any "waiters" that we are done.
		synchronized (this) { notifyAll(); }
		// Fire an event for asynchronous JVR event listeners.
		new JVREvent(this).fire();
		// For debugging only.
		logger.info("(FINISHED)\n" + this);
	}

	/**
	 *
	 */
	public String toString() {
		return "ImmediateCAP{"+
			"finished="+finished+";"+
			"connected="+connected+";"+
			"stopped="+stopped+";"+
			"cpTerm="+cpTerm+";"+
			"connType="+connType+";"+
			"jumpToPamdDelay="+jumpToPamdDelay+";"+
			"dxDev="+dxDev+";"+
			"notReady="+notReady+";"+
			"pamdDelay="+pamdDelay+";"+
			"pamdDelayStarted="+pamdDelayStarted+";"+
			"silon="+silon+";"+
			"siltime="+siltime+""+
			"}";
	}

}
