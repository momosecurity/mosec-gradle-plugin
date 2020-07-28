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

import com.immomo.momosec.gradle.plugins.stubs.MyConfiguration
import com.immomo.momosec.gradle.plugins.stubs.MyResolvedDependency
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.logging.Logger
import org.junit.Assert
import org.junit.Test


import static org.mockito.Mockito.*

class TestProjectDependencyCollector {

    private final Project project = mock(Project.class)

    @Test
    void collectTest() {
        def spyLog = spy(mock(Logger.class))
        def confNameFilter = /.*/
        def spyCollector = spy(new ProjectDependencyCollector(project, null, confNameFilter, true))
        def confContainer = mock(ConfigurationContainer.class)

        // for spy log
        doNothing().when(spyLog).info(anyString())
        doNothing().when(spyLog).debug(anyString())
        doNothing().when(spyLog).warn(anyString())
        doNothing().when(spyLog).error(anyString())

        // for project.configuration.findAll
        def spyConf = spy(new MyConfiguration())
        doReturn([spyConf].toSet()).when(confContainer).findAll(any(Closure.class))

        // for mosecConf.resolvedConfiguration.firstLevelModuleDependencies
        def resolvedConf = mock(ResolvedConfiguration.class)
        doReturn(resolvedConf).when(spyConf).getResolvedConfiguration()

        def parent = new MyResolvedDependency('com.study.parent', 'parent', '1.0.0')
        parent.setChildren(new MyResolvedDependency('com.study.child', 'child', '1.0.0'))
        doReturn(parent.getChildren()).when(resolvedConf).getFirstLevelModuleDependencies()

        // for spy project
        doReturn('com.study.parent:parent').when(project).getName()
        doReturn('1.0.0').when(project).getVersion()
        doReturn(spyLog).when(project).getLogger()
        doReturn([project].toSet()).when(project).getAllprojects()
        doReturn(confContainer).when(project).getConfigurations()

        def depsTree = spyCollector.collect()

        verify(spyCollector, times(1)).collect()
        verify(project, times(1)).getAllprojects()
        verify(project, atLeastOnce()).getConfigurations()
        verify(spyConf, times(1)).getResolvedConfiguration()
        verify(resolvedConf, times(1)).getFirstLevelModuleDependencies()
        Assert.assertEquals('com.study.parent:parent', depsTree.get('name'))
        Assert.assertEquals('1.0.0', depsTree.get('version'))
        Assert.assertEquals(2,
                (((depsTree.get('dependencies') as Map)
                    .get('com.study.child:child') as Map)
                        .get('from') as List)
                            .size())
    }

    @Test
    void depsToDictTest() {
        def parent = new MyResolvedDependency('com.study.parent', 'parent', '1.0.0')
        def child = new MyResolvedDependency('com.study.child', 'child', '1.0.0')
        def child_child = new MyResolvedDependency('com.study.child_child', 'child_child', '1.0.0')

        parent.setChildren(child)
        child.setChildren(child_child)

        child_child.setParent(child)
        child.setParent(parent)

        def collector = new ProjectDependencyCollector(project, null, null, true)
        def chain = ['com.study.parent:parent@1.0.0']

        def childOnlyProvenanceDepsTree = collector.depsToDict.call(parent.getChildren(), chain as ArrayList, true)
        def expectChildOnlyProvenanceTree = [
            'com.study.child:child': [
                'name': 'com.study.child:child',
                'version': '1.0.0',
                'from': ['com.study.parent:parent@1.0.0', 'com.study.child:child@1.0.0'],
                'dependencies': [:]
            ]
        ]
        Assert.assertEquals(expectChildOnlyProvenanceTree, childOnlyProvenanceDepsTree)

        def childNotOnlyProvenanceDepsTree = collector.depsToDict.call(parent.getChildren(), chain as ArrayList, false)
        def expectChildNotOnlyProvenanceTree = [
            'com.study.child:child': [
                'name': 'com.study.child:child',
                'version': '1.0.0',
                'from': ['com.study.parent:parent@1.0.0', 'com.study.child:child@1.0.0'],
                'dependencies': [
                    'com.study.child_child:child_child': [
                        'name': 'com.study.child_child:child_child',
                        'version': '1.0.0',
                        'from': ['com.study.parent:parent@1.0.0', 'com.study.child:child@1.0.0', 'com.study.child_child:child_child@1.0.0'],
                        'dependencies': [:]
                    ]
                ]
            ]
        ]
        Assert.assertEquals(expectChildNotOnlyProvenanceTree, childNotOnlyProvenanceDepsTree)
    }

    @Test
    void simplifyDepsTest() {
        def depsTree = [
            'name': 'com.study.parent:parent',
            'version': '1.0.0',
            'from': ['com.study.parent:parent@1.0.0'],
            'dependencies': [
                'com.study.child1:child1': [
                    'name': 'com.study.child1:child1',
                    'version': '1.0.0',
                    'from': ['com.study.parent:parent@1.0.0', 'com.study.child1:child1@1.0.0'],
                    'dependencies': [:]
                ],
                'com.study.child2:child2': [
                    'name': 'com.study.child2:child2',
                    'version': '1.0.0',
                    'from': ['com.study.parent:parent@1.0.0', 'com.study.child2:child2@1.0.0'],
                    'dependencies': [
                        'com.study.child1:child1': [
                            'name': 'com.study.child1:child1',
                            'version': '1.0.0',
                            'from': ['com.study.parent:parent@1.0.0', 'com.study.child2:child2@1.0.0', 'com.study.child1:child1@1.0.0'],
                            'dependencies': [:]
                        ]
                    ]
                ]
            ]
        ]

        def expectDepsTree = [
            'name': 'com.study.parent:parent',
            'version': '1.0.0',
            'from': ['com.study.parent:parent@1.0.0'],
            'dependencies': [
                'com.study.child1:child1': [
                    'name': 'com.study.child1:child1',
                    'version': '1.0.0',
                    'from': ['com.study.parent:parent@1.0.0', 'com.study.child1:child1@1.0.0'],
                    'dependencies': [:]
                ],
                'com.study.child2:child2': [
                    'name': 'com.study.child2:child2',
                    'version': '1.0.0',
                    'from': ['com.study.parent:parent@1.0.0', 'com.study.child2:child2@1.0.0'],
                    'dependencies': [:]
                ]
            ]
        ]

        def collector = new ProjectDependencyCollector(project, null, null, true)
        collector.simplifyDeps.call(depsTree)

        Assert.assertEquals(expectDepsTree, depsTree)
    }

    @Test
    void matchesAttributeFilter() {
        def match
        def conf = new MyConfiguration()    // has usage:java-runtime attributes

        String matchAttrSpec = "usage:java-runtime"
        def collector = new ProjectDependencyCollector(project, matchAttrSpec.split(',').collect{ it.split(':') }, null, true)
        match = collector.matchesAttributeFilter.call(conf)
        Assert.assertTrue(match)

        String notMatchAttrSpec = "usage:imnotexist"
        def collectorWithWrongAttrSpec = new ProjectDependencyCollector(project, notMatchAttrSpec.split(',').collect{ it.split(':') }, null, true)
        match = collectorWithWrongAttrSpec.matchesAttributeFilter.call(conf)
        Assert.assertFalse(match)
    }

}
