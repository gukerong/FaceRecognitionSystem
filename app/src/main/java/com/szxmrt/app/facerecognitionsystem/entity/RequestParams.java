/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.szxmrt.app.facerecognitionsystem.entity;

import java.io.File;
import java.util.Map;

public interface RequestParams {

    Map<String, File> getFileParams();
    Map<String, String> getStringParams();
}
