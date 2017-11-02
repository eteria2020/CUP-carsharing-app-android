package it.sharengo.eteria.ui.base.fragments;

import android.os.Bundle;

import butterknife.Unbinder;
import it.handroix.core.base.HdxBaseDialogFragment;

public class BaseDialogFragment extends HdxBaseDialogFragment {

    protected Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

}
