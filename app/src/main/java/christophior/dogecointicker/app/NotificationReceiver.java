package christophior.dogecointicker.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static christophior.dogecointicker.app.Settings.share;

public class NotificationReceiver extends BroadcastReceiver{

    public static String UPDATE_NOTI = "christophior.dogecointicker.app.UPDATE_NOTI";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("updatetest", "please update");

        SharedPreferences prefs = share;

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        Intent updateIntent = new Intent();
        updateIntent.setAction(UPDATE_NOTI);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context, 0, updateIntent, 0);

        if (prefs.getBoolean(("Cryptsy"), true) ||
                prefs.getBoolean(("CoinedUp"), true) ||
                prefs.getBoolean(("Coins-E"), true) ||
                prefs.getBoolean(("Bter"), true) ||
                prefs.getBoolean(("Vircurex"), true)) {
            mBuilder.setStyle(inboxStyle);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setOngoing(true);
            mBuilder.setContentTitle("Dogecoin Prices:");
            mBuilder.addAction(R.drawable.ic_notification, "Click to update", pendingUpdate);
        }

        if (prefs.getBoolean(("Cryptsy"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Cryptsy")
                    + " mBTC @ Cryptsy");
        if (prefs.getBoolean(("CoinedUp"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("CoinedUp")
                    + " mBTC @ CoinedUp");
        if (prefs.getBoolean(("Coins-E"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Coins-E")
                    + " mBTC @ Coins-E");
        if (prefs.getBoolean(("Bter"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Bter")
                    + " mBTC @ Bter");
        if (prefs.getBoolean(("Vircurex"), false))
            inboxStyle.addLine("1 Doge = " + MainActivity.exchangePrices.get("Vircurex")
                    + " mBTC @ Vircurex");

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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

        mNotificationManager.notify(2, mBuilder.build());
    }
}
