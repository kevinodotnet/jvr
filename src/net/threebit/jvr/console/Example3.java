package net.threebit.jvr.console;

/*
 * $Id: Example3.java,v 1.2 2004/07/02 18:11:38 kevino Exp $
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
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;
import net.threebit.jvr.*;

/**
 * Performs an Outbound GlobalCall call over a Digital T1 channel.
 */
public class Example3 extends AbstractExample {

	/**
	 *
	 */
	public String getName() { return "GlobalCall Outbound: Digital"; }

	JList dxResourceList = getDxResourceList();
	/**
	 *
	 */
	public void run () {

		GridBagConstraints gbc;

		// "Voice Resource" for the test.
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		JTextField t = new JTextField("Voice Resource");
		t.setEditable(false);
		gbl.setConstraints(t,gbc);
		propPanel.add(t);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane s = new JScrollPane(dxResourceList);
		gbl.setConstraints(s,gbc);
		propPanel.add(s);

		// Pack and Show
		setupDialog.pack();
		setupDialog.setLocationRelativeTo(console.frame);
		setupDialog.setVisible(true);

		/*

		// Collect some user data.
		dlg = new JDialog(console.frame,"Example 3 Setup",true);
		dlg.getContentPane().setLayout(new BorderLayout());

		// Properties area
		propPanel = new JPanel();
		dlg.getContentPane().add(propPanel,BorderLayout.CENTER);

		GridBagLayout gbl = new GridBagLayout();
		propPanel.setLayout(gbl);

		// Dialog buttons at the bottom.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		dlg.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		// "Run"
		{
			JButton b = new JButton("Run");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					final Object[] dx = analogDxList.getSelectedValues();
					if (dx.length != 1) {
						// "Please select one, and only one, voice resource."
						JOptionPane.showMessageDialog(dlg,"Please select one, and only one, resource.","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					Thread t = new Thread() {
						public void run() {
							runExample((String)dx[0]);
						}
					};
					t.start();
					dlg.dispose();
				}
			});
			buttonPanel.add(b);
		}
		// "Cancel"
		{
			JButton b = new JButton("Cancel");
			b.addActionListener(new AbstractAction(){
				public void actionPerformed (ActionEvent e) {
					dlg.dispose();
				}
			});
			buttonPanel.add(b);
		}

		// Pack and Show
		dlg.pack();
		dlg.setLocationRelativeTo(console.frame);
		dlg.setVisible(true);
		*/
	}

	/**
	 *
	 */
	public void runExample (String deviceName) {
	}

}

