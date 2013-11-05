package net.threebit.jvr.test;

/*
 * $Id: msTests.java,v 1.17 2005/01/26 01:34:05 kevino Exp $
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

public class msTests extends AbstractTest {

	public void test_newfunctions() throws Exception {
		int b = ms.open("msiB1",0);
		logger.info("DSP Count: " + ms.dsprescount(b));
		logger.info("Disabling ZIP");
		ms.setbrdparm(b,ms.MSG_ZIPENA,new Integer(ms.MS_ZIPDISABLE));
		logger.info("DSP Count: " + ms.dsprescount(b));
		logger.info("Enabling ZIP");
		ms.setbrdparm(b,ms.MSG_ZIPENA,new Integer(ms.MS_ZIPENABLE));
		logger.info("DSP Count: " + ms.dsprescount(b));
		ms.close(b);
	}

	/*
	public void test1_openclose_setmsk() throws Exception {
		int dev = ms.open("msiB1C1",0);
		assertTrue(dev > 0);
		ms.setevtmsk(dev,ms.DTG_SIGEVT,ms.MSMM_OFFHOOK|ms.MSMM_ONHOOK|ms.MSMM_HOOKFLASH,ms.DTA_SETMSK);
		try { Thread.sleep(500); } catch (Exception ignore) { }
		ms.close(dev);
	}

	public void test3_getxmitslot_listen() throws Exception {
		int d1 = ms.open("msiB1C1",0);
		int d2 = ms.open("msiB1C2",0);
		ms.unlisten(d1);
		ms.unlisten(d2);
		SC_TSINFO tsinfo1 = ms.getxmitslot(d1);
		SC_TSINFO tsinfo2 = ms.getxmitslot(d2);
		ms.listen(d1,tsinfo2);
		ms.listen(d2,tsinfo1);
		ms.close(d1);
		ms.close(d2);
	}

	// This test has been commented out because it's really annoying to
	// have your MSI station ring all the time when you're trying to test
	// other stuff.
	public void test4_genring() throws Exception {
		final int msDev = ms.open("msiB1C1",0);
		class TEL extends JVREventListener {
			public boolean handleEvent (Event e) throws JVRException {
				if (e.device == msDev) { synchronized (this) { notifyAll(); } }
				return true;
			}
		}
		TEL tel = new TEL();
		jvr.addEventListener(tel);
		logger.info("genring(1) ASYNC");
		ms.genring(msDev,1,ms.EV_ASYNC);
		synchronized (tel) { tel.wait(); }
		jvr.removeEventListener(tel);
		ms.close(msDev);
	}
	*/

	/*public void test5_estdelconf() throws Exception {
		int VOICE = 3;
		int msDev = ms.open("msiB1C1",0);
		int msBrd = ms.open("msiB1",0);
		int[] dxDev = new int[7];
		for (int x = 0; x < VOICE; x++) {
			int chan = ((x+1)%4)+1;
			int board = (x/4)+1;
			dxDev[x] = dx.open("dxxxB"+board+"C"+chan,0);
			dx.unlisten(dxDev[x]);
		}
		ms.unlisten(msDev);
		MS_CDT[] cdt = MS_CDT.newArray(4);
		cdt[0].chan_num = 1; // msiB1C1
		cdt[0].chan_sel = ms.MSPN_STATION;
		cdt[0].chan_attr = ms.MSPA_NULL;
		for (int x = 0; x < VOICE; x++) {
			cdt[x+1].chan_num = (int) dx.getxmitslot(dxDev[x]).ts();
			cdt[x+1].chan_sel = ms.MSPN_TS;
			cdt[x+1].chan_attr = ms.MSPA_NULL;
		}
		for (int x = 0; x < cdt.length; x++) {
			logger.info("estconf(in) : " + cdt[x]);
		}
		int confID = ms.estconf(msBrd,cdt,ms.MSCA_NULL);
		for (int x = 0; x < cdt.length; x++) {
			logger.info("estconf(out): " + cdt[x]);
		}
		for (int x = 0; x < VOICE; x++) {
			dx.listen(dxDev[x], cdt[x+1].listenTS());
		}
		// Make some noise for 10 seconds
		class TEL extends JVREventListener {
			int d = 0;
			public boolean handleEvent (Event e) throws JVRException {
				if (e.type == dx.TDX_DIAL) {
					dx.dial(e.device,","+(d++%10),null,dx.EV_ASYNC);
				}
				return true;
			}
		}
		TEL tel = new TEL();
		jvr.addEventListener(tel);
		for (int x = 0; x < VOICE; x++) {
			dx.dial(dxDev[x],","+x,null,dx.EV_ASYNC);
			try { Thread.sleep(100+x); } catch (Exception ignore) { }
		}
		try { Thread.sleep(10000); } catch (Exception e) { }
		jvr.removeEventListener(tel);
		for (int x = 0; x < VOICE; x++) { dx.stopch(dxDev[x],dx.EV_SYNC); }
		ms.delconf(msBrd,confID);
		ms.close(msBrd);
		ms.close(msDev);
		for (int x = 0; x < VOICE; x++) { dx.close(dxDev[x]); }
	}*/

	/*
	estconf (int dev, MS_CDT[] cdt, int attr) throws JVRException;
	delconf (int dev, int confID) throws JVRException;
	monconf (int dev, int confID) throws JVRException;
	unmonconf (int dev, int confID) throws JVRException;
	genring (int dev, int len, int mode) throws JVRException;
	stopfn (int dev, int func) throws JVRException;
	*/

}
