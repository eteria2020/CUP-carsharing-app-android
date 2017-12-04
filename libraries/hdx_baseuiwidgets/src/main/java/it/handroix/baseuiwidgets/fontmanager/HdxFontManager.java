package it.handroix.baseuiwidgets.fontmanager;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lucapiras on 29/09/16.
 */

public class HdxFontManager {

    private static final String TAG = "HdxFontManager";

    private static HdxFontManager instance;

    private Map<String, Typeface> fonts;

    private HdxFontManager() {
        fonts = new HashMap<String, Typeface>();
    }

    public static HdxFontManager getInstance() {
        if(instance==null){
            instance = new HdxFontManager();
        }
        return instance;
    }

    public Typeface getFont(Context context, String asset) {

        if (fonts.containsKey(asset)) {
            return fonts.get(asset);
        }

        Typeface font = null;
        try {
            font = Typeface.createFromAsset(context.getAssets(), asset);
            fonts.put(asset, font);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
        }
        return font;
    }
}
