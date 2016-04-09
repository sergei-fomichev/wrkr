# wrkr
Sergei Fomichev and Mike Stowell - see the Wiki for a description of our project.  Mike is responsible for the mobile and smartwatch app, Sergei is responsible for the web server and Leap Motion.

We will update our progress for each week below.  The most recent week appears at top, and older progress appears at the bottom.

## Progress: March 30 - April 10 2016

### Mobile + Smartwatch App

 - implemented a basic classifier for now based on last week's research.  Will continue to use it until time permits by the end of the project to use a true machine learning classifier
 - fixed API bug that caused InputStream exceptions
 - polished up additional API error handling
 - added AsyncTasks to perform all networking tasks in the background
 - used API to display proper notifications in the home screen (though the API spec here will change, so will redo this piece next week as well)
 - floating action button in app links to the wrkr website

### Web + Leap Motion

 - 

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
 - ...
## Progress: Pre- March 22 2016

 - Came up with the idea for the app and website
 - Implemented a shell application and just started accelerometer recording, figured out debugging over bluetooth
 - Started playing with the Leap Motion device
 - Figured out where we will host the web server
