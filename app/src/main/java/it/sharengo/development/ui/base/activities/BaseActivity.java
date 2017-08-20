package it.sharengo.development.ui.base.activities;

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
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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


        findViewById(R.id.customLoading).setAlpha(1.0f);
        findViewById(R.id.customLoading).setVisibility(View.VISIBLE);

        ImageView customLoadingAnim = (ImageView)findViewById(R.id.customLoadingAnim);
        customLoadingAnim.setBackgroundResource(R.drawable.loader_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) customLoadingAnim.getBackground();
        frameAnimation.start();

        //super.showLoading();
    }

    public void hideLoading() {
        //super.hideLoading();
        //findViewById(R.id.customLoading).setVisibility(View.GONE);

        //Background
        /*ImageView customBkgLoading = (ImageView)findViewById(R.id.customBkgLoading);
        customBkgLoading.setBackgroundResource(R.drawable.loader_bkg_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) customBkgLoading.getBackground();
        frameAnimation.start();

        checkIfAnimationDone(frameAnimation);*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                hideLoaderAnimation();
            }
        }, 700);




    }

    private void hideLoaderAnimation(){
        RelativeLayout customLoading = (RelativeLayout) findViewById(R.id.customLoading);

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
                    findViewById(R.id.customLoading).setVisibility(View.GONE);
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
    };
}
