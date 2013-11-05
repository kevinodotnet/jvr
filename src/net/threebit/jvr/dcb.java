package net.threebit.jvr;

/*
 * $Id: dcb.java,v 1.1 2004/12/16 01:38:15 kevino Exp $
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
 * (functions matching <b>dcb_*</b>).
 */

public abstract class dcb extends jvr {

	/**
	 * dcb_open.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="">win32</a>,<a 
	 * href="">linux</a>)
	 */
	public static native int open (String name, int flags) throws JVRException;

	/**
	 * dcb_close.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="">win32</a>,<a 
	 * href="">linux</a>)
	 */
	public static native int close (int dev) throws JVRException;

	/**
	 * dcb_delconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="">win32</a>,<a 
	 * href="">linux</a>)
	 */
	public static native int delconf (int dev, int confID) throws JVRException;

	/**
	 * dcb_monconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="">win32</a>,<a 
	 * href="">linux</a>)
	 * <p>
	 * Instead of returning the success/error status, this method returns the
	 * XMIT timeslot of the conference.  There is no loss of signalling because on
	 * an error condition, estconf() will throw an Exception.
	 */
	public static native long monconf (int dev, int confID) throws JVRException;

	/**
	 * dcb_unmonconf.
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="">win32</a>,<a 
	 * href="">linux</a>)
	 */
	public static native int unmonconf (int dev, int confID) throws JVRException;

}
