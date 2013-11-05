package net.threebit.jvr;

/*
 * $Id: TN_GEN.java,v 1.5 2004/07/02 18:11:38 kevino Exp $
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
 * Ringback for North America is 440/480 at -19db, 2 second duration.
 * When adding to TN_GENCAD, add 4 second off time.
 */

public class TN_GEN extends JVRBaseClass {

	public int tg_dflag;
	public int tg_freq1;
	public int tg_freq2;
	public int tg_ampl1;
	public int tg_ampl2;
	public int tg_dur;

	public String toString() {
		return "TN_GEN{"+
			"tg_dflag: "+tg_dflag+"; "+
			"tg_freq1: "+tg_freq1+"; "+
			"tg_freq2: "+tg_freq2+"; "+
			"tg_ampl1: "+tg_ampl1+"; "+
			"tg_ampl2: "+tg_ampl2+"; "+
			"tg_dur: "+tg_dur+
			"}";
	}

}
