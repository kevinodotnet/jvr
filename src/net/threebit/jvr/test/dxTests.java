package net.threebit.jvr.test;

/*
 * $Id: dxTests.java,v 1.56 2005/01/26 01:34:05 kevino Exp $
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

public class dxTests extends AbstractTest {

	public void testX() { System.exit(1); }

	public void test22_uio_1() throws Exception {

		int d = dx.open("dxxxB1C1",0);
		DV_TPT[] tpt = new DV_TPT[1];
		tpt[0] = new DV_TPT();
		tpt[0].tp_type = dx.IO_EOT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 50;
		tpt[0].tp_flags = dx.TF_MAXTIME;

		// Create a custom UserIO object that will delete read/write/seek calls to the RandomAccessFile class.
		final RandomAccessFile f = new RandomAccessFile("c:\\code\\oreivr\\dict\\eng\\misc\\maximumRecordingTimeExceeded.vox","rw");
		UserIO uio = new UserIO();
		uio.setHandler(new UserIOHandler(){
			public byte[] read (UserIO io, int len) throws IOException {
				logger.info("len: " + len);
				byte[] ret = new byte[len];
				int r = f.read(ret);
				if (r == -1) { return null; }
				byte[] ret2 = new byte[r];
				for (int x = 0; x < r; x++) { ret2[x] = ret[x]; }
				return ret2;
			}
			public long seek (UserIO io, long offset, int whence) throws IOException {
				logger.info("offset: " + offset + " whence: " + jvr.symbolName("SEEK_",whence));
				if (whence == dx.SEEK_SET) {
					f.seek(offset);
				}
				else if (whence == dx.SEEK_CUR) {
					f.seek( f.getFilePointer() + offset );
				}
				else if (whence == dx.SEEK_END) {
					f.seek( f.length() + offset );
				}
				else {
					logger.info("Unknown whence value: " + whence);
					return -1;
				}
				return f.getFilePointer();
			}
			public int write (UserIO io, byte[] buffer) throws IOException {
				logger.info("buffer.length: " + buffer.length);
				f.write(buffer);
				return buffer.length;
			}
		});

		logger.info("Recording...");
		dx.rec( d,new DX_IOTT[]{uio},tpt,dx.RM_TONE|dx.EV_SYNC);
		logger.info("Playing...");
		dx.play(d,new DX_IOTT[]{uio},null,dx.EV_SYNC);
		logger.info("Done...");

		dx.close(d);
	}

	class HideThese extends AbstractTest {
	public void test21_initcallp() throws Exception {
		int d = dx.open("dxxxB1C1",0);
		dx.deltones(d);
		dx.chgdur(dx.TID_RNGBK1, 40, 10, 20, 5);
		dx.chgfreq(dx.TID_RNGBK1, 367,62,470,47);
		dx.chgrepcnt(dx.TID_RNGBK1, 2);
		dx.initcallp(d);
		dx.close(d);
	}


	// public void testX() { System.exit(1); }

	/*
	public void test20_dxxpb_native() throws Exception {
		logger.info("Allocating");
		DX_XPB x = new DX_XPB();
		logger.info("x: " + x);
		try { Thread.sleep(1000); } catch (Exception ignore) { }
		logger.info("NULLING");
		x = null;
		System.gc();
		try { Thread.sleep(1000); } catch (Exception ignore) { }
		logger.info("x: " + x);
		logger.info("DONE");
	}
	*/

	public void test19_rec_play_async() throws Exception {

		logger.info("0");
		final DX_XPB xpb = new DX_XPB();
		logger.info("1");
		xpb.wFileFormat = dx.FILE_FORMAT_WAVE;
		logger.info("2");
		xpb.wDataFormat = dx.DATA_FORMAT_PCM;
		logger.info("3");
		xpb.nSamplesPerSec = dx.DRT_11KHZ;
		logger.info("4");
		xpb.wBitsPerSample = 8;

		logger.info("A");
		final DV_TPT tpt[]  = DV_TPT.newArray(2);
		tpt[0].tp_type = dx.IO_CONT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 10;
		tpt[0].tp_flags = dx.TF_MAXTIME;
		tpt[1].tp_type = dx.IO_EOT;
		tpt[1].tp_termno = dx.DX_MAXDTMF;
		tpt[1].tp_length = 1;
		tpt[1].tp_flags = dx.TF_MAXDTMF;
		logger.info("B");

		final String filename = "dxTests-test19_recplayasync.wav";
		new File(filename).delete();
		final DX_IOTT[] iottArray = DX_IOTT.newArray(1);
		final DX_IOTT iott = iottArray[0];
		iott.io_type = dx.IO_DEV;
		iott.io_fhandle = dx.fileopen(filename,dx.WIN32_O_CREAT|dx.WIN32_O_RDWR|dx.WIN32_O_BINARY,dx.WIN32_S_IREAD|dx.WIN32_S_IWRITE);
		logger.info("C");

		final int dxDev = dx.open("dxxxB1C1",0);
		logger.info("dxDev: " + dxDev);
		dx.stopch(dxDev,dx.EV_SYNC);
		// int msDev = ms.open("msiB1C1",0);
		/*
		ms.setevtmsk(msDev,ms.DTG_SIGEVT,ms.MSMM_OFFHOOK|ms.MSMM_ONHOOK|ms.MSMM_HOOKFLASH,ms.DTA_SETMSK);
		dx.setevtmsk(dxDev,dx.DM_SILOF|dx.DM_SILON);
		dx.listen(dxDev,ms.getxmitslot(msDev));
		ms.listen(msDev,dx.getxmitslot(dxDev));
		*/

		JVRMetaEventListener mel = new JVRMetaEventListener() {
			public boolean handleEvent (MetaEvent e) throws JVRException {
				if (e.evtdev != dxDev) { return true; }
				if (e.evttype == dx.TDX_ERROR) {
					logger.info(""+sr.ATDV_LASTERR((int)e.evtdev));
					logger.info(""+sr.ATDV_ERRMSGP((int)e.evtdev));
					synchronized (this) { notifyAll(); }
				}
				if (e.evttype == dx.TDX_RECORD) {
					logger.info("Playback ASYNC style.");
					dx.clrdigbuf(dxDev);
					dx.fileclose(iott.io_fhandle);
					iott.io_fhandle = dx.fileopen(filename,dx.WIN32_O_RDONLY|dx.WIN32_O_BINARY,0);
					dx.playiottdata(dxDev,iottArray,tpt,xpb,dx.EV_ASYNC);
					// dx.playvox (dxDev, filename, null, null, dx.EV_SYNC);
					// dx.playwav(dxDev, filename, null, dx.EV_SYNC);
					// synchronized (this) { notifyAll(); }
				}
				if (e.evttype == dx.TDX_PLAY) {
					logger.info("Finished playing");
					logger.info("TERMMSK: " + dx.ATDX_TERMMSK(dxDev));
					synchronized (this) { notifyAll(); }
				}
				return true;
			}
		};
		jvr.addEventListener(mel);
		logger.info("Recording ASYNC style.");
		dx.reciottdata(dxDev,iottArray,tpt,xpb,dx.EV_ASYNC|dx.RM_TONE);
		logger.info("recording started");
		synchronized (mel) { mel.wait(); }
		logger.info("finished synchronization");
		jvr.removeEventListener(mel);

		dx.fileclose(iott.io_fhandle);
		dx.close(dxDev);
		// ms.close(msDev);
		logger.info("TEST finished");
	}
	public void testX() { System.exit(1); }


	public void test18_no_dial_cap() throws Exception {
		int dxDev = dx.open("dxxxB1C1",0);
		int msDev = ms.open("msiB1C1",0);
		ms.setevtmsk(msDev,ms.DTG_SIGEVT,ms.MSMM_OFFHOOK|ms.MSMM_ONHOOK|ms.MSMM_HOOKFLASH,ms.DTA_SETMSK);
		dx.setevtmsk(dxDev,dx.DM_SILOF|dx.DM_SILON);
		dx.listen(dxDev,ms.getxmitslot(msDev));
		ms.listen(msDev,dx.getxmitslot(dxDev));
		logger.info("Starting CAP using dx.dial");
		DX_CAP cap = new DX_CAP();
		cap.ca_nbrdna = 8;
		cap.ca_intflg = dx.DX_PAMDOPTEN;
		cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;
		cap.ca_hedge = 1;
		long cpterm = dx.dial(dxDev,"",cap,dx.EV_SYNC|dx.DX_CALLP);
		if (cpterm == dx.CR_CNCT) {
			// Since this is ISDN specific, we expect only one of
			// CON_PVD or CON_PAMD
			long cpConnType = dx.ATDX_CONNTYPE(dxDev);
			if (cpConnType == dx.CON_CAD) { logger.info("CON_CAD"); }
			else if (cpConnType == dx.CON_PVD) { logger.info("CON_PVD"); }
			else if (cpConnType == dx.CON_PAMD) { 
				logger.info("CON_PAMD"); 
				logger.info("SILON_SILOFF set");
				dx.setevtmsk(dxDev,dx.DM_SILOF|dx.DM_SILON);
				try { Thread.sleep(30*1000); } catch (Exception ignore) { }
			}
			else if (cpConnType == dx.CON_LPC) { logger.info("CON_LPC"); }
			else { throw new RuntimeException("Not possible"); }
		}
		else if (cpterm == dx.CR_BUSY) { logger.info("CR_BUSY"); }
		else if (cpterm == dx.CR_CEPT) { logger.info("CR_CEPT"); }
		else if (cpterm == dx.CR_ERROR) { logger.info("CR_ERROR"); }
		else if (cpterm == dx.CR_FAXTONE) { logger.info("CR_FAXTONE"); }
		else if (cpterm == dx.CR_NOANS) { logger.info("CR_NOANS"); }
		else if (cpterm == dx.CR_NODIALTONE) { logger.info("CR_NODIALTONE"); }
		else if (cpterm == dx.CR_NORB) { logger.info("CR_NORB"); }
		else if (cpterm == dx.CR_STOPD) { logger.info("CR_STOPD"); }
		else { throw new RuntimeException("ERROR: this condition should never be true."); }
		dx.close(dxDev);
		ms.close(msDev);
	}

	public void test17_wtring_and_getdigits_again() throws Exception {
		int dev = dx.open("dxxxB1C1",0);
		dx.clrdigbuf(dev);

		for (int x = 0; x < 1; x++) {

			logger.info("sethook(ONHOOK)");
			dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);

			logger.info("wtring()");
			dx.wtring(dev, 1, dx.DX_OFFHOOK, -1);

			DV_TPT tpt[]  = DV_TPT.newArray(2);
			tpt[0].tp_type = dx.IO_CONT;
			tpt[0].tp_termno = dx.DX_MAXTIME;
			tpt[0].tp_length = 10 * 10;
			tpt[0].tp_flags = dx.TF_MAXTIME;
			tpt[1].tp_type = dx.IO_EOT;
			tpt[1].tp_termno = dx.DX_MAXDTMF;
			tpt[1].tp_length = 5;
			tpt[1].tp_flags = dx.TF_MAXDTMF;
			logger.info("getdig()");
			logger.info("  got: " + dx.getdig(dev, tpt, null, dx.EV_SYNC));

			logger.info("sethook(ONHOOK)");
			dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);

			try {
				for (int i = 0; i < 15; i++) {
					logger.info("Sleeping " + (15-i));
					Thread.sleep(1000);
				}
			}
			catch (Exception ignore) { }

		}

		dx.close(dev);
	}


	public void test16_wtring_and_getdigits() throws Exception {
		int dev = dx.open("dxxxB1C1",0);
		dx.clrdigbuf(dev);

		logger.info("setting hook state");
		dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);

		logger.info("waiting for a ring");
		dx.wtring(dev, 1, dx.DX_OFFHOOK, -1);

		DV_TPT tpt[]  = DV_TPT.newArray(2);
		tpt[0].tp_type = dx.IO_CONT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 10 * 10;
		tpt[0].tp_flags = dx.TF_MAXTIME;
		tpt[1].tp_type = dx.IO_EOT;
		tpt[1].tp_termno = dx.DX_MAXDTMF;
		tpt[1].tp_length = 25;
		tpt[1].tp_flags = dx.TF_MAXDTMF;

		logger.info("waiting for digits");
		String phoneNumber = dx.getdig(dev, tpt, null, dx.EV_SYNC);
		logger.info("got: '"+phoneNumber+"'");

		logger.info("hangup and close");
		dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);
		dx.close(dev);
	}

	public void test15_fileopen_linux() throws Exception {
		if (! linux) { return; }
		int dxdev = dx.open("dxxxB1C1",0);
		int f = dx.fileopen(
			"/tmp/testfile.vox",
			dx.LINUX_O_CREAT,
			777
		);
		dx.fileclose(f);
		dx.close(dxdev);
	}


	public void test14_wtcallid() throws Exception {
		int dxdev = dx.open("dxxxB1C1",0);
		try {
			logger.info("enabling callerid.");
			dx.setparm(dxdev,dx.DXCH_CALLID,dx.DX_CALLIDENABLE);
			logger.info("wtcallid()");
			String callid = dx.wtcallid(dxdev,2,-1);
			logger.info("wtcallid() returned: '"+callid+"'");
		}
		catch (JVRException e) {
			logger.throwing(getClass().getName(),"test14_wtcallid",e);
		}
		dx.close(dxdev);
	}

	public void test13_recwav() throws Exception {
		int dxdev = dx.open("dxxxB1C1",0);
		int msdev = ms.open("msiB1C1",0);
		ms.listen(msdev, dx.getxmitslot(dxdev));
		dx.listen(dxdev, ms.getxmitslot(msdev));

		String filename = "dxTests-test13_recwav.wav";
		DX_XPB xpb = null;
		DV_TPT tpt[] = DV_TPT.newArray(2);
		tpt[0].tp_type = dx.IO_CONT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 50;
		tpt[0].tp_flags = dx.TF_MAXTIME;
		tpt[1].tp_type = dx.IO_EOT;
		tpt[1].tp_termno = dx.DX_MAXDTMF;
		tpt[1].tp_length = 1;
		tpt[1].tp_flags = dx.TF_MAXDTMF;

		logger.info("recwav()");
		dx.recwav(dxdev, filename, tpt, xpb, dx.RM_TONE|dx.EV_SYNC);

		tpt = DV_TPT.newArray(1);
		tpt[0].tp_type = dx.IO_EOT;
		tpt[0].tp_termno = dx.DX_MAXDTMF;
		tpt[0].tp_length = 1;
		tpt[0].tp_flags = dx.TF_MAXDTMF;
		logger.info("playwav()");
		dx.playwav(dxdev, filename, tpt, dx.EV_SYNC);

		dx.close(dxdev);
		ms.close(msdev);
	}

	public void test1_openclose() throws Exception {
		int dev = dx.open("dxxxB1C1",0);
		assertTrue(dev > 0);
		dx.close(dev);
	}

	/*public void test2_sethook_and_events() throws Exception {

		class Test2EventListener extends JVREventListener {
			public int times = 0;
			public boolean handleEvent (Event e) {
				// Remember how many times we've seen an event.
				times ++;
				return true;
			}
		}

		Test2EventListener el = new Test2EventListener();
		jvr.addEventListener(el);
		int dev = dx.open("dxxxB1C1",0);
		dx.setevtmsk(
			dev,
			dx.DM_LCOFF|
			dx.DM_LCON|
			dx.DM_RINGS|
			dx.DM_RNGOFF|
			dx.DM_SILOF|
			dx.DM_SILON|
			dx.DM_WINK|
			dx.DM_LCREV
		);
		assertTrue(dev > 0);
		for (int x = 0; x < 2; x++) {
			dx.sethook(dev,jvr.DX_ONHOOK,jvr.EV_ASYNC);
			Thread.sleep(100);
		}
		dx.close(dev);
		Thread.sleep(200);
		// Make sure that out custom event listener saw at least
		// two events go by.
		assertTrue(el.times >= 2);
		jvr.removeEventListener(el);
	}*/

	public void test3_getxmitslot_listen() throws Exception {
		int d1 = dx.open("dxxxB1C1",0);
		int d2 = dx.open("dxxxB1C2",0);
		dx.unlisten(d1);
		dx.unlisten(d2);
		dx.listen(d1, dx.getxmitslot(d2));
		dx.listen(d2, dx.getxmitslot(d1));
		dx.close(d1);
		dx.close(d2);
	}

	/**
	 *
	 */
	public void test4_listen_dial() throws Exception {
		logger.info("Listen to dialing on MSI port");
		int dxdev = dx.open("dxxxB1C1",0);
		int msdev = ms.open("msiB1C1",0);
		ms.listen(msdev, dx.getxmitslot(dxdev));
		dx.listen(dxdev, ms.getxmitslot(msdev));
		for (int x = 3; x > 3; x--) {
			logger.info("  " + x + " more second(s)");
			dx.dial(dxdev,"6135551212",null,dx.EV_SYNC);
			Thread.sleep(100);
		}
		dx.close(dxdev);
		ms.close(msdev);
	}

	/*public void test5_dial_digitevt() throws Exception {
		int d1 = dx.open("dxxxB1C1",0);
		int d2 = dx.open("dxxxB1C2",0);
		logger.info("Dialing on dxxxB1C1; should see DG_DIGIT event on dxxxB1C2");
		dx.listen(d1, dx.getxmitslot(d2));
		dx.listen(d2, dx.getxmitslot(d1));
		dx.setevtmsk(d1, dx.DM_DIGITS);
		dx.setevtmsk(d2, dx.DM_DIGITS);
		JVREventListener el = new JVREventListener() {
			public boolean handleEvent (Event e) {
				return true;
			}
		};
		jvr.addEventListener(el);
		for (int x = 0; x < 3; x++) {
			String dialstr = ""+x;
			logger.info("dialing: " + dialstr);
			dx.dial(d1,dialstr,null,dx.EV_SYNC);
			// Thread.sleep(1000)
		}
		jvr.removeEventListener(el);
		dx.close(d1);
		dx.close(d2);
	}*/

	/*
	 *
	public void test6_event_load() throws Exception {
		final int toSend = 3;
		final int[] eventCount = new int[24];
		final int[] sentCount = new int[24];
		final int[] dev = new int[24];
		class TestEventListener extends JVREventListener {
			public boolean handleEvent (Event e) {
				// count up the number of events for each device.
				int offset = -1;
				for (int x = 0; x < 24; x++) { if (e.device == dev[x]) { offset = x; } }
				eventCount[offset]++;
				return true;
			}
		}
		ArrayList dialers = new ArrayList();
		for (int x = 0; x < 24; x++) {
			int chan = ((x+1)%4)+1;
			int board = (x/4)+1;
			dev[x] = dx.open("dxxxB"+board+"C"+chan,0);
			dx.unlisten(dev[x]);
		}
		// Make the last 12 listen to the first twelve.
		for (int x = 0; x < 12; x++) {
			dx.listen(dev[x+12], dx.getxmitslot(dev[x]));
			dx.setevtmsk(dev[x+12], dx.DM_DIGITS);
		}
		TestEventListener el = new TestEventListener();
		jvr.addEventListener(el);
		int threadCount = 0;
		// Create 12 thread to dial on the first 12.
		for (int x = 0; x < 12; x++) {
			final int mydev = dev[x];
			final int offset = x;
			Thread dialer = new Thread() {
				public void run() {
					java.util.Random r = new Random();
					try {
						for (int y = 0; y < toSend; y++) {
							dx.dial(mydev,""+y,null,dx.EV_SYNC);
							sentCount[offset]++;
							try { Thread.sleep(400 + r.nextInt(200)); } catch (Exception ignore) { }
						}
					}
					catch (JVRException e) {
						logger.throwing(getClass().getName(),"run",e);
					}
				}
			};
			threadCount ++;
			dialer.start();
			dialers.add(dialer);
		}
		for (Iterator i = dialers.iterator(); i.hasNext(); ) {
			logger.info("waiting for dialers to finish: " + threadCount);
			Thread t = (Thread) i.next();
			t.join();
			threadCount--;
		}
		Thread.sleep(1000);
		jvr.removeEventListener(el);
		for (int x = 0; x < 12; x++) {
			dx.close(dev[x]);
			try {
				assertTrue(eventCount[x] == 0);
				assertTrue(sentCount[x] == toSend);
			}
			catch (AssertionFailedError e) {
				logger.info("offset: " + x + " count: " + eventCount[x] + " sent: " + sentCount[x]);
				throw e;
			}
		}
		for (int x = 12; x < 24; x++) {
			dx.close(dev[x]);
			try {
				assertTrue(eventCount[x] == toSend);
				assertTrue(sentCount[x] == 0);
			}
			catch (AssertionFailedError e) {
				logger.info("offset: " + x + " count: " + eventCount[x] + " sent: " + sentCount[x]);
				throw e;
			}
		}
	}
	*/

	public void test7_tones() throws Exception {
		int dev = dx.open("dxxxB1C1",0);
		int msi = ms.open("msiB1C1",0);
		dx.unlisten(dev);
		ms.listen(msi,dx.getxmitslot(dev));
		ms.close(msi);

		TN_GENCAD tngencad = new TN_GENCAD();
		tngencad.cycles = 1;
		tngencad.numsegs = 1;
		tngencad.tone[0] = dx.bldtngen(440,480,-19,-19,200); // dialtone.
		tngencad.offtime[0] = 400; // 4 seconds.
		DV_TPT tpt[] = DV_TPT.newArray(1);
		tpt[0].tp_type = dx.IO_EOT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 200; // 20 seconds (3 rings)
		tpt[0].tp_flags = dx.TF_MAXTIME;
		logger.info("Playing regular ring back cadence (4 seconds of silence)");
		dx.playtoneEx(dev,tngencad,tpt,dx.EV_SYNC);
		logger.info("Playing dial tone for 2 seconds");
		tpt[0].tp_length = 20;
		TN_GEN tngen = dx.bldtngen(350,440,-20,-20,-1);
		dx.playtone(dev,tngen,tpt,dx.EV_SYNC);
		dx.close(dev);
	}

	/*public void test8_getdig() throws Exception {
		DV_DIGIT digit = new DV_DIGIT();
		final int dx1 = dx.open("dxxxB1C1",0);
		final int dx2 = dx.open("dxxxB1C2",0);
		dx.listen(dx1,dx.getxmitslot(dx2));
		dx.listen(dx2,dx.getxmitslot(dx1));
		dx.stopch(dx1,dx.EV_SYNC);
		dx.stopch(dx2,dx.EV_SYNC);
		dx.setevtmsk(dx1, dx.DM_DIGOFF);
		dx.setevtmsk(dx2, dx.DM_DIGOFF);

		final DV_TPT tpt[] = DV_TPT.newArray(2);
		tpt[0].tp_type = dx.IO_CONT;
		tpt[0].tp_termno = dx.DX_MAXDTMF;
		tpt[0].tp_length = 1;
		tpt[0].tp_flags = dx.TF_MAXDTMF;
		tpt[1].tp_type = dx.IO_EOT;
		tpt[1].tp_termno = dx.DX_IDDTIME;
		tpt[1].tp_length = 100;
		tpt[1].tp_flags = dx.TF_MAXSIL;

		class TestEventListener extends JVREventListener {
			int dials = 0;
			public DV_DIGIT asyncBuf = null;
			public boolean handleEvent (Event e) throws JVRException {

				if (e.type == dx.TDX_DIAL && e.device == dx1) {
					// previous dialing I/O finished;  Wait three seconds then
					// kick off another one.  We need to delay so that the
					// GETDIG DV_TPT specs allow getdig() to finish.
					// Do the waiting in a thread so that the event handler is
					// not blocked.
					String dialStr = ""+dials++;
					logger.info("ASYNC dial '"+dialStr+"' start");
					dx.dial(dx1,dialStr,null,dx.EV_ASYNC);
				}
				else if (e.type == dx.TDX_GETDIG && e.device == dx2) {
					// Since we only get events on ASYNC, report on the last sync operation.
					logger.info("TDX_GETDIG/ASYNC (digits: '"+asyncBuf+"')");

					Thread syncWorker = new Thread() {
						public void run() {
							try {
								// SYNC varient 1.
								logger.info("getdig(SYNC)");
								String s = dx.getdig(dx2, tpt, null, dx.EV_SYNC);
								logger.info("  (s: '"+s+"')");
			
								// SYNC varient 2.
								logger.info("getdig(SYNC)");
								DV_DIGIT digBuf = new DV_DIGIT();
								s = dx.getdig(dx2, tpt, digBuf, dx.EV_SYNC);
								logger.info("  (digits: '"+digBuf+"' s: '"+s+"')");
			
								// And, finally, kick out another ASYNC request to repeat the cycle of life.
								logger.info("getdig(ASYNC)");
								asyncBuf = new DV_DIGIT();
								dx.getdig(dx2, tpt, asyncBuf, dx.EV_ASYNC);
							}
							catch (Exception e) {
								logger.throwing(getClass().getName(),"run",e);
							}
						}
					};
					syncWorker.start();

				}
				else {
					logger.info("UNHANDLED EVENT !!!!!");
				}
				return true;
			}
		}
		TestEventListener el = new TestEventListener();
		jvr.addEventListener(el);

		// Kick off the ASYNC operations
		el.asyncBuf = new DV_DIGIT();
		logger.info("//////////////////////////////////////////////////////////");
		logger.info("ASYNC getdig start");
		dx.getdig(dx2, tpt, el.asyncBuf, dx.EV_ASYNC);
		logger.info("ASYNC dial '0' start");
		dx.dial(dx1,"0",null,dx.EV_ASYNC);
		while (el.dials < 3) {
			try { Thread.sleep(1000); } catch (Exception ignore) { }
		}
		jvr.removeEventListener(el);
		dx.stopch(dx1,dx.EV_SYNC);
		dx.stopch(dx2,dx.EV_SYNC);
		dx.close(dx1);
		dx.close(dx2);
		logger.info("//////////////////////////////////////////////////////////");
	}*/

	public void test9_recplay() throws Exception {

		DX_XPB xpb = null;
		DV_TPT tpt[] = DV_TPT.newArray(2);
		tpt[0].tp_type = dx.IO_CONT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 50;
		tpt[0].tp_flags = dx.TF_MAXTIME;
		tpt[1].tp_type = dx.IO_EOT;
		tpt[1].tp_termno = dx.DX_MAXDTMF;
		tpt[1].tp_length = 1;
		tpt[1].tp_flags = dx.TF_MAXDTMF;

		int dxDev = dx.open("dxxxB1C1",0);
		int msDev = ms.open("msiB1C1",0);

		dx.listen(dxDev, ms.getxmitslot(msDev));
		ms.listen(msDev, dx.getxmitslot(dxDev));

		String rec1 = "test9_recf-1.vox";

		logger.info("recf() " + rec1);
		dx.recf(dxDev, rec1, tpt, dx.RM_TONE|dx.EV_SYNC);
		logger.info("playvox() " + rec1);
		dx.playvox (dxDev, rec1, null, null, dx.EV_SYNC);

		ms.close(msDev);
		dx.close(dxDev);

	}

	/*public void test10_rec_iott() throws Exception {
		if (win32) {
			final int dxDev = dx.open("dxxxB1C1",0);
			int msDev = ms.open("msiB1C1",0);
			dx.listen(dxDev, ms.getxmitslot(msDev));
			ms.listen(msDev, dx.getxmitslot(dxDev));
			final DV_TPT tpt[] = DV_TPT.newArray(1);
			tpt[0].tp_type = dx.IO_EOT;
			tpt[0].tp_termno = dx.DX_MAXTIME;
			tpt[0].tp_length = 20;
			tpt[0].tp_flags = dx.TF_MAXTIME;
			//
			// EV_SYNC
			//
			final String filename = "test.vox";
			{
				new File(filename).delete();
				DX_IOTT[] iottArray = DX_IOTT.newArray(1);
				DX_IOTT iott = iottArray[0];
				iott.io_type = dx.IO_DEV;
				iott.io_fhandle = dx.fileopen(filename,dx.WIN32_O_CREAT|dx.WIN32_O_RDWR|dx.WIN32_O_BINARY,dx.WIN32_S_IREAD|dx.WIN32_S_IWRITE);
				iott.io_length = -1;
				logger.info("dx.rec()");
				dx.rec(dxDev,iottArray,tpt,dx.RM_TONE|dx.EV_SYNC);
				dx.fileclose(iott.io_fhandle);
				logger.info("dx.playvox()");
				dx.playvox (dxDev, filename, null, null, dx.EV_SYNC);
			}
			//
			// EV_ASYNC
			//
			// The DX_IOTT object needs to remain in-scope for the entire duration of the asynchronous call to dx_rec()
			new File(filename).delete();
			final DX_IOTT[] iottArray = DX_IOTT.newArray(1);
			final DX_IOTT iott = iottArray[0];
			iott.io_type = dx.IO_DEV;
			iott.io_fhandle = dx.fileopen(filename,dx.WIN32_O_CREAT|dx.WIN32_O_RDWR|dx.WIN32_O_BINARY,dx.WIN32_S_IREAD|dx.WIN32_S_IWRITE);
			iott.io_length = -1;
			class TEL extends JVREventListener {
				public boolean handleEvent (Event e) throws JVRException {
					if (e.type == dx.TDX_DIAL) {
						// start ASYNC recording
						logger.info("dx.rec() EV_ASYNC");
						dx.rec(dxDev,iottArray,tpt,dx.RM_TONE|dx.EV_ASYNC);
					}
					if (e.type == dx.TDX_RECORD) {
						// play back recorded file
						dx.fileclose(iott.io_fhandle);
						// done
						synchronized (this) { notifyAll(); }
					}
					if (e.type == dx.TDX_PLAY) {
					}
					return true;
				}
			}
			TEL el = new TEL();
			jvr.addEventListener(el);
			dx.dial(dxDev,"1",null,dx.EV_ASYNC); // to kick the context into the event handler.
			synchronized (el) { el.wait(); }
			jvr.removeEventListener(el);
			logger.info("dx.playvox() EV_SYNC");
			dx.playvox (dxDev, filename, null, null, dx.EV_SYNC);
			//
			// EV_ASYNC
			//
			dx.close(dxDev);
			ms.close(msDev);
		}
		if (linux) {
			throw new Exception("TODO: write a DX_IOTT and dx_recf() test for LINUX");
		}
	}*/

	public void test11_wtring() throws Exception {
		int dxdev = dx.open("dxxxB1C1",0);
		final int msdev = ms.open("msiB1C1",0);
		dx.listen(dxdev, ms.getxmitslot(msdev));
		ms.listen(msdev, dx.getxmitslot(dxdev));
		ms.stopfn(msdev,ms.MTF_RING);
		dx.stopch(dxdev,dx.EV_SYNC);

		Thread ringer = new Thread() {
			public void run() {
				try {
					sleep(1000); 
					logger.info("generating RING");
					ms.genring(msdev,2,ms.EV_SYNC);
				}
				catch (Exception ignore) { }
			}
		};
		ringer.start();
		logger.info("waiting for ring");
		try {
			dx.wtring(dxdev, 2, dx.DX_ONHOOK, 10);
			assertTrue(false);
		}
		catch (DialogicException e) {
			// TODO: assert that the exception is an I/O timeout exception.
			// logger.throwing(getClass().getName(),"test11_wtring",e);
		}
		dx.close(dxdev);
		ms.close(msdev);
	}

	/*public void test12_mreciottdata() throws Exception {
		if (!win32) { return; } // win32 only test.

		final int dx1 = dx.open("dxxxB1C1",0);
		final int dx2 = dx.open("dxxxB1C2",0);
		final int dx3 = dx.open("dxxxB1C3",0);
		int msi = ms.open("msiB1C1",0);

		ms.unlisten(msi);
		dx.stopch(dx1,dx.EV_SYNC);
		dx.stopch(dx2,dx.EV_SYNC);
		dx.stopch(dx3,dx.EV_SYNC);

		// Use dx1 and dx2 to create "noise" suitable for recording.
		// This event listener will guarantee that noise keeps happening
		// until the event listener is removed.
		class TEL extends JVREventListener {
			public boolean handleEvent (Event e) throws JVRException {
				if (e.device == dx1 || e.device == dx2) {
					String dial = "1";
					if (e.device == dx2) { dial = "2"; }
					dx.dial(e.device,dial,null,dx.EV_ASYNC);
				}
				return true;
			}
		}
		TEL tel = new TEL();
		jvr.addEventListener(tel);
		logger.info("Kicking out the events!  Why are the events not showing up?");
		ms.listen(msi,dx.getxmitslot(dx1));
		dx.dial(dx1,"0",null,dx.EV_ASYNC);
		dx.dial(dx2,"0",null,dx.EV_ASYNC);

		// Prepare timeslot info for the two slots we will record.
		SC_TSINFO tsinfo = new SC_TSINFO();
		tsinfo.add( dx.getxmitslot(dx1).ts() );
		tsinfo.add( dx.getxmitslot(dx2).ts() );
		logger.info("" + tsinfo);

		DX_IOTT[] iottArray = DX_IOTT.newArray(1);
		DX_IOTT iott = iottArray[0];
		iott.io_type = dx.IO_DEV;
		iott.io_fhandle = dx.fileopen("mreciottdata_test.vox",dx.WIN32_O_CREAT|dx.WIN32_O_RDWR|dx.WIN32_O_BINARY,dx.WIN32_S_IREAD|dx.WIN32_S_IWRITE);
		iott.io_length = -1;

		DV_TPT tpt[] = DV_TPT.newArray(1);
		tpt[0].tp_type = dx.IO_EOT;
		tpt[0].tp_termno = dx.DX_MAXTIME;
		tpt[0].tp_length = 30;
		tpt[0].tp_flags = dx.TF_MAXTIME;

		DX_XPB xpb = new DX_XPB();
		xpb.wFileFormat = dx.FILE_FORMAT_VOX;
		xpb.wDataFormat = dx.DATA_FORMAT_DIALOGIC_ADPCM;
		xpb.nSamplesPerSec = dx.DRT_8KHZ;
		xpb.wBitsPerSample = 4;

		logger.info("iott  : " + DX_IOTT.toString(iottArray));
		logger.info("tpt   : " + DV_TPT.toString(tpt));
		logger.info("xpb   : " + xpb);
		logger.info("tsinfo: " + tsinfo);
		dx.mreciottdata(dx3,iottArray,tpt,xpb,0,tsinfo);
		dx.fileclose(iott.io_fhandle);

		dx.unlisten(dx3);
		ms.listen(msi,dx.getxmitslot(dx3));
		logger.info("done; playback the file");
		dx.playvox(dx3,"mreciottdata_test.vox",null,null,dx.EV_SYNC);

		jvr.removeEventListener(tel);
		dx.close(dx1);
		dx.close(dx2);
		dx.close(dx3);
		ms.close(msi);
	}*/
	} // class HideThese
}
