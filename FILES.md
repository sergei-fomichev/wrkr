## Mobile
- [Manifest](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/Wrkr/mobile/src/main/AndroidManifest.xml)
  - AndroidManifest.xml
- [Java sources](https://github.com/uml-ubicomp-2016-spring/wrkr/tree/master/app/Wrkr/mobile/src/main/java/edu/uml/cs/mstowell/wrkr)
  - list/MListAdapter.java
  - list/SwipeDismissListViewTouchListener.java - **from Google, not written by me**
  - object/RestAPI.java
  - receiver/SetWristAlarmReceiver.java
  - receiver/StartWristTrackingReceiver.java
  - receiver/StopWristTrackingReceiver.java
  - service/RecordDataService.java
  - service/WearListenerService.java
  - ui/HelpFragment.java
  - ui/HomeFragment.java
  - ui/MainActivity.java
  - ui/ProfileFragment.java
  - ui/SettingsFragment.java
- [Layouts](https://github.com/uml-ubicomp-2016-spring/wrkr/tree/master/app/Wrkr/mobile/src/main/res/layout)
  - activity_main.xml
  - app_bar_main.xml
  - content_main.xml
  - fragment_help.xml
  - fragment_home.xml
  - fragment_profile.xml
  - fragment_settings.xml
  - list_item.xml
  - nav_header_main.xml

## Wear
- [Manifest](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/Wrkr/wear/src/main/AndroidManifest.xml)
  - AndroidManifest.xml
- [Java sources](https://github.com/uml-ubicomp-2016-spring/wrkr/tree/master/app/Wrkr/wear/src/main/java/edu/uml/cs/mstowell/wrkr)
  - ml/Logistic.java - **from [this Github repo](https://github.com/tpeng/logistic-regression), modified by myself**
  - service/MobileListenerService.java
  - service/WristTrackingListener.java
  - service/WristTrackingService.java
  - ui/MyDisplayActivity.java
  - ui/MyPostNotificationReceiver.java
  - ui/MyStubBroadcastActivity.java
- [Layout](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/Wrkr/wear/src/main/res/layout/activity_display.xml)
  - activity_display.xml

## Mobile+Wear Global Code
- [Global Java classes](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/WrkrLib/wrkrlib/src/main/java/edu/uml/cs/mstowell/wrkrlib/common/)
  - APIClientCommon.java
  - Globals.java
  - User.java
- [Logistic Regression Training Data](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/WrkrLib/wrkrlib/src/main/res/raw/train.csv)
  - train.csv

## Web API
- [README.md (api spec)](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/api/README.md)
- [api.php](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/api/api.php)
- [respond.php](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/api/respond.php)

## Web code
- [home page](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/web/Website/index.html)
  - index.html
- [scripts for LM](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/web/Leap-Motion/js/script.js)
  - script.js
- [exercises object](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/web/Leap-Motion/js/exercises.js)
  - exercises.js
- [scripts for the website](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/web/Website/js/scripts.js)
  - scripts.js
