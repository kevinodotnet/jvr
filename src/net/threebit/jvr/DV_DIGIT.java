package net.threebit.jvr;

/*
 * $Id: DV_DIGIT.java,v 1.10 2004/07/02 18:11:37 kevino Exp $
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
 * Java representation of the DV_DIGIT struct.
 */
public class DV_DIGIT extends JvrJni {
	static { jvr.foo(); } // importSymbols() hack until a better solution is available.

	/**
	 * A private locking object.  By using <code>lock</code> as a synchronization
	 * point in the Java context, we negate the need to perform thread-safe locking
	 * in the JNI context.
	 */
	private static final Object lock = new Object();

	/**
	 * Offset of the DV_DIGIT struct buffer allocated to this instance.
	 */
	private int offset = -1;

	/**
	 * Calls <code>allocateBuffer()</code>.
	 */
	public DV_DIGIT() throws JVRException {
		synchronized (lock) {
			offset = allocateBuffer();
		}
	}

	/**
	 * Allocates a native-context DV_DIGIT struct for this instance of DV_DIGIT.
	 */
	public static native int allocateBuffer () throws JVRException;

	/**
	 * Releases the native-context DV_DIGIT structure.
	 */
	public static native void releaseBuffer (int offset) throws JVRException;

	/**
	 * Accesses the native-context DV_DIGIT structure and converts the collected
	 * digits to a string.
	 */
	public String getDigits () throws JVRException {
		return getDigits(offset);
	}

	/**
	 * Private, native version of <code>getDigits</code> instance method.
	 */
	private static native String getDigits (int offset) throws JVRException;

	/**
	 * Returns the digits collected into this DV_DIGIT instance.
	 * Example:
	 * <pre>
	 * DV_DIGIT digits;
	 * // ... previous calls to dx.getdig()
	 * logger.info("The caller typed in '"+digits+"'");
	 * </pre>
	 */
	public String toString() {
		try {
			return getDigits(offset);
		}
		catch (JVRException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Ensures that the DV_DIGIT structure allocated to this instance is released.
	 */
	public void finalize () {
		try {
			synchronized (lock) {
				releaseBuffer(offset);
			}
		}
		catch (JVRException e) {
			logger.throwing(DV_DIGIT.class.getName(),"finalize",e);
			throw new RuntimeException(e);
		}
	}

}
