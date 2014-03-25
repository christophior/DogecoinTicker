package christophior.dogecointicker.dogecointicker;

import android.app.Activity;
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


public class cryptsy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptsy);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cryptsy, menu);

        System.out.println("creating graph");
        // init series data
        GraphViewSeries series = new GraphViewSeries(new GraphViewData[] {
                  new GraphViewData(1, 2.0d)
                , new GraphViewData(2, 1.5d)
                , new GraphViewData(3, 2.5d)
                , new GraphViewData(4, 1.0d)}
        );

        GraphView graphView = new LineGraphView(
                this // context
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
//        graphView.setScalable(true);

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
        layout.addView(graphView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
