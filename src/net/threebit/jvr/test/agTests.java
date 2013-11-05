package net.threebit.jvr.test;

/*
 * $Id: agTests.java,v 1.8 2004/07/02 18:11:38 kevino Exp $
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

public class agTests extends AbstractTest {

	public void testX() { System.exit(1); }

	public void test1_getxmitslot_listen() throws Exception {
		int d1 = dx.open("dxxxB1C1",0);
		int d2 = dx.open("dxxxB1C2",0);
		ag.unlisten(d1);
		ag.unlisten(d2);
		dx.listen(d1,ag.getxmitslot(d2));
		dx.listen(d2,ag.getxmitslot(d1));
		dx.close(d1);
		dx.close(d2);
	}

}
