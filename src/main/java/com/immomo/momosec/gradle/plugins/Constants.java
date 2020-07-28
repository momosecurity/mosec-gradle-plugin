/*
 * Copyright 2020 momosecurity.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.immomo.momosec.gradle.plugins;

public class Constants {

    public static final String ERROR_ON_PROJECT_TYPE = "Project type not allow. Use [Maven | Android].";

    public static final String ERROR_ON_VULNERABLE = "Dependency Vulnerable Found!";

    public static final String ERROR_ON_API = "API return data format error.";

    public static final String ERROR_ON_NULL_ENDPOINT = "API endpoint not setting. Setting by MOSEC_ENDPOINT env.";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String PROJECT_LANGUAGE = "java";

    public static final String MOSEC_ENDPOINT_ENV = "MOSEC_ENDPOINT";
}
