package net.threebit.jvr;

/*
 * $Id: CT_DEVINFO.java,v 1.2 2004/07/02 18:11:37 kevino Exp $
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
 * Java equivalent of the Dialogic CT_DEVINFO structure.
 */

public class CT_DEVINFO extends JVRBaseClass {
	public long ct_prodid;  
	public int ct_devfamily;  
	public int ct_devmode;  
	public int ct_nettype;  
	public int ct_busmode;  
	public int ct_busencoding;  
	public int ct_rfu[] = new int[7];  
}
