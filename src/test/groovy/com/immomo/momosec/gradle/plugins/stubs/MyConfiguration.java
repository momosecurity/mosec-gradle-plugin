package com.immomo.momosec.gradle.plugins.stubs;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.artifacts.*;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.attributes.DefaultImmutableAttributesFactory;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskDependency;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyConfiguration implements Configuration {
    @Override
    public ResolutionStrategy getResolutionStrategy() {
        return null;
    }

    @Override
    public Configuration resolutionStrategy(Closure closure) {
        return null;
    }

    @Override
    public Configuration resolutionStrategy(Action<? super ResolutionStrategy> action) {
        return null;
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public Configuration setVisible(boolean b) {
        return null;
    }

    @Override
    public Set<Configuration> getExtendsFrom() {
        return null;
    }

    @Override
    public Configuration setExtendsFrom(Iterable<Configuration> iterable) {
        return null;
    }

    @Override
    public Configuration extendsFrom(Configuration... configurations) {
        return null;
    }

    @Override
    public boolean isTransitive() {
        return false;
    }

    @Override
    public Configuration setTransitive(boolean b) {
        return null;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Configuration setDescription(@Nullable String s) {
        return null;
    }

    @Override
    public Set<Configuration> getHierarchy() {
        return null;
    }

    @Override
    public Set<File> resolve() {
        return null;
    }

    @Override
    public Set<File> files(Closure closure) {
        return null;
    }

    @Override
    public Set<File> files(Spec<? super Dependency> spec) {
        return null;
    }

    @Override
    public Set<File> files(Dependency... dependencies) {
        return null;
    }

    @Override
    public FileCollection fileCollection(Spec<? super Dependency> spec) {
        return null;
    }

    @Override
    public FileCollection fileCollection(Closure closure) {
        return null;
    }

    @Override
    public FileCollection fileCollection(Dependency... dependencies) {
        return null;
    }

    @Override
    public ResolvedConfiguration getResolvedConfiguration() {
        return null;
    }

    @Override
    public String getUploadTaskName() {
        return null;
    }

    @Override
    public TaskDependency getBuildDependencies() {
        return null;
    }

    @Override
    public TaskDependency getTaskDependencyFromProjectDependency(boolean b, String s) {
        return null;
    }

    @Override
    public DependencySet getDependencies() {
        return null;
    }

    @Override
    public DependencySet getAllDependencies() {
        return null;
    }

    @Override
    public DependencyConstraintSet getDependencyConstraints() {
        return null;
    }

    @Override
    public DependencyConstraintSet getAllDependencyConstraints() {
        return null;
    }

    @Override
    public PublishArtifactSet getArtifacts() {
        return null;
    }

    @Override
    public PublishArtifactSet getAllArtifacts() {
        return null;
    }

    @Override
    public Set<ExcludeRule> getExcludeRules() {
        return null;
    }

    @Override
    public Configuration exclude(Map<String, String> map) {
        return null;
    }

    @Override
    public Configuration defaultDependencies(Action<? super DependencySet> action) {
        return null;
    }

    @Override
    public Configuration withDependencies(Action<? super DependencySet> action) {
        return null;
    }

    @Override
    public Set<Configuration> getAll() {
        return null;
    }

    @Override
    public ResolvableDependencies getIncoming() {
        return null;
    }

    @Override
    public ConfigurationPublications getOutgoing() {
        return null;
    }

    @Override
    public void outgoing(Action<? super ConfigurationPublications> action) {

    }

    @Override
    public Configuration copy() {
        return null;
    }

    @Override
    public Configuration copyRecursive() {
        return null;
    }

    @Override
    public Configuration copy(Spec<? super Dependency> spec) {
        return null;
    }

    @Override
    public Configuration copyRecursive(Spec<? super Dependency> spec) {
        return null;
    }

    @Override
    public Configuration copy(Closure closure) {
        return null;
    }

    @Override
    public Configuration copyRecursive(Closure closure) {
        return null;
    }

    @Override
    public void setCanBeConsumed(boolean b) {

    }

    @Override
    public boolean isCanBeConsumed() {
        return false;
    }

    @Override
    public void setCanBeResolved(boolean b) {

    }

    @Override
    public boolean isCanBeResolved() {
        return false;
    }

    @Override
    public Configuration attributes(Action<? super AttributeContainer> action) {
        return null;
    }

    @Override
    public AttributeContainer getAttributes() {
        DefaultImmutableAttributesFactory attributesFactory = new DefaultImmutableAttributesFactory(null, null);

        AttributeContainer attrContainer = attributesFactory.mutable();
        attrContainer.attribute(Attribute.of("usage", String.class), Usage.JAVA_RUNTIME);
        return attrContainer;
    }

    @Override
    public File getSingleFile() throws IllegalStateException {
        return null;
    }

    @Override
    public Set<File> getFiles() {
        return null;
    }

    @Override
    public boolean contains(File file) {
        return false;
    }

    @Override
    public String getAsPath() {
        return null;
    }

    @Override
    public FileCollection plus(FileCollection fileCollection) {
        return null;
    }

    @Override
    public FileCollection minus(FileCollection fileCollection) {
        return null;
    }

    @Override
    public FileCollection filter(Closure closure) {
        return null;
    }

    @Override
    public FileCollection filter(Spec<? super File> spec) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FileTree getAsFileTree() {
        return null;
    }

    @Override
    public void addToAntBuilder(Object o, String s, AntType antType) {

    }

    @Override
    public Object addToAntBuilder(Object o, String s) {
        return null;
    }

    @Override
    public Iterator<File> iterator() {
        return null;
    }
}
