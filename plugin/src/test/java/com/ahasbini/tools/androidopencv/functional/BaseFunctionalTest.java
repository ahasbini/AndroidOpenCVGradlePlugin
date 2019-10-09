package com.ahasbini.tools.androidopencv.functional;

import com.ahasbini.tools.androidopencv.BaseTest;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class BaseFunctionalTest extends BaseTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private GradleRunner gradleRunnerBuilder = GradleRunner.create().withDebug(true).forwardOutput();

    protected TemporaryFolder getTestProjectDir() {
        return testProjectDir;
    }

    protected GradleRunner getGradleRunnerBuilder() {
        return gradleRunnerBuilder;
    }

    protected void writeFile(File destination, String content) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(destination))) {
            output.write(content);
        }
    }

    protected void writeFileFromClasspath(String classpath, File file) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(classpath);
        Files.copy(resourceAsStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    protected String buildOutputRegex(String output) {
        return "(?s).*" + output.replace("?", "\\?")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("\n", "\n.*") + ".*";
    }

}
