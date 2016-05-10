package madwhale.g82.com.anstagram_gangnam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 8-C
 * https://goo.gl/91iKsb
 *
 */
public class TimelineFragment extends Fragment {


    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View baseView = inflater.inflate(R.layout.fragment_timeline, container, false);


        return baseView;
    }


}
