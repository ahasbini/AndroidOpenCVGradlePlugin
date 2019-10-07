package com.ahasbini.tools.androidopencv.functional.gradle;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class SimpleGradleTest extends BaseFunctionalTest {

    @Before
    public void setup() throws IOException {
        File settingsFile = getTestProjectDir().newFile("settings.gradle");
        writeFileFromClasspath("/SimpleGradleTest_testHelloWorldTask_settings.gradle",
                settingsFile);

        File buildFile = getTestProjectDir().newFile("build.gradle");
        writeFileFromClasspath("/SimpleGradleTest_testHelloWorldTask_build.gradle",
                buildFile);
    }

    @Test
    public void testHelloWorldTask() {
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("helloWorld")
                .build();

        Assert.assertTrue(result.getOutput().contains("Hello world!"));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld").getOutcome());
    }
}
