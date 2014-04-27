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
import java.text.*;


public class ExchangeList extends ListActivity {

    /* Credit for much of the JSON code and outline for how it works goes to Ravi Tamada at:
       http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
     */
    
    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String urlMarketPrices = "http://doge.yottabyte.nu/json/markets.json";


    public static HashMap<String, Double> exchangePrices = new HashMap<String, Double>();

    public final String[] EXCHANGES = new String[] { "Cryptsy", "CoinedUp",
            "Coins-E", "Bter", "Vircurex"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHashMap();
        (new GetJson()).execute();
        double[] prices = {0.0, 0.0, 0.0, 0.0, 0.0};
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
        Bundle b;

        switch(position){
            case 0:
                Intent newCryptsyActivity = new Intent(this, Exchange.class);
                b = new Bundle();
                b.putString("market", "Cryptsy");
                newCryptsyActivity.putExtras(b);
                startActivity(newCryptsyActivity);
//                Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Intent newCoinedUpActivity = new Intent(this, Exchange.class);
                b = new Bundle();
                b.putString("market", "CoinedUp");
                newCoinedUpActivity.putExtras(b);
                startActivity(newCoinedUpActivity);
//                Toast.makeText(this, "RESTRICTED! NO API ACCESS YET!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Intent newCoinseActivity = new Intent(this, Exchange.class);
                b = new Bundle();
                b.putString("market", "Coins-E");
                newCoinseActivity.putExtras(b);
                startActivity(newCoinseActivity);
//                Toast.makeText(this, "RESTRICTED! NO API ACCESS YET!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Intent newBterActivity = new Intent(this, Exchange.class);
                b = new Bundle();
                b.putString("market", "Bter");
                newBterActivity.putExtras(b);
                startActivity(newBterActivity);
//                Toast.makeText(this, "RESTRICTED! NO API ACCESS YET!", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Intent newVircurexActivity = new Intent(this, Exchange.class);
                b = new Bundle();
                b.putString("market", "Vircurex");
                newVircurexActivity.putExtras(b);
                startActivity(newVircurexActivity);
//                Toast.makeText(this, "RESTRICTED! NO API ACCESS YET!", Toast.LENGTH_SHORT).show();
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
//            pDialog = new ProgressDialog(ExchangeList.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//
        }

        private void updateExchangePrice(String exchange, double price){

            int coefficient = 1000;

            if (exchange.equals("cryptsy")){
                exchangePrices.put("Cryptsy", formatPricemBTC(price));
            } else if (exchange.equals("coinedup")){
                exchangePrices.put("CoinedUp", formatPricemBTC(price));
            } else if (exchange.equals("coins-e")){
                exchangePrices.put("Coins-E", formatPricemBTC(price));
            } else if (exchange.equals("bter")){
                exchangePrices.put("Bter", formatPricemBTC(price));
            } else if (exchange.equals("vircurex")){
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

                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject singleMarket = jsonArray.getJSONObject(i);
                        updateExchangePrice(singleMarket.getString("m"), singleMarket.getDouble("l"));
                    }

                    System.out.println("Cryptsy Price: " + exchangePrices.get("Cryptsy"));
                    System.out.println("CoinedUp Price: " + exchangePrices.get("CoinedUp"));
                    System.out.println("Coins-E Price: " + exchangePrices.get("Coins-E"));
                    System.out.println("Bter Price: " + exchangePrices.get("Bter"));
                    System.out.println("Vircurex Price: " + exchangePrices.get("Vircurex"));

                } catch (JSONException e) { e.printStackTrace(); }

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
//             * Updating parsed JSON data into view
//             * */
//            "Cryptsy", "CoinedUp","Coins-E", "Bter", "Vircurex"
            double[] prices = {exchangePrices.get("Cryptsy"), exchangePrices.get("CoinedUp"),
                    exchangePrices.get("Coins-E"), exchangePrices.get("Bter"), exchangePrices.get("Vircurex")};
            setListAdapter(new ExchangeArrayAdapter(ExchangeList.this, EXCHANGES, prices));
        }

        public double formatPricemBTC(Double price){
            DecimalFormat df = new DecimalFormat("0.00000");
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }

}
