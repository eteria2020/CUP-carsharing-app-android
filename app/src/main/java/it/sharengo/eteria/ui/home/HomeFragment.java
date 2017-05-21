package it.sharengo.eteria.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;

public class HomeFragment extends BaseMvpFragment<HomePresenter> implements HomeMvpView {

    private static final String TAG = HomeFragment.class.getSimpleName();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    ////////////////////////////////////
    //
    //         BUTTERKNIFE
    //
    ////////////////////////////////////
    

    ////////////////////////////////////
    //
    //         MVP
    //
    ////////////////////////////////////



}
