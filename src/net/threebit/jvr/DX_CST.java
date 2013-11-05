package net.threebit.jvr;

/*
 * $Id: DX_CST.java,v 1.8 2004/07/02 18:11:37 kevino Exp $
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
 * Java equivalent of the Dialogic DX_CST struct.
 */

public class DX_CST extends JVRBaseClass {

	/** "unsigned short" becomes "int" */
	public int cst_event;

	/** "unsigned short" becomes "int" */
	public int cst_data;

	/**
	 *
	 */
	public String toString() {
		String s = "DX_CST{";

		if (cst_event == dx.DE_DIGITS) { s += "DE_DIGITS"; }
		else if (cst_event == dx.DE_LCOFF) { s += "DE_LCOFF"; }
		else if (cst_event == dx.DE_LCON) { s += "DE_LCON"; }
		else if (cst_event == dx.DE_LCREV) { s += "DE_LCREV"; }
		else if (cst_event == dx.DE_RINGS) { s += "DE_RINGS"; }
		else if (cst_event == dx.DE_RNGOFF) { s += "DE_RNGOFF"; }
		else if (cst_event == dx.DE_SILOFF) { s += "DE_SILOFF"; }
		else if (cst_event == dx.DE_SILON) { s += "DE_SILON"; }
		else if (cst_event == dx.DE_TONEOFF) { s += "DE_TONEOFF"; }
		else if (cst_event == dx.DE_TONEON) { s += "DE_TONEON"; }
		else if (cst_event == dx.DE_WINK) { s += "DE_WINK"; }
		else if (cst_event == dx.DX_OFFHOOK) { s += "DX_OFFHOOK"; }
		else if (cst_event == dx.DX_ONHOOK) { s += "DX_ONHOOK"; }
		else { s += cst_event; }
		s += ";";

		if (cst_event == dx.DE_WINK || cst_event == dx.DX_OFFHOOK || cst_event == dx.DX_ONHOOK) {
			s += "n/a";
		}
		else if (cst_event == dx.DE_DIGITS) {
			char digit = (char) ((cst_data<<8)>>8);
			s += "'"+digit+"'";
		}
		else if (cst_event == dx.DE_SILON || cst_event == dx.DE_SILOF) {
			s += cst_data;
			s += "ms";
		}
		else {
			s += cst_data;
		}
		s += "}";
		return s;
	}

}

