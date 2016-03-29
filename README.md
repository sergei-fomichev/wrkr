# wrkr
Sergei Fomichev and Mike Stowell

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
