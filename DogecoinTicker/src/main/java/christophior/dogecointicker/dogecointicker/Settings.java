package christophior.dogecointicker.dogecointicker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int mId = 0;

    private HashMap<String, Integer> currentIDs = new HashMap<String, Integer>();
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
        CharSequence currTxt = listPrefer.getEntry();
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
//        if (key.equals("Cryptsy") && !temp)
//            mNotificationManager.cancelAll();

//        if (currentIDs.containsKey(key))
//            currentIDs.remove(key);
//        else
//            currentIDs.put(key, mId);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();

        boolean temp = sharedPreferences.getBoolean(key, false);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ExchangeList.class);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if (sharedPreferences.getBoolean(("Cryptsy"), true) ||
                sharedPreferences.getBoolean(("CoinedUp"), true) ||
                sharedPreferences.getBoolean(("Coins-E"), true) ||
                sharedPreferences.getBoolean(("Bter"), true) ||
                sharedPreferences.getBoolean(("Vircurex"), true)) {
            mBuilder.setStyle(inboxStyle);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setOngoing(true);
            mBuilder.setContentTitle("Dogecoin Prices:");
        }

        if (sharedPreferences.getBoolean(("Cryptsy"), false))
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get("Cryptsy")
                    + " " + currentCurrency + " @ Cryptsy");
        if (sharedPreferences.getBoolean(("CoinedUp"), false))
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get("CoinedUp")
                    + " " + currentCurrency + " @ CoinedUp");
        if (sharedPreferences.getBoolean(("Coins-E"), false))
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get("Coins-E")
                    + " " + currentCurrency + " @ Coins-E");
        if (sharedPreferences.getBoolean(("Bter"), false))
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get("Bter")
                    + " " + currentCurrency + " @ Bter");
        if (sharedPreferences.getBoolean(("Vircurex"), false))
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get("Vircurex")
                    + " " + currentCurrency + " @ Vircurex");

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Exchange.class);
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