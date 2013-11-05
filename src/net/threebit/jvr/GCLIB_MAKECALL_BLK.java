package net.threebit.jvr;

/*
 * $Id: GCLIB_MAKECALL_BLK.java,v 1.3 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GCLIB_MAKECALL_BLK structure.
 */

public class GCLIB_MAKECALL_BLK extends JVRBaseClass {

	/**
	 *
	 */
	GCLIB_ADDRESS_BLK destination = new GCLIB_ADDRESS_BLK();

	/**
	 *
	 */
	GCLIB_ADDRESS_BLK origination = new GCLIB_ADDRESS_BLK();

	/**
	 *
	 */
	GCLIB_CHAN_BLK chan_info = new GCLIB_CHAN_BLK();

	/**
	 *
	 */
	GCLIB_CALL_BLK call_info = new GCLIB_CALL_BLK();

	/**
	 *
	 */
	GC_PARM_BLK ext_data = null;
	// GC_PARM_BLK ext_data = new GC_PARM_BLK();

}
