/*
 * $Id: dx_open_test.cpp,v 1.3 2004/07/02 18:11:38 kevino Exp $
 *
 * Copyright (c) 2004 Kevin O'Donnell
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

// Because Dialogic header files do not support GCC on win32.
#ifdef __GNUC__
#define __cdecl
#endif

// System
#include <stdio.h>
#include <stdarg.h>
#include <sys/errno.h>
#include <string.h>
#ifdef LINUX
#include <stdlib.h>
#endif
// Linux/Win32 differences
#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif
// Dialogic
#include <srllib.h>
#include <dxxxlib.h>
#include <dtilib.h>
#include <sctools.h>
#include <msilib.h>
#include <dxdigit.h>
#include <gclib.h>  
#include <gcerr.h>  

/**
 * Simple test program that opens the "dxxxB1C1" voice resource.
 * If you receive errors when you run this program then your
 * Dialogic installation is not working correctly (so don't
 * expect JVR to work at all either).
 */
int main (int argc, char **argv) {

	if (dx_open("dxxxB1C1",0) == -1) {
#ifdef WIN32
		printf("FAILED (errno=%d)\n",dx_fileerrno());
#endif
#ifdef LINUX
		printf("FAILED (errno=%d)\n",errno);
#endif
		exit(1);
	}

	printf("OK\n");
	exit(0);
}
