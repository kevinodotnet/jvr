package net.threebit.jvr.util;

/*
 * $Id: GCOutbound.java,v 1.19 2004/12/12 00:12:13 kevino Exp $
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
 *
 */

public class GCOutbound extends JVRMetaEventListener {

	/**
	 *
	 */
	GC_PARM_BLK setConfigParmBlk = new GC_PARM_BLK();

	/** CAP processing. */
	public ImmediateCAP ic = null;

	/** Global Call protocol that the device was opened with. */
	public String protocol = null;

	/** Voice device handle used for dx.dial() calls. */
	public int dxdev;

	/** Line device used for dialing. */
	public long linedev;

	/** Called party number. */
	public String destination;

	/** Calling party number. */
	public String origin;

	/** Call Reference Number if available. */
	public long crn = -1;

	/** Set to true when this class is finished. */
	public boolean finished = false;

	/** Saved exception if one happens during dialing. */
	public Throwable error = null;

	/** True if the call becomes connected. */
	public boolean connected = false;

	/** The type of connection that was made (if any). */
	public int connectType = -1;

	/** 
	 * If the call is not connected, this is the value of event.gcInfo.gcValue
	 * that was captured at the time of the GCEV_DISCONNECTED event.
	 */
	public int disconnectCause = -1;

	/**
	 *
	 */
	public int pamdDelay = 1500;

	/**
	 *
	 */
	public DX_CAP cap = new DX_CAP();

	/**
	 *
	 */
	public int makeCallTimeout = 60;

	/**
	 * Constructs a GCOutbound object that will drive a call to the specified number.
	 * @param dxdev A voice device handle to use.
	 * @param linedev A valid Global Call line device handle.
	 * @param destination The phone number to connect to.
	 * @param origin The phone number that is provided for caller ID (the number *doing* the calling).
	 */
	public GCOutbound (int dxdev, long linedev, String destination, String origin) throws JVRException {
		this.dxdev = dxdev;
		this.linedev = linedev;
		this.destination = destination;
		this.origin = origin;

		cap.ca_nbrdna = 8;
		cap.ca_intflg = dx.DX_PAMDOPTEN;
		cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;
		cap.ca_hedge = 1;
	}

	/**
	 * After initialization, call this method to start dialing.  This method
	 * calls {@link gc#ResetLineDev} to make certain that the line device
	 * is NULL/IDLE.  It also primes the event handling mechanisms.
	 */
	public void start() throws JVRException {
		/*
		if (protocol != null && (!protocol.equals("ISDN"))) {
			gc.SetParm(linedev,gc.GCPR_MEDIADETECT,new GC_PARM(gc.GCPV_ENABLE));
		}
		*/
		if (protocol != null && (!protocol.equals("ISDN"))) {
			gc.SetCallProgressParm(linedev,cap);
		}
		gc.ResetLineDev(linedev,gc.EV_ASYNC);
	}

	public boolean handleEvent (JVREvent e) throws JVRException {
		if (finished) { return true; }
		if (e.target == ic) {
			jvr.removeEventListener(ic);
			if (ic.connected) {
				finished(); 
			}
			else {
				// IC is done, but the line is not connected - we are responsible for hanging up.
				gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
			}
		}
		return true;
	}

	public boolean handleEvent (MetaEvent e) throws JVRException {
		if (finished) { return true; }
		if (! e.isGC()) { return true; }
		if (e.linedev != linedev) { return true; }
		try {
			if (e.evttype == gc.GCEV_RESETLINEDEV) { 
				if (protocol != null && protocol.equals("ISDN")) {
					// Configure the linedev for G.711 Mu-law Layer 1 Protocol.  This may
					// not apply in all cases, but I don't feel like taking the time to
					// *not* hardcode it at the moment.
					gc.util_insert_parm_val(
						setConfigParmBlk,
						gc.GCSET_CHAN_CAPABILITY,
						gc.GCPARM_CAPABILITY,
						4, // size of an integer; needs work here - Java programmers should not need to know data sizes.
						gc.GCCAP_AUDIO_g711Ulaw56k
					);
					gc.SetConfigData(gc.GCTGT_CCLIB_CHAN,e.linedev,setConfigParmBlk,gc.EV_ASYNC,gc.GCUPDATE_IMMEDIATE,gc.EV_ASYNC);
				}
				else {
					// Go ahead and start the call.
					// TODO: make 30 second timeout configurable.
					crn = gc.MakeCall(e.linedev, destination, null, makeCallTimeout, gc.EV_ASYNC);
				}
			}
			else if (e.evttype == gc.GCEV_SETCONFIGDATA) {
				// Only set the calling party number when protocol is "ISDN".
				if (protocol != null && protocol.equals("ISDN")) {
					gc.SetCallingNum(e.linedev, origin);
				}
				// Timeout value has no meaning for ISDN calls.
				crn = gc.MakeCall(e.linedev, destination, null, 0, gc.EV_ASYNC);
			}
			else if (e.evttype == gc.GCEV_PROCEEDING) {
				if (protocol != null && protocol.equals("ISDN")) {
					// TODO: make configurable.
					// Only to CAP when running as ISDN.
					dx.listen(dxdev, dt.getxmitslot(gc.GetNetworkH(e.linedev)));
					dt.listen(gc.GetNetworkH(e.linedev), dx.getxmitslot(dxdev));
					ic = new ImmediateCAP(dxdev,cap,pamdDelay);
					jvr.addEventListener(ic);
					ic.start();
				}
			}
			else if (e.evttype == gc.GCEV_ALERTING) {
				// Nothing to do here.
			}
			else if (e.evttype == gc.GCEV_PROGRESSING) {
				// Nothing to do here.
			}
			else if (e.evttype == gc.GCEV_CALLSTATUS) {
				// Timeout or no answer
				// if (ic != null) { dx.stopch(dxdev,dx.EV_ASYNC); }
				connected = false;
				gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
			}
			else if (e.evttype == gc.GCEV_CONNECTED) {

				// For FXS and Analog protocols, check for "CONNECTED" but not really connected conditions.
				// If they are met, then drop the call.
				if (protocol == null || (!protocol.equals("ISDN"))) {
					if (e.gcInfo.gcValue == gc.GCRV_NORB || e.gcInfo.gcValue == gc.GCRV_DIALTONE) {
						// logger.info("Connected, but not really connected: " + jvr.symbolName("GCRV_",e.gcInfo.gcValue));
						disconnectCause = e.gcInfo.gcValue;
						gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						return true;
					}
				}

				// The call is "really" connected.
				connected = true;
				connectType = ((Integer)gc.GetCallInfo(crn,gc.CONNECT_TYPE)).intValue();
				// When not running as ISDN, start ImmediateCAP to exhaust the pamdDelay
				// if this is a PAMD media detection.
				if (protocol == null || (!protocol.equals("ISDN"))) {
					if (connectType == gc.GCCT_PAMD) {
						// logger.info("Other");
						ic = new ImmediateCAP(dxdev,cap,pamdDelay);
						ic.jumpToPamdDelay = true;
						jvr.addEventListener(ic);
						ic.start();
					}
					else {
						// Not PAMD, so we are finished.
						finished();
					}
				}
			}
			else if (e.evttype == gc.GCEV_DISCONNECTED) {
				if (connected) { connected = false; /*logger.info("changing connect status to false");*/ }
				if (ic != null) { dx.stopch(dxdev,dx.EV_ASYNC); }
				// When the remote side hangs up (if at all), then a disconnect event will occur.
				// When that happens, we need to drop the call.  This is here because a caller
				// may drop while we are doing Call Analysis.
				//
				// OR
				//
				// Call was never completed in the first place (USER_BUSY, etc).
				//
				// Remember the gcValue.
				disconnectCause = e.gcInfo.gcValue;
				gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
			}
			else if (e.evttype == gc.GCEV_DROPCALL) {
				// Call is dropped; now release all of the call resources.
				connected = false;
				gc.ReleaseCallEx(crn,gc.EV_ASYNC);
			}
			else if (e.evttype == gc.GCEV_RELEASECALL) {
				// Use case is finished.  Mark as done (so satisfy assertTrue() below) and exit.
				connected = false;
				finished();
			}
			/////////////////////////////
			// Errors, Errors, Errors. //
			/////////////////////////////
			else if (e.evttype == gc.GCEV_SETCONFIGDATA_FAIL) { 
				failed(e); 
			}
			else if (e.evttype == gc.GCEV_RESTARTFAIL) { 
				failed(e); 
			}
			else if (e.evttype == gc.GCEV_RELEASECALL_FAIL) { 
				failed(e); 
			}
			else if (e.evttype == gc.GCEV_TASKFAIL) { 
				failed(e); 
			}
			// else { logger.info("!!!!! NOT HANDLED !!!!!"); } // just for testing.
			return true;
		}
		catch (JVRException ex) {
			// We should not receive exceptions while handling events.
			// Default policy is to hang up any calls that are present.
			// Since we are inside the event handler there is no reason
			// to rethrow the exception - just mark ourselves as finished.
			logger.info("Exception while handling event: " + e);
			logger.throwing(getClass().getName(),"handleEvent",ex);
			if (crn != -1) {
				try {
					// TODO: don't use 'NORMAL_CLEARING'
					gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_SYNC);
				}
				catch (JVRException ignore) { logger.throwing(getClass().getName(),"handleEvent",ignore); }
				try {
					gc.ReleaseCallEx(crn,gc.EV_SYNC);
				}
				catch (JVRException ignore) { logger.throwing(getClass().getName(),"handleEvent",ignore); }
			}
			if (linedev != -1) {
				try {
					gc.ResetLineDev(linedev,gc.EV_SYNC);
				}
				catch (JVRException ignore) { logger.throwing(getClass().getName(),"handleEvent",ignore); }
				try {
					gc.WaitCall(linedev,0,gc.EV_ASYNC);
				}
				catch (JVRException ignore) { logger.throwing(getClass().getName(),"handleEvent",ignore); }
			}
			if (error == null) { error = ex; }
			else {
				logger.throwing(getClass().getName(),"handleEvent",ex); 
			}
			finished();
		}
		return true;
	}

	/**
	 *
	 */
	public void failed (Object o) {
		Throwable newError = null;
		if (o instanceof Throwable) {
			newError = new JVRException("Error during processing.",(Throwable)o);
		}
		else {
			newError = new JVRException("Error during processing: " + o);
		}
		if (error == null) { error = newError; }
		else {
			// Log the latest error but keep the original one.
			logger.throwing(getClass().getName(),"failed",newError);
		}
		finished();
	}

	/**
	 * Called when the class decides that it is no longer responsible
	 * for the line device and/or call reference number.
	 */
	public void finished() {
		// Mark ourselves as finished.
		if (finished) { return; }
		finished = true;
		if (ic != null) {
			jvr.removeEventListener(ic);
			try {
				dx.stopch(dxdev,dx.EV_ASYNC);
			}
			catch (JVRException e) { logger.throwing(getClass().getName(),"finished",e); }
		}
		// Notify any "waiters" that we are done.
		synchronized (this) { notifyAll(); }
		// Fire an event for asynchronous JVR event listeners.
		new JVREvent(this,"finished").fire();
		// For debugging only.
		// logger.info("(FINISHED)\n" + this);
	}

	/**
	 * Synchronous function.
	 * 
	 * @param dxdev Voice device handle to use when doing CAP.
	 * @param linedev Global call line device handle to use to make the call.
	 * @param destination Destination phone number.
	 * @param origin Origin (calling party number).
	 * @param timeout Time in milliseconds before the call is aborted.
	 * @param protocol The Global Call protocol that was used to open the line device.
	 * @return The instance of GCOutbound that was used to make the call.
	 */
	public static GCOutbound makeCall (final int dxdev, final long linedev, 
		final String destination, final String origin, final long timeout, final DX_CAP cap, final String protocol) throws JVRException {

		//
		// Class that listens for events on an instance of GCOutbound.
		// ASYNC/SYNC bridge.
		//
		class MyEventListener extends JVRMetaEventListener {
			/** Instance of GCOutbound that does that actual calling. */
			public GCOutbound gcob = null;
			/** True if the listener finishes normally. */
			public boolean finished = false;
			/** Handle events on "gcob". */
			public boolean handleEvent (JVREvent event) {
				try {
					if (event.target == this) {
						// This is the signal to begin the call.
						gcob = new GCOutbound(dxdev,linedev,destination,origin);
						gcob.protocol = protocol;
						if (cap != null) { gcob.cap = cap; }
						jvr.addEventListener(gcob);
						gcob.start();
					}
					else if (event.target == gcob) {
						// Is this the "finished" event?
						if ("finished".equals(event.data)) {
							// logger.info("Connect Type: " + jvr.symbolName("GCCT_",gcob.connectType));
							finished = true;
							jvr.removeEventListener(gcob);
							synchronized (this) { notifyAll(); }
						}
					}
				}
				catch (Throwable t) {
					jvr.removeEventListener(gcob);
					logger.throwing(getClass().getName(),"handleEvent",t);
				}
				return true;
			}
		}

		MyEventListener my = new MyEventListener();
		jvr.addEventListener(my);
		synchronized (my) {
			try {
				new JVREvent(my).fire();
				if (timeout <= 0) {
					my.wait(); 
				}
				else {
					my.wait(timeout); 
				}
			}
			catch (InterruptedException ignore) { }
		}
		jvr.removeEventListener(my);

		if (! my.finished) {
			// TODO: The GCOutbound instance may still be running. Perhaps the call exists
			// now.  Unfortunately the CRN is lost and there is no way to hang up the line!
			throw new JVRException("Timeout expired waiting for GCOutbound to finish.");
		}

		GCOutbound gcob = my.gcob;
		if (gcob.error != null) { throw new JVRException("GCOutbound raised an exception",gcob.error); }
		if (! gcob.connected) {
			// Globalcall never connected.  If CAP finished then use it as the exception detail.
			if (gcob.ic != null && gcob.ic.finished) {
				throw new JVRException("GCOutbound did not connect.  CAP resulted in " + jvr.symbolName("CR_",my.gcob.ic.cpTerm));
			}
			// CAP did not finish, so use the Globalcall disconnect cause.
			throw new JVRException("Not connected: " +jvr.symbolName("GCRV_",my.gcob.disconnectCause));
		}

		return gcob;
	}

	/**
	 *
	 */
	public String toString() {
		return 
			"GCOutbound{"+
			"linedev="+linedev+";"+
			"dxdev="+dxdev+";"+
			"crn="+crn+";"+
			"connected="+connected+";"+
			"destination="+destination+";"+
			"origin="+origin+";"+
			"protocol="+protocol+";"+
			"finished="+finished+";"+
			"connectType="+connectType+";"+
			"disconnectCause="+disconnectCause+";"+
			"ic="+ic+""+
			"}";
	}

}
