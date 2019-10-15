package com.ahasbini.tools.androidopencv.functional.plugin;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class PluginTest extends BaseFunctionalTest {

    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app and cpp code
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib and cpp code

    private final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Test
    public void testMissingAndroidPluginWithPluginsDsl() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testMissingAndroidPluginWithPluginsDsl",
                getTestProjectDir().getRoot());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withPluginClasspath(getPluginClassPath())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS")
                .buildAndFail();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                messages.getString("missing_android_gradle_plugin"))));
    }

    @Test
    public void testMissingAndroidPluginWithApplyPlugin() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testMissingAndroidPluginWithApplyPlugin",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS")
                .buildAndFail();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                messages.getString("missing_android_gradle_plugin"))));
    }

    @Test
    public void testMissingAndroidOpenCVVersion() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testMissingAndroidOpenCVVersion",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS")
                .withGradleVersion("4.1")
                .buildAndFail();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                messages.getString("missing_opencv_version"))));
    }

    @Test
    public void testEmptyAndroidOpenCVVersion() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testEmptyAndroidOpenCVVersion",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS")
                .withGradleVersion("4.1")
                .buildAndFail();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                messages.getString("missing_opencv_version"))));
    }

    @Test
    public void testSuccessfulProjectConfigurationWithDryRun() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath(
                "/PluginTest_testSuccessfulProjectConfigurationWithDryRun",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace", "-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
                .withGradleVersion("4.1")
                .build();

        // TODO: 13-Oct-19 ahasbini: assert the results 
    }

    @Test
    public void testSuccessfulConsecutiveBuildsWithDryRun() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath(
                "/PluginTest_testSuccessfulConsecutiveBuildsWithDryRun",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace", "-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
                .withGradleVersion("4.1")
                .build();

        result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace", "-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
                .withGradleVersion("4.1")
                .build();

        // TODO: 13-Oct-19 ahasbini: assert
    }

    @Test
    public void testSuccessfulBuildWithCustomUrlWithDryRun() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath(
                "/PluginTest_testSuccessfulBuildWithCustomUrlWithDryRun",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace", "-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
                .withGradleVersion("4.1")
                .build();

        // TODO: 13-Oct-19 ahasbini: assert
    }

    @Test
    public void testSuccessfulBuild() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testSuccessfulBuild",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace", "-PENABLE_ANDROID_OPENCV_LOGS", ":assembleDebug")
                .withGradleVersion("5.2.1")
                .buildAndFail();

        // TODO: 13-Oct-19 ahasbini: assert
    }
}
