package net.threebit.jvr;

/*
 * $Id: GC_START_STRUCT.java,v 1.9 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the GC_START_STRUCT struct.
 */

public class GC_START_STRUCT extends JVRBaseClass {

	public int num_cclibs;
	public CCLIB_START_STRUCT[] cclib_list = null;

	/**
	 * Constructs a new GC_START_STRUCT object with a cclib_list member of the specified length.
	 */
	public static GC_START_STRUCT newArray (int cclength) {
		GC_START_STRUCT gcss = new GC_START_STRUCT();
		gcss.num_cclibs = cclength;
		gcss.cclib_list = new CCLIB_START_STRUCT[cclength];
		for (int x = 0; x < cclength; x++) {
			gcss.cclib_list[x] = new CCLIB_START_STRUCT();
		}
		return gcss;
	}

}
