visualization.pdf shows the strongest cluster of user-at-keyboard data by only looking at the X vs Z accelerometer readings.  See the main page README for more information about these visualizations.

Color scheme:
 - red = typing at a keyboard
 - orange = sitting
 - purple = scratching head
 - blue = jogging
 - yellow = walking
 - green = standing still

By combining X, Z, and WMA data, we will be able to use a supervised expectation maximization machine learning algorithm to classify groups of data based on some probability of certainty that data falls within the "user-at-keyboard" cluster.

**Edit April 23, 2016:** a logistic regression classifier has been shown to provide great results for our usage.  Using all of X, Y, Z, acceleromation magnitude, and WMA data, trained on just over 1,000 data points, the logistic classifier can predict the same user's wrist activity of being at a keyboard with roughly 96% precision.  The data used for training can be found [here](https://github.com/uml-ubicomp-2016-spring/wrkr/blob/master/app/WrkrLib/wrkrlib/src/main/res/raw/train.csv).
