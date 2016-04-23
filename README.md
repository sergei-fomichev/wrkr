# wrkr
Sergei Fomichev and Mike Stowell - see the Wiki for a description of our project.  Mike is responsible for the mobile and smartwatch app, Sergei is responsible for the web server and Leap Motion.

We will update our progress for each week below.  The most recent week appears at top, and older progress appears at the bottom.

## Progress: April 18 - April 24 2016

### Mobile + Smartwatch App

 - Fixed mobile-side API usage of timestamps
 - Set up AlarmManagers both on boot and on activity start to ensure that every weekday at 8am, the phone and wear data recording and communication services run, and every weekday at 4pm, the services are killed
 - Added logistic regression to the application - now real machine learning as opposed to bounded-box fitting is applied
 - Deleted unused application tests and other remnants from previous weeks
 - Swapped the Messenger in the SettingsFragment with a BroadcastReceiver to ensure it always receives wear information
 - Moved call to getNotificationList in HomeFragment to onResume so that user notifications are refreshed often

### Web + Leap Motion

 - 

## Progress: April 11 - April 17 2016

### Mobile + Smartwatch App

 - moved API to mobile-only since wear doesn't have on-board wifi
 - moved data classification logic to wear
 - added retry timer to WristTrackingService if watch and phone are disconnected: will retry every 5 minutes to send notification
 - added karma score to user profile
 - fixed the wear accelerometer data collecting service from being killed
   - the system was killing my service after 18-20 minutes of being alive, so a combination of the following fixed this:
      - mobile and wear service return START_STICKY in onStartCommand to request being rescheduled
      - mobile and wear service both use persistent notifications - mobile side needs to keep the API communication ready for when the wear sends a message that the user needs an exercise, and wear needs to keep recording data and classifying
      - wear service reregisters the accelerometer listener every 30 seconds
      - wear service holds a partial wakelock
      - receiver that starts the wear service also starts an AlarmManager that reschedules the service every 60 seconds.  Service has logic to avoid re-initializing components if it was already up.  That way if the system kills my service, it'll be back up and running again in < 60 seconds.
 - closed issue [#4](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/4), [#5](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/5), [#7](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/7), [#8](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/8) ... opened issues [#9](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/9), [#10](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/10)

### Web + Leap Motion

 - Created the User Stat page
 - Transfered the hand exerciser to the web site
 - Made the exercises change status after user done it, both on the server and the website
 - Debugged certan exercises from the false exercise count

## Progress: March 30 - April 10 2016

### Mobile + Smartwatch App

 - implemented a basic classifier for now based on last week's research.  Will continue to use it until time permits by the end of the project to use a true machine learning classifier
 - fixed API bug that caused InputStream exceptions
 - polished up additional API error handling
 - added AsyncTasks to perform all networking tasks in the background
 - used API to display proper notifications in the home screen (though the API spec here will change, so will redo this piece next week as well)
   - includes timestamp math to inform users when exercises were posted and recommend how long the user has to complete them
 - floating action button in app links to the wrkr website
 - created Issue [#3](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/3), [#4](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/4), [#5](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/5), and [#6](https://github.com/uml-ubicomp-2016-spring/wrkr/issues/6)

### Web + Leap Motion

 - Improved hand exerciser by making the interactive hand area.
 - Implemented a new exercise called fingers spread.
 - Put the icon to show when hand is found.
 - Worked on the users dashboard on a website.
 - Fixed an API bug.

## Progress: March 29 - April 4 2016

### Mobile + Smartwatch App

- Implemented and tested the API for the Java/mobile side
- Using a partial wakelock + accelerometer re-register combination to keep the watches accelerometer on
  - only one or the other does not work, but so far using both seems fine. Needs more testing.
- Wrote csv files containing accelerometer, magnitude, and weighted-moving-average from watch.
  - contains data of me sitting, typing slow, typing fast, standing, walking, jogging, and scratching my head, 10 seconds each, repeat all trials x3
  - analyzed data on iSENSE
    - Found that X vs Z, WMA vs Z, and WMA vs Y provided the strongest distinguishing clusters, so in the future I will probably use an Expectation Maximization clustering algorithm trained on this data to classify.  More features may be explored.
    - Links: http://isenseproject.org/visualizations/1133, http://isenseproject.org/visualizations/1131, http://isenseproject.org/visualizations/1129 (expand "Groups" in the left hand side to see color codes).  Base project: http://isenseproject.org/projects/2145

### Web + Leap Motion

- Implemented the exercising cycle for both hands together. 
- Fixed bugs
- Write the server API for sync with database.  

### Etc

 - Mike created the data directory & the Wiki page for this project


## Progress: March 22 - March 28 2016

### Mobile + Smartwatch App

- Added ability to manage and sync your Google profile to the app
- Finished much of the mobile app’s UI
- unregister/register wear accelerometer every 5 minutes so it doesn’t shut off when recording data
  - Though I think I may need to resort to Wakelock or another mechanism, because this isn’t always reliable - sometimes the accelerometer reports data at a much, much slower rate (watch is “sleeping?”), even though I specify maximum rate
- Accelerometer data is now stored in a JSONObject and sent back to the mobile phone
  - Currently sending: accel X, Y, Z, magnitude, and a weighted moving average
  - Future plan: possibly using gravity to determine orientation
  - These will be fed as features to a machine learning algorithm
- Did a battery test: confirmed that large battery drain was due to debug over bluetooth, not due to the accelerometer being on and magnitude/WMA being calculated frequently


### Web + Leap Motion

 - Added the exercise progress bar, so you can see how many exercises you have left in a nice way. 
 - Implemented the second hand which goes after the first one.
 - Made the first API command implemented. 

## Progress: Pre- March 22 2016

 - Came up with the idea for the app and website
 - Implemented a shell application and just started accelerometer recording, figured out debugging over bluetooth
 - Started playing with the Leap Motion device
 - Figured out where we will host the web server
