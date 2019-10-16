package com.ahasbini.tools.androidopencv.functional;

import com.ahasbini.tools.androidopencv.BaseTest;

import org.gradle.internal.impldep.aQute.lib.strings.Strings;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahasbini on 05-Oct-19.
 */
public class BaseFunctionalTest extends BaseTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    private GradleRunner gradleRunnerBuilder = GradleRunner.create()
            .withDebug(true).forwardOutput();

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

    protected void writeFolderContentsFromClasspath(String classpath, File directory)
            throws URISyntaxException, IOException {
        URL resource = getClass().getResource(classpath);
        File classpathDir = new File(resource.toURI());
        File[] files = classpathDir.listFiles();
        if (files != null) {
            for (File file : files) {
                recursiveCopy(file, new File(directory, file.getName()));
            }
        }
    }

    private void recursiveCopy(File src, File dst) throws IOException {
        Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if (src.isDirectory()) {
            File[] files = src.listFiles();
            if (files != null) {
                for (File file : files) {
                    recursiveCopy(file, new File(dst, file.getName()));
                }
            }
        }
    }

    protected String buildOutputRegex(String output) {
        return "(?s).*" + output.replace("\\", "\\\\")
                .replace("?", "\\?")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("\n", "\n.*") + ".*";
    }

    protected List<File> getPluginClassPath() throws URISyntaxException, IOException {
        URL pluginClasspathResource = BaseFunctionalTest.class.getClassLoader()
                .getResource("plugin-classpath.txt");
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, " +
                    "run `testClasses` build task.");
        }

        List<String> strings = Files.readAllLines(
                new File(pluginClasspathResource.toURI())
                        .toPath());

        ArrayList<File> files = new ArrayList<>();
        for (String string : strings) {
            files.add(new File(string));
        }

        return files;
    }

    protected void injectBuildScriptClassPath(File buildFile, List<File> classpath) throws IOException {
        if (!classpath.isEmpty()) {
            ArrayList<String> finalClasspathList = new ArrayList<>();

            for (File file : classpath) {
                finalClasspathList.add("'" + file.getAbsolutePath()
                        .replace("\\", "\\\\") + "'");
            }

            String finalClasspathString = Strings.join(",", finalClasspathList);

            List<String> strings = Files.readAllLines(buildFile.toPath());
            ArrayList<String> modifiedStrings = new ArrayList<>();
            for (String string : strings) {
                modifiedStrings.add(string.replace("/* [:classpath_injection] */",
                        "classpath files(" + finalClasspathString + ")"));
            }

            Files.write(buildFile.toPath(), modifiedStrings);
        } else {
            throw new IllegalArgumentException("classpath cannot be empty");
        }
    }
}
