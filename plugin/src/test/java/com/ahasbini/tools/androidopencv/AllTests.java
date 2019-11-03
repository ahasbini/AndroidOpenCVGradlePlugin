package com.ahasbini.tools.androidopencv;

import com.ahasbini.tools.androidopencv.functional.AllFunctionalTests;
import com.ahasbini.tools.androidopencv.integration.AllIntegrationTests;
import com.ahasbini.tools.androidopencv.unit.AllUnitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by ahasbini on 03-Nov-19.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AllUnitTests.class,
        AllIntegrationTests.class,
        AllFunctionalTests.class
})
public class AllTests {

}
