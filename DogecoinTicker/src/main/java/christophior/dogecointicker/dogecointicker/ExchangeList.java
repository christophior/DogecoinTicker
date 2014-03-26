package christophior.dogecointicker.dogecointicker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.*;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.view.View;
import android.os.*;
import android.app.*;
import org.json.*;
import java.util.*;
import android.util.*;


public class ExchangeList extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String urlCryptsy = "http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=132";
    private static String urlCoinedup = "https://api.coinedup.com/markets";
    private static String urlCoinse = "https://www.coins-e.com/api/v2/market/DOGE_BTC/depth/";
    private static String urlBter = "http://data.bter.com/api/1/ticker/doge_btc";
    private static String urlVircurex = "https://api.vircurex.com/api/get_highest_bid.json?base=DOGE&alt=BTC";


    HashMap<String, Double> exchangePrices = new HashMap<String, Double>();

    public final String[] EXCHANGES = new String[] { "Cryptsy", "CoinedUp",
            "Coins-E", "Bter", "Vircurex"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHashMap();
        (new GetJson()).execute();
        double[] prices = {1.0, 2.0, 3.0, 4.0, 5.0};
        setListAdapter(new ExchangeArrayAdapter(this, EXCHANGES, prices));
    }

    private void initHashMap(){
        for (String ex : EXCHANGES){
            exchangePrices.put(ex, 0.0);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);

        switch(position){
            case 0:
                Intent newActivity = new Intent(this, cryptsy.class);
                startActivity(newActivity);
//                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
            case 1:
//                Intent newActivity = new Intent(this, coinedup.class);
//                startActivity(newActivity);
                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
            case 2:
//                Intent newActivity = new Intent(this, coinse.class);
//                startActivity(newActivity);
                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
            case 3:
//                Intent newActivity = new Intent(this, bter.class);
//                startActivity(newActivity);
                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
            case 4:
//                Intent newActivity = new Intent(this, vircurex.class);
//                startActivity(newActivity);
                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exchange_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, Settings.class), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            // Showing progress dialog
////            pDialog = new ProgressDialog(ExchangeList.this);
////            pDialog.setMessage("Please wait...");
////            pDialog.setCancelable(false);
////            pDialog.show();
//
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonCryptsy = sh.makeServiceCall(urlCryptsy, ServiceHandler.GET);
//            System.out.println("Response: > " + jsonCryptsy);

            if (jsonCryptsy != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonCryptsy);
                    JSONObject dogeCryptsy = ((jsonObj.getJSONObject("return")).getJSONObject("markets")).getJSONObject("DOGE");
                    String cryptsyPrice = dogeCryptsy.getString("lasttradeprice");
                    System.out.println("Cryptsy Price: " + cryptsyPrice);
                    exchangePrices.put("Cryptsy", Double.parseDouble(cryptsyPrice) * 1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            String jsonCoinse = sh.makeServiceCall(urlCoinse, ServiceHandler.GET);

            if (jsonCoinse != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonCoinse);
                    String coinsePrice = jsonObj.getString("ltp");
                    System.out.println("Coins-E Price: " + coinsePrice);
                    exchangePrices.put("Coins-E", Double.parseDouble(coinsePrice));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            String jsonBter = sh.makeServiceCall(urlBter, ServiceHandler.GET);

            if (jsonBter != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonBter);
                    String bterPrice = jsonObj.getString("avg");
                    System.out.println("Bter Price: " + bterPrice);
                    exchangePrices.put("Bter", Double.parseDouble(bterPrice));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            String jsonVircurex = sh.makeServiceCall(urlVircurex, ServiceHandler.GET);

            if (jsonVircurex != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonVircurex);
                    String vircurexPrice = jsonObj.getString("value");
                    System.out.println("Vircurex Price: " + vircurexPrice);
                    exchangePrices.put("Vircurex", Double.parseDouble(vircurexPrice));
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
//            super.onPostExecute(result);
//            // Dismiss the progress dialog
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//            /**
//             * Updating parsed JSON data into ListView
//             * */
//                    ExchangeList.this, contactList,
//                    R.layout.list_item, new String[] { TAG_NAME, TAG_EMAIL, TAG_PHONE_MOBILE }, new int[] { R.id.name, R.id.email, R.id.mobile });
//
//            setListAdapter(adapter);
        }

    }

}
