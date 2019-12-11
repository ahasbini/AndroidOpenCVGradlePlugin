package com.ahasbini.tools.androidopencv.functional.android;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by ahasbini on 26-Nov-19.
 */
public class AndroidPluginTest extends BaseFunctionalTest {

    @Test
    public void testPreBuildBeingFirstTask() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/AndroidPluginTest_testPreBuildBeingFirstTask",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":assemble")
                .withGradleVersion("4.1")
                .build();

        Assert.assertEquals(0, result.getTasks().indexOf(result.task(":preBuild")));
    }

}
