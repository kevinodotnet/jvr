package net.threebit.jvr;

/*
 * $Id: JvrJni.java,v 1.7 2004/08/25 12:08:41 kevino Exp $
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

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

/**
 * This is the base class for any and all JVR classes that contain
 * native methods.  This class is reponsible for loading the shared
 * library.
 */

public class JvrJni extends JVRBaseClass {
	static {
		//
		// Initialize the library.
		//
		try {
			System.loadLibrary("jvr");
		}
		catch (UnsatisfiedLinkError e) {
			String message =
				"An error occurred while locating jvr.dll (windows) or jvr.so (linux). " +
				"Check the contents of 'java.library.path': " +
				"(" + System.getProperty("java.library.path") + ")";
			throw new Error(message,e);
		}
	}
}
