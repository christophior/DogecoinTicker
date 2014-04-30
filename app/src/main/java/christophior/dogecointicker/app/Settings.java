package christophior.dogecointicker.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.Override;
import java.lang.String;

import static christophior.dogecointicker.app.NotificationReceiver.UPDATE_NOTI;


public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int mId = 0;
    private String currentCurrency = "mBTC";
    NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        ListPreference listPrefer = (ListPreference) findPreference("currency_to_use");
        currentCurrency = listPrefer.getValue();

        if (pref instanceof CheckBoxPreference)
            createNotification(key, sharedPreferences);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            return;
        }
    }

    public void createNotification(String key, SharedPreferences sharedPreferences) {

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Intent updateIntent = new Intent();
        updateIntent.setAction(UPDATE_NOTI);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(this, 0, updateIntent, 0);

        if (sharedPreferences.getBoolean(("Cryptsy"), true) ||
                sharedPreferences.getBoolean(("CoinedUp"), true) ||
                sharedPreferences.getBoolean(("Coins-E"), true) ||
                sharedPreferences.getBoolean(("Bter"), true) ||
                sharedPreferences.getBoolean(("Vircurex"), true)) {
            mBuilder.setStyle(inboxStyle);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setOngoing(true);
            mBuilder.setContentTitle("Dogecoin Prices:");
            mBuilder.addAction(R.drawable.ic_notification, "Click to update", pendingUpdate);


//            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//            Intent i = new Intent(this, NotificationService.class);
//            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//            am.cancel(pi);
//                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                        SystemClock.elapsedRealtime() + 5000,
//                        5000, pi);
//            System.out.println("Alarm has started");
        }
        sendBroadcast(updateIntent);

        if (sharedPreferences.getBoolean(("Cryptsy"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Cryptsy")
                    + " " + currentCurrency + " @ Cryptsy");
        if (sharedPreferences.getBoolean(("CoinedUp"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("CoinedUp")
                    + " " + currentCurrency + " @ CoinedUp");
        if (sharedPreferences.getBoolean(("Coins-E"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Coins-E")
                    + " " + currentCurrency + " @ Coins-E");
        if (sharedPreferences.getBoolean(("Bter"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Bter")
                    + " " + currentCurrency + " @ Bter");
        if (sharedPreferences.getBoolean(("Vircurex"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Vircurex")
                    + " " + currentCurrency + " @ Vircurex");

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on.

        mId++;
        mNotificationManager.notify(mId, mBuilder.build());
    }

}