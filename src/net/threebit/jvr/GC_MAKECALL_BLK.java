package net.threebit.jvr;

/*
 * $Id: GC_MAKECALL_BLK.java,v 1.4 2004/07/02 18:11:38 kevino Exp $
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
 * Java representation of the GC_MAKECALL_BLK struct.
 */

public class GC_MAKECALL_BLK extends JVRBaseClass {

	/**
	 *
	 */
	public GCLIB_MAKECALL_BLK gclib = null;

	/**
	 * <code>CCLIB_MAKECALL_BLK</code> is not a Dialogic component, but is
	 * used here to be more descriptive than <code>java.lang.Object</code>.
	 */
	public CCLIB_MAKECALL_BLK cclib = null;

	/**
	 *
	 */
	public String toString() {
		return "GC_MAKECALL_BLK{"+gclib+","+cclib+"}";
	}

}
