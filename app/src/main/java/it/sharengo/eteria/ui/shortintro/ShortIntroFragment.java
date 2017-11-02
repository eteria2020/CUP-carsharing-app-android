package it.sharengo.eteria.ui.shortintro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class ShortIntroFragment extends BaseMvpFragment<ShortIntroPresenter> implements ShortIntroMvpView, AnimationListener {

    private static final String TAG = ShortIntroFragment.class.getSimpleName();

    private GifDrawable gifDrawable;

    @BindView(R.id.introGifImageView)
    GifImageView mGif;

    public static ShortIntroFragment newInstance() {
        ShortIntroFragment fragment = new ShortIntroFragment();
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
        View view = inflater.inflate(R.layout.fragment_short_intro, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        gifDrawable = (GifDrawable) mGif.getDrawable();
        gifDrawable.addAnimationListener(this);

        startGifAnimation();

        return view;
    }

    private void startGifAnimation(){

        gifDrawable.setLoopCount(1);
    }

    @Override
    public void onAnimationCompleted(int loopNumber) {
        //Navigator.launchOnboarding(ShortIntroFragment.this);
        if(getActivity() != null)
            getActivity().finish();
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
