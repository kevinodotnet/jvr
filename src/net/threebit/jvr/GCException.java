package net.threebit.jvr;

/*
 * $Id: GCException.java,v 1.3 2004/07/02 18:11:38 kevino Exp $
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
 * GCException is the exception class that is thrown due to an error
 * result from a Global Call function.  A special class is given for
 * global call errors because a <code>GC_INFO</code> object is
 * provided for additional information.
 */

public class GCException extends DialogicException {

	// Custom constructor.
	public GC_INFO gcInfo = null;

	/**
	 * @param message Additional message from the JVR native library about the error.
	 * @param file C/C++ file where the exception originated.
	 * @param line Line number where the exception originated.
	 * @param gcInfo GlobalCall Information about the error.
	 */
	public GCException (String message, String file, int line, GC_INFO gcInfo) {
		super(
			message + "; " +
			gcInfo.gcMsg+"; "+
			"ccMsg:"+gcInfo.ccMsg+"; "+
			"gcValue:"+gcInfo.gcValue+"; "+
			"ccValue:"+gcInfo.ccValue+"; "+
			"ccLibName:"+gcInfo.ccLibName+"; "+
			"ccLibId:"+gcInfo.ccLibId+"; "+
			"additionalInfo:"+gcInfo.additionalInfo+"; ("+file+":"+line+")"
		);
		this.gcInfo = gcInfo;
	};

	/**
	 * @param file C/C++ file where the exception originated.
	 * @param line Line number where the exception originated.
	 * @param gcInfo GlobalCall Information about the error.
	 */
	public GCException (String file, int line, GC_INFO gcInfo) {
		super(
			gcInfo.gcMsg+"; "+
			"ccMsg:"+gcInfo.ccMsg+"; "+
			"gcValue:"+gcInfo.gcValue+"; "+
			"ccValue:"+gcInfo.ccValue+"; "+
			"ccLibName:"+gcInfo.ccLibName+"; "+
			"ccLibId:"+gcInfo.ccLibId+"; "+
			"additionalInfo:"+gcInfo.additionalInfo+"; ("+file+":"+line+")"
		);
		this.gcInfo = gcInfo;
	};

	// Standard constructors.
	public GCException () { };
	public GCException (String message) { super(message); }
	public GCException (String message, Throwable t) { super(message,t); }
	public GCException (Throwable t) { super(t); }

}
