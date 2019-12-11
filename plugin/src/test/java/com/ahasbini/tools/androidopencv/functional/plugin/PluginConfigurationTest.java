package com.ahasbini.tools.androidopencv.functional.plugin;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class PluginConfigurationTest extends BaseFunctionalTest {

    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app and cpp code
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib and cpp code

    // TODO: 03-Nov-19 ahasbini: find a way to remove withGradleVersion

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
    public void testSuccessfulConsecutiveBuildsWithDryRun() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath(
                "/PluginTest_testSuccessfulConsecutiveBuildsWithDryRun",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
                .withGradleVersion("4.1")
                .build();

        result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", "-m", ":assemble")
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
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":assembleDebug")
                .withGradleVersion("5.2.1")
                .build();

        // TODO: 13-Oct-19 ahasbini: assert
        // TODO: 02-Nov-19 ahasbini: assert that java sources have been compiled
        // TODO: 02-Nov-19 ahasbini: assert that opencv has been downloaded and extracted
        // TODO: 02-Nov-19 ahasbini: assert that native libs have been coped in project dir
        // TODO: 02-Nov-19 ahasbini: assert that project has compiled successfully and generated outputs
    }

    @Test
    public void testTasksOutcome() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testTasksOutcome",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":tasks")
                .withGradleVersion("5.2.1")
                .build();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex("" +
                "AndroidOpenCV tasks\r\n" +
                "-------------------\r\n" +
                "cleanAndroidOpenCVBuildCache - Cleans AndroidOpenCV build-cache folder in user home directory.\r\n" +
                "cleanAndroidOpenCVBuildFolder - Cleans AndroidOpenCV folder in project build directory.\r\n" +
                "setupAndroidOpenCV - Configures and installs AndroidOpenCV dependencies for project.")));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":tasks").getOutcome());
    }

    @Test
    public void testCleanAndroidOpenCVBuildCacheTaskHelpOutcome()
            throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testCleanAndroidOpenCVBuildCacheTaskHelpOutcome",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":help", "--task", "cleanAndroidOpenCVBuildCache")
                .withGradleVersion("5.2.1")
                .build();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex("" +
                "Detailed task information for cleanAndroidOpenCVBuildCache\r\n" +
                "\r\n" +
                "Path\r\n" +
                "     :cleanAndroidOpenCVBuildCache\r\n" +
                "\r\n" +
                "Type\r\n" +
                "     CleanAndroidOpenCVBuildCacheTask (com.ahasbini.tools.androidopencv.task.CleanAndroidOpenCVBuildCacheTask)\r\n" +
                "\r\n" +
                "Options\r\n" +
                "     --all     Cleans all versions\r\n" +
                "\r\n" +
                "     --version     Cleans the specified version instead of the version defined in build.gradle androidOpenCV block\r\n" +
                "\r\n" +
                "Description\r\n" +
                "     Cleans AndroidOpenCV build-cache folder in user home directory.\r\n" +
                "\r\n" +
                "Group\r\n" +
                "     AndroidOpenCV")));
        //noinspection ConstantConditions
        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":help").getOutcome());
    }

    @Test
    public void testSuccessfulConsecutiveBuilds() throws IOException, URISyntaxException {

        // SETUP
        writeFolderContentsFromClasspath("/PluginTest_testSuccessfulConsecutiveBuilds",
                getTestProjectDir().getRoot());
        injectBuildScriptClassPath(new File(getTestProjectDir().getRoot(), "build.gradle"),
                getPluginClassPath());

        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":assembleDebug")
                .withGradleVersion("5.2.1")
                .build();

        result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("-PENABLE_ANDROID_OPENCV_LOGS", ":assembleDebug")
                .withGradleVersion("5.2.1")
                .build();

        // TODO: 13-Oct-19 ahasbini: assert
        // TODO: 02-Nov-19 ahasbini: assert that java sources have been compiled
        // TODO: 02-Nov-19 ahasbini: assert that opencv has been downloaded and extracted
        // TODO: 02-Nov-19 ahasbini: assert that native libs have been coped in project dir
        // TODO: 02-Nov-19 ahasbini: assert that project has compiled successfully and generated outputs
    }
}
