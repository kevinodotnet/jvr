/*
 * $Id: dx.cpp,v 1.50 2005/02/03 00:30:47 kevino Exp $
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
#include "net_threebit_jvr_dx.h"
#include "net_threebit_jvr_DV_0005fDIGIT.h"
#include "net_threebit_jvr_DX_0005fIOTT.h"
#include "net_threebit_jvr_DX_0005fXPB.h"

////////////////////////////////////////////////////////////////////////////////////////
// net_threebit_jvr_DX_0005fIOTT.h
////////////////////////////////////////////////////////////////////////////////////////

DX_IOTT jvr_iott_buff[JVR_IOTT_SIZE];
int jvr_iott_alloc[JVR_IOTT_SIZE];
int jvr_iott_ready = 0; // not ready by default

/*
 * Class:     net_threebit_jvr_DX_0005fIOTT
 * Method:    allocate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_DX_1IOTT_allocate
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	// Initialize the DX_IOTT handling; this happens only once per JVM
	if (!jvr_iott_ready) {
		for (int x = 0; x < JVR_IOTT_SIZE; x++) {
			jvr_iott_alloc[x] = 0;
			jvr_iott_buff[x].io_type = 0;
			jvr_iott_buff[x].rfu = 0;
			jvr_iott_buff[x].io_fhandle = 0;
			jvr_iott_buff[x].io_bufp = NULL;
			jvr_iott_buff[x].io_offset = 0;
			jvr_iott_buff[x].io_length = 0;
			jvr_iott_buff[x].io_nextp = NULL;
			jvr_iott_buff[x].io_prevp = NULL;
		}
		jvr_iott_ready = 1;
	}
	// Find the first available DX_IOTT structure
	int offset = -1;
	for (int x = 0; x < JVR_IOTT_SIZE; x++) {
		if (!jvr_iott_alloc[x]) {
			offset = x;
			break;
		}
	}
	if (offset == -1) {
		jvr_throw(JVR_THROW_JVR,"No native-context DX_IOTT structs are available for allocation (IOTT_SIZE:%d)",JVR_IOTT_SIZE);
		JVR_EXIT; return;
	}
	// Save the offset in the DX_IOTT object
	jclass clazz = dx_iott_class; // e->Find_Class("net/threebit/jvr/DX_IOTT");
	jfieldID field = e->GetFieldID(clazz,"offset","I");
	e->SetIntField(o,field,offset);
	jvr_iott_alloc[offset] = 1;
	JVR_EXIT; return;
}

/*
 * Class:     net_threebit_jvr_DX_0005fIOTT
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_DX_1IOTT_release
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	// Save the offset in the DX_IOTT object
	// jclass clazz = e->Find_Class("net/threebit/jvr/DX_IOTT");
	// jfieldID field = e->GetFieldID(clazz,"offset","I");
	int offset = e->GetIntField(o,dx_iott_offset);
	jvr_iott_alloc[offset] = 0;
	e->SetIntField(o,dx_iott_offset,-1);
	JVR_EXIT; return;
}

////////////////////////////////////////////////////////////////////////////////////////
// net_threebit_jvr_DV_0005fDIGIT.h
////////////////////////////////////////////////////////////////////////////////////////

// How many native-context DV_DIGIT buffers will be make available for allocation to
// net.threebit.jvr.DV_DIGIT instances?
#define DIGBUF_MAXSIZE 512

// An array of digit buffers that are candidates for allocation to DV_DIGIT instances.
DV_DIGIT jvr_digbuf[DIGBUF_MAXSIZE];

// A record of which jvr_digbuf entries have been allocated.
int jvr_digbuf_alloc[DIGBUF_MAXSIZE];

// Flag to control initialization of this sub-component
#define JVR_DIGBUF_INIT 0
#define JVR_DIGBUF_READY 1

/*
 * Class:     net_threebit_jvr_DV_0005fDIGIT
 * Method:    allocateBuffer
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_DV_1DIGIT_allocateBuffer
  (JNIEnv *e, jclass c)
{
	static int jvr_digbuf_state = JVR_DIGBUF_INIT;
	JVR_ENTER;
	// NOTE: allocateBuffer is only called from the DV_DIGIT constructor,
	// which is wrapped in a synchronized block, so no need to worry about
	// thread-safety hear.
	
	// Let the first invocation of DV_DIGIT.allocateBuffer() initialize the
	// digit buffer states.
	if (jvr_digbuf_state == JVR_DIGBUF_INIT) {
		for (int x = 0; x < DIGBUF_MAXSIZE; x++) {
			jvr_digbuf_alloc[x] = 0; // unallocated
		}
		jvr_digbuf_state = JVR_DIGBUF_READY;
	}

	// Find the first available buffer
	int offset = -1;
	for (int x = 0; x < DIGBUF_MAXSIZE; x++) {
		if (jvr_digbuf_alloc[x] == 0) {
			jvr_digbuf_alloc[x] = 1;
			offset = x;
			break;
		}
	}
	if (offset == -1) {
		jvr_throw(JVR_THROW_JVR,"No available native-context digit buffers");
		JVR_EXIT; return -1;
	}

	JVR_EXIT; return offset;
}

/*
 * Class:     net_threebit_jvr_DV_0005fDIGIT
 * Method:    releaseBuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_DV_1DIGIT_releaseBuffer
  (JNIEnv *e, jclass c, jint offset)
{
	JVR_ENTER;
	/*
	if (jvr_digbuf_state =! JVR_DIGBUF_READY) {
		jvr_throw(JVR_THROW_JVR,"jvr_digbuf_state (%d) is not in state JVR_DIGBUF_READY",jvr_digbuf_state);
		JVR_EXIT; return;
	}
	*/
	jvr_digbuf_alloc[offset] = 0;
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_DV_0005fDIGIT
 * Method:    getDigits
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_threebit_jvr_DV_1DIGIT_getDigits
  (JNIEnv *e, jclass c, jint offset)
{
	JVR_ENTER;
	/*
	if (jvr_digbuf_state =! JVR_DIGBUF_READY) {
		jvr_throw(JVR_THROW_JVR,"jvr_digbuf_state (%d) is not in state JVR_DIGBUF_READY",jvr_digbuf_state);
		JVR_EXIT; return NULL;
	}
	*/
	if (jvr_digbuf_alloc[offset] == 0) {
		jvr_throw(JVR_THROW_JVR,"Digit buffer offset %d is marked as 'unallocated'",offset);
		JVR_EXIT; return NULL;
	}
	char digits[DG_MAXDIGS+1];
	for (int x = 0; x <= DG_MAXDIGS; x++) {
		digits[x] = jvr_digbuf[offset].dg_value[x];
	}
	digits[DG_MAXDIGS] = 0; // guarantee null pointer termination of (char*)
	jstring ret = e->NewStringUTF(digits);
	JVR_EXIT;
	return ret;
}

////////////////////////////////////////////////////////////////////////////////////////
// net_threebit_jvr_DX_0005fXPB.h
////////////////////////////////////////////////////////////////////////////////////////

#define JVR_DXXPB_SIZE 200
DX_XPB jvr_dxxpb_buff[JVR_DXXPB_SIZE];
int jvr_dxxpb_alloc[JVR_DXXPB_SIZE];
int jvr_dxxpb_ready = 0; // not ready by default.

/*
 * Class:     net_threebit_jvr_DX_0005fXPB
 * Method:    allocate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_DX_1XPB_allocate
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	// Initialize if required
	if (!jvr_dxxpb_ready) {
		for (int x = 0; x < JVR_DXXPB_SIZE; x++) {
			jvr_dxxpb_alloc[x] = 0;
		}
		jvr_dxxpb_ready = 1;
	}
	// printf("ALLOCATE offset:%d (%s:%d)\n",e->GetIntField(o,dx_xpb_offset),__FILE__,__LINE__);
	// Find the first available DX_IOTT structure
	int offset = -1;
	for (int x = 0; x < JVR_DXXPB_SIZE; x++) {
		if (!jvr_dxxpb_alloc[x]) {
			offset = x;
			break;
		}
	}
	if (offset == -1) {
		jvr_throw(JVR_THROW_JVR,"No native-context DX_XPB structs are available for allocation (size:%d)",JVR_DXXPB_SIZE);
		JVR_EXIT; return;
	}
	// Save the offset in the object instance
	/*
	jclass clazz = e->Find_Class("net/threebit/jvr/DX_XPB");
	jfieldID field = e->GetFieldID(clazz,"offset","I");
	e->SetIntField(o,field,offset);
	*/
	// TODO: why does dx_xpb_offset field cause problems?
	e->SetIntField(o,dx_xpb_offset,offset);
	jvr_dxxpb_alloc[offset] = 1;
	// printf("ALLOCATE return (%d/%d) %s:%d\n",offset,e->GetIntField(o,dx_xpb_offset),__FILE__,__LINE__);
	JVR_EXIT; return;
}

/*
 * Class:     net_threebit_jvr_DX_0005fXPB
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_DX_1XPB_release
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	int offset = e->GetIntField(o,dx_xpb_offset);
	// printf("RELEASE offset:%d (%s:%d)\n",offset,__FILE__,__LINE__);
	jvr_dxxpb_alloc[offset] = 0;
	// printf("RELEASE o.offset:%d (%s:%d)\n",e->GetIntField(o,dx_xpb_offset),__FILE__,__LINE__);
	e->SetIntField(o,dx_xpb_offset,-1);
	// printf("RELEASE o.offset:%d (%s:%d)\n",e->GetIntField(o,dx_xpb_offset),__FILE__,__LINE__);
	JVR_EXIT; return;
}

////////////////////////////////////////////////////////////////////////////////////////
// net_threebit_jvr_dx.h
////////////////////////////////////////////////////////////////////////////////////////

/*
 * Class:     net_threebit_jvr_dx
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_open
  (JNIEnv *e, jclass c, jstring name, jint flags)
{
	JVR_ENTER;
	char *namep = (char*) e->GetStringUTFChars(name,NULL);
	int ret = dx_open(namep,flags);
	if (ret == -1) {
#ifdef WIN32
		jvr_throw(JVR_THROW_DG,"dx_open(\"%s\",%d) failed; dx_fileerrno(): %d",namep,flags,dx_fileerrno());
#endif
#ifdef LINUX
		jvr_throw(JVR_THROW_DG,"dx_open(\"%s\",%d) failed; errno: %d",namep,flags,errno);
#endif
	}
	e->ReleaseStringUTFChars(name,namep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_close
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dx_close(dev);
	if (ret == -1) {
#ifdef WIN32
		jvr_throw(JVR_THROW_DG,"dx_close(%d) failed; dx_fileerrno(): %d",dev,dx_fileerrno());
#endif
#ifdef LINUX
		jvr_throw(JVR_THROW_DG,"dx_close(%d) failed; errno: %d",dev,errno);
#endif
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    sethook
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_sethook
  (JNIEnv *e, jclass c, jint dev, jint hookstate, jint mode)
{
	JVR_ENTER;
	int ret = dx_sethook(dev,hookstate,mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_sethook(%d,%d,%d) failed: %s",dev,hookstate,mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    setevtmsk
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_setevtmsk
  (JNIEnv *e, jclass c, jint dev, jint mask)
{
	JVR_ENTER;
	int ret = dx_setevtmsk(dev,(unsigned int)mask);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_setevtmsk(%d,%d) failed: %s",dev,mask,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    listen
 * Signature: (ILnet/threebit/jvr/SC_TSINFO;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_listen
  (JNIEnv *e, jclass c, jint dev, jobject jtsinfo)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { JVR_EXIT; return -1; }
	int ret = dx_listen(dev, &tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_listen(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return -1;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    unlisten
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_unlisten
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dx_unlisten(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_unlisten(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    getxmitslot
 * Signature: (I)Lnet/threebit/jvr/SC_TSINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_dx_getxmitslot
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;

	int ret = dx_getxmitslot(dev,&tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_getxmitslot(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
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
 * Class:     net_threebit_jvr_dx
 * Method:    dial
 * Signature: (ILjava/lang/String;Lnet/threebit/jvr/DX_CAP;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_dial
  (JNIEnv *e, jclass c, jint dev, jstring jdialstr, jobject jdxcap, jint mode)
{
	JVR_ENTER;
	DX_CAP dxcap;
	DX_CAP *dxcapp = &dxcap;

	if (jdxcap == NULL) {
		dxcapp = NULL;
	}
	else {
		if (jvr_convert_dxcap_j2n(e,jdxcap,&dxcap) == -1) { JVR_EXIT; return -1; }
	}

	char *dialstr = (char *) e->GetStringUTFChars(jdialstr,NULL);
	int ret = dx_dial(dev, dialstr, dxcapp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_dial(%d,%s,%d) failed: %s",dev,dialstr,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	e->ReleaseStringUTFChars(jdialstr,dialstr);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    stopch
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_stopch
  (JNIEnv *e, jclass c, jint dev, jint mode)
{
	JVR_ENTER;
	int ret = dx_stopch(dev,(unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_stopch(%d,%d) failed: %s",dev,mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    clrdigbuf
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_clrdigbuf
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	int ret = dx_clrdigbuf(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_clrdigbuf(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    bldtngen
 * Signature: (IIIII)Lnet/threebit/jvr/TN_GEN;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_dx_bldtngen
  (JNIEnv *e, jclass c, jint freq1, jint freq2, jint ampl1, jint ampl2, jint dur)
{
	JVR_ENTER;
	TN_GEN tngen;
	dx_bldtngen( &tngen, (unsigned short) freq1, (unsigned short) freq2, (short) ampl1, (short) ampl2, (short) dur);  

	jclass clazz = tn_gen_class; // e->Find_Class("net/threebit/jvr/TN_GEN");
	jmethodID init = e->GetMethodID(clazz,"<init>","()V");
	jobject jtngen = e->NewObject(clazz, init);

	jvr_convert_tngen_n2j(e,jtngen,&tngen);
	JVR_EXIT; return jtngen;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    playtone
 * Signature: (ILnet/threebit/jvr/TN_GEN;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_playtone
  (JNIEnv *e, jclass c, jint dev, jobject jtngen, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	if (jtngen == NULL) { jvr_throw(JVR_THROW_JVR,"jtngen argument cannot be null"); JVR_EXIT; return -1; }
	if (jtpt == NULL) { jvr_throw(JVR_THROW_JVR,"jtpt argument cannot be null"); JVR_EXIT; return -1; }

	TN_GEN tngen;
	if (jvr_convert_tngen_j2n(e,jtngen,&tngen) == -1) { JVR_EXIT; return -1; }

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	if (jtpt != NULL) {
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}

	if (tpt == NULL) { jvr_throw(JVR_THROW_JVR,"tpt is null!"); JVR_EXIT; return -1; }

	int ret = dx_playtone(dev,&tngen,tpt,mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_playtone(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    playtoneEx
 * Signature: (ILnet/threebit/jvr/TN_GENCAD;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_playtoneEx
  (JNIEnv *e, jclass c, jint dev, jobject jtngencad, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	if (jtngencad == NULL) { jvr_throw(JVR_THROW_JVR,"jtngencad argument cannot be null"); JVR_EXIT; return -1; }
	if (jtpt == NULL) { jvr_throw(JVR_THROW_JVR,"jtpt argument cannot be null"); JVR_EXIT; return -1; }

	TN_GENCAD tngencad;
	if (jvr_convert_tngencad_j2n(e,jtngencad,&tngencad) == -1) { JVR_EXIT; return -1; }

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	if (jtpt != NULL) {
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}

	int ret = dx_playtoneEx(dev,&tngencad,tpt,mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"dx_playtoneEx(%d,%d) failed: %s",dev,mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_TERMMSK
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1TERMMSK
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_TERMMSK(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_TERMMSK(%d) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    getdig
 * Signature: (I[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DV_DIGIT;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_threebit_jvr_dx_getdig
  (JNIEnv *e, jclass c, jint dev, jobjectArray jtpt, jobject jdigit, jint mode)
{
	JVR_ENTER;
	if (jtpt == NULL) {
		jvr_throw(JVR_THROW_JVR,"DV_TPT[] cannot be null");
		JVR_EXIT; return NULL;
	}
	if (mode == EV_ASYNC && jdigit == NULL) {
		jvr_throw(JVR_THROW_JVR,"A DV_DIGIT object must be provided when EV_ASYNC mode is specified.");
		JVR_EXIT; return NULL;
	}

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	if (jtpt != NULL) {
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return NULL;
		}
	}

	DV_DIGIT *digitp;
	DV_DIGIT digitbuf; // for EV_SYNC mode when no java instance provided.
	if (jdigit == NULL) {
		// Place the digits into this functions private digit buffer
		digitp = &digitbuf;
	}
	else {
		// Find the digit buffer assigned to the instance object.
		jclass clazz = dv_digit_class; // e->Find_Class("net/threebit/jvr/DV_DIGIT");
		jfieldID field = e->GetFieldID(clazz,"offset","I");
		int offset = e->GetIntField(jdigit,field);
		digitp = &jvr_digbuf[offset];
	}
	int ret = dx_getdig(dev, tpt, digitp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_getdig(%d,%d) failed: %s",dev,(unsigned short)mode,ATDV_ERRMSGP(dev));
		JVR_EXIT; return NULL;
	}
	if (mode == EV_SYNC) {
		// Return the digits as a string.
		char digits[DG_MAXDIGS+1];
		for (int x = 0; x <= DG_MAXDIGS; x++) {
			digits[x] = digitp->dg_value[x];
		}
		digits[DG_MAXDIGS] = 0; // guarantee null pointer termination of (char*)
		jstring ret = e->NewStringUTF(digits);
		JVR_EXIT; return ret;
	}
	// EV_ASYNC mode just returns NULL
	JVR_EXIT; return NULL;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    playvox
 * Signature: (ILjava/lang/String;[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DX_XPB;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_playvox
  (JNIEnv *e, jclass c, jint dev, jstring filename, jobjectArray jtpt, jobject jxpb, jint mode)
{
	JVR_ENTER;
	if (filename == NULL) { jvr_throw(JVR_THROW_JVR,"filename argument cannot be null"); JVR_ENTER; return -1; }
	DX_XPB xpb;
	DX_XPB *xpbp = NULL;
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jxpb != NULL) {
		xpbp = &xpb;
		if (jvr_convert_dxxpb_j2n(e,jxpb,&xpb) == -1) {
			JVR_EXIT; return -1;
		}
	}
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}
	char *filenamep = (char*) e->GetStringUTFChars(filename,NULL);
	int ret = dx_playvox(dev, filenamep, tptp, xpbp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_playvox(%d,%s,%d) failed: %s",dev,filenamep,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	e->ReleaseStringUTFChars(filename,filenamep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    playwav
 * Signature: (ILjava/lang/String;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_playwav
  (JNIEnv *e, jclass c, jint dev, jstring filename, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	if (filename == NULL) { jvr_throw(JVR_THROW_JVR,"filename argument cannot be null"); JVR_EXIT; return -1; }
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}
	char *filenamep = (char*) e->GetStringUTFChars(filename,NULL);
	int ret = dx_playwav(dev, filenamep, tptp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_playwav(%d,%s,%d) failed: %s",dev,filenamep,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	e->ReleaseStringUTFChars(filename,filenamep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    recf
 * Signature: (ILjava/lang/String;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_recf
  (JNIEnv *e, jclass c, jint dev, jstring filename, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	if (filename == NULL) { jvr_throw(JVR_THROW_JVR,"filename argument cannot be null"); JVR_EXIT; return -1; }
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}
	char *filenamep = (char*) e->GetStringUTFChars(filename,NULL);
	int ret = dx_recf(dev, filenamep, tptp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_recf(%d,%s,%d) failed: %s",dev,filenamep,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	e->ReleaseStringUTFChars(filename,filenamep);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    rec
 * Signature: (I[Lnet/threebit/jvr/DX_IOTT;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_rec
  (JNIEnv *e, jclass c, jint dev, jobjectArray jiott, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	DX_IOTT *iottp = NULL;
	if (jiott == NULL) { jvr_throw(JVR_THROW_JVR,"jiott argument cannot be null"); JVR_EXIT; return -1; }
	if (jvr_convert_dxiott_j2n(e,jiott,&iottp) == -1) { JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp is still NULL!"); JVR_EXIT; return -1; }
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}
	int ret = dx_rec(dev, iottp, tptp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_rec(%d,%d) failed: %s",dev,(unsigned short)mode, ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    fileopen
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_fileopen
  (JNIEnv *e, jclass c, jstring filename, jint flags, jint pmode)
{
	JVR_ENTER;
#ifdef WIN32
	char *filenamep = (char*) e->GetStringUTFChars(filename,NULL);
	int ret = dx_fileopen(filenamep,flags,pmode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_fileopen(%s,%d,%d) failed: %d",filenamep,flags,pmode,dx_fileerrno());
	}
	e->ReleaseStringUTFChars(filename,filenamep);
	JVR_EXIT; return ret;
#endif
#ifdef LINUX
	char *filenamep = (char*) e->GetStringUTFChars(filename,NULL);
	int ret = open(filenamep,(int)flags,(mode_t)pmode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"open(%s,%d,%d) failed.  errno=%d",filenamep,flags,pmode,errno);
	}
	e->ReleaseStringUTFChars(filename,filenamep);
	JVR_EXIT; return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    fileclose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_fileclose
  (JNIEnv *e, jclass c, jint handle)
{
	JVR_ENTER;
#ifdef WIN32
	int ret = dx_fileclose(handle);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_fileclose(%d) failed: %d",handle,dx_fileerrno());
	}
#endif
#ifdef LINUX
	int ret = close(handle);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"close(%d) failed.",handle);
	}
	JVR_EXIT; return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    dx_fileerrno
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_fileerrno
  (JNIEnv *e, jclass c)
{
#ifdef WIN32
	return dx_fileerrno();
#endif
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	return -1;
#endif
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_CONNTYPE
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1CONNTYPE
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_CONNTYPE(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_CONNTYPE(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_LONGLOW
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1LONGLOW
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_LONGLOW(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_LONGLOW(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_SHORTLOW
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1SHORTLOW
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_SHORTLOW(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_SHORTLOW(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_SIZEHI
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1SIZEHI
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_SIZEHI(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_SIZEHI(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_TRCOUNT
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1TRCOUNT
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_TRCOUNT(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_TRCOUNT(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_CPTERM
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1CPTERM
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_CPTERM(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_CPTERM(%ld) failed",dev);
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    wtring
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_wtring
  (JNIEnv *e, jclass c, jint dev, jint rings, jint hookState, jint timeout)
{
	JVR_ENTER;
	int ret = dx_wtring(dev, rings, hookState, timeout);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_wtring(%d,%d,%d,%d) failed: %s",dev,rings,hookState,timeout,ATDV_ERRMSGP(dev));
	}
	return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    mreciottdata
 * Signature: (I[Lnet/threebit/jvr/DX_IOTT;[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DX_XPB;ILnet/threebit/jvr/SC_TSINFO;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_mreciottdata
  (JNIEnv *e, jclass c, jint dev, jobjectArray jiottArray, jobjectArray jtptArray, jobject jxpb, jint mode, jobject jtsinfo)
{
	JVR_ENTER;
	DX_IOTT *iottp = NULL;
	if (jiottArray == NULL) { jvr_throw(JVR_THROW_JVR,"jiottArray argument cannot be null"); JVR_EXIT; return -1; }
	if (jvr_convert_dxiott_j2n(e,jiottArray,&iottp) == -1) { JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp is still null; jvr_convert_dxiott_j2n() failed"); JVR_EXIT; return -1; }

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	if (jtptArray != NULL) {
		if (jvr_convert_dvtpt_j2n(e,jtptArray,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}

	DX_XPB *xpbp = NULL;
	DX_XPB xpb;
	if (jxpb != NULL) {
		if (jvr_convert_dxxpb_j2n(e,jxpb,&xpb) == -1) {
			JVR_EXIT; return -1;
		}
		xpbp = &xpb;
	}

	SC_TSINFO tsinfo;
	long ts[2];
	tsinfo.sc_numts = 2;
	tsinfo.sc_tsarrayp = (long int*) &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { JVR_EXIT; return -1; }

	int ret = dx_mreciottdata (dev, iottp, tpt, xpbp, (unsigned short) mode, &tsinfo);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_mreciottdata(dev:%d,mode:%d) failed: %s",dev,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    ATDX_HOOKST
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_dx_ATDX_1HOOKST
  (JNIEnv *e, jclass c, jint dev)
{
	JVR_ENTER;
	long ret = ATDX_HOOKST(dev);
	if (ret == AT_FAILURE) {
		jvr_throw(JVR_THROW_DG,"ATDX_HOOKST(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    recwav
 * Signature: (ILjava/lang/String;[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DX_XPB;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_recwav
  (JNIEnv *e, jclass c, jint dev, jstring jfilename, jobjectArray jtpt, jobject jxpb, jint mode)
{
	JVR_ENTER;
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT; return -1;
#endif
#ifdef WIN32
	if (jfilename == NULL) { jvr_throw(JVR_THROW_JVR,"jfilename argument cannot be null"); JVR_EXIT; return -1; }
	DX_XPB xpb;
	DX_XPB *xpbp = NULL;
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jxpb != NULL) {
		xpbp = &xpb;
		if (jvr_convert_dxxpb_j2n(e,jxpb,&xpb) == -1) { JVR_EXIT; return -1; }
	}
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) { JVR_EXIT; return -1; }
	}
	char *filename = (char*) e->GetStringUTFChars(jfilename,NULL);

	int ret = dx_recwav(dev,filename,tptp,xpbp,(unsigned short)mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_recwav(%d,%s,DV_TPT[],DX_XPB,%d) failed: %s",dev,filename,(unsigned short)mode,ATDV_ERRMSGP(dev));
		JVR_EXIT; return ret;
	}
	e->ReleaseStringUTFChars(jfilename,filename);
	JVR_EXIT; return ret;
#endif
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    wtcallid
 * Signature: (III)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_threebit_jvr_dx_wtcallid
  (JNIEnv *e, jclass c, jint dev, jint rings, jint timeout)
{
	JVR_ENTER;
	unsigned char buf[1024]; // 1k is overkill, but memory is cheap and it's on the stack.
	for (int x = 0; x < 1024; x++) { buf[x] = 0; } // initialize to all nulls.
	int ret = dx_wtcallid(dev, rings, (short) timeout, buf);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_wtcallid(%d,%d,%d) failed: %s",dev,rings,timeout,ATDV_ERRMSGP(dev));
		JVR_EXIT; return NULL;
	}
	JVR_EXIT; return e->NewStringUTF((const char*) buf);
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    getparm
 * Signature: (IJ)J
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_getparm
  (JNIEnv *e, jclass c, jint dev, jint parm)
{
	JVR_ENTER;
	int value;
	int ret = dx_getparm(dev,(unsigned long)parm,&value);
	if (ret == -1) {
		jvr_throw(JVR_THROW_JVR,"dx_getparm(%d,%ld) failed: %s",dev,parm,ATDV_ERRMSGP(dev));
		JVR_EXIT; return ret;
	}
	JVR_EXIT; return value;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    setparm
 * Signature: (IJJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_setparm
  (JNIEnv *e, jclass c, jint dev, jint parm, jint value)
{
	JVR_ENTER;
	short val = (short) value;
	void *valuep = (void*) &val;
	if (parm == DXCH_TXDATABUFSIZE || parm == DXCH_RXDATABUFSIZE) { valuep = (void*) &value; }
	int ret = dx_setparm(dev, (unsigned long)parm, valuep);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_setparm(%d,%ld,%ld) failed: %s",dev,parm,value,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    play
 * Signature: (I[Lnet/threebit/jvr/DX_IOTT;[Lnet/threebit/jvr/DV_TPT;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_play
  (JNIEnv *e, jclass c, jint dev, jobjectArray jiott, jobjectArray jtpt, jint mode)
{
	JVR_ENTER;
	DX_IOTT *iottp = NULL;
	if (jiott == NULL) { jvr_throw(JVR_THROW_JVR,"jiott argument cannot be null"); JVR_EXIT; return -1; }
	if (jvr_convert_dxiott_j2n(e,jiott,&iottp) == -1) { JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp is still NULL!"); JVR_EXIT; return -1; }
	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT *tptp = NULL;
	if (jtpt != NULL) {
		tptp = (DV_TPT*) &tpt;
		if (jvr_convert_dvtpt_j2n(e,jtpt,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}
	// printf("%s:%d dx_play mark\n",__FILE__,__LINE__); fflush(stdout);
	int ret = dx_play(dev, iottp, tptp, (unsigned short) mode);
	// printf("%s:%d dx_play mark\n",__FILE__,__LINE__); fflush(stdout);
	if (ret == -1) {
	// printf("%s:%d dx_play mark\n",__FILE__,__LINE__); fflush(stdout);
		jvr_throw(JVR_THROW_DG,"dx_play(%d,%d) failed: %s",dev,(unsigned short)mode, ATDV_ERRMSGP(dev));
	}
	// printf("%s:%d dx_play mark %d\n",__FILE__,__LINE__,ret); fflush(stdout);
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    reciottdata
 * Signature: (I[Lnet/threebit/jvr/DX_IOTT;[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DX_XPB;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_reciottdata
  (JNIEnv *e, jclass c, jint dev, jobjectArray jiottArray, jobjectArray jtptArray, jobject jxpb, jint mode)
{
	JVR_ENTER;
	DX_IOTT *iottp = NULL;
	if (jiottArray == NULL) { jvr_throw(JVR_THROW_JVR,"jiottArray argument cannot be null"); JVR_EXIT; return -1; }
	if (jvr_convert_dxiott_j2n(e,jiottArray,&iottp) == -1) { JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp is still null; jvr_convert_dxiott_j2n() failed"); JVR_EXIT; return -1; }

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	if (jtptArray != NULL) {
		if (jvr_convert_dvtpt_j2n(e,jtptArray,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}

	DX_XPB *xpbp = NULL;
	if (jxpb != NULL) {
		int offset = e->GetIntField(jxpb,dx_xpb_offset);
		xpbp = &jvr_dxxpb_buff[offset];
		if (jvr_convert_dxxpb_j2n(e,jxpb,xpbp) == -1) {
			JVR_EXIT; return -1;
		}
	}
	// printf("%s:%d\n",__FILE__,__LINE__);

	int ret = dx_reciottdata (dev, iottp, tpt, xpbp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_reciottdata(dev:%d,mode:%d) failed: %s",dev,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    playiottdata
 * Signature: (I[Lnet/threebit/jvr/DX_IOTT;[Lnet/threebit/jvr/DV_TPT;Lnet/threebit/jvr/DX_XPB;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_playiottdata
  (JNIEnv *e, jclass c, jint dev, jobjectArray jiottArray, jobjectArray jtptArray, jobject jxpb, jint mode)
{
	JVR_ENTER;
	DX_IOTT *iottp = NULL;
	if (jiottArray == NULL) { jvr_throw(JVR_THROW_JVR,"jiottArray argument cannot be null"); JVR_EXIT; return -1; }
	if (jvr_convert_dxiott_j2n(e,jiottArray,&iottp) == -1) { JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp is still null; jvr_convert_dxiott_j2n() failed"); JVR_EXIT; return -1; }

	DV_TPT tpt[JVR_DVTPT_MAXSIZE];
	DV_TPT* tptp = NULL;
	if (jtptArray != NULL) {
		tptp = &tpt[0];
		if (jvr_convert_dvtpt_j2n(e,jtptArray,tpt) == -1) {
			JVR_EXIT; return -1;
		}
	}

	DX_XPB *xpbp = NULL;
	if (jxpb != NULL) {
		int offset = e->GetIntField(jxpb,dx_xpb_offset);
		xpbp = &jvr_dxxpb_buff[offset];
		if (jvr_convert_dxxpb_j2n(e,jxpb,xpbp) == -1) {
			JVR_EXIT; return -1;
		}
	}

	/*
	printf("MANUAL!!!\n");
	printf("xpb->wFileFormat: %hu\n", xpbp->wFileFormat);
	printf("xpb->nSamplesPerSec: %hu\n", xpbp->nSamplesPerSec);
	printf("xpb->wBitsPerSample: %lu\n", xpbp->wBitsPerSample);
	printf("xpb->wDataFormat: %lu\n", xpbp->wDataFormat);
	xpbp->wFileFormat = 2;
	xpbp->nSamplesPerSec = 88;
	xpbp->wBitsPerSample = 8;
	xpbp->wDataFormat = 8;
	printf("   xpb->wFileFormat: %hu\n", xpbp->wFileFormat);
	printf("   xpb->nSamplesPerSec: %hu\n", xpbp->nSamplesPerSec);
	printf("   xpb->wBitsPerSample: %lu\n", xpbp->wBitsPerSample);
	printf("   xpb->wDataFormat: %lu\n", xpbp->wDataFormat);
	*/

	int ret = dx_playiottdata (dev, iottp, tptp, xpbp, (unsigned short) mode);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_playiottdata(dev:%d,mode:%d) failed: %s",dev,(unsigned short)mode,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT; return ret;
}

/**
 * Used for user supplied input/output functions.  See initDxClass();
 */
int dx_uio_read (int fd, char* ptr, unsigned cnt) {
	JavaVM *vm = jvr_getVM();
	JNIEnv *e;
	int detach = 0;
	if (vm->GetEnv((void **)&e, JNI_VERSION_1_2) != JNI_OK) {
		vm->AttachCurrentThread((void**)&e,&attachArgs);
		detach = 1;
	}
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		return -1;
	}

  jcharArray charArray = (jcharArray) e->CallStaticObjectMethod(dx_class, dx_uio_read_method, (jint) fd, (jint) cnt);
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		if (detach) { vm->DetachCurrentThread(); }
		return -1;
	}
	if (charArray == NULL) {
		// EOF
		if (detach) { vm->DetachCurrentThread(); }
		return -1;
	}

	int buflen = e->GetArrayLength(charArray);
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		if (detach) { vm->DetachCurrentThread(); }
		return -1;
	}
	if (buflen == 0) {
		// EOF aka no data
		if (detach) { vm->DetachCurrentThread(); }
		return -1;
	}

	jchar* bufp = (jchar*) e->GetCharArrayElements(charArray,0);
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		if (detach) { vm->DetachCurrentThread(); }
		return -1;
	}

	memcpy(ptr,bufp,buflen);
	e->ReleaseCharArrayElements(charArray, bufp, JNI_ABORT);
	e->DeleteLocalRef(charArray);
	if (detach) { vm->DetachCurrentThread(); }
	return buflen;
}

/**
 * Used for user supplied input/output functions.  See initDxClass();
 */
int dx_uio_write (int fd, char* ptr, unsigned cnt) {

	JavaVM *vm = jvr_getVM();
	JNIEnv *e;
	int detach = 0;
	if (vm->GetEnv((void **)&e, JNI_VERSION_1_2) != JNI_OK) {
		vm->AttachCurrentThread((void**)&e,&attachArgs);
		detach = 1;
	}
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		if (detach) { vm->DetachCurrentThread(); }
		return 0;
	}

	jbyteArray byteArray = e->NewByteArray(cnt);
	jbyte *bufp = (jbyte*) e->GetByteArrayElements(byteArray,0);
	memcpy(bufp,ptr,cnt);
	e->ReleaseByteArrayElements(byteArray,bufp,0);

	int len = e->CallStaticIntMethod(dx_class, dx_uio_write_method, (jint) fd, byteArray);
	e->DeleteLocalRef(byteArray);
	if (detach) { vm->DetachCurrentThread(); }
	return len;
}

/**
 * Used for user supplied input/output functions.  See initDxClass();
 */
long dx_uio_seek (int fd, long offset, int whence) {

	JavaVM *vm = jvr_getVM();
	JNIEnv *e;
	int detach = 0;
	if (vm->GetEnv((void **)&e, JNI_VERSION_1_2) != JNI_OK) {
		vm->AttachCurrentThread((void**)&e,&attachArgs);
		detach = 1;
	}
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		return -1;
	}
	long ret = e->CallStaticLongMethod(dx_class,dx_uio_seek_method,(jint) fd,(jlong) offset,(jint) whence);
	if (detach) { vm->DetachCurrentThread(); }
	return ret;

}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    initDxClass
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_dx_initDxClass
  (JNIEnv *e, jclass c) {

	DX_UIO uio;
	uio.u_read = dx_uio_read;
	uio.u_write = dx_uio_write;
	uio.u_seek = dx_uio_seek;
	if (dx_setuio(uio) == -1) {
		jvr_throw(JVR_THROW_DG,"dx_setuio() failed.");
		return;
	}

	return;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    deltones
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_deltones
  (JNIEnv *e, jclass c, jint dev) {
	JVR_ENTER;
	int ret = dx_deltones(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_deltones(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    chgdur
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_chgdur
  (JNIEnv *e, jclass c, jint tonetype, jint ontime, jint ondev, jint offtime, jint offdev) {
	JVR_ENTER;
	int ret = dx_chgdur(tonetype, ontime, ondev, offtime, offdev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_chgdur(%d,%d,%d,%d,%d) failed.",tonetype,ontime,ondev,offtime,offdev);
	}
	if (ret == -2) {
		jvr_throw(JVR_THROW_DG,"dx_chgdur(%d,%d,%d,%d,%d) failed: Unknown tone type",tonetype,ontime,ondev,offtime,offdev);
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    chgfreq
 * Signature: (IIIII)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_chgfreq
  (JNIEnv *e, jclass c, jint tonetype, jint freq1, jint freq1dev, jint freq2, jint freq2dev) {
	JVR_ENTER;
	int ret = dx_chgfreq(tonetype, freq1, freq1dev, freq2, freq2dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_chgfreq(%d,%d,%d,%d) failed.",tonetype,freq1,freq1dev,freq2,freq2dev);
	}
	if (ret == -2) {
		jvr_throw(JVR_THROW_DG,"dx_chgfreq(%d,%d,%d,%d) failed: Unknown tone type",tonetype,freq1,freq1dev,freq2,freq2dev);
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    chgrepcnt
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_chgrepcnt
  (JNIEnv *e, jclass c, jint tonetype, jint repcnt) {
	JVR_ENTER;
	int ret = dx_chgrepcnt(tonetype, repcnt);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_chgrepcnt(%d,%d) failed.",tonetype,repcnt);
	}
	if (ret == -2) {
		jvr_throw(JVR_THROW_DG,"dx_chgrepcnt(%d,%d) failed: Unknown tone type",tonetype,repcnt);
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_dx
 * Method:    initcallp
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_dx_initcallp
  (JNIEnv *e, jclass c, jint dev) {
	JVR_ENTER;
	int ret = dx_initcallp(dev);
	if (ret == -1) {
		jvr_throw(JVR_THROW_DG,"dx_initcallp(%d) failed: %s",dev,ATDV_ERRMSGP(dev));
	}
	JVR_EXIT;
	return ret;
}
