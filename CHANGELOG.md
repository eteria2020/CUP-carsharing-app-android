# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.4.17] - 2020-03-02
### Changed
- IT: added docukey path for embedded PDF on legal webview (#650)
      removed city button from settings menu

## [3.4.16] - 2020-02-10
### Changed
- IT: added SOS button on Support Area (#734)

## [3.4.15] - 2020-01-16
### Changed
- IT: bugfix on settings cities selection (cityJson)

## [3.4.14] - 2020-01-10
### Changed
- NL: updated invalid_driver_license_login_alert message (#605)
- updated DRIVER_LICENSE baseURL in UserAreaFragment.java

## [3.4.14] - 2020-01-08
### Changed
- added endpointSitePayments for userarea payments (#547)

## [3.4.13] - 2019-12-23
### Changed
- SK: updated faq links (#512)

## [3.4.13] - 2019-12-18
### Changed
- SL: updated translations (#476)

## [3.4.12] - 2019-12-02
### Changed
- removed chronology rate minute line (#341)
- updated routeLogin for NL

## [3.4.12] - 2019-12-02
### Changed
- removed chronology rate minute line (#341)
- updated routeLogin for NL

## [3.4.11] - 2019-11-15
### Changed
- SL: updated translations 

## [3.4.9] - 2019-11-12
### Changed
- hidden app tutorial 

## [3.4.8] - 2019-09-23
### Changed
- added SL version  

## [3.4.8] - 2019-09-09
### Changed
- new Google Maps API key  


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