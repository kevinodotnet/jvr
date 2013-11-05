/*
 * $Id: ag.cpp,v 1.6 2005/02/03 00:30:47 kevino Exp $
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

#include "common.h"
#include "net_threebit_jvr_ag.h"

/*
 * Class:     net_threebit_jvr_ag
 * Method:    listen
 * Signature: (ILnet/threebit/jvr/SC_TSINFO;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ag_listen
  (JNIEnv *e, jclass c, jint dev, jobject jtsinfo)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { JVR_EXIT; return -1; }
	int ret = ag_listen(dev, &tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ag_listen(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return -1;
}

/*
 * Class:     net_threebit_jvr_ag
 * Method:    unlisten
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ag_unlisten
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = ag_unlisten(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ag_unlisten(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ag
 * Method:    getxmitslot
 * Signature: (I)Lnet/threebit/jvr/SC_TSINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_ag_getxmitslot
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;

	int ret = ag_getxmitslot(dev,&tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ag_getxmitslot(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
		JVR_EXIT; return NULL;
	}
	jclass clazz = sc_tsinfo_class; // e->Find_Class("net/threebit/jvr/SC_TSINFO");
	jmethodID init = e->GetMethodID(clazz, "<init>", "()V");
	jobject jtsinfo = e->NewObject(clazz, init);
	if (jvr_convert_sctsinfo_n2j(e,jtsinfo,&tsinfo) == -1) {
		JVR_EXIT; return NULL;
	}
	JVR_EXIT; return jtsinfo;
}

