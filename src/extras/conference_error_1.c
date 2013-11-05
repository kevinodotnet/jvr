/*
 * $Id: conference_error_1.c,v 1.3 2004/07/02 18:11:37 kevino Exp $
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
#include <msilib.h>

/**
 * This programs sole goal is to create too many conferences + conferencees
 * to verify what errors are returned, and when.  Just confirming a bug I
 * have in another program.
 *
 * This is not usefull code.
 */

int
main (int argc, char **argv)
{

	int msiBH;
	int msiH[8];
	int dxH[24];
	int dtH[24];
	long msiTS[8];
	long dxTS[24];
	long dtTS[24];
	int x,y;
	char *deviceName[10];
	SC_TSINFO tsinfo;
	MS_CDT cdt[4];
	int confId[100];

	//
	// Open everything
	//
	if ((msiBH = ms_open("msiB1",0)) == -1) {
		printf("ms_open failed; (board)\n");
		exit(1);
	}
	tsinfo.sc_numts = 1;
	for (x = 1; x <= 8; x++) {
		sprintf(deviceName,"msiB1C%d",x);
		if ((msiH[x-1] = ms_open(deviceName,0)) == -1) {
			printf("ms_open failed\n");
			exit(1);
		}
		tsinfo.sc_tsarrayp = &msiTS[x-1];
		if (ms_getxmitslot(msiH[x-1],&tsinfo) == -1) {
			printf("ms_getxmitslot() failed\n");
			exit(1);
		}
	}
	for (x = 1; x <= 6; x++) {
		for (y = 1; y <= 4; y++) {
			sprintf(deviceName,"dxxxB%dC%d",x,y);
			if ((dxH[((x-1)*4+y)-1] = dx_open(deviceName,0)) == -1) {
				printf("dx_open failed\n");
				exit(1);
			}
			tsinfo.sc_tsarrayp = &dxTS[((x-1)*4+y)-1];
			if (dx_getxmitslot(dxH[((x-1)*4+y)-1],&tsinfo) == -1) {
				printf("dx_getxmitslot() failed\n");
				exit(1);
			}
		}
	}
	for (x = 1; x <= 24; x++) {
		sprintf(deviceName,"dtiB1T%d",x);
		if ((dtH[x-1] = dt_open(deviceName,0)) == -1) {
			printf("dt_open failed\n");
			exit(1);
		}
		tsinfo.sc_tsarrayp = &dtTS[x-1];
		if (dt_getxmitslot(dtH[x-1],&tsinfo) == -1) {
			printf("dt_getxmitslot() failed\n");
			exit(1);
		}
	}

	//
	// Create conferences (8 conferences of 4 members)
	// to max out conference resources (32 total)
	//
	for (x = 0; x <= 7; x++) {
		printf("Starting conference %d ",x);
		cdt[0].chan_num = x+1;  
		cdt[0].chan_sel = MSPN_STATION;  
		cdt[0].chan_attr = MSPA_NULL;
		cdt[1].chan_num = dxTS[x];
		cdt[1].chan_sel = MSPN_TS;
		cdt[1].chan_attr = MSPA_NULL;
		cdt[2].chan_num = dtTS[x];
		cdt[2].chan_sel = MSPN_TS;
		cdt[2].chan_attr = MSPA_NULL;
		cdt[3].chan_num = dxTS[x+8];
		cdt[3].chan_sel = MSPN_TS;
		cdt[3].chan_attr = MSPA_NULL;
		if (ms_estconf(msiBH, cdt, 4, MSCA_NULL, &confId[x]) != 0) { 
			printf("ms_estconf() failed: %s\n",ATDV_ERRMSGP(msiBH));
			exit(1);
		}
		printf("\n");
	}

	//
	// Delete all conferences
	//
	for (x = 0; x <= 7; x++) {
		if (ms_delconf(msiBH, confId[x]) == -1) {
			printf("ms_delconf() failed\n");
			exit(1);
		}
	}

	//
	// Close everything
	//
	for (x = 1; x <= 8; x++) {
		if (ms_close(msiH[x-1]) == -1) {
			printf("ms_close() failed\n");
		}
	}
	for (x = 1; x <= 6; x++) {
		for (y = 1; y <= 4; y++) {
			if (dx_close(dxH[((x-1)*4+y)-1]) == -1) {
				printf("dx_close() failed; x:%d y:%d\n",x,y);
			}
		}
	}
	for (x = 1; x <= 24; x++) {
		if (dt_close(dtH[x-1]) == -1) {
			printf("dt_close() failed\n");
		}
	}

}
