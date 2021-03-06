package net.threebit.jvr;

/*
 * $Id: GCLIB_ADDRESS_BLK.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GCLIB_ADDRESS_BLK structure.
 */

public class GCLIB_ADDRESS_BLK extends JVRBaseClass {

	/**
	 *
	 */
	String address = null;

	/**
	 *
	 */
	int address_type = -1;

	/**
	 *
	 */
	int address_plan = -1;

	/**
	 *
	 */
	String sub_address = null;

	/**
	 *
	 */
	int sub_address_type = -1;

	/**
	 *
	 */
	int sub_address_plan = -1;

}
