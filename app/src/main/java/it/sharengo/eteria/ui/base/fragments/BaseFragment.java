package it.sharengo.eteria.ui.base.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.Unbinder;
import it.handroix.core.base.HdxBaseFragment;
import it.sharengo.eteria.App;

public class BaseFragment extends HdxBaseFragment {
    
    protected Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
