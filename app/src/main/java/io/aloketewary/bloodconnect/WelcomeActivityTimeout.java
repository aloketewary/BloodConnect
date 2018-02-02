package io.aloketewary.bloodconnect;

import android.app.Application;
import android.os.SystemClock;

/**
 * Created by AlokeT on 2/1/2018.
 */

public class WelcomeActivityTimeout extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(4000);
    }
}
