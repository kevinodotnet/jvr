package net.threebit.jvr;

/*
 * $Id: TN_GENCAD.java,v 1.5 2004/07/02 18:11:38 kevino Exp $
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

public class TN_GENCAD extends JVRBaseClass {

	public int cycles = -1;
	public int numsegs = 0;
	public int offtime[] = new int[4];
	public TN_GEN tone[] = new TN_GEN[4];

	public TN_GENCAD() {
		for (int x = 0; x < 4; x++) { 
			tone[x] = new TN_GEN(); 
		}
	}

	public String toString() {
		String s = "TN_GENCAD{cycles:"+cycles+";tones:"+numsegs;
		if (numsegs > 0) {
			s+=";offtime:";
			for (int x = 0; x < numsegs-1; x++) { s += offtime[x]; if (x!=numsegs) { s+=","; }}
			s+=";tones:";
			for (int x = 0; x < numsegs; x++) { s += "["+x+"]"+tone[x]; }
		}
		s += "}";
		return s;
	}

}
