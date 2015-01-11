package christophior.dogecointicker.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import static christophior.dogecointicker.app.Settings.share;

public class NotificationReceiver extends BroadcastReceiver {

    public static String UPDATE_NOTI = "christophior.dogecointicker.app.UPDATE_NOTI";
    public static HashMap<String, Double> exchangePrices = new HashMap<String, Double>();
    private static String urlMarketPrices = "http://doge.yottabyte.nu/json/markets.json";
    private String currentCurrency = "mBTC";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("updatetest", "please update");
        (new GetJson()).execute();

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

        if (prefs.getBoolean(("Cryptsy"), true) || prefs.getBoolean(("Coins-E"), true) ||
               prefs.getBoolean(("Bter"), true) || prefs.getBoolean(("Vircurex"), true)) {
            mBuilder.setStyle(inboxStyle);
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setOngoing(true);
            mBuilder.setContentTitle("Dogecoin Prices:");
            mBuilder.addAction(R.drawable.ic_notification, "Click to update", pendingUpdate);
        }

        if (prefs.getBoolean(("Cryptsy"), false))
            inboxStyle.addLine("1 Doge = " + exchangePrices.get("Cryptsy")
                    + " " + currentCurrency + " @ Cryptsy");
        if (prefs.getBoolean(("Coins-E"), false))
            inboxStyle.addLine("1 Doge = " + exchangePrices.get("Coins-E")
                    + " " + currentCurrency + " @ Coins-E");
        if (prefs.getBoolean(("Bter"), false))
            inboxStyle.addLine("1 Doge = " + exchangePrices.get("Bter")
                    + " " + currentCurrency + " @ Bter");
        if (prefs.getBoolean(("Vircurex"), false))
            inboxStyle.addLine("1 Doge = " + exchangePrices.get("Vircurex")
                    + " " + currentCurrency + " @ Vircurex");

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


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private void updateExchangePrice(String exchange, double price) {

            int coefficient = 1000;

            if (exchange.equals("cryptsy")) {
                exchangePrices.put("Cryptsy", formatPricemBTC(price));
            } else if (exchange.equals("coins-e")) {
                exchangePrices.put("Coins-E", formatPricemBTC(price));
            } else if (exchange.equals("bter")) {
                exchangePrices.put("Bter", formatPricemBTC(price));
            } else if (exchange.equals("vircurex")) {
                exchangePrices.put("Vircurex", formatPricemBTC(price));
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonMarketPrice = sh.makeServiceCall(urlMarketPrices, ServiceHandler.GET);

            if (jsonMarketPrice != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonMarketPrice);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject singleMarket = jsonArray.getJSONObject(i);
                        updateExchangePrice(singleMarket.getString("m"), singleMarket.getDouble("l"));
                    }

//                    System.out.println("Cryptsy Price: " + exchangePrices.get("Cryptsy"));
//                    System.out.println("Coins-E Price: " + exchangePrices.get("Coins-E"));
//                    System.out.println("Bter Price: " + exchangePrices.get("Bter"));
//                    System.out.println("Vircurex Price: " + exchangePrices.get("Vircurex"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            double[] prices = {exchangePrices.get("Cryptsy"), exchangePrices.get("Coins-E"),
                    exchangePrices.get("Bter"), exchangePrices.get("Vircurex")};
        }

        public double formatPricemBTC(Double price) {
            DecimalFormat df = new DecimalFormat("0.00000");
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }
}
