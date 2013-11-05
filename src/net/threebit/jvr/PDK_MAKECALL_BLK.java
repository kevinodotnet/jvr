package net.threebit.jvr;

/*
 * $Id: PDK_MAKECALL_BLK.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic PDK_MAKECALL_BLK structure.
 */

public class PDK_MAKECALL_BLK extends CCLIB_MAKECALL_BLK {

	/**
	 * One of 
	 */
	long flags = -1;

	/**
	 * Reserved for future use.
	 */
	long[] ul_rfu = new long[4];

	/**
	 *
	 */
	public PDK_MAKECALL_BLK() {
		flags = -1;
		ul_rfu[0] = -1;
		ul_rfu[1] = -1;
		ul_rfu[2] = -1;
		ul_rfu[3] = -1;
	}

	/**
	 *
	 */
	public String toString() { 
		String s = "PDK_MAKECALL_BLK{";
		s += "flags=";
		if (flags == jvr.NO_CALL_PROGRESS) {
			s += "NO_CALL_PROGRESS";
		}
		else if (flags == jvr.MEDIA_TYPE_DETECT ) {
			s += "MEDIA_TYPE_DETECT";
		}
		else if (flags == -1) {
			s += "<init>";
		}
		else {
			s += flags;
		}
		/*
		s += ",";
		s += "ul_rfu=[";
		s += ul_rfu[0]+",";
		s += ul_rfu[1]+",";
		s += ul_rfu[2]+",";
		s += ul_rfu[3]+",";
		s += "]";
		*/
		s += "}";
		return s;
	}

}
