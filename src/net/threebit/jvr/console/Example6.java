package net.threebit.jvr.console;

/*
 * $Id: Example6.java,v 1.1 2004/12/07 02:38:10 kevino Exp $
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
public class Example6 extends AbstractExample {

	/**
	 *
	 */
	public String getName() { return "MSI Listen for Events"; }

	/**
	 *
	 */
	public void run () {
		Thread t = new Thread() {
			public void run() {
				try {

					String port = "msiB1C1";
					info("Opening MSI channel: " + port);
					final int d1 = ms.open(port,0);
		
					JVRMetaEventListener m = new JVRMetaEventListener() {
						public boolean handleEvent (MetaEvent e) throws JVRException {
							if (e.evtdev != d1) { return true; } // ignore
							if (e.evttype == ms.MSEV_SIGEVT) {
								int hook = ((Integer)e.evtdata).intValue();
								if (hook == ms.MSMM_OFFHOOK) {
									info("OFFHOOK");
								}
								else if (hook == ms.MSMM_ONHOOK) {
									info("ONHOOK");
								}
								else if (hook == ms.MSMM_HOOKFLASH) {
									info("FLASH");
								}
								else {
									info("Unknown hook event: " + hook);
								}
							}
							return true;
						}
					};
					jvr.addEventListener(m);
		
					info("Setting event mask");
					ms.setevtmsk(d1,ms.DTG_SIGEVT,
						ms.MSMM_OFFHOOK|ms.MSMM_ONHOOK|ms.MSMM_HOOKFLASH,ms.DTA_ADDMSK);
					int d = 20;
					info("Sleeping for "+d+" seconds.  Take the MSI port on and off hook.");
					try { Thread.sleep(d*1000); } catch (Throwable ignore) { }
					info("Done, closing...");
		
					jvr.removeEventListener(m);
					ms.close(d1);
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};
		t.start();
	}

}

