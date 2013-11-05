/*
 * $Id: gc_1.c,v 1.3 2004/07/02 18:11:37 kevino Exp $
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
#include <gclib.h>
#include <gcerr.h>

#define  MAX_STRING_SIZE        100 

// For GC error handling.
int cclibid;
int gc_error;
int cc_error;
char *msg;

void
start_gc_cc_libraries ()
{

	// Kevin's testing environment includes analog (D/4, D/160SC-LS)
	// and digital (D/240SC-T1) hardware.
	GC_START_STRUCT gclib_start;
	CCLIB_START_STRUCT cclib_start[] = {
		{"GC_ANAPI_LIB", NULL},
		{"GC_PDKRT_LIB",NULL}
	};
	gclib_start.num_cclibs = 2;
	gclib_start.cclib_list = cclib_start;
	if (gc_Start (&gclib_start) != GC_SUCCESS) {
		gc_ErrorValue (&gc_error, &cclibid, &cc_error);
		gc_ResultMsg (LIBID_GC, (long) gc_error, &msg);
		printf ("%s:%d Error in gc_Start ErrorValue: %d - %s\n", __FILE__, __LINE__, gc_error, msg);
		exit (1);
	}
}

// http://resource.intel.com/telecom/support/releases/winnt/SR511FP1/onldoc/htmlfiles/gclibrf/gc_cclie.htm
void
print_all_cclibs_status ()
{
	int cclibidp;
	int i;
	char str[MAX_STRING_SIZE], str1[MAX_STRING_SIZE];
	GC_CCLIB_STATUSALL cclib_status_all;
	if (gc_CCLibStatusEx ("GC_ALL_LIB", &cclib_status_all) != GC_SUCCESS) {
		gc_ErrorValue (&gc_error, &cclibid, &cc_error);
		gc_ResultMsg (LIBID_GC, (long) gc_error, &msg);
		printf ("%s:%d %d - %s\n", __FILE__, __LINE__,gc_error,msg);
		exit (1);
	}
	strcpy (str, "Call Control Library Status:\n");
	for (i = 0; i < GC_TOTAL_CCLIBS; i++) {
		if (gc_CCLibNameToID(cclib_status_all.cclib_state[i].name,&cclibidp) != GC_SUCCESS) {
			gc_ErrorValue (&gc_error, &cclibid, &cc_error);
			gc_ResultMsg (LIBID_GC, (long) gc_error, &msg);
			printf ("%s:%d %d - %s\n", __FILE__, __LINE__,gc_error,msg);
			exit (1);
		}
		switch (cclib_status_all.cclib_state[i].state) {
			case GC_CCLIB_CONFIGURED:
				printf("%s\t configured\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			case GC_CCLIB_AVAILABLE:
				printf("%s\t available\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			case GC_CCLIB_FAILED:
				printf("%s\t is not available for use\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			default:
				printf("%s\t unknown CCLIB status\n", cclib_status_all.cclib_state[i].name);
				break;
		}
	}
}

void
dial() 
{
	LINEDEV dev = 0;
	CRN crn = 0;
	printf("Opening GC device: ");
	if (gc_OpenEx(&dev,":P_pdk_us_ls_fxs_io:N_dtiB1T1:V_dxxxB1C1",EV_SYNC,NULL) != GC_SUCCESS) {
		printf("gc_OpenEx failed\n");
		exit(1);
	}
	printf("%ld\n",dev);
	if (gc_ResetLineDev(dev,EV_SYNC) != GC_SUCCESS) {
		printf("gc_ResetLineDev() failed\n");
		exit(1);
	}
	printf("Dialing: ");
	if (gc_MakeCall(dev,&crn,"8672620",NULL,0,EV_SYNC) != GC_SUCCESS) {
		printf("gc_MakeCall() failed\n");
		gc_ErrorValue( &gc_error, &cclibid, &cc_error);  
		gc_ResultMsg( LIBID_GC, (long) gc_error, &msg);  
		printf("gc_MakeCall() failed: %d - %s\n",gc_error,msg);  
		exit(1);
	}
	printf("%ld\n",crn);
	if (gc_DropCall(crn, GC_NORMAL_CLEARING, EV_SYNC) != GC_SUCCESS) {
		printf("gc_DropCall failed\n");
		exit(1);
	}
	if (gc_ReleaseCallEx(crn,EV_SYNC) != GC_SUCCESS) {
		printf("gc_ReleaseCallEx() failed\n");
		exit(1);
	}
	if (gc_Close(dev) < 0) {
		printf("gc_Close() failed\n");
		exit(1);
	}
}

int
main (int argc, char **argv)
{

	start_gc_cc_libraries ();
	print_all_cclibs_status();
	dial();

}
