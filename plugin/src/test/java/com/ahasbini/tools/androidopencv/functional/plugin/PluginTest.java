package com.ahasbini.tools.androidopencv.functional.plugin;

import com.ahasbini.tools.androidopencv.functional.BaseFunctionalTest;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class PluginTest extends BaseFunctionalTest {

    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and app and cpp code
    // TODO: 06-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin and lib and cpp code
    // TODO: 09-Oct-19 ahasbini: Implement test for applying plugin with android gradle plugin but without opencv version

    private final ResourceBundle resource = ResourceBundle.getBundle("messages");

    @Test
    public void testMissingAndroidPlugin() throws IOException {

        // SETUP
        File settingsFile = getTestProjectDir().newFile("settings.gradle");
        writeFileFromClasspath("/PluginTest_testMissingAndroidPlugin_settings.gradle",
                settingsFile);

        File buildFile = getTestProjectDir().newFile("build.gradle");
        writeFileFromClasspath("/PluginTest_testMissingAndroidPlugin_build.gradle",
                buildFile);

        // TEST
        BuildResult result = getGradleRunnerBuilder()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("helloWorld")
                .withPluginClasspath()
                .buildAndFail();

        Assert.assertTrue(result.getOutput().matches(buildOutputRegex(
                resource.getString("missing_android_plugin"))));
        Assert.assertNull(result.task(":helloWorld"));
    }
}
