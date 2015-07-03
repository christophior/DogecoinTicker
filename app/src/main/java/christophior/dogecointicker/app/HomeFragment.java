package christophior.dogecointicker.app;

/**
 * Created by Christophior on 4/29/14.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(false);
        return rootView;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
//    }
}