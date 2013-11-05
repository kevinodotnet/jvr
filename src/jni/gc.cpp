/*
 * $Id: gc.cpp,v 1.40 2005/02/03 00:30:47 kevino Exp $
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
#include "net_threebit_jvr_gc.h"
#include "net_threebit_jvr_GC_0005fPARM_0005fBLK.h"

//
// This macro is used to collect the global call error information then
// pass it to the regular exception throwing macro.  This must be implemented
// as a macro to allow proper file and line numbers to be included in the
// exception information.
//
// Use it in your code like this:
// int ret = gc_SomeGCFunction(...);
// if (ret != GC_SUCCESS) { GC_THROW; }
// return ret;
//
#define GC_THROW GC_THROW_MSG(NULL)

#define GC_THROW_MSG(errmsg) \
{ \
	char *msg = errmsg; \
 	GC_INFO gcInfo; \
	int gc_ErrorInfo_ret = gc_ErrorInfo(&gcInfo); \
	if (gc_ErrorInfo_ret == GC_SUCCESS) { \
		jclass clazz = gc_info_class; \
		jmethodID method = e->GetMethodID(clazz,"<init>","()V"); \
		jobject jgcInfo = e->NewObject(clazz,method); \
		if (jvr_convert_gcinfo_n2j(e,jgcInfo,&gcInfo) == -1) { \
			printf("Failure occurred inside GC_THROW (%s:%d)",__FILE__,__LINE__); \
		} \
		else { \
			clazz = gcexception_class; \
			if (msg == NULL) { \
				jmethodID method = e->GetMethodID(clazz,"<init>","(Ljava/lang/String;ILnet/threebit/jvr/GC_INFO;)V"); \
				jobject exception = e->NewObject(clazz,method,e->NewStringUTF(__FILE__),__LINE__,jgcInfo); \
				e->Throw((jthrowable) exception); \
			} \
			else { \
				jmethodID method = e->GetMethodID(clazz,"<init>","(Ljava/lang/String;Ljava/lang/String;ILnet/threebit/jvr/GC_INFO;)V"); \
				jobject exception = e->NewObject(clazz,method,e->NewStringUTF(msg),e->NewStringUTF(__FILE__),__LINE__,jgcInfo); \
				e->Throw((jthrowable) exception); \
			} \
		} \
	} \
	else { \
		jvr_throw(JVR_THROW_DG,"gc_ErrorInfo() failed."); \
	} \
}

/**
 * Debugging: Not for export.
void print_gc_cclib_status() {
	int cclibid;
	int gc_error;
	long cc_error;
	char *msg;
	int cclibidp;
	int i;
	char str[100], str1[100];
	GC_CCLIB_STATUSALL cclib_status_all;
	if (gc_CCLibStatusEx ("GC_ALL_LIB", &cclib_status_all) != GC_SUCCESS) {
		gc_ErrorValue (&gc_error, &cclibid, &cc_error);
		gc_ResultMsg (LIBID_GC, (long) gc_error, &msg);
		printf ("%s:%d %d - %s\n", __FILE__, __LINE__,gc_error,msg);
		exit (1);
	}
	strcpy (str, "Call Control Library Status:\n");
	for (i = 0; i < GC_TOTAL_CCLIBS; i++) {
		if (gc_CCLibNameToID(cclib_status_all.cclib_state[i].name,&cclibidp) != GC_SUCCESS) {
			gc_ErrorValue (&gc_error, &cclibid, &cc_error);
			gc_ResultMsg (LIBID_GC, (long) gc_error, &msg);
			printf ("%s:%d %d - %s\n", __FILE__, __LINE__,gc_error,msg);
			exit (1);
		}
		switch (cclib_status_all.cclib_state[i].state) {
			case GC_CCLIB_CONFIGURED:
				printf("%s\t configured\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			case GC_CCLIB_AVAILABLE:
				printf("%s\t available\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			case GC_CCLIB_FAILED:
				printf("%s\t is not available for use\t(id: %d)\n", cclib_status_all.cclib_state[i].name,cclibidp);
				break;
			default:
				printf("%s\t unknown CCLIB status\n", cclib_status_all.cclib_state[i].name);
				break;
		}
	}
}
 */

////////////////////////////////////////////////////////////////////////////////////////
// net.threebit.jvr.GC_PARM_BLK
////////////////////////////////////////////////////////////////////////////////////////

#define JVR_GCPARMBLK_SIZE 200
GC_PARM_BLKP jvr_gcparmblk_buff[JVR_GCPARMBLK_SIZE];
int jvr_gcparmblk_alloc[JVR_GCPARMBLK_SIZE];
int jvr_gcparmblk_ready = 0; // not ready by default.

/*
 * Class:     net_threebit_jvr_GC_0005fPARM_0005fBLK
 * Method:    allocateNative
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_GC_1PARM_1BLK_allocateNative
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	// Initialize if required
	if (!jvr_gcparmblk_ready) {
		for (int x = 0; x < JVR_GCPARMBLK_SIZE; x++) {
			jvr_gcparmblk_alloc[x] = 0;
			jvr_gcparmblk_buff[x] = NULL;
		}
		jvr_gcparmblk_ready = 1;
	}
	// Find the first available DX_IOTT structure
	int offset = -1;
	for (int x = 0; x < JVR_GCPARMBLK_SIZE; x++) {
		if (!jvr_gcparmblk_alloc[x]) {
			offset = x;
			break;
		}
	}
	if (offset == -1) {
		jvr_throw(JVR_THROW_JVR,"No native-context GC_PARM_BLK structs are available for allocation (size:%d)",JVR_GCPARMBLK_SIZE);
		JVR_EXIT;
		return;
	}
	// Save the offset in the object instance
	e->SetIntField(o,gc_parm_blk_offset,offset);
	jvr_gcparmblk_alloc[offset] = 1;
	JVR_EXIT;
	return;
}

/*
 * Class:     net_threebit_jvr_GC_0005fPARM_0005fBLK
 * Method:    releaseNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_threebit_jvr_GC_1PARM_1BLK_releaseNative
  (JNIEnv *e, jobject o)
{
	JVR_ENTER;
	// TODO: memory leak: if buff is not null, then must pass to memory
	// freer gc_util_***
	int offset = e->GetIntField(o,gc_parm_blk_offset);
	jvr_gcparmblk_alloc[offset] = 0;
	if (jvr_gcparmblk_buff[offset] != NULL) {
		gc_util_delete_parm_blk(jvr_gcparmblk_buff[offset]);
	}
	jvr_gcparmblk_buff[offset] = NULL;
	e->SetIntField(o,gc_parm_blk_offset,-1);
	JVR_EXIT;
	return;
}

////////////////////////////////////////////////////////////////////////////////////////
// END
////////////////////////////////////////////////////////////////////////////////////////

/*
 * Class:     net_threebit_jvr_gc
 * Method:    Start
 * Signature: (Lnet/threebit/jvr/GC_START_STRUCT;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_Start
  (JNIEnv *e, jclass c, jobject jgcss)
{
	JVR_ENTER;
	if (jgcss == NULL) {
		int ret = gc_Start(NULL);
		if (ret != GC_SUCCESS) { GC_THROW; }
		JVR_EXIT;
		return ret;
	}
	else {
		GC_START_STRUCT gcss;
		CCLIB_START_STRUCT ccss[10];
		gcss.num_cclibs = 0;
		gcss.cclib_list = ccss;
		for (int x = 0; x < 10; x++) { ccss[x].cclib_name = NULL; ccss[x].cclib_data = NULL; }

		jclass clazz = gc_start_struct_class; // e->Find_Class("net/threebit/jvr/GC_START_STRUCT");
		jfieldID field = e->GetFieldID(clazz,"num_cclibs","I");
		gcss.num_cclibs = e->GetIntField(jgcss,field);
		if (gcss.num_cclibs > 10) {
			jvr_throw(JVR_THROW_JVR,"Number of CCLIBS (%d) exceeds maximum supported value (%d)",gcss.num_cclibs,10);
			JVR_EXIT;
			return GC_ERROR;
		}

		field = e->GetFieldID(clazz,"cclib_list","[Lnet/threebit/jvr/CCLIB_START_STRUCT;");
		jobjectArray jccssArray = (jobjectArray) e->GetObjectField(jgcss,field);

		clazz = cclib_start_struct_class; // e->Find_Class("net/threebit/jvr/CCLIB_START_STRUCT");
		// Verify that no cclib_data data was given (it is not supported yet)
		for (int x = 0; x < gcss.num_cclibs; x++) {
			jobject jccss = e->GetObjectArrayElement(jccssArray,x);
			field = e->GetFieldID(clazz,"cclib_data","Ljava/lang/Object;");
			if (e->GetObjectField(jccss,field) != NULL) {
				jvr_throw(JVR_THROW_JVR,"cclib_data field is not supported yet.  It was non-null at offset %d",x);
				JVR_EXIT;
				return GC_ERROR;
			}
		}
		// Populate the cclib_name fields.
		for (int x = 0; x < gcss.num_cclibs; x++) {
			jobject jccss = e->GetObjectArrayElement(jccssArray,x);
			field = e->GetFieldID(clazz,"cclib_name","Ljava/lang/String;");
			ccss[x].cclib_name = (char*) e->GetStringUTFChars((jstring) e->GetObjectField(jccss,field),NULL);
		}

//		// Debugging
//		printf("gcss.num_cclibs: %d\n",gcss.num_cclibs);
//		for (int x = 0; x < gcss.num_cclibs; x++) {
//			printf("[%d] cclib_name: %s\n",x,(gcss.cclib_list+x)->cclib_name);
//		}

		// Start em up!
		int ret = gc_Start(&gcss);
		if (ret != GC_SUCCESS) { GC_THROW; }

		// Release the cclib_name buffers.
		for (int x = 0; x < gcss.num_cclibs; x++) {
			e->ReleaseStringUTFChars(
				(jstring) e->GetObjectField( 
					e->GetObjectArrayElement(jccssArray,x),
					field
				),
				ccss[x].cclib_name
			);
		}
		// printf("----- AFTER[2] -----\n"); print_gc_cclib_status();
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    Stop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_Stop
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
	int ret = gc_Stop();
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    CCLibStatusEx
 * Signature: (Ljava/lang/String;Lnet/threebit/jvr/GC_CCLIB_STATUSALL;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_CCLibStatusEx
  (JNIEnv *e, jclass c, jstring jcclib_name, jobject jstatus)
{
	JVR_ENTER;
	if (jcclib_name == NULL) { jvr_throw(JVR_THROW_JVR,"jcclib_name argument cannot be null"); return GC_ERROR; }
	char *cclib_name = (char*) e->GetStringUTFChars(jcclib_name,NULL);
	if (strcmp(cclib_name,"GC_ALL_LIB") == 0) {
		e->ReleaseStringUTFChars(jcclib_name,cclib_name);
		if (jstatus == NULL) {
			jvr_throw(JVR_THROW_JVR,"jstatus argument cannot be null when requesting status of 'GC_ALL_LIB'");
			JVR_EXIT;
			return GC_ERROR;
		}
		GC_CCLIB_STATUSALL statusall;
		if (gc_CCLibStatusEx ("GC_ALL_LIB", &statusall) != GC_SUCCESS) {
			GC_THROW; 
			JVR_EXIT;
			return GC_ERROR;
		}
		// Initialize array of CC Lib States
		jclass clazz = gc_cclib_state_class; // e->Find_Class("net/threebit/jvr/GC_CCLIB_STATE");
		jmethodID method = e->GetMethodID(clazz,"<init>","()V"); // new GC_CCLIB_STATE()
		jfieldID field;
		jobjectArray cclibStates = (jobjectArray) e->NewObjectArray(GC_TOTAL_CCLIBS,clazz,NULL);
		// Copy the data into the Java format:
		for (int x = 0; x < GC_TOTAL_CCLIBS; x++) {
			jobject cclibstate = e->NewObject(clazz,method);
			e->SetObjectArrayElement(cclibStates,x,cclibstate);
			field = e->GetFieldID(clazz,"name","Ljava/lang/String;");
			e->SetObjectField(cclibstate,field,e->NewStringUTF( statusall.cclib_state[x].name ));
			field = e->GetFieldID(clazz,"state","I");
			e->SetIntField(cclibstate,field,statusall.cclib_state[x].state);
		}
		// Set the array member in the supplied status object
		clazz = gc_cclib_statusall_class; // e->Find_Class("net/threebit/jvr/GC_CCLIB_STATUSALL");
		field = e->GetFieldID(clazz,"cclib_state","[Lnet/threebit/jvr/GC_CCLIB_STATE;");
		e->SetObjectField(jstatus,field,cclibStates);
		JVR_EXIT;
		return GC_SUCCESS;
	}
	else {
		int status;
		if (gc_CCLibStatusEx (cclib_name, &status) != GC_SUCCESS) {
			e->ReleaseStringUTFChars(jcclib_name,cclib_name);
			GC_THROW; 
			JVR_EXIT;
			return GC_ERROR;
		}
		e->ReleaseStringUTFChars(jcclib_name,cclib_name);
		JVR_EXIT;
		return status;
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    OpenEx
 * Signature: (Ljava/lang/String;ILjava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_gc_OpenEx
  (JNIEnv *e, jclass c, jstring jdevName, jint mode, jobject userAttr)
{
	JVR_ENTER;
	LINEDEV linedev;
	char* devName = (char*) e->GetStringUTFChars(jdevName,NULL);
	int ret = gc_OpenEx(&linedev,devName,mode,NULL);
	e->ReleaseStringUTFChars(jdevName,devName);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
	return linedev;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    Close
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_Close
  (JNIEnv *e, jclass c, jlong linedev)
{
	JVR_ENTER;
	int ret = gc_Close(linedev);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetMetaEventEx
 * Signature: (J)Lnet/threebit/jvr/METAEVENT;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_gc_GetMetaEventEx
  (JNIEnv *e, jclass c, jlong eventHandle)
{
	JVR_ENTER;
#ifdef LINUX
	jvr_throw(JVR_THROW_JVR,"Not supported in linux release");
	JVR_EXIT;
	return NULL;
#endif
#ifdef WIN32
	JVR_EELOG("CHECK");
	METAEVENT meta;
	JVR_EELOG("CHECK");
	if (gc_GetMetaEventEx(&meta,(unsigned long)eventHandle) != GC_SUCCESS) {
		JVR_EELOG("CHECK");
		GC_THROW;
		JVR_EXIT;
		return NULL;
	}
	JVR_EELOG("CHECK");
	if (metaevent_class == NULL) { JVR_EELOG("WARNING IS NULL"); }
	if (metaevent_init == NULL) { JVR_EELOG("WARNING IS NULL"); }
	//jvr_log(e,__FILE__,__LINE__,"metaevent_class : %d",metaevent_class);
	//jvr_log(e,__FILE__,__LINE__,"metaevent_init  : %d",metaevent_init);
	//jvr_log(e,__FILE__,__LINE__,"e               : %d",e);
	jobject jmeta = e->NewObject(metaevent_class,metaevent_init);
	JVR_EELOG("CHECK");
	if (jvr_convert_metaevent_n2j(e,jmeta,&meta) == -1) { 
		JVR_EXIT;
		return NULL; 
	}
	JVR_EXIT;
	return jmeta;
#endif
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetMetaEvent
 * Signature: ()Lnet/threebit/jvr/METAEVENT;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_gc_GetMetaEvent
  (JNIEnv *e, jclass c)
{
	JVR_ENTER;
#ifdef WIN32
	jvr_throw(JVR_THROW_JVR,"Not supported in win32 release");
	JVR_EXIT;
	return NULL;
#endif
#ifdef LINUX
	METAEVENT meta;
	if (gc_GetMetaEvent(&meta) != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return NULL;
	}
	jclass clazz = metaevent_class; // e->Find_Class("net/threebit/jvr/MetaEvent");
	jmethodID method = e->GetMethodID(clazz,"<init>","()V");
	jobject jmeta = e->NewObject(clazz,method);
	if (jvr_convert_metaevent_n2j(e,jmeta,&meta) == -1) { return NULL; }
	JVR_EXIT;
	return jmeta;
#endif
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    ResetLineDev
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_ResetLineDev
  (JNIEnv *e, jclass c, jlong linedev, jlong mode)
{
	JVR_ENTER;
	int ret = gc_ResetLineDev(linedev,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    MakeCall
 * Signature: (JLjava/lang/String;Lnet/threebit/jvr/GC_MAKECALL_BLK;IJ)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_gc_MakeCall
  (JNIEnv *e, jclass c, jlong linedev, jstring jnumber, jobject jmakecall, jint timeout, jlong mode)
{
	JVR_ENTER;
	if (jmakecall != NULL) {
		jvr_throw(JVR_THROW_JVR,"Sorry; but the GC_MAKECALL_BLK argument is not supported yet.");
		JVR_EXIT;
		return GC_ERROR;
	}
	CRN crn;
	GC_MAKECALL_BLK *makecallp = NULL;
	char *number = (char*) e->GetStringUTFChars(jnumber,NULL);
	int ret = gc_MakeCall(linedev,&crn,number,makecallp,timeout,(unsigned long)mode);
	e->ReleaseStringUTFChars(jnumber,number);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
	return crn;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    DropCall
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_DropCall
  (JNIEnv *e, jclass c, jlong crn, jint cause, jlong mode)
{
	JVR_ENTER;
	int ret = gc_DropCall(crn,cause,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    ReleaseCallEx
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_ReleaseCallEx
  (JNIEnv *e, jclass c, jlong crn, jlong mode)
{
	JVR_ENTER;
	int ret = gc_ReleaseCallEx(crn,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetCallInfo
 * Signature: (JI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_gc_GetCallInfo
  (JNIEnv *e, jclass c, jlong crn, jint infoId)
{
	JVR_ENTER;
	// There is a warning in the docs that says:
	//
	//   "Ensure that the application verifies that the buffer pointed to by the 
	//    valuep parameter is large enough to hold the information requested by 
	//    the info_id parameter."
	//
	// Unfortunately, it doesn't tell us what the maximum size might be!  So,
	// we'll just use up alot instead.  One Kb should be enough.  This sits on
	// the stack so it is not a permanent memory problem.
	char value[1024];
	for (int x = 0; x < 1024; x++) { value[x] = '.'; }
	value[1023] = 0;
	int ret = gc_GetCallInfo(crn,infoId,value);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return NULL;
	}

	switch (infoId) {
		// String cases
		case CALLINFOTYPE:
		case CALLNAME:
		case CALLTIME:
		case CATEGORY_DIGIT:
		case DESTINATION_ADDRESS:
		case ORIGINATION_ADDRESS:
		{
			jobject s = e->NewStringUTF(value);
			JVR_EXIT;
			return s;
		}
		// Integer case
		case CONNECT_TYPE:
		case PRESENT_RESTRICT:
		{
			jclass clazz = integer_class; // e->Find_Class("java/lang/Integer");
			jmethodID method = e->GetMethodID(clazz, "<init>", "(I)V");
			jobject s = e->NewObject(clazz,method,(char)value[0]);
			JVR_EXIT;
			return s;
		}
		default:
			// Since gc_GetCallInfo did not fail, it's a good ID, but JVR is behind 
			// in handling support for it.
			jvr_throw(JVR_THROW_JVR,"Unknown information ID: %d; gc_GetCallInfo understood it, but JVR does not know how to interpret it.",infoId);
			JVR_EXIT;
			return NULL;
	}
	JVR_EXIT;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    Listen
 * Signature: (JLnet/threebit/jvr/SC_TSINFO;J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_Listen
  (JNIEnv *e, jclass c, jlong linedev, jobject jtsinfo, jlong mode)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;
	if (jvr_convert_sctsinfo_j2n(e,jtsinfo,&tsinfo) == -1) { 
		JVR_EXIT;
		return -1; 
	}
	int ret = gc_UnListen(linedev,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    UnListen
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_UnListen
  (JNIEnv *e, jclass c, jlong linedev, jlong mode)
{
	JVR_ENTER;
	int ret = gc_UnListen(linedev,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    WaitCall
 * Signature: (JIJ)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_gc_WaitCall
  (JNIEnv *e, jclass c, jlong linedev, jint timeout, jlong mode)
{
	JVR_ENTER;
	CRN crn;
	CRN *crnp = &crn;
	if (mode == EV_ASYNC) {
		// CRN pointer must be NULL for EV_ASYNC mode.
		crnp = NULL;
	}
	int ret = gc_WaitCall(linedev,crnp,NULL,timeout,(unsigned long)mode);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	if (mode == EV_SYNC) {
		// return the CRN instead
		JVR_EXIT;
		return crn;
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    AnswerCall
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_AnswerCall
  (JNIEnv *e, jclass c, jlong crn, jint rings, jlong mode)
{
	JVR_ENTER;
	int ret = gc_AnswerCall(crn,rings,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    CRN2LineDev
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_gc_CRN2LineDev
  (JNIEnv *e, jclass c, jlong crn)
{
	JVR_ENTER;
	long linedev;
	int ret = gc_CRN2LineDev(crn,&linedev);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return (long) ret;
	}
	JVR_EXIT;
	return linedev;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    AttachResource
 * Signature: (JILnet/threebit/jvr/GC_PARM_BLK;Lnet/threebit/jvr/GC_PARM_BLK;IJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_AttachResource
  (JNIEnv *e, jclass c, jlong linedev, jint resourceH, jobject resourceAttr, jobject returnAttr, jint resourceType, jlong mode)
{
	JVR_ENTER;
	if (resourceAttr != NULL) { jvr_throw(JVR_THROW_JVR,"GC_PARM_BLK argument not supported yet."); 
		JVR_EXIT;
		return GC_ERROR; 
	}
	if (returnAttr != NULL) { jvr_throw(JVR_THROW_JVR,"GC_PARM_BLK argument not supported yet."); 
		JVR_EXIT;
		return GC_ERROR; 
	}

	int ret = gc_AttachResource(linedev,resourceH,NULL,NULL,resourceType,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    Detach
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_Detach
  (JNIEnv *e, jclass c, jlong linedev, jint voiceH, jlong mode)
{
	JVR_ENTER;
	int ret = gc_Detach(linedev,voiceH,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetCallState
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_GetCallState
  (JNIEnv *e, jclass c, jlong crn)
{
	JVR_ENTER;
	int state;
	int ret = gc_GetCallState(crn,&state);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
	return state;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetXmitSlot
 * Signature: (J)Lnet/threebit/jvr/SC_TSINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_gc_GetXmitSlot
  (JNIEnv *e, jclass c, jlong linedev)
{
	JVR_ENTER;
	SC_TSINFO tsinfo;
	long ts;
	tsinfo.sc_numts = 1;
	tsinfo.sc_tsarrayp = &ts;

	int ret = gc_GetXmitSlot(linedev,&tsinfo);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return NULL;
	}

	jclass clazz = sc_tsinfo_class; // e->Find_Class("net/threebit/jvr/SC_TSINFO");
	jmethodID init = e->GetMethodID(clazz, "<init>", "()V");
	jobject jtsinfo = e->NewObject(clazz, init);
	if (jvr_convert_sctsinfo_n2j(e,jtsinfo,&tsinfo) == -1) {
		JVR_EXIT;
		return NULL;
	}
	JVR_EXIT;
	return jtsinfo;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetResourceH
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_GetResourceH
  (JNIEnv *e, jclass c, jlong linedev, jint type)
{
	JVR_ENTER;
	int handle;
	int ret = gc_GetResourceH(linedev,&handle,type);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
	return handle;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetCTInfo
 * Signature: (J)Lnet/threebit/jvr/CT_DEVINFO;
 */
JNIEXPORT jobject JNICALL Java_net_threebit_jvr_gc_GetCTInfo
  (JNIEnv *e, jclass c, jlong linedev)
{
	JVR_ENTER;
	CT_DEVINFO devinfo;
	int ret = gc_GetCTInfo(linedev,&devinfo);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return NULL;
	}

	jclass clazz = ct_devinfo_class; // e->Find_Class("net/threebit/jvr/CT_DEVINFO");
	jmethodID init = e->GetMethodID(clazz, "<init>", "()V");
	jobject jdevinfo = e->NewObject(clazz, init);
	if (jvr_convert_ctdevinfo_n2j(e,jdevinfo,&devinfo) == -1) {
		JVR_EXIT;
		return NULL;
	}
	JVR_EXIT;
	return jdevinfo;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetChanState
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_SetChanState
  (JNIEnv *e, jclass c, jlong linedev, jint state, jlong mode)
{
	JVR_ENTER;
	int ret = gc_SetChanState(linedev,state,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    LoadDxParm
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_LoadDxParm
  (JNIEnv *e, jclass c, jlong linedev, jstring jfilename)
{
	JVR_ENTER;
	if (jfilename == NULL) { 
		jvr_throw(JVR_THROW_JVR,"jfilename argument cannot be null"); 
		JVR_EXIT;
		return GC_ERROR; 
	}
	char errmsg[2048];
	for (int x = 0; x < 2048; x++) { errmsg[x] = 0; }
	char *filename = (char*) e->GetStringUTFChars(jfilename,NULL);
	int ret = gc_LoadDxParm(linedev, filename, errmsg, 2048);
	if (ret != GC_SUCCESS) {
		GC_THROW_MSG(errmsg);
	}
	e->ReleaseStringUTFChars(jfilename,filename);
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    CallAck
 * Signature: (JLnet/threebit/jvr/GC_CALLACK_BLK;J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_CallAck
  (JNIEnv *e, jclass c, jlong crn, jobject jcallack, jlong mode)
{
	JVR_ENTER;
	if (jcallack == NULL) { jvr_throw(JVR_THROW_JVR,"jcallack argument cannot be null"); 
		JVR_EXIT;
		return GC_ERROR; 
	}

	GC_CALLACK_BLK callack;
	if (jvr_convert_gccallackblk_j2n(e,jcallack,&callack) == -1) { 
		JVR_EXIT;
		return GC_ERROR; 
	}

	int ret = gc_CallAck(crn,&callack,(unsigned long)mode);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetVoiceH
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_GetVoiceH
  (JNIEnv *e, jclass c, jlong linedev)
{
	JVR_ENTER;
	int voiceh;
	int ret = gc_GetVoiceH(linedev,&voiceh);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return ret;
	}
	JVR_EXIT;
	return voiceh;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetInfoElem
 * Signature: (IJLnet/threebit/jvr/IE_BLK;I)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_SetInfoElem
  (JNIEnv *e, jclass c, jint targetType, jlong targetId, jobject jieblk, jint duration)
{
	JVR_ENTER;
	IE_BLK ieblk;
	if (jvr_convert_ieblk_j2n(e,jieblk,&ieblk) == -1) { 
		JVR_EXIT;
		return -1; 
	}

	GC_PARM_BLKP infoparmblkp = NULL;
	GC_PARM_DATAP t_parm_datap = NULL;  

	/*
	gc_util_insert_parm_ref(GC_PARM_BLKP *parm_blkpp, unsigned short setID,
                            unsigned short parmID, unsigned char data_size,
                            void *datap);*/

	gc_util_insert_parm_ref(&infoparmblkp, GCIS_SET_IE, GCIS_PARM_UIEDATA, sizeof(IE_BLK), (void*) &ieblk);  
	if (infoparmblkp == NULL) {
		jvr_throw(JVR_THROW_JVR,"Failed to convert IE_BLK to native context; memory error");
		JVR_EXIT;
		return GC_ERROR;
	}  

	int ret = gc_SetUserInfo(GCTGT_GCLIB_NETIF, targetId, infoparmblkp, GC_SINGLECALL);  
	if (ret != GC_SUCCESS) { GC_THROW; }
	gc_util_delete_parm_blk(infoparmblkp);  
	JVR_EXIT;
	return (ret);
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetConfigData
 * Signature: (IJLnet/threebit/jvr/GC_PARM_BLK;IIJ)J
 */
JNIEXPORT jlong JNICALL Java_net_threebit_jvr_gc_SetConfigData
  (JNIEnv *e, jclass c, jint targetType, jlong targetId, jobject jgcparmblk, jint timeout, jint updateCond, jlong mode)
{
	JVR_ENTER;
	// get the offset from the GC_PARM_BLK object.
	int offset = e->GetIntField(jgcparmblk,gc_parm_blk_offset);
	// get the native context GC_PARM_BLKP.
	GC_PARM_BLKP parm = jvr_gcparmblk_buff[offset];
	// Set the config data
	long requestId = -1;
	int ret = gc_SetConfigData(
		targetType,
		targetId,
		parm,
		timeout,
		updateCond,
		&requestId,
		(unsigned long) mode
	);
	if (ret != GC_SUCCESS) {
		GC_THROW;
		JVR_EXIT;
		return GC_ERROR;
	}
	JVR_EXIT;
	return requestId;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    util_insert_parm_val
 * Signature: (Lnet/threebit/jvr/GC_PARM_BLK;IIIJ)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_util_1insert_1parm_1val
  (JNIEnv *e, jclass c, jobject jgcparmblk, jint setId, jint parmId, jint size, jlong data)
{
	JVR_ENTER;
	// get the offset from the GC_PARM_BLK object.
	int offset = e->GetIntField(jgcparmblk,gc_parm_blk_offset);
	// set the parameter value
	int ret = gc_util_insert_parm_val(
		&jvr_gcparmblk_buff[offset],
		(unsigned short) setId,
		(unsigned short) parmId,
		(unsigned char) size,
		(unsigned long) data
	);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetCallingNum
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_SetCallingNum
  (JNIEnv *e, jclass c, jlong linedev, jstring jnumber)
{
	JVR_ENTER;
	if (jnumber == NULL) { jvr_throw(JVR_THROW_JVR,"jnumber argument cannot be null"); 
		JVR_EXIT;
		return GC_ERROR; 
	}
	char *number = (char*) e->GetStringUTFChars(jnumber,NULL);
	int ret = gc_SetCallingNum(linedev,number);
	e->ReleaseStringUTFChars(jnumber,number);
	if (ret != GC_SUCCESS) { GC_THROW; }
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    GetNetworkH
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_GetNetworkH
  (JNIEnv *e, jclass c, jlong linedev)
{
	JVR_ENTER;
	int dev;
	int ret = gc_GetNetworkH(linedev,&dev);
	if (ret != GC_SUCCESS) { 
		GC_THROW; 
		JVR_EXIT;
		return ret; 
	}
	JVR_EXIT;
	return dev;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetParm
 * Signature: (JILnet/threebit/jvr/GC_PARM;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_SetParm
  (JNIEnv *e, jclass c, jlong linedev, jint parmId, jobject jgcparm)
{
	JVR_ENTER;
	GC_PARM gcparm;
	if (jvr_convert_gcparm_j2n(e,jgcparm,&gcparm) == -1) { 
		JVR_EXIT;
		return -1; 
	}
	/*
	printf("BEFORE:\n");
	printf("gc_SetParm INT  (%d,%d)\n",parmId,gcparm.intvalue);
	printf("gc_SetParm LONG (%d,%ld)\n",parmId,gcparm.longvalue);
	gcparm.intvalue = GCPV_DISABLE;
	printf("AFTER:\n");
	printf("gc_SetParm INT  (%d,%d)\n",parmId,gcparm.intvalue);
	printf("gc_SetParm LONG (%d,%ld)\n",parmId,gcparm.longvalue);
	*/
	int ret = gc_SetParm(linedev, parmId, gcparm);
	if (ret != GC_SUCCESS) {
		GC_THROW;
	}
	JVR_EXIT;
	return ret;
}

/*
 * Class:     net_threebit_jvr_gc
 * Method:    SetCallProgressParm
 * Signature: (JLnet/threebit/jvr/DX_CAP;)I
 */
JNIEXPORT jint JNICALL Java_net_threebit_jvr_gc_SetCallProgressParm
  (JNIEnv *e, jclass c, jlong linedev, jobject jdxcap)
{
	JVR_ENTER;

	DX_CAP dxcap;
	DX_CAP *dxcapp = &dxcap;
	if (jdxcap == NULL) {
		dxcapp = NULL;
	}
	else if (jvr_convert_dxcap_j2n(e,jdxcap,&dxcap) == -1) {
		JVR_EXIT; 
		return -1; 
	}

	int ret = gc_SetCallProgressParm(linedev,dxcapp);
	if (ret != GC_SUCCESS) {
		GC_THROW;
	}
	JVR_EXIT;
	return ret;

}
