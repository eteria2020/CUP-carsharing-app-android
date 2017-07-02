package it.sharengo.development.ui.chronology;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;


public class ChronologyFragment extends BaseMvpFragment<ChronologyPresenter> implements ChronologyMvpView {

    private static final String TAG = ChronologyFragment.class.getSimpleName();

    private ChronologyAdapter mAdapter;

    @BindView(R.id.chronRecyclerView)
    RecyclerView mRv;

    @BindView(R.id.emptyChronLayout)
    ViewGroup emptyChronLayout;

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

        mPresenter.getTrips();
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
    @OnClick(R.id.goToMapButton)
    public void onMapClick(){
        Navigator.launchMap(this);
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showList(List<Trip> tripList) {
        emptyChronLayout.setVisibility(View.GONE);
        mRv.setVisibility(View.VISIBLE);
        mAdapter.setData(tripList);
    }

    @Override
    public void showEmptyResult(){
        emptyChronLayout.setVisibility(View.VISIBLE);
        mRv.setVisibility(View.GONE);
    }


}
