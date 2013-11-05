package net.threebit.jvr;

/*
 * $Id: DX_IOTT.java,v 1.11 2005/01/05 02:06:20 kevino Exp $
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
 * Java representation of the DX_IOTT struct.
 * <p>
 * This object will reserve a native-context DX_IOTT struct when requested.
 * This allows asynchronous calls to various I/O functions that require their
 * DX_IOTT struct to remain in-scope for the duration of their execution.
 * Since the native-context DX_IOTT struct will not be "freed" for use by other
 * componenents until this object is garbage collected (or explicitly released),
 * we guarantee that no collisions will occur.
 * <p>
 * The <code>io_nextp</code> and <code>io_prep</code> members have been omitted
 * from the Java object because the linked-list structures are handled internally
 * by JVR.
 * <p>
 * The <code>io_bufp</code> member is also not supported at this time.
 * <p>
 * Do not specify any of <code>IO_CONT</code>, <code>IO_LINK</code> or <code>IO_EOT</code>
 * in <code>io_type</code> as JVR will assign the value automatically.  Placing it in
 * manually will likely result in errors.
 */

public class DX_IOTT extends JvrJni {
	static { jvr.foo(); } // importSymbols() hack until a better solution is available.

	public int io_fhandle = 0;
	public int io_type = 0;
	public int rfu = 0;
	public long io_length = -1;
	public long io_offset = 0;

	/** Offset of the DX_IOTT struct buffer allocated to this instance. */
	private int offset = -1;

	/**
	 * Calls <code>allocate()</code> automatically.
	 */
	public DX_IOTT() throws JVRException {
		synchronized (DX_IOTT.class) {
			allocate();
		}
	}

	/**
	 * Allocates a native-context DX_IOTT structure.  This method is marked
	 * private since it is called automatically by the constructor.
	 */
	private native void allocate() throws JVRException;

	/**
	 * Releases the native-context DX_IOTT structure.
	 */
	public native void release() throws JVRException;

	/**
	 * Ensures that the DX_IOTT structure allocated to this instance is released.
	 */
	public void finalize () {
		try {
			synchronized (DX_IOTT.class) {
				release();
			}
		}
		catch (JVRException e) {
			logger.throwing(DX_IOTT.class.getName(),"finalize",e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns an array pre-populated with new DX_IOTT instances.
	 */
	public static DX_IOTT[] newArray (int size) throws JVRException {
		if (size <= 0) { throw new RuntimeException("size must be >= 0"); }
		DX_IOTT[] array = new DX_IOTT[size];
		for (int x = 0; x < size; x++) {
			array[x] = new DX_IOTT();
		}
		return array;
	}

	/**
	 *
	 */
	public String toString() {
		return "DX_IOTT{io_fhandle="+io_fhandle+";io_type="+io_type+";rfu="+rfu+";io_length="+io_length+";io_offset="+io_offset+"}";
	}

	/**
	 * Debugging convenience.
	 */
	public static String toString (DX_IOTT[] iott) {
		if (iott == null) { return "DX_IOTT[]{null}"; }
		if (iott.length == 0) { return "DX_IOTT[0]{}"; }
		if (iott.length == 1) {
			return
				"DX_IOTT[1]{io_fhandle="+iott[0].io_fhandle+";io_type="+iott[0].io_type+";rfu="+
				iott[0].rfu+";io_length="+iott[0].io_length+";io_offset="+iott[0].io_offset+"}"
			;
		}
		String s = "DX_IOTT["+iott.length+"]{\n";
		for (int x = 0; x < iott.length; x++) { s += "["+x+"]: "+iott[x]+"\n"; }
		s += "}";
		return s;
	}

	/**
	 * Convenience method that calls dx.fileclose()
	 * for any IO_DEV handles.
	 */
	public static void closeFiles (DX_IOTT[] iott) {
		if (iott == null) { return; }
		for (int x = 0; x < iott.length; x++) {
			try {
				DX_IOTT i = iott[x];
				if (i == null) { continue; }
				if (i.io_type != dx.IO_DEV) { continue; }
				if (i.io_fhandle == 0) { continue; } // already closed or never opened.
				dx.fileclose(i.io_fhandle);
				i.io_fhandle = 0;
			}
			catch (Throwable t) {
				logger.throwing(DX_IOTT.class.getName(),"closeFiles",t);
			}
		}
	}

}
