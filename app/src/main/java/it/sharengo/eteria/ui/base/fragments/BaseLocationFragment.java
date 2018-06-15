package it.sharengo.eteria.ui.base.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import it.handroix.core.base.HdxBaseActivity;
import it.handroix.location.HdxActivityLocationHelper;
import it.handroix.location.HdxLocationUpdateListener;
import it.sharengo.eteria.ui.base.map.BaseMapFragment;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;

/**
 * Created by Fulvio on 03/04/2018.
 */

public abstract class BaseLocationFragment<T extends BasePresenter> extends BaseMvpFragment<T> implements HdxLocationUpdateListener {



    protected HdxActivityLocationHelper mHdxActivityLocationHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mHdxActivityLocationHelper = HdxActivityLocationHelper.newInstance((HdxBaseActivity) getActivity());
        mHdxActivityLocationHelper.setupLocationOnDemandMode(this);
    }

    @Override
    public void onNewLocation(Location location) {

    }

    @Override
    public void onLocationUnavailable() {

    }

    @Override
    public void onRequestRationalFineLocationPermission() {

    }

    @Override
    public void onFineLocationDenied() {

    }
}
