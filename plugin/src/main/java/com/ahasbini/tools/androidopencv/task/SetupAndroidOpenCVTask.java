package com.ahasbini.tools.androidopencv.task;

import com.ahasbini.tools.androidopencv.PluginException;
import com.ahasbini.tools.androidopencv.internal.service.Injector;
import com.ahasbini.tools.androidopencv.internal.util.ExceptionUtils;
import com.ahasbini.tools.androidopencv.internal.util.Logger;

import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This is an empty task used to group all the other tasks
 * <p>
 * Created by ahasbini on 27-Nov-19.
 */
// TODO: 11-Dec-19 ahasbini: add locking mechanism to prevent clashing between several instances
@NonNullApi
public class SetupAndroidOpenCVTask extends DefaultTask {

    private final Logger logger = Logger.getLogger(SetupAndroidOpenCVTask.class);
    private final ResourceBundle messages = Injector.getMessages();

    public SetupAndroidOpenCVTask() {
        getOutputs().upToDateWhen(task -> !Injector.getCacheManager(getProject()).isCacheModified());
        doLast(task -> {
            logger.debug("doLast called");

            try {
                Injector.getCacheManager(getProject()).saveCache();
            } catch (IOException e) {
                throw new PluginException("Unable to save cache file.\n" +
                        ExceptionUtils.getCauses(e, messages.getString("caused_by")), e);
            }
        });
    }

    @Override
    public String getName() {
        return "setupAndroidOpenCV";
    }

    @Override
    public String getDescription() {
        return "Configures and installs AndroidOpenCV dependencies for project.";
    }

    @Override
    public String getGroup() {
        return "AndroidOpenCV";
    }
}
