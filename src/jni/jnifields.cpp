/*
 * $Id: jnifields.cpp,v 1.16 2005/02/03 00:30:48 kevino Exp $
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

#ifndef __int64
// GCC doesn't have a __int64 built-in, and this patch basically uses
// "long long" instead. 
// See http://www.xraylith.wisc.edu/~khan/software/gnu-win32/README.jni.txt
typedef long long __int64;
#endif

#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <time.h>

#include <jni.h>
#define JNIFIELDSCPP
// #include "common.h"
#include "jnifields.h"

////////////////////////////////////////////////////////////////////////////////////
// 
////////////////////////////////////////////////////////////////////////////////////


// s = e->FindClass(n);
// printf("init class %s\n",n); 
#define INIT_CLASS(s,n) {\
	s = (jclass) e->NewGlobalRef((jobject)e->FindClass(n)); \
	if (s == NULL) { \
		char buffer[1024]; \
		snprintf(buffer,1000,"FindClass(\"%s\") failed. (%s:%d)",n,__FILE__,__LINE__); \
		e->ThrowNew(e->FindClass("net/threebit/jvr/JVRException"), buffer); \
		return -1; \
	} \
}

#define INIT_METHOD_VOID(c,s) {INIT_METHOD(c,s,"<init>","()V");}

// printf("init_static_method(%s,%s)\n",m,sig); 
#define INIT_STATIC_METHOD(c,s,m,sig) {\
	s = e->GetStaticMethodID(c,m,sig); \
	if (s == NULL) { \
		char buffer[1024]; \
		snprintf(buffer,1000,"GetStaticMethodID(%s,%s) failed. (%s:%d)",m,sig,__FILE__,__LINE__); \
		e->ThrowNew(e->FindClass("net/threebit/jvr/JVRException"), buffer); \
		return -1; \
	} \
}

#define INIT_METHOD(c,s,m,sig) {\
	s = e->GetMethodID(c,m,sig); \
	if (s == NULL) { \
		char buffer[1024]; \
		snprintf(buffer,1000,"GetMethodID(%s,%s) failed. (%s:%d)",m,sig,__FILE__,__LINE__); \
		e->ThrowNew(e->FindClass("net/threebit/jvr/JVRException"), buffer); \
		return -1; \
	} \
}


// printf("init field %s\n",f); 
#define INIT_FIELD(c,s,f,sig) { \
	s = e->GetFieldID(c,f,sig); \
	if (s == NULL) { \
		char buffer[1024]; \
		snprintf(buffer,1000,"GetFieldID(%s,%s) failed. (%s:%d)",f,sig,__FILE__,__LINE__); \
		e->ThrowNew(e->FindClass("net/threebit/jvr/JVRException"), buffer); \
		return -1; \
	} \
}


/*
 *
 */

int initializeJNIFields (JNIEnv *e)
{

	INIT_CLASS(jvrexception_class,"net/threebit/jvr/JVRException");
	INIT_CLASS(cclib_start_struct_class,"net/threebit/jvr/CCLIB_START_STRUCT");
	INIT_CLASS(ct_devinfo_class,"net/threebit/jvr/CT_DEVINFO");
	INIT_CLASS(dv_digit_class,"net/threebit/jvr/DV_DIGIT");
	INIT_CLASS(dv_tpt_class,"net/threebit/jvr/DV_TPT");
	INIT_CLASS(dx_cap_class,"net/threebit/jvr/DX_CAP");
	INIT_CLASS(dx_class,"net/threebit/jvr/dx");
	INIT_CLASS(dx_cst_class,"net/threebit/jvr/DX_CST");
	INIT_CLASS(dx_iott_class,"net/threebit/jvr/DX_IOTT");
	INIT_CLASS(dx_xpb_class,"net/threebit/jvr/DX_XPB");
	INIT_CLASS(gc_callack_blk_class,"net/threebit/jvr/GC_CALLACK_BLK");
	INIT_CLASS(gc_cclib_state_class,"net/threebit/jvr/GC_CCLIB_STATE");
	INIT_CLASS(gc_cclib_statusall,"net/threebit/jvr/GC_CCLIB_STATUSALL");
	INIT_CLASS(gc_cclib_statusall_class,"net/threebit/jvr/GC_CCLIB_STATUSALL");
	INIT_CLASS(gc_info_class,"net/threebit/jvr/GC_INFO"); 
	INIT_CLASS(gc_parm_blk_class,"net/threebit/jvr/GC_PARM_BLK");
	INIT_CLASS(gc_parm_class,"net/threebit/jvr/GC_PARM");
	INIT_CLASS(gc_start_struct_class,"net/threebit/jvr/GC_START_STRUCT");
	INIT_CLASS(gcexception_class,"net/threebit/jvr/GCException");
	INIT_CLASS(hashmap_class,"java/util/HashMap");
	INIT_CLASS(ie_blk_class,"net/threebit/jvr/IE_BLK");
	INIT_CLASS(integer_class,"java/lang/Integer");
	INIT_CLASS(jvr_class,"net/threebit/jvr/jvr");
	INIT_CLASS(long_class,"java/lang/Long");
	INIT_CLASS(metaevent_class,"net/threebit/jvr/MetaEvent");
	INIT_CLASS(ms_cdt_class,"net/threebit/jvr/MS_CDT");
	INIT_CLASS(sc_tsinfo_class,"net/threebit/jvr/SC_TSINFO");
	INIT_CLASS(tn_gen_class,"net/threebit/jvr/TN_GEN");
	INIT_CLASS(tn_gencad_class,"net/threebit/jvr/TN_GENCAD");

	INIT_FIELD(dx_cst_class,dx_cst_cst_data,"cst_data","I");
	INIT_FIELD(dx_cst_class,dx_cst_cst_event,"cst_event","I");
	INIT_FIELD(dx_iott_class,dx_iott_offset,"offset","I");
	INIT_FIELD(dx_xpb_class,dx_xpb_nSamplesPerSec,"nSamplesPerSec","J");
	INIT_FIELD(dx_xpb_class,dx_xpb_offset,"offset","I");
	INIT_FIELD(dx_xpb_class,dx_xpb_wBitsPerSample,"wBitsPerSample","J");
	INIT_FIELD(dx_xpb_class,dx_xpb_wDataFormat,"wDataFormat","J");
	INIT_FIELD(dx_xpb_class,dx_xpb_wFileFormat,"wFileFormat","I");
	INIT_FIELD(gc_info_class,gc_info_additionalInfo,"additionalInfo","Ljava/lang/String;");	
	INIT_FIELD(gc_info_class,gc_info_ccLibId,"ccLibId","I");	
	INIT_FIELD(gc_info_class,gc_info_ccLibName,"ccLibName","Ljava/lang/String;");	
	INIT_FIELD(gc_info_class,gc_info_ccMsg,"ccMsg","Ljava/lang/String;");	
	INIT_FIELD(gc_info_class,gc_info_ccValue,"ccValue","J");	
	INIT_FIELD(gc_info_class,gc_info_gcMsg,"gcMsg","Ljava/lang/String;");
	INIT_FIELD(gc_info_class,gc_info_gcValue,"gcValue","I");
	INIT_FIELD(gc_parm_blk_class,gc_parm_blk_offset,"offset","I");
	INIT_FIELD(gc_parm_class,gc_parm_isString,"isString","Z");
	INIT_FIELD(gc_parm_class,gc_parm_nvalue,"nvalue","J");
	INIT_FIELD(gc_parm_class,gc_parm_svalue,"svalue","Ljava/lang/String;");
	INIT_FIELD(ie_blk_class,ie_blk_data,"data","[I");	
	INIT_FIELD(ie_blk_class,ie_blk_length,"length","I");	
	INIT_FIELD(metaevent_class,metaevent_cclibid,"cclibid","I");
	INIT_FIELD(metaevent_class,metaevent_crn,"crn","J");
	INIT_FIELD(metaevent_class,metaevent_evtdata,"evtdata","Ljava/lang/Object;");
	INIT_FIELD(metaevent_class,metaevent_evtdev,"evtdev","J");
	INIT_FIELD(metaevent_class,metaevent_evtlen,"evtlen","J");
	INIT_FIELD(metaevent_class,metaevent_evttype,"evttype","J");
	INIT_FIELD(metaevent_class,metaevent_flags,"flags","J");
	INIT_FIELD(metaevent_class,metaevent_gcInfo,"gcInfo","Lnet/threebit/jvr/GC_INFO;");
	INIT_FIELD(metaevent_class,metaevent_linedev,"linedev","J");
	INIT_FIELD(metaevent_class,metaevent_magicno,"magicno","J");
	INIT_FIELD(metaevent_class,metaevent_rfu1,"rfu1","I");
	INIT_FIELD(ms_cdt_class,ms_cdt_chan_attr,"chan_attr","I");
	INIT_FIELD(ms_cdt_class,ms_cdt_chan_num,"chan_num","I");
	INIT_FIELD(ms_cdt_class,ms_cdt_chan_sel,"chan_sel","I");

	INIT_METHOD(integer_class,integer_init,"<init>","(I)V");
	INIT_METHOD(integer_class,integer_intvalue,"intValue","()I");
	INIT_METHOD(long_class,long_init,"<init>","(J)V");

	INIT_METHOD_VOID(dx_cst_class,dx_cst_init);
	INIT_METHOD_VOID(gc_info_class,gc_info_init);
	INIT_METHOD_VOID(metaevent_class,metaevent_init);

	INIT_STATIC_METHOD(dx_class,dx_uio_read_method,"uio_read","(II)[B");
	INIT_STATIC_METHOD(dx_class,dx_uio_seek_method,"uio_seek","(IJI)J");
	INIT_STATIC_METHOD(dx_class,dx_uio_write_method,"uio_write","(I[B)I");
	INIT_STATIC_METHOD(jvr_class,jvr_handleEvent_win32,"handleEvent","(J)V");

	return 0;
}

