package net.threebit.jvr;

/*
 * $Id: GC_PARM_BLK.java,v 1.6 2004/07/02 18:11:38 kevino Exp $
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
 * Java equivalent of the Dialogic GC_PARM_BLK structure.
 * <p>
 * This class is not supported yet.  Supply NULL anywhere where
 * it is required.
 */

public class GC_PARM_BLK extends JvrJni {
	static { jvr.foo(); } // importSymbols() hack until a better solution is available.

	/**
	 * Offset within native context GC_PARM_BLK array that 
	 * is allocated to this instance.  A value of -1 means
	 * that no instance has been allocated.
	 */
	private int offset = -1;

	/**
	 *
	 */
	public GC_PARM_BLK() throws JVRException {
		synchronized (GC_PARM_BLK.class) {
			allocateNative();
		}
	}

	/**
	 * Allocates native resources.
	 */
	private native void allocateNative() throws JVRException;

	/**
	 * Releases native resources.
	 */
	private native void releaseNative() throws JVRException;

	/**
	 *
	 */
	public void finalize() {
		try {
			synchronized (GC_PARM_BLK.class) {
				releaseNative();
			}
		}
		catch (JVRException e) {
			logger.throwing(GC_PARM_BLK.class.getName(),"finalize",e);
			throw new RuntimeException(e);
		}
	}

}
