package christophior.dogecointicker.dogecointicker;

import christophior.dogecointicker.dogecointicker.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ExchangeArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final double[] prices;

    public ExchangeArrayAdapter(Context context, String[] values, double[] prices) {
        super(context, R.layout.activity_exchange_list, values);
        this.context = context;
        this.values = values;
        this.prices = prices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_exchange_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView textViewPrice = (TextView) rowView.findViewById(R.id.label_price);

//        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        textView.setText(values[position]);
        textViewPrice.setText(prices[position] + " Satoshi");

        // Change icon based on name

//        String s = values[position];
//        System.out.println(s);
//
//        if (s.equals("Cryptsy")) {
//            imageView.setImageResource(R.drawable.cryptsy_logo);
//        } else if (s.equals("CoinedUp")) {
//            imageView.setImageResource(R.drawable.coinedup_logo);
//        } else if (s.equals("Coins-E")) {
//            imageView.setImageResource(R.drawable.coinse_logo);
//        } else if (s.equals("Bter")) {
//            imageView.setImageResource(R.drawable.bter_logo);
//        } else {
//            imageView.setImageResource(R.drawable.vircurex_logo);
//        }

        return rowView;
    }
}