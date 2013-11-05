package net.threebit.jvr;

/*
 * $Id: GC_CALLACK_BLK.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GC_CALLACK_BLK structure.
 * <p>
 * Whereas the original Dialogic struct has union members, the
 * Java equivalent has been "flattened out" to simply things.
 * So, where we would have accessed the "info_type" member in
 * C/C++ as:
 * <pre>
 * callack.service.info.info_type = DESTINATION_ADDRESS;
 * </pre>
 * Just refer directly to the member in Java:
 * <pre>
 * callack.info_type = DESTINATION_ADDRESS;
 * </pre>
 * JVR will handle the conversion within the native library.
 */

public class GC_CALLACK_BLK extends JVRBaseClass {

	public long type;

	public long rfu;

	/** Part of "dnis" struct. */
	public int accept; // part of "dnis" struct

	/** Part of "info" struct. */
	public int info_len;
	
	/** Part of "info" struct. */
	public int info_type;

	/** Part of "isdn" struct. */
	public int acceptance;

	/** Part of "isdn" struct. */
	public long linedev;

	public long gc_private[] = new long[4];

}
