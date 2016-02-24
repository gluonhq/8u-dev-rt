/*
 * Copyright (c) 2012, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#ifndef DALVIKUTILS_H
#define	DALVIKUTILS_H

#include "dalvikConst.h"

#ifdef	__cplusplus
extern "C" {
#endif     

#define CHECK_EXCEPTION(env) \
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {                                 \
        LOGE("Detected outstanding Java exception at %s:%s:%d\n", \
                __FUNCTION__, __FILE__, __LINE__);                                 \
        (*env)->ExceptionDescribe(env);                                            \
        (*env)->ExceptionClear(env);                                               \
    };    

int to_jfx_touch_action(int state);
char *describe_surface_format(int format);
char *describe_touch_action(int state);

#ifdef	__cplusplus
}
#endif

#endif	/* DALVIKUTILS_H */
