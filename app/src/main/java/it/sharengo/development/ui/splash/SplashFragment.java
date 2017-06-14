package it.sharengo.development.ui.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

import static android.content.Context.MODE_PRIVATE;

public class SplashFragment extends BaseMvpFragment<SplashPresenter> implements SplashMvpView {

    private static final String TAG = SplashFragment.class.getSimpleName();
    
    @BindView(R.id.splash_content)
    View mSplashContent;

    public static SplashFragment newInstance() {
        SplashFragment instance = new SplashFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSplashContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSplashContent.startAnimation(anim);

        mPresenter.loadData();
    }

    @Override
    public void navigateToHome() {

        //Se è il primo accesso, mostro il login
        if(mPresenter.isFirstAccess(getActivity().getPreferences(MODE_PRIVATE))){
            mPresenter.setFirstAccess(getActivity().getPreferences(MODE_PRIVATE));
            Navigator.launchLogin(this);
            getActivity().finish();
        }else{
            Navigator.launchHome(this);
            getActivity().finish();
        }
    }
}
