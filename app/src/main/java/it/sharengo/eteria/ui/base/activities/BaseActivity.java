package it.sharengo.eteria.ui.base.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import it.handroix.core.base.HdxBaseActivity;
import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.home.HomeActivity;
import it.sharengo.eteria.ui.mapgoogle.MapGoogleActivity;

public abstract class BaseActivity extends HdxBaseActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame);
        setupLoadingFragment(android.R.id.content);
    }

    public void showLoading() {


//        findViewById(R.id.customLoading).setAlpha(1.0f);
//        findViewById(R.id.customLoading).setVisibility(View.VISIBLE);

        //super.showLoading();
    }

    public void hideLoading() {
        //super.hideLoading();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                hideLoaderAnimation((RelativeLayout) findViewById(R.id.customLoading));
            }
        }, 2000);

    }

    public void showLoadingChronology() {

//        findViewById(R.id.customLoadingChronology).setAlpha(1.0f);
//        findViewById(R.id.customLoadingChronology).setVisibility(View.VISIBLE);

    }

    public void hideLoadingChronology() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                hideLoaderAnimation((RelativeLayout) findViewById(R.id.customLoadingChronology));
            }
        }, 700);

    }

    private void hideLoaderAnimation(final RelativeLayout customLoading){

        if(customLoading != null) {


            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(customLoading, "alpha",  1f, .0f);
            fadeOut.setDuration(200);

            final AnimatorSet mAnimationSet = new AnimatorSet();

            mAnimationSet.play(fadeOut);

            mAnimationSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //mAnimationSet.start();
                    customLoading.setVisibility(View.GONE);
                }
            });
            mAnimationSet.start();
        }

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
    }

    @Override
    public void onResume() {
        super.onResume();
        App.addActivityToStack(this.getClass());
    }

    @Override
    protected void onDestroy() {
        App.removeActivityToStack(this.getClass());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            if (this.getClass().equals(MapGoogleActivity.class)) {
                finish();
            } else {
                if (App.getmStackActivity().size() > 1) {
                    super.onBackPressed();
                } else {
                    finish();
                    if (!this.getClass().equals(MapGoogleActivity.class))
                        Navigator.launchMapGoogle(BaseActivity.this, Navigator.REQUEST_MAP_DEFAULT);
                }
            }
        }catch (Exception e) {
            Log.e("BOMB","Exception while onBackPressed",e);
        }
    }
}
