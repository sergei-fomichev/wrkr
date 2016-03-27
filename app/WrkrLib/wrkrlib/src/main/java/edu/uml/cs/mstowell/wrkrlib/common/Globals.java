package edu.uml.cs.mstowell.wrkrlib.common;

/**
 * Common variables to share between wrkr mobile and wrkr wear
 */
public interface Globals {

    int FRAGMENT_INDEX_HOME = 0;
    int FRAGMENT_INDEX_PROFILE = 1;
    int FRAGMENT_INDEX_SETTINGS = 2;
    int FRAGMENT_INDEX_HELP = 3;

    // account
    String GLOBAL_PREFS = "wrkr_global_prefs";
    String USER_EMAIL = "wrkr_user_email";
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

    // listener service and broadcast receiver comm
    String WEAR_DATA_KEY = "wear_data_key";
    String WEAR_DATA_VALUES = "wear_data_values";

    // activity detection
    String WRIST_BROADCAST_ACTION = "edu.uml.cs.mstowell.wrkr.ACTIVITY_UPDATE";
    String WRIST_BROADCAST_DATA = "data";
}
