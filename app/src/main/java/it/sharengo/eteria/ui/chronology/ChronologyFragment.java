package it.sharengo.development.ui.chronology;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.activities.BaseActivity;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;


public class ChronologyFragment extends BaseMvpFragment<ChronologyPresenter> implements ChronologyMvpView {

    private static final String TAG = ChronologyFragment.class.getSimpleName();

    private ChronologyAdapter mAdapter;
    private List<Trip> mTripsList;
    private LongOperation longOperation;

    @BindView(R.id.chronRecyclerView)
    RecyclerView mRv;

    @BindView(R.id.emptyChronLayout)
    ViewGroup emptyChronLayout;

    @BindView(R.id.progressList)
    ViewGroup progressList;

    @BindView(R.id.progressBarList)
    ProgressBar progressBarList;


    public static ChronologyFragment newInstance() {
        ChronologyFragment fragment = new ChronologyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new ChronologyAdapter(mActionListener, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chronology, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        try {
            ((BaseActivity) getActivity()).showLoadingChronology();
        }catch (OutOfMemoryError e){}

        mRv.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lm.setSmoothScrollbarEnabled(false);
        mRv.setLayoutManager(lm);
        mRv.setAdapter(mAdapter);
        //mRv.addItemDecoration(new DividerItemDecoration(mRv.getContext(), lm.getOrientation()));


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mPresenter.getTrips();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(longOperation != null) longOperation.cancel(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LISTENERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private ChronologyAdapter.OnItemActionListener mActionListener = new ChronologyAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(Trip trip) {

        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show activity of Google Maps.
     */

    @OnClick(R.id.goToMapButton)
    public void onMapClick(){
        //Navigator.launchMap(this, Navigator.REQUEST_MAP_DEFAULT);
        Navigator.launchMapGoogle(this, Navigator.REQUEST_MAP_DEFAULT);
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Show user trip performed.
     *
     * @param  tripList  user trip to show
     */
    @Override
    public void showList(final List<Trip> tripList) {
        emptyChronLayout.setVisibility(View.GONE);
        mRv.setVisibility(View.VISIBLE);

        //((BaseActivity) getActivity()).showLoading();

        mTripsList = tripList;
        longOperation = new LongOperation();
        longOperation.execute();

    }

    /**
     * Show empty view if not exist user trips.
     */
    @Override
    public void showEmptyResult(){
        emptyChronLayout.setVisibility(View.VISIBLE);
        mRv.setVisibility(View.GONE);
        ((BaseActivity) getActivity()).hideLoadingChronology();
    }

    /**
     * Show dialog if there's a network error.
     */
    @Override
    public void showChronError(Throwable e){
        //Mostro un messaggio di errore
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.error_msg_network_general),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                Navigator.launchHome(ChronologyFragment.this);
                getActivity().finish();
            }
        });
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            for (int i = 0; i < 1; i++) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            //progressBarList.setVisibility(View.GONE);
            if((BaseActivity) getActivity() != null)
            ((BaseActivity) getActivity()).hideLoadingChronology();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    mAdapter.setData(mTripsList);
                    progressList.setVisibility(View.GONE);
                }
            }, 100);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
