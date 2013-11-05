package net.threebit.jvr;

/*
 * $Id: CCLIB_START_STRUCT.java,v 1.5 2004/07/02 18:11:37 kevino Exp $
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
 * Java equivalent of the CCLIB_START_STRUCT struct.
 */

public class CCLIB_START_STRUCT extends JVRBaseClass {

	/**
	 * Name of the call control library that should be started.
	 * <ul>
	 * <li>GC_ANAPI_LIB</li>
	 * <li>GC_CUSTOM1_LIB</li>
	 * <li>GC_CUSTOM2_LIB</li>
	 * <li>GC_DM3CC_LIB</li>
	 * <li>GC_ICAPI_LIB</li>
	 * <li>GC_IPM_LIB</li>
	 * <li>GC_ISDN_LIB</li>
	 * <li>GC_PDKRT_LIB</li>
	 * <li>GC_SS7_LIB</li>
	 * </ul>
	 */
	public String cclib_name = null;

	/**
	 * I haven't figured out this members usage yet - so it is ignored for now.
	 * Looks like the regular call control libraries do not require it anyway.
	 */
	public Object cclib_data = null;

}
