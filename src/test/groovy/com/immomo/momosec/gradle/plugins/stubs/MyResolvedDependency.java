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
