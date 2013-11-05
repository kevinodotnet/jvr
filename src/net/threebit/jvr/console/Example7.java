package net.threebit.jvr.console;

/*
 * $Id: Example7.java,v 1.4 2005/01/08 20:12:26 kevino Exp $
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
 *
 */
public class Example7 extends AbstractExample {

	JDialog dlg = null;
	int d7;
	int d8;
	JVRMetaEventListener m = new JVRMetaEventListener() {
		public boolean handleEvent (MetaEvent e) throws JVRException {
			if (e.evtdev != d8) { return true; } // ignore
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

	/**
	 *
	 */
	public String getName() { return "MSI Gen Ring"; }

	public void finalize() {
		try {
			jvr.removeEventListener(m);
			ms.close(d7);
			ms.close(d8);
		}
		catch (Throwable ignore) { }
	}

	/**
	 *
	 */
	public void run () {
		try {

			d7 = ms.open("msiB1C7",0);
			d8 = ms.open("msiB1C8",0);
			jvr.addEventListener(m);

			ms.listen(d7, ms.getxmitslot(d8));
			ms.listen(d8, ms.getxmitslot(d7));
			
			ms.setevtmsk(d8,ms.DTG_SIGEVT,
				ms.MSMM_OFFHOOK|ms.MSMM_ONHOOK|ms.MSMM_HOOKFLASH,ms.DTA_ADDMSK);
	
			// Collect some user data.
			dlg = new JDialog(console.frame,"Example 1 Setup",true);
			dlg.getContentPane().setLayout(new BorderLayout());
	
			// Properties area
			JPanel propPanel = new JPanel();
			dlg.getContentPane().add(propPanel,BorderLayout.CENTER);
	
			JButton b = new JButton("Ring");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					try {
						ms.genring(d8,2,ms.EV_ASYNC);
					}
					catch (Throwable ignore) {
						ignore.printStackTrace();
					}
				}
			});
			propPanel.add(b);

			dlg.pack();
			dlg.setLocationRelativeTo(console.frame);
			dlg.setVisible(true);
		}
		catch (Throwable ignore) { }

	}

}

