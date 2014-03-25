package christophior.dogecointicker.dogecointicker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Zach on 3/24/2014.
 */
public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("ttt_prefs");
        addPreferencesFromResource(R.xml.preferences);
    }

}
