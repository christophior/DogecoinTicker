package christophior.dogecointicker.app;

import java.lang.CharSequence;
import java.lang.Double;
import java.lang.Override;
import java.lang.String;
import java.lang.System;
import java.lang.Void;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends Activity {
    private ProgressDialog pDialog;
    private static String urlMarketPrices = "http://doge.yottabyte.nu/json/markets.json";
    public static HashMap<String, Double> exchangePrices = new HashMap<String, Double>();
    static public final String[] EXCHANGES = new String[] { "Cryptsy", "Coins-E", "Bter", "Vircurex"};

    // Keep track of which fragment and time interval we're on
    static protected int currentFragment = 0;
    static protected int currentInterval = 7;
    static protected double dogeConverterValue = 1000;

    AlertDialog levelDialog;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        initHashMap();
        (new GetJson()).execute();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();


		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], 0, true, Double.toString(exchangePrices.get("Cryptsy")) + " mBTC"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], 0, true, Double.toString(exchangePrices.get("Coins-E")) + " mBTC"));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], 0, true, Double.toString(exchangePrices.get("Bter")) + " mBTC"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], 0, true, Double.toString(exchangePrices.get("Vircurex")) + " mBTC"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], 0, true, ""));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for home screen
			displayView(5);
		}
	}

    private void initHashMap(){
        for (String ex : EXCHANGES){
            exchangePrices.put(ex, 0.0);
        }
    }

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
            startActivityForResult(new Intent(this, Settings.class), 0);
			return true;
        case R.id.action_refresh:
            displayView(currentFragment);
            return true;
        case R.id.action_interval:
            intervalDialog();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    public void intervalDialog() {
        final CharSequence[] items = {"1 hour", "3 hours", "12 hours", "24 hours", "3 days", "7 days", "14 days", "30 days"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Interval");
        builder.setSingleChoiceItems(items, currentInterval, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                currentInterval = item;
                levelDialog.dismiss();
                displayView(currentFragment);
            }
        });

        levelDialog = builder.create();
        levelDialog.show();
    }

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        boolean isDonateOrHome = currentFragment == 4 || currentFragment == 5;
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen && !isDonateOrHome);
        menu.findItem(R.id.action_interval).setVisible(!drawerOpen && !isDonateOrHome);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
        (new GetJson()).execute();

        switch (position) {
		case 0:
            currentFragment = position;
			fragment = new ExchangeFragment();
			break;
		case 1:
            currentFragment = position;
			fragment = new ExchangeFragment();
			break;
		case 2:
            currentFragment = position;
			fragment = new ExchangeFragment();
			break;
		case 3:
            currentFragment = position;
			fragment = new ExchangeFragment();
			break;
        case 4:
            currentFragment = position;
            fragment = new DonateFragment();
            break;
        case 5:
            currentFragment = position;
            fragment = new HomeFragment();
            break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private void updateExchangePrice(String exchange, double price){

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
            System.out.println(jsonMarketPrice);

            if (jsonMarketPrice != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonMarketPrice);

                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject singleMarket = jsonArray.getJSONObject(i);
                        updateExchangePrice(singleMarket.getString("m"), singleMarket.getDouble("l"));
                    }

//                    System.out.println("Cryptsy Price: " + exchangePrices.get("Cryptsy"));
//                    System.out.println("Coins-E Price: " + exchangePrices.get("Coins-E"));
//                    System.out.println("Bter Price: " + exchangePrices.get("Bter"));
//                    System.out.println("Vircurex Price: " + exchangePrices.get("Vircurex"));

                } catch (JSONException e) { e.printStackTrace(); }

            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                Log.e("ServiceHandler", urlMarketPrices);
            }

            DecimalFormat menu = new DecimalFormat("#");
            menu.setMinimumFractionDigits(5);
            menu.setMaximumFractionDigits(5);

            String cryptsyMenu = menu.format(exchangePrices.get("Cryptsy"));
//            String coinedupMenu = menu.format(exchangePrices.get("CoinedUp"));
            String coinseMenu = menu.format(exchangePrices.get("Coins-E"));
            String bterMenu = menu.format(exchangePrices.get("Bter"));
            String vircurexMenu = menu.format(exchangePrices.get("Vircurex"));

            navDrawerItems.get(0).setCount(cryptsyMenu + " mBTC");
//            navDrawerItems.get(1).setCount(coinedupMenu + " mBTC");
            navDrawerItems.get(1).setCount(coinseMenu + " mBTC");
            navDrawerItems.get(2).setCount(bterMenu + " mBTC");
            navDrawerItems.get(3).setCount(vircurexMenu + " mBTC");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
        }

        public double formatPricemBTC(Double price){
            DecimalFormat df = new DecimalFormat("0.00000");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(symbols);
            double result = price * 1000;
            String formattedPrice = df.format(result);
            return Double.parseDouble(formattedPrice);
        }
    }

}
