package net.threebit.jvr;

/*
 * $Id: DV_TPT.java,v 1.6 2004/07/02 18:11:37 kevino Exp $
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
 * Java representation of the DV_TPT struct.
 */

public class DV_TPT extends JVRBaseClass {

	public int rfu;
	public int tp_data;
	public int tp_flags;
	public int tp_length;
	public int tp_termno;
	public int tp_type;

	/**
	 *
	 */
	public String toString() {
		return
			"DV_TPT{" +
			"tp_type=" + tp_type+"; " +
			"tp_termno=" + tp_termno+"; " +
			"tp_length=" + tp_length+"; " +
			"tp_flags=" + tp_flags+"; " +
			"tp_data=" + tp_data +
			"}"
		;
	}

	/**
	 *
	 */
	public static String toString (DV_TPT[] tpt) {
		if (tpt == null) { return "DV_TPT[]{null};"; }
		if (tpt.length == 0) { return "DV_TPT[0]{};"; }
		String s = "DV_TPT["+tpt.length+"]{\n";
		for (int x = 0; x < tpt.length; x++) { s += ""+tpt[x]; }
		s += "}";
		return s;
	}

	/**
	 *
	 */
	public static DV_TPT[] newArray (int length) {
		if (length <= 0) { throw new AssertionError("length must be greater than 0."); }
		DV_TPT result[] = new DV_TPT[length];
		for (int x = 0; x < length; x++) {
			result[x] = new DV_TPT();
		}
		return result;
	}

}
