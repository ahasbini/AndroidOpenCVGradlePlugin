package com.ahasbini.tools.androidopencv.functional.gradle;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class SimpleGradleTest extends BaseFunctionalTest {

    @Test
    public void testHelloWorldTask() throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath("/SimpleGradleTest_testHelloWorldTask",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments("helloWorld")
                .build();

        Assert.assertTrue(result.getOutput().contains("Hello world!"));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld").getOutcome());
    }

    @Test
    public void testHelloWorldTaskWithEmptyProject() throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath("/SimpleGradleTest_testHelloWorldTaskWithEmptyProject",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments("helloWorld")
                .build();

        Assert.assertTrue(result.getOutput().contains("Hello world!"));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld").getOutcome());
    }

    @Test
    public void testListTasksOfEmptyProject() throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath("/SimpleGradleTest_testListTasksOfEmptyProject",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments(":empty-project:tasks", "--all")
                .build();

        Assert.assertTrue(result.getOutput().contains("Tasks runnable from project :empty-project"));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":empty-project:tasks").getOutcome());
    }

    @Test
    public void testModifyDependenciesInAfterEvaluate() throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath(
                "/SimpleGradleTest_testModifyDependenciesInAfterEvaluate",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments(":dependencies")
                .build();


        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                "implementation - Implementation only dependencies for source set 'main'. (n)\r\n" +
                        "\\--- junit:junit:4.12 (n)")));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome());
    }

    @Test
    public void testAddNonExistingDependencyInAfterEvaluate()
            throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath(
                "/SimpleGradleTest_testAddNonExistingDependencyInAfterEvaluate",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments(":dependencies")
                .withGradleVersion("4.1")
                .build();


        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                "implementation - Implementation only dependencies for source set 'main'. (n)\r\n" +
                        "\\--- non-existing-jar-0.0.1 (n)")));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome());
    }

    @Test
    public void testAddJarDependencyWithMovingInAfterEvaluate()
            throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath(
                "/SimpleGradleTest_testAddJarDependencyWithMovingInAfterEvaluate",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments(":dependencies")
                .withGradleVersion("4.1")
                .build();


        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                "implementation - Implementation only dependencies for source set 'main'. (n)\r\n" +
                        "\\--- existing-after-move-jar:0.0.1 (n)")));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies").getOutcome());
    }

    @Test
    public void testAddJarDependencyWithMovingInAfterEvaluateWithCompilation()
            throws IOException, URISyntaxException {
        // SETUP

        writeFolderContentsFromClasspath(
                "/SimpleGradleTest_testAddJarDependencyWithMovingInAfterEvaluateWithCompilation",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(getTestProjectDir().getRoot())
                .withArguments(":assemble")
                .withGradleVersion("4.1")
                .build();


        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":assemble").getOutcome());
    }
}
