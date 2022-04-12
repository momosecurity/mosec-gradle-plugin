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
package com.immomo.momosec.gradle.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.immomo.momosec.gradle.plugins.exceptions.NetworkErrorException
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.immomo.momosec.gradle.plugins.Renderer.writeToFile

class Mosec implements Plugin<Project> {

    /**
     * https://developer.android.com/studio/build/dependencies#variant_aware
     * -PconfAttr=buildtype:debug,usage:java-runtime
     */
    private List confAttrSpec = null

    /**
     * 过滤configuration
     * -Pconfiguration=confNameRegex  or  -Pconfiguration=spcificConfName
     */
    private String confNameFilter = ".*"

    /**
     * 威胁等级 [High|Medium|Low]
     */
    private String severityLevel = 'High'
    private def allowSeverityLevel = ['High', 'Medium', 'Low']

    /**
     * 仅检查直接依赖
     */
    private Boolean onlyProvenance = false

    /**
     * 发现漏洞即编译失败
     */
    private Boolean failOnVuln = true

    /**
     * 项目类型 [Maven|Android]
     * 决定了使用的检索漏洞库
     */
    private String projectType = 'Android'
    private def allowProjectType = ['Maven', 'Android']

    /**
     * 上报API
     */
    private String endpoint

    /**
     * 仅分析不上报
     */
    private Boolean onlyAnalyze = false

    /**
     * 依赖输出到文件
     */
    private String outputDepToFile

    @Override
    void apply(Project project) {
        project.task('mosec').doLast {
            getArguments(project)

            ProjectDependencyCollector collector = new ProjectDependencyCollector(project, confAttrSpec, confNameFilter, onlyProvenance)
            Map depsTree = collector.collect()

            if (depsTree == null) { return }

            if (onlyAnalyze) {
                if (outputDepToFile != null && outputDepToFile != "") {
                    writeToFile(outputDepToFile, new GsonBuilder().setPrettyPrinting().create().toJson(depsTree))
                }
                return
            }

            depsTree['type'] = projectType
            depsTree['language'] = Constants.PROJECT_LANGUAGE
            depsTree['severityLevel'] = severityLevel

            HttpPost request = new HttpPost(endpoint)
            request.addHeader("Content-Type", Constants.CONTENT_TYPE_JSON)
            HttpEntity entity = new StringEntity(new GsonBuilder().create().toJson(depsTree))
            request.setEntity(entity)

            HttpClientHelper httpClientHelper = new HttpClientHelper()
            HttpClient client = httpClientHelper.buildHttpClient()
            HttpResponse response = client.execute(request)

            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new NetworkErrorException(response.getStatusLine().getReasonPhrase())
            }
            InputStream resContent = response.getEntity().getContent()
            JsonParser parser = new JsonParser()
            JsonObject responseJson
            try {
                responseJson = parser.parse(new BufferedReader(new InputStreamReader(resContent))).getAsJsonObject()
                GsonBuilder gsonBuilder = new GsonBuilder()
                gsonBuilder.registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(), new MapDeserializerDoubleAsIntFix())
                Gson gson = gsonBuilder.create()
                depsTree['result'] = gson.fromJson(responseJson, new TypeToken<Map <String, Object>>(){}.getType())
            } catch (Exception ignored) {
                throw new NetworkErrorException(Constants.ERROR_ON_API)
            }

            if (outputDepToFile != null && outputDepToFile != "") {
                writeToFile(outputDepToFile, new GsonBuilder().setPrettyPrinting().create().toJson(depsTree))
            }

            Renderer renderer = new Renderer(project.logger, failOnVuln)
            renderer.renderResponse(responseJson)
        }
    }

    void getArguments(Project project) {
        if (project.hasProperty('projectType')) {
            projectType = project.property('projectType').toString()

            if (!allowProjectType.contains(projectType)) {
                throw new Exception(Constants.ERROR_ON_PROJECT_TYPE)
            }
        }

        if (project.hasProperty('onlyAnalyze')) {
            onlyAnalyze = new Boolean(project.property('onlyAnalyze').toString())
        }

        if (project.hasProperty('endpoint')) {
            endpoint = project.property('endpoint').toString()
        }

        def endpoint_env = System.getenv(Constants.MOSEC_ENDPOINT_ENV)
        if (endpoint_env != null) {
            endpoint = endpoint_env
        }

        if (!onlyAnalyze && endpoint == null) {
            throw new RuntimeException(Constants.ERROR_ON_NULL_ENDPOINT)
        }

        if (project.hasProperty('outputDepToFile')) {
            outputDepToFile = project.property('outputDepToFile').toString()
        }

        if (project.hasProperty('confAttr')) {
            confAttrSpec = project.property('confAttr').toString().toLowerCase().split(',').collect { it.split(':') }
        }

        if (project.hasProperty('configuration')) {
            confNameFilter = String.format("%s", project.property('configuration').toString().toLowerCase())
        }

        if (project.hasProperty('severityLevel')) {
            severityLevel = project.property('severityLevel')

            if (!allowSeverityLevel.contains(severityLevel)) {
                throw new Exception(Constants.ERROR_ON_SEVERITY_LEVEL)
            }
        }

        if (project.hasProperty('onlyProvenance')) {
            onlyProvenance = new Boolean(project.property('onlyProvenance').toString())
        }

        if (project.hasProperty('failOnVuln')) {
            failOnVuln = new Boolean(project.property('failOnVuln').toString())
        }
    }
}
