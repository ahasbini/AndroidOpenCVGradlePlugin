# AndroidOpenCVGradlePlugin

Gradle Plugin that will automate retrieving the Android OpenCV SDK and
linking it to the project, making it easy to include OpenCV into
Android applications.

## Usage

For usage in an Android Project, the below changes are needed:

1. In the project ```build.gradle``` (at the root directory of the
  project folder), modify the ```repositories``` and ```dependencies```
  block as below:

    ```gradle
    buildscript {
        repositories {
            maven {  // At the beginning of the block
              url "https://plugins.gradle.org/m2/"
            }
            // ... google() or jcenter() or others
            maven { // At the end of the block and after google()
               url 'https://repo.gradle.org/gradle/libs-releases'
            }
        }
        dependencies {
            // ... the Android plugin and other classpath definitions
            classpath 'com.ahasbini.tools:android-opencv-gradle-plugin:0.1.+'
        }
    }
    ```

2. In the ```app``` module (or application/library module that you're
  developing) ```build.gradle``` file, add the
  ```android-opencv-gradle-plugin``` plugin and the ```androidOpenCV```
  as below:

    ```gradle
    // apply plugin: 'com.android.application' or other Android plugin
    apply plugin: 'com.ahasbini.android-opencv-gradle-plugin' // After the Android plugin
    
    // ...
    
    android {
        // ...
    }
    
    androidOpenCV { // After the android block
    
        // Required: Version of OpenCV to be used in the project
        version '3.3.0'
    }
    
    // ...
    ```

3. __Optional__: If the project did not contain any C++ code (usually
   located in ```jni``` or ```cpp``` folders under
   ```{project_app_module}/src/main/```), perform the below changes:

   * Add the below in ```app``` module (or application/library module
   that you're developing) ```build.gradle``` file:

        ```gradle
        android {
            // ...
            externalNativeBuild {
                cmake {
                    path "CMakeLists.txt"
                }
            }
        }
        ```

   * Create the ```CMakeLists.txt``` in ```app``` module (or
   application/library module that you're developing) directory and
   check the
   [Android Guides for NDK](https://developer.android.com/ndk/guides)
   or the [sample](sample) for more info.

4. Do a Gradle Sync
   <img src="https://developer.android.com/studio/images/buttons/toolbar-sync-gradle.png" width="16px" height="16px"/>,
   refresh linked C++ projects (**Build > Refresh Linked C++
   Projects**) and compile to make sure the integration was successful.

### Troubleshooting

In most cases the plugin will print out clear error messages to what 
might be wrong in the build. In case the error messages are cryptic or 
unsure if the plugin is causing it or not, two parameters for the build 
command could be leveraged for getting more info.

 - `--debug` flag, this will output more logging (lots of them) from 
 gradle and any plugin used with the build including logs from 
 AndroidOpenCVGradlePlugin. Example:
 
    ```shell
    # On Windows:
    gradlew.bat --debug <task>
    # or on *nix:
    ./gradlew --debug <task>
    ```
 
 - `-PENABLE_ANDROID_OPENCV_LOGS` project parameter flag, this is 
 specific to the AndroidOpenCVGradlePlugin and it will print out its 
 logs without enabling the logs for everything else in the build. Example:
    
    ```shell
    # On Windows:
    gradlew.bat -PENABLE_ANDROID_OPENCV_LOGS <task>
    # or on *nix:
    ./gradlew -PENABLE_ANDROID_OPENCV_LOGS <task>
    ```
 
 - Perform a ```clean```. Example:
    
    ```shell
    # On Windows:
    gradlew.bat clean
    # or on *nix:
    ./gradlew clean
    ```

## Underlying Logic & Implementation

TL;DR The plugin downloads the ```opencv-xxx-android-sdk.zip```,
extracts the files, compiles the Java sources into AARs, and links them
along with JNI binaries into the project using ```dependencies``` and
```externalNativeBuild``` configurations.

In detail, below are the steps it carries out (primarily in this order):

 - **[AndroidOpenCVGradlePlugin.java](plugin/src/main/java/com/ahasbini/tools/androidopencv/AndroidOpenCVGradlePlugin.java)
   (Configuration Phase)**
   - Extract the requested version of OpenCV from the ```androidOpenCV```
   block.
   - Set the OpenCV JNI directory and arguments of the
   ```externalNativeBuild``` in the ```android``` block.
   - Add ```flatDir``` repository with
   ```{user_home}/.androidopencv/{version}/build-cache/outputs``` path
   and add dependencies ```debugImplementation``` and
   ```releaseImplementation``` with the names of the AARs to project
   ```dependencies```.
 - **[DownloadAndroidOpenCVTask.java](plugin/src/main/java/com/ahasbini/tools/androidopencv/task/DownloadAndroidOpenCVTask.java)
   (Execution Phase)**
   - Download the ```opencv-xxx-android-sdk.zip``` into the directory 
   ```{user_home}/.androidopencv/{version}``` using the url template
   ```"https://sourceforge.net/projects/opencvlibrary/files/" + version + "/opencv-" + version + "-android-sdk.zip"```.
 - **[UnZipAndroidOpenCVTask.java](plugin/src/main/java/com/ahasbini/tools/androidopencv/task/UnZipAndroidOpenCVTask.java)
   (Execution Phase)**
   - Extract the downloaded zip file within the 
   ```{user_home}/.androidopencv/{version}``` folder.
 - **[CopyAndroidOpenCVJniLibsTask.java](plugin/src/main/java/com/ahasbini/tools/androidopencv/task/CopyAndroidOpenCVJniLibsTask.java)
   (Execution Phase)**
   - Copy the JNI libs/directories from the extracted zip folder into 
   ```{project_module_directory}/build/androidopencv```.
 - **[BuildAndroidOpenCVAarsTask.java](plugin/src/main/java/com/ahasbini/tools/androidopencv/task/BuildAndroidOpenCVAarsTask.java)
   (Execution Phase)**
   - Compile AAR binaries from Java source and place outputs (debug and
   release builds) in
   ```{user_home}/.androidopencv/{version}/build-cache``` using the
   [Gradle Tooling API](https://docs.gradle.org/current/userguide/embedding.html).

## Contributing & Future Plans

As this is still under development and testing, feel
free to share your contributions to the project with finding issues,
code improvements and/or feature additions and requests. The build 
scripts are configured and made ready for testing and publishing custom 
builds to the local machine for use in other projects. This can be done 
similar to to the below steps:

```shell
git clone https://github.com/ahasbini/AndroidOpenCVGradlePlugin.git
cd AndroidOpenCVGradlePlugin

# Perform some code changes and run some tests in your favorite IDE/Editor

# Publish custom builds on Windows:
gradlew.bat :plugin:publishToMavenLocal
# or on *nix:
./gradlew :plugin:publishToMavenLocal
```

Once commands above complete to succession, the plugin is now located
in the local Maven repository of the machine (```.m2```) under
```com\ahasbini\tools\android-opencv-gradle-plugin```.

With regards to the test cases, the current tests executed for the 
plugin can be found in the [test](plugin/src/test/java) folder which 
include various [unit, integration and functional](https://guides.gradle.org/testing-gradle-plugins/) 
test cases. The tests also make use of the [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html)
along with pre-defined build scripts or project setups found in the 
test [resources](plugin/src/test/resources) folder to best simulate the 
use cases of the plugin from within the project itself.

As part of the ongoing development, below is a brief list of things 
(TODOs) that are in plan for the project:

 - [ ] Add Android NDK version checking and configuration for proper
 linking with compiled binaries of OpenCV
 - [x] Add plugin ```clean``` tasks
 - [ ] Add plugin install of custom built Android OpenCV task
 - [ ] Add more tests and assertions in test cases
 - [ ] CI/CD integration
