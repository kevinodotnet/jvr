package net.threebit.jvr.console;

/*
 * $Id: JVRConsole.java,v 1.12 2005/01/19 02:44:25 kevino Exp $
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
 * An optional GUI environment.  You do not need to use JVRConsole
 * at all if you don't want to.  The console will contain example 
 * code and other utilities as they are developed.
 */

public class JVRConsole extends JVRBaseClass {

	static JVRConsole singleton = null;
	JFrame frame = null;
	JTextArea textArea = null;
	JMenu examples = null;

	/**
	 *
	 */
	public void run () {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ignore) { }

		// Main frame
		frame = new JFrame("JVR Console");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		// File
		JMenu file = new JMenu("File");
		menuBar.add(file);
		file.add(new AbstractAction("Quit") {
			public void actionPerformed (ActionEvent e) {
				System.exit(0);
			}
		});

		// Debug
		JMenu Debug = new JMenu("Debug");
		menuBar.add(Debug);
		Debug.add(new AbstractAction("On") {
			public void actionPerformed (ActionEvent e) {
				jvr.setDebugLevel(jvr.JVR_ALL);
			}
		});
		Debug.add(new AbstractAction("Off") {
			public void actionPerformed (ActionEvent e) {
				jvr.setDebugLevel(jvr.JVR_NONE);
			}
		});

		// Examples
		examples = new JMenu("Examples");
		menuBar.add(examples);
		for (int x = 1;; x++) {
			try {
				Class clazz = Class.forName("net.threebit.jvr.console.Example"+x);
				Constructor constructor = clazz.getConstructor(null);
				final AbstractExample e = (AbstractExample) constructor.newInstance(null);
				examples.add(new AbstractAction(e.getName()) {
					public void actionPerformed (ActionEvent ev) {
						e.run();
					}
				});
			}
			catch (ClassNotFoundException e) { break; }
			catch (Exception e) {
				logger.throwing(getClass().getName(),"run",e);
				System.exit(1);
			}
		}

		// A place to log messages.
		textArea = new JTextArea(10,25);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setText("");
		frame.getContentPane().add(new JScrollPane( textArea ), BorderLayout.CENTER);

		// Set the icon for the frame/window.
		/*
		try {
			ImageIO.setUseCache(false);
			ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("jpg").next();
			URL url = new URL("http://threebit.net/projects/jvr/images/icon.jpg");
			ImageInputStream iis = ImageIO.createImageInputStream(url.openStream());
			reader.setInput(iis);
			BufferedImage bi = reader.read(0);
			frame.setIconImage(bi);
		}
		catch (Throwable ignore) {
			// icons just aren't important.
			ignore.printStackTrace();
		}
		*/
		frame.pack();
		frame.setVisible(true);
		textArea.append("JVR Console started.\n");
	}

	/**
	 * JVRConsole singleton accessor.
	 */
	public static synchronized JVRConsole getConsole() {
		if (singleton == null) {
			singleton = new JVRConsole();
		}
		return singleton;
	}

	/**
	 *
	 */
	public static void main (String[] args) {
		JVRConsole console = JVRConsole.getConsole();
		console.run();
	}

}

