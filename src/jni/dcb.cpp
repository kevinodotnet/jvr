/*
 * $Id: dcb.cpp,v 1.1 2004/12/16 01:38:15 kevino Exp $
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
#include "net_threebit_jvr_dcb.h"

/*
 * Class:     net_threebit_jvr_dcb
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dcb_open
  (JNIEnv *e, jclass c, jstring name, jint flags)
{
	JVR_ENTER;
	char *namep = (char*) e->GetStringUTFChars(name,NULL);
	int ret = dcb_open(namep,flags);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dcb_open(\"%s\",%d) failed",namep,flags);
	}
	e->ReleaseStringUTFChars(name,namep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dcb
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dcb_close
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dcb_close(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dcb_close(%d) failed. errno = %d",dev,errno);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dcb
 * Method:    delconf
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dcb_delconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	int ret = dcb_delconf(dev,confID);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dcb_delconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dcb
 * Method:    monconf
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dcb_monconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	long ts;
	int ret = dcb_monconf(dev,confID,&ts);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dcb_monconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
		JVR_EXIT; return -1;
	}
	JVR_EXIT; return ts;
}

/*
 * Class:     net_threebit_jvr_dcb
 * Method:    unmonconf
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dcb_unmonconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	int ret = dcb_unmonconf(dev,confID);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dcb_unmonconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

