package net.threebit.jvr.console;

/*
 * $Id: AbstractExample.java,v 1.6 2004/07/02 18:11:38 kevino Exp $
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
 * Base class for all example classes.
 */

public abstract class AbstractExample extends JVRBaseClass {

	/**
	 * JVRConsole will set this member to itself before invoking
	 * the <code>run()</code> method.  Implementing classes are
	 * free to assume that it is not-null.
	 */
	public JVRConsole console = JVRConsole.getConsole();

	/**
	 * A dialog panel to which the implementing example can add
	 * configuration items.
	 */
	JDialog setupDialog = null;

	/**
	 * Panel (within setupDialog) where configuration items are placed.
	 */
	JPanel propPanel = null;

	/**
	 * Button panel at the bottom of the setup dialog.  Implementing
	 * classes must add the Run/Cancel buttons themselves.
	 */
	JPanel buttonPanel = null;

	/**
	 * Run button at the bottom of the setup dialog.  Implementing classes
	 * should add an actionlistener.
	 */
	JButton runButton = null;

	/**
	 * LayoutManager used by <code>propPanel</code>
	 */
	GridBagLayout gbl = null;

	/**
	 * Creates common Swing elements.
	 */
	public AbstractExample() {
		setupDialog = new JDialog(console.frame,"Example Configuration",true);
		setupDialog.getContentPane().setLayout(new BorderLayout());

		propPanel = new JPanel();
		setupDialog.getContentPane().add(propPanel,BorderLayout.CENTER);

		gbl = new GridBagLayout();
		propPanel.setLayout(gbl);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		setupDialog.getContentPane().add(buttonPanel,BorderLayout.SOUTH);

		JButton runButton = new JButton("Run");
		buttonPanel.add(runButton);

		JButton b = new JButton("Cancel");
		b.addActionListener(new AbstractAction(){
			public void actionPerformed (ActionEvent e) {
				setupDialog.dispose();
			}
		});
		buttonPanel.add(b);
	}

	/**
	 * Implementing classes should override this method to return
	 * a more descriptive name (spaces are OK).  By default, the
	 * fully qualified class name of the example is provided.
	 */
	public String getName() { return getClass().getName(); }

	/**
	 * Abstract method that must be implemented by the example class.
	 */
	public abstract void run ();

	/**
	 * Helper method that logs a line of text (\n is appended automatically)
	 * to the JVRConsole window and to the Logger facilities.
	 */
	public void info (String message) {
		console.textArea.append(message+"\n");
		logger.info(message);
	}

	/**
	 * Returns a JList composed of String members - one for each
	 * Voice Resource (dxxx*).
	 */
	public JList getDxResourceList() {
		DefaultListModel dlm = new DefaultListModel();
		for (int x = 1;; x++) {
			try {
				int c = x%4==0?4:x%4;
				int b = c==4?x/4:x/4+1;
				String devName = "dxxxB"+b+"C"+c;
				// dx.open() will throw an exception if the device
				// does not exist, thereby breaking the infinite for() loop.
				int dev = dx.open(devName,0);
				dlm.addElement(devName);
				dx.close(dev);
			}
			catch (Exception ignore) { break; }
		}
		return new JList(dlm);
	}

	/**
	 * Returns a JList composed of String members - one for each
	 * Voice Resource (dxxx*) that is connected to an analog network
	 * device.
	 */
	public JList getAnalogDxResourceList() {
		DefaultListModel dlm = new DefaultListModel();
		JList dxList = getDxResourceList();
		ListModel lm = dxList.getModel();
		// Prune away non-analog resources from the list of all resources.
		for (int x = 0; x < lm.getSize(); x++) {
			try {
				String devName = (String) lm.getElementAt(x);
				int dev = dx.open(devName,0);
				try {
					// If any of the voice resources _are not_ connected to 
					// analog loop-start lines, then they will be suppressed
					// here because sethook() will not be supported.
					dx.ATDX_HOOKST(dev);
					dlm.addElement(devName);
				}
				catch (Exception ignore) { }
				dx.close(dev);
			}
			catch (Exception ignore) { break; }
		}
		return new JList(dlm);
	}

}
