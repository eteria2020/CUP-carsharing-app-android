package it.sharengo.development.ui.longintro;


import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.login.LoginActivity;
import it.sharengo.development.ui.login.LoginFragment;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class LongIntroFragment extends BaseMvpFragment<LongIntroPresenter> implements LongIntroMvpView, AnimationListener {

    private static final String TAG = LongIntroFragment.class.getSimpleName();

    private GifDrawable gifDrawable;
    private boolean gifAnimation;

    @BindView(R.id.introGifImageView)
    GifImageView mGif;

    @BindView(R.id.introTextView)
    TextView introTextView;

    @BindView(R.id.intro2TextView)
    TextView intro2TextView;

    @BindView(R.id.intro3TextView)
    TextView intro3TextView;

    @BindView(R.id.introGifLayout)
    ViewGroup introGifLayout;

    public static LongIntroFragment newInstance() {
        LongIntroFragment fragment = new LongIntroFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_long_intro, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        gifDrawable = (GifDrawable) mGif.getDrawable();
        gifDrawable.addAnimationListener(this);

        gifAnimation = true;

        startGifAnimation();

        return view;
    }

    private void startGifAnimation(){

        //Preparo l'animazione delle scritte
        introGifLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = introGifLayout.getWidth();
                int height = introGifLayout.getHeight();
                if (width > 0 && height > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        introGifLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        introGifLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    introTextView.setTranslationY(introGifLayout.getHeight());
                    intro2TextView.setTranslationY(introGifLayout.getHeight());
                    intro3TextView.setTranslationY(introGifLayout.getHeight());
                }
            }
        });

        gifDrawable.setLoopCount(1);
    }

    @Override
    public void onAnimationCompleted(int loopNumber) {

        if(gifAnimation) {

            try {
                gifDrawable = new GifDrawable(getResources(), R.drawable.intro_long_part2);
                mGif.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            gifDrawable.setLoopCount(1);
            gifDrawable.start();
            gifDrawable.addAnimationListener(this);

            showWords();

            gifAnimation = false;
        }

    }

    private void showWords(){
        introTextView.setVisibility(View.VISIBLE);
        introTextView.animate().setStartDelay(300).setDuration(700).translationY(0);

        intro2TextView.setVisibility(View.VISIBLE);
        intro2TextView.animate().setStartDelay(1600).setDuration(700).translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

                animation.removeListener(this);

                introTextView.animate().setStartDelay(500).alpha(0.0f).setListener(null);;
                intro2TextView.animate().setStartDelay(500).alpha(0.0f).setListener(null);;


                intro3TextView.setVisibility(View.VISIBLE);
                intro3TextView.animate().setStartDelay(500).setDuration(700).translationY(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        animator.removeAllListeners();

                        intro3TextView.animate().setStartDelay(800).alpha(0.0f).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                animator.removeAllListeners();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Navigator.launchOnboarding(LongIntroFragment.this);
                                        getActivity().finish();
                                    }
                                }, 1000);


                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
