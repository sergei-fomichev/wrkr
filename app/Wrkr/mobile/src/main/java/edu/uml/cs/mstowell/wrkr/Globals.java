package edu.uml.cs.mstowell.wrkr;

/**
 * Created by Mike on 3/2/2016.
 */
public interface Globals {

    final static int FRAGMENT_INDEX_HOME = 0;
    final static int FRAGMENT_INDEX_PROFILE = 1;
    final static int FRAGMENT_INDEX_SETTINGS = 2;
    final static int FRAGMENT_INDEX_HELP = 3;

    // account
    final static String GLOBAL_PREFS = "wrkr_global_prefs";
    final static String USER_EMAIL = "wrkr_user_email";
    final static int REQUEST_CODE_EMAIL = 1;

    // wear comm
    final static String MSG_WRIST_EXER_TIME = "/wrist_exer_time";
    final static String MSG_WEAR_MSG_ACK = "/wear_message_ack";
    final static String MSG_INIT_FROM_DEVICE = "/init_from_device";

    // listener service and broadcast receiver comm
    final static String WEAR_DATA_KEY = "wear_data_key";
    final static String WEAR_DATA_VALUES = "wear_data_values";
}
