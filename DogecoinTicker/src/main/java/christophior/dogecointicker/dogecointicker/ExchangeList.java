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
    private static String url = "http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=132";

    protected double price;
    // contacts JSONArray
//    JSONArray contacts = null;
    JSONArray jsonarray = null;

    // Hashmap for ListView
//    ArrayList<HashMap<String, String>> contactList;
    ArrayList<HashMap<String, String>> jsonList;


    public final String[] EXCHANGES = new String[] { "Cryptsy", "CoinedUp",
            "Coins-E", "Bter", "Vircurex"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetJson().execute();

        double[] prices = {price, 2.0, 3.0, 4.0, 5.0};
        setListAdapter(new ExchangeArrayAdapter(this, EXCHANGES, prices));
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
//        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

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
            price = 7.0;

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
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            System.out.println("Response: > " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
//                    jsonarray = jsonObj.getJSONArray("return");
                    JSONObject root = ((jsonObj.getJSONObject("return")).getJSONObject("markets")).getJSONObject("DOGE");
                    String priceString = root.getString("lasttradeprice");
                    System.out.println(priceString);
                    price = 8.0;
                    // looping through All Contacts
//                    for (int i = 0; i < jsonarray.length(); i++) {
//                        JSONObject c = jsonarray.getJSONObject(i);

//                        String id = c.getString(TAG_ID);
//                        String name = c.getString(TAG_NAME);
//                        String email = c.getString(TAG_EMAIL);
//                        String address = c.getString(TAG_ADDRESS);
//                        String gender = c.getString(TAG_GENDER);
//
//                        // Phone node is JSON Object
//                        JSONObject phone = c.getJSONObject(TAG_PHONE);
//                        String mobile = phone.getString(TAG_PHONE_MOBILE);
//                        String home = phone.getString(TAG_PHONE_HOME);
//                        String office = phone.getString(TAG_PHONE_OFFICE);
//
//                        // tmp hashmap for single contact
//                        HashMap<String, String> contact = new HashMap<String, String>();
//
//                        // adding each child node to HashMap key => value
//                        contact.put(TAG_ID, id);
//                        contact.put(TAG_NAME, name);
//                        contact.put(TAG_EMAIL, email);
//                        contact.put(TAG_PHONE_MOBILE, mobile);
//
//                        // adding contact to contact list
//                        jsonList.add(contact);
//                    }
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
