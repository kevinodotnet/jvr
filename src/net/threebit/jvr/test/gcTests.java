package net.threebit.jvr.test;

/*
 * $Id: gcTests.java,v 1.32 2004/07/02 18:11:38 kevino Exp $
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

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import junit.framework.*;
import net.threebit.jvr.*;
import net.threebit.jvr.util.*;

/**
 * Due to PTR 26695, you cannot to gc_Start(), gc_OpenEx(), gc_Stop() more than
 * once in a single JVM execution.  Therefore, all of the GC unit tests must be
 * put into a single method.
 */
public class gcTests extends AbstractTest {

	public void testX() { System.exit(1); }

	public void test2_wonk() throws Exception {
		/*

		DX_CAP cap = new DX_CAP();
		cap.ca_nbrdna = 8;
		cap.ca_intflg = dx.DX_PAMDOPTEN;
		cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;
		cap.ca_hedge = 1;

		long linedev = gc.OpenEx(":P_ISDN:N_dtiB1T1",gc.EV_ASYNC,null);
		Thread.sleep(1000);
		int dxDev = dx.open("dxxxB1C1",0);
		dx.listen(dxDev,dt.getxmitslot(gc.GetNetworkH(linedev)));
		dt.listen(gc.GetNetworkH(linedev),dx.getxmitslot(dxDev));

		final ISDNOutbound io = new ISDNOutbound(linedev,"6138672620","5309001111");
		final ImmediateCAP icap = new ImmediateCAP(dxDev,cap,5000);

		JVRMetaEventListener mel = new JVRMetaEventListener() {
			public boolean handleEvent (JVREvent e) { try {
				if (e.target == icap) {
					logger.info("CAP finished");
					jvr.removeEventListener(icap);
					synchronized (this) { notifyAll(); }
				}
				if (e.target == io) {
					// remove the listener.
					jvr.removeEventListener(io);
					if (io.connected) {
						// start CAP
						jvr.addEventListener(icap);
						icap.start();
					}
					else {
						// no answer, busy, whatever, we are done either way.
						synchronized (this) { notifyAll(); }
					}
				}
				return true;
			} catch (Exception ee) { ee.printStackTrace(); } return true; }
		};
		jvr.addEventListener(mel);
		jvr.addEventListener(io);
		jvr.handleEvent(new JVREvent("Not an event"));

		logger.info("start");
		io.start();

		synchronized (mel) {
			logger.info("wait");
			mel.wait(60*1000);
		}
		jvr.removeEventListener(mel);
		*/
	}

	/** Commented out cause it requires multiple start/stop calls. */
	public void test1_Start() throws Exception {
		/*
		GC_CCLIB_STATUSALL status = new GC_CCLIB_STATUSALL();
		GC_START_STRUCT gcss = null;
		// One lib
		gcss = GC_START_STRUCT.newArray(1);
		gcss.cclib_list[0].cclib_name = "GC_ANAPI_LIB";
		gc.Start(gcss);
		gc.CCLibStatusEx ("GC_ALL_LIB",status); // logger.info("1:"+status);
		gc.Stop();
		// Two lib
		gcss = GC_START_STRUCT.newArray(2);
		gcss.cclib_list[0].cclib_name = "GC_ANAPI_LIB";
		gcss.cclib_list[1].cclib_name = "GC_ISDN_LIB";
		gc.Start(gcss);
		gc.CCLibStatusEx ("GC_ALL_LIB",status); // logger.info("2:"+status);
		gc.Stop();
		// Three lib
		gcss = GC_START_STRUCT.newArray(3);
		gcss.cclib_list[0].cclib_name = "GC_ANAPI_LIB";
		gcss.cclib_list[1].cclib_name = "GC_ISDN_LIB";
		gcss.cclib_list[2].cclib_name = "GC_PDKRT_LIB";
		assertTrue(gc.CCLibStatusEx("GC_ISDN_LIB",null) == gc.GC_CCLIB_CONFIGURED);
		gc.Start(gcss);
		assertTrue(gc.CCLibStatusEx("GC_ISDN_LIB",null) == gc.GC_CCLIB_AVAILABLE);
		gc.CCLibStatusEx ("GC_ALL_LIB",status); // logger.info("3:"+status);
		gc.Stop();
		// NULL
		gc.Start(null);
		gc.CCLibStatusEx ("GC_ALL_LIB",status); // logger.info("4:"+status);
		gc.Stop();
		// GC_ALL_LIB
		gcss = new GC_START_STRUCT();
		gcss.num_cclibs = gc.GC_ALL_LIB;
		gc.Start(gcss);
		gc.CCLibStatusEx ("GC_ALL_LIB",status); // logger.info("5:"+status);
		gc.Stop();
		*/
	}

	/**
	 *
	 */
	public void test2_everything() throws Exception {
		gc.Start(null);
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		{
			GC_MAKECALL_BLK makecall = new GC_MAKECALL_BLK();
			makecall.gclib = new GCLIB_MAKECALL_BLK();
			makecall.cclib = new PDK_MAKECALL_BLK();
			/*
			makecall.gclib.destination.address = "16138672620";
			makecall.gclib.destination.address_type = 
			makecall.gclib.origination
			makecall.gclib.chan_info
			makecall.gclib.call_info
			makecall.gclib.ext_data
			*/
		}
		if (true) { return; }
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		/*
		{
		class TEL extends JVRMetaEventListener {
			public boolean done = false;
			public boolean handleEvent (MetaEvent e) throws JVRException {
				if (e.evttype == gc.GCEV_UNBLOCKED) {
					done = true;
					synchronized (this) { notifyAll(); }
				}
				return true;
			}
		}
		TEL tel = new TEL();
		jvr.addEventListener(tel);
		long linedev = gc.OpenEx(":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",gc.EV_SYNC,null);
		synchronized (tel) { tel.wait(5000); }
		jvr.removeEventListener(tel);
		gc.Close(linedev);
		assertTrue(tel.done);
		}
		*/
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		{
			// Inbound test that lets the user enter a number of digits followed by the # sign.
			class TEL extends JVRMetaEventListener {
				public boolean done = false;
				public long crn = -1;
				public DV_DIGIT digBuf = null;
				public boolean handleEvent (MetaEvent e) throws JVRException {
					try {
						if (! e.isGC()) {
							// Look for the termination of "GetDIGIT"
							if (e.evttype == dx.TDX_GETDIG) {
								logger.info("DropCall");
								gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
								logger.info(""+digBuf);
							}
							else {
								logger.info("Unhandled Event: " + e);
								if (crn != 0) {
									logger.info("DropCall");
									gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
								}
							}
							return true;
						}
						if (e.evttype == gc.GCEV_OPENEX) {
							// We expect to see this given the gc_OpenEx() function that was called
							// by the parent class (see below).  However, there isn't really anything
							// to do at this point because we have to wait for the UNBLOCKED event.
							// It's good to know that we should be expecting one now though.
						}
						else if (e.evttype == gc.GCEV_UNBLOCKED) {
							// After gc_OpenEx(), we expect this event to notify us that the
							// LINEDEV is ready for use.  Calling ResetLineDev() assures that
							// the linedev is in the NULL state.  An alarm on the T1 line would
							// not make the OpenEx fail, but would result in the UNBLOCKED 
							// event not arriving.
							logger.info("ResetLineDev()");
							gc.ResetLineDev(e.linedev,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_RESETLINEDEV) {
							// Following a successfull RESET, we are ready to make our first call.
							// All you code readers, that's my phone number, so please change it
							// before running the test for yourself.  Thankfully the area code is 
							// missing. :)
							logger.info("WaitCall()");
							gc.WaitCall(e.linedev,0,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_OFFERED) {
							logger.info("AnswerCall");
							crn = e.crn;
							gc.AnswerCall(e.crn,0,gc.EV_ASYNC);
							/*
							Not supported on my hardware.  So sad.
							logger.info("CallAck()");
							GC_CALLACK_BLK callack = new GC_CALLACK_BLK();
							callack.type = gc.GCACK_SERVICE_INFO;
							callack.info_type = gc.ORIGINATION_ADDRESS; // ANI
							callack.info_len = 10;
							gc.CallAck(e.crn, callack, gc.EV_ASYNC);
							*/
						}
						else if (e.evttype == gc.GCEV_ANSWERED) {
							// int voiceH = gc.GetResourceH(e.linedev,gc.GC_VOICEDEVICE);
							int voiceH = gc.GetVoiceH(e.linedev);
							// issue a "getdigits" on it.
							digBuf = new DV_DIGIT();
							DV_TPT tpt[] = DV_TPT.newArray(2);
							tpt[0].tp_type = dx.IO_CONT;
							tpt[0].tp_termno = dx.DX_MAXDTMF;
							tpt[0].tp_length = 3;
							tpt[0].tp_flags = dx.TF_MAXDTMF;
							tpt[1].tp_type = dx.IO_EOT;
							tpt[1].tp_termno = dx.DX_IDDTIME;
							tpt[1].tp_length = 100;
							tpt[1].tp_flags = dx.TF_MAXSIL;
							logger.info("dx.getdig()");
							dx.getdig(voiceH, tpt, digBuf, dx.EV_ASYNC);
						}
						else if (false) {
							logger.info("DropCall()");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_DISCONNECTED) {
							// When the remote side hangs up (if at all), then a disconnect event will occur.
							// When that happens, we need to drop the call.
							logger.info("DropCall");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_DROPCALL) {
							// Call is dropped; now release all of the call resources.
							logger.info("ReleaseCallEx()");
							gc.ReleaseCallEx(e.crn,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_RELEASECALL) {
							// Use case is finished.  Mark as done (so satisfy assertTrue() below) and exit.
							logger.info("FINISHED SUCCESSFULLY");
							done = true;
							synchronized (this) { notifyAll(); }
						}
						/////////////////////////////
						// Errors, Errors, Errors. //
						/////////////////////////////
						else if (e.evttype == gc.GCEV_OPENEX_FAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else if (e.evttype == gc.GCEV_RELEASECALL_FAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else if (e.evttype == gc.GCEV_TASKFAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else {
							// There's always a chance that you don't code for an event that might occur.
							// It's always good to put in a trailing else{} that just logs that you've
							// missed a condition.  There is no worse bug than "missing" a condition, but
							// never finding out where that is in the code.
							logger.info("Unhandled Event: " + e);
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
					}
					catch (JVRException ex) {
						logger.throwing(getClass().getName(),"handleEvent",ex);
						if (e.crn != 0) {
							logger.info("DropCall");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
					}
					return true;
				}
			}
			TEL tel = new TEL();
			jvr.addEventListener(tel);
			long linedev = gc.OpenEx(":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",gc.EV_ASYNC,null);
			synchronized (tel) { tel.wait(60000); } // 60 seconds for this test is the max.
			jvr.removeEventListener(tel);
			gc.Close(linedev);
			assertTrue(tel.done);
		}
		if (true) { return; }
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		{
			// A straight up inbound call test.  Once the call is received a hangup is issued immediately.
			class TEL extends JVRMetaEventListener {
				public boolean done = false;
				public boolean handleEvent (MetaEvent e) throws JVRException {
					try {
						if (! e.isGC()) {
							// This even is not associated with a global call function.  Skip it.
							return true;
						}
						if (e.evttype == gc.GCEV_OPENEX) {
							// We expect to see this given the gc_OpenEx() function that was called
							// by the parent class (see below).  However, there isn't really anything
							// to do at this point because we have to wait for the UNBLOCKED event.
							// It's good to know that we should be expecting one now though.
						}
						else if (e.evttype == gc.GCEV_UNBLOCKED) {
							// After gc_OpenEx(), we expect this event to notify us that the
							// LINEDEV is ready for use.  Calling ResetLineDev() assures that
							// the linedev is in the NULL state.  An alarm on the T1 line would
							// not make the OpenEx fail, but would result in the UNBLOCKED 
							// event not arriving.
							logger.info("ResetLineDev()");
							gc.ResetLineDev(e.linedev,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_RESETLINEDEV) {
							// Following a successfull RESET, we are ready to make our first call.
							// All you code readers, that's my phone number, so please change it
							// before running the test for yourself.  Thankfully the area code is 
							// missing. :)
							logger.info("WaitCall()");
							gc.WaitCall(e.linedev,0,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_OFFERED) {
							logger.info("AnswerCall");
							gc.AnswerCall(e.crn,0,gc.EV_ASYNC);
							/*
							Not supported on my hardware.  So sad.
							logger.info("CallAck()");
							GC_CALLACK_BLK callack = new GC_CALLACK_BLK();
							callack.type = gc.GCACK_SERVICE_INFO;
							callack.info_type = gc.ORIGINATION_ADDRESS; // ANI
							callack.info_len = 10;
							gc.CallAck(e.crn, callack, gc.EV_ASYNC);
							*/
						}
						else if (e.evttype == gc.GCEV_ANSWERED) {
							// Good to go, the line has been answered and picked up.  In real life we
							// would do something interesting at this point, like connect the network
							// resource to an MSI station, or use the voice resource to do IVR stuff.
							// Instead, let's fast-forward to the hangup stage.
							//
							// First, see if there is anything interesting about the call.  Each one is
							// wrapped in a try/catch because not all information types are supported by
							// all call libraries.
							try { logger.info("Answered CALLNAME: " + gc.GetCallInfo(e.crn,gc.CALLNAME)); } catch (GCException ignore) { }
							try { logger.info("Answered CALLTIME: " + gc.GetCallInfo(e.crn,gc.CALLTIME)); } catch (GCException ignore) { }
							try { logger.info("Answered DESTINATION_ADDRESS: " + gc.GetCallInfo(e.crn,gc.DESTINATION_ADDRESS)); } catch (GCException ignore) { }
							try { logger.info("Answered ORIGINATION_ADDRESS: " + gc.GetCallInfo(e.crn,gc.ORIGINATION_ADDRESS)); } catch (GCException ignore) { }
							logger.info("DropCall()");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_DISCONNECTED) {
							// When the remote side hangs up (if at all), then a disconnect event will occur.
							// When that happens, we need to drop the call.
							logger.info("DropCall");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_DROPCALL) {
							// Call is dropped; now release all of the call resources.
							logger.info("ReleaseCallEx()");
							gc.ReleaseCallEx(e.crn,gc.EV_ASYNC);
						}
						else if (e.evttype == gc.GCEV_RELEASECALL) {
							// Use case is finished.  Mark as done (so satisfy assertTrue() below) and exit.
							logger.info("FINISHED SUCCESSFULLY");
							done = true;
							synchronized (this) { notifyAll(); }
						}
						/////////////////////////////
						// Errors, Errors, Errors. //
						/////////////////////////////
						else if (e.evttype == gc.GCEV_OPENEX_FAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else if (e.evttype == gc.GCEV_RELEASECALL_FAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else if (e.evttype == gc.GCEV_TASKFAIL) {
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
						else {
							// There's always a chance that you don't code for an event that might occur.
							// It's always good to put in a trailing else{} that just logs that you've
							// missed a condition.  There is no worse bug than "missing" a condition, but
							// never finding out where that is in the code.
							logger.info("Unhandled Event: " + e);
							if (e.crn != 0) {
								logger.info("DropCall");
								gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
							}
						}
					}
					catch (JVRException ex) {
						logger.throwing(getClass().getName(),"handleEvent",ex);
						if (e.crn != 0) {
							logger.info("DropCall");
							gc.DropCall(e.crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
						}
					}
					return true;
				}
			}
			TEL tel = new TEL();
			jvr.addEventListener(tel);
			long linedev = gc.OpenEx(":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",gc.EV_ASYNC,null);
			synchronized (tel) { tel.wait(60000); } // 60 seconds for this test is the max.
			jvr.removeEventListener(tel);
			gc.Close(linedev);
			assertTrue(tel.done);
		}
		if (true) { return; }
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		{
		// OUTBOUND CALL TEST
		// Kick off the use case by opening a LINEDEV.  Since this is done ASYNC, control will
		// get to the event listener.
		class TEL extends JVRMetaEventListener {

			public boolean done = false;
			public long crn = -1;
			public boolean handleEvent (MetaEvent e) throws JVRException {

				if (! e.isGC()) {
					// This even is not associated with a global call function.  Skip it.
					return true;
				}
				if (e.evttype == gc.GCEV_OPENEX) {
					// We expect to see this given the gc_OpenEx() function that was called
					// by the parent class (see below).  However, there isn't really anything
					// to do at this point because we have to wait for the UNBLOCKED event.
					// It's good to know that we should be expecting one now though.
				}
				else if (e.evttype == gc.GCEV_UNBLOCKED) {
					// After gc_OpenEx(), we expect this event to notify us that the
					// LINEDEV is ready for use.  Calling ResetLineDev() assures that
					// the linedev is in the NULL state.  An alarm on the T1 line would
					// not make the OpenEx fail, but would result in the UNBLOCKED 
					// event not arriving.
					logger.info("ResetLineDev()");
					gc.ResetLineDev(e.linedev,gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_RESETLINEDEV) {
					// Following a successfull RESET, we are ready to make our first call.
					// All you code readers, that's my phone number, so please change it
					// before running the test for yourself.  Thankfully the area code is 
					// missing. :)
					logger.info("MakeCall()");
					crn = gc.MakeCall(e.linedev, "8672620", null, 0, gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_CALLSTATUS) {
					// This will happen if Call Progress returns that the call was not answered, etc.
					logger.info("DropCall");
					gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_CONNECTED) {
					// The call is now connected.  Query for the CONNECT_TYPE information 
					// ID for this call to see what type of connection happened.
					int ctype = ((Integer)gc.GetCallInfo(crn,gc.CONNECT_TYPE)).intValue();
					if (ctype == gc.GCCT_CAD) { logger.info("Connection Type: GCCT_CAD"); }
					else if (ctype == gc.GCCT_DISCARDED) { logger.info("Connection Type: GCCT_DISCARDED"); }
					else if (ctype == gc.GCCT_FAX1) { logger.info("Connection Type: GCCT_FAX1"); }
					else if (ctype == gc.GCCT_FAX2) { logger.info("Connection Type: GCCT_FAX2"); }
					else if (ctype == gc.GCCT_INPROGRESS) { logger.info("Connection Type: GCCT_INPROGRESS"); }
					else if (ctype == gc.GCCT_NA) { logger.info("Connection Type: GCCT_NA"); }
					else if (ctype == gc.GCCT_PAMD) { logger.info("Connection Type: GCCT_PAMD"); }
					else if (ctype == gc.GCCT_PVD) { logger.info("Connection Type: GCCT_PVD"); }
					else if (ctype == gc.GCCT_UNKNOWN) { logger.info("Connection Type: GCCT_UNKNOWN"); }
					else { logger.info("Connection Type: UNKNOWN("+ctype+")"); }
					// This test does not do anything meaningfull with the call, so start
					// the drop/release cycle right away.
					logger.info("DropCall()");
					gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_DISCONNECTED) {
					// When the remote side hangs up (if at all), then a disconnect event will occur.
					// When that happens, we need to drop the call.
					logger.info("DropCall");
					gc.DropCall(crn,gc.GC_NORMAL_CLEARING,gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_DROPCALL) {
					// Call is dropped; now release all of the call resources.
					logger.info("ReleaseCallEx()");
					gc.ReleaseCallEx(crn,gc.EV_ASYNC);
				}
				else if (e.evttype == gc.GCEV_RELEASECALL) {
					// Use case is finished.  Mark as done (so satisfy assertTrue() below) and exit.
					logger.info("FINISHED SUCCESSFULLY");
					done = true;
					synchronized (this) { notifyAll(); }
				}
				/////////////////////////////
				// Errors, Errors, Errors. //
				/////////////////////////////
				else if (e.evttype == gc.GCEV_OPENEX_FAIL) {
					synchronized (this) { notifyAll(); }
				}
				else if (e.evttype == gc.GCEV_RELEASECALL_FAIL) {
					synchronized (this) { notifyAll(); }
				}
				else if (e.evttype == gc.GCEV_TASKFAIL) {
					synchronized (this) { notifyAll(); }
				}
				else {
					// There's always a chance that you don't code for an event that might occur.
					// It's always good to put in a trailing else{} that just logs that you've
					// missed a condition.  There is no worse bug than "missing" a condition, but
					// never finding out where that is in the code.
					logger.info("Unhandled Event: " + e);
				}
				return true;
			}
		}
		// Create and register our event listener that will drive this ASYNC test.
		TEL tel = new TEL();
		jvr.addEventListener(tel);
		// Open the linedev.  By doing it with EV_ASYNC, control will be passed to our handler.
		long linedev = gc.OpenEx(":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",gc.EV_ASYNC,null);
		synchronized (tel) { tel.wait(60000); } // 60 seconds for this test is the max.
		// 60 seconds has elapsed, or the handler has notified us that it is done.
		jvr.removeEventListener(tel);
		gc.Close(linedev);
		// Assert that the handler had a successfull test.
		assertTrue(tel.done);
		}
		//////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		gc.Stop();
	}

}
