package com.ahasbini.tools.androidopencv.functional;

import com.ahasbini.tools.androidopencv.functional.android.AndroidPluginTest;
import com.ahasbini.tools.androidopencv.functional.gradle.AllGradleTests;
import com.ahasbini.tools.androidopencv.functional.plugin.AllPluginTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by ahasbini on 05-Oct-19.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AllGradleTests.class,
        AndroidPluginTest.class,
        AllPluginTests.class
})
public class AllFunctionalTests  {

}
