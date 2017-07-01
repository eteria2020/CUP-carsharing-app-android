package it.sharengo.development.ui.base.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import java.util.Locale;

import butterknife.Unbinder;
import it.handroix.core.base.HdxBaseFragment;
import it.sharengo.development.App;
import it.sharengo.development.R;

import static android.content.Context.MODE_PRIVATE;

public class BaseFragment extends HdxBaseFragment {
    
    protected Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponent().inject(this);

        SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        String lang = mPref.getString(getString(R.string.preference_lang), "it");

        Resources res = getActivity().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
