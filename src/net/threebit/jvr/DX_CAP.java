package net.threebit.jvr;

/*
 * $Id: DX_CAP.java,v 1.5 2004/07/02 18:11:37 kevino Exp $
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
 * Java equivalent of the Dialogic DX_CAP structure.
 */

public class DX_CAP extends JVRBaseClass {
	public byte ca_pamd_qtemp = 0;
	public byte ca_pamd_spdval = 0;
	public int ca_alowmax = 0;
	public int ca_ansrdgl = 0;
	public int ca_blowmax = 0;
	public int ca_cnosig = 0;
	public int ca_cnosil = 0;
	public int ca_dtn_deboff = 0;
	public int ca_dtn_npres = 0;
	public int ca_dtn_pres = 0;
	public int ca_hedge = 0;
	public int ca_hi1bmax = 0;
	public int ca_hi1ceil = 0;
	public int ca_hi1tola = 0;
	public int ca_hi1tolb = 0;
	public int ca_higltch = 0;
	public int ca_hisiz = 0;
	public int ca_intflg = 0;
	public int ca_intfltr = 0;
	public int ca_lcdly = 0;
	public int ca_lcdly1 = 0;
	public int ca_lo1bmax = 0;
	public int ca_lo1ceil = 0;
	public int ca_lo1rmax = 0;
	public int ca_lo1tola = 0;
	public int ca_lo1tolb = 0;
	public int ca_lo2bmax = 0;
	public int ca_lo2rmin = 0;
	public int ca_lo2tola = 0;
	public int ca_lo2tolb = 0;
	public int ca_logltch = 0;
	public int ca_lower2frq = 0;
	public int ca_lower3frq = 0;
	public int ca_lowerfrq = 0;
	public int ca_maxansr = 0;
	public int ca_maxintering = 0;
	public int ca_mxtime2frq = 0;
	public int ca_mxtime3frq = 0;
	public int ca_mxtimefrq = 0;
	public int ca_nbrbeg = 0;
	public int ca_nbrdna = 0;
	public int ca_noanswer = 0;
	public int ca_nsbusy = 0;
	public int ca_pamd_failtime = 0;
	public int ca_pamd_minring = 0;
	public int ca_rejctfrq = 0;
	public int ca_stdely = 0;
	public int ca_time2frq = 0;
	public int ca_time3frq = 0;
	public int ca_timefrq = 0;
	public int ca_upper2frq = 0;
	public int ca_upper3frq = 0;
	public int ca_upperfrq = 0;
	public int rfu1 = 0;
	public int rfu2 = 0;
	public int rfu3 = 0;
	public int rfu4 = 0;
}
