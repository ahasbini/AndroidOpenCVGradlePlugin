package com.ahasbini.tools.androidopencv.unit.plugin;

import com.ahasbini.tools.androidopencv.unit.BaseUnitTest;
import com.ahasbini.tools.androidopencv.internal.util.ExceptionUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ahasbini on 03-Nov-19.
 */
public class UtilsTest extends BaseUnitTest {

    @Test
    public void testExceptionFormat() {
        String causes = ExceptionUtils.getCauses(
                new RuntimeException("This is the first cause",
                        new IllegalStateException("This is the second cause",
                                new NullPointerException("This is the third cause"))),
                "\tCaused by %s\n"
        );

        Assert.assertTrue(causes.matches("" +
                "\tCaused by " + RuntimeException.class.getName() + ": This is the first cause\n" +
                "\tCaused by " + IllegalStateException.class.getName() + ": This is the second cause\n" +
                "\tCaused by " + NullPointerException.class.getName() + ": This is the third cause\n"));
    }

}
