package it.sharengo.development.ui.tripend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class TripEndFragment extends BaseMvpFragment<TripEndPresenter> implements TripEndMvpView {

    private static final String TAG = TripEndFragment.class.getSimpleName();

    public static final String ARG_CO2 = "ARG_CO2";

    private float co2 = 0;

    @BindView(R.id.cos2TextView)
    TextView cos2TextView;

    public static TripEndFragment newInstance(float mCo2) {
        TripEndFragment fragment = new TripEndFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_CO2, mCo2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            co2 = getArguments().getFloat(ARG_CO2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_end, container, false);
        mUnbinder = ButterKnife.bind(this, view);


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

    /**
     * Close current view and open Chronology.
     */
    @OnClick(R.id.historicButton)
    public void historicButton(){
        Navigator.launchChronology(this);
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
