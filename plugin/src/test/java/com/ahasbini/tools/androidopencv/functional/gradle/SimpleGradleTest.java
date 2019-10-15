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
}
