package christophior.dogecointicker.app;

/**
 * Created by ChrisV on 4/29/14.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DonateFragment extends Fragment {

    public DonateFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_donate, container, false);
        setHasOptionsMenu(false);
        return rootView;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
//    }
}