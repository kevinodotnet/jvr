package net.threebit.jvr.console;

/*
 * $Id: Example11.java,v 1.1 2005/01/19 02:44:25 kevino Exp $
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
public class Example11 extends AbstractExample {

	JDialog dlg = null;

	int msBrd;
	int msi7;
	int msi8;
	int dt2;
	int dx1;
	int dx2;
	int confID;

	public String getName() {
		return "MSI; est/add/rem/del conf";
	}

	/**
	 *
	 */
	public void run () {
		try {

			msBrd = ms.open("msiB1",0);
			msi7 = ms.open("msiB1C7",0);
			dx1 = dx.open("dxxxB6C4",0);
			dx2 = dx.open("dxxxB6C3",0);

			// make noise on the two voice devices
			Thread n1 = new Thread() {
				public void run() {
					while (true) {
						try {
							dx.dial(dx1,"1",null,dx.EV_SYNC);
						}
						catch (Throwable ignore) { info("n1: " + ignore); }
						finally {
							try { Thread.sleep(500); } catch (Throwable ignore) { }
						}
					}
				}
			};
			Thread n2 = new Thread() {
				public void run() {
					while (true) {
						try {
							dx.dial(dx2,"2",null,dx.EV_SYNC);
						}
						catch (Throwable ignore) { info("n2: " + ignore); }
						finally {
							try { Thread.sleep(500); } catch (Throwable ignore) { }
						}
					}
				}
			};
			n1.start();
			try { Thread.sleep(200); } catch (Throwable ignore) { }
			n2.start();

			MS_CDT[] cdt = MS_CDT.newArray(1);
			cdt[0].chan_num = 7; // msiB1C7
			cdt[0].chan_sel = ms.MSPN_STATION;
			cdt[0].chan_attr = ms.MSPA_NULL;

			info("Adding first MSI device");
			confID = ms.estconf(msBrd,cdt,ms.MSCA_NULL);
			// ms.listen(msi7, cdt[0].listenTS()); // not required for MSI side.
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Adding first voice resource");
			MS_CDT mscdt1 = new MS_CDT();
			mscdt1.chan_num = (int) dx.getxmitslot(dx1).ts();
			mscdt1.chan_sel = ms.MSPN_TS;
			ms.addtoconf(msBrd,confID,mscdt1);
			dx.listen(dx1, mscdt1.listenTS());
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Adding second voice resource");
			MS_CDT mscdt2 = new MS_CDT();
			mscdt2.chan_num = (int) dx.getxmitslot(dx2).ts();
			mscdt2.chan_sel = ms.MSPN_TS;
			ms.addtoconf(msBrd,confID,mscdt2);
			dx.listen(dx2, mscdt2.listenTS());
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Removing second voice resource");
			ms.remfromconf(msBrd,confID,mscdt2);
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Removing first voice resource");
			ms.remfromconf(msBrd,confID,mscdt1);
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Removing MSI");
			ms.remfromconf(msBrd,confID,cdt[0]);
			try { Thread.sleep(3000); } catch (Throwable ignore) { }

			info("Deleting the conference");
			ms.delconf(msBrd,confID);

			/*
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
					while (true) {
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
			*/
			
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

			dlg.pack();
			dlg.setLocationRelativeTo(console.frame);
			dlg.setVisible(true);
			
		}
		catch (Throwable ignore) {
			ignore.printStackTrace();
		}
	}
}

