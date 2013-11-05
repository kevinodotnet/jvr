/*
 * $Id: wtcallid_test.cpp,v 1.3 2004/07/02 18:11:38 kevino Exp $
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
	int dev;
	short val = -1;

	printf("Opening device.\n");
	if ((dev=dx_open("dxxxB1C1",0))==-1) {
		printf("dx_open() failed\n");
		exit(0);
	}

	printf("Getting DXCH_CALLID value.\n");
	if (dx_getparm(dev,DXCH_CALLID,(void*)&val)==-1) {
		printf("dx_getparm() failed: %s\n",ATDV_ERRMSGP(dev));
		exit(0);
	}
	printf("DXCH_CALLID value: %d\n",val);

	val = DX_CALLIDENABLE;
	printf("Setting DXCH_CALLID value.\n");
	if (dx_setparm(dev,DXCH_CALLID,(void*)&val)==-1) {
		printf("dx_setparm() failed: %s\n",ATDV_ERRMSGP(dev));
		exit(0);
	}

	val = -1;
	printf("Getting DXCH_CALLID value.\n");
	if (dx_getparm(dev,DXCH_CALLID,(void*)&val)==-1) {
		printf("dx_getparm() failed: %s\n",ATDV_ERRMSGP(dev));
		exit(0);
	}
	printf("DXCH_CALLID value: %d\n",val);

	printf("Waiting for a call (with CallID information)\n");
	unsigned char buffer[64];
	for (int x = 0; x < 64; buffer[x] = 0); 
	if (dx_wtcallid(dev,2,-1,buffer)==-1) {
		printf("dx_wtcallid() failed: %d %s\n",ATDV_ERRMSGP(dev));
		exit(0);
	}
	printf("dx_wtcallid() OK!");

	dx_close(dev);
	exit(0);

}
