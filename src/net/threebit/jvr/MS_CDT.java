package net.threebit.jvr;

/*
 * $Id: MS_CDT.java,v 1.4 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic MS_CST structure.
 */

public class MS_CDT extends JVRBaseClass {

	public int chan_num = 0;
	public int chan_sel = 0;
	public int chan_attr = 0;

	/**
	 * Returns the equivalient of MS_CDT.chan_lts;
	 */
	public int listenTS() { return chan_attr; }

	/**
	 *
	 */
	public static MS_CDT[] newArray (int size) {
		if (size <= 0) { throw new AssertionError("size must be greater than 0."); }
		MS_CDT result[] = new MS_CDT[size];
		for (int x = 0; x < size; x++) { result[x] = new MS_CDT(); }
		return result;
	}

	/**
	 *
	 */
	public String toString() { return "MS_CDT{"+chan_num+","+chan_sel+","+chan_attr+"}"; }

}
