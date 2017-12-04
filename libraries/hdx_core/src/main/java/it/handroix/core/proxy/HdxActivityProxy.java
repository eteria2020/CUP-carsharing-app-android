package it.handroix.core.proxy;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by luca on 21/07/14.
 */
public interface HdxActivityProxy {

    public void onCreate(Bundle savedInstanceState);

    public void onPostCreate(Bundle savedInstanceState);

    public void onConfigurationChanged(Configuration newConfig);

    public boolean onOptionsItemSelected(MenuItem item);

    public void onStart();

    public void onStop();

    public void onPause();

    public void onResume();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}
