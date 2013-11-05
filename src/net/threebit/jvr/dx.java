package net.threebit.jvr;

/*
 * $Id: dx.java,v 1.49 2004/07/19 17:47:21 kevino Exp $
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

/**
 * Java encapsulation of the Dialogic <b>Voice</b> Software
 * (functions matching <b>dx_*</b>).
 */

public abstract class dx extends jvr {
	// static RandomAccessFile f = null;

	/**
	 * The <code>open</code> method opens a Dialogic voice resource and returns a device
	 * handle.
	 * <p>
	 * <pre class="code">
	 * int deviceHandle = <b>dx.open("dxxxB1C1",0)</b>;
	 * // use "deviceHandle" with other dx methods.
	 * dx.close(deviceHandle);</pre>
	 * <p/>
	 * Dialogic API Reference for this function: (<a
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-109.html#P13649_360348">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-82.html#P10360_264129">linux</a>)
	 * <p/>
	 * @param name The name of the voice resource to open.  Example: "dxxxB1C1".
	 * @param flags Reserved for future use.
	 * @return Dialogic device handle.
	 * @throws JVRException On error.
	 */
	public static native int open (String name, int flags) throws JVRException;

	/**
	 * Closes the device with the given handle, whether busy or idle. 
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-76.html#P8525_225733">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-57.html#P6561_166523">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error 
	 */
	public static native int close (int dev) throws JVRException;

	/**
	 * Sets the given device to the given hook state (DX_ONHOOK,DX_OFFHOOK). 
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-130.html#P17615_461356">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-99.html#P13946_368507">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @param hookstate [DX_ONHOOK|DX_OFFHOOK]
	 * @param mode [EV_SYNC|EV_ASYNC]
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error
	 * @see jvr#DX_ONHOOK
	 * @see jvr#DX_OFFHOOK
	 */
	public static native int sethook (int dev, int hookstate, int mode) throws JVRException;

	/**
	 * Sets the event mask for the device with the given handle.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-128.html#P17198_451523">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-97.html#P13567_358565">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @param bitmask one or more of {DM_LCOFF,DM_LCON,DM_RINGS,DM_RNGOFF,DM_SILOF,DM_SILON,DM_WINK,DM_DIGITS,DM_LCREV}
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error
	 * @see jvr#DM_LCOFF
	 * @see jvr#DM_LCON
	 * @see jvr#DM_RINGS
	 * @see jvr#DM_RNGOFF
	 * @see jvr#DM_SILOF
	 * @see jvr#DM_SILON
	 * @see jvr#DM_WINK
	 * @see jvr#DM_DIGITS
	 * @see jvr#DM_LCREV
	 */
	public static native int setevtmsk (int dev, int bitmask) throws JVRException;

	/**
	 * Sets the identified SCBus to listen to a certain device.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511FP1/onldoc/htmlfiles/scbapint/0439-04-20.html">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-79.html#P9982_253154">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @param tsinfo scbus timeslot info
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error
	 * @see SC_TSINFO
	 */
	public static native int listen (int dev, SC_TSINFO tsinfo) throws JVRException;

	/**
	 * Places the supplied timeslot value into a new SC_TSINFO object then calls the
	 * regular <code>listen</code> method.
	 * 
	 * @param dev handle for device
	 * @param ts SC_BUS id to listen to 
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error
	 */
	public static int listen (int dev, long ts) throws JVRException {
		SC_TSINFO tsinfo = new SC_TSINFO();
		tsinfo.add(ts);
		return listen(dev,tsinfo);
	}

	/**
	 * Removes SCBus listening for a given device.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511FP1/onldoc/htmlfiles/scbapint/0439-04-21.html">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-106.html#P15022_397487">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException on error
	 */
	public static native int unlisten (int dev) throws JVRException;

	/**
	 * Gets the SCBus transmit channel which is listening to the device with the given handle. 
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511FP1/onldoc/htmlfiles/scbapint/0439-04-19.html#P2164_65013">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-74.html#P9324_233117">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @return SCBus timeslot information which is listening
	 * @throws JVRException on error
	 */
	public static native SC_TSINFO getxmitslot (int dev) throws JVRException;

	/**
	 * Dials a given number, optionally using the given call analysis.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-82.html#P9096_236813">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-63.html#P7125_178253">linux</a>)
	 * <p/>
	 * If <code>DX_CALLP</code> is given, <code>DX_CAP</code> may be null.  When given, the call analysis result is returned, 
	 * being one of the <code>CR_*</code> constants.
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @param dialstr number to dial
	 * @param cap call analysis block
	 * @param mode <code>[EV_SYNC,EV_ASYNC],DX_CALLP</code>
	 * @return call analysis result (<code>CR_</code>*) when <code>DX_CAP</code> provided, otherwise exception thrown on error
	 * @throws JVRException on bad
	 * 
	 * @see jvr#CR_BUSY  
	 * @see jvr#CR_CEPT  
	 * @see jvr#CR_CNCT  
	 * @see jvr#CR_ERROR  
	 * @see jvr#CR_FAXTONE  
	 * @see jvr#CR_NOANS  
	 * @see jvr#CR_NODIALTONE  
	 * @see jvr#CR_NORB  
	 * @see jvr#CR_STOPD  
	 */
	public static native int dial (int dev, String dialstr, DX_CAP cap, int mode) throws JVRException;

	/**
	 * Terminates IO on given channel and sets it to idle.
	 * 
	 * NOTE: if the mode is EV_ASYNC, channel stopping is initiated, however the method may return 
	 * before completion.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-136.html#P18601_484049">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-105.html#P14878_392634">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle for device
	 * @param mode [EV_SYNC|EV_ASYNC]
	 * @return nothing useful, exception thrown on error.
	 * @throws JVRException
	 */
	public static native int stopch (int dev, int mode) throws JVRException;

	/**
	 * Clears the digit buffer on the device with the gvien handle
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-78.html#P8668_228857">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-59.html#P6734_170148">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return nothing interesting, exception thrown on error.
	 * @throws JVRException
	 */
	public static native int clrdigbuf (int dev) throws JVRException;

	/**
	 * Instead of specifing a pointer to a TN_GEN structure (Dialogic API), that argument is
	 * omitted and a net.threebit.jvr.TN_GEN object is returned instead.
	 * <p>
	 * <table style='border-collapse: collapse; border: solid #000000 1px;'>
	 * <tr>
	 * <th colspan='2' style='text-align: center;'>North America</th>
	 * </tr>
	 * <tr>
	 * <th style='text-align: right;'>Dial Tone</th>
	 * <td style='padding-left: 5px;'><pre>dx.bldtngen(350,440,-20,-20,-1);</pre></td>
	 * </tr>
	 * <tr>
	 * <th style='text-align: right;'>Ring Back</th>
	 * <td style='padding-left: 5px;'><pre>dx.bldtngen(440,480,-19,-19,200);</pre></td>
	 * </tr>
	 * </table>
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-72.html#P7839_212105">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-53.html#P5901_152216">linux</a>)
	 * <p/>
	 * 
	 * @param freq1
	 * @param freq2
	 * @param ampl1
	 * @param ampl2
	 * @param duration
	 * @return created TN_GEN object
	 * @throws JVRException
	 */
	public static native TN_GEN bldtngen(int freq1,int freq2,int ampl1,int ampl2,int duration) throws JVRException;

	/**
	 * Plays the tone as defined in TN_GEN on the given device.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-113.html#P14586_386613">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-86.html#P11325_291746">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param tngen profile of the tone to be generated
	 * @param tpt temination conditions for the tone
	 * @param mode [EV_SYNC|EV_ASYNC]
	 * @return nothing interesting, exception thrown on error.
	 * @throws JVRException
	 */
	public static native int playtone (int dev, TN_GEN tngen, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-114.html#P14896_392937">win32</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param tngencad
	 * @param tpt
	 * @param mode
	 * @return nothing interesting, exception thrown on error.
	 * @throws JVRException
	 */
	public static native int playtoneEx (int dev,TN_GENCAD tngencad, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * Returns reason for temination of the last IO call.  
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-61.html#P5939_169375">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-42.html#P4132_111298">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return nothing interesting, exception thrown on error.
	 * @throws JVRException
	 * @see jvr#TM_NORMTERM
	 * @see jvr#TM_MAXSIL
	 * @see jvr#TM_MAXNOSIL
	 * @see jvr#TM_LCOFF
	 * @see jvr#TM_IDDTIME
	 * @see jvr#TM_MAXTIME
	 * @see jvr#TM_DIGIT
	 * @see jvr#TM_PATTERN
	 * @see jvr#TM_USRSTOP
	 * @see jvr#TM_EOD
	 * @see jvr#TM_TONE
	 * @see jvr#TM_ERROR
	 */
	public static native long ATDX_TERMMSK (int dev) throws JVRException;

	/**
	 * Gets digits on the device with the given handle.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-92.html#P10954_278828">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-69.html#P8411_207966">linux</a>)
	 * <p/>
	 * In addition to the provided <code>digit</code> parameter, when called in synchronous 
	 * mode, <code>getdig</code> will return a String containing the digits that were collected
	 * (if any).  Otherwise null is returned.  In asynchronous mode, null is always returned and
	 * the collected digits will be available in the given <code>digit</code> buffer.
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action device to use
	 * @param tpt termination parameters
	 * @param digit digits, when asynchronous
	 * @param mode [EV_SYNC|EV_ASYNC]
	 * @return digits, when synchrous
	 * @throws JVRException on error
	 */
	public static native String getdig (int dev, DV_TPT[] tpt, DV_DIGIT digit, int mode) throws JVRException;

	/**
	 * Plays a given VOX file using the device of the given handle with the given termination conditions.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-115.html#P15139_400512">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-87.html#P11608_298128">linux</a>)
	 * <p/>
	 * <b>VOX Links:</b><ul>
	 * <li><a href="http://www.cis.ksu.edu/~tim/vox/">vox/devox</a>: wav &lt;-&gt; vox
	 * <li><a href="http://www.adobe.com/products/audition/main.html">Adobe Audition</a>: ex-cool edit, now even more expense!
	 * <li><a href=" http://jsresources.org/faq.html#N104AF">JavaSound vox tip</a> (note: no javasound vox processor exists, perhaps you want to write one?)
	 * </ul> 
	 * <p/>
	 * <b>Example:</b><ul><pre>
	 * DV_TPT[] tpts=tpt.newArray(1);
	 * tpts[0].tp_type=dx.IO_EOT; // end on tone
	 * tpts[0].tp_termno=dx.DX_MAXDTMF; // max # of dtmf inputs
	 * tpts[0].tp_length=1; // term on press of 1
	 * tpts[0].tp_flags=dx.TF_MAXDTMF; //  terminate with max # of digits
	 * dx.playvox(id, file.getAbsolutePath(), tpts, null, dx.EV_SYNC);
	 * </pre></ul>
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action device handle
	 * @param filename vox to play
	 * @param tpt Termination Parameter Table
	 * @param xpb IO Transfer Block (can be <code>null</code>)
	 * @param mode play mode.  Combination of [EV_SYNC|EV_ASYNC],MD_ADPCM,MD_PCM,PM_ALAW,PM_TONE,PM_SR6,PM_SR8,PM_ADSIALERT,PM_ADSI
	 * @return nothing useful, exception on error 
	 * @throws JVRException on error playing.
	 */
	public static native int playvox (int dev, String filename, DV_TPT[] tpt, DX_XPB xpb, int mode) throws JVRException;

	/**
	 * Plays a given WAV file using the device of the given handle with the given termination conditions.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-116.html#P15267_402899">win32</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param filename vox to play
	 * @param tpt Termination Parameter Table
	 * @param mode Play Mode.  Must be <code>dx.EV_SYNC</code> or
	 * <code>dx.PM_TONE|dx.EV_SYNC</code> if you want a 200ms audible tone
	 * to play before the WAV file.
	 * @return nothing useful, exception on error 
	 * @throws JVRException on error playing.
	 */
	public static native int playwav (int dev, String filename, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * Records to a specific file.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-118.html#P15905_418105">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-89.html#P12150_313702">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param filename file to record to 
	 * @param tpt temination conditions
	 * @param mode <b>EV_SYNC only</b>
	 * @return nothing useful, exception on error 
	 * @throws JVRException on error
	 * @see DV_TPT
	 */
	public static native int recf (int dev, String filename, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * Records voice data to a specified destination.
	 *  
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-117.html#P15404_405366">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-88.html#P11724_300709">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param iott io descriptor 
	 * @param tpt termination conditions
	 * @param mode [EV_SYNC|EV_ASYNC]
	 * @return nothing useful, exception on error 
	 * @throws JVRException on error
	 */
	public static native int rec (int dev, DX_IOTT[] iott, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * dx_fileopen.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-87.html#P10277_264840">win32</a>)
	 * <p/>
	 *
	 * This function is not ordinarialy available under Linux, but since Java programs
	 * cannot call the Unix/Posix fopen() function, JVR provides this method under Linux.
	 * See "man open" for more details.
	 *
	 * <p/>
	 * 
	 * @param filename
	 *   Name of file to open.
	 *   <p>
	 * @param flags
	 *   Flags that are passed to the native file open function.
	 *   <p>
	 *   On win32, bitwise or of:
	 *   {@link jvr#WIN32_O_APPEND WIN32_O_APPEND},
	 *   {@link jvr#WIN32_O_BINARY WIN32_O_BINARY},
	 *   {@link jvr#WIN32_O_CREAT WIN32_O_CREAT},
	 *   {@link jvr#WIN32_O_RDONLY WIN32_O_RDONLY},
	 *   {@link jvr#WIN32_O_RDWR WIN32_O_RDWR},
	 *   {@link jvr#WIN32_O_SEQUENTIAL WIN32_O_SEQUENTIAL},
	 *   {@link jvr#WIN32_O_TEXT WIN32_O_TEXT},
	 *   {@link jvr#WIN32_O_TRUNC WIN32_O_TRUNC},
	 *   {@link jvr#WIN32_O_WRONLY WIN32_O_WRONLY},
	 *   {@link jvr#WIN32_S_IREAD WIN32_S_IREAD},
	 *   {@link jvr#WIN32_S_IWRITE WIN32_S_IWRITE}.
	 *   <p>
	 *   On linux, bitwise or of:
	 *   {@link jvr#LINUX_O_APPEND LINUX_O_APPEND},
	 *   {@link jvr#LINUX_O_CREAT LINUX_O_CREAT},
	 *   {@link jvr#LINUX_O_EXCL LINUX_O_EXCL},
	 *   {@link jvr#LINUX_O_LARGEFILE LINUX_O_LARGEFILE},
	 *   {@link jvr#LINUX_O_NDELAY LINUX_O_NDELAY},
	 *   {@link jvr#LINUX_O_NOCTTY LINUX_O_NOCTTY},
	 *   {@link jvr#LINUX_O_NOFOLLOW LINUX_O_NOFOLLOW},
	 *   {@link jvr#LINUX_O_NONBLOCK LINUX_O_NONBLOCK},
	 *   {@link jvr#LINUX_O_SYNC LINUX_O_SYNC},
	 *   {@link jvr#LINUX_O_TRUNC LINUX_O_TRUNC}.
	 *   <p>
	 * @param pmode
	 *   Mode parameter that is passed to the native file open function.
	 *   <p>
	 *   On win32, bitwise or of:
	 *   {@link jvr#WIN32_S_IREAD WIN32_S_IREAD},
	 *   {@link jvr#WIN32_S_IWRITE WIN32_S_IWRITE}.
	 *   <p>
	 *   On linux, bitwise or of:
	 *   {@link jvr#LINUX_S_IRGRP LINUX_S_IRGRP},
	 *   {@link jvr#LINUX_S_IROTH LINUX_S_IROTH},
	 *   {@link jvr#LINUX_S_IRUSR LINUX_S_IRUSR},
	 *   {@link jvr#LINUX_S_IRWXG LINUX_S_IRWXG},
	 *   {@link jvr#LINUX_S_IRWXO LINUX_S_IRWXO},
	 *   {@link jvr#LINUX_S_IRWXU LINUX_S_IRWXU},
	 *   {@link jvr#LINUX_S_IWGRP LINUX_S_IWGRP},
	 *   {@link jvr#LINUX_S_IWOTH LINUX_S_IWOTH},
	 *   {@link jvr#LINUX_S_IWUSR LINUX_S_IWUSR},
	 *   {@link jvr#LINUX_S_IXGRP LINUX_S_IXGRP},
	 *   {@link jvr#LINUX_S_IXOTH LINUX_S_IXOTH},
	 *   {@link jvr#LINUX_S_IXUSR LINUX_S_IXUSR}.
	 *   <p>
	 * @return The native-context file handle.  You must call {@link dx#fileclose} manually.
	 * @throws JVRException on error
	 */
	public static native int fileopen (String filename, int flags, int pmode) throws JVRException;

	/**
	 * dx_fileclose <b>(Win32 Only)</b>.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-85.html#P10101_258294">win32</a>)
	 * <p/>
	 * 
	 * @param handle
	 * @return nothing useful, exception on error 
	 * @throws JVRException on error
	 */
	public static native int fileclose (int handle) throws JVRException;

	/**
	 * dx_fileerrno() <b>(Win32 Only)</b>.
	 *
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-86.html#P10199_260437">win32</a>)
	 * <p/>
	 *  
	 * @return the number of the error that occured during file number opening. 
	 * @throws JVRException on error
	 */
	public static native int fileerrno () throws JVRException;

	/**
	 * Returns the connection for the call on the given device after @link dx#dial(int, String, DX_CAP, int)  
	 * when using call analysis.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-40.html#P3713_129191">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-20.html#P1860_68797">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return type of connection that was reached
	 * @throws JVRException on error
	 * @see jvr#CON_CAD
	 * @see jvr#CON_PVD
	 * @see jvr#CON_PAMD
	 */
	public static native long ATDX_CONNTYPE (int dev) throws JVRException;

	/**
	 * Returns duration of long low from call analysis.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-56.html#P5438_159269">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-37.html#P3628_100703">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return long low duration
	 * @throws JVRException on error
	 */
	public static native long ATDX_LONGLOW (int dev) throws JVRException;

	/**
	 * Returns duration of the shorter silence from call analysis.
	 *  
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-58.html#P5611_162629">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-39.html#P3802_104132">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return shorter silence duration
	 * @throws JVRException
	 */
	public static native long ATDX_SHORTLOW (int dev) throws JVRException;

	/**
	 * Returns duration of initial detected signal from call analysis.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-59.html#P5721_165360">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-40.html#P3915_106965">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return duraction of initial signal
	 * @throws JVRException
	 */
	public static native long ATDX_SIZEHI (int dev) throws JVRException;

	/**
	 * Returns the number of bytes transfered in the last play or record on the given channel.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-63.html#P6244_175920">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-44.html#P4436_117878">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return number of bytes that were recorded.
	 * @throws JVRException on error
	 */
	public static native long ATDX_TRCOUNT (int dev) throws JVRException;

	/**
	 * Determins call termination conditions when using call analysis.
	 * 
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-42.html#P4013_134623">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-22.html#P2141_74121">linux</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @return call analysis termination conditions
	 * @throws JVRException on error
	 * @see jvr#CR_BUSY
	 * @see jvr#CR_CEPT
	 * @see jvr#CR_CNCT
	 * @see jvr#CR_FAXTONE
	 * @see jvr#CR_NOANS
	 * @see jvr#CR_NODIALTONE
	 * @see jvr#CR_NORB
	 * @see jvr#CR_STOPD
	 * @see jvr#CR_ERROR
	 */
	public static native long ATDX_CPTERM (int dev) throws JVRException;

	/**
	 * Waits for a specified number of rings, then sets the given channel to the given hookstate.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-143.html#P19687_514938">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-110.html#P15555_411733">linux</a>)
	 * <p/>
	 * On Linux only: You can terminate a call to wtring by calling {@link dx#stopch} from another thread.  See the Linux Dialogic API
	 * docs for dx_wtring for all of the details.  Stopping dx_wtring is not supported on Win32 in 5.1.1/SP1.  If you need to terminate
	 * dx_wtring on win32, use shorter timeout values (say 1 second) and an out-of-band control flag instead.
	 * 
	 * @param dev Handle to device on which to perform action
	 * @param rings Number of rings to wait for.
	 * @param hookState The hookstate to set to after receiving the rings.  {@link jvr#DX_ONHOOK DX_ONHOOK} or {@link jvr#DX_OFFHOOK DX_OFFHOOK}.
	 * @param timeout Time, in seconds, to wait for the rings.  Specify -1 to wait forever.
	 * @return Nothing useful, exception on error.
	 * @throws JVRException on error.
	 */
	public static native int wtring (int dev, int rings, int hookState, int timeout) throws JVRException;

	/**
	 * dx_mreciottdata <b>(Win32 Only)</b>.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-108.html#P13382_351576">win32</a>)
	 * <p/>
	 * 
	 * @param dev handle to device on which to perform action
	 * @param iott
	 * @param tpt
	 * @param xpb
	 * @param mode
	 * @param tsinfo Instead of a pointer to a timeslot information structure, supply an array of SC_TSINFO objects, of length 2.
	 * @return nothing useful, exception on error 
	 * @throws JVRException
	 */
	public static native int mreciottdata (int dev, DX_IOTT[] iott, DV_TPT[] tpt, DX_XPB xpb, int mode, SC_TSINFO tsinfo) throws JVRException;

	/**
	 * Returns the hookstate of the specified handle.
	 *
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-54.html#P5253_156486">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-35.html#P3432_97826">linux</a>)
	 * <p/>
	 *
	 */
	public static native long ATDX_HOOKST (int dev) throws JVRException;

	/**
	 * Records voice data to a single <code>wav</code> file <b>(Win32 Only)</b>.
	 * <p/>
	 * Dialogic API Reference for dx_recwav: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-121.html#P16339_427682">win32</a>)
	 * <p/>
	 * <b>Example:</b>
	 * <pre>
	 * // Provide a filename for recording the file.  This can be
	 * // any valid filename such as "c:\\temp\\myfile.wav" or
	 * // "\\someother.wav".
	 * String filename = "foobar.wav";
	 *
	 * // I forget what the transfer parameter block is for, but
	 * // leaving it as 'null' will still produce valid WAV files.
	 * DX_XPB xpb = null;
	 *
	 * // Specify one or more termination clauses for the operation.
	 * DV_TPT tpt[] = DV_TPT.newArray(2);
	 * // This one means "record for no more than 5 seconds".
	 * tpt[0].tp_type = dx.IO_CONT;
	 * tpt[0].tp_termno = dx.DX_MAXTIME;
	 * tpt[0].tp_length = 50;
	 * tpt[0].tp_flags = dx.TF_MAXTIME;
	 * // This one means "stop when you hear 1 key press".
	 * tpt[1].tp_type = dx.IO_EOT;
	 * tpt[1].tp_termno = dx.DX_MAXDTMF;
	 * tpt[1].tp_length = 1;
	 * tpt[1].tp_flags = dx.TF_MAXDTMF;
	 *
	 * // Record to the wav file.  "RM_TONE" will cause a short
	 * // tone to be played before recording begins, as in
	 * // "Leave your message at the beap".  EV_SYNC is mandatory.
	 * dx.recwav(dxdev, filename, tpt, xpb, dx.RM_TONE|dx.EV_SYNC);
	 * 
	 * // And just for fun, play the file right back.  Obviously
	 * // this has nothing to do with recording and is optional.
	 * //
	 * // Reset the termination clause to only terminate on a key
	 * // press.  Since this is a playback operation, it will
	 * // automatically stop at the end of the file (so we don't
	 * // need to worry about a MAXTIME clause.
	 * tpt = DV_TPT.newArray(1);
	 * tpt[0].tp_type = dx.IO_EOT;
	 * tpt[0].tp_termno = dx.DX_MAXDTMF;
	 * tpt[0].tp_length = 1;
	 * tpt[0].tp_flags = dx.TF_MAXDTMF;
	 * dx.playwav(dxdev, filename, tpt, dx.EV_SYNC);
	 * </pre>
	 * <p>
	 * @param dev Handle to the device on which the action is performed.
	 * @param filename Filename of the <code>wav</code> file to record to.
	 * @param tpt Array of termination parameters.
	 * @param xpb Transfer parameter block.
	 * @param mode Bitmask mode.  <code>dx.EV_SYNC</code> is mandatory.  Include <code>dx.RM_TONE</code> to play audible tone before recording.
	 * @throws JVRException on any error.
	 */
	public static native int recwav (int dev, String filename, DV_TPT[] tpt, DX_XPB xpb, int mode) throws JVRException;

	/**
	 * Waits for an incoming call then picks up the line and returns caller id information.
	 * Note that you have to enable Caller ID detection on the chanel first using
	 * <code>dx.setparm(dev,dx.DXCH_CALLID,dx.DX_CALLIDENABLE)</code>
	 * <p>
	 * <b>NOTE</b>: Using my D/41ESC card, I have not managed to get Caller ID information
	 * during an incoming call.  I suspect it is my card, so you're system may have better
	 * luck.  If you manage to use this function successfully, please let me know so I can
	 * remove this notice.
	 * <p/>
	 * Dialogic API Reference for dx_wtcallid: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-142.html#P19570_510782">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-109.html#P15460_408427">linux</a>)
	 * <p>
	 * @param dev Handle to the device on which the action is performed.
	 * @param rings Number of rings to wait until the call is returned.
	 * @param timeout Number of seconds to wait for an incoming call.  Specify -1 to wait forever.
	 * @throws JVRException on any error.
	 */
	public static native String wtcallid (int dev, int rings, int timeout) throws JVRException;

	/**
	 * Gets the value of a Voice Board or Channel parameter.
	 * <p/>
	 * Dialogic API Reference for dx_getparm: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-96.html#P11808_301184">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-72.html#P9072_227077">linux</a>)
	 * <p>
	 */
	public static native int getparm (int dev, int parm) throws JVRException;

	/**
	 * Sets a Voice Board or Channel parameter.
	 * <p/>
	 * Dialogic API Reference for dx_setparm: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-131.html#P17829_465854">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-100.html#P14169_373476">linux</a>)
	 * <p>
	 */
	public static native int setparm (int dev, int parm, int value) throws JVRException;

	/**
	 * Dialogic API Reference for dx_play: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-110.html#P13736_362319">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-83.html#P10460_266440">linux</a>)
	 */
	public static native int play (int dev, DX_IOTT[] iott, DV_TPT[] tpt, int mode) throws JVRException;

	/**
	 * Dialogic API Reference for dx_reciottdata: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-119.html#P16041_421255">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-90.html#P12333_318600">linux</a>)
	 */
	public static native int reciottdata (int dev, DX_IOTT[] iott, DV_TPT[] tpt, DX_XPB xpb, int mode) throws JVRException;

	/**
	 * Dialogic API Reference for dx_playiottdata: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-112.html#P14406_382266">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-85.html#P11174_287720">linux</a>)
	 */
	public static native int playiottdata (int dev, DX_IOTT[] iott, DV_TPT[] tpt, DX_XPB xpb, int mode) throws JVRException;

	/**
	 * Used internally by JVR to pass I/O requests from the native
	 * context to the java context.
	 */
	private static byte[] uio_read (int fd, int len) {
		try {
			UserIO uio = UserIO.getUserIO(fd);
			if (uio == null) { return null; }
			if (uio.getHandler() == null) { logger.info("No handler defined for " + uio); }
			return uio.getHandler().read( uio, len );
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_read",e);
			return null;
		}
		/*
		try {
			// logger.info("fd = " + fd + ", len = " + len);
			byte[] ret = new byte[len];
			int r = f.read(ret);
			if (r == -1) {
				return null; 
			}
			// TODO: eliminate this buffer copy.
			// change this function to take a byte[] argument and
			// return the actual length instead of requiring a fully
			// fill array return value.
			byte[] ret2 = new byte[r];
			for (int x = 0; x < r; x++) { ret2[x] = ret[x]; }
			return ret2;
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_read",e);
			return null;
		}
		*/
	}

	/**
	 * Used internally by JVR to pass I/O requests from the native
	 * context to the java context.
	 */
	private static int uio_write (int fd, byte[] buffer) {
		try {
			UserIO uio = UserIO.getUserIO(fd);
			if (uio == null) { return -1; }
			if (uio.getHandler() == null) { logger.info("No handler defined for " + uio); }
			return uio.getHandler().write( uio, buffer );
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_write",e);
			return -1;
		}
		/*
		try {
			// logger.info("fd = " + fd + " buffer = "+(buffer==null?"null":""+buffer.length));
			f.write(buffer);
			return buffer.length;
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_write",e);
			return -1;
		}
		*/
	}

	/**
	 * Used internally by JVR to pass I/O requests from the native
	 * context to the java context.
	 */
	private static long uio_seek (int fd, long offset, int whence) {
		try {
			UserIO uio = UserIO.getUserIO(fd);
			if (uio == null) { return -1; }
			if (uio.getHandler() == null) { logger.info("No handler defined for " + uio); }
			return uio.getHandler().seek( uio, offset, whence );
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_seek",e);
			return -1;
		}
		/*
		try {
			// logger.info("fd = " + fd + ", offset = " + offset + ", whence = " + whence);
			if (whence == dx.SEEK_SET) { // SEEK_SET
				f.seek(offset);
			}
			else if (whence == dx.SEEK_CUR) { // SEEK_CUR
				f.seek( f.getFilePointer() + offset );
			}
			else if (whence == dx.SEEK_END) { // SEEK_END
				f.seek( f.length() + offset );
			}
			else {
				logger.info("Unknown whence value: " + whence);
				return -1;
			}
			return f.getFilePointer();
		}
		catch (IOException e) {
			logger.throwing(dx.class.getName(),"uio_seek",e);
			return -1;
		}
		*/
	}

	/**
	 * Used by dx static initialization block to prepare the native context.
	 */
	private static native void initDxClass() throws JVRException;
	static {
		try {
			initDxClass();
			// TODO: remove this development hack.
			// f = new RandomAccessFile("c:\\code\\oreivr\\dict\\eng\\misc\\maximumRecordingTimeExceeded.vox","rw");
			// logger.info(""+f.length());
		}
		catch (JVRException e) {
			RuntimeException e2 = new RuntimeException(e); 
			logger.throwing(dx.class.getName(),"<init>",e2);
			throw(e2);
		}
		/*
		catch (FileNotFoundException e) {
			RuntimeException e2 = new RuntimeException(e); 
			logger.throwing(dx.class.getName(),"<init>",e2);
			throw(e2);
		}
		catch (IOException e) {}
		*/
	}

	/**
	 * Dialogic API Reference for dx_deltones: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-81.html#P8976_234678">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-62.html#P7018_176372">linux</a>)
	 */
	public static native int deltones (int dev) throws JVRException;

	/**
	 * Dialogic API Reference for dx_chgdur: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-73.html#P7968_215055">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-54.html#P6030_155416">linux</a>)
	 */
	public static native int chgdur (int tonetype, int ontime, int ondev, int offtime, int offdev) throws JVRException;

	/**
	 * Dialogic API Reference for dx_chgfreq: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-74.html#P8155_218553">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-55.html#P6208_159046">linux</a>)
	 */
	public static native int chgfreq (int tonetype, int freq1, int freq1dev, int freq2, int freq2dev) throws JVRException;

	/**
	 * Dialogic API Reference for dx_chgrepcnt: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-75.html#P8349_222365">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-56.html#P6393_162997">linux</a>)
	 */
	public static native int chgrepcnt (int tonetype, int repcnt) throws JVRException;

	/**
	 * Dialogic API Reference for dx_initcallp: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/pgmgd3/1456-04-103.html#P12829_336293">win32</a>,<a
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/vox_api/1453-02-78.html#P9831_249321">linux</a>)
	 */
	public static native int initcallp (int dev) throws JVRException;

}
