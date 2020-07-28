package com.immomo.momosec.gradle.plugins

import groovy.json.JsonOutput
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.attributes.Attribute

class ProjectDependencyCollector {

    private final Project project
    private final List confAttrSpec
    private final String confNameFilter
    private final Boolean onlyProvenance

    private final MosecLogHelper logHelper = new MosecLogHelper()

    private final def mosecMergedDepsConf = 'mosecMergedDepsConf'

    ProjectDependencyCollector(Project project, List confAttrSpec, String confNameFilter, Boolean onlyProvenance) {
        this.project = project
        this.confAttrSpec = confAttrSpec
        this.confNameFilter = confNameFilter
        this.onlyProvenance = onlyProvenance
    }

    def collect() {
        def projName = project.name
        def projVersion = project.version
        def logger = project.logger

        logger.info logHelper.strongInfo('MOSEC: ') + 'task is executing via doLast on ' + projName

        def depsTree = [
            'name': projName,
            'version': projVersion,
            'from': [projName + '@' + projVersion],
            'dependencies': [:],
        ]

        Map<Attribute<?>, Set<?>> allConfigurationAttributes = new HashMap<>()
        Map<String, Set<String>> attributesAsStrings = new HashMap<>()
        project.allprojects.each { proj ->
            proj.configurations.findAll({
                it.name != mosecMergedDepsConf &&
                it.name =~ confNameFilter &&
                matchesAttributeFilter.call(it)
            }).each { conf ->
                if (!conf.hasProperty('attributes')) {
                    // Gradle before version 3 does not support attributes
                    return
                }
                def attrs = conf.attributes
                attrs.keySet().each({ attr ->
                    def value = attrs.getAttribute(attr as Attribute<Object>)
                    if (!allConfigurationAttributes.containsKey(attr)) {
                        allConfigurationAttributes[attr] = new HashSet()
                        attributesAsStrings[attr.name] = new HashSet()
                    }
                    allConfigurationAttributes[attr].add(value)
                    attributesAsStrings[attr.name].add(value.toString())
                })
            }
        }

        logger.debug 'MOSEC: JSON Attr ' + JsonOutput.toJson(attributesAsStrings)

        def mosecConf = null
        def mergeableConfs = project.configurations.findAll({it.name != mosecMergedDepsConf && it.name =~ confNameFilter})

        if (confAttrSpec != null) {
            mergeableConfs = mergeableConfs.findAll(matchesAttributeFilter)
        }

        if (mergeableConfs.size() == 0 && project.configurations.size() > 0) {
            throw new RuntimeException('MOSEC: Matching configurations not found: ' + confNameFilter +
                    ', availabie configurations for project ' + project + ': ' +
                    project.configurations.collect({ it.name }))
        } else if (mergeableConfs.size() == 1) {
            mosecConf = mergeableConfs.first()
        } else if (mergeableConfs.size() > 1) {
            logger.info logHelper.strongInfo('MOSEC: ') + 'constructing merged configuration from ' +  mergeableConfs.collect({conf -> conf.name})
            mosecConf = project.configurations.create(mosecMergedDepsConf)

            mergeableConfs.each { mosecConf.extendsFrom(it) }

            if (mosecConf.hasProperty('attributes')) {
                allConfigurationAttributes.each({ attr, valueSet ->
                    if (valueSet.size() == 1) {
                        mosecConf.attributes.attribute(attr, valueSet.head())
                    }
                })
            }
        }

        if (mosecConf != null) {
            logger.info logHelper.strongInfo('MOSEC: ') + 'resolving configuration ' + mosecConf.name

            Set<ResolvedDependency> gradleDeps = mosecConf.resolvedConfiguration.firstLevelModuleDependencies

            logger.debug 'MOSEC: converting dependency graph to DepTree'

            ArrayList<String> from = new ArrayList<String>(){{ add(projName + '@' + projVersion) }}
            depsTree['dependencies'] = depsToDict.call(gradleDeps, from, onlyProvenance)
            simplifyDeps.call(depsTree)

            logger.debug 'MOSEC: depsTree ' + JsonOutput.prettyPrint(JsonOutput.toJson(depsTree))
            return depsTree
        } else {
            logger.error('MOSEC: no configuration found.')
        }
    }

    def depsToDict = { Set<ResolvedDependency> deps, ArrayList<String> currentChain, Boolean onlyProvenance ->
        def res = [:]
        deps.each { d ->
            def depName = d.moduleGroup + ':' + d.moduleName
            def depNameVersion = depName + '@' + d.moduleVersion

            if (!currentChain.contains(depNameVersion)) {
                currentChain.add(depNameVersion)
                def row = ['name': depName, 'version': d.moduleVersion, 'from': currentChain.clone()]
                def subDeps = [:]
                if (!onlyProvenance) {
                    subDeps = depsToDict.call(d.children, currentChain, onlyProvenance)
                }
                currentChain.remove(depNameVersion)
                if (subDeps.size() > 0) {
                    row['dependencies'] = subDeps
                } else {
                    row['dependencies'] = [:]
                }
                res[depName] = row
            }
        }
        return res
    }

    def simplifyDeps = { deps ->
        def q = [] as Queue
        def s = new HashSet<String>()

        q.add(deps)

        def elem = [:]
        while ( (elem = q.poll()) != null) {
            def removeNames = []
            elem['dependencies'].each { name, d ->
                def depFull = d['name'] + '@' + d['version']
                if (s.contains(depFull)) {
                    removeNames.add(name)
                    return
                }
                s.add(depFull as String)
                q.add(d)
            }

            removeNames.each {name ->
                elem['dependencies'].remove(name)
            }
        }
    }

    def matchesAttributeFilter = {Configuration conf ->
        if (!conf.hasProperty('attributes')) {
            // Gradle before version 3 does not support attributes
            return true
        }
        def matches = true
        def attrs = conf.attributes
        attrs.keySet().each({ attr ->
            def attrValueAsString = attrs.getAttribute(attr as Attribute<Object>).toString().toLowerCase()
            for(String[] keyValueFilter : confAttrSpec) {
                if (attr.name.toLowerCase().contains(keyValueFilter[0])
                        && attrValueAsString != keyValueFilter[1]) {
                    matches = false
                }
            }
        })
        return matches
    }
}
