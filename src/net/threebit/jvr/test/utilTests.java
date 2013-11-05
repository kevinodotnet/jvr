package net.threebit.jvr.test;

/*
 * $Id: utilTests.java,v 1.16 2005/01/26 01:34:05 kevino Exp $
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
 * For testing net.threebit.jvr.util.* classes.
 */
public class utilTests extends AbstractTest {

	class Skip {
	public void test_notest() {
	}


	public void test_jvrevents_1() throws Throwable {
		logger.info("Hook: " + jvr.symbolName("ERROR",0));
		try { new JVREvent("1").fire(-1); } catch (Throwable ignore) { }
		new JVREvent("5000").fire(5000);
		Thread.sleep(2000);
		new JVREvent("1000").fire(1000);
		// new JVREvent("2").fire(2000);
		// new JVREvent("3").fire(3000);
		// new JVREvent("0").fire(500);
		Thread.sleep(10000000);
	}

	int dxdev = -1;
	long ld1 = -1;
	long ld2 = -1;
	String cell = "6138672620";
	String home = "6137296268";
	// String fax = "6138225215";
	String fax = "6132360804";
	String origin = "8005551212";
	String notInService = "2019868659";
	String disc = notInService;

	// public void testX() { System.exit(1); }

	public void test_gcoutbound_voice_fxs_t1() throws Throwable {

		int times = 50;
		times = 1;
		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",gc.EV_ASYNC,null);
		Thread.sleep(1000);

		/*
		long monld = gc.OpenEx(":P_ISDN:N_dtiB4T3",gc.EV_ASYNC,null);
		GCOutbound mongcob = GCOutbound.makeCall(dxdev,monld,cell,origin,0,null); 
		dt.listen(gc.GetNetworkH(monld), dt.getxmitslot(gc.GetNetworkH(ld1)));
		*/

		for (int x = 0; x < times; x++) {
			logger.info("/////////////////////////////////////////////////////////");
			logger.info("////// FXS_IO Voice " + (x+1) + " of " + times + " //////");
			logger.info("/////////////////////////////////////////////////////////");
			GCOutbound gcob = null;
			try { 
				gcob = GCOutbound.makeCall(dxdev,ld1,","+cell,origin,0,null,"pdk_us_ls_fxs_io"); 
			}
			catch (JVRException e) { 
				// Must get back CR_CEPT
				if (e.getMessage().indexOf("GCRV_BUSY") == -1) { logger.throwing("","",e); }
			}
			// try { gcUtil.hangup(ld1,gcob.crn,0); }
			// catch (JVRException e) { logger.throwing("","",e); }
		}

		dx.close(dxdev);
		gc.Close(ld1);
	}


	public void test_metaevent_eventtime () throws Throwable {
		JVRMetaEventListener tel = new JVRMetaEventListener() {
			public boolean handleEvent (JVREvent e) {
				if (e.target == this && "done".equals(e.data)) {
					synchronized (this) { notifyAll(); }
				}
				return true;
			}
		};
		jvr.addEventListener(tel);
		synchronized (tel) {
			new JVREvent(tel).fire(); 
			new JVREvent(tel).fire(); 
			new JVREvent(tel).fire(); 
			new JVREvent(tel,"done").fire(); 
			tel.wait();
		}
		jvr.removeEventListener(tel);
	}


	public void test_gcoutbound_fax() throws Throwable {
		if (true) { return; } // fax stuff seems broken.  Always returns 'connect' states (PVD,CAD,etc).

		int times = 50;
		times = 1;
		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_ISDN:N_dtiB4T1",gc.EV_ASYNC,null);

		long monld = gc.OpenEx(":P_ISDN:N_dtiB4T3",gc.EV_ASYNC,null);
		GCOutbound mongcob = GCOutbound.makeCall(dxdev,monld,cell,origin,0,null,"ISDN"); 
		dt.listen(gc.GetNetworkH(monld), dt.getxmitslot(gc.GetNetworkH(ld1)));

		for (int x = 0; x < times; x++) {
			logger.info("/////////////////////////////////////////////////");
			logger.info("////// FAX " + (x+1) + " of " + times + " //////");
			logger.info("/////////////////////////////////////////////////");
			GCOutbound gcob = null;
			try { 
				gcob = GCOutbound.makeCall(dxdev,ld1,fax,origin,0,null,"ISDN"); 
			}
			catch (JVRException e) { 
				// Must get back CR_CEPT
				if (e.getMessage().indexOf("GCRV_BUSY") == -1) { logger.throwing("","",e); }
			}
			// try { gcUtil.hangup(ld1,gcob.crn,0); }
			// catch (JVRException e) { logger.throwing("","",e); }
		}
		gcUtil.hangup(monld,mongcob.crn,0);
		dx.close(dxdev);
		gc.Close(ld1);
		gc.Close(monld);
	}

	public void test_gcoutbound_busy() throws Throwable {
		int times = 50;
		times = 1;
		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_ISDN:N_dtiB4T1",gc.EV_ASYNC,null);
		for (int x = 0; x < times; x++) {
			logger.info("/////////////////////////////////////////////////");
			logger.info("////// BUSY " + (x+1) + " of " + times + " //////");
			logger.info("/////////////////////////////////////////////////");
			GCOutbound gcob = null;
			try { 
				DX_CAP cap = new DX_CAP();
				cap.ca_nbrdna = 1;
				cap.ca_intflg = dx.DX_PAMDOPTEN;
				cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;
				cap.ca_hedge = 1;
				gcob = GCOutbound.makeCall(dxdev,ld1,home,origin,0,cap,"ISDN"); 
			}
			catch (JVRException e) { 
				// Must get back CR_CEPT
				if (e.getMessage().indexOf("GCRV_BUSY") == -1) { logger.throwing("","",e); }
			}
			// try { gcUtil.hangup(ld1,gcob.crn,0); }
			// catch (JVRException e) { logger.throwing("","",e); }
		}
		dx.close(dxdev);
		gc.Close(ld1);
	}

	public void test_gcoutbound_noanswer() throws Throwable {
		int times = 50;
		times = 1;
		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_ISDN:N_dtiB4T1",gc.EV_ASYNC,null);
		for (int x = 0; x < times; x++) {
			logger.info("///////////////////////////////////////////////////////////");
			logger.info("////// NO ANSWER Call " + (x+1) + " of " + times + " //////");
			logger.info("///////////////////////////////////////////////////////////");
			if (x == 0) { Thread.sleep(2000); }
			GCOutbound gcob = null;
			try { 
				DX_CAP cap = new DX_CAP();
				cap.ca_nbrdna = 1;
				cap.ca_intflg = dx.DX_PAMDOPTEN;
				cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;
				cap.ca_hedge = 1;
				gcob = GCOutbound.makeCall(dxdev,ld1,home,origin,0,cap,"ISDN"); 
			}
			catch (JVRException e) { 
				// Must get back CR_CEPT
				if (e.getMessage().indexOf("CR_NOANS") == -1) { logger.throwing("","",e); }
			}
			// try { gcUtil.hangup(ld1,gcob.crn,0); }
			// catch (JVRException e) { logger.throwing("","",e); }
		}
		dx.close(dxdev);
		gc.Close(ld1);
	}

	public void test_gcoutbound_cept() throws Throwable {
		int times = 50;
		times = 1;
		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_ISDN:N_dtiB4T1",gc.EV_ASYNC,null);
		for (int x = 0; x < times; x++) {
			logger.info("//////////////////////////////////////////////////////");
			logger.info("////// CEPT Call " + (x+1) + " of " + times + " //////");
			logger.info("//////////////////////////////////////////////////////");
			GCOutbound gcob = null;
			try { gcob = GCOutbound.makeCall(dxdev,ld1,notInService,origin,0,null,"ISDN"); }
			catch (JVRException e) { 
				// Must get back CR_CEPT
				if (e.getMessage().indexOf("CR_CEPT") == -1) { logger.throwing("","",e); }
			}
			// try { gcUtil.hangup(ld1,gcob.crn,0); }
			// catch (JVRException e) { logger.throwing("","",e); }
		}
		dx.close(dxdev);
		gc.Close(ld1);
	}

	public void test_gcoutbound_2_dead() throws Throwable {
		/*
		int dxdev;
		long ld1;
		long ld2;
		long crn1 = -1;
		long crn2 = -1;

		dxdev = dx.open("dxxxB1C1",0);
		ld1 = gc.OpenEx(":P_ISDN:N_dtiB4T22",gc.EV_ASYNC,null);
		ld2 = gc.OpenEx(":P_ISDN:N_dtiB4T23",gc.EV_ASYNC,null);
		// crn2 = GCOutbound.makeCall(dxdev,ld2,"6138672620","6138743333",0);
		dt.listen(gc.GetNetworkH(ld2), dt.getxmitslot(gc.GetNetworkH(ld2)));
		try {
			crn1 = GCOutbound.makeCall(dxdev,ld1,"6138672620","6138743333",0);
		}
		catch (JVRException e) { logger.throwing("","",e); }
		if (crn1 != -1) { gcUtil.hangup(ld1,crn1,60000); }
		if (crn2 != -1) { gcUtil.hangup(ld2,crn2,60000); }
		gc.Close(ld1);
		gc.Close(ld2);

		// dt.listen(gc.GetNetworkH(ld2), dt.getxmitslot(gc.GetNetworkH(ld1)));
		*/
	}

	public void test_gcoutbound_1_dead() throws Throwable {
		/*
		if ("1".equals("1")) { return; }

		int dxdev = dx.open("dxxxB1C1",0);
		long linedev = gc.OpenEx(":P_ISDN:N_dtiB4T13",gc.EV_ASYNC,null);
		long mon_linedev = gc.OpenEx(":P_ISDN:N_dtiB4T11",gc.EV_ASYNC,null);
		// long mon_crn = GCOutbound.makeCall(dxdev,mon_linedev,"6137296268","6138743333",0);
		long crn = -1;

		// dt.listen(gc.GetNetworkH(mon_linedev), dt.getxmitslot(gc.GetNetworkH(linedev)));
		// dt.listen(gc.GetNetworkH(linedev), dt.getxmitslot(gc.GetNetworkH(mon_linedev)));

		logger.info("-------------------------------");
		logger.info("NOT IN SERVICE");
		logger.info("-------------------------------");
		try {
			// crn = GCOutbound.makeCall(dxdev,linedev,"2019868659",origin,0);
			crn = GCOutbound.makeCall(dxdev,linedev,"6138672620",origin,0);
		} catch (JVRException ignore) {
			logger.throwing("","",ignore);
		}
		if (crn != -1) { gcUtil.hangup(linedev,crn,60000); }

		if ("1".equals("1")) { return; }

		// ISDN just returns 'connected' for fax lines.  Makes sense.
		logger.info("-------------------------------");
		logger.info("FAX");
		logger.info("-------------------------------");
		Thread.sleep(3000);
		logger.info("Making call");
		try {
			crn = gcoutbound.makeCall(linedev,fax,origin,0);
		} catch (JVRException ignore) { }
		logger.info("Hanging up");
		gcUtil.hangup(linedev,crn,60000);
		logger.info("Hungup");

		logger.info("-------------------------------");
		logger.info("NO ANSWER");
		logger.info("-------------------------------");
		Thread.sleep(3000);
		logger.info("Making call");
		try {
			crn = GCOutbound.makeCall(dxdev,linedev,home,origin,0);
			assertTrue(false); // should have had an exception.
		} catch (JVRException ignore) { }
		logger.info("Hanging up");
		gcUtil.hangup(linedev,crn,60000);
		logger.info("Hungup");

		logger.info("-------------------------------");
		logger.info("BUSY");
		logger.info("-------------------------------");
		Thread.sleep(3000);
		logger.info("Making call");
		try {
			crn = GCOutbound.makeCall(dxdev,linedev,home,origin,0);
			assertTrue(false); // should have had an exception.
		} catch (JVRException ignore) { }
		logger.info("Hanging up");
		gcUtil.hangup(linedev,crn,10000);
		logger.info("Hungup");

		logger.info("-------------------------------");
		logger.info("Answer this call");
		logger.info("-------------------------------");
		Thread.sleep(3000);
		logger.info("Making call");
		crn = GCOutbound.makeCall(dxdev,linedev,cell,origin,0);
		logger.info("Hanging up");
		gcUtil.hangup(linedev,crn,10000);
		logger.info("Hungup");

		gc.Close(linedev);
		*/
	}
	}

}

