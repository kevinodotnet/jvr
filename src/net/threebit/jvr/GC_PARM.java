package net.threebit.jvr;

/*
 * $Id: GC_PARM.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GC_PARM structure.
 */

public class GC_PARM extends JVRBaseClass {

	private long nvalue = -1;
  private String svalue = null;
	private boolean isString = false;

	/**
	 *
	 */
	public GC_PARM (String value) { 
		if (value == null) { throw new IllegalArgumentException("A GC_PARM string value cannot be null"); }
		isString = true;
		svalue = value;
	}

	/**
	 *
	 */
	public GC_PARM (int value) { nvalue = value; }

	/**
	 *
	 */
	public GC_PARM (long value) { nvalue = value; }

}
