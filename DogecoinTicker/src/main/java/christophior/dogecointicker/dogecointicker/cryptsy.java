package christophior.dogecointicker.dogecointicker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.graphics.Color;
import android.util.*;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.*;


public class cryptsy extends Activity {

    private static String urlCryptsy = "http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=132";
    protected static ArrayList<points> pointList = new ArrayList<points>();
    ArrayList<GraphViewData> GraphData = new ArrayList<GraphViewData>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new GetJson()).execute();
        setContentView(R.layout.activity_cryptsy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cryptsy, menu);

//        System.out.println("creating graph");
//        // init series data
//        GraphViewSeries series = new GraphViewSeries(new GraphViewData[]{
//                new GraphViewData(1, 2.0)
//                , new GraphViewData(2, 1.5)
//                , new GraphViewData(3, 2.5)
//                , new GraphViewData(4, 1.0)}
//        );
//
//        GraphView graphView = new LineGraphView(
//                this // context
//                , "" // heading
//        );
//
//        graphView.addSeries(series); // data
//        // set styles
////        graphView.getGraphViewStyle().setGridColor(Color.GREEN);
////        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
////        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
////        graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.big));
////        graphView.getGraphViewStyle().setNumHorizontalLabels(5);
////        graphView.getGraphViewStyle().setNumVerticalLabels(4);
////        graphView.getGraphViewStyle().setVerticalLabelsWidth(300);
//        // set view port, start=2, size=40
////        graphView.setViewPort(2, 40);
////        graphView.setScrollable(true);
//        // optional - activate scaling / zooming
////        graphView.setScalable(true);
//
//        LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
//        layout.addView(graphView);

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
            pDialog = new ProgressDialog(cryptsy.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
//
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonCryptsy = sh.makeServiceCall(urlCryptsy, ServiceHandler.GET);
            if (jsonCryptsy != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonCryptsy);
                    JSONObject dogeCryptsy = ((jsonObj.getJSONObject("return")).getJSONObject("markets")).getJSONObject("DOGE");
                    String cryptsyPrice = dogeCryptsy.getString("lasttradeprice");
                    System.out.println("Cryptsy Price: " + cryptsyPrice);
                    JSONArray trades = (((jsonObj.getJSONObject("return")).getJSONObject("markets")).getJSONObject("DOGE")).getJSONArray("recenttrades");
                    for (int i = trades.length()-1; i >= 0; i--){
                        JSONObject t = trades.getJSONObject(i);
                        pointList.add(new points(t.getString("time"), t.getDouble("price")));
                        System.out.println("added trade(" + t.getString("id") + ") to list of points");
                    }

                    GraphData = new ArrayList<GraphViewData>();
                    for (points p : pointList){
                        GraphData.add(new GraphViewData(p.time, p.price));
                    }

//                    exchangePrices.put("Cryptsy", formatPricemBTC(cryptsyPrice));
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
            if (pDialog.isShowing())
                pDialog.dismiss();
//            /**
//             * Updating parsed JSON data into view
//             * */
//
            System.out.println("creating graph");
            // init series data
            GraphViewSeries series = new GraphViewSeries(GraphData.toArray(new GraphViewData[GraphData.size()]));

            GraphView graphView = new LineGraphView(
                    cryptsy.this // context
                    , "" // heading
            );

            graphView.addSeries(series); // data
            // set styles
//        graphView.getGraphViewStyle().setGridColor(Color.GREEN);
//        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
//        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//        graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.big));
//        graphView.getGraphViewStyle().setNumHorizontalLabels(5);
//        graphView.getGraphViewStyle().setNumVerticalLabels(4);
//        graphView.getGraphViewStyle().setVerticalLabelsWidth(300);
            // set view port, start=2, size=40
//        graphView.setViewPort(2, 40);
//        graphView.setScrollable(true);
            // optional - activate scaling / zooming
        graphView.setScalable(true);

            LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
            layout.addView(graphView);
        }

        public double formatPricemBTC(String price){
            DecimalFormat df = new DecimalFormat("0.00000");
            double result = Double.parseDouble(price) * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }
}

class points{
    public String date;
    public double price;
    public double time;

    public points(String d, double p){
        date = d;
        price = p;
        time = generateTimeDouble(d);
    }

    public double generateTimeDouble(String d){
        double result = 0;
        String[] splitDate = d.split(":");
        result = Double.parseDouble(splitDate[1]) + (Double.parseDouble(splitDate[2])/60);
        System.out.println("SD1: " + splitDate[1] + " SD2: " + splitDate[2]);
        System.out.println("Double rep: " + result);
        return result;
    }
}
