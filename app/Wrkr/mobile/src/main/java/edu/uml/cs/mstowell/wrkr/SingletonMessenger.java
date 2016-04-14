package edu.uml.cs.mstowell.wrkr;

import android.os.Handler;
import android.os.Messenger;

/**
 * Singleton messenger class
 */
public class SingletonMessenger {

    private static Messenger instance;

    public static Messenger getInstance(Handler h) {
        if (instance == null) {
            instance = new Messenger(h);
        }
        return instance;
    }
}
