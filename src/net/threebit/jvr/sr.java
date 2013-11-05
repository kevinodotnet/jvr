package net.threebit.jvr;

/*
 * $Id: sr.java,v 1.18 2004/07/02 18:11:38 kevino Exp $
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
 * Java encapsulation of the Dialogic <b>Standard Runtime Library</b> Software
 * (functions matching <b>sr_*</b>).
 */

public abstract class sr extends jvr {

	/**
	 * sr_getevtdev.
	 * <b>Win32 Only</b>.
	 * @param ehandle 'unsigned long' becomes 'long'
	 */
	public static native long getevtdev(long ehandle) throws JVRException;

	/**
	 * sr_getevttype.
	 * <b>Win32 Only</b>.
	 * @param ehandle 'unsigned long' becomes 'long'
	 */
	public static native long getevttype(long ehandle) throws JVRException;

	/**
	 * sr_getevtdev.
	 * <b>Linux Only</b>.
	 */
	public static native int getevtdev() throws JVRException;

	/**
	 * sr_getevttype.
	 * <b>Linux Only</b>.
	 */
	public static native long getevttype() throws JVRException;

	/**
	 * sr_waitevt.  Not supported.  Always throws an exception.
	 * @deprecated No longer supported.
	 */
	public static final long waitevt() throws JVRException {
		throw new RuntimeException("Function not supported.");
	}

	/**
	 * sr_waitevtEx.  Not supported.  Always throws an exception.
	 * @deprecated
	 */
	public static final long waitevtEx() {
		throw new RuntimeException("Function not supported.");
	}

	/**
	 * ATDV_LASTERR.
	 */
	public static native long ATDV_LASTERR (int dev) throws JVRException;

	/**
	 * ATDV_ERRMSGP.
	 */
	public static native String ATDV_ERRMSGP (int dev) throws JVRException;

	/**
	 * ATDV_NAMEP.
	 */
	public static native String ATDV_NAMEP (int dev) throws JVRException;

	/**
	 * sr_getevtlen.
	 * <b>Windows Only</b>.
	 * <p>
	 * Since the value returned by this function is only valid within the
	 * scope of the original signal handler, you probably do not want to
	 * call this function directly.  Instead, register as an
	 * event listener {@link jvr#addEventListener}.
	 *
	 * @throws JVRException if there is no current event to report on.
	 */
	public static native long getevtlen (long ehandle) throws JVRException;

	/**
	 * sr_getevtlen.
	 * <b>Linux Only</b>.
	 * <p>
	 * Since the value returned by this function is only valid within the
	 * scope of the original signal handler, you probably do not want to
	 * call this function directly.  Instead, register as an
	 * event listener {@link jvr#addEventListener}.
	 *
	 * @throws JVRException if there is no current event to report on.
	 */
	public static native long getevtlen () throws JVRException;

	/**
	 * sr_getevtdatap.
	 * <b>Windows Only</b>.
	 * <p>
	 * Note that the "p" has been dropped in the method name since in Java
	 * we do not return a pointer.
	 * <p>
	 * An Object is returned because depending on the type of event, several
	 * different cases are possible.  The JVR event
	 * dispatcher will handle each return value.
	 */
	public static native Object getevtdata (long ehandle) throws JVRException;

	/**
	 * sr_getevtdatap.
	 * <b>Linux Only</b>.
	 * <p>
	 * Note that the "p" has been dropped in the method name since in Java
	 * we do not return a pointer.
	 * <p>
	 * An Object is returned because depending on the type of event, several
	 * different cases are possible.  The JVR event
	 * dispatcher will handle each return value.
	 */
	public static native Object getevtdata () throws JVRException;

}
