package net.threebit.jvr;

/*
 * $Id: MetaEvent.java,v 1.15 2005/01/05 02:06:20 kevino Exp $
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

import java.lang.reflect.*;

/**
 * Java equivalent of the Dialogic METAEVENT structure.  Note that the case
 * of the class name as been modified to match Java conventions.
 */

public class MetaEvent extends JVREventBaseClass {

	public long magicno;                

	/** <code>unsigned long</code> becomes <code>long</code>. */
	public long flags;  

	/** <code>void*</code> becomes an Object. */
	public Object evtdata;

	public long evtlen;

	public long evtdev;

	public long evttype;

	public long linedev;  

	public long crn;  

	/** <code>void*</code> becomes an Object. */
	public Object extevtdatap;  

	/** <code>void*</code> becomes an Object. */
	public Object usrattr;  

	public int cclibid;  

	public int rfu1;  

	/**
	 * Global Call information about this event.  It is provided by default
	 * because it may not be possible to get it after the original METAEVENT
	 * pointer/struct has passed out of scope.
	 */
	public GC_INFO gcInfo = null;

	/**
	 * True if the event is a global-call specific event.  Shorthand for
	 * <p>
	 * <pre>
	 * ((event.flags &amp; gc.GCME_GC_EVENT) == gc.GCME_GC_EVENT);
	 * </pre>
	 */
	public boolean isGC() {
		return ((flags&gc.GCME_GC_EVENT)==gc.GCME_GC_EVENT);
	}

	/**
	 *
	 */
	public String toString() {
		String s = "MetaEvent{";
		String evtsymbol = null;
		if (isGC()) {
			// Reflect into the jvr class for all int members that match
			// GCEV_*.  Look for a member with the same value as evttype
			// and display the field name instead.  If no match, just display
			// the int value.
			evtsymbol = jvr.symbolName("GCEV_",evttype);
			/*
			evtsymbol = null;
			Field[] fields = jvr.class.getDeclaredFields();
			try {
				for (int x = 0; x < fields.length; x++) {
					if (fields[x].getName().startsWith("GCEV_")) {
						if (fields[x].getInt(jvr.class) == evttype) {
							evtsymbol = fields[x].getName();
							break;
						}
					}
				}
			}
			catch (IllegalAccessException ignore) { logger.info(""+ignore); }
			if (evtsymbol != null) { s += evtsymbol; }
			else { s += evttype; }
			*/
		}
		else {
			String deviceName = "";
			try { 
				deviceName = sr.ATDV_NAMEP((int)evtdev); 
			} 
			catch (JVRException ignore) { }
			if (deviceName.startsWith("dxxx")) {
				long termmsk = -1;
				try { termmsk = dx.ATDX_TERMMSK((int)evtdev); } catch (JVRException ignore) { }
				evtsymbol = jvr.symbolName("TDX_",evttype) + "/" + jvr.symbolName("TM_",termmsk);
			}
			else {
				// MSI? Other device types.
				// TODO: try at least.
			}
			if (evtsymbol == null) {
				evtsymbol = ""+evttype; 
			}
		}
		s += evtsymbol;
		s += ";";
		if (isGC()) {
			s += "linedev=" + linedev + ";";
			s += "crn=" + crn + ";";
			if (crn > 0 && evttype !=gc.GCEV_RELEASECALL) {
				// insert call state
				/*
				try {
					s += "crnState="+jvr.symbolName("GCST_",gc.GetCallState(crn))+";";
				}
				catch (JVRException nonFatal) {
					logger.throwing(getClass().getName(),"toString",nonFatal);
				}
				*/
			}
			if (
				(gcInfo.gcValue != gc.GCRV_NORMAL) && 
				(evtsymbol != null && (!evtsymbol.equals("GCEV_UNBLOCKED"))) &&
				(evtsymbol != null && (!evtsymbol.equals("GCEV_PROCEEDING"))) &&
				(evtsymbol != null && (!evtsymbol.equals("GCEV_ALERTING"))) &&
				(evtsymbol != null && (!evtsymbol.equals("GCEV_PROGRESSING"))) &&
				(!"Success".equals(gcInfo.gcMsg))
				) {
				s += gcInfo;
			}
		}
		else {
			s += "evtdev=" + evtdev + ";";
			s += "evtdata=" + evtdata + ";"; // suppressed because who cares about it right now.
		}
		s += "age="+age()+";";
		s += "}";
		return s;
		// s += "flags=" + flags + ";";
		// s += "evtlen=" + evtlen + ";"; // suppressed because who cares about it right now.
		// s += "extevtdatap=" + extevtdatap + ";"; // suppressed because who cares about it right now.
		// s += "cclibid=" + cclibid + ";"; // suppressed because who cares about it right now.
		// s += "magicno=" + magicno + ";"; // suppressed since it is for Dialogic use only.
		// s += "usrattr=" + usrattr + ";"; // suppressed since it is not supported by JVR yet.
		// s += "rfu1=" + rfu1; // suppressed since it is reserved for use by Dialogic.
	}

}
