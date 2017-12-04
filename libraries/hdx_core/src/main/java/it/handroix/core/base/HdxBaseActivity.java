package it.handroix.core.base;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.handroix.core.proxy.HdxActivityProxy;

/**
 * Created by andrealucibello on 04/07/14.
 */
public class HdxBaseActivity extends AppCompatActivity {

    private final String KEY_IS_LOADING = "is_loading";
    private final String LOADING_FRAGMENT_TAG = "handroix_loading_fragment";

    private List<HdxActivityProxy> mProxies;

    Fragment mHdxLoadingFragment;
    private boolean mIsLoading = false;

    private int mLoadingFramentContainerId = -1;

    /**
     * Add a new proxy that will receive the activity event.
     *
     * @param proxy
     */
    public void addProxy(HdxActivityProxy proxy) {
        if (mProxies == null) {
            mProxies = new ArrayList<HdxActivityProxy>();
        }
        mProxies.add(proxy);
    }

    /**
     * ACTIVITY PROXY FORWARDING MANAGEMENT
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onCreate(savedInstanceState);
            }
        }

        if (savedInstanceState != null) {
            mIsLoading = savedInstanceState.getBoolean(KEY_IS_LOADING, false);
        }
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onPostCreate(savedInstanceState);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onConfigurationChanged(newConfig);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean res = false;
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                if (res) {
                    proxy.onOptionsItemSelected(item);
                } else {
                    res = proxy.onOptionsItemSelected(item);
                }
            }
        }
        if (!res) {
            return super.onOptionsItemSelected(item);
        }
        return res;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onPause();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onResume();
            }
        }

        if (mIsLoading) {
            mHdxLoadingFragment = getSupportFragmentManager().findFragmentByTag(LOADING_FRAGMENT_TAG);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onStart();
            }
        }
    }

    public void onStop() {
        super.onStop();
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onStop();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mProxies != null) {
            for (HdxActivityProxy proxy : mProxies) {
                proxy.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_LOADING, mIsLoading);
        super.onSaveInstanceState(outState);
    }

    /**
     * LOADING FRAGMENT MANAGEMENT
     */

    protected void showLoading() {
        if (mIsLoading) {
            return;
        }
        if (mHdxLoadingFragment == null || mLoadingFramentContainerId == -1) {
            throw new IllegalStateException("setupLoadingFragment not called!!");
        }
        mIsLoading = true;

        getSupportFragmentManager()
                .beginTransaction()
                .add(mLoadingFramentContainerId, mHdxLoadingFragment, LOADING_FRAGMENT_TAG)
                .commit();
    }

    protected void hideLoading() {
        if (mIsLoading) {
            this.mIsLoading = false;
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(this.mHdxLoadingFragment)
                    .commit();
        }
    }

    protected void hideLoadingImmediately() {
        if (mIsLoading) {
            this.mIsLoading = false;
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this.mHdxLoadingFragment)
                    .commit();
        }
    }

    public void setupLoadingFragment(int viewContainerId, Fragment loadingFragment) {
        this.mLoadingFramentContainerId = viewContainerId;
        if (loadingFragment != null) {
            mHdxLoadingFragment = loadingFragment;
        } else {
            mHdxLoadingFragment = new HdxLoadingFragment();
        }
    }

    public void setupLoadingFragment(int viewContainerId) {
        setupLoadingFragment(viewContainerId, null);
    }
}
