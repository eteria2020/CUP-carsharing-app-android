package it.sharengo.development.ui.settingslang;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

import it.sharengo.development.R;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

import static android.content.Context.MODE_PRIVATE;

public class SettingsLangPresenter extends BasePresenter<SettingsLangMvpView> {

    private static final String TAG = SettingsLangPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public SettingsLangPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);
        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public void loadData(Context context){
        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        getMvpView().showList(mPref.getString(context.getString(R.string.preference_lang), "it"));
    }

    public void setLang(Context context, String lang){
        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(context.getString(R.string.preference_lang),lang);
        editor.commit();

        mAppRepository.putLang(lang);


        getMvpView().reloadApp();
    }

}



