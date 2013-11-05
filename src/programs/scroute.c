/*
 * $Id: scroute.c,v 1.3 2004/07/02 18:11:38 kevino Exp $
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

// System
#include <stdio.h>
#include <stdarg.h>
#include <sys/errno.h>
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

int main (int argc, char **argv) {

	// -s <name> Name of the source device
	char *source = NULL;

	// -d <name> Name of the destination device
	char *dest = NULL;

	// -S Where -s is an voice/analog resource (dxxx), select the analog device (voice is assumed by default)
	int sourceAnalog = FALSE;

	// -D Where -d is an voice/analog resource (dxxx), select the analog device (voice is assumed by default)
	int destAnalog = FALSE;

	// -u Unidirectional (bidirectional is assumed)
	int uni = FALSE;

	// For getopt()
	int opt;
	extern char *optarg;
	extern int optind, opterr, optopt;

	// MS/DX/DT for source/dest
	int sourceMS = 0;
	int sourceDX = 0;
	int sourceDT = 0;
	int destMS = 0;
	int destDX = 0;
	int destDT = 0;

	// Timeslots
	SC_TSINFO sourceTS;
	SC_TSINFO destTS;

	// Device handles
	int sourceDev;
	int destDev;

	// Process the command line
	while ((opt = getopt(argc,argv, "s:d:SDU")) != -1) {
		switch (opt) {
			case 's':
				source = (char*) malloc (strlen(optarg)+1);
				strncpy(source,optarg,strlen(optarg)+1);
				break;
			case 'd':
				dest = (char*) malloc (strlen(optarg)+1);
				strncpy(dest,optarg,strlen(optarg)+1);
				break;
			case 'S':
				sourceAnalog = TRUE;
				break;
			case 'D':
				destAnalog = TRUE;
				break;
			case 'U':
				uni = TRUE;
				break;
		}
	}

	if (source == NULL) { printf("-s DEVICENAME missing\n"); exit(1); }
	if (dest == NULL) { printf("-d DEVICENAME missing\n"); exit(1); }

	/*
	printf("source: %s (%d)\n",source,sourceAnalog);
	printf("dest  : %s (%d)\n",dest,destAnalog);
	printf("uni   : %d\n",uni);
	*/

	if (strncmp(source,"dxxx",4) == 0) { sourceDX = 1; }
	else if (strncmp(source,"msi",3) == 0) { sourceMS = 1; }
	else if (strncmp(source,"dt",2) == 0) { sourceDT = 1; }
	else { printf("Unknown device type: %s\n",source); exit(1); }
	if (strncmp(dest,"dxxx",4) == 0) { destDX = 1; }
	else if (strncmp(dest,"msi",3) == 0) { destMS = 1; }
	else if (strncmp(dest,"dt",2) == 0) { destDT = 1; }
	else { printf("Unknown device type: %s\n",dest); exit(1); }

	//
	// Source OPEN and TSINFO
	//
	if (sourceDX) {
		sourceDev = dx_open(source,0);
		if (sourceAnalog) {
			ag_getxmitslot(sourceDev,&sourceTS);
		}
		else {
			dx_getxmitslot(sourceDev,&sourceTS);
		}
	}
	if (sourceMS) {
		sourceDev = ms_open(source,0);
		ms_getxmitslot(sourceDev,&sourceTS);
	}
	if (sourceDT) {
		sourceDev = dt_open(source,0);
		dt_getxmitslot(sourceDev,&sourceTS);
	}
	if (sourceDev == -1) { printf("Error opening source device.\n"); exit(1); }
	//
	// Dest OPEN and TSINFO
	//
	if (destDX) {
		destDev = dx_open(dest,0);
		if (destAnalog) {
			ag_getxmitslot(destDev,&destTS);
		}
		else {
			dx_getxmitslot(destDev,&destTS);
		}
	}
	if (destMS) {
		destDev = ms_open(dest,0);
		ms_getxmitslot(destDev,&destTS);
	}
	if (destDT) {
		destDev = dt_open(dest,0);
		dt_getxmitslot(destDev,&destTS);
	}
	if (destDev == -1) { printf("Error opening dest device.\n"); exit(1); }
	//
	// Destination listens to source
	//
	if (destDX) {
		dx_listen(destDev,&sourceTS);
	}
	if (destMS) {
		ms_listen(destDev,&sourceTS);
	}
	if (destDT) {
		dt_listen(destDev,&sourceTS);
	}
	if (! uni) {
		if (sourceDX) {
			dx_listen(sourceDev,&destTS);
		}
		if (sourceMS) {
			ms_listen(sourceDev,&destTS);
		}
		if (sourceDT) {
			dt_listen(sourceDev,&destTS);
		}
	}
	//
	// Close
	//
	if (sourceDX) { dx_close(sourceDev); }
	if (sourceMS) { ms_close(sourceDev); }
	if (sourceDT) { dt_close(sourceDev); }
	if (destDX) { dx_close(destDev); }
	if (destMS) { ms_close(destDev); }
	if (destDT) { dt_close(destDev); }
	exit(0);
}
