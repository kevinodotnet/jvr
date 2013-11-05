package net.threebit.jvr.console;

/*
 * $Id: Example5.java,v 1.1 2004/12/01 10:57:14 kevino Exp $
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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;
import net.threebit.jvr.*;

/**
 * Performs an Outbound GlobalCall call over a Digital T1 channel.
 */
public class Example5 extends AbstractExample {

	/**
	 *
	 */
	public String getName() { return "ms.open/unlisten/listen/close"; }

	/**
	 *
	 */
	public void run () {
		try {
			info("Opening MSI channels.");
			int d1 = ms.open("msiB1C1",0);
			int d2 = ms.open("msiB1C2",0);
			info("Unlisten");
			ms.unlisten(d1);
			ms.unlisten(d2);
			info("Listen");
			ms.listen(d1,ms.getxmitslot(d2));
			ms.listen(d2,ms.getxmitslot(d1));
			info("Close");
			ms.close(d1);
			ms.close(d2);
		}
		catch (Throwable t) {
			t.printStackTrace();
			info(t.getMessage());
		}
	}

}

