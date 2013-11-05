package net.threebit.jvr;

/*
 * $Id: gc.java,v 1.32 2004/12/19 15:46:57 kevino Exp $
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
 * Java encapsulation of the Dialogic <b>GlobalCall</b> Software
 * (functions matching <b>gc_*</b>).
 * <p>
 * Since JVR uses the GlobalCall event functions within it's own event handling
 * system, this class contains a static initialization block that forces the execution
 * of {@link gc#Start}.  Ergo, applications do not need to start the GlobalCall
 * system manually.  The static initialization is as follows:
 * <pre>
 * static {
 *   gc.Start(null); // Starts all call control libraries.
 * }
 * </pre>
 * <p>
 * Note to self:
 * <br>
 * <a href="http://resource.intel.com/telecom/support/applets/winnt/callanalysis/index.htm">
 * Why & how to incorporate Call Analysis within E1/T1 Application
 * </a>.<br>
 * <a href="http://resource.intel.com/telecom/support/appnotes/8775/index.htm">More of the same</a>.
 * <a href="http://resource.intel.com/telecom/support/appnotes/8775/8775FN01.pdf">More of the same</a>.
 */

public abstract class gc extends jvr {

	static {
		try {
			// Start a subset of all of the gc protocols.
			GC_START_STRUCT gcss = GC_START_STRUCT.newArray(2);
			// TODO: review what other protocols we will need.
			gcss.cclib_list[0].cclib_name = "GC_PDKRT_LIB";
			gcss.cclib_list[0].cclib_data = null;
			// TODO: May need ISDN
			gcss.cclib_list[1].cclib_name = "ISDN";
			gcss.cclib_list[1].cclib_data = null;
			// Start global call
			gc.Start(gcss);
		}
		catch (JVRException e) {
			RuntimeException e2 = new RuntimeException("Error starting the GlobalCall package.",e);
			logger.throwing(gc.class.getName(),"<init>",e2);
			throw(e2);
		}
	}

	/**
	 * gc_Start() starts the call control libraries.
	 * This method is called with a <code>null</code> argument automatically
	 * by JVR when the <code>gc</code> class is initialized.  In other words: you never 
	 * need to call this function.  In fact, errors will occur if you try.  Since <code>null</code>
	 * is passed in the default implementation there is no opportunity to pass in custom
	 * startup parameters.  This will be dealt with in a future release.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-106.html#P14636_453684">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-106.html#P14636_453684">linux</a>)
	 * <p/>
	 */
	public static native int Start (GC_START_STRUCT start) throws JVRException;

	/**
	 * gc_Stop() stops the call control libraries.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-108.html#P14854_460571">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-108.html#P14854_460571">linux</a>)
	 * <p/>
	 */
	public static native int Stop () throws JVRException;

	/**
	 * gc_CCLibStatusEx.
	 * <p>
	 * This method has two modes of operations:
	 * <ul>
	 * <li>
	 * <code>cclib_name == "GC_ALL_LIB"</code>.  In this mode, the supplied <code>status</code>
	 * object will be used to return the status of all of the Call Control Libraries.
	 * </li>
	 * <li>
	 * Otherwise, the <code>status</code> parameter is ignored (supply NULL if need be) and the
	 * status of the specified Call Control library is provided by the method return value.  This
	 * differs from the original Dialogic spec that requires an (int*) argument.
	 * </li>
	 * </ul>
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-27.html#P3738_81636">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-27.html#P3738_81636">linux</a>)
	 */
	public static native int CCLibStatusEx (String cclib_name, GC_CCLIB_STATUSALL status) throws JVRException;

	/**
	 * gc_OpenEx.
	 * <p>
	 * Instead of taking a (LINEDEV*) parameter, a <code>long</code> is returned instead.
	 * <p>
	 * For now, the value of <code>userAttr</code> is ignored.  Specify null instead.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-73.html#P10059_294962">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-73.html#P10059_294962">linux</a>)
	 * <p/>
	 */
	public static native long OpenEx (String deviceName, int mode, Object userAttr) throws JVRException;

	/**
	 * gc_Close.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-28.html#P3849_86027">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-28.html#P3849_86027">linux</a>)
	 * <p/>
	 */
	public static native int Close (long linedev) throws JVRException;

	/**
	 * gc_GetMetaEventEx (Windows only).
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-55.html#P7685_218832">win32</a>)
	 * <p/>
	 */
	public static native MetaEvent GetMetaEventEx(long eventHandle) throws JVRException;

	/**
	 * gc_GetMetaEvent (Linux only)
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-54.html#P7391_207914">linux</a>)
	 * <p/>
	 */
	public static native MetaEvent GetMetaEvent() throws JVRException;

	/**
	 * gc_ResetLineDev.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-80.html#P11182_334939">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-80.html#P11182_334939">linux</a>)
	 * <p/>
	 */
	public static native int ResetLineDev (long linedev, long mode) throws JVRException;

	/**
	 * gc_MakeCall.
	 * <p>
	 * Instead of taking a <code>(long *crn)</code> parameter, the CRN value is returned
	 * as a <code>long</code>.  Error status is provided via an exception.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-71.html#P9803_283899">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-71.html#P9803_283899">linux</a>)
	 * <p/>
	 */
	public static native long MakeCall (long linedev, String numberString, GC_MAKECALL_BLK makecall, int timeout, long mode) throws JVRException;

	/**
	 * gc_DropCall.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-32.html#P4293_101409">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-32.html#P4293_101409">linux</a>)
	 * <p/>
	 */
	public static native int DropCall (long crn, int cause, long mode) throws JVRException;
	
	/**
	 * gc_ReleaseCallEx.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-76.html#P10643_315698">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-76.html#P10643_315698">linux</a>)
	 * <p/>
	 */
	public static native int ReleaseCallEx (long crn, long mode) throws JVRException;

	/**
	 * gc_GetCallInfo.
	 * <p>
	 * Instead of taking a <code>(void*)</code> argument, this method returns an Object
	 * that is a copy of the requested information.  Since the type of information returned
	 * depends on the id of the information requested, this method may return several
	 * different types of objects.
	 * <p>
	 * <table style='border-collapse: collapse;' border="1">
	 * <tr><th>info_id Parameter</th><th>Returned Object</th></tr>
	 * <tr>
	 *   <td>
	 *   CALLINFOTYPE<br>
	 *   CALLNAME<br>
	 *   CALLTIME<br>
	 *   </td>
	 *   <td style='valign: top';>java.lang.String</td>
	 * </tr>
	 * <tr>
	 *   <td>
	 *   CATEGORY_DIGIT<br>
	 *   </td>
	 *   <td style='valign: top';>java.lang.String</td>
	 * </tr>
	 * <tr>
	 *   <td>
	 *   DESTINATION_ADDRESS<br>
	 *   ORIGINATION_ADDRESS
	 *   </td>
	 *   <td style='valign: top';>java.lang.String</td>
	 * </tr>
	 * <tr>
	 *   <td>
	 *   CONNECT_TYPE<br>
	 *   PRESENT_RESTRICT 
	 *   </td>
	 *   <td style='valign: top';>java.lang.Integer</td>
	 * </tr>
	 * </table>
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-43.html#P5733_151623">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-43.html#P5733_151623">linux</a>)
	 * <p/>
	 */
	public static native Object GetCallInfo (long crn, int infoId) throws JVRException;

	/**
	 * gc_Listen.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-69.html#P9391_270478">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-69.html#P9391_270478">linux</a>)
	 * <p/>
	 */
	public static native int Listen (long linedev, SC_TSINFO tsinfo, long mode) throws JVRException;

	/**
	 * gc_UnListen.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-113.html#P15367_476602">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-113.html#P15367_476602">linux</a>)
	 * <p/>
	 */
	public static native int UnListen (long linedev, long mode) throws JVRException;

	/**
	 * gc_WaitCall.
	 * <p>
	 * Instead of taking a <code>(CRN*)</code> argument, when running in EV_SYNC mode, the
	 * call reference number will be returned.  Otherwise, the return value is unmodified,
	 * except that it is a <code>long</code> instead of an <code>int</code>.
	 * <p>
	 * The <code>GC_WAITCALL_BLK</code> argument is omitted since it is reserved for future
	 * use.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-119.html#P16006_494509">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-119.html#P16006_494509">linux</a>)
	 * <p/>
	 */
	public static native long WaitCall (long linedev, int timeout, long mode) throws JVRException;

	/**
	 * gc_AnswerCall.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-17.html#P2619_41701">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-17.html#P2619_41701">linux</a>)
	 * <p/>
	 */
	public static native int AnswerCall (long crn, int rings, long mode) throws JVRException;

	/**
	 * gc_CRN2LineDev.
	 * <p>
	 * Instead of taking a <code>(CRN*)</code> argument, a <code>long</code> is returned.
	 * Error status is reflected by throwing an exception.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-30.html#P4082_94407">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-30.html#P4082_94407">linux</a>)
	 * <p/>
	 */
	public static native long CRN2LineDev (long crn) throws JVRException;

	/**
	 * gc_AttachResource.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-19.html#P2781_46540">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-19.html#P2781_46540">linux</a>)
	 * <p/>
	 */
	public static native int AttachResource 
		(long linedev, int resourceh, GC_PARM_BLK resourceAttr, GC_PARM_BLK returnAttr, int resourceType, long mode) 
		throws JVRException;

	/**
	 * gc_Detach.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-31.html#P4167_96494">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-31.html#P4167_96494">linux</a>)
	 * <p/>
	 */
	public static native int Detach (long linedev, int voiceH, long mode) throws JVRException;

	/**
	 * gc_GetCallState.
	 * <p>
	 * Instead of taking an <code>(int*)</code>, the call state is returned.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-45.html#P6032_161291">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-45.html#P6032_161291">linux</a>)
	 * <p/>
	 */
	public static native int GetCallState (long crn) throws JVRException;

	/**
	 * gc_GetXmitSlot.
	 * <p>
	 * An SC_TSINFO object is returned instead of taking an <code>(SC_TSINFO*)</code> argument.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-65.html#P8890_255893">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-65.html#P8890_255893">linux</a>)
	 * <p/>
	 */
	public static native SC_TSINFO GetXmitSlot (long linedev) throws JVRException;

	/**
	 * gc_GetResourceH.
	 * <p>
	 * The resource handle is returned.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-59.html#P8109_231952">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-59.html#P8109_231952">linux</a>)
	 * <p/>
	 */
	public static native int GetResourceH (long linedev, int resourceType) throws JVRException;

	/**
	 * gc_GetCTInfo.
	 * <p>
	 * Due to problems when compiling the native library with this function enabled,
	 * it has been deprecated temporarily.
	 * <pre>
	 * symbolput.cpp:29: `CT_DFD41D' undeclared (first use this function)
	 * symbolput.cpp:29: (Each undeclared identifier is reported only once for 
	 * each function it appears in.)
	 * symbolput.cpp:31: `CT_DFMSI' undeclared (first use this function)
	 * symbolput.cpp:37: `CT_NTIPT' undeclared (first use this function)
	 * </pre>
	 * @deprecated Due to native library problems.  This is resolvable just it
	 * is not a blocker at this time (easier to disable than to fix).
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-48.html#P6659_185861">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-48.html#P6659_185861">linux</a>)
	 * <p/>
	 */
	public static CT_DEVINFO GetCTInfo (long linedev) throws JVRException {
		throw new RuntimeException("This function is temporarily deprecated.  Do not use.");
	}

	/**
	 * gc_SetChanState.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-96.html#P13271_404822">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-96.html#P13271_404822">linux</a>)
	 * <p/>
	 */
	public static native int SetChanState (long linedev, int state, long mode) throws JVRException;

	/**
	 * gc_LoadDxParm.
	 * <p>
	 * The error parameters are omitted.  If an error occurs, the error text is included
	 * in the thrown exception.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-70.html#P9521_274553">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-70.html#P9521_274553">linux</a>)
	 * <p/>
	 */
	public static native int LoadDxParm (long linedev, String filename) throws JVRException;

	/**
	 * gc_CallAck.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-21.html#P3065_58531">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-21.html#P3065_58531">linux</a>)
	 * <p/>
	 */
	public static native int CallAck (long crn, GC_CALLACK_BLK callack, long mode) throws JVRException;

	/**
	 * gc_GetVoiceH.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-64.html#P8793_252813">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-64.html#P8793_252813">linux</a>)
	 * <p/>
	 */
	public static native int GetVoiceH (long linedev) throws JVRException;

	/**
	 * A fusion of gc_SetInfoElem and gc_SetUserInfo.
	 * @deprecated Use SetConfigData instead.
	 */
	public static native int SetInfoElem (int targetType, long targetId, IE_BLK ie, int duration) throws JVRException;

	/**
	 * gc_SetConfigData.
	 * <p>
	 * Returns long instead of int.  Long value is equivalent to the original (long * request_idp) pointer.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-97.html#P13396_408664">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-97.html#P13396_408664">linux</a>)
	 * <p/>
	 */
	public static native long SetConfigData (int targetType, long targetId, GC_PARM_BLK data, int timeout, int updateCond, long mode) throws JVRException;

	/**
	 * gc_util_insert_parm_val.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-117.html#P15790_488216">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-117.html#P15790_488216">linux</a>)
	 * <p/>
	 */
	public static native int util_insert_parm_val (GC_PARM_BLK parm, int setId, int parmId, int size, long data) throws JVRException;

	/**
	 * gc_SetCallingNum.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-94.html#P13069_398885">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-94.html#P13069_398885">linux</a>)
	 * <p/>
	 */
	public static native int SetCallingNum (long linedev, String number) throws JVRException;

	/**
	 * gc_GetNetworkH.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-57.html#P7898_225329">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-57.html#P7898_225329">linux</a>)
	 * <p/>
	 */
	public static native int GetNetworkH (long linedev) throws JVRException;

	/**
	 * gc_SetParm.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-100.html#P13908_429392">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-100.html#P13908_429392">linux</a>)
	 * <p/>
	 */
	public static native int SetParm (long linedev, int parmId, GC_PARM parm) throws JVRException;

	/**
	 * gc_SetCallProgressParm.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/gcapi/0387-09-95.html#P13162_401569">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/gcapi/0387-09-95.html#P13162_401569">linux</a>)
	 * <p/>
	 */
	public static native int SetCallProgressParm (long linedev, DX_CAP cap) throws JVRException;

}
