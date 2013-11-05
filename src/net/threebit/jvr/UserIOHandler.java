package net.threebit.jvr;

/*
 * $Id: UserIOHandler.java,v 1.1 2004/07/19 17:47:21 kevino Exp $
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

import java.io.IOException;

/**
 *
 */

public interface UserIOHandler {

	/**
	 *
	 */
	public byte[] read (UserIO io, int len) throws IOException;

	/**
	 *
	 */
	public long seek (UserIO io, long offset, int whence) throws IOException;

	/**
	 *
	 */
	public int write (UserIO io, byte[] buffer) throws IOException;

}

