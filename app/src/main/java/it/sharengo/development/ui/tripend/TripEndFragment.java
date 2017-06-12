package it.sharengo.development.ui.tripend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class TripEndFragment extends BaseMvpFragment<TripEndPresenter> implements TripEndMvpView {

    private static final String TAG = TripEndFragment.class.getSimpleName();

    public static final String ARG_TIMESTAMP = "ARG_POST_ID";

    private int timestamp = 0;

    @BindView(R.id.cos2TextView)
    TextView cos2TextView;

    public static TripEndFragment newInstance(int timestamp) {
        TripEndFragment fragment = new TripEndFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TIMESTAMP, timestamp);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            timestamp = getArguments().getInt(ARG_TIMESTAMP);
            Log.w("timestamp",": "+timestamp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_end, container, false);
        mUnbinder = ButterKnife.bind(this, view);




        long unixTime = System.currentTimeMillis() / 1000L;
        Log.w("unixTime",": "+unixTime);

        int diffTime = (int) (unixTime - timestamp);
        Log.w("diffTime",": "+diffTime);

        float co2 = ((float) diffTime)/60/60*17*106;

        Log.w("co2",": "+co2);

        cos2TextView.setText(String.format(getString(R.string.tripend_text3_label), co2));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
