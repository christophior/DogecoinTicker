package christophior.dogecointicker.app;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Double;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.System;
import java.lang.Void;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExchangeFragment extends Fragment {

    private static String urlExchange = "";
    private static String urlBitcoinPrice = "http://doge.yottabyte.nu/json/fiat.json";

    ArrayList<GraphViewData> GraphData = new ArrayList<GraphView.GraphViewData>();
    private ProgressDialog pDialog;
    public static double highPrice = -1.0, lowPrice = 9999.0, percentChange = 0.0;
    boolean EveryOtherLine = true;
    int startingHour, xAxisCount = 0;
    private String[] markets = {"cryptsy", "coinedup", "coins-e", "bter", "vircurex"};
    private String[] intervals = {"1h", "3h", "12h", "24h", "3d", "7d", "14d", "30d"};
    private String[] interval_labels = {"1 hour", "3 hours", "12 hours", "24 hours",
            "3 days", "7 days", "14 days", "30 days"};
    private double btcValueInUSD = 0;
    private double dogeCurrentValue = 0;
    public double btcConverterValue = 0, usdConverterValue = 0;

    private FragmentTabHost mTabHost;

    public ExchangeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Bundle b = getIntent().getExtras();
        // get API url
//        String market = b.getString("market");
        urlExchange = ("http://doge.yottabyte.nu/json/" + markets[MainActivity.currentFragment] + "/" + intervals[MainActivity.currentInterval]+ ".json");
        // reset stats
        highPrice = -1.0;
        lowPrice = 9999.0;
        percentChange = 0.0;

        EveryOtherLine = true;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        startingHour = Integer.parseInt(df.format(c.getTime())) - 2;
        xAxisCount = 0;

        (new GetJson()).execute();
    }


    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
//
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            System.out.println("in exchange fragment");
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            dogeCurrentValue = MainActivity.exchangePrices.get(MainActivity.EXCHANGES[MainActivity.currentFragment]);
            // Making a request to url and getting response
            String jsonData = sh.makeServiceCall(urlExchange, ServiceHandler.GET);
            String jsonBitcoinPrice = sh.makeServiceCall(urlBitcoinPrice, ServiceHandler.GET);

            if (jsonData != null) {
                try {
                    JSONArray jsonArrayRoot = new JSONArray(jsonData);
                    JSONArray trades = jsonArrayRoot.getJSONArray(0);

                    System.out.println("getting trades");
                    GraphData = new ArrayList<GraphViewData>();

                    for (int i = 0; i < trades.length(); ++i) {
                        JSONArray t = trades.getJSONArray(i);
                        double time = t.getDouble(0), price = 1000 * t.getDouble(1);
                        GraphData.add(new GraphViewData(time, price));

                        // replace price with new format for high and low
                        price = formatPricemBTC(t.getDouble(1));
                        if (price > highPrice)
                            highPrice = price;
                        if (price < lowPrice)
                            lowPrice = price;

//                        System.out.println("added trade(" + time + ", " + price + ") to graph");
//                        System.out.println(time);
                    }
                    double first = (trades.getJSONArray(0)).getDouble(1);
                    double last = (trades.getJSONArray(trades.length() - 1)).getDouble(1);
                    percentChange = (last - first) / (first) * 100;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            if (jsonBitcoinPrice != null){
                try{
                    JSONObject btcPrice = new JSONObject(jsonBitcoinPrice);
                    btcValueInUSD = btcPrice.getDouble("usd");
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
            GraphViewSeries.GraphViewSeriesStyle seriesStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.WHITE, 4);

            GraphViewSeries series = new GraphViewSeries("", seriesStyle, GraphData.toArray(new GraphViewData[GraphData.size()]) );
            GraphView graphView = new LineGraphView(getActivity(), "");

            graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {

                    if (isValueX) {
                        if (EveryOtherLine){
                            EveryOtherLine = false;
                            int hourVal = (startingHour + xAxisCount) % 24;
                            xAxisCount+=2;
                            return hourVal + ":00";
                        } else {
                            EveryOtherLine = true;
                            xAxisCount+=2;
                            return "";
                        }
                    } else {
                        if (EveryOtherLine){
                            EveryOtherLine = false;
                            return null;
                        } else {
                            EveryOtherLine = true;
                            return "";
                        }
                    }
                }
            });

            graphView.addSeries(series); // data
            // set styles
            if (getResources().getConfiguration().orientation == 1) {
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
//              graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
//              graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//                graphView.getGraphViewStyle().setTextSize(100);
                graphView.getGraphViewStyle().setNumHorizontalLabels(1);
                graphView.getGraphViewStyle().setNumVerticalLabels(1);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
//              graphView.setViewPort(2, 40);
//              graphView.setScrollable(true);
                // optional - activate scaling / zooming
//                graphView.setScalable(false);
            } else {
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
//              graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.YELLOW);
//              graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
                graphView.getGraphViewStyle().setTextSize(22);
                graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.DKGRAY);
                graphView.getGraphViewStyle().setVerticalLabelsColor(Color.DKGRAY);
                graphView.getGraphViewStyle().setNumHorizontalLabels(13);
                graphView.getGraphViewStyle().setNumVerticalLabels(15);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(100);
//              graphView.setViewPort(2, 40);
//              graphView.setScrollable(true);
                // optional - activate scaling / zooming
//                graphView.setScalable(false);
            }

            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(5);
            DecimalFormat currency = new DecimalFormat("#");
            currency.setMinimumFractionDigits(2);
            currency.setMaximumFractionDigits(2);

            LinearLayout layout = (LinearLayout)getView().findViewById(R.id.graph);
            layout.addView(graphView);

            if (getResources().getConfiguration().orientation == 1) {
                TextView tv_high = (TextView)getView().findViewById(R.id.high_text);
                tv_high.setText(df.format(highPrice));

                TextView tv_low = (TextView)getView().findViewById(R.id.low_text);
                tv_low.setText(df.format(lowPrice));

                // need to fix current price, using highest price as placeholder
                TextView tv_current = (TextView)getView().findViewById(R.id.current_text);
                tv_current.setText(df.format(dogeCurrentValue));

                TextView tv_change = (TextView)getView().findViewById(R.id.change_text);
                tv_change.setText((new DecimalFormat("0.00")).format(percentChange) + "%");

                // change graph background based on percent change (need to implement for landscape mode)
                if (percentChange < 0) {
                    tv_change.setTextColor(getResources().getColor(R.color.negative_percent_change_text));

                    LinearLayout ll_title = (LinearLayout) getView().findViewById(R.id.title);
                    LinearLayout ll_graph = (LinearLayout) getView().findViewById(R.id.graph);

                    ll_title.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
                    ll_graph.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
                }

                TextView tv_title = (TextView)getView().findViewById(R.id.exchange_title);
                tv_title.setText(markets[MainActivity.currentFragment]);

                TextView tv_interval = (TextView)getView().findViewById(R.id.exchange_interval);
                tv_interval.setText(interval_labels[MainActivity.currentInterval]);

                TextView tv_dogeConverterValue = (TextView)getView().findViewById(R.id.dogecoin_converter_value);
                tv_dogeConverterValue.setText(String.valueOf(MainActivity.dogeConverterValue));

                btcConverterValue = MainActivity.dogeConverterValue*dogeCurrentValue/1000;
                usdConverterValue = btcConverterValue * btcValueInUSD;

                TextView tv_btcConverterValue = (TextView)getView().findViewById(R.id.btc_converter_value);
                TextView tv_usdConverterValue = (TextView)getView().findViewById(R.id.usd_converter_value);
                tv_btcConverterValue.setText(String.valueOf(btcConverterValue));
                String usdValueString = currency.format(usdConverterValue);
                tv_usdConverterValue.setText("$"+usdValueString);

                TextView tv_btcLabel = (TextView)getView().findViewById(R.id.btc_converter_label);
                String bitcoin_dollar_value = String.valueOf((int)btcValueInUSD);
                tv_btcLabel.setText(getResources().getText(R.string.btc_converter_label) + " ($" + bitcoin_dollar_value + ")");

            } else {
                if (percentChange < 0) {
                    LinearLayout ll_graph = (LinearLayout) getView().findViewById(R.id.graph);
                    ll_graph.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
                }
            }
        }


        public double formatPricemBTC(Double price) {
            DecimalFormat df = new DecimalFormat("0.00000");
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }
}
