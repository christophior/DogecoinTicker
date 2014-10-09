package christophior.dogecointicker.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import android.view.View.OnClickListener;
import java.text.DateFormat;
import java.util.Date;


public class ExchangeFragment extends Fragment implements OnClickListener {

    private static String urlExchange = "";
    private static String urlBitcoinPrice = "http://doge.yottabyte.nu/json/fiat.json";

    DecimalFormat addCommas = new DecimalFormat("#,###");
    DecimalFormat twoDigits = new DecimalFormat("#,###.00");


    ArrayList<GraphViewData> GraphData = new ArrayList<GraphView.GraphViewData>();
    private ProgressDialog pDialog;
    public static double highPrice = -1.0, lowPrice = 9999.0, percentChange = 0.0;
    boolean EveryOtherLine = true;
    int startingHour, xAxisCount = 0;
    private String[] markets = {"cryptsy", "coins-e", "bter", "vircurex"};
    private String[] intervals = {"1h", "3h", "12h", "24h", "3d", "7d", "14d", "30d"};
    private String[] interval_labels = {"1 hour", "3 hours", "12 hours", "24 hours",
            "3 days", "7 days", "14 days", "30 days"};
    private double btcValueInUSD = 0;
    private double dogeCurrentValue = 0;
    public double btcConverterValue = 0, usdConverterValue = 0;

    public ExchangeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);

        if (getResources().getConfiguration().orientation == 1) {
            TextView DogeConverterValue = (TextView) rootView.findViewById(R.id.dogecoin_converter_value);
            DogeConverterValue.setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dogecoin_converter_value) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setMessage("Enter your amount of Doge.");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    MainActivity.dogeConverterValue = Double.parseDouble(value);

                    DecimalFormat sixDigitsOrLess = new DecimalFormat("#");
                    sixDigitsOrLess.setMaximumFractionDigits(6);

                    TextView tv_dogeConverterValue = (TextView)getView().findViewById(R.id.dogecoin_converter_value);
                    tv_dogeConverterValue.setText(addCommas.format((int)MainActivity.dogeConverterValue));

                    btcConverterValue = MainActivity.dogeConverterValue*dogeCurrentValue/1000;
                    usdConverterValue = btcConverterValue * btcValueInUSD;

                    TextView tv_btcConverterValue = (TextView)getView().findViewById(R.id.btc_converter_value);
                    TextView tv_usdConverterValue = (TextView)getView().findViewById(R.id.usd_converter_value);
                    tv_btcConverterValue.setText(String.valueOf(sixDigitsOrLess.format(btcConverterValue)));
                    String usdValueString = twoDigits.format(usdConverterValue);
                    tv_usdConverterValue.setText("$"+usdValueString);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
        }
    }

    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // lock rotation while getting data
            mLockScreenRotation();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            System.out.println("in exchange fragment");
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            dogeCurrentValue = MainActivity.exchangePrices.get(MainActivity.EXCHANGES[MainActivity.currentFragment]);

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
                    }

                    double first = (trades.getJSONArray(0)).getDouble(1);
                    double last = (trades.getJSONArray(trades.length() - 1)).getDouble(1);
                    percentChange = (last - first) / (first) * 100;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                Log.e("ServiceHandler", urlExchange);
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
                Log.e("ServiceHandler", urlBitcoinPrice);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            System.out.println("creating graph");

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
                            xAxisCount+=2;
                            SimpleDateFormat sim = null;
                            long time = (long)value;
                            Date date = new Date(time);

                            switch (MainActivity.currentInterval){
                                case 0: //1h
                                    sim = new SimpleDateFormat("kk:mm");
                                    break;
                                case 1: //3h
                                    sim = new SimpleDateFormat("kk:mm");
                                    break;
                                case 2: //12h
                                    sim = new SimpleDateFormat("kk:00");
                                    break;
                                case 3: //24h
                                    sim = new SimpleDateFormat("kk:00");
                                    break;
                                case 4: //3d
                                    sim = new SimpleDateFormat("MM/dd kk:00");
                                    break;
                                case 5: //7d
                                    sim = new SimpleDateFormat("MM/dd kk:00");
                                    break;
                                case 6: //14d
                                    sim = new SimpleDateFormat("MMM dd");
                                    break;
                                case 7: //30d
                                    sim = new SimpleDateFormat("MMM dd");
                                    break;
                                default:
                                    sim = new SimpleDateFormat("kk:00");
                                    break;
                            }

                            return sim.format(date);
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
//              ((LineGraphView)graphView).setDrawBackground(true);
//              ((LineGraphView)graphView).setBackgroundColor(Color.rgb(185, 224, 123));
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
                graphView.getGraphViewStyle().setNumHorizontalLabels(1);
                graphView.getGraphViewStyle().setNumVerticalLabels(1);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(1);
//                graphView.setViewPort(2, 40);
//                graphView.setScrollable(true);
//                graphView.setScalable(false);
            } else {
                graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
                graphView.getGraphViewStyle().setTextSize(22);
                graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
                graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
                graphView.getGraphViewStyle().setNumHorizontalLabels(13);
                graphView.getGraphViewStyle().setNumVerticalLabels(15);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(100);
//                graphView.setViewPort(2, 40);
//                graphView.setScrollable(true);
//                graphView.setScalable(false);
            }

            DecimalFormat fiveDigitsOrLess = new DecimalFormat("#");
            fiveDigitsOrLess.setMaximumFractionDigits(5);

            DecimalFormat sixDigitsOrLess = new DecimalFormat("#");
            sixDigitsOrLess.setMaximumFractionDigits(6);

            LinearLayout layout = (LinearLayout)getView().findViewById(R.id.graph);
            layout.addView(graphView);

            if (getResources().getConfiguration().orientation == 1) {
                TextView tv_high = (TextView)getView().findViewById(R.id.high_text);
                tv_high.setText(sixDigitsOrLess.format(highPrice));

                TextView tv_low = (TextView)getView().findViewById(R.id.low_text);
                tv_low.setText(sixDigitsOrLess.format(lowPrice));

                // need to fix current price, using highest price as placeholder
                TextView tv_current = (TextView)getView().findViewById(R.id.current_text);
                tv_current.setText(sixDigitsOrLess.format(dogeCurrentValue));

                TextView tv_change = (TextView)getView().findViewById(R.id.change_text);
                tv_change.setText((new DecimalFormat("0.00")).format(percentChange) + "%");

                // change graph background based on percent change (need to implement for landscape mode)
                if (percentChange < 0) {
                    tv_change.setTextColor(getResources().getColor(R.color.negative_percent_change_text));

                    LinearLayout ll_title = (LinearLayout) getView().findViewById(R.id.title);
                    LinearLayout ll_graph = (LinearLayout) getView().findViewById(R.id.graph);

                    ll_title.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
                    ll_graph.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
//                    ((LineGraphView)graphView).setBackgroundColor(Color.rgb(247, 115, 101));
                }

                TextView tv_title = (TextView)getView().findViewById(R.id.exchange_title);
                tv_title.setText(markets[MainActivity.currentFragment]);

                TextView tv_interval = (TextView)getView().findViewById(R.id.exchange_interval);
                tv_interval.setText(interval_labels[MainActivity.currentInterval]);

                TextView tv_dogeConverterValue = (TextView)getView().findViewById(R.id.dogecoin_converter_value);
                tv_dogeConverterValue.setText(addCommas.format((int)MainActivity.dogeConverterValue));

                btcConverterValue = MainActivity.dogeConverterValue*dogeCurrentValue/1000;
                usdConverterValue = btcConverterValue * btcValueInUSD;

                TextView tv_btcConverterValue = (TextView)getView().findViewById(R.id.btc_converter_value);
                TextView tv_usdConverterValue = (TextView)getView().findViewById(R.id.usd_converter_value);
                tv_btcConverterValue.setText(String.valueOf(sixDigitsOrLess.format(btcConverterValue)));
                String usdValueString = twoDigits.format(usdConverterValue);
                tv_usdConverterValue.setText("$"+usdValueString);

                TextView tv_btcLabel = (TextView)getView().findViewById(R.id.btc_converter_label);
                String bitcoin_dollar_value = String.valueOf((int)btcValueInUSD);
                tv_btcLabel.setText(getResources().getText(R.string.btc_converter_label) + " ($" + bitcoin_dollar_value + ")");

            } else {
                if (percentChange < 0) {
                    LinearLayout ll_graph = (LinearLayout) getView().findViewById(R.id.graph);
                    ll_graph.setBackgroundColor(getResources().getColor(R.color.graph_background_color2));
//                    ((LineGraphView)graphView).setBackgroundColor(Color.rgb(185, 224, 123));
                }
            }

            // unlock rotation
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        public double formatPricemBTC(Double price) {
            DecimalFormat df = new DecimalFormat("0.00000");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(symbols);
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }

        private void mLockScreenRotation() {
            switch (getResources().getConfiguration().orientation){
                case Configuration.ORIENTATION_PORTRAIT:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }
        }
    }
}
