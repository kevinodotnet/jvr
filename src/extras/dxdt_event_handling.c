/*
 * $Id: dxdt_event_handling.c,v 1.2 2004/07/02 18:11:37 kevino Exp $
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

#define TRUE 1
#define FALSE 0

// Voice resource
char *dxDev = NULL;

// Network resource (optional)
char *dtDev = NULL;

// Handles after open()
int dx;
int dt;

// SC timeslot information
long dxTsNum;
long dtTsNum;
SC_TSINFO dxTs;
SC_TSINFO dtTs;

// Digital info
long tsBits;

// True when -n specified.
int digital = FALSE;

// Stage of the finite state machine.
int stage = 0;

int getArgs (int argc, char **argv) {

	int opt;
	extern char *optarg;
	extern int optind, opterr, optopt;

	while ((opt = getopt(argc,argv, "d:n:")) != -1) {
		switch (opt) {
			case 'd':
				dxDev = (char*) malloc(strlen(optarg)+1);
				strncpy(dxDev,optarg,strlen(optarg)+1);
				break;
			case 'n':
				dtDev = (char*) malloc(strlen(optarg)+1);
				strncpy(dtDev,optarg,strlen(optarg)+1);
				digital = TRUE;
				break;
		}
	}
	if (dxDev == NULL) { printf("-d dxxxBXCY missing.\n"); return -1; }
	return 0;
}

int init () {
	int mode;
	//
	// Set the System Runtime Library (SRL) to Asynchronous mode.
	//
	printf("Setting SRL parameters\n");
#ifdef LINUX
	mode = SR_POLLMODE;
	if (sr_setparm(SRL_DEVICE, SR_MODEID, &mode) == -1) {
		printf("sr_setparm() failed (SR_MODEID): %s\n",ATDV_ERRMSGP(SRL_DEVICE));
		return -1;
	}
#endif
#ifdef WIN32
	mode = SR_STASYNC;
	if (sr_setparm(SRL_DEVICE, SR_MODELTYPE, &mode) == -1) {
		printf("sr_setparm() failed (SR_MODELTYPE): %s\n",ATDV_ERRMSGP(SRL_DEVICE));
		return -1;
	}
	//
	// Set the SRL to polled mode (I guess this means that we do not
	// register an event handler function, but call sr_waitevt() instead.)
	//
	mode = SR_POLLMODE;
	if (sr_setparm(SRL_DEVICE, SR_MODEID, &mode ) == -1) {
		printf("sr_setparm() failed (SR_MODEID): %s\n",ATDV_ERRMSGP(SRL_DEVICE));
		return -1;
	}
#endif
	//
	// Open the resources.
	//
	printf("Opening Voice/Analog Resource: %s ",dxDev);
	if ((dx = dx_open(dxDev,0)) == -1) {
		printf("failed\n");
		return -1;
	}
	printf("handle: %d\n",dx);
	if (digital) {
		printf("Opening network resource: %s ",dtDev);
		if ((dt = dt_open(dtDev,0)) == -1) {
			printf("failed\n");
			return -1;
		}
		printf("handle: %d\n",dt);
	}
	printf("SCBus Routing\n");
	dxTs.sc_numts = 1;
	dxTs.sc_tsarrayp = &dxTsNum;
	dtTs.sc_numts = 1;
	dtTs.sc_tsarrayp = &dtTsNum;
	if (dx_getxmitslot(dx,&dxTs) == -1) {
		printf("dx_getxmitslot() failed: %s\n",ATDV_ERRMSGP(dx));
		return -1;
	}
	if (dt_getxmitslot(dt,&dtTs) == -1) {
		printf("dt_getxmitslot() failed: %s\n",ATDV_ERRMSGP(dt));
		return -1;
	}
	if (dx_listen(dx,&dtTs) == -1) {
		printf("dx_listen() failed: %s\n",ATDV_ERRMSGP(dx));
		return -1;
	}
	if (dt_listen(dt,&dxTs) == -1) {
		printf("dt_listen() failed: %s\n",ATDV_ERRMSGP(dt));
		return -1;
	}
	//
	// Event Masks.
	//
	if (digital) {
		if (dt_setevtmsk(dt,DTG_SIGEVT,DTMM_AON|DTMM_AOFF|DTMM_BON|DTMM_BOFF|DTMM_WINK,DTA_SETMSK) == -1) {
			printf("dt_setevtmsk() failed. %s\n",ATDV_ERRMSGP(dt));
			return -1;
		}
	}
	else {
		printf("Analog example is not ready yet!\n");
		return -1;
	}
	//
	// Set initial hook state.
	//
	// goOnHook();
#ifdef WIN32
	Sleep(1000);
#endif
#ifdef LINUX
	sleep(1);
#endif
}

void printSigBits(int dev) {
	long tsBits;
	if ((tsBits = ATDT_TSSGBIT(dev)) == AT_FAILURE) {
		printf("Signal Bits dev: %d ATDT_TSSGBIT() failed %s.",ATDV_ERRMSGP(dev));
		return;
	}
	printf("RX %ld/%ld TX %ld/%ld",
		(tsBits & DTSG_RCVA)?1:0,
		(tsBits & DTSG_RCVB)?1:0,
		(tsBits & DTSG_XMTA)?1:0,
		(tsBits & DTSG_XMTB)?1:0
	);
}

int sigbit(int dev,long bit) {
	long tsBits;
	if ((tsBits = ATDT_TSSGBIT(dev)) == AT_FAILURE) {
		printf("Signal Bits dev: %d ATDT_TSSGBIT() failed %s.",ATDV_ERRMSGP(dev));
		exit(1);
	}
	return ((tsBits & bit)?1:0);
}

int close () {
	printf("Closing resources\n");
	if (dx_close(dx) == -1) {
		printf("dx_close() failed: %s\n",ATDV_ERRMSGP(dx));
		return -1;
	}
	if (dt_close(dt) == -1) {
		printf("dt_close() failed: %s\n",ATDV_ERRMSGP(dt));
		return -1;
	}
}

int onhook (int dev) {
	long tsBits;
	int hookState;
	if (digital) {
		if ((tsBits = ATDT_TSSGBIT(dev)) == AT_FAILURE) {
			printf("Signal Bits dev: %d ATDT_TSSGBIT() failed %s.",ATDV_ERRMSGP(dev));
			exit(1);
		}
		hookState = ((tsBits & DTSG_XMTA)?0:1) | ((tsBits & DTSG_XMTB)?0:1);
		return hookState;
	}
	else {
		printf("%d NOT supported on non-digital yet.",__LINE__);
	}
}

void goOnHook() {
	if (digital) {
		if ((dt_settssig (dt, DTB_AOFF|DTB_BBIT, DTA_SETMSK)) == -1) {
			printf("dt_settssig() failed; could not go ON HOOK. %s\n",ATDV_ERRMSGP(dt));
			return -1;
		}
	}
	else {
		printf("%d NOT supported on non-digital yet.",__LINE__);
	}
}

/*
void goOffHook() {
	if (digital) {
		// printSigBits(dt);
		if (dt_settssig (dt, DTB_ABIT|DTB_BBIT, DTA_SETMSK) == -1) {
			printf("dt_settssig() failed; could not go ON HOOK. %s\n",ATDV_ERRMSGP(dt));
		}
		// printSigBits(dt);
	}
	else {
		printf("%d NOT supported on non-digital yet.",__LINE__);
	}
}
*/

void processEvents() {
	long timeout;
	int dev;
	int type;
	long bitmask;
	DX_CST *cstp;
	unsigned short *dt_data;

	printf("Initial signal bit states: ");
	printSigBits(dt);
	printf("\n");

	while (TRUE) {
		//
		// Wait for the next event on a device.
		//
		printf("waiting (%s) ... ",(onhook(dt)?"ONHOOK":"OFFHOOK")); fflush(stdout);
		if (sr_waitevt(1000) == -1) {
			printf("\n");
			continue;
		}
		dev = sr_getevtdev();
		type = sr_getevttype();
		printf("%s\n",(dev==dx?dxDev:dtDev));
		//
		// Handle the event.
		//
		if (dev == dx) {
		}
		else if (dev == dt) {
			printf("  ");
			switch (type) {
				case DTEV_T1ERRC:
					printf("DTEV_T1ERRC\n");
					break;
				case DTEV_SIG:
					printf("DTEV_SIG [");
					printSigBits(dev);
					printf("] ");
					dt_data = (unsigned short*) sr_getevtdatap();
					if ((*dt_data & DTMM_AON) == DTMM_AON) { 
						printf("(AON) ");
						/*
						if (onhook(dev)) {
							printf(" do not know how to handle AON when onhook ");
						}
						else {
							printf(" do not know how to handle AON when offhook ");
						}
						*/
						printf("\n");
					}
					if ((*dt_data & DTMM_AOFF) == DTMM_AOFF) { 
						printf("(AOFF) ");
						/*
						if (onhook(dev)) {
							printf(" do not know how to handle AOFF when OFFhook ");
						}
						else {
							printf(" do not know how to handle AOFF when offhook ");
						}
						*/
						printf("\n");
					}
					if ((*dt_data & DTMM_BON) == DTMM_BON) {
						printf("(BON) "); 
						if (! sigbit(dev,DTSG_RCVA)) {
							// A is off, B is on; end of RING on an offered call.
						}
						else {
							printf("DUNNO %d",__LINE__);
						}
						/*
						if (onhook(dev)) {
							printf("RING STOP ");
						}
						else {
							printf(" do not know how to handle BON when offhook ");
						}
						*/
						printf("\n");
					}
					if ((*dt_data & DTMM_BOFF) == DTMM_BOFF) {
						printf("(BOFF) ");
						//
						// 
						//
						if (! sigbit(dev,DTSG_RCVA)) {
							// A and B are both off: CO is offering a call by ringing.
							printf("Setting TX A bit to ON ");
							if (dt_settssig (dt, DTB_ABIT|DTB_BBIT, DTA_SETMSK) == -1) {
								printf("dt_settssig() failed; could not go ON HOOK. %s\n",ATDV_ERRMSGP(dt));
							}
						}
						else {
							printf("DUNNO! %d",__LINE__);
						}
						printf("\n");
					}
					break;
				case DTEV_COMRSP:
					printf("DTEV_COMRSP\n");
					break;
				case DTEV_DATRSP:
					printf("DTEV_DATRSP\n");
					break;
				case DTEV_RETDIAG:
					printf("DTEV_RETDIAG\n");
					break;
				case DTEV_WINKCPLT:
					printf("DTEV_WINKCPLT\n");
					break;
				case DTEV_RCVPDG:
					printf("DTEV_RCVPDG\n");
					break;
				case DTEV_PDDONE:
					printf("DTEV_PDDONE\n");
					break;
				case DTEV_ERREVT:
					printf("DTEV_ERREVT\n");
					break;
				case DTEV_MTFCNCPT :
					printf("DTEV_MTFCNCPT\n");
					break;
				default:
					printf("default\n");
					break;
			}
		}
		else { printf("unknown device!  Should never happen.\n"); }
	}
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
	// Enter an infinite loop that handles all of the messages.
	//
	processEvents();
	//
	// Shutodnw and exit.
	//
	if (close() == -1) {
		exit(1);
	}
	exit(0);
}
