package net.threebit.jvr;

/*
 * $Id: ag.java,v 1.6 2005/01/19 02:44:25 kevino Exp $
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
 * Java encapsulation of the Dialogic <b>Voice</b> Software
 * (functions matching <b>ag_*</b>).
 */

public class ag extends jvr {

	/**
	 * ag_listen
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/scbapint/0439-04-12.html#P1175_39521">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/scrtfunc/0313-04-09.html#P1041_27501">linux</a>)
	 */
	public static native int listen (int dev, SC_TSINFO tsinfo) throws JVRException;

	/**
	 * Equivalent to:
	 * <pre>
	 * SC_TSINFO tsinfo = new SC_TSINFO();
	 * tsinfo.add(ts);
	 * return listen(dev,tsinfo);
	 * </pre>
	 */
	public static int listen (int dev, long ts) throws JVRException {
		SC_TSINFO tsinfo = new SC_TSINFO();
		tsinfo.add(ts);
		return listen(dev,tsinfo);
	}

	/**
	 * ag_unlisten
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/scbapint/0439-04-13.html#P1314_43910">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/scrtfunc/0313-04-10.html#P1175_31848">linux</a>)
	 */
	public static native int unlisten (int dev) throws JVRException;

	/**
	 * ag_getxmitslot
	 * <p/>
	 * Dialogic API Reference for this function: (<a 
	 * href="http://resource.intel.com/telecom/support/releases/winnt/SR511/docs/htmlfiles/scbapint/0439-04-11.html#P1044_36109">win32</a>,<a 
	 * href="http://resource.intel.com/telecom/support/releases/unix51/linux51/SR5.1_Linux/Onldoc/html_files/scrtfunc/0313-04-08.html#P916_24170">linux</a>)
	 */
	public static native SC_TSINFO getxmitslot (int dev); 

}
