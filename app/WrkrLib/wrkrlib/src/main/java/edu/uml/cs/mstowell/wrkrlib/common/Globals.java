package edu.uml.cs.mstowell.wrkrlib.common;

/**
 * Common variables to share between wrkr mobile and wrkr wear
 */
public interface Globals {

    String WEBSITE_URL = "http://www.cs.uml.edu/~sfomiche/wrkr/";

    int FRAGMENT_INDEX_HOME = 0;
    int FRAGMENT_INDEX_PROFILE = 1;
    int FRAGMENT_INDEX_SETTINGS = 2;
    int FRAGMENT_INDEX_HELP = 3;

    // ML constants
    double LIKELIHOOD_PERCENTAGE = 0.8;
    int EXERCISE_TRIGGER_TIME = 1800; // 1800 seconds = 30 minutes
    int DATA_SIZE = 50;
    int DATA_HERTZ = 5;
    int NUM_FEATURES = 5;
    String LOGISTIC_MODEL_TRAINED = "logistic_model_trained";
    String LOGISTIC_WEIGHTS = "logistic_weights";

    // account
    String GLOBAL_PREFS = "wrkr_global_prefs";
    String USER_EMAIL = "wrkr_user_email";
    String USER_ID = "wrkr_user_id";
    String USER_TIME_AT_KEYBOARD = "wrkr_user_time_at_keyboard";
    int REQUEST_CODE_EMAIL = 1;

    // wear comm
    String MSG_WRIST_EXER_TIME = "/wrist_exer_time";
    String MSG_WEAR_MSG_ACK = "/wear_message_ack";
    String MSG_INIT_FROM_DEVICE = "/init_from_device";
    String MSG_START_ACCEL = "/start_accel";
    String MSG_START_ACCEL_ACK = "/start_accel_ack";
    String MSG_STOP_ACCEL = "/stop_accel";
    String MSG_STOP_ACCEL_ACK = "/stop_accel_ack";
    String MSG_WEAR_DATA = "/msg_wear_data";
    String MSG_USER_NEEDS_EXERCISE = "/msg_user_needs_exercise";
    String DO_NOT_VIBRATE = "/do_not_vibrate";

    // listener service and broadcast receiver comm
    String WEAR_DATA_KEY = "wear_data_key";
    String WEAR_DATA_VALUES = "wear_data_values";

    // activity detection
    String WRIST_BROADCAST_ACTION = "edu.uml.cs.mstowell.wrkr.ACTIVITY_UPDATE";
    String WRIST_BROADCAST_DATA = "data";

    // watch to phone comm for API
    String WATCH_TO_PHONE_BROADCAST_ACTION = "edu.uml.cs.mstowell.wrkr.WTP_BROADCAST";
    String WTP_BROADCAST_DATA = "data";
    String RECORD_SERVICE_MESSENGER = "record_service_messenger";

    // when to fire the services to record data
    String SET_WRIST_ALARM_BROADCAST_ACTION = "edu.uml.cs.mstowell.wrkr.SET_WRIST_ABA";
    int START_TRACKING_HOUR = 8;   // 8:00am
    int START_TRACKING_MINUTE = 0; //
    int STOP_TRACKING_HOUR = 16;   // 4:00pm
    int STOP_TRACKING_MINUTE = 0;  //
}
