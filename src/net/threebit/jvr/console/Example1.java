package net.threebit.jvr.console;

/*
 * $Id: Example1.java,v 1.7 2004/07/02 18:11:38 kevino Exp $
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
 * An outbound-dialing example that uses analog loop-start voice resources
 * to dial an outside number using Call Progress and Analysis.  After a
 * connection is made, the remote caller is recorded until they press a
 * DTMF key, the recording is played back to the called party, then the
 * call is automatically hung up.
 */

public class Example1 extends AbstractExample {

	public String getName() { return "Voice/Analog Outbound"; }

	JDialog dlg = null;
	JPanel propPanel = null;
 	JList analogDxList = null;
	JTextField phoneNumber = null;

	public void run () {

		// Collect some user data.
		dlg = new JDialog(console.frame,"Example 1 Setup",true);
		dlg.getContentPane().setLayout(new BorderLayout());

		// Properties area
		propPanel = new JPanel();
		dlg.getContentPane().add(propPanel,BorderLayout.CENTER);

		GridBagLayout gbl = new GridBagLayout();
		propPanel.setLayout(gbl);

		// List of voice resources
		DefaultListModel dlm = new DefaultListModel();
		for (int x = 1;; x++) {
			try {
				int c = x%4==0?4:x%4;
				int b = c==4?x/4:x/4+1;
				String devName = "dxxxB"+b+"C"+c;
				int dev = dx.open(devName,0);
				try {
					// If any of the voice resources _are not_ connected to 
					// analog loop-start lines, then they will be suppressed
					// here because sethook() will not be supported.
					dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);
					dlm.addElement(devName);
				}
				catch (Exception ignore) { }
				dx.close(dev);
			}
			catch (Exception ignore) { break; }
		}
 		analogDxList = new JList(dlm);
		{
			GridBagConstraints gbc;
			// Choose a voice resource:
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			JTextField t = new JTextField("Analog Voice Resource");
			t.setEditable(false);
			gbl.setConstraints(t,gbc);
			propPanel.add(t);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			JScrollPane s = new JScrollPane(analogDxList);
			gbl.setConstraints(s,gbc);
			propPanel.add(s);
			// Choose the destination phone number.
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.BOTH;
			JTextField l2 = new JTextField("Phone Number");
			l2.setEditable(false);
			gbl.setConstraints(l2,gbc);
			propPanel.add(l2);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.BOTH;
			phoneNumber = new JTextField("8672620"); // kevino at home.
			gbl.setConstraints(phoneNumber,gbc);
			propPanel.add(phoneNumber);
		}

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
							runExample((String)dx[0],phoneNumber.getText());
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
	}

	/**
	 *
	 */
	public void runExample (String deviceName, String phoneNumber) {
		info("Analog voice resource: " + deviceName);
		info("Phone number         : " + phoneNumber);
		try {

			// Open the device.  All access to dialogic hardware is done through
			// "device handles".
			info("Opening " + deviceName);
			int dev = dx.open(deviceName,0);

			// Make sure that the device is "onhook" for at least 2 seconds.  It 
			// is always possible that the line is off hook right now.  Never assume
			// that the device is in any particular state.  Closing a device does
			// not reset it in any way.
			info("Setting ONHOOK to make sure no calls are active.");
			dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);
			try { Thread.sleep(2000); } catch (Exception ignore) {}

			// Each device name "dxxxB1C1" is actually two devices in one.
			// 1) A voice resource.  This resource does the dialing, DTMF detection,
			//    etc etc etc.  While it is named "dxxxB1C1", it's better to think
			//    of it as a virtual resource.  It can be used in conjunction with 
			//    any "real" interface.
			// 2) A real interface - in analog loop-start cases, this is an actual
			//    RJ-11 port.  While we commonly use the "matching" voice and analog
			//    pairs together, that is not actually forced.
			// Just like we won't assume that the device is ONHOOK right now, we
			// won't assume that the voice resource and the analog network resource
			// can "hear" eachother.  Make them listen to eachother now
			info("Routing analog and voice resources to eachother.");
			dx.listen(dev, ag.getxmitslot(dev));
			ag.listen(dev, dx.getxmitslot(dev));

			// DX_CAP is where you control how the Dialogic API controls it's
			// progress and analysis logic.  The options are too many to list here
			// (and I've barely used even 5% of them).  We'll just set a few basic
			// ones:
			DX_CAP cap = new DX_CAP();
			// Return "No Answer" after 8 ring-backs.
			cap.ca_nbrdna = 8;
			// Enable Positive Voice Detection, Positive Answering Machine Detection
			// and Operator Intercept.
			cap.ca_intflg = dx.DX_PAMDOPTEN;
			// Recommended setting from Dialogic.  Controls how long the dialogic
			// API sits around trying to figure out if this is a real person or
			// an answering machine.  The longer you wait, the more likely you
			// are to get it right.  However, the longer you wait after a real 
			// person says "Hello", the more likely it is that they will hang up.
			cap.ca_pamd_spdval = (byte) dx.PAMD_ACCU;

			// You have to explicitely go OFFHOOK before dialing.  "dx.dial" does
			// not assume anything.
			dx.sethook(dev, dx.DX_OFFHOOK, dx.EV_SYNC);

			// Dial the number with call progress and analysis enabled.
			info("Dialing " + phoneNumber);
			int result = dx.dial(dev,phoneNumber,cap,dx.EV_SYNC|dx.DX_CALLP);

			// Are we connected?
			if (result == dx.CR_CNCT) {
				// Yes we are.  But what type of connection is it?
				long connType = dx.ATDX_CONNTYPE(dev);
				if (connType == dx.CON_CAD) {
					// This means that the voice resource has stopped hearing
					// the "ring back".  In other words, someone has answered
					// the phone but has kept perfectly quiet.
					info("CON_CAD");
				}
				else if (connType == dx.CON_PVD) {
					// Call answered and Dialogic *thinks* it's a person.
					info("CON_PVD");
				}
				else if (connType == dx.CON_PAMD) {
					// Call answered and Dialogic *thinks* it's an answering 
					// machine, or some other automated attendent.
					info("CON_PAMD");
				}
				else if (connType == dx.CON_LPC) {
					// I think this means that the call was answered, but then
					// hung up.  I guess this is reported as a "connection" because
					// technically the call *was* connected.  However, there is
					// no longer anyone on the other end.  I have yet to confirm
					// this - if anyone knows for sure, let me know so I can update
					// this little blurb.
					info("CON_LPC");
				}
				else { throw new RuntimeException("Not possible"); }

				if (connType == dx.CON_CAD || connType == dx.CON_PVD || connType == dx.CON_PAMD) {

					// DV_TPT tells the API "how, when and why" a certain operation
					// should terminate.  In this case, we're going to give two 
					// termination clauses.
					DV_TPT tpt[] = DV_TPT.newArray(2);
					// 1) Stop recording if 60 seconds has elapsed.  I like to always
					// 		put in MAXTIME conditions because you can never trust that
					// 		the person is going to stay on the line and press they key.
					// 		If they hang up, the API will never "hear" a keypress, and 
					// 		will go on, and on, and on, recording whatever it hears.
					tpt[0].tp_type = dx.IO_CONT;
					tpt[0].tp_termno = dx.DX_MAXTIME;
					tpt[0].tp_length = 600;
					tpt[0].tp_flags = dx.TF_MAXTIME;
					// 2) Stop recording when we hear 1 DTMF key.  This is the user's
					// 		method of stopping the recording.
					tpt[1].tp_type = dx.IO_EOT;
					tpt[1].tp_termno = dx.DX_MAXDTMF;
					tpt[1].tp_length = 1;
					tpt[1].tp_flags = dx.TF_MAXDTMF;

					// Start the recording.  RM_TONE tells Dialogic to "beep" before
					// the recording starts - a convenient way to let the user know
					// that something usefull is going on.
					info("Recording...");
					dx.recf(dev, "Example1.vox", tpt, dx.RM_TONE|dx.EV_SYNC);

					// The recording has finished, but we don't know _why_ it finished.
					// ATDX_TERMMSK will tell us the "cause of termination".  Note that
					// several termination conditions can be true at the same time, so
					// we have to check for bit-masks, not equality.
					long termmsk = dx.ATDX_TERMMSK(dev);
					// The function "completed normally".
					if ((termmsk&dx.TM_NORMTERM)==dx.TM_NORMTERM) { info("dx.TM_NORMTERM"); }
					// They user pressed a key.
					if ((termmsk&dx.TM_MAXDTMF)==dx.TM_MAXDTMF) { info("dx.TM_MAXDTMF"); }
					// The maximum amount of time as elapsed.
					if ((termmsk&dx.TM_MAXTIME)==dx.TM_MAXTIME) { info("dx.TM_MAXTIME"); }
					// This happens if another thread has called dx.stopch() on the
					// device handle that was doing the recording.
					if ((termmsk&dx.TM_USRSTOP)==dx.TM_USRSTOP) { info("dx.TM_USRSTOP"); }
					// An error occurred.
					if ((termmsk&dx.TM_ERROR)==dx.TM_ERROR) { info("dx.TM_ERROR"); }
					// The first of the termination types should not be applicable in
					// this condition, but are listed for completeness.
					if ((termmsk&dx.TM_MAXSIL)==dx.TM_MAXSIL) { info("dx.TM_MAXSIL"); }
					if ((termmsk&dx.TM_MAXNOSIL)==dx.TM_MAXNOSIL) { info("dx.TM_MAXNOSIL"); }
					if ((termmsk&dx.TM_LCOFF)==dx.TM_LCOFF) { info("dx.TM_LCOFF"); }
					if ((termmsk&dx.TM_IDDTIME)==dx.TM_IDDTIME) { info("dx.TM_IDDTIME"); }
					if ((termmsk&dx.TM_DIGIT)==dx.TM_DIGIT) { info("dx.TM_DIGIT"); }
					if ((termmsk&dx.TM_PATTERN)==dx.TM_PATTERN) { info("dx.TM_PATTERN"); }
					if ((termmsk&dx.TM_EOD)==dx.TM_EOD) { info("dx.TM_EOD"); }
					if ((termmsk&dx.TM_TONE)==dx.TM_TONE) { info("dx.TM_TONE"); }

					// Only do the playback if the termination condition was DTMF.
					// Otherwise we have to assume that the caller has hung up.
					if ((termmsk&dx.TM_MAXDTMF)==dx.TM_MAXDTMF) {

						// When a user hits a DTMF key, only the fact that the key
						// was pressed is reported.  *What* key is actually in the
						// voice resources digit buffer.  In this example, we don't
						// care _what_ key was pressed.  However, before we can start
						// another I/O operation that has a DTMF termination clause,
						// we have to clear the buffer.  Otherwise, it will terminate
						// *right away*.
						dx.clrdigbuf(dev);

						// Play back the same file.  Since recf() uses the default
						// VOX file format, we do not need to specify a DX_XPB
						// structure.
						//
						// We will also give the user the option to terminate the
						// playback early by again hitting a DTMF key.
						tpt = DV_TPT.newArray(1);
						tpt[0].tp_type = dx.IO_EOT;
						tpt[0].tp_termno = dx.DX_MAXDTMF;
						tpt[0].tp_length = 1;
						tpt[0].tp_flags = dx.TF_MAXDTMF;
						info("Playing...");
						dx.playvox (dev, "Example1.vox", tpt, null, dx.EV_SYNC);

						// And again, check the reason for function termination.
						termmsk = dx.ATDX_TERMMSK(dev);
						// The function "completed normally".
						if ((termmsk&dx.TM_NORMTERM)==dx.TM_NORMTERM) { info("dx.TM_NORMTERM"); }
						// EOD means "end of data".  For playvox(), that means 
						// "the whole file was played".
						if ((termmsk&dx.TM_EOD)==dx.TM_EOD) { info("dx.TM_EOD"); }
						// They user pressed a key.
						if ((termmsk&dx.TM_MAXDTMF)==dx.TM_MAXDTMF) { info("dx.TM_MAXDTMF"); }
						// This happens if another thread has called dx.stopch() on the
						// device handle that was doing the recording.
						if ((termmsk&dx.TM_USRSTOP)==dx.TM_USRSTOP) { info("dx.TM_USRSTOP"); }
						// An error occurred.
						if ((termmsk&dx.TM_ERROR)==dx.TM_ERROR) { info("dx.TM_ERROR"); }
						// The first of the termination types should not be applicable in
						// this condition, but are listed for completeness.
						if ((termmsk&dx.TM_MAXTIME)==dx.TM_MAXTIME) { info("dx.TM_MAXTIME"); }
						if ((termmsk&dx.TM_MAXSIL)==dx.TM_MAXSIL) { info("dx.TM_MAXSIL"); }
						if ((termmsk&dx.TM_MAXNOSIL)==dx.TM_MAXNOSIL) { info("dx.TM_MAXNOSIL"); }
						if ((termmsk&dx.TM_LCOFF)==dx.TM_LCOFF) { info("dx.TM_LCOFF"); }
						if ((termmsk&dx.TM_IDDTIME)==dx.TM_IDDTIME) { info("dx.TM_IDDTIME"); }
						if ((termmsk&dx.TM_DIGIT)==dx.TM_DIGIT) { info("dx.TM_DIGIT"); }
						if ((termmsk&dx.TM_PATTERN)==dx.TM_PATTERN) { info("dx.TM_PATTERN"); }
						if ((termmsk&dx.TM_TONE)==dx.TM_TONE) { info("dx.TM_TONE"); }
					}
				}
			}
			else if (result == dx.CR_BUSY) { logger.info("CR_BUSY"); }
			else if (result == dx.CR_CEPT) { logger.info("CR_CEPT"); }
			else if (result == dx.CR_ERROR) { logger.info("CR_ERROR"); }
			else if (result == dx.CR_FAXTONE) { logger.info("CR_FAXTONE"); }
			else if (result == dx.CR_NOANS) { logger.info("CR_NOANS"); }
			else if (result == dx.CR_NODIALTONE) { logger.info("CR_NODIALTONE"); }
			else if (result == dx.CR_NORB) { logger.info("CR_NORB"); }
			else if (result == dx.CR_STOPD) { logger.info("CR_STOPD"); }
			else {
				// General Programming Tip:
				// Always put in trailing else{} clauses, even though you think it is
				// not possible.  By throwing this exception, you'll guarantee that the
				// bug will be spotted right away.  Without this, the error will 
				// manifest itself elsewhere and you'll be on a wild-goose chase for a long
				// time.
				throw new RuntimeException("ERROR: this condition should never be true.");
			}

			// Whether or not the call was connected or not, we have to hang up manually.
			// Also, guarantee that we _stay_ hung up for at least 2 seconds.  That way,
			// if some other code goes OFFHOOK, we don't accidentally hook flash.
			info("Hanging up");
			dx.sethook(dev,dx.DX_ONHOOK,dx.EV_SYNC);
			try { Thread.sleep(2000); } catch (Exception ignore) { }

			// We are done with the device, so close it.
			info("Closing");
			dx.close(dev);

			info("Done");
		}
		catch (JVRException e) {
			logger.throwing(Example1.class.getName(),"runExample",e);
		}
	}
}

