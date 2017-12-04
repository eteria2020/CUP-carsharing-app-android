package it.handroix.core.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import it.handroix.core.proxy.HdxFragmentProxy;

/**
 * Created by andrealucibello on 05/06/14.
 */
public class HdxBaseFragment extends Fragment {


    private List<HdxFragmentProxy> mProxies;
    protected Context mContext;

    public void addProxy(HdxFragmentProxy proxy){
        if(mProxies==null){
            mProxies =  new ArrayList<>();
        }
        mProxies.add(proxy);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mContext == null) mContext = context;
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onAttach(context);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onDetach();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onStart();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onStop();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onResume();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onPause();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onDestroy();
            }
        }
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onLowMemory();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mProxies!=null){
            for(HdxFragmentProxy proxy:mProxies){
                proxy.onConfigurationChanged(newConfig);
            }
        }
    }
}
