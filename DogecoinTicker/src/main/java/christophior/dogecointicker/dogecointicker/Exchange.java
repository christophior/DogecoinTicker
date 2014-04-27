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


import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.*;


public class Exchange extends Activity {

    private static String urlExchange = "";
    ArrayList<GraphViewData> GraphData = new ArrayList<GraphViewData>();
    private ProgressDialog pDialog;
    public static double highPrice = -1.0, lowPrice = 9999.0, percentChange = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        // get API url
        String market = b.getString("market");
        urlExchange = ("http://doge.yottabyte.nu/json/" + market.toLowerCase() + "/24h.json");
        setTitle(market);
        // reset stats
        highPrice = -1.0;
        lowPrice = 9999.0;
        percentChange = 0.0;

        (new GetJson()).execute();
        setContentView(R.layout.activity_exchange);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            pDialog = new ProgressDialog(Exchange.this);
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
            String jsonData = sh.makeServiceCall(urlExchange, ServiceHandler.GET);
            if (jsonData != null) {
                try {
                    JSONArray jsonArrayRoot = new JSONArray(jsonData);
                    JSONArray trades = jsonArrayRoot.getJSONArray(0);

                    System.out.println("getting trades");
                    GraphData = new ArrayList<GraphViewData>();

                    for (int i=0; i<trades.length(); ++i){
                        JSONArray t = trades.getJSONArray(i);
                        double time = t.getDouble(0), price = 1000*t.getDouble(1);
                        time /= 10000;
                        time -= 139800000;
                        GraphData.add(new GraphViewData(time, price));

                        // replace price with new format for high and low
                        price = formatPricemBTC(t.getDouble(1));
                        if (price > highPrice)
                            highPrice = price;
                        if (price < lowPrice)
                            lowPrice = price;

                        System.out.println("added trade(" + time + ", " + price + ") to graph");
                    }
                    double first = (trades.getJSONArray(0)).getDouble(1);
                    double last = (trades.getJSONArray(trades.length()-1)).getDouble(1);
                    percentChange = (last-first)/(first)*100;

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
//            GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
//            seriesStyle.setValueDependentColor(new ValueDependentColor() {
//                @Override
//                public int get(GraphViewDataInterface data) {
//                    return Color.rgb(0, 153, 0);
//                }
//            });

            // init series data
            GraphViewSeries series = new GraphViewSeries(GraphData.toArray(new GraphViewData[GraphData.size()]) );
            GraphView graphView = new LineGraphView(
                    Exchange.this // context
                    , "" // heading
            );

            graphView.addSeries(series); // data
            // set styles
            if (getResources().getConfiguration().orientation == 1) {
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
//              graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
//              graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//                graphView.getGraphViewStyle().setTextSize(100);
                graphView.getGraphViewStyle().setNumHorizontalLabels(1);
                graphView.getGraphViewStyle().setNumVerticalLabels(15);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
//              graphView.setViewPort(2, 40);
//              graphView.setScrollable(true);
                // optional - activate scaling / zooming
//                graphView.setScalable(false);
            } else {
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
//              graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
//              graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//                graphView.getGraphViewStyle().setTextSize(0);
                graphView.getGraphViewStyle().setNumHorizontalLabels(12);
                graphView.getGraphViewStyle().setNumVerticalLabels(15);
//                graphView.getGraphViewStyle().setVerticalLabelsWidth(0);
//              graphView.setViewPort(2, 40);
//              graphView.setScrollable(true);
            // optional - activate scaling / zooming
//                graphView.setScalable(false);
            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
            layout.addView(graphView);

            if (getResources().getConfiguration().orientation == 1) {
                TextView tv_high = (TextView) findViewById(R.id.high_text);
                tv_high.setText(String.valueOf(highPrice));

                TextView tv_low = (TextView) findViewById(R.id.low_text);
                tv_low.setText(String.valueOf(lowPrice));

                TextView tv_change = (TextView) findViewById(R.id.change_text);
                tv_change.setText((new DecimalFormat("0.00")).format(percentChange) + "%");
            }
        }

        public double formatPricemBTC(Double price){
            DecimalFormat df = new DecimalFormat("0.00000");
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }
}
