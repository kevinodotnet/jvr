package net.threebit.jvr;

/*
 * $Id: dt.java,v 1.15 2004/07/02 18:11:38 kevino Exp $
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
 * Java encapsulation of the Dialogic <b>R4 Digital Network Interface</b> Software
 * (functions matching <b>dt_*</b>).
 */

public abstract class dt extends jvr {

	/**
	 * dt_open
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-32.html#P3526_114263">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-32.html#P3526_114263">linux</a>)
	 */
	public static native int open (String name, int flags) throws JVRException;

	/**
	 * dt_close
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-25.html#P2254_82395">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-25.html#P2254_82395">linux</a>)
	 */
	public static native int close (int dev) throws JVRException;

	/**
	 * dt_setevtmsk
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-35.html#P3927_127214">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-35.html#P3927_127214">linux</a>)
	 */
	public static native int setevtmsk (int dev, int event, int bitmask, int action) throws JVRException;

	/**
	 * dt_listen
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-32.html#P3526_114263">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-32.html#P3526_114263">linux</a>)
	 */
	public static native int listen (int dev, SC_TSINFO tsinfo) throws JVRException;

	/**
	 * Places the supplied timeslot value into a new SC_TSINFO object then calls the
	 * regular <code>listen</code> method.
	 */
	public static int listen (int dev, long ts) throws JVRException {
		SC_TSINFO tsinfo = new SC_TSINFO();
		tsinfo.add(ts);
		return listen(dev,tsinfo);
	}

	/**
	 * dt_unlisten
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-43.html#P5012_159983">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-43.html#P5012_159983">linux</a>)
	 */
	public static native int unlisten (int dev) throws JVRException;

	/**
	 * dt_getxmitslot
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-31.html#P3405_110719">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-31.html#P3405_110719">linux</a>)
	 */
	public static native SC_TSINFO getxmitslot (int dev) throws JVRException;

	/**
	 * dt_xmitwink
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-45.html#P5239_165721">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-45.html#P5239_165721">linux</a>)
	 */
	public static native int xmitwink (int dev, int tmo) throws JVRException;

	/**
	 * dt_settssigsim
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-40.html#P4626_148130">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-40.html#P4626_148130">linux</a>)
	 */
	public static native int settssigsim (int dev, int bitmask) throws JVRException;

	/**
	 * dt_settssig
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-39.html#P4494_144737">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-39.html#P4494_144737">linux</a>)
	 */
	public static native int settssig (int dev, int bitmask, int action) throws JVRException;

	/**
	 * ATDT_TSSGBIT
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-21.html#P1653_62735">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-21.html#P1653_62735">linux</a>)
	 */
	public static native long ATDT_TSSGBIT (int dev) throws JVRException;

	/**
	 * ATDT_STATUS
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/dni/1313-03-19.html#P1417_57092">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/dni/1313-03-19.html#P1417_57092">linux</a>)
	 */
	public static native long ATDT_STATUS (int dev) throws JVRException;

}
