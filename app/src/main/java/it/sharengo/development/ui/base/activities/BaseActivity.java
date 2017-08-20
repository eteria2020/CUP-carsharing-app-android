package it.sharengo.development.ui.base.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import it.handroix.core.base.HdxBaseActivity;
import it.sharengo.development.R;

public abstract class BaseActivity extends HdxBaseActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame);
        setupLoadingFragment(android.R.id.content);
    }

    public void showLoading() {
        Log.w("SHOW","LOADING");
        findViewById(R.id.customLoading).setVisibility(View.VISIBLE);
        ImageView customBkgLoading = (ImageView)findViewById(R.id.customBkgLoading);
        customBkgLoading.setBackgroundResource(R.drawable.loader_bkg_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) customBkgLoading.getBackground();
        frameAnimation.start();

        checkIfAnimationDone(frameAnimation);

        //super.showLoading();
    }

    public void hideLoading() {
        //super.hideLoading();
        //findViewById(R.id.customLoading).setVisibility(View.GONE);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void checkIfAnimationDone(AnimationDrawable anim){
        final AnimationDrawable a = anim;
        int timeBetweenChecks = 300;
        Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)){
                    checkIfAnimationDone(a);
                } else{
                    findViewById(R.id.customLoading).setVisibility(View.GONE);
                }
            }
        }, timeBetweenChecks);
    };
}
