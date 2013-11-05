/*
 * $Id: jvr.h,v 1.33 2005/01/26 01:34:04 kevino Exp $
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

static JavaVMAttachArgs attachArgs = { JNI_VERSION_1_2, NULL, NULL} ;

// Use this macro as the boolean control in if() statements
// when logging optional debugging information.  For example:
//
// if (JVR_DEBUG(JVR_INFO)) { /* logging info here */ }
// becomes
// if (1 <= jvr_getdebuglevel()) { /* logging ... */ }
//
#define JVR_NONE			0
#define JVR_INFO			1
#define JVR_WARNING		2
#define JVR_ERROR			3
#define JVR_ALL				99
#define JVR_DEBUG(level) level <= jvr_getdebuglevel()
// Convenience for jvr_log() arguments
#define JVR_LOG e,__FILE__,__LINE__
// ENTER function macro.
#define JVR_ENTER JVR_EELOG("ENTER")
#define JVR_EXIT  JVR_EELOG("EXIT ")
#define JVR_EELOG(JVR_EE) do \
	{ \
		if (JVR_DEBUG(JVR_ALL)) { \
			time_t now; \
			struct tm *t; \
			char timebuf[1024]; \
			time(&now); \
			t = localtime(&now); \
			strftime(timebuf,1000,"%Y.%m.%d %H:%M:%S",t); \
			jvr_log(JVR_LOG,"%s %s %s",timebuf,JVR_EE,__PRETTY_FUNCTION__); \
		} \
	} while(0)
#define JVR_LOG e,__FILE__,__LINE__

// The default length of DV_TPT pointer arrays when passing
// between callers and the convertion functions.
#define JVR_DVTPT_MAXSIZE 10

// The default maximum length of contiguous DX_IOTT structures
#define JVR_DXIOTT_MAXSIZE 10

// Maximum number of MS_CDT elements
#define JVR_MSCDT_MAXSIZE 10

/*
 * Get the one and only JVM instance.
 */
JavaVM* jvr_getVM();

/*
 * Throw a JNI exception.
 *
 * e   : Java Environment
 * file: The file in which the exception is being thrown.
 * line: The line number where the call to jvr_throw is located.
 * ... : See printf() family of functions.
 *
 * Note: use JVR_MARK macro to end up with __FILE__,__LINE__ for the first
 *       to arguments.
 *
 */
void jvr_throw (JNIEnv *e, const char* exceptionClass, const char* file, int line, const char* format, ...);

/**
 *
 */
void jvr_log (JNIEnv *e, const char* file, int line, const char* format, ...);

/**
 * Convert net.threebit.jvr.SC_TSINFO to the SC_TSINFO struct.
 * Return 0 on success, -1 on error (an exception will already have been thrown)
 */
int jvr_convert_sctsinfo_j2n(JNIEnv *e, jobject jtsinfo, SC_TSINFO *tsinfo);
/**
 * Convert from SC_TSINFO struct to net.threebit.jvr.SC_TSINFO;
 */
int jvr_convert_sctsinfo_n2j(JNIEnv *e, jobject jtsinfo, SC_TSINFO *tsinfo);

/**
 * Convert from net.threebit.jvr.DX_CAP to DX_CAP struct.
 */
int jvr_convert_dxcap_j2n (JNIEnv *e, jobject jdxcap, DX_CAP *dxcap);

/**
 * Convert from net.threebit.jvr.DX_CAP to DX_CAP struct.
 */
int jvr_convert_tngen_n2j (JNIEnv *e, jobject jtngen, TN_GEN *tngen);

/**
 * Convert from DV_TPT object to struct.
 *
 * The caller must guarantee that the size of (DV_TPT*) is at
 * least JVR_MAX_DVTPT.
 */
int jvr_convert_dvtpt_j2n (JNIEnv *e, jobjectArray jtpt, DV_TPT *tpt);

/**
 *
 */
int jvr_convert_tngen_j2n (JNIEnv *e, jobject jtngen, TN_GEN *tngen);

/**
 *
 */
int jvr_convert_tngencad_j2n(JNIEnv *e, jobject jtngencad, TN_GENCAD *tngencad);

/**
 *
 */
int jvr_convert_dxxpb_j2n (JNIEnv *e, jobject jxpb, DX_XPB *xpb);

// Number of native-context DX_IOTT structs available for allocation
#define JVR_IOTT_SIZE 200

/**
 *
 */
int jvr_convert_dxiott_j2n(JNIEnv *e, jobjectArray jiott, DX_IOTT **iottp);

/**
 *
 */
int jvr_convert_mscdtarray_j2n (JNIEnv *e, jobjectArray jmscdt, MS_CDT *cdt, int *numParties);

/**
 *
 */
int jvr_convert_mscdtarray_n2j (JNIEnv *e, jobjectArray jmscdt, MS_CDT *cdt, int numParties);

/**
 *
 */
int jvr_convert_mscdt_j2n (JNIEnv *e, jobject jmscdt, MS_CDT *cdt);

/**
 *
 */
int jvr_convert_mscdt_n2j (JNIEnv *e, jobject jmscdt, MS_CDT *cdt);

/**
 *
 */
int jvr_convert_metaevent_n2j (JNIEnv *e, jobject jmeta, METAEVENT *meta);

/**
 *
 */
jobject jvr_convert_evtdatap_n2j (JNIEnv *e, long type, long len, void *datap, METAEVENT *meta);

/**
 *
 */
int jvr_convert_gcinfo_n2j (JNIEnv *e, jobject jgcinfo, GC_INFO *gcinfo);

/**
 *
 */
int jvr_convert_ctdevinfo_n2j (JNIEnv *e, jobject jdevinfo, CT_DEVINFO *devinfo);

/**
 *
 */
int jvr_convert_gccallackblk_j2n (JNIEnv *e, jobject jcallack, GC_CALLACK_BLK *callack);

/**
 *
 */
int jvr_convert_ieblk_j2n (JNIEnv *e, jobject jieblk, IE_BLK *ieblk);

/**
 *
 */
void jvr_setdebuglevel (int level);

/**
 *
 */
int jvr_getdebuglevel();

/**
 *
 */
int jvr_convert_gcparm_j2n (JNIEnv *e, jobject jgcparm, GC_PARM *gcparm);

/**
 *
 */
int jvr_convert_intValue (JNIEnv *e, jobject intObject);
