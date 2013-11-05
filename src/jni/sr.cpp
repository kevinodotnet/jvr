/*
 * $Id: sr.cpp,v 1.16 2004/07/02 18:11:37 kevino Exp $
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
#include "net_threebit_jvr_sr.h"

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtdev
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_getevtdev__J
  (JNIEnv *e, jclass c, jlong ehandle)
{
	JVR_ENTER;
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT; return -1;
#endif
#ifdef WIN32
	jlong ret = sr_getevtdev((unsigned long)ehandle);
	JVR_EXIT; 
	return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevttype
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_getevttype__J
  (JNIEnv *e, jclass c, jlong ehandle)
{
	JVR_ENTER;
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT; return -1;
#endif
#ifdef WIN32
	jlong ret = sr_getevttype((unsigned long)ehandle);
	JVR_EXIT;
	return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtdev
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_sr_getevtdev__
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
#ifdef WIN32
	jvr_throw(JVR_THROW_JVR,"Not supported in win32 release");
	JVR_EXIT; return -1;
#endif
#ifdef LINUX
	jint ret = sr_getevtdev();
	JVR_EXIT;
	return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevttype
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_getevttype__
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
#ifdef WIN32
	jvr_throw(JVR_THROW_JVR,"Not supported in win32 release");
	JVR_EXIT; return -1;
#endif
#ifdef LINUX
	jlong ret = sr_getevttype();
	JVR_EXIT;
	return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    ATDV_LASTERR
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_ATDV_1LASTERR
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDV_LASTERR(dev);
	if (ret == EDV_BADDESC) {
		jvr_throw(JVR_THROW_DG,"ATDV_LASTERR(%d) failed: Invalid device handle",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    ATDV_ERRMSGP
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_threebit_jvr_sr_ATDV_1ERRMSGP
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	jstring s = e->NewStringUTF(ATDV_ERRMSGP(dev));
	JVR_EXIT;
	return s;
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    ATDV_NAMEP
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_threebit_jvr_sr_ATDV_1NAMEP
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	jstring s = e->NewStringUTF(ATDV_NAMEP(dev));
	JVR_EXIT;
	return s;
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtlen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_getevtlen__J
  (JNIEnv *e, jclass c, jlong ehandle)
{
	JVR_ENTER;
#ifdef WIN32
	jlong ret = sr_getevtlen((unsigned long)ehandle);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"No current event.");
	}
	JVR_EXIT; return ret;
#endif
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT; return -1;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtlen
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_sr_getevtlen__
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
#ifdef WIN32
	jvr_throw(JVR_THROW_JVR,"Not supported in win32 release");
	JVR_EXIT; return -1;
#endif
#ifdef LINUX
	jlong ret = sr_getevtlen();
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"No current event.");
	}
	JVR_EXIT; return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtdata
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_sr_getevtdata__J
  (JNIEnv *e, jclass c, jlong ehandle)
{
	JVR_ENTER;
#ifdef WIN32
	long type = sr_getevttype((unsigned long)ehandle);
	long len = sr_getevtlen((unsigned long)ehandle);
	void *datap = sr_getevtdatap((unsigned long)ehandle);
	// Conversion from the native data to a java object is
	// shared between the SRL and the GC libraries.
	jobject ret = jvr_convert_evtdatap_n2j(e,type,len,datap,NULL);
	JVR_EXIT;
	return ret;
#endif
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT; return NULL;
#endif
}

/*
 * Class:     net_threebit_jvr_sr
 * Method:    getevtdata
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_sr_getevtdata__
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
#ifdef WIN32
	jvr_throw(JVR_THROW_JVR,"Not supported in win32 release");
	JVR_EXIT; return NULL;
#endif
#ifdef LINUX
	long type = sr_getevttype();
	long len = sr_getevtlen();
	void *datap = sr_getevtdatap();
	// Conversion from the native data to a java object is
	// shared between the SRL and the GC libraries.
	jobject ret = jvr_convert_evtdatap_n2j(e,type,len,datap,NULL);
	JVR_EXIT;
	return ret;
#endif
}
