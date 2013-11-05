/*
 * $Id: dtmf_state_machine.c,v 1.2 2004/07/02 18:11:37 kevino Exp $
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

#ifdef WIN32
#include <windows.h>
#endif
#include <stdio.h>
#include <fcntl.h>
#include <srllib.h>
#include <dxxxlib.h>
#include <dtilib.h>
#include <sctools.h>

// Voice resource
char *dxDev = NULL;

// MSI station
char *msiDev = NULL;

// Handles after open()
int dx;
int msi;

// SC timeslot information
long dxTsNum;
long msiTsNum;
SC_TSINFO dxTs;
SC_TSINFO msiTs;

// Digital info
long tsBits;

// DTMF string to watch for.
char *dtmf = NULL;

// Position within DTMF that the state machine is in.
int dtmfIndex = 0;

// Used with dx_getdig()
DV_DIGIT digitBuf;
int digits = -1;

// Termination conditions
DV_TPT tpt = {IO_EOT, DX_MAXDTMF, 1, TF_MAXDTMF};

/**
 * 
 */
int getArgs (int argc, char **argv) {

	int opt;
	extern char *optarg;
	extern int optind, opterr, optopt;

	while ((opt = getopt(argc,argv, "d:m:s:")) != -1) {
		switch (opt) {
			case 'd':
				dxDev = (char*) malloc(strlen(optarg)+1);
				strncpy(dxDev,optarg,strlen(optarg)+1);
				break;
			case 'm':
				msiDev = (char*) malloc(strlen(optarg)+1);
				strncpy(msiDev,optarg,strlen(optarg)+1);
				break;
			case 's':
				dtmf = (char*) malloc(strlen(optarg)+1);
				strncpy(dtmf,optarg,strlen(optarg)+1);
				break;
		}
	}
	if (dxDev == NULL) { printf("-d dxxxBXCY missing.\n"); return -1; }
	if (msiDev == NULL) { printf("-m msiBXCY missing.\n"); return -1; }
	if (dtmf == NULL) { printf("-s dtmf_string missing\n"); return -1; }
	return 0;
}

/**
 *
 */
int init () {
	printf("Opening Voice/Analog Resource: %s ",dxDev);
	if ((dx = dx_open(dxDev,0)) == -1) {
		printf("failed\n");
		return -1;
	}
	printf("handle: %d\n",dx);
	printf("Opening MSI Resource: %s ",msiDev);
	if ((msi = ms_open(msiDev,0)) == -1) {
		printf("failed\n");
		return -1;
	}
	printf("handle: %d\n",msi);
	dxTs.sc_numts = 1;
	dxTs.sc_tsarrayp = &dxTsNum;
	msiTs.sc_numts = 1;
	msiTs.sc_tsarrayp = &msiTsNum;
	if (dx_getxmitslot(dx,&dxTs) == -1) {
		printf("dx_getxmitslot() failed: %s\n",ATDV_ERRMSGP(dx));
		return -1;
	}
	if (ms_getxmitslot(msi,&msiTs) == -1) {
		printf("msi_getxmitslot() failed: %s\n",ATDV_ERRMSGP(msi));
		return -1;
	}
	if (dx_listen(dx,&msiTs) == -1) {
		printf("dx_listen() failed: %s\n",ATDV_ERRMSGP(dx));
		return -1;
	}
	if (ms_listen(msi,&dxTs) == -1) {
		printf("ms_listen() failed: %s\n",ATDV_ERRMSGP(msi));
		return -1;
	}
	return 0;
}

int main (int argc, char **argv) {
	//
	// Get and validate the command line; setup global configuration variables.
	//
	if (getArgs(argc,argv) == -1) {
		exit(1);
	}
	//
	// Initialize the Dialogic resources.
	//
	if (init() == -1) {
		exit(1);
	}
	//
	// Loop forever until the DTMF state machine is satisfied.
	//
	while (1) {
		printf("DTMF state machine. dtmfIndex: %d dtmf: %s expecting: %c\n",dtmfIndex,dtmf,dtmf[dtmfIndex]);
		if (dx_recvox(dx, "c:\\junk.vox", &tpt, NULL, EV_SYNC) == -1) {
			printf("dx_recvox failed: %s\n",ATDV_ERRMSGP(dx));
			break;
		}
		// Make sure we terminated due to DTMF
		if (ATDX_TERMMSK(dx) & TM_MAXDTMF) {
			printf("DTMF happened\n");
			// Get the digit that was received.
			if ((digits = dx_getdig(dx,&tpt,&digitBuf,EV_SYNC)) == -1) {
				printf("dx_getdig() failed: %s\n",ATDV_ERRMSGP(dx));
				break;
			}
			printf("got digit: %c\n",digitBuf.dg_value[0]);
			if (digitBuf.dg_value[0] == dtmf[dtmfIndex]) {
				dtmfIndex++;
				if (dtmfIndex == strlen(dtmf)) {
					printf("  END OF STATE MACHINE!\n");
					break;
				}
				printf("  and it matches!\n");
			}
			else {
				printf("  no match.\n");
				dtmfIndex = 0;
			}
			dx_clrdigbuf(dx);
		}
		else {
			printf("Some other termination mask.\n");
		}
	}
	//
	// Shutdown and exit.
	//
	ms_close(msi);
	dx_close(dx);
	exit(0);
}
