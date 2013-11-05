package net.threebit.jvr.test;

/*
 * $Id: dtTests.java,v 1.10 2004/07/02 18:11:38 kevino Exp $
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

public class dtTests extends AbstractTest {

	public void testX() { System.exit(1); }

	public void test1_openclose() throws Exception {
		int dev = dt.open("dtiB1T1",0);
		assertTrue(dev > 0);
		dt.close(dev);
	}

	public void test2_getxmitslot_listen() throws Exception {
		int d1 = dt.open("dtiB1T1",0);
		int d2 = dt.open("dtiB1T2",0);
		dt.unlisten(d1);
		dt.unlisten(d2);
		dt.listen(d1,dt.getxmitslot(d2));
		dt.listen(d2,dt.getxmitslot(d1));
		dt.close(d1);
		dt.close(d2);
	}
}
