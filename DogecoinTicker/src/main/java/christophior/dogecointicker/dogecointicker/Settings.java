package christophior.dogecointicker.dogecointicker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.HashMap;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int mId = 0;
    private int bigCheck = 0;

    private HashMap<String, Integer> notIDs = new HashMap<String, Integer>();
    private String currentCurrency = "mBTC";
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this);
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);
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
//        if (bigCheck > 0) {
//            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get(key)
//                    + " " + currentCurrency + " @ " + key);
//            Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
//        }
        if (!key.equals("currency_to_use"))
            createNotification(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            return;
        }
    }

    public void createNotification(String key) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notIDs.containsKey(key)) {
            mNotificationManager.cancel(notIDs.get(key));
            notIDs.remove(key);
            return;
        }


//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentText(key + " \t\t1 Doge = " + ExchangeList.exchangePrices.get(key)
//                                + " " + currentCurrency)
//                        .setOngoing(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, Exchange.class);


        inboxStyle.setBigContentTitle("Dogecoin Prices:");
        //if (mId >= 1) {
            inboxStyle.addLine("1 Doge = " + ExchangeList.exchangePrices.get(key)
                    + " " + currentCurrency + " @ " + key);
            if (bigCheck == 0) {
                mBuilder.setStyle(inboxStyle);
                mBuilder.setSmallIcon(R.drawable.ic_launcher);
                mBuilder.setOngoing(true);
            }
        //}


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
        notIDs.put(key, mId);
        mId++;
        bigCheck++;
        mNotificationManager.notify(notIDs.get(key), mBuilder.build());
    }
}
