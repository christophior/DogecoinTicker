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


public class ExchangeList extends ListActivity {

    static final String[] EXCHANGES = new String[] { "Cryptsy", "CoinedUp",
            "Coins-E", "Bter", "Vircurex"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ExchangeArrayAdapter(this, EXCHANGES));
        createNotification();
    }

    public void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, cryptsy.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(cryptsy.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_exchange_list);
//    }

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
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(this, Settings.class), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
