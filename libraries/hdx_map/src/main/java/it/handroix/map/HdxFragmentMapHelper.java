package it.handroix.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapView;
import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLngBounds;

import it.handroix.core.base.HdxBaseFragment;
import it.handroix.core.proxy.HdxFragmentProxy;

public class HdxFragmentMapHelper implements HdxFragmentProxy {

    private Context mContext;

    private HdxBaseFragment mFragment;

    private MapView mMapView;
    private GoogleMap mMap;
    private FrameLayout mView;

    private ClusterOptionsProvider mClusterOptionsProvider;
    private boolean mStandardClusteringActive;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;


    //PAN LISTENER
    public interface MapPanListener{
        void onNewMapCenter(LatLngBounds bounds);
    }
    private MapPanListener mMapPanListener;
    private boolean mMapActive;

    public static HdxFragmentMapHelper newInstance(Context context,HdxBaseFragment fragment) {
        HdxFragmentMapHelper mInstance = new HdxFragmentMapHelper(context, fragment);
        mInstance.mFragment.addProxy(mInstance);
        return mInstance;
    }

    private HdxFragmentMapHelper(Context context,HdxBaseFragment fragment) {
        this.mContext = context;
        this.mFragment = fragment;
    }

    public void setupMap(FrameLayout mapViewContainer, final OnMapReadyCallback mapReadyCallback, Bundle savedInstanceState){
        mView = mapViewContainer;

        mMapView = new MapView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(lp);
        mView.addView(mMapView);
        MapsInitializer.initialize(mContext);
        mMapView.onCreate(savedInstanceState);


        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
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

                        mOnGlobalLayoutListener = null;
                    }
                }
            }
        };


        mMapView.getExtendedMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mMapView.getHeight() > 0 && mMapView.getWidth() > 0) {
                    mapReadyCallback.onMapReady(mMap);
                    if (mOnGlobalLayoutListener != null) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            // before Jelly Bean:
                            mView.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
                        } else {
                            // for Jelly Bean and later:
                            mView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        }
                    }
                }
            }
        });

        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        mStandardClusteringActive = Boolean.FALSE;
    }

    public void setupStandardClustering(ClusterOptionsProvider clusterOptionsProvider){
        if(mMap==null){
            throw new IllegalStateException("map not ready, wait for onMapReady before setup clustering");
        }
        mClusterOptionsProvider = clusterOptionsProvider;
        ClusteringSettings clusteringSettings = new ClusteringSettings();
        clusteringSettings.addMarkersDynamically(true);
        clusteringSettings.enabled(false);
        clusteringSettings.clusterOptionsProvider(mClusterOptionsProvider);
        clusteringSettings.minMarkersCount(5);
        mMap.setClustering(clusteringSettings);
        mStandardClusteringActive = Boolean.FALSE;
    }

    public void toggleStandardClustering(boolean enabled){

        if(mMap==null){
            throw new IllegalStateException("map not ready, wait for onMapReady before setup clustering");
        }

        if(mClusterOptionsProvider==null){
            throw new IllegalStateException("missing clusterOptionProvider, call setupStandardClustering before toggle clustering");
        }

        if(enabled){
            if(!mStandardClusteringActive){
                ClusteringSettings clusteringSettings = new ClusteringSettings();
                clusteringSettings.addMarkersDynamically(true);
                clusteringSettings.enabled(true);
                clusteringSettings.clusterOptionsProvider(mClusterOptionsProvider);
                clusteringSettings.minMarkersCount(5);
                mMap.setClustering(clusteringSettings);
                mStandardClusteringActive = Boolean.TRUE;
            }
        }else {
            if(mStandardClusteringActive){
                ClusteringSettings clusteringSettings = new ClusteringSettings();
                clusteringSettings.addMarkersDynamically(true);
                clusteringSettings.enabled(false);
                clusteringSettings.clusterOptionsProvider(mClusterOptionsProvider);
                clusteringSettings.minMarkersCount(5);
                mMap.setClustering(clusteringSettings);
                mStandardClusteringActive = Boolean.FALSE;
            }
        }
    }

    public void setMapPanListener(Context context,MapPanListener mapPanListener,long delay){
        TouchableWrapper touchView = new TouchableWrapper(context,delay);
        mMapPanListener = mapPanListener;
        mView.removeView(mMapView);
        touchView.addView(mMapView);
        mView.addView(touchView);
    }

    private class TouchableWrapper extends FrameLayout {

        private static final int PAN_MSG = 1;

        private long delay;
        private LatLngBounds mCachedBounds;

        public TouchableWrapper(Context context,long delay) {
            super(context);
            this.delay = delay;
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == PAN_MSG && mMapActive){
                    LatLngBounds currentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                    if((mCachedBounds.getCenter().latitude != currentBounds.getCenter().latitude ||
                            mCachedBounds.getCenter().longitude != currentBounds.getCenter().longitude)){

                        mMapPanListener.onNewMapCenter(currentBounds);
                    }

                }
            }
        };

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeMessages(PAN_MSG);
                    mCachedBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.sendEmptyMessageDelayed(PAN_MSG, delay);
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    public void onAttach(Context context){
        if (mContext == null) mContext = context;
    }

    public void onDetach(){
        mContext = null;
    }

    @Override
    public void onResume() {
        mMapActive = true;
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapActive = false;
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
