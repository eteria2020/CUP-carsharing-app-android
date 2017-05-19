package it.sharengo.eteria.data.datasources;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesDataSource {

    static final String KEY_FIRST_ACCESS = "KEY_FIRST_ACCESS";

    private final SharedPreferences mPref;

    @Inject
    public PreferencesDataSource(SharedPreferences sharedPreferences) {
        mPref = sharedPreferences;
    }

    public boolean getFirstAccess() {
        return mPref.getBoolean(KEY_FIRST_ACCESS, true);
    }

    public boolean saveFirstAccess(boolean firstAccess) {
        return setBoolean(KEY_FIRST_ACCESS, firstAccess);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }
    
    /*
     *
     *      PRIVATE
     *      
     */

    private boolean setBoolean(String key, boolean data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, data);
        return editor.commit();
    }

    private boolean setInt(String key, int data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, data);
        return editor.commit();
    }

    private boolean setString(String key, String data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, data);
        return editor.commit();
    }
}
