package net.threebit.jvr.test;

/*
 * $Id: AbstractTest.java,v 1.5 2004/07/02 18:11:38 kevino Exp $
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

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import junit.framework.*;
import net.threebit.jvr.*;

public class AbstractTest extends TestCase {

	/** Logger for all test cases. */
	public final static Logger logger = Logger.getLogger("net.threebit.jvr.test");
	/** True when running on Linux */
	public static boolean linux = false;
	/** True when running on Windows */
	public static boolean win32 = false;

	static {
		String platform = System.getProperty("net.threebit.jvr.test.platform");
		if (platform.equals("linux")) { linux = true; }
		else if (platform.equals("win32")) { win32 = true; }
		else { 
			logger.info("Unknown platform type '"+platform+"'; check the value of 'net.threebit.jvr.test.platform'");
		}
	}

}
