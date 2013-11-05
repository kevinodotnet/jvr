/*
 * $Id: jvr.cpp,v 1.64 2005/02/03 00:30:48 kevino Exp $
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
#include "net_threebit_jvr_jvr.h"

// Reference to the Virtual Machine that is set at the same time as symbol importation.
static JavaVM *vm;

//static int jvrDebugLevel = JVR_ALL;
static int jvrDebugLevel = JVR_NONE;

//////////////////////////////////////////////////////////////////////
// jvr.h Implementations
//////////////////////////////////////////////////////////////////////

/**
 * Throw an exception using the specified class.
 */
void jvr_throw (JNIEnv *e,const char* exceptionClass, const char* file, int line, const char* format, ...) {

	int bufSize = 10*1024;
	size_t maxSize = (10*1024)-2;
	char message[bufSize];
	va_list vlist = NULL;
	int length = 0;

	// Prepare the exception message.
	va_start(vlist,format);
	length += vsnprintf(&message[0]+length,maxSize-length,format,vlist);
	length += snprintf(&message[0]+length,maxSize-length," (%s:%d)",file,line);

	// Throw the exception.
	jclass exception = e->FindClass(exceptionClass);
	if (exception == NULL) {
		e->ThrowNew(e->FindClass("java.lang.Error"), "jvr_throw error: failed to find exception class.");
		return;
	}
	e->ThrowNew(exception, message);
}

/**
 *
 */
void jvr_log (JNIEnv *e, const char* file, int line, const char* format, ...) {
	int bufSize = 10*1024;
	size_t maxSize = (10*1024)-2;
	char message[bufSize];
	va_list vlist = NULL;
	int length = 0;

	// Prepare the exception message.
	va_start(vlist,format);
	length += vsnprintf(&message[0]+length,maxSize-length,format,vlist);
	length += snprintf(&message[0]+length,maxSize-length," (%s:%d)",file,line);

	printf(message);
	printf("\n");
	fflush(stdout);
}

#ifdef WIN32
/**
 * The SRL event handler (win32)
 */
long int jvr_eventHandler (unsigned long ehandle)  
{  
	JNIEnv *e;
	JVR_ENTER;
	vm->AttachCurrentThread((void**)&e,&attachArgs);
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		return -1;
	}
  e->CallStaticVoidMethod(jvr_class, jvr_handleEvent_win32, (jlong) ehandle);
	if (e->ExceptionOccurred()) {
		e->ExceptionDescribe();
		e->ExceptionClear();
		return -1;
	}
	vm->DetachCurrentThread();
	JVR_EXIT;
	return(1);
}
#endif

#ifdef LINUX
/**
 * The SRL event handler (linux)
 */
long jvr_eventHandler (void *p)
{  
	JNIEnv *e;
	vm->AttachCurrentThread((void**)&e,&attachArgs);
	jmethodID handleEventMethod = e->GetStaticMethodID(jvr_class, "handleEvent", "()V");
  e->CallStaticVoidMethod(jvrClass, handleEventMethod);
	vm->DetachCurrentThread();
	return(1);
}
#endif

/**
 *
 */
int jvr_convert_sctsinfo_j2n(JNIEnv *e, jobject jtsinfo, SC_TSINFO *tsinfo) {
	JVR_ENTER;

	// Can't convert to/from NULLs.
	if (jtsinfo == NULL) { jvr_throw(JVR_THROW_JVR,"jtsinfo argument cannot be null"); JVR_EXIT; return -1; }
	if (tsinfo == NULL) { jvr_throw(JVR_THROW_JVR,"tsinfo argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = sc_tsinfo_class; // e->Find_Class("net/threebit/jvr/SC_TSINFO");
	jfieldID field;

	// The array length in the provided SC_TSINFO struct.  Since the caller must
	// provide the long[] pointer, we must guarantee that there is enough space
	// considering that the actual java long[] may be too big.  If the java
	// version is smaller, then that's OK, we just don't want to buffer overflow.
	field = e->GetFieldID(clazz,"sc_numts","J");
	if (e->GetLongField(jtsinfo,field) > tsinfo->sc_numts) {
		jvr_throw(JVR_THROW_JVR,"Cannot store %d timeslots.  Only have %d slots available",e->GetLongField(jtsinfo,field),tsinfo->sc_numts);
		JVR_EXIT; return -1;
	}
	tsinfo->sc_numts = e->GetLongField(jtsinfo,field);

	field = e->GetFieldID(clazz,"sc_tsarray","[J");
	jlongArray longArray = (jlongArray) e->GetObjectField(jtsinfo,field);
	jlong *longBuf = e->GetLongArrayElements(longArray,NULL);
	for (int x = 0; x < tsinfo->sc_numts; x++) {
		tsinfo->sc_tsarrayp[0] = longBuf[x];
	}
	e->ReleaseLongArrayElements(longArray,longBuf,0);

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_sctsinfo_n2j (JNIEnv *e, jobject jtsinfo, SC_TSINFO *tsinfo) {
	JVR_ENTER;

	if (tsinfo == NULL) {
		jvr_throw(JVR_THROW_JVR,"tsinfo argument cannot be null"); 
		JVR_EXIT; return -1; 
	}
	if (jtsinfo == NULL) {
		jvr_throw(JVR_THROW_JVR,"jtsinfo argument cannot be null"); 
		JVR_EXIT; return -1; 
	}

	jclass clazz = sc_tsinfo_class; // e->Find_Class("net/threebit/jvr/SC_TSINFO");
	jmethodID init = e->GetMethodID(clazz, "<init>", "()V");
	jfieldID field;

	// Set int field
	field = e->GetFieldID(clazz,"sc_numts","J");
	e->SetLongField(jtsinfo, field, tsinfo->sc_numts);

	// Create new long[] and copy over the values
	jlongArray longArray = e->NewLongArray(tsinfo->sc_numts);
	jlong *longBuf = e->GetLongArrayElements(longArray,NULL);
	for (int x = 0; x < tsinfo->sc_numts; x++) {
		longBuf[x] = tsinfo->sc_tsarrayp[x];
	}
	e->ReleaseLongArrayElements(longArray,longBuf,0);

	// Set array field
	field = e->GetFieldID(clazz,"sc_tsarray","[J");
	e->SetObjectField(jtsinfo,field,longArray);

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_dxcap_j2n (JNIEnv *e, jobject jdxcap, DX_CAP *dxcap) {
	JVR_ENTER;

	if (jdxcap == NULL) { jvr_throw(JVR_THROW_JVR,"jdxcap argument cannot be null"); JVR_EXIT; return -1; }
	if (dxcap == NULL) { jvr_throw(JVR_THROW_JVR,"dxcap argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = dx_cap_class; // e->Find_Class("net/threebit/jvr/DX_CAP");
	jfieldID field = NULL;

	// byte fields
	field = e->GetFieldID(clazz,"ca_pamd_qtemp","B");
	dxcap->ca_pamd_qtemp = e->GetByteField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_pamd_spdval","B");
	dxcap->ca_pamd_spdval = e->GetByteField(jdxcap,field);

	// int fields
	field = e->GetFieldID(clazz,"ca_alowmax","I");
	dxcap->ca_alowmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_ansrdgl","I");
	dxcap->ca_ansrdgl = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_blowmax","I");
	dxcap->ca_blowmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_cnosig","I");
	dxcap->ca_cnosig = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_cnosil","I");
	dxcap->ca_cnosil = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_dtn_deboff","I");
	dxcap->ca_dtn_deboff = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_dtn_npres","I");
	dxcap->ca_dtn_npres = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_dtn_pres","I");
	dxcap->ca_dtn_pres = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hedge","I");
	dxcap->ca_hedge = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hi1bmax","I");
	dxcap->ca_hi1bmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hi1ceil","I");
	dxcap->ca_hi1ceil = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hi1tola","I");
	dxcap->ca_hi1tola = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hi1tolb","I");
	dxcap->ca_hi1tolb = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_higltch","I");
	dxcap->ca_higltch = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_hisiz","I");
	dxcap->ca_hisiz = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_intflg","I");
	dxcap->ca_intflg = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_intfltr","I");
	dxcap->ca_intfltr = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lcdly","I");
	dxcap->ca_lcdly = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lcdly1","I");
	dxcap->ca_lcdly1 = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo1bmax","I");
	dxcap->ca_lo1bmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo1ceil","I");
	dxcap->ca_lo1ceil = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo1rmax","I");
	dxcap->ca_lo1rmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo1tola","I");
	dxcap->ca_lo1tola = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo1tolb","I");
	dxcap->ca_lo1tolb = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo2bmax","I");
	dxcap->ca_lo2bmax = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo2rmin","I");
	dxcap->ca_lo2rmin = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo2tola","I");
	dxcap->ca_lo2tola = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lo2tolb","I");
	dxcap->ca_lo2tolb = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_logltch","I");
	dxcap->ca_logltch = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lower2frq","I");
	dxcap->ca_lower2frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lower3frq","I");
	dxcap->ca_lower3frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_lowerfrq","I");
	dxcap->ca_lowerfrq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_maxansr","I");
	dxcap->ca_maxansr = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_maxintering","I");
	dxcap->ca_maxintering = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_mxtime2frq","I");
	dxcap->ca_mxtime2frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_mxtime3frq","I");
	dxcap->ca_mxtime3frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_mxtimefrq","I");
	dxcap->ca_mxtimefrq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_nbrbeg","I");
	dxcap->ca_nbrbeg = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_nbrdna","I");
	dxcap->ca_nbrdna = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_noanswer","I");
	dxcap->ca_noanswer = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_nsbusy","I");
	dxcap->ca_nsbusy = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_pamd_failtime","I");
	dxcap->ca_pamd_failtime = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_pamd_minring","I");
	dxcap->ca_pamd_minring = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_rejctfrq","I");
	dxcap->ca_rejctfrq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_stdely","I");
	dxcap->ca_stdely = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_time2frq","I");
	dxcap->ca_time2frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_time3frq","I");
	dxcap->ca_time3frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_timefrq","I");
	dxcap->ca_timefrq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_upper2frq","I");
	dxcap->ca_upper2frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_upper3frq","I");
	dxcap->ca_upper3frq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"ca_upperfrq","I");
	dxcap->ca_upperfrq = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"rfu1","I");
	dxcap->rfu1 = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"rfu2","I");
	dxcap->rfu2 = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"rfu3","I");
	dxcap->rfu3 = e->GetIntField(jdxcap,field);
	field = e->GetFieldID(clazz,"rfu4","I");
	dxcap->rfu4 = e->GetIntField(jdxcap,field);

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_tngen_n2j (JNIEnv *e, jobject jtngen, TN_GEN *tngen) {
	JVR_ENTER;

	if (jtngen == NULL) { jvr_throw(JVR_THROW_JVR,"jtngen argument cannot be null"); JVR_EXIT; return -1; }
	if (tngen == NULL) { jvr_throw(JVR_THROW_JVR,"tngen argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = tn_gen_class; // e->Find_Class("net/threebit/jvr/TN_GEN");
	jfieldID field = NULL;

	field = e->GetFieldID(clazz,"tg_dflag","I");
	e->SetIntField(jtngen,field,tngen->tg_dflag);
	field = e->GetFieldID(clazz,"tg_freq1","I");
	e->SetIntField(jtngen,field,tngen->tg_freq1);
	field = e->GetFieldID(clazz,"tg_freq2","I");
	e->SetIntField(jtngen,field,tngen->tg_freq2);
	field = e->GetFieldID(clazz,"tg_ampl1","I");
	e->SetIntField(jtngen,field,tngen->tg_ampl1);
	field = e->GetFieldID(clazz,"tg_ampl2","I");
	e->SetIntField(jtngen,field,tngen->tg_ampl2);
	field = e->GetFieldID(clazz,"tg_dur","I");
	e->SetIntField(jtngen,field,tngen->tg_dur);

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_dvtpt_j2n (JNIEnv *e, jobjectArray jtptArray, DV_TPT *tpt) {
	JVR_ENTER;
	if (jtptArray == NULL) { jvr_throw(JVR_THROW_JVR,"jtptArray argument cannot be null"); JVR_EXIT; return -1; }
	if (e->GetArrayLength(jtptArray) == 0) { jvr_throw(JVR_THROW_JVR,"jtptArray cannot have size 0"); JVR_EXIT; return -1; }
	if (tpt == NULL) { jvr_throw(JVR_THROW_JVR,"tpt argument cannot be null"); JVR_EXIT; return -1; }

	jsize arrayLength = e->GetArrayLength(jtptArray);
	if (arrayLength > JVR_DVTPT_MAXSIZE) {
		jvr_throw(JVR_THROW_JVR,"Length of jtptArray (%d) cannot exceed JVR_DVTPT_MAXSIZE (%d)",arrayLength,JVR_DVTPT_MAXSIZE);
		JVR_EXIT; return -1;
	}
	//
	// Copy each DV_TPT element
	//
	jclass clazz = dv_tpt_class; // e->Find_Class("net/threebit/jvr/DV_TPT");
	jfieldID field = NULL;
	DV_TPT *current;
	/*
	printf("-----[ DV_TPT convert ]-----\n");
	printf("Number of DV_TPT elements: %d\n",arrayLength);
	*/
	for (int x = 0; x < arrayLength; x++) {
		jobject jtpt = e->GetObjectArrayElement(jtptArray,x);
		if (jtpt == NULL) {
			jvr_throw(JVR_THROW_JVR,"NULL element at offset %d in jtptArray",x);
			JVR_EXIT; return -1;
		}
		current = tpt+x;
		field = e->GetFieldID(clazz,"tp_type","I");
		current->tp_type = (unsigned short) e->GetIntField(jtpt,field);
		field = e->GetFieldID(clazz,"tp_termno","I");
		current->tp_termno = (unsigned short) e->GetIntField(jtpt,field);
		field = e->GetFieldID(clazz,"tp_length","I");
		current->tp_length = (unsigned short) e->GetIntField(jtpt,field);
		field = e->GetFieldID(clazz,"tp_flags","I");
		current->tp_flags = (unsigned short) e->GetIntField(jtpt,field);
		field = e->GetFieldID(clazz,"tp_data","I");
		current->tp_data = (unsigned short) e->GetIntField(jtpt,field);
		field = e->GetFieldID(clazz,"rfu","I");
		current->rfu = (unsigned short) e->GetIntField(jtpt,field);
		/*
		printf(
			"[%d] type %hd termno %hd length %hd flags %hd data %hd\n",
			x,
			(unsigned short)current->tp_type,
			(unsigned short)current->tp_termno,
			(unsigned short)current->tp_length,
			(unsigned short)current->tp_flags,
			(unsigned short)current->tp_data
		);
		*/
	}
	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_tngen_j2n (JNIEnv *e, jobject jtngen, TN_GEN *tngen) {
	JVR_ENTER;

	if (jtngen == NULL) { jvr_throw(JVR_THROW_JVR,"jtngen argument cannot be null"); JVR_EXIT; return -1; }
	if (tngen == NULL) { jvr_throw(JVR_THROW_JVR,"tngen argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = tn_gen_class; // e->Find_Class("net/threebit/jvr/TN_GEN");
	jfieldID field;

	field = e->GetFieldID(clazz,"tg_dflag","I");
	tngen->tg_dflag = (unsigned short) e->GetIntField(jtngen,field);
	field = e->GetFieldID(clazz,"tg_freq1","I");
	tngen->tg_freq1 = (unsigned short) e->GetIntField(jtngen,field);
	field = e->GetFieldID(clazz,"tg_freq2","I");
	tngen->tg_freq2 = (unsigned short) e->GetIntField(jtngen,field);
	field = e->GetFieldID(clazz,"tg_ampl1","I");
	tngen->tg_ampl1 = (short) e->GetIntField(jtngen,field);
	field = e->GetFieldID(clazz,"tg_ampl2","I");
	tngen->tg_ampl2 = (short) e->GetIntField(jtngen,field);
	field = e->GetFieldID(clazz,"tg_dur","I");
	tngen->tg_dur = (short) e->GetIntField(jtngen,field);

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_tngencad_j2n(JNIEnv *e, jobject jtngencad, TN_GENCAD *tngencad) {
	JVR_ENTER;

	if (jtngencad == NULL) { jvr_throw(JVR_THROW_JVR,"jtngencad argument cannot be null"); JVR_EXIT; return -1; }
	if (tngencad == NULL) { jvr_throw(JVR_THROW_JVR,"tngencad argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = tn_gencad_class; // e->Find_Class("net/threebit/jvr/TN_GENCAD");
	jfieldID field;

	field = e->GetFieldID(clazz,"cycles","I");
	tngencad->cycles = e->GetIntField(jtngencad,field);
	field = e->GetFieldID(clazz,"numsegs","I");
	tngencad->numsegs = e->GetIntField(jtngencad,field);

	field = e->GetFieldID(clazz,"offtime","[I");
	jintArray intArray = (jintArray) e->GetObjectField(jtngencad,field);
	jint *intBuf = e->GetIntArrayElements(intArray,NULL);
	for (int x = 0; x < tngencad->numsegs; x++) {
		tngencad->offtime[x] = intBuf[x];
	}
	e->ReleaseIntArrayElements(intArray,intBuf,0);

	field = e->GetFieldID(clazz,"tone","[Lnet/threebit/jvr/TN_GEN;");
	jobjectArray jtngenArray = (jobjectArray) e->GetObjectField(jtngencad,field);
	if (jtngenArray == NULL) { jvr_throw(JVR_THROW_JVR,"tone member cannot be null"); JVR_EXIT; return -1; }
	for (int x = 0; x < tngencad->numsegs; x++) {
		jobject jtngen = e->GetObjectArrayElement(jtngenArray,x);
		if (jvr_convert_tngen_j2n(e,jtngen,&tngencad->tone[x]) == -1) {
			// exception already raised.
			JVR_EXIT; return -1;
		}
	}

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_dxxpb_j2n (JNIEnv *e, jobject jxpb, DX_XPB *xpb) {
	JVR_ENTER;

	if (jxpb == NULL) { jvr_throw(JVR_THROW_JVR,"jxpb argument cannot be null"); JVR_EXIT; return -1; }
	if (xpb == NULL) { jvr_throw(JVR_THROW_JVR,"xpb argument cannot be null"); JVR_EXIT; return -1; }

	xpb->wFileFormat = e->GetIntField(jxpb,dx_xpb_wFileFormat);
	xpb->nSamplesPerSec = e->GetLongField(jxpb,dx_xpb_nSamplesPerSec);
	xpb->wBitsPerSample = e->GetLongField(jxpb,dx_xpb_wBitsPerSample);
	xpb->wDataFormat = e->GetLongField(jxpb,dx_xpb_wDataFormat);

	JVR_EXIT; return 0;
}

extern DX_IOTT jvr_iott_buff[JVR_IOTT_SIZE];
extern int jvr_iott_alloc[JVR_IOTT_SIZE];
extern int jvr_iott_ready;

/**
 *
 */
int jvr_convert_dxiott_j2n(JNIEnv *e, jobjectArray jiottArray, DX_IOTT **iottp) {
	JVR_ENTER;

	if (jiottArray == NULL) { jvr_throw(JVR_THROW_JVR,"jiottArray argument cannot be null"); JVR_EXIT; return -1; }
	if (iottp == NULL) { jvr_throw(JVR_THROW_JVR,"iottp argument cannot be null"); JVR_EXIT; return -1; }
	int length = e->GetArrayLength(jiottArray);
	if (length == 0) { jvr_throw(JVR_THROW_JVR,"jiottArray has length 0"); JVR_EXIT; return -1; }
	if (length > JVR_IOTT_SIZE) {
		jvr_throw(JVR_THROW_JVR,"Length of jiottArray (%d) cannot exceed JVR_IOTT_SIZE (%d)",length,JVR_IOTT_SIZE);
		JVR_EXIT; return -1;
	}

	jclass clazz = dx_iott_class; // e->Find_Class("net/threebit/jvr/DX_IOTT");
	jfieldID field;
	int offset;
	DX_IOTT *current = NULL;
	DX_IOTT *prev = NULL;

	for (int x = 0; x < length; x++) {
		// DX_IOTT object and native-context allocation
		jobject jiott = e->GetObjectArrayElement(jiottArray,x);
		if (jiott == NULL) { jvr_throw(JVR_THROW_JVR,"jiottArray contains NULL element at offset %d",x); JVR_EXIT; return -1; }
		field = e->GetFieldID(clazz,"offset","I");
		offset = e->GetIntField(jiott,field);
		if (offset == -1) { jvr_throw(JVR_THROW_JVR,"DX_IOTT object at offset %d has no native-context allocation",x); JVR_EXIT; return -1; }
		current = (DX_IOTT*) &jvr_iott_alloc[JVR_IOTT_SIZE];
		if (x == 0) {
			// On the first pass, set the supplied pointer-to-a-pointer to the first
			// DX_IOTT struct in the linked list.
			*iottp = current;
		}
		// DX_IOTT object to struct conversion
		current = *iottp;
		current->io_prevp = prev; // Assigns NULL on first pass (which is correct)
		if (prev != NULL) {
			prev->io_nextp = current;
		}

		field = e->GetFieldID(clazz,"io_type","I");
		current->io_type = (unsigned short) e->GetIntField(jiott,field);
		if ((current->io_type & IO_CONT) || (current->io_type & IO_EOT) || (current->io_type & IO_LINK)) {
			jvr_throw(
				JVR_THROW_JVR,
				"DX_IOTT.io_type at offset %d included IO_EOT, IO_CONT or IO_LINK.  These are managed by JVR automatically.  Do not include them.",x);
			JVR_EXIT; return -1;
		}
		field = e->GetFieldID(clazz,"rfu","I");
		current->rfu = (unsigned short) e->GetIntField(jiott,field);
		field = e->GetFieldID(clazz,"io_fhandle","I");
		current->io_fhandle = e->GetIntField(jiott,field);
		field = e->GetFieldID(clazz,"io_offset","J");
		current->io_offset = (unsigned long) e->GetLongField(jiott,field);
		field = e->GetFieldID(clazz,"io_length","J");
		current->io_length = e->GetLongField(jiott,field);
		current->io_bufp = NULL; // io_bufp not supported yet.
		if (x == (length-1)) {
			current->io_type = current->io_type | IO_EOT;
			current->io_nextp = NULL;
		}
		else {
			current->io_type = current->io_type | IO_LINK;
		}
	}
	// Verify DX_IOTT struct/linked list
	/*
	current = *iottp;
	int x = 0;
	if (current == NULL) {
		printf("%s:%d ERROR; returned iottp is null",__FILE__,__LINE__);
	}
	else {
		printf("DX_IOTT[%d] (%d)\n",x,(int)current);
		printf("  io_type: %hd (",(unsigned short)current->io_type);
		if (current->io_type & IO_CONT) { printf("IO_CONT "); }
		if (current->io_type & IO_LINK) { printf("IO_LINK "); }
		if (current->io_type & IO_EOT) { printf("IO_EOT "); }
		if (current->io_type & IO_DEV) { printf("IO_DEV "); }
		if (current->io_type & IO_MEM) { printf("IO_MEM "); }
		if (current->io_type & IO_UIO) { printf("IO_UIO "); }
		printf(")\n");
		printf("  rfu: %hd\n",(unsigned short)current->rfu);
		printf("  io_fhandle: %d\n",current->io_fhandle);
		printf("  io_offset: %ld\n",current->io_offset);
		printf("  io_length: %ld\n",current->io_length);
		printf("  io_nextp: %d\n",(int)current->io_nextp);
		printf("  io_prevp: %d\n",(int)current->io_prevp);
	}
	*/
	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_mscdtarray_j2n (JNIEnv *e, jobjectArray jmscdtArray, MS_CDT *cdt, int *numParties) {
	JVR_ENTER;
	if (jmscdtArray == NULL) { jvr_throw(JVR_THROW_JVR,"jmscdtArray argument cannot be null"); JVR_EXIT; return -1; }
	if (cdt == NULL) { jvr_throw(JVR_THROW_JVR,"cdt argument cannot be null"); JVR_EXIT; return -1; }
	if (numParties == NULL) { jvr_throw(JVR_THROW_JVR,"numParties argument cannot be null"); JVR_EXIT; return -1; }
	int length = e->GetArrayLength(jmscdtArray);
	if (length == 0) {
		jvr_throw(JVR_THROW_JVR,"MS_CDT array cannot have length zero"); 
		JVR_EXIT; return -1;
	}
	if (length >= JVR_MSCDT_MAXSIZE) {
		jvr_throw(JVR_THROW_JVR,"MS_CDT array has length %d, which exceeds maximum of %d",length,JVR_MSCDT_MAXSIZE); 
		JVR_EXIT; return -1;
	}
	for (int x = 0; x < length; x++) {
		// Convert each array member separately.
		MS_CDT *current = (cdt+x);
		jobject jmscdt = e->GetObjectArrayElement(jmscdtArray,x);
		if (jvr_convert_mscdt_j2n (e, jmscdt, current) == -1) {
			JVR_EXIT;
			return -1;
		}
	}
	*numParties = length;
	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_mscdtarray_n2j (JNIEnv *e, jobjectArray jmscdtArray, MS_CDT *cdt, int numParties) {
	JVR_ENTER;
	if (jmscdtArray == NULL) { jvr_throw(JVR_THROW_JVR,"jmscdtArray argument cannot be null"); JVR_EXIT; return -1; }
	if (cdt == NULL) { jvr_throw(JVR_THROW_JVR,"cdt argument cannot be null"); JVR_EXIT; return -1; }

	int length = e->GetArrayLength(jmscdtArray);
	if (length != numParties) {
		jvr_throw(JVR_THROW_JVR,"Supplied MS_CDT array length (%d) not equal to numparties (%d)",length,numParties);
		JVR_EXIT; return -1;
	}

	for (int x = 0; x < numParties; x++) {
		MS_CDT *current = (cdt+x);
		jobject jmscdt = e->GetObjectArrayElement(jmscdtArray,x);
		if (jvr_convert_mscdt_n2j(e,jmscdt,current) == -1) {
			JVR_EXIT;
			return -1;
		}
	}

	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_mscdt_n2j (JNIEnv *e, jobject jmscdt, MS_CDT *cdt) {
	JVR_ENTER;

	if (cdt == NULL) {jvr_throw(JVR_THROW_JVR,"cdt argument cannot be null"); JVR_EXIT; return -1; }
	if (jmscdt == NULL) {jvr_throw(JVR_THROW_JVR,"jmscdt argument cannot be null"); JVR_EXIT; return -1; }

	e->SetIntField(jmscdt,ms_cdt_chan_num,cdt->chan_num);
	e->SetIntField(jmscdt,ms_cdt_chan_sel,cdt->chan_sel);
	e->SetIntField(jmscdt,ms_cdt_chan_attr,cdt->chan_attr);

	JVR_EXIT;
	return 0;
}

/**
 *
 */
int jvr_convert_mscdt_j2n (JNIEnv *e, jobject jmscdt, MS_CDT *cdt) {
	JVR_ENTER;
	cdt->chan_num = e->GetIntField(jmscdt,ms_cdt_chan_num);
	cdt->chan_sel = e->GetIntField(jmscdt,ms_cdt_chan_sel);
	cdt->chan_attr = e->GetIntField(jmscdt,ms_cdt_chan_attr);
	JVR_EXIT;
	return 0;
}

/**
 *
 */
int jvr_convert_metaevent_n2j (JNIEnv *e, jobject jmeta, METAEVENT *meta) {
	JVR_ENTER;
	if (jmeta == NULL) { jvr_throw(JVR_THROW_JVR,"jmeta argument cannot be null"); JVR_EXIT; return -1; }
	if (meta == NULL) { jvr_throw(JVR_THROW_JVR,"meta argument cannot be null"); JVR_EXIT; return -1; }

	e->SetLongField(jmeta,metaevent_magicno,(long) meta->magicno);
	e->SetLongField(jmeta,metaevent_flags,meta->flags);
	// Conversion from the native data to a java object is
	// shared between the SRL and the GC libraries.
	e->SetObjectField(
		jmeta,
		metaevent_evtdata,
		jvr_convert_evtdatap_n2j(
			e,
			meta->evttype,
			meta->evtlen,
			meta->evtdatap,
			meta
		)
	);
	e->SetLongField(jmeta,metaevent_evtlen,meta->evtlen);
	e->SetLongField(jmeta,metaevent_evtdev,meta->evtdev);
	e->SetLongField(jmeta,metaevent_evttype,meta->evttype);
	e->SetLongField(jmeta,metaevent_linedev,meta->linedev);
	e->SetLongField(jmeta,metaevent_crn,meta->crn);
	// TODO: extevtdatap
	//field = e->GetFieldID(clazz,"x","J");
	//e->SetLongField(jmeta,field,meta.x);
	// TODO: usrattr
	// field = e->GetFieldID(clazz,"x","J");
	// e->SetLongField(jmeta,field,meta.x);
	e->SetIntField(jmeta,metaevent_cclibid,meta->cclibid);
	e->SetIntField(jmeta,metaevent_rfu1,meta->rfu1);
	// Gather GC event information if applicable.
	// Otherwise leave the gcInfo member as "null".
	jobject jgcInfo = NULL;
	if (meta->flags & GCME_GC_EVENT) {
		GC_INFO gcInfo;
		if (gc_ResultInfo(meta, &gcInfo) != GC_SUCCESS) {
			jvr_throw(JVR_THROW_DG,"gc_ResultInfo() failed!");
			JVR_EXIT; return -1;
		}
		jgcInfo = e->NewObject(gc_info_class,gc_info_init);
		if (jgcInfo == NULL) {
			e->ExceptionDescribe();
			// TODO: why is null?
			//
			JVR_EXIT; return -1;
		}
		if (jvr_convert_gcinfo_n2j(e,jgcInfo,&gcInfo) == -1) {
			JVR_EXIT; return -1;
		}
	}
	e->SetObjectField(jmeta,metaevent_gcInfo,jgcInfo);

	JVR_EXIT; return 0;
}

/**
 *
 */
jobject jvr_convert_evtdatap_n2j (JNIEnv *e, long type, long len, void *datap, METAEVENT *meta) {
	JVR_ENTER;

	// Normal; no additional data to return.
	if (datap == NULL) { JVR_EXIT; return NULL; }

	if (type == TDX_CST || type == TDX_SETHOOK) {
		DX_CST *cstp = (DX_CST*) datap;
		jobject jcst = e->NewObject(dx_cst_class,dx_cst_init);
		e->SetIntField(jcst, dx_cst_cst_event,cstp->cst_event);
		e->SetIntField(jcst, dx_cst_cst_data,cstp->cst_data);
		JVR_EXIT; return jcst;
	}

	switch (len) {
		case 0:
			JVR_EXIT; return NULL;
		case 1: {
			char *cdatap = (char*) datap;
			jobject ret = e->NewObject(integer_class,integer_init,*cdatap);
			JVR_EXIT; 
			return ret;
		}
		case 2: {
			short *sdatap = (short*) datap;
			if (*sdatap >= 0) {
				jobject ret = e->NewObject(integer_class,integer_init, *sdatap);
				JVR_EXIT;
				return ret;
			}
			else {
				// Assume that we should have casted to (unsigned short)
				jobject ret = e->NewObject(integer_class,integer_init, (unsigned short) *sdatap);
				JVR_EXIT; 
				return ret;
			}
		}
		case 4: {
			int *idatap = (int*) datap;
			jobject ret = e->NewObject(integer_class,integer_init, *idatap);
			JVR_EXIT; 
			return ret;
		}
		case 8: {
			long *ldatap = (long*) datap;
			jobject ret = e->NewObject(long_class,long_init, *ldatap);
			JVR_EXIT; 
			return ret;
		}
		/*
		case 12: {
			byte buffer[12];
			byte *cdatap = (byte*) datap;
			for (int x =0; x < 12; x++) { buffer[x] = &datap;
			break;
	
		}*/
		case 16: {
			// TODO: what are these things?
			// Starting to think that I need a generic byte[]->Object converter
			// and leave the handling of this stuff to Java.  Handling 16 like
			// this because global call is generating them, and "not handling"
			// them is no longer an option.  Need a solution here.
			JVR_EXIT; return NULL;
		}
		default:
			char buffer[1024];
			snprintf(buffer,1000,"Unhandled condition: type=%ld len=%ld (%s:%d)",(long int)type,(long int)len,__FILE__,__LINE__);
			jobject ret = e->NewStringUTF(buffer);
			JVR_EXIT; 
			return ret;
	}
	// never reached.
	JVR_EXIT;
}

/**
 *
 */
int jvr_convert_gcinfo_n2j (JNIEnv *e, jobject jgcInfo, GC_INFO *gcInfo) {
	JVR_ENTER;
	if (jgcInfo == NULL) {
		jvr_throw(JVR_THROW_JVR,"jgcInfo argument cannot be null");
		JVR_EXIT; return -1; 
	}
	if (gcInfo == NULL) { jvr_throw(JVR_THROW_JVR,"gcInfo argument cannot be null"); JVR_EXIT; return -1; }
	e->SetObjectField(jgcInfo,gc_info_gcMsg,e->NewStringUTF(gcInfo->gcMsg));
	e->SetObjectField(jgcInfo,gc_info_ccLibName,e->NewStringUTF(gcInfo->ccLibName));
	e->SetObjectField(jgcInfo,gc_info_ccMsg,e->NewStringUTF(gcInfo->ccMsg));
	e->SetObjectField(jgcInfo,gc_info_additionalInfo,e->NewStringUTF(gcInfo->additionalInfo));
	e->SetIntField(jgcInfo,gc_info_gcValue,gcInfo->gcValue);
	e->SetIntField(jgcInfo,gc_info_ccLibId,gcInfo->ccLibId);
	e->SetLongField(jgcInfo,gc_info_ccValue,gcInfo->ccValue);
	JVR_EXIT; return 0;
}

/**
 *
 */
int jvr_convert_ctdevinfo_n2j (JNIEnv *e, jobject jdevinfo, CT_DEVINFO *devinfo) {
	JVR_ENTER;
	if (devinfo == NULL) { jvr_throw(JVR_THROW_JVR,"devinfo argument cannot be null"); JVR_EXIT; return -1; }
	if (jdevinfo == NULL) { jvr_throw(JVR_THROW_JVR,"jdevinfo argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = ct_devinfo_class; // e->Find_Class("net/threebit/jvr/CT_DEVINFO");
	jfieldID field;

	e->SetLongField(jdevinfo, e->GetFieldID(clazz,"ct_prodid","J"), devinfo->ct_prodid);
	e->SetIntField(jdevinfo, e->GetFieldID(clazz,"ct_devfamily","I"), devinfo->ct_devfamily);
	e->SetIntField(jdevinfo, e->GetFieldID(clazz,"ct_devmode","I"), devinfo->ct_devmode);
	e->SetIntField(jdevinfo, e->GetFieldID(clazz,"ct_nettype","I"), devinfo->ct_nettype);
	e->SetIntField(jdevinfo, e->GetFieldID(clazz,"ct_busmode","I"), devinfo->ct_busmode);
	e->SetIntField(jdevinfo, e->GetFieldID(clazz,"ct_busencoding","I"), devinfo->ct_busencoding);

	jintArray intArray = e->NewIntArray(7);
	e->SetObjectField(jdevinfo,e->GetFieldID(clazz,"ct_rfu","[I"),intArray);

	jint *intBuf = e->GetIntArrayElements(intArray,NULL);
	for (int x = 0; x < 7; x++) {
		intBuf[x] = devinfo->ct_rfu[x];
	}
	e->ReleaseIntArrayElements(intArray,intBuf,0);
	JVR_EXIT;
}

/**
 *
 */
int jvr_convert_gccallackblk_j2n(JNIEnv *e, jobject jcallack, GC_CALLACK_BLK *callack) {
	JVR_ENTER;
	if (jcallack == NULL) { jvr_throw(JVR_THROW_JVR,"jcallack argument cannot be null"); JVR_EXIT; return -1; }
	if (callack == NULL) { jvr_throw(JVR_THROW_JVR,"callack argument cannot be null"); JVR_EXIT; return -1; }

	jclass clazz = gc_callack_blk_class; // e->Find_Class("net/threebit/jvr/GC_CALLACK_BLK");
	callack->type = e->GetLongField(jcallack, e->GetFieldID(clazz,"type","J"));
	if (callack->type != GCACK_SERVICE_INFO) {
		jvr_throw(JVR_THROW_JVR,"JVR only supports GCACK_SERVICE_INFO at this time.  Sorry.  Care to donate an ISDN test environment?");
		JVR_EXIT; return -1;
	}

	callack->service.info.info_len = e->GetIntField(jcallack, e->GetFieldID(clazz,"info_len","I"));
	callack->service.info.info_type = e->GetIntField(jcallack, e->GetFieldID(clazz,"info_type","I"));
	JVR_EXIT; 
	return 0;
}

/**
 *
 */
int jvr_convert_ieblk_j2n (JNIEnv *e, jobject jieblk, IE_BLK *ieblk) {
	JVR_ENTER;
	if (jieblk == NULL) { jvr_throw(JVR_THROW_JVR,"jieblk argument cannot be null"); JVR_EXIT; return -1; }
	if (ieblk == NULL) { jvr_throw(JVR_THROW_JVR,"ieblk argument cannot be null"); JVR_EXIT; return -1; }

	ieblk->length = (short) e->GetIntField(jieblk,ie_blk_length);
	if (ieblk->length >= MAXLEN_IEDATA) {
		jvr_throw(JVR_THROW_JVR,"length (%d) exceeds MAXLEN_IEDATA (%d)",ieblk->length,MAXLEN_IEDATA);
		JVR_EXIT; return -1;
	}

	jintArray intArray = (jintArray) e->GetObjectField(jieblk,ie_blk_data);
	jint *intBuf = e->GetIntArrayElements(intArray,NULL);
	for (int x = 0; x < ieblk->length; x++) {
		ieblk->data[x] = (char) intBuf[x];
	}
	e->ReleaseIntArrayElements(intArray,intBuf,0);
	JVR_EXIT;
	return 0;
}

/**
 *
 */
int jvr_convert_gcparm_j2n (JNIEnv *e, jobject jgcparm, GC_PARM *gcparm) {
	JVR_ENTER;
	if (jgcparm == NULL) { jvr_throw(JVR_THROW_JVR,"jgcparm argument cannot be null"); JVR_EXIT; return -1; }
	if (gcparm == NULL) { jvr_throw(JVR_THROW_JVR,"gcparm argument cannot be null"); JVR_EXIT; return -1; }

	if (e->GetBooleanField(jgcparm, gc_parm_isString)) {
		jvr_throw(JVR_THROW_JVR,"GC_PARM objects with String values are not implemented yet.");
		JVR_EXIT;
		return -1;
	}
	gcparm->longvalue = (long) e->GetLongField(jgcparm, gc_parm_nvalue);
	JVR_EXIT;
	return 0;
}

/**
 *
 */
int jvr_convert_intValue (JNIEnv *e, jobject intObject) {
	JVR_ENTER;
	if (intObject == NULL) { jvr_throw(JVR_THROW_JVR,"Cannot convert from NULL java.lang.Integer"); }
	int i = e->CallIntMethod(intObject,integer_intvalue);
	JVR_EXIT;
	return i;
}

//////////////////////////////////////////////////////////////////////
// net_threebit_jvr_jvr.h Implementations
//////////////////////////////////////////////////////////////////////

// Shorthand for putting an String/Integer pair into the symbol map.
// Only used within JNI implementation of importSymbols()
#define PUT_INT(k,v) (e->CallObjectMethod(map,putMethod,e->NewStringUTF(k), e->NewObject(intClass, intInit, v)))

/*
 * Class:     net_threebit_jvr_jvr
 * Method:    importSymbols
 * Signature: ()Ljava/util/HashMap;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_jvr_importSymbols
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
	// class and method finder
	if (initializeJNIFields(e) == -1) { JVR_EXIT; return NULL; } // exception already thrown.

	// HashMap that will be returned
	jclass mapClass = hashmap_class; // e->Find_Class("java/util/HashMap");
	jmethodID putMethod = e->GetMethodID(mapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
	jobject map = e->NewObject(mapClass, e->GetMethodID(mapClass, "<init>", "()V"));

	// Integer.constructor
	jclass intClass = integer_class; // e->Find_Class("java/lang/Integer");
	jmethodID intInit = e->GetMethodID(intClass, "<init>", "(I)V");

	#ifdef DG_USER1_ASCII
	PUT_INT("DG_USER1_ASCII",DG_USER1_ASCII);
	#endif
	#ifdef DG_USER2_ASCII
	PUT_INT("DG_USER2_ASCII",DG_USER2_ASCII);
	#endif
	#ifdef DG_USER3_ASCII
	PUT_INT("DG_USER3_ASCII",DG_USER3_ASCII);
	#endif
	#ifdef DG_USER4_ASCII
	PUT_INT("DG_USER4_ASCII",DG_USER4_ASCII);
	#endif
	#ifdef DG_USER5_ASCII
	PUT_INT("DG_USER5_ASCII",DG_USER5_ASCII);
	#endif

	// The list of symbols that were being imported was starting to
	// get out of hand, so a file has been dedicated to them.
	#include "symbolput.cpp"
	JVR_EXIT; return map;
}

/*
 * Class:     net_threebit_jvr_jvr
 * Method:    initEventModel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_jvr_initEventModel
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
	// Same a link to the virtual machine for use by the event handler.
	if (e->GetJavaVM(&vm) < 0) {
		jvr_throw(JVR_THROW_JVR,"GetJavaVM failed");
		JVR_EXIT; return;
	}

#ifdef WIN32
	if (sr_enbhdlr(EV_ANYDEV, EV_ANYEVT,&jvr_eventHandler) == -1) {
		jvr_throw(JVR_THROW_DG,"sr_enbhdlr() failed: (%d) %s",ATDV_LASTERR(SRL_DEVICE),ATDV_ERRMSGP(SRL_DEVICE));
		JVR_EXIT; return;
	}
#endif

#ifdef LINUX
	int par = SR_SIGMODE;
	if (sr_setparm(SRL_DEVICE, SR_MODEID, &par ) == -1 ) {
		jvr_throw(JVR_THROW_DG,"sr_enbhdlr() failed: (%d) %s",ATDV_LASTERR(SRL_DEVICE),ATDV_ERRMSGP(SRL_DEVICE));
		JVR_EXIT; return;
	}
	if (sr_enbhdlr(EV_ANYDEV, EV_ANYEVT,&jvr_eventHandler) == -1) {
		jvr_throw(JVR_THROW_DG,"sr_enbhdlr() failed: (%d) %s",ATDV_LASTERR(SRL_DEVICE),ATDV_ERRMSGP(SRL_DEVICE));
		JVR_EXIT; return;
	}
#endif
	JVR_EXIT;
}

/**
 *
 */
void jvr_setdebuglevel (int level) {
	jvrDebugLevel = level;
}

/**
 *
 */
int jvr_getdebuglevel() {
	return jvrDebugLevel;
}

/*
 * Class:     net_threebit_jvr_jvr
 * Method:    setDebugLevel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_jvr_setDebugLevel
  (JNIEnv *e, jclass c, jint level) {
	jvr_setdebuglevel(level);
}

/*
 * Class:     net_threebit_jvr_jvr
 * Method:    getDebugLevel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_jvr_getDebugLevel
  (JNIEnv *, jclass) {
	return jvr_getdebuglevel();
}

/*
 * Class:     net_threebit_jvr_jvr
 * Method:    logNoise
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_jvr_logNoise
  (JNIEnv *e, jclass c) {
	JVR_EELOG("NOISE");
}

/*
 * Get the one and only JVM instance.
 */
JavaVM* jvr_getVM() {
	return vm;
}
