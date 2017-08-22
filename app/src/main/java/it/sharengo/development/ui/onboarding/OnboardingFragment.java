package it.sharengo.development.ui.onboarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;


public class OnboardingFragment extends BaseMvpFragment<OnboardingPresenter> implements OnboardingMvpView, AnimationListener {

    private static final String TAG = OnboardingFragment.class.getSimpleName();

    private GifDrawable gifDrawable;
    private int nextAnimation;
    private boolean backAnimation;
    private String lang;
    private boolean animation;

    @BindView(R.id.animImageView)
    GifImageView mGif;

    @BindView(R.id.onboardLayout)
    ViewGroup onboardLayout;

    @BindView(R.id.slideIndicator1)
    View slideIndicator1;

    @BindView(R.id.slideIndicator2)
    View slideIndicator2;

    @BindView(R.id.slideIndicator3)
    View slideIndicator3;

    @BindView(R.id.textTextView)
    TextView textTextView;


    public static OnboardingFragment newInstance() {
        OnboardingFragment fragment = new OnboardingFragment();
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
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        lang = mPref.getString(getString(R.string.preference_lang), Locale.getDefault().getLanguage());

        gifDrawable = (GifDrawable) mGif.getDrawable();

        animation = false;
        nextAnimation = 0;
        backAnimation = false;

        onboardLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {

                if(!animation && nextAnimation != 2){
                    backAnimation = true;
                    nextAnimation();
                }

            }
            public void onSwipeLeft() {

                if(!animation){
                    backAnimation = false;
                    nextAnimation();
                }
            }
            public void onSwipeBottom() {
            }

        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(nextAnimation == 0)
            nextAnimation();
    }

    private Drawable getBgDrawable(int icon){
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) getActivity().getDrawable(icon);
        }else{
            drawable = (Drawable) getResources().getDrawable(icon);
        }

        return drawable;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Animation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void nextAnimation(){
        animation = true;

        switch (nextAnimation){
            case 0: //Car 1 - Ingresso
                setIndicator(1);
                startAnimationCar1Ingresso();
                break;
            case 1: //Car 1 - Loop
                setIndicator(1);
                startAnimationCar1Loop();
                break;
            case 2: //Car 1 - Uscita
                setIndicator(1);
                startAnimationCar1Uscita();
                break;
            case 3: //Car 2 - Ingresso
                setIndicator(2);
                startAnimationCar2Ingresso();
                break;
            case 4: //Car 2 - Loop
                setIndicator(2);
                startAnimationCar2Loop();
                break;
            case 5: //Car 2 - Uscita
                setIndicator(2);
                startAnimationCar2Uscita();
                break;
            case 6: //Car 3 - Ingresso
                setIndicator(3);
                startAnimationCar3Ingresso();
                break;
            case 7: //Car 3 - Loop
                setIndicator(3);
                startAnimationCar3Loop();
                break;
            case 8:
                //Car 3 - Uscita
                setIndicator(3);
                startAnimationCar3Uscita();
                break;
            case 9:
                getActivity().finish();
                break;

        }
    }

    private void setIndicator(int indicator){
        switch (indicator){
            case 1:
                slideIndicator1.setBackground(getBgDrawable(R.drawable.btn_bkg_mediumseagreen));
                slideIndicator2.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                slideIndicator3.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                break;
            case 2:
                slideIndicator1.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                slideIndicator2.setBackground(getBgDrawable(R.drawable.btn_bkg_mediumseagreen));
                slideIndicator3.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                break;
            case 3:
                slideIndicator1.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                slideIndicator2.setBackground(getBgDrawable(R.drawable.btn_bkg_white));
                slideIndicator3.setBackground(getBgDrawable(R.drawable.btn_bkg_mediumseagreen));
                break;
        }
    }

    //Car 1
    private void startAnimationCar1Ingresso(){

        int da = (lang.equals("it")) ? R.drawable.auto_a_ingresso : R.drawable.auto_a_ingresso_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 1;
    }

    private void startAnimationCar1Loop(){

        textTextView.setText(getString(R.string.onboarding_car1_label));
        textTextView.animate().setDuration(100).alpha(1.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_a_loop : R.drawable.auto_a_loop_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 2;
    }

    private void startAnimationCar1Uscita(){

        textTextView.animate().alpha(0.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_a_uscita : R.drawable.auto_a_uscita_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 3;
    }

    //Car 2
    private void startAnimationCar2Ingresso(){

        int da = (lang.equals("it")) ? R.drawable.auto_b_ingresso : R.drawable.auto_b_ingresso_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 4;
    }

    private void startAnimationCar2Loop(){

        textTextView.setText(getString(R.string.onboarding_car2_label));
        textTextView.animate().setDuration(100).alpha(1.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_b_loop : R.drawable.auto_b_loop_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 5;
    }

    private void startAnimationCar2Uscita(){

        textTextView.animate().alpha(0.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_b_uscita : R.drawable.auto_b_uscita_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        if(backAnimation)
            nextAnimation = 0;
        else
            nextAnimation = 6;

    }

    //Car 3
    private void startAnimationCar3Ingresso(){

        int da = (lang.equals("it")) ? R.drawable.auto_c_ingresso : R.drawable.auto_c_ingresso_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 7;
    }

    private void startAnimationCar3Loop(){

        textTextView.setText(getString(R.string.onboarding_car3_label));
        textTextView.animate().setDuration(100).alpha(1.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_c_loop : R.drawable.auto_c_loop_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        nextAnimation = 8;
    }

    private void startAnimationCar3Uscita(){

        textTextView.animate().alpha(0.0f).setListener(null);

        int da = (lang.equals("it")) ? R.drawable.auto_c_uscita : R.drawable.auto_c_uscita_en;

        try {
            gifDrawable = new GifDrawable(getResources(), da);
            mGif.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifDrawable.setLoopCount(1);
        gifDrawable.start();
        gifDrawable.addAnimationListener(this);

        if(backAnimation)
            nextAnimation = 3;
        else
            nextAnimation = 9;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAnimationCompleted(int loopNumber) {

        animation = false;


        if(nextAnimation == 0 || nextAnimation == 1 || nextAnimation == 3 || nextAnimation == 4 || nextAnimation == 6 || nextAnimation == 7 || nextAnimation == 9)
            nextAnimation();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.skipButton)
    public void onSkipClick(){
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method for manage swipe on onboarding view.
     */
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}
