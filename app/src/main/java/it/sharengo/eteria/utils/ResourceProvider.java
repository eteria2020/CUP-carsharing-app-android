package it.sharengo.eteria.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.MenuItem;

public class ResourceProvider {
    
    public static final String TAG = ResourceProvider.class.getSimpleName();
    
    public static String getMenuItemLabel(Context context, MenuItem menuItem) {
        
        switch(menuItem.section) {
            case HOME:
                return context.getString(R.string.menu_home);
            case LOGIN:
                return context.getString(R.string.menu_login);
            case SIGNUP:
                return context.getString(R.string.menu_signup);
            case FAQ:
                return context.getString(R.string.menu_faq);
            case RATES:
                return context.getString(R.string.menu_rates);
            case HELP:
                return context.getString(R.string.menu_help);
            case PROFILE:
                return context.getString(R.string.menu_profile);
            case BOOKING:
                return context.getString(R.string.menu_booking);
            case HISTORIC:
                return context.getString(R.string.menu_historic);
            case BUY:
                return context.getString(R.string.menu_buy);
            case SHARE:
                return context.getString(R.string.menu_share);
            case SETTINGS:
                return context.getString(R.string.menu_setting);
            case LOGOUT:
                return context.getString(R.string.menu_logout);
        }

        Log.e(TAG, "Resource not found for MenuItem section: " + menuItem.section);
        return "";
    }

    public static Drawable getMenuItemIcon(Context context, MenuItem menuItem) {

        switch(menuItem.section) {
            case HOME:
                return getDrawable(context, R.drawable.ic_profilo);
            case LOGIN:
                return getDrawable(context, R.drawable.ic_login);
            case SIGNUP:
                return getDrawable(context, R.drawable.ic_iscrizione);
            case FAQ:
                return getDrawable(context, R.drawable.ic_faq);
            case RATES:
                return getDrawable(context, R.drawable.ic_tariffe);
            case HELP:
                return getDrawable(context, R.drawable.ic_assistenza_nero);
            case PROFILE:
                return getDrawable(context, R.drawable.ic_profilo);
            case BOOKING:
                return getDrawable(context, R.drawable.ic_prenota);
            case HISTORIC:
                return getDrawable(context, R.drawable.ic_cron_corse);
            case BUY:
                return getDrawable(context, R.drawable.ic_acquistaminuti);
            case SHARE:
                return getDrawable(context, R.drawable.ic_invita_amico);
            case SETTINGS:
                return getDrawable(context, R.drawable.ic_impostazioni);
            case LOGOUT:
                return getDrawable(context, R.drawable.ic_logout);
        }

        Log.e(TAG, "Resource not found for MenuItem section: " + menuItem.section);
        return null;
    }

    public static Drawable getDrawable(Context context, int icon){
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) context.getDrawable(icon);
        }else{
            drawable = (Drawable) context.getResources().getDrawable(icon);
        }

        return drawable;
    }
    
}
