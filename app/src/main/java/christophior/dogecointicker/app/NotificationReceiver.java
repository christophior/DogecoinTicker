package christophior.dogecointicker.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Zach on 4/29/2014.
 */
public class NotificationReceiver extends BroadcastReceiver{

    public static String UPDATE_NOTI = "christophior.dogecointicker.app.UPDATE_NOTI";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("HELLO");

            Log.v("updatetest", "please update");
    }
}
