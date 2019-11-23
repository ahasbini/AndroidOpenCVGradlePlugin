package com.ahasbini.tools.androidopencv.unit.plugin;

import com.ahasbini.tools.androidopencv.service.FilesManager;
import com.ahasbini.tools.androidopencv.unit.BaseUnitTest;

import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilesManagerTest extends BaseUnitTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testWriteAndGetMd5SumsSuccess()
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        File folder = temporaryFolder.newFolder("folder");

        File file = new File(folder, "test.txt");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        file = new File(folder, "test2.out");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        File innerFolder = new File(folder, "folder");
        //noinspection ResultOfMethodCallIgnored
        innerFolder.mkdir();

        file = new File(innerFolder, "test3.pdf");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        File md5SumFile = temporaryFolder.newFile("test.md5");

        FilesManager filesManager = new FilesManager(Mockito.mock(Project.class));
        Map<String, String> outputMap = filesManager.writeAndGetMd5Sums(md5SumFile, folder,
                false, null);

        Map<String, String> sampleMap = new LinkedHashMap<>();
        sampleMap.put("test.txt", "d41d8cd98f00b204e9800998ecf8427e");
        sampleMap.put("test2.out", "d41d8cd98f00b204e9800998ecf8427e");
        sampleMap.put("folder\\test3.pdf", "d41d8cd98f00b204e9800998ecf8427e");

        Assert.assertEquals(outputMap, sampleMap);
    }

    @Test
    public void testWriteAndGetMd5SumsFailure()
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        File folder = temporaryFolder.newFolder("folder");

        File file = new File(folder, "test.txt");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        file = new File(folder, "test2.out");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        File innerFolder = new File(folder, "folder");
        //noinspection ResultOfMethodCallIgnored
        innerFolder.mkdir();

        file = new File(innerFolder, "test3.pdf");
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        File md5SumFile = temporaryFolder.newFile("test.md5");

        FilesManager filesManager = new FilesManager(Mockito.mock(Project.class));
        Map<String, String> outputMap = filesManager.writeAndGetMd5Sums(md5SumFile, folder,
                false, null);

        Map<String, String> sampleMap = new LinkedHashMap<>();
        sampleMap.put("test.txt", "d41d8cd98f00b204e9800998ecf8427e");

        Assert.assertNotEquals(outputMap, sampleMap);
    }

}
