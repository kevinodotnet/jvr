package net.threebit.jvr;

/*
 * $Id: GC_CCLIB_STATUSALL.java,v 1.3 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GC_CCLIB_STATUSALL structure.
 */

public class GC_CCLIB_STATUSALL extends JVRBaseClass {

	/**
	 *
	 */
	public GC_CCLIB_STATE cclib_state[] = null;

	/**
	 *
	 */
	public String toString() {
		if (cclib_state == null) {
			return "GC_CCLIB_STATUSALL{null};";
		}
		else {
			String s = "GC_CCLIB_STATUSALL{";
			if (cclib_state.length > 1) { s += "\n"; }
			for (int x = 0; x < cclib_state.length; x++) {
				if (cclib_state.length > 1) { s += "\t"; }
				s += cclib_state[x].name;
				s += "\t";
				if (cclib_state[x].state == gc.GC_CCLIB_AVAILABLE) { s += "Available"; }
				else if (cclib_state[x].state == gc.GC_CCLIB_CONFIGURED) { s += "Configured"; }
				else if (cclib_state[x].state == gc.GC_CCLIB_FAILED) { s += "Failed"; }
				else { s += cclib_state[x].state; }
				// if (!((x+1)==cclib_state.length)) { s += " "; }
				if (cclib_state.length > 1) { s += "\n"; }
			}
			s += "}";
			return s;
		}
	}
}
