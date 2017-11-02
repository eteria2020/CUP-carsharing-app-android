Change Log
==========

Version *(2017-05-03)*
----------------------------
- **NEW HANDROIX GRADLE PLUGIN INTEGRATION!!!** to mark app launcher icon.
- **DEVEL and PROD flavors removed**: endpoint is now specified inside app build.gradle file.  
- Mock improved and more utilities added.



Version *(0000-00-00)*
----------------------------
- **rxbindings** upgraded to 1.0.1.  
- **'com.neenbedankt.gradle.plugins:android-apt:1.8' removed**: we can use *annotationProcessor* instead of *apt*.  
  ```groovy
  annotationProcessor "com.google.dagger:dagger-compiler:$rootProject.ext.daggerVersion"
  ```

- multidex enabled by default.  
- handroix update to version **3.8.0**.  
- mock log improved.  
- *runConfigurations* excluded from gitignore.  
- *gradle parallel and daemon* included inside gradle.properties  
- *subscribeOn()* moved inside RetrofitDataSource  
- *menu* handling with intent  