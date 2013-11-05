package net.threebit.jvr.console;

/*
 * $Id: Example9.java,v 1.1 2005/01/05 02:06:20 kevino Exp $
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
public class Example9 extends AbstractExample {

	/**
	 *
	 */
	public String getName() { return "MSI > MSI > VoiceRecord > Line"; }

	/**
	 *
	 */
	public void run () {
		try {

			final int dtdev = dt.open("dtiB1T1",0);
			final int dxdev = dx.open("dxxxB1C1",0);
			final int msdev = ms.open("msiB1C7",0);

			dx.listen( dxdev, dt.getxmitslot(dtdev) );
			ms.listen( msdev, dt.getxmitslot(dtdev) );
			dt.listen( dtdev, ms.getxmitslot(msdev) );

			// go on hook
			dt.settssig( dtdev, dt.DTB_AOFF|dt.DTB_BOFF, dt.DTA_SETMSK );

			try { Thread.sleep(2000); } catch (Throwable ignore) { }

			// go off hook
			dt.settssig( dtdev, dt.DTB_ABIT|dt.DTB_BBIT, dt.DTA_SETMSK );

			// start recording.
			final Thread recorder = new Thread() {
				public void run() {
					try {
						String file = "c:\\phonerecording-"+System.currentTimeMillis()+".wav";
						DX_XPB format = DX_XPB.waveFormat();
						dx.recwav(dxdev, file, null, format, dx.EV_SYNC);
					}
					catch (JVRException e) {
						e.printStackTrace();
					}
				}
			};

			// Do nothing until "hangup" is pressed.
			JDialog dlg = new JDialog(console.frame,"Dialing through MSI",true);
			dlg.getContentPane().setLayout(new BorderLayout());
	
			JButton b = new JButton("Hangup");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					try {
						// go on hook
						dt.settssig( dtdev, dt.DTB_AOFF|dt.DTB_BOFF, dt.DTA_SETMSK );
						dx.stopch( dxdev, dx.EV_SYNC );
					}
					catch (Throwable ignore) {
						ignore.printStackTrace();
					}
				}
			});
			dlg.getContentPane().add(b,BorderLayout.CENTER);

			JButton bb = new JButton("Start Recording");
			bb.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					try {
						recorder.start();
					}
					catch (Throwable ignore) {
						ignore.printStackTrace();
					}
				}
			});
			dlg.getContentPane().add(bb,BorderLayout.NORTH);

			dlg.pack();
			dlg.setLocationRelativeTo(console.frame);
			dlg.setVisible(true);

		}
		catch (JVRException e) {
		}
	}

}

