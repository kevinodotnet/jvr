package net.threebit.jvr;

/*
 * $Id: JVRBaseClass.java,v 1.3 2004/07/02 18:11:38 kevino Exp $
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

import java.util.logging.Logger;

/**
 * The base class for all JVR classes.  Provides basic logging and other
 * utility functions.
 */

public class JVRBaseClass implements java.io.Serializable {

	/**
	 * Debugging logger.
	 */
	public static final Logger logger = Logger.getLogger("net.threebit.jvr");

}
