/*
 * $Id: common.h,v 1.21 2004/12/16 01:38:15 kevino Exp $
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

// All of the dialogic H files assume MFC/Borland calling and return conventions.
#ifdef __GNUC__
#define __cdecl
#endif

// System
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <time.h>
#ifdef LINUX
#include <sys/errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#endif
// Linux/Win32 differences
#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif
// Dialogic
#include <srllib.h>
#include <dxxxlib.h>
#include <dtilib.h>
#include <sctools.h>
#include <msilib.h>
#include <dxdigit.h>
#include <gclib.h>  
#include <gcerr.h>  
#include <pdk_public.h>
#include <cclib.h>
#include <gcisdn.h>
#include <dcblib.h>

// Java
#include <jni.h>
// Miscellaneous
#define JVR_THROW_JVR e,"net/threebit/jvr/JVRException"     ,__FILE__,__LINE__
#define JVR_THROW_DG  e,"net/threebit/jvr/DialogicException",__FILE__,__LINE__
// JVR
#include "jvr.h"
#include "jnifields.h"
