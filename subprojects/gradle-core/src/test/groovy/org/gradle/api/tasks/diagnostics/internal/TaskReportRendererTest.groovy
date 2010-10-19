/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks.diagnostics.internal

import org.gradle.api.Rule
import org.gradle.logging.internal.TestStyledTextOutput
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

/**
 * @author Hans Dockter
 */
class TaskReportRendererTest {
    private final TestStyledTextOutput writer = new TestStyledTextOutput().ignoreStyle()
    private final TaskReportRenderer renderer = new TaskReportRenderer()

    @Before
    public void setup() {
        renderer.output = writer
    }

    @Test public void testWritesTaskAndDependenciesWithNoDetail() {
        TaskDetails task1 = [getPath: {':task1'}, getDescription: {'task1Description'}, getDependencies: {[':task11', ':task12'] as LinkedHashSet}] as TaskDetails
        TaskDetails task2 = [getPath: {':task2'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        TaskDetails task3 = [getPath: {':task3'}, getDescription: {null}, getDependencies: {[':task1'] as Set}] as TaskDetails
        Rule rule1 = [getDescription: {'rule1Description'}] as Rule
        Rule rule2 = [getDescription: {'rule2Description'}] as Rule

        List testDefaultTasks = ['task1', 'task2']
        renderer.showDetail(false)
        renderer.addDefaultTasks(testDefaultTasks)
        renderer.startTaskGroup('group')
        renderer.addTask(task1)
        renderer.addChildTask(task2)
        renderer.addTask(task3)
        renderer.completeTasks()
        renderer.addRule(rule1)
        renderer.addRule(rule2)

        def expected = '''Default tasks: task1, task2

Group tasks
-----------
:task1 - task1Description
:task3

Rules
-----
rule1Description
rule2Description
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testWritesTaskAndDependenciesWithDetail() {
        TaskDetails task11 = [getPath: {':task11'}] as TaskDetails
        TaskDetails task12 = [getPath: {':task12'}] as TaskDetails
        TaskDetails task1 = [getPath: {':task1'}, getDescription: {'task1Description'}, getDependencies: {[task11, task12] as Set}] as TaskDetails
        TaskDetails task2 = [getPath: {':task2'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        TaskDetails task3 = [getPath: {':task3'}, getDescription: {null}, getDependencies: {[task1] as Set}] as TaskDetails
        Rule rule1 = [getDescription: {'rule1Description'}] as Rule
        Rule rule2 = [getDescription: {'rule2Description'}] as Rule

        List testDefaultTasks = ['task1', 'task2']
        renderer.showDetail(true)
        renderer.addDefaultTasks(testDefaultTasks)
        renderer.startTaskGroup('group')
        renderer.addTask(task1)
        renderer.addChildTask(task2)
        renderer.addTask(task3)
        renderer.completeTasks()
        renderer.addRule(rule1)
        renderer.addRule(rule2)

        def expected = '''Default tasks: task1, task2

Group tasks
-----------
:task1 - task1Description [:task11, :task12]
    :task2
:task3 [:task1]

Rules
-----
rule1Description
rule2Description
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testWritesTasksForSingleGroup() {
        TaskDetails task = [getPath: {':task1'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        renderer.addDefaultTasks([])
        renderer.startTaskGroup('group')
        renderer.addTask(task)
        renderer.completeTasks()

        def expected = '''Group tasks
-----------
:task1
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testWritesTasksForMultipleGroups() {
        TaskDetails task = [getPath: {':task1'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        TaskDetails task2 = [getPath: {':task2'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        renderer.addDefaultTasks([])
        renderer.startTaskGroup('group')
        renderer.addTask(task)
        renderer.startTaskGroup('other')
        renderer.addTask(task2)
        renderer.completeTasks()

        def expected = '''Group tasks
-----------
:task1

Other tasks
-----------
:task2
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testWritesTasksForDefaultGroup() {
        TaskDetails task = [getPath: {':task1'}, getDescription: {null}, getDependencies: {[] as Set}] as TaskDetails
        renderer.addDefaultTasks([])
        renderer.startTaskGroup('')
        renderer.addTask(task)
        renderer.completeTasks()

        def expected = '''Tasks
-----
:task1
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testProjectWithNoTasksAndNoRules() {
        renderer.completeTasks()

        def expected = '''No tasks
'''
        assertEquals(expected, writer.getValue())
    }

    @Test public void testProjectWithRulesAndNoTasks() {
        String ruleDescription = "someDescription"

        renderer.completeTasks()
        renderer.addRule([getDescription: {ruleDescription}] as Rule)

        def expected = '''No tasks

Rules
-----
someDescription
'''
        assertEquals(expected, writer.getValue())
    }
}
