package it.sharengo.development.ui.mapgoogle;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.handroix.map.HdxFragmentMapHelper;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.base.map.BaseMapFragment;


public class MapGoogleFragment extends BaseMapFragment<MapGooglePresenter> implements MapGoogleMvpView, OnMapReadyCallback {

    private static final String TAG = MapGoogleFragment.class.getSimpleName();

    @BindView(R.id.map_container)
    FrameLayout mMapContainer;

    public static MapGoogleFragment newInstance() {
        MapGoogleFragment fragment = new MapGoogleFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_google, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mMapHelper = HdxFragmentMapHelper.newInstance(getActivity(), this);
        mMapHelper.setupMap(mMapContainer, this, savedInstanceState);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        mPresenter.onMapIsReady();
    }

    @Override
    public void onNewLocation(Location location) {
        super.onNewLocation(location);
        mPresenter.onLocationIsReady(location.getLatitude(), location.getLongitude());
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
