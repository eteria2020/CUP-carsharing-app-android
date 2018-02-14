package it.handroix.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import it.handroix.core.base.HdxBaseFragment;
import it.handroix.core.proxy.HdxFragmentProxy;

public class HdxFragmentLiteMapHelper implements HdxFragmentProxy {

    private Context mContext;

    private HdxBaseFragment mFragment;

    private MapView mMapView;
    private FrameLayout mView;

    private GoogleMap mMap;

    public static HdxFragmentLiteMapHelper newInstance(Context context,HdxBaseFragment fragment) {
        HdxFragmentLiteMapHelper mInstance = new HdxFragmentLiteMapHelper(context, fragment);
        mInstance.mFragment.addProxy(mInstance);
        return mInstance;
    }

    private HdxFragmentLiteMapHelper(Context context, HdxBaseFragment fragment) {
        this.mContext = context;
        this.mFragment = fragment;
    }

    public void setupLiteMap(FrameLayout mapViewContainer, final OnMapReadyCallback mapReadyCallback, Bundle savedInstanceState){
        mView = mapViewContainer;
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.liteMode(true);
        mMapView = new MapView(mContext,mapOptions);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(lp);
        mView.addView(mMapView);
        MapsInitializer.initialize(mContext);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });

        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mMapView.getHeight() > 0 && mMapView.getWidth() > 0) {

                    if (mMap != null) {
                        mapReadyCallback.onMapReady(mMap);

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            // before Jelly Bean:
                            mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            // for Jelly Bean and later:
                            mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            }
        });
    }

    public void onAttach(Context context){
        if (mContext == null) mContext = context;
    }

    public void onDetach(){
        mContext = null;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {}

    @Override
    public void onSaveInstanceState(Bundle bundle) {}

    @Override
    public void onRequestPermissionsResult(int i, String[] strings, int[] ints) {}

    @Override
    public void onConfigurationChanged(Configuration configuration) {}

    @Override
    public void onStart(){};

    @Override
    public void onStop(){};
}
