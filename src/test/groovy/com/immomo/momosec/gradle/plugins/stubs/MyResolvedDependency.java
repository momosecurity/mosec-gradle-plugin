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
package com.immomo.momosec.gradle.plugins.stubs;

import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.artifacts.ResolvedModuleVersion;

import java.util.HashSet;
import java.util.Set;

public class MyResolvedDependency implements ResolvedDependency {
    private String group;
    private String name;
    private String version;
    private Set<ResolvedDependency> children = new HashSet<>();
    private Set<ResolvedDependency> parent = new HashSet<>();

    public MyResolvedDependency(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getModuleGroup() {
        return group;
    }

    @Override
    public String getModuleName() {
        return name;
    }

    @Override
    public String getModuleVersion() {
        return version;
    }

    @Override
    public String getConfiguration() {
        return null;
    }

    @Override
    public ResolvedModuleVersion getModule() {
        return null;
    }

    @Override
    public Set<ResolvedDependency> getChildren() {
        return children;
    }

    public void setChildren(ResolvedDependency dependency) {
        this.children.add(dependency);
    }

    @Override
    public Set<ResolvedDependency> getParents() {
        return parent;
    }

    public void setParent(ResolvedDependency dependency) {
        this.parent.add(dependency);
    }

    @Override
    public Set<ResolvedArtifact> getModuleArtifacts() {
        return null;
    }

    @Override
    public Set<ResolvedArtifact> getAllModuleArtifacts() {
        return null;
    }

    @Override
    public Set<ResolvedArtifact> getParentArtifacts(ResolvedDependency resolvedDependency) {
        return null;
    }

    @Override
    public Set<ResolvedArtifact> getArtifacts(ResolvedDependency resolvedDependency) {
        return null;
    }

    @Override
    public Set<ResolvedArtifact> getAllArtifacts(ResolvedDependency resolvedDependency) {
        return null;
    }
}
