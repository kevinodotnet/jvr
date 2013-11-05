/*
 * $Id: ms.cpp,v 1.16 2005/02/03 00:30:48 kevino Exp $
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
#include "net_threebit_jvr_ms.h"

/*
 * Class:     net_threebit_jvr_ms
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_open
  (JNIEnv *e, jclass c, jstring name, jint flags)
{
	JVR_ENTER;
	char *namep = (char*) e->GetStringUTFChars(name,NULL);
	int ret = ms_open(namep,flags);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_open(\"%s\",%d) failed",namep,flags);
	}
	e->ReleaseStringUTFChars(name,namep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_close
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = ms_close(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_close(%d) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    setevtmsk
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_setevtmsk
  (JNIEnv *e, jclass c, jint dev, jint event, jint mask, jint action)
{
	JVR_ENTER;
	int ret = ms_setevtmsk(dev,event,(unsigned short)mask,action);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_setevtmsk(%d,%d,%d,%d) failed: %s",dev,event,mask,action,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    listen
 * Signature: (ILnet/threebit/jvr/SC_TSINFO;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_listen
  (JNIEnv *e, jclass c, jint dev, jobject jtsinfo)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { JVR_EXIT; return -1; }
	int ret = ms_listen(dev, &tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_listen(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return -1;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    unlisten
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_unlisten
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = ms_unlisten(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_unlisten(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    getxmitslot
 * Signature: (I)Lnet/threebit/jvr/SC_TSINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_ms_getxmitslot
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;

	int ret = ms_getxmitslot(dev,&tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_getxmitslot(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
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
 * Class:     net_threebit_jvr_ms
 * Method:    estconf
 * Signature: (I[Lnet/threebit/jvr/MS_CDT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_estconf
  (JNIEnv *e, jclass c, jint dev, jobjectArray jmscdt, jint attr)
{
	JVR_ENTER;
	MS_CDT cdt[JVR_MSCDT_MAXSIZE];
	int numParties;
	int confID;
	if (jvr_convert_mscdtarray_j2n(e,jmscdt,(MS_CDT*)&cdt,&numParties) == -1) { JVR_EXIT; return -1; }
	int ret = ms_estconf(dev, cdt, numParties, attr, &confID);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_estconf(%d,%d,%d) failed: %s",dev,numParties,attr,ATDV_ERRMSGP(dev));
		JVR_EXIT; return ret;
	}
	if (jvr_convert_mscdtarray_n2j(e,jmscdt,(MS_CDT*)&cdt,numParties) == -1) { 
		// TODO: should we delete the conference?  Maybe we should since the
		// caller will have no wait of doing it for themselves later on (since
		// the thrown exception blocks their receipt of the conference ID.
		JVR_EXIT; return -1;
	}
	JVR_EXIT; return confID;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    delconf
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_delconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	int ret = ms_delconf(dev,confID);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_delconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    monconf
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_ms_monconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	long ts;
	int ret = ms_monconf(dev,confID,&ts);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_monconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
		JVR_EXIT; return -1;
	}
	JVR_EXIT; return ts;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    unmonconf
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_unmonconf
  (JNIEnv *e, jclass c, jint dev, jint confID)
{
	JVR_ENTER;
	int ret = ms_unmonconf(dev,confID);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_unmonconf(%d,%d) failed: %s",dev,confID,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    genring
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_genring
  (JNIEnv *e, jclass c, jint dev, jint len, jint mode)
{
	JVR_ENTER;
	int ret = ms_genring(dev,(unsigned short)len,(unsigned short)mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"ms_genring(%d,%hd,%hd) failed: %s",dev,(unsigned short)len,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    stopfn
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_stopfn
  (JNIEnv *e, jclass c, jint dev, jint func)
{
	JVR_ENTER;
	int ret = ms_stopfn((unsigned int)dev,(unsigned int)func);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_stopfn(%d,%d) failed: %s",(unsigned int) dev,(unsigned int) func,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    addtoconf
 * Signature: (IILnet/threebit/jvr/MS_CDT;)I
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_ms_addtoconf
  (JNIEnv *e, jclass c, jint dev, jint conf, jobject jmscdt)
{
	JVR_ENTER;
	MS_CDT cdt;
	if (jvr_convert_mscdt_j2n(e,jmscdt,(MS_CDT*)&cdt) == -1) { JVR_EXIT; return; }
	int ret = ms_addtoconf(dev,conf,&cdt);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_addtoconf(%d,%d) failed: %s",(unsigned int) dev,(unsigned int) conf,ATDV_ERRMSGP(dev));
	}
	if (jvr_convert_mscdt_n2j(e,jmscdt,(MS_CDT*)&cdt) == -1) {
		// TODO: the party has been added but we had some problem
		// converting the structure back.  Nothing we can do really.
		JVR_EXIT;
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    remfromconf
 * Signature: (IILnet/threebit/jvr/MS_CDT;)I
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_ms_remfromconf
  (JNIEnv *e, jclass c, jint dev, jint conf, jobject jmscdt)
{
	JVR_ENTER;
	MS_CDT cdt;
	if (jvr_convert_mscdt_j2n(e,jmscdt,(MS_CDT*)&cdt) == -1) { JVR_EXIT; return; }
	int ret = ms_remfromconf(dev,conf,&cdt);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_addtoconf(%d,%d) failed: %s",(unsigned int) dev,(unsigned int) conf,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    ATMS_TSSGBIT
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_ms_ATMS_1TSSGBIT
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long tsbits = ATMS_TSSGBIT(dev);
	switch (tsbits) {
		case MS_ONHOOK:
			break;
		case MS_OFFHOOK:
			break;
		default:
			jvr_throw(JVR_THROW_DG,"ATMS_TSSGBIT(%d) failed (%ld,%s)",(unsigned int) dev,tsbits,ATDV_ERRMSGP(dev));
			return -1;
	}
	return tsbits;
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    dsprescount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_ms_dsprescount
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int value;
	if (ms_dsprescount(dev, &value) == -1) {
		jvr_throw(JVR_THROW_DG,"ms_dsprescount(%d) failed: %s",(unsigned int) dev,ATDV_ERRMSGP(dev));
	}
	return value;
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    setbrdparm
 * Signature: (IJLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_ms_setbrdparm
  (JNIEnv *e, jclass c, jint dev, jint paramint, jobject arg)
{
	JVR_ENTER;
	long param = (long) paramint;
	void *valuep = NULL;
	int intValue;

	// Given that the "valuep" needs special handling for this method I haven't bothered implementing
	// all of the board parameters yet.  Just the ones I need for my applications have been done so far.
	// Speak up on the JVR list if you need one of these implemented.
	if (param == MSCB_ND) { jvr_throw(JVR_THROW_JVR,"Setting MSCB_ND is not supported by JVR yet."); return; }
	if (param == MSCB_ZIP) { jvr_throw(JVR_THROW_JVR,"Setting MSCB_ZIP is not supported by JVR yet."); return; }
	if (param == MSG_DBOFFTM) { jvr_throw(JVR_THROW_JVR,"Setting MSCBDBOFFTM is not supported by JVR yet."); return; }
	if (param == MSG_DBONTM) { jvr_throw(JVR_THROW_JVR,"Setting MSCBDBONTM is not supported by JVR yet."); return; }
	if (param == MSG_DISTINCTRNG) { jvr_throw(JVR_THROW_JVR,"Setting MSCBDISTINCTRNG is not supported by JVR yet."); return; }
	if (param == MSG_MAXFLASH) { jvr_throw(JVR_THROW_JVR,"Setting MSG_MAXFLASH is not supported by JVR yet."); return; }
	if (param == MSG_MINFLASH) { jvr_throw(JVR_THROW_JVR,"Setting MSG_MINFLASH is not supported by JVR yet."); return; }
	if (param == MSG_PDRNGCAD) { jvr_throw(JVR_THROW_JVR,"Setting MSG_PDRNGCAD is not supported by JVR yet."); return; }
	if (param == MSG_RING) { jvr_throw(JVR_THROW_JVR,"Setting MSG_RING is not supported by JVR yet."); return; }
	if (param == MSG_RNGCAD) { jvr_throw(JVR_THROW_JVR,"Setting MSG_RNGCAD is not supported by JVR yet."); return; }
	if (param == MSG_UDRNGCAD) { jvr_throw(JVR_THROW_JVR,"Setting MSG_UDRNGCAD is not supported by JVR yet."); return; }

	if (param == MSG_ZIPENA) {
		// arg is a java.lang.Integer object that contains the ZIP tone enabled/disabled setting.  Convert to int.
		intValue = jvr_convert_intValue(e,arg);
		valuep = &intValue;
	}

	if (ms_setbrdparm(dev,param,valuep) == -1) {
		jvr_throw(JVR_THROW_DG,"ms_setbrdparm(%d,%d) failed: %s",(unsigned int) dev,paramint,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_ms
 * Method:    setcde
 * Signature: (IILnet/threebit/jvr/MS_CDT;)V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_ms_setcde
  (JNIEnv *e, jclass c, jint dev, jint conf, jobject jmscdt)
{
	JVR_ENTER;
	MS_CDT cdt;
	if (jvr_convert_mscdt_j2n(e,jmscdt,(MS_CDT*)&cdt) == -1) { JVR_EXIT; return; }
	int ret = ms_setcde(dev,conf,&cdt);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"ms_setcde(%d,%d) failed: %s",(unsigned int) dev,(unsigned int) conf,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT;
}
