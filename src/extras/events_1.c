/*
 * $Id: events_1.c,v 1.3 2004/07/02 18:11:37 kevino Exp $
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

int kickit = 1;

#ifdef WIN32
long int event_handler_1 (unsigned long evhandle)  
{  
	printf("event_handler_1(%d); eventType: 0x%x\n",sr_getevtdev(evhandle),sr_getevttype(evhandle));
	return(0);
}

long int event_handler_2 (unsigned long evhandle)  
{  
	printf("event_handler_2(%d); eventType: 0x%x\n",sr_getevtdev(evhandle),sr_getevttype(evhandle));
	kickit = 1;
	return(1);
}

int
main (int argc, char **argv)
{
	int dev;
	int x = 1;
	printf("Enabling handler 1\n");
	if (sr_enbhdlr(EV_ANYDEV, EV_ANYEVT,&event_handler_1) == -1) {
		printf("sr_enbhdlr() failed\n");
		exit(1);
	}
	printf("Enabling handler 2\n");
	if (sr_enbhdlr(EV_ANYDEV, EV_ANYEVT,&event_handler_2) == -1) {
		printf("sr_enbhdlr() failed\n");
		exit(1);
	}
	printf("Opening device\n");
	dev = dx_open("dxxxB1C1",0);
	printf("  handle: %d\n",dev);
	while (x++) {
		printf("[%d] Worker thread...\n",x);
		if (kickit && x%5==0) {
			printf("  kickit\n");
			Sleep(500);
			printf("  setting HOOK\n");
			dx_sethook(dev,DX_ONHOOK,EV_ASYNC);
			kickit = 0;
			printf("  sleeping\n");
		}
		Sleep(1000);
	}
}
#endif

#ifdef LINUX
long event_handler_1 (void *p)
{  
	printf("event_handler_1(%d); eventType: 0x%x\n",sr_getevtdev(),sr_getevttype());
	kickit = 1;
	return(0);
}

int
main (int argc, char **argv)
{
	int dev;
	int x = 1;
	int par = SR_SIGMODE;
	if (sr_setparm(SRL_DEVICE, SR_MODEID, &par ) == -1 ) {
		printf("sr_setparm failed\n");
		return;
	}
	printf("Enabling handler 1\n");
	if (sr_enbhdlr(EV_ANYDEV, EV_ANYEVT,event_handler_1) == -1) {
		printf("sr_enbhdlr() failed\n");
		exit(1);
	}
	printf("Opening device\n");
	dev = dx_open("dxxxB1C1",0);
	printf("  handle: %d\n",dev);
	while (x++ < 20) {
		printf("[%d] Worker thread...\n",x);
		if (kickit && x%3==0) {
			printf("  kickit\n");
			sleep(1);
			printf("  setting HOOK\n");
			dx_sethook(dev,DX_ONHOOK,EV_ASYNC);
			kickit = 0;
			printf("  sleeping\n");
		}
		sleep(1);
	}
}
#endif
