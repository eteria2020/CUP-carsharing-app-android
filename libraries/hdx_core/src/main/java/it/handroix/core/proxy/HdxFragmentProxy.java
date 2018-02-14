package it.handroix.core.proxy;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Created by luca on 28/07/14.
 */
public interface HdxFragmentProxy {

    void onAttach(Context context);

    void onDetach();

    void onResume();

    void onPause();

    void onStart();

    void onStop();

    void onDestroy();

    void onLowMemory();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onConfigurationChanged(Configuration newConfig);

}
