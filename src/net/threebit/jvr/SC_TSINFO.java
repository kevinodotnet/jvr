package net.threebit.jvr;

/*
 * $Id: SC_TSINFO.java,v 1.7 2004/07/02 18:11:38 kevino Exp $
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

public class SC_TSINFO extends JVRBaseClass {

	public long sc_numts = 0;
	public long[] sc_tsarray = null;

	/**
	 * Accessor for sc_tsarray[0].
	 */
	public long ts() {
		return ts(0);
	}

	/**
	 * Accessfor for sc_tsarray[offset]
	 */
	public long ts (int offset) {
		if (sc_tsarray == null) { throw new RuntimeException("sc_tsarray is null"); }
		if (sc_tsarray.length <= offset) { throw new RuntimeException("invalid offset: " + offset + " length: " + sc_tsarray.length); }
		return sc_tsarray[offset];
	}

	/**
	 * Adds the given timeslot identifier to the list of timeslots recorded
	 * in this object.  Saves having to recreate and copy the long[] each 
	 * time an addition is made.
	 */
	public void add (long ts) {
		// Assert
		if (sc_numts < 0) { throw new IllegalStateException("sc_numts cannot be less than zero."); }
		// Initialize if necessary
		if (sc_tsarray == null) { sc_tsarray = new long[(int)sc_numts]; }
		// Add the new timeslot
		sc_numts++;
		if (sc_tsarray.length < sc_numts) {
			long[] newArray = new long[(int)sc_numts];
			for (int x = 0; x < (sc_numts-1); x++) { newArray[x] = sc_tsarray[x]; }
			sc_tsarray = newArray;
		}
		sc_tsarray[(int)(sc_numts-1)] = ts;
	}

	/**
	 *
	 */
	public String toString() {
		String s = "SC_TSINFO{";
		s += "sc_numts="+sc_numts+";";
		s += "sc_tsarrayp=";
		if (sc_numts == 0) {
			s += "null";
		}
		else {
			for (int x = 0; x < sc_numts; x++) {
				s += sc_tsarray[x];
				if ((x+1)!=sc_numts) { s += ","; }
			}
		}
		s+= "}";
		return s;
	}

}
