package net.threebit.jvr.console;

/*
 * $Id: Example10.java,v 1.2 2005/01/26 01:34:04 kevino Exp $
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
public class Example10 extends AbstractExample {

	JDialog dlg = null;

	int msi7;
	int msi8;
	int dt2;
	int dx1;
	boolean stop = false;

	public String getName() {
		return "MSI; genring; voice xconnect msi-to-msi";
	}

	/**
	 *
	 */
	public void run () {
		try {

			msi7 = ms.open("msiB1C7",0);
			msi8 = ms.open("msiB1C8",0);
			dt2 = dt.open("dtiB1T2",0);
			dx1 = dx.open("dxxxB6C4",0);

			// T1 timeslot goes offhook
			dt.settssig( dt2, dt.DTB_ABIT|dt.DTB_BBIT, dt.DTA_SETMSK );

			// MSI listen to eachother
			ms.listen(msi7, ms.getxmitslot(msi8));
			ms.listen(msi8, ms.getxmitslot(msi7));
			dx.listen(dx1, dt.getxmitslot(dt2));
			dt.listen(dt2, dx.getxmitslot(dx1));

			// make continous noise on the test line.
			Thread t = new Thread() {
				public void run () {
					while (!stop) {
						try {
							dx.playwav(dx1,"C:\\code\\pbx\\etc\\wav\\is-this-love-bob-marley.wav",null,dx.EV_SYNC);
						}
						catch (JVRException e) {
							e.printStackTrace();
						}
						finally {
							try { Thread.sleep(1000); } catch (Throwable ignore) { }
						}
					}
				}
			};
			t.start();
			
			dlg = new JDialog(console.frame,getName(),true);
			dlg.getContentPane().setLayout(new BorderLayout());
	
			JPanel propPanel = new JPanel();
			dlg.getContentPane().add(propPanel,BorderLayout.CENTER);
	
			JButton b = new JButton("Ring");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					try {
						ms.genring(msi8,1,ms.EV_ASYNC);
					}
					catch (Throwable ignore) {
						ignore.printStackTrace();
					}
				}
			});
			propPanel.add(b);

			b = new JButton("Stop");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					stop = true;
					dlg.dispose();
					try { dx.stopch(dx1,dx.EV_SYNC); } catch (JVRException ee) { }
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

