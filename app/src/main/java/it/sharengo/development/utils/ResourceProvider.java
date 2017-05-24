package it.sharengo.development.utils;

import android.content.Context;
import android.util.Log;

import it.sharengo.development.R;
import it.sharengo.development.data.models.MenuItem;

public class ResourceProvider {
    
    public static final String TAG = ResourceProvider.class.getSimpleName();
    
    public static String getMenuItemLabel(Context context, MenuItem menuItem) {
        
        switch(menuItem.section) {
            case HOME:
                return context.getString(R.string.menu_home);
        }

        Log.e(TAG, "Resource not found for MenuItem section: " + menuItem.section);
        return "";
    }
    
}
