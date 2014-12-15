package christophior.dogecointicker.app;

/**
 * Created by ChrisV on 4/29/14.
 */
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DonateFragment extends Fragment {

    private static Button swap = null;
    private static boolean inAboutView = false;


    public DonateFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_donate, container, false);
        setHasOptionsMenu(false);

        final LinearLayout barCode = (LinearLayout)rootView.findViewById(R.id.barcode_container);
        final LinearLayout aboutView = (LinearLayout) rootView.findViewById(R.id.about_container);

        swap = (Button)rootView.findViewById(R.id.flip_button);
        swap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(inAboutView){
                    animate(barCode);
                    barCode.setVisibility(View.VISIBLE);
                    aboutView.setVisibility(View.INVISIBLE);
                }else{
                    animate(aboutView);
                    barCode.setVisibility(View.INVISIBLE);
                    aboutView.setVisibility(View.VISIBLE);
                }
                inAboutView = !inAboutView;
            }

            //TODO:Long click listener here...


        });


        swap.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Address is now in your clipboard :)", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Hash", "DST5CnZWs8VMY9gBdKiMKTdkusR2ta6RRn");
                clipboard.setPrimaryClip(clip);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public void animate(View view) {
        Animation slideInAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        slideInAnimation.setDuration(500);

        switch (view.getId()){
            case R.id.barcode_container:
                view.startAnimation(slideInAnimation);
                break;

            case R.id.about_container:
                view.startAnimation(slideInAnimation);
                break;
        }
        view.startAnimation(slideInAnimation);
    }
}
