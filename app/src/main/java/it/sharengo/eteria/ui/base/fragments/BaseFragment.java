package it.sharengo.eteria.ui.base.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

import butterknife.Unbinder;
import it.handroix.core.base.HdxBaseFragment;
import it.sharengo.eteria.App;
import it.sharengo.eteria.R;

import static android.content.Context.MODE_PRIVATE;

public class BaseFragment extends HdxBaseFragment {
    
    protected Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().getComponent().inject(this);

        SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        String username =  mPref.getString("KEY_USERNAME", "");

        String lang;
        if(!username.isEmpty())
            lang = mPref.getString(getString(R.string.preference_lang), Locale.getDefault().getLanguage());
        else
            lang = Locale.getDefault().getLanguage();
		Log.d("BOMB", "LOCALE IS " +lang + " " + this.getClass());
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
