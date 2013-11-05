package net.threebit.jvr;

/*
 * $Id: jvr.java,v 1.75 2005/01/26 01:34:04 kevino Exp $
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

import java.lang.reflect.*;
import java.util.*;

/**
 * This is the base class for all Dialogic Software encapsulation classes.
 * It is responsible for initializing the JNI bridge to the Dialogic SRL,
 * setting up the event notification threads and other housekeeping tasks.
 * <p>
 * It also provides implementations for common Dialogic functions that do
 * not belong in any of the functional categories (Voice, Digital, Conferencing,
 * GlobalCall) such as error reporting (ATDV_LASTERR), etc.
 */

public abstract class jvr extends JvrJni {

	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_APPEND = 02000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_CREAT = 0100;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_EXCL = 0200;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_LARGEFILE = 0100000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_NDELAY = 04000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_NOCTTY = 0400;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_NOFOLLOW = 0400000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_NONBLOCK = 04000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_SYNC = 010000;
	/** For dx_fileopen(). Imported from Linux include files. */
	public static int LINUX_O_TRUNC = 01000;
	/** For dx_fileopen(). User (file owner) has read, write and execute permission. */
	public static int LINUX_S_IRWXU = 00700;
	/** For dx_fileopen(). User has read permission. */
	public static int LINUX_S_IRUSR = 00400;
	/** For dx_fileopen(). User has write permission. */
	public static int LINUX_S_IWUSR = 00200;
	/** For dx_fileopen(). User has execute permission. */
	public static int LINUX_S_IXUSR = 00100;
	/** For dx_fileopen(). Group has read, write and execute permission. */
	public static int LINUX_S_IRWXG = 00070;
	/** For dx_fileopen(). Group has read permission. */
	public static int LINUX_S_IRGRP = 00040;
	/** For dx_fileopen(). Group has write permission. */
	public static int LINUX_S_IWGRP = 00020;
	/** For dx_fileopen(). Group has execute permission. */
	public static int LINUX_S_IXGRP = 00010;
	/** For dx_fileopen(). Others have read, write and execute permission. */
	public static int LINUX_S_IRWXO = 00007;
	/** For dx_fileopen(). Others have read permission. */
	public static int LINUX_S_IROTH = 00004;
	/** For dx_fileopen(). Others have write permisson. */
	public static int LINUX_S_IWOTH = 00002;
	/** For dx_fileopen(). Others have execute permission. */
	public static int LINUX_S_IXOTH = 00001;

	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_APPEND = 0x0008;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_BINARY = 0x8000;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_CREAT = 0x0100;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_RDONLY = 0x0000;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_RDWR = 0x0002;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_SEQUENTIAL = 0x0020;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_TEXT = 0x4000;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_TRUNC = 0x0200;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_O_WRONLY = 0x0001;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_S_IREAD = 0000400;
	/** For dx_fileopen(). Imported from c:\Program Files\Microsoft Visual Studio\VC98\Include\fcntl.h */
	public static int WIN32_S_IWRITE = 0000200;

	public static int DG_USER1_ASCII = -1;
	public static int DG_USER2_ASCII = -1;
	public static int DG_USER3_ASCII = -1;
	public static int DG_USER4_ASCII = -1;
	public static int DG_USER5_ASCII = -1;

	////////////////////////////////////////////
	// This section managed by bin/symbols.sh   
  // DO NOT EDIT MANUALLY                     
	////////////////////////////////////////////
	// public static int GCCAP_DATA_t38Fax; // not present in 5.1.1 FP1
	// public static int GCCT_FAX;
	public static int MS_ZIPDISABLE;
	public static int MS_ZIPENABLE;
	public static int MSG_ACTID;
	public static int MSG_ACTTALKNOTIFYINTERVAL;
	public static int MSG_ALGORITHM;
	public static int MSG_CLKMODE;
	public static int MSG_DBOFFTM;
	public static int MSG_DBONTM;
	public static int MSG_DISTINCTRNG;
	public static int MSG_FIRSTRINGNOTIFY;
	public static int MSG_LOUDENGTHR;
	public static int MSG_MAXFLASH;
	public static int MSG_MINFLASH;
	public static int MSG_PDRNGCAD;
	public static int MSG_RESTBL;
	public static int MSG_RING;
	public static int MSG_RNGCAD;
	public static int MSG_TONECLAMP;
	public static int MSG_UDRNGCAD;
	public static int MSG_VOLDIG;
	public static int MSG_ZIPENA;
	public static int CALLINFOTYPE;
	public static int CALLNAME;
	public static int CALLTIME;
	public static int CATEGORY_DIGIT;
	public static int CON_CAD;
	public static int CON_LPC;
	public static int CON_PAMD;
	public static int CON_PVD;
	public static int CONNECT_TYPE;
	public static int CR_BUSY;
	public static int CR_CEPT;
	public static int CR_CNCT;
	public static int CR_ERROR;
	public static int CR_FAXTONE;
	public static int CR_NOANS;
	public static int CR_NODIALTONE;
	public static int CR_NORB;
	public static int CR_STOPD;
	public static int CT_BEALAW;
	public static int CT_BEULAW;
	public static int CT_BMH100;
	public static int CT_BMPEB;
	public static int CT_BMSCBUS;
	public static int CT_DFD41D;
	public static int CT_DFD41E;
	public static int CT_DFMSI;
	public static int CT_DFSPAN;
	public static int CT_DMNETWORK;
	public static int CT_DMRESOURCE;
	public static int CT_NTANALOG;
	public static int CT_NTE1;
	public static int CT_NTIPT;
	public static int CT_NTMSI;
	public static int CT_NTNONE;
	public static int CT_NTT1;
	public static int DATA_FORMAT_ALAW;
	public static int DATA_FORMAT_DIALOGIC_ADPCM;
	public static int DATA_FORMAT_G726;
	public static int DATA_FORMAT_GSM610_MICROSOFT;
	public static int DATA_FORMAT_GSM610_TIPHON;
	public static int DATA_FORMAT_MULAW;
	public static int DATA_FORMAT_PCM;
	public static int DE_DIGITS;
	public static int DE_LCOFF;
	public static int DE_LCON;
	public static int DE_LCREV;
	public static int DE_RINGS;
	public static int DE_RNGOFF;
	public static int DE_SILOF;
	public static int DE_SILOFF;
	public static int DE_SILON;
	public static int DE_TONEOFF;
	public static int DE_TONEON;
	public static int DE_WINK;
	public static int DEEC_BPVS;
	public static int DEEC_CECS;
	public static int DEEC_DPM;
	public static int DEEC_ECS;
	public static int DEEC_FSERR;
	public static int DEEC_LOS;
	public static int DEEC_MFSERR;
	public static int DEEC_RDMA;
	public static int DEEC_RLOS;
	public static int DEEC_RRA;
	public static int DEEC_RSA1;
	public static int DEEC_RUA1;
	public static int DESTINATION_ADDRESS;
	public static int DG_DPD_ASCII;
	public static int DG_DTMF_ASCII;
	public static int DG_END;
	public static int DG_MF_ASCII;
	public static int DM_0;
	public static int DM_1;
	public static int DM_2;
	public static int DM_3;
	public static int DM_4;
	public static int DM_5;
	public static int DM_6;
	public static int DM_7;
	public static int DM_8;
	public static int DM_9;
	public static int DM_A;
	public static int DM_B;
	public static int DM_C;
	public static int DM_D;
	public static int DM_DIGITS;
	public static int DM_DIGOFF;
	public static int DM_LCOFF;
	public static int DM_LCON;
	public static int DM_LCREV;
	public static int DM_P;
	public static int DM_RINGS;
	public static int DM_RNGOFF;
	public static int DM_S;
	public static int DM_SILOF;
	public static int DM_SILON;
	public static int DM_WINK;
	public static int DRT_11KHZ;
	public static int DRT_6KHZ;
	public static int DRT_8KHZ;
	public static int DTA_ADDMSK;
	public static int DTA_SETMSK;
	public static int DTA_SUBMSK;
	public static int DTB_ABIT;
	public static int DTB_AOFF;
	public static int DTB_AON;
	public static int DTB_BBIT;
	public static int DTB_BOFF;
	public static int DTB_BON;
	public static int DTB_CBIT;
	public static int DTB_COFF;
	public static int DTB_CON;
	public static int DTB_DBIT;
	public static int DTB_DOFF;
	public static int DTB_DON;
	public static int DTEC_B8ZSD;
	public static int DTEC_BPVS;
	public static int DTEC_DPM;
	public static int DTEC_ECS;
	public static int DTEC_FERR;
	public static int DTEC_LOS;
	public static int DTEC_OOF;
	public static int DTEC_RBL;
	public static int DTEC_RCLX;
	public static int DTEC_RED;
	public static int DTEC_RLOS;
	public static int DTEC_RYEL;
	public static int DTG_E1ERREVT;
	public static int DTG_PDIGEVT;
	public static int DTG_SIGEVT;
	public static int DTG_T1ERREVT;
	public static int DTIS_DISABLE;
	public static int DTIS_ENABLE;
	public static int DTMM_AOFF;
	public static int DTMM_AON;
	public static int DTMM_BOFF;
	public static int DTMM_BON;
	public static int DTMM_COFF;
	public static int DTMM_CON;
	public static int DTMM_DOFF;
	public static int DTMM_DON;
	public static int DTMM_WINK;
	public static int DX_CALLIDDISABLE;
	public static int DX_CALLIDENABLE;
	public static int DX_CALLP;
	public static int DX_DIGMASK;
	public static int DX_DIGTYPE;
	public static int DX_IDDTIME;
	public static int DX_LCOFF;
	public static int DX_MAXDATA;
	public static int DX_MAXDTMF;
	public static int DX_MAXNOSIL;
	public static int DX_MAXSIL;
	public static int DX_MAXTIME;
	public static int DX_OFFHOOK;
	public static int DX_ONHOOK;
	public static int DX_OPTDIS;
	public static int DX_OPTEN;
	public static int DX_OPTNOCON;
	public static int DX_PAMDENABLE;
	public static int DX_PAMDOPTEN;
	public static int DX_PMOFF;
	public static int DX_PMON;
	public static int DX_PVDENABLE;
	public static int DX_PVDOPTEN;
	public static int DX_PVDOPTNOCON;
	public static int DX_TONE;
	public static int DX_TONEOFF;
	public static int DX_TONEON;
	public static int DXBD_CHNUM;
	public static int DXBD_FLASHCHR;
	public static int DXBD_FLASHTM;
	public static int DXBD_HWTYPE;
	public static int DXBD_INITDLY;
	public static int DXBD_MAXPDOFF;
	public static int DXBD_MAXSLOFF;
	public static int DXBD_MFDELAY;
	public static int DXBD_MFLKPTONE;
	public static int DXBD_MFMINON;
	public static int DXBD_MFTONE;
	public static int DXBD_MINIPD;
	public static int DXBD_MINISL;
	public static int DXBD_MINLCOFF;
	public static int DXBD_MINOFFHKTM;
	public static int DXBD_MINPDOFF;
	public static int DXBD_MINPDON;
	public static int DXBD_MINSLOFF;
	public static int DXBD_MINSLON;
	public static int DXBD_MINTIOFF;
	public static int DXBD_MINTION;
	public static int DXBD_OFFHDLY;
	public static int DXBD_P_BK;
	public static int DXBD_P_IDD;
	public static int DXBD_P_MK;
	public static int DXBD_PAUSETM;
	public static int DXBD_R_EDGE;
	public static int DXBD_R_IRD;
	public static int DXBD_R_OFF;
	public static int DXBD_R_ON;
	public static int DXBD_RXBUFSIZE;
	public static int DXBD_S_BNC;
	public static int DXBD_SYSCFG;
	public static int DXBD_T_IDD;
	public static int DXBD_TTDATA;
	public static int DXBD_TXBUFSIZE;
	public static int DXCH_ADSIACK_FSK;
	public static int DXCH_ADSIACK_LENGTH;
	public static int DXCH_ADSIALERT_ACK;
	public static int DXCH_ADSIALERT_AMPL;
	public static int DXCH_ADSIALERT_LENGTH;
	public static int DXCH_AUDIOLINEIN;
	public static int DXCH_BARGEIN_RECFORMAT;
	public static int DXCH_BARGEINONLY;
	public static int DXCH_CALLID;
	public static int DXCH_DFLAGS;
	public static int DXCH_DIGBUFMODE;
	public static int DXCH_DONOTTRIMTONES;
	public static int DXCH_DSPCARRIERDETECTDEBOUNCE;
	public static int DXCH_DSPCARRIERDETECTTHRESHOLD;
	public static int DXCH_DTINITSET;
	public static int DXCH_DTMFDEB;
	public static int DXCH_DTMFTLK;
	public static int DXCH_EC_TAP_LENGTH;
	public static int DXCH_FSKINTERBLKTIMEOUT;
	public static int DXCH_MAXRWINK;
	public static int DXCH_MFDIAG;
	public static int DXCH_MFMODE;
	public static int DXCH_MINRWINK;
	public static int DXCH_NUMRXBUFFERS;
	public static int DXCH_NUMTXBUFFERS;
	public static int DXCH_PLAYDRATE;
	public static int DXCH_RECRDRATE;
	public static int DXCH_RINGCNT;
	public static int DXCH_RXDATABUFSIZE;
	public static int DXCH_SCRDISABLED;
	public static int DXCH_SCRENABLED;
	public static int DXCH_SCRFEATURE;
	public static int DXCH_SPEECHNONPLAYTHRESH;
	public static int DXCH_SPEECHNONPLAYTRIGG;
	public static int DXCH_SPEECHNONPLAYWINDOW;
	public static int DXCH_SPEECHPLAYTHRESH;
	public static int DXCH_SPEECHPLAYTRIGG;
	public static int DXCH_SPEECHPLAYWINDOW;
	public static int DXCH_SPEECHSNR;
	public static int DXCH_SPEECHTHRESH;
	public static int DXCH_T_IDD;
	public static int DXCH_TTDATA;
	public static int DXCH_TXDATABUFSIZE;
	public static int DXCH_VADTIMEOUT;
	public static int DXCH_VARNUMBUFFERS;
	public static int DXCH_WINKDLY;
	public static int DXCH_WINKLEN;
	public static int DXCH_XFERBUFSIZE;
	public static int E_MSBADRNGSTA;
	public static int EDT_BADBRDERR;
	public static int EDT_BADCMDERR;
	public static int EDT_DATTO;
	public static int EDT_FWERR;
	public static int EDT_INVBD;
	public static int EDT_INVTS;
	public static int EDT_NOMEMERR;
	public static int EDT_PARAMERR;
	public static int EDT_RANGEERR;
	public static int EDT_SIZEERR;
	public static int EDT_SKIPRPLYERR;
	public static int EDT_SYSTEM;
	public static int EDT_TMOERR;
	public static int EV_ASYNC;
	public static int EV_STOPWTRING;
	public static int EV_SYNC;
	public static int FILE_FORMAT_VOX;
	public static int FILE_FORMAT_WAVE;
	public static int GC_ACK;
	public static int GC_ALL_LIB;
	public static int GC_ALLCALLS;
	public static int GC_ANAPILIB_SET_MAX;
	public static int GC_ANAPILIB_SET_MIN;
	public static int GC_CALL_REJECTED;
	public static int GC_CCLIB_AVAILABLE;
	public static int GC_CCLIB_CONFIGURED;
	public static int GC_CCLIB_FAILED;
	public static int GC_CCLIB_SET_SPAN;
	public static int GC_CCLIB_SET_START;
	public static int GC_CHANNEL_UNACCEPTABLE;
	public static int GC_CUSTOM1LIB_SET_MAX;
	public static int GC_CUSTOM1LIB_SET_MIN;
	public static int GC_CUSTOM2LIB_SET_MAX;
	public static int GC_CUSTOM2LIB_SET_MIN;
	public static int GC_DEST_OUT_OF_ORDER;
	public static int GC_DM3CCLIB_SET_MAX;
	public static int GC_DM3CCLIB_SET_MIN;
	public static int GC_FIRMWARE_SET_MAX;
	public static int GC_FIRMWARE_SET_MIN;
	public static int GC_GCLIB_DEF_SET_MAX;
	public static int GC_GCLIB_DEF_SET_MIN;
	public static int GC_GCLIB_GEN_SET_MAX;
	public static int GC_GCLIB_GEN_SET_MIN;
	public static int GC_GCLIB_TECH_SET_MAX;
	public static int GC_GCLIB_TECH_SET_MIN;
	public static int GC_GCLIB_USE_SET_MAX;
	public static int GC_GCLIB_USE_SET_MIN;
	public static int GC_ICAPILIB_SET_MAX;
	public static int GC_ICAPILIB_SET_MIN;
	public static int GC_IP_TECH_SET_MAX;
	public static int GC_IP_TECH_SET_MIN;
	public static int GC_IPMLIB_SET_MAX;
	public static int GC_IPMLIB_SET_MIN;
	public static int GC_ISDNLIB_SET_MAX;
	public static int GC_ISDNLIB_SET_MIN;
	public static int GC_MEDIADEVICE;
	public static int GC_NACK;
	public static int GC_NET_GCLINEDEVICE;
	public static int GC_NETWORK_CONGESTION;
	public static int GC_NETWORKDEVICE;
	public static int GC_NORMAL_CLEARING;
	public static int GC_PDKRTLIB_SET_MAX;
	public static int GC_PDKRTLIB_SET_MIN;
	public static int GC_PROTOCOL_SET_MAX;
	public static int GC_PROTOCOL_SET_MIN;
	public static int GC_R_O;
	public static int GC_REQ_CHANNEL_NOT_AVAIL;
	public static int GC_SEND_SIT;
	public static int GC_SINGLECALL;
	public static int GC_SS7LIB_SET_MAX;
	public static int GC_SS7LIB_SET_MIN;
	public static int GC_TOTAL_CCLIBS;
	public static int GC_UNASSIGNED_NUMBER;
	public static int GC_USER_BUSY;
	public static int GC_VALUE_CHAR;
	public static int GC_VALUE_INT;
	public static int GC_VALUE_LONG;
	public static int GC_VALUE_PARM_ID;
	public static int GC_VALUE_SHORT;
	public static int GC_VALUE_STRING;
	public static int GC_VALUE_UCHAR;
	public static int GC_VALUE_UINT;
	public static int GC_VALUE_ULONG;
	public static int GC_VALUE_USHORT;
	public static int GC_VOICEDEVICE;
	public static int GC_W_I;
	public static int GC_W_N;
	public static int GC_W_X;
	public static int GCABSENCE_OUT_OF_AREA;
	public static int GCABSENCE_PRIVATE;
	public static int GCACK_SERVICE_INFO;
	public static int GCACK_SERVICE_PROC;
	public static int GCADDRINFO_ENBLOC;
	public static int GCADDRINFO_OVERLAP;
	public static int GCADDRPLAN_ISDN;
	public static int GCADDRPLAN_PRIVATE;
	public static int GCADDRPLAN_TELEPHONY;
	public static int GCADDRPLAN_UNKNOWN;
	public static int GCADDRTYPE_DOMAIN;
	public static int GCADDRTYPE_EMAIL;
	public static int GCADDRTYPE_INTL;
	public static int GCADDRTYPE_IP;
	public static int GCADDRTYPE_LOC;
	public static int GCADDRTYPE_NAT;
	public static int GCADDRTYPE_TRANSPARENT;
	public static int GCADDRTYPE_URL;
	public static int GCCAP_AUDIO_g711Alaw56k;
	public static int GCCAP_AUDIO_g711Alaw64k;
	public static int GCCAP_AUDIO_g711Ulaw56k;
	public static int GCCAP_AUDIO_g711Ulaw64k;
	public static int GCCAP_AUDIO_G721ADPCM;
	public static int GCCAP_AUDIO_g722_48k;
	public static int GCCAP_AUDIO_g722_56k;
	public static int GCCAP_AUDIO_g722_64k;
	public static int GCCAP_AUDIO_g7231_5_3k;
	public static int GCCAP_AUDIO_g7231_6_3k;
	public static int GCCAP_AUDIO_g7231AnnexCCapability;
	public static int GCCAP_AUDIO_g726_16k;
	public static int GCCAP_AUDIO_g726_24k;
	public static int GCCAP_AUDIO_g726_32k;
	public static int GCCAP_AUDIO_g726_40k;
	public static int GCCAP_AUDIO_g728;
	public static int GCCAP_AUDIO_g729;
	public static int GCCAP_AUDIO_g729AnnexA;
	public static int GCCAP_AUDIO_g729AnnexAwAnnexB;
	public static int GCCAP_AUDIO_g729wAnnexB;
	public static int GCCAP_AUDIO_gsmAdaptiveMultiRate;
	public static int GCCAP_AUDIO_gsmEnhancedFullRate;
	public static int GCCAP_AUDIO_gsmFullRate;
	public static int GCCAP_AUDIO_gsmHalfRate;
	public static int GCCAP_AUDIO_is11172;
	public static int GCCAP_AUDIO_is127EnhancedVariableRate;
	public static int GCCAP_AUDIO_is13818;
	public static int GCCAP_AUDIO_nonstandard;
	public static int GCCAP_clientUserInput;
	public static int GCCAP_conference;
	public static int GCCAP_DATA_CCITTV110;
	public static int GCCAP_DATA_CCITTV120;
	public static int GCCAP_DATA_CCITTX31;
	public static int GCCAP_DATA_dsm_cc;
	public static int GCCAP_DATA_dsvdControl;
	public static int GCCAP_DATA_h222;
	public static int GCCAP_DATA_h224;
	public static int GCCAP_DATA_nlpd;
	public static int GCCAP_DATA_nonStandard;
	public static int GCCAP_DATA_t120;
	public static int GCCAP_DATA_t140;
	public static int GCCAP_DATA_t30Fax;
	public static int GCCAP_DATA_t434;
	public static int GCCAP_DATA_t84;
	public static int GCCAP_DATA_usrData;
	public static int GCCAP_dontCare;
	public static int GCCAP_h235Security;
	public static int GCCAP_muxH222;
	public static int GCCAP_muxH223;
	public static int GCCAP_muxH223AnnexA;
	public static int GCCAP_muxH2250;
	public static int GCCAP_muxNonStandard;
	public static int GCCAP_muxVgMux;
	public static int GCCAP_noChange;
	public static int GCCAP_nonStandard;
	public static int GCCAP_rxEncryption;
	public static int GCCAP_txEncryption;
	public static int GCCAP_unknown;
	public static int GCCAP_VIDEO_h261;
	public static int GCCAP_VIDEO_h262;
	public static int GCCAP_VIDEO_h263;
	public static int GCCAP_VIDEO_is11172;
	public static int GCCAP_VIDEO_nonstandard;
	public static int GCCAPRATE_100;
	public static int GCCAPRATE_110;
	public static int GCCAPRATE_1200;
	public static int GCCAPRATE_1200_75;
	public static int GCCAPRATE_12000;
	public static int GCCAPRATE_128000;
	public static int GCCAPRATE_134;
	public static int GCCAPRATE_14400;
	public static int GCCAPRATE_150;
	public static int GCCAPRATE_1536000;
	public static int GCCAPRATE_16000;
	public static int GCCAPRATE_19200;
	public static int GCCAPRATE_1920000;
	public static int GCCAPRATE_200;
	public static int GCCAPRATE_2400;
	public static int GCCAPRATE_300;
	public static int GCCAPRATE_32000;
	public static int GCCAPRATE_3600;
	public static int GCCAPRATE_384000;
	public static int GCCAPRATE_4800;
	public static int GCCAPRATE_48000;
	public static int GCCAPRATE_50;
	public static int GCCAPRATE_5300;
	public static int GCCAPRATE_56000;
	public static int GCCAPRATE_600;
	public static int GCCAPRATE_6300;
	public static int GCCAPRATE_64000;
	public static int GCCAPRATE_7200;
	public static int GCCAPRATE_75;
	public static int GCCAPRATE_75_1200;
	public static int GCCAPRATE_8000;
	public static int GCCAPRATE_9600;
	public static int GCCAPRATE_DEFAULT;
	public static int GCCAPRATE_EINI460;
	public static int GCCAPTYPE_3KHZ_AUDIO;
	public static int GCCAPTYPE_7KHZ_AUDIO;
	public static int GCCAPTYPE_AUDIO;
	public static int GCCAPTYPE_DTMF;
	public static int GCCAPTYPE_MUX;
	public static int GCCAPTYPE_RDATA;
	public static int GCCAPTYPE_UDATA;
	public static int GCCAPTYPE_UNDEFINED;
	public static int GCCAPTYPE_VIDEO;
	public static int GCCAT_COIN_BOX;
	public static int GCCAT_CPTP;
	public static int GCCAT_DATA;
	public static int GCCAT_MAINT_EQUIP;
	public static int GCCAT_MOBILE;
	public static int GCCAT_OPERATOR;
	public static int GCCAT_SPECIAL;
	public static int GCCAT_SUB_NOPRIOR;
	public static int GCCAT_SUB_PRIOR;
	public static int GCCAT_VPN;
	public static int GCCONTROL_APP;
	public static int GCCONTROL_TCCL;
	public static int GCCT_CAD;
	public static int GCCT_DISCARDED;
	public static int GCCT_FAX1;
	public static int GCCT_FAX2;
	public static int GCCT_INPROGRESS;
	public static int GCCT_NA;
	public static int GCCT_PAMD;
	public static int GCCT_PVD;
	public static int GCCT_UNKNOWN;
	public static int GCEV_ACCEPT;
	public static int GCEV_ACKCALL;
	public static int GCEV_ALARM;
	public static int GCEV_ALERTING;
	public static int GCEV_ANSWERED;
	public static int GCEV_ATTACH;
	public static int GCEV_ATTACH_FAIL;
	public static int GCEV_BLINDTRANSFER;
	public static int GCEV_BLOCKED;
	public static int GCEV_CALLINFO;
	public static int GCEV_CALLPROC;
	public static int GCEV_CALLPROGRESS;
	public static int GCEV_CALLSTATUS;
	public static int GCEV_COMPLETETRANSFER;
	public static int GCEV_CONGESTION;
	public static int GCEV_CONNECTED;
	public static int GCEV_D_CHAN_STATUS;
	public static int GCEV_DETACH;
	public static int GCEV_DETACH_FAIL;
	public static int GCEV_DETECTED;
	public static int GCEV_DIALING;
	public static int GCEV_DIALTONE;
	public static int GCEV_DISCONNECTED;
	public static int GCEV_DIVERTED;
	public static int GCEV_DROPCALL;
	public static int GCEV_ERROR;
	public static int GCEV_EXTENSION;
	public static int GCEV_FACILITY;
	public static int GCEV_FACILITY_ACK;
	public static int GCEV_FACILITY_REJ;
	public static int GCEV_FACILITYREQ;
	public static int GCEV_FATALERROR;
	public static int GCEV_GETCONFIGDATA;
	public static int GCEV_GETCONFIGDATA_FAIL;
	public static int GCEV_HOLDACK;
	public static int GCEV_HOLDCALL;
	public static int GCEV_HOLDREJ;
	public static int GCEV_ISDNMSG;
	public static int GCEV_L2BFFRFULL;
	public static int GCEV_L2FRAME;
	public static int GCEV_L2NOBFFR;
	public static int GCEV_LISTEN;
	public static int GCEV_MEDIA_ACCEPT;
	public static int GCEV_MEDIA_REJ;
	public static int GCEV_MEDIA_REQ;
	public static int GCEV_MEDIADETECTED;
	public static int GCEV_MOREDIGITS;
	public static int GCEV_MOREINFO;
	public static int GCEV_NODYNMEM;
	public static int GCEV_NOFACILITYBUF;
	public static int GCEV_NOTIFY;
	public static int GCEV_NOUSRINFOBUF;
	public static int GCEV_NSI;
	public static int GCEV_OFFERED;
	public static int GCEV_OPENEX;
	public static int GCEV_OPENEX_FAIL;
	public static int GCEV_PROCEEDING;
	public static int GCEV_PROGRESSING;
	public static int GCEV_RELEASECALL;
	public static int GCEV_RELEASECALL_FAIL;
	public static int GCEV_REQANI;
	public static int GCEV_REQMOREINFO;
	public static int GCEV_RESETLINEDEV;
	public static int GCEV_RESTARTFAIL;
	public static int GCEV_RETRIEVEACK;
	public static int GCEV_RETRIEVECALL;
	public static int GCEV_RETRIEVEREJ;
	public static int GCEV_SENDMOREINFO;
	public static int GCEV_SERVICEREQ;
	public static int GCEV_SERVICERESP;
	public static int GCEV_SERVICERESPCMPLT;
	public static int GCEV_SETBILLING;
	public static int GCEV_SETCHANSTATE;
	public static int GCEV_SETCONFIGDATA;
	public static int GCEV_SETCONFIGDATA_FAIL;
	public static int GCEV_SETUP_ACK;
	public static int GCEV_SETUPTRANSFER;
	public static int GCEV_STOPMEDIA_REQ;
	public static int GCEV_SWAPHOLD;
	public static int GCEV_TASKFAIL;
	public static int GCEV_TRANSFERACK;
	public static int GCEV_TRANSFERREJ;
	public static int GCEV_TRANSIT;
	public static int GCEV_UNBLOCKED;
	public static int GCEV_UNLISTEN;
	public static int GCEV_USRINFO;
	public static int GCLS_INSERVICE;
	public static int GCLS_MAINTENANCE;
	public static int GCLS_OUT_OF_SERVICE;
	public static int GCME_GC_EVENT;
	public static int GCMEDSEL_MEDIUM_EXCL;
	public static int GCMEDSEL_MEDIUM_PREF;
	public static int GCMKCALLBLK_DEFAULT;
	public static int GCPARM_1ST_CRN;
	public static int GCPARM_2ND_CRN;
	public static int GCPARM_ADDR_DATA;
	public static int GCPARM_ADDR_PLAN;
	public static int GCPARM_ADDR_TYPE;
	public static int GCPARM_ADDRESS;
	public static int GCPARM_BOARD_LDID;
	public static int GCPARM_CALL_ADDR_INFO;
	public static int GCPARM_CALL_CATEGORY;
	public static int GCPARM_CALLACK;
	public static int GCPARM_CALLPROC;
	public static int GCPARM_CALLSTATE;
	public static int GCPARM_CAPABILITY;
	public static int GCPARM_CCLIB_ID;
	public static int GCPARM_CCLIB_NAME;
	public static int GCPARM_CHAN_MEDIA_ID;
	public static int GCPARM_CHAN_MEDIA_SEL;
	public static int GCPARM_DATE;
	public static int GCPARM_DEVICENAME;
	public static int GCPARM_GENERIC;
	public static int GCPARM_GET_MSK;
	public static int GCPARM_INFO;
	public static int GCPARM_IP_NETIF;
	public static int GCPARM_MAX_INFO;
	public static int GCPARM_MIN_INFO;
	public static int GCPARM_NAME;
	public static int GCPARM_NAME_ABSENCE;
	public static int GCPARM_NETWORKH;
	public static int GCPARM_NUMBER_ABSENCE;
	public static int GCPARM_PROTOCOL_ID;
	public static int GCPARM_PROTOCOL_NAME;
	public static int GCPARM_RATE;
	public static int GCPARM_SUBADDR_DATA;
	public static int GCPARM_SUBADDR_PLAN;
	public static int GCPARM_SUBADDR_TYPE;
	public static int GCPARM_TIME;
	public static int GCPARM_TYPE;
	public static int GCPARM_VOICEH;
	public static int GCPARM_VOICENAME;
	public static int GCPR_MEDIADETECT;
	public static int GCPV_DISABLE;
	public static int GCPV_ENABLE;
	public static int GCR_CHARGE;
	public static int GCR_NOCHARGE;
	public static int GCRV_ALARM;
	public static int GCRV_ATTACH_ERROR;
	public static int GCRV_B8ZSD;
	public static int GCRV_B8ZSDOK;
	public static int GCRV_BPVS;
	public static int GCRV_BPVSOK;
	public static int GCRV_BUSY;
	public static int GCRV_CCLIBSPECIFIC;
	public static int GCRV_CECS;
	public static int GCRV_CECSOK;
	public static int GCRV_CEPT;
	public static int GCRV_CONGESTION;
	public static int GCRV_CPERROR;
	public static int GCRV_DESTINATION_ADDRESS_REQ;
	public static int GCRV_DIALTONE;
	public static int GCRV_DPM;
	public static int GCRV_DPMOK;
	public static int GCRV_ECS;
	public static int GCRV_ECSOK;
	public static int GCRV_FATALERROR_ACTIVE;
	public static int GCRV_FATALERROR_OCCURRED;
	public static int GCRV_FERR;
	public static int GCRV_FERROK;
	public static int GCRV_FSERR;
	public static int GCRV_FSERROK;
	public static int GCRV_GLARE;
	public static int GCRV_INFO_NONE_NOMORE;
	public static int GCRV_INFO_NONE_TIMEOUT;
	public static int GCRV_INFO_PRESENT_ALL;
	public static int GCRV_INFO_PRESENT_MORE;
	public static int GCRV_INFO_SENT;
	public static int GCRV_INFO_SOME_NOMORE;
	public static int GCRV_INFO_SOME_TIMEOUT;
	public static int GCRV_INTERNAL;
	public static int GCRV_LOS;
	public static int GCRV_LOSOK;
	public static int GCRV_MEDIA_OPEN_FAILED;
	public static int GCRV_MEDIA_REQ_CANCELLED;
	public static int GCRV_MFSERR;
	public static int GCRV_MFSERROK;
	public static int GCRV_NETWORK_OPEN_FAILED;
	public static int GCRV_NOANSWER;
	public static int GCRV_NODYNMEM;
	public static int GCRV_NONRECOVERABLE_FATALERROR;
	public static int GCRV_NORB;
	public static int GCRV_NORMAL;
	public static int GCRV_NOT_INSERVICE;
	public static int GCRV_NOVOICE;
	public static int GCRV_OOF;
	public static int GCRV_OOFOK;
	public static int GCRV_ORIGINATION_ADDRESS_REQ;
	public static int GCRV_PROTOCOL;
	public static int GCRV_RBL;
	public static int GCRV_RBLOK;
	public static int GCRV_RCL;
	public static int GCRV_RCLOK;
	public static int GCRV_RDMA;
	public static int GCRV_RDMAOK;
	public static int GCRV_RECOVERABLE_FATALERROR;
	public static int GCRV_RED;
	public static int GCRV_REDOK;
	public static int GCRV_REJECT;
	public static int GCRV_RESETABLE_FATALERROR;
	public static int GCRV_RESULT;
	public static int GCRV_RLOS;
	public static int GCRV_RLOSOK;
	public static int GCRV_RRA;
	public static int GCRV_RRAOK;
	public static int GCRV_RSA1;
	public static int GCRV_RSA1OK;
	public static int GCRV_RUA1;
	public static int GCRV_RUA1OK;
	public static int GCRV_RYEL;
	public static int GCRV_RYELOK;
	public static int GCRV_SIGNALLING;
	public static int GCRV_STOPD;
	public static int GCRV_TIMEOUT;
	public static int GCRV_UNALLOCATED;
	public static int GCSET_CALL_BLK;
	public static int GCSET_CALL_CONFIG;
	public static int GCSET_CALLER_ID;
	public static int GCSET_CALLEVENT_MSK;
	public static int GCSET_CALLINFO;
	public static int GCSET_CALLSTATE_MSK;
	public static int GCSET_CCLIB_INFO;
	public static int GCSET_CHAN_BLK;
	public static int GCSET_CHAN_CAPABILITY;
	public static int GCSET_CRN_INDEX;
	public static int GCSET_DEST_ADDR;
	public static int GCSET_DEVICEINFO;
	public static int GCSET_GENERIC;
	public static int GCSET_NETIF_NUM;
	public static int GCSET_ORIG_ADDR;
	public static int GCSET_PARM;
	public static int GCSET_PROTOCOL;
	public static int GCSET_SERVREQ;
	public static int GCST_ACCEPTED;
	public static int GCST_ALERTING;
	public static int GCST_CALLROUTING;
	public static int GCST_CONNECTED;
	public static int GCST_DETECTED;
	public static int GCST_DIALING;
	public static int GCST_DIALTONE;
	public static int GCST_DISCONNECTED;
	public static int GCST_GETMOREINFO;
	public static int GCST_IDLE;
	public static int GCST_NULL;
	public static int GCST_OFFERED;
	public static int GCST_ONHOLD;
	public static int GCST_ONHOLDPENDINGTRANSFER;
	public static int GCST_PROCEEDING;
	public static int GCST_SENDMOREINFO;
	public static int GCSUBADDR_IA5;
	public static int GCSUBADDR_OSI;
	public static int GCSUBADDR_UNKNOWN;
	public static int GCSUBADDR_USER;
	public static int GCTGT_CCLIB_CHAN;
	public static int GCTGT_CCLIB_CRN;
	public static int GCTGT_CCLIB_NETIF;
	public static int GCTGT_CCLIB_SYSTEM;
	public static int GCTGT_FIRMWARE_CHAN;
	public static int GCTGT_FIRMWARE_NETIF;
	public static int GCTGT_GCLIB_CHAN;
	public static int GCTGT_GCLIB_CRN;
	public static int GCTGT_GCLIB_NETIF;
	public static int GCTGT_GCLIB_SYSTEM;
	public static int GCTGT_PROTOCOL_CHAN;
	public static int GCTGT_PROTOCOL_NETIF;
	public static int GCTGT_PROTOCOL_SYSTEM;
	public static int GCUPDATE_ATNULL;
	public static int GCUPDATE_IMMEDIATE;
	public static int IO_CONT;
	public static int IO_DEV;
	public static int IO_EOT;
	public static int IO_LINK;
	public static int IO_MEM;
	public static int IO_UIO;
	public static int IO_USEOFFSET;
	public static int JVR_ALL;
	public static int JVR_ERROR;
	public static int JVR_INFO;
	public static int JVR_NONE;
	public static int JVR_WARNING;
	public static int MAXLEN_IEDATA;
	public static int MD_ADPCM;
	public static int MD_GAIN;
	public static int MD_NOGAIN;
	public static int MD_PCM;
	public static int MEDIA_TYPE_DETECT;
	public static int MSCA_ND;
	public static int MSCA_NN;
	public static int MSCA_NULL;
	public static int MSEV_ERREVT;
	public static int MSEV_NORING;
	public static int MSEV_RING;
	public static int MSEV_SIGEVT;
	public static int MSEV_SIGMSK;
	public static int MSMM_HOOKFLASH;
	public static int MSMM_OFFHOOK;
	public static int MSMM_ONHOOK;
	public static int MSMM_RNGOFFHK;
	public static int MSMM_RNGSTOP;
	public static int MSMM_TERM;
	public static int MSPA_COACH;
	public static int MSPA_NOAGC;
	public static int MSPA_NULL;
	public static int MSPA_PUPIL;
	public static int MSPA_RO;
	public static int MSPA_TARIFF;
	public static int MSPN_STATION;
	public static int MSPN_TS;
	public static int MTF_RING;
	public static int NO_CALL_PROGRESS;
	public static int ORIGINATION_ADDRESS;
	public static int PAMD_ACCU;
	public static int PAMD_FULL;
	public static int PAMD_QUAL1TMP;
	public static int PAMD_QUAL2TMP;
	public static int PAMD_QUICK;
	public static int PM_ADSI;
	public static int PM_ADSIALERT;
	public static int PM_ALAW;
	public static int PM_SR6;
	public static int PM_SR8;
	public static int PM_TONE;
	public static int PRESENT_RESTRICT;
	public static int RM_ALAW;
	public static int RM_SR6;
	public static int RM_SR8;
	public static int RM_TONE;
	public static int SEEK_CUR;
	public static int SEEK_END;
	public static int SEEK_SET;
	public static int TDX_BARGEIN;
	public static int TDX_CALLP;
	public static int TDX_CST;
	public static int TDX_DIAL;
	public static int TDX_ERROR;
	public static int TDX_GETDIG;
	public static int TDX_GETR2MF;
	public static int TDX_NOSTOP;
	public static int TDX_PLAY;
	public static int TDX_PLAYTONE;
	public static int TDX_RECORD;
	public static int TDX_RXDATA;
	public static int TDX_SETHOOK;
	public static int TDX_TXDATA;
	public static int TDX_UNKNOWN;
	public static int TDX_WINK;
	public static int TF_10MS;
	public static int TF_CLRBEG;
	public static int TF_CLREND;
	public static int TF_DIGMASK;
	public static int TF_DIGTYPE;
	public static int TF_EDGE;
	public static int TF_FIRST;
	public static int TF_IDDTIME;
	public static int TF_LCOFF;
	public static int TF_LEVEL;
	public static int TF_MAXDATA;
	public static int TF_MAXDTMF;
	public static int TF_MAXNOSIL;
	public static int TF_MAXSIL;
	public static int TF_MAXTIME;
	public static int TF_PMON;
	public static int TF_SETINIT;
	public static int TF_TONE;
	public static int TF_USE;
	public static int TID_BUSY1;
	public static int TID_BUSY2;
	public static int TID_DIAL_INTL;
	public static int TID_DIAL_LCL;
	public static int TID_DIAL_XTRA;
	public static int TID_DISCONNECT;
	public static int TID_FAX1;
	public static int TID_FAX2;
	public static int TID_RNGBK1;
	public static int TID_RNGBK2;
	public static int TM_DIGIT;
	public static int TM_EOD;
	public static int TM_ERROR;
	public static int TM_IDDTIME;
	public static int TM_LCOFF;
	public static int TM_MAXDTMF;
	public static int TM_MAXNOSIL;
	public static int TM_MAXSIL;
	public static int TM_MAXTIME;
	public static int TM_NORMTERM;
	public static int TM_PATTERN;
	public static int TM_TONE;
	public static int TM_USRSTOP;
	public static int U_IES;
	////////////////////////////////////////////
	// END OF bin/symbols.sh managed sectionh   
	////////////////////////////////////////////

	/**
	 * List of registered event listeners.
	 */
	protected static final ArrayList listeners = new ArrayList();
	protected static final ArrayList addListeners = new ArrayList();
	protected static final ArrayList removeListeners = new ArrayList();
	protected static boolean iteratingOnListeners = false;

	/**
	 * List of unhandled events.
	 */
	protected static final ArrayList eventQueue = new ArrayList();

	/**
	 * A private thread is responsible for dispatching event to registered
	 * listeners once the Event object has completed initialization.
	 */
	private static JVREventDispatcher eventDispatcher = null;

	/**
	 * This function returns a map of symbol name and value pairs.  It is called
	 * automatically during static class initialization.  Since all of the 
	 * Dialogic API classes (dx,dt,sr,...) subclass <b>jvr</b>, the importation
	 * of symbol values is guaranteed to happen automatically the first time it
	 * is required.
	 * <p>
	 * This method is called automatically during static initialization of the <b>jvr</b> class.
	 */
	private static native HashMap importSymbols() throws JVRException;

	/**
	 * Initializes the event model.
	 * <p>
	 * This method is called automatically during static initialization of the <b>jvr</b> class.
	 */
	private static native void initEventModel() throws JVRException;

	/**
	 * <b>Win32 Only</b>.  Called by native code when the SRL has notified 
	 * the native event handler that an event has occurred.
	 */
	private static final void handleEvent (long eventHandle) throws JVRException {
		handleEvent(gc.GetMetaEventEx(eventHandle));
	}

	/**
	 * <b>Linux Only</b>.  Called by native code when the SRL has notified 
	 * the native event handler that an event has occurred.
	 */
	private static final void handleEvent () throws JVRException {
		handleEvent(gc.GetMetaEvent());
	}

	/**
	 * Called when a new event is created by any class and appens the 
	 * provided event to the list of events that are being handled.
	 */
	public static final void handleEvent (JVREvent event) {
		synchronized (eventQueue) { 
			eventQueue.add(event); 
			eventQueue.notifyAll();
		}
	}

	/**
	 * Called by the <b>handleEvent()</b> functions once platform dependent 
	 * event handling conditions have been handled.
	 */
	public static final void handleEvent (MetaEvent event) throws JVRException {
		// Queue the event for handling.  This method runs from the same thread
		// as the native signal handler, so we do not want to tie it up
		// running the actual handlers.
		synchronized (eventQueue) { 
			eventQueue.add(event); 
			eventQueue.notifyAll();
		}
	}

// 	/**
// 	 * Adds a {@link JVREventListener} to the event notification queue.  Listeners are called
// 	 * in the order they were registered in (first registered, first called).  When
// 	 * a listener handles an event and returns true, the next listener (if any) is
// 	 * called.  If the listener returns false, no further handlers are called.
// 	 * @deprecated Use MetaEventListener objects now.
// 	 */
// 	public static void addEventListener (JVREventListener e) {
// 		throw new RuntimeException("deprecated.");
// 	}
// 
// 	/**
// 	 * Removes a {@link JVREventListener} from the event notification queue.
// 	 * @deprecated Use MetaEventListener objects now.
// 	 */
// 	public static void removeEventListener (JVREventListener e) {
// 		throw new RuntimeException("deprecated.");
// 	}

	/**
	 *
	 */
	public static void addEventListener (MetaEventListener e) {
		if (e == null) { throw new AssertionError("Supplied listener was null"); }
		synchronized (listeners) {
			if (iteratingOnListeners) {
				addListeners.add(e);
				return;
			}
			if (! listeners.contains(e)) {
				listeners.add(e);
			}
		}
	}

	/**
	 *
	 */
	public static void removeEventListener (MetaEventListener e) {
		// if (e == null) { throw new AssertionError("Supplied listener was null"); }
		if (e == null) { return; }
		synchronized (listeners) {
			if (iteratingOnListeners) {
				removeListeners.add(e);
				return;
			}
			if (listeners.contains(e)) {
				listeners.remove(listeners.indexOf(e));
			}
			else { logger.info("WARNING: not in listener list: " + e); }
		}
	}

	//
	// Static initialization of the jvr class.
	//
	// It appears that this initializer is run everytime the jvr class passes out of scope.
	// I would have assumed that it would still only be run once.
	//
	static {
		// Start the EventDispatcher now so that it is guaranteed to be ready before the
		// first event can even be created.
		eventDispatcher = new JVREventDispatcher();
		eventDispatcher.setName("JVR Event Dispatcher");
		eventDispatcher.setDaemon(true); // so the JVR can exit even if this thread is running.
		eventDispatcher.start();
		try {
			//
			// Import symbols.
			//
			Map symbols = importSymbols(); 
			if (symbols == null) {
				throw new RuntimeException("importSymbols() returned null; no symbols imported");
			}
			for (Iterator i = symbols.keySet().iterator(); i.hasNext(); ) {
				String key = (String) i.next();
				Object val = symbols.get(key);
				try {
					Field f = jvr.class.getField(key);
					Class type = f.getType();
					if (type.getName().equals("short")) { f.setShort(jvr.class, ((Integer) val).shortValue()); }
					else if (type.getName().equals("char")) { f.setChar(jvr.class,(char) ((Integer) val).intValue()); }
					else if (type.getName().equals("int")) { f.setInt(jvr.class,((Integer) val).intValue()); }
					else if (type.getName().equals("byte")) { f.setByte(jvr.class,((Integer) val).byteValue()); }
					else if (type.getName().equals("java.lang.String")) { f.set(null,val); }
					else {
						AssertionError e = new AssertionError("Unsupported type '"+type.getName()+"' for key '"+key+"'"); 
						logger.throwing(jvr.class.getName(),"<init>",e);
						throw(e);
					}
				}
				catch (NoSuchFieldException e) {
					RuntimeException e2 = new RuntimeException("Error on key '"+key+"'",e); 
					logger.throwing(jvr.class.getName(),"<init>",e2);
					throw(e2);
				}
				catch (IllegalAccessException e) {
					RuntimeException e2 = new RuntimeException("Error on key '"+key+"'",e); 
					logger.throwing(jvr.class.getName(),"<init>",e2);
					throw(e2);
				}
			}
			//
			// Setup the event programming model.
			//
			initEventModel();
		}
		catch (JVRException e) {
			RuntimeException e2 = new RuntimeException(e); 
			logger.throwing(jvr.class.getName(),"<init>",e2);
			throw(e2);
		}
		// TODO: remove this when the null-pointer errors have gone away.
		/*
		Thread noiseMaker = new Thread() {
			public void run() {
				while (true) {
					try {
						logNoise();
						sleep(1000);
					}
					catch (Throwable ignore) {}
				}
			}
		};
		noiseMaker.setDaemon(true);
		noiseMaker.start();
		*/
	}

	/**
	 * A do nothing method that is here so that jvr is callable.
	 * This is a hack to make sure that the jvr class is initialized
	 * before any classes that extend JvrJni run in native code.
	 * A better solution is needed.
	 */
	public static void foo() {}

	/**
	 *
	 */
	public static native void setDebugLevel (int level);

	/**
	 *
	 */
	public static native int getDebugLevel();

	/**
	 *
	 */
	public static String symbolName (String prefix, long v) {
		int value = (int) v;
		ArrayList l = new ArrayList();
		Field[] fields = jvr.class.getDeclaredFields();
		try {
			for (int x = 0; x < fields.length; x++) {
				if (fields[x].getName().startsWith(prefix)) {
					if (fields[x].getInt(jvr.class) == value) {
						l.add(fields[x].getName());
					}
				}
			}
			if (l.size() == 0) {
				return ""+value;
			}
			else if (l.size() == 1) {
				return (String) l.get(0);
			}
			else {
				// WARN that there were multiple matches.
				String names = "["+l.get(0);
				for (int x = 1; x < l.size(); x++) {
					names += ":"+l.get(x);
				}
				names += "]";
				return names;
			}
		}
		catch (IllegalAccessException ignore) { logger.info(""+ignore); }
		return ""+value;
	}

	/**
	 *
	 */
	public static native void logNoise();

}
