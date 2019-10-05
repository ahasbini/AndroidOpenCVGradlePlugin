package com.ahasbini.tools.androidopencv.functional;

import com.ahasbini.tools.androidopencv.BaseTest;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

}
