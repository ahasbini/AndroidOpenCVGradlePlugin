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

    private File settingsFile;
    private File buildFile;

    @Before
    public void setup() throws IOException {
        settingsFile = getTestProjectDir().newFile("settings.gradle");
        buildFile = getTestProjectDir().newFile("build.gradle");
    }

    @Test
    public void testHelloWorldTask() throws IOException {
        writeFile(settingsFile, "rootProject.name = 'hello-world'");
        String buildFileContent = "task helloWorld {" +
                "    doLast {" +
                "        println 'Hello world!'" +
                "    }" +
                "}";
        writeFile(buildFile, buildFileContent);

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("helloWorld")
                .build();

        Assert.assertTrue(result.getOutput().contains("Hello world!"));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld").getOutcome());
    }
}
