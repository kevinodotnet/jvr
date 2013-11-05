/*
 * $Id: dt.cpp,v 1.12 2005/02/03 00:30:47 kevino Exp $
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
#include "net_threebit_jvr_dt.h"

/*
 * Class:     net_threebit_jvr_dt
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_open
  (JNIEnv *e, jclass c, jstring name, jint flags)
{
	JVR_ENTER;
	char *namep = (char*) e->GetStringUTFChars(name,NULL);
	int ret = dt_open(namep,flags);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_open(\"%s\",%d) failed",namep,flags);
	}
	e->ReleaseStringUTFChars(name,namep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_close
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dt_close(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_close(%d) failed;",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    setevtmsk
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_setevtmsk
  (JNIEnv *e, jclass c, jint dev, jint event, jint mask, jint action)
{
	JVR_ENTER;
	int ret = dt_setevtmsk(dev,event,(unsigned short)mask,action);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_setevtmsk(%d,%d,%d,%d) failed: %s",dev,event,mask,action,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    listen
 * Signature: (ILnet/threebit/jvr/SC_TSINFO;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_listen
  (JNIEnv *e, jclass c, jint dev, jobject jtsinfo)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { JVR_EXIT; return -1; }
	int ret = dt_listen(dev, &tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_listen(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return -1;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    unlisten
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_unlisten
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dt_unlisten(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_unlisten(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    getxmitslot
 * Signature: (I)Lnet/threebit/jvr/SC_TSINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_dt_getxmitslot
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;

	int ret = dt_getxmitslot(dev,&tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_getxmitslot(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
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

/*
 * Class:     net_threebit_jvr_dt
 * Method:    xmitwink
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_xmitwink
  (JNIEnv *e, jclass c, jint dev, jint timeout)
{
	JVR_ENTER;
	int ret = dt_xmitwink(dev,(unsigned int)timeout);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_xmitwink(%d,%d) failed: %s",dev,timeout,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    settssigsim
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_settssigsim
  (JNIEnv *e, jclass c, jint dev, jint bitmask)
{
	JVR_ENTER;
	int ret = dt_settssigsim(dev,(unsigned short)bitmask);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_settssigsim(%d,%d) failed: %s",dev,bitmask,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    settssig
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dt_settssig
  (JNIEnv *e, jclass c, jint dev, jint bitmask, jint action)
{
	JVR_ENTER;
	int ret = dt_settssig(dev,(unsigned short)bitmask,action);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dt_settssig(%d,%d,%d) failed: %s",dev,bitmask,action,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    ATDT_TSSGBIT
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dt_ATDT_1TSSGBIT
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDT_TSSGBIT(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDT_TSSGBIT(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dt
 * Method:    ATDT_STATUS
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dt_ATDT_1STATUS
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDT_STATUS(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDT_STATUS(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}
