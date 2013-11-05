package net.threebit.jvr;

/*
 * $Id: UserIO.java,v 1.2 2004/07/26 20:49:27 kevino Exp $
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

import java.lang.ref.*;
import java.util.*;

/**
 * An extention of the DX_IOTT structure that is used
 * when performing user defined input/output actions.
 */

public class UserIO extends DX_IOTT {

	/**
	 * Internal counter of the number of UserIO objects
	 * that have been created since the JVM was initialized.
	 * Used to set the io_fhandle member at initialization time.
	 */
	private static int fhandleMark = 0;

	/**
	 * List of soft references to all of the current
	 * UserIO objects.  Used by the DX class to map 
	 * the supplied file descriptor to an object.
	 */
	protected static ArrayList refList = new ArrayList();

	/**
	 * The handler that will be called when user input/output
	 * on this object is detected.
	 */
	private UserIOHandler handler = null;

	/**
	 *
	 */
	public UserIO() throws JVRException {
		super();
		init();
	}

	/**
	 *
	 */
	public UserIO (UserIOHandler handler) throws JVRException {
		super();
		init();
		setHandler(handler);
	}

	/**
	 * Sets the io_fhandle and io_type parameters to the right
	 * values for the UserIO object.  Called by the constructors
	 * so there is no need to call this manually.
	 */
	private void init() {
		synchronized (UserIO.class) {
			fhandleMark ++;
			io_fhandle = fhandleMark;
			io_type = dx.IO_DEV | dx.IO_UIO;
			refList.add(new SoftReference(this));
		}
	}

	/**
	 *
	 */
	public void setHandler (UserIOHandler handler) {
		this.handler = handler;
	}

	/**
	 *
	 */
	public UserIOHandler getHandler () {
		return handler;
	}

	/**
	 * Ensures that the DX_IOTT structure allocated to this instance is released.
	 */
	public void finalize () {
		super.finalize();
	}

	/**
	 *
	 */
	public String toString() {
		// return "DX_IOTT{io_fhandle="+io_fhandle+";io_type="+io_type+";rfu="+rfu+";io_length="+io_length+";io_offset="+io_offset+"}";
		return "";
	}

	/**
	 *
	 */
	public static UserIO getUserIO (int handle) {
		synchronized (UserIO.class) {
			for (Iterator i = refList.iterator(); i.hasNext(); ) {
				SoftReference sr = (SoftReference) i.next();
				if (sr.get() == null) {
					i.remove();
					continue;
				}
				UserIO uio = (UserIO) sr.get();
				if (uio.io_fhandle == handle) {
					return uio;
				}
			}
		}
		return null;
	}

}
