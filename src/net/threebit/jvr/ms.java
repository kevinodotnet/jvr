package net.threebit.jvr;

/*
 * $Id: ms.java,v 1.21 2005/01/26 01:34:04 kevino Exp $
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
 * Java encapsulation of the Dialogic <b>R4 Conferencing</b> Software
 * (functions matching <b>ms_*</b>).
 */

public abstract class ms extends jvr {

	/**
	 * ms_open.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-42.html#P4297_115826">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-42.html#P4297_115826">linux</a>)
	 */
	public static native int open (String name, int flags) throws JVRException;

	/**
	 * ms_close.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-26.html#P1771_52369">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-26.html#P1771_52369">linux</a>)
	 */
	public static native int close (int dev) throws JVRException;

	/**
	 * ms_setevtmsk.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-46.html#P5171_138023">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-46.html#P5171_138023">linux</a>)
	 */
	public static native int setevtmsk (int dev, int event, int bitmask, int action) throws JVRException;

	/**
	 * ms_listen.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-64.html#P6470_170860">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-64.html#P6470_170860">linux</a>)
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
	 * ms_unlisten.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-65.html#P6634_175517">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-65.html#P6634_175517">linux</a>)
	 */
	public static native int unlisten (int dev) throws JVRException;

	/**
	 * ms_getxmitslot.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-63.html#P6376_168641">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-63.html#P6376_168641">linux</a>)
	 */
	public static native SC_TSINFO getxmitslot (int dev) throws JVRException;

	/**
	 * ms_estconf.
	 * <p>
	 * Instead of returning the success/error status, this method returns the
	 * new conference identifier.  There is no loss of signalling because on
	 * an error condition, estconf() will throw an Exception.
	 * <p>
	 * Instead of stating the number of parties explicitely, it is implied by
	 * the length of the MS_CDT array
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-30.html#P2207_61181">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-30.html#P2207_61181">linux</a>)
	 */
	public static native int estconf (int dev, MS_CDT[] cdt, int attr) throws JVRException;

	/**
	 * ms_delconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-27.html#P1858_54299">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-27.html#P1858_54299">linux</a>)
	 */
	public static native int delconf (int dev, int confID) throws JVRException;

	/**
	 * ms_monconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-41.html#P4155_112204">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-41.html#P4155_112204">linux</a>)
	 * <p>
	 * Instead of returning the success/error status, this method returns the
	 * XMIT timeslot of the conference.  There is no loss of signalling because on
	 * an error condition, estconf() will throw an Exception.
	 */
	public static native long monconf (int dev, int confID) throws JVRException;

	/**
	 * ms_unmonconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-52.html#P5931_155538">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-52.html#P5931_155538">linux</a>)
	 */
	public static native int unmonconf (int dev, int confID) throws JVRException;

	/**
	 * ms_genring.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-32.html#P2769_76182">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-32.html#P2769_76182">linux</a>)
	 */
	public static native int genring (int dev, int len, int mode) throws JVRException;

	/**
	 * ms_stopfn.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-49.html#P5585_147746">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-49.html#P5585_147746">linux</a>)
	 */
	public static native int stopfn (int dev, int func) throws JVRException;

	/**
	 * ms_addtoconf
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-24.html#P1260_40859">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-24.html#P1260_40859">linux</a>)
	 */
	public static native void addtoconf (int dev, int conf, MS_CDT cdt) throws JVRException;

	/**
	 * ms_remfromconf
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-43.html#P4397_117925">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-43.html#P4397_117925">linux</a>)
	 */
	public static native void remfromconf (int dev, int conf, MS_CDT cdt) throws JVRException;

	/**
	 * ATMS_TSSGBIT
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-23.html#P1135_38579">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-23.html#P1135_38579">linux</a>)
	 */
	public static native long ATMS_TSSGBIT (int dev) throws JVRException;

	/**
	 * ms_dsprescount 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-29.html#P2075_58543">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-29.html#P2075_58543">linux</a>)
	 */
	public static native int dsprescount (int dev) throws JVRException;

	/**
	 * ms_setbrdparm
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-44.html#P4540_120780">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-44.html#P4540_120780">linux</a>)
	 */
	public static native void setbrdparm (int dev, int param, Object value) throws JVRException;

	/**
	 * ms_setcde
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/msi/1218-04-45.html#P4940_134018">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/msi/1218-04-45.html#P4940_134018">linux</a>)
	 */
	public static native void setcde (int dev, int confID, MS_CDT cdt) throws JVRException;

}
